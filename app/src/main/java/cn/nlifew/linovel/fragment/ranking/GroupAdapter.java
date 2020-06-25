package cn.nlifew.linovel.fragment.ranking;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.app.ThisApp;
import cn.nlifew.linovel.utils.DisplayUtils;
import cn.nlifew.xqdreader.bean.ranking.RankingGroupBean;

class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.Holder> {
    private static final String TAG = "GroupAdapter";

    interface OnItemClickListener {
        void onItemClick(RankingGroupBean.DataType data);
    }

    GroupAdapter(Fragment fragment) {
        mFragment = fragment;

        if (! (fragment instanceof OnItemClickListener)) {
            throw new UnsupportedOperationException("Fragment have to implement OnItemSelectListener");
        }
        mClickListener = (OnItemClickListener) fragment;
        mDataSet = new ArrayList<>(16);
    }

    private final Fragment mFragment;
    private final OnItemClickListener mClickListener;
    private final List<RankingGroupBean.DataType> mDataSet;
    private int mSelectedItemPosition;
    private Drawable mSelectedBackground;

    void updateDataSet(RankingGroupBean.DataType[] data) {
        mDataSet.clear();
        mDataSet.addAll(Arrays.asList(data));
        notifyDataSetChanged();
        if (data.length != 0) {
            ThisApp.mH.post(() -> {
                mSelectedItemPosition = 0;
                mClickListener.onItemClick(data[0]);
            });
        }
        if (mSelectedBackground == null) {
            mSelectedBackground = mFragment.getResources()
                    .getDrawable(R.drawable.ranking_group_item_bkg_selected);
        }
    }


    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context c = mFragment.getContext();

        TextView tv = new TextView(c);
        tv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                DisplayUtils.dp2px(50)
        ));
        tv.setGravity(Gravity.CENTER);
        return new Holder(tv);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        RankingGroupBean.DataType data = mDataSet.get(position);
        holder.mTextView.setText(data.Name);
        holder.mTextView.setBackground(position == mSelectedItemPosition ?
                mSelectedBackground : null);
        holder.mTextView.setOnClickListener((v) -> {
            notifyItemChanged(mSelectedItemPosition);
            mSelectedItemPosition = position;
            holder.mTextView.setBackground(mSelectedBackground);
            mClickListener.onItemClick(mDataSet.get(position));
        });
    }

    static final class Holder extends RecyclerView.ViewHolder {
        Holder(@NonNull View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }
        final TextView mTextView;
    }
}
