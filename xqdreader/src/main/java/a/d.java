package a;

public class d {
    static {
        System.loadLibrary("d-lib");
    }

    public static native byte[] c(String str);
}
