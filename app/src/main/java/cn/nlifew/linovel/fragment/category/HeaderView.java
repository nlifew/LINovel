package cn.nlifew.linovel.fragment.category;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.utils.DisplayUtils;
import cn.nlifew.xqdreader.bean.category.CategoryBean;

class HeaderView extends LinearLayout {
    private static final int TYPE_UNKNOWN       = 0;
    private static final int TYPE_SITE_VIEW     = 1;
    private static final int TYPE_FILTER_VIEW   = 2;
    private static final int TYPE_ORDER_VIEW    = 3;
    private static final int TYPE_UNION_VIEW    = 4;

    HeaderView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setDividerDrawable(context.getDrawable(R.drawable.divider_horizontal));
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

    private void updateViews() {
        removeAllViews();
        if (mHeaderData == null) {
            return;
        }

        final Context context = getContext();
        final int dp30 = DisplayUtils.dp2px(30);

        if (mHeaderData.SiteList != null) {
            HorizontalListView view = new HorizontalListView(context);
            for (int i = 0; i < mHeaderData.SiteList.length; i++) {
                CategoryBean.SiteType site = mHeaderData.SiteList[i];
                view.appendItem(site.Name, site.Id);
                if (site.Id == mHeaderData.SiteType) {
                    view.setSelectedItem(i);
                }
            }
            view.setTag(R.id.view_tag_1, "site");
            view.setTag(R.id.view_tag_2, TYPE_SITE_VIEW);
            view.setOnItemSelectListener(this::performChangeItem);
            view.commit();
            addView(view, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp30
            ));
        }
        if (mHeaderData.FiltrLines != null) {
            for (CategoryBean.FilterLineType filter : mHeaderData.FiltrLines) {
                HorizontalListView view = new HorizontalListView(context);
                for (CategoryBean.FilterUnionType union : filter.FilterUnions) {
                    view.appendItem(union.Name, union);
                }
                // 可能需要更新二级菜单
                view.setTag(R.id.view_tag_1, filter.Tag);
                view.setTag(R.id.view_tag_2, TYPE_FILTER_VIEW);
                view.setOnItemSelectListener(this::updateExtMenu);
                view.commit();
                addView(view, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp30
                ));
            }
        }
        if (mHeaderData.Orders != null) {
            HorizontalListView view = new HorizontalListView(context);
            for (CategoryBean.OrderType order : mHeaderData.Orders) {
                view.appendItem(order.Name, order.Id);
            }
            view.setTag(R.id.view_tag_1, "order");
            view.setTag(R.id.view_tag_2, TYPE_ORDER_VIEW);
            view.setOnItemSelectListener(this::performChangeItem);
            view.commit();
            addView(view, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp30
            ));
        }
    }

    private void performChangeItem(HorizontalListView view, Object id) {
        if (mItemSelectListener != null) {
            String key = (String) view.getTag(R.id.view_tag_1);
            mItemSelectListener.onItemChanged(key, id.toString());
        }
    }

    private void updateExtMenu(HorizontalListView v, Object tag) {
        final int curViewIndex = getChildIndex(v);
        final int nextViewType = (int) getChildAt( curViewIndex + 1)
                .getTag(R.id.view_tag_2);

        final CategoryBean.FilterUnionType union = (CategoryBean.FilterUnionType) tag;

        if (union.Extvalue != null && nextViewType != TYPE_UNION_VIEW) {
            // 如果当前选中的有二级菜单，但之前的没有，添加一个
            HorizontalListView view = new HorizontalListView(getContext());
            for (CategoryBean.ExtValueType extValue : union.Extvalue) {
                view.appendItem(extValue.Name, extValue.Id);
            }
            view.setTag(R.id.view_tag_1, union.SubTag);
            view.setTag(R.id.view_tag_2, TYPE_UNION_VIEW);
            view.setOnItemSelectListener(this::performChangeItem);
            view.commit();
            view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            addView(view, curViewIndex + 1);
        }
        else if (union.Extvalue != null) {
            // 如果当前选中的有二级菜单，之前的也有，更新它
            HorizontalListView view = (HorizontalListView) getChildAt(curViewIndex + 1);
            view.clearItems();
            for (CategoryBean.ExtValueType extValue : union.Extvalue) {
                view.appendItem(extValue.Name, extValue.Id);
            }
            view.setTag(R.id.view_tag_1, union.SubTag);
            view.commit();
        }
        else if (nextViewType == TYPE_UNION_VIEW) {
            // 如果当前选中的没有二级菜单，但之前的有，移除掉
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

    interface OnItemChangedListener {
        void onItemChanged(String key, String value);
    }

    private OnItemChangedListener mItemSelectListener;

    void setOnItemChangedListener(OnItemChangedListener listener) {
        mItemSelectListener = listener;
    }
}
