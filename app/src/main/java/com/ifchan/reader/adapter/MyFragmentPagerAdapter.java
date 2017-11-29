package com.ifchan.reader.adapter;

/**
 * Created by daily on 11/25/17.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ifchan.reader.fragment.FragmentHot;
import com.ifchan.reader.fragment.FragmentNew;
import com.ifchan.reader.fragment.FragmentOver;
import com.ifchan.reader.fragment.FragmentReputation;
import com.ifchan.reader.fragment.FragmentMonthly;

/**
 * Created by Carson_Ho on 16/7/22.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mTitles = new String[]{"热门", "新书","好评","完结","包月"};

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return new FragmentNew();
        } else if (position == 2) {
            return new FragmentReputation();
        }else if (position==3){
            return new FragmentOver();
        } else if (position == 4) {
            return new FragmentMonthly();
        }
        return new FragmentHot();
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
