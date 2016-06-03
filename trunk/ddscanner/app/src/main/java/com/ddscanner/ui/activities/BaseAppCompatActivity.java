package com.ddscanner.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.ddscanner.utils.LocationHelper;
import com.ddscanner.utils.LogUtils;

public class BaseAppCompatActivity extends AppCompatActivity {

    private static final String TAG = BaseAppCompatActivity.class.getName();

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1301;
    private static final int REQUEST_CODE_LOCATION_PROVIDERS = 1302;

    private LocationHelper locationHelper;

    /**
     * Call this method to get user location. Subscribe to LocationReadyEvent for result
     */
    public void getLocation() {
        if (locationHelper == null) {
            locationHelper = new LocationHelper(this);
        }
        try {
            locationHelper.checkLocationConditions();
            locationHelper.requestLocation();
        } catch (LocationHelper.LocationProvidersNotAvailableException e) {
            LogUtils.i(TAG, "location providers not available. starting LocationProvidersNotAvailableActivity");
            LocationProvidersNotAvailableActivity.showForResult(this, REQUEST_CODE_LOCATION_PROVIDERS);
        } catch (LocationHelper.LocationPPermissionsNotGrantedException e) {
            LogUtils.i(TAG, "location permission not granted. starting LocationPermissionNotGrantedActivity");
            LocationPermissionNotGrantedActivity.showForResult(this, REQUEST_CODE_LOCATION_PERMISSION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i(TAG, "onActivityResult requestCode = " + requestCode + " resultCode = " + resultCode);
        switch (requestCode) {
            case REQUEST_CODE_LOCATION_PROVIDERS:
            case REQUEST_CODE_LOCATION_PERMISSION:
                getLocation();
                break;
        }
    }

}
