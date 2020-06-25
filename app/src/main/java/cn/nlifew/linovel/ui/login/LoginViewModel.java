package cn.nlifew.linovel.ui.login;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import cn.nlifew.xqdreader.entity.Account;
import cn.nlifew.xqdreader.bean.login.LoginBean;
import cn.nlifew.xqdreader.bean.login.LoginBean_V2;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.LoginUtils;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {
    private static final String TAG = "LoginViewModel";

    final MutableLiveData<State> mState;

    static final class State {
        static final int TARGET_NULL        = 0;
        static final int TARGET_ID          = 1;
        static final int TARGET_PASSWORD    = 2;
        static final int TARGET_TOAST       = 3;

        static final int STATUS_READY   =   1;
        static final int STATUS_SUCCESS =   2;
        static final int STATUS_FAILED  =   3;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({TARGET_NULL, TARGET_ID, TARGET_PASSWORD, TARGET_TOAST})
        @interface Target {  }

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({STATUS_FAILED, STATUS_READY, STATUS_SUCCESS})
        @interface Status {  }

        final String message;
        final @Target int targetView;
        final @Status int status;

        State(@Status int status, @Target int target, String msg) {
            this.status = status;
            this.message = msg;
            this.targetView = target;
        }
    }

    public LoginViewModel() {
        mState = new MutableLiveData<>(null);
    }

    void checkFormData(CharSequence id, CharSequence password) {
        State state;

        if (TextUtils.isEmpty(id)) {
            state = new State(State.STATUS_FAILED, State.TARGET_ID, "输入为空");
        }
        else if (TextUtils.isEmpty(password)) {
            state = new State(State.STATUS_FAILED, State.TARGET_PASSWORD, "密码为空");
        }
        else if (password.length() < 6) {
            state = new State(State.STATUS_FAILED, State.TARGET_PASSWORD, "密码长度过短");
        }
        else {
            state = new State(State.STATUS_READY, State.TARGET_NULL, null);
        }
        mState.setValue(state);
    }

    void tryToLogin(String id, String password) {
        Map<String, String> body = LoginUtils.createLoginBody();
        body.put("ticket", "1");
        body.put("username", id);
        body.put("password", password);

        IRequest request = NetworkUtils.create(IRequest.class);
        Call<LoginBean> call = request.loginByPassword(body);
        call.enqueue(new Callback<LoginBean>() {

            @Override
            public void onResponse(Call<LoginBean> call, Response<LoginBean> response) {
                LoginBean bean;
                LoginBean_V2 v2 = null;

                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }

                bean.trim();

                State state = getLoginBeanErrorState(bean);
                if (state == null) {
                    try {
                        v2 = getLoginBeanV2(bean);
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: ", e);
                        state = new State(State.STATUS_FAILED, State.TARGET_TOAST, e.toString());
                    }
                }
                if (state == null) {
                    Account account = new Account();
                    account.setLoginBean(bean);
                    account.setLoginBeanV2(v2);
                    account.save();
//                    Account.saveAccount(account);
                    state = new State(State.STATUS_SUCCESS, State.TARGET_NULL, "登录成功");
                }
                mState.postValue(state);
            }

            @Override
            public void onFailure(Call<LoginBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                State state = new State(State.STATUS_FAILED, State.TARGET_TOAST, t.toString());
                mState.postValue(state);
            }
        });
    }

    private static State getLoginBeanErrorState(LoginBean bean) {
        State state = null;

        if (bean.code != 0) {
            state = new State(State.STATUS_FAILED, State.TARGET_TOAST,
                    bean.code + " " + bean.message);
        }
        else if (bean.data == null || bean.data.ywGuid == 0 ||
                TextUtils.isEmpty(bean.data.ywKey) ||
                TextUtils.isEmpty(bean.data.ywOpenId)) {
            state = new State(State.STATUS_FAILED, State.TARGET_PASSWORD,
                    "账号或密码错误");
        }
        else if (bean.data.isRiskAccount || bean.data.needSecureCookie) {
            state = new State(State.STATUS_FAILED, State.TARGET_TOAST,
                    "你的账户信息异常或者需要安全 Cookie");
        }
        return state;
    }

    private static LoginBean_V2 getLoginBeanV2(LoginBean bean) throws IOException{

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
}
