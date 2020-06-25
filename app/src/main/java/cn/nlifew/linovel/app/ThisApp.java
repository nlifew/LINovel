package cn.nlifew.linovel.app;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import cn.nlifew.xqdreader.XQDReader;

public class ThisApp extends Application {
    private static final String TAG = "ThisApp";

    public static ThisApp currentApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        currentApplication = this;
        XQDReader.install(this);
    }

    public static final Handler mH = new Handler();
}
