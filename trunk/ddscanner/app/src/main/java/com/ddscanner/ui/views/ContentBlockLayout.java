package com.ddscanner.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.ddscanner.R;

public class ContentBlockLayout extends LinearLayout {

    private Paint paint;

    public ContentBlockLayout(Context context) {
        super(context);
        init(context);
    }

    public ContentBlockLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ContentBlockLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context,R.color.black_text));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0,0,getMeasuredWidth(), 1, paint);
        canvas.drawRect(0, getMeasuredHeight() - 1,getMeasuredWidth(), getMeasuredHeight(), paint);
    }
}
