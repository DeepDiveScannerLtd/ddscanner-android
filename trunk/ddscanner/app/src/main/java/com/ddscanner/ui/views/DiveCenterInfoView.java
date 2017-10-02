package com.ddscanner.ui.views;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.DiveCenter;
import com.ddscanner.screens.user.profile.UserProfileActivity;

public class DiveCenterInfoView extends RelativeLayout implements View.OnClickListener {

    TextView diveCenterName;
    TextView address;
    RatingView ratingView;
    boolean isShown;
    private String lastId;

    public DiveCenterInfoView(Context context) {
        super(context);
        init(context);
    }

    public DiveCenterInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DiveCenterInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_dive_center_info, this);
        setOnClickListener(this);
        ratingView = findViewById(R.id.rating);
        diveCenterName = findViewById(R.id.dive_center_name);
        address = findViewById(R.id.address);
    }

    public void show(DiveCenter diveCenter) {
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
        diveCenterName.setText(diveCenter.getName());
        ratingView.removeAllViews();
        ratingView.setRating(Math.round(diveCenter.getRating()), R.drawable.ic_iw_star_full, R.drawable.ic_iw_star_empty);
        isShown = true;
        lastId = diveCenter.getId();
    }

    public void hide(int diveSpotInfoHeight) {
        try {
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
            isShown = false;
        } catch (NullPointerException ignored) {

        }
    }

    @Override
    public void onClick(View view) {
        UserProfileActivity.show(getContext(), lastId, 0);
    }
}
