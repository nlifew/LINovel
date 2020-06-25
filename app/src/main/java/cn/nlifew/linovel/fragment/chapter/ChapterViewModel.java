package cn.nlifew.linovel.fragment.chapter;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.nlifew.xqdreader.bean.ChapterListBean;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChapterViewModel extends ViewModel {
    private static final String TAG = "ChapterViewModel";

    public ChapterViewModel() {
        mErrMsg = new MutableLiveData<>(null);
        mChapters = new MutableLiveData<>(null);
    }

    final MutableLiveData<String> mErrMsg;
    final MutableLiveData<ChapterListBean.DataType> mChapters;

    private String mNovelId;
    private int mReverseIndex;
    private ChapterListBean.ChapterType[] mOriginChapters;
    private ChapterListBean.ChapterType[] mOriginChaptersReversed;

    void setNovelId(String id) {
        mNovelId = id;
    }

    void refreshChapters() {
        // 考虑到加载一次完整的章节列表需要比较大的内存占用，
        // 需要把第一次的返回结果缓存下来，之后直接返回
        ChapterListBean.DataType data = mChapters.getValue();
        if (data != null) {
            data.Chapters = mOriginChapters;
            mChapters.setValue(data);
            return;
        }

        IRequest request = NetworkUtils.create(IRequest.class);
        Call<ChapterListBean> call = request.getChapterList(mNovelId, 0, 0, "");
        call.enqueue(new Callback<ChapterListBean>() {
            @Override
            public void onResponse(Call<ChapterListBean> call, Response<ChapterListBean> response) {
                ChapterListBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                bean.trim();
                if (bean.Result != 0 || bean.Data == null) {
                    mErrMsg.postValue(bean.Result + " " + bean.Message);
                    return;
                }
                if (bean.Data.Chapters == null) {
                    mErrMsg.postValue("null chapter list");
                    return;
                }
                if (mOriginChapters == null) {
                    mOriginChapters = bean.Data.Chapters;
                }
                mOriginChapters = bean.Data.Chapters;
                mChapters.postValue(bean.Data);
            }

            @Override
            public void onFailure(Call<ChapterListBean> call, Throwable t) {

            }
        });
    }

    void reverseChapters() {
        ChapterListBean.DataType data = mChapters.getValue();
        if (data == null) {
            mErrMsg.postValue("空列表");
            return;
        }
        if ((mReverseIndex++ & 1) == 1) {
            data.Chapters = mOriginChapters;
            mChapters.setValue(data);
            return;
        }
        if (mOriginChaptersReversed != null) {
            data.Chapters = mOriginChaptersReversed;
            mChapters.setValue(data);
            return;
        }
        new ReverseChapters().execute(mOriginChapters);
    }

    void filterChapters(String s) {
        if (mOriginChapters == null) {
            mErrMsg.postValue("空列表");
            return;
        }
        new FilterChapters().execute(s);
    }

    private final class ReverseChapters extends AsyncTask<ChapterListBean.ChapterType[], Void, ChapterListBean.ChapterType[]> {
        @Override
        protected ChapterListBean.ChapterType[] doInBackground(ChapterListBean.ChapterType[]... types) {
            ChapterListBean.ChapterType[] oldArray = types[0], newArray;
            newArray = new ChapterListBean.ChapterType[oldArray.length];

            int i = 0, j = oldArray.length - 1;
            while (i < oldArray.length) {
                newArray[j--] = oldArray[i++];
            }
            return newArray;
        }

        @Override
        protected void onPostExecute(ChapterListBean.ChapterType[] chapterTypes) {
            mOriginChaptersReversed = chapterTypes;
            ChapterListBean.DataType data = mChapters.getValue();
            if (data != null) {
                data.Chapters = chapterTypes;
                mChapters.setValue(data);
            }
        }
    }
    private final class FilterChapters extends AsyncTask<String, Void, ChapterListBean.ChapterType[]> {
        @Override
        protected ChapterListBean.ChapterType[] doInBackground(String... strings) {
            ChapterListBean.ChapterType[] chapters = mOriginChapters;
            List<ChapterListBean.ChapterType> list = new ArrayList<>(chapters.length);
            String s = strings[0];

            for (ChapterListBean.ChapterType chapter : chapters) {
                if (chapter.N.contains(s)) {
                    list.add(chapter);
                }
            }
            chapters = new ChapterListBean.ChapterType[list.size()];
            list.toArray(chapters);
            return chapters;
        }

        @Override
        protected void onPostExecute(ChapterListBean.ChapterType[] chapterTypes) {
            ChapterListBean.DataType data = mChapters.getValue();
            if (data != null) {
                data.Chapters = chapterTypes;
                mChapters.setValue(data);
            }
        }
    }
}
