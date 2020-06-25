package cn.nlifew.linovel.fragment.category;

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

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.ui.novel.NovelActivity;
import cn.nlifew.linovel.utils.DisplayUtils;
import cn.nlifew.xqdreader.bean.category.CategoryBean;
import cn.nlifew.xqdreader.bean.category.CategoryBookBean;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import cn.nlifew.xqdreader.utils.Utils;

import static cn.nlifew.linovel.fragment.category.DataSetWrapper.TYPE_BOOK_ITEM;
import static cn.nlifew.linovel.fragment.category.DataSetWrapper.TYPE_FILTER_ITEM;
import static cn.nlifew.linovel.fragment.category.DataSetWrapper.TYPE_ORDER_ITEM;
import static cn.nlifew.linovel.fragment.category.DataSetWrapper.TYPE_SITE_ITEM;
import static cn.nlifew.linovel.fragment.category.DataSetWrapper.TYPE_SUB_FILTER_ITEM;

class RecyclerAdapterImpl extends RecyclerView.Adapter {
    private static final String TAG = "RecyclerAdapterImpl";

    interface OnItemSelectListener {
        void onItemSelect(String key, String value);
    }

    RecyclerAdapterImpl(Fragment fragment) {
        mFragment = fragment;
        if (! (fragment instanceof OnItemSelectListener)) {
            throw new UnsupportedOperationException("This Fragment must implements OnCategorySelectListener");
        }
        mCategoryCallback = (OnItemSelectListener) fragment;
    }

    private final Fragment mFragment;
    private final DataSetWrapper mDataSet = new DataSetWrapper();
    private final OnItemSelectListener mCategoryCallback;

    void updateBookList(CategoryBookBean.BookType[] books) {
        int old = mDataSet.getCategoryItemCount();
        mDataSet.updateBooks(books);
        notifyItemRangeChanged(old, books.length);
    }

    void appendBookList(CategoryBookBean.BookType[] books) {
        int old = getItemCount();
        mDataSet.appendBooks(books);
        notifyItemRangeChanged(old, books.length);
    }

    void updateCategory(CategoryBean.DataType data) {
        mDataSet.updateCategory(data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataSet.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.getItemType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_SITE_ITEM:
            case TYPE_FILTER_ITEM:
            case TYPE_SUB_FILTER_ITEM:
            case TYPE_ORDER_ITEM: {
                HorizontalListView view = new HorizontalListView(mFragment.getContext());
                ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        DisplayUtils.dp2px(30)
                );
                lp.leftMargin = lp.rightMargin = lp.topMargin = lp.bottomMargin
                        = DisplayUtils.dp2px(10);
                view.setLayoutParams(lp);
                return new CategoryHolder(view);
            }
            case TYPE_BOOK_ITEM: {
                View view = LayoutInflater.from(mFragment.getContext())
                        .inflate(R.layout.fragment_home_vertical_item, parent, false);
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)
                        view.getLayoutParams();
                lp.leftMargin = DisplayUtils.dp2px(20);
                lp.rightMargin = DisplayUtils.dp2px(15);
                lp.topMargin = lp.bottomMargin = DisplayUtils.dp2px(8);
                return new BookHolder(view);
            }
        }
        throw new UnsupportedOperationException("unknown ItemType: " + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int type = mDataSet.getItemType(position);

        switch (type) {
            case TYPE_SITE_ITEM:
                ((CategoryHolder) holder).onBindSiteType(position);
                break;
            case TYPE_FILTER_ITEM:
                ((CategoryHolder) holder).onBindFilterType(position);
                break;
            case TYPE_SUB_FILTER_ITEM:
                ((CategoryHolder) holder).onBindSubFilterType(position);
                break;
            case TYPE_ORDER_ITEM:
                ((CategoryHolder) holder).onBindOrderType(position);
                break;
            case TYPE_BOOK_ITEM:
                ((BookHolder) holder).onBindBook(position);
                break;
        }
    }

    private final class CategoryHolder extends RecyclerView.ViewHolder {
        CategoryHolder(@NonNull View itemView) {
            super(itemView);
            mView = (HorizontalListView) itemView;
        }
        private final HorizontalListView mView;

        void onBindSiteType(int position) {
            CategoryBean.SiteType[] data = mDataSet.getItem(position);
            if (data == mView.getTag()) {
                return;
            }

            final int curSiteType = mDataSet.currentSiteType();

            mView.setTag(data);
            mView.clearItems();
            for (int i = 0; i < data.length; i++) {
                CategoryBean.SiteType site = data[i];
                mView.appendItem(site.Name, Integer.toString(site.Id));
                if (site.Id == curSiteType) {
                    mView.setSelectedItem(i);
                }
            }
            mView.setOnItemSelectListener((v, tag) ->
                mCategoryCallback.onItemSelect("site", (String) tag)
            );
            mView.commit();
        }

        void onBindOrderType(int position) {
            CategoryBean.OrderType[] data = mDataSet.getItem(position);
            if (data == mView.getTag()) {
                return;
            }
            mView.setTag(data);
            mView.clearItems();
            for (CategoryBean.OrderType order : data) {
                mView.appendItem(order.Name, Integer.toString(order.Id));
            }
            mView.setOnItemSelectListener((v, tag) ->
                    mCategoryCallback.onItemSelect("order", (String) tag)
            );
            mView.commit();
        }

        void onBindFilterType(final int position) {
            CategoryBean.FilterLineType data = mDataSet.getItem(position);
            if (data == mView.getTag()) {
                return;
            }

            mView.setTag(data);
            mView.clearItems();
            for (CategoryBean.FilterUnionType union : data.FilterUnions) {
                mView.appendItem(union.Name, union);
            }

            mView.setOnItemSelectListener((v, tag) -> {
                CategoryBean.FilterUnionType union = (CategoryBean.FilterUnionType) tag;
                mCategoryCallback.onItemSelect(data.Tag, Integer.toString(union.Id));

                int myPosition = getAdapterPosition();
                int nextItemType = mDataSet.getItemType(myPosition + 1);
                if (union.Extvalue == null && nextItemType == TYPE_SUB_FILTER_ITEM) {
                    // 如果当前选中的选项没有二级菜单，但之前的有，移除掉
                    mDataSet.removeCategoryItem(myPosition + 1);
                    notifyItemRemoved(myPosition + 1);
                }
                else if (union.Extvalue != null && nextItemType != TYPE_SUB_FILTER_ITEM) {
                    // 如果当前选中的选项有二级菜单，但之前的没有，新增一个
                    mDataSet.insertCategoryItem(myPosition + 1, union.Extvalue);
                    notifyItemInserted(myPosition + 1);
                }
                else if (union.Extvalue != null) {
                    // 如果之前选中的选项有二级菜单，之前的也有，修改它
                    mDataSet.setCategoryItem(myPosition + 1, union.Extvalue);
                    notifyItemChanged(myPosition + 1);
                }
            });
            mView.commit();
        }

        void onBindSubFilterType(int position) {
            CategoryBean.ExtValueType[] data = mDataSet.getItem(position);
            if (data == mView.getTag()) {
                return;
            }

            mView.setTag(data);
            mView.clearItems();
            for (CategoryBean.ExtValueType ext : data) {
                mView.appendItem(ext.Name, Integer.toString(ext.Id));
            }
            mView.setOnItemSelectListener((v, tag) -> {
                int myPosition = getAdapterPosition();
                CategoryBean.FilterLineType line = mDataSet.getItem(myPosition - 1);
                mCategoryCallback.onItemSelect(line.Tag, (String) tag);
            });
            mView.commit();
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

        private final TextView mTitleView;
        private final TextView mAboutView;
        private final TextView mDescriptionView;
        private final ImageView mCoverView;

        void onBindBook(int position) {
            final CategoryBookBean.BookType book = mDataSet.getItem(position);

            mTitleView.setText(book.BookName);
            mDescriptionView.setText(book.Description);
            mAboutView.setText(getBookAbout(book));

            String cover = NetworkUtils.novelCoverImage(book.BookId);
            Glide.get(mFragment.getContext())
                    .getRequestManagerRetriever()
                    .get(mFragment)
                    .asBitmap()
                    .load(cover)
                    .into(mCoverView);

            itemView.setOnClickListener(v -> {
                String uri = "linovel://novel?id=" + book.BookId
                        + "&title=" + book.BookName
                        + "&outOfBook=0";
                Context context = mFragment.getContext();
                Intent intent = new Intent(context, NovelActivity.class);
                intent.setData(Uri.parse(uri));
                context.startActivity(intent);
            });
        }
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
