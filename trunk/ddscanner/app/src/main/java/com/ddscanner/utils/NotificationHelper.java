package com.ddscanner.utils;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.receivers.NotificationReceiver;
import com.ddscanner.ui.activities.MainActivity;

public class NotificationHelper {

    public static int REQUEST_CODE = 1;

    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static void setupAlarmManager(Context context) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, DDScannerApplication.getInstance().getSharedPreferenceHelper().getLastRunTime(), AlarmManager.INTERVAL_DAY * 10, pendingIntent);
    }

    public static Notification getNotification(Context context, PendingIntent pendingIntent) {
        Notification noti = new Notification.Builder(context)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentTitle("Deep dive scanner")
                .setContentText("We haven’t seen you for long. Let’s see what you’ve missed!")
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setWhen(System.currentTimeMillis())
                .build();
        return noti;
    }

}
