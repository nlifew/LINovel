package cn.nlifew.xqdreader.utils;

import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

@SuppressWarnings("WeakerAccess")
public final class CryptoUtils {
    private static final String TAG = "CryptoUtils";

    private CryptoUtils() {  }

    /**
     * 用来随机生成一个 IMEI
     * 关于 IMEI 格式的说明：
     * 1. TAC, 类型分配码，8 位，早期为 6 位，表示生产厂商；
     *     其中前两位为授权 IMEI 机构的代码，如 01 为美国，86 为中国
     * 2. FAC，装配地区码，2 位，与 TAC 共 8 位
     * 3. SNR 序列号，6 位，可随机
     * 4. CD 检验码，1 位，由前 14 位计算得到，规则如下：
     *      (1) 将偶数位数字乘 2，分别计算个位数和十位数的和
     *      (2) 将奇数位数字相加，再加上上面的值
     *      (3) 取这个数的个位，如果为 0，则校验码为 0，否则为 10 减去这个值
     *      例：35 89 01 80 69 72 41，经 (1), 5x2=10, 9x2=18, 1x2=2, 0x2=0, 9x2=18, 2x2=4, 1x2=2，
     *      个位数和十位数相加，为 1+0+1+8+2+0+1+8+4+2=27, 经 (2), 29+3+8+0+8+6+7+4=63
     *      经 (3)，校验码为 10-3=7
     * @return imei
     */

    public static String randomIMEI() {
        StringBuilder sb = Utils.obtainStringBuilder(16);
        sb.append("86767402").append(String.format(
                Locale.US, "%06d", (int)(Math.random() * 1000000)));

        int sum = 0;

        for (int i = 0; i < 14; i++) {
            int t = sb.charAt(i) - '0';
            sum += (i & 1) == 0 ? t : ((t << 1) / 10 + (t << 1) % 10);
        }
        sum %= 10;
        sb.append(sum == 0 ? 0 : 10 - sum);

        String imei = sb.toString();
        Utils.recycle(sb);
        return imei;
    }

    public static String randomUUID() {
        String uuid = UUID.randomUUID().toString();
        char[] buff = new char[16];
        for (int i = 0, j = 0; i < buff.length; i++) {
            char ch = uuid.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                buff[j++] = (char) (ch - 'A' + 'a');
            }
            else if (ch != '-') {
                buff[j++] = ch;
            }
        }
        return new String(buff, 0, buff.length);
    }

    public static final char[] KEYS = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/'
    };

    public static String encodeBytes(byte[] bytes) {
        int length = bytes.length;
        StringBuilder sb = Utils.obtainStringBuilder((bytes.length * 3) / 2);
        int i = length - 3;
        int i2 = 0;
        int i3 = 0;
        while (i3 <= i) {
            int b2 = ((bytes[i3] & 255) << 16) | ((bytes[i3 + 1] & 255) << 8) | (bytes[i3 + 2] & 255);
            sb.append(KEYS[(b2 >> 18) & 63]);
            sb.append(KEYS[(b2 >> 12) & 63]);
            sb.append(KEYS[(b2 >> 6) & 63]);
            sb.append(KEYS[b2 & 63]);
            int i4 = i3 + 3;
            int i5 = i2 + 1;
            if (i2 >= 14) {
                sb.append(" ");
                i5 = 0;
            }
            i2 = i5;
            i3 = i4;
        }
        if (i3 == (0 + length) - 2) {
            int i6 = ((bytes[i3 + 1] & 255) << 8) | ((bytes[i3] & 255) << 16);
            sb.append(KEYS[(i6 >> 18) & 63]);
            sb.append(KEYS[(i6 >> 12) & 63]);
            sb.append(KEYS[(i6 >> 6) & 63]);
            sb.append("=");
        } else if (i3 == (0 + length) - 1) {
            int i7 = (bytes[i3] & 255) << 16;
            sb.append(KEYS[(i7 >> 18) & 63]);
            sb.append(KEYS[(i7 >> 12) & 63]);
            sb.append("==");
        }
        String s = sb.toString();
        Utils.recycle(sb);
        return s;
    }

    private static final IvParameterSpec IV_PARAMETER_SPEC = new IvParameterSpec(new byte[8]);

    static String encodeString(String str) {
        byte[] bArr = encodeBytes(str.getBytes(), "0821CAAD409B8402");
        if (bArr == null) {
            return "";
        }

        StringBuilder stringBuffer = Utils.obtainStringBuilder(128);
        int length = bArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            int i2 = i + 1;
            int b2 = bArr[i] & 255;
            if (i2 == length) {
                stringBuffer.append(KEYS[b2 >>> 2]);
                stringBuffer.append(KEYS[(b2 & 3) << 4]);
                stringBuffer.append("==");
                break;
            }
            int i3 = i2 + 1;
            int b3 = bArr[i2] & 255;
            if (i3 == length) {
                stringBuffer.append(KEYS[b2 >>> 2]);
                stringBuffer.append(KEYS[((b2 & 3) << 4) | ((b3 & 240) >>> 4)]);
                stringBuffer.append(KEYS[(b3 & 15) << 2]);
                stringBuffer.append("=");
                break;
            }
            i = i3 + 1;
            int b4 = bArr[i3] & 255;
            stringBuffer.append(KEYS[b2 >>> 2]);
            stringBuffer.append(KEYS[((b2 & 3) << 4) | ((b3 & 240) >>> 4)]);
            stringBuffer.append(KEYS[((b3 & 15) << 2) | ((b4 & 192) >>> 6)]);
            stringBuffer.append(KEYS[b4 & 63]);
        }
        String s = stringBuffer.toString();
        Utils.recycle(stringBuffer);
        return s;
    }

    public static byte[] decodeBytes(byte[] src, String key) {
        if (src == null || src.length == 0 || key == null) {
            return null;
        }
        byte[] keys = key.getBytes(StandardCharsets.UTF_8);
        if (keys.length == 16) {
            byte[] newBytes = new byte[24];
            System.arraycopy(keys, 0, newBytes, 0, 16);
            System.arraycopy(keys, 0, newBytes, 16, 8);
            keys = newBytes;
        }
        try {
            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(keys, "DESede");
            cipher.init(DECRYPT_MODE, secretKeySpec, IV_PARAMETER_SPEC);

            return cipher.doFinal(src);
        } catch (Exception e) {
            Log.e(TAG, "decodeBytes: ", e);
        }
        return null;
    }

    public static byte[] encodeBytes(byte[] src, String key) {
        if (src == null || src.length == 0 || key == null) {
            return null;
        }

        byte[] keys = key.getBytes();

        if (keys.length == 16) {
            byte[] newBytes = new byte[24];
            System.arraycopy(keys, 0, newBytes, 0, 16);
            System.arraycopy(keys, 0, newBytes, 16, 8);
            keys = newBytes;
        }
        try {
            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(keys, "DESede");
            cipher.init(ENCRYPT_MODE, secretKeySpec, IV_PARAMETER_SPEC);

            return cipher.doFinal(src);
        } catch (Exception e) {
            Log.e(TAG, "decodeBytes: ", e);
        }
        return null;
    }
}
