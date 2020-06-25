package cn.nlifew.linovel.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;

import cn.nlifew.linovel.R;

public class SingleChoicePreference extends Preference implements
        DialogInterface.OnClickListener{
    private static final String TAG = "SingleChoicePreference";

    public SingleChoicePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public SingleChoicePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SingleChoicePreference(Context context) {
        super(context);
        init(context, null, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SingleChoicePreference);
        mEntries = a.getTextArray(R.styleable.SingleChoicePreference_entries);
        mEntryValues = a.getTextArray(R.styleable.SingleChoicePreference_entryValues);
        a.recycle();
    }

    private CharSequence[] mEntries;
    private int mCurrentValue;
    private CharSequence[] mEntryValues;


    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(@Nullable Object defaultValue) {
        String now = getPersistedString((String) defaultValue);
        for (int i = 0; i < mEntryValues.length; i++) {
            if (TextUtils.equals(now, mEntryValues[i])) {
                mCurrentValue = i;
                break;
            }
        }
    }

    @Override
    protected void onClick() {
        new AlertDialog.Builder(getContext())
                .setTitle(getTitle())
                .setSingleChoiceItems(mEntries, mCurrentValue, this)
                .show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();

        if (callChangeListener(mEntryValues[which])) {
            mCurrentValue = which;
            persistString(mEntryValues[which].toString());
        }
    }

    public CharSequence[] getEntries() {
        return mEntries;
    }

    public void setEntries(CharSequence[] entries) {
        this.mEntries = entries;
    }

    public CharSequence[] getEntryValues() {
        return mEntryValues;
    }

    public void setEntryValues(CharSequence[] entryValues) {
        this.mEntryValues = entryValues;
    }
}
