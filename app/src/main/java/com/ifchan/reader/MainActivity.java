package com.ifchan.reader;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.testscroll.TextReaderActivity;
import com.ifchan.reader.adapter.MyMainFragmentAdapter;
import com.ifchan.reader.utils.AppUtils;
import com.ifchan.reader.utils.CacheUtil;
import com.ifchan.reader.utils.PermissionUtils;

import java.io.File;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements NavigationView
        .OnNavigationItemSelectedListener {

    public static final String INTENT_MODE = "INTENT_MODE";
    public static final int INTENT_MODE_HOT_BOOK = 1;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyMainFragmentAdapter mMyMainFragmentAdapter;

    private TabLayout.Tab one;
    private TabLayout.Tab two;
    private TabLayout.Tab three;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();
        init();
        initTab();
        initDrawerView();
    }

    private void initTab() {
        mViewPager = findViewById(R.id.main_view_pager);
        mMyMainFragmentAdapter = new MyMainFragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMyMainFragmentAdapter);

        //将TabLayout与ViewPager绑定在一起
        mTabLayout = findViewById(R.id.main_tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);

        //指定Tab的位置
        one = mTabLayout.getTabAt(0);
        two = mTabLayout.getTabAt(1);
        three = mTabLayout.getTabAt(2);

        //设置Tab的图标，假如不需要则把下面的代码删去
        one.setIcon(R.mipmap.ic_launcher);
        two.setIcon(R.mipmap.ic_launcher);
        three.setIcon(R.mipmap.ic_launcher);
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
        PermissionUtils.applyForPhoneStatePermission(this,this);
    }

    private void init() {
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)
                findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        AppUtils.init(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_search:
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initDrawerView() {
        NavigationView navigationView = findViewById(R.id.main_navigation);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_drawer_menu_clear_cache:
                CacheUtil.clearImageCache();
                CacheUtil.clearRichTextCache();
                break;
            case R.id.main_drawer_menu_clear_bookshelf:

                break;
        }
        DrawerLayout drawerLayout = findViewById(R.id.main_drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
