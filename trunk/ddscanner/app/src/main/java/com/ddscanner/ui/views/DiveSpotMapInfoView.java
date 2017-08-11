package com.ddscanner.ui.views;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.utils.Constants;

import java.util.HashMap;
import java.util.Map;

public class DiveSpotMapInfoView extends RelativeLayout {
    
    private TextView diveSpotName;
    private TextView diveSpotType;
    private RatingView ratingView;
    private Map<String, Integer> infoWindowBackgroundImages = new HashMap<>();
    
    public DiveSpotMapInfoView(Context context) {
        super(context);
        init();
    }

    public DiveSpotMapInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DiveSpotMapInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        inflate(getContext(), R.layout.view_dive_spot_map_info, this);
        
        diveSpotName = findViewById(R.id.dive_spot_title);
        diveSpotType = findViewById(R.id.object);
        ratingView = findViewById(R.id.rating_view);

        infoWindowBackgroundImages.put(Constants.OBJECT_TYPE_WRECK, R.drawable.iw_card_wreck);
        infoWindowBackgroundImages.put(Constants.OBJECT_TYPE_CAVE, R.drawable.iw_card_cave);
        infoWindowBackgroundImages.put(Constants.OBJECT_TYPE_REEF, R.drawable.iw_card_reef);
        infoWindowBackgroundImages.put(Constants.OBJECT_TYPE_OTHER, R.drawable.iw_card_other);

    }
    
    
    public void show(DiveSpotShort diveSpotShort) {
        this.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        setVisibility(View.VISIBLE);
                    }
                });
        ratingView.removeAllViews();
        diveSpotName.setText(diveSpotShort.getName());
        diveSpotType.setText(diveSpotShort.getObject());
        ratingView.setRating(Math.round(diveSpotShort.getRating()), R.drawable.ic_iw_star_full, R.drawable.ic_iw_star_empty);
        setBackgroundResource(infoWindowBackgroundImages.get(diveSpotShort.getObject()));
    }

    public void hide(int diveSpotInfoHeight) {
        this.animate()
                .translationY(diveSpotInfoHeight)
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        setVisibility(View.GONE);
                    }
                });
    }

}
