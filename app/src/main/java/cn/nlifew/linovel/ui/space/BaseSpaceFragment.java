package cn.nlifew.linovel.ui.space;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import cn.nlifew.linovel.fragment.BaseFragment;
import cn.nlifew.linovel.fragment.loadmore.BaseLoadMoreFragment;
import cn.nlifew.linovel.utils.DisplayUtils;
import cn.nlifew.linovel.widget.LoadMoreRecyclerView;

public abstract class BaseSpaceFragment extends BaseLoadMoreFragment {
    private static final String TAG = "BaseSpaceFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mSwipeLayout.setEnabled(false);
        return view;
    }

    @Override
    protected RecyclerView.Adapter newRecyclerAdapter() {
        return null;
    }
}
