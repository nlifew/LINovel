package cn.nlifew.linovel.fragment.bookmark;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import cn.nlifew.linovel.fragment.loadmore.BaseLoadMoreFragment;
import cn.nlifew.linovel.utils.DisplayUtils;
import cn.nlifew.linovel.utils.ToastUtils;
import cn.nlifew.linovel.widget.PaddingDividerDecoration;
import cn.nlifew.xqdreader.bean.TopBookMarkBean;

public class BookMarkFragment extends BaseLoadMoreFragment {
    private static final String TAG = "BookMarkFragment";

    private static final String ARGS_NOVEL_ID = "ARGS_NOVEL_ID";

    public static BookMarkFragment newInstance(String bookId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_NOVEL_ID, bookId);

        BookMarkFragment fragment = new BookMarkFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            throw new UnsupportedOperationException("call newInstance() to make a Fragment");
        }
        mViewModel = new ViewModelProvider(this).get(BookMarkViewModel.class);
        mViewModel.setBookId(bundle.getString(ARGS_NOVEL_ID));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel.mErrMsg.observe(owner, this::onErrMsgChanged);
        mViewModel.mBookMarks.observe(owner, this::onBookMarksChanged);

        PaddingDividerDecoration decoration = new PaddingDividerDecoration(
                getContext(), PaddingDividerDecoration.VERTICAL
        );
        decoration.setPadding(DisplayUtils.dp2px(20), 0,
                DisplayUtils.dp2px(15), 0);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setLoadMoreEnabled(false);    // 禁用上滑加载
        return view;
    }

    private BookMarkViewModel mViewModel;
    private RecyclerAdapterImpl mRecyclerAdapter;

    @Override
    protected RecyclerView.Adapter newRecyclerAdapter() {
        return mRecyclerAdapter = new RecyclerAdapterImpl(this);
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh: start");
        mViewModel.refreshBookMarkList();
    }

    private void onErrMsgChanged(String s) {
        if (s != null) {
            ToastUtils.getInstance(getContext()).show(s);
            setLoading(false);
        }
    }

    private void onBookMarksChanged(TopBookMarkBean.BookMarkType[] data) {
        if (data != null) {
            mRecyclerAdapter.updateDataSet(data);
            setLoading(false);
        }
    }
}
