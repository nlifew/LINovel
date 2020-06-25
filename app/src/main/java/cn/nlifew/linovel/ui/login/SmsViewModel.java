package cn.nlifew.linovel.ui.login;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import cn.nlifew.xqdreader.entity.Account;
import cn.nlifew.xqdreader.bean.login.LoginBean;
import cn.nlifew.xqdreader.bean.login.LoginBean_V2;
import cn.nlifew.xqdreader.bean.login.SmsBean;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.LoginUtils;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmsViewModel extends ViewModel {
    private static final String TAG = "SmsViewModel";

    static final CountDown sCountDownTimer = new CountDown();

    static final class State {
        static final int STATUS_FAILED  =   -1;
        static final int STATUS_OK      =   0;
        static final int STATUS_READY   =   1;
        static final int STATUS_VERIFY  =   2;


        @IntDef({
                STATUS_OK,
                STATUS_FAILED,
                STATUS_READY,
                STATUS_VERIFY
        })
        @Retention(RetentionPolicy.SOURCE)
        @interface Status {  }

        final @Status int status;
        final String message;

        State(int status, String message) {
            this.status = status;
            this.message = message;
        }
    }

    public SmsViewModel() {
        mState = new MutableLiveData<>(null);
    }

    final MutableLiveData<State> mState;
    private String mSessionKey;

    void updateVerifyData(String phone, JSONObject json) {
        Log.d(TAG, "updateVerifyData: " + Thread.currentThread().getName());

        String ticket = "", randstr = "";
        int code = -1;
        String errMessage = "";

        try {
            code = json.optInt("errorCode");
            errMessage = json.optString("errMessage");
            randstr = json.optString("randstr");
            ticket = json.optString("ticket");
        } catch (Exception e) {
            Log.e(TAG, "updateVerifyData: ", e);
            code = -1;
            errMessage = e.toString();
        }
        if (code == -1) {
            State state = new State(State.STATUS_FAILED, errMessage);
            mState.postValue(state);
            return;
        }
        sendMsmCode(phone, randstr, ticket);
    }

    void sendMsmCode(String phone, String randStr, String ticket) {
        Map<String, String> body = createSendSmsBody(phone, randStr, ticket);

        IRequest request = NetworkUtils.create(IRequest.class);
        Call<SmsBean> call = request.sendSmsCode(body);
        call.enqueue(new Callback<SmsBean>() {
            @Override
            public void onResponse(Call<SmsBean> call, Response<SmsBean> response) {
                SmsBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }

                bean.trim();
                State state;

                if (bean.code != 0 || bean.data == null || (mSessionKey = bean.data.sessionKey) == null) {
                    // 返回了错误信息
                    state = new State(State.STATUS_FAILED, bean.code + " " + bean.message);
                }
                else if (bean.data.nextAction == 0) {
                    // 服务器允许登录
                    state = new State(State.STATUS_READY, "验证码已发送");
                }
                else if (bean.data.nextAction == 11) {
                    // 需要验证码验证
                    state = new State(State.STATUS_VERIFY, "需要验证您的身份");
                }
                else {
                    // 未知 action
                    state = new State(State.STATUS_FAILED, "unknown action: " +
                            bean.data.nextAction);
                }
                mState.postValue(state);
            }

            @Override
            public void onFailure(Call<SmsBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                State state = new State(State.STATUS_FAILED, t.toString());
                mState.postValue(state);
            }
        });
    }

    void tryToLogin(String phone, String sms) {
        Map<String, String> body = createSmsLoginBody(phone, sms);

        IRequest request = NetworkUtils.create(IRequest.class);
        Call<LoginBean> call = request.loginBySms(body);
        call.enqueue(new Callback<LoginBean>() {
            @Override
            public void onResponse(Call<LoginBean> call, Response<LoginBean> response) {
                LoginBean bean;

                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }

                bean.trim();
                State state = null;
                LoginBean_V2 v2 = null;

                if (bean.code != 0 || bean.data == null) {
                    state = new State(State.STATUS_FAILED, bean.code + " " + bean.message);
                }
                else if (bean.data.ywGuid == 0 || TextUtils.isEmpty(bean.data.ywKey) ||
                        TextUtils.isEmpty(bean.data.ywOpenId)) {
                    state = new State(State.STATUS_FAILED, "未返回 ywKey");
                }
                else if (bean.data.isRiskAccount || bean.data.needSecureCookie) {
                    state = new State(State.STATUS_FAILED, "账户存在风险或需要安全 cookie");
                }
                else {
                    try {
                        v2 = getLoginBean_V2(bean);
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: failed when get LoginBean_V2", e);
                        state = new State(State.STATUS_FAILED, e.toString());
                    }
                }
                if (state == null) {
                    Account account = new Account();
                    account.setLoginBean(bean);
                    account.setLoginBeanV2(v2);
                    account.save();
//                    Account.saveAccount(account);
                    state = new State(State.STATUS_OK, "欢迎");
                }
                mState.postValue(state);
            }

            @Override
            public void onFailure(Call<LoginBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                State state = new State(State.STATUS_FAILED, t.toString());
                mState.postValue(state);
            }
        });
    }

    private LoginBean_V2 getLoginBean_V2(LoginBean bean) throws IOException {
        Map<String, String> body = LoginUtils.createV2Body(bean);

        IRequest request = NetworkUtils.create(IRequest.class);
        Call<LoginBean_V2> call = request.getLoginBean_V2(body);

        Response<LoginBean_V2> response = call.execute();

        LoginBean_V2 v2;
        if (! response.isSuccessful() || (v2 = response.body()) == null) {
            throw new IOException(response.code() + " " + response.message());
        }

        v2.trim();

        if (v2.Result != 0 || v2.Data == null || v2.Data.UserInfo == null) {
            throw new IOException(v2.Result + " " + v2.Message + " " + v2.Data);
        }
        return v2;
    }

    private Map<String, String> createSendSmsBody(String phone, String randStr, String ticket) {
        Map<String, String> body = LoginUtils.createLoginBody();

        body.put("phone", phone);
        body.put("type", "1");
        body.put("needRegister", "1");

        if (mSessionKey != null) {
            body.put("sessionKey", mSessionKey);
        }
        if (randStr != null) {
            body.put("code", randStr);
        }
        if (ticket != null) {
            body.put("sig", ticket);
        }
        return body;
    }

    private Map<String, String> createSmsLoginBody(String phone, String sms) {
        Map<String, String> body = LoginUtils.createLoginBody();
        body.put("phone", phone);
        body.put("phonecode", sms);
        body.put("phonekey", mSessionKey);
        return body;
    }
}
