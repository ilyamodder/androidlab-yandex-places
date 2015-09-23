package ru.ilyamodder.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
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
        } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {

                WifiManager wifiManager = (WifiManager) context.getSystemService (Context.WIFI_SERVICE);
                WifiInfo info = wifiManager.getConnectionInfo();
                String ssid = info.getSSID();

                String savedSsid = PreferenceManager.getDefaultSharedPreferences(context).getString("ssid", null);
                if (savedSsid != null && savedSsid.equals(ssid)) {
                    Notification notification = notificationBuilder
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .build();

                    notifyMgr.notify(0, notification);
                }
            }


        }
    }
}
