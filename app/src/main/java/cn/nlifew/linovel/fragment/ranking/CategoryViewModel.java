package cn.nlifew.linovel.fragment.ranking;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;

import cn.nlifew.linovel.app.ThisApp;
import cn.nlifew.linovel.settings.Settings;
import cn.nlifew.xqdreader.bean.ranking.RankingCategoryBean;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryViewModel extends ViewModel {
    private static final String TAG = "CategoryViewModel";

    public CategoryViewModel() {
        mErrMsg = new MutableLiveData<>(null);
        mRankingCategory = new MutableLiveData<>(null);
    }

//    private int mSiteId;
    final MutableLiveData<String> mErrMsg;
    final MutableLiveData<RankingCategoryBean.CategoryType[]> mRankingCategory;

//    void setSiteId(int siteId) {
//        mSiteId = siteId;
//    }

    void loadRankingCategory() {
        IRequest request = NetworkUtils.create(IRequest.class);
        Call<RankingCategoryBean> call = request.getRankingCategory();
        call.enqueue(new Callback<RankingCategoryBean>() {
            @Override
            public void onResponse(Call<RankingCategoryBean> call, Response<RankingCategoryBean> response) {
                RankingCategoryBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }

                bean.trim();
                if (bean.Result != 0 || bean.Data == null) {
                    mErrMsg.postValue(bean.Result + " " + bean.Message);
                    return;
                }

                // 遍历直到找到符合的 siteId
                Settings settings = Settings.getInstance(ThisApp.currentApplication);
                int siteId = settings.getSiteType() == 0 ? 12 : 11;
                for (RankingCategoryBean.DataType data : bean.Data) {
                    if (data != null && data.SiteId == siteId) {
                        // 好耶 (
                        mRankingCategory.postValue(data.CategoryList);
                        return;
                    }
                }
                // 没命中?
                mErrMsg.postValue("target siteId " + siteId + " NOT found");
            }

            @Override
            public void onFailure(Call<RankingCategoryBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }
}
