package com.ifchan.reader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ifchan.reader.adapter.BookRecyclerViewAdapter;
import com.ifchan.reader.adapter.IndexRecyclerViewAdapter;
import com.ifchan.reader.entity.Book;
import com.ifchan.reader.entity.Index;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BookDetailsActivity extends AppCompatActivity {
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INDEX_LOADED:
                    InitIndexRecyclerView();
                    break;
            }
        }
    };
    private final String TAG = "@vir BookDetailsActivity";
    private static final int INDEX_LOADED = 0;
    private Book mBook;
    private List<Index> mIndexList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        receive();
        initBasicBookInfo();
        initIndex();
    }

    private void receive() {
        Intent intent = getIntent();
        mBook = (Book) intent.getSerializableExtra(BookRecyclerViewAdapter.INTENT_BOOK_FOR_DETAILS);
    }

    private void initBasicBookInfo() {
        TextView author = findViewById(R.id.book_details_author);
        TextView tvClass =findViewById(R.id.book_details_class);
        author.setText(mBook.getAuthor()+" | ");
        tvClass.setText(mBook.getMajorCate());
        ImageView imageView = findViewById(R.id.book_details_image_view);
        File file = new File(mBook.getCoverPath());
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(mBook.getCoverPath());
            imageView.setImageBitmap(bitmap);
        }

        Button buttonAdd, buttonReading;
        buttonAdd = findViewById(R.id.book_details_add_to_follow_list);
        buttonReading = findViewById(R.id.book_details_start_reading_button);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        buttonReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TextView textViewFollowers, textViewRetentionRatio, textViewDailyUpdate,
                textViewShortIntroduce;
        textViewFollowers = findViewById(R.id.book_details_rtv4);
        textViewRetentionRatio = findViewById(R.id.book_details_rtv5);
        textViewDailyUpdate = findViewById(R.id.book_details_rtv6);
        textViewShortIntroduce = findViewById(R.id.book_details_short_introduce);
        textViewFollowers.setText(Integer.toString(mBook.getLatelyFollower()));
        textViewRetentionRatio.setText(mBook.getRetentionRatio());
        textViewShortIntroduce.setText(mBook.getShortIntro());
    }

    private void initIndex() {
        mIndexList = new ArrayList<>();
        getIndex();
    }

    private void getIndex() {
        //http://api.zhuishushenqi.com/toc/577b4fb1a3d28cdb512440f9?view=chapters
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                InputStream inputStream = null;
                try {
                    url = new URL("http://api.zhuishushenqi.com/mix-atoc/" + mBook.getId()
                            + "?view=chapters");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setRequestMethod("GET");
                    connection.setDoOutput(true);
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
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
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject indexJsonObject = jsonArray.getJSONObject(i);
                            Index index = new Index(indexJsonObject.getString("title"),
                                    indexJsonObject.getString("link"));
                            mIndexList.add(index);
                        }
                        Message message = new Message();
                        message.what = INDEX_LOADED;
                        mHandler.sendMessage(message);
                    }
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

    private void InitIndexRecyclerView() {
        RecyclerView indexRecyclerView = findViewById(R.id.book_details_index_recycler_view);
        IndexRecyclerViewAdapter indexRecyclerViewAdapter = new IndexRecyclerViewAdapter
                (BookDetailsActivity.this, mIndexList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BookDetailsActivity.this);
        indexRecyclerView.setLayoutManager(linearLayoutManager);
        indexRecyclerView.setAdapter(indexRecyclerViewAdapter);
    }
}
