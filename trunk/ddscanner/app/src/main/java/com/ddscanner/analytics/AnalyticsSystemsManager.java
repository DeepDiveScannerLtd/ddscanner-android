package com.ddscanner.analytics;

import android.content.Context;

import com.ddscanner.R;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class AnalyticsSystemsManager {

//    private static FirebaseAnalytics firebaseAnalytics;
    private static GoogleAnalytics analytics;
    private static Tracker tracker;

    private AnalyticsSystemsManager() {

    }

    public static void initAnalyticsSystems(Context context) {
        // Init Google Firebase
//        FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(!BuildConfig.DEBUG);
//        FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(true);
//        firebaseAnalytics = FirebaseAnalytics.getInstance(context);

        // Init flurry
        FlurryAgent.init(context, context.getString(R.string.flurry_api_key));

        //Google analytics
        analytics = GoogleAnalytics.getInstance(context);
        tracker = analytics.newTracker(R.string.google_analytics_trackingId);

    }

//    public static FirebaseAnalytics getFirebaseAnalytics() {
//        return firebaseAnalytics;
//    }
}
