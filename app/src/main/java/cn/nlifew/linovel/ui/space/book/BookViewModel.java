package cn.nlifew.linovel.ui.space.book;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;

import cn.nlifew.xqdreader.bean.user.UserBean;
import cn.nlifew.xqdreader.bean.user.UserBooksBean;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookViewModel extends ViewModel {
    private static final String TAG = "BookViewModel";

    public BookViewModel() {
        mErrMsg = new MutableLiveData<>(null);
        mUserBooksData = new MutableLiveData<>(null);
    }

    final MutableLiveData<String> mErrMsg;
    final MutableLiveData<UserBean.AuthorBookType> mUserBooksData;

    private String mAuthorId;

    void setAuthorId(String authorId) { mAuthorId = authorId; }

    void refreshData() {
        if (mAuthorId == null || mAuthorId.equals("0")) {
            mErrMsg.postValue("没有更多信息");
            return;
        }

        IRequest request = NetworkUtils.create(IRequest.class);
        Call<UserBooksBean> call = request.getUserBooks(mAuthorId);
        call.enqueue(new Callback<UserBooksBean>() {
            @Override
            public void onResponse(Call<UserBooksBean> call, Response<UserBooksBean> response) {
                UserBooksBean bean;

                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }

                bean.trim();
                if (bean.Result != 0 || bean.Data == null) {
                    mErrMsg.postValue(bean.Result + " " + bean.Message);
                    return;
                }
                if (bean.Data.BookList == null) {
                    mErrMsg.postValue("null BookList");
                    return;
                }
                mUserBooksData.postValue(bean.Data);
            }

            @Override
            public void onFailure(Call<UserBooksBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }
}
