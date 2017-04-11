package com.ddscanner.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.events.InstanceIDReceivedEvent;
import com.ddscanner.ui.views.DDProgressBarView;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.squareup.otto.Subscribe;

import java.util.Locale;

public class SplashActivity extends BaseAppCompatActivity implements DialogClosedListener, View.OnClickListener {

    private static final String TAG = SplashActivity.class.getName();

    private Handler handler = new Handler();
    private Runnable showMainActivityRunnable;
    private long activityShowTimestamp;

    private TextView progressMessage;
    private TextView skip;
    private Button signUpButton;
    private Button loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO remove after creating working login mechanism
       // DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
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
        skip = (TextView) findViewById(R.id.skip);
        loginButton = (Button) findViewById(R.id.login);
        signUpButton = (Button) findViewById(R.id.sign_up);

        skip.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
            skip.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            signUpButton.setVisibility(View.GONE);
            showMainActivity();
        }

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);

        mainLayout.startAnimation(fadeInAnimation);

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.skip:
                MainActivity.show(SplashActivity.this, Helpers.hasConnection(SplashActivity.this));
                SplashActivity.this.finish();
                break;
            case R.id.login:
                SignUpActivity.showForResult(this, false, ActivitiesRequestCodes.REQUEST_CODE_SPLASH_ACTIVITY_LOGIN);
                break;
            case R.id.sign_up:
                SignUpActivity.showForResult(this, true, ActivitiesRequestCodes.REQUEST_CODE_SPLASH_ACTIVITY_SIGN_UP);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_SPLASH_ACTIVITY_SIGN_UP:
            case ActivitiesRequestCodes.REQUEST_CODE_SPLASH_ACTIVITY_LOGIN:
                if (resultCode == RESULT_OK) {
                    showMainActivity();
                }
                break;
        }
    }
}