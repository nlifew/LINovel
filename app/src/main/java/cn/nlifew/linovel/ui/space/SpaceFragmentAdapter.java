package cn.nlifew.linovel.ui.space;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import cn.nlifew.linovel.adapter.BaseFragmentPagerAdapter;
import cn.nlifew.linovel.fragment.BaseFragment;
import cn.nlifew.linovel.ui.space.book.UserBookFragment;
import cn.nlifew.linovel.ui.space.chapter.ChapterReviewFragment;
import cn.nlifew.linovel.ui.space.circle.CircleReviewFragment;

class SpaceFragmentAdapter extends BaseFragmentPagerAdapter {

    SpaceFragmentAdapter(FragmentActivity activity) {
        super(activity.getSupportFragmentManager());
        mActivity = activity;

        Uri uri = activity.getIntent().getData();
        String authorId;
        mIsAuthor = uri != null
                && (authorId = uri.getQueryParameter("authorId")) != null
                && ! "0".equals(authorId);
    }

    private final FragmentActivity mActivity;
    private boolean mIsAuthor;

    @Override
    public int getCount() {
        return mIsAuthor ? 3 : 2;
    }

    @Override
    public BaseFragment createBaseFragment(int position) {
        final Uri uri = mActivity.getIntent().getData();
        if (! mIsAuthor) position ++;

        switch (position) {
            case 0: return UserBookFragment.newInstance(uri.getQueryParameter("authorId"));
            case 1: return ChapterReviewFragment.newInstance(uri.getQueryParameter("id"));
            case 2: return CircleReviewFragment.newInstance(uri.getQueryParameter("id"));
        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (! mIsAuthor) position ++;

        switch (position) {
            case 0: return "作品";
            case 1: return "评论";
            case 2: return "帖子";
        }
        return "";
    }
}
