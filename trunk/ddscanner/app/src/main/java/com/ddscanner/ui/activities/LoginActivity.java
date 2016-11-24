package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.RegisterResponse;
import com.ddscanner.entities.SignInType;
import com.ddscanner.events.LoggedInEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, InfoDialogFragment.DialogClosedListener {

    private static final String TAG = "SOCIAL";

    private CallbackManager callbackManager;

    private GoogleApiClient mGoogleApiClient;
    private MaterialDialog materialDialog;
    private Toolbar toolbar;

    private Button login;
    private Button signUp;

    private LoginResultListener loginResultListener = new LoginResultListener();

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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        signUp = (Button) findViewById(R.id.sign_up);
        login = (Button) findViewById(R.id.login);

        signUp.setOnClickListener(this);
        login.setOnClickListener(this);

        setupToolbar();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.login_high);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_close);
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
        if (!Helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
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
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_LOGIN_ACTIVITY_FAILED_TO_CONNECT:
            case DialogsRequestCodes.DRC_LOGIN_ACTIVITY_USER_NOT_FOUND:
            case DialogsRequestCodes.DRC_LOGIN_ACTIVITY_UNEXPECTED_ERROR:
            case DialogsRequestCodes.DRC_LOGIN_ACTIVITY_GOOGLE_SIGN_IN_FAIL:
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
        }
    }

    private class LoginResultListener extends DDScannerRestClient.ResultListener<RegisterResponse> {

        private String token;
        private SignInType socialNetwork;

        public void setToken(String token) {
            this.token = token;
        }

        void setSocialNetwork(SignInType socialNetwork) {
            this.socialNetwork = socialNetwork;
        }

        @Override
        public void onSuccess(RegisterResponse result) {
            materialDialog.dismiss();
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setToken(token);
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setSn(socialNetwork.getName());
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsUserSignedIn(true, socialNetwork);
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setUserServerId(result.getUserOld().getId());
            DDScannerApplication.bus.post(new LoggedInEvent());
            setResult(Activity.RESULT_OK);
            finish();
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_LOGIN_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            switch (errorType) {
                case UNAUTHORIZED_401:
                    Crashlytics.log("801 error on identify");
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_message_user_not_found, DialogsRequestCodes.DRC_LOGIN_ACTIVITY_USER_NOT_FOUND, false);
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_LOGIN_ACTIVITY_UNEXPECTED_ERROR, false);
            }
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