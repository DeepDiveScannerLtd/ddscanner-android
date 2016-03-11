
package travel.ilave.deepdivescanner.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.Adapter;

import travel.ilave.deepdivescanner.DDScannerApplication;

public class SharedPreferenceHelper {

    private static final String PREFERENCES_GCM_ID = "PREFERENCES_GCM_ID";
    private static final String CITY = "CITY";
    private static final String LICENSE = "LICENSE";

    private static SharedPreferences prefs;

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
        editor.putString("TOKEN", token);
        editor.commit();
    }

    public static String getToken() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString("TOKEN", "");
    }
}
