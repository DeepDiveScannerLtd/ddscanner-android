package com.ddscanner.analytics;

import android.os.Bundle;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.BuildConfig;
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
    private static final String EVENT_NAME_SEARCH_SEA_LIFE = "search_sea_life";
    private static final String EVENT_NAME_USER_LIKES_VIEW = "user_likes_view";
    private static final String EVENT_NAME_USER_DISLIKES_VIEW = "user_dislikes_view";
    private static final String EVENT_NAME_REVIEWER_CREATED_VIEW = "reviewer_created_view";
    private static final String EVENT_NAME_REVIEWER_EDITED_VIEW = "reviewer_edited_view";
    private static final String EVENT_NAME_REVIEWER_CHECK_INS_VIEW = "reviewer_check_ins_view";
    private static final String EVENT_NAME_REVIEWER_REVIEWS_VIEW = "reviewer_reviews_view";
    private static final String EVENT_NAME_USER_ACHIEVEMENTS_VIEW = "user_achievements_view";
    private static final String EVENT_NAME_USER_REVIEWS_VIEW = "user_reviews_view";

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
    private static final String EVENT_NAME_DIVE_SPOT_REPORT_PHOTO = "dive_spot_photo_report";
    private static final String EVENT_NAME_DIVE_SPOT_EDITED = "dive_spot_edited";
    private static final String EVENT_NAME_DIVE_SPOT_CREATED = "dive_spot_created";
    private static final String EVENT_NAME_SEALIFE_CREATE = "create_sea_life";
    private static final String EVENT_NAME_SEALIFE_CREATED = "sea_life_created";
    private static final String EVENT_NAME_REPORT_REVIEW = "reviewer_review_report";
    private static final String EVENT_NAME_DELETE_REVIEW = "user_review_delete";
    private static final String EVENT_NAME_DELETED_REVIEW = "user_review_deleted";
    private static final String EVENT_NAME_EDIT_REVIEW = "user_review_edit";
    private static final String EVENT_NAME_EDITED_REVIEW = "user_review_edited";
    private static final String EVENT_NAME_REVIEWER_FACEBOOK_OPENED = "reviewer_facebook_open";
    private static final String EVENT_NAME_DIVE_SPOT__PHOTO_REPORT_SENT = "dive_spot_photo_report_sent";
    private static final String EVENT_NAME_REVIEWER_REVIEW_REPORT_SENT = "reviewer_review_report_sent";
    private static final String EVENT_NAME_REVIEW_SHOW_ALL = "review_show_all";

    // ----------------------------------------------------
    // UserOld activity
    // ----------------------------------------------------

    private static final String EVENT_NAME_CHECK_IN = "check_in";
    private static final String EVENT_PARAMETER_NAME_CHECK_IN_STATUS = "status";
    private static final String EVENT_NAME_CHECK_OUT = "check_out";

    private static final String EVENT_NAME_SEND_REVIEW = "send_review";
    private static final String EVENT_PARAMETER_NAME_SEND_REVIEW_SOURCE = "source";

    private static final String EVENT_NAME_COMMENT_LIKED = "comment_liked";
    private static final String EVENT_NAME_COMMENT_DISLIKED = "comment_disliked";

    private static final String EVENT_NAME_DIVE_SPOT_PHOTO_ADDED = "dive_spot_photo_added";

    private static final String EVENT_NAME_GUIDE_USEFUL = "guide_useful";
    private static final String EVENT_NAME_GUIDE_NOT_USEFUL = "guide_not_useful";
    private static final String EVENT_PARAMETER_NAME_QUESTION = "question";

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
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

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
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

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

    public static void trackGuideUseful(String question) {

        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        Bundle params = new Bundle();
        params.putString(EVENT_PARAMETER_NAME_QUESTION, question);
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_GUIDE_USEFUL, params);

        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_QUESTION, question);
        FlurryAgent.logEvent(EVENT_NAME_GUIDE_USEFUL, flurryParams);

        // Appsflyer
        Map<String, Object> appsflyerParams = new HashMap<>();
        appsflyerParams.put(EVENT_PARAMETER_NAME_QUESTION, question);
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_GUIDE_USEFUL, appsflyerParams);

    }

    public static void trackGuideNotUseful(String question) {

        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        Bundle params = new Bundle();
        params.putString(EVENT_PARAMETER_NAME_QUESTION, question);
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_GUIDE_NOT_USEFUL, params);

        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_QUESTION, question);
        FlurryAgent.logEvent(EVENT_NAME_GUIDE_NOT_USEFUL, flurryParams);

        // Appsflyer
        Map<String, Object> appsflyerParams = new HashMap<>();
        appsflyerParams.put(EVENT_PARAMETER_NAME_QUESTION, question);
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_GUIDE_NOT_USEFUL, appsflyerParams);


    }

    public static void trackDiveSpotValid() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

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
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

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
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_EDIT_DIVE_SPOT, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_EDIT_DIVE_SPOT);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_EDIT_DIVE_SPOT, null);
    }

    public static void trackDiveSpotCreation() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_CREATE_DIVE_SPOT, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_CREATE_DIVE_SPOT);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_CREATE_DIVE_SPOT, null);
    }

    public static void trackSealifeCreation() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_SEALIFE_CREATE, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_SEALIFE_CREATE);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_SEALIFE_CREATE, null);
    }

    public static void trackSealifeCreated() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_SEALIFE_CREATED, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_SEALIFE_CREATED);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_SEALIFE_CREATED, null);
    }

    public static void trackReviewEdited() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_EDITED_REVIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_EDITED_REVIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_EDITED_REVIEW, null);
    }

    public static void trackReviewrFacebookOpened() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_REVIEWER_FACEBOOK_OPENED, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_REVIEWER_FACEBOOK_OPENED);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_REVIEWER_FACEBOOK_OPENED, null);
    }

    public static void trackDiveSpotReviewReportSent() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_REVIEWER_REVIEW_REPORT_SENT, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_REVIEWER_REVIEW_REPORT_SENT);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_REVIEWER_REVIEW_REPORT_SENT, null);
    }

    public static void trackDiveSpotphotoReportSent() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOT__PHOTO_REPORT_SENT, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT__PHOTO_REPORT_SENT);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOT__PHOTO_REPORT_SENT, null);
    }

    public static void trackDivespotCreated() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOT_CREATED, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_CREATED);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOT_CREATED, null);
    }

     public static void trackDivespotEdited() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOT_EDITED, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_EDITED);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOT_EDITED, null);
    }

    public static void trackReviewShowAll() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_REVIEW_SHOW_ALL, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_REVIEW_SHOW_ALL);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_REVIEW_SHOW_ALL, null);
    }


    public static void trackCheckIn(CheckInStatus status) {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

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
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_CHECK_OUT, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_CHECK_OUT);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_CHECK_OUT, null);
    }

    public static void trackSendReview(SendReviewSource sendReviewSource) {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

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
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_COMMENT_LIKED, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_COMMENT_LIKED);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_COMMENT_LIKED, null);
    }

    public static void trackCommentDisliked() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_COMMENT_DISLIKED, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_COMMENT_DISLIKED);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_COMMENT_DISLIKED, null);
    }

    public static void trackDiveSpotPhotoAdded() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOT_PHOTO_ADDED, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_PHOTO_ADDED);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOT_PHOTO_ADDED, null);
    }

    public static void trackContactDiveCenter(ContactDiveCenterMethod method) {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

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
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOTS_MAP_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOTS_MAP_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOTS_MAP_VIEW, null);
    }

    public static void trackDiveSpotListView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOTS_LIST_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOTS_LIST_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOTS_LIST_VIEW, null);
    }

    public static void trackDiveCentersMapView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_CENTERS_MAP_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_CENTERS_MAP_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_CENTERS_MAP_VIEW, null);
    }

    public static void trackDiveCentersListView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_CENTERS_LIST_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_CENTERS_LIST_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_CENTERS_LIST_VIEW, null);
    }

    public static void trackNotificationsView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_NOTIFICATIONS_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_NOTIFICATIONS_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_NOTIFICATIONS_VIEW, null);
    }

    public static void trackActivityView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_ACTIVITY_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_ACTIVITY_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_ACTIVITY_VIEW, null);
    }

    public static void trackUserProfileView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_USER_PROFILE_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_USER_PROFILE_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_USER_PROFILE_VIEW, null);
    }

    public static void trackUserCheckinsView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_USER_CHECK_INS_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_USER_CHECK_INS_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_USER_CHECK_INS_VIEW, null);
    }

    public static void trackUserEditedView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_USER_EDITED_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_USER_EDITED_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_USER_EDITED_VIEW, null);
    }

    public static void trackUserCreatedView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_USER_CREATED_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_USER_CREATED_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_USER_CREATED_VIEW, null);
    }

    public static void trackUserFavoritesView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_USER_FAVORITES_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_USER_FAVORITES_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_USER_FAVORITES_VIEW, null);
    }

    public static void trackDiveSpotCheckinsView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOT_CHECK_INS_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_CHECK_INS_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOT_CHECK_INS_VIEW, null);
    }

    public static void trackDiveSpotPhotosView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOT_PHOTOS_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_PHOTOS_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOT_PHOTOS_VIEW, null);
    }

    public static void trackDiveSpotSealifeView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOT_SEALIFE_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_SEALIFE_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOT_SEALIFE_VIEW, null);
    }

    public static void trackDeviSpotReviewsView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOT_REVIEWS_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_REVIEWS_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOT_REVIEWS_VIEW, null);
    }

    public static void trackReviewerProfileView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_REVIEWER_PROFILE_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_REVIEWER_PROFILE_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_REVIEWER_PROFILE_VIEW, null);
    }

    public static void trackSearchByDiveSpot() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_SEARCH_BY_DIVE_SPOT, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_SEARCH_BY_DIVE_SPOT);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_SEARCH_BY_DIVE_SPOT, null);
    }

    public static void trackSearchByLocation() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_SEARCH_BY_LOCATION, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_SEARCH_BY_LOCATION);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_SEARCH_BY_LOCATION, null);
    }

    public static void trackSearchSeaLife() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_SEARCH_SEA_LIFE, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_SEARCH_SEA_LIFE);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_SEARCH_SEA_LIFE, null);
    }

    public static void trackPhotoReport() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_SPOT_REPORT_PHOTO, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_REPORT_PHOTO);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOT_REPORT_PHOTO, null);
    }

    public static void trackReviewReport() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_REPORT_REVIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_REPORT_REVIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_REPORT_REVIEW, null);
    }

    public static void trackDeleteReview() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DELETE_REVIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DELETE_REVIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DELETE_REVIEW, null);
    }

    public static void trackReviewDeleted() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DELETED_REVIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DELETED_REVIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DELETED_REVIEW, null);
    }

    public static void trackEditReview() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_EDIT_REVIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_EDIT_REVIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_EDIT_REVIEW, null);
    }

    public static void trackUserLikesView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_USER_LIKES_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_USER_LIKES_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_USER_LIKES_VIEW, null);
    }

    public static void trackUserDislikesView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_USER_DISLIKES_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_USER_DISLIKES_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_USER_DISLIKES_VIEW, null);
    }

    public static void trackUserAchievementsView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_USER_ACHIEVEMENTS_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_USER_ACHIEVEMENTS_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_USER_ACHIEVEMENTS_VIEW, null);
    }

    public static void trackUserReviewsView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_USER_REVIEWS_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_USER_REVIEWS_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_USER_REVIEWS_VIEW, null);
    }

    public static void trackReviewerReviewsView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_REVIEWER_REVIEWS_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_REVIEWER_REVIEWS_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_REVIEWER_REVIEWS_VIEW, null);
    }

    public static void trackReviewerCreatedView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_REVIEWER_CREATED_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_REVIEWER_CREATED_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_REVIEWER_CREATED_VIEW, null);
    }

    public static void trackReviewerEditedView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_REVIEWER_EDITED_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_REVIEWER_EDITED_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_REVIEWER_EDITED_VIEW, null);
    }

    public static void trackReviewerCheckInsView() {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_REVIEWER_CHECK_INS_VIEW, null);

        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_REVIEWER_CHECK_INS_VIEW);

        // Appsflyer
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_REVIEWER_CHECK_INS_VIEW, null);
    }

    public static void trackUnknownServerError(String url, String errorText) {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

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
        FROM_PROFILE_REVIEWS("profile_reviews"),
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
                case "profile_reviews":
                    return FROM_PROFILE_REVIEWS;
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
