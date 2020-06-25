package cn.nlifew.linovel.fragment.novel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cn.nlifew.xqdreader.bean.CommentBean;
import cn.nlifew.xqdreader.bean.NovelBean;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NovelViewModel extends ViewModel {
    private static final String TAG = "NovelViewModel";

    private final MutableLiveData<NovelBean.DataType> mNovelData;
    private final MutableLiveData<String> mErrorMsg;
    private final MutableLiveData<CommentBean.DataType> mCommentData;

    private String mNovelId;
    private String mOutOfBook;

    public NovelViewModel() {
        mNovelData = new MutableLiveData<>(null);
        mErrorMsg = new MutableLiveData<>(null);
        mCommentData = new MutableLiveData<>(null);
    }

    void setNovelInfo(String novelId, String outOfBook) {
        mNovelId = novelId;
        mOutOfBook = outOfBook;
    }

    void startLoadNovelInfo() {
        if (mNovelId == null || mOutOfBook == null) {
            mErrorMsg.setValue("null novel info");
            return;
        }

        IRequest request = NetworkUtils.create(IRequest.class);
        Call<NovelBean> call = request.getNovelById(mNovelId, 0, mOutOfBook);
        call.enqueue(new Callback<NovelBean>() {
            @Override
            public void onResponse(Call<NovelBean> call, Response<NovelBean> response) {
                NovelBean novel = response.body();
                if (novel.Result != 0) {
                    onFailure(call, new RuntimeException(novel.Message));
                } else {
                    novel.trim();
                    mNovelData.postValue(novel.Data);
                }
            }

            @Override
            public void onFailure(Call<NovelBean> call, Throwable t) {
                mErrorMsg.postValue(t.toString());
            }
        });
    }


    private int mPage = 1;
    private long mCircleId;
    private int mTotalComments;
    private int mCurrentComments;

    void startLoadComments() {
        /*
         * 这里的数据存在脏读写的问题
         * 我们需要从服务端获取数据，然后更新上面的 mPage 等信息，
         * 如果下面的回调发生在子线程，我们这里获取到的数据就是不准确的，
         * 很有可能绕过这里的检查，访问到不存在的评论。
         * 但我们这里并没有加 synchronized 关键字进行同步，
         * 原因之一是 View 层必须做好限制，保证在这一次请求完成之前(不管成功还是失败),
         * 都不能发出下一条访问。
         */
        if (mPage != 1 && mCurrentComments >= mTotalComments) {
            // 说明此时已经浏览到最后一条数据，我们直接返回一个错误
            mErrorMsg.setValue("没有更多数据");
            return;
        }

        IRequest request = NetworkUtils.create(IRequest.class);
        Call<CommentBean> call = request.getNovelComment(
                mCircleId, 6, 0,
                mNovelId, 1, mPage, 20
        );
        call.enqueue(new Callback<CommentBean>() {
            @Override
            public void onResponse(Call<CommentBean> call, Response<CommentBean> response) {
                CommentBean bean = response.body();
                if (bean == null || bean.Result != 0) {
                    onFailure(call, new RuntimeException(bean == null ?
                            "null response" : bean.Message));
                    return;
                }
                bean.trim();

                CommentBean.TopicDataType[] comments = bean.Data.TopicDataList;
                mTotalComments = bean.Data.TotalCount;
                mCurrentComments += comments.length;
                mPage ++;
                if (comments.length != 0) {
                    mCircleId = comments[0].CircleId;
                }
                mCommentData.postValue(bean.Data);
            }

            @Override
            public void onFailure(Call<CommentBean> call, Throwable t) {

            }
        });
    }


    public LiveData<NovelBean.DataType> getNovelData() {
        return mNovelData;
    }

    LiveData<String> getErrorMsg() {
        return mErrorMsg;
    }

    LiveData<CommentBean.DataType> getCommentsData() {
        return mCommentData;
    }
}
