package com.ddscanner;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.ddscanner.analytics.AnalyticsSystemsManager;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.utils.DialogHelpers;
import com.ddscanner.utils.DiveSpotPhotosContainer;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.facebook.FacebookSdk;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class DDScannerApplication extends Application {

    private static final String TAG = DDScannerApplication.class.getName();

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    public static Bus bus = new Bus();
    private static boolean activityVisible;
    public static boolean isActivitiesFragmentVisible = false;
    private static String activeUserType;

    // These are now application member fields, no static methods involved. This is done for mocking them during instrumentation tests
    private DDScannerRestClient ddScannerRestClient;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private DialogHelpers dialogHelpers;
    private DiveSpotPhotosContainer diveSpotPhotosContainer = new DiveSpotPhotosContainer();
    private ArrayList<String> notificationsContainer = new ArrayList<>();

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
        activeUserType = SharedPreferenceHelper.getActiveUserType().getName();
        dialogHelpers = new DialogHelpers();
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static void showErrorActivity(Context context) {
//        Log.i(TAG, "showErrorActivity");
//        Intent error = new Intent(context, InternetClosedActivity.class);
//        error.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(error);
    }

    public DialogHelpers getDialogHelpers() {
        return dialogHelpers;
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

    public DDScannerRestClient getDdScannerRestClient(Activity context) {
        ddScannerRestClient.with(context);
        return ddScannerRestClient;
    }

    public SharedPreferenceHelper getSharedPreferenceHelper() {
        return sharedPreferenceHelper;
    }

    public DiveSpotPhotosContainer getDiveSpotPhotosContainer() {
        return diveSpotPhotosContainer;
    }

    public ArrayList<String> getNotificationsContainer() {
        return notificationsContainer;
    }

    public void addNotificationToList(String id) {
        notificationsContainer.add(id);
    }

    public void clearNotificationsContainer() {
        this.notificationsContainer.clear();
    }

    public void setActiveUserType(String userType) {
        activeUserType = userType;
    }

    public String getActiveUserType() {
        return activeUserType;
    }

}
