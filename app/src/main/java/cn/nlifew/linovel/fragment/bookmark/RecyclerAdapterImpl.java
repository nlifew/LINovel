package cn.nlifew.linovel.fragment.bookmark;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.ui.reading.ReadingActivity;
import cn.nlifew.linovel.utils.TimeUtils;
import cn.nlifew.xqdreader.bean.TopBookMarkBean;

class RecyclerAdapterImpl extends RecyclerView.Adapter {
    private static final String TAG = "RecyclerAdapterImpl";

    RecyclerAdapterImpl(Fragment fragment) {
        mFragment = fragment;
    }

    private final Fragment mFragment;
    private TopBookMarkBean.BookMarkType[] mDataSet;

    void updateDataSet(TopBookMarkBean.BookMarkType[] data) {
        mDataSet = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.length;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mFragment.getContext())
                .inflate(R.layout.fragment_chapter_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        final Holder holder = ((Holder) h);
        final TopBookMarkBean.BookMarkType bookMark = mDataSet[position];

        holder.mTitleView.setText(bookMark.ChapterName);
        holder.mTimeView.setText(TimeUtils.formatDate(bookMark.CreateDate));
        holder.itemView.setTag(bookMark);
    }

    private final class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Holder(@NonNull View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.title);
            mTimeView = itemView.findViewById(R.id.time);
            itemView.setOnClickListener(this);
        }

        private TextView mTitleView;
        private TextView mTimeView;

        @Override
        public void onClick(View v) {
            TopBookMarkBean.BookMarkType bookMark = ((TopBookMarkBean.BookMarkType) v.getTag());
            String uri = "linovel://reading?id=" + bookMark.BookId
                    + "&chapterId=" + bookMark.ChapterId;
            Context context = mFragment.getContext();
            Intent intent = new Intent(context, ReadingActivity.class);
            intent.setData(Uri.parse(uri));
            context.startActivity(intent);
        }
    }
}
