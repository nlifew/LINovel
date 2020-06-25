package cn.nlifew.linovel.ui.settings;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.ui.BaseActivity;

public class SettingsActivity extends BaseActivity {
    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        useDefaultLayout("设置");

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_base_host, new SettingsFragment())
                .commit();
    }
}
