package cn.nlifew.linovel.fragment.group;

import android.os.Bundle;
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
import cn.nlifew.xqdreader.bean.GroupBean;

public class GroupFragment extends BaseLoadMoreFragment {
    private static final String TAG = "GroupFragment";

    private static final String ARGS_GROUP_ID = "ARGS_GROUP_ID";

    public static GroupFragment newInstance(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_GROUP_ID, id);
        GroupFragment fragment = new GroupFragment();
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
        mViewModel = new ViewModelProvider(this).get(GroupViewModel.class);
        mViewModel.setGroupId(bundle.getString(ARGS_GROUP_ID));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mRecyclerView.setPadding(DisplayUtils.dp2px(20),
                DisplayUtils.dp2px(5)
                , DisplayUtils.dp2px(15),
                0);

        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel.mErrorMsg.observe(owner, this::onErrorMsgChanged);
        mViewModel.mRefreshGroupData.observe(owner, this::onRefreshGroupData);
        mViewModel.mLoadMoreGroupDatas.observe(owner, this::onLoadMoreGroupData);

        return view;
    }

    private GroupViewModel mViewModel;
    private RecyclerAdapterImpl mRecyclerAdapter;

    @Override
    public void onLoadMore() {
        setLoading(true);
        mViewModel.loadMoreGroupData();
    }

    @Override
    public void onRefresh() {
        setLoading(true);
        mViewModel.refreshGroupData();
    }

    private void onErrorMsgChanged(String msg) {
        if (msg != null) {
            ToastUtils.getInstance(getContext()).show(msg);
            setLoading(false);
        }
    }

    private void onRefreshGroupData(GroupBean.DataType data) {
        if (data != null) {
            mRecyclerAdapter.refreshDataSet(data);
            setLoading(false);
        }
    }

    private void onLoadMoreGroupData(GroupBean.DataType data) {
        if (data != null) {
            mRecyclerAdapter.appendDataSet(data);
            setLoading(false);
        }
    }


    @Override
    protected RecyclerView.Adapter newRecyclerAdapter() {
        mRecyclerAdapter = new RecyclerAdapterImpl(this);
        return mRecyclerAdapter;
    }
}
