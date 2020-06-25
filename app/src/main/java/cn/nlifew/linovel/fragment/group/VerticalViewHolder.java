package cn.nlifew.linovel.fragment.group;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.adapter.FragmentViewHolder;
import cn.nlifew.linovel.ui.novel.NovelActivity;
import cn.nlifew.xqdreader.bean.SquareBean;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import cn.nlifew.xqdreader.utils.Utils;

public class VerticalViewHolder extends FragmentViewHolder<SquareBean.BookDataType> {

    VerticalViewHolder(Fragment fragment, ViewGroup parent) {
        super(fragment, R.layout.fragment_home_vertical_item, parent);
        mTitleView = itemView.findViewById(R.id.fragment_home_vertical_item_title);
        mAboutView = itemView.findViewById(R.id.fragment_home_vertical_item_about);
        mCoverView = itemView.findViewById(R.id.fragment_home_vertical_item_cover);
        mSummaryView = itemView.findViewById(R.id.fragment_home_vertical_item_description);
    }


    private final TextView mTitleView;
    private final ImageView mCoverView;
    private final TextView mAboutView;
    private final TextView mSummaryView;

    @Override
    public void onBindViewHolder(SquareBean.BookDataType book) {
        itemView.setTag(book);
        itemView.setOnClickListener(this::onItemViewClick);

        mTitleView.setText(book.BookName);
        mSummaryView.setText(book.Description);

        StringBuilder sb = Utils.obtainStringBuilder(32);
        mAboutView.setText(getBookAbout(book, sb));
        Utils.recycle(sb);

        String cover = NetworkUtils.novelCoverImage(book.BookId);
        Glide.with(mFragment).asBitmap().load(cover).into(mCoverView);
    }

    private void onItemViewClick(View v) {
        SquareBean.BookDataType book = (SquareBean.BookDataType) v.getTag();
        StringBuilder sb = Utils.obtainStringBuilder(64);
        sb.append("linovel://novel?id=").append(book.BookId)
                .append("&title=").append(book.BookName)
                .append("&outOfBook=").append(book.IsOutBook);
        String data = sb.toString();
        Utils.recycle(sb);

        Context context = mFragment.getContext();
        Intent intent = new Intent(context, NovelActivity.class);
        intent.setData(Uri.parse(data));
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
