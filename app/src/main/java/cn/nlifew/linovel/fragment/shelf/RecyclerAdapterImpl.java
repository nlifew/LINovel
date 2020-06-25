package cn.nlifew.linovel.fragment.shelf;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.ui.novel.NovelActivity;
import cn.nlifew.linovel.ui.reading.ReadingActivity;
import cn.nlifew.linovel.utils.TimeUtils;
import cn.nlifew.xqdreader.entity.BookShelf;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import cn.nlifew.xqdreader.utils.Utils;

class RecyclerAdapterImpl extends RecyclerView.Adapter implements
        RecyclerView.OnChildAttachStateChangeListener{
    private static final String TAG = "RecyclerAdapterImpl";

    interface OnItemDragListener {
        void onItemDrag(RecyclerView.ViewHolder holder);
    }

    RecyclerAdapterImpl(Fragment fragment) {
        mFragment = fragment;
        if (! (fragment instanceof OnItemDragListener)) {
            throw new UnsupportedOperationException("The Fragment must implements OnItemDragListener");
        }
        mDragListener = ((OnItemDragListener) fragment);
    }

    private boolean mEditable;
    private RecyclerView mHostView;
    private final Fragment mFragment;
    private final OnItemDragListener mDragListener;
    final List<BookShelf> mDataSet = new ArrayList<>();

    boolean setEditable(boolean editable) {
        if (editable == mEditable) {
            return mEditable;
        }
        mEditable = editable;
        int visible = editable ? View.VISIBLE : View.GONE;
        for (int i = 0, n = mHostView.getChildCount(); i < n; i++) {
            View view = mHostView.getChildAt(i);
            Holder holder = ((Holder) mHostView.getChildViewHolder(view));
            holder.mDragView.setVisibility(visible);
        }
        return ! mEditable;
    }

    boolean isEditable() {
        return mEditable;
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mHostView = recyclerView;
        recyclerView.addOnChildAttachStateChangeListener(this);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.removeOnChildAttachStateChangeListener(this);
        mHostView = null;
    }

    @Override
    public void onChildViewAttachedToWindow(@NonNull View view) {
        Holder holder = ((Holder) mHostView.getChildViewHolder(view));
        if (holder != null) {
            holder.mDragView.setVisibility(mEditable ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onChildViewDetachedFromWindow(@NonNull View view) {

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mFragment.getContext())
                .inflate(R.layout.fragment_shelf_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        final Holder holder = (Holder) h;
        final BookShelf book = mDataSet.get(position);

        holder.mTitleView.setText(book.BookName);
        holder.mAboutView.setText(holder.getBookAbout(book));
        holder.mDescView.setText(holder.getBookDesc(book));
        holder.mDragView.setVisibility(mEditable ? View.VISIBLE : View.GONE);

        String cover = NetworkUtils.novelCoverImage(book.BookId);
        Glide.get(mFragment.getContext())
                .getRequestManagerRetriever()
                .get(mFragment)
                .asBitmap()
                .load(cover)
                .into(holder.mCoverView);

        holder.mCoverView.setOnClickListener(v -> {
            String uri = "linovel://novel?id=" + book.BookId
                    + "&title=" + book.BookName
                    + "&outOfBook=0";
            Context context = mFragment.getContext();
            Intent intent = new Intent(context, NovelActivity.class);
            intent.setData(Uri.parse(uri));
            context.startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            String uri = "linovel://reading?id=" + book.BookId;
            Context context = mFragment.getContext();
            Intent intent = new Intent(context, ReadingActivity.class);
            intent.setData(Uri.parse(uri));
            context.startActivity(intent);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private final class Holder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        Holder(@NonNull View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.title);
            mAboutView = itemView.findViewById(R.id.about);
            mDescView = itemView.findViewById(R.id.description);
            mCoverView = itemView.findViewById(R.id.cover);
            mDragView = itemView.findViewById(R.id.drag);

            mDragView.setOnTouchListener(this);
        }

        final TextView mTitleView;
        final ImageView mCoverView;
        final ImageView mDragView;
        final TextView mAboutView;
        final TextView mDescView;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mDragListener.onItemDrag(this);
            return false;
        }


        private String getBookAbout(BookShelf book) {
            StringBuilder sb = Utils.obtainStringBuilder(32)
                    .append(book.Author).append(" - ")
                    .append(book.CategoryName).append(" - ")
                    .append(book.BookStatus).append(" - ");
            String s = sb.toString();
            Utils.recycle(sb);
            return s;
        }

        private String getBookDesc(BookShelf book) {
            StringBuilder sb = Utils.obtainStringBuilder(32);
            if (book.LastChapterUpdateTime > book.LastVipChapterUpdateTime) {
                sb.append(TimeUtils.formatDate(book.LastChapterUpdateTime))
                        .append(" - ")
                        .append(book.LastUpdateChapterName);
            } else {
                sb.append(TimeUtils.formatDate(book.LastVipChapterUpdateTime))
                        .append(" - ")
                        .append(book.LastVipUpdateChapterName);
            }
            String s = sb.toString();
            Utils.recycle(sb);
            return s;
        }
    }
}
