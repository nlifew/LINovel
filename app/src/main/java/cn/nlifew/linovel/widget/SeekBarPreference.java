package cn.nlifew.linovel.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.DialogPreference;
import androidx.preference.Preference;

import cn.nlifew.linovel.R;

public class SeekBarPreference extends Preference implements
        SeekBar.OnSeekBarChangeListener,
        DialogInterface.OnClickListener {
    private static final String TAG = "SeekBarPreference";

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SeekBarPreference(Context context) {
        super(context);
        init(context, null, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SeekBarPreference);
        mMinValue = a.getInt(R.styleable.SeekBarPreference_minValue,
                0);
        mMaxValue = a.getInt(R.styleable.SeekBarPreference_maxValue,
                100);

        mInfoText = a.getString(R.styleable.SeekBarPreference_topText);

        a.recycle();
    }

    int mMinValue;
    private int mMaxValue;
    private int mCurrentValue;
    private String mInfoText;
    private AlertDialog mDialog;

    TextView mValueView;
    TextView mInfoTextView;
    private SeekBar mSeekBar;

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, mMinValue);
    }

    @Override
    protected void onSetInitialValue(@Nullable Object defaultValue) {
        mCurrentValue = getPersistedInt((int) defaultValue);
    }

    @Override
    protected void onClick() {
        if (mDialog == null) {
            Context context = getContext();
            View view = View.inflate(context, R.layout.preference_seekbar, null);
            onBindDialogView(view);
            mDialog = new AlertDialog.Builder(context)
                    .setPositiveButton("确定", this)
                    .setNegativeButton("取消", this)
                    .setView(view)
                    .create();
        }
        mSeekBar.setProgress(mCurrentValue - mMinValue);
        onProgressChanged(mSeekBar, mCurrentValue - mMinValue, false);
        mDialog.show();
    }

    private void onBindDialogView(View v) {
        mInfoTextView = v.findViewById(R.id.top_title);
        mInfoTextView.setText(mInfoText);

        mValueView = v.findViewById(R.id.seek_value);
        mSeekBar = v.findViewById(R.id.seek_bar);
        mSeekBar.setMax(mMaxValue - mMinValue);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        int process = mMinValue + mSeekBar.getProgress();
        if (which == DialogInterface.BUTTON_POSITIVE &&
                callChangeListener(process)) {
            mCurrentValue = process;
            persistInt(process);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mValueView.setText(Integer.toString(mMinValue + progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
