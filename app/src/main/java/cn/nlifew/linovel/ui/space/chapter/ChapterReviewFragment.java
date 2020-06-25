package cn.nlifew.linovel.ui.space.chapter;

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
import cn.nlifew.linovel.ui.space.BaseSpaceFragment;
import cn.nlifew.linovel.utils.DisplayUtils;
import cn.nlifew.linovel.utils.ToastUtils;
import cn.nlifew.linovel.widget.PaddingDividerDecoration;

import static cn.nlifew.linovel.ui.space.chapter.ChapterReviewViewModel.ChapterReviewWrapper.TYPE_LOAD_MORE;
import static cn.nlifew.linovel.ui.space.chapter.ChapterReviewViewModel.ChapterReviewWrapper.TYPE_REFRESH;

public class ChapterReviewFragment extends BaseSpaceFragment {
    private static final String TAG = "ChapterReviewFragment";

    private static final String ARGS_USER_ID = "ARGS_USER_ID";


    public static ChapterReviewFragment newInstance(String userId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_USER_ID, userId);

        ChapterReviewFragment fragment = new ChapterReviewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            throw new UnsupportedOperationException("call newInstance() to obtain a Fragment");
        }
        mViewModel = new ViewModelProvider(this).get(ChapterReviewViewModel.class);
        mViewModel.setUserId(bundle.getString(ARGS_USER_ID));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel.mErrMsg.observe(owner, this::onErrMsgChanged);
        mViewModel.mUserChapterReview.observe(owner, this::onChapterReviewChanged);

        mRecyclerAdapter = new RecyclerAdapterImpl(this);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        PaddingDividerDecoration divider = new PaddingDividerDecoration(getContext(),
                PaddingDividerDecoration.VERTICAL);
        divider.setPadding(DisplayUtils.dp2px(45), 0,
                DisplayUtils.dp2px(15), 0);
        mRecyclerView.addItemDecoration(divider);

        return view;
    }

    private ChapterReviewViewModel mViewModel;
    private RecyclerAdapterImpl mRecyclerAdapter;

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh: start");
        mViewModel.refreshChapterReview();
    }

    @Override
    public void onLoadMore() {
        Log.d(TAG, "onLoadMore: start");
        mViewModel.loadMoreChapterReview();
    }

    private void onErrMsgChanged(String s) {
        if (s != null) {
            ToastUtils.getInstance(getContext()).show(s);
            setLoading(false);
        }
    }

    private void onChapterReviewChanged(ChapterReviewViewModel.ChapterReviewWrapper wrapper) {
        if (wrapper == null) {
            return;
        }

        switch (wrapper.type) {
            case TYPE_REFRESH:
                mRecyclerAdapter.updateDataSet(wrapper.review);
                setLoading(false);
                break;
            case TYPE_LOAD_MORE:
                mRecyclerAdapter.appendDataSet(wrapper.review);
                setLoading(false);
                break;
        }
    }
}
