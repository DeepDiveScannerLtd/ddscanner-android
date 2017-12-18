package com.ddscanner.ui.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Helpers;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class LoginActivity extends BaseAppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    VideoView videoView;
    boolean isForAddAccount;
    ImageView close;

    public static void show(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void showFromApplication(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_to_continue);
        isForAddAccount = getIntent().getBooleanExtra("add", false);
        findViews();
        themeNavAndStatusBar();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void themeNavAndStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;
        Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        w.setNavigationBarColor(ContextCompat.getColor(this ,android.R.color.transparent));
        w.setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
//        if (isForAddAccount) {
            Helpers.setMargins(close, 0,getStatusBarHeight(), 0,0);
//        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void findViews() {
        Button signUp = findViewById(R.id.sign_up);
        Button login = findViewById(R.id.login);
            close = findViewById(R.id.close_button);
            close.setVisibility(View.VISIBLE);
            close.setOnClickListener(view -> onBackPressed());
        videoView = findViewById(R.id.video_view);
        Uri uri = Uri.parse(getString(R.string.vido_resource_pattern, getPackageName(), R.raw.login_video));
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.setOnPreparedListener(mediaPlayer -> mediaPlayer.setLooping(true));
        signUp.setOnClickListener(this);
        login.setOnClickListener(this);

//        setupToolbar(R.string.login_high, R.id.toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_SOCIAL_NETWORKS_SIGN_IN:
            case ActivitiesRequestCodes.REQUEST_CODE_SOCIAL_NETWORKS_SIGN_UP:
                if (resultCode == RESULT_OK) {
                    if (isForAddAccount) {
                        DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsMustRefreshDiveSpotActivity(true);
                        setResult(RESULT_OK);
                        finish();
                        return;
                    }
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        if (isForAddAccount) {
            finish();
//        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
    }

    public static void showForResult(Activity context, int code, boolean isForAddAccount) {
        Intent intent = new Intent(context, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("add", isForAddAccount);
        context.startActivityForResult(intent, code);
    }

    public static void showForResult(Activity context, int code) {
        Intent intent = new Intent(context, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivityForResult(intent, code);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.privacy_policy:
                PrivacyPolicyActivity.show(LoginActivity.this);
                break;
            case R.id.close:
                onBackPressed();
                break;
            case R.id.login:
                SignUpActivity.showForResult(this, false, ActivitiesRequestCodes.REQUEST_CODE_SOCIAL_NETWORKS_SIGN_IN);
                break;
            case R.id.sign_up:
                SignUpActivity.showForResult(this, true, ActivitiesRequestCodes.REQUEST_CODE_SOCIAL_NETWORKS_SIGN_UP);
                break;
        }
    }

}