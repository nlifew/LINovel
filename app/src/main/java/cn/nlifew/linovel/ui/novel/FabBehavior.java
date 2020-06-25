package cn.nlifew.linovel.ui.novel;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import cn.nlifew.linovel.utils.DisplayUtils;

public class FabBehavior extends CoordinatorLayout.Behavior<FloatingActionsMenu> {
    public FabBehavior() {
        super();
    }

    public FabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull FloatingActionsMenu child, @NonNull MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN
                && child.isExpanded()
                && ! parent.isPointInChildBounds(child, x, y)) {
            child.collapse();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull FloatingActionsMenu child, @NonNull MotionEvent ev) {
        if (child.isExpanded()) {
            child.collapse();
        }
        return false;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                       @NonNull FloatingActionsMenu child,
                                       @NonNull View directTargetChild,
                                       @NonNull View target, int axes, int type) {
        return (axes & View.SCROLL_AXIS_VERTICAL) != 0;
    }

    private float mMaxY, mCurY;

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                               @NonNull FloatingActionsMenu child,
                               @NonNull View target,
                               int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed,
                               int type, @NonNull int[] consumed) {
        if (mMaxY == 0) {
            // 只针对上下滚动 RecyclerView 适配
            mMaxY = coordinatorLayout.getHeight()
                    - child.getY() - child.getHeight()
                    + DisplayUtils.dp2px(54 + 20);
        }

        if (mCurY >= mMaxY && dyConsumed >= 0 || mCurY <= 0 && dyConsumed <= 0) {
            // 可能超出范围 ?
            return;
        }
        mCurY = Math.min(mMaxY, mCurY + dyConsumed);
        child.setTranslationY(mCurY);
    }
}