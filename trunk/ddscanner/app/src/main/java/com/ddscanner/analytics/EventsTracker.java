package com.ddscanner.analytics;

import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

public class EventsTracker {

    private static final String EVENT_NAME_CONTACT_DIVE_CENTER = "contact_dive_center";
    private static final String EVENT_PARAMETER_CONTACT_DIVE_CENTER_TYPE_PHONE_CALL = "phone_call";
    private static final String EVENT_PARAMETER_CONTACT_DIVE_CENTER_TYPE_EMAIL = "email";

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
//        Bundle params = new Bundle();
//        params.putString(FirebaseAnalytics.Param.ITEM_NAME, "dive_spot");
//        params.putString(FirebaseAnalytics.Param.ITEM_ID, diveSpotId);
//        params.putString(FirebaseAnalytics.Param.ORIGIN, spotViewSource.getName());
//        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);

        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_DIVE_SPOT_ID, diveSpotId);
        flurryParams.put(EVENT_PARAMETER_NAME_VIEW_SOURCE, spotViewSource.getName());
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_VIEW, flurryParams);
    }

    public static void trackDiveCenterView(String diveCenterId, SpotViewSource spotViewSource) {
        // Google Firebase
//        Bundle params = new Bundle();
//        params.putLong(EVENT_PARAMETER_NAME_DIVE_CENTER_ID, diveCenterId);
//        params.putString(EVENT_PARAMETER_NAME_VIEW_SOURCE, spotViewSource.getName());
//        AnalyticsSystemsManager.getFirebaseAnalytics().logEvent(EVENT_NAME_DIVE_CENTER_VIEW, params);

        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_DIVE_CENTER_ID, diveCenterId);
        flurryParams.put(EVENT_PARAMETER_NAME_VIEW_SOURCE, spotViewSource.getName());
        FlurryAgent.logEvent(EVENT_NAME_DIVE_CENTER_VIEW, flurryParams);
    }

    public static void trackDiveSpotValid() {
        // Flurry
//        Map<String, String> flurryParams = new HashMap<>();
//        flurryParams.put(EVENT_PARAMETER_NAME_DIVE_SPOT_VALIDATION_RESULT, result.getName());
//        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_VALID, flurryParams);
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_VALID);
    }

    public static void trackDiveSpotInvalid() {
        // Flurry
//        Map<String, String> flurryParams = new HashMap<>();
//        flurryParams.put(EVENT_PARAMETER_NAME_DIVE_SPOT_VALIDATION_RESULT, result.getName());
//        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_INVALID, flurryParams);
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_INVALID);
    }

    public static void trackDiveSpotEdit() {
        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_EDIT_DIVE_SPOT);
    }

    public static void trackDiveSpotCreation() {
        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_CREATE_DIVE_SPOT);
    }

    public static void trackCheckIn(CheckInStatus status) {
        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_CHECK_IN_STATUS, status.getName());
        FlurryAgent.logEvent(EVENT_NAME_CHECK_IN, flurryParams);
    }

    public static void trackCheckOut() {
        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_CHECK_OUT);
    }

    public static void trackReviewSending(SendReviewSource sendReviewSource) {
        // Flurry
        Map<String, String> flurryParams = new HashMap<>();
        flurryParams.put(EVENT_PARAMETER_NAME_SEND_REVIEW_SOURCE, sendReviewSource.getName());
        FlurryAgent.logEvent(EVENT_NAME_SEND_REVIEW, flurryParams);
    }

    public static void trackCommentLiked() {
        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_COMMENT_LIKED);
    }

    public static void trackCommentDisliked() {
        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_COMMENT_DISLIKED);
    }

    public static void trackDiveSpotPhotoAdded() {
        // Flurry
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_PHOTO_ADDED);
    }

    public static void trackContactDiveCenter() {
        FlurryAgent.logEvent(EVENT_NAME_CONTACT_DIVE_CENTER);
    }

    public static void trackContactDiveCenterByPhone() {
        FlurryAgent.logEvent(EVENT_PARAMETER_CONTACT_DIVE_CENTER_TYPE_PHONE_CALL);
    }

    public static void trackContactDiveCenterByEmail() {
        FlurryAgent.logEvent(EVENT_PARAMETER_CONTACT_DIVE_CENTER_TYPE_EMAIL);
    }

    public static void trackDiveSpotMapView() {
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOTS_MAP_VIEW);
    }

    public static void trackDiveSpotListView() {
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOTS_LIST_VIEW);
    }

    public static void trackDiveCentersMapView() {
        FlurryAgent.logEvent(EVENT_NAME_DIVE_CENTERS_MAP_VIEW);
    }

    public static void trackDiveCentersListView() {
        FlurryAgent.logEvent(EVENT_NAME_DIVE_CENTERS_LIST_VIEW);
    }

    public static void trackNotificationsView() {
        FlurryAgent.logEvent(EVENT_NAME_NOTIFICATIONS_VIEW);
    }

    public static void trackActivityView() {
        FlurryAgent.logEvent(EVENT_NAME_ACTIVITY_VIEW);
    }

    public static void trackUserProfileView() {
        FlurryAgent.logEvent(EVENT_NAME_USER_PROFILE_VIEW);
    }

    public static void trackUserCheckinsView() {
        FlurryAgent.logEvent(EVENT_NAME_USER_CHECK_INS_VIEW);
    }

    public static void trackUserEditedView() {
        FlurryAgent.logEvent(EVENT_NAME_USER_EDITED_VIEW);
    }

    public static void trackUserCreatedView() {
        FlurryAgent.logEvent(EVENT_NAME_USER_CREATED_VIEW);
    }

    public static void trackUserFavoritesView() {
        FlurryAgent.logEvent(EVENT_NAME_USER_FAVORITES_VIEW);
    }

    public static void trackDiveSpotCheckinsView() {
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_CHECK_INS_VIEW);
    }

    public static void trackDiveSpotPhotosView() {
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_PHOTOS_VIEW);
    }

    public static void trackDiveSpotSealifeView() {
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_SEALIFE_VIEW);
    }

    public static void trackDeviSpotReviewsView() {
        FlurryAgent.logEvent(EVENT_NAME_DIVE_SPOT_REVIEWS_VIEW);
    }

    public static void trackReviewerProfileView() {
        FlurryAgent.logEvent(EVENT_NAME_REVIEWER_PROFILE_VIEW);
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

}
