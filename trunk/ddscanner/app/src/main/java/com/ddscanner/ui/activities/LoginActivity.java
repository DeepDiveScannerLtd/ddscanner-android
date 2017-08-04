package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class LoginActivity extends BaseAppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    public static void show(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_to_continue);
        findViews();
    }

    private void findViews() {
        Button signUp = findViewById(R.id.sign_up);
        Button login = findViewById(R.id.login);

        signUp.setOnClickListener(this);
        login.setOnClickListener(this);

        setupToolbar(R.string.login_high, R.id.toolbar);
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
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsMustRefreshDiveSpotActivity(true);
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
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

    public static void showForResult(Activity context, int code) {
        Intent intent = new Intent(context, LoginActivity.class);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}