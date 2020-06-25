package cn.nlifew.linovel.fragment.category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.nlifew.xqdreader.bean.category.CategoryBean;
import cn.nlifew.xqdreader.bean.category.CategoryBookBean;

class DataSetWrapper {
    private static final String TAG = "DataSetWrapper";

    static final int TYPE_UNKNOWN           =   0;
    static final int TYPE_SITE_ITEM         =   1;
    static final int TYPE_FILTER_ITEM       =   2;
    static final int TYPE_SUB_FILTER_ITEM   =   3;
    static final int TYPE_ORDER_ITEM        =   4;
    static final int TYPE_BOOK_ITEM         =   5;

    private int mCurrentSiteType;
    private final List<Object> mCategoryItemList = new ArrayList<>(8);
    private final List<CategoryBookBean.BookType> mBookList = new ArrayList<>(64);

    int getCategoryItemCount() {
        return mCategoryItemList.size();
    }

    int getItemCount() {
        return mCategoryItemList.size() + mBookList.size();
    }

    int getItemType(int position) {
        int m = mCategoryItemList.size();
        int n = mBookList.size();

        if (position >= m + n) {
            return TYPE_UNKNOWN;
        }
        if (position >= m) {
            return TYPE_BOOK_ITEM;
        }

        final Class<?> cls = mCategoryItemList.get(position).getClass();
        if (cls == CategoryBean.SiteType[].class) {
            return TYPE_SITE_ITEM;
        }
        if (cls == CategoryBean.FilterLineType.class) {
            return TYPE_FILTER_ITEM;
        }
        if (cls == CategoryBean.ExtValueType[].class) {
            return TYPE_SUB_FILTER_ITEM;
        }
        if (cls == CategoryBean.OrderType[].class) {
            return TYPE_ORDER_ITEM;
        }
        return TYPE_UNKNOWN;
    }

    @SuppressWarnings("unchecked")
    <T> T getItem(int position) {
        int n = mCategoryItemList.size();
        return position < n ?
                (T) mCategoryItemList.get(position) :
                (T) mBookList.get(position - n);
    }

    void insertCategoryItem(int index, Object obj) {
        mCategoryItemList.add(index, obj);
    }

    void setCategoryItem(int index, Object obj) {
        mCategoryItemList.set(index, obj);
    }

    void removeCategoryItem(int index) {
        mCategoryItemList.remove(index);
    }

    void updateCategory(CategoryBean.DataType data) {
        mCategoryItemList.clear();

        if (data.SiteList != null) {
            mCategoryItemList.add(data.SiteList);
        }
        for (int i = 0, n = data.FiltrLines == null ? 0 : data.FiltrLines.length; i < n; i++) {
            if (data.FiltrLines[i] != null) {
                mCategoryItemList.add(data.FiltrLines[i]);
            }
        }
        if (data.Orders != null) {
            mCategoryItemList.add(data.Orders);
        }
        mCurrentSiteType = data.SiteType;
    }

    void updateBooks(CategoryBookBean.BookType[] books) {
        mBookList.clear();
        mBookList.addAll(Arrays.asList(books));
    }

    int currentSiteType() {
        return mCurrentSiteType;
    }

    void appendBooks(CategoryBookBean.BookType[] books) {
        mBookList.addAll(Arrays.asList(books));
    }
}
