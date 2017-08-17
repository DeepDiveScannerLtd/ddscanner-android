package com.ddscanner.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.ui.activities.MainActivity;

import static android.content.ContentValues.TAG;


public class CheckResentRunService extends Service {

    private final static String TAG = "CheckRecentPlay";
    private static Long MILLISECS_PER_DAY = 86400000L;
    private static long delay = MILLISECS_PER_DAY * 10;

    @Override
    public void onCreate() {
        super.onCreate();

        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsNotificationEnabled()) {

            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getLastRunTime() < System.currentTimeMillis() - delay)
                sendNotification();
        }

        setAlarm();

        stopSelf();

    }

    public void setAlarm() {

        Intent serviceIntent = new Intent(this, CheckResentRunService.class);
        PendingIntent pi = PendingIntent.getService(this, 131313, serviceIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pi);
        Log.v(TAG, "Alarm set");

    }

    public void sendNotification() {

        Intent mainIntent = new Intent(this, MainActivity.class);
        Notification noti = new Notification.Builder(this)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, 131314, mainIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentTitle("Deep dive scanner")
                .setContentText("We haven’t seen you for long. Let’s see what you’ve missed!")
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setWhen(System.currentTimeMillis())
                .build();

        NotificationManager notificationManager
                = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(131315, noti);

        Log.v(TAG, "Notification sent");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
