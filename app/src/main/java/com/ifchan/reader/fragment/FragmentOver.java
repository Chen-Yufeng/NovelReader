package com.ifchan.reader.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ifchan.reader.R;

/**
 * Created by daily on 11/25/17.
 */

public class FragmentOver extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_over, container, false);
    }
}
