package ru.ilyamodder.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private ListView mLvMain;

    private NotificationManager mNotifyMgr;

    private AlarmManager mAlarmMgr;

    public static final int ITEM_SHOW_NOW = 0,
                            ITEM_SHOW_AT = 1,
                            ITEM_SHOW_ON_WIFI = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        mLvMain = (ListView) v.findViewById(R.id.lvMain);
        mLvMain.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
                                    getResources().getStringArray(R.array.main_list)));

        mNotifyMgr =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mAlarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        mLvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case ITEM_SHOW_NOW:
                        showNow();
                        break;
                    case ITEM_SHOW_AT:
                        showAt();
                        break;
                    case ITEM_SHOW_ON_WIFI:
                        showOnWifi();
                        break;
                }
            }
        });

        Intent intent = new Intent(getActivity(), BatteryListenerService.class);
        intent.setAction(BatteryListenerService.ACTION_START_LISTENING);

        getActivity().startService(intent);

        return v;
    }

    private void showNow() {
        Notification notification = new NotificationCompat.Builder(getActivity())
                .setContentTitle(getActivity().getString(R.string.main_notification_title))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        mNotifyMgr.notify(0, notification);
    }

    private void showAt() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(getActivity(), NotificationReceiver.class);
        intent.setAction(NotificationReceiver.ACTION_ALARM_TICK);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0,
                intent, 0);

        if (Build.VERSION.SDK_INT >= 19) {
            mAlarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            mAlarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        Toast.makeText(getActivity(), "Уведоиление запланировано", Toast.LENGTH_SHORT).show();

    }

    private void showOnWifi() {

    }

    private void showAfterOneMinute() {
        Intent intent = new Intent(getActivity(), NotificationReceiver.class);
        intent.setAction(NotificationReceiver.ACTION_ALARM_TICK);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0,
                intent, 0);

        long time = System.currentTimeMillis() + 1000*60;

        if (Build.VERSION.SDK_INT >= 19) {
            mAlarmMgr.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else {
            mAlarmMgr.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }

        Toast.makeText(getActivity(), "Уведоиление запланировано", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showAfterOneMinute();
    }
}
