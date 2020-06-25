package cn.nlifew.linovel.ui.reading;

import android.app.Activity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import cn.nlifew.linovel.settings.Settings;

class PagerAdapterImpl extends RecyclerView.Adapter {
    private static final String TAG = "PagerAdapterImpl";

    private static final int TYPE_CONTENT = 0;
    private static final int TYPE_LOADING = 1;

    PagerAdapterImpl(Activity activity, ViewPager2 pager) {
        if (! (activity instanceof OnChapterChangedListener)) {
            throw new UnsupportedOperationException("The host must implements OnChapterChangedListener");
        }
        mActivity = activity;
        mChapterListener = (OnChapterChangedListener) activity;
        mDataSet = new ArrayList<>(32);
        mHostView = pager;
        pager.registerOnPageChangeCallback(new PagerChangedImpl());
    }

    interface OnChapterChangedListener {
        void onLoadLastChapter();
        void onLoadNextChapter();
        void onLoadCurrentChapter();
        void onItemSelected(int position, int total);
    }

    private final Activity mActivity;
    private final OnChapterChangedListener mChapterListener;
    private final List<CharSequence> mDataSet;
    private final ViewPager2 mHostView;

    private boolean mBusy;

    void setIsLoadingData(boolean loading) {
        mBusy = loading;
        mHostView.setUserInputEnabled(! loading);
    }

    void updateChapter(List<CharSequence> list, int pageIndex) {
        mDataSet.clear();
        mDataSet.addAll(list);
        notifyDataSetChanged();
        mHostView.setCurrentItem(pageIndex + 1, false);
        setIsLoadingData(false);
    }

    List<CharSequence> getDataSet() { return mDataSet; }

    int getCurrentPagerIndex() {
        int pos = mHostView.getCurrentItem();
        int n = mDataSet.size();
        if (pos <= 1) return 0;
        if (pos >= n) return n - 1;
        return pos - 1;
    }

    /**
     * mDataSet 保存的是每一页的文字
     * 当 mDataSet 长度为 0 时，说明数据集等待初始化，我们返回 1 用来显示一个 "加载中" 的页面
     * 否则就返回 mDataSet.size() + 2，用来显示 "上一章" 和 "下一章" 的 "加载中" 页面
     * @return count
     */
    @Override
    public int getItemCount() {
        int n = mDataSet.size();
        return n == 0 ? 1 : n + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position > mDataSet.size()) {
            return TYPE_LOADING;
        }
        return TYPE_CONTENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView tv = new ReadingView(mActivity);
        tv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        Settings settings = Settings.getInstance(mActivity);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, settings.getReadingTextSize());
        tv.setTextColor(0xFF000000 | settings.getReadingTextColor());

        if (viewType == TYPE_LOADING) {
            tv.setGravity(Gravity.CENTER);
            tv.setText("加载中");
        }
        return new Holder(tv);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        Log.d(TAG, "onBindViewHolder: " + position);

        if (position == 0 || position > mDataSet.size()) {
            return;
        }
        ((Holder) h).onBindViewHolder(mDataSet.get(position - 1));
    }

    private final class PagerChangedImpl extends ViewPager2.OnPageChangeCallback {

        private boolean mShouldLoadCurrentChapter = true;

        @Override
        public void onPageSelected(int position) {
            Log.d(TAG, "onPageSelected: " + position);

            if (mShouldLoadCurrentChapter) {
                mShouldLoadCurrentChapter = false;
                loadCurrentChapter();
            }
            else if (position == 0) {
                loadLastChapter();
            }
            else if (position > mDataSet.size()) {
                loadNextChapter();
            }
            else {
                mChapterListener.onItemSelected(position - 1, mDataSet.size());
            }
        }
    }

    private void loadCurrentChapter() {
        setIsLoadingData(true);
        mChapterListener.onLoadCurrentChapter();
    }

    private void loadLastChapter() {
        if (mBusy) {
            // 此时正在加载内容，忽略掉此次请求
            Log.w(TAG, "loadLastChapter: I am loading last chapter");
            return;
        }
        setIsLoadingData(true);
        mChapterListener.onLoadLastChapter();
    }

    private void loadNextChapter() {
        if (mBusy) {
            // 此时正在加载内容，忽略掉此次请求
            Log.w(TAG, "loadNextChapter: I am loading next chapter");
            return;
        }
        setIsLoadingData(true);
        mChapterListener.onLoadNextChapter();
    }

    private final class Holder extends RecyclerView.ViewHolder implements
            View.OnClickListener{
        Holder(@NonNull View itemView) {
            super(itemView);
        }

        void onBindViewHolder(CharSequence text) {
            TextView tv = (TextView) itemView;
            tv.setText(text);
            tv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int width = v.getWidth() - v.getPaddingLeft() - v.getPaddingRight();
            float x = ((ReadingView) v).getClickPoint().x;

            Log.d(TAG, "onClick: [x, width] = [" + x + ", " + width + "]");

            if (x < width / 3.0f) {
                Log.d(TAG, "onClick: last");
                mHostView.setCurrentItem(mHostView.getCurrentItem() - 1, true);
            }
            else if (x > width * 2 / 3.0f) {
                Log.d(TAG, "onClick: next");
                mHostView.setCurrentItem(mHostView.getCurrentItem() + 1, true);
            }
        }
    }
}
