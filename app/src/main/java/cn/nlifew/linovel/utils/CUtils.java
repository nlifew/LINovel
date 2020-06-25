package cn.nlifew.linovel.utils;

public final class CUtils {

    static {
        System.loadLibrary("cutils");
    }

    public static native int getStringOffset(String s, int utf_offset);

    public static native int getStringUTFOffset(String s, int offset);
}
