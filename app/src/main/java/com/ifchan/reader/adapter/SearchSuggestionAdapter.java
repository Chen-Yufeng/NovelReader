package com.ifchan.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ifchan.reader.BookDetailsActivity;
import com.ifchan.reader.R;
import com.ifchan.reader.entity.Book;

import java.io.File;
import java.util.List;

/**
 * Created by daily on 11/23/17.
 */

public class SearchSuggestionAdapter extends RecyclerView.Adapter<SearchSuggestionAdapter
        .ViewHolder> {
    public static final String INTENT_BOOK_FOR_DETAILS = "INTENT_BOOK_FOR_DETAILS";
    private List<String> mStringList;
    private Context mContext;

    public SearchSuggestionAdapter(List<String> stringList, Context context) {
        mStringList = stringList;
        mContext = context;
    }

    public void setStringList(List<String> stringList) {
        mStringList = stringList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_suggestion, parent
                , false);
        final ViewHolder holder = new ViewHolder(view);
        holder.bookView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 12/8/17 此处要根据书名获取id 
//                Intent intent = new Intent(mContext, BookDetailsActivity.class);
//                intent.putExtra(BookRecyclerViewAdapter.INTENT_BOOK_FOR_DETAILS,)
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(mStringList.get(position));
    }

    @Override
    public int getItemCount() {
        return mStringList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private View bookView;
        private TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            bookView = itemView;
            name = itemView.findViewById(R.id.item_search_suggestion_text_view);
        }
    }
}

