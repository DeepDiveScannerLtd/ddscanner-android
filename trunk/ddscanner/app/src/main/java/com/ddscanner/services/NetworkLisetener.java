package com.ddscanner.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.events.InternerConnectionOpenedEvent;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;

/**
 * Created by lashket on 19.5.16.
 */
public class NetworkLisetener extends BroadcastReceiver {

    private static final String TAG = NetworkLisetener.class.getName();

    private Helpers helpers = new Helpers();

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i(TAG, "onReceive");
        if (DDScannerApplication.isActivityVisible()) {
            if (helpers.hasConnection(context)) {
                DDScannerApplication.bus.post(new InternerConnectionOpenedEvent());
            } else {
                DDScannerApplication.showErrorActivity(context);
            }
        }
    }

}
