package travel.ilave.deepdivescanner.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.ui.views.DDProgressBarView;

/**
 * Created by Vitaly on 29.11.2015.
 */
public class SplashActivity extends Activity {

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

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashActivity.this.finish();
                MainActivity.show(SplashActivity.this);
            }
        }, 3 * DDProgressBarView.ANIMATION_DURATION);
    }

}
