package cn.nlifew.linovel.ui.space;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.app.ThisApp;
import cn.nlifew.linovel.ui.BaseActivity;
import cn.nlifew.linovel.utils.ToastUtils;
import cn.nlifew.linovel.widget.DismissView;
import cn.nlifew.xqdreader.bean.user.UserBean;

import static cn.nlifew.linovel.widget.DismissView.OnStateChangedListener.STATE_FINISH_COLLAPSE;
import static cn.nlifew.linovel.widget.DismissView.OnStateChangedListener.STATE_FINISH_EXPAND;

/**
 * 用来展示用户信息的 Activity。
 * 你必须通过 Intent 传递一个 Uri 格式的 Data，内至少含有 id, authorId, title 键值对。
 * 需要注意的是：id 和 authorId 是两个不同的值，前者更准确来说是 userId。
 * 每个用户都有个唯一的 userId，只有作者才有 authorId，默认值为 0。
 * 例：
 * Context context = getContext();
 * Uri uri = Uri.parse("linovel://user?id=123456&authorId=0&title=LiHua");
 * Intent intent = new Intent(context, SpaceActivity.class);
 * intent.setData(uri);
 * context.startActivity(intent);
 */

public class SpaceActivity extends BaseActivity{
    private static final String TAG = "SpaceActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_in_top);
        setContentView(R.layout.activity_space);

        Toolbar toolbar = findViewById(R.id.activity_space_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        DismissView dismissView = findViewById(R.id.activity_space_dismiss);
        dismissView.setOnStateChangedListener(this::onStateChanged);
        dismissView.setSupportClickListener((v) -> finish());

        SpaceFragmentAdapter adapter = new SpaceFragmentAdapter(this);
        TabLayout tab = findViewById(R.id.activity_space_tab);
        ViewPager pager = findViewById(R.id.activity_space_pager);
        pager.setAdapter(adapter);
        tab.setupWithViewPager(pager);

        Uri uri = getIntent().getData();
        if (uri == null) {
            throw new UnsupportedOperationException("give me a Data");
        }
        Log.d(TAG, "onCreate: " + uri.toString());
        final String title = uri.getQueryParameter("title");
        setTitle(title);

        final String userId = uri.getQueryParameter("id");
        final String authorId = uri.getQueryParameter("authorId");

        mViewModel = new ViewModelProvider(this).get(SpaceViewModel.class);
        mViewModel.setUserId(userId, authorId);
        mViewModel.mErrMsg.observe(this, this::onErrMsgChanged);
        mViewModel.mUserData.observe(this, this::onUserDataChanged);
        ThisApp.mH.postDelayed(() -> mViewModel.refreshUserData(), 500);
    }

    private SpaceViewModel mViewModel;


    private void onErrMsgChanged(String s) {
        if (s != null) {
            ToastUtils.getInstance(this).show(s);
        }
    }

    private void onUserDataChanged(UserBean.UserType user) {
        if (user == null) {
            return;
        }
        if (user.Description == null || user.Description.length() == 0) {
            user.Description = getString(R.string.default_sign);
        }
        TextView tv = findViewById(R.id.activity_space_sign);
        tv.setText(user.Description);

        RequestOptions options = RequestOptions.errorOf(R.drawable.ic_account_circle_white_48dp);
        Glide.get(this)
                .getRequestManagerRetriever()
                .get(this)
                .asBitmap()
                .apply(options)
                .load(user.HeadImage)
                .into((ImageView) findViewById(R.id.activity_space_head));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_in_top);
    }

    private void onStateChanged(DismissView view, int state) {
        switch (state) {
            case STATE_FINISH_EXPAND:
                finish();
                break;
            case STATE_FINISH_COLLAPSE:
                break;
        }
    }
}
