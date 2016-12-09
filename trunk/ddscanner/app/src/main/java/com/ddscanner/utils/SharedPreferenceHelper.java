
package com.ddscanner.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.entities.SignInType;
import com.ddscanner.entities.User;
import com.google.gson.Gson;

public class SharedPreferenceHelper {

    private static final String PREFERENCES_GCM_ID = "PREFERENCES_GCM_ID";
    private static final String TOKEN = "TOKEN";
    private static final String IS_USER_SIGNED_IN = "ISLOGINED";
    private static final String IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH";
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


    private static SharedPreferences prefs;

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

    public boolean isUserAppIdReceived() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getBoolean(IS_USER_APP_ID_RECEIVED, false);
    }

    public void setPhotolink(String value) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(PHOTOLINK, value);
        editor.commit();
    }

    public String getPhotolink() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(PHOTOLINK, "");
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

    public void setGcmId(String appInstanceId) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(PREFERENCES_GCM_ID, appInstanceId);
        editor.commit();
    }

    public void setIsUserSignedIn(Boolean isUserSignedIn, SignInType signInType) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        if (isUserSignedIn) {
            if (signInType == null) {
                throw new RuntimeException("signInType must not be null when isUserSignedIn = true");
            }
            editor.putString(IS_USER_SIGNED_IN, "1");
            editor.putString(SIGN_IN_TYPE, signInType.getName());
        } else {
            editor.putString(IS_USER_SIGNED_IN, "0");
            editor.remove(SIGN_IN_TYPE);
        }
        System.out.println("LOGINED");
        editor.commit();
    }

    public boolean isUserLoggedIn() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        String is = prefs.getString(IS_USER_SIGNED_IN, "");
        if (is.equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isFirstLaunch() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getBoolean(IS_FIRST_LAUNCH, true);
    }

    public void setIsFirstLaunch(boolean isFirstLaunch) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putBoolean(IS_FIRST_LAUNCH, isFirstLaunch);
        editor.commit();
    }

    public String getGcmId() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(PREFERENCES_GCM_ID, "");
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

    public void logout() {
      //  SharedPreferenceHelper.setToken("");
        setUserServerId("");
        setSecret("");
        setSn("");
        setToken("");
        setIsUserSignedIn(false, null);
    }

    public void setUserServerId(String id) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(USER_SERVER_ID, id);
        editor.commit();
    }

    public String getUserServerId() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(USER_SERVER_ID, "");
    }

    public void setLastShowingNotificationTime(long time) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putLong("LASTNOTIF", time);
        editor.commit();
    }

    public long getLastShowingNotificationTime() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getLong("LASTNOTIF", 0);
    }

    public void setLastShowingActivityTime(long time) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putLong("LASTACTIVITY", time);
        editor.commit();
    }

    public long getLastShowingActivityTime() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getLong("LASTACTIVITY", 0);
    }

    public void setActiveUser(User account) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        if (account == null) {
            editor.putString(LOGGED_USER, "");
            editor.commit();
            return;
        }
        String resultString = new Gson().toJson(account);
        editor.putString(LOGGED_USER, resultString);
        editor.commit();
    }

    public void clear() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public User getUser() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        if (prefs.getString(LOGGED_USER, "").isEmpty()) {
            return null;
        }
        User account = new Gson().fromJson(prefs.getString(LOGGED_USER, ""), User.class);
        return account;
    }

    public void saveDiveCenter(User account) {
        String resultString = new Gson().toJson(account);
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(LOGGED_DIVE_CENTER, resultString);
        editor.commit();
    }

    public User getLoggedDiveCenter() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        User account = new Gson().fromJson(prefs.getString(LOGGED_DIVE_CENTER, ""), User.class);
        return account;
    }

    public void setActiveUserType(int type) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putInt(LOGGED_TYPE, type);
        editor.commit();
    }

    public int getActiveUserType() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getInt(LOGGED_TYPE, -1);
    }

    public String getLoggedUserToken() {
        switch (getActiveUserType()) {
            case 0:
                return getLoggedDiveCenter().getToken();
            case 1:
            case 2:
                return getUser().getToken();
            default:
                return "";
        }
    }

    public void setIsDiveCenterLoggedIn(boolean isLogged) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putBoolean(IS_DC_LOGGED_IN, isLogged);
        editor.commit();
    }

    public void setIsUserLoggedIn(boolean isLogged) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putBoolean(IS_USER_LOGGED_IN, isLogged);
        editor.commit();
    }

    public boolean getIsDcLoggedIn() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getBoolean(IS_DC_LOGGED_IN, false);
    }

    public boolean getIsUserLoggedIn() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getBoolean(IS_USER_LOGGED_IN, false);
    }

}
