package com.ddscanner.utils;

/**
 * Created by lashket on 7.5.16.
 */
public class Constants {

    public static final int MAIN_ACTIVITY_REQUEST_CODE_GO_TO_MY_LOCATION = 1012;
    public static final String images = "https:";
    public static final String DIVESPOTID = "ID";
    public static final String IS_HAS_INTERNET = "IS_HAS_INTERNET";
    public static final String IS_LOCATION = "IS_LOCATION";

    /*Profile dialog*/
    public static final String PROFILE_DIALOG_INTENT_USER = "USER";
    public static final String PROFILE_DIALOG_FACEBOOK_URL = "https://www.facebook.com/";
    public static final String PROFILE_DIALOG_FACEBOOK_OLD_URI = "fb://facewebmodal/f?href=";
    public static final String PROFILE_DIALOG_FACEBOOK_NEW_URI = "fb://page/";
    public static final String PROFILE_DIALOG_TWITTER_URI = "twitter://user?screen_name=";
    public static final String PROFILE_DIALOG_TWITTER_URL = "https://twitter.com/";
    public static final String PROFILE_DIALOG_GOOGLE_URL = "https://plus.google.com/";


    /*Search activity*/
    public static final int SEARCH_ACTIVITY_REQUEST_CODE_LOGIN = 1010;
    public static final int SEARCH_ACTIVITY_RESULT_CODE_MY_LOCATION = 1011;
    public static final String SEARCH_ACTIVITY_INTENT_KEY = "LATLNGBOUNDS";

    /*Maplist fragment*/
    public static final String OBJECT_TYPE_REEF = "Reef";
    public static final String OBJECT_TYPE_CAVE = "Cave";
    public static final String OBJECT_TYPE_WRECK = "Wreck";
    public static final String OBJECT_TYPE_OTHER = "Other";

    /*Add dive spot activity*/
    public static final String ADD_DIVE_SPOT_ACTIVITY_LATLNG = "location";
    public static final String ADD_DIVE_SPOT_ACTIVITY_SEALIFE = "sealife";
    public static final int ADD_DIVE_SPOT_ACTIVITY_REQUEST_CODE_PICK_PHOTO = 1001;
    public static final int ADD_DIVE_SPOT_ACTIVITY_REQUEST_CODE_PICK_SEALIFE = 1002;
    public static final int ADD_DIVE_SPOT_ACTIVITY_REQUEST_CODE_PICK_LOCATION = 1003;
    public static final int ADD_DIVE_SPOT_ACTIVITY_REQUEST_CODE_LOGIN = 1004;
    public static final int ADD_DIVE_SPOT_ACTIVITY_REQUEST_CODE_LOGIN_TO_SEND = 1005;
    public static final int ADD_DIVE_SPOT_ACTIVITY_REQUEST_CODE_LOGIN_TO_GET_DATA = 1006;
    public static final int ADD_DIVE_SPOT_ACTIVITY_REQUEST_CODE_PERMISSION_READ_STORAGE = 1007;
    public static final String ADD_DIVE_SPOT_ACTIVITY_DIVESPOT = "divespot";
    public static final String ADD_DIVE_SPOT_ACTIVITY_SEALIFE_ARRAY = "sealife[]";
    public static final String ADD_DIVE_SPOT_ACTIVITY_IMAGES_ARRAY = "images[]";
    public static final String ADD_DIVE_SPOT_INTENT_LOCATION_NAME = "LOCATIONNAME";

    /*Main activity*/
    public static final String MAIN_ACTIVITY_ACTVITY_EXTRA_LATLNGBOUNDS = "LATLNGBOUNDS";
    public static final int MAIN_ACTIVITY_ACTVITY_REQUEST_PERMISSION_READ_STORAGE = 2010;
    public static final int MAIN_ACTIVITY_ACTVITY_REQUEST_PERMISSION_CAMERA = 2011;
    public static final int MAIN_ACTIVITY_ACTVITY_REQUEST_PERMISSION_WRITE_STORAGE = 2012;
    public static final int MAIN_ACTIVITY_ACTVITY_REQUEST_PERMISSION_CAMERA_AND_WRITE_STORAGE = 2013;

    /*Fields in filters*/
    public static final String FILTERS_VALUE_OBJECT = "object";
    public static final String FILTERS_VALUE_ACCESS = "access";
    public static final String FILTERS_VALUE_CURRENTS = "currents";
    public static final String FILTERS_VALUE_LEVEL = "level";
    public static final String FILTERS_VALUE_VISIBILITY = "visibility";
    public static final String FILTERS_VALUE_REPORT = "report";

    public static final int REQUEST_CODE_MAP_LIST_FRAGMENT_GET_LOCATION_ON_FRAGMENT_START = 9010;
    public static final int REQUEST_CODE_MAP_LIST_FRAGMENT_GO_TO_CURRENT_LOCATION = 9011;
    public static final int REQUEST_CODE_DIVE_CENTERS_MAP_GO_TO_CURRENT_LOCATION = 9009;
    public static final int REQUEST_CODE_MAIN_ACTIVITY_GET_LOCATION_ON_ACTIVITY_START = 9012;
    public static final int REQUEST_CODE_OPEN_LOGIN_SCREEN = 9013;
    public static final int REQUEST_CODE_NEED_TO_LOGIN = 9014;

    /*Foreign user activity*/
    public static final String FOREIGN_USER_ACTIVITY_INTENT_ISCREATED = "ISCREATED";
    public static final String FOREIGN_USER_ACTIVITY_INTENT_ISEDITED = "ISEDITED";
    public static final String FOREIGN_USER_ACTIVITY_INTENT_ISCHECKIN = "ISCHECKIN";
    public static final String FOREIGN_USER_ACTIVITY_INTENT_USER_ID = "USERID";
    public static final int FOREIGN_USER_REQUEST_CODE_LOGIN = 1040;
    public static final int FOREIGN_USER_REQUEST_CODE_SHOW_LIST = 1042;
    public static final int FOREIGN_USER_REQUEST_CODE_SHOW_LIKES_LIST = 1043;
    public static final int FOREIGN_USER_SPOT_LIST_REQUEST_CODE_LOGIN = 1041;

    /*User Likes activity*/
    public static final String USER_LIKES_ACTIVITY_INTENT_IS_LIKE = "ISLIKE";
    public static final String USER_LIKES_ACTIVITY_INTENT_USER_ID = "USERID";
    public static final int USER_LIKES_ACTIVITY_REQUEST_CODE_LOGIN = 1050;

    /*Add photo to dive spot activity*/
    public static final String ADD_PHOTO_ACTIVITY_INTENT_IMAGES = "IMAGES";
    public static final String ADD_PHOTO_ACTIVITY_INTENT_DIVE_SPOT_ID = "id";

    public static final String MULTIPART_TYPE_TEXT = "multipart/form-data";

    /*Guide description activity*/
    public static final String GUIDE_DESCRIPTION_ACTIVITY_INTENT_ITEM = "ITEM";


    /*Image slider activity*/
    public static final int SLIDER_ACTIVITY_REQUEST_CODE_LOGIN_FOR_REPORT = 1313;
    public static final int SLIDER_ACTIVITY_REQUEST_CODE_LOGIN_FOR_DELETE = 1314;

    /*Photos activity*/
    public static final int PHOTOS_ACTIVITY_REQUEST_CODE_SLIDER = 8989;

    /*Dive spot details activity*/
    public static final String DIVE_SPOT_DETAILS_ACTIVITY_EXTRA_IS_FROM_AD_DIVE_SPOT = "ISNEW";

    /*Self reviews*/
    public static final String SELF_REVIEWS_ACTIVITY_INTENT_USER_ID = "USERID";
    public static final int SELF_REVIEWS_REQUEST_CODE_LOGIN_TO_VIEW_COMMENTS = 1070;
    public static final int SELF_REVIEWS_REQUEST_CODE_LOGIN_TO_DELETE_COMMENTS = 1071;

    /*Review slider activity*/
    public static final String REVIEWS_IMAGES_SLIDE_INTENT_IMAGES = "IMAGES";
    public static final String REVIEWS_IMAGES_SLIDE_INTENT_POSITION = "POSITION";

    /*Leave review activity*/
    public static final int LEAVE_REVIEW_ACTIVITY_REQUEST_CODE_PERMISSION_READ_STORAGE = 1090;

    /*Add sealife activity*/
    public static final int ADD_SEALIFE_ACTIVITY_REQUEST_CODE_PERMISSION_READ_STORAGE = 5500;
}
