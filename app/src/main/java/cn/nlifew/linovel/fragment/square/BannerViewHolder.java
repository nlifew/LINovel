package cn.nlifew.linovel.fragment.square;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.adapter.FragmentViewHolder;
import cn.nlifew.linovel.utils.ToastUtils;
import cn.nlifew.xqdreader.bean.SquareBean;
import cn.nlifew.xqdreader.utils.Utils;

public class BannerViewHolder extends FragmentViewHolder<SquareBean.GroupType>
        implements LifecycleEventObserver{
    private static final String TAG = "BannerViewHolder";
    private static final long BANNER_PAUSE_TIME_MILLIS = 5000;


    BannerViewHolder(Fragment fragment) {
        super(fragment, new BannerViewPager(fragment.getContext()));

        Point point = Utils.getDisplaySize(fragment.getContext());

        BannerViewPager banner = (BannerViewPager) itemView;
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                point.x,
                point.x * 375 / 1080
        );
        banner.setLayoutParams(lp);

        mBannerAdapter = new Adapter();
        banner.setAdapter(mBannerAdapter);
        banner.setCurrentItem(Integer.MAX_VALUE / 8, false);

        final Lifecycle lifecycle = fragment.getLifecycle();
        lifecycle.addObserver(this);
    }

    private final Adapter mBannerAdapter;
    private final List<SquareBean.SimpleDataType> mBannerDataSet
            = new ArrayList<>(6);

    @Override
    public void onBindViewHolder(SquareBean.GroupType group) {
        SquareBean.SimpleDataType[] data = (SquareBean.SimpleDataType[]) group.Data;
        mBannerDataSet.clear();
        mBannerDataSet.addAll(Arrays.asList(data));
        mBannerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        switch (event) {
            case ON_DESTROY:
                source.getLifecycle().removeObserver(this);
                break;
            case ON_STOP:
                mH.removeCallbacksAndMessages(null);
                break;
            case ON_START:
                mH.sendMessageDelayed(Message.obtain(), BANNER_PAUSE_TIME_MILLIS);
        }
    }


    private void onBannerItemClick(View view) {
        Context context = mFragment.getContext();
        Uri uri = Uri.parse((String) view.getTag(R.id.banner_item_tag));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "onBannerItemClick: " + uri, e);
            ToastUtils.getInstance(context).show(e.toString());
        }
    }


    @SuppressLint("HandlerLeak")
    private final Handler mH = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
//            Log.d(TAG, "handleMessage: " + System.currentTimeMillis());

            ViewPager pager = (ViewPager) itemView;

            int nextItemPosition = pager.getCurrentItem() + 1;
            if (nextItemPosition != mBannerAdapter.getCount()) {
                pager.setCurrentItem(nextItemPosition, true);
            }
            else {
                nextItemPosition = Integer.MAX_VALUE / 8;
                pager.setCurrentItem(nextItemPosition, false);
            }

            sendMessageDelayed(Message.obtain(), BANNER_PAUSE_TIME_MILLIS);
        }
    };

    private final class Adapter extends PagerAdapter {

        private View mCachedView;

        private View onCreateView() {
            ImageView iv = new ImageView(mFragment.getContext());
            iv.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            return iv;
        }

        private void onBindView(View view, int position) {
            SquareBean.SimpleDataType data = mBannerDataSet.get(position);
            view.setTag(R.id.banner_item_tag, data.ActionUrl);
            view.setOnClickListener(BannerViewHolder.this::onBannerItemClick);


            Glide.with(mFragment).asBitmap().load(data.ImageUrl)
                    .into((ImageView) view);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = mCachedView;
            mCachedView = null;
            if (view == null) {
                view = onCreateView();
            }
            int n = mBannerDataSet.size();
            if (n != 0) {
                onBindView(view,position % n);
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
            mCachedView = view;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }
    }
}
