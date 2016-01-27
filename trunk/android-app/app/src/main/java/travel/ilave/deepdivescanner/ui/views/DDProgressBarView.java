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
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

import travel.ilave.deepdivescanner.R;

public class DDProgressBarView extends View {

    private static final String TAG = DDProgressBarView.class.getName();

    public static final int ANIMATION_DURATION = 1500;

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
        aPoints.add(new PointF(296 + progressBarLeftTopX, 304 + progressBarLeftTopY));
        aPoints.add(new PointF(277 + progressBarLeftTopX, 304 + progressBarLeftTopY));
        aPoints.add(new PointF(274 + progressBarLeftTopX, 305 + progressBarLeftTopY));
        aPoints.add(new PointF(189 + progressBarLeftTopX, 366 + progressBarLeftTopY));
        aPoints.add(new PointF(160 + progressBarLeftTopX, 357 + progressBarLeftTopY));
        aPoints.add(new PointF(133 + progressBarLeftTopX, 346 + progressBarLeftTopY));
        aPoints.add(new PointF(104 + progressBarLeftTopX, 331 + progressBarLeftTopY));
        aPoints.add(new PointF(81 + progressBarLeftTopX, 316 + progressBarLeftTopY));
        aPoints.add(new PointF(62 + progressBarLeftTopX, 300 + progressBarLeftTopY));
        aPoints.add(new PointF(44 + progressBarLeftTopX, 280 + progressBarLeftTopY));
        aPoints.add(new PointF(28 + progressBarLeftTopX, 255 + progressBarLeftTopY));
        aPoints.add(new PointF(18 + progressBarLeftTopX, 230 + progressBarLeftTopY));
        aPoints.add(new PointF(11 + progressBarLeftTopX, 195 + progressBarLeftTopY));
        aPoints.add(new PointF(17 + progressBarLeftTopX, 164 + progressBarLeftTopY));
        aPoints.add(new PointF(29 + progressBarLeftTopX, 134 + progressBarLeftTopY));
        aPoints.add(new PointF(45 + progressBarLeftTopX, 109 + progressBarLeftTopY));
        aPoints.add(new PointF(64 + progressBarLeftTopX, 89 + progressBarLeftTopY));
        aPoints.add(new PointF(88 + progressBarLeftTopX, 70 + progressBarLeftTopY));
        aPoints.add(new PointF(112 + progressBarLeftTopX, 55 + progressBarLeftTopY));
        aPoints.add(new PointF(139 + progressBarLeftTopX, 42 + progressBarLeftTopY));
        aPoints.add(new PointF(168 + progressBarLeftTopX, 31 + progressBarLeftTopY));
        aPoints.add(new PointF(196 + progressBarLeftTopX, 23 + progressBarLeftTopY));
        aPoints.add(new PointF(227 + progressBarLeftTopX, 16 + progressBarLeftTopY));
        aPoints.add(new PointF(255 + progressBarLeftTopX, 13 + progressBarLeftTopY));
        aPoints.add(new PointF(289 + progressBarLeftTopX, 11 + progressBarLeftTopY));
        aPoints.add(new PointF(322 + progressBarLeftTopX, 13 + progressBarLeftTopY));
        aPoints.add(new PointF(350 + progressBarLeftTopX, 16 + progressBarLeftTopY));
        aPoints.add(new PointF(381 + progressBarLeftTopX, 23 + progressBarLeftTopY));
        aPoints.add(new PointF(409 + progressBarLeftTopX, 31 + progressBarLeftTopY));
        aPoints.add(new PointF(438 + progressBarLeftTopX, 42 + progressBarLeftTopY));
        aPoints.add(new PointF(465 + progressBarLeftTopX, 55 + progressBarLeftTopY));
        aPoints.add(new PointF(489 + progressBarLeftTopX, 70 + progressBarLeftTopY));
        aPoints.add(new PointF(513 + progressBarLeftTopX, 89 + progressBarLeftTopY));
        aPoints.add(new PointF(532 + progressBarLeftTopX, 109 + progressBarLeftTopY));
        aPoints.add(new PointF(548 + progressBarLeftTopX, 134 + progressBarLeftTopY));
        aPoints.add(new PointF(560 + progressBarLeftTopX, 164 + progressBarLeftTopY));
        aPoints.add(new PointF(566 + progressBarLeftTopX, 195 + progressBarLeftTopY));
        aPoints.add(new PointF(559 + progressBarLeftTopX, 230 + progressBarLeftTopY));
        aPoints.add(new PointF(549 + progressBarLeftTopX, 255 + progressBarLeftTopY));
        aPoints.add(new PointF(533 + progressBarLeftTopX, 280 + progressBarLeftTopY));
        aPoints.add(new PointF(515 + progressBarLeftTopX, 300 + progressBarLeftTopY));
        aPoints.add(new PointF(496 + progressBarLeftTopX, 316 + progressBarLeftTopY));
        aPoints.add(new PointF(473 + progressBarLeftTopX, 331 + progressBarLeftTopY));
        aPoints.add(new PointF(444 + progressBarLeftTopX, 346 + progressBarLeftTopY));
        aPoints.add(new PointF(417 + progressBarLeftTopX, 357 + progressBarLeftTopY));
        aPoints.add(new PointF(388 + progressBarLeftTopX, 366 + progressBarLeftTopY));
        aPoints.add(new PointF(303 + progressBarLeftTopX, 305 + progressBarLeftTopY));
        aPoints.add(new PointF(300 + progressBarLeftTopX, 304 + progressBarLeftTopY));
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
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        progressBarLeftTopX = (w - backgroundBitmap.getWidth()) / 2;
        progressBarLeftTopY = (h - backgroundBitmap.getHeight()) / 2;

        initOnSizeChanged();
    }

}
