package com.ddscanner.utils;

public class Constants {

    public static final long NOTIFICATION_SHOWING_INTERVAL = 864000000;

    public static final String ANALYTIC_VERSION = "v1_3";
    public static final String PLATFORM = "android";
    public static final String TUTORIAL_VERSION = "onboarding_1";

    //CreateDiveCenterArguments
    public static final String ARG_ID = "id";
    public static final String ARG_DC_TYPE = "type";
    public static final String ARG_DC_NAME = "dc_name";

    public static final String images = "https:";
    public static final String DIVESPOTID = "ID";

    /*Profile dialog*/
    public static final String PROFILE_DIALOG_FACEBOOK_URL = "https://www.facebook.com/";
    public static final String PROFILE_DIALOG_FACEBOOK_OLD_URI = "fb://facewebmodal/f?href=";
    public static final String PROFILE_DIALOG_FACEBOOK_NEW_URI = "fb://page/";
    public static final String PROFILE_DIALOG_TWITTER_URI = "twitter://user?screen_name=";
    public static final String PROFILE_DIALOG_TWITTER_URL = "https://twitter.com/";

    public static final String SEARCH_ACTIVITY_INTENT_KEY = "LATLNGBOUNDS";

    /*Maplist fragment*/
    public static final String OBJECT_TYPE_REEF = "Reef";
    public static final String OBJECT_TYPE_CAVE = "Cave";
    public static final String OBJECT_TYPE_WRECK = "Wreck";
    public static final String OBJECT_TYPE_OTHER = "Other";

    /*Add dive spot activity*/
    public static final String ADD_DIVE_SPOT_ACTIVITY_RESULT_LAT_LNG = "new_spot_location";
    public static final String ADD_DIVE_SPOT_ACTIVITY_SEALIFE = "sealife";
    public static final String ADD_DIVE_SPOT_ACTIVITY_SEALIFE_ARRAY = "sealifes[]";
    public static final String ADD_DIVE_SPOT_ACTIVITY_IMAGES_ARRAY = "photos[]";
    public static final String ADD_DIVE_SPOT_ACTIVITY_MAPS_ARRAY = "maps[]";
    public static final String ADD_DIVE_SPOT_INTENT_DIVESPOT_ID = "divespot_id";
    public static final String ADD_DIVE_SPOT_INTENT_IS_FROM_MAP = "is_from_map";

    public static final int MAIN_ACTIVITY_ACTVITY_REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY = 2014;

    /*UserOld Likes activity*/
    public static final String USER_LIKES_ACTIVITY_INTENT_IS_LIKE = "ISLIKE";
    public static final String USER_LIKES_ACTIVITY_INTENT_USER_ID = "USERID";

    /*Add photo to dive spot activity*/
    public static final String ADD_PHOTO_ACTIVITY_INTENT_IMAGES = "IMAGES";
    public static final String ADD_PHOTO_ACTIVITY_INTENT_DIVE_SPOT_ID = "id";

    public static final String MULTIPART_TYPE_TEXT = "multipart/form-data";

    /*Guide description activity*/
    public static final String GUIDE_DESCRIPTION_ACTIVITY_INTENT_ITEM = "ITEM";


    public static final int PHOTOS_ACTIVITY_REQUEST_CODE_PERMISSION_READ_STORAGE = 8988;
    /*Dive spot details activity*/
    public static final String DIVE_SPOT_DETAILS_ACTIVITY_EXTRA_IS_FROM_AD_DIVE_SPOT = "ISNEW";
    public static final int DIVE_SPOT_DETAILS_ACTIVITY_REQUEST_CODE_PERMISSION_READ_STORAGE = 1083;

    public static final String USER_TYPE_DIVE_CENTER = "divecenter";
    public static final String USER_TYPE_DIVER = "diver";
    public static final String USER_TYPE_INSTRUCTOR = "diver";

}
