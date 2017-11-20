package com.ddscanner.ui.views;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.utils.Constants;
import com.google.android.gms.maps.model.Marker;

public class DiveCenterInfoView extends RelativeLayout {

    private Marker marker;
    private TextView diveCenterName;
    private RatingView ratingView;
    private TextView address;

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
        ratingView = findViewById(R.id.rating_view);

    }


}
