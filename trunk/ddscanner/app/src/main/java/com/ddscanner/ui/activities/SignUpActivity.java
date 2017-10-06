package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.BaseUser;
import com.ddscanner.entities.SignInType;
import com.ddscanner.entities.SignUpResponseEntity;
import com.ddscanner.interfaces.ConfirmationDialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.profile.edit.EditDiveCenterProfileActivity;
import com.ddscanner.screens.profile.edit.divecenter.search.SearchDiveCenterActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogHelpers;
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
import com.google.gson.Gson;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class SignUpActivity extends BaseAppCompatActivity implements ConfirmationDialogClosedListener, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

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
    private boolean isNameEmpty = true;
    private boolean isPasswordEmpty = true;
    private boolean isEmailEmpty = true;
    private TextWatcher nameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            isNameEmpty = name.getText().length() <= 0;
            changeButtonState();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            isPasswordEmpty = !(password.getText().length() > 3 && password.getText().length() < 33);
            changeButtonState();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            isEmailEmpty = !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches();
            changeButtonState();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private boolean isSignUpScreen = true;

    private CallbackManager facebookCallbackManager;
    private GoogleApiClient mGoogleApiClient;

    private boolean needToClearDefaultAccount;

    private SigningResultListener signInResultListener = new SigningResultListener(false);
    private SigningResultListener signUpResultListener = new SigningResultListener(true);

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
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        fbLogin = findViewById(R.id.fb_custom);
        googleLogin = findViewById(R.id.custom_google);
        buttonSignUp = findViewById(R.id.btn_login_or_sign_up_via_email);
        privacyPolicy = findViewById(R.id.privacy_policy);
        forgotPasswordView = findViewById(R.id.forgot_password);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        name.addTextChangedListener(nameTextWatcher);
        password.addTextChangedListener(passwordTextWatcher);
        email.addTextChangedListener(emailTextWatcher);
        setUi();
    }

    private void setUi() {
        if (!isRegister) {
            tabLayout.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
            isNameEmpty = false;
        } else {
            forgotPasswordView.setVisibility(View.GONE);
        }
        changeButtonState();
        materialDialog = Helpers.getMaterialDialog(this);
        forgotPasswordView.setOnClickListener(this);
        googleLogin.setOnClickListener(this);
        fbLogin.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        changeUiAccordingRegister();
        setupTabLayout();
        setPrivacyPolicyText();
    }

    private void setPrivacyPolicyText() {
        ArrayList<Link> links = new ArrayList<>();
        Link tosLink = new Link("Terms of Service");
        tosLink.setOnClickListener(clickedText -> {
            TermsOfServiceActivity.show(this);
        });
        tosLink.setTextColor(ContextCompat.getColor(DDScannerApplication.getInstance(),R.color.notification_clickable_text_color));
        tosLink.setUnderlined(false);
        Link ppLink = new Link("Privacy Policy");
        ppLink.setOnClickListener(clickedText -> PrivacyPolicyActivity.show(this));
        ppLink.setTextColor(ContextCompat.getColor(DDScannerApplication.getInstance(),R.color.notification_clickable_text_color));
        ppLink.setUnderlined(false);
        links.add(tosLink);
        links.add(ppLink);
        LinkBuilder.on(privacyPolicy).addLinks(links).build();
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
                    DDScannerApplication.getInstance().getDdScannerRestClient(this).postUserSignUp(email.getText().toString(), password.getText().toString(), userType, null, null, name.getText().toString(), signUpResultListener);
                    break;
                }
                DDScannerApplication.getInstance().getDdScannerRestClient(this).postUserLogin(email.getText().toString(), password.getText().toString(), null, null, null, null, signInResultListener);
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

    private void initGoogleLoginManager() {
        needToClearDefaultAccount = true;
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_login_manager_key))
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
                        Toast.makeText(SignUpActivity.this, R.string.cant_connect_to_google, Toast.LENGTH_SHORT).show();
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
                        GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> sendLoginRequest(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserAppId(), SignInType.FACEBOOK, loginResult.getAccessToken().getToken())).executeAsync();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException error) {
                    }
                });
    }

    private void sendLoginRequest(String appId, SignInType signInType, String token) {
//        DDScannerApplication.getDdScannerRestClient().postLogin(appId, signInType, token, loginResultListener);
        materialDialog.show();
        DDScannerApplication.getInstance().getDdScannerRestClient(this).postUserLogin(null, null, null, null, signInType, token, signInResultListener);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_SIGN_UP_ACTIVITY_DIVE_CENTER_LOGIN:
                DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsNeedContinueRegistration(false);
                setResult(RESULT_OK);
                finish();
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_SIGN_UP_ACTIVITY_PICK_DIVECENTER:
                setResult(RESULT_OK);
                finish();
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_MAIN_ACTIVITY_CHOSE_GOOGLE_ACCOUNT:
                materialDialog.dismiss();
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

    private void changeButtonState() {
        if (!isEmailEmpty && !isNameEmpty && !isPasswordEmpty) {
            buttonSignUp.setTextColor(ContextCompat.getColor(this, R.color.black_text));
            buttonSignUp.setOnClickListener(this);
        } else {
            buttonSignUp.setTextColor(ContextCompat.getColor(this, R.color.empty_login_button_text));
            buttonSignUp.setOnClickListener(null);
        }

    }

    class SigningResultListener extends DDScannerRestClient.ResultListener<SignUpResponseEntity> {

        boolean isSignUp;

        SigningResultListener(boolean isSignUp) {
            this.isSignUp = isSignUp;
        }

        @Override
        public void onSuccess(SignUpResponseEntity result) {
            materialDialog.dismiss();
            Log.i(TAG, "onSuccess: ");
            BaseUser baseUser = new BaseUser();
            baseUser.setActive(true);
            baseUser.setType(result.getType());
            baseUser.setToken(result.getToken());
            baseUser.setId(result.getId());
            baseUser.setName(name.getText().toString());
            DDScannerApplication.getInstance().getSharedPreferenceHelper().addUserToList(baseUser);
            EventsTracker.trackRegistration(result.getType());
            if (isSignUp && result.getType() != 0) {
                DialogHelpers.showInstructorConfirmationDialog(getSupportFragmentManager());
                return;
            }
            if (isSignUp && result.getType() == 0) {
                DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsNeedContinueRegistration(true);
                EditDiveCenterProfileActivity.showForResult(SignUpActivity.this, new Gson().toJson(baseUser), ActivitiesRequestCodes.REQUEST_CODE_SIGN_UP_ACTIVITY_DIVE_CENTER_LOGIN, false);
                return;
            }
            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            switch (errorType) {
                case ENTITY_NOT_FOUND_404:
                    UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.title_pass_incorrect, R.string.pass_incorrect, false);
                    break;
                case DATA_ALREADY_EXIST_409:
                    UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.title_email_exist, R.string.message_email_exist, false);
                    break;
                default:
                    UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.unexcepted_error_title, R.string.error_unexpected_error, false);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }
    }

    @Override
    public void onNegativeDialogClicked() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onPositiveDialogClicked() {
        EventsTracker.trackYesInstructorClicked();
        SearchDiveCenterActivity.showForResult(SignUpActivity.this, ActivitiesRequestCodes.REQUEST_CODE_SIGN_UP_ACTIVITY_PICK_DIVECENTER, false);
    }
}
