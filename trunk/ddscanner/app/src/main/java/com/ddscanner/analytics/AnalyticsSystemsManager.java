package com.ddscanner.analytics;

import android.content.Context;
import android.text.TextUtils;

import com.ddscanner.R;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class AnalyticsSystemsManager {

//    private static FirebaseAnalytics firebaseAnalytics;
    public static GoogleAnalytics analytics;
    public static Tracker googleAnalyticsEventsTracker;

    private AnalyticsSystemsManager() {

    }

    public static void initAnalyticsSystems(Context context) {
        // Init Google Firebase
//        FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(!BuildConfig.DEBUG);
//        FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(true);
//        firebaseAnalytics = FirebaseAnalytics.getInstance(context);

        // Init flurry
        FlurryAgent.init(context, context.getString(R.string.flurry_api_key));
        if (!TextUtils.isEmpty(SharedPreferenceHelper.getUserAppId())) {
            FlurryAgent.setUserId(SharedPreferenceHelper.getUserAppId());
        }

        //Google analytics
        analytics = GoogleAnalytics.getInstance(context);
        googleAnalyticsEventsTracker = analytics.newTracker(R.string.google_analytics_trackingId);

    }

    public static void setUserIdForAnalytics(String userAppId) {
        // Flurry
        FlurryAgent.setUserId(userAppId);
    }

//    public static FirebaseAnalytics getFirebaseAnalytics() {
//        return firebaseAnalytics;
//    }
}
