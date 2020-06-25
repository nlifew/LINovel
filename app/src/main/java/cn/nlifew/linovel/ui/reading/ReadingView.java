package cn.nlifew.linovel.ui.reading;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import cn.nlifew.linovel.settings.Settings;

public class ReadingView extends AppCompatTextView {
    public ReadingView(Context context) {
        super(context);
        init(context);
    }

    public ReadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ReadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Settings settings = Settings.getInstance(context);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, settings.getReadingTextSize());
        setTextColor(0xFF000000 | settings.getReadingTextColor());
    }

    private final PointF mLastClickPoint = new PointF();


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            mLastClickPoint.x = event.getX();
            mLastClickPoint.y = event.getY();
        }
        return super.dispatchTouchEvent(event);


    }

    PointF getClickPoint() {
        return mLastClickPoint;
    }
}
