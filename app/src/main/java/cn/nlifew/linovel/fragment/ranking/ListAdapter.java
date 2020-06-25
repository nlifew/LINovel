package cn.nlifew.linovel.fragment.ranking;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.ui.novel.NovelActivity;
import cn.nlifew.xqdreader.bean.ranking.RankingListBean;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import cn.nlifew.xqdreader.utils.Utils;

class ListAdapter extends RecyclerView.Adapter<ListAdapter.Holder> {

    ListAdapter(Fragment fragment) {
        mFragment = fragment;
        mDataSet = new ArrayList<>(64);
    }

    private final Fragment mFragment;
    private final List<RankingListBean.DataType> mDataSet;

    void updateDataSet(RankingListBean.DataType[] data) {
        mDataSet.clear();
        mDataSet.addAll(Arrays.asList(data));
        notifyDataSetChanged();
    }

    void appendDataSet(RankingListBean.DataType[] data) {
        int old = mDataSet.size();
        mDataSet.addAll(Arrays.asList(data));
        notifyItemRangeChanged(old, data.length);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = mFragment.getContext();
        View view = LayoutInflater.from(context).inflate(
                R.layout.fragment_ranking_item,
                parent,
                false
        );
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        RankingListBean.DataType data = mDataSet.get(position);
        holder.mTitleView.setText(data.BookName);
        holder.mAboutView.setText(getBookAbout(data));

        Context context = mFragment.getContext();
        Glide.get(context)
                .getRequestManagerRetriever()
                .get(mFragment)
                .asBitmap()
                .load(NetworkUtils.novelCoverImage(data.BookId))
                .into(holder.mCoverView);

        holder.itemView.setOnClickListener((v) -> {
            StringBuilder sb = Utils.obtainStringBuilder(64)
                    .append("linovel://novel?id=").append(data.BookId)
                    .append("&title=").append(data.BookName)
                    .append("&outOfBook=").append(data.IsOutBook);
            Uri uri = Uri.parse(sb.toString());
            Utils.recycle(sb);

            Intent intent = new Intent(context, NovelActivity.class);
            intent.setData(uri);
            context.startActivity(intent);
        });
    }

    private static String getBookAbout(RankingListBean.DataType data) {
        StringBuilder sb = Utils.obtainStringBuilder(32)
                .append(data.Author).append(" - ")
                .append(data.CategoryName).append(" - ");
        if (data.WordsCount > 10000) {
            sb.append(data.WordsCount / 10000).append("万字");
        } else {
            sb.append(data.WordsCount).append('字');
        }
        String s = sb.toString();
        Utils.recycle(sb);
        return s;
    }

    static final class Holder extends RecyclerView.ViewHolder {
        Holder(@NonNull View itemView) {
            super(itemView);
            mCoverView = itemView.findViewById(R.id.fragment_ranking_item_cover);
            mTitleView = itemView.findViewById(R.id.fragment_ranking_item_title);
            mAboutView = itemView.findViewById(R.id.fragment_ranking_item_about);
        }
        final ImageView mCoverView;
        final TextView mTitleView;
        final TextView mAboutView;
    }
}
