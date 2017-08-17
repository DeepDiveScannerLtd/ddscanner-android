package com.ddscanner.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ddscanner.services.CheckResentRunService;

public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, CheckResentRunService.class);
            context.startService(pushIntent);
        }
    }
}
