package com.ifchan.reader.fragment;

import android.support.v4.app.Fragment;

import com.ifchan.reader.entity.Book;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by daily on 11/29/17.
 */

public abstract class MyBasicFragment extends Fragment {
    FragmentHot.OnAttachedListener mOnAttachedListener;

    public interface OnAttachedListener {
        public String getSex();

        public String getType();
    }


    protected void sortBook(List<Book> books) {
        Comparator comparator = new Comparator<Book>() {

            @Override
            public int compare(Book o1, Book o2) {
                return o2.getLatelyFollower() - o1.getLatelyFollower();
            }
        };
        Collections.sort(books, comparator);
    }
}
