package cn.nlifew.linovel.fragment.square;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import cn.nlifew.linovel.adapter.FragmentViewHolder;
import cn.nlifew.xqdreader.bean.SquareBean;

class RecyclerAdapterImpl extends RecyclerView.Adapter<FragmentViewHolder> {

    RecyclerAdapterImpl(Fragment fragment) {
        mFragment = fragment;
    }

    private final Fragment mFragment;
    private SquareBean.DataType mDataSet;

    void updateDataSet(SquareBean.DataType data) {
        mDataSet = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mDataSet == null || mDataSet.ItemList == null) {
            return 0;
        }

        int j = 0;
        for (int i = 0; i < mDataSet.ItemList.length; i++) {
            SquareBean.GroupType g = mDataSet.ItemList[i];
            if (g == null) break;

            // 这里只适配三种格式，快慢指针法把符合要求的移动到数组的左边
            if (g.Format == SquareBean.GroupType.FORMAT_BANNER ||
                g.Format == SquareBean.GroupType.FORMAT_VERTICAL ||
                g.Format == SquareBean.GroupType.FORMAT_HORIZONTAL) {
                mDataSet.ItemList[j++] = g;
            }
        }
        if (j < mDataSet.ItemList.length - 1) {
            mDataSet.ItemList[j] = null;
        }
        return j;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.ItemList[position].Format;
    }

    @NonNull
    @Override
    public FragmentViewHolder<SquareBean.GroupType> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case SquareBean.GroupType.FORMAT_VERTICAL: {
                return new VerticalViewHolder(mFragment, parent);
            }
            case SquareBean.GroupType.FORMAT_HORIZONTAL: {
                return new HorizontalViewHolder(mFragment, parent);
            }
            case SquareBean.GroupType.FORMAT_BANNER: {
                return new BannerViewHolder(mFragment);
            }
        }
        throw new UnsupportedOperationException("no support");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(@NonNull FragmentViewHolder holder, int position) {
        holder.onBindViewHolder(mDataSet.ItemList[position]);
    }
}
