
package com.ddscanner.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ddscanner.DDScannerApplication;
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
    private static final String USERID = "USERID";
    private static final String PHOTOLINK = "PHOTOLINK";
    private static final String USERNAME = "USERNAME";
    private static final String LINK = "LINK";
    private static final String USER_SERVER_ID = "USER_SERVER_ID";
    private static final String USER_APP_ID = "USER_APP_ID";
    private static final String IS_USER_APP_ID_RECEIVED = "IS_USER_APP_ID_RECEIVED";
    private static final String LOGGED_USER = "LOGGED_USER";
    private static final String LOGGED_DIVE_CENTER = "LOGGED_DIVE_CENTER";
    private static final String LOGGED_TYPE = "LOGGED_TYPE";
    private static final String IS_DC_LOGGED_IN = "IS_DC_LOGGED_IN";
    private static final String IS_USER_LOGGED_IN = "IS_USER_LOGGED_IN";
    private static final String DC_TOKEN = "DC_TOKEN";
    private static final String USER_TOKEN= "USER_TOKEN";
    private static final String IS_MUST_REFRESH_DIVE_SPOT_ACTIVITY = "IS_MUST_REFRESH_DIVE_SPOT_ACTIVITY";
    private static final String USERS_LiST = "USERS_LIST";
    private static final String SEALIFES_LIST = "sealifes";
    private static final String LAST_USER_KNOWN_LATITUDE = "latitude";
    private static final String LAST_USER_KNOWN_LONGITUDE = "longitude";

    private static SharedPreferences prefs;

    public void setUserLocation(LatLng latLng) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(LAST_USER_KNOWN_LATITUDE, String.valueOf(latLng.latitude));
        editor.putString(LAST_USER_KNOWN_LONGITUDE, String.valueOf(latLng.longitude));
        editor.commit();
    }

    private boolean isLocationSaved() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        if (prefs.getString(LAST_USER_KNOWN_LATITUDE, "").isEmpty() || prefs.getString(LAST_USER_KNOWN_LONGITUDE, "").isEmpty()) {
            return false;
        }
        return true;
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
        editor.commit();
    }

    public boolean getIsMustRefreshDiveSpotActivity() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getBoolean(IS_MUST_REFRESH_DIVE_SPOT_ACTIVITY, false);
    }

    public void setIsFirstLaunch(boolean isFirstLaunch) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putBoolean(IS_FIRST_LAUNCH, isFirstLaunch);
        editor.commit();
    }

    public boolean getIsFirstLaunch() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getBoolean(IS_FIRST_LAUNCH, true);
    }

    public void clear() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public void setUserAppId(String appId) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(USER_APP_ID, appId);
        editor.commit();
    }

    public String getUserAppId() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(USER_APP_ID, "");
    }

    public void setUserAppIdReceived() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putBoolean(IS_USER_APP_ID_RECEIVED, true);
        editor.commit();
    }

    public void setCurrents(String value) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(CURRENTS, value);
        editor.commit();
    }

    public String getCurrents() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(CURRENTS, "");
    }

    public void setLevel(String value) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(LEVEL, value);
        editor.commit();
    }

    public String getLevel() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(LEVEL, "");
    }

    public boolean isFiltersApplyied() {
        if (getLevel().isEmpty() && getObject().isEmpty() && getSealifesList() == null) {
            return false;
        }
        return true;
    }

    public void setVisibility(String value) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(VISIBILITY, value);
        editor.commit();
    }

    public String getVisibility() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(VISIBILITY, "");
    }


    public void setObject(String value) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(OBJECT, value);
        editor.commit();
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
        editor.commit();
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
        editor.commit();
    }

    public void setIsUserSignedIn(boolean isUserSignedIn, SignInType signInType) {
        Log.i(TAG, "setIsUserSignedIn " + isUserSignedIn);
        if (isUserSignedIn && signInType == null) {
            throw new RuntimeException("signInType must not be null when isUserSignedIn = true");
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putBoolean(IS_USER_SIGNED_IN, isUserSignedIn);
        editor.commit();
    }

    public void setToken(String token) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(TOKEN, token);
        editor.commit();
    }

    public String getToken() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(TOKEN, "");
    }

    public void setSn(String sn) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(SN, sn);
        editor.commit();
    }

    public String getSn() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(SN, "");
    }

    public void setSecret(String secret) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(SECRET, secret);
        editor.commit();
    }

    public String getSecret() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(SECRET, "");
    }

    public void setUserServerId(String id) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(USER_SERVER_ID, id);
        editor.commit();
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
        editor.commit();
    }

    /*Multi accounts mechanism*/

    public enum UserType {
        DIVECENTER, DIVER, INSTRUCTOR, NONE
    }

    public UserType getActiveUserType() {
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
        editor.commit();
    }

    public boolean getIsUserSignedIn() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        if (prefs.getString(USERS_LiST, "").equals("") || getUsersList().size() == 0) {
            return false;
        }
        return true;
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

}
