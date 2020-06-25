package cn.nlifew.linovel.ui.history;

import android.os.Bundle;

import androidx.annotation.Nullable;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.fragment.history.HistoryFragment;
import cn.nlifew.linovel.ui.BaseActivity;

public class HistoryActivity extends BaseActivity {
    private static final String TAG = "HistoryActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        useDefaultLayout("历史记录");

        HistoryFragment fragment = new HistoryFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_base_host, fragment)
                .commit();
    }
}
