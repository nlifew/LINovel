package com.tencent.captchasdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.webkit.WebView;

import static com.tencent.captchasdk.DisplayHelper.DENSITY;

class b extends WebView {

    /* renamed from: a reason: collision with root package name */
    private Paint f25168a;

    /* renamed from: b reason: collision with root package name */
    private Paint f25169b;

    /* renamed from: c reason: collision with root package name */
    private float f25170c;

    /* renamed from: d reason: collision with root package name */
    private int f25171d;
    private int e;
    private int f;
    private int g;

    public b(Context context) {
        super(context);
        a(context);
    }

    private void a(Context context) {
        this.f25168a = new Paint();
        this.f25168a.setColor(-1);
        this.f25168a.setAntiAlias(true);
        this.f25168a.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
        this.f25169b = new Paint();
        this.f25169b.setXfermode(null);
    }

    private void a(Canvas canvas) {
        Path path = new Path();
        path.moveTo((float) this.f, this.f25170c);
        path.lineTo((float) this.f, (float) this.g);
        path.lineTo(this.f25170c, (float) this.g);
        path.arcTo(new RectF((float) this.f, (float) this.g, ((float) this.f) + (this.f25170c * 2.0f), ((float) this.g) + (this.f25170c * 2.0f)), -90.0f, -90.0f);
        path.close();
        canvas.drawPath(path, this.f25168a);
    }

    private void b(Canvas canvas) {
        Path path = new Path();
        path.moveTo((float) this.f, ((float) (this.g + this.e)) - this.f25170c);
        path.lineTo((float) this.f, (float) (this.g + this.e));
        path.lineTo(((float) this.f) + this.f25170c, (float) (this.g + this.e));
        path.arcTo(new RectF((float) this.f, ((float) (this.g + this.e)) - (this.f25170c * 2.0f), ((float) this.f) + (this.f25170c * 2.0f), (float) (this.g + this.e)), 90.0f, 90.0f);
        path.close();
        canvas.drawPath(path, this.f25168a);
    }

    private void c(Canvas canvas) {
        Path path = new Path();
        path.moveTo(((float) (this.f + this.f25171d)) - this.f25170c, (float) (this.g + this.e));
        path.lineTo((float) (this.f + this.f25171d), (float) (this.g + this.e));
        path.lineTo((float) (this.f + this.f25171d), ((float) (this.g + this.e)) - this.f25170c);
        path.arcTo(new RectF(((float) (this.f + this.f25171d)) - (this.f25170c * 2.0f), ((float) (this.g + this.e)) - (this.f25170c * 2.0f), (float) (this.f + this.f25171d), (float) (this.g + this.e)), DENSITY, 90.0f);
        path.close();
        canvas.drawPath(path, this.f25168a);
    }

    private void d(Canvas canvas) {
        Path path = new Path();
        path.moveTo((float) (this.f + this.f25171d), ((float) this.g) + this.f25170c);
        path.lineTo((float) (this.f + this.f25171d), (float) this.g);
        path.lineTo(((float) (this.f + this.f25171d)) - this.f25170c, (float) this.g);
        path.arcTo(new RectF(((float) (this.f + this.f25171d)) - (this.f25170c * 2.0f), (float) this.g, (float) (this.f + this.f25171d), ((float) this.g) + (this.f25170c * 2.0f)), -90.0f, 90.0f);
        path.close();
        canvas.drawPath(path, this.f25168a);
    }

    public void a(int i, int i2, float f2) {
        this.f25170c = f2;
        this.f25171d = i;
        this.e = i2;
    }

    public void draw(Canvas canvas) {
        this.f = getScrollX();
        this.g = getScrollY();
        Bitmap createBitmap = Bitmap.createBitmap(this.f + this.f25171d, this.g + this.e, Config.ARGB_8888);
        Canvas canvas2 = new Canvas(createBitmap);
        super.draw(canvas2);
        a(canvas2);
        d(canvas2);
        b(canvas2);
        c(canvas2);
        canvas.drawBitmap(createBitmap, DENSITY, DENSITY, this.f25169b);
        createBitmap.recycle();
    }
}