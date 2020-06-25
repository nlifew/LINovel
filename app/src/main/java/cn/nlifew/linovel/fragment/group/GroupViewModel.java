package cn.nlifew.linovel.fragment.group;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cn.nlifew.xqdreader.bean.GroupBean;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupViewModel extends ViewModel {
    private static final String TAG = "CategoryViewModel";

    final MutableLiveData<String> mErrorMsg;
    final MutableLiveData<GroupBean.DataType> mLoadMoreGroupDatas;
    final MutableLiveData<GroupBean.DataType> mRefreshGroupData;

    public GroupViewModel() {
        mErrorMsg = new MutableLiveData<>(null);
        mRefreshGroupData = new MutableLiveData<>(null);
        mLoadMoreGroupDatas = new MutableLiveData<>(null);
    }

    private int mTotalItemCount;
    private int mCurrentItemCount;
    private int mPageIndex;
    private String mNovelGroupId;

    void setGroupId(String id) {
        mNovelGroupId = id;
    }

    void refreshGroupData() {
        if (mNovelGroupId == null) {
            mErrorMsg.setValue("null Group Id");
            return;
        }
        mPageIndex = 2;
        mTotalItemCount = mCurrentItemCount = 0;

        IRequest request = NetworkUtils.create(IRequest.class);
        Call<GroupBean> call = request.getGroupById(mNovelGroupId);
        call.enqueue(new Callback<GroupBean>() {
            @Override
            public void onResponse(Call<GroupBean> call, Response<GroupBean> response) {
                GroupBean bean = response.body();
                if (bean == null || bean.Result != 0) {
                    onFailure(call, new RuntimeException(bean == null ?
                            "null response" : bean.Message));
                    return;
                }
                bean.trim();
                mTotalItemCount = bean.Data.Data.TotalCount;
                mCurrentItemCount = bean.Data.Data.Items.length;
                mRefreshGroupData.postValue(bean.Data);
            }

            @Override
            public void onFailure(Call<GroupBean> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                mErrorMsg.postValue(t.toString());
            }
        });
    }

    void loadMoreGroupData() {
        if (mNovelGroupId == null) {
            throw new UnsupportedOperationException("I need a ID");
        }
        if (mTotalItemCount != 0 && mCurrentItemCount >= mTotalItemCount) {
            mErrorMsg.setValue("没有更多数据");
            return;
        }

        IRequest request = NetworkUtils.create(IRequest.class);
        Call<GroupBean> call = request.getGroupById(mNovelGroupId, mPageIndex);
        call.enqueue(new Callback<GroupBean>() {
            @Override
            public void onResponse(Call<GroupBean> call, Response<GroupBean> response) {
                GroupBean bean = response.body();
                if (bean == null || bean.Result != 0) {
                    onFailure(call, new RuntimeException(bean == null ?
                            "null response" : bean.Message));
                    return;
                }
                bean.trim();
                mTotalItemCount = bean.Data.Data.TotalCount;
                mCurrentItemCount += bean.Data.Data.Items.length;
                mPageIndex ++;
                mLoadMoreGroupDatas.postValue(bean.Data);
            }

            @Override
            public void onFailure(Call<GroupBean> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                mErrorMsg.postValue(t.toString());
            }
        });

    }
}
