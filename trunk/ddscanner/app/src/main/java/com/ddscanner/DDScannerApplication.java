package com.ddscanner;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.ddscanner.analytics.AnalyticsSystemsManager;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.InternetClosedActivity;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.facebook.FacebookSdk;
import com.squareup.otto.Bus;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class DDScannerApplication extends Application {

    private static final String TAG = DDScannerApplication.class.getName();

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    public static final String TWITTER_KEY = "tT7PhwjwXb8dEXbhQzI529VR4";
    public static final String TWITTER_SECRET = "C4wijpAOBWWwUVsmtyoMEhWUQD5P6BFulUDTVQGQmrJI32BlaT";
    public static Bus bus = new Bus();
    private static boolean activityVisible;
    public static boolean isActivitiesFragmentVisible = false;

    // These are now application member fields, no static methods involved. This is done for mocking them during instrumentation tests
    private DDScannerRestClient ddScannerRestClient;
    private SharedPreferenceHelper sharedPreferenceHelper;

    private static DDScannerApplication instance;

    public static DDScannerApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Locale.setDefault(new Locale("en_EN"));
//        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
//        }
        FacebookSdk.sdkInitialize(this);
        instance = this;
        AnalyticsSystemsManager.initAnalyticsSystems(this);

        ddScannerRestClient = new DDScannerRestClient();
        sharedPreferenceHelper = new SharedPreferenceHelper();
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static void showErrorActivity(Context context) {
        LogUtils.i(TAG, "showErrorActivity");
        Intent error = new Intent(context, InternetClosedActivity.class);
        error.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(error);
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    public DDScannerRestClient getDdScannerRestClient() {
        return ddScannerRestClient;
    }

    public SharedPreferenceHelper getSharedPreferenceHelper() {
        return sharedPreferenceHelper;
    }
}
