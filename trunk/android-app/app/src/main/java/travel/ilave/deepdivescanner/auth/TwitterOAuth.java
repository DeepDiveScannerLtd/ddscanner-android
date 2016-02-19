package travel.ilave.deepdivescanner.auth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import travel.ilave.deepdivescanner.DDScannerApplication;
import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.ui.activities.SocialLogin;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterOAuth extends Activity {

    private static final String TAG = TwitterOAuth.class.getSimpleName();

    private WebView mTwitterView;

    private Twitter mTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup twitter client
        mTwitter = new TwitterFactory(new ConfigurationBuilder()
                .setOAuthConsumerKey(DDScannerApplication.TWITTER_KEY)
                .setOAuthConsumerSecret(DDScannerApplication.TWITTER_SECRET)
               // .setOAuthAccessToken("262839527-nZvvEWdTD8nKKmTNGY0tn5UXmO7RnZfmkroOdRr6")
               // .setOAuthAccessTokenSecret("KCl4AbgsRgSTHWqXXB1JtJg3rCuJ9wMzSc8O7BjC4gJWB")
                .build()).getInstance();

        // setup twitter webview
        mTwitterView = new WebView(this);
        mTwitterView.getSettings().setJavaScriptEnabled(true);

        // initialize view
        setContentView(mTwitterView);

        // start the web view
        loginToTwitter();
    }

    private void loginToTwitter() {
        // fetch the oauth request token then prompt the user to authorize the application
        new AsyncTask<Void, Void, RequestToken>() {
            @Override
            protected RequestToken doInBackground(Void... params) {
                RequestToken token = null;
                try {
                    token = mTwitter.getOAuthRequestToken("twitterapp://connect");
                    System.out.println(token);
                } catch (TwitterException te) {
                    Log.e(TAG, te.toString());
                }
                return token;
            }

            @Override
            protected void onPostExecute(final RequestToken token) {
                mTwitterView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(final WebView view, final String url) {
                        if (url.startsWith("twitterapp://connect")) {
                            getTwitterOAuthTokenAndLogin(token, Uri.parse(url).getQueryParameter("oauth_verifier"));
                            System.out.println(Uri.parse(url).toString());
                        }
                    }
                });
                mTwitterView.loadUrl(token.getAuthorizationURL());
            }
        }.execute();
    }

    private void getTwitterOAuthTokenAndLogin(final RequestToken requestToken, final String oauthVerifier) {
        // once a user authorizes the application, get the auth token and return to the MainActivity
        new AsyncTask<Void, Void, AccessToken>() {
            @Override
            protected AccessToken doInBackground(Void... params) {
                AccessToken accessToken = null;
                try {
                    accessToken = mTwitter.getOAuthAccessToken(requestToken, oauthVerifier);
                } catch (TwitterException te) {
                    Log.e(TAG, te.toString());
                }
                return accessToken;
            }

            @Override
            protected void onPostExecute(AccessToken token) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("oauth_token", token.getToken());
                resultIntent.putExtra("oauth_token_secret", token.getTokenSecret());
                resultIntent.putExtra("user_id", token.getUserId() + "");
                setResult(SocialLogin.RC_TWITTER_LOGIN, resultIntent);
                finish();
            }
        }.execute();
    }
}