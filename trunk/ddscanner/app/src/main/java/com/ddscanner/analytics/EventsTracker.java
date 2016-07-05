package com.ddscanner.analytics;

import android.os.Bundle;

import com.flurry.android.FlurryAgent;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Map;

public class EventsTracker {

    private static final String EVENT_NAME_DIVE_SPOT_VIEW = "dive_spot_view";
    private static final String EVENT_NAME_DIVE_CENTER_VIEW = "dive_center_view";
    private static final String EVENT_PARAMETER_NAME_DIVE_SPOT_ID = "dive_spot_id";
    private static final String EVENT_PARAMETER_NAME_DIVE_CENTER_ID = "dive_center_id";
    private static final String EVENT_PARAMETER_NAME_VIEW_FORM = "source";

    private static final String EVENT_NAME_CHECK_IN = "check_in";
    private static final String EVENT_PARAMETER_CHECK_IN_STEP_BUTTON_CLICK = "click";
    private static final String EVENT_PARAMETER_CHECK_IN_STEP_CONFIRMATION_YES = "confirmed";
    private static final String EVENT_PARAMETER_CHECK_IN_STEP_CONFIRMATION_NO = "cancelled";

    private static final String EVENT_NAME_CONTACT_DIVE_CENTER = "contact_dive_center";
    private static final String EVENT_PARAMETER_CONTACT_DIVE_CENTER_TYPE_PHONE_CALL = "phone_call";
    private static final String EVENT_PARAMETER_CONTACT_DIVE_CENTER_TYPE_EMAIL = "email";

//    private static final String EVENT_NAME_ = "";

    private EventsTracker() {

    }

    public static void trackDiveSpotView(String diveSpotId, SpotViewSource spotViewSource) {
        // Google Firebase
        // way 1
//        Bundle params = new Bundle();
//        params.putLong(EVENT_PARAMETER_NAME_DIVE_SPOT_ID, diveSpotId);
//        params.putString(EVENT_PARAMETER_NAME_VIEW_FORM, spotViewSource.getName());
//        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOT_VIEW, params);
        // way 2
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, "dive_spot");
        params.putString(FirebaseAnalytics.Param.ITEM_ID, diveSpotId);
        params.putString(FirebaseAnalytics.Param.ORIGIN, spotViewSource.getName());
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);

        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_DIVE_SPOT_ID, diveSpotId);
        flurryParams.put(EVENT_PARAMETER_NAME_VIEW_FORM, spotViewSource.getName());
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_VIEW, flurryParams);
    }

    public static void trackDiveCenterView(long diveCenterId, SpotViewSource spotViewSource) {
        // Google Firebase
        Bundle params = new Bundle();
        params.putLong(EVENT_PARAMETER_NAME_DIVE_CENTER_ID, diveCenterId);
        params.putString(EVENT_PARAMETER_NAME_VIEW_FORM, spotViewSource.getName());
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_CENTER_VIEW, params);
    }

    public enum SpotViewSource {
        FROM_MAP("map"), FROM_LIST("list"), FROM_SEARCH("search"), FROM_ACTIVITIES("activities"), FROM_NOTIFICATIONS("notifications"), FROM_PROFILE_CHECKINS("profile_checkins"), FROM_PROFILE_CREATED("profile_created"), FROM_PROFILE_EDITED("profile_edited"), FROM_PROFILE_FAVOURITES("profile_favourites"), UNKNOWN("unknown");

        private String name;

        SpotViewSource(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static SpotViewSource getByName(String name) {
            switch (name) {
                case "map":
                    return FROM_MAP;
                case "list":
                    return FROM_LIST;
                case "search":
                    return FROM_SEARCH;
                case "activities":
                    return FROM_ACTIVITIES;
                case "notifications":
                    return FROM_NOTIFICATIONS;
                case "profile_edited":
                    return FROM_PROFILE_EDITED;
                case "profile_created":
                    return FROM_PROFILE_CREATED;
                case "profile_checkins":
                    return FROM_PROFILE_CHECKINS;
                case "profile_favourites":
                    return FROM_PROFILE_FAVOURITES;
                default:
                    return UNKNOWN;
            }
        }
    }

}
