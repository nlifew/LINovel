package cn.nlifew.linovel.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class TextColorPreference extends SeekBarPreference {
    private static final String TAG = "TextColorPreference";


    public TextColorPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextColorPreference(Context context) {
        super(context);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        progress += mMinValue;
        mValueView.setText(String.format("%06X", progress));
        mInfoTextView.setTextColor(0xff000000 | progress);
    }
}
