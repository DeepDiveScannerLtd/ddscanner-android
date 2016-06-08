package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
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
public class NeedToLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Helpers helpers = new Helpers();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_to_login);
        findViewById(R.id.btn_open_login_screen).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_login_screen:

                break;
        }
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, NeedToLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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
