package com.ifchan.reader.adapter;

/**
 * Created by daily on 11/25/17.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ifchan.reader.fragment.FragmentBookshelf;
import com.ifchan.reader.fragment.FragmentCommunity;
import com.ifchan.reader.fragment.FragmentFindNew;
import com.ifchan.reader.fragment.FragmentHot;
import com.ifchan.reader.fragment.FragmentMonthly;
import com.ifchan.reader.fragment.FragmentNew;
import com.ifchan.reader.fragment.FragmentOver;
import com.ifchan.reader.fragment.FragmentReputation;

public class MyMainFragmentAdapter extends FragmentPagerAdapter {

    private String[] mTitles = new String[]{"书架", "社区","发现"};

    public MyMainFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return new FragmentCommunity();
        } else if (position == 2) {
            return new FragmentFindNew();
        }
        return new FragmentBookshelf();
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
