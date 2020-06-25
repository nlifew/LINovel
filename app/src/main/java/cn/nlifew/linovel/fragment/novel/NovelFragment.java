package cn.nlifew.linovel.fragment.novel;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import cn.nlifew.linovel.fragment.BaseFragment;
import cn.nlifew.linovel.utils.ToastUtils;
import cn.nlifew.linovel.widget.LoadMoreRecyclerView;
import cn.nlifew.xqdreader.bean.CommentBean;
import cn.nlifew.xqdreader.bean.NovelBean;

public class NovelFragment extends BaseFragment implements
        LoadMoreRecyclerView.OnLoadMoreListener {

    private static final String TAG = "NovelFragment";

    private static final String ARGS_NOVEL_ID = "ARGS_NOVEL_ID";
    private static final String ARGS_OUT_OF_BOOK = "ARGS_OUT_OF_BOOK";

    public static NovelFragment newInstance(String novelId, String outOfBook) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_NOVEL_ID, novelId);
        bundle.putString(ARGS_OUT_OF_BOOK, outOfBook);

        NovelFragment fragment = new NovelFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            throw new UnsupportedOperationException("call newInstance() to make a instance");
        }
        String novelId = bundle.getString(ARGS_NOVEL_ID);
        String outOfBook = bundle.getString(ARGS_OUT_OF_BOOK);

        mViewModel = new ViewModelProvider(this)
                .get(NovelViewModel.class);
        mViewModel.setNovelInfo(novelId, outOfBook);
    }


    private NovelViewModel mViewModel;
    private RecyclerAdapterImpl mFragmentAdapter;
    private LoadMoreRecyclerView mLoadMoreRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context c = getContext();

        LoadMoreRecyclerView view = new LoadMoreRecyclerView(c);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        view.setLayoutParams(lp);
        view.setLayoutManager(new LinearLayoutManager(c));
        view.addItemDecoration(new DividerItemDecoration(c, DividerItemDecoration.VERTICAL));
        view.setOnLoadMoreListener(this);

        mFragmentAdapter = new RecyclerAdapterImpl(this);
        view.setAdapter(mFragmentAdapter);

        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel.getErrorMsg().observe(owner, this::onErrorMsgChanged);
        mViewModel.getNovelData().observe(owner, this::onNovelDataChanged);
        mViewModel.getCommentsData().observe(owner, this::onCommentsDataChanged);

        return mLoadMoreRecyclerView = view;
    }

    @Override
    public void onLoadMore() {
        Log.d(TAG, "onLoadMore: start");
        mViewModel.startLoadComments();
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        mViewModel.startLoadNovelInfo();
    }

    private void onErrorMsgChanged(String msg) {
        if (msg != null) {
            Log.w(TAG, "onErrorMsgChanged: " + msg);
            ToastUtils.getInstance(getContext()).show(msg);
            mLoadMoreRecyclerView.setLoadMoreWorking(false);
        }
    }

    private void onNovelDataChanged(NovelBean.DataType data) {
        if (data != null) {
            mFragmentAdapter.updateNovelData(data);
            mViewModel.startLoadComments();
        }
    }

    private void onCommentsDataChanged(CommentBean.DataType data) {
        if (data != null) {
            mFragmentAdapter.appendCommentsData(data);
            mLoadMoreRecyclerView.setLoadMoreWorking(false);
        }
    }


    public NovelViewModel getViewModel() {
        return mViewModel;
    }
}
