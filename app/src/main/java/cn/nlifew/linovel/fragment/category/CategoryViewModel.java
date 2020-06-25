package cn.nlifew.linovel.fragment.category;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import cn.nlifew.xqdreader.bean.category.CategoryBean;
import cn.nlifew.xqdreader.bean.category.CategoryBookBean;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import cn.nlifew.xqdreader.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryViewModel extends ViewModel {
    private static final String TAG = "CategoryViewModel";

    static final class CategoryBookWrapper {

        static final int TYPE_REFRESH   = 1;
        static final int TYPE_LOAD_MORE = 2;

        @IntDef({TYPE_REFRESH, TYPE_LOAD_MORE})
        @Retention(RetentionPolicy.SOURCE)
        @interface Type {}

        @Type int type;
        CategoryBookBean.BookType[] books;

        CategoryBookWrapper(int type, CategoryBookBean.BookType[] books) {
            this.type = type;
            this.books = books;
        }
    }

    public CategoryViewModel() {
        mErrMsg = new MutableLiveData<>(null);
        mBooks = new MutableLiveData<>(null);
        mCategory = new MutableLiveData<>(null);
    }

    final MutableLiveData<String> mErrMsg;
    final MutableLiveData<CategoryBookWrapper> mBooks;
    final MutableLiveData<CategoryBean.DataType> mCategory;

    private int mPageIndex;
    private int mItemTotalCount;
    private int mItemCurrentCount;

    private String mSiteId, mOrder;
    private Map<String, String> mFilterMap = new ArrayMap<>(6);

    void setSiteId(String siteId) {
        mSiteId = siteId;
    }

    void refreshCategory() {
        mOrder = null;
        mFilterMap.clear();

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
                mCategory.postValue(bean.Data);
            }

            @Override
            public void onFailure(Call<CategoryBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    boolean updateQueryParam(String key, String value) {
        if ("site".equals(key)) {
            mSiteId = value;
        }
        else if ("order".equals(key)) {
            mOrder = value;
        }
        else {
            mFilterMap.put(key, value);
        }

        boolean complete = mSiteId != null && mOrder != null;
        if (complete) {
            if ("site".equals(key)) {
                refreshCategory();
            }
            else {
                refreshBooks();
            }
        }
        return complete;
    }

    private void refreshBooks() {
        mPageIndex = 1;
        mItemCurrentCount = mItemTotalCount = 0;
        loadBooks(CategoryBookWrapper.TYPE_REFRESH);
    }

    void loadMoreBooks() {
        if (mItemCurrentCount >= mItemTotalCount) {
            mErrMsg.postValue("没有更多数据");
            return;
        }
        loadBooks(CategoryBookWrapper.TYPE_LOAD_MORE);
    }

    private void loadBooks(final int type) {
        StringBuilder sb = Utils.obtainStringBuilder(64);
        for (Map.Entry<String, String> entry : mFilterMap.entrySet()) {
            sb.append(entry.getKey()).append('=').append(entry.getValue()).append(',');
        }
        int n = sb.length();
        if (n != 0) sb.setLength(n - 1);
        final String filters = sb.toString();
        Utils.recycle(sb);


        IRequest request = NetworkUtils.create(IRequest.class);
        Call<CategoryBookBean> call = request.getCategoryBooks(
                mPageIndex, 20, mSiteId, filters, mOrder
        );
        call.enqueue(new Callback<CategoryBookBean>() {
            @Override
            public void onResponse(Call<CategoryBookBean> call, Response<CategoryBookBean> response) {
                CategoryBookBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                bean.trim();
                if (bean.Result != 0 || bean.Data == null) {
                    mErrMsg.postValue(bean.Result + " " + bean.Message);
                    return;
                }
                if (bean.Data.Books == null) {
                    mErrMsg.postValue("null Book List");
                    return;
                }

                mPageIndex ++;
                mItemTotalCount = bean.Data.TotalCount;
                mItemCurrentCount += bean.Data.Books.length;
                mBooks.postValue(new CategoryBookWrapper(type, bean.Data.Books));
            }

            @Override
            public void onFailure(Call<CategoryBookBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }
}
