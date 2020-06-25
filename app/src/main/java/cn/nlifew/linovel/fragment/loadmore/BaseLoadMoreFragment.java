package cn.nlifew.linovel.fragment.loadmore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.fragment.BaseFragment;
import cn.nlifew.linovel.widget.LoadMoreRecyclerView;

public abstract class BaseLoadMoreFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        LoadMoreRecyclerView.OnLoadMoreListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loadmore, container, false);

        mSwipeLayout = view.findViewById(R.id.fragment_loadmore_swipe);
        mSwipeLayout.setOnRefreshListener(this);

        mRecyclerView = view.findViewById(R.id.fragment_loadmore_recycler);
        mRecyclerView.setOnLoadMoreListener(this);

        RecyclerView.Adapter adapter = newRecyclerAdapter();
        mRecyclerView.setAdapter(adapter);

        return view;
    }

    protected SwipeRefreshLayout mSwipeLayout;
    protected LoadMoreRecyclerView mRecyclerView;

    protected abstract RecyclerView.Adapter newRecyclerAdapter();

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        setLoading(true);
        onRefresh();
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onLoadMore() {
    }

    public void setLoading(boolean loading) {
        mSwipeLayout.setRefreshing(loading);
        mRecyclerView.setLoadMoreWorking(loading);
    }

    public boolean isLoading() { return mRecyclerView.isLoadMoreWorking(); }
}
