package travel.ilave.deepdivescanner.ui.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

import travel.ilave.deepdivescanner.R;

public class DDProgressBarView extends View {

    private static final String TAG = DDProgressBarView.class.getName();

    public static final int ANIMATION_DURATION = 3500;

    private static Bitmap circleBitmap;
    private static Bitmap backgroundBitmap;

    private ValueAnimator valueAnimator;

    //points defining our curve
    private List<PointF> aPoints = new ArrayList<PointF>();
    private Paint paint;
    private Paint backgroundPaint;
    private Path circleMovementPath = new Path();
    private PathMeasure movementPathMeasure;

    private int progressBarLeftTopX;
    private int progressBarLeftTopY;
    private float koef = 1;

    public DDProgressBarView(Context context) {
        super(context);
        init(context);
    }

    public DDProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public DDProgressBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        //load background


        if (backgroundBitmap == null) {
            backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mask);
        }

        //load sprite
        if (circleBitmap == null) {
            circleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
        }

        //init paint object
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.rgb(0, 148, 255));

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.TRANSPARENT);
    }

    private void initOnSizeChanged() {
        //init random set of points
        aPoints.add(new PointF(296 / koef + progressBarLeftTopX, 304 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(277 / koef + progressBarLeftTopX, 304 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(274 / koef + progressBarLeftTopX, 305 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(189 / koef + progressBarLeftTopX, 366 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(160 / koef + progressBarLeftTopX, 357 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(133 / koef + progressBarLeftTopX, 346 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(104 / koef + progressBarLeftTopX, 331 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(81 / koef + progressBarLeftTopX, 316 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(62 / koef + progressBarLeftTopX, 300 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(44 / koef + progressBarLeftTopX, 280 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(28 / koef + progressBarLeftTopX, 255 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(18 / koef + progressBarLeftTopX, 230 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(11 / koef + progressBarLeftTopX, 195 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(17 / koef + progressBarLeftTopX, 164 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(29 / koef + progressBarLeftTopX, 134 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(45 / koef + progressBarLeftTopX, 109 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(64 / koef + progressBarLeftTopX, 89 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(88 / koef + progressBarLeftTopX, 70 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(112 / koef + progressBarLeftTopX, 55 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(139 / koef + progressBarLeftTopX, 42 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(168 / koef + progressBarLeftTopX, 31 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(196 / koef + progressBarLeftTopX, 23 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(227 / koef + progressBarLeftTopX, 16 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(255 / koef + progressBarLeftTopX, 13 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(289 / koef + progressBarLeftTopX, 11 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(322 / koef + progressBarLeftTopX, 13 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(350 / koef + progressBarLeftTopX, 16 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(381 / koef + progressBarLeftTopX, 23 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(409 / koef + progressBarLeftTopX, 31 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(438 / koef + progressBarLeftTopX, 42 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(465 / koef + progressBarLeftTopX, 55 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(489 / koef + progressBarLeftTopX, 70 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(513 / koef + progressBarLeftTopX, 89 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(532 / koef + progressBarLeftTopX, 109 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(548 / koef + progressBarLeftTopX, 134 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(560 / koef + progressBarLeftTopX, 164 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(566 / koef + progressBarLeftTopX, 195 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(559 / koef + progressBarLeftTopX, 230 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(549 / koef + progressBarLeftTopX, 255 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(533 / koef + progressBarLeftTopX, 280 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(515 / koef + progressBarLeftTopX, 300 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(496 / koef + progressBarLeftTopX, 316 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(473 / koef + progressBarLeftTopX, 331 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(444 / koef + progressBarLeftTopX, 346 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(417 / koef + progressBarLeftTopX, 357 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(388 / koef + progressBarLeftTopX, 366 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(303 / koef + progressBarLeftTopX, 305 / koef + progressBarLeftTopY));
        aPoints.add(new PointF(300 / koef + progressBarLeftTopX, 304 / koef + progressBarLeftTopY));
        //init smooth curve
        PointF point = aPoints.get(0);
        circleMovementPath.moveTo(point.x, point.y);
        for (int i = 0; i < aPoints.size() - 1; i++) {
            point = aPoints.get(i);
            PointF next = aPoints.get(i + 1);
            circleMovementPath.quadTo(point.x, point.y, (next.x + point.x) / 2, (point.y + next.y) / 2);
        }
        point = aPoints.get(aPoints.size() - 1);
        PointF next = aPoints.get(0);
        circleMovementPath.quadTo(point.x, point.y, (next.x + point.x) / 2, (point.y + next.y) / 2);
        movementPathMeasure = new PathMeasure(circleMovementPath, false);

        valueAnimator = ValueAnimator.ofFloat(0, movementPathMeasure.getLength());
        valueAnimator.setDuration(ANIMATION_DURATION);
        valueAnimator.setInterpolator(new AccelerateInterpolator(0.8f));
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.start();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println(canvas.getWidth());
        System.out.println("----------" + koef);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);
        canvas.drawBitmap(backgroundBitmap, progressBarLeftTopX, progressBarLeftTopY, paint);
        //animate the sprite
        Matrix mxTransform = new Matrix();
        movementPathMeasure.getMatrix((Float) valueAnimator.getAnimatedValue(), mxTransform, PathMeasure.POSITION_MATRIX_FLAG);
        mxTransform.preTranslate(-circleBitmap.getWidth() / 2, -circleBitmap.getHeight() / 2);
        canvas.drawBitmap(circleBitmap, mxTransform, null);
        canvas.scale(canvas.getWidth() / koef, canvas.getHeight() / koef);
        canvas.translate(canvas.getWidth(), canvas.getHeight());
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        koef = (float)1920.0 / h;
       progressBarLeftTopX = (w - backgroundBitmap.getWidth()) / 2;
        progressBarLeftTopY = (h - backgroundBitmap.getHeight()) / 2;
        initOnSizeChanged();
    }

}
