package com.ifchan.reader.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ifchan.reader.R;
import com.ifchan.reader.adapter.BookRecyclerViewAdapter;
import com.ifchan.reader.entity.Book;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daily on 11/25/17.
 */

// TODO: 11/29/17 Download Covers

public class FragmentCommunity extends MyBasicFragment {

    private static final int IMAGE_LOADED = 0;
    private View mView;
    private List<Book> mBookList = new ArrayList<>();
    BookRecyclerViewAdapter mBookRecyclerViewAdapter;
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

    private void refreshRecyclerView() {
        mBookRecyclerViewAdapter.notifyItemRangeInserted(0, mBookList.size() - 1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_bookshelf, container, false);
        initRecyclerView();
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }
}
