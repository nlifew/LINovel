package cn.nlifew.linovel.fragment.ranking;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.nlifew.linovel.adapter.BaseFragmentStatePagerAdapter;
import cn.nlifew.linovel.fragment.BaseFragment;
import cn.nlifew.xqdreader.bean.ranking.RankingCategoryBean;

class CategoryAdapter extends BaseFragmentStatePagerAdapter {
    private static final String TAG = "CategoryAdapter";

//    interface FragmentFactory {
//        BaseFragment createFragment(RankingCategoryBean.CategoryType category);
//    }

    CategoryAdapter(Fragment fragment) {
        super(fragment.getChildFragmentManager());

//        if (! (fragment instanceof FragmentFactory)) {
//            throw new UnsupportedOperationException("Fragment have to implement FragmentFactory");
//        }

        mDataSet = new ArrayList<>(12);
//        mFragmentFactory = (FragmentFactory) fragment;
    }


    private final List<RankingCategoryBean.CategoryType> mDataSet;
//    private final FragmentFactory mFragmentFactory;

    void updateDataSet(RankingCategoryBean.CategoryType[] array) {
        mDataSet.clear();
        mDataSet.addAll(Arrays.asList(array));
        notifyDataSetChanged();
    }

    @Override
    public BaseFragment createBaseFragment(int position) {
        final RankingCategoryBean.CategoryType t = mDataSet.get(position);
        return RankingFragment.newInstance(t.Id);
//        return mFragmentFactory.createFragment(mDataSet.get(position));
    }

    @Override
    public int getCount() {
        return mDataSet.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mDataSet.get(position).Name;
    }
}
