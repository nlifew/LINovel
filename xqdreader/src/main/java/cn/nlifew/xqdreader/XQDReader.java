package cn.nlifew.xqdreader;

import android.annotation.SuppressLint;
import android.app.ActivityThread;
import android.app.Application;
import android.app.ApplicationPackageManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import cn.nlifew.xqdreader.utils.CryptoUtils;


public final class XQDReader {
    private static final String TAG = "XQDReader";

    @SuppressLint("StaticFieldLeak")
    public static Context sContext;

    public static void install(Application app) {
        sContext = app;

        try {
            // 获取 ActivityThread 里的 sPackageManager 原始对象
            Field sPackageManager = ActivityThread.class
                    .getDeclaredField("sPackageManager");
            sPackageManager.setAccessible(true);

            // 创建代理对象
            Object chief = sPackageManager.get(null);
            Class<?> cls = chief.getClass();
            Object spy = Proxy.newProxyInstance(
                    cls.getClassLoader(),
                    cls.getInterfaces(),
                    new IPackageManagerSpy(chief)
            );

            // 替换掉 ActivityThread 里的 sPackageManager
            sPackageManager.set(null, spy);

            // 替换掉 ApplicationPackageManager 里的 mPm
            PackageManager pm = app.getPackageManager();
            Field mPm = ApplicationPackageManager.class.getDeclaredField("mPM");
            mPm.setAccessible(true);
            mPm.set(pm, spy);
        } catch (Throwable t) {
            Log.e(TAG, "install: failed", t);
        }

        SharedPreferences pref = app.getSharedPreferences("xQDReader", Context.MODE_PRIVATE);
        if ((IMEI = pref.getString("imei", null)) != null) {
            UUID = pref.getString("uuid", "");
        }
        else {
            IMEI = CryptoUtils.randomIMEI();
            UUID = CryptoUtils.randomUUID();

            pref.edit().putString("imei", IMEI)
                    .putString("uuid", UUID)
                    .apply();
        }
    }


    public static String IMEI;
    public static String UUID;
    public static final String VERSION_CODE = "426";
    public static final String VERSION_NAME = "7.9.18";
    public static final String APP_ID = "12";
    public static final String AREA_ID = "30";
    public static final String UNKNOWN_VERSION_CODE = "1000209";
}
