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
    private static final String EVENT_NAME_USER_FAVORITES_VIEW = "user_favorites_view";
    private static final String EVENT_NAME_DIVE_SPOT_CHECK_INS_VIEW = "dive_spot_check_ins_view";
    private static final String EVENT_NAME_DIVE_SPOT_PHOTOS_VIEW = "dive_spot_photos_view";
    private static final String EVENT_NAME_DIVE_SPOT_SEALIFE_VIEW = "dive_spot_sealife_view";
    private static final String EVENT_NAME_DIVE_SPOT_REVIEWS_VIEW = "dive_spot_reviews_view";
    private static final String EVENT_NAME_REVIEWER_PROFILE_VIEW = "foreign_profile_view";
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
    private static final String EVENT_NAME_SKIP_REGISTRATION = "skip_registration";
    private static final String EVENT_NAME_DIVE_SPOT_EDITORS_VIEW = "dive_spot_editors_view";
    private static final String EVENT_NAME_DIVE_SPOT_MAPS_VIEW = "dive_spot_maps_view";
    private static final String EVENT_PARAMETER_NAME_DIVE_CENTER_TYPE = "type";
    private static final String EVENT_NAME_DIVE_SPOT_LOCATION_ON_MAP_VIEW = "dive_spot_location_on_map_view";

    // ----------------------------------------------------
    // Content management
    // ----------------------------------------------------

    private static final String EVENT_NAME_DIVE_SPOT_VALID = "dive_spot_valid";
    private static final String EVENT_NAME_DIVE_SPOT_INVALID = "dive_spot_invalid";
//    private static final String EVENT_PARAMETER_NAME_DIVE_SPOT_VALIDATION_RESULT = "result";

    private static final String EVENT_NAME_EDIT_DIVE_SPOT = "dive_spot_edit_click";

    private static final String EVENT_NAME_CREATE_DIVE_SPOT = "dive_spot_create_click";
    private static final String EVENT_NAME_DIVE_SPOT_REPORT_PHOTO = "dive_spot_photo_report";
    private static final String EVENT_NAME_DIVE_SPOT_EDITED = "dive_spot_edited";
    private static final String EVENT_NAME_DIVE_SPOT_CREATED = "dive_spot_created";
    private static final String EVENT_NAME_SEALIFE_CREATE = "sea_life_create_click";
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
    private static final String EVENT_NAME_REVIEW_SENT = "review_sent";

    // ----------------------------------------------------
    // UserOld activity
    // ----------------------------------------------------

    private static final String EVENT_NAME_CHECK_IN = "check_in";
    private static final String EVENT_PARAMETER_NAME_CHECK_IN_STATUS = "status";
    private static final String EVENT_NAME_CHECK_OUT = "check_out";

    private static final String EVENT_NAME_SEND_REVIEW = "send_review";
    private static final String EVENT_PARAMETER_NAME_SOURCE = "source";

    private static final String EVENT_NAME_COMMENT_LIKED = "review_liked";
    private static final String EVENT_NAME_COMMENT_DISLIKED = "review_disliked";

    private static final String EVENT_NAME_DIVE_SPOT_PHOTO_ADDED = "dive_spot_photo_added";

    private static final String EVENT_NAME_GUIDE_USEFUL = "guide_useful";
    private static final String EVENT_NAME_GUIDE_NOT_USEFUL = "guide_not_useful";
    private static final String EVENT_PARAMETER_NAME_QUESTION = "question";

    private static final String EVENT_NAME_USER_PROFILE_EDITED = "user_profile_edited";

    private static final String EVENT_NAME_EDIT_SEALIFE = "sea_life_edit_click";
    private static final String EVENT_NAME_EDITED_SEALIFE = "sea_life_edited";
    private static final String EVENT_NAME_SKIP_TUTORIAL = "skip_tutorial";

    // ----------------------------------------------------
    // Logging
    // ----------------------------------------------------

    private static final String EVENT_NAME_UNKNOWN_SERVER_ERROR = "unknown_error";
    private static final String EVENT_PARAMETER_NAME_ERROR_URL = "url";
    private static final String EVENT_PARAMETER_NAME_ERROR_TEXT = "text";
    private static final String EVENT_PARAMETER_NAME_USER_TYPE = "user_type";

    // ----------------------------------------------------
    // Sales
    // ----------------------------------------------------

    private static final String EVENT_NAME_REGISTRATION_DIVE_CENTER = "registration_dc";
    private static final String EVENT_NAME_REGISTRATION_DIVER = "registration_diver";
    private static final String EVENT_NAME_YES_IM_INSTRUCTOR = "yes_im_instructor";
    private static final String EVENT_NAME_INSTR_REG_DC_USER_CHOSEN = "dc_search_dc_user_chosen";
    private static final String EVENT_NAME_INSTR_REG_DC_LEGACY_CHOSEN = "dc_search_dc_legacy_chosen";
    private static final String EVENT_NAME_INSTR_REG_DC_LEGACY_INVITED = "dc_search_dc_legacy_invited";
    private static final String EVENT_NAME_INSTR_REG_ADD_NEW_CHOSEN = "dc_search_add_new_chosen";
    private static final String EVENT_NAME_INSTR_REG_DC_NEW_INVITED = "dc_search_dc_new_invited";
    private static final String EVENT_NAME_WATCH_TUTORIAL = "tutorial_watched";
    private static final String EVENT_PARAMETER_NAME_TUTORIAL_PAGE = "page";


    // ----------------------------------------------------
    // ----------------------------------------------------
    // ----------------------------------------------------

    public static void trackDiveSpotLocationOnMapView() {
        trackEventWithoutParameters(EVENT_NAME_DIVE_SPOT_LOCATION_ON_MAP_VIEW);
    }

    public static void trackEditorsView() {
        trackEventWithoutParameters(EVENT_NAME_DIVE_SPOT_EDITORS_VIEW);
    }

    public static void trackMapsView() {
        trackEventWithoutParameters(EVENT_NAME_DIVE_SPOT_MAPS_VIEW);
    }

    public static void trackWatchTutorial() {
        trackEventWithoutParameters(EVENT_NAME_WATCH_TUTORIAL);
    }

    public static void trackSkipRegistration() {
        trackEventWithoutParameters(EVENT_NAME_SKIP_REGISTRATION);
    }

    public static void trackYesInstructorClicked() {
        trackEventWithoutParameters(EVENT_NAME_YES_IM_INSTRUCTOR);
    }

    public static void trackUserAchievementsView(AchievementsViewSource source) {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        String sourceName = source.getName();

        Bundle params = new Bundle();


        params.putString(EVENT_PARAMETER_NAME_VIEW_SOURCE, sourceName);
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_USER_ACHIEVEMENTS_VIEW, params);

        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_VIEW_SOURCE, sourceName);
        FlurryAgent.logEvent(EVENT_NAME_USER_ACHIEVEMENTS_VIEW, flurryParams);

        // Appsflyer
        Map<String, Object> appsflyerParams = new HashMap<>();
        appsflyerParams.put(EVENT_PARAMETER_NAME_VIEW_SOURCE, sourceName);
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_USER_ACHIEVEMENTS_VIEW, appsflyerParams);

        //Facebook
        AnalyticsSystemsManager.getLogger().logEvent(EVENT_NAME_USER_ACHIEVEMENTS_VIEW, params);



//        trackEventWithoutParameters(EVENT_NAME_USER_ACHIEVEMENTS_VIEW);
    }

    public static void trackSkipTutorial(String currentPosition) {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        Bundle params = new Bundle();

//        trackEventWithoutParameters(EVENT_NAME_SKIP_TUTORIAL);

        params.putString(EVENT_PARAMETER_NAME_TUTORIAL_PAGE, currentPosition);
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_SKIP_TUTORIAL, params);

        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_TUTORIAL_PAGE, currentPosition);
        FlurryAgent.logEvent(EVENT_NAME_SKIP_TUTORIAL, flurryParams);

        // Appsflyer
        Map<String, Object> appsflyerParams = new HashMap<>();
        appsflyerParams.put(EVENT_PARAMETER_NAME_TUTORIAL_PAGE, currentPosition);
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_SKIP_TUTORIAL, appsflyerParams);

        //Facebook
        AnalyticsSystemsManager.getLogger().logEvent(EVENT_NAME_SKIP_TUTORIAL, params);

    }

    private static void trackInstructorChosingDcEvenets(String eventName) {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }

        // Google Firebase
        Bundle params = new Bundle();

        params.putString(EVENT_PARAMETER_NAME_SOURCE, DDScannerApplication.getInstance().getSharedPreferenceHelper().getDivecenterSearchSource());
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(eventName, params);

        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_SOURCE, DDScannerApplication.getInstance().getSharedPreferenceHelper().getDivecenterSearchSource());
        FlurryAgent.logEvent(eventName, flurryParams);

        // Appsflyer
        Map<String, Object> appsflyerParams = new HashMap<>();
        appsflyerParams.put(EVENT_PARAMETER_NAME_SOURCE, DDScannerApplication.getInstance().getSharedPreferenceHelper().getDivecenterSearchSource());
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), eventName, appsflyerParams);

        //Facebook
        AnalyticsSystemsManager.getLogger().logEvent(eventName, params);
    }

    public static void trackInstructorRegistrationDcUserChosen() {
        trackInstructorChosingDcEvenets(EVENT_NAME_INSTR_REG_DC_USER_CHOSEN);
    }

    public static void trackInstructorRegistrationDcLegacyChosen() {
        trackInstructorChosingDcEvenets(EVENT_NAME_INSTR_REG_DC_LEGACY_CHOSEN);
    }

    public static void trackInstructorRegistrationDcLegacyInvited() {
        trackInstructorChosingDcEvenets(EVENT_NAME_INSTR_REG_DC_LEGACY_INVITED);
    }

    public static void trackInstructorRegistrationAddNewChosen() {
        trackInstructorChosingDcEvenets(EVENT_NAME_INSTR_REG_ADD_NEW_CHOSEN);
    }

    public static void trackInstructorRegistrationDcNewInvited() {
        trackInstructorChosingDcEvenets(EVENT_NAME_INSTR_REG_DC_NEW_INVITED);
    }

    private EventsTracker() {

    }

    private static void trackEventWithoutParameters(String eventName) {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }
        String userType = DDScannerApplication.getInstance().getActiveUserType();
        // Google Firebase
        Bundle params = new Bundle();

        params.putString(EVENT_PARAMETER_NAME_USER_TYPE, userType);
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(eventName, params);

        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_USER_TYPE, userType);
        FlurryAgent.logEvent(eventName, flurryParams);

        // Appsflyer
        Map<String, Object> appsflyerParams = new HashMap<>();
        appsflyerParams.put(EVENT_PARAMETER_NAME_USER_TYPE, userType);
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), eventName, appsflyerParams);

        //Facebook
        AnalyticsSystemsManager.getLogger().logEvent(eventName, params);
    }

    public static void trackDiveSpotView(String diveSpotId) {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }
        String userType = DDScannerApplication.getInstance().getActiveUserType();
        // Google Firebase
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, "dive_spot");
        params.putString(FirebaseAnalytics.Param.ITEM_ID, diveSpotId);
        params.putString(EVENT_PARAMETER_NAME_USER_TYPE, userType);
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);

        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_DIVE_SPOT_ID, diveSpotId);
        flurryParams.put(EVENT_PARAMETER_NAME_USER_TYPE, userType);
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_VIEW, flurryParams);

        // Appsflyer
        Map<String, Object> appsflyerParams = new HashMap<>();
        appsflyerParams.put(EVENT_PARAMETER_NAME_DIVE_SPOT_ID, diveSpotId);
        appsflyerParams.put(EVENT_PARAMETER_NAME_USER_TYPE, userType);
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_SPOT_VIEW, appsflyerParams);

        //Facebook
        AnalyticsSystemsManager.getLogger().logEvent(EVENT_NAME_DIVE_SPOT_VIEW, params);
    }

    public static void trackDiveCenterView(String diveCenterId, String dcType) {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }
        String userType = DDScannerApplication.getInstance().getActiveUserType();
        // Google Firebase
        Bundle params = new Bundle();
        params.putString(EVENT_PARAMETER_NAME_DIVE_CENTER_ID, diveCenterId);
        params.putString(EVENT_PARAMETER_NAME_DIVE_CENTER_TYPE, dcType);
        params.putString(EVENT_PARAMETER_NAME_USER_TYPE, userType);
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_CENTER_VIEW, params);

        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_DIVE_CENTER_ID, diveCenterId);
        flurryParams.put(EVENT_PARAMETER_NAME_USER_TYPE, userType);
        flurryParams.put(EVENT_PARAMETER_NAME_DIVE_CENTER_TYPE, dcType);
        FlurryAgent.logEvent(EVENT_NAME_DIVE_CENTER_VIEW, flurryParams);

        // Appsflyer
        Map<String, Object> appsflyerParams = new HashMap<>();
        appsflyerParams.put(EVENT_PARAMETER_NAME_DIVE_CENTER_ID, diveCenterId);
        appsflyerParams.put(EVENT_PARAMETER_NAME_USER_TYPE, userType);
        appsflyerParams.put(EVENT_PARAMETER_NAME_DIVE_CENTER_TYPE, dcType);
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_DIVE_CENTER_VIEW, appsflyerParams);

        //Facebook
        AnalyticsSystemsManager.getLogger().logEvent(EVENT_NAME_DIVE_CENTER_VIEW, params);

    }

    public static void trackGuideUseful(String question) {

        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }
        String userType = DDScannerApplication.getInstance().getActiveUserType();
        // Google Firebase
        Bundle params = new Bundle();
        params.putString(EVENT_PARAMETER_NAME_QUESTION, question);
        params.putString(EVENT_PARAMETER_NAME_USER_TYPE, userType);
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_GUIDE_USEFUL, params);

        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_QUESTION, question);
        flurryParams.put(EVENT_PARAMETER_NAME_USER_TYPE, userType);
        FlurryAgent.logEvent(EVENT_NAME_GUIDE_USEFUL, flurryParams);

        // Appsflyer
        Map<String, Object> appsflyerParams = new HashMap<>();
        appsflyerParams.put(EVENT_PARAMETER_NAME_QUESTION, question);
        appsflyerParams.put(EVENT_PARAMETER_NAME_USER_TYPE, userType);
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_GUIDE_USEFUL, appsflyerParams);

        //Facebook
        AnalyticsSystemsManager.getLogger().logEvent(EVENT_NAME_GUIDE_USEFUL, params);

    }

    public static void trackGuideNotUseful(String question) {

        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }
        String userType = DDScannerApplication.getInstance().getActiveUserType();
        // Google Firebase
        Bundle params = new Bundle();
        params.putString(EVENT_PARAMETER_NAME_QUESTION, question);
        params.putString(EVENT_PARAMETER_NAME_USER_TYPE, userType);
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_GUIDE_NOT_USEFUL, params);

        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_QUESTION, question);
        flurryParams.put(EVENT_PARAMETER_NAME_USER_TYPE, userType);
        FlurryAgent.logEvent(EVENT_NAME_GUIDE_NOT_USEFUL, flurryParams);

        // Appsflyer
        Map<String, Object> appsflyerParams = new HashMap<>();
        appsflyerParams.put(EVENT_PARAMETER_NAME_QUESTION, question);
        appsflyerParams.put(EVENT_PARAMETER_NAME_USER_TYPE, userType);
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_GUIDE_NOT_USEFUL, appsflyerParams);

        //Facebook
        AnalyticsSystemsManager.getLogger().logEvent(EVENT_NAME_GUIDE_NOT_USEFUL, params);
    }

    public static void trackRegistration(int userType) {
        if (userType == 0) {
            trackEventWithoutParameters(EVENT_NAME_REGISTRATION_DIVE_CENTER);
        } else {
            trackEventWithoutParameters(EVENT_NAME_REGISTRATION_DIVER);
        }
    }

    public static void trackDiveSpotValid() {
        trackEventWithoutParameters(EVENT_NAME_DIVE_SPOT_VALID);
    }

    public static void trackDiveSpotInvalid() {
        trackEventWithoutParameters(EVENT_NAME_DIVE_SPOT_INVALID);
    }

    public static void trackDiveSpotEdit() {
        trackEventWithoutParameters(EVENT_NAME_EDIT_DIVE_SPOT);
    }

    public static void trackDiveSpotCreation() {
        trackEventWithoutParameters(EVENT_NAME_CREATE_DIVE_SPOT);
    }

    public static void trackSealifeCreation() {
        trackEventWithoutParameters(EVENT_NAME_SEALIFE_CREATE);
    }

    public static void trackSealifeCreated() {
        trackEventWithoutParameters(EVENT_NAME_SEALIFE_CREATED);
    }

    public static void trackReviewEdited() {
        trackEventWithoutParameters(EVENT_NAME_EDITED_REVIEW);
    }

    public static void trackReviewrFacebookOpened() {
        trackEventWithoutParameters(EVENT_NAME_REVIEWER_FACEBOOK_OPENED);
    }

    public static void trackDiveSpotReviewReportSent() {
        trackEventWithoutParameters(EVENT_NAME_REVIEWER_REVIEW_REPORT_SENT);
    }

    public static void trackDiveSpotphotoReportSent() {
        trackEventWithoutParameters(EVENT_NAME_DIVE_SPOT__PHOTO_REPORT_SENT);
    }

    public static void trackDivespotCreated() {
        trackEventWithoutParameters(EVENT_NAME_DIVE_SPOT_CREATED);
    }

    public static void trackDivespotEdited() {
        trackEventWithoutParameters(EVENT_NAME_DIVE_SPOT_EDITED);
    }

    public static void trackReviewShowAll() {
        trackEventWithoutParameters(EVENT_NAME_REVIEW_SHOW_ALL);
    }


    public static void trackCheckIn() {
       trackEventWithoutParameters(EVENT_NAME_CHECK_IN);
    }

    public static void trackCheckOut() {
        trackEventWithoutParameters(EVENT_NAME_CHECK_OUT);
    }

    public static void trackReviewSent() {
        trackEventWithoutParameters(EVENT_NAME_REVIEW_SENT);
    }

    public static void trackSendReview() {
        trackEventWithoutParameters(EVENT_NAME_SEND_REVIEW);
    }

    public static void trackCommentLiked() {
        trackEventWithoutParameters(EVENT_NAME_COMMENT_LIKED);
    }

    public static void trackCommentDisliked() {
        trackEventWithoutParameters(EVENT_NAME_COMMENT_DISLIKED);
    }

    public static void trackDiveSpotPhotoAdded() {
        trackEventWithoutParameters(EVENT_NAME_DIVE_SPOT_PHOTO_ADDED);
    }

    public static void trackContactDiveCenter() {
        trackEventWithoutParameters(EVENT_NAME_CONTACT_DIVE_CENTER);
    }

    public static void trackDiveSpotMapView() {
        trackEventWithoutParameters(EVENT_NAME_DIVE_SPOTS_MAP_VIEW);
    }

    public static void trackDiveSpotListView() {
        trackEventWithoutParameters(EVENT_NAME_DIVE_SPOTS_LIST_VIEW);
    }

    public static void trackDiveCentersMapView() {
        trackEventWithoutParameters(EVENT_NAME_DIVE_CENTERS_MAP_VIEW);
    }

    public static void trackDiveCentersListView() {
        trackEventWithoutParameters(EVENT_NAME_DIVE_CENTERS_LIST_VIEW);
    }

    public static void trackNotificationsView() {
        trackEventWithoutParameters(EVENT_NAME_NOTIFICATIONS_VIEW);
    }

    public static void trackActivityView() {
        trackEventWithoutParameters(EVENT_NAME_ACTIVITY_VIEW);
    }

    public static void trackUserProfileView() {
        trackEventWithoutParameters(EVENT_NAME_USER_PROFILE_VIEW);
    }

    public static void trackUserCheckinsView() {
        trackEventWithoutParameters(EVENT_NAME_USER_CHECK_INS_VIEW);
    }

    public static void trackUserEditedView() {
        trackEventWithoutParameters(EVENT_NAME_USER_EDITED_VIEW);
    }

    public static void trackUserCreatedView() {
        trackEventWithoutParameters(EVENT_NAME_USER_CREATED_VIEW);
    }

    public static void trackUserFavoritesView() {
        trackEventWithoutParameters(EVENT_NAME_USER_FAVORITES_VIEW);
    }

    public static void trackDiveSpotCheckinsView() {
        trackEventWithoutParameters(EVENT_NAME_DIVE_SPOT_CHECK_INS_VIEW);
    }

    public static void trackEditSealife() {
        trackEventWithoutParameters(EVENT_NAME_EDIT_SEALIFE);
    }

    public static void trackSealifeEdited() {
        trackEventWithoutParameters(EVENT_NAME_EDITED_SEALIFE);
    }

    public static void trackDiveSpotPhotosView() {
        trackEventWithoutParameters(EVENT_NAME_DIVE_SPOT_PHOTOS_VIEW);
    }

    public static void trackDiveSpotSealifeView() {
        trackEventWithoutParameters(EVENT_NAME_DIVE_SPOT_SEALIFE_VIEW);
    }

    public static void trackDeviSpotReviewsView() {
        trackEventWithoutParameters(EVENT_NAME_DIVE_SPOT_REVIEWS_VIEW);
    }

    public static void trackReviewerProfileView() {
        trackEventWithoutParameters(EVENT_NAME_REVIEWER_PROFILE_VIEW);
    }

    public static void trackSearchByDiveSpot() {
        trackEventWithoutParameters(EVENT_NAME_SEARCH_BY_DIVE_SPOT);
    }

    public static void trackSearchByLocation() {
        trackEventWithoutParameters(EVENT_NAME_SEARCH_BY_LOCATION);
    }

    public static void trackSearchSeaLife() {
        trackEventWithoutParameters(EVENT_NAME_SEARCH_SEA_LIFE);
    }

    public static void trackPhotoReport() {
        trackEventWithoutParameters(EVENT_NAME_DIVE_SPOT_REPORT_PHOTO);
    }

    public static void trackReviewReport() {
        trackEventWithoutParameters(EVENT_NAME_REPORT_REVIEW);
    }

    public static void trackDeleteReview() {
        trackEventWithoutParameters(EVENT_NAME_DELETE_REVIEW);
    }

    public static void trackReviewDeleted() {
        trackEventWithoutParameters(EVENT_NAME_DELETED_REVIEW);
    }

    public static void trackEditReview() {
        trackEventWithoutParameters(EVENT_NAME_EDIT_REVIEW);
    }

    public static void trackUserLikesView() {
        trackEventWithoutParameters(EVENT_NAME_USER_LIKES_VIEW);
    }

    public static void trackUserDislikesView() {
        trackEventWithoutParameters(EVENT_NAME_USER_DISLIKES_VIEW);
    }

    public static void trackUserReviewsView() {
        trackEventWithoutParameters(EVENT_NAME_USER_REVIEWS_VIEW);
    }

    public static void trackReviewerReviewsView() {
//        trackEventWithoutParameters(EVENT_NAME_REVIEWER_REVIEWS_VIEW);
    }

    public static void trackReviewerCreatedView() {
//        trackEventWithoutParameters(EVENT_NAME_REVIEWER_CREATED_VIEW);
    }

    public static void trackReviewerEditedView() {
//        trackEventWithoutParameters(EVENT_NAME_REVIEWER_EDITED_VIEW);
    }

    public static void trackReviewerCheckInsView() {
//        trackEventWithoutParameters(EVENT_NAME_REVIEWER_CHECK_INS_VIEW);
    }

    public static void trackUnknownServerError(String url, String errorText) {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }
        String userType = DDScannerApplication.getInstance().getActiveUserType();
        // Google Firebase
        Bundle params = new Bundle();
        params.putString(EVENT_PARAMETER_NAME_ERROR_TEXT, errorText);
        params.putString(EVENT_PARAMETER_NAME_USER_TYPE, userType);
        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_UNKNOWN_SERVER_ERROR, params);

        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_ERROR_URL, url);
        flurryParams.put(EVENT_PARAMETER_NAME_ERROR_TEXT, errorText);
        flurryParams.put(EVENT_PARAMETER_NAME_USER_TYPE, userType);
        FlurryAgent.logEvent(EVENT_NAME_UNKNOWN_SERVER_ERROR, flurryParams);

        // Appsflyer
        Map<String, Object> appsflyerParams = new HashMap<>();
        appsflyerParams.put(EVENT_PARAMETER_NAME_ERROR_URL, url);
        appsflyerParams.put(EVENT_PARAMETER_NAME_ERROR_TEXT, errorText);
        appsflyerParams.put(EVENT_PARAMETER_NAME_USER_TYPE, userType);
        AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), EVENT_NAME_UNKNOWN_SERVER_ERROR, appsflyerParams);

        //Facebook
        AnalyticsSystemsManager.getLogger().logEvent(EVENT_NAME_UNKNOWN_SERVER_ERROR, params);
    }

    public enum AchievementsViewSource {

        POINTS("points"), DETAILS("show_details");

        private String name;

        AchievementsViewSource(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

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
