package cn.nlifew.linovel.ui.reading;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.ui.BaseActivity;
import cn.nlifew.linovel.utils.ToastUtils;

import static cn.nlifew.linovel.ui.reading.ReadingViewModel.ChapterWrapper.TYPE_LAST_CHAPTER;
import static cn.nlifew.linovel.ui.reading.ReadingViewModel.ChapterWrapper.TYPE_NEXT_CHAPTER;

/**
 * 这个类用来打开具体的阅读界面。
 * 你必须给 Intent 提供一个 Uri 格式的 Data，用来传递必要的数据。
 * Uri 必须包括：bookId；可以省略：chapterId
 * 例如：
 * Context c = getContext();
 * Uri data = Uri.parse("linovel://reading?id=12345");
 * Intent intent = new Intent(c, ReadingActivity.class);
 * intent.setData(data);
 * c.startActivity(intent);
 *
 * 当 chapterId 不为空的时候，我们会直接跳转到具体的章节；
 * 如果为空，就尝试从服务器同步上次的阅读进度。
 *
 * 需要注意的是，在服务器返回了数据之后，我们需要对这些文本进行分页处理。
 * 在当前版本的实现中，我们向根布局添加了一个隐藏的 TextView，即 R.id.activity_reading_measure.
 * 我们先把文本赋给这个 TextView，测量之后再交给 Helper 分页。
 * 在之前的版本中，我们手动构造 StaticLayout，而不是从 TextView 中获取，
 * 但这个方法十分不可靠。
 * 目前的方案似乎也并不完美，因为我们必须确保这个测量用的 TextView 和 ViewPager 中的 TextView
 * 参数完全一致(width, height, padding, textSize, textStyle等)，否则测量出的
 * 结果就会不准确。
 */

public class ReadingActivity extends BaseActivity implements
        PagerAdapterImpl.OnChapterChangedListener {
    private static final String TAG = "ReadingActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_reading);

        mMeasureView = findViewById(R.id.activity_reading_measure);
        mPageIndexView = findViewById(R.id.activity_reading_page);

        ViewPager2 pager = findViewById(R.id.activity_reading_pager);
        mPagerAdapter = new PagerAdapterImpl(this, pager);
        pager.setAdapter(mPagerAdapter);

        Uri data = getIntent().getData();
        if (data == null) {
            throw new UnsupportedOperationException("give me a Data");
        }
        String bookId = data.getQueryParameter("id");
        String chapterId = data.getQueryParameter("chapterId");

        mViewModel = new ViewModelProvider(this).get(ReadingViewModel.class);
        mViewModel.setBookId(bookId, chapterId);
        mViewModel.mErrMsg.observe(this, this::onErrorMessageChanged);
        mViewModel.mChapter.observe(this, this::onChapterChanged);
    }

    private ReadingViewModel mViewModel;
    private PagerAdapterImpl mPagerAdapter;
    private TextView mPageIndexView;

    private TextView mMeasureView;


    @Override
    protected void onStop() {
        super.onStop();
        // 这一章一共读了多少字符 ?
        List<CharSequence> list = mPagerAdapter.getDataSet();
        int pageIndex = mPagerAdapter.getCurrentPagerIndex();
        int position = Helper.getPositionByPageIndex(list, pageIndex);

        // 添加书签
        mViewModel.saveToBookMark(position);
        mViewModel.saveToReadingHistory(position);
    }

    private void onErrorMessageChanged(String s) {
        if (s != null) {
            ToastUtils.getInstance(this).show(s);
            mPagerAdapter.setIsLoadingData(false);
        }
    }

    private void onChapterChanged(ReadingViewModel.ChapterWrapper wrapper) {
        if (wrapper == null) {
            return;
        }
        mMeasureView.setVisibility(View.VISIBLE);
        mMeasureView.setText(Helper.append(wrapper.title, wrapper.content));
        mMeasureView.post(() -> {
            List<CharSequence> list = Helper.split(mMeasureView);
            int pageIndex = Helper.getPageIndexByPosition(list, wrapper.position);
            if (wrapper.type == TYPE_LAST_CHAPTER) {
                pageIndex = list.size() - 1;
            }
            mPagerAdapter.updateChapter(list, pageIndex);
            mMeasureView.setVisibility(View.INVISIBLE);
        });
    }

    @Override
    public void onLoadLastChapter() {
        Log.d(TAG, "onLoadLastChapter: start");
        mViewModel.loadLastChapter();
    }

    @Override
    public void onLoadNextChapter() {
        Log.d(TAG, "onLoadNextChapter: start");
        mViewModel.loadNextChapter();
    }

    @Override
    public void onLoadCurrentChapter()  {
        Log.d(TAG, "onLoadCurrentChapter: start");
        mViewModel.loadCurrentChapter();
    }

    @Override
    public void onItemSelected(int position, int total) {
        mPageIndexView.setText(position + 1 + "/" + total);
    }
}
