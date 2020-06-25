package cn.nlifew.xqdreader.utils;


import java.io.IOException;
import java.util.Map;

import cn.nlifew.xqdreader.utils.SignUtils;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {
    private static final String TAG = "HeaderInterceptor";

    public static final String IGNORE_ADD_HEADER = "ignore_add_header";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        Request.Builder newRequest = oldRequest.newBuilder();

        Headers headers = oldRequest.headers();
        if (headers.get(IGNORE_ADD_HEADER) != null) {
            newRequest.removeHeader(IGNORE_ADD_HEADER);
        }
        else {
            Map<String, String> map = SignUtils.sign(oldRequest.url(), oldRequest.body());
            for (Map.Entry<String, String> entry : map.entrySet()) {
                newRequest.header(entry.getKey(), entry.getValue());
            }
        }
        return chain.proceed(newRequest.build());
    }
}
