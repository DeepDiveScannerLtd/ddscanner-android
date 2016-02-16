package travel.ilave.deepdivescanner;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.firebase.client.Firebase;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Vitaly on 28.11.2015.
 */
public class DDScannerApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "tT7PhwjwXb8dEXbhQzI529VR4";
    private static final String TWITTER_SECRET = "C4wijpAOBWWwUVsmtyoMEhWUQD5P6BFulUDTVQGQmrJI32BlaT";


    private static DDScannerApplication instance;

    public static DDScannerApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        Firebase.setAndroidContext(this);
        instance = this;
        Fresco.initialize(this);
    }
}
