package com.ifchan.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ifchan.reader.BookDetailsActivity;
import com.ifchan.reader.R;
import com.ifchan.reader.entity.Book;
import com.ifchan.reader.utils.imagechcheutils.MyBitmapUtils;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by daily on 11/23/17.
 */

public class BookRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView
        .ViewHolder> {
    public static final String INTENT_BOOK_FOR_DETAILS = "INTENT_BOOK_FOR_DETAILS";
    private final String TAG = "@vir BRVAdapter";
    private List<Book> mBookList;
    private Context mContext;
    // 普通布局
    private final int TYPE_ITEM = 1;
    // 脚布局
    private final int TYPE_FOOTER = 2;
    // 当前加载状态，默认为加载完成
    private int loadState = 2;
    // 正在加载
    public final static int LOADING = 1;
    // 加载完成
    public final static int LOADING_COMPLETE = 2;
    // 加载到底
    public final static int LOADING_END = 3;

    private boolean enableFooter = false;


    public BookRecyclerViewAdapter(List<Book> bookList, Context context) {
        this.mBookList = bookList;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_book, parent
                    , false);
            final ViewHolder holder = new ViewHolder(view);
            holder.bookView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, BookDetailsActivity.class);
                    intent.putExtra(INTENT_BOOK_FOR_DETAILS, mBookList.get(holder.getAdapterPosition
                            ()));
                    mContext.startActivity(intent);
                }
            });

            return holder;
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.refresh_footer,
                    parent, false);
            return new FootViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            Book book = mBookList.get(position);
            viewHolder.name.setText(book.getTitle());
            viewHolder.author.setText(book.getAuthor());
            viewHolder.shortIntro.setText(book.getShortIntro());
            viewHolder.stat.setText(book.getLatelyFollower() + "人在追 | " + book.getRetentionRatio() +
                    "%读者存留");
//        File file = new File(book.getCoverPath());
//        if (file.exists()) {
//            Bitmap bitmap = BitmapFactory.decodeFile(book.getCoverPath());
//            holder.mImageView.setImageBitmap(bitmap);
//        }
            new MyBitmapUtils().disPlay(viewHolder.mImageView, book.getCover());
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            switch (loadState) {
                case LOADING: // 正在加载
                    footViewHolder.pbLoading.setVisibility(View.VISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.VISIBLE);
                    footViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_COMPLETE: // 加载完成
                    footViewHolder.pbLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_END: // 加载到底
                    footViewHolder.pbLoading.setVisibility(View.GONE);
                    footViewHolder.tvLoading.setVisibility(View.GONE);
                    footViewHolder.llEnd.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (enableFooter) {
            return mBookList.size() + 1;
        } else {
            return mBookList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (enableFooter) {
            if (position + 1 == getItemCount()) {
                return TYPE_FOOTER;
            } else {
                return TYPE_ITEM;
            }
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    // 如果当前是footer的位置，那么该item占据2个单元格，正常情况下占据1个单元格
                    return getItemViewType(position) == TYPE_FOOTER ? gridManager.getSpanCount()
                            : 1;
                }
            });
        }
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

    private class FootViewHolder extends RecyclerView.ViewHolder {

        ProgressBar pbLoading;
        TextView tvLoading;
        LinearLayout llEnd;

        FootViewHolder(View itemView) {
            super(itemView);
            pbLoading = (ProgressBar) itemView.findViewById(R.id.pb_loading);
            tvLoading = (TextView) itemView.findViewById(R.id.tv_loading);
            llEnd = (LinearLayout) itemView.findViewById(R.id.ll_end);
        }
    }

    /**
     * 设置上拉加载状态
     *
     * @param loadState 0.正在加载 1.加载完成 2.加载到底
     */
    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }

    public int getLoadState() {
        return loadState;
    }

    public void setEnableFooter(boolean enableFooter) {
        this.enableFooter = enableFooter;
    }
}

