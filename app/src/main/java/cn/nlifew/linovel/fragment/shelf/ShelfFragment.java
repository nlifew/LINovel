package cn.nlifew.linovel.fragment.shelf;

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
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.fragment.loadmore.BaseLoadMoreFragment;
import cn.nlifew.linovel.utils.ToastUtils;
import cn.nlifew.xqdreader.entity.BookShelf;

public class ShelfFragment extends BaseLoadMoreFragment implements
        RecyclerAdapterImpl.OnItemDragListener{
    private static final String TAG = "ShelfFragment";


    ShelfViewModel mViewModel;
    RecyclerAdapterImpl mRecyclerAdapter;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ShelfViewModel.class);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel.mErrMsg.observe(owner, this::onErrMsgChanged);
        mViewModel.mBookShelfList.observe(owner, this::onBookShelfChanged);

        TouchHelperCallback callback = new TouchHelperCallback(this);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setLoadMoreEnabled(false);    // 禁用上滑加载
        return view;
    }

    @Override
    protected RecyclerView.Adapter newRecyclerAdapter() {
        return mRecyclerAdapter = new RecyclerAdapterImpl(this);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.frament_shelf_options, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        if (mRecyclerAdapter == null) {
            return;
        }
        boolean isEditable = mRecyclerAdapter.isEditable();
        MenuItem editItem = menu.findItem(R.id.fragment_shelf_edit);
        MenuItem doneItem = menu.findItem(R.id.fragment_shelf_edit_done);

        editItem.setVisible(! isEditable);
        doneItem.setVisible(isEditable);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_shelf_edit:
                setEditable(true);
                return true;
            case R.id.fragment_shelf_edit_done:
                setEditable(false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh: start");
        mViewModel.refreshBookShelf();
    }

    private void onErrMsgChanged(String s) {
        if (s != null) {
            ToastUtils.getInstance(getContext()).show(s);
            setLoading(false);
        }
    }

    private void onBookShelfChanged(BookShelf[] list) {
        if (list != null) {
            mRecyclerAdapter.mDataSet.clear();
            mRecyclerAdapter.mDataSet.addAll(Arrays.asList(list));
            mRecyclerAdapter.notifyDataSetChanged();
            setLoading(false);
        }
    }

    public boolean isEditable() { return mRecyclerAdapter.isEditable(); }

    public void setEditable(boolean editable) {
        boolean old = mRecyclerAdapter.setEditable(editable);

        FragmentActivity activity = getActivity();
        if (old != editable && activity != null) {
            activity.invalidateOptionsMenu();
        }
    }

    @Override
    public void onItemDrag(RecyclerView.ViewHolder holder) {
        mItemTouchHelper.startDrag(holder);
    }
}
