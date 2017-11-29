package com.ifchan.reader.bookviewer;

import android.app.AliasActivity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ifchan.reader.AllClassActivity;
import com.ifchan.reader.R;
import com.ifchan.reader.adapter.MyFragmentPagerAdapter;
import com.ifchan.reader.fragment.FragmentHot;
import com.ifchan.reader.fragment.MyBasicFragment;

public class AllClassBookViewerActivity extends AppCompatActivity implements MyBasicFragment
        .OnAttachedListener {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;

    private TabLayout.Tab one;
    private TabLayout.Tab two;
    private TabLayout.Tab three;
    private TabLayout.Tab four;
    private TabLayout.Tab five;

    private String sex;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_class_book_viewer);

        receiveIntent();
        init();
    }

    private void receiveIntent() {
        Intent intentReceived = getIntent();
        sex = intentReceived.getStringExtra(AllClassActivity.ALL_CLASS_SEX);
        type = intentReceived.getStringExtra(AllClassActivity.CLASS);
    }

    private void init() {
        mViewPager = findViewById(R.id.all_class_view_pager);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(myFragmentPagerAdapter);

        //将TabLayout与ViewPager绑定在一起
        mTabLayout = findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);

        //指定Tab的位置
        one = mTabLayout.getTabAt(0);
        two = mTabLayout.getTabAt(1);
        three = mTabLayout.getTabAt(2);
        four = mTabLayout.getTabAt(3);
        five = mTabLayout.getTabAt(4);

        //设置Tab的图标，假如不需要则把下面的代码删去
        one.setIcon(R.mipmap.ic_launcher);
        two.setIcon(R.mipmap.ic_launcher);
        three.setIcon(R.mipmap.ic_launcher);
        four.setIcon(R.mipmap.ic_launcher);
        five.setIcon(R.mipmap.ic_launcher);

    }

    @Override
    public String getSex() {
        return sex;
    }

    @Override
    public String getType() {
        return type;
    }
}
