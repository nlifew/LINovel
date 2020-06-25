package cn.nlifew.linovel.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.appbar.AppBarLayout;

public class DismissAppBarBehavior extends AppBarLayout.Behavior {
    private static final String TAG = "DismissAppBarBehavior";

    public DismissAppBarBehavior() {
        super();
    }

    public DismissAppBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private DismissBehavior mDismissBehavior;

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout child, @NonNull View dependency) {
        if (dependency instanceof DismissView) {
            if (mDismissBehavior == null) {
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)
                        dependency.getLayoutParams();
                mDismissBehavior = (DismissBehavior) lp.getBehavior();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent,
                                          @NonNull AppBarLayout child,
                                          @NonNull View dependency) {
        final float translationY = dependency.getHeight() + dependency.getTranslationY();
        child.setTranslationY(translationY);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent,
                                         @NonNull AppBarLayout child,
                                         @NonNull MotionEvent ev) {
        final boolean b = super.onInterceptTouchEvent(parent, child, ev);

        final int action = ev.getActionMasked();

//        Log.d(TAG, "onInterceptTouchEvent: " + action + " " + b);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = (int) ev.getY();
                mActivePointerId = ev.getPointerId(0);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mActivePointerId = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                if (b) {
                    // 如果父类返回了 true，之后的所有事件会直接分发给 onTouchEvent()
                    // 但此时 onTouchEvent() 是不会接收到 ACTION_DOWN 事件的，
                    // 所以我们必须完成必要的初始化
                    mLastMotionY = (int) ev.getY();
                    child.startNestedScroll(View.SCROLL_AXIS_VERTICAL);
                }
                break;
        }
        return b;
    }


    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent,
                                @NonNull AppBarLayout child,
                                @NonNull MotionEvent ev) {
//        Log.d(TAG, "onTouchEvent: " + ev.getActionMasked());

        final int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_MOVE) {
            return onMoveEvent(parent, child, ev);
        }

        boolean b = super.onTouchEvent(parent, child, ev);

        if (! b) {
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = (int) ev.getY();
                mActivePointerId = ev.getPointerId(0);
                child.startNestedScroll(View.SCROLL_AXIS_VERTICAL);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                child.stopNestedScroll();
                mActivePointerId = -1;
                break;
        }
        return true;
    }

    private int mLastMotionY;
    private int mActivePointerId = -1;
    private final int[] mScrollConsumed = new int[2];
    private final int[] mScrollOffset = new int[2];

    private boolean onMoveEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        int activePointerIndex;
        if (mActivePointerId == -1 || (activePointerIndex = ev.findPointerIndex(mActivePointerId)) == -1) {
            return super.onTouchEvent(parent, child, ev);
        }

        final int y = (int) ev.getY(activePointerIndex);
        int dy = mLastMotionY - y;
        int dyConsumed = 0;

        child.dispatchNestedPreScroll(0, dy, mScrollConsumed, mScrollOffset);

        mLastMotionY = y;

//        Log.d(TAG, "onMoveEvent: dy=" + dy + ", consumedDy=" + mScrollConsumed[1]);

        dyConsumed += mScrollConsumed[1];

        if (Math.abs(dyConsumed) < Math.abs(dy)) {
            // 滑动事件未被父 View 全部消耗，我们交给 AppBarLayout 处理
//            MotionEvent fakeEvent = MotionEvent.obtain(ev);
//            fakeEvent.offsetLocation(0, mTotalConsumedY);
//            super.onTouchEvent(parent, child, fakeEvent);

            super.onNestedPreScroll(parent, child, child, 0, dy - dyConsumed, mScrollConsumed, ViewCompat.TYPE_TOUCH);
            dyConsumed += mScrollConsumed[1];

            // 如果是下拉事件，AppBarLayout 又没有消费，我们可能需要展开 AppBarLayout
            if (dy < 0 && mScrollConsumed[1] == 0) {
                super.onNestedScroll(parent, child, child, 0, 0,
                        0, dy -  dyConsumed, ViewCompat.TYPE_TOUCH,
                        mScrollConsumed);
                dyConsumed += mScrollConsumed[1];
            }
        }
        if (Math.abs(dyConsumed) < Math.abs(dy)) {
            child.dispatchNestedScroll(0, dyConsumed,
                    0, dy - dyConsumed,
                    mScrollOffset);
        }

        return true;
    }


    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout parent,
                                       @NonNull AppBarLayout child,
                                       @NonNull View directTargetChild,
                                       View target, int nestedScrollAxes, int type) {
        if (directTargetChild == child) {
            return false;
        }
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout,
                                  @NonNull AppBarLayout child,
                                  View target,
                                  int dx, int dy,
                                  int[] consumed,
                                  int type) {
//        Log.d(TAG, "onNestedPreScroll: dy=" + dy + ", dyConsumed=" + consumed[1]);

        if (mDismissBehavior != null && mDismissBehavior.mBlockOthersPreScroll) {
            return;
        }
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout,
                               @NonNull AppBarLayout child,
                               View target,
                               int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed,
                               int type, int[] consumed) {
//        Log.d(TAG, "onNestedScroll: start");
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);

        if (mDismissBehavior != null) {
            mDismissBehavior.onAppBarPostNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
        }
    }
}
