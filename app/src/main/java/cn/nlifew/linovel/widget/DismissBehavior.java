package cn.nlifew.linovel.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.appbar.AppBarLayout;

import cn.nlifew.linovel.widget.DismissView;

import static cn.nlifew.linovel.widget.DismissView.OnStateChangedListener.STATE_FINISH_COLLAPSE;
import static cn.nlifew.linovel.widget.DismissView.OnStateChangedListener.STATE_FINISH_EXPAND;
import static cn.nlifew.linovel.widget.DismissView.OnStateChangedListener.STATE_PREPARE_COLLAPSE;
import static cn.nlifew.linovel.widget.DismissView.OnStateChangedListener.STATE_PREPARE_EXPAND;

@SuppressWarnings("unused")
public class DismissBehavior extends CoordinatorLayout.Behavior<DismissView> {
    private static final String TAG = "DismissBehavior";

    public DismissBehavior() {
        super();
    }

    public DismissBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull DismissView child, @NonNull MotionEvent ev) {
        return mIsAnimation;
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull DismissView child, @NonNull MotionEvent ev) {
        if (mIsAnimation) {
            return true;
        }
        final int action = ev.getActionMasked();
        Log.d(TAG, "onTouchEvent: " + action);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int x = (int) ev.getX();
                final int y = (int) ev.getY();
                if (! parent.isPointInChildBounds(child, x, y)) {
                    return false;
                }
                mLastMotionY = y;
                mLastDownY = y;
                mLastDownTime = System.currentTimeMillis();
                mActivePointerId = ev.getPointerId(0);
                mLastTranslationY = (int) child.getTranslationY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final int activeIndex;
                if (mActivePointerId == -1 || (activeIndex = ev.findPointerIndex(mActivePointerId)) == -1) {
                    return false;
                }
                final int y = (int) ev.getY(activeIndex);
                int dy = mLastMotionY - y;
                mLastMotionY = y;
//                Log.d(TAG, "onTouchEvent: dy=" + dy);
                scroll(parent, child, dy);
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                int activeIndex;
                if (mActivePointerId != -1 && (activeIndex =
                        ev.findPointerIndex(mActivePointerId)) != -1) {
                    // 先判断是否需要触发点击事件
                    final int y = (int) ev.getY(activeIndex);
                    final long time = System.currentTimeMillis();
                    if (child.mSupportClickListener != null
                            && y == mLastDownY
                            && time - mLastDownTime <= 50) {
                        child.post(() -> child.mSupportClickListener.onClick(child));
                    }
                    else {
                        startAnimation(parent, child);
                    }
                }
                mActivePointerId = -1;
                mLastMotionY = 0;
                break;
        }
        return true;
    }

    private boolean mIsAnimation;
    private int mLastTranslationY;
    private int mActivePointerId = -1;
    private int mLastMotionY;
    private long mLastDownTime;
    private int mLastDownY;

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                       @NonNull DismissView child,
                                       @NonNull View directTargetChild,
                                       @NonNull View target,
                                       int axes,
                                       int type) {
        if (mChildView == null) {
            mChildView = child;
        }
        mLastTranslationY = (int) child.getTranslationY();

        return (axes & View.SCROLL_AXIS_VERTICAL) != 0;
    }

    private int mMaxTranslationY;
    boolean mBlockOthersPreScroll;
    private View mChildView;
    private float mDownTranslationScale = 1;

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout parent,
                                  @NonNull DismissView child,
                                  @NonNull View target,
                                  int dx, int dy,
                                  @NonNull int[] consumed,
                                  int type) {
//        Log.d(TAG, "onNestedPreScroll: dy=" + dy + ", dyConsumed=" + consumed[1]);
        consumed[1] = scroll(parent, child, dy);
        mBlockOthersPreScroll = consumed[1] != 0;
    }

    private int scroll(CoordinatorLayout parent, DismissView child, int dy) {

        if (mMaxTranslationY == 0) {
            mMaxTranslationY = child.getHeight();
        }

        mBlockOthersPreScroll = false;
        int dyConsumed = 0;
        final int translationY = (int) child.getTranslationY();

        if (dy > 0) {
            // 手指向上滑动
            dyConsumed = Math.min(mMaxTranslationY + translationY, dy);
            child.setTranslationY(translationY - dyConsumed);
        }
        else if (dy < 0 && mMaxTranslationY + translationY > 0) {
            // 手指向下滑动，并且 DismissView 仍然可见
            child.setTranslationY(translationY - dy * mDownTranslationScale);
            dyConsumed = dy;
        }
//        Log.d(TAG, "onNestedPreScroll: dy=" + dy + ", dyConsumed=" + consumed[1]);

        return dyConsumed;
    }


    void onAppBarPostNestedScroll(CoordinatorLayout parent,
                                  AppBarLayout appBar,
                                  View target,
                                  int dxConsumed, int dyConsumed,
                                  int dxUnconsumed, int dyUnconsumed,
                                  int type, int[] consumed) {
        // 如果事件已经被 AppBarLayout 全部消费，或者手指向上滑动，直接返回
        if (dyUnconsumed >= 0 || consumed[1] <= dyUnconsumed || type != ViewCompat.TYPE_TOUCH) {
            return;
        }

        // 此时我们需要拉下 DismissView
//        Log.d(TAG, "onAppBarPostNestedScroll: dyUnconsumed=" + dyUnconsumed + ", dyConsumed=" + consumed[1]);

        int translationY = (int) mChildView.getTranslationY();
        mChildView.setTranslationY(translationY - dyUnconsumed - consumed[1]);

        // 经过上面的平移，此时 child 已经开始在屏幕中可见，因此这个函数不会再调用，
        // 而是在 onNestedPreScroll() 中被处理
        mDownTranslationScale = 0.3f;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                               @NonNull DismissView child,
                               @NonNull View target,
                               int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed,
                               int type, @NonNull int[] consumed) {
        if (! (target instanceof AppBarLayout)) {
            return;
        }

        onAppBarPostNestedScroll(coordinatorLayout, (AppBarLayout) target, target,
                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
        consumed[1] = dyUnconsumed;
    }


    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout parent,
                                   @NonNull DismissView child,
                                   @NonNull View target, int type) {
        startAnimation(parent, child);
    }

    private void startAnimation(CoordinatorLayout parent, DismissView child) {
        final int curTranslationY = (int) child.getTranslationY();

        Log.d(TAG, "onStopNestedScroll: lastY=" + mLastTranslationY + ", curY=" + curTranslationY);

        if (mIsAnimation || mLastTranslationY == curTranslationY ||
                mMaxTranslationY + curTranslationY <= 0) {
            return;
        }

        mIsAnimation = true;

        // 如果之前不可见
        if (mMaxTranslationY + mLastTranslationY <= 50) {
            if (mMaxTranslationY + curTranslationY < mMaxTranslationY / 3) {
                dragToTop(parent, child, curTranslationY);
            } else {
                dragToBottom(parent, child, curTranslationY);
            }
        }
        else {
            if (curTranslationY < mLastTranslationY) {
                dragToTop(parent, child, curTranslationY);
            } else {
                dragToBottom(parent, child, curTranslationY);
            }
        }
    }

    private void dragToTop(CoordinatorLayout parent, DismissView child, int curTranslationY) {
        Log.d(TAG, "dragToTop: start");

        ValueAnimator animator = ValueAnimator.ofInt(curTranslationY, -mMaxTranslationY);
        animator.setDuration(200);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                child.performChangeState(STATE_PREPARE_COLLAPSE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                child.performChangeState(STATE_FINISH_COLLAPSE);
                mIsAnimation = false;
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final int value = (int) animation.getAnimatedValue();
                child.setTranslationY(value);
            }
        });
        animator.start();
    }

    private void dragToBottom(CoordinatorLayout parent, DismissView child, int curTranslationY) {
        ValueAnimator animator = ValueAnimator.ofInt(
                curTranslationY,
                parent.getHeight() - child.getBottom()
        );
        animator.setDuration(200);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                child.performChangeState(STATE_PREPARE_EXPAND);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                child.performChangeState(STATE_FINISH_EXPAND);
                mIsAnimation = false;
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final int value = (int) animation.getAnimatedValue();
                child.setTranslationY(value);
            }
        });
        animator.start();
    }

    @Override
    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout,
                                    @NonNull DismissView child,
                                    @NonNull View target,
                                    float velocityX, float velocityY) {
        return velocityY < 0 && child.getTranslationY() + mMaxTranslationY > 0;
    }
}
