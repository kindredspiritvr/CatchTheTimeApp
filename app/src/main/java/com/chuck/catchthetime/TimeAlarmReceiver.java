package com.chuck.catchthetime;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

public class TimeAlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "catch_times";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!AlarmScheduler.isEnabled(context)) return;

        String time = intent.getStringExtra(AlarmScheduler.EXTRA_TIME);
        if (time == null) time = "now";

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Catch the time",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for neat-looking clock moments.");
            notificationManager.createNotificationChannel(channel);
        }

        if (Build.VERSION.SDK_INT < 33 ||
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            Intent openApp = new Intent(context, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(
                    context, 1, openApp,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            Notification.Builder builder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    ? new Notification.Builder(context, CHANNEL_ID)
                    : new Notification.Builder(context);

            Notification notification = builder
                    .setSmallIcon(com.chuck.catchthetime.R.drawable.ic_notification)
                    .setContentTitle("Catch the time!")
                    .setContentText(time)
                    .setTicker("Catch the time! (" + time + ")")
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent)
                    .build();

            notificationManager.notify((int) (System.currentTimeMillis() & 0x7fffffff), notification);
        }

        // Chain only the next event rather than keeping dozens of alarms registered.
        AlarmScheduler.scheduleNext(context);
    }
}
