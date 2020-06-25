package com.tencent.captchasdk;

import org.json.JSONObject;

public interface TCaptchaVerifyListener {
    void onVerifyCallback(JSONObject jSONObject);
}