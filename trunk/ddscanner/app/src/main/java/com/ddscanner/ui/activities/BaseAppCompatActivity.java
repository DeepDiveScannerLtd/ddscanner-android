package com.ddscanner.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.ddscanner.utils.LocationHelper;
import com.ddscanner.utils.LogUtils;

import java.util.HashSet;

public class BaseAppCompatActivity extends AppCompatActivity {

    private static final String TAG = BaseAppCompatActivity.class.getName();

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1301;
    private static final int REQUEST_CODE_LOCATION_PROVIDERS = 1302;

    private LocationHelper locationHelper;
    private HashSet<Integer> requestCodes = new HashSet<>();

    /**
     * Call this method to get user location. Subscribe to LocationReadyEvent for result
     */
    public void getLocation(int requestCode) {
        LogUtils.i(TAG, "location check: getLocation request code = " + requestCode + " request codes = " + requestCodes);
        if (requestCode != -1) {
            requestCodes.add(requestCode);
        }
        if (locationHelper == null) {
            locationHelper = new LocationHelper(this);
        }
        try {
            locationHelper.checkLocationConditions();
            locationHelper.requestLocation(requestCodes);
            requestCodes.clear();
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
                // Because by this time we have already put request code to collection, we are passing -1.
                getLocation(-1);
                break;
        }
    }

}
