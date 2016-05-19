package com.ddscanner.services;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.events.InternerConnectionOpenedEvent;
import com.ddscanner.events.InternetConnectionClosedEvent;
import com.ddscanner.ui.activities.InternetClosedActivity;
import com.ddscanner.utils.Helpers;

import java.util.List;

/**
 * Created by lashket on 19.5.16.
 */
public class NetworkLisetener extends BroadcastReceiver {

    private Helpers helpers = new Helpers();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (helpers.hasConnection(context)) {
            DDScannerApplication.bus.post(new InternerConnectionOpenedEvent());
        } else {
         DDScannerApplication.showErrorActivity(context);
        }
    }


}
