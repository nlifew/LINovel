package cn.nlifew.linovel.fragment.square;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;

import cn.nlifew.linovel.app.ThisApp;
import cn.nlifew.linovel.settings.Settings;
import cn.nlifew.xqdreader.bean.SquareBean;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SquareViewModel extends ViewModel {
    private static final String TAG = "SquareViewModel";

    final MutableLiveData<String> mErrorMsg;
    final MutableLiveData<SquareBean.DataType> mData;
    private String mSquareId;

    public SquareViewModel() {
        mData = new MutableLiveData<>(null);
        mErrorMsg = new MutableLiveData<>(null);
    }

    void setSquareId(String id) {
        mSquareId = id;
    }

    void startLoadData() {
        if (mSquareId == null) {
            mErrorMsg.setValue("null Square Id");
            return;
        }
        Settings settings = Settings.getInstance(ThisApp.currentApplication);
        int siteType = settings.getSiteType() == 0 ? 1 : 0;

        IRequest request = NetworkUtils.create(IRequest.class);
        Call<SquareBean> call = request.getSquarePage(mSquareId, siteType);
        call.enqueue(new Callback<SquareBean>() {
            @Override
            public void onResponse(Call<SquareBean> call, Response<SquareBean> response) {
                SquareBean bean;
                if (! response.isSuccessful() ||(bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }

                bean.trim();
                if (bean.Result != 0 || bean.Data == null) {
                    mErrorMsg.postValue(bean.Result + " " + bean.Message);
                    return;
                }
                mData.postValue(bean.Data);
            }

            @Override
            public void onFailure(Call<SquareBean> call, Throwable t) {
                mErrorMsg.postValue(t.toString());
            }
        });
    }

}
