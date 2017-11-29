package com.ifchan.reader.bookviewer;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BookViewerActivity extends AppCompatActivity {
    private static final int IMAGE_LOADED = 2;
    private static final int INTENT_MODE_READER_LEFT = 3;
    private List<Book> mBookListHotBook = new ArrayList<>();
    private List<Book> mBookListReaderLeft = new ArrayList<>();
    private RecyclerView mRecyclerViewHotBook;
    private LinearLayoutManager mLinearLayoutManagerHotBook;
    private LinearLayoutManager mLinearLayoutManagerReaderLeft;
    private BookRecyclerViewAdapter mBookRecyclerViewAdapterHotBook;
    private RecyclerView mRecyclerViewReaderLeft;
    private BookRecyclerViewAdapter mBookRecyclerViewAdapterReaderLeft;
    private int mMode;
    private List<View> viewContainer;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MainActivity.INTENT_MODE_HOT_BOOK:
                    sortBook(mBookListHotBook);
                    mBookRecyclerViewAdapterHotBook.notifyItemRangeInserted(0, mBookListHotBook
                            .size() - 1);
                    getBookImage(mBookListHotBook);
                    break;
                case INTENT_MODE_READER_LEFT:
                    sortBook(mBookListReaderLeft);
                    mBookRecyclerViewAdapterReaderLeft.notifyItemRangeInserted(0,
                            mBookListReaderLeft.size() - 1);
                    getBookImage(mBookListReaderLeft);
                    break;
                case IMAGE_LOADED:
                    mBookRecyclerViewAdapterHotBook.notifyItemChanged(msg.arg1);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_viewer);

        initViewPager();
        receiveIntent();
    }

    private void sortBook(List<Book> books) {
        Comparator comparator = new Comparator<Book>() {

            @Override
            public int compare(Book o1, Book o2) {
                return o2.getLatelyFollower() - o1.getLatelyFollower();
            }
        };
        Collections.sort(books, comparator);
    }


    private void receiveIntent() {
        Intent intentReceived = getIntent();
        mMode = intentReceived.getIntExtra(MainActivity.INTENT_MODE, 0);
        switch (mMode) {
            case MainActivity.INTENT_MODE_HOT_BOOK:
                getHotBook();
                getReaderLeftBook();
                break;
        }
    }


    private void initViewPager() {
        mLinearLayoutManagerHotBook = new LinearLayoutManager(BookViewerActivity.this);
        mLinearLayoutManagerReaderLeft = new LinearLayoutManager(BookViewerActivity.this);
        ViewPager viewPager = findViewById(R.id.book_viewer_view_pager);
        viewContainer = new ArrayList<>();
        LayoutInflater layoutInflater = getLayoutInflater().from(BookViewerActivity.this);
        View pageHotBook = layoutInflater.inflate(R.layout.page_hot_book, null);
        mRecyclerViewHotBook = pageHotBook.findViewById(R.id.page_hot_book_recycler_view);
        mRecyclerViewHotBook.setLayoutManager(mLinearLayoutManagerHotBook);
        mBookRecyclerViewAdapterHotBook = new BookRecyclerViewAdapter(mBookListHotBook,
                BookViewerActivity.this);
        mRecyclerViewHotBook.setAdapter(mBookRecyclerViewAdapterHotBook);
        View pageReaderLeft = layoutInflater.inflate(R.layout.page_reader_left, null);
        mRecyclerViewReaderLeft = pageReaderLeft.findViewById(R.id.page_reader_left_recycler_view);
        mRecyclerViewReaderLeft.setLayoutManager(mLinearLayoutManagerReaderLeft);
        mBookRecyclerViewAdapterReaderLeft = new BookRecyclerViewAdapter(mBookListReaderLeft,
                BookViewerActivity.this);
        mRecyclerViewReaderLeft.setAdapter(mBookRecyclerViewAdapterReaderLeft);
        viewContainer.add(pageHotBook);
        viewContainer.add(pageReaderLeft);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewContainer.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewContainer.get(position));
                return viewContainer.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(viewContainer.get(position));
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });

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
                        book.setCoverPath(imageTemp.getPath() + "/" +
                                book.getId() + ".jpg");
                        mBookListHotBook.add(book);
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

    private void getReaderLeftBook() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                InputStream inputStream = null;
                try {
                    url = new URL("http://api.zhuishushenqi.com/ranking/564547c694f1c6a144ec979b");
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
                        book.setCoverPath(imageTemp.getPath() + "/" +
                                book.getId() + ".jpg");
                        mBookListReaderLeft.add(book);
                    }
                    Message message = new Message();
                    message.what = INTENT_MODE_READER_LEFT;
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

}
