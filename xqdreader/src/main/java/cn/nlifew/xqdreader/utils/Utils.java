package cn.nlifew.xqdreader.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.util.Log;
import android.view.WindowManager;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

public final class Utils {
    private static final String TAG = "Utils";

    private Utils() {  }

//    public static

    static PackageInfo getPackageInfo(Context c, String pkgName, int flag) {
        PackageManager pm = c.getPackageManager();
        try {
            return pm.getPackageInfo(pkgName, flag);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getPackageInfo: " + pkgName, e);
        }
        return null;
    }

    public static Point getDisplaySize(Context c) {
        Point point = new Point();
        WindowManager wm = (WindowManager) c
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(point);
        return point;
    }


    private static void appendInteger(String s, int offset, StringBuilder sb) {
        int n = s.length();
        int idx = -1;
        for (int i = offset; i < n; i++) {
            char ch = s.charAt(i);
            if (ch >= '0' && ch <= '9') {
                idx = i != 0 && s.charAt(i - 1) == '-' ? i - 1 : i;
                break;
            }
        }
        for (int i = idx + 1; i < n && i > 0; i++) {
            char ch = s.charAt(i);
            if (ch < '0' || ch > '9') {
                sb.append(s, idx, i);
                return;
            }
        }
    }

    public static void appendQueryParam(String s, int offset, String key, StringBuilder sb) {
        int n = key.length();
        for (int index = s.indexOf(key, offset); index > 0; index = s.indexOf(key, index + 1)) {
            if (s.charAt(index - 1) != '"' || s.charAt(index + n) != '"' ||
                    s.charAt(index + n + 1) != ':') {
                continue;
            }
            index += n + 2;
            if (s.charAt(index) == '"') {
                int end = s.indexOf('"', index + 1);
                sb.append(s, index + 1, end);
            } else {
                appendInteger(s, index, sb);
            }
            return;
        }
    }



    private static final int MAX_STRING_BUILDER_POOL_SIZE = 10;
    private static StringBuilderReference sStringBuilderPool;
    private static int sStringBuilderPoolSize;

    public static StringBuilder obtainStringBuilder(int capacity) {
        synchronized (StringBuilderReference.class) {

            while (sStringBuilderPool != null) {
                StringBuilderReference ref = sStringBuilderPool;
                sStringBuilderPool = ref.next;
                ref.next = null;
                sStringBuilderPoolSize --;

                StringBuilder sb = ref.get();
                if (sb != null) {
                    sb.ensureCapacity(capacity);
                    return sb;
                }
            }
            return new StringBuilder(capacity);
        }
    }

    public static void recycle(StringBuilder sb) {
        synchronized (StringBuilderReference.class) {
            if (sStringBuilderPoolSize >= MAX_STRING_BUILDER_POOL_SIZE) {
                return;
            }
            sb.setLength(0);
            StringBuilderReference ref = new StringBuilderReference(sb);
            ref.next = sStringBuilderPool;
            sStringBuilderPool = ref;
            sStringBuilderPoolSize ++;
        }
    }

    private static final class StringBuilderReference extends SoftReference<StringBuilder> {
        StringBuilderReference(StringBuilder referent) {
            super(referent);
        }

        StringBuilderReference next;
    }
}
