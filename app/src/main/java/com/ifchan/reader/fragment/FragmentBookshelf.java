package com.ifchan.reader.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.AsyncTask;
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
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.ifchan.reader.R;
import com.ifchan.reader.adapter.BookRecyclerViewAdapter;
import com.ifchan.reader.entity.Book;
import com.ifchan.reader.entity.Index;
import com.ifchan.reader.helper.BookshelfDataBaseHelper;
import com.ifchan.reader.helper.DataBaseHelper;
import com.ifchan.reader.listener.EndlessRecyclerOnScrollListener;
import com.ifchan.reader.pullrefreshlayout.PullRefreshLayout;
import com.ifchan.reader.utils.imagechcheutils.MyBitmapUtils;
import com.ifchan.reader.utils.novel.HeaderUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daily on 11/25/17.
 */


public class FragmentBookshelf extends MyBasicFragment {

    private static final int IMAGE_LOADED = 0;
    private View mView;
    private List<Book> mBookList = new ArrayList<>();
    BookRecyclerViewAdapter mBookRecyclerViewAdapter;
    private BookshelfDataBaseHelper mDataBaseHelper;
    private SQLiteDatabase db;
    private boolean[] hasNew;
    private int[] latestChapterCount;
    private int[] lastChapterCount;
    private PullRefreshLayout layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_bookshelf, container, false);
        initPullToRefresh();
        initRecyclerView();
        loadHistoryFromDateBase();
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBase();
    }

    private void loadHistoryFromDateBase() {
        Cursor cursor = db.query("Bookshelf", null, null, null, null, null, null);
        lastChapterCount = new int[cursor.getCount()];
        hasNew = new boolean[cursor.getCount()];
        int index = 0;
        mBookList.clear();
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

                Book book = new Book(bookid, title, author, shortIntro, cover, coverPath, site,
                        Integer.parseInt(latelyFollower), retentionRatio, majorCate);
                mBookList.add(book);
                lastChapterCount[index] = cursor.getInt(cursor.getColumnIndex("lastGetChapter"));
                index++;
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

    private void initPullToRefresh() {
        layout = mView.findViewById(R.id.fragment_hot_pullrefreshlayout);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mBookList.size() != 0) {
                    new AsyncTask<Object, String, String>() {

                        @Override
                        protected void onProgressUpdate(String... values) {
                            super.onProgressUpdate(values);
                            mBookRecyclerViewAdapter.notifyDataSetChanged();
                            mDataBaseHelper.refresh(values[0], values[1], values[2], values[3], db);
                            getIndexCount(values[4], Integer.parseInt(values[0]) - 1);
                        }

                        @Override
                        protected void onPostExecute(String id) {
                            super.onPostExecute(id);
                        }

                        @Override
                        protected String doInBackground(Object... objects) {
                            if (objects[0] instanceof List) {
                                List<Book> books = (List<Book>) objects[0];
                                int count = 0;
                                for (Book book : books) {
                                    count++;
                                    String url = "http://api.zhuishushenqi.com/book/" + book
                                            .getId();
                                    try {
                                        URL aURL = new URL(url);
                                        HttpURLConnection connection = (HttpURLConnection) aURL
                                                .openConnection();
                                        HeaderUtil.setConnectionHeader(connection);
                                        InputStream inputStream = connection.getInputStream();
                                        BufferedReader reader = new BufferedReader(new
                                                InputStreamReader(inputStream));
                                        String line;
                                        StringBuilder builder = new StringBuilder();
                                        while ((line = reader.readLine()) != null) {
                                            builder.append(line);
                                        }
                                        JSONObject received = new JSONObject(builder.toString());
                                        String cover = received.getString("cover");
                                        cover = URLDecoder.decode(cover.substring(cover.indexOf
                                                        ('h'),
                                                cover
                                                        .lastIndexOf
                                                                ('%')), "UTF-8");
                                        book.setCover(cover);
                                        book.setLatelyFollower(Integer.parseInt(received.getString
                                                ("latelyFollower")));
                                        book.setRetentionRatio(received.getString
                                                ("retentionRatio"));
                                        publishProgress(Integer.toString(count), cover, received
                                                .getString("latelyFollower"), received.getString
                                                ("retentionRatio"), book.getId());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            return null;
                        }
                    }.execute(mBookList);
                } else {
                    layout.setRefreshing(false);
                    Toast.makeText(getActivity(), "书架空空如也，快去找书吧", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getIndexCount(final String id, final int position) {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (lastChapterCount[position] < integer) {
                    hasNew[position] = true;
                    latestChapterCount[position] = integer;
                }
                if (position + 1 >= hasNew.length) {
                    mBookRecyclerViewAdapter.setHasNew(hasNew);
                    mBookRecyclerViewAdapter.setLatestChapterCount(latestChapterCount);
                    mBookRecyclerViewAdapter.notifyDataSetChanged();
                    layout.setRefreshing(false);
                }
            }

            @Override
            protected Integer doInBackground(Void... voids) {
                int count = 0;
                URL url;
                InputStream inputStream = null;
                try {
                    url = new URL("http://api.zhuishushenqi.com/mix-atoc/" + id
                            + "?view=chapters");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    HeaderUtil.setConnectionHeader(connection);
//                    connection.setRequestProperty("Host","api.zhuishushenqi.com");
//                    connection.setRequestProperty("Connection","keep-alive");
//                    connection.setRequestProperty("User-Agent","Mozilla/5.0 (X11; Linux x86_64)
// AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.9 Safari/537.36");
//                    connection.setConnectTimeout(5000);
//                    connection.setRequestMethod("GET");
//                    connection.setDoOutput(true);
//                    int responseCode = connection.getResponseCode();
//                    if (responseCode == 200) {
                    inputStream = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader
                            (inputStream));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    JSONObject jsonObject = new JSONObject(builder.toString());
                    JSONObject data = jsonObject.getJSONObject("mixToc");
                    JSONArray jsonArray = data.getJSONArray("chapters");
                    count++;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return count;
            }
        }.execute();
    }
}
