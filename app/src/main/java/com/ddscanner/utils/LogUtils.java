package com.ddscanner.utils;

import android.util.Log;

/**
 * Created by unight on 30.04.2015.
 */
public abstract class LogUtils {

    public static boolean IS_DEBUG = true;

    public static void i(String message) {
        if (IS_DEBUG) {
            Log.i("", message);
        }
    }

    public static void i(String tag, String message) {
        if (IS_DEBUG) {
            Log.i(tag, message);
        }
    }

    public static void e(String message) {
        if (IS_DEBUG) {
            Log.e("", message);
        }
    }

    public static void e(String tag, String message) {
        if (IS_DEBUG) {
            Log.e(tag, message);
        }
    }
}
