package com.ifchan.reader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ifchan.reader.R;
import com.ifchan.reader.entity.Book;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by daily on 11/23/17.
 */

public class BookRecyclerViewAdapter extends RecyclerView.Adapter<BookRecyclerViewAdapter
        .ViewHolder> {
    private final String TAG = "@vir BRVAdapter";
    private List<Book> mBookList;
    private Context mContext;

    public BookRecyclerViewAdapter(List<Book> bookList, Context context) {
        this.mBookList = bookList;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_book, parent
                , false);
        final ViewHolder holder = new ViewHolder(view);
        holder.bookView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Book book = mBookList.get(position);
        holder.name.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.shortIntro.setText(book.getShortIntro());
        holder.stat.setText(book.getLatelyFollower() + "人在追 | " + book.getRetentionRatio() +
                "%读者存留");
        File file = new File(book.getCoverPath());
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(book.getCoverPath());
            holder.mImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private View bookView;
        private ImageView mImageView;
        private TextView name;
        private TextView author;
        private TextView shortIntro;
        private TextView stat;

        public ViewHolder(View itemView) {
            super(itemView);
            bookView = itemView;
            mImageView = itemView.findViewById(R.id.item_book_image_view);
            name = itemView.findViewById(R.id.item_book_book_name);
            author = itemView.findViewById(R.id.item_book_book_author);
            shortIntro = itemView.findViewById(R.id.item_book_book_short_intro);
            stat = itemView.findViewById(R.id.item_book_book_stat);
        }
    }
}

