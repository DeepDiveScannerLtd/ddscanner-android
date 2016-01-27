
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

    public static String getGcmId() {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(PREFERENCES_GCM_ID, "");
    }



    public static String loadPref(String name) {
        prefs = PreferenceManager.getDefaultSharedPreferences(DDScannerApplication.getInstance());
        return prefs.getString(name, "");
    }
}
