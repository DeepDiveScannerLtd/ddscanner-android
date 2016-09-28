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

    // SplashActivity
    public static final int DRC_SPLASH_ACTIVITY_FAILED_TO_CONNECT = 1701;
    public static final int DRC_SPLASH_ACTIVITY_UNEXPECTED_ERROR = 1702;

    // LoginActivity
    public static final int DRC_LOGIN_ACTIVITY_FAILED_TO_CONNECT = 1801;
    public static final int DRC_LOGIN_ACTIVITY_UNEXPECTED_ERROR = 1802;
    public static final int DRC_LOGIN_ACTIVITY_USER_NOT_FOUND = 1803;
    public static final int DRC_LOGIN_ACTIVITY_GOOGLE_SIGN_IN_FAIL = 1804;
}
