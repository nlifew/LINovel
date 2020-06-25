package cn.nlifew.linovel.fragment.category;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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
import java.util.Objects;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.ui.novel.NovelActivity;
import cn.nlifew.linovel.utils.DisplayUtils;
import cn.nlifew.xqdreader.bean.category.CategoryBean;
import cn.nlifew.xqdreader.bean.category.CategoryBookBean;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import cn.nlifew.xqdreader.utils.Utils;

class RecyclerAdapterImpl2 extends RecyclerView.Adapter {
    private static final String TAG = "RecyclerAdapterImpl2";

    private static final int TYPE_BOOK_VIEW = 0;
    private static final int TYPE_HEADER_VIEW = 1;


    RecyclerAdapterImpl2(Fragment fragment) {
        mFragment = fragment;
        if (! (fragment instanceof HeaderView.OnItemChangedListener)) {
            throw new UnsupportedOperationException("Fragment must implement OnItemChangedListener");
        }
    }

    private final Fragment mFragment;
    private CategoryBean.DataType mHeaderData;
    private final List<CategoryBookBean.BookType> mBookList = new ArrayList<>(32);

    void updateHeader(CategoryBean.DataType data) {
         mHeaderData = data;
        mBookList.clear();
        notifyDataSetChanged();
    }

    void updateBookList(CategoryBookBean.BookType[] books) {
        mBookList.clear();
        mBookList.addAll(Arrays.asList(books));
        int old = mHeaderData == null ? 0 : 1;
        notifyItemRangeChanged(old, books.length);
    }

    void appendBookList(CategoryBookBean.BookType[] books) {
        int old = getItemCount();
        mBookList.addAll(Arrays.asList(books));
        notifyItemRangeChanged(old, books.length);
    }

    @Override
    public int getItemCount() {
        return mBookList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER_VIEW : TYPE_BOOK_VIEW;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = mFragment.getContext();

        if (viewType == TYPE_HEADER_VIEW) {
            HeaderView view = new HeaderView(context);
            view.setOnItemChangedListener((HeaderView.OnItemChangedListener) mFragment);
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.leftMargin = lp.rightMargin = DisplayUtils.dp2px(15);
            view.setLayoutParams(lp);
            return new Holder(view);
        }
        View view = LayoutInflater.from(context)
                .inflate(R.layout.fragment_home_vertical_item, parent, false);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)
                view.getLayoutParams();
        lp.leftMargin = DisplayUtils.dp2px(20);
        lp.rightMargin = DisplayUtils.dp2px(15);
        lp.topMargin = lp.bottomMargin = DisplayUtils.dp2px(8);
        return new BookHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        final int type = getItemViewType(position);
        if (type == TYPE_HEADER_VIEW) {
            HeaderView view = (HeaderView) h.itemView;
            CategoryBean.DataType old = view.getCategoryHeader();

            final long t0 = System.currentTimeMillis();
            if (! Objects.equals(old, mHeaderData)) {
                view.setCategoryHeader(mHeaderData);
            }
            final long t1 = System.currentTimeMillis();
            Log.d(TAG, "onBindViewHolder: cost: " + (t1 - t0) + " ms.");

            return;
        }

        BookHolder holder = ((BookHolder) h);
        CategoryBookBean.BookType book = mBookList.get(position - 1);

        holder.mTitleView.setText(book.BookName);
        holder.mDescriptionView.setText(book.Description);
        holder.mAboutView.setText(getBookAbout(book));

        String cover = NetworkUtils.novelCoverImage(book.BookId);
        Glide.get(mFragment.getContext())
                .getRequestManagerRetriever()
                .get(mFragment)
                .asBitmap()
                .load(cover)
                .into(holder.mCoverView);

        holder.itemView.setOnClickListener(v -> {
            String uri = "linovel://novel?id=" + book.BookId
                    + "&title=" + book.BookName
                    + "&outOfBook=0";
            Context context = mFragment.getContext();
            Intent intent = new Intent(context, NovelActivity.class);
            intent.setData(Uri.parse(uri));
            context.startActivity(intent);
        });
    }


    private static final class Holder extends RecyclerView.ViewHolder {
        Holder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private final class BookHolder extends RecyclerView.ViewHolder {
        BookHolder(@NonNull View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.fragment_home_vertical_item_title);
            mAboutView = itemView.findViewById(R.id.fragment_home_vertical_item_about);
            mDescriptionView = itemView.findViewById(R.id.fragment_home_vertical_item_description);
            mCoverView = itemView.findViewById(R.id.fragment_home_vertical_item_cover);
        }

        final TextView mTitleView;
        final TextView mAboutView;
        final TextView mDescriptionView;
        final ImageView mCoverView;
    }

    private static String getBookAbout(CategoryBookBean.BookType book) {
        StringBuilder sb = Utils.obtainStringBuilder(64)
                .append(book.AuthorName).append(" - ")
                .append(book.CategoryName).append(" - ")
                .append(book.ActionStatusString).append(" - ");
        if (book.WordsCount > 10000) {
            sb.append(book.WordsCount / 10000).append("万字");
        } else {
            sb.append('字');
        }
        String s = sb.toString();
        Utils.recycle(sb);
        return s;
    }
}
