package cn.nlifew.linovel.fragment.bookmark;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;

import cn.nlifew.xqdreader.bean.TopBookMarkBean;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookMarkViewModel extends ViewModel {
    private static final String TAG = "BookMarkViewModel";

    public BookMarkViewModel() {
        mErrMsg = new MutableLiveData<>(null);
        mBookMarks = new MutableLiveData<>(null);
    }

    private String mBookId;
    final MutableLiveData<String> mErrMsg;
    final MutableLiveData<TopBookMarkBean.BookMarkType[]> mBookMarks;

    void setBookId(String bookId) { mBookId = bookId; }

    void refreshBookMarkList() {
        IRequest request = NetworkUtils.create(IRequest.class);
        Call<TopBookMarkBean> call = request.getTopBookMarkList(mBookId);
        call.enqueue(new Callback<TopBookMarkBean>() {
            @Override
            public void onResponse(Call<TopBookMarkBean> call, Response<TopBookMarkBean> response) {
                TopBookMarkBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                bean.trim();
                if (bean.Result != 0 || bean.TopBookMarkList == null) {
                    mErrMsg.postValue(bean.Result + " " + bean.Message);
                    return;
                }
                mBookMarks.postValue(bean.TopBookMarkList);
            }

            @Override
            public void onFailure(Call<TopBookMarkBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }
}
