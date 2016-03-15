package com.ddscanner.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.ddscanner.R;
import com.ddscanner.services.GPSTracker;
import com.ddscanner.ui.views.DDProgressBarView;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Vitaly on 29.11.2015.
 */
public class SplashActivity extends Activity {

    private LatLng latLng;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        setContentView(R.layout.activity_splash);
        GPSTracker gps = new GPSTracker(SplashActivity.this);
        latLng = new LatLng(gps.getLatitude(), gps.getLongitude());
        System.out.println(latLng);
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashActivity.this.finish();
                CityActivity.show(SplashActivity.this, latLng);
            }
        }, DDProgressBarView.ANIMATION_DURATION);
    }

}
