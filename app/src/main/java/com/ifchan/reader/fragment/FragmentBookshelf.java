package com.ifchan.reader.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ifchan.reader.R;
import com.ifchan.reader.adapter.BookRecyclerViewAdapter;
import com.ifchan.reader.entity.Book;
import com.ifchan.reader.helper.BookshelfDataBaseHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daily on 11/25/17.
 */

// TODO: 重复加载!

public class FragmentBookshelf extends MyBasicFragment {

    private static final int IMAGE_LOADED = 0;
    private View mView;
    private List<Book> mBookList = new ArrayList<>();
    BookRecyclerViewAdapter mBookRecyclerViewAdapter;
    private BookshelfDataBaseHelper mDataBaseHelper;
    private SQLiteDatabase db;
    private int lastBookCount = -1;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IMAGE_LOADED:
                    refreshRecyclerView();
                    break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_bookshelf, container, false);
        initDataBase();
        loadHistoryFromDateBase();
        initRecyclerView();
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void loadHistoryFromDateBase() {
        Cursor cursor = db.query("Bookshelf", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String bookid = cursor.getString(cursor.getColumnIndex("bookid"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String author = cursor.getString(cursor.getColumnIndex("author"));
                String shortIntro = cursor.getString(cursor.getColumnIndex("shortIntro"));
                String cover = cursor.getString(cursor.getColumnIndex("cover"));
                String coverPath = cursor.getString(cursor.getColumnIndex("coverPath"));
                String site = cursor.getString(cursor.getColumnIndex("site"));
                String latelyFollower = cursor.getString(cursor.getColumnIndex("latelyFollower"));

                String retentionRatio = cursor.getString(cursor.getColumnIndex
                        ("retentionRatio"));
                String majorCate = cursor.getString(cursor.getColumnIndex("majorCate"));
                mBookList.add(new Book(bookid, title, author, shortIntro, cover, coverPath, site,
                        Integer.parseInt(latelyFollower), retentionRatio, majorCate));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void initDataBase() {
        mDataBaseHelper = BookshelfDataBaseHelper.getInstance(getActivity());
        db = mDataBaseHelper.getWritableDatabase();
    }

    private void refreshRecyclerView() {
        mBookRecyclerViewAdapter.notifyItemRangeInserted(0, mBookList.size() - 1);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void getBookImage(final List<Book> books) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream imageInputStream = null;
                FileOutputStream fileOutputStream = null;
                int count = 0;
                for (Book book : books) {
                    URL imageUrl;
                    try {
                        imageUrl = new URL(book.getCover());
                        imageInputStream = imageUrl.openStream();
                        fileOutputStream = new FileOutputStream(book.getCoverPath());
                        byte[] bytes = new byte[512];
                        int read;
                        while ((read = imageInputStream.read(bytes)) != -1) {
                            fileOutputStream.write(bytes, 0, read);
                        }
                        Message message = new Message();
                        message.what = IMAGE_LOADED;
                        message.arg1 = count;
                        count++;
                        mHandler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (imageInputStream != null) {
                                imageInputStream.close();
                            }
                            if (fileOutputStream != null) {
                                fileOutputStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = mView.findViewById(R.id.fragment_hot_recycler_view);
        mBookRecyclerViewAdapter = new BookRecyclerViewAdapter(mBookList,
                getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mBookRecyclerViewAdapter);

        //swipe to delete
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper
                .SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT |
                ItemTouchHelper.RIGHT) {


            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                mBookList.remove(position);
                mDataBaseHelper.delFromBookshelf(position + 1, db);
                mBookRecyclerViewAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder
                    viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState,
                        isCurrentlyActive);
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //滑动时改变Item的透明度
                    final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
