package cn.nlifew.xqdreader.utils;

import android.util.Log;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.nlifew.xqdreader.bean.login.LoginBean;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    private NetworkUtils() {  }

    private static Retrofit sRetrofit;

    private static Retrofit createRetrofit() {
        HostnameVerifier verifier = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };
        X509TrustManager x509 = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[] {x509},
                    new SecureRandom());
            builder = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory())
                    .hostnameVerifier(verifier);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            Log.e(TAG, "createRetrofit: ", e);
        }

        builder.addInterceptor(new HeaderInterceptor());
        Executor callbackExecutor = new Executor() {
            @Override
            public void execute(Runnable command) {
                try {
                    command.run();
                } catch (Exception e) {
                    String TAG = Thread.currentThread().getName();
                    Log.e(TAG, "execute: avoid to shutting down", e);
                }
            }
        };

        return new Retrofit.Builder()
                .baseUrl("https://druidv6.if.qidian.com/")
                .client(builder.build())
                .callbackExecutor(callbackExecutor)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static <T> T create(Class<T> clazz) {
        if (sRetrofit == null) {
            synchronized (NetworkUtils.class) {
                if (sRetrofit == null) {
                    sRetrofit = createRetrofit();
                }
            }
        }
        return sRetrofit.create(clazz);
    }

    public static String novelCoverImage(int id) {
        return String.format(Locale.US, "http://bookcover.yuewen.com/qdbimg/349573/%d/180",
                id);
    }

    public static String novelCoverImage(String id) {
        return String.format(Locale.US, "http://bookcover.yuewen.com/qdbimg/349573/%s/180",
                id);
    }
}
