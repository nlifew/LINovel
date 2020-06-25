package com.tencent.captchasdk;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.tencent.captchasdk.e.a;
import org.json.JSONObject;

import cn.nlifew.thirdparty.R;


public class TCaptchaDialog extends Dialog {
    private static final String TAG = "TCaptchaDialog";

    /* renamed from: a reason: collision with root package name */
    private Context f25155a;

    /* renamed from: b reason: collision with root package name */
    private String f25156b;
    /* access modifiers changed from: private */

    /* renamed from: c reason: collision with root package name */
    private float f25157c;
    /* access modifiers changed from: private */

    /* renamed from: d reason: collision with root package name */
    private b f25158d;
    private String e;
    /* access modifiers changed from: private */
    private RelativeLayout f;
    /* access modifiers changed from: private */
    private TCaptchaVerifyListener g;
    private e h;
    private a i = new a() {
        public void a(int i, int i2) {
            LayoutParams layoutParams = TCaptchaDialog.this.f25158d.getLayoutParams();
            layoutParams.width = (int) (((float) i) * TCaptchaDialog.this.f25157c);
            layoutParams.height = (int) (((float) i2) * TCaptchaDialog.this.f25157c);
            TCaptchaDialog.this.f25158d.setLayoutParams(layoutParams);
            TCaptchaDialog.this.f25158d.setVisibility(View.VISIBLE);
            TCaptchaDialog.this.f.setVisibility(View.INVISIBLE);
        }

        public void a(int i, String str) {
            TCaptchaDialog.this.dismiss();
            try {
                if (TCaptchaDialog.this.g != null) {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("ret", i);
                    jSONObject.put("info", str);
                    TCaptchaDialog.this.g.onVerifyCallback(jSONObject);
                    TCaptchaDialog.this.dismiss();
                }
            } catch (Exception e) {
                Log.e(TAG, "a: ", e);
            }
        }

        public void a(String str) {
            try {
                if (TCaptchaDialog.this.g != null) {
                    TCaptchaDialog.this.g.onVerifyCallback(new JSONObject(str));
                }
                TCaptchaDialog.this.dismiss();
            } catch (Exception e) {
                Log.e(TAG, "a: ", e);
            }
        }
    };

    public TCaptchaDialog(@NonNull Context context, int i2, String str, TCaptchaVerifyListener tCaptchaVerifyListener, String str2) {
        super(context, i2);
        a(context, str, tCaptchaVerifyListener, str2);
    }

    public TCaptchaDialog(@NonNull Context context, String str, TCaptchaVerifyListener tCaptchaVerifyListener, String str2) {
        super(context);
        a(context, str, tCaptchaVerifyListener, str2);
    }

    public TCaptchaDialog(@NonNull Context context, boolean z, OnCancelListener onCancelListener, String str, TCaptchaVerifyListener tCaptchaVerifyListener, String str2) {
        super(context, z, onCancelListener);
        a(context, str, tCaptchaVerifyListener, str2);
    }

    private void a(@NonNull Context context, String str, TCaptchaVerifyListener tCaptchaVerifyListener, String str2) {
        this.f25155a = context;
        this.f25156b = str;
        this.g = tCaptchaVerifyListener;
        this.e = str2;
    }

    public void dismiss() {
        try {
            if (this.h != null) {
                this.h.a();
            }
            if (this.f25158d != null) {
                if (this.f25158d.getParent() != null) {
                    ((ViewGroup) this.f25158d.getParent()).removeView(this.f25158d);
                }
                this.f25158d.removeAllViews();
                this.f25158d.destroy();
                this.f25158d = null;
            }
        } catch (Exception e2) {
            Log.e(TAG, "dismiss: ", e2);
        }
        super.dismiss();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(R.layout.tcaptcha_popup);
        this.f25157c = this.f25155a.getResources().getDisplayMetrics().density;
        RelativeLayout relativeLayout = findViewById(R.id.tcaptcha_container);
        this.f25158d = new b(this.f25155a);
        this.f = findViewById(R.id.tcaptcha_indicator_layout);
        this.h = new e(this.f25155a, this.i, this.f25156b, this.f25158d, this.e, c.a(this.f25155a, getWindow(), relativeLayout, this.f, this.f25158d));
    }
}