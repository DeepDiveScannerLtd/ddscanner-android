package com.ddscanner.analytics;

import android.os.Bundle;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.DDScannerApplication;
import com.flurry.android.FlurryAgent;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Map;

public class EventsTracker {

    private static final String EVENT_NAME_CONTACT_DIVE_CENTER = "contact_dive_center";
    private static final String EVENT_PARAMETER_NAME_CONTACT_DIVE_CENTER_METHOD = "method";

    // ----------------------------------------------------
    // Screens views
    // ----------------------------------------------------

    private static final String EVENT_NAME_DIVE_SPOTS_MAP_VIEW = "dive_spots_map_view";
    private static final String EVENT_NAME_DIVE_SPOTS_LIST_VIEW = "dive_spots_list_view";
    private static final String EVENT_NAME_DIVE_CENTERS_MAP_VIEW = "dive_centers_map_view";
    private static final String EVENT_NAME_DIVE_CENTERS_LIST_VIEW = "dive_centers_list_view";
    private static final String EVENT_NAME_NOTIFICATIONS_VIEW = "notifications_view";
    private static final String EVENT_NAME_ACTIVITY_VIEW = "activity_view";
    private static final String EVENT_NAME_USER_PROFILE_VIEW = "user_profile_view";
    private static final String EVENT_NAME_USER_CHECK_INS_VIEW = "user_check_ins_view";
    private static final String EVENT_NAME_USER_CREATED_VIEW = "user_created_view";
    private static final String EVENT_NAME_USER_EDITED_VIEW = "user_edited_view";
    private static final String EVENT_NAME_USER_FAVORITES_VIEW = "user_favourites_view";
    private static final String EVENT_NAME_DIVE_SPOT_CHECK_INS_VIEW = "dive_spot_check_ins_view";
    private static final String EVENT_NAME_DIVE_SPOT_PHOTOS_VIEW = "dive_spot_photos_view";
    private static final String EVENT_NAME_DIVE_SPOT_SEALIFE_VIEW = "dive_spot_sealife_view";
    private static final String EVENT_NAME_DIVE_SPOT_REVIEWS_VIEW = "dive_spot_reviews_view";
    private static final String EVENT_NAME_REVIEWER_PROFILE_VIEW = "reviewer_profile_view";
    private static final String EVENT_NAME_SEARCH_BY_DIVE_SPOT = "search_by_dive_spot";
    private static final String EVENT_NAME_SEARCH_BY_LOCATION = "search_by_location";

    private static final String EVENT_NAME_DIVE_SPOT_VIEW = "dive_spot_view";
    private static final String EVENT_NAME_DIVE_CENTER_VIEW = "dive_center_view";
    private static final String EVENT_PARAMETER_NAME_DIVE_SPOT_ID = "dive_spot_id";
    private static final String EVENT_PARAMETER_NAME_DIVE_CENTER_ID = "dive_center_id";
    private static final String EVENT_PARAMETER_NAME_VIEW_SOURCE = "source";

    // ----------------------------------------------------
    // Content management
    // ----------------------------------------------------

    private static final String EVENT_NAME_DIVE_SPOT_VALID = "dive_spot_valid";
    private static final String EVENT_NAME_DIVE_SPOT_INVALID = "dive_spot_invalid";
//    private static final String EVENT_PARAMETER_NAME_DIVE_SPOT_VALIDATION_RESULT = "result";

    private static final String EVENT_NAME_EDIT_DIVE_SPOT = "edit_dive_spot";

    private static final String EVENT_NAME_CREATE_DIVE_SPOT = "create_dive_spot";

    // ----------------------------------------------------
    // User activity
    // ----------------------------------------------------

    private static final String EVENT_NAME_CHECK_IN = "check_in";
    private static final String EVENT_PARAMETER_NAME_CHECK_IN_STATUS = "status";
    private static final String EVENT_NAME_CHECK_OUT = "check_out";

    private static final String EVENT_NAME_SEND_REVIEW = "send_review";
    private static final String EVENT_PARAMETER_NAME_SEND_REVIEW_SOURCE = "source";

    private static final String EVENT_NAME_COMMENT_LIKED = "comment_liked";
    private static final String EVENT_NAME_COMMENT_DISLIKED = "comment_disliked";

    private static final String EVENT_NAME_DIVE_SPOT_PHOTO_ADDED = "dive_spot_photo_added";

    private static final String EVENT_NAME_USER_PROFILE_EDITED = "user_profile_edited";

    // ----------------------------------------------------
    // Logging
    // ----------------------------------------------------

    private static final String EVENT_NAME_UNKNOWN_SERVER_ERROR = "unknown_error";
    private static final String EVENT_PARAMETER_NAME_ERROR_URL = "url";
    private static final String EVENT_PARAMETER_NAME_ERROR_TEXT = "text";

    // ----------------------------------------------------
    // ----------------------------------------------------
    // ----------------------------------------------------

    private EventsTracker() {

    }

    public static void trackDiveSpotView(String diveSpotId, SpotViewSource spotViewSource) {
        // Google Firebase
        // way 1
//        Bundle params = new Bundle();
//        params.putLong(EVENT_PARAMETER_NAME_DIVE_SPOT_ID, diveSpotId);
//        params.putString(EVENT_PARAMETER_NAME_VIEW_SOURCE, spotViewSource.getName());
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
        flurryParams.put(EVENT_PARAMETER_NAME_VIEW_SOURCE, spotViewSource.getName());
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_VIEW, flurryParams);

        // Appsflyer
        Map<String, Object> appsflyerParams = new HashMap<>();
        appsflyerParams.put(EVENT_PARAMETER_NAME_DIVE_SPOT_ID, diveSpotId);
        appsflyerParams.put(EVENT_PARAMETER_NAME_VIEW_SOURCE, spotViewSource.getName());
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOT_VIEW, appsflyerParams);
    }

    public static void trackDiveCenterView(String diveCenterId, SpotViewSource spotViewSource) {
        // Google Firebase
        Bundle params = new Bundle();
        params.putString(EVENT_PARAMETER_NAME_DIVE_CENTER_ID, diveCenterId);
        params.putString(EVENT_PARAMETER_NAME_VIEW_SOURCE, spotViewSource.getName());
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_CENTER_VIEW, params);

        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_DIVE_CENTER_ID, diveCenterId);
        flurryParams.put(EVENT_PARAMETER_NAME_VIEW_SOURCE, spotViewSource.getName());
        FlurryAgent.logEvent(EVENT_NAME_DIVE_CENTER_VIEW, flurryParams);

        // Appsflyer
        Map<String, Object> appsflyerParams = new HashMap<>();
        appsflyerParams.put(EVENT_PARAMETER_NAME_DIVE_CENTER_ID, diveCenterId);
        appsflyerParams.put(EVENT_PARAMETER_NAME_VIEW_SOURCE, spotViewSource.getName());
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_CENTER_VIEW, appsflyerParams);
    }

    public static void trackDiveSpotValid() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOT_VALID, null);

        // Flurry
//        Map<String, String> flurryParams = new HashMap<>();
//        flurryParams.put(EVENT_PARAMETER_NAME_DIVE_SPOT_VALIDATION_RESULT, result.getName());
//        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_VALID, flurryParams);
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_VALID);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOT_VALID, null);
    }

    public static void trackDiveSpotInvalid() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOT_INVALID, null);

        // Flurry
//        Map<String, String> flurryParams = new HashMap<>();
//        flurryParams.put(EVENT_PARAMETER_NAME_DIVE_SPOT_VALIDATION_RESULT, result.getName());
//        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_INVALID, flurryParams);
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_INVALID);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOT_INVALID, null);
    }

    public static void trackDiveSpotEdit() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_EDIT_DIVE_SPOT, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_EDIT_DIVE_SPOT);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_EDIT_DIVE_SPOT, null);
    }

    public static void trackDiveSpotCreation() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_CREATE_DIVE_SPOT, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_CREATE_DIVE_SPOT);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_CREATE_DIVE_SPOT, null);
    }

    public static void trackCheckIn(CheckInStatus status) {
        // Google Firebase
        Bundle params = new Bundle();
        params.putString(EVENT_PARAMETER_NAME_CHECK_IN_STATUS, status.getName());
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_CHECK_IN, params);

        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_CHECK_IN_STATUS, status.getName());
        FlurryAgent.logEvent(EVENT_NAME_CHECK_IN, flurryParams);

        // Appsflyer
        Map<String, Object> appsflyerParams = new HashMap<>();
        appsflyerParams.put(EVENT_PARAMETER_NAME_CHECK_IN_STATUS, status.getName());
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_CHECK_IN, appsflyerParams);
    }

    public static void trackCheckOut() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_CHECK_OUT, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_CHECK_OUT);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_CHECK_OUT, null);
    }

    public static void trackReviewSending(SendReviewSource sendReviewSource) {
        // Google Firebase
        Bundle params = new Bundle();
        params.putString(EVENT_PARAMETER_NAME_SEND_REVIEW_SOURCE, sendReviewSource.getName());
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_SEND_REVIEW, params);

        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_SEND_REVIEW_SOURCE, sendReviewSource.getName());
        FlurryAgent.logEvent(EVENT_NAME_SEND_REVIEW, flurryParams);

        // Appsflyer
        Map<String, Object> appsflyerParams = new HashMap<>();
        appsflyerParams.put(EVENT_PARAMETER_NAME_SEND_REVIEW_SOURCE, sendReviewSource.getName());
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_SEND_REVIEW, appsflyerParams);
    }

    public static void trackCommentLiked() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_COMMENT_LIKED, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_COMMENT_LIKED);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_COMMENT_LIKED, null);
    }

    public static void trackCommentDisliked() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_COMMENT_DISLIKED, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_COMMENT_DISLIKED);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_COMMENT_DISLIKED, null);
    }

    public static void trackDiveSpotPhotoAdded() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOT_PHOTO_ADDED, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_PHOTO_ADDED);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOT_PHOTO_ADDED, null);
    }

    public static void trackContactDiveCenter(ContactDiveCenterMethod method) {
        // Google Firebase
        Bundle params = new Bundle();
        params.putString(EVENT_PARAMETER_NAME_CONTACT_DIVE_CENTER_METHOD, method.getName());
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_CONTACT_DIVE_CENTER, params);

        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_CONTACT_DIVE_CENTER_METHOD, method.getName());
        FlurryAgent.logEvent(EVENT_NAME_CONTACT_DIVE_CENTER, flurryParams);

        // Appsflyer
        Map<String, Object> appsflyerParams = new HashMap<>();
        appsflyerParams.put(EVENT_PARAMETER_NAME_CONTACT_DIVE_CENTER_METHOD, method.getName());
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_CONTACT_DIVE_CENTER, appsflyerParams);
    }

    public static void trackDiveSpotMapView() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOTS_MAP_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOTS_MAP_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOTS_MAP_VIEW, null);
    }

    public static void trackDiveSpotListView() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOTS_LIST_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOTS_LIST_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOTS_LIST_VIEW, null);
    }

    public static void trackDiveCentersMapView() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_CENTERS_MAP_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_CENTERS_MAP_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_CENTERS_MAP_VIEW, null);
    }

    public static void trackDiveCentersListView() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_CENTERS_LIST_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_CENTERS_LIST_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_CENTERS_LIST_VIEW, null);
    }

    public static void trackNotificationsView() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_NOTIFICATIONS_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_NOTIFICATIONS_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_NOTIFICATIONS_VIEW, null);
    }

    public static void trackActivityView() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_ACTIVITY_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_ACTIVITY_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_ACTIVITY_VIEW, null);
    }

    public static void trackUserProfileView() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_USER_PROFILE_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_USER_PROFILE_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_USER_PROFILE_VIEW, null);
    }

    public static void trackUserCheckinsView() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_USER_CHECK_INS_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_USER_CHECK_INS_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_USER_CHECK_INS_VIEW, null);
    }

    public static void trackUserEditedView() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_USER_EDITED_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_USER_EDITED_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_USER_EDITED_VIEW, null);
    }

    public static void trackUserCreatedView() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_USER_CREATED_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_USER_CREATED_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_USER_CREATED_VIEW, null);
    }

    public static void trackUserFavoritesView() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_USER_FAVORITES_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_USER_FAVORITES_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_USER_FAVORITES_VIEW, null);
    }

    public static void trackDiveSpotCheckinsView() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOT_CHECK_INS_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_CHECK_INS_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOT_CHECK_INS_VIEW, null);
    }

    public static void trackDiveSpotPhotosView() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOT_PHOTOS_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_PHOTOS_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOT_PHOTOS_VIEW, null);
    }

    public static void trackDiveSpotSealifeView() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOT_SEALIFE_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_SEALIFE_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOT_SEALIFE_VIEW, null);
    }

    public static void trackDeviSpotReviewsView() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOT_REVIEWS_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_REVIEWS_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOT_REVIEWS_VIEW, null);
    }

    public static void trackReviewerProfileView() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_REVIEWER_PROFILE_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_REVIEWER_PROFILE_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_REVIEWER_PROFILE_VIEW, null);
    }

    public static void trackSearchByDiveSpot() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_SEARCH_BY_DIVE_SPOT, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_SEARCH_BY_DIVE_SPOT);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_SEARCH_BY_DIVE_SPOT, null);
    }

    public static void trackSearchByLocation() {
        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_SEARCH_BY_LOCATION, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_SEARCH_BY_LOCATION);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_SEARCH_BY_LOCATION, null);
    }

    public static void trackUnknownServerError(String url, String errorText) {
        // Google Firebase
//        Bundle params = new Bundle();
//        params.putString(EVENT_PARAMETER_NAME_ERROR_TEXT, errorText);
//        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_UNKNOWN_SERVER_ERROR, params);

        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_ERROR_URL, url);
        flurryParams.put(EVENT_PARAMETER_NAME_ERROR_TEXT, errorText);
        FlurryAgent.logEvent(EVENT_NAME_UNKNOWN_SERVER_ERROR, flurryParams);

        // Appsflyer
        Map<String, Object> appsflyerParams = new HashMap<>();
        appsflyerParams.put(EVENT_PARAMETER_NAME_ERROR_URL, url);
        appsflyerParams.put(EVENT_PARAMETER_NAME_ERROR_TEXT, errorText);
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_UNKNOWN_SERVER_ERROR, appsflyerParams);
    }

    public enum SpotViewSource {
        FROM_MAP("map"),
        FROM_LIST("list"),
        FROM_SEARCH("search"),
        FROM_ACTIVITIES("activities"),
        FROM_NOTIFICATIONS("notifications"),
        FROM_PROFILE_CHECKINS("profile_checkins"),
        FROM_PROFILE_CREATED("profile_created"),
        FROM_PROFILE_EDITED("profile_edited"),
        FROM_PROFILE_FAVOURITES("profile_favourites"),
        UNKNOWN("unknown");

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

//    public enum DiveSpotValidationResult {
//        SUCCESS("success"), CANCELLED("cancelled"), CANCELLED_ON_LOGIN("cancelled_on_login"), ERROR("error");
//
//        private String name;
//
//        DiveSpotValidationResult(String name) {
//            this.name = name;
//        }
//
//        public String getName() {
//            return name;
//        }
//    }

    public enum CheckInStatus {
        SUCCESS("success"), CANCELLED("cancelled");

        private String name;

        CheckInStatus(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum SendReviewSource {
        FROM_RATING_BAR("rating_bar"), FROM_EMPTY_REVIEWS_LIST("empty_reviews_list"), FROM_REVIEWS_LIST("reviews_list"), UNKNOWN("unknown");

        private String name;

        private SendReviewSource(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static SendReviewSource getByName(String name) {
            switch (name) {
                case "rating_bar":
                    return FROM_RATING_BAR;
                case "reviews_list":
                    return FROM_REVIEWS_LIST;
                default:
                    return UNKNOWN;
            }
        }
    }

    public enum ContactDiveCenterMethod {
        PHONE_CALL("phone_call"), EMAIL("email"), UNKNOWN("unknown");

        private String name;

        private ContactDiveCenterMethod(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static ContactDiveCenterMethod getByName(String name) {
            switch (name) {
                case "phone_call":
                    return PHONE_CALL;
                case "email":
                    return EMAIL;
                default:
                    return UNKNOWN;
            }
        }
    }

}
