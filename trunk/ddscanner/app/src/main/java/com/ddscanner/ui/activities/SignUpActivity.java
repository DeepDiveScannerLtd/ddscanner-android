package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.BaseUser;
import com.ddscanner.entities.SignInType;
import com.ddscanner.entities.SignUpResponseEntity;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.dialogs.ActionSuccessDialogFragment;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.util.Arrays;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    private TabLayout tabLayout;
    private Toolbar toolbar;
    private LinearLayout fbLogin;
    private LinearLayout googleLogin;
    private Button buttonSignUp;
    private MenuItem signMenuItem;
    private boolean isRegister;
    private TextView privacyPolicy;
    private TextView forgotPasswordView;
    private EditText email;
    private EditText password;
    private EditText name;
    private MaterialDialog materialDialog;
    private String userType;

    private boolean isSignUpScreen = true;

    private CallbackManager facebookCallbackManager;
    private GoogleApiClient mGoogleApiClient;

    private boolean needToClearDefaultAccount;


    private DDScannerRestClient.ResultListener<SignUpResponseEntity> signUpResultListener = new DDScannerRestClient.ResultListener<SignUpResponseEntity>() {
        @Override
        public void onSuccess(SignUpResponseEntity result) {
            materialDialog.dismiss();
            Log.i(TAG, "onSuccess: ");
            BaseUser baseUser = new BaseUser();
            baseUser.setActive(true);
            baseUser.setType(result.getType());
            baseUser.setToken(result.getToken());
            baseUser.setId(result.getId());
            DDScannerApplication.getInstance().getSharedPreferenceHelper().addUserToList(baseUser);
            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            switch (errorType) {
                case ENTITY_NOT_FOUND_404:
                    ActionSuccessDialogFragment.show(SignUpActivity.this, R.string.title_pass_incorrect, R.string.pass_incorrect);
                    break;
                default:
                    InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_unexpected_error, R.string.error_connection_failed, false);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    };

    public static void showForResult(Activity context, boolean isRegister, int requestCode) {
        Intent intent = new Intent(context, SignUpActivity.class);
        intent.putExtra("isregister", isRegister);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        isRegister = getIntent().getBooleanExtra("isregister", false);
        userType = Constants.USER_TYPE_DIVER;
        findViews();
    }

    private void findViews() {
        materialDialog = Helpers.getMaterialDialog(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        fbLogin = (LinearLayout) findViewById(R.id.fb_custom);
        googleLogin = (LinearLayout) findViewById(R.id.custom_google);
        buttonSignUp = (Button) findViewById(R.id.btn_login_or_sign_up_via_email);
        privacyPolicy = (TextView) findViewById(R.id.privacy_policy);
        forgotPasswordView = (TextView) findViewById(R.id.forgot_password);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        name = (EditText) findViewById(R.id.name);
        setUi();
    }

    private void setUi() {
        if (!isRegister) {
            tabLayout.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
        }
        materialDialog = Helpers.getMaterialDialog(this);
        forgotPasswordView.setOnClickListener(this);
        googleLogin.setOnClickListener(this);
        fbLogin.setOnClickListener(this);
        buttonSignUp.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        changeUiAccordingRegister();
        setupTabLayout();
        setPrivacyPolicyText();
    }

    private void setPrivacyPolicyText() {
        final SpannableString spannableString = new SpannableString(privacyPolicy.getText());
        privacyPolicy.setHighlightColor(Color.TRANSPARENT);
        spannableString.setSpan(new MyClickableSpan(privacyPolicy.getText().toString()) {
            @Override
            public void onClick(View tv) {
                TermsOfServiceActivity.show(SignUpActivity.this);
            }
        }, 32, 48, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new MyClickableSpan(privacyPolicy.getText().toString()) {
            @Override
            public void onClick(View tv) {
                PrivacyPolicyActivity.show(SignUpActivity.this);
                tv.invalidate();
            }
        }, 53, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        privacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        privacyPolicy.setText(spannableString);
    }

    private void changeUiAccordingRegister() {
        if (isRegister) {
            getSupportActionBar().setTitle(R.string.sign_up_toolbar);
            buttonSignUp.setText(getString(R.string.sign_up));
            return;
        }
        getSupportActionBar().setTitle(R.string.login_toolbar);
        buttonSignUp.setText(getString(R.string.login));
        return;
    }

    private void changeSocialButtonsState(int visibility) {
        fbLogin.setVisibility(visibility);
        googleLogin.setVisibility(visibility);
    }

    private void setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.diver)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.dive_cente)));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        changeSocialButtonsState(View.VISIBLE);
                        userType = Constants.USER_TYPE_DIVER;
                        break;
                    case 1:
                        changeSocialButtonsState(View.GONE);
                        userType = Constants.USER_TYPE_DIVE_CENTER;
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forgot_password:
                ForgotPasswordActivity.show(this);
                break;
            case R.id.fb_custom:
                materialDialog.show();
                if (AccessToken.getCurrentAccessToken() == null) {
                    fbLogin();
                } else {
                    LoginManager.getInstance().logOut();
                    fbLogin();
                }
                break;
            case R.id.custom_google:
                materialDialog.show();
                googleSignIn();
                break;
            case R.id.btn_login_or_sign_up_via_email:
                materialDialog.show();
                if (isRegister) {
                    DDScannerApplication.getInstance().getDdScannerRestClient().postUserSignUp(email.getText().toString(), password.getText().toString(), userType, null, null, name.getText().toString(), signUpResultListener);
                    break;
                }
                DDScannerApplication.getInstance().getDdScannerRestClient().postUserLogin(email.getText().toString(), password.getText().toString(), "28.13123", "21.323232", null, null, signUpResultListener);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class MyClickableSpan extends ClickableSpan {

        String clicked;

        public MyClickableSpan(String string) {
            super();
            clicked = string;
        }

        public void onClick(View tv) {

        }

        public void updateDrawState(TextPaint ds) {
            ds.setColor(Color.parseColor("#a3a3a3"));
            ds.setUnderlineText(false);
        }
    }

    private void initGoogleLoginManager() {
        needToClearDefaultAccount = true;
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("195706914618-ist9f8ins485k2gglbomgdp4l2pn57iq.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (needToClearDefaultAccount) {
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                            mGoogleApiClient.clearDefaultAccountAndReconnect();
                            needToClearDefaultAccount = false;
                            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                            startActivityForResult(signInIntent, ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_CHOSE_GOOGLE_ACCOUNT);
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        // TODO Implement
                    }
                })
                .build();
    }

    private void initFBLoginManager() {
        facebookCallbackManager = CallbackManager.Factory.create();
    }

    /*Google*/
    private void googleSignIn() {
        if (mGoogleApiClient == null) {
            initGoogleLoginManager();
        } else if (mGoogleApiClient.isConnected()) {
            needToClearDefaultAccount = true;
            mGoogleApiClient.clearDefaultAccountAndReconnect();
        }
    }

    private void fbLogin() {
        if (facebookCallbackManager == null) {
            initFBLoginManager();
        }
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(facebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                sendLoginRequest(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserAppId(), SignInType.FACEBOOK, loginResult.getAccessToken().getToken());
                            }
                        }).executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // TODO Implement
                    }

                    @Override
                    public void onError(FacebookException error) {
                        // TODO Implement
                    }
                });
    }

    private void sendLoginRequest(String appId, SignInType signInType, String token) {
//        DDScannerApplication.getDdScannerRestClient().postLogin(appId, signInType, token, loginResultListener);
        materialDialog.show();
        DDScannerApplication.getInstance().getDdScannerRestClient().postUserLogin(null, null, null, null, signInType, token, signUpResultListener);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_CHOSE_GOOGLE_ACCOUNT:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    GoogleSignInAccount acct = result.getSignInAccount();
                    if (acct == null) {
                        Helpers.handleUnexpectedServerError(getSupportFragmentManager(), "google_login", "result.getSignInAccount() returned null");
                    } else {
                        String idToken = acct.getIdToken();
                        sendLoginRequest(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserAppId(), SignInType.GOOGLE, idToken);
                    }
                }
                break;
            default:
                if (facebookCallbackManager != null) {
                    facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }
    }
}
