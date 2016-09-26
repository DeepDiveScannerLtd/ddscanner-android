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

    //DiveSpotsListActivity
    public static final int DRC_DIVE_SPOTS_LIST_ACTIVITY_FAILED_TO_CONNECT = 1301;

    // ForeignUserDiveSpotListActivity
    public static final int DRC_FOREIGN_USER_DIVE_SPOTS_ACTIVITY_FAILED_TO_CONNECT = 1401;
}
