package a;

import android.content.Context;

public class c {

    static {
        System.loadLibrary("c-lib");    /* s */
        System.loadLibrary("sos");      /* sign */
        System.loadLibrary("crypto");   /* MD5, AES ... */
    }

    /**
     *
     * @param c 提供 PackageManager 以进行签名验证等
     * @param url 包装后的网络请求参数
     * @param timeMs System.currentTimeMillis() + x, x 通常为 0
     * @param token QDConfig.db/setting/SettingUserToken 的以 "|" 分割的第一个子字符串
     * @param imei IMEI
     * @param zero 通常为 "0"
     * @return signs
     */
    public static native byte[] s(Context c, String url, String timeMs, String token, String imei, String zero);

    public static native byte[] sign(Context context, String url, String timeMs, String token, String imei, String zero, int one);
}
