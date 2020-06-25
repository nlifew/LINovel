package cn.nlifew.xqdreader.utils;

import android.graphics.Point;
import android.os.Build;
import android.util.ArrayMap;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import a.c;
import cn.nlifew.xqdreader.XQDReader;
import cn.nlifew.xqdreader.entity.Account;
import cn.nlifew.xqdreader.bean.login.LoginBean;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

import static cn.nlifew.xqdreader.XQDReader.APP_ID;
import static cn.nlifew.xqdreader.XQDReader.AREA_ID;
import static cn.nlifew.xqdreader.XQDReader.IMEI;
import static cn.nlifew.xqdreader.XQDReader.UNKNOWN_VERSION_CODE;
import static cn.nlifew.xqdreader.XQDReader.VERSION_CODE;

class SignUtils {
    private static final String TAG = "SignUtils";


    static Map<String, String> sign(HttpUrl url, RequestBody body) {
        Map<String, String> map = new TreeMap<>();

        if (body == null) {
            for (int i = 0, n = url.querySize(); i < n; i++) {
                String key = url.queryParameterName(i).toLowerCase(Locale.US);
                String value = URLDecoder.decode(url.queryParameterValue(i));
                map.put(key, value);
            }
        }
        else if (body instanceof FormBody) {
            FormBody form = (FormBody) body;
            for (int i = 0, n = form.size(); i < n; i++) {
                String key = form.name(i).toLowerCase(Locale.US);
                String value = URLDecoder.decode(form.value(i));
                map.put(key, value);
            }
        }

        String str = "";

        if (map.size() != 0) {
            StringBuilder sb = Utils.obtainStringBuilder(256);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sb.append(entry.getKey()).append('=').append(entry.getValue())
                        .append('&');
            }
            map.clear();
            sb.setLength(sb.length() - 1);
            str = sb.toString();
            Utils.recycle(sb);
        }

        map.put("QDSign", getQDSign(str));
        map.put("AegisSign", getAegisSign(str));
        map.put("QDInfo", getQDInfo());
        map.put("User-Agent", getUserAgent());
        map.put("Cookie", getCookie());
        return map;
    }

    private static String getQDSign(String url) {
        Account account = Account.currentAccount();
        String token = "0";

        if (account != null) {
            token = account.getUserId();
        }

        byte[] bytes = c.sign(XQDReader.sContext, url,
                Long.toString(System.currentTimeMillis()),
                token, IMEI, "0", 1);

        return CryptoUtils.encodeBytes(bytes);
    }

    private static String getAegisSign(String url) {
        Account account = Account.currentAccount();
        String token = "0";

        if (account != null) {
            token = account.getUserId();
        }

        byte[] bytes = c.s(XQDReader.sContext, url,
                Long.toString(System.currentTimeMillis()),
                token, IMEI, "0");
        return CryptoUtils.encodeBytes(bytes);
    }

    private static String getQDInfo() {
        Point screen = Utils.getDisplaySize(XQDReader.sContext);

        StringBuilder sb = Utils.obtainStringBuilder(128)
                .append(IMEI).append('|')                   // 小写的 imei
                .append(XQDReader.VERSION_NAME).append('|')           // 起点阅读的版本名
                .append(screen.x).append('|')               // 屏幕分辨宽
                .append(screen.y).append('|')               // 屏幕分辨高
                .append(UNKNOWN_VERSION_CODE).append('|')   // 看起来像个版本号，不知道是什么
                .append(Build.VERSION.RELEASE).append('|')  // android 版本
                .append("1").append('|')                    // 未知
                .append(Build.MODEL).append('|')            // 设备名
                .append(VERSION_CODE).append('|')           // 起点阅读的版本号
                .append(UNKNOWN_VERSION_CODE).append('|')   // 和前面的相同
                .append("4").append('|')                    // 未知
                .append("0").append('|')                    // 未知
                .append(System.currentTimeMillis()).append('|')
                .append("0").append('|')                    // 未知
                .append(IMEI).append('|')                   // 再来一次 imei
                .append(XQDReader.UUID);                              // 小写的 uuid
        String s = sb.toString();
        Utils.recycle(sb);
        return CryptoUtils.encodeString(s);
    }

    private static String getUserAgent() {
        StringBuilder sb = Utils.obtainStringBuilder(64);
        sb.append("Mozilla/mobile QDReaderAndroid/")
                .append(XQDReader.VERSION_NAME).append('/')
                .append(VERSION_CODE).append('/')
                .append(UNKNOWN_VERSION_CODE).append('/')
                .append(IMEI);
        String s = sb.toString();
        Utils.recycle(sb);
        return s;
    }

    private static String getCookie() {
        String ywKey = "", ywGuid = "";
        String appID = APP_ID, areaId = AREA_ID;

        Account account = Account.currentAccount();
        if (account != null) {
            ywKey = account.ywKey;
            ywGuid = account.ywGuid;
            appID = account.appId;
            areaId = account.areaId;
        }

        StringBuilder sb = Utils.obtainStringBuilder(512);

        if (account != null) {
            sb.append("loginType=").append("4").append("; ")
                    .append("lgk=").append("1").append("; ")
                    .append("cmfuToken=").append(account.cmfuToken).append("; ");
        }

        sb.append("QDInfo=").append(getQDInfo()).append("; ")
                .append("ywkey=").append(ywKey).append("; ")
                .append("ywguid=").append(ywGuid).append("; ")
                .append("appId=").append(appID).append("; ")
                .append("areaId=").append(areaId).append("; ")
                .append("lang=").append("cn").append("; ")
                .append("mode=").append("normal").append("; ")
                .append("bar=").append("72").append("; ")
                .append("qimei=").append(IMEI);

        String s = sb.toString();
        Utils.recycle(sb);
        return s;
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
