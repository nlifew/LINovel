package cn.nlifew.linovel.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import cn.nlifew.linovel.R;

public final class Settings {
    private static final String TAG = "Settings";

    private static Settings sInstance;
    public static Settings getInstance(Context c) {
        if (sInstance == null) {
            synchronized (Settings.class) {
                if (sInstance == null) {
                    sInstance = new Settings(c);
                }
            }
        }
        return sInstance;
    }

    private Settings(Context context) {
        context = context.getApplicationContext();
        mRes = context.getResources();
        mName = mRes.getString(R.string.settings_name);
        mPref = context.getSharedPreferences(mName, Context.MODE_PRIVATE);
    }

    private final Resources mRes;
    private final SharedPreferences mPref;
    private final String mName;

    public String getName() {
        return mName;
    }

    public boolean isNightMode() {
        String key = mRes.getString(R.string.settings_key_night_mode);
        boolean defValue = mRes.getBoolean(R.bool.settings_def_night_mode);
        return mPref.getBoolean(key, defValue);
    }

    public void setNightMode(boolean night) {
        String key = mRes.getString(R.string.settings_key_night_mode);
        mPref.edit().putBoolean(key, night).apply();
    }

    public boolean isNightAuto() {
        String key = mRes.getString(R.string.settings_key_night_auto);
        boolean defValue = mRes.getBoolean(R.bool.settings_def_night_auto);
        return mPref.getBoolean(key, defValue);
    }

    public int getNightOnTime() {
        String key = mRes.getString(R.string.settings_key_night_on);
        int defValue = mRes.getInteger(R.integer.settings_def_night_on);
        return mPref.getInt(key, defValue);
    }

    public int getNightOffTime() {
        String key = mRes.getString(R.string.settings_key_night_off);
        int defValue = mRes.getInteger(R.integer.settings_def_night_off);
        return mPref.getInt(key, defValue);
    }

    public boolean navBarHasColor() {
        String key = mRes.getString(R.string.settings_key_nav);
        boolean defValue = mRes.getBoolean(R.bool.settings_def_nav);
        return mPref.getBoolean(key, defValue);
    }

    public int getReadingTextSize() {
        String key = mRes.getString(R.string.settings_key_reading_text_size);
        int defValue = mRes.getInteger(R.integer.settings_def_reading_text_size);
        return mPref.getInt(key, defValue);
    }

    public int getReadingTextColor() {
        String key = mRes.getString(R.string.settings_key_reading_text_color);
        int defValue = mRes.getInteger(R.integer.settings_def_reading_text_color);
        return mPref.getInt(key, defValue);
    }

    public int getSiteType() {
        String key = mRes.getString(R.string.settings_key_site);
        String defValue = mRes.getString(R.string.settings_def_site);
        String s = mPref.getString(key, defValue);
        return Integer.parseInt(s);
    }
}
