package com.ifchan.reader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ifchan.reader.R;
import com.zzhoujay.richtext.RichText;

import java.util.logging.Handler;

/**
 * Created by daily on 12/10/17.
 */

public class RichTextRecyclerViewAdapter extends RecyclerView.Adapter<RichTextRecyclerViewAdapter
        .ViewHolder> {
    private Context mContext;
    private String[] testString = new String[]{
            "## Hello, World.",
            "`f(x)`",
            "# MarkDown \n - IfChan \n - I'm rich text.\n## head2\n### head3"
    };

    public RichTextRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // TODO: 12/10/17 What is the ViewGroup parent here means?
        View view = LayoutInflater.from(mContext).inflate(R.layout.rich_text_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        RichText.from(testString[position]).singleLoad(false).into(holder.mTextView);
        RichText.fromMarkdown(testString[position]).into(holder.mTextView);
    }

    @Override
    public int getItemCount() {
        return testString.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.rich_text_contain);
        }
    }
}
