package cn.nlifew.linovel.fragment.category2;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.fragment.BaseFragment;
import cn.nlifew.linovel.fragment.loadmore.BaseLoadMoreFragment;
import cn.nlifew.linovel.utils.DisplayUtils;
import cn.nlifew.linovel.utils.ToastUtils;
import cn.nlifew.linovel.widget.LoadMoreRecyclerView;
import cn.nlifew.linovel.widget.PaddingDividerDecoration;
import cn.nlifew.xqdreader.bean.category.CategoryBean;

import static cn.nlifew.linovel.fragment.category2.CategoryViewModel2.CategoryBookWrapper.TYPE_LOAD_MORE;
import static cn.nlifew.linovel.fragment.category2.CategoryViewModel2.CategoryBookWrapper.TYPE_REFRESH;


public class CategoryFragment extends BaseLoadMoreFragment implements
        HeaderView.OnItemSelectListener {
    private static final String TAG = "CategoryFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CategoryViewModel2.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        PaddingDividerDecoration d = new PaddingDividerDecoration(getContext(),
                PaddingDividerDecoration.VERTICAL);
        d.setPadding(DisplayUtils.dp2px(20), 0, DisplayUtils.dp2px(15), 0);
        mRecyclerView.addItemDecoration(d);

        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel.mErrMsg.observe(owner, this::onErrMsgChanged);
        mViewModel.mHeader.observe(owner, this::onCategoryHeaderChanged);
        mViewModel.mBookList.observe(owner, this::onBookListChanged);

        return view;
    }

    @Override
    protected RecyclerView.Adapter newRecyclerAdapter() {
        return mRecyclerAdapter = new RecyclerAdapterImpl(this);
    }

    private CategoryViewModel2 mViewModel;
    private RecyclerAdapterImpl mRecyclerAdapter;


    @Override
    public void onLoadMore() {
        setLoading(true);
        mViewModel.loadMoreBooks();
    }

    @Override
    public void onRefresh() {
        setLoading(true);
        mViewModel.refreshHeader();
    }

    private void onErrMsgChanged(String s) {
        if (s != null) {
            ToastUtils.getInstance(getContext()).show(s);
            setLoading(false);
        }
    }

    private void onCategoryHeaderChanged(CategoryBean.DataType header) {
        if (header == null) {
            return;
        }
        mRecyclerAdapter.updateHeader(header);
        setLoading(false);
    }

    private void onBookListChanged(CategoryViewModel2.CategoryBookWrapper wrapper) {
        if (wrapper == null) {
            return;
        }
        switch (wrapper.type) {
            case TYPE_REFRESH:
                mRecyclerAdapter.updateBookList(wrapper.books);
                break;
            case TYPE_LOAD_MORE:
                mRecyclerAdapter.appendBookList(wrapper.books);
                break;
        }
        setLoading(false);
    }

    @Override
    public void onItemSelect(String key, String value) {
        Log.d(TAG, "onItemSelect: [" + key + ", " + value + "]");
        if (mViewModel.updateQueryParam(key, value)) {
            setLoading(true);
        }
    }
}
