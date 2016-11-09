package com.ddscanner.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ddscanner.R;
import com.ddscanner.utils.Helpers;

public class AchievementCountryFlagView extends View {

    private static final String TAG = AchievementCountryFlagView.class.getSimpleName();

    private Bitmap backgrondBitmap;
    private Bitmap flagBitmap;
    private float koefX = 1;
    private float koefY = 1;
    private int flagHeightPx;
    private int flagWidthPx;
    private int backGroundWidthPx;
    private int backgroundHeightPx;

    public AchievementCountryFlagView(Context context) {
        super(context);
        init(context);
    }

    public AchievementCountryFlagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AchievementCountryFlagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        flagHeightPx = Math.round(Helpers.convertDpToPixel(15, context));
        flagWidthPx = Math.round(Helpers.convertDpToPixel(15, context));
//        backgroundHeightPx = Math.round(Helpers.convertDpToPixel(33, context));
//        backGroundWidthPx = Math.round(Helpers.convertDpToPixel(33, context));
//        if (backgrondBitmap == null) {
//            backgrondBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circle_flag);
//            backgrondBitmap = Bitmap.createScaledBitmap(backgrondBitmap, backGroundWidthPx, backgroundHeightPx, false);
//        }
    }

    public void setFlagBitmap(int resourceId) {
        flagBitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        flagBitmap = Bitmap.createScaledBitmap(flagBitmap, flagWidthPx, flagHeightPx, false);
    }

    public void setFlagBitmap(Bitmap bitmap) {
        flagBitmap = bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (backgrondBitmap == null) {
            backgrondBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circle_flag);
            backgrondBitmap = Bitmap.createScaledBitmap(backgrondBitmap, getMeasuredWidth(), getMeasuredHeight(), false);
        }
        Log.i(TAG, String.valueOf(getMeasuredHeight()));
        Log.i(TAG, String.valueOf(getMeasuredWidth()));
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        if (flagBitmap != null) {
            canvas.drawBitmap(backgrondBitmap, 0, 0, null);
            canvas.drawBitmap(getCroppedBitmap(flagBitmap, flagWidthPx), (getMeasuredWidth() - flagWidthPx) / 2, (getMeasuredHeight() - flagHeightPx) / 2, null);
        }
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;

        if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
            float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
            float factor = smallest / radius;
            sbmp = Bitmap.createScaledBitmap(bmp, (int)(bmp.getWidth() / factor), (int)(bmp.getHeight() / factor), false);
        } else {
            sbmp = bmp;
        }

        Bitmap output = Bitmap.createBitmap(radius, radius,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, radius, radius);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(radius / 2,
                radius / 2, radius / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        koefX = (float) w / 75;
        koefY = (float) h / 75;
    }

}
