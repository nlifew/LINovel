package cn.nlifew.linovel.fragment.group;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.nlifew.xqdreader.bean.GroupBean;
import cn.nlifew.xqdreader.bean.SquareBean;

public class RecyclerAdapterImpl extends RecyclerView.Adapter<VerticalViewHolder> {
    private static final String TAG = "RecyclerAdapterImpl";

    public RecyclerAdapterImpl(Fragment fragment) {
        mFragment = fragment;
        mDataSet = new ArrayList<>(64);
    }

    private final Fragment mFragment;
    private final List<SquareBean.BookDataType> mDataSet;

    void refreshDataSet(GroupBean.DataType data) {
        if (data.Format != SquareBean.GroupType.FORMAT_VERTICAL) {
            Log.w(TAG, "refreshDataSet: invalid format: " + data.Format);
            return;
        }
        mDataSet.clear();
        mDataSet.addAll(Arrays.asList(data.Data.Items));
        notifyDataSetChanged();
    }

    void appendDataSet(GroupBean.DataType data) {
        if (data.Format != SquareBean.GroupType.FORMAT_VERTICAL) {
            Log.w(TAG, "refreshDataSet: invalid format: " + data.Format);
            return;
        }
        int old = mDataSet.size();
        mDataSet.addAll(Arrays.asList(data.Data.Items));
        notifyItemRangeChanged(old, data.Data.Items.length);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @NonNull
    @Override
    public VerticalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VerticalViewHolder(mFragment, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalViewHolder holder, int position) {
        holder.onBindViewHolder(mDataSet.get(position));
    }
}
