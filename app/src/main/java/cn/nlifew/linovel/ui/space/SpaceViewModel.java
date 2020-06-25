package cn.nlifew.linovel.ui.space;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;

import cn.nlifew.xqdreader.bean.user.UserBean;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpaceViewModel extends ViewModel {
    private static final String TAG = "SpaceViewModel";

    public SpaceViewModel() {
        mErrMsg = new MutableLiveData<>(null);
        mUserData = new MutableLiveData<>(null);
    }

    final MutableLiveData<String> mErrMsg;
    final MutableLiveData<UserBean.UserType> mUserData;

    private String mUserId, mAuthorId;

    void setUserId(String userId, String authorId) {
        mUserId = userId;
        mAuthorId = authorId;
    }

    void refreshUserData() {
        IRequest request = NetworkUtils.create(IRequest.class);
        Call<UserBean> call = request.getUserPageInfo(mUserId, mAuthorId);
        call.enqueue(new Callback<UserBean>() {
            @Override
            public void onResponse(Call<UserBean> call, Response<UserBean> response) {
                UserBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                bean.trim();
                if (bean.Result != 0 || bean.Data == null) {
                    mErrMsg.postValue(bean.Result + " " + bean.Message);
                    return;
                }
                if (bean.Data.UserInfo == null) {
                    mErrMsg.postValue("null user info");
                    return;
                }
                mUserData.postValue(bean.Data.UserInfo);
            }

            @Override
            public void onFailure(Call<UserBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }
}
