package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ddscanner.R;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LocationHelper;
import com.ddscanner.utils.LogUtils;

/**
 * Created by lashket on 19.5.16.
 */
public class APIUpdatedActivity extends AppCompatActivity {

    private static final String TAG = APIUpdatedActivity.class.getName();
    private static final int REQUEST_CODE_TURN_ON_LOCATION_SETTINGS = 5000;

    private Helpers helpers = new Helpers();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_location_providers);

        Window w = getWindow();
        w.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        w.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    public static void show(Activity context) {
        Intent intent = new Intent(context, APIUpdatedActivity.class);
        context.startActivity(intent);
    }

}
