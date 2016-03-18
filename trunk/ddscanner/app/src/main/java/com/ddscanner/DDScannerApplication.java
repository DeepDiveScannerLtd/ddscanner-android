package com.ddscanner;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Vitaly on 28.11.2015.
 */
public class DDScannerApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    public static final String TWITTER_KEY = "tT7PhwjwXb8dEXbhQzI529VR4";
    public static final String TWITTER_SECRET = "C4wijpAOBWWwUVsmtyoMEhWUQD5P6BFulUDTVQGQmrJI32BlaT";



    private static DDScannerApplication instance;

    public static DDScannerApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig));
        FacebookSdk.sdkInitialize(this);
        instance = this;
        Fresco.initialize(this);

       /* TwitterSession twitterSession = Twitter.getSessionManager().getActiveSession();
        TwitterAuthToken authToken = twitterSession.getAuthToken();*/


    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
