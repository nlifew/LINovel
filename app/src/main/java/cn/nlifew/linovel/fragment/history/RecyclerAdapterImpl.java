package cn.nlifew.linovel.fragment.history;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.utils.TimeUtils;
import cn.nlifew.xqdreader.entity.ReadingRecord;
import cn.nlifew.xqdreader.utils.NetworkUtils;

class RecyclerAdapterImpl extends RecyclerView.Adapter {
    private static final String TAG = "RecyclerAdapterImpl";

    RecyclerAdapterImpl(Fragment activity) {
        mFragment = activity;
    }

    private final Fragment mFragment;
    private final List<ReadingRecord> mDataSet = new ArrayList<>(32);


    void clearDataSet() {
        mDataSet.clear();
        notifyDataSetChanged();
    }

    void updateDataSet(List<ReadingRecord> list) {
        mDataSet.clear();
        mDataSet.addAll(list);
        notifyDataSetChanged();
    }

    void appendDataSet(List<ReadingRecord> list) {
        int old = mDataSet.size();
        mDataSet.addAll(list);
        notifyItemRangeChanged(old, mDataSet.size());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mFragment.getContext())
                .inflate(R.layout.fragment_history_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        Holder holder = (Holder) h;
        ReadingRecord record = mDataSet.get(position);

        holder.mTitleView.setText(record.bookName);
        holder.mAboutView.setText(record.chapterName);
        holder.mDescView.setText(TimeUtils.formatDate(record.time));

        String cover = NetworkUtils.novelCoverImage(record.bookId);
        Glide.get(mFragment.getContext())
                .getRequestManagerRetriever()
                .get(mFragment)
                .asBitmap()
                .load(cover)
                .into(holder.mCoverView);
    }


    private final class Holder extends RecyclerView.ViewHolder {
        Holder(@NonNull View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.fragment_history_item_title);
            mAboutView = itemView.findViewById(R.id.fragment_history_item_about);
            mDescView = itemView.findViewById(R.id.fragment_history_item_desc);
            mCoverView = itemView.findViewById(R.id.fragment_history_item_cover);
        }

        CheckBox mCheckBox; // todo
        TextView mTitleView;
        TextView mAboutView;
        TextView mDescView;
        ImageView mCoverView;
    }
}
