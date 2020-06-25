package cn.nlifew.linovel.fragment.novel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.Locale;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.adapter.FragmentViewHolder;
import cn.nlifew.linovel.ui.space.SpaceActivity;
import cn.nlifew.linovel.utils.TimeUtils;
import cn.nlifew.xqdreader.bean.CommentBean;
import cn.nlifew.xqdreader.utils.Utils;

class CommentViewHolder extends FragmentViewHolder<CommentBean.TopicDataType> {

    CommentViewHolder(Fragment fragment, ViewGroup parent) {
        super(fragment, R.layout.fragment_novel_item, parent);

        mHeadView = itemView.findViewById(R.id.fragment_comment_item_head);
        mNameView = itemView.findViewById(R.id.fragment_comment_item_name);
        mTextView = itemView.findViewById(R.id.fragment_comment_item_text);
        mStarView = itemView.findViewById(R.id.fragment_comment_item_star);
        mCommentView = itemView.findViewById(R.id.fragment_comment_item_comment);
        mTimeView = itemView.findViewById(R.id.fragment_comment_item_time);
    }

    private ImageView mHeadView;
    private TextView mNameView;
    private TextView mTextView;
    private TextView mStarView;
    private TextView mCommentView;
    private TextView mTimeView;


    @Override
    public void onBindViewHolder(CommentBean.TopicDataType data) {
        mNameView.setText(data.UserName);
        mTextView.setText(data.Body);
        mStarView.setText(data.StarCount == 0 ? "点赞" : Integer.toString(data.StarCount));
        mCommentView.setText(data.PostCount == 0 ? "评论" : Integer.toString(data.PostCount));
        mTimeView.setText(TimeUtils.formatDate(data.PostDate));

        Glide.get(mFragment.getContext())
                .getRequestManagerRetriever()
                .get(mFragment)
                .asBitmap()
                .load(data.UserHeadIcon)
                .into(mHeadView);
        mHeadView.setOnClickListener(v -> {
            String uri = "linovel://user?id=" + data.UserId
                    + "&authorId=" + data.AuthorId
                    + "&title=" + data.UserName;
            Context context = mFragment.getContext();
            Intent intent = new Intent(context, SpaceActivity.class);
            intent.setData(Uri.parse(uri));
            context.startActivity(intent);
        });
    }

}
