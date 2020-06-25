package cn.nlifew.linovel.widget;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.preference.Preference;

public class TimePickerPreference extends Preference implements
        TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "TimePickerPreference";

    public TimePickerPreference(Context context) {
        super(context);
    }

    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int mTime = 830;
    private TimePickerDialog mPickerDialog;

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, mTime);
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        mTime = getPersistedInt((int) defaultValue);
    }

    @Override
    protected void onClick() {
        final int hour = mTime / 100;
        final int minute = mTime % 100;

        if (mPickerDialog == null) {
            mPickerDialog = new TimePickerDialog(getContext(),
                    this, hour, minute, true);
        }
        mPickerDialog.updateTime(hour, minute);
        mPickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int time = 100 * hourOfDay + minute;
        if (callChangeListener(time)) {
            mTime = time;
            persistInt(time);
        }
    }
}
