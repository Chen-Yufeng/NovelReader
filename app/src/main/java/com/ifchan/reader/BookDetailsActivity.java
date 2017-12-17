package com.ifchan.reader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ifchan.reader.activity.MyTxtReaderActivity;
import com.ifchan.reader.adapter.BookRecyclerViewAdapter;
import com.ifchan.reader.adapter.IndexExpandableListViewAdapter;
import com.ifchan.reader.entity.Book;
import com.ifchan.reader.entity.Index;
import com.ifchan.reader.helper.BookshelfDataBaseHelper;
import com.ifchan.reader.utils.imagechcheutils.MyBitmapUtils;
import com.ifchan.reader.utils.novel.HeaderUtil;
import com.ifchan.reader.utils.novel.NovelTextGetter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class BookDetailsActivity extends AppCompatActivity {
    private static final int IMAGE_LOADED = 0;
    public static final String TXT_PATH = "TXT_PATH";
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INDEX_LOADED:
                    initIndexView();
                    renewBookImage();
                    buttonAdd.setEnabled(true);
                    break;
            }
        }
    };
    private int indexCount;
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
    private ScrollView mScrollView;
    private NovelTextGetter mNovelTextGetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        initScrollView();
        receive();
        initBasicBookInfo();
        initDataBase();
        initIndex();
        loadIndex();
    }

    private void loadIndex() {
        mNovelTextGetter = new NovelTextGetter();
        mNovelTextGetter.downloadIndexList(mBook.getId());
    }

    private void initScrollView() {
        mScrollView = findViewById(R.id.book_details_scroll_view);
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
                    buttonAdd.setBackgroundColor(Color.GRAY);
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
        buttonAdd.setEnabled(false);
        buttonReading = findViewById(R.id.book_details_start_reading_button);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!haveBeenAdded) {
                    mDataBaseHelper.addToBookshelf(db, databaseSize + 1, mBook.getId(), mBook
                                    .getTitle(), mBook.getAuthor(), mBook.getShortIntro(), mBook
                                    .getCover(),
                            mBook.getCoverPath(), mBook.getSite(), mBook.getLatelyFollower(), mBook
                                    .getRetentionRatio(), mBook.getMajorCate(), Integer.toString
                                    (indexCount));
                    databaseSize++;
                    buttonAdd.setText("已添加");
                    buttonAdd.setEnabled(false);
                }
            }
        });
        buttonReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTextReader();
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

    private void startTextReader() {
        Intent intent = new Intent(getApplicationContext(), MyTxtReaderActivity.class);
//        Intent intent = new Intent(getApplicationContext(), TextReaderActivity.class);
        startActivity(intent);
    }

    private void getBookImage(final Book book) {
        new MyBitmapUtils().disPlay(imageView, book.getCover());
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
                    indexCount = jsonArray.length();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject indexJsonObject = jsonArray.getJSONObject(i);
                        Index index = new Index(indexJsonObject.getString("title"),
                                indexJsonObject.getString("link"));
                        mIndexList.add(index);
                    }
                    Message message = new Message();
                    message.what = INDEX_LOADED;
                    mHandler.sendMessage(message);
//                    }
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

    private void initIndexView() {
        final ExpandableListView expandableListView = findViewById(R.id
                .book_details_expandable_list_view);
        List<String[]> s = new ArrayList<>();
        int index;
        for (index = 0; index < mIndexList.size() - 20; index += 20) {
            String[] temp = new String[20];
            for (int i = 0; i < 20; i++) {
                temp[i] = mIndexList.get(index + i).getTitle();
            }
            s.add(temp);
        }
        String[] temp = new String[mIndexList.size() - index];
        for(int i = 0;i < mIndexList.size() - index;i++) {
            temp[i] = mIndexList.get(index + i).getTitle();
        }
        s.add(temp);
        final String[][] indexString = s.toArray(new String[s.size()][]);

        final IndexExpandableListViewAdapter adapter = new IndexExpandableListViewAdapter
                (BookDetailsActivity
                        .this, indexString);
        expandableListView.setAdapter(adapter);
//        Utility.setListViewHeightBasedOnChildren(expandableListView, adapter);
        setListViewHeight(expandableListView);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
                                        long id) {
                setListViewHeight(parent,groupPosition);
//                if (isFloded) {
//                    ViewGroup.LayoutParams params = expandableListView.getLayoutParams();
//                    params.height = 65 * indexString.length + 170;
//                    expandableListView.setLayoutParams(params);
////                    Util.setListViewHeightBasedOnChildren(expandableListView);
//                    isFloded = false;
//                } else {
//                    Utility.setListViewHeightBasedOnChildren(expandableListView, adapter);
////                    Util.setListViewHeightBasedOnChildren(expandableListView);
//                    isFloded = true;
//                }
                return false;
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int
                    childPosition, long id) {
                if (mNovelTextGetter.isLoaded()) {
                    mNovelTextGetter.download(mBook.getTitle(),groupPosition * 20 + childPosition);
                    Intent intent = new Intent(BookDetailsActivity.this, MyTxtReaderActivity.class);
                    File externalFolder = Environment.getExternalStorageDirectory();
                    File temp = new File(externalFolder.getPath() + "/Reader/temp/novel/"+
                            mBook.getTitle());
                    try {
                        intent.putExtra(TXT_PATH, externalFolder.getPath() + "/Reader/temp/novel/"+
                                mBook.getTitle() + "/" + URLEncoder.encode(mIndexList.get
                                (groupPosition * 20 + childPosition).getTitle(),"UTF-8"));
                        startActivity(intent);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
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

    private void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

//如何实现的?
    private void setListViewHeight(ExpandableListView listView,
                                   int group) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();

    }
}
