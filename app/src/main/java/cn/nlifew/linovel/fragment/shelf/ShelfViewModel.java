package cn.nlifew.linovel.fragment.shelf;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;

import cn.nlifew.xqdreader.bean.BookShelfBean;
import cn.nlifew.xqdreader.entity.BookShelf;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import cn.nlifew.xqdreader.utils.ShelfUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShelfViewModel extends ViewModel {
    private static final String TAG = "ShelfViewModel";

    public ShelfViewModel() {
        mErrMsg = new MutableLiveData<>(null);
        mBookShelfList = new MutableLiveData<>(null);
    }

    final MutableLiveData<String> mErrMsg;
    final MutableLiveData<BookShelf[]> mBookShelfList;

    void refreshBookShelf() {
        String text = ShelfUtils.empty();

        IRequest request = NetworkUtils.create(IRequest.class);
        Call<BookShelfBean> call = request.refreshBookShelf(text, 0);
        call.enqueue(new Callback<BookShelfBean>() {
            @Override
            public void onResponse(Call<BookShelfBean> call, Response<BookShelfBean> response) {
                BookShelfBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }

                bean.trim();
                if (bean.Result != 0 || bean.Data == null) {
                    mErrMsg.postValue(bean.Result + " " + bean.Message);
                    return;
                }
                if (bean.Data.ServerCase == null || bean.Data.ServerCase.BookInfo == null) {
                    mErrMsg.postValue("null BookInfo");
                    return;
                }
                BookShelf.saveAll(bean.Data.ServerCase.BookInfo);
                mBookShelfList.postValue(bean.Data.ServerCase.BookInfo);
            }

            @Override
            public void onFailure(Call<BookShelfBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    void removeBookShelf(int bookId) {
        String text = ShelfUtils.removeBook(bookId);
        IRequest request = NetworkUtils.create(IRequest.class);
        Call<BookShelfBean> call = request.refreshBookShelf(text, System.currentTimeMillis());
        call.enqueue(new Callback<BookShelfBean>() {
            @Override
            public void onResponse(Call<BookShelfBean> call, Response<BookShelfBean> response) {
                BookShelfBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                bean.trim();
                if (bean.Result != 0 || bean.Data == null) {
                    mErrMsg.postValue(bean.Result + " " + bean.Message);
                }
                // todo 需要根据服务器返回的数据动态修改本地记录
                // 包括新增和移除两部分的，这里先不实现
            }

            @Override
            public void onFailure(Call<BookShelfBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }
}
