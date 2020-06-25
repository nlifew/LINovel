package cn.nlifew.linovel.fragment.category2;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.nlifew.linovel.utils.DisplayUtils;

class HorizontalListView extends RecyclerView {
    private static final String TAG = "HorizontalListView";


    public HorizontalListView(@NonNull Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LinearLayoutManager lm = new LinearLayoutManager(context);
        lm.setOrientation(HORIZONTAL);
        setLayoutManager(lm);

        mAdapter = new AdapterImpl();
        setAdapter(mAdapter);
    }

    private AdapterImpl mAdapter;

    public interface OnItemSelectListener {
        void onItemSelect(HorizontalListView view, Object tag);
    }

    private OnItemSelectListener mItemClickListener;

    public void setOnItemSelectListener(OnItemSelectListener cli) {
        mItemClickListener = cli;
    }

    public OnItemSelectListener getOnItemClickListener() {
        return mItemClickListener;
    }

    public void appendItem(String text, Object tag) {
        mAdapter.mDataSet.add(text);
        mAdapter.mDataSet.add(tag);
    }

    public void setSelectedItem(int index) {
        int n = mAdapter.mDataSet.size() / 2;
        int old = mAdapter.mCheckedItemPosition;
        if (old != n) {
            mAdapter.mCheckedItemPosition = index;

            if (old > -1 && old < n) {
                mAdapter.notifyItemChanged(old);
            }
            if (index > -1 && index < n) {
                mAdapter.notifyItemChanged(index);
            }
        }
    }

    public void commit() {
        mAdapter.notifyDataSetChanged();
    }

    public void clear() {
        mAdapter.mCheckedItemPosition = -1;
        mAdapter.mDataSet.clear();
    }

    private class AdapterImpl extends Adapter {

        final List<Object> mDataSet = new ArrayList<>(24);
        int mCheckedItemPosition = -1;

        final Drawable mCheckedDrawable = new ColorDrawable(0xFFFFF5F5);

        @Override
        public int getItemCount() {
            return mDataSet.size() / 2;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView tv = new TextView(getContext());
            MarginLayoutParams lp = new MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            lp.leftMargin = lp.rightMargin = DisplayUtils.dp2px(5);
            tv.setLayoutParams(lp);
            tv.setGravity(Gravity.CENTER);
            return new Holder(tv);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, final int position) {
            final TextView tv = (TextView) h.itemView;
            final String text = (String) mDataSet.get(position * 2);
            final Object id = mDataSet.get(position * 2 + 1);

            tv.setText(text);

            final boolean checked = position == mCheckedItemPosition;
            Log.d(TAG, "onBindViewHolder: [" + position + "/" + mCheckedItemPosition + "]");

            tv.setBackground(checked ? mCheckedDrawable : null);
            tv.setTextColor(checked ? 0xFFED424B : 0xFF737373);
            tv.getPaint().setFakeBoldText(checked);

            tv.setOnClickListener(v -> {
                setSelectedItem(position);
                if (mItemClickListener != null) {
                    mItemClickListener.onItemSelect(HorizontalListView.this, id);
                }
            });
        }
    }

    private static final class Holder extends ViewHolder {
        Holder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
