package com.ifchan.reader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ifchan.reader.adapter.BookRecyclerViewAdapter;
import com.ifchan.reader.adapter.IndexExpandableListViewAdapter;
import com.ifchan.reader.entity.Book;
import com.ifchan.reader.entity.Index;
import com.ifchan.reader.helper.BookshelfDataBaseHelper;
import com.ifchan.reader.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BookDetailsActivity extends AppCompatActivity {
    private static final int IMAGE_LOADED = 0;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INDEX_LOADED:
                    renewBookImage();
                    break;
            }
        }
    };
    private final String TAG = "@vir BookDetailsActivity";
    private static final int INDEX_LOADED = 0;
    private Book mBook;
    private List<Index> mIndexList;
    private static boolean isFloded = true;
    private BookshelfDataBaseHelper mDataBaseHelper;
    private SQLiteDatabase db;
    private int databaseSize = 0;
    private boolean haveBeenAdded = false;
    private Button buttonAdd, buttonReading;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        receive();
        initBasicBookInfo();
        initDataBase();
        initIndex();
        test();
    }


    private void initDataBase() {
        mDataBaseHelper = BookshelfDataBaseHelper.getInstance(BookDetailsActivity.this);
        db = mDataBaseHelper.getWritableDatabase();
        Cursor cursor = db.query("Bookshelf", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                databaseSize++;
                String bookid = cursor.getString(cursor.getColumnIndex("bookid"));
                if (bookid.equals(mBook.getId())) {
                    haveBeenAdded = true;
                    buttonAdd.setEnabled(false);
                    buttonAdd.setText("已添加");
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void receive() {
        Intent intent = getIntent();
        mBook = (Book) intent.getSerializableExtra(BookRecyclerViewAdapter.INTENT_BOOK_FOR_DETAILS);
    }

    private void initBasicBookInfo() {
        TextView name = findViewById(R.id.book_details_book_name);
        TextView author = findViewById(R.id.book_details_author);
        TextView tvClass = findViewById(R.id.book_details_class);
        name.setText(mBook.getTitle());
        author.setText(mBook.getAuthor() + " | ");
        tvClass.setText(mBook.getMajorCate());
        imageView = findViewById(R.id.book_details_image_view);
        File file = new File(mBook.getCoverPath());
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(mBook.getCoverPath());
            imageView.setImageBitmap(bitmap);
        } else {
            getBookImage(mBook);
        }

        buttonAdd = findViewById(R.id.book_details_add_to_follow_list);
        buttonReading = findViewById(R.id.book_details_start_reading_button);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!haveBeenAdded) {
                    mDataBaseHelper.addToBookshelf(db, databaseSize + 1, mBook.getId(), mBook
                                    .getTitle(), mBook.getAuthor(), mBook.getShortIntro(), mBook
                                    .getCover(),

                            mBook.getCoverPath(), mBook.getSite(), mBook.getLatelyFollower(), mBook
                                    .getRetentionRatio(), mBook.getMajorCate());
                    databaseSize++;
                    buttonAdd.setText("已添加");
                    buttonAdd.setEnabled(false);
                }
            }
        });
        buttonReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TextView textViewFollowers, textViewRetentionRatio, textViewWords,
                textViewShortIntroduce;
        textViewFollowers = findViewById(R.id.book_details_rtv4);
        textViewRetentionRatio = findViewById(R.id.book_details_rtv5);
        textViewWords = findViewById(R.id.book_details_rtv6);
        textViewShortIntroduce = findViewById(R.id.book_details_short_introduce);
        textViewFollowers.setText(Integer.toString(mBook.getLatelyFollower()));
        textViewRetentionRatio.setText(mBook.getRetentionRatio());
        textViewShortIntroduce.setText(mBook.getShortIntro());
    }

    private void getBookImage(final Book book) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream imageInputStream = null;
                FileOutputStream fileOutputStream = null;
                int count = 0;
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
        }).start();
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

    private void test() {
        final ExpandableListView expandableListView = findViewById(R.id
                .book_details_expandable_list_view);
        final IndexExpandableListViewAdapter adapter = new IndexExpandableListViewAdapter
                (BookDetailsActivity
                        .this, new String[][]{{"1", "2", "3", "1", "2", "3", "1", "2", "3"}});
        expandableListView.setAdapter(adapter);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
                                        long id) {
                if (isFloded) {
                    Utility.setListViewHeightBasedOnChildren(expandableListView, adapter);
//                    Util.setListViewHeightBasedOnChildren(expandableListView);
                    isFloded = false;
                } else {
                    ViewGroup.LayoutParams params = expandableListView.getLayoutParams();
                    params.height = 200;
                    expandableListView.setLayoutParams(params);
//                    Util.setListViewHeightBasedOnChildren(expandableListView);
                    isFloded = true;
                }
                return false;
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int
                    childPosition, long id) {
                Toast.makeText(BookDetailsActivity.this, "Click", Toast.LENGTH_LONG);
                return true;
            }
        });
    }

    private void renewBookImage() {
        File file = new File(mBook.getCoverPath());
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(mBook.getCoverPath());
            imageView.setImageBitmap(bitmap);
        }
    }
}
