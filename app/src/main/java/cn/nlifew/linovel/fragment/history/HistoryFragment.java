package cn.nlifew.linovel.fragment.history;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import cn.nlifew.linovel.utils.ToastUtils;

public class HistoryFragment extends BaseLoadMoreFragment implements
        SearchView.OnCloseListener,
        SearchView.OnQueryTextListener {
    private static final String TAG = "HistoryFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel.mErrMsg.observe(owner, this::onErrMsgChanged);
        mViewModel.mRecordList.observe(owner, this::onReadingRecordChanged);

        return view;
    }


    private HistoryViewModel mViewModel;
    private RecyclerAdapterImpl mRecyclerAdapter;

    @Override
    protected RecyclerView.Adapter newRecyclerAdapter() {
        return mRecyclerAdapter = new RecyclerAdapterImpl(this);
    }

    @Override
    public void onRefresh() {
        setLoading(true);
        mViewModel.refreshReadingRecord();
    }

    @Override
    public void onLoadMore() {
        setLoading(true);
        mViewModel.loadMoreReadingRecord();
    }

    private void onErrMsgChanged(String s) {
        if (s != null) {
            ToastUtils.getInstance(getContext()).show(s);
            setLoading(false);
        }
    }

    private void onReadingRecordChanged(HistoryViewModel.Wrapper wrapper) {
        if (wrapper == null) {
            return;
        }
        switch (wrapper.type) {
            case HistoryViewModel.Wrapper.TYPE_REFRESH:
                mRecyclerAdapter.updateDataSet(wrapper.list);
                break;
            case HistoryViewModel.Wrapper.TYPE_LOAD_MORE:
                mRecyclerAdapter.appendDataSet(wrapper.list);
                break;
        }
        setLoading(false);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_history_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.fragment_history_delete) {
            DialogInterface.OnClickListener cli = (dialog, which) -> {
                dialog.dismiss();
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    mViewModel.clearAllHistory();
                    mRecyclerAdapter.clearDataSet();
                }
            };
            new AlertDialog.Builder(getContext())
                    .setTitle("提示")
                    .setMessage("这将删除本地存储的所有历史记录(不包括服务器)")
                    .setPositiveButton("删除", cli)
                    .setNegativeButton("取消", cli)
                    .create()
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
