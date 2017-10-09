package com.ddscanner.ui.views;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ddscanner.R;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.MapboxMap;

public class MapControlView extends RelativeLayout implements View.OnClickListener {

    ImageView btnZoomPlus;
    ImageView btnZoomMinus;
    ImageView btnGoToMyLocation;
    MapboxMap mapboxMap;

    public MapControlView(Context context) {
        super(context);
        init(context);
    }

    public MapControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MapControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_map_control, this);
        btnGoToMyLocation = findViewById(R.id.go_to_my_location);
        btnZoomMinus = findViewById(R.id.zoom_minus);
        btnZoomPlus = findViewById(R.id.zoom_plus);
    }

    public void appendWithMap(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        btnZoomPlus.setOnClickListener(this);
        btnZoomMinus.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.zoom_minus:
                mapboxMap.animateCamera(CameraUpdateFactory.zoomOut());
                break;
            case R.id.zoom_plus:
                mapboxMap.animateCamera(CameraUpdateFactory.zoomIn());
                break;
        }
    }
}
