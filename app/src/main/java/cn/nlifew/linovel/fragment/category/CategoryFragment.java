package cn.nlifew.linovel.fragment.category;

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
import cn.nlifew.linovel.utils.ToastUtils;
import cn.nlifew.xqdreader.bean.category.CategoryBean;

import static cn.nlifew.linovel.fragment.category.CategoryViewModel.CategoryBookWrapper.TYPE_LOAD_MORE;
import static cn.nlifew.linovel.fragment.category.CategoryViewModel.CategoryBookWrapper.TYPE_REFRESH;

/**
 * 时间仓促，勉强实现功能
 * 如果允许，请重构
 * @deprecated use CategoryFragment {@link cn.nlifew.linovel.fragment.category2.CategoryFragment}
 */
@Deprecated
public class CategoryFragment extends BaseLoadMoreFragment implements
        HeaderView.OnItemChangedListener {
    private static final String TAG = "CategoryFragment";
//    private static final boolean DEBUG = true;

//    private static final String ARGS_SITE_ID = "ARGS_SITE_ID";

//    public static CategoryFragment newInstance(String siteId) {
//        Bundle bundle = new Bundle();
//        bundle.putString(ARGS_SITE_ID, siteId);
//
//        CategoryFragment fragment = new CategoryFragment();
//        fragment.setArguments(bundle);
//        return fragment;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Bundle bundle = getArguments();
//        if (bundle == null) {
//            throw new UnsupportedOperationException("call newInstance() to obtain a Fragment");
//        }
        mViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
//        mViewModel.setSiteId(bundle.getString(ARGS_SITE_ID));
    }

    private CategoryViewModel mViewModel;
    private RecyclerAdapterImpl2 mRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel.mErrMsg.observe(owner, this::onErrMsgChanged);
        mViewModel.mBooks.observe(owner, this::onBookListChanged);
        mViewModel.mCategory.observe(owner, this::onCategoryChanged);

        return view;
    }

    @Override
    protected RecyclerView.Adapter newRecyclerAdapter() {
        return mRecyclerAdapter = new RecyclerAdapterImpl2(this);
    }

    @Override
    public void onRefresh() {
        setLoading(true);
        mViewModel.refreshCategory();
    }

    @Override
    public void onLoadMore() {
        setLoading(true);
        mViewModel.loadMoreBooks();
    }

    private void onErrMsgChanged(String s) {
        if (s != null) {
            ToastUtils.getInstance(getContext()).show(s);
            setLoading(false);
        }
    }

    private void onCategoryChanged(CategoryBean.DataType data) {
        if (data != null) {
            mRecyclerAdapter.updateHeader(data);
            setLoading(false);
        }
    }

    private void onBookListChanged(CategoryViewModel.CategoryBookWrapper wrapper) {
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
    public void onItemChanged(String key, String value) {
        Log.d(TAG, "onItemSelect: [" + key + ", " + value + "]");
        if (mViewModel.updateQueryParam(key, value)) {
            setLoading(true);
        }
    }
}
