package travel.ilave.deepdivescanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.request.RegisterRequest;
import travel.ilave.deepdivescanner.rest.RestClient;
import travel.ilave.deepdivescanner.utils.SharedPreferenceHelper;


/**
 * Created by lashket on 24.2.16.
 */
public class SocialNetworks extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

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
    private String appId;

    public static void show(Context context) {
        Intent intent = new Intent(context, SocialNetworks.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_login);
        appId = SharedPreferenceHelper.getGcmId();
        /*TWITTER*/
        twitterCustomBtn = (Button) findViewById(R.id.twitter_custom);
        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                TwitterSession session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                //  Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                Twitter.getApiClient(session).getAccountService().verifyCredentials(true, false, new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        System.out.println(result.data.name);
                        SharedPreferenceHelper.setIsUserSignedIn(true);
                        onBackPressed();
                    }

                    @Override
                    public void failure(TwitterException e) {

                    }
                });
                TwitterAuthToken authToken = session.getAuthToken();
                Log.i(TAG, session.getAuthToken().secret + "\n" + session.getAuthToken().token);
                sendRegisterRequest(putTokensToMap(appId, "tw", authToken.token, authToken.secret));
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
                    Log.i(TAG, "LOGGED OUT");
                }
            }
        });
        /*Google plus*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("200652897389-onnv3dgvcnf7bj8dku3psjntif6hiua0.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this )
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
        LoginManager.getInstance().logInWithReadPermissions(SocialNetworks.this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i(TAG, loginResult.getAccessToken().toString());
                        String result = String.valueOf(object);
                        System.out.println(result);
                        Log.i(TAG, "FB - " + loginResult.getAccessToken().getToken());
                        sendRegisterRequest(putTokensToMap(appId, "fb", loginResult.getAccessToken().getToken()));
                        onBackPressed();

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
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        Log.i(TAG, "Logout");
                        // [END_EXCLUDE]
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        Log.i(TAG, "revoking");
                        // [END_EXCLUDE]
                    }
                });
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 140) {
            loginButton.onActivityResult(requestCode, resultCode, data);
        }
         else if(requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess());
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                String idToken = acct.getIdToken();
                Log.d(TAG, "idToken:" + idToken);
                sendRegisterRequest(putTokensToMap(appId, "go", idToken));
                // TODO(user): send token to server and validate server-side
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private RegisterRequest putTokensToMap(String... args) {
        Map<String,String> map = new HashMap<String,String>();
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
        RestClient.getServiceInstance().registerUser(userData, new retrofit.Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                Log.i(TAG, responseString);
                SharedPreferenceHelper.setIsUserSignedIn(true);
                SharedPreferenceHelper.setToken(userData.getToken());
                SharedPreferenceHelper.setSn(userData.getSocial());
                if (userData.getSocial().equals("tw")) {
                    SharedPreferenceHelper.setSecret(userData.getSecret());
                }
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i(TAG, error.getMessage());
                String json =  new String(((TypedByteArray)error.getResponse().getBody()).getBytes());
                Log.i(TAG,json.toString());
            }
        });
    }
}