package cn.nlifew.linovel.fragment.ranking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;

import cn.nlifew.linovel.fragment.BaseFragment;
import cn.nlifew.linovel.fragment.container.BaseContainerFragment;
import cn.nlifew.linovel.utils.ToastUtils;
import cn.nlifew.xqdreader.bean.ranking.RankingCategoryBean;

public class RankingCategoryFragment extends BaseContainerFragment /*implements
    CategoryAdapter.FragmentFactory*/ {
    private static final String TAG = "RankingCategoryFragment";

//    private static final String ARGS_SITE_ID = "ARGS_SITE_ID";

//    public static RankingCategoryFragment newInstance(int siteId) {
//        Bundle bundle = new Bundle();
//        bundle.putInt(ARGS_SITE_ID, siteId);
//
//        RankingCategoryFragment fragment = new RankingCategoryFragment();
//        fragment.setArguments(bundle);
//        return fragment;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Bundle bundle = getArguments();
//        if (bundle == null) {
//            throw new UnsupportedOperationException("use newInstance() to obtain a Fragment");
//        }
        mViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
//        mViewModel.setSiteId(bundle.getInt(ARGS_SITE_ID));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        LifecycleOwner owner = getViewLifecycleOwner();
        mViewModel.mErrMsg.observe(owner, this::onErrMsgChanged);
        mViewModel.mRankingCategory.observe(owner, this::onRankingCategoryChanged);

        mFragmentAdapter = new CategoryAdapter(this);
        mViewPager.setAdapter(mFragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    private CategoryViewModel mViewModel;
    private CategoryAdapter mFragmentAdapter;

    private void onErrMsgChanged(String s) {
        if (s != null) {
            ToastUtils.getInstance(getContext()).show(s);
        }
    }

    private void onRankingCategoryChanged(RankingCategoryBean.CategoryType[] data) {
        if (data != null) {
            mFragmentAdapter.updateDataSet(data);
        }
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        mViewModel.loadRankingCategory();
    }


//    @Override
//    public BaseFragment createFragment(RankingCategoryBean.CategoryType category) {
//        Bundle bundle = getArguments();
//        return RankingFragment.newInstance(
//                bundle.getInt(ARGS_SITE_ID), category.Id
//        );
//    }
}
