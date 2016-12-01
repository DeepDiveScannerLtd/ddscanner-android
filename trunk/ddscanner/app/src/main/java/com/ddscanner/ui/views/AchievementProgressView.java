package com.ddscanner.ui.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.ddscanner.R;

import java.util.ArrayList;
import java.util.List;

public class AchievementProgressView extends View {

    private int framesPerSecond = 60;
    private long animationDuration = 1800;
    private float animationLength;
    private List<PointF> points = new ArrayList<>();
    private Paint linePaint;
    private Paint backgroundPaint;
    private Bitmap sharkBitmap;
    private Path linePath = new Path();
    private float koefX = 1;
    private float koefY = 1;
    private Path tempPath = new Path();
    private ValueAnimator valueAnimator;
    private ValueAnimator sharkValuesAnimator;
    private PathMeasure linePathMeasure;
    private long startTime;
    private Matrix matrix = new Matrix();
    private Matrix sharkMatrix = new Matrix();
    Paint paintRect= new Paint();
    private float percents;
    private Camera camera = new Camera();

    private Bitmap backgroundBitmap;

    public AchievementProgressView(Context context) {
        super(context);
        init(context);

        this.startTime = System.currentTimeMillis();
        this.postInvalidate();
    }

    public AchievementProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

        this.startTime = System.currentTimeMillis();
        this.postInvalidate();
    }

    public AchievementProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

        this.startTime = System.currentTimeMillis();
        this.postInvalidate();
    }

    private void init(Context context) {
        if (sharkBitmap == null) {
            sharkBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_shark);
        }
        paintRect.setColor(ContextCompat.getColor(context, android.R.color.transparent));
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#2e80a0"));
        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#bfdbf2"));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
     //   linePaint.setMaskFilter(new BlurMaskFilter(1, BlurMaskFilter.Blur.NORMAL));
        linePaint.setPathEffect(new DashPathEffect(new float[] {26,18}, 0));
    }

    private void initAfterFindingKoefs() {

        linePaint.setStrokeWidth(6 * koefX);

        linePath.reset();

        linePath.moveTo(18 * koefX,40 * koefY);
        linePath.quadTo(61 * koefX, 14 * koefY, 105 * koefX, 26 * koefY);
        linePath.quadTo(146 * koefX, 34 * koefY, 165 * koefX, 22 * koefY);
        linePath.quadTo(201 * koefX, 1 * koefY, 233 * koefX, 34 * koefY);
        linePath.quadTo(246 * koefX, 49 * koefY, 286 * koefX, 32 * koefY);
        linePath.quadTo(330 * koefX, 14 * koefY, 372 * koefX, 34 * koefY);
        linePath.quadTo(395 * koefX, 46 * koefY, 438 * koefX, 22 * koefY);
        linePath.quadTo(480 * koefX, 2 * koefY, 523 * koefX, 31 * koefY);
        linePath.quadTo(545 * koefX, 44 * koefY, 555 * koefX, 40 * koefY);

        linePathMeasure = new PathMeasure(linePath, false);
        this.animationLength = linePathMeasure.getLength() * percents;
        valueAnimator =  new ValueAnimator().ofFloat(0, linePathMeasure.getLength() * percents);
        valueAnimator.setDuration(animationDuration);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0,0, getMeasuredWidth(), getMeasuredHeight(), paintRect);
        long elapsedTime = System.currentTimeMillis() - startTime;
        tempPath.reset();
        matrix.reset();
        float[] points = new float[20];
        linePathMeasure.getMatrix((Float)valueAnimator.getAnimatedValue(), matrix, PathMeasure.POSITION_MATRIX_FLAG);
        linePathMeasure.getSegment(0, (Float) valueAnimator.getAnimatedValue(), tempPath, true);
        canvas.drawPath(tempPath, linePaint);
        matrix.mapPoints(points);
        canvas.drawBitmap(sharkBitmap, points[0], (getMeasuredHeight() - sharkBitmap.getHeight()) / 2, null);

        if(elapsedTime < animationDuration && points[0] < animationLength) {
            this.postInvalidateDelayed(1000 / framesPerSecond);
        }
    }

    public void setPercent(float percents) {
        this.percents = percents;
        initAfterFindingKoefs();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        koefX = (float) w / 660;
        koefY = (float) h / 56;
        initAfterFindingKoefs();
        invalidate();
    }

    @Override
    public void invalidate() {
        if (percents != 0f && koefX != 0f && koefY != 0) {
            super.invalidate();
        }
    }
}
