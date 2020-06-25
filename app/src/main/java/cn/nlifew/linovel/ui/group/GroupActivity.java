package cn.nlifew.linovel.ui.group;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.fragment.group.GroupFragment;
import cn.nlifew.linovel.ui.BaseActivity;

/**
 * 以竖直列表的方式展示小说列表
 *
 * 启动该 Activity 需要通过 Intent.setData() 设置一个 Uri，里面携带了必要的数据
 * 包括：id, title
 * 例如 linovel://group?id=26&title=畅销精选
 */
public class GroupActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        if (uri == null) {
            throw new UnsupportedOperationException("give me a Uri data");
        }
        String id = uri.getQueryParameter("id");
        String title = uri.getQueryParameter("title");

        useDefaultLayout(title);

        Fragment fragment = GroupFragment.newInstance(id);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_base_host, fragment)
                .commit();
    }
}
