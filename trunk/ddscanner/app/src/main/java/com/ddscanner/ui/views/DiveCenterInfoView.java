package com.ddscanner.ui.views;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.BaseMapEntity;
import com.ddscanner.utils.Constants;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

public class DiveCenterInfoView extends RelativeLayout {

    private Marker marker;
    private TextView diveCenterName;
    private TextView address;
    private boolean isShown = false;
    private String lastDcId;

    public DiveCenterInfoView(Context context) {
        super(context);
        init();
    }

    public DiveCenterInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DiveCenterInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_dive_center_info, this);

        diveCenterName = findViewById(R.id.dive_center_name);
        address = findViewById(R.id.address);

    }

    public void show(BaseMapEntity diveCenter, Marker marker) {
        isShown = true;
        if (this.marker != null) {
            try {
                this.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dc));
            } catch (Exception ignored) {

            }
        }
        this.marker = marker;
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
//        diveSpotType.setText(diveCenter.getObject());
        address.setText(diveCenter.getAddresses().get(0).getName());
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dc_selected));
        lastDcId = String.valueOf(diveCenter.getId());
    }

    public void hide(int diveSpotInfoHeight) {
        isShown = false;
        if (marker != null) {
            try {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dc));
            } catch (Exception ignored) {

            }
        }
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

    public boolean isShown() {
        return this.isShown;
    }

    public String getLastDcId() {
        return lastDcId;
    }

}
