package cn.nlifew.linovel.ui.main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.adapter.BaseFragmentPagerAdapter;
import cn.nlifew.linovel.fragment.BaseFragment;
import cn.nlifew.linovel.fragment.category2.CategoryFragment;
import cn.nlifew.linovel.fragment.ranking.RankingCategoryFragment;
import cn.nlifew.linovel.fragment.square.SquareFragment;

public class MainAdapter extends BaseFragmentPagerAdapter implements
        TabLayout.OnTabSelectedListener {
    private static final String TAG = "MainAdapter";

    private static final int FIRST_ITEM_POSITION = 1;

    MainAdapter(FragmentActivity activity) {
        super(activity.getSupportFragmentManager());
        mContext = activity;
    }

    private final Context mContext;

    void bind(TabLayout tabLayout, ViewPager pager) {
        pager.setAdapter(this);
        pager.setCurrentItem(FIRST_ITEM_POSITION);
        setFirstItemPosition(FIRST_ITEM_POSITION);

        tabLayout.setupWithViewPager(pager);
        tabLayout.addOnTabSelectedListener(this);

        // setUpWithViewPager 会实时更新，因此这里的 getTabCount() 没问题
        for (int i = 0, n = tabLayout.getTabCount(); i < n; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            onConfigureTab(tab, i);
            if (i == FIRST_ITEM_POSITION) {
                onTabSelected(tab);
            }
            else {
                onTabUnselected(tab);
            }
        }
    }

    @Override
    public BaseFragment createBaseFragment(int position) {
        Log.d(TAG, "createBaseFragment: " + position);

        BaseFragment fragment;

        switch (position) {
            case 0: fragment = new CategoryFragment(); break;
            case 1: fragment = SquareFragment.newInstance("0"); break;
            case 2: fragment = new RankingCategoryFragment(); break;
            default: throw new IndexOutOfBoundsException(String.valueOf(position));
        }
//        fragment.setLazyLoadEnabled(false);
        return fragment;
    }


    private void onConfigureTab(TabLayout.Tab tab, int position) {
        Log.d(TAG, "onConfigureTab: " + position);

        TextView tv = new TextView(mContext);
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        tv.setText(getPageTitle(position));

        tab.setCustomView(tv);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        Log.d(TAG, "getPageTitle: " + position);
        switch (position) {
            case 0: return mContext.getString(R.string.activity_main_tab_category);
            case 1: return mContext.getString(R.string.activity_main_tab_home);
            case 2: return mContext.getString(R.string.activity_main_tab_ranking);
        }
        return "";
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.d(TAG, "onTabSelected: " + tab.getPosition());
        TextView tv = (TextView) tab.getCustomView();
        if (tv != null) {
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        Log.d(TAG, "onTabUnselected: " + tab.getPosition());
        TextView tv = (TextView) tab.getCustomView();
        if (tv != null) {
            tv.setTextColor(0xFF5771cc);    // FF6F85DF
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
