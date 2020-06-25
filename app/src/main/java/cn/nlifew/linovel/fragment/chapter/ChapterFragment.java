package cn.nlifew.linovel.fragment.chapter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.fragment.loadmore.BaseLoadMoreFragment;
import cn.nlifew.linovel.utils.DisplayUtils;
import cn.nlifew.linovel.utils.ToastUtils;
import cn.nlifew.linovel.widget.PaddingDividerDecoration;
import cn.nlifew.xqdreader.bean.ChapterListBean;

public class ChapterFragment extends BaseLoadMoreFragment implements
        SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    private static final String TAG = "ChapterFragment";

    private static final String ARGS_NOVEL_ID = "ARGS_NOVEL_ID";

    public static ChapterFragment newInstance(String bookId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_NOVEL_ID, bookId);
        ChapterFragment fragment = new ChapterFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            throw new UnsupportedOperationException("call newInstance to make a Fragment");
        }
        setHasOptionsMenu(true);
        mViewModel = new ViewModelProvider(this).get(ChapterViewModel.class);
        mViewModel.setNovelId(bundle.getString(ARGS_NOVEL_ID));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel.mErrMsg.observe(owner, this::onErrMsgChanged);
        mViewModel.mChapters.observe(owner, this::onChapterListChanged);


        PaddingDividerDecoration decoration = new PaddingDividerDecoration(
                getContext(), PaddingDividerDecoration.VERTICAL
        );
        decoration.setPadding(DisplayUtils.dp2px(20), 0,
                DisplayUtils.dp2px(15), 0);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setLoadMoreEnabled(false);    // 禁用上滑加载
        return view;
    }

    private ChapterViewModel mViewModel;
    private RecyclerAdapterImpl mRecyclerAdapter;

    @Override
    protected RecyclerView.Adapter newRecyclerAdapter() {
        return mRecyclerAdapter = new RecyclerAdapterImpl(this);
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh: start");
        mViewModel.refreshChapters();
    }

    private void onErrMsgChanged(String s) {
        if (s != null) {
            ToastUtils.getInstance(getContext()).show(s);
            setLoading(false);
        }
    }

    private void onChapterListChanged(ChapterListBean.DataType data) {
        if (data == null) {
            return;
        }
        mRecyclerAdapter.refreshDataSet(data);
        setLoading(false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_chapter_options, menu);
        SearchView searchView = (SearchView) menu
                .findItem(R.id.fragment_chapter_search)
                .getActionView();
        searchView.setOnCloseListener(this);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (! isLoading()) {
            if (item.getItemId() == R.id.fragment_chapter_sort) {
                setLoading(true);
                mViewModel.reverseChapters();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onClose() {
        if (! isLoading()) {
            setLoading(true);
            mViewModel.refreshChapters();
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (! isLoading()) {
            setLoading(true);
            mViewModel.filterChapters(query);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
