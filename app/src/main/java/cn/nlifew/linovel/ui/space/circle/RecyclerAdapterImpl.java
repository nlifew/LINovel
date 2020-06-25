package cn.nlifew.linovel.ui.space.circle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.utils.TimeUtils;
import cn.nlifew.xqdreader.bean.user.UserBean;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import cn.nlifew.xqdreader.utils.Utils;

class RecyclerAdapterImpl extends RecyclerView.Adapter {
    private static final String TAG = "RecyclerAdapterImpl";

    RecyclerAdapterImpl(Fragment fragment) {
        mFragment = fragment;
        mDataSet = new ArrayList<>(64);
    }

    private final Fragment mFragment;
    private final List<UserBean.CircleReviewItemType> mDataSet;

    void updateDataSet(UserBean.CircleReviewType data) {
        mDataSet.clear();
        mDataSet.addAll(Arrays.asList(data.CircleReviewList));
        notifyDataSetChanged();
    }

    void appendDataSet(UserBean.CircleReviewType data) {
        int old = mDataSet.size();
        mDataSet.addAll(Arrays.asList(data.CircleReviewList));
        notifyItemRangeChanged(old, data.CircleReviewList.length);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mFragment.getContext())
                .inflate(R.layout.fragment_space_circle_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        final Holder holder = (Holder) h;
        final UserBean.CircleReviewItemType item = mDataSet.get(position);

        holder.mNameView.setText(item.NickName);
        holder.mContentView.setText(item.Content);
        holder.mTitleView.setText(item.CircleName);
        holder.mAboutView.setText(getBookAbout(item));
        holder.mTimeView.setText(TimeUtils.formatDate(item.Date));
        holder.mStarView.setText(item.LikeCount == 0 ? "赞" : Integer.toString(item.LikeCount));
        holder.mCommentView.setText(item.ReplyCount == 0 ? "评论" : Integer.toString(item.ReplyCount));

        RequestManager rm = Glide
                .get(mFragment.getContext())
                .getRequestManagerRetriever()
                .get(mFragment);

        rm.asBitmap().load(item.HeadImg).into(holder.mHeadView);
        rm.asBitmap().load(NetworkUtils.novelCoverImage(item.BookId))
                .into(holder.mCoverView);
    }

    private static final class Holder extends RecyclerView.ViewHolder {
        Holder(@NonNull View itemView) {
            super(itemView);

            mHeadView = itemView.findViewById(R.id.fragment_space_circle_item_head);
            mNameView = itemView.findViewById(R.id.fragment_space_circle_item_name);
            mContentView = itemView.findViewById(R.id.fragment_space_circle_item_text);
            mStarView = itemView.findViewById(R.id.fragment_space_circle_item_star);
            mCommentView = itemView.findViewById(R.id.fragment_space_circle_item_comment);
            mTimeView = itemView.findViewById(R.id.fragment_space_circle_item_time);

            View view = itemView.findViewById(R.id.fragment_space_circle_item_origin);
            mCoverView = view.findViewById(R.id.fragment_space_circle_item_cover);
            mTitleView = view.findViewById(R.id.fragment_space_circle_item_title);
            mAboutView = view.findViewById(R.id.fragment_space_circle_item_about);
        }
        final ImageView mHeadView;
        final TextView mNameView;//
        final TextView mContentView;//
        final TextView mStarView;//
        final TextView mCommentView;//
        final TextView mTimeView;//
        final ImageView mCoverView;
        final TextView mTitleView;//
        final TextView mAboutView;//
    }

    private static String getBookAbout(UserBean.CircleReviewItemType item) {
        StringBuilder sb = Utils.obtainStringBuilder(64)
                .append(item.AuthorName).append(" - ")
                .append(item.CategoryName).append(" - ")
                .append(item.BookStatus).append(" - ");
        if (item.WordsCount > 10000) {
            sb.append(item.WordsCount / 10000).append("万字");
        } else {
            sb.append(item.WordsCount).append('字');
        }
        String s = sb.toString();
        Utils.recycle(sb);
        return s;
    }
}
