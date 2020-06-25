package cn.nlifew.linovel.ui.space.chapter;

import android.util.Log;

import androidx.annotation.IntDef;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.nlifew.xqdreader.bean.user.UserBean;
import cn.nlifew.xqdreader.bean.user.UserChapterReviewBean;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChapterReviewViewModel extends ViewModel {
    private static final String TAG = "ChapterReviewViewModel";

    static final class ChapterReviewWrapper {

        static final int TYPE_REFRESH   =   1;
        static final int TYPE_LOAD_MORE =   2;

        @IntDef({TYPE_REFRESH, TYPE_LOAD_MORE})
        @Retention(RetentionPolicy.SOURCE)
        @interface Type {  }

        @Type int type;
        UserBean.ChapterReviewType review;

        ChapterReviewWrapper(int type, UserBean.ChapterReviewType review) {
            this.type = type;
            this.review = review;
        }
    }

    public ChapterReviewViewModel() {
        mErrMsg = new MutableLiveData<>(null);
        mUserChapterReview = new MutableLiveData<>(null);
    }

    final MutableLiveData<String> mErrMsg;
    final MutableLiveData<ChapterReviewWrapper> mUserChapterReview;

    private String mUserId;
    private int mPageIndex;
    private int mItemTotalCount, mItemCurrentCount;

    void setUserId(String id) { mUserId = id; }

    void refreshChapterReview() {
        mPageIndex = 1;
        mItemTotalCount = 0;
        mItemCurrentCount = 0;
        loadChapterReview(ChapterReviewWrapper.TYPE_REFRESH);
    }

    void loadMoreChapterReview() {
        if (mItemCurrentCount >= mItemTotalCount) {
            mErrMsg.postValue("没有更多数据");
            return;
        }
        loadChapterReview(ChapterReviewWrapper.TYPE_LOAD_MORE);
    }

    private void loadChapterReview(int type) {
        IRequest request = NetworkUtils.create(IRequest.class);
        Call<UserChapterReviewBean> call = request.getUserChapterReview(mUserId, mPageIndex);
        call.enqueue(new Callback<UserChapterReviewBean>() {
            @Override
            public void onResponse(Call<UserChapterReviewBean> call, Response<UserChapterReviewBean> response) {
                UserChapterReviewBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                bean.trim();
                if (bean.Result != 0 || bean.Data == null) {
                    mErrMsg.postValue(bean.Result + " " + bean.Message);
                    return;
                }
                if (bean.Data.ChapterReviewList == null) {
                    mErrMsg.postValue("null chapter review list");
                    return;
                }
                mPageIndex ++;
                mItemTotalCount = bean.Data.Count;
                mItemCurrentCount += bean.Data.ChapterReviewList.length;
                mUserChapterReview.postValue(new ChapterReviewWrapper(type, bean.Data));
            }

            @Override
            public void onFailure(Call<UserChapterReviewBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }
}
