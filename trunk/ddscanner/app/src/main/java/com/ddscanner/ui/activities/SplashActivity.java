package com.ddscanner.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.InstanceIDReceivedEvent;
import com.ddscanner.events.UserIdentificationFailedEvent;
import com.ddscanner.events.UserSuccessfullyIdentifiedEvent;
import com.ddscanner.services.RegistrationIntentService;
import com.ddscanner.ui.views.DDProgressBarView;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LocationHelper;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.squareup.otto.Subscribe;

public class SplashActivity extends BaseAppCompatActivity {

    private static final String TAG = SplashActivity.class.getName();

    private Helpers helpers = new Helpers();

    private Handler handler = new Handler();
    private Runnable showMainActivityRunnable;
    private long activityShowTimestamp;

    private LinearLayout mainLayout;
    private Animation fadeInAnimation;

    private TextView progressMessage;

    @Override
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
        activityShowTimestamp = System.currentTimeMillis();

        progressMessage = (TextView) findViewById(R.id.message);

        mainLayout = (LinearLayout) findViewById(R.id.main);

        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);

        mainLayout.startAnimation(fadeInAnimation);

        if (SharedPreferenceHelper.isFirstLaunch()) {
            registerForGCM();
        } else {
            showMainActivity();
        }

 //       RemoteConfigManager.initRemoteConfig();
    }

    private void registerForGCM() {
        if (!SharedPreferenceHelper.isUserAppIdReceived()) {
            if (checkPlayServices()) {
                progressMessage.setText(R.string.start_process_register_for_gcm);
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            } else {
                // No need to handle. This case was handled in checkPlayServices()
            }
        } else {
            // This means we've received appId but failed to make identify request.Try again
            progressMessage.setText(R.string.start_process_register_for_ddscanner);
            identifyUser("", "");
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, ActivitiesRequestCodes.REQUEST_CODE_SPLASH_ACTIVITY_PLAY_SERVICES_RESOLUTION).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    private void showMainActivity() {
        Log.i(TAG, "showMainActivity");
        final boolean isInternet = helpers.hasConnection(this);
        showMainActivityRunnable = new Runnable() {
            @Override
            public void run() {
                MainActivity.show(SplashActivity.this, isInternet);
                SplashActivity.this.finish();
            }
        };
        if (System.currentTimeMillis() - activityShowTimestamp < DDProgressBarView.ANIMATION_DURATION) {
            handler.postDelayed(showMainActivityRunnable, DDProgressBarView.ANIMATION_DURATION - (System.currentTimeMillis() - activityShowTimestamp));
        } else {
            handler.post(showMainActivityRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
       // handler.removeCallbacks(showMainActivityRunnable);
        DDScannerApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DDScannerApplication.activityResumed();
        if (!helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
     //   handler.removeCallbacks(showMainActivityRunnable);
        DDScannerApplication.bus.unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        DDScannerApplication.bus.register(this);
    }

    @Subscribe
    public void onAppInstanceIdReceived(InstanceIDReceivedEvent event) {
        progressMessage.setText(R.string.start_process_register_for_ddscanner);
        identifyUser("", "");
    }

    @Subscribe
    public void onUserIdentified(UserSuccessfullyIdentifiedEvent event) {
        progressMessage.setText("");
        showMainActivity();
        SharedPreferenceHelper.setIsFirstLaunch(false);
    }

    @Subscribe
    public void onUserIdentificationFailed(UserIdentificationFailedEvent event) {
        DialogUtils.showConnectionErrorDialog(SplashActivity.this);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(showMainActivityRunnable);
        finish();
    }

}