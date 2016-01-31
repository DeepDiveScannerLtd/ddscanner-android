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
    private float koefX = 1;
    private float koefY = 1;

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
        aPoints.add(new PointF(296 / koefX + progressBarLeftTopX, 304 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(277 / koefX + progressBarLeftTopX, 304 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(274 / koefX + progressBarLeftTopX, 305 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(189 / koefX + progressBarLeftTopX, 366 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(160 / koefX + progressBarLeftTopX, 357 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(133 / koefX + progressBarLeftTopX, 346 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(104 / koefX + progressBarLeftTopX, 331 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(81 / koefX + progressBarLeftTopX, 316 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(62 / koefX + progressBarLeftTopX, 300 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(44 / koefX + progressBarLeftTopX, 280 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(28 / koefX + progressBarLeftTopX, 255 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(18 / koefX + progressBarLeftTopX, 230 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(11 / koefX + progressBarLeftTopX, 195 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(17 / koefX + progressBarLeftTopX, 164 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(29 / koefX + progressBarLeftTopX, 134 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(45 / koefX + progressBarLeftTopX, 109 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(64 / koefX + progressBarLeftTopX, 89 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(88 / koefX + progressBarLeftTopX, 70 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(112 / koefX + progressBarLeftTopX, 55 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(139 / koefX + progressBarLeftTopX, 42 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(168 / koefX + progressBarLeftTopX, 31 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(196 / koefX + progressBarLeftTopX, 23 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(227 / koefX + progressBarLeftTopX, 16 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(255 / koefX + progressBarLeftTopX, 13 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(289 / koefX + progressBarLeftTopX, 11 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(322 / koefX + progressBarLeftTopX, 13 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(350 / koefX + progressBarLeftTopX, 16 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(381 / koefX + progressBarLeftTopX, 23 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(409 / koefX + progressBarLeftTopX, 31 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(438 / koefX + progressBarLeftTopX, 42 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(465 / koefX + progressBarLeftTopX, 55 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(489 / koefX + progressBarLeftTopX, 70 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(513 / koefX + progressBarLeftTopX, 89 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(532 / koefX + progressBarLeftTopX, 109 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(548 / koefX + progressBarLeftTopX, 134 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(560 / koefX + progressBarLeftTopX, 164 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(566 / koefX + progressBarLeftTopX, 195 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(559 / koefX + progressBarLeftTopX, 230 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(549 / koefX + progressBarLeftTopX, 255 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(533 / koefX + progressBarLeftTopX, 280 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(515 / koefX + progressBarLeftTopX, 300 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(496 / koefX + progressBarLeftTopX, 316 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(473 / koefX + progressBarLeftTopX, 331 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(444 / koefX + progressBarLeftTopX, 346 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(417 / koefX + progressBarLeftTopX, 357 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(388 / koefX + progressBarLeftTopX, 366 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(303 / koefX + progressBarLeftTopX, 305 / koefY + progressBarLeftTopY));
        aPoints.add(new PointF(300 / koefX + progressBarLeftTopX, 304 / koefY + progressBarLeftTopY));
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
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);
        canvas.drawBitmap(backgroundBitmap, progressBarLeftTopX, progressBarLeftTopY, paint);
        //animate the sprite
        Matrix mxTransform = new Matrix();
        movementPathMeasure.getMatrix((Float) valueAnimator.getAnimatedValue(), mxTransform, PathMeasure.POSITION_MATRIX_FLAG);
        mxTransform.preTranslate(-circleBitmap.getWidth() / 2, -circleBitmap.getHeight() / 2);
        canvas.drawBitmap(circleBitmap, mxTransform, null);
        canvas.scale(canvas.getWidth() / koefX, canvas.getHeight() / koefY);
        canvas.translate(canvas.getWidth(), canvas.getHeight());
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        koefX = (float)1920.0 / h;
        koefY = (float)1080.0 / w;
       progressBarLeftTopX = (w - backgroundBitmap.getWidth()) / 2;
        progressBarLeftTopY = (h - backgroundBitmap.getHeight()) / 2;
        initOnSizeChanged();
    }

}