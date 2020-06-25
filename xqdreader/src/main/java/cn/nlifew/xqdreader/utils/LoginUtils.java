package cn.nlifew.xqdreader.utils;

import android.os.Build;
import android.util.ArrayMap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.nlifew.xqdreader.XQDReader;
import cn.nlifew.xqdreader.bean.login.LoginBean;

import static cn.nlifew.xqdreader.XQDReader.APP_ID;
import static cn.nlifew.xqdreader.XQDReader.AREA_ID;
import static cn.nlifew.xqdreader.XQDReader.IMEI;
import static cn.nlifew.xqdreader.XQDReader.UNKNOWN_VERSION_CODE;
import static cn.nlifew.xqdreader.XQDReader.VERSION_CODE;

public final class LoginUtils {

    public static Map<String, String> createLoginBody() {

        Map<String, String> map = new ArrayMap<>(24);
        map.put("devicename", Build.MODEL);
        map.put("source", UNKNOWN_VERSION_CODE);
        map.put("signature", getSignature());
        map.put("appid", APP_ID);
        map.put("referer", "http://android.qidian.com");
        map.put("auto", "1");
        map.put("ticket", "0");
        map.put("devicetype", getDeviceType());
        map.put("qimei", IMEI);
        map.put("format", "json");
        map.put("osversion", getOsVersion());
        map.put("imei", IMEI);
        map.put("sdkversion", "201");
        map.put("autotime", "30");
        map.put("version", VERSION_CODE);
        map.put("areaid", AREA_ID);
        map.put("returnurl", "http://www.qidian.com");
        return map;
    }

    public static Map<String, String> createV2Body(LoginBean bean) {
        Map<String, String> map = new ArrayMap<>(12);
        map.put("ywkey", bean.data.ywKey);
        map.put("appId", Integer.toString(bean.data.appId));
        map.put("areaId", Integer.toString(bean.data.areaId));
        map.put("isFirstRegister", "false");
        map.put("fromSource", UNKNOWN_VERSION_CODE);
        map.put("loginFrom", "0");
        map.put("ywguid", Long.toString(bean.data.ywGuid));
        return map;
    }

    private static String getSignature() {
        StringBuilder sb = Utils.obtainStringBuilder(64);
        sb.append(IMEI).append('|').append(IMEI).append('|')
                .append(System.currentTimeMillis() / 1000);
        String s = sb.toString();
        Utils.recycle(sb);

        byte[] bytes = a.d.c(s);
        s = CryptoUtils.encodeBytes(bytes);
        return s;
    }

    private static String getDeviceType() {
        StringBuilder sb = Utils.obtainStringBuilder(32);
        sb.append(Build.BRAND).append('_').append(Build.MODEL);
        String s = sb.toString();
        Utils.recycle(sb);
        return s;
    }

    private static String getOsVersion() {
        StringBuilder sb = Utils.obtainStringBuilder(32);
        sb.append("Android").append(Build.VERSION.RELEASE).append('_')
                .append(XQDReader.VERSION_NAME).append('_').append(VERSION_CODE);
        String s = sb.toString();
        Utils.recycle(sb);
        return s;
    }


}
