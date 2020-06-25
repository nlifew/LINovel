package cn.nlifew.linovel.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;

public final class IOUtils {
    private static final String TAG = "IOUtils";

    private static SoftReference<byte[]> IOBuff;
    private static final byte[] DATA_BUFF = new byte[8];


    public static int readLEInt(InputStream is) throws IOException {
        synchronized (DATA_BUFF) {
            int count = is.read(DATA_BUFF, 0, 4);
            if (count != 4) {
                throw new IOException("expected 4 bytes, actually " + count + " bytes");
            }
            int result = 0;
            result |= (DATA_BUFF[3] & 0xff) << 24;
            result |= (DATA_BUFF[2] & 0xff) << 16;
            result |= (DATA_BUFF[1] & 0xff) << 8;
            result |= (DATA_BUFF[0] & 0xff);

            return result;
        }
    }

    public synchronized static InputStream dupStream(InputStream is, File file) throws IOException {
        Log.d(TAG, "dupStream: " + file.getAbsolutePath());

        byte[] buff;

        if (IOBuff == null || (buff = IOBuff.get()) == null) {
            buff = new byte[4*1024];
            IOBuff = new SoftReference<>(buff);
        }

        OutputStream os = new FileOutputStream(file);

        int count;
        while ((count = is.read(buff, 0, buff.length)) != -1) {
            os.write(buff, 0, count);
        }
        os.flush();
        os.close();
        is.close();

        return new FileInputStream(file);
    }
}
