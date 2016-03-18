
package com.ddscanner.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.ddscanner.DDScannerApplication;

public class SharedPreferenceHelper {

    private static final String PREFERENCES_GCM_ID = "PREFERENCES_GCM_ID";
    private static final String TOKEN = "TOKEN";
    private static final String SN = "SOCIALNETWORK";
    private static final String SECRET = "SECRET";
    private static final String CURRENTS = "CURRENTS";
    private static final String VISIBILITY = "VISIBILITY";
    private static final String LEVEL = "LEVEL";
    private static final String OBJECT = "OBJECT";


    private static SharedPreferences prefs;

    public static void setCurrents(String value) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(CURRENTS, value);
        editor.commit();
    }

    public static String getCurrents() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(CURRENTS, "");
    }

    public static void setLevel(String value) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(LEVEL, value);
        editor.commit();
    }

    public static String getLevel() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(LEVEL, "");
    }


    public static void setVisibility(String value) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(VISIBILITY, value);
        editor.commit();
    }

    public static String getVisibility() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(VISIBILITY, "");
    }


    public static void setObject(String value) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(OBJECT, value);
        editor.commit();
    }

    public static String getObject() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(OBJECT, "");
    }

    public static void setGcmId(String appInstanceId) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(PREFERENCES_GCM_ID, appInstanceId);
        editor.commit();
    }

    public static void setIsUserSignedIn(Boolean isUserSignedIn) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString("ISLOGINED", "1");
        System.out.println("LOGINED");
        editor.commit();
    }

    public static boolean getIsUserLogined() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        String is = prefs.getString("ISLOGINED", "");
        if (is.equals("1")) { return true; }
        else { return false; }
    }

    public static String getGcmId() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(PREFERENCES_GCM_ID, "");
    }

    public static void setToken(String token) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(TOKEN, token);
        editor.commit();
    }

    public static String getToken() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(TOKEN, "");
    }

    public static void setSn(String sn) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(SN, sn);
        editor.commit();
    }

    public static String getSn() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(SN, "");
    }

    public static void setSecret(String secret) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        Editor editor = prefs.edit();
        editor.putString(SECRET, secret);
        editor.commit();
    }

    public static String getSecret() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(SECRET, "");
    }
}
