package com.ddscanner.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import com.ddscanner.R;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogHelpers;
import com.ddscanner.utils.LocationHelper;

import java.util.HashSet;

public class BaseAppCompatActivity extends AppCompatActivity {

    private static final String TAG = BaseAppCompatActivity.class.getName();

    private LocationHelper locationHelper;
    private HashSet<Integer> requestCodes = new HashSet<>();
    private int menuResourceId = -1;

    /**
     * Call this method to get user location. Subscribe to LocationReadyEvent for result
     */
    public void getLocation(int requestCode) {
        Log.i(TAG, "location check: getLocation request code = " + requestCode + " request codes = " + requestCodes);
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
            DialogHelpers.showDialogForEnableLocationProviders(this);
        } catch (LocationHelper.LocationPPermissionsNotGrantedException e) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, ActivitiesRequestCodes.REQUEST_CODE_LOCATION_PERMISSION_NOT_GRANTED_ACTIVITY_LOCATION_PERMISSION);
        }
    }

    /**
     * Show default toolbar with back button with title
     * @param titleresId resource id for toolbar title
     * @param toolbarId toolbar id in layout
     */
    public void setupToolbar(int titleresId, int toolbarId) {
        setSupportActionBar((Toolbar) findViewById(toolbarId));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(titleresId);
    }

    /**
     * Show default toolbar with back button with title and menu
     * @param titleresId resource id for toolbar title
     * @param toolbarId toolbar id in layout
     * @param menuResId resource id for menu
     */
    public void setupToolbar(int titleresId, int toolbarId, int menuResId) {
        this.menuResourceId = menuResId;
        setSupportActionBar((Toolbar) findViewById(toolbarId));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(titleresId);
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menuResourceId != -1) {
            getMenuInflater().inflate(this.menuResourceId, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult requestCode = " + requestCode + " resultCode = " + resultCode);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_LOCATION_PROVIDERS_NOT_AVAILABLE_ACTIVITY_TURN_ON_LOCATION_SETTINGS:
                if (resultCode == RESULT_OK) {
                    getLocation(-1);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_LOCATION_PERMISSION_NOT_GRANTED_ACTIVITY_LOCATION_PERMISSION:
                Log.i(TAG, "onRequestPermissionsResult grantResults = " + grantResults[0] + " " + grantResults[1]);
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getLocation(-1);
                } else {
                    // Do nothing. Keep showing this activity
                }
                break;
        }
    }

}
