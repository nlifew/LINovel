package cn.nlifew.linovel.fragment.category2;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.Objects;

import cn.nlifew.linovel.app.ThisApp;
import cn.nlifew.linovel.fragment.category.CategoryViewModel;
import cn.nlifew.linovel.settings.Settings;
import cn.nlifew.xqdreader.bean.category.CategoryBean;
import cn.nlifew.xqdreader.bean.category.CategoryBookBean;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import cn.nlifew.xqdreader.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryViewModel2 extends ViewModel {
    private static final String TAG = "CategoryViewModel2";

    static final class CategoryBookWrapper {

        static final int TYPE_REFRESH   = 1;
        static final int TYPE_LOAD_MORE = 2;

        @IntDef({TYPE_REFRESH, TYPE_LOAD_MORE})
        @Retention(RetentionPolicy.SOURCE)
        @interface Type {}

        @Type
        int type;
        CategoryBookBean.BookType[] books;

        CategoryBookWrapper(int type, CategoryBookBean.BookType[] books) {
            this.type = type;
            this.books = books;
        }
    }

    public CategoryViewModel2() {
        Settings settings = Settings.getInstance(ThisApp.currentApplication);
        mSiteId = settings.getSiteType() == 0 ? "12" : "11";
    }

    final MutableLiveData<String> mErrMsg = new MutableLiveData<>(null);
    final MutableLiveData<CategoryBean.DataType> mHeader = new MutableLiveData<>(null);
    final MutableLiveData<CategoryBookWrapper> mBookList = new MutableLiveData<>(null);

    private int mPageIndex;
    private int mItemTotalCount;
    private int mItemCurrentCount;

    private String mSiteId, mOrder;
    private Map<String, String> mFilterMap = new ArrayMap<>(6);

    boolean updateQueryParam(String key, String value) {
        switch (key) {
            case "site": mSiteId = value; break;
            case "order": mOrder = value; break;
            default: mFilterMap.put(key, value);
        }
        boolean ok = mSiteId != null && mOrder != null;
        if (ok) {
            if ("site".equals(key)) {
                refreshHeader();
            } else {
                refreshBooks();
            }
        }
        return ok;
    }

    void refreshHeader() {
        mOrder = null;
        mFilterMap.clear();
        mPageIndex = 1;
        mItemTotalCount = mItemCurrentCount = 0;

        IRequest request = NetworkUtils.create(IRequest.class);
        Call<CategoryBean> call = request.getCategory(mSiteId);
        call.enqueue(new Callback<CategoryBean>() {
            @Override
            public void onResponse(Call<CategoryBean> call, Response<CategoryBean> response) {
                CategoryBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                bean.trim();
                if (bean.Result != 0 || bean.Data == null) {
                    mErrMsg.postValue(bean.Result + " " + bean.Message);
                    return;
                }
                mOrder = Integer.toString(bean.Data.Orders[0].Id);
                CategoryBookWrapper wrapper = null;
                try {
                    Call<CategoryBookBean> c = newBookListCall();
                    CategoryBookBean resp = parseBookListResp(c.execute());
                    wrapper = new CategoryBookWrapper(
                            CategoryBookWrapper.TYPE_REFRESH,
                            resp.Data.Books
                    );
                    mItemTotalCount = resp.Data.TotalCount;
                } catch (Exception e) {
                    Log.e(TAG, "onResponse: ", e);
                    mErrMsg.postValue(e.toString());
                }
                mHeader.postValue(bean.Data);
                mBookList.postValue(wrapper);
            }

            @Override
            public void onFailure(Call<CategoryBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    private void refreshBooks() {
        mPageIndex = 1;
        mItemTotalCount = mItemCurrentCount = 0;
        loadBooks(CategoryBookWrapper.TYPE_REFRESH);
    }

    void loadMoreBooks() {
        if (mItemCurrentCount >= mItemTotalCount) {
            mErrMsg.postValue("没有更多数据");
            return;
        }
        loadBooks(CategoryBookWrapper.TYPE_LOAD_MORE);
    }

    private void loadBooks(int type) {
        Call<CategoryBookBean> call = newBookListCall();
        call.enqueue(new Callback<CategoryBookBean>() {
            @Override
            public void onResponse(Call<CategoryBookBean> call, Response<CategoryBookBean> response) {
                CategoryBookBean bean;
                try {
                    bean = parseBookListResp(response);
                } catch (Exception e) {
                    onFailure(call, e);
                    return;
                }
                if (bean.Data.Books == null) {
                    mErrMsg.postValue("null book list");
                    return;
                }
                mPageIndex ++;
                mItemTotalCount = bean.Data.TotalCount;
                mItemCurrentCount += bean.Data.Books.length;
                mBookList.postValue(new CategoryBookWrapper(type, bean.Data.Books));
            }

            @Override
            public void onFailure(Call<CategoryBookBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call, t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    private Call<CategoryBookBean> newBookListCall() {
        StringBuilder sb = Utils.obtainStringBuilder(64);
        for (Map.Entry<String, String> entry : mFilterMap.entrySet()) {
            sb.append(entry.getKey()).append('=').append(entry.getValue()).append(',');
        }
        int n = sb.length();
        if (n != 0) sb.setLength(n - 1);
        final String filters = sb.toString();
        Utils.recycle(sb);

        IRequest request = NetworkUtils.create(IRequest.class);
        return request.getCategoryBooks(
                mPageIndex, 20, mSiteId, filters, mOrder
        );
    }

    private CategoryBookBean parseBookListResp(Response<CategoryBookBean> resp)
            throws IOException {
        CategoryBookBean bean;
        if (! resp.isSuccessful() || (bean = resp.body()) == null) {
            throw new IOException(resp.code() + " " + resp.message());
        }
        bean.trim();
        if (bean.Result != 0 || bean.Data == null) {
            throw new IOException(bean.Result + " " + bean.Message);
        }
        return bean;
    }
}
