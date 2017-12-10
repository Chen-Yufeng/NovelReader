package com.ifchan.reader.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ifchan.reader.R;
import com.ifchan.reader.adapter.RichTextRecyclerViewAdapter;
import com.zzhoujay.richtext.RichText;

import java.io.File;

/**
 * Created by daily on 11/25/17.
 */

// TODO: 11/29/17 Download Covers

public class FragmentCommunity extends MyBasicFragment {
    private View mView;
    private RecyclerView mRecyclerView;
    private RichTextRecyclerViewAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_community, container, false);

        init();
        return mView;
    }

    private void init() {
        initRichTextManager();
        initRecyclerView();
    }

    private void initRichTextManager() {
        File externalFolder = Environment.getExternalStorageDirectory();
        File richTextTemp = new File(externalFolder.getPath() + "/Reader/temp/richtext");
        RichText.initCacheDir(richTextTemp);
    }

    private void initRecyclerView() {
        mRecyclerView = mView.findViewById(R.id.fragment_community_recycler_view);
        mAdapter = new RichTextRecyclerViewAdapter(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
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
