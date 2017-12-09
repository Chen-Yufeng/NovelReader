package com.ifchan.reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ifchan.reader.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daily on 12/8/17.
 */

public class IndexExpandableListViewAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private String[][] index;


    public IndexExpandableListViewAdapter(Context context, String[][] index) {
        mContext = context;
        this.index = index;
    }

    public void setIndex(String[][] index) {
        this.index = index;
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return index[groupPosition].length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return "目录";
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return index[groupPosition][childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup
            parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.index_tab, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.indexTab = convertView.findViewById(R.id.index_tab_text_view);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.indexTab.setText("目录");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View
            convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.index_content, parent,
                    false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.indexContent = convertView.findViewById(R.id.index_content_text_view);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.indexContent.setText(index[groupPosition][childPosition]);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class GroupViewHolder {
        TextView indexTab;
    }

    static class ChildViewHolder {
        TextView indexContent;
    }
}
