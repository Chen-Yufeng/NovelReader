package com.ifchan.reader.fragment;

import android.support.v4.app.Fragment;


/**
 * Created by daily on 11/29/17.
 */

public abstract class MyBasicFragment extends Fragment {
    FragmentHot.OnAttachedListener mOnAttachedListener;

    public interface OnAttachedListener {
        public String getSex();

        public String getType();
    }
}
