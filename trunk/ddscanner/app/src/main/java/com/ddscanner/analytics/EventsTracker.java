package com.ddscanner.analytics;

import android.os.Bundle;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.BuildConfig;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.utils.Constants;
import com.facebook.appevents.AppEventsConstants;
import com.flurry.android.FlurryAgent;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EventsTracker {

    private static final String EVENT_NAME_CONTACT_DIVE_CENTER = "contact_dive_centers";
    private static final String EVENT_PARAMETER_NAME_CONTACT_DIVE_CENTER_METHOD = "method";

    // ----------------------------------------------------
    // Screens views
    // ----------------------------------------------------

    private static final String EVENT_NAME_ABOUT_DDS_MENU_VIEW = "about_dds_menu_view";
    private static final String EVENT_NAME_ABOUT_DDS_VIEW = "about_dds_view";
    private static final String EVENT_NAME_CONTACT_US_VIEW = "contact_us_view";
    private static final String EVENT_NAME_GUIDE_TO_DDS_VIEW = "guide_to_dds_view";
    private static final String EVENT_NAME_GUIDE_TO_DDS_ITEM_VIEW = "guide_to_dds_item_view";
    private static final String EVENT_NAME_REVIEW_VIEW = "review_view";
    private static final String EVENT_NAME_IMAGE_VIEW = "image_view";

    private static final String EVENT_NAME_MY_PROFILE_VIEW = "my_profile_view";
    private static final String EVENT_NAME_MY_CHECK_INS_VIEW = "my_check_ins_view";
    private static final String EVENT_NAME_MY_CREATED_VIEW = "my_created_view";
    private static final String EVENT_NAME_MY_EDITED_VIEW = "my_edited_view";
    private static final String EVENT_NAME_MY_FAVORITES_VIEW = "my_favorites_view";
    private static final String EVENT_NAME_MY_REVIEWS_VIEW = "my_reviews_view";
    private static final String EVENT_NAME_MY_LIKES_VIEW = "my_likes_view";
    private static final String EVENT_NAME_MY_DISLIKES_VIEW = "my_dislikes_view";
    private static final String EVENT_NAME_MY_ACHIEVEMENTS_VIEW = "my_achievements_view";
    private static final String EVENT_NAME_MY_PHOTOS_VIEW = "my_photos_view";
    private static final String EVENT_NAME_MY_INSTRUCTORS_VIEW = "my_instructors_view";
    private static final String EVENT_NAME_MY_LANGUAGES_VIEW = "my_languages_view";
    private static final String EVENT_NAME_MY_BRANDS_VIEW = "my_brands_view";
    private static final String EVENT_NAME_MY_DIVE_SPOTS_VIEW = "my_dive_spots_view";
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
    private static final String EVENT_NAME_DIVE_SPOT_SEALIFE_VIEW = "sealife_view";
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
    private static final String EVENT_NAME_DIVE_CENTER_VIEW = "dc_profile_view";
    private static final String EVENT_PARAMETER_NAME_DIVE_SPOT_ID = "dive_spot_id";
    private static final String EVENT_PARAMETER_NAME_DIVE_CENTER_ID = "dive_center_id";
    private static final String EVENT_PARAMETER_NAME_VIEW_SOURCE = "source";
    private static final String EVENT_NAME_SKIP_REGISTRATION = "skip_registration";
    private static final String EVENT_NAME_DIVE_SPOT_EDITORS_VIEW = "dive_spot_editors_view";
    private static final String EVENT_NAME_DIVE_SPOT_MAPS_VIEW = "dive_spot_maps_view";
    private static final String EVENT_PARAMETER_NAME_DIVE_CENTER_TYPE = "type";
    private static final String EVENT_NAME_DIVE_SPOT_LOCATION_ON_MAP_VIEW = "dive_spot_location_on_map_view";
    private static final String EVENT_NAME_INSTRUCTORS_VIEW = "dc_instructors_view";
    private static final String EVENT_NAME_DIVESPOTS_TO_APPROVE_VIEW = "dive_spots_to_be_approved_view";
    private static final String EVENT_NAME_DC_LANGUAGES_VIEW = "dc_languages_view";
    private static final String EVENT_NAME_DC_BRANDS_VIEW = "dc_brands_view";
    private static final String EVENT_NAME_DC_CREATED_VIEW = "dc_created_view";
    private static final String EVENT_NAME_DC_EDITED_VIEW = "dc_edited_view";
    private static final String EVENT_NAME_DC_SPOTS_VIEW = "dc_dive_spots_view";
    private static final String EVENT_NAME_PHONE_CLICK = "phone_click";
    private static final String EVENT_NAME_EMAIL_LONG_CLICK = "email_long_click";

    public static void trackPhoneClick() {
        trackEventWithoutParameters(EVENT_NAME_PHONE_CLICK);
    }

    public static void trackEmailLongClick() {
        trackEventWithoutParameters(EVENT_NAME_EMAIL_LONG_CLICK);
    }

    public static void trackDcLanguagesView() {
        trackEventWithoutParameters(EVENT_NAME_DC_LANGUAGES_VIEW);
    }

    public static void trackDcBrandsView() {
        trackEventWithoutParameters(EVENT_NAME_DC_BRANDS_VIEW);
    }

    public static void trackDcCreatedView() {
        trackEventWithoutParameters(EVENT_NAME_DC_CREATED_VIEW);
    }

    public static void trackDcEditedView() {
        trackEventWithoutParameters(EVENT_NAME_DC_EDITED_VIEW);
    }

    public static void trackDcSpotsView() {
        trackEventWithoutParameters(EVENT_NAME_DC_SPOTS_VIEW);
    }

    // ----------------------------------------------------
    // Content management
    // ----------------------------------------------------

    private static final String EVENT_NAME_DIVE_SPOT_VALID = "dive_spot_valid";
    private static final String EVENT_NAME_DIVE_SPOT_INVALID = "dive_spot_invalid";
//    private static final String EVENT_PARAMETER_NAME_DIVE_SPOT_VALIDATION_RESULT = "result";

    private static final String EVENT_NAME_EDIT_DIVE_SPOT = "dive_spot_edit_click";
    private static final String EVET_NAME_DIVE_SPOT_MAP_ADDED = "dive_spot_map_added";
    private static final String EVENT_NAME_CREATE_DIVE_SPOT = "dive_spot_create_click";
    private static final String EVENT_NAME_DIVE_SPOT_REPORT_PHOTO = "image_report_click";
    private static final String EVENT_NAME_DIVE_SPOT_EDITED = "dive_spot_edited";
    private static final String EVENT_NAME_DIVE_SPOT_CREATED = "dive_spot_created";
    private static final String EVENT_NAME_SEALIFE_CREATE = "sea_life_create_click";
    private static final String EVENT_NAME_SEALIFE_CREATED = "sea_life_created";
    private static final String EVENT_NAME_REPORT_REVIEW = "review_report_click";
    private static final String EVENT_NAME_DELETE_REVIEW = "user_review_delete";
    private static final String EVENT_NAME_DELETED_REVIEW = "user_review_deleted";
    private static final String EVENT_NAME_EDIT_REVIEW = "user_review_edit_click";
    private static final String EVENT_NAME_EDITED_REVIEW = "user_review_edited";
    private static final String EVENT_NAME_REVIEWER_FACEBOOK_OPENED = "reviewer_facebook_open";
    private static final String EVENT_NAME_DIVE_SPOT__PHOTO_REPORT_SENT = "image_reported";
    private static final String EVENT_NAME_REVIEWER_REVIEW_REPORT_SENT = "review_reported";
    private static final String EVENT_NAME_REVIEW_SHOW_ALL = "review_show_all";
    private static final String EVENT_NAME_REVIEW_SENT = "review_sent";
    private static final String EVENT_NAME_APPLY_DS_FILTER = "apply_ds_filter";
    private static final String EVENT_NAME_NEW_ACCOUNT_ADDED = "new_account_added";
    private static final String EVENT_NAME_PROFILE_EDITED = "profile_edited";

    // ----------------------------------------------------
    // UserOld activity
    // ----------------------------------------------------

    private static final String EVENT_NAME_CHECK_IN = "check_in";
    private static final String EVENT_PARAMETER_NAME_CHECK_IN_STATUS = "status";
    private static final String EVENT_NAME_CHECK_OUT = "check_out";

    private static final String EVENT_NAME_SEND_REVIEW = "review_create_screen_view";
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
    private static final String EVENT_PARAMETER_NAME_RESPONSE_CODE = "response_code";
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
    private static final String EVENT_PARAMETER_NAME_TUTORIAL_STATE = "tutorial_state";
    private static final String EVENT_PARAMETER_NAME_PLATFORM = "platform";
    private static final String EVENT_PARAMETER_NAME_TUTORIAL_VERSION = "tutorial_version";
    private static final String EVENT_NAME_DC_SEARCH_NEW_CHOSEN = "dc_search_new_chosen";

    // ----------------------------------------------------
    // Booking
    // ----------------------------------------------------
    private static final String EVENT_NAME_BOOKING_DC_LIST_VIEW  = "booking_dcs_list_view";
    private static final String EVENT_NAME_BOOKING_REQUEST_VIEW = "booking_request_view";
    private static final String EVENT_NAME_BOOKING_DC_PROFILE_VIEW = "booking_dc_profile_view";
    private static final String EVENT_NAME_BOOKING_DC_PROFILE_PHONE_CLICK = "booking_dc_profile_phone_click";
    private static final String EVENT_NAME_BOOKING_DC_PROFILE_EMAIL_CLICK = "booking_dc_profile_email_click";
    private static final String EVENT_NAME_BOOKING_DC_PROFILE_EMAIL_LONG_CLICK = "booking_dc_profile_email_long_click";
    private static final String EVENT_NAME_BOOKING_REQUEST_SENT = "booking_request_sent";
    private static final String EVENT_NAME_BOOKING_CANCELLED = "booking_cancelled";
    // ----------------------------------------------------
    // ----------------------------------------------------
    // ----------------------------------------------------

    public static void trackFirstLaunchForFacebookAnalytics() {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, "0");
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "0");
        params.putInt(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, 0);
        params.putInt(AppEventsConstants.EVENT_PARAM_PAYMENT_INFO_AVAILABLE, 0);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "0");
        AnalyticsSystemsManager.getLogger().logEvent(AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT, 0, params);
    }

    private static void trackEventWithParameters(Map<String, String> params, String eventName) {

        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }
        try {
            String userType = DDScannerApplication.getInstance().getActiveUserType();

            String tutorialState = DDScannerApplication.getInstance().getTutorialState();

//        params.put(EVENT_PARAMETER_NAME_TUTORIAL_STATE, tutorialState);
            params.put(EVENT_PARAMETER_NAME_USER_TYPE, userType);
            params.put(EVENT_PARAMETER_NAME_PLATFORM, Constants.PLATFORM);
            params.put(EVENT_PARAMETER_NAME_TUTORIAL_VERSION, Constants.TUTORIAL_VERSION);

            Bundle firebaseParams = new Bundle();
            Map<String, Object> appsflyerParams = new HashMap<>();

            for (Map.Entry entry : params.entrySet()) {
                firebaseParams.putString(entry.getKey().toString(), entry.getValue().toString());
                appsflyerParams.put(entry.getKey().toString(), entry.getValue());
            }

            AnalyticsSystemsManager.getLogger().logEvent(eventName, firebaseParams);
            FlurryAgent.logEvent(eventName, params);
            AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), eventName, appsflyerParams);
            FirebaseAnalytics firebaseAnalytics = AnalyticsSystemsManager.getFirebaseAnalytics();
            firebaseAnalytics.setUserProperty(EVENT_PARAMETER_NAME_USER_TYPE, userType);
            firebaseAnalytics.setUserProperty(EVENT_PARAMETER_NAME_PLATFORM, Constants.PLATFORM);
            firebaseAnalytics.setUserProperty(EVENT_PARAMETER_NAME_TUTORIAL_VERSION, Constants.TUTORIAL_VERSION);
            firebaseAnalytics.logEvent(eventName, firebaseParams);
        } catch (Exception ignored) {

        }
    }

    private static void trackEventWithoutParameters(String eventName) {
        if (!BuildConfig.COLLECT_ANALYTICS_DATA) {
            return;
        }
        try {
            String tutorialState = DDScannerApplication.getInstance().getTutorialState();
            String userType = DDScannerApplication.getInstance().getActiveUserType();
            // Google Firebase
            Bundle params = new Bundle();

            params.putString(EVENT_PARAMETER_NAME_USER_TYPE, userType);
//        params.putString(EVENT_PARAMETER_NAME_TUTORIAL_STATE, tutorialState);
            params.putString(EVENT_PARAMETER_NAME_PLATFORM, Constants.PLATFORM);
            params.putString(EVENT_PARAMETER_NAME_TUTORIAL_VERSION, Constants.TUTORIAL_VERSION);
            AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(eventName, params);

            // Flurry
            Map<String, String> flurryParams = new HashMap<>();
            flurryParams.put(EVENT_PARAMETER_NAME_USER_TYPE, userType);
//        flurryParams.put(EVENT_PARAMETER_NAME_TUTORIAL_STATE, tutorialState);
            flurryParams.put(EVENT_PARAMETER_NAME_PLATFORM, Constants.PLATFORM);
            flurryParams.put(EVENT_PARAMETER_NAME_TUTORIAL_VERSION, Constants.TUTORIAL_VERSION);
            FlurryAgent.logEvent(eventName, flurryParams);

            // Appsflyer
            Map<String, Object> appsflyerParams = new HashMap<>();
            appsflyerParams.put(EVENT_PARAMETER_NAME_USER_TYPE, userType);
//        appsflyerParams.put(EVENT_PARAMETER_NAME_TUTORIAL_STATE, tutorialState);
            appsflyerParams.put(EVENT_PARAMETER_NAME_PLATFORM, Constants.PLATFORM);
            appsflyerParams.put(EVENT_PARAMETER_NAME_TUTORIAL_VERSION, Constants.TUTORIAL_VERSION);
            AppsFlyerLib.getInstance().trackEvent(DDScannerApplication.getInstance(), eventName, appsflyerParams);

            //Facebook
            AnalyticsSystemsManager.getLogger().logEvent(eventName, params);
        } catch (Exception ignored) {

        }
    }

    public static void trackMyProfileView() {
        trackEventWithoutParameters(EVENT_NAME_MY_PROFILE_VIEW);
    }

    public static void trackMyCheckinsView() {
        trackEventWithoutParameters(EVENT_NAME_MY_CHECK_INS_VIEW);
    }

    public static void trackMyFavoritesView() {
        trackEventWithoutParameters(EVENT_NAME_MY_FAVORITES_VIEW);
    }

    public static void trackMyEditedView() {
        trackEventWithoutParameters(EVENT_NAME_MY_EDITED_VIEW);
    }

    public static void trackMyCreatedView() {
        trackEventWithoutParameters(EVENT_NAME_MY_CREATED_VIEW);
    }

    public static void trackMyPhotosView() {
        trackEventWithoutParameters(EVENT_NAME_MY_PHOTOS_VIEW);
    }

    public static void trackBookingEmailLongClck() {
        trackEventWithoutParameters(EVENT_NAME_BOOKING_DC_PROFILE_EMAIL_LONG_CLICK);
    }

    public static void trackMyLikesView() {
        trackEventWithoutParameters(EVENT_NAME_MY_LIKES_VIEW);
    }

    public static void trackMyDislikesView() {
        trackEventWithoutParameters(EVENT_NAME_MY_DISLIKES_VIEW);
    }

    public static void trackMyAchievemtsView() {
        trackEventWithoutParameters(EVENT_NAME_MY_ACHIEVEMENTS_VIEW);
    }

    public static void trackMyReviewsView() {
        trackEventWithoutParameters(EVENT_NAME_MY_REVIEWS_VIEW);
    }

    public static void trackMyBrandsView() {
        trackEventWithoutParameters(EVENT_NAME_MY_BRANDS_VIEW);
    }

    public static void trackMyDiveSpotsView() {
        trackEventWithoutParameters(EVENT_NAME_MY_DIVE_SPOTS_VIEW);
    }

    public static void trackMyInstructorsView() {
        trackEventWithoutParameters(EVENT_NAME_MY_INSTRUCTORS_VIEW);
    }

    public static void trackMyLanguagesView() {
        trackEventWithoutParameters(EVENT_NAME_MY_LANGUAGES_VIEW);
    }

    public static void trackBookingDcListView() {
        trackEventWithoutParameters(EVENT_NAME_BOOKING_DC_LIST_VIEW);
    }

    public static void trackBookingRequestView() {
        trackEventWithoutParameters(EVENT_NAME_BOOKING_REQUEST_VIEW);
    }

    public static void trackBookingDcProfileView() {
        trackEventWithoutParameters(EVENT_NAME_BOOKING_DC_PROFILE_VIEW);
    }

    public static void trackBookingDcProfilePhoneClick() {
        trackEventWithoutParameters(EVENT_NAME_BOOKING_DC_PROFILE_PHONE_CLICK);
    }

    public static void trackBookingDcProfileEmailClick() {
        trackEventWithoutParameters(EVENT_NAME_BOOKING_DC_PROFILE_EMAIL_CLICK);
    }

    public static void trackBookingRequestSent() {
        trackEventWithoutParameters(EVENT_NAME_BOOKING_REQUEST_SENT);
    }

    public static void trackBookingCancelled() {
        trackEventWithoutParameters(EVENT_NAME_BOOKING_CANCELLED);
    }

    public static void tracNewDcChosenEvent() {
        trackEventWithoutParameters(EVENT_NAME_DC_SEARCH_NEW_CHOSEN);
    }

    public static void diveSpotsToApproveView() {
        trackEventWithoutParameters(EVENT_NAME_DIVESPOTS_TO_APPROVE_VIEW);
    }

    public static void trackInstructorsView() {
        trackEventWithoutParameters(EVENT_NAME_INSTRUCTORS_VIEW);
    }

    public static void trackProfileEdited() {
        trackEventWithoutParameters(EVENT_NAME_PROFILE_EDITED);
    }

    public static void trackNewAccountAdded() {
        trackEventWithoutParameters(EVENT_NAME_NEW_ACCOUNT_ADDED);
    }

    public static void trackApplyDsFilter() {
        trackEventWithoutParameters(EVENT_NAME_APPLY_DS_FILTER);
    }

    public static void trackAboutDDsMenuView() {
        trackEventWithoutParameters(EVENT_NAME_ABOUT_DDS_MENU_VIEW);
    }

    public static void trackAboutDDSView() {
        trackEventWithoutParameters(EVENT_NAME_ABOUT_DDS_VIEW);
    }

    public static void trackContactUsView() {
        trackEventWithoutParameters(EVENT_NAME_CONTACT_US_VIEW);
    }

    public static void trackGuideToDDSView() {
        trackEventWithoutParameters(EVENT_NAME_GUIDE_TO_DDS_VIEW);
    }

    public static void trackGuideToDDSItemView(String question) {
        Map<String, String> map = new HashMap<>();
        map.put(EVENT_PARAMETER_NAME_QUESTION, question);
        trackEventWithParameters(map, EVENT_NAME_GUIDE_TO_DDS_ITEM_VIEW);
    }

    public static void trackReviewView() {
        trackEventWithoutParameters(EVENT_NAME_REVIEW_VIEW);
    }

    public static void trackImageView(PhotoOpenedSource source) {
        Map<String, String> map = new HashMap<>();
        map.put(EVENT_PARAMETER_NAME_SOURCE, source.name().toLowerCase());
        trackEventWithParameters(map, EVENT_NAME_IMAGE_VIEW);
    }

    public static void trackMapAdded() {
        trackEventWithoutParameters(EVET_NAME_DIVE_SPOT_MAP_ADDED);
    }

    public static void trackUserAchievementsView(AchievementsViewSource source) {
        Map<String, String> map = new HashMap<>();
        map.put(EVENT_PARAMETER_NAME_VIEW_SOURCE, source.getName());
        trackEventWithParameters(map, EVENT_NAME_USER_ACHIEVEMENTS_VIEW);
    }

    public static void trackSkipTutorial(String currentPosition) {
        Map<String, String> map = new HashMap<>();
        map.put(EVENT_PARAMETER_NAME_TUTORIAL_PAGE, currentPosition);
        trackEventWithParameters(map, EVENT_NAME_SKIP_TUTORIAL);
    }

    public static void trackDiveSpotView(String diveSpotId, SpotViewSource source) {
        Map<String, String> map = new HashMap<>();
        map.put(EVENT_PARAMETER_NAME_DIVE_SPOT_ID, diveSpotId);
        map.put(EVENT_PARAMETER_NAME_VIEW_SOURCE, source.getName());
        trackEventWithParameters(map, EVENT_NAME_DIVE_SPOT_VIEW);
    }

    public static void trackDiveCenterView(String diveCenterId, String dcType) {
        Map<String, String> map = new HashMap<>();
        map.put(EVENT_PARAMETER_NAME_DIVE_CENTER_ID, diveCenterId);
        map.put(EVENT_PARAMETER_NAME_DIVE_CENTER_TYPE, dcType);
        trackEventWithParameters(map, EVENT_NAME_DIVE_CENTER_VIEW);
    }

    public static void trackGuideUseful(String question) {
        Map<String, String> map = new HashMap<>();
        map.put(EVENT_PARAMETER_NAME_QUESTION, question);
        trackEventWithParameters(map, EVENT_NAME_GUIDE_USEFUL);
    }

    public static void trackGuideNotUseful(String question) {
        Map<String, String> map = new HashMap<>();
        map.put(EVENT_PARAMETER_NAME_QUESTION, question);
        trackEventWithParameters(map, EVENT_NAME_GUIDE_NOT_USEFUL);
    }

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
//        trackEventWithoutParameters(EVENT_NAME_REVIEWER_FACEBOOK_OPENED);
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
//        trackEventWithoutParameters(EVENT_NAME_REVIEW_SHOW_ALL);
    }


    public static void trackCheckIn() {
       trackEventWithoutParameters(EVENT_NAME_CHECK_IN);
    }

    public static void trackCheckOut() {
        trackEventWithoutParameters(EVENT_NAME_CHECK_OUT);
    }

    public static void trackReviewSent(SendReviewSource source) {
        Map<String, String> map = new HashMap<>();
        map.put(EVENT_PARAMETER_NAME_VIEW_SOURCE, source.getName());
        trackEventWithParameters(map, EVENT_NAME_REVIEW_SENT);
    }

    public static void trackSendReview(SendReviewSource source) {
        Map<String, String> map = new HashMap<>();
        map.put(EVENT_PARAMETER_NAME_VIEW_SOURCE, source.getName());
        trackEventWithParameters(map, EVENT_NAME_SEND_REVIEW);
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
//        trackEventWithoutParameters(EVENT_NAME_DIVE_CENTERS_MAP_VIEW);
    }

    public static void trackDiveCentersListView() {
//        trackEventWithoutParameters(EVENT_NAME_DIVE_CENTERS_LIST_VIEW);
    }

    public static void trackNotificationsView() {
        trackEventWithoutParameters(EVENT_NAME_NOTIFICATIONS_VIEW);
    }

    public static void trackActivityView() {
        trackEventWithoutParameters(EVENT_NAME_ACTIVITY_VIEW);
    }

    public static void trackUserProfileView(String type) {
        Map<String, String> map = new HashMap<>();
        map.put(EVENT_PARAMETER_NAME_DIVE_CENTER_TYPE, type);
        trackEventWithParameters(map, EVENT_NAME_USER_PROFILE_VIEW);
    }

    public static void trackUserCheckinsView(String type) {
        Map<String, String> map = new HashMap<>();
        map.put(EVENT_PARAMETER_NAME_DIVE_CENTER_TYPE, type);
        trackEventWithParameters(map, EVENT_NAME_USER_CHECK_INS_VIEW);
    }

    public static void trackUserEditedView(String type) {
        Map<String, String> map = new HashMap<>();
        map.put(EVENT_PARAMETER_NAME_DIVE_CENTER_TYPE, type);
        trackEventWithParameters(map, EVENT_NAME_USER_EDITED_VIEW);
    }

    public static void trackUserCreatedView(String type) {
        Map<String, String> map = new HashMap<>();
        map.put(EVENT_PARAMETER_NAME_DIVE_CENTER_TYPE, type);
        trackEventWithParameters(map, EVENT_NAME_USER_CREATED_VIEW);
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

    public static void trackDiveSpotSealifeView(SealifeViewSource source) {
        Map<String, String> map = new HashMap<>();
        map.put(EVENT_PARAMETER_NAME_VIEW_SOURCE, source.getSource());
        trackEventWithParameters(map, EVENT_NAME_DIVE_SPOT_SEALIFE_VIEW);
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
//        trackEventWithoutParameters(EVENT_NAME_SEARCH_SEA_LIFE);
    }

    public static void trackPhotoReport() {
        trackEventWithoutParameters(EVENT_NAME_DIVE_SPOT_REPORT_PHOTO);
    }

    public static void trackReviewReport() {
        trackEventWithoutParameters(EVENT_NAME_REPORT_REVIEW);
    }

    public static void trackDeleteReview() {
//        trackEventWithoutParameters(EVENT_NAME_DELETE_REVIEW);
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

    public static void trackUserReviewsView(String type) {
        Map<String, String> map = new HashMap<>();
        map.put(EVENT_PARAMETER_NAME_DIVE_CENTER_TYPE, type);
        trackEventWithParameters(map, EVENT_NAME_USER_REVIEWS_VIEW);
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

    public static void trackUnknownServerError(String url, int errorCode, String errorMessage) {
        Map<String, String> map = new HashMap<>();
        map.put(EVENT_PARAMETER_NAME_RESPONSE_CODE, String.valueOf(errorCode));
        map.put(EVENT_PARAMETER_NAME_ERROR_TEXT, errorMessage);
        map.put(EVENT_PARAMETER_NAME_ERROR_URL, url);
        trackEventWithParameters(map, EVENT_NAME_UNKNOWN_SERVER_ERROR);
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
        FROM_RATING_BAR("stars"), FROM_REVIEWS_LIST("reviews_list"), CHECK_IN("check_in"), WRITE_REVIEW_BUTTON("write_review_button");

        private String name;

        SendReviewSource(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    public enum ContactDiveCenterMethod {
        PHONE_CALL("phone_call"), EMAIL("email"), UNKNOWN("unknown");

        private String name;

        ContactDiveCenterMethod(String name) {
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

    public enum SealifeViewSource {
        DIVE_SPOT("dive_spot"), REVIEW("review");

        String source;

        SealifeViewSource(String source) {
            this.source = source;
        }

        public String getSource() {
            return source;
        }
    }

    public enum DiveCenterSearchSource {

        REGISTRATION("registration"), PROFILE("profile");

        String source;

        DiveCenterSearchSource(String source) {
            this.source = source;
        }

        public String getSource() {
            return this.source;
        }

    }

}
