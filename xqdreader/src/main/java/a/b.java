package a;

import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import cn.nlifew.xqdreader.utils.CryptoUtils;
import cn.nlifew.xqdreader.utils.Utils;

@SuppressWarnings({"unused"})
public class b {
    private static final String TAG = "b";

    static {
        System.loadLibrary("load-jni");
    }

    public static native byte[] b(long novelId, long chapterId, byte[] bytes, long userId, String imei);


    public static String s(String str, String str2) {
        if (str == null) {
            str = "";
        }
        if (str2 == null) {
            str2 = "";
        }
        String s = a(b(str, str2));
        if (s.length() < 24) {
            StringBuilder sb = Utils.obtainStringBuilder(24);
            sb.append(s);
            for (int i = s.length(); i < 24; i++) {
                sb.append((char) 0);
            }
            s = sb.toString();
            Utils.recycle(sb);
        }
        return s;
    }


    private static int[] b(String str, String str2) {
        int[] b2 = b(str);
        if (b2.length < 16) {
            b2 = Arrays.copyOf(b2, 16);
        }
        else if (b2.length > 16) {
            b2 = a(b2, str.length() * 8);
        }
        int[] iArr = new int[16];
        int[] iArr2 = new int[16];

        for (int i2 = 0; i2 < 16; i2++) {
            iArr[i2] = b2[i2] ^ 909522486;
            iArr2[i2] = b2[i2] ^ 1549556828;
        }
        return a(mergeArray(iArr2, a(mergeArray(iArr, b(str2)), (str2.length() * 8) + 512)), 672);
    }

    private static int[] b(String str) {
        int[] iArr = new int[(str.length() * 8)];
        for (int i = 0; i < str.length() * 8; i += 8) {
            int i2 = i >> 5;
            iArr[i2] = iArr[i2] | ((str.charAt(i / 8) & 255) << (24 - (i & 31)));
        }
        int i3 = 0;
        while (i3 < iArr.length && iArr[i3] != 0) {
            i3++;
        }
        return Arrays.copyOf(iArr, i3);
    }


    private static int[] mergeArray(int[] iArr, int[] iArr2) {
        int[] iArr3 = new int[(iArr.length + iArr2.length)];
        System.arraycopy(iArr, 0, iArr3, 0, iArr.length);
        System.arraycopy(iArr2, 0, iArr3, iArr.length, iArr2.length);
        return iArr3;
    }

    private static String a(int[] iArr) {
        iArr = ensureArrayHasThisIndex(iArr, iArr.length * 4);

        StringBuilder str = Utils.obtainStringBuilder(64);

        for (int i = 0; i < iArr.length * 4; i += 3) {
            int i2 = (((iArr[i >> 2] >> ((3 - (i & 3)) * 8)) & 255) << 16) |
                    (((iArr[(i + 1) >> 2] >> ((3 - ((i + 1) & 3)) * 8)) & 255) << 8) |
                    ((iArr[(i + 2) >> 2] >> ((3 - ((i + 2) & 3)) * 8)) & 255);

            for (int j = 0; j < 4; j++) {
                if ((i * 8) + (j * 6) > iArr.length * 32) {
                    str.append('=');
                } else {
                    str.append(CryptoUtils.KEYS[(i2 >> ((3 - j) * 6)) & 63]);
                }
            }
        }
        String s = str.toString();
        Utils.recycle(str);
        return a(s);
    }

    private static String a(String str) {
        if (str == null) {
            str = "";
        }
        int length = str.length();
        if (length <= 1) {
            return str;
        }
        char charAt = str.charAt(length - 1);

        int idx = length - 1;
        while (idx >= 0 && str.charAt(idx) == charAt) {
            idx --;
        }
        return str.substring(0, idx + 1);
    }

    private static int[] a(int[] iArr, int i) {
        int[] b2 = ensureArrayHasThisIndex(iArr, i >> 5);
        b2[i >> 5] = b2[i >> 5] | (128 << (24 - (i & 31)));

        int[] b3 = ensureArrayHasThisIndex(b2, (((i + 64) >> 9) << 4) + 15);
        b3[(((i + 64) >> 9) << 4) + 15] = i;
        int[] iArr2 = new int[80];
        int i3 = 1732584193;
        int i4 = -271733879;
        int i5 = -1732584194;
        int i6 = 271733878;
        int i7 = -1009589776;
        for (int i8 = 0; i8 < b3.length; i8 += 16) {
            int i9 = 0;
            int i10 = i7;
            int i11 = i6;
            int i12 = i5;
            int i13 = i4;
            int i14 = i3;
            while (i9 < 80) {
                if (i9 < 16) {
                    iArr2[i9] = b3[i8 + i9];
                } else {
                    iArr2[i9] = a(((iArr2[i9 - 3] ^ iArr2[i9 - 8]) ^ iArr2[i9 - 14]) ^ iArr2[i9 - 16], 1);
                }
                int b4 = b(b(a(i14, 5), a(i9, i13, i12, i11)), b(b(i10, iArr2[i9]), a(i9)));
                int a2 = a(i13, 30);
                i9++;
                i13 = i14;
                i14 = b4;
                i10 = i11;
                i11 = i12;
                i12 = a2;
            }
            i3 = b(i14, i3);
            i4 = b(i13, i4);
            i5 = b(i12, i5);
            i6 = b(i11, i6);
            i7 = b(i10, i7);
        }
        return new int[]{i3, i4, i5, i6, i7};
    }

    private static int a(int i, int i2) {
        return (i << i2) | (i >>> (32 - i2));
    }

    private static int b(int i, int i2) {
        int i3 = (i & 65535) + (i2 & 65535);
        return (i3 & 65535) | ((( (i >> 16) + (i2 >> 16)) + (i3 >> 16)) << 16);
    }

    private static int a(int i, int i2, int i3, int i4) {
        if (i < 20) {
            return (i2 & i3) | ((~i2) & i4);
        }
        if (i < 40) {
            return (i2 ^ i3) ^ i4;
        }
        if (i < 60) {
            return (i2 & i3) | (i2 & i4) | (i3 & i4);
        }
        return (i2 ^ i3) ^ i4;
    }

    private static int a(int i) {
        if (i < 20) {
            return 1518500249;
        }
        if (i < 40) {
            return 1859775393;
        }
        return i < 60 ? -1894007588 : -899497514;
    }

    private static int[] ensureArrayHasThisIndex(int[] iArr, int i) {
        return iArr.length >= i + 1 ? iArr : Arrays.copyOf(iArr, i + 1);
    }

    public static String m(String str, String str2) {
        byte[] bytes = encodeBytes(str.getBytes(), str2.getBytes());
        return CryptoUtils.encodeBytes(bytes);
    }

    public static byte[] d(byte[] bytes, String str) {
        return CryptoUtils.decodeBytes(bytes, str);
    }

    private static byte[] encodeBytes(byte[] bytes1, byte[] bytes2) {
        // 将参数 1 调整长度到 64。如果超过 64 位，那么取它的 MD5
        // 后面不够的内容填充为 0
        if (bytes1.length > 64) {
            bytes1 = digest(bytes1);
        }
        if (bytes1.length != 64) {
            byte[] newBytes = new byte[64];
            System.arraycopy(bytes1, 0, newBytes, 0, bytes1.length);
            bytes1 = newBytes;
        }

        // 构建一个新的数组，它的前 64 位是参数 1 和 54 的异或
        // 后面的内容为 参数 2
        byte[] bytes3 = new byte[64 + bytes2.length];
        for (int i = 0; i < 64; i++) {
            bytes3[i] = (byte) (bytes1[i] ^ 54);
        }
        System.arraycopy(bytes2, 0, bytes3, 64, bytes2.length);

        // 计算上面这个数组的 MD5
        bytes3 = digest(bytes3);

        // 再次构建一个新数组，它的前 64 位是参数 1 和 92 的异或
        // 后面的内容为上面的数组
        byte[] bytes4 = new byte[64 + bytes3.length];
        for (int i = 0; i < 64; i++) {
            bytes4[i] = (byte) (bytes1[i] ^ 92);
        }
        System.arraycopy(bytes3, 0, bytes4, 64, bytes3.length);

        return digest(bytes4);
    }

    private static byte[] digest(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(bytes);
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "digest: ", e);
        }
        return new byte[0];
    }
}
