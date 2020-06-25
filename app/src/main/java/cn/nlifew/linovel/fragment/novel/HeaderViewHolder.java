package cn.nlifew.linovel.fragment.novel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.adapter.FragmentViewHolder;
import cn.nlifew.linovel.ui.chapter.ChapterActivity;
import cn.nlifew.linovel.ui.space.SpaceActivity;
import cn.nlifew.xqdreader.bean.NovelBean;

import static cn.nlifew.linovel.fragment.novel.Helper.toCategory;
import static cn.nlifew.linovel.fragment.novel.Helper.toSpanText;

class HeaderViewHolder extends FragmentViewHolder<NovelBean.DataType> implements View.OnClickListener {
    private static final String TAG = "HeaderViewHolder";

    HeaderViewHolder(Fragment fragment, ViewGroup parent) {
        super(fragment, R.layout.fragment_novel_header, parent);
        onViewCreated(itemView);
    }

    private TextView mWordsView;
    private TextView mRecommendsView;
    private TextView mMouthCardView;
    private TextView mSummaryView;
    private TextView mCategoryView;
    private TextView mAuthorView;
    private TextView mAuthorMoreView;

    private void onViewCreated(View view) {
        mWordsView = view.findViewById(R.id.fragment_novel_words);
        mRecommendsView = view.findViewById(R.id.fragment_novel_recommends);
        mMouthCardView = view.findViewById(R.id.fragment_novel_month);
        mSummaryView = view.findViewById(R.id.fragment_novel_summary);
        mCategoryView = view.findViewById(R.id.fragment_novel_category);
        mAuthorView = view.findViewById(R.id.fragment_novel_author);
        mAuthorMoreView = view.findViewById(R.id.fragment_novel_author_more);
    }

    @Override
    public void onBindViewHolder(final NovelBean.DataType data) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        mWordsView.setText(toSpanText(ssb, data.WordsCnt, "字", data.BookStatus));
        mRecommendsView.setText(toSpanText(ssb, data.RecommendAll, "", "推荐票"));
        mMouthCardView.setText(toSpanText(ssb, data.MonthTicketCount, "", "月票"));

        mSummaryView.setText(data.Description);

        mCategoryView.setText(toCategory(data));
        mCategoryView.setTag(data);
        mCategoryView.setOnClickListener(this);

        mAuthorView.setText(data.Author);
        mAuthorMoreView.setTag(data);
        mAuthorMoreView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        NovelBean.DataType data = (NovelBean.DataType) v.getTag();

        if (v == mAuthorMoreView) {
            String uri = "linovel://user?id=" + data.AuthorUserId
                    + "&authorId=" + data.AuthorId
                    + "&title=" + data.Author;
            Context context = mFragment.getContext();
            Intent intent = new Intent(context, SpaceActivity.class);
            intent.setData(Uri.parse(uri));
            context.startActivity(intent);
        }
        else if (v == mCategoryView) {
            String uri = "linovel://chapter?id=" + data.BookId;
            Context context = mFragment.getContext();
            Intent intent = new Intent(context, ChapterActivity.class);
            intent.setData(Uri.parse(uri));
            context.startActivity(intent);
        }
    }
}
