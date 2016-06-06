package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.InternerConnectionOpenedEvent;
import com.ddscanner.utils.Helpers;
import com.squareup.otto.Subscribe;

/**
 * Created by lashket on 19.5.16.
 */
public class InternetClosedActivity extends AppCompatActivity implements View.OnClickListener {

    private Helpers helpers = new Helpers();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        Button btnRefresh = (Button) findViewById(R.id.btn_retry);
        btnRefresh.setOnClickListener(this);

        Window w = getWindow();
        w.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        w.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @Override
    public void onClick(View v) {
        if (helpers.hasConnection(this)) {
            onBackPressed();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, InternetClosedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Subscribe
    public void internetCOnnectionIsOpened(InternerConnectionOpenedEvent event) {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DDScannerApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DDScannerApplication.activityResumed();
    }

}
