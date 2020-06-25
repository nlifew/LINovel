package cn.nlifew.linovel.ui.space.circle;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import cn.nlifew.linovel.ui.space.BaseSpaceFragment;
import cn.nlifew.linovel.utils.DisplayUtils;
import cn.nlifew.linovel.utils.ToastUtils;
import cn.nlifew.linovel.widget.PaddingDividerDecoration;

import static cn.nlifew.linovel.ui.space.circle.CircleReviewViewModel.CircleReviewWrapper.TYPE_LOAD_MORE;
import static cn.nlifew.linovel.ui.space.circle.CircleReviewViewModel.CircleReviewWrapper.TYPE_REFRESH;

public class CircleReviewFragment extends BaseSpaceFragment {
    private static final String TAG = "CircleReviewFragment";

    private static final String ARGS_USER_ID = "ARGS_USER_ID";

    public static CircleReviewFragment newInstance(String userId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_USER_ID, userId);

        CircleReviewFragment fragment = new CircleReviewFragment();
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
        mViewModel = new ViewModelProvider(this).get(CircleReviewViewModel.class);
        mViewModel.setUserId(bundle.getString(ARGS_USER_ID));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel.mErrMsg.observe(owner, this::onErrMsgChanged);
        mViewModel.mUserCircleReview.observe(owner, this::onCircleReviewChanged);

        mRecyclerAdapter = new RecyclerAdapterImpl(this);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        PaddingDividerDecoration divider = new PaddingDividerDecoration(getContext(),
                PaddingDividerDecoration.VERTICAL);
        divider.setPadding(DisplayUtils.dp2px(45), 0,
                DisplayUtils.dp2px(15), 0);
        mRecyclerView.addItemDecoration(divider);

        return view;
    }

    private CircleReviewViewModel mViewModel;
    private RecyclerAdapterImpl mRecyclerAdapter;

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh: start");
        mViewModel.refreshCircleReview();
    }

    @Override
    public void onLoadMore() {
        Log.d(TAG, "onLoadMore: start");
        mViewModel.loadMoreCircleReview();
    }

    private void onErrMsgChanged(String s) {
        if (s != null) {
            ToastUtils.getInstance(getContext()).show(s);
            setLoading(false);
        }
    }

    private void onCircleReviewChanged(CircleReviewViewModel.CircleReviewWrapper wrapper) {
        if (wrapper == null) {
            return;
        }
        switch (wrapper.type) {
            case TYPE_REFRESH:
                mRecyclerAdapter.updateDataSet(wrapper.review);
                break;
            case TYPE_LOAD_MORE:
                mRecyclerAdapter.appendDataSet(wrapper.review);
                break;
        }
        setLoading(false);
    }
}
