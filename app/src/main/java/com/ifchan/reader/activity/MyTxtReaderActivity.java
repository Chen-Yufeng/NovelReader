package com.ifchan.reader.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.ifchan.reader.BookDetailsActivity;
import com.ifchan.reader.R;
import com.ifchan.reader.view.TxtView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MyTxtReaderActivity extends Activity {
    private String mContants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_txt_reader);

        initText();
        initViewPager();
    }

    private void initText() {
        Intent intent = getIntent();
        File txt = new File(intent.getStringExtra(BookDetailsActivity.TXT_PATH));
        StringBuilder contants = new StringBuilder();
        if (txt.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(txt)));
                String line;
                while ((line = reader.readLine()) != null) {
                    contants.append(line);
                }
                mContants = contants.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initViewPager() {
        ViewPager viewPager = findViewById(R.id.activity_my_txt_reader_viewpager);
        List<View> viewContainer = new ArrayList<>();
        LayoutInflater inflater = getLayoutInflater().from(MyTxtReaderActivity.this);
        View view0 = inflater.inflate(R.layout.txtreader_page1, null);
        View view1 = inflater.inflate(R.layout.txtreader_page1, null);
        View view2 = inflater.inflate(R.layout.txtreader_page1, null);
        viewContainer.add(view0);
        viewContainer.add(view1);
        viewContainer.add(view2);
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
    }

}
