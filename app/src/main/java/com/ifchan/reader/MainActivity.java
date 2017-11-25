package com.ifchan.reader;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.ifchan.reader.bookviewer.BookViewerActivity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    public static final String INTENT_MODE = "INTENT_MODE";
    public static final int INTENT_MODE_HOT_BOOK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();
        init();
    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new
                    String[]{READ_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new
                    String[]{WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void init() {
        LinearLayout linearLayoutHotBook = findViewById(R.id.linear_layout_hot_book);
        linearLayoutHotBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, BookViewerActivity.class);
                intent.putExtra(INTENT_MODE, INTENT_MODE_HOT_BOOK);
                startActivity(intent);
            }
        });
        LinearLayout linearLayoutLatestBook = findViewById(R.id.linear_layout_all_book);
        linearLayoutLatestBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllClassActivity.class);
                startActivity(intent);
            }
        });
    }
}
