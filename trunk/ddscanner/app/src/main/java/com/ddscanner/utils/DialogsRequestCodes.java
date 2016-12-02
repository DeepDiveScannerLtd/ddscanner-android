package com.ddscanner.utils;


public class DialogsRequestCodes {
    /**
     * Store here all the dialogs request and result codes.
     * <p/>
     * Request code naming rules: REQUEST_CODE_<activity name>_<request name>
     * Result code naming rules: RESULT_CODE_<activity name>_<request name>
     * Request int value convention: XXYY, where XX - unique "id" of activity/screen, YY - unique "id" of request code
     */

    // DiveSpotDetailsActivity
    public static final int DRC_DIVE_SPOT_DETAILS_ACTIVITY_DIVE_SPOT_NOT_FOUND = 1001;
    public static final int DRC_DIVE_SPOT_DETAILS_ACTIVITY_FAILED_TO_CONNECT = 1002;

    // DiveSpotPhotosActivity
    public static final int DRC_DIVE_SPOT_PHOTOS_ACTIVITY_DIVE_SPOT_NOT_FOUND = 1101;
    public static final int DRC_DIVE_SPOT_PHOTOS_ACTIVITY_CONNECTION_FAILURE = 1102;

    // ReviewsActivity
    public static final int DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE = 1201;
    public static final int DRC_REVIEWS_ACTIVITY_FAILED_TO_CONNECT = 1202;
    public static final int DRC_REVIEWS_ACTIVITY_DIVE_SPOT_NOT_FOUND = 1203;
    public static final int DRC_REVIEWS_ACTIVITY_DELETED_COMMENT_NOT_FOUND = 1204;
    public static final int DRC_REVIEWS_ACTIVITY_REPORTED_COMMENT_NOT_FOUND = 1205;
    public static final int DRC_REVIEWS_ACTIVITY_RIGHTS_NEED = 1206;
    public static final int DRC_REVIEWS_ACTIVITY_REPORT_SELF_REVIEW = 1207;

    // DiveSpotsListActivity
    public static final int DRC_DIVE_SPOTS_LIST_ACTIVITY_FAILED_TO_CONNECT = 1301;

    // UserLikesDislikesActivity
    public static final int DRC_USER_LIKES_DISLIKES_ACTIVITY_FAILED_TO_CONNECT = 1401;
    
// ForeignUserDiveSpotListActivity
    public static final int DRC_FOREIGN_USER_DIVE_SPOTS_ACTIVITY_FAILED_TO_CONNECT = 1501;

    // ImageSliderActivity
    public static final int DRC_IMAGE_SLIDER_ACTIVITY_FAILED_TO_CONNECT = 1601;
    public static final int DRC_IMAGE_SLIDER_ACTIVITY_CONNECTION_FAILURE_GET_REPORT_TYPES = 1602;
    public static final int DRC_IMAGE_SLIDER_ACTIVITY_BAD_REQUEST_IN_REPORT = 1603;
    
    // SplashActivity
    public static final int DRC_SPLASH_ACTIVITY_FAILED_TO_CONNECT = 1701;
    public static final int DRC_SPLASH_ACTIVITY_UNEXPECTED_ERROR = 1702;

    // LoginActivity
    public static final int DRC_LOGIN_ACTIVITY_FAILED_TO_CONNECT = 1801;
    public static final int DRC_LOGIN_ACTIVITY_UNEXPECTED_ERROR = 1802;
    public static final int DRC_LOGIN_ACTIVITY_USER_NOT_FOUND = 1803;
    public static final int DRC_LOGIN_ACTIVITY_GOOGLE_SIGN_IN_FAIL = 1804;
    
    // SearchActivity
    public static final int DRC_SEARCH_ACTIVITY_FAILED_TO_CONNECT = 1901;
    public static final int DRC_SEARCH_ACTIVITY_UNEXPECTED_ERROR = 1902;

    //ForeignProfileActivity
    public static final int DRC_FOREIGN_PROFILE_ACTIVITY_FAILED_TO_CONNECT = 2001;
    public static final int DRC_FOREIGN_PROFILE_ACTIVITY_UNKNOWN_ERROR = 2002;

    //SelfCommentsActivity
    public static final int DRC_SELF_COMMENTS_ACTIVITY_FAILED_TO_CONNECT = 2101;
    public static final int DRC_SELF_COMMENTS_ACTIVITY_UNKNOWN_ERROR = 2102;
       public static final int DRC_SELF_COMMENTS_ACTIVITY_COMMENT_NOT_FOUND = 2103;
// AddDiveSpotActivity
    public static final int DRC_ADD_DIVE_SPOT_ACTIVITY_CONNECTION_ERROR = 2301;
    public static final int DRC_ADD_DIVE_SPOT_ACTIVITY_UNEXPECTED_ERROR = 2302;

    // LeaveReviewActivity
    public static final int DRC_LEAVE_REVIEW_ACTIVITY_FAILED_TO_CONNECT = 2401;
    public static final int DRC_LEAVE_REVIEW_ACTIVITY_UNKNOWN_ERROR = 2402;

    // EditCommentActivity
    public static final int DRC_EDIT_COMMENT_ACTIVITY_COMMENT_NOT_FOUND = 2601;
    
    // EditDiveSpotActivity
    public static final int DRC_EDIT_DIVE_SPOT_ACTIVITY_FAILED_TO_CONNECT = 2701;
    public static final int DRC_EDIT_DIVE_SPOT_ACTIVITY_DIVE_SPOT_NOT_FOUND = 2702;
    public static final int DRC_EDIT_DIVE_SPOT_ACTIVITY_UNEXPECTED_ERROR = 2703;
    public static final int DRC_EDIT_DIVE_SPOT_ACTIVITY_NO_RIGHTS_ERROR = 2704;

    // AddPhotosActivity
    public static final int DRC_ADD_PHOTOS_ACTIVITY_DIVE_SPOT_NOT_FOUND = 2801;

    // FilterActivity
    public static final int DRC_FILTER_ACTIVITY_FAILED_TO_CONNECT = 3001;
    public static final int DRC_FILTER_ACTIVITY_UNEXPECTED_ERROR = 3002;

    //SearchSealifeActivity
    public static final int DRC_SEARCH_SEALIFE_ACTIVITY_FAILED_TO_CONNECT = 3101;
    public static final int DRC_SEARCH_SEALIFE_ACTIVITY_UNEXPECTED_ERROR = 3102;

    //NotificationsFragment
    public static final int DRC_NOTIFICATIONS_FRAGMENT_FAILED_TO_CONNECT = 3301;
    public static final int DRC_NOTIFICATIONS_FRAGMENT_UNEXPECTED_ERROR = 3302;

    //ProfileFragment
    public static final int DRC_PROFILE_FRAGMENT_FAILED_TO_CONNECT = 3401;
    public static final int DRC_PROFILE_FRAGMENT_UNEXPECTED_ERROR = 3402;

    //DiveCentersClusterManager
    public static final int DRC_DIVE_CENTERS_CLUSTER_MANAGER_FAILED_TO_CONNECT = 3501;
    public static final int DRC_DIVE_CENTERS_CLUSTER_MANAGER_UNEXPECTED_ERROR = 3502;

    //ReviewImagesSlider
    public static final int DRC_REVIEWS_SLIDER_ACTIVITY_FAILED_TO_CONNECT = 3601;
    public static final int DRC_REVIEWS_SLIDER_ACTIVITY_UNEXPECTED_ERROR = 3602;

    //AchievementsActivity
    public static final int DRC_ACHIEVEMENTS_ACTIVITY_FAILED_TO_CONNECT = 3701;
    public static final int DRC_ACHIEVEMENTS_ACTIVITY_UNEXPECTED_ERROR = 3702;

    //SealifeDetailsActivity
    public static final int DRC_SEALIFE_ACTIVITY_FAILEED_TO_CONNECT = 3801;

    //UserProfileActivity
    public static final int DRC_USER_PROFILE_ACTIVITY_FAILED_TO_CONNECT = 3901;

    //DiveSpotMapsActivity
    public static final int DRC_MAPS_ACTIVITY_FAILED = 4001;

}
