package cn.nlifew.linovel.ui.novel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;

import cn.nlifew.xqdreader.bean.BookShelfBean;
import cn.nlifew.xqdreader.database.CommonDatabase;
import cn.nlifew.xqdreader.entity.BookShelf;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import cn.nlifew.xqdreader.utils.ShelfUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NovelViewModel extends ViewModel {
    private static final String TAG = "NovelViewModel";

    public NovelViewModel() {
        mErrMsg = new MutableLiveData<>(null);
        mSubscribeStatus = new MutableLiveData<>(false);
    }

    String mNovelId;
    final MutableLiveData<String> mErrMsg;
    final MutableLiveData<Boolean> mSubscribeStatus;

    void refreshBookShelf(boolean subscribe) {

        int bookId = Integer.parseInt(mNovelId, 10);
        String text = subscribe ? ShelfUtils.addBook(bookId) : ShelfUtils.removeBook(bookId);

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
                if (bean.Result != 0 || bean.Data == null || bean.Data.ServerCase == null) {
                    mErrMsg.postValue(bean.Result + " " + bean.Message);
                    return;
                }
                try {
                    CommonDatabase db = CommonDatabase.getInstance();
                    BookShelf.Helper helper = db.getBookShiftHelper();

                    if (bean.Data.ServerCase.BookInfo != null) {
                        helper.saveAll(bean.Data.ServerCase.BookInfo);
                    }
                    int n = bean.Data.ServerCase.DelBookId == null ? 0
                            : bean.Data.ServerCase.DelBookId.length;
                    for (int i = 0; i < n; i++) {
                        helper.deleteById(bean.Data.ServerCase.DelBookId[i]);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onResponse: ", e);
                    mErrMsg.postValue("同步本地书架失败 " + e.toString());
                    return;
                }
                mSubscribeStatus.postValue(subscribe);
            }

            @Override
            public void onFailure(Call<BookShelfBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }
}
