package cn.nlifew.linovel.fragment.chapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.widget.RoofItemDecoration;
import cn.nlifew.linovel.ui.reading.ReadingActivity;
import cn.nlifew.linovel.utils.DisplayUtils;
import cn.nlifew.xqdreader.bean.ChapterListBean;

class RecyclerAdapterImpl extends RecyclerView.Adapter  {
    private static final String TAG = "RecyclerAdapterImpl";

    RecyclerAdapterImpl(Fragment fragment) {
        mFragment = fragment;
        mRoofHelper = new RoofHelper();
        mDateHelper = new DateHelper();
    }

    private final Fragment mFragment;
    private ChapterListBean.DataType mChapterData;
    private final RoofItemDecoration.ItemHelper mRoofHelper;
    private final DateHelper mDateHelper;

    void refreshDataSet(ChapterListBean.DataType data) {
        mChapterData = data;
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        RoofItemDecoration decoration = new RoofItemDecoration(mRoofHelper);
        recyclerView.addItemDecoration(decoration);
    }

    @Override
    public int getItemCount() {
        return mChapterData == null || mChapterData.Chapters == null ?
                0 : mChapterData.Chapters.length;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mFragment.getContext())
                .inflate(R.layout.fragment_chapter_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        final Holder holder = (Holder) h;
        final ChapterListBean.ChapterType chapter = mChapterData.Chapters[position];

        holder.mTitleView.setText(chapter.N);
        holder.mTimeView.setText(mDateHelper.format(chapter.T));

        holder.itemView.setTag(chapter.C);
    }

    private final class Holder extends RecyclerView.ViewHolder implements
            View.OnClickListener{
        Holder(@NonNull View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.title);
            mTimeView = itemView.findViewById(R.id.time);
            itemView.setOnClickListener(this);
        }
        final TextView mTitleView;
        final TextView mTimeView;

        @Override
        public void onClick(View v) {
            int chapterId = (int) v.getTag();
            String uri = "linovel://reading?id=" + mChapterData.BookId
                    + "&chapterId=" + chapterId;
            Context context = mFragment.getContext();
            Intent intent = new Intent(context, ReadingActivity.class);
            intent.setData(Uri.parse(uri));
            context.startActivity(intent);
        }
    }

    private static final class DateHelper {
        private final Date mDate = new Date();
        private FieldPosition mFieldPosition = new FieldPosition(0);
        private final StringBuffer mTextBuilder = new StringBuffer(32);
        private final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

        CharSequence format(long time) {
            mDate.setTime(time);
            mTextBuilder.setLength(0);
            mDateFormat.format(mDate, mTextBuilder, mFieldPosition);
            return mTextBuilder;
        }
    }

    private final class RoofHelper implements RoofItemDecoration.ItemHelper {

        private final Paint mPaint;
        private final int mTextSize;

        RoofHelper() {
            mTextSize = DisplayUtils.sp2px(12);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setTextSize(mTextSize);
        }

        @Override
        public int getItemType(int position) {
            return mChapterData.Chapters[position].Vc;
        }

        @Override
        public void draw(Canvas c, int l, int t, int r, int b, int type) {
            String text = null;
            for (ChapterListBean.VolumeType volume : mChapterData.Volumes) {
                if (volume.VolumeCode == type) {
                    text = volume.VolumeName;
                    break;
                }
            }
            if (text == null) {
                return;
            }
            mPaint.setColor(0xFFF5F7FA);
            c.drawRect(l, t, r, b, mPaint);

            mPaint.setColor(0xFF737373);
            c.drawText(text, DisplayUtils.dp2px(20),
                    (b + t) / 2 + mTextSize / 2, mPaint);
        }
    }
}
