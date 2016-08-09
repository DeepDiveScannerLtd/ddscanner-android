package com.ddscanner.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.InstanceIDReceivedEvent;
import com.ddscanner.events.RemoteConfigFetchedEvent;
import com.ddscanner.events.UserSuccessfullyIdentifiedEvent;
import com.ddscanner.services.RegistrationIntentService;
import com.ddscanner.ui.views.DDProgressBarView;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.RemoteConfigManager;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.squareup.otto.Subscribe;

/**
 * Created by Vitaly on 29.11.2015.
 */
public class SplashActivity extends BaseAppCompatActivity {

    private static final String TAG = SplashActivity.class.getName();
    private static final int REQUEST_CODE_PLAY_SERVICES_RESOLUTION = 9000;

    private LocationManager locationManager;

    private Helpers helpers = new Helpers();

    private Handler h = new Handler();
    private Runnable runnable;
    private long activityShowTimestamp;

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

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

//        if (SharedPreferenceHelper.isFirstLaunch()) {
//            registerForGCM();
//        } else {
//            showMainActivity();
//        }

        RemoteConfigManager.initRemoteConfig();
    }

    private void registerForGCM() {
        if (!SharedPreferenceHelper.isUserAppIdReceived() && checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, REQUEST_CODE_PLAY_SERVICES_RESOLUTION).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    private boolean checkIsProvidersEnabled() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (checkIsProvidersEnabled()) {
                showMainActivity();
            } else {
                showAlertDialog();
            }
        }
    }

    private void showMainActivity() {
        final boolean isInternet = helpers.hasConnection(this);
        final boolean isLocation = checkIsProvidersEnabled();
        runnable = new Runnable() {
            @Override
            public void run() {
                MainActivity.show(SplashActivity.this, isInternet, isLocation);
                SplashActivity.this.finish();
            }
        };
        if (System.currentTimeMillis() - activityShowTimestamp < DDProgressBarView.ANIMATION_DURATION) {
            h.postDelayed(runnable, DDProgressBarView.ANIMATION_DURATION - (System.currentTimeMillis() - activityShowTimestamp));
        } else {
            h.post(runnable);
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                R.string.locatio_dialog_title)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_yes,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, 1);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.btn_no,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
       // h.removeCallbacks(runnable);
        DDScannerApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DDScannerApplication.activityResumed();
        if (!helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
        activityShowTimestamp = System.currentTimeMillis();
    }



    @Override
    protected void onStop() {
        super.onStop();
     //   h.removeCallbacks(runnable);
        DDScannerApplication.bus.unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        DDScannerApplication.bus.register(this);
    }

    @Subscribe
    public void onAppInstanceIdReceived(InstanceIDReceivedEvent event) {
        identifyUser("", "");
    }

    @Subscribe
    public void onUserIdentified(UserSuccessfullyIdentifiedEvent event) {
        showMainActivity();
        SharedPreferenceHelper.setIsFirstLaunch(false);
    }

    @Subscribe
    public void onRemoteConfigFetched(RemoteConfigFetchedEvent event) {
        Log.i(TAG, "onRemoteConfigFetched " + RemoteConfigManager.getFirebaseRemoteConfig().getBoolean(RemoteConfigManager.KEY_REQUIRE_APP_UPDATE));
        if (!RemoteConfigManager.getFirebaseRemoteConfig().getBoolean(RemoteConfigManager.KEY_REQUIRE_APP_UPDATE)) {
            if (SharedPreferenceHelper.isFirstLaunch()) {
                registerForGCM();
            } else {
                showMainActivity();
            }
        } else {
            APIUpdatedActivity.show(this);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        h.removeCallbacks(runnable);
        finish();
    }


}
