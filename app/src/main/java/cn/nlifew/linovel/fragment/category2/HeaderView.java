package cn.nlifew.linovel.fragment.category2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.utils.DisplayUtils;
import cn.nlifew.xqdreader.bean.category.CategoryBean;

class HeaderView extends LinearLayout {
    private static final String TAG = "HeaderView";

    private static final int TYPE_UNKNOWN       = 0;
    private static final int TYPE_SITE_VIEW     = 1;
    private static final int TYPE_FILTER_VIEW   = 2;
    private static final int TYPE_ORDER_VIEW    = 3;
    private static final int TYPE_UNION_VIEW    = 4;

    HeaderView(Context context) {
        super(context);
        setOrientation(VERTICAL);
    }

    private CategoryBean.DataType mHeaderData;

    void setCategoryHeader(CategoryBean.DataType data) {
        if (mHeaderData != data) {
            mHeaderData = data;
            updateViews();
        }
    }

    CategoryBean.DataType getCategoryHeader() {
        return mHeaderData;
    }

    private ArrayList<HorizontalListView> mCacheViews;

    private void cacheChildViews() {
        int n = getChildCount();
        if (n != 0 && mCacheViews == null) {
            mCacheViews = new ArrayList<>(n);
        }

        HorizontalListView view;

        for (int i = 0; i < n; i++) {
            view = (HorizontalListView) getChildAt(i);
            view.clear();
            mCacheViews.add(view);
        }
    }

    private HorizontalListView obtainChildView() {
        int n;
        if (mCacheViews == null || (n = mCacheViews.size()) == 0) {
            return new HorizontalListView(getContext());
        }
        return mCacheViews.remove(n - 1);
    }

    private void updateViews() {
        cacheChildViews();
        removeAllViews();

        if (mHeaderData == null) {
            return;
        }

        final int dp30 = DisplayUtils.dp2px(30);

        if (mHeaderData.SiteList != null) {
            HorizontalListView view = obtainChildView();
            int selectedItem = -1;

            for (int i = 0; i < mHeaderData.SiteList.length; i++) {
                CategoryBean.SiteType site = mHeaderData.SiteList[i];
                view.appendItem(site.Name, site.Id);
                if (site.Id == mHeaderData.SiteType) {
                    selectedItem = i;
                }
            }
            view.commit();

            view.setSelectedItem(selectedItem);
            view.setOnItemSelectListener(this::performChangeItem);
            view.setTag(R.id.view_tag_1, "site");
            view.setTag(R.id.view_tag_2, TYPE_SITE_VIEW);
            addView(view, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp30
            ));
        }
        if (mHeaderData.FiltrLines != null) {
            for (CategoryBean.FilterLineType filter : mHeaderData.FiltrLines) {
                HorizontalListView view = obtainChildView();
                int selectedItem = -1;
                for (int i = 0; i < filter.FilterUnions.length; i++) {
                    CategoryBean.FilterUnionType union = filter.FilterUnions[i];
                    view.appendItem(union.Name, union);
                    if (filter.UnionType == union.Id) {
                        selectedItem = i;
                    }
                }
                view.commit();
                view.setSelectedItem(selectedItem);

                // 可能需要更新二级菜单
                view.setOnItemSelectListener(this::updateExtMenu);

                view.setTag(R.id.view_tag_1, filter.Tag);
                view.setTag(R.id.view_tag_2, TYPE_FILTER_VIEW);
                addView(view, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp30
                ));
            }
        }
        if (mHeaderData.Orders != null) {
            HorizontalListView view = obtainChildView();
            int selectedItem = -1;
            for (int i = 0; i < mHeaderData.Orders.length; i++) {
                CategoryBean.OrderType order = mHeaderData.Orders[i];
                view.appendItem(order.Name, order.Id);
                if (mHeaderData.OrderType == order.Id) {
                    selectedItem = i;
                }
            }
            view.commit();
            view.setSelectedItem(selectedItem);
            view.setOnItemSelectListener(this::performChangeItem);

            view.setTag(R.id.view_tag_1, "order");
            view.setTag(R.id.view_tag_2, TYPE_ORDER_VIEW);
            addView(view, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp30
            ));
        }
    }

    private void performChangeItem(HorizontalListView view, Object id) {
        if (mItemSelectListener != null) {
            String key = (String) view.getTag(R.id.view_tag_1);
            mItemSelectListener.onItemSelect(key, id.toString());
        }
    }

    private void updateExtMenu(HorizontalListView v, Object tag) {
        final int curViewIndex = getChildIndex(v);
        final int nextViewType = (int) getChildAt( curViewIndex + 1)
                .getTag(R.id.view_tag_2);

        final CategoryBean.FilterUnionType union = (CategoryBean.FilterUnionType) tag;

        if (union.Extvalue != null && nextViewType != TYPE_UNION_VIEW) {
            // 如果当前选中的有二级菜单，但之前的没有，添加一个
            HorizontalListView view = obtainChildView();
            int selectedItem = -1;
            for (int i = 0; i < union.Extvalue.length; i++) {
                CategoryBean.ExtValueType extValue = union.Extvalue[i];
                view.appendItem(extValue.Name, extValue.Id);
                if (union.ExtType == extValue.Id) {
                    selectedItem = i;
                }
            }

            view.commit();
            view.setSelectedItem(selectedItem);
            view.setOnItemSelectListener(this::performChangeItem);

            view.setTag(R.id.view_tag_1, union.SubTag);
            view.setTag(R.id.view_tag_2, TYPE_UNION_VIEW);
            view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            addView(view, curViewIndex + 1);
        }
        else if (union.Extvalue != null) {
            // 如果当前选中的有二级菜单，之前的也有，更新它
            HorizontalListView view = (HorizontalListView) getChildAt(curViewIndex + 1);
            view.clear();
            int selectedItem = -1;
            for (int i = 0; i < union.Extvalue.length; i++) {
                CategoryBean.ExtValueType extValue = union.Extvalue[i];
                view.appendItem(extValue.Name, extValue.Id);
                if (union.ExtType == extValue.Id) {
                    selectedItem = i;
                }
            }
            view.commit();
            view.setSelectedItem(selectedItem);
            view.setTag(R.id.view_tag_1, union.SubTag);
        }
        else if (nextViewType == TYPE_UNION_VIEW) {
            // 如果当前选中的没有二级菜单，但之前的有，移除掉
            HorizontalListView view = (HorizontalListView) getChildAt(curViewIndex + 1);
            view.clear();
            if (mCacheViews == null) {
                mCacheViews = new ArrayList<>(4);
            }
            mCacheViews.add(view);
            removeViewAt(curViewIndex + 1);
        }

        performChangeItem(v, union.Id);
    }

    private int getChildIndex(View view) {
        for (int i = 0, n = getChildCount(); i < n; i++) {
            if (view == getChildAt(i)) {
                return i;
            }
        }
        return -1;
    }

    interface OnItemSelectListener {
        void onItemSelect(String key, String value);
    }

    private OnItemSelectListener mItemSelectListener;

    void setOnItemSelectListener(OnItemSelectListener listener) {
        mItemSelectListener = listener;
    }
}
