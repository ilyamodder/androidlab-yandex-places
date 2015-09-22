package ru.ilyamodder.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_ALARM_TICK = "ru.ilyamodder.notification.ALARM_TICK";

    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.main_notification_title))
                .setSmallIcon(R.mipmap.ic_launcher);
        NotificationManager notifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (intent.getAction().equals(ACTION_ALARM_TICK)) {
            Notification notification = notificationBuilder
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();

            notifyMgr.notify(0, notification);
        } else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Notification notification = notificationBuilder
                    .setOngoing(true)
                    .build();
            notifyMgr.notify(0, notification);
        }
    }
}
