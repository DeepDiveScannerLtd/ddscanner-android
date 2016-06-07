package com.ddscanner.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ddscanner.R;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;

/**
 * Created by lashket on 19.5.16.
 */
public class LocationPermissionNotGrantedActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LocationPermissionNotGrantedActivity.class.getName();
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1301;

    private Helpers helpers = new Helpers();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(TAG, "onCreate " + this);
        setContentView(R.layout.activity_no_location_permission);

        findViewById(R.id.btn_grant_permission).setOnClickListener(this);

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
        // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {

        // Show an expanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.

//            } else {
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
//            }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_LOCATION_PERMISSION:
                LogUtils.i(TAG, "onRequestPermissionsResult grantResults = " + grantResults[0] + " " + grantResults[1]);
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    // Do nothing. Keep showing this activity
                }
                break;
        }
    }

    public static void showForResult(Activity context, int requestCode) {
        Intent intent = new Intent(context, LocationPermissionNotGrantedActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

}