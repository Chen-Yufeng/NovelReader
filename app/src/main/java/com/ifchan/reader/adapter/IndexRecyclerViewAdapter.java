package com.ifchan.reader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ifchan.reader.R;
import com.ifchan.reader.entity.Index;

import java.util.List;

/**
 * Created by daily on 12/1/17.
 */

public class IndexRecyclerViewAdapter extends RecyclerView.Adapter<IndexRecyclerViewAdapter
        .ViewHolder> {
    private Context mContext;
    private List<Index> mIndexList;

    public IndexRecyclerViewAdapter(Context context, List<Index> indexList) {
        mContext = context;
        mIndexList = indexList;
    }

    @Override

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_comment, parent
                , false);
        final IndexRecyclerViewAdapter.ViewHolder holder = new IndexRecyclerViewAdapter.ViewHolder(view);
        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Index index = mIndexList.get(position);
        holder.mTextView.setText(index.getTitle());
    }

    @Override
    public int getItemCount() {
        return mIndexList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mLinearLayout;
        private TextView mTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            mLinearLayout = itemView.findViewById(R.id.item_comment_linear_layout);
            mTextView = itemView.findViewById(R.id.item_comment_text_view);
        }
    }
}
