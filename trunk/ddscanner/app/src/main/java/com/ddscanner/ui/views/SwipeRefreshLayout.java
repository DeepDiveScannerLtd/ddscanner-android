package com.ddscanner.ui.views;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.ddscanner.R;

public class SwipeRefreshLayout extends android.support.v4.widget.SwipeRefreshLayout {


    public SwipeRefreshLayout(Context context) {
        super(context);
        init(context);
    }

    public SwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setColorSchemeColors(ContextCompat.getColor(context, R.color.primary));
    }

}
