package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ddscanner.R;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.LocationHelper;
import com.ddscanner.utils.LogUtils;

/**
 * Created by lashket on 19.5.16.
 */
public class LocationProvidersNotAvailableActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LocationProvidersNotAvailableActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(TAG, "LocationProvidersNotAvailableActivity onCreate " + this);
        setContentView(R.layout.activity_no_location_providers);

        findViewById(R.id.btn_open_settings).setOnClickListener(this);

        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_LOCATION_PROVIDERS_NOT_AVAILABLE_ACTIVITY_TURN_ON_LOCATION_SETTINGS);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i(TAG, "onActivityResult requestCode = " + requestCode + " resultCode = " + resultCode + " " + this);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_LOCATION_PROVIDERS_NOT_AVAILABLE_ACTIVITY_TURN_ON_LOCATION_SETTINGS:
                if (LocationHelper.isLocationProvidersAvailable(this)) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    // Do nothing. Keep showing this activity
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    public static void showForResult(Activity context, int requestCode) {
        Log.i(TAG, "LocationProvidersNotAvailableActivity showForResult");
        Intent intent = new Intent(context, LocationProvidersNotAvailableActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

}
