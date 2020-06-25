package cn.nlifew.linovel.ui.space.book;

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
import cn.nlifew.xqdreader.bean.user.UserBean;

public class UserBookFragment extends BaseSpaceFragment {
    private static final String TAG = "UserBookFragment";

    private static final String ARGS_AUTHOR_ID = "ARGS_AUTHOR_ID";

    public static UserBookFragment newInstance(String authorId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_AUTHOR_ID, authorId);

        UserBookFragment fragment = new UserBookFragment();
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
        mViewModel = new ViewModelProvider(this).get(BookViewModel.class);
        mViewModel.setAuthorId(bundle.getString(ARGS_AUTHOR_ID));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel.mErrMsg.observe(owner, this::onErrMsgChanged);
        mViewModel.mUserBooksData.observe(owner, this::onUserBooksChanged);

        mRecyclerAdapter = new RecyclerAdapterImpl(this);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLoadMoreEnabled(false);

        PaddingDividerDecoration divider = new PaddingDividerDecoration(getContext(),
                PaddingDividerDecoration.VERTICAL);
        divider.setPadding(DisplayUtils.dp2px(20), 0,
                DisplayUtils.dp2px(15), 0);
        mRecyclerView.addItemDecoration(divider);

        return view;
    }

    private BookViewModel mViewModel;
    private RecyclerAdapterImpl mRecyclerAdapter;


    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh: start");
        mViewModel.refreshData();
    }

    private void onErrMsgChanged(String s) {
        if (s != null) {
            ToastUtils.getInstance(getContext()).show(s);
            setLoading(false);
        }
    }

    private void onUserBooksChanged(UserBean.AuthorBookType data) {
        if (data != null) {
            mRecyclerAdapter.updateDataSet(data);
            setLoading(false);
        }
    }
}
