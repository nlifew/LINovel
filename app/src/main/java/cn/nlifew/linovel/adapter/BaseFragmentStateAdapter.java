package cn.nlifew.linovel.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import cn.nlifew.linovel.fragment.BaseFragment;

public abstract class BaseFragmentStateAdapter extends FragmentStateAdapter
    implements BaseFragment.OnLazyLoadListener {
    private static final String TAG = "BaseFragmentStateAdapte";

    public BaseFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public BaseFragmentStateAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public BaseFragmentStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    private int mFirstItemPosition;
    private BaseFragment mFirstItem;
    private boolean mLazyLoaded = false;

    public void setFirstItemPosition(int position) {
        if (mLazyLoaded || mFirstItem != null) {
            throw new UnsupportedOperationException("Fragments has been called after onLazyLoad()");
        }
        mFirstItemPosition = position;
    }

    public abstract BaseFragment createBaseFragment(int position);


    @NonNull
    @Override
    public final Fragment createFragment(int position) {
        BaseFragment fragment = createBaseFragment(position);
        if (position == mFirstItemPosition) {
            mFirstItem = fragment;
        }
        fragment.setOnLazyLoadListener(this);
        return fragment;
    }

    @Override
    public boolean onLazyLoad(BaseFragment fragment) {
        if (mLazyLoaded) {
            return true;
        }
        if (mFirstItem == fragment) {
            mFirstItem = null;
            mLazyLoaded = true;
            return true;
        }
        return false;
    }
}
