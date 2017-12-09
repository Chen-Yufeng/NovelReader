package com.ifchan.reader.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ifchan.reader.AllClassActivity;
import com.ifchan.reader.R;
import com.ifchan.reader.bookviewer.BookViewerActivity;

/**
 * Created by daily on 11/25/17.
 */

// TODO: 11/29/17 Download Covers

public class FragmentFindNew extends MyBasicFragment {

    public static final String INTENT_MODE = "INTENT_MODE";
    public static final int INTENT_MODE_HOT_BOOK = 1;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_find_new, container, false);
        init();
        return mView;
    }

    private void init() {
        // TODO: 12/9/17  .findViewById???
        LinearLayout linearLayoutHotBook = mView.findViewById(R.id.linear_layout_hot_book);
        linearLayoutHotBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), BookViewerActivity.class);
                // TODO: 12/9/17  ()???
                intent.putExtra(INTENT_MODE, INTENT_MODE_HOT_BOOK);
                startActivity(intent);
            }
        });
        LinearLayout linearLayoutLatestBook = mView.findViewById(R.id.linear_layout_all_book);
        linearLayoutLatestBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AllClassActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
