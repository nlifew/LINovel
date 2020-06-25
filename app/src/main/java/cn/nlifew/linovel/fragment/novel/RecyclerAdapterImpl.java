package cn.nlifew.linovel.fragment.novel;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.nlifew.linovel.adapter.FragmentViewHolder;
import cn.nlifew.xqdreader.bean.CommentBean;
import cn.nlifew.xqdreader.bean.NovelBean;

class RecyclerAdapterImpl extends RecyclerView.Adapter<FragmentViewHolder> {
    private static final int TYPE_COMMENT   =   1;
    private static final int TYPE_NOVEL     =   2;
    private static final int TYPE_ALIKE     =   3;

    RecyclerAdapterImpl(Fragment fragment) {
        mFragment = fragment;
        mComments = new ArrayList<>(64);
    }

    private final Fragment mFragment;
    private NovelBean.DataType mNovelData;
    private List<CommentBean.TopicDataType> mComments;


    void updateNovelData(NovelBean.DataType data) {
        mNovelData = data;
        notifyDataSetChanged();
    }

    void appendCommentsData(CommentBean.DataType data) {
        int oldSize = getItemCount();
        mComments.addAll(Arrays.asList(data.TopicDataList));
        int newSize = getItemCount();

        notifyItemRangeChanged(oldSize, newSize - oldSize);
    }

    @Override
    public int getItemCount() {
        int size = mComments.size();
        if (mNovelData != null) {
            size += 2;
        }
        return size;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0: return TYPE_NOVEL;
            case 1: return TYPE_ALIKE;
            default: return TYPE_COMMENT;
        }
    }

    @NonNull
    @Override
    public FragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_NOVEL: return new HeaderViewHolder(mFragment, parent);
            case TYPE_ALIKE: return new AlikeViewHolder(mFragment, parent);
            default:
                return new CommentViewHolder(mFragment, parent);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(@NonNull FragmentViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_NOVEL:
                holder.onBindViewHolder(mNovelData);
            case TYPE_ALIKE:
                holder.onBindViewHolder(mNovelData);
                break;
            default:
                holder.onBindViewHolder(mComments.get(position - 2));
        }
    }
}
