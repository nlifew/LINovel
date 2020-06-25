package cn.nlifew.linovel.ui.novel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.app.ThisApp;
import cn.nlifew.linovel.fragment.novel.NovelFragment;
import cn.nlifew.linovel.ui.reading.ReadingActivity;
import cn.nlifew.linovel.utils.ToastUtils;
import cn.nlifew.xqdreader.bean.NovelBean;
import cn.nlifew.xqdreader.entity.Account;
import cn.nlifew.xqdreader.entity.BookShelf;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import cn.nlifew.xqdreader.utils.Utils;

class Helper {
    private static final String TAG = "Helper";

    Helper(NovelActivity activity) {
        mActivity = activity;
    }

    private final NovelActivity mActivity;
    private NovelFragment mFragment;
    private ImageView mCoverView;
    private TextView mSubtitleView;
    private CircleProgressFab mSubscribeFab;
    private NovelViewModel mViewModel;
    private boolean mIsSubscribing;
    private boolean mShouldObverseNovelDataChange = true;

    private Drawable mSubscribeIcon, mUnSubscribeIcon;

    void onCreate() {
        mCoverView = mActivity.findViewById(R.id.activity_novel_cover);
        mSubtitleView = mActivity.findViewById(R.id.activity_novel_subtitle);

        mSubscribeFab = mActivity.findViewById(R.id.activity_novel_collect_progress);

        Uri uri = mActivity.getIntent().getData();
        if (uri == null) {
            throw new UnsupportedOperationException("give me a Uri Data");
        }
        String title = uri.getQueryParameter("title");
        mActivity.setTitle(title);

        String id = uri.getQueryParameter("id");
        String isOutOfBook = uri.getQueryParameter("outOfBook");

        mFragment = NovelFragment.newInstance(id, isOutOfBook);
        mActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_novel_host, mFragment)
                .commit();

        mViewModel = new ViewModelProvider(mActivity).get(NovelViewModel.class);
        mViewModel.mNovelId = id;
        mViewModel.mErrMsg.observe(mActivity, this::onErrMsgChanged);
        mViewModel.mSubscribeStatus.observe(mActivity, this::onSubscribeStatusChanged);
    }

    void onResume() {
        if (mShouldObverseNovelDataChange) {
            mShouldObverseNovelDataChange = false;

            // 不出意外的话现在 Fragment 的 ViewMode 已经初始化完成了
            mFragment.getViewModel()
                    .getNovelData()
                    .observe(mActivity, this::onNovelDataChanged);
        }
    }

    private void onNovelDataChanged(NovelBean.DataType data) {
        if (data == null) return;

        mCoverView.setVisibility(View.VISIBLE);
        String cover = NetworkUtils.novelCoverImage(data.BookId);
        Glide.with(mActivity).asBitmap().load(cover).into(mCoverView);

        StringBuilder sb = Utils.obtainStringBuilder(32)
                .append(data.Author).append(" - ")
                .append(data.CategoryName).append(" - ")
                .append(data.SubCategoryName);
        mSubtitleView.setText(sb);
        Utils.recycle(sb);

        BookShelf book = BookShelf.findById(data.BookId);
        mViewModel.mSubscribeStatus.postValue(book != null);
    }

    private void onErrMsgChanged(String s) {
        if (s != null) {
            ToastUtils.getInstance(mActivity).show(s);
        }
    }

    private void onSubscribeStatusChanged(boolean subscribe) {
        if (mSubscribeIcon == null) {
            mSubscribeIcon = mActivity.getDrawable(R.drawable.ic_playlist_add_check_white_24dp);
            mUnSubscribeIcon = mActivity.getDrawable(R.drawable.ic_playlist_add_white_24dp);
        }


        if (! mIsSubscribing) {     // 如果此时没有动画，我们直接更新 UI
            mSubscribeFab.setTitle(subscribe ? "从书架移除" : "添加到书架");
            mSubscribeFab.setDrawableIcon(subscribe ? mSubscribeIcon : mUnSubscribeIcon);
        }
        else {
            mSubscribeFab.beginFinalAnimation();
            mSubscribeFab.attachListener(() -> {
                String msg = subscribe ? "成功添加到书架" : "成功从书架移除";
                Snackbar.make(mSubscribeFab, msg, BaseTransientBottomBar.LENGTH_SHORT)
                        .show();
                mIsSubscribing = false;
                mSubscribeFab.attachListener(null);

                mSubscribeFab.setTitle(subscribe ? "从书架移除" : "添加到书架");
                mSubscribeFab.setDrawableIcon(subscribe ? mSubscribeIcon : mUnSubscribeIcon);
            });
        }
    }

    void onClick(View view) {
        Account account = Account.currentAccount();
        if (account == null) {
            DialogInterface.OnClickListener cli = (dialog, which) -> dialog.dismiss();
            new AlertDialog.Builder(mActivity)
                    .setTitle("登录您的帐号")
                    .setMessage("您可以点击 侧边栏-头像 登录您的帐号")
                    .setPositiveButton("知道了", cli)
                    .show();
            return;
        }

        switch (view.getId()) {
            case R.id.activity_novel_collect: {
                if (mIsSubscribing) return;
                mIsSubscribing = true;
                mSubscribeFab.show();
                mViewModel.refreshBookShelf(! mViewModel.mSubscribeStatus.getValue());
                break;
            }
            case R.id.activity_novel_read: {
                String uri = "linovel://reading?id=" + mViewModel.mNovelId;
                Intent intent = new Intent(mActivity, ReadingActivity.class);
                intent.setData(Uri.parse(uri));
                mActivity.startActivity(intent);
                break;
            }
        }
    }
}
