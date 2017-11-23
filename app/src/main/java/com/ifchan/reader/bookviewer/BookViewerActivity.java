package com.ifchan.reader.bookviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ifchan.reader.MainActivity;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class BookViewerActivity extends AppCompatActivity {
    private static final int IMAGE_LOADED = 2;
    private List<Book> mBookList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private BookRecyclerViewAdapter mBookRecyclerViewAdapter;
    private int mMode;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MainActivity.INTENT_MODE_HOT_BOOK:
                    mBookRecyclerViewAdapter.notifyItemRangeInserted(0, mBookList.size() - 1);
                    getBookImage();
                    break;
                case IMAGE_LOADED:
                    mBookRecyclerViewAdapter.notifyItemChanged(msg.arg1);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_viewer);

        receiveIntent();
        initRecyclerView();
    }

    private void receiveIntent() {
        Intent intentReceived = getIntent();
        mMode = intentReceived.getIntExtra(MainActivity.INTENT_MODE, 0);
        switch (mMode) {
            case MainActivity.INTENT_MODE_HOT_BOOK:
                getHotBook();
                break;
            case MainActivity.INTENT_MODE_LATEST_BOOK:
                getLatestBook();
                break;
        }
    }


    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.book_viewer_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(BookViewerActivity.this);
        mBookRecyclerViewAdapter = new BookRecyclerViewAdapter(mBookList, BookViewerActivity.this);
        mRecyclerView.setAdapter(mBookRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
    }

    private void getHotBook() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                InputStream inputStream = null;
                try {
                    url = new URL("http://api.zhuishushenqi.com/ranking/54d43437d47d13ff21cad58b");
                    inputStream = url.openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    JSONObject jsonObject = new JSONObject(builder.toString());
                    JSONObject ranking = jsonObject.getJSONObject("ranking");
                    JSONArray books = ranking.getJSONArray("books");
                    File externalFolder = Environment.getExternalStorageDirectory();
                    File imageTemp = new File(externalFolder.getPath() + "/Reader/temp/cover");
                    if (!imageTemp.exists()) {
                        imageTemp.mkdirs();
                    }
                    for (int i = 0; i < books.length(); i++) {
                        JSONObject j = books.getJSONObject(i);
                        Book book = new Book(j.getString("_id"), j.getString("title"),
                                j.getString("author"), j.getString("shortIntro"), j.getString
                                ("cover"), j.getString("site"), j.getInt("banned"), j.getInt
                                ("latelyFollower"), j.getString("retentionRatio"));
                        String codedPath = book.getCover();
                        String decode = URLDecoder.decode(codedPath); //change afterwards
                        String http = decode.substring(decode.indexOf('h'), decode.lastIndexOf
                                ('/'));
                        book.setCover(http);
                        book.setCoverPath(imageTemp.getPath()+"/" +
                                book.getId() + ".jpg");
                        mBookList.add(book);
                    }
                    Message message = new Message();
                    message.what = MainActivity.INTENT_MODE_HOT_BOOK;
                    mHandler.sendMessage(message);
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
            }
        }).start();
    }

    private void getBookImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream imageInputStream = null;
                FileOutputStream fileOutputStream = null;
                int count = 0;
                for (Book book : mBookList) {
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
                            imageInputStream.close();
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    private void getLatestBook() {

    }
}
