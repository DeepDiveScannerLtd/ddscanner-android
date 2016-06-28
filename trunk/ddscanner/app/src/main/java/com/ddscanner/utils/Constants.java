package com.ddscanner.utils;

/**
 * Created by lashket on 7.5.16.
 */
public class Constants {

    public static final String images = "http:";
    public static final String DIVESPOTID = "ID";
    public static final String IS_HAS_INTERNET = "IS_HAS_INTERNET";
    public static final String IS_LOCATION = "IS_LOCATION";

    /*Search activity*/
    public static final int SEARCH_ACTIVITY_REQUEST_CODE_LOGIN = 1010;

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

    /*Fields in filters*/
    public static final String FILTERS_VALUE_OBJECT = "object";
    public static final String FILTERS_VALUE_ACCESS = "access";
    public static final String FILTERS_VALUE_CURRENTS = "currents";
    public static final String FILTERS_VALUE_LEVEL = "level";
    public static final String FILTERS_VALUE_VISIBILITY = "visibility";

    public static final int REQUEST_CODE_MAP_LIST_FRAGMENT_GET_LOCATION_ON_FRAGMENT_START = 9010;
    public static final int REQUEST_CODE_MAP_LIST_FRAGMENT_GO_TO_CURRENT_LOCATION = 9011;
    public static final int REQUEST_CODE_DIVE_CENTERS_MAP_GO_TO_CURRENT_LOCATION = 9009;
    public static final int REQUEST_CODE_MAIN_ACTIVITY_GET_LOCATION_ON_ACTIVITY_START = 9012;
    public static final int REQUEST_CODE_OPEN_LOGIN_SCREEN = 9013;
    public static final int REQUEST_CODE_NEED_TO_LOGIN = 9014;

    public static final String MULTIPART_TYPE_TEXT = "multipart/form-data";

}
