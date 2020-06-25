package cn.nlifew.linovel.utils;

public final class TextUtils {
    private static final String TAG = "TextUtils";

    public static void subInteger(String s, int startPosition, StringBuilder sb) {
        int idx = -1, n = s.length();
        for (int i = startPosition; i < n; i++) {
            char ch = s.charAt(i);
            if (ch >= '0' && ch <= '9') {
                idx = i;
                break;
            }
        }
        for (int i = idx; i < n && i != -1; i++) {
            char ch = s.charAt(i);
            if (ch < '0' || ch > '9') {
                sb.append(s, idx, i);
                return;
            }
        }
    }
}
