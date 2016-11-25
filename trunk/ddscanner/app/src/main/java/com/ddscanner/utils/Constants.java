package com.ddscanner.utils;

/**
 * Created by lashket on 7.5.16.
 */
public class Constants {

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


    public static final String SEARCH_ACTIVITY_INTENT_KEY = "LATLNGBOUNDS";

    /*Maplist fragment*/
    public static final String OBJECT_TYPE_REEF = "Reef";
    public static final String OBJECT_TYPE_CAVE = "Cave";
    public static final String OBJECT_TYPE_WRECK = "Wreck";
    public static final String OBJECT_TYPE_OTHER = "Other";

    /*Add dive spot activity*/
    public static final String ADD_DIVE_SPOT_ACTIVITY_LATLNG = "location";
    public static final String ADD_DIVE_SPOT_ACTIVITY_RESULT_LAT_LNG = "new_spot_location";
    public static final String ADD_DIVE_SPOT_ACTIVITY_SEALIFE = "sealife";
    public static final String ADD_DIVE_SPOT_ACTIVITY_DIVESPOT = "divespot";
    public static final String ADD_DIVE_SPOT_ACTIVITY_SEALIFE_ARRAY = "sealife[]";
    public static final String ADD_DIVE_SPOT_ACTIVITY_IMAGES_ARRAY = "images[]";
    public static final String ADD_DIVE_SPOT_INTENT_LOCATION_NAME = "LOCATIONNAME";
    public static final String ADD_DIVE_SPOT_INTENT_DIVESPOT_ID = "divespot_id";
    public static final String ADD_DIVE_SPOT_INTENT_IS_FROM_MAP = "is_from_map";

    public static final int MAIN_ACTIVITY_ACTVITY_REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY = 2014;
    /*Fields in filters*/
    public static final String FILTERS_VALUE_OBJECT = "object";
    public static final String FILTERS_VALUE_ACCESS = "access";
    public static final String FILTERS_VALUE_CURRENTS = "currents";
    public static final String FILTERS_VALUE_LEVEL = "level";
    public static final String FILTERS_VALUE_VISIBILITY = "visibility";
    public static final String FILTERS_VALUE_REPORT = "report";

    /*Foreign user activity*/
    public static final String FOREIGN_USER_ACTIVITY_INTENT_ISCREATED = "ISCREATED";
    public static final String FOREIGN_USER_ACTIVITY_INTENT_ISEDITED = "ISEDITED";
    public static final String FOREIGN_USER_ACTIVITY_INTENT_ISCHECKIN = "ISCHECKIN";
    public static final String FOREIGN_USER_ACTIVITY_INTENT_USER_ID = "USERID";

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

    /*Self reviews*/
    public static final String SELF_REVIEWS_ACTIVITY_INTENT_USER_ID = "USERID";

    /*Review slider activity*/
    public static final String REVIEWS_IMAGES_SLIDE_INTENT_IMAGES = "IMAGES";
    public static final String REVIEWS_IMAGES_SLIDE_INTENT_POSITION = "POSITION";

    /*Main activity*/
    public static final String MAIN_ACTIVITY_ACTVITY_EXTRA_LATLNGBOUNDS = "LATLNGBOUNDS";

    public static final String USER_TYPE_DIVE_CENTER = "divecenter";
    public static final String USER_TYPE_DIVER = "diver";
    public static final String USER_TYPE_INSTRUCTOR = "diver";

    /*Images path constants*/
    public static final String IMAGE_PATH_PREVIEW = "/images/divespots/preview/";
    public static final String IMAGE_PATH_MEDIUM = "/images/divespots/medium/";
    public static final String IMAGE_PATH_ORIGIN = "/images/divespots/origin/";

    public static final String SEALIFE_IMAGE_PATH_PREVIEW = "/images/sealife/preview/";
    public static final String SEALIFE_IMAGE_PATH_MEDIUM = "/images/sealife/medium/";
    public static final String SEALIFE_IMAGE_PATH_ORIGIN = "/images/sealife/origin/";

    public static final String DISTRICT_IMAGE_PATH_PREVIEW = "/images/district/preview/";
    public static final String DISTRICT_IMAGE_PATH_MEDIUM = "/images/district/medium/";
    public static final String DISTRICT_IMAGE_PATH_ORIGIN = "/images/district/origin/";

    public static final String USER_IMAGE_PATH_PREVIEW = "/images/social/preview/";
    public static final String USER_IMAGE_PATH_MEDIUM = "/images/social/medium/";
    public static final String USER_IMAGE_PATH_ORIGIN = "/images/social/origin/";

}
