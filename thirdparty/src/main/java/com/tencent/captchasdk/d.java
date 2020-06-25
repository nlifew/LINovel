package com.tencent.captchasdk;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

class d extends Drawable implements Animatable {

    /* renamed from: a reason: collision with root package name */
    private HashMap<ValueAnimator, AnimatorUpdateListener> f25175a = new HashMap<>();

    /* renamed from: b reason: collision with root package name */
    private ArrayList<ValueAnimator> f25176b;

    /* renamed from: c reason: collision with root package name */
    private int f25177c = 255;

    /* renamed from: d reason: collision with root package name */
    private Rect f25178d = new Rect();
    private float[] e = {1.0f, 1.0f, 1.0f};

    /* access modifiers changed from: private */
    private int[] f = {255, 255, 255};
    private boolean g;
    private Paint h = new Paint();

    d() {
        this.h.setColor(-1);
        this.h.setStyle(Style.FILL);
        this.h.setAntiAlias(true);
    }

    private ArrayList<ValueAnimator> a() {
        ArrayList<ValueAnimator> arrayList = new ArrayList<>();
        int[] iArr = {0, 300, 600};
        int[] iArr2 = {255, 51, 255};

        for (int i = 0; i < 3; i++) {
            ValueAnimator ofInt = ValueAnimator.ofInt(iArr2);
            ofInt.setDuration(900);
            ofInt.setRepeatCount(-1);
            ofInt.setStartDelay((long) iArr[i]);

            final int i2 = i;

            a(ofInt, new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    d.this.f[i2] = (int) valueAnimator.getAnimatedValue();
                    d.this.f();
                }
            });
            arrayList.add(ofInt);
        }
        return arrayList;
    }

    private void a(int i, int i2, int i3, int i4) {
        this.f25178d = new Rect(i, i2, i3, i4);
    }

    private void a(ValueAnimator valueAnimator, AnimatorUpdateListener animatorUpdateListener) {
        this.f25175a.put(valueAnimator, animatorUpdateListener);
    }

    private void a(Rect rect) {
        a(rect.left, rect.top, rect.right, rect.bottom);
    }

    private void b() {
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < this.f25176b.size()) {
                ValueAnimator valueAnimator = this.f25176b.get(i2);
                AnimatorUpdateListener animatorUpdateListener = this.f25175a.get(valueAnimator);
                if (animatorUpdateListener != null) {
                    valueAnimator.addUpdateListener(animatorUpdateListener);
                }
                valueAnimator.start();
                i = i2 + 1;
            } else {
                return;
            }
        }
    }

    private void c() {
        if (this.f25176b == null) {
            return;
        }
        for (ValueAnimator valueAnimator : this.f25176b) {
            if (valueAnimator != null && valueAnimator.isStarted()) {
                valueAnimator.removeAllUpdateListeners();
                valueAnimator.end();
            }
        }
    }

    private void d() {
        if (!this.g) {
            this.f25176b = a();
            this.g = true;
        }
    }

    private boolean e() {
        Iterator it = this.f25176b.iterator();
        if (it.hasNext()) {
            return ((ValueAnimator) it.next()).isStarted();
        }
        return false;
    }

    /* access modifiers changed from: private */
    private void f() {
        invalidateSelf();
    }

    private int g() {
        return this.f25178d.width();
    }

    private int h() {
        return this.f25178d.height();
    }

    void a(int i) {
        this.h.setColor(i);
    }

    public void draw(@NonNull Canvas canvas) {
        float g2 = (float) ((g() * 8) / 52);
        float g3 = (((float) g()) - (g2 * 2.0f)) / 6.0f;
        float g4 = ((float) (g() / 2)) - ((g3 * 2.0f) + g2);
        float h2 = (float) (h() / 2);
        for (int i = 0; i < 3; i++) {
            canvas.save();
            canvas.translate((g3 * 2.0f * ((float) i)) + g4 + (((float) i) * g2), h2);
            canvas.scale(this.e[i], this.e[i]);
            this.h.setAlpha(this.f[i]);
            canvas.drawCircle(DisplayHelper.DENSITY, DisplayHelper.DENSITY, g3, this.h);
            canvas.restore();
        }
    }

    public int getAlpha() {
        return this.f25177c;
    }

    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    public boolean isRunning() {
        Iterator it = this.f25176b.iterator();
        if (it.hasNext()) {
            return ((ValueAnimator) it.next()).isRunning();
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        a(rect);
    }

    public void setAlpha(int i) {
        this.f25177c = i;
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void start() {
        d();
        if (this.f25176b != null && !e()) {
            b();
            invalidateSelf();
        }
    }

    public void stop() {
        c();
    }
}