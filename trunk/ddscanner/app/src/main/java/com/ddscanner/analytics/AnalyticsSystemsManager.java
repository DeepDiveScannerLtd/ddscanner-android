package com.ddscanner.analytics;

import android.content.Context;

import com.ddscanner.BuildConfig;
import com.google.firebase.analytics.FirebaseAnalytics;

public class AnalyticsSystemsManager {

    private static FirebaseAnalytics firebaseAnalytics;

    private AnalyticsSystemsManager() {

    }

    public static void initAnalyticsSystems(Context context) {
        // Init Google Firebase
//        FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(!BuildConfig.DEBUG);
        FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(true);
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static FirebaseAnalytics getFirebaseAnalytics() {
        return firebaseAnalytics;
    }
}
