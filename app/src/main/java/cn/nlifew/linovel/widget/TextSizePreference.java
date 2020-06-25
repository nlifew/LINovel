package cn.nlifew.linovel.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.SeekBar;

public class TextSizePreference extends SeekBarPreference {
    private static final String TAG = "TextSizePreference";

    public TextSizePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextSizePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextSizePreference(Context context) {
        super(context);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        super.onProgressChanged(seekBar, progress, fromUser);
        mInfoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, progress + mMinValue);
    }
}
