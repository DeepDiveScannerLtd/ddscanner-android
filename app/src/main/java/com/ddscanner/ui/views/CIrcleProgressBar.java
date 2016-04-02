package com.ddscanner.ui.views;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.ddscanner.R;
import com.facebook.drawee.drawable.*;

/**
 * Created by lashket on 22.3.16.
 */
public class CIrcleProgressBar extends com.facebook.drawee.drawable.ProgressBarDrawable {
    private int mlevel = 0;

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    int color = R.color.primary;

    final RectF oval = new RectF();

    int radius = 30;

    public CIrcleProgressBar(){
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected boolean onLevelChange(int level) {
        mlevel = level;
        invalidateSelf();
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (getHideWhenZero() && mlevel == 0) {
            return;
        }
        oval.set(canvas.getWidth() / 2 - radius, canvas.getHeight() / 2 - radius,
                canvas.getWidth() / 2 + radius, canvas.getHeight() / 2 + radius);

        drawCircle(canvas, mlevel, color);
    }


    private void drawCircle(Canvas canvas, float level, int color) {
        paint.setColor(color);
        float angle;
        angle = level * 360 / 10000;
        canvas.drawArc(oval, 0, Math.round(angle), false, paint);
    }


}
