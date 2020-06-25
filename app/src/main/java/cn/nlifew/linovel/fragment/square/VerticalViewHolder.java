package cn.nlifew.linovel.fragment.square;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.adapter.FragmentViewHolder;
import cn.nlifew.linovel.ui.novel.NovelActivity;
import cn.nlifew.linovel.utils.ToastUtils;
import cn.nlifew.xqdreader.bean.SquareBean;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import cn.nlifew.xqdreader.utils.Utils;

class VerticalViewHolder extends FragmentViewHolder<SquareBean.GroupType> {
    private static final String TAG = "VerticalViewHolder";

    private static final class Holder {
        final ImageView coverView;
        final TextView titleView;
        final TextView aboutView;
        final TextView descriptionView;
        final View itemView;

        Holder(View v) {
            itemView = v;
            coverView = v.findViewById(R.id.fragment_home_vertical_item_cover);
            titleView = v.findViewById(R.id.fragment_home_vertical_item_title);
            aboutView = v.findViewById(R.id.fragment_home_vertical_item_about);
            descriptionView = v.findViewById(R.id.fragment_home_vertical_item_description);
        }
    }

    VerticalViewHolder(Fragment fragment, ViewGroup parent) {
        super(fragment, R.layout.fragment_home_vertical, parent);

        titleView = itemView.findViewById(R.id.fragment_home_vertical_title);
        arrowView = itemView.findViewById(R.id.fragment_home_vertical_arrow);
        itemViews = new Holder[] {
                new Holder(itemView.findViewById(R.id.fragment_home_vertical_item_1)),
                new Holder(itemView.findViewById(R.id.fragment_home_vertical_item_2)),
                new Holder(itemView.findViewById(R.id.fragment_home_vertical_item_3)),
        };
    }

    private final TextView titleView;
    private final TextView arrowView;
    private final Holder[] itemViews;


    @Override
    public void onBindViewHolder(SquareBean.GroupType group) {
        titleView.setText(group.Title);

        arrowView.setTag(group.ActionUrl);
        arrowView.setText(group.ActionText);
        arrowView.setOnClickListener(this::onArrowViewClick);

        StringBuilder sb = Utils.obtainStringBuilder(32);

        SquareBean.BookDataType[] books = (SquareBean.BookDataType[]) group.Data;
        for (int i = 0; i < itemViews.length; i++) {
            Holder holder = itemViews[i];
            SquareBean.BookDataType book = books[i];
            holder.titleView.setText(book.BookName);
            holder.aboutView.setText(getBookAbout(book, sb));
            holder.descriptionView.setText(book.Description);

            String cover = NetworkUtils.novelCoverImage(book.BookId);
            Glide.with(mFragment).asBitmap().load(cover).into(holder.coverView);

            holder.itemView.setTag(book);
            holder.itemView.setOnClickListener(this::onNovelViewClick);
        }
        Utils.recycle(sb);
    }

    private void onArrowViewClick(View v) {
        Context c = mFragment.getContext();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse((String) v.getTag());
        intent.setData(uri);

        try {
            c.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "onArrowViewClick: " + uri, e);
            ToastUtils.getInstance(c).show(e.toString());
        }
    }

  private void onNovelViewClick(View v) {
        SquareBean.BookDataType book = (SquareBean.BookDataType) v.getTag();
        Uri data = Uri.parse("linovel://novel?id=" + book.BookId
                + "&title=" + book.BookName + "&outOfBook=" + book.IsOutBook);

        Context context = mFragment.getContext();
        Intent intent = new Intent(context, NovelActivity.class);
        intent.setData(data);
        context.startActivity(intent);
    }

    private static String getBookAbout(SquareBean.BookDataType book, StringBuilder sb) {
        sb.setLength(0);
        sb.append(book.AuthorName).append('-')
                .append(book.CategoryName).append('-')
                .append(book.BookStatus).append('-');
        if (book.WordsCount > 10000) {
            sb.append(book.WordsCount / 10000).append("万字");
        }
        else {
            sb.append(book.WordsCount / 1000).append("千字");
        }
        return sb.toString();
    }
}
