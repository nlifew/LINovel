package cn.nlifew.linovel.fragment.shelf;

import android.util.Log;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;

import cn.nlifew.xqdreader.entity.BookShelf;

class TouchHelperCallback extends ItemTouchHelper.Callback {
    private static final String TAG = "TouchHelperCallback";

    TouchHelperCallback(ShelfFragment fragment) {
        mFragment = fragment;
    }

    private final ShelfFragment mFragment;

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlag, swipeFlag);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        boolean editable = mFragment.isEditable();
        if (! editable) {
            mFragment.setEditable(true);
        }
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return mFragment.isEditable();
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE && viewHolder != null) {
            viewHolder.itemView.setTranslationZ(10);
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setTranslationZ(0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        int from = viewHolder.getAdapterPosition();
        int to = target.getAdapterPosition();

        Collections.swap(mFragment.mRecyclerAdapter.mDataSet, from, to);
        mFragment.mRecyclerAdapter.notifyItemMoved(from, to);

        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        final RecyclerAdapterImpl adapter = mFragment.mRecyclerAdapter;
        final BookShelf old = adapter.mDataSet.remove(position);
        adapter.notifyItemRemoved(position);

        Snackbar.make(mFragment.getView(), "已从书架移除", BaseTransientBottomBar.LENGTH_LONG)
                .setAction("撤销", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.mDataSet.add(position, old);
                        adapter.notifyItemInserted(position);
                    }
                })
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        // 当 snackBar 消失时，通知 ViewModel 移除这本书
                        if (event != DISMISS_EVENT_ACTION) {
                            mFragment.mViewModel.removeBookShelf(old.BookId);
                        }
                    }
                })
                .show();
    }
}
