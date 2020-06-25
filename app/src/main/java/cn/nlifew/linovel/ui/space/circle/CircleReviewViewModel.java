package cn.nlifew.linovel.ui.space.circle;

import android.util.Log;

import androidx.annotation.IntDef;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.nlifew.xqdreader.bean.user.UserBean;
import cn.nlifew.xqdreader.bean.user.UserCircleReviewBean;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CircleReviewViewModel extends ViewModel {
    private static final String TAG = "CircleReviewViewModel";

    static final class CircleReviewWrapper {
        static final int TYPE_REFRESH   = 1;
        static final int TYPE_LOAD_MORE = 2;

        @IntDef({TYPE_REFRESH, TYPE_LOAD_MORE})
        @Retention(RetentionPolicy.SOURCE)
        @interface Type {}

        @Type int type;
        UserBean.CircleReviewType review;

        CircleReviewWrapper(int type, UserBean.CircleReviewType data) {
            this.type = type;
            this.review = data;
        }
    }

    public CircleReviewViewModel() {
        mErrMsg = new MutableLiveData<>(null);
        mUserCircleReview = new MutableLiveData<>(null);
    }

    final MutableLiveData<String> mErrMsg;
    final MutableLiveData<CircleReviewWrapper> mUserCircleReview;

    private String mUserId;
    private int mPageIndex;
    private int mItemTotalCount;
    private int mItemCurrentCount;

    void setUserId(String id) { mUserId = id; }


    void refreshCircleReview() {
        mPageIndex = 1;
        mItemTotalCount = mItemCurrentCount = 0;
        loadUserCircleReview(CircleReviewWrapper.TYPE_REFRESH);
    }

    void loadMoreCircleReview() {
        if (mItemCurrentCount >= mItemTotalCount) {
            mErrMsg.postValue("没有更多数据");
            return;
        }
        loadUserCircleReview(CircleReviewWrapper.TYPE_LOAD_MORE);
    }

    private void loadUserCircleReview(int type) {
        IRequest request = NetworkUtils.create(IRequest.class);
        Call<UserCircleReviewBean> call = request.getUserCircleReview(mUserId, mPageIndex, 20);
        call.enqueue(new Callback<UserCircleReviewBean>() {
            @Override
            public void onResponse(Call<UserCircleReviewBean> call, Response<UserCircleReviewBean> response) {
                UserCircleReviewBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                bean.trim();
                if (bean.Result != 0 || bean.Data == null) {
                    mErrMsg.postValue(bean.Result + " " + bean.Message);
                    return;
                }
                if (bean.Data.CircleReviewList == null) {
                    mErrMsg.postValue("null CircleReview list");
                    return;
                }
                mPageIndex ++;
                mItemTotalCount = bean.Data.Count;
                mItemCurrentCount += bean.Data.CircleReviewList.length;
                mUserCircleReview.postValue(new CircleReviewWrapper(type, bean.Data));
            }

            @Override
            public void onFailure(Call<UserCircleReviewBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }
}
