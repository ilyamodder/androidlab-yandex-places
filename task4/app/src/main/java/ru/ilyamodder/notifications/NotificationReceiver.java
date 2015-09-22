package ru.ilyamodder.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_ALARM_TICK = "ru.ilyamodder.notification.ALARM_TICK";

    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_ALARM_TICK)) {
            Notification notification = new NotificationCompat.Builder(context)
                    .setContentTitle(context.getString(R.string.main_notification_title))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();
            NotificationManager notifyMgr =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notifyMgr.notify(0, notification);
        }
    }
}
