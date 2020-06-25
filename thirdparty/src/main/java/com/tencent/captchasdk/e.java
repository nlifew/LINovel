package com.tencent.captchasdk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.WebViewTransport;
import android.webkit.WebViewClient;
import java.net.URLDecoder;
import org.json.JSONObject;

class e {
    private static final String TAG = "e";

    /* renamed from: a reason: collision with root package name */
    private a f25181a;

    /* renamed from: b reason: collision with root package name */
    private Context f25182b;

    /* renamed from: c reason: collision with root package name */
    private String f25183c;

    /* renamed from: d reason: collision with root package name */
    private WebView f25184d;
    private String e;
    private int f;
    private final WebChromeClient g = new WebChromeClient() {
        public void onCloseWindow(WebView webView) {
            super.onCloseWindow(webView);
        }

        public boolean onCreateWindow(WebView webView, boolean z, boolean z2, Message message) {
            try {
                WebView webView2 = new WebView(webView.getContext());
                webView2.setWebViewClient(new WebViewClient() {
                    public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(str));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            webView.getContext().startActivity(intent);
                        } catch (Exception e) {
                            Log.e(TAG, "shouldOverrideUrlLoading: ", e);
                        }
                        return true;
                    }
                });
                ((WebViewTransport) message.obj).setWebView(webView2);
                message.sendToTarget();
            } catch (Exception e) {
                Log.e(TAG, "onCreateWindow: ", e);
            }
            return true;
        }

        public void onProgressChanged(WebView webView, int i) {
        }

        public void onReceivedTitle(WebView webView, String str) {
        }
    };
    private final WebViewClient h = new WebViewClient() {
        public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
            Uri url = webResourceRequest.getUrl();
            if (!"tcwebscheme".equals(url.getScheme())) {
                return super.shouldOverrideUrlLoading(webView, webResourceRequest);
            }
            e.this.a(url);
            return true;
        }

        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
            Uri parse = Uri.parse(str);
            if (! "tcwebscheme".equals(parse.getScheme())) {
                return super.shouldOverrideUrlLoading(webView, str);
            }

            e.this.a(parse);
            return true;
        }
    };

    public interface a {
        void a(int i, int i2);

        void a(int i, String str);

        void a(String str);
    }

    e(Context context, a aVar, String str, WebView webView, String str2, int i) {
        this.f25182b = context;
        this.f25181a = aVar;
        this.f25183c = str;
        this.f25184d = webView;
        if (str2 == null || str2.length() <= 0) {
            this.e = "";
        } else {
            this.e = str2;
        }
        if (i > 0) {
            this.f = i;
        } else {
            this.f = 100;
        }
        b();
    }

    /* access modifiers changed from: private */
    public void a(Uri uri) {
        if ("tcwebscheme".equals(uri.getScheme())) {
            String authority = uri.getAuthority();

            if ("callback".equals(authority)) {
                e(uri);
            }
            if ("readyCallback".equals(authority)) {
                d(uri);
            }
            if ("collectCallback".equals(authority)) {
                c(uri);
            }
            if ("jserrorCallback".equals(authority)) {
                b(uri);
            }
        }
    }

    private boolean a(Context context) {
        try {
            return (context.getApplicationInfo().flags & 2) != 0;
        } catch (Exception e2) {
            return false;
        }
    }

    private void b(Uri uri) {
        if (uri != null) {
            try {
                if (this.f25182b != null && this.f25181a != null) {
                    this.f25181a.a(-1001, URLDecoder.decode(uri.getQueryParameter("msg"), "utf-8"));
                }
            } catch (Exception e2) {
                this.f25181a.a(-1001, uri.getQueryParameter("msg"));
            }
        }
    }

    private void c(Uri uri) {
        if (this.f25182b != null && uri != null) {
            try {
                String queryParameter = uri.getQueryParameter("list");
                String queryParameter2 = uri.getQueryParameter("callback");
                if (queryParameter != null && uri.getQueryParameter("callback") != null && Integer.parseInt(queryParameter) > 0) {
                    int parseInt = Integer.parseInt(queryParameter);
                    JSONObject jSONObject = new JSONObject();
                    if ((parseInt & 1) == 1) {
                        c.a();
                        jSONObject.put("cpu_info", c.f25172a);
                        jSONObject.put("cpu_hardware", c.f25173b);
                        jSONObject.put("cpu_serial", c.f25174c);
                    }
                    if ((parseInt & 2) == 2) {
                        jSONObject.put("battery_level", c.a(this.f25182b));
                    }
                    if ((parseInt & 4) == 4) {
                        jSONObject.put("dpi", (double) c.b(this.f25182b));
                        jSONObject.put("width", c.c(this.f25182b));
                        jSONObject.put("height", c.d(this.f25182b));
                    }
                    if ((parseInt & 8) == 8) {
                        jSONObject.put("sensor_flag", c.e(this.f25182b));
                    }
                    if ((parseInt & 16) == 16) {
                        jSONObject.put("network_type", c.a(this.f25182b, 0));
                        jSONObject.put("network_operator_name", c.a(this.f25182b, 1));
                        jSONObject.put("network_connection_type", c.a(this.f25182b, 2));
                    }
                    if ((parseInt & 32) == 32) {
                        jSONObject.put("wifi_ssid", c.b(this.f25182b, 0));
                        jSONObject.put("wifi_bssid", c.b(this.f25182b, 1));
                        jSONObject.put("wifi_connected", c.f(this.f25182b));
                    }
                    if ((parseInt & 64) == 64) {
                        jSONObject.put("band_version", c.b());
                        jSONObject.put("osname", c.e());
                    }
                    if ((parseInt & 128) == 128) {
                        jSONObject.put("app_name", c.g(this.f25182b));
                        jSONObject.put("app_version", c.h(this.f25182b));
                    }
                    if ((parseInt & 256) == 256) {
                        jSONObject.put("kernel_version", c.f());
                    }
                    if ((parseInt & 512) == 512) {
                        jSONObject.put("is_emulator", c.c());
                        jSONObject.put("is_root", c.d());
                    }
                    if (jSONObject.length() > 0) {
                        jSONObject.put("platform", "Android");
                        this.f25184d.evaluateJavascript("javascript:window." + queryParameter2 + "(" + jSONObject.toString() + ")", new ValueCallback<String>() {
                            /* renamed from: a */
                            public void onReceiveValue(String str) {
                            }
                        });
                    }
                }
            } catch (Exception e2) {
                Log.e(TAG, "c: ", e2);
            }
        }
    }

    private void d(Uri uri) {
        String w = uri.getQueryParameter("width");
        String h = uri.getQueryParameter("height");
        if (this.f25181a != null && w != null && h != null) {
            this.f25181a.a(Integer.parseInt(w), Integer.parseInt(h));
        }
    }

    private void e(Uri uri) {
        try {
            if (this.f25181a != null) {
                this.f25181a.a(URLDecoder.decode(uri.getQueryParameter("retJson"), "utf-8"));
            }
        } catch (Exception e2) {
            this.f25181a.a(uri.getQueryParameter("retJson"));
        }
    }

    void a() {
        this.f25181a = null;
        this.f25182b = null;
        this.f25184d = null;
    }

    private void b() {
        if (this.f25182b == null) {
            Log.e("tcaptcha verify_error", "context is null");
        } else if (this.f25181a == null) {
            Log.e("tcaptcha verify_error", "listener is null");
        } else {
            this.f25184d.getSettings().setDefaultTextEncodingName("UTF-8");
            this.f25184d.setWebChromeClient(this.g);
            this.f25184d.setWebViewClient(this.h);
            try {
                this.f25184d.removeJavascriptInterface("searchBoxJavaBridge_");
            } catch (Exception e2) {
                Log.e(TAG, "b: ", e2);
            }
            WebSettings settings = this.f25184d.getSettings();
            try {
                settings.setJavaScriptEnabled(true);
                settings.setJavaScriptCanOpenWindowsAutomatically(true);
                settings.setSupportMultipleWindows(true);
                settings.setNeedInitialFocus(false);
                settings.setUserAgentString(settings.getUserAgentString() + " TCSDK/1.0.2");
                settings.setAllowFileAccess(true);
                settings.setAppCacheEnabled(true);
                settings.setDomStorageEnabled(true);
                settings.setDatabaseEnabled(true);
                CookieManager.getInstance().setAcceptCookie(true);
                CookieManager.getInstance().setAcceptThirdPartyCookies(this.f25184d, true);
            } catch (Exception e3) {
                Log.e(TAG, "b: ", e3);
            }
            settings.setBuiltInZoomControls(false);
            settings.setSupportZoom(false);
            if (a(this.f25182b)) {
                try {
                    WebView.setWebContentsDebuggingEnabled(true);
                } catch (Exception e4) {
                    Log.e(TAG, "b: ", e4);
                }
            }
            this.f25184d.loadUrl("file:///android_asset/tcaptcha_webview.html?appid=" + this.f25183c + "&width=" + this.f + "&height=" + this.f + "&map=" + this.e);
        }
    }
}