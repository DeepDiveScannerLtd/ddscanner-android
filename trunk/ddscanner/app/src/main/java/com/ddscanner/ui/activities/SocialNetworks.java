package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.R;
import com.ddscanner.entities.RegisterResponse;
import com.ddscanner.entities.request.RegisterRequest;
import com.ddscanner.rest.RestClient;
import com.ddscanner.utils.EventTrackerHelper;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class SocialNetworks extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;

    private static final String TAG = "SOCIAL";

    private TwitterLoginButton loginButton;
    private Button twitterCustomBtn;

    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;
    private Button fbCustomLogin;

    private SignInButton googleSignIn;
    private Button signIn;
    private GoogleApiClient mGoogleApiClient;
    private com.ddscanner.entities.User selfProfile;
    private RegisterResponse registerResponse = new RegisterResponse();

    public static void show(Context context) {
        Intent intent = new Intent(context, SocialNetworks.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_login);
        AppsFlyerLib.getInstance().trackEvent(getApplicationContext(),
                EventTrackerHelper.EVENT_SIGN_IN_OPENED, new HashMap<String, Object>());
        /*TWITTER*/
        twitterCustomBtn = (Button) findViewById(R.id.twitter_custom);
        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;
                Twitter.getApiClient(session).getAccountService()
                        .verifyCredentials(true, false, new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        SharedPreferenceHelper.setIsUserSignedIn(true);
                    }

                    @Override
                    public void failure(TwitterException e) {

                    }
                });
                TwitterAuthToken authToken = session.getAuthToken();
                Log.i(TAG, session.getAuthToken().secret + "\n" + session.getAuthToken().token);
                AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), EventTrackerHelper
                        .EVENT_SIGN_IN_BTN_CLICK, new HashMap<String, Object>() {{
                    put(EventTrackerHelper.PARAM_SIGN_IN_BTN_CLICK, "twitter");
                }});
                sendRegisterRequest(putTokensToMap(SharedPreferenceHelper.getUserAppId(),
                        "tw", authToken.token, authToken.secret));
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });
        twitterCustomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
            }
        });
        /*FACEBOOK*/
        callbackManager = CallbackManager.Factory.create();
        fbCustomLogin = (Button) findViewById(R.id.fb_custom);
        fbCustomLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccessToken.getCurrentAccessToken() == null) {
                    fbLogin();
                    Log.i(TAG, "LOGED IN");
                } else {
                    LoginManager.getInstance().logOut();
                    fbLogin();
                    Log.i(TAG, "LOGGED OUT");
                }
            }
        });
        /*Google plus*/
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("195706914618-ist9f8ins485k2gglbomgdp4l2pn57iq.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        signIn = (Button) findViewById(R.id.custom_google);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void fbLogin() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(SocialNetworks.this,
                Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        sendRegisterRequest(putTokensToMap(SharedPreferenceHelper.getUserAppId(),
                                "fb", loginResult.getAccessToken().getToken()));

                    }
                }).executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    /*Google plus*/
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 140) {
            loginButton.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess());
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                String idToken = acct.getIdToken();
                sendRegisterRequest(putTokensToMap(SharedPreferenceHelper.getUserAppId(),
                        "go", idToken));
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private RegisterRequest putTokensToMap(String... args) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setAppId(args[0]);
        registerRequest.setSocial(args[1]);
        registerRequest.setToken(args[2]);
        if (args.length == 4) {
            registerRequest.setSecret(args[3]);
        }
        return registerRequest;
    }

    private void sendRegisterRequest(final RegisterRequest userData) {
        Call<ResponseBody> call = RestClient.getServiceInstance().registerUser(userData);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, responseString);
                    SharedPreferenceHelper.setIsUserSignedIn(true);
                    SharedPreferenceHelper.setToken(userData.getToken());
                    SharedPreferenceHelper.setSn(userData.getSocial());
                    if (userData.getSocial().equals("tw")) {
                        SharedPreferenceHelper.setSecret(userData.getSecret());
                    }
                    registerResponse = new Gson().fromJson(responseString, RegisterResponse.class);
                    selfProfile = registerResponse.getUser();
                  //  SharedPreferenceHelper.setUserid(selfProfile.getId());
                 //   SharedPreferenceHelper.setPhotolink(selfProfile.getPicture());
                  //  SharedPreferenceHelper.setUsername(selfProfile.getName());
                  //  SharedPreferenceHelper.setLink(selfProfile.getLink());
                    SharedPreferenceHelper.setUserServerId(selfProfile.getId());
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    // TODO Handle errors
                    Log.i(TAG, response.raw().message());
                    String responseString = "";
                    try {
                        responseString = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, responseString);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // TODO Handle errors
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), EventTrackerHelper
                .EVENT_SIGN_IN_CANCELLED, new HashMap<String, Object>());
        finish();
    }

}