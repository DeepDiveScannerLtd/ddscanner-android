package com.ddscanner.utils;

public class ActivitiesRequestCodes {

    /**
     * Store here all the activities request and result codes.
     * <p/>
     * Request code naming rules: REQUEST_CODE_<activity name>_<request name>
     * Result code naming rules: RESULT_CODE_<activity name>_<request name>
     * Request int value convention: XXYY, where XX - unique "id" of activity/screen, YY - unique "id" of request code
     */

    //    BaseAppCompatActivity
    public static final int REQUEST_CODE_LOCATION_PERMISSION = 1101;
    public static final int REQUEST_CODE_LOCATION_PROVIDERS = 1102;
    public static final int REQUEST_PERMISSION_FRO_PICK_MULTIPLY_PHOTOS = 1103;

    //    SplashActivity
    public static final int REQUEST_CODE_SPLASH_ACTIVITY_PLAY_SERVICES_RESOLUTION = 1201;
    public static final int REQUEST_CODE_SPLASH_ACTIVITY_LOGIN = 1202;
    public static final int REQUEST_CODE_SPLASH_ACTIVITY_SIGN_UP = 1203;

    //    MainActivity
    public static final int REQUEST_CODE_MAIN_ACTIVITY_GO_TO_MY_LOCATION = 1301;
    public static final int REQUEST_CODE_MAIN_ACTIVITY_PLACE_AUTOCOMPLETE = 1302;
    public static final int REQUEST_CODE_MAIN_ACTIVITY_PICK_PHOTO = 1303;
    public static final int REQUEST_CODE_MAIN_ACTIVITY_LOGIN = 1304;
    public static final int REQUEST_CODE_MAIN_ACTIVITY_IMAGE_CAPTURE = 1305;
    public static final int REQUEST_CODE_MAIN_ACTIVITY_PERMISSION_READ_STORAGE = 1306;
    public static final int REQUEST_CODE_MAIN_ACTIVITY_PERMISSION_CAMERA = 1307;
    public static final int REQUEST_CODE_MAIN_ACTIVITY_PERMISSION_WRITE_STORAGE = 1308;
    public static final int REQUEST_CODE_MAIN_ACTIVITY_PERMISSION_CAMERA_AND_WRITE_STORAGE = 1309;
    public static final int REQUEST_CODE_MAIN_ACTIVITY_CHOSE_GOOGLE_ACCOUNT = 1310;
    public static final int REQUEST_CODE_MAIN_ACTIVITY_LOGIN_TO_ADD_ACCOUNT = 1311;
    public static final int REQUEST_CODE_MAIN_ACTIVITY_SHOW_EDIT_PROFILE_ACTIVITY = 1312;
    public static final int REQUEST_CODE_MAIN_ACTIVITY_SHOW_INSTRUCTORS_ACTIVITY = 1314;
    public static final int REQUEST_CODE_MAIN_ACTIVITY_PICK_DIVE_CENTER_FOR_INSTRUCTOR = 1315;
    public static final int REQUEST_CODE_MAIN_ACTIVITY_FILTERS = 1316;

    //    SearchActivity
    public static final int REQUEST_CODE_SEARCH_ACTIVITY_LOGIN = 1401;
    public static final int RESULT_CODE_SEARCH_ACTIVITY_MY_LOCATION = 1402;

    //    AddDiveSpotActivity
    public static final int REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_PHOTO = 1501;
    public static final int REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_SEALIFE = 1502;
    public static final int REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_LOCATION = 1503;
    //    public static final int REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_LOGIN = 1504;
    public static final int REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_LOGIN_TO_SEND = 1505;
    public static final int REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_LOGIN_TO_GET_DATA = 1506;
    public static final int REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PERMISSION_READ_STORAGE = 1507;
    public static final int REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_LANGUAGE = 1508;
    public static final int REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_COUNTRY = 1509;

    //    MapListFragment
    public static final int REQUEST_CODE_MAP_LIST_FRAGMENT_GET_LOCATION_ON_FRAGMENT_START = 1601;
    public static final int REQUEST_CODE_MAP_LIST_FRAGMENT_GO_TO_CURRENT_LOCATION = 1602;

    //    DiveCentersActivity
    public static final int REQUEST_CODE_DIVE_CENTERS_MAP_GO_TO_CURRENT_LOCATION = 1701;

    public static final int REQUEST_CODE_OPEN_LOGIN_SCREEN = 1801;

    //    ForeignProfileActivity
    public static final int REQUEST_CODE_FOREIGN_USER_LOGIN = 1901;
    public static final int REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SEE_CHECKINS = 1902;
    public static final int REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SEE_CREATED = 1903;
    public static final int REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SEE_EDITED = 1904;

    //    ImageSliderActivity
    public static final int REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_REPORT = 2001;
    public static final int REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_DELETE = 2002;
    public static final int REQUEST_CODE_SLIDER_ACTIVITY_LOGIN_FOR_LIKE = 2003;

    //    DiveSpotPhotosActivity
    public static final int REQUEST_CODE_PHOTOS_ACTIVITY_SLIDER = 2101;
    public static final int REQUEST_CODE_PHOTOS_ADD_PHOTOS = 2102;
    public static final int REQUEST_CODE_PHOTOS_LOGIN = 2103;
    public static final int REQUEST_CODE_PHOTOS_SELECT_PHOTOS = 2104;

    //    DiveSpotDetailsActivity
    public static final int REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_PICK_PHOTOS = 2201;
    public static final int REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_PICK_PHOTOS = 2202;
    public static final int REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_ADD_PHOTOS_ACTIVITY = 2203;
    public static final int REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LEAVE_REVIEW = 2204;
    public static final int REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_EDIT_DIVE_SPOT = 2205;
    public static final int REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_PHOTOS = 2207;
    public static final int REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_REVIEWS = 2208;
    public static final int REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_VALIDATE_SPOT = 2209;
    public static final int REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_INVALIDATE_SPOT = 2210;
    public static final int REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_EDIT_SPOT = 2211;
    public static final int REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_CHECK_IN = 2212;
    public static final int REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_CHECK_OUT = 2213;
    public static final int REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_ADD_TO_FAVOURITES = 2214;
    public static final int REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_REMOVE_FROM_FAVOURITES = 2215;
    public static final int REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_SHOW_FOR_ADD_PHOTOS = 2216;
    public static final int REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_SHOW_FOR_ADD_MAPS = 2217;
    public static final int REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_PICK_PHOTO_FOR_DIALOG = 2218;
    public static final int REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LOGIN_TO_LEAVE_REVIEW = 2219;

    //    SelfCommentsActivity
    public static final int REQUEST_CODE_SELF_REVIEWS_LOGIN_TO_VIEW_COMMENTS = 2301;
    public static final int REQUEST_CODE_SELF_REVIEWS_LOGIN_TO_DELETE_COMMENTS = 2302;
    // TODO Get rid of this code. It is impossible to face case when it is used
    public static final int REQUEST_CODE_SELF_REVIEWS_EDIT_MY_REVIEW = 2303;

    //    LeaveReviewActivity
    public static final int REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_PERMISSION_READ_STORAGE = 2401;
    public static final int REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_LOGIN = 2402;
    public static final int REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_PICK_PHOTO = 2403;
    public static final int REQUEST_CODE_LEAVE_REVIEW_ACTIVITY_PICK_SEALIFE = 2404;

    //    AddSealifeActivity
    public static final int REQUEST_CODE_ADD_SEALIFE_ACTIVITY_PERMISSION_READ_STORAGE = 2501;
    public static final int REQUEST_CODE_ADD_SEALIFE_ACTIVITY_PICK_PHOTO = 2502;
    public static final int REQUEST_CODE_ADD_SEALIFE_ACTIVITY_LOGIN_TO_SEND = 2503;

    //    AddPhotosDoDiveSpotActivity
    public static final int REQUEST_CODE_ADD_PHOTOS_DO_DIVE_SPOT_ACTIVITY_LOGIN_TO_SEND = 2601;

    //
    public static final int REQUEST_CODE_FOREIGN_USER_SPOT_LIST_LOGIN_TO_GET_CHECKINS = 2701;
    public static final int REQUEST_CODE_FOREIGN_USER_SPOT_LIST_LOGIN_TO_GET_ADDED = 2702;
    public static final int REQUEST_CODE_FOREIGN_USER_SPOT_LIST_LOGIN_TO_GET_EDITED = 2703;

    //    DiveSpotsListActivity
    public static final int REQUEST_CODE_DIVE_SPOTS_LIST_ACTIVITY_LOGIN = 2801;

    //    EditCommentActivity
    public static final int REQUEST_CODE_EDIT_COMMENT_ACTIVITY_LOGIN = 2901;
    public static final int REQUEST_CODE_EDIT_COMMENT_ACTIVITY_PICK_PHOTOS = 2902;

    //    EditDiveSpotActivity
    public static final int REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_PICK_PHOTO = 3001;
    public static final int REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_PICK_LOCATION = 3002;
    public static final int REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_PICK_SEALIFE = 3003;
    public static final int REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_LOGIN_TO_SEND = 3004;
    public static final int REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_LOGIN_TO_GET_DATA = 3005;
    public static final int REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_PICK_COUNTRY = 3006;

    //    UserLikesDislikesActivity
    public static final int REQUEST_CODE_USER_LIKES_DISLIKES_ACTIVITY_LOGIN = 3101;

    public static final int REQUEST_CODE_LOCATION_PERMISSION_NOT_GRANTED_ACTIVITY_LOCATION_PERMISSION = 3201;

    public static final int REQUEST_CODE_LOCATION_PROVIDERS_NOT_AVAILABLE_ACTIVITY_TURN_ON_LOCATION_SETTINGS = 3301;

    //    PickLocationActivity
    public static final int REQUEST_CODE_PICK_LOCATION_ACTIVITY_PLACE_AUTOCOMPLETE = 3401;

    //    ReviewsActivity
    public static final int REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN = 3501;
    public static final int REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LEAVE_REPORT = 3502;
    public static final int REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DELETE_COMMENT = 3503;
    public static final int REQUEST_CODE_REVIEWS_ACTIVITY_WRITE_REVIEW = 3504;
    public static final int REQUEST_CODE_REVIEWS_ACTIVITY_EDIT_MY_REVIEW = 3505;
    public static final int REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LIKE_REVIEW = 3506;
    public static final int REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DISLIKE_REVIEW = 3507;
    public static final int REQUEST_CODE_REVIEWS_ACTIVITY_SHOW_SLIDER = 3508;
    public static final int REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LEAVE_REVIEW = 3509;

    //    SearchSealifeActivity
    public static final int REQUEST_CODE_SEARCH_SEALIFE_ACTIVITY_ADD_SEALIFE = 3601;

    //    SocialNetworks
    public static final int REQUEST_CODE_SOCIAL_NETWORKS_SIGN_IN = 3701;
    public static final int REQUEST_CODE_SOCIAL_NETWORKS_LOGIN = 3702;
    public static final int REQUEST_CODE_SOCIAL_NETWORKS_SIGN_UP = 3703;

    //ReviewsPhotosImageSliderActivity
    public static final int REQUEST_CODE_LOGIN_TO_LEAVE_REPORT = 3801;

    //AchievementsActivity
    public static final int REQUEST_CODE_ACHIEVEMENTS_ACTIVITY_LOGIN_TO_ACHIEVEMNTS = 3901;

    //FilterActivity
    public static final int FILTER_ACTIVITY_PICK_SEALIFE = 4001;

    //Base pick photos activity
    public static final int BASE_PICK_PHOTOS_ACTIVITY_PERMISSIO_WRITE_STORAGE = 4101;
    public static final int BASE_PICK_PHOTOS_ACTIVITY_PERMISSIO_READ_STORAGE = 4102;
    public static final int BASE_PICK_PHOTOS_ACTIVITY_PICK_PHOTO_FROM_CAMERA = 4103;
    public static final int BASE_PICK_PHOTOS_ACTIVITY_PICK_PHOTO_FROM_GALLERY = 4104;

    //Edit dive center profile activity
    public static final int EDIT_DIVE_CENTER_ACTIVITY_ADD_SPOT = 4201;
    public static final int EDIT_DIVE_CENTER_ACTIVITY_PICK_LOCATION = 4202;
    public static final int EDIT_DIVE_CENTER_ACTIVITY_ADD_LANGUAGE = 4203;
    public static final int EDIT_DIVE_CENTER_ACTIVITY_CHOOSE_ADDRESS = 4204;

    //Edit profile activity
    public static final int REQUEST_CODE_EDIT_PROFILE_ACTIVITY_CHOOSE_DIVE_CENTER = 4301;

    //Dive center spots activity
    public static final int REQUEST_CODE_DIVE_CENTER_SPOTS_ACTIVITY_GO_TO_MY_LOCATION = 4401;

    public static final int REQUEST_CODE_SHOW_USER_PROFILE_PHOTOS = 4501;

    //Foreign user profile activity
    public static final int REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_CHECKINS = 4601;
    public static final int REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_EDITED = 4602;
    public static final int REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_CREATED = 4603;
    public static final int REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_LIKES = 4604;
    public static final int REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_DISLIKES = 4605;
    public static final int REQUEST_CODE_FOREIGN_USER_LOGIN_TO_SHOW_REVIEWS = 4606;

    //Dive Center Languages Activity
    public static final int REQUEST_CODE_LANGUAGES_ACTIVITY_LOGIN = 4701;

    //SignUpActivity
    public static final int REQUEST_CODE_SIGN_UP_ACTIVITY_PICK_DIVECENTER = 4801;

    //ChangeAddressActivity
    public static final int REQUEST_CODE_ADDRESS_ACTIVITY_OPEN_MAP = 4901;
    public static final int REQUEST_CODE_ADDRESS_ACTIVITY_CHOOSE_COUNTRY = 4902;

    //SealifeDetailsActivity
    public static final int REQUEST_CODE_SEALIFE_ACTIVITY_EDIT_SEALIFE = 5001;

}