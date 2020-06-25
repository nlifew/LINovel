package cn.nlifew.linovel.fragment.ranking;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.fragment.BaseFragment;
import cn.nlifew.linovel.utils.ToastUtils;
import cn.nlifew.linovel.widget.LoadMoreRecyclerView;
import cn.nlifew.xqdreader.bean.ranking.RankingGroupBean;

import static cn.nlifew.linovel.fragment.ranking.RankingViewModel.RankingListWrapper.TYPE_LOAD_MORE;
import static cn.nlifew.linovel.fragment.ranking.RankingViewModel.RankingListWrapper.TYPE_REFRESH;

public class RankingFragment extends BaseFragment implements
        GroupAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        LoadMoreRecyclerView.OnLoadMoreListener {
    private static final String TAG = "RankingFragment";

//    private static final String ARGS_SITE_ID = "ARGS_SITE_ID";
    private static final String ARGS_CATEGORY_ID = "ARGS_CATEGORY_ID";

    static RankingFragment newInstance(/*int siteId, */int categoryId) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_CATEGORY_ID, categoryId);
//        bundle.putInt(ARGS_SITE_ID, siteId);

        RankingFragment fragment = new RankingFragment();
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
        mViewModel = new ViewModelProvider(this).get(RankingViewModel.class);
        mViewModel.setRankingParam(bundle.getInt(ARGS_CATEGORY_ID));
//        mViewModel.setRankingParam(bundle.getInt(ARGS_SITE_ID), bundle.getInt(ARGS_CATEGORY_ID));
    }

    private RankingViewModel mViewModel;
    private GroupAdapter mGroupAdapter;
    private ListAdapter mListAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private LoadMoreRecyclerView mListView;
    private TextView mTitleView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel.mErrMsg.observe(owner, this::onErrMsgChanged);
        mViewModel.mRankingGroup.observe(owner, this::onRankingGroupChanged);
        mViewModel.mRankingList.observe(owner, this::onRankingListChanged);

        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        mTitleView = view.findViewById(R.id.fragment_ranking_title);

        mGroupAdapter = new GroupAdapter(this);
        RecyclerView groupView = view.findViewById(R.id.fragment_ranking_group);
        groupView.setAdapter(mGroupAdapter);
        groupView.setBackgroundColor(0xfff5f7fa);

        mListAdapter = new ListAdapter(this);
        mListView = view.findViewById(R.id.fragment_ranking_list);
        mListView.setOnLoadMoreListener(this);
        mListView.setAdapter(mListAdapter);
        mListView.addItemDecoration(new DividerItemDecoration(
                getContext(), DividerItemDecoration.VERTICAL)
        );

        mSwipeLayout = view.findViewById(R.id.fragment_ranking_swipe);
        mSwipeLayout.setOnRefreshListener(this);

        return view;
    }


    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        // fixme
        mViewModel.refreshRankingGroup();
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh: start");
        mListView.setLoadMoreWorking(true);
        mViewModel.refreshRankingList();
    }

    private void onRankingGroupChanged(RankingGroupBean.DataType[] data) {
        if (data == null) {
            return;
        }
        mGroupAdapter.updateDataSet(data);
    }

    @Override
    public void onLoadMore() {
        Log.d(TAG, "onLoadMore: start");
        mSwipeLayout.setRefreshing(true);
        mViewModel.loadMoreRankingList();
    }

    @Override
    public void onItemClick(RankingGroupBean.DataType data) {
        // 尝试从 SubItems 中找到具体的 item
        RankingGroupBean.SubItemType subItem = null;
        for (RankingGroupBean.SubItemType item : data.SubItems) {
            if (item != null && item.TopId == data.DefaultTopId) {
                subItem = item;
                break;
            }
        }
        if (subItem == null) {
            ToastUtils.getInstance(getContext()).show("topId 未命中 " + data.DefaultTopId);
            return;
        }
        mTitleView.setText(subItem.Desc);
        mViewModel.setRankingTopId(subItem.TopId);

        mSwipeLayout.setRefreshing(true);
        onRefresh();
    }


    private void onRankingListChanged(RankingViewModel.RankingListWrapper wrapper) {
        if (wrapper == null) {
            return;
        }
        mSwipeLayout.setRefreshing(false);
        mListView.setLoadMoreWorking(false);

        switch (wrapper.type) {
            case TYPE_REFRESH: {
                mListAdapter.updateDataSet(wrapper.data);
                break;
            }
            case TYPE_LOAD_MORE: {
                mListAdapter.appendDataSet(wrapper.data);
                break;
            }
        }
    }

    private void onErrMsgChanged(String s) {
        if (s != null) {
            ToastUtils.getInstance(getContext()).show(s);
            mSwipeLayout.setRefreshing(false);
            mListView.setLoadMoreWorking(false);
        }
    }
}
