package cn.nlifew.linovel.ui.space.chapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.utils.TimeUtils;
import cn.nlifew.xqdreader.bean.user.UserBean;

class RecyclerAdapterImpl extends RecyclerView.Adapter {
    private static final String TAG = "RecyclerAdapterImpl";

    RecyclerAdapterImpl(Fragment fragment) {
        mFragment = fragment;
        mDataSet = new ArrayList<>(64);
    }

    private final Fragment mFragment;
    private final List<UserBean.ChapterReviewItemType> mDataSet;

    void updateDataSet(UserBean.ChapterReviewType data) {
        mDataSet.clear();
        mDataSet.addAll(Arrays.asList(data.ChapterReviewList));
        notifyDataSetChanged();
    }

    void appendDataSet(UserBean.ChapterReviewType data) {
        int old = mDataSet.size();
        mDataSet.addAll(Arrays.asList(data.ChapterReviewList));
        notifyItemRangeChanged(old, data.ChapterReviewList.length);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mFragment.getContext())
                .inflate(R.layout.fragment_space_chapter_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        final Holder holder = (Holder) h;
        final UserBean.ChapterReviewItemType item = mDataSet.get(position);

        holder.mNameView.setText(item.NickName);
        holder.mContentView.setText(item.Content);
        holder.mTimeView.setText(TimeUtils.formatDate(item.CreateTime));
        holder.mStarView.setText(item.LikeCount == 0 ? "赞" : Integer.toString(item.LikeCount));

        if (TextUtils.isEmpty(item.RefferContent)) {
            holder.mOriginView.setVisibility(View.GONE);
        }
        else {
            holder.mOriginView.setVisibility(View.VISIBLE);
            holder.mOriginView.setText(item.RefferContent);
        }

        Glide.get(mFragment.getContext())
                .getRequestManagerRetriever()
                .get(mFragment)
                .asBitmap()
                .load(item.HeadImg)
                .into(holder.mHeadView);
    }

    private static final class Holder extends RecyclerView.ViewHolder {
        Holder(@NonNull View itemView) {
            super(itemView);
            mHeadView = itemView.findViewById(R.id.fragment_space_chapter_item_head);
            mNameView = itemView.findViewById(R.id.fragment_space_chapter_item_name);
            mContentView = itemView.findViewById(R.id.fragment_space_chapter_item_text);
            mOriginView = itemView.findViewById(R.id.fragment_space_chapter_item_origin);
            mStarView = itemView.findViewById(R.id.fragment_space_chapter_item_star);
//            mCommentView = itemView.findViewById(R.id.fragment_chapter_review_item_comment);
            mTimeView = itemView.findViewById(R.id.fragment_space_chapter_item_time);
        }

        final ImageView mHeadView;
        final TextView mNameView;
        final TextView mContentView;
        final TextView mOriginView;
        final TextView mStarView;
//        final TextView mCommentView;
        final TextView mTimeView;
    }
}
