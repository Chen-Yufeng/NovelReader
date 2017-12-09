package com.ifchan.reader.utils;

/**
 * Created by daily on 12/9/17.
 * 不是很清楚
 */

import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.ifchan.reader.adapter.IndexExpandableListViewAdapter;

public class Utility {
    public static void setListViewHeightBasedOnChildren(ExpandableListView listView,
                                                        IndexExpandableListViewAdapter
                                                                listAdapter) {
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getChildrenCount(0); i++) {
            boolean isLastChile = i + 1 == listAdapter.getChildrenCount(0) ? true : false;
            View listItem = listAdapter.getChildView(0, i, isLastChile, null, listView);
            listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + 200;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
