package com.ifchan.reader.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daily on 11/25/17.
 */

// TODO: 11/29/17 Download Covers

public class FragmentMonthly extends MyBasicFragment {

    private static final int IMAGE_LOADED = 1;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    sortBook(mBookList);
                    refreshRecyclerView();
                    getBookImage(mBookList);
//                    initRecyclerView();
                    break;
                case IMAGE_LOADED:
                    mBookRecyclerViewAdapter.notifyItemChanged(msg.arg1);
                    break;
            }
        }
    };


    private View mView;
    private List<Book> mBookList = new ArrayList<>();
    BookRecyclerViewAdapter mBookRecyclerViewAdapter;

    private void refreshRecyclerView() {
        mBookRecyclerViewAdapter.notifyItemRangeInserted(0, mBookList.size() - 1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_monthly, container, false);
        initRecyclerView();
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getJSON();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mOnAttachedListener = (OnAttachedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement Listener");
        }
    }

    private void getJSON() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                InputStream inputStream = null;
                try {
                    url = new URL("http://api.zhuishushenqi" +
                            ".com/book/by-categories?gender=" + URLEncoder.encode
                            (mOnAttachedListener.getSex(), "UTF-8")
                            + "&type=monthly&major=" + URLEncoder.encode(mOnAttachedListener.getType()
                            , "UTF-8") + "&minor=&start=0&limit=20");
                    inputStream = url.openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    JSONObject jsonObject = new JSONObject(builder.toString());
                    JSONArray books = jsonObject.getJSONArray("books");
                    File externalFolder = Environment.getExternalStorageDirectory();
                    File imageTemp = new File(externalFolder.getPath() + "/Reader/temp/cover");
                    if (!imageTemp.exists()) {
                        imageTemp.mkdirs();
                    }
                    for (int i = 0; i < books.length(); i++) {
                        JSONObject jsonObjectBook = books.getJSONObject(i);
                        String id = jsonObjectBook.getString("_id");
                        String title = jsonObjectBook.getString("title");
                        String author = jsonObjectBook.getString("author");
                        String shortIntro = jsonObjectBook.getString("shortIntro");
                        String cover = jsonObjectBook.getString("cover");
                        cover = URLDecoder.decode(cover);
                        cover = cover.substring(cover.indexOf('h'),cover.lastIndexOf('/'));
                        String coverPath = imageTemp.getPath() + "/" +
                                id + ".jpg";
                        String site = jsonObjectBook.getString("site");
                        int banned = jsonObjectBook.getInt("banned");
                        int latelyFollower = jsonObjectBook.getInt("latelyFollower");
                        String retentionRatio = Integer.toString(jsonObjectBook.getInt
                                ("retentionRatio"));
                        Book book = new Book(id, title, author, shortIntro, cover, site, banned,
                                latelyFollower, retentionRatio,jsonObjectBook.getString("majorCate"));
                        book.setCoverPath(coverPath);
                        mBookList.add(book);
                    }
                    Message message = new Message();
                    message.what = 0;
                    mHandler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
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
        RecyclerView recyclerView = mView.findViewById(R.id.fragment_monthly_recycler_view);
        mBookRecyclerViewAdapter = new BookRecyclerViewAdapter(mBookList,
                getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mBookRecyclerViewAdapter);
    }
}
