package travel.ilave.deepdivescanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
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
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.utils.SharedPreferenceHelper;


/**
 * Created by lashket on 24.2.16.
 */
public class SocialNetworks extends AppCompatActivity{
    private TwitterLoginButton loginButton;
    private Button twitterCustomBtn;

    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;
    private Button fbCustomLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_login);
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
                System.out.println("TOKEN - " + authToken.token + " SECRET - " + authToken.secret);
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
                    Log.i("LOGIN", "LOGED IN");
                }
                else {
                    LoginManager.getInstance().logOut();
                    Log.i("LOGIN", "LOGGED OUT");
                }
            }
        });
    }

    private void fbLogin() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(SocialNetworks.this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                            String result = String.valueOf(object);
                            System.out.println(result);
                        SharedPreferenceHelper.setIsUserSignedIn(true);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 140) {
            loginButton.onActivityResult(requestCode, resultCode, data);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, SocialNetworks.class);
        context.startActivity(intent);
    }
}
