package com.ddscanner.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.ddscanner.R;
import com.ddscanner.utils.Helpers;

public class AchievementLineProgresView extends View {

    private Paint rectanglePaint = new Paint();
    private RectF progressRectangle = new RectF();
    private float progress = 0.0f;

    public AchievementLineProgresView(Context context) {
        super(context);
        init(context);
    }

    public AchievementLineProgresView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AchievementLineProgresView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        rectanglePaint.setColor(ContextCompat.getColor(context, R.color.achievement_progress_color));
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        progressRectangle.set(0,0, getMeasuredWidth() * progress, getMeasuredHeight());
        canvas.drawRoundRect(progressRectangle, Helpers.convertDpToPixel(2, getContext()), Helpers.convertDpToPixel(2, getContext()), rectanglePaint);
    }

}
}
