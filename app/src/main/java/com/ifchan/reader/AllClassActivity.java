package com.ifchan.reader;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ScrollView;

import com.ifchan.reader.adapter.GridViewAdapter;
import com.ifchan.reader.bookviewer.AllClassBookViewerActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AllClassActivity extends AppCompatActivity {
    private static final int LOADED_JSON = 1;
    public static final String ALL_CLASS_SEX = "ALL_CLASS_SEX";
    public static final String IS_MALE = "male";
    public static final String IS_FEMALE = "female";
    public static final String CLASS = "CLASS";
    private ScrollView scrollView;
    private GridView gridViewMale;
    private GridView gridViewFemale;
    private GridViewAdapter gridViewAdapterMale;
    private GridViewAdapter gridViewAdapterFemale;
    private List<String> mClassNameMale = new ArrayList<>();
    private List<String> mBookCountMale = new ArrayList<>();
    private List<String> mClassNameFemale = new ArrayList<>();
    private List<String> mBookCountFemale = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADED_JSON:
//                    gridViewAdapterMale.notifyDataSetChanged();
//                    gridViewMale.invalidateViews();
//                    gridViewAdapterFemale.notifyDataSetChanged();
//                    gridViewFemale.invalidateViews();
                    init();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_class);

        getJSON();
    }

    private void getJSON() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                InputStream inputStream = null;
                try {
                    url = new URL("http://api.zhuishushenqi.com/cats/lv2/statistics");
                    inputStream = url.openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    JSONObject jsonObjectSex = new JSONObject(builder.toString());
                    JSONArray jsonArrayMale = jsonObjectSex.getJSONArray("male");
                    for (int i = 0; i < jsonArrayMale.length(); i++) {
                        JSONObject jsonObjectClass = jsonArrayMale.getJSONObject(i);
                        mClassNameMale.add(jsonObjectClass.getString("name"));
                        mBookCountMale.add(Integer.toString(jsonObjectClass.getInt("bookCount")));
                    }
                    JSONArray jsonArrayFemale = jsonObjectSex.getJSONArray("female");
                    for (int i = 0; i < jsonArrayFemale.length(); i++) {
                        JSONObject jsonObjectClass = jsonArrayFemale.getJSONObject(i);
                        mClassNameFemale.add(jsonObjectClass.getString("name"));
                        mBookCountFemale.add(Integer.toString(jsonObjectClass.getInt
                                ("bookCount")));
                    }
                    Message message = new Message();
                    message.what = LOADED_JSON;
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void init() {
//        scrollView = findViewById(R.id.all_class_scroll_view);
//        scrollView.post(new Runnable() {
//            @Override
//            public void run() {
//                scrollView.scrollTo(0, scrollView.getBottom());
//            }
//        });
        gridViewMale = findViewById(R.id.all_class_male_grid_view);
        gridViewFemale = findViewById(R.id.all_class_female_grid_view);
        gridViewAdapterMale = new GridViewAdapter(AllClassActivity.this, mClassNameMale,
                mBookCountMale);
        gridViewAdapterFemale = new GridViewAdapter(AllClassActivity.this, mClassNameFemale,
                mBookCountFemale);
        gridViewMale.setAdapter(gridViewAdapterMale);
        gridViewFemale.setAdapter(gridViewAdapterFemale);
        gridViewMale.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AllClassActivity.this, AllClassBookViewerActivity.class);
                intent.putExtra(ALL_CLASS_SEX,IS_MALE);
                intent.putExtra(CLASS, mClassNameMale.get(position));
                startActivity(intent);
            }
        });
        gridViewFemale.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AllClassActivity.this, AllClassBookViewerActivity.class);
                intent.putExtra(ALL_CLASS_SEX,IS_FEMALE);
                intent.putExtra(CLASS, mClassNameFemale.get(position));
                startActivity(intent);
            }
        });
    }
}
