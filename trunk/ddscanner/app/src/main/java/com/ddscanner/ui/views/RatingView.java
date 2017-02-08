package com.ddscanner.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ddscanner.utils.Helpers;

public class RatingView extends LinearLayout {

    public RatingView(Context context) {
        super(context);
        
    }

    public RatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
    }

    public RatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        
    }
    
    public void setRating(int rating, int filledResId, int emptyResId) {

        for (int i = 0; i < rating; i++) {
            ImageView filledStar = new ImageView(getContext());
            filledStar.setImageResource(filledResId);
            filledStar.setPadding(0,0,Math.round(Helpers.convertDpToPixel(3, getContext())),0);
            this.addView(filledStar);
        }

        for (int i = 0; i < 5 - rating; i++) {
            ImageView emptyStar = new ImageView(getContext());
            emptyStar.setImageResource(emptyResId);
            emptyStar.setPadding(0,0,Math.round(Helpers.convertDpToPixel(3, getContext())),0);
            this.addView(emptyStar);
        }
    }
}
