package cn.nlifew.linovel.ui.reading;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import cn.nlifew.linovel.app.ThisApp;
import cn.nlifew.linovel.utils.CUtils;
import cn.nlifew.linovel.utils.ToastUtils;
import cn.nlifew.xqdreader.bean.AddBookMarkBean;
import cn.nlifew.xqdreader.bean.ChapterListBean;
import cn.nlifew.xqdreader.bean.TopBookMarkBean;
import cn.nlifew.xqdreader.entity.ReadingRecord;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadingViewModel extends ViewModel {
    private static final String TAG = "ReadingViewModel";

    static final class ChapterWrapper {
        static final int TYPE_NEXT_CHAPTER = 1;
        static final int TYPE_LAST_CHAPTER = -1;

        @IntDef({TYPE_NEXT_CHAPTER, TYPE_LAST_CHAPTER})
        @Retention(RetentionPolicy.SOURCE)
        @interface Type {  }

        String content = "";
        String title = "";
        int position;
        int chapterId;
        final @Type int type;

        ChapterWrapper(int type) {
            this.type = type;
        }
    }

    public ReadingViewModel() {
        mErrMsg = new MutableLiveData<>(null);
        mChapter = new MutableLiveData<>(null);
    }

    private String mBookId, mChapterId;
    private int mCurrentChapterIndex = -1;
    private ChapterListBean.DataType mChapterList;
    private TopBookMarkBean.BookMarkType[] mBookMarkList;


    final MutableLiveData<String> mErrMsg;
    final MutableLiveData<ChapterWrapper> mChapter;

    void setBookId(String bookId, String chapterId) {
        mBookId = bookId;
        mChapterId = chapterId;
    }

    private void performLoadCurrentChapter() {
        if (mChapterList == null || (mBookMarkList == null && mChapterId == null)) {
            return;
        }

        ChapterWrapper wrapper = new ChapterWrapper(ChapterWrapper.TYPE_NEXT_CHAPTER);
        wrapper.chapterId = mChapterList.FirstChapterId;

        if (mChapterId != null) {
            // 已经指定了要阅读哪一章节
            wrapper.chapterId = Integer.parseInt(mChapterId);
        }
        else if (mBookMarkList.length > 0) {
            // 从书签中加载上次的阅读进度
            wrapper.chapterId = mBookMarkList[0].ChapterId;
            wrapper.title = mBookMarkList[0].ChapterName;
            wrapper.position = mBookMarkList[0].Position;
        }

        // 遍历所有的章节信息，直到找到这个章节
        mCurrentChapterIndex = -1;
        for (int i = 0; i < mChapterList.Chapters.length; i++) {
            if (mChapterList.Chapters[i].C == wrapper.chapterId) {
                mCurrentChapterIndex = i;
                wrapper.title = mChapterList.Chapters[i].N;
                break;
            }
        }
        // 没找到这个章节，这就很要命
        if (mCurrentChapterIndex == -1) {
            mErrMsg.postValue("Missing ChapterId " + wrapper.chapterId + " in ChapterList");
            return;
        }

        // 到现在为止还算正常，那就尝试加载这个章节的内容
        try {
            wrapper.content = Helper.parseChapterText(mBookId, wrapper.chapterId);
        } catch (Exception e) {
            Log.e(TAG, "performLoadCurrentChapter: ", e);
            mErrMsg.postValue(e.toString());
            return;
        }
        // 将 UTF-8 格式的字节偏移转化为字符偏移
        if (wrapper.position != 0) {
            wrapper.position = CUtils.getStringOffset(wrapper.content, wrapper.position);
        }

        // 如果到这里说明一切正常
        mChapter.postValue(wrapper);
    }

    private void loadBookChapterList() {
        IRequest request = NetworkUtils.create(IRequest.class);
        Call<ChapterListBean> call = request.getChapterList(mBookId,
                0, 0, "");
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
                    mErrMsg.postValue("null ChapterList");
                    return;
                }
                synchronized (ReadingViewModel.this) {
                    mChapterList = bean.Data;
                    performLoadCurrentChapter();
                }
            }

            @Override
            public void onFailure(Call<ChapterListBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    private void loadTopBookMarks() {
        IRequest request = NetworkUtils.create(IRequest.class);
        Call<TopBookMarkBean> call = request.getTopBookMarkList(mBookId);
        call.enqueue(new Callback<TopBookMarkBean>() {
            @Override
            public void onResponse(Call<TopBookMarkBean> call, Response<TopBookMarkBean> response) {
                TopBookMarkBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                bean.trim();
                if (bean.Result != 0 || bean.TopBookMarkList == null) {
                    mErrMsg.postValue(bean.Result + " " + bean.Message);
                    return;
                }
                synchronized (ReadingViewModel.this) {
                    mBookMarkList = bean.TopBookMarkList;
                    performLoadCurrentChapter();
                }
            }

            @Override
            public void onFailure(Call<TopBookMarkBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    void loadCurrentChapter() {
        loadBookChapterList();
        if (mChapterId == null) {
            loadTopBookMarks();
        }
    }

    private void loadChapter(int type) {
        final ChapterListBean.ChapterType t = mChapterList.Chapters[mCurrentChapterIndex + type];
        IRequest request = NetworkUtils.create(IRequest.class);
        Call<ResponseBody> call = request.getChapterContent(mBookId, t.C, 0);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody body;
                if (! response.isSuccessful() || (body = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }

                String text;

                try {
                    InputStream is = body.byteStream();
                    text = Helper.parseChapterText(is, mBookId, t.C);
                    is.close();
                } catch (Exception e) {
                    Log.e(TAG, "onResponse: ", e);
                    mErrMsg.postValue(e.toString());
                    return;
                } finally {
                    body.close();
                }

                mCurrentChapterIndex += type;
                ChapterWrapper chapter = new ChapterWrapper(type);
                chapter.title = t.N;
                chapter.content = text;
                chapter.chapterId = t.C;
                mChapter.postValue(chapter);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + call.request(), t);
                mErrMsg.postValue(t.toString());
            }
        });
    }

    void loadNextChapter() {
        if (mCurrentChapterIndex >= mChapterList.Chapters.length) {
            mErrMsg.postValue("没有更多章节");
            return;
        }
        loadChapter(ChapterWrapper.TYPE_NEXT_CHAPTER);
    }

    void loadLastChapter() {
        if (mCurrentChapterIndex <= 0) {
            mErrMsg.postValue("没有更多章节");
            return;
        }
        loadChapter(ChapterWrapper.TYPE_LAST_CHAPTER);
    }

    void saveToBookMark(int position) {
        ChapterWrapper wrapper = mChapter.getValue();
        if (wrapper == null) {  // 初始化尚未完成
            return;
        }
        position = CUtils.getStringUTFOffset(wrapper.content, position);
        if (position == -1) position = 0;

        Map<String, String> map = new ArrayMap<>(8);
        map.put("bookid", mBookId);
        map.put("spos", "0");
        map.put("wordsdesc", "");
        map.put("marktype", "1");
        map.put("uploadtime", Long.toString(System.currentTimeMillis()));
        map.put("epos", "0");
        map.put("chaptername", wrapper.title);
        map.put("position", Integer.toString(position));
        map.put("chapterid", Integer.toString(wrapper.chapterId));

        IRequest request = NetworkUtils.create(IRequest.class);
        Call<AddBookMarkBean> call = request.addBookMark(map);
        call.enqueue(new Callback<AddBookMarkBean>() {
            @Override
            public void onResponse(Call<AddBookMarkBean> call, Response<AddBookMarkBean> response) {
                AddBookMarkBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                bean.trim();
                if (bean.Result != 0) {
                    ToastUtils.getInstance(ThisApp.currentApplication)
                            .post(bean.Result + " " + bean.Message);
                }
            }

            @Override
            public void onFailure(Call<AddBookMarkBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                ToastUtils.getInstance(ThisApp.currentApplication).post(t.toString());
            }
        });
    }

    void saveToReadingHistory(int position) {
        ChapterWrapper wrapper = mChapter.getValue();
        if (wrapper == null) {
            return;
        }

        ReadingRecord record = new ReadingRecord();
        record.bookId = Integer.parseInt(mBookId);
        record.bookName = mChapterList.BookName;

        record.chapterId = wrapper.chapterId;
        record.chapterName = wrapper.title;
        record.time = System.currentTimeMillis();
        record.save();
    }
}
