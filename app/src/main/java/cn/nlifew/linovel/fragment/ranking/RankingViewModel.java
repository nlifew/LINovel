package cn.nlifew.linovel.fragment.ranking;

import android.util.Log;

import androidx.annotation.IntDef;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.nlifew.linovel.app.ThisApp;
import cn.nlifew.linovel.settings.Settings;
import cn.nlifew.xqdreader.bean.ranking.RankingGroupBean;
import cn.nlifew.xqdreader.bean.ranking.RankingListBean;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankingViewModel extends ViewModel {
    private static final String TAG = "RankingViewModel";

    static final class RankingListWrapper {
        static final int TYPE_REFRESH   =   1;
        static final int TYPE_LOAD_MORE =   2;

        @IntDef({TYPE_REFRESH, TYPE_LOAD_MORE})
        @Retention(RetentionPolicy.SOURCE)
        @interface Type {}

        @Type int type;
        RankingListBean.DataType[] data;

        RankingListWrapper(@Type int type, RankingListBean.DataType[] data) {
            this.type = type;
            this.data = data;
        }
    }

    public RankingViewModel() {
        mErrMsg = new MutableLiveData<>(null);
        mRankingGroup = new MutableLiveData<>(null);
        mRankingList = new MutableLiveData<>(null);
    }

    final MutableLiveData<String> mErrMsg;
    final MutableLiveData<RankingListWrapper> mRankingList;
    final MutableLiveData<RankingGroupBean.DataType[]> mRankingGroup;

    private int/* mSiteId, */mCategoryId;
    private int mTopId, mPageIndex;

    void setRankingParam(/*int siteId, */int categoryId) {
//        mSiteId = siteId;
        mCategoryId = categoryId;
    }

    void setRankingTopId(int topId) {
        mTopId = topId;
        mPageIndex = 1;
    }

    void refreshRankingGroup() {
        Settings settings = Settings.getInstance(ThisApp.currentApplication);
        int siteId = settings.getSiteType() == 0 ? 12 : 11;

        mPageIndex = 1;

        IRequest request = NetworkUtils.create(IRequest.class);
        Call<RankingGroupBean> call = request.getRankingGroup(siteId, mCategoryId);
        call.enqueue(new Callback<RankingGroupBean>() {
            @Override
            public void onResponse(Call<RankingGroupBean> call, Response<RankingGroupBean> response) {
                RankingGroupBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }

                bean.trim();
                if (bean.Result != 0 || bean.Data == null) {
                    mErrMsg.postValue(bean.Result + " " + bean.Message);
                    return;
                }

                mRankingGroup.postValue(bean.Data);
            }

            @Override
            public void onFailure(Call<RankingGroupBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    private void loadRankingList(final int type) {
        Settings settings = Settings.getInstance(ThisApp.currentApplication);
        int siteId = settings.getSiteType() == 0 ? 12 : 11;

        IRequest request = NetworkUtils.create(IRequest.class);
        Call<RankingListBean> call = request.getRankingList(
                siteId, mTopId, mCategoryId, mPageIndex, 20
        );
        call.enqueue(new Callback<RankingListBean>() {
            @Override
            public void onResponse(Call<RankingListBean> call, Response<RankingListBean> response) {
                RankingListBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                bean.trim();
                if (bean.Result != 0 || bean.Data == null) {
                    mErrMsg.postValue(bean.Result + " " + bean.Message);
                    return;
                }
                mPageIndex ++;
                mRankingList.postValue(new RankingListWrapper(type, bean.Data));
            }

            @Override
            public void onFailure(Call<RankingListBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    void refreshRankingList() {
        loadRankingList(RankingListWrapper.TYPE_REFRESH);
    }

    void loadMoreRankingList() {
        loadRankingList(RankingListWrapper.TYPE_LOAD_MORE);
    }
}
