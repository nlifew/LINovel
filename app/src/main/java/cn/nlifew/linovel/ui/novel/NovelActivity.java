package cn.nlifew.linovel.ui.novel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.fragment.novel.NovelFragment;
import cn.nlifew.linovel.ui.BaseActivity;
import cn.nlifew.linovel.ui.reading.ReadingActivity;
import cn.nlifew.xqdreader.entity.Account;
import cn.nlifew.xqdreader.bean.NovelBean;
import cn.nlifew.xqdreader.entity.BookShelf;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import cn.nlifew.xqdreader.utils.Utils;

/**
 * 这个类负责小说界面的显示，包括作者，人气，评论等等
 * 当有其它组件试图 startActivity 时，必须要通过 Intent 提供一个 Data，
 * 这个 Data 必须含有 title, id, outOfBook 三个键值。
 * 例如：
 * Context context = getContext();
 * Uri data = Uri.parseUri("linovel://novel?id=12345&title=ThisBookDoesnotExist&outOfBook=0");
 * Intent intent = new Intent(context, NovelActivity.class);
 * intent.setData(data);
 * context.startActivity(intent);
 *
 * NovelActivity 保留了显示封面、子标题、悬浮按钮的作用，其余的内容显示则全部交给了 NovelFragment。
 * 我们需要等待 NovelFragment 完成服务器数据的获取，才能显示上述内容。
 * 也就是说，我们需要观察 NovelViewModel 数据的变化。
 * 但 onCreate() 还为时过早，NovelFragment 还没有完成基本生命周期的回调，
 * NovelViewModel 肯定为空。因此，我们把这一步放到 onResume 中完成。
 *
 * 另外需要注意的是，在布局文件 R.layout.activity_novel 中，
 * 可以看到 Fragment 所依附的 View 是一个 FrameLayout 而不是 NestedScrollView，
 * 这是因为 NovelFragment 中如果存在 RecyclerView，会和 NestedScrollView
 * 产生滑动冲突，即前者的复用机制失效，参考 https://www.jianshu.com/p/801f5255b28a
 * 因此原则上要求，NovelFragment 的根布局是一个 RecyclerView，其余的 View 作为
 * RecyclerView 的不同 Type 显示出来。
 *
 */

public class NovelActivity extends BaseActivity implements
        View.OnClickListener {
    private static final String TAG = "NovelActivity";

    private final Helper mHelper = new Helper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel);

        Toolbar toolbar = findViewById(R.id.activity_novel_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mHelper.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHelper.onResume();
    }

    @Override
    public void onClick(View v) {
        mHelper.onClick(v);

        /*
        Account account = Account.currentAccount();
        if (account == null) {
            DialogInterface.OnClickListener cli = (dialog, which) -> {
                dialog.dismiss();
            };
            new AlertDialog.Builder(this)
                    .setTitle("您需要登录您的账号")
                    .setMessage("点击 主界面-侧边栏-头像，可以登录您的帐号")
                    .setPositiveButton("确定", cli)
                    .setNegativeButton("取消", cli)
                    .show();
            return;
        }
        Uri uri = getIntent().getData();
        String novelId = uri.getQueryParameter("id");

        switch (v.getId()) {
            case R.id.activity_novel_add: {

                break;
            }
            case R.id.activity_novel_read: {
                Uri data = Uri.parse("linovel://reading?id=" + novelId);
                Intent intent = new Intent(this, ReadingActivity.class);
                intent.setData(data);
                startActivity(intent);
                break;
            }
        }
 */
    }
}
