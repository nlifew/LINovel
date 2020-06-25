package cn.nlifew.linovel.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

public class DismissScrollBehavior extends AppBarLayout.ScrollingViewBehavior {
    private static final String TAG = "DismissScrollBehavior";

    public DismissScrollBehavior() {
        super();
    }

    public DismissScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof DismissView
                | super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        if (dependency instanceof DismissView) {
            DismissView v = (DismissView) dependency;
            child.setTranslationY(dependency.getHeight() + dependency.getTranslationY());
            return true;
        }
        return super.onDependentViewChanged(parent, child, dependency);
    }
}
