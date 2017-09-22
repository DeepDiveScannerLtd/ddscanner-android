package com.ddscanner.receivers;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ddscanner.ui.activities.MainActivity;
import com.ddscanner.utils.NotificationHelper;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent oututIntent = new Intent(context, MainActivity.class);
        oututIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, NotificationHelper.REQUEST_CODE, oututIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        Notification notification = NotificationHelper.getNotification(context, pendingIntent);
//        NotificationHelper.getNotificationManager(context).notify(NotificationHelper.REQUEST_CODE, notification);
    }
}
