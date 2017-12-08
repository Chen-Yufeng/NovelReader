package com.ifchan.reader.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by daily on 11/23/17.
 */

public class AllClassGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mClassName;
    private List<String> mBookCount;

    public AllClassGridViewAdapter(Context context, List<String> className, List<String> bookCount) {
        mContext = context;
        mClassName = className;
        mBookCount = bookCount;
    }

    @Override

    public int getCount() {
        return mClassName.size();
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
        textView.setText(mClassName.get(position)+'\n'+mBookCount.get(position));
        return textView;
    }
}
