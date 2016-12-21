package com.ddscanner.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.events.InternerConnectionOpenedEvent;
import com.ddscanner.utils.Helpers;

/**
 * Created by lashket on 19.5.16.
 */
public class NetworkLisetener extends BroadcastReceiver {

    private static final String TAG = NetworkLisetener.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
        if (DDScannerApplication.isActivityVisible()) {
            if (Helpers.hasConnection(context)) {
                DDScannerApplication.bus.post(new InternerConnectionOpenedEvent());
            } else {
                DDScannerApplication.showErrorActivity(context);
            }
        }
    }

}
