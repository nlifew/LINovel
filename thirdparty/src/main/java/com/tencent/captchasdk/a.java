package com.tencent.captchasdk;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;

class a extends View {

    /* renamed from: a reason: collision with root package name */
    private d f25165a;

    /* renamed from: b reason: collision with root package name */
    private int f25166b = Color.rgb(0, 118, 255);

    /* renamed from: c reason: collision with root package name */
    private boolean f25167c;

    public a(Context context) {
        super(context);
    }

    private void a() {
        if (getVisibility() == VISIBLE) {
            if (this.f25165a != null) {
                this.f25167c = true;
            }
            postInvalidate();
        }
    }

    private void a(int i, int i2) {
        int paddingRight = i - (getPaddingRight() + getPaddingLeft());
        int paddingTop = i2 - (getPaddingTop() + getPaddingBottom());
        if (this.f25165a != null) {
            this.f25165a.setBounds(0, 0, paddingRight, paddingTop);
        }
    }

    private void b() {
        if (this.f25165a != null) {
            this.f25165a.stop();
            this.f25167c = false;
        }
        postInvalidate();
    }

    private void c() {
        int[] drawableState = getDrawableState();
        if (this.f25165a != null && this.f25165a.isStateful()) {
            this.f25165a.setState(drawableState);
        }
    }

    /* access modifiers changed from: 0000 */
    public void a(Canvas canvas) {
        d dVar = this.f25165a;
        if (dVar != null) {
            int save = canvas.save();
            canvas.translate((float) getPaddingLeft(), (float) getPaddingTop());
            dVar.draw(canvas);
            canvas.restoreToCount(save);
            if (this.f25167c ) {
                dVar.start();
                this.f25167c = false;
            }
        }
    }

    public void a(d dVar) {
        if (this.f25165a != dVar) {
            if (this.f25165a != null) {
                this.f25165a.setCallback(null);
                unscheduleDrawable(this.f25165a);
            }
            this.f25165a = dVar;
            this.f25165a.a(this.f25166b);
            if (dVar != null) {
                dVar.setCallback(this);
            }
            postInvalidate();
        }
    }

    @TargetApi(21)
    public void drawableHotspotChanged(float f, float f2) {
        super.drawableHotspotChanged(f, f2);
        if (this.f25165a != null) {
            this.f25165a.setHotspot(f, f2);
        }
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        c();
    }

    public void invalidateDrawable(@NonNull Drawable drawable) {
        if (verifyDrawable(drawable)) {
            Rect bounds = drawable.getBounds();
            int scrollX = getScrollX() + getPaddingLeft();
            int scrollY = getScrollY() + getPaddingTop();
            invalidate(bounds.left + scrollX, bounds.top + scrollY, scrollX + bounds.right, bounds.bottom + scrollY);
            return;
        }
        super.invalidateDrawable(drawable);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        a();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        b();
        super.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        a(canvas);
    }

    /* access modifiers changed from: protected */
    public synchronized void onMeasure(int i, int i2) {
        int i3;
        int i4 = 0;
        synchronized (this) {
            d dVar = this.f25165a;
            if (dVar != null) {
                i3 = dVar.getIntrinsicWidth();
                i4 = dVar.getIntrinsicHeight();
            } else {
                i3 = 0;
            }
            c();
            setMeasuredDimension(resolveSizeAndState(i3 + getPaddingLeft() + getPaddingRight(), i, 0), resolveSizeAndState(i4 + getPaddingTop() + getPaddingBottom(), i2, 0));
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        a(i, i2);
    }

    /* access modifiers changed from: protected */
    public void onVisibilityChanged(@NonNull View view, int i) {
        super.onVisibilityChanged(view, i);
        if (i == 8 || i == 4) {
            b();
        } else {
            a();
        }
    }

    public void setVisibility(int i) {
        if (getVisibility() != i) {
            super.setVisibility(i);
            if (i == 8 || i == 4) {
                b();
            } else {
                a();
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(@NonNull Drawable drawable) {
        return drawable == this.f25165a || super.verifyDrawable(drawable);
    }
}