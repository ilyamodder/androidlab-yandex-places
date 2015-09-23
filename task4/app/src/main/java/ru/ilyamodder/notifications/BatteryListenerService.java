package ru.ilyamodder.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

public class BatteryListenerService extends Service {

    public static final String ACTION_START_LISTENING
            = "ru.ilyamodder.notifications.ACTION_START_LISTENING";

    private BroadcastReceiver mBroadcastReceiver;

    public BatteryListenerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals(ACTION_START_LISTENING)) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                    if (batteryLevel == 30) {
                        showNotification();
                    }
                }
            };
            registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void showNotification() {

        PendingIntent settingsPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(Intent.ACTION_POWER_USAGE_SUMMARY), 0);

        //https://stackoverflow.com/questions/7040742/android-notification-manager-having-a-notification-without-an-intent
        PendingIntent emptyPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(), 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Батарея разряжена")
                .setContentText("Осталось всего 30%")
                .addAction(R.mipmap.ic_launcher, "Настройки", settingsPendingIntent)
                .addAction(R.mipmap.ic_launcher, "ОК", emptyPendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notifyMgr.notify(0, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
