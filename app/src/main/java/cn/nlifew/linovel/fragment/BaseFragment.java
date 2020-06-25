package cn.nlifew.linovel.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import cn.nlifew.linovel.app.ThisApp;

public class BaseFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    public interface OnLazyLoadListener {
        boolean onLazyLoad(BaseFragment fragment);
    }

    private OnLazyLoadListener mLazyLoadListener;
    private boolean mLazyLoadEnabled = true;
    private boolean mLazyLoaded = false;
    private int mLazyLoadDelayMs = 100;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Lifecycle lifecycle = getLifecycle();
        lifecycle.addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                Log.d(TAG, "onStateChanged: " + event);
                switch (event) {
                    case ON_RESUME:
                        lazyLoadIfReady();
                        break;
                    case ON_DESTROY:
                        mLazyLoaded = false;
                        lifecycle.removeObserver(this);
                }
            }
        });
    }

    private void lazyLoadIfReady() {
        if (mLazyLoadEnabled && ! mLazyLoaded &&
                (mLazyLoadListener == null || mLazyLoadListener.onLazyLoad(this))) {
            mLazyLoaded = true;
            ThisApp.mH.postDelayed(this::onLazyLoad, mLazyLoadDelayMs);
        }
    }

    @CallSuper
    protected void onLazyLoad() {
        Log.d(TAG, "onLazyLoad: start " + this);
    }

    public void setLazyLoadEnabled(boolean enabled) {
        mLazyLoadEnabled = enabled;
    }

    public void setLazyLoadDelayMs(int time) { this.mLazyLoadDelayMs = time; }

    public void setOnLazyLoadListener(OnLazyLoadListener listener) {
        mLazyLoadListener = listener;
    }

}
