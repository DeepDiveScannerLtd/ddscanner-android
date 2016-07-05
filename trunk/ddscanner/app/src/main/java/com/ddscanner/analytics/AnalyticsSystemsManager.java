package com.ddscanner.analytics;

import android.content.Context;

import com.ddscanner.R;
import com.flurry.android.FlurryAgent;
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

        // Init flurry
        FlurryAgent.init(context, context.getString(R.string.flurry_api_key));
    }

    public static FirebaseAnalytics getFirebaseAnalytics() {
        return firebaseAnalytics;
    }
}
