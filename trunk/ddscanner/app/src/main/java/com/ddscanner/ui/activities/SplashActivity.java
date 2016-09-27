package com.ddscanner.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.events.InstanceIDReceivedEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.services.RegistrationIntentService;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.ui.views.DDProgressBarView;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.EventTrackerHelper;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.squareup.otto.Subscribe;

public class SplashActivity extends BaseAppCompatActivity implements InfoDialogFragment.DialogClosedListener {

    private static final String TAG = SplashActivity.class.getName();

    private Handler handler = new Handler();
    private Runnable showMainActivityRunnable;
    private long activityShowTimestamp;

    private TextView progressMessage;

    private DDScannerRestClient.ResultListener<Void> identifyResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            progressMessage.setText("");
            showMainActivity();
            SharedPreferenceHelper.setIsFirstLaunch(false);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_SPLASH_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case USER_NOT_FOUND_ERROR_C801:
                    // Currently handle it as an unexpected error. Later identify request will be removed
                    Crashlytics.log("801 error on identify");
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_SPLASH_ACTIVITY_UNEXPECTED_ERROR, false);
                    break;
            }
        }
    };

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

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);

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
            DDScannerApplication.getDdScannerRestClient().postIdentifyUser("", "", identifyResultListener);
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
        showMainActivityRunnable = new Runnable() {
            @Override
            public void run() {
                MainActivity.show(SplashActivity.this, Helpers.hasConnection(SplashActivity.this));
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
        if (!Helpers.hasConnection(this)) {
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
        DDScannerApplication.getDdScannerRestClient().postIdentifyUser("", "", identifyResultListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(showMainActivityRunnable);
        finish();
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_SPLASH_ACTIVITY_UNEXPECTED_ERROR:
            case DialogsRequestCodes.DRC_SPLASH_ACTIVITY_FAILED_TO_CONNECT:
                finish();
                break;
        }
    }
}