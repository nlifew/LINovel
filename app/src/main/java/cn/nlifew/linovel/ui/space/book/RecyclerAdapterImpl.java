package cn.nlifew.linovel.ui.space.book;

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
import cn.nlifew.xqdreader.bean.user.UserBean;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import cn.nlifew.xqdreader.utils.Utils;

class RecyclerAdapterImpl extends RecyclerView.Adapter {
    private static final String TAG = "RecyclerAdapterImpl";

    private static final int TYPE_BOOK_ITEM = 1;
    private static final int TYPE_TAIL_ITEM = 2;

    RecyclerAdapterImpl(Fragment fragment) {
        mFragment = fragment;
        mDataSet = new ArrayList<>(16);
    }

    private final Fragment mFragment;
    private final List<UserBean.AuthorBookItemType> mDataSet;

    void updateDataSet(UserBean.AuthorBookType book) {
        mDataSet.clear();
        mDataSet.addAll(Arrays.asList(book.BookList));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mFragment.getContext())
                .inflate(R.layout.fragment_space_book_item, parent, false);
        return new BookHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        final BookHolder holder = (BookHolder) h;
        final UserBean.AuthorBookItemType book = mDataSet.get(position);

        holder.mTitleView.setText(book.BookName);
        holder.mSummaryView.setText(book.Description);
        holder.mAboutView.setText(getBookAbout(book));

        String url = NetworkUtils.novelCoverImage(book.BookId);
        Glide.get(mFragment.getContext())
                .getRequestManagerRetriever()
                .get(mFragment)
                .asBitmap()
                .load(url)
                .into(holder.mCoverView);
    }

    private static final class BookHolder extends RecyclerView.ViewHolder {
        BookHolder(@NonNull View itemView) {
            super(itemView);
            mCoverView = itemView.findViewById(R.id.fragment_space_book_item_cover);
            mTitleView = itemView.findViewById(R.id.fragment_space_book_item_title);
            mAboutView = itemView.findViewById(R.id.fragment_space_book_item_about);
            mSummaryView = itemView.findViewById(R.id.fragment_space_book_item_description);
        }
        final TextView mSummaryView;
        final TextView mAboutView;
        final TextView mTitleView;
        final ImageView mCoverView;
    }

//    private static final class TailHolder extends RecyclerView.ViewHolder {
//        TailHolder(@NonNull View itemView) {
//            super(itemView);
//        }
//    }

    private static String getBookAbout(UserBean.AuthorBookItemType book) {
        StringBuilder sb = Utils.obtainStringBuilder(32)
                .append(book.CategoryName).append(" - ")
                .append(book.BookStatus).append(" - ");
        if (book.WordsCount > 10000) {
            sb.append(book.WordsCount / 10000).append("万字");
        } else {
            sb.append(book.WordsCount).append("字");
        }
        String s = sb.toString();
        Utils.recycle(sb);
        return s;
    }
}
