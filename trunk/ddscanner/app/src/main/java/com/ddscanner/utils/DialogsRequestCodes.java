package com.ddscanner.utils;


public class DialogsRequestCodes {
    /**
     * Store here all the dialogs request and result codes.
     * <p/>
     * Request code naming rules: REQUEST_CODE_<activity name>_<request name>
     * Result code naming rules: RESULT_CODE_<activity name>_<request name>
     * Request int value convention: XXYY, where XX - unique "id" of activity/screen, YY - unique "id" of request code
     */

//    DiveSpotDetailsActivity
    public static final int DRC_DIVE_SPOT_DETAILS_ACTIVITY_DIVE_SPOT_NOT_FOUND = 1001;
    public static final int DRC_DIVE_SPOT_DETAILS_ACTIVITY_FAILED_TO_CONNECT = 1002;

    //    DiveSpotPhotosActivity
    public static final int DRC_DIVE_SPOT_PHOTOS_ACTIVITY_DIVE_SPOT_NOT_FOUND = 1101;
    public static final int DRC_DIVE_SPOT_PHOTOS_ACTIVITY_CONNECTION_FAILURE = 1102;

//  ReviewsActivity
    public static final int DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE = 1201;
    public static final int DRC_REVIEWS_ACTIVITY_FAILED_TO_CONNECT = 1202;
    public static final int DRC_REVIEWS_ACTIVITY_DIVE_SPOT_NOT_FOUND = 1203;
    public static final int DRC_REVIEWS_ACTIVITY_DELETED_COMMENT_NOT_FOUND = 1204;
    public static final int DRC_REVIEWS_ACTIVITY_REPORTED_COMMENT_NOT_FOUND = 1205;
    public static final int DRC_REVIEWS_ACTIVITY_RIGHTS_NEED = 1206;
    public static final int DRC_REVIEWS_ACTIVITY_REPORT_SELF_REVIEW = 1207;
    
    //DiveSpotsListActivity
    public static final int DRC_DIVE_SPOTS_LIST_ACTIVITY_FAILED_TO_CONNECT = 1301;

    //User likes dislikes activity
    public static final int DRC_USER_LIKES_DISLIKES_ACTIVITY_FAILED_TO_CONNECT = 1501;

    //ImageSliderActivity
    public static final int DRC_IMAGE_SLIDER_ACTIVITY_FAILED_TO_CONNECT = 1601;
    public static final int DRC_IMAGE_SLIDER_ACTIVITY_CONNECTION_FAILURE_GET_REPORT_TYPES = 1602;
    public static final int DRC_IMAGE_SLIDER_ACTIVITY_BAD_REQUEST_IN_REPORT = 1603;
}
