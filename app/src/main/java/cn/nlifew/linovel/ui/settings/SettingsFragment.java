package cn.nlifew.linovel.ui.settings;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.settings.Settings;
import cn.nlifew.linovel.ui.BaseActivity;
import cn.nlifew.linovel.utils.ToastUtils;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = "SettingsFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Log.d(TAG, "onCreatePreferences: " + rootKey);

        String name = Settings.getInstance(getContext()).getName();
        getPreferenceManager().setSharedPreferencesName(name);

        setPreferencesFromResource(R.xml.settings, rootKey);
    }

}
