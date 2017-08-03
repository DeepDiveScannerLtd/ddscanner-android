
package com.ddscanner.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.BaseUser;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.entities.SignInType;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPreferenceHelper {

    private static final String TAG = SharedPreferenceHelper.class.getName();

    private static final String PREFERENCES_GCM_ID = "PREFERENCES_GCM_ID";
    private static final String TOKEN = "TOKEN";
    private static final String IS_USER_SIGNED_IN = "ISLOGINED";
    private static final String IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH_VERSION_3_0_5";
    private static final String SIGN_IN_TYPE = "SIGN_IN_TYPE";
    private static final String SN = "SOCIALNETWORK";
    private static final String SECRET = "SECRET";
    private static final String CURRENTS = "CURRENTS";
    private static final String VISIBILITY = "VISIBILITY";
    private static final String LEVEL = "LEVEL";
    private static final String OBJECT = "OBJECT";
    private static final String USER_SERVER_ID = "USER_SERVER_ID";
    private static final String USER_APP_ID = "USER_APP_ID";
    private static final String IS_USER_APP_ID_RECEIVED = "IS_USER_APP_ID_RECEIVED";
    private static final String IS_MUST_REFRESH_DIVE_SPOT_ACTIVITY = "IS_MUST_REFRESH_DIVE_SPOT_ACTIVITY";
    private static final String USERS_LiST = "USERS_LIST";
    private static final String SEALIFES_LIST = "sealifes";
    private static final String LAST_USER_KNOWN_LATITUDE = "latitude";
    private static final String LAST_USER_KNOWN_LONGITUDE = "longitude";
    private static final String DIVECENTER_SEARCH_SOURCE = "dive_center_search_source";
    //using for fb event, don't delete this
    private static final String IS_NEED_TO_SHOW_TUTORIAL = "show_tutorial";
    private static final String TUTORIAL_STATE = "tutorial_state";
    private static final String IS_MUST_TRACK_FB_EVENT = "fb_event";
    private static final String IS_MUST_TO_SHOW_DIVE_SPOT_DETAILS_TUTORIAL = "dive_spot_tutorial";

    private static SharedPreferences prefs;

    public enum SearchSourceType {
        REGISTRATION("registration"), PROFILE("profile");

        private String source;

        SearchSourceType(String source) {
            this.source = source;
        }

        public String getSource() {
            return source;
        }
    }

    public enum TutorialState {

        SKIPPED("skipped"), WATCHED("watched"), PARTLY_WATCHED("partly_watched");

        String state;

        TutorialState(String state) {
            this.state = state;
        }

        public String getState() {
            return state;
        }
    }

    public void setFbTracked() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putBoolean(IS_MUST_TRACK_FB_EVENT, false);
        editor.apply();
    }

    public boolean getIsMustTrackFbEvent() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getBoolean(IS_MUST_TRACK_FB_EVENT, true);
    }

    public void setIsMustToShowDiveSpotDetailsTutorial() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putBoolean(IS_MUST_TO_SHOW_DIVE_SPOT_DETAILS_TUTORIAL, true);
        editor.apply();
    }

    public boolean getIsMstToShowDiveSpotTutorial() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getBoolean(IS_MUST_TO_SHOW_DIVE_SPOT_DETAILS_TUTORIAL, false);
    }

    public void setTutorialState(TutorialState state) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(TUTORIAL_STATE, state.getState());
        editor.apply();
        DDScannerApplication.getInstance().setTutorialState(state);
    }

    public String getTutorialState() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(TUTORIAL_STATE, TutorialState.PARTLY_WATCHED.getState());
    }

    public void setDivecenterSearchSource(SearchSourceType type) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(DIVECENTER_SEARCH_SOURCE, type.getSource());
        editor.apply();
    }

    public String getDivecenterSearchSource() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(DIVECENTER_SEARCH_SOURCE, "");
    }

    public void setUserLocation(LatLng latLng) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(LAST_USER_KNOWN_LATITUDE, String.valueOf(latLng.latitude));
        editor.putString(LAST_USER_KNOWN_LONGITUDE, String.valueOf(latLng.longitude));
        editor.apply();
    }

    private boolean isLocationSaved() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return !(prefs.getString(LAST_USER_KNOWN_LATITUDE, "").isEmpty() || prefs.getString(LAST_USER_KNOWN_LONGITUDE, "").isEmpty());
    }

    public String getUserLattitude() {
        if (isLocationSaved()) {
            prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
            return prefs.getString(LAST_USER_KNOWN_LATITUDE, "");
        }
        return null;
    }

    public String getUserLongitude() {
        if (isLocationSaved()) {
            prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
            return prefs.getString(LAST_USER_KNOWN_LONGITUDE, "");
        }
        return null;
    }

    public void setIsMustRefreshDiveSpotActivity(boolean value) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putBoolean(IS_MUST_REFRESH_DIVE_SPOT_ACTIVITY, value);
        editor.apply();
    }

    public boolean getIsMustRefreshDiveSpotActivity() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getBoolean(IS_MUST_REFRESH_DIVE_SPOT_ACTIVITY, false);
    }

    public void setIsFirstLaunch(boolean isFirstLaunch) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putBoolean(IS_FIRST_LAUNCH, isFirstLaunch);
        editor.apply();
    }

    public boolean getIsFirstLaunch() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getBoolean(IS_FIRST_LAUNCH, true);
    }

    public void clear() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public void setUserAppId(String appId) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(USER_APP_ID, appId);
        editor.apply();
    }

    public String getUserAppId() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(USER_APP_ID, "");
    }

    public void setUserAppIdReceived() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putBoolean(IS_USER_APP_ID_RECEIVED, true);
        editor.apply();
    }

    public void setCurrents(String value) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(CURRENTS, value);
        editor.apply();
    }

    public String getCurrents() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(CURRENTS, "");
    }

    public void setLevel(String value) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(LEVEL, value);
        editor.apply();
    }

    public String getLevel() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(LEVEL, "");
    }

    public boolean isFiltersApplyied() {
        return !(getLevel().isEmpty() && getObject().isEmpty() && getSealifesList() == null);
    }

    public void setVisibility(String value) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(VISIBILITY, value);
        editor.apply();
    }

    public String getVisibility() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(VISIBILITY, "");
    }


    public void setObject(String value) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(OBJECT, value);
        editor.apply();
    }

    public String getObject() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(OBJECT, "");
    }

    public void setSealifesList(ArrayList<SealifeShort> sealifesList) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        if (sealifesList == null) {
            editor.putString(SEALIFES_LIST, "");
        } else {
            editor.putString(SEALIFES_LIST, new Gson().toJson(sealifesList));
        }
        editor.apply();
    }

    public void clearFilters() {
        setObject("");
        setLevel("");
        setSealifesList(null);
    }

    public ArrayList<SealifeShort> getSealifesList() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        if (prefs.getString(SEALIFES_LIST, "").isEmpty()) {
            return null;
        }
        Type listType = new TypeToken<ArrayList<SealifeShort>>(){}.getType();
        return new Gson().fromJson(prefs.getString(SEALIFES_LIST,""), listType);
    }

    public void setGcmId(String appInstanceId) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(PREFERENCES_GCM_ID, appInstanceId);
        editor.apply();
    }

    private void setIsUserSignedIn(boolean isUserSignedIn, SignInType signInType) {
        Log.i(TAG, "setIsUserSignedIn " + isUserSignedIn);
        if (isUserSignedIn && signInType == null) {
            throw new RuntimeException("signInType must not be null when isUserSignedIn = true");
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putBoolean(IS_USER_SIGNED_IN, isUserSignedIn);
        editor.apply();
    }

    public void setToken(String token) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(TOKEN, "");
    }

    public void setSn(String sn) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(SN, sn);
        editor.apply();
    }

    public String getSn() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(SN, "");
    }

    public void setSecret(String secret) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(SECRET, secret);
        editor.apply();
    }

    public String getSecret() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(SECRET, "");
    }

    public void setUserServerId(String id) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(USER_SERVER_ID, id);
        editor.apply();
    }

    public String getUserServerId() {
        if (getIsUserSignedIn()) {
            return getActiveUser().getId();
        }
        return "";
    }

    public void setLastShowingNotificationTime(long time) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putLong("LASTNOTIF", time);
        editor.apply();
    }

    /*Multi accounts mechanism*/

    public enum UserType {
        DIVECENTER("dc"), DIVER("diver"), INSTRUCTOR("instructor"), NONE("not_logged_in");

        private String name;

        UserType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

    }

    public static UserType getActiveUserType() {
        if (getIsUserSignedIn()) {
            switch (getActiveUser().getType()) {
                case 0:
                    return UserType.DIVECENTER;
                case 1:
                    return UserType.DIVER;
                case 2:
                    return UserType.INSTRUCTOR;
            }
        }
        return UserType.NONE;
    }

    public static String getActiveUserToken() {
        return getActiveUser().getToken();
    }



    public static ArrayList<BaseUser> getUsersList() {
        Type listType = new TypeToken<ArrayList<BaseUser>>(){}.getType();
        return new Gson().fromJson(prefs.getString(USERS_LiST, ""), listType);
    }

    public static void setUsersList(ArrayList<BaseUser> users) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(USERS_LiST, new Gson().toJson(users));
        editor.apply();
        updateUserTypeInApplication();
    }

    public static boolean getIsUserSignedIn() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return !(prefs.getString(USERS_LiST, "").equals("") || getUsersList().size() == 0);
    }

    public void addUserToList(BaseUser baseUser) {
        boolean isUserAlsoInList = false;
        ArrayList<BaseUser> users;
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        if (getIsUserSignedIn()) {
            users = getUsersList();
            for (BaseUser user : users) {
                if (user.isActive()) {
                    users.get(users.indexOf(user)).setActive(false);
                }
                if (user.getId().equals(baseUser.getId())) {
                    users.set(users.indexOf(user), baseUser);
                    isUserAlsoInList = true;
                }
            }
            if (!isUserAlsoInList) {
                users.add(baseUser);
                EventsTracker.trackNewAccountAdded();
            }
            setUsersList(users);
            return;
        }
        users = new ArrayList<>();
        users.add(baseUser);
        setUsersList(users);
    }

    public void logoutFromAllAccounts() {
        setIsUserSignedIn(false, null);
        setUsersList(new ArrayList<BaseUser>());
        updateUserTypeInApplication();
    }

    public static void removeUserFromList(String id) {
        ArrayList<BaseUser> users = getUsersList();
        for (BaseUser baseUser : users) {
            if (baseUser.getId().equals(id)) {
                users.remove(users.indexOf(baseUser));
                break;
            }
        }
        if (users.size() > 0) {
            BaseUser baseUser = users.get(0);
            baseUser.setActive(true);
            users.set(0, baseUser);
        }
        setUsersList(users);
    }

    public static BaseUser getActiveUser() {
        for (BaseUser baseUser : getUsersList()) {
            if (baseUser.isActive()) {
                return baseUser;
            }
        }
        return null;
    }

    public static void changeActiveUser(String id) {
        ArrayList<BaseUser> users = getUsersList();
        for (BaseUser user :users) {
            if (user.isActive()) {
                user.setActive(false);
                users.set(users.indexOf(user), user);
            }
            if (user.getId().equals(id)) {
                user.setActive(true);
                users.set(users.indexOf(user), user);
            }
        }
        setUsersList(users);
    }

    private static void updateUserTypeInApplication() {
        DDScannerApplication.getInstance().setActiveUserType(getActiveUserType().getName());
    }

    public static void setIsNeedToShowTutorial() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putBoolean(IS_NEED_TO_SHOW_TUTORIAL, false);
        editor.apply();
    }

    public static boolean getIsNeedToShowTutorial() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getBoolean(IS_NEED_TO_SHOW_TUTORIAL, true);
    }

}
