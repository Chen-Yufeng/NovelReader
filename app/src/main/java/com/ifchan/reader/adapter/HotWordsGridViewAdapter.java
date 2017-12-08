package com.ifchan.reader.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by daily on 11/23/17.
 */

public class HotWordsGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mHotWords;

    public HotWordsGridViewAdapter(Context context,List<String> hotWords) {
        mContext = context;
        mHotWords = hotWords;
    }

    @Override

    public int getCount() {
        return mHotWords.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = new TextView(mContext);
            textView.setLayoutParams(new GridView.LayoutParams(200, 200));
            //add sth???
//            textView.setGravity(Gravity.CENTER);
//            textView.setPadding(8, 8, 8, 8);
        } else {
            textView = (TextView) convertView;
        }
        textView.setText(mHotWords.get(position));
        return textView;
    }
}
