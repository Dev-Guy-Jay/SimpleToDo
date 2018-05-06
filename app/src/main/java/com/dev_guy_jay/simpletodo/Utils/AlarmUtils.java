package com.dev_guy_jay.simpletodo.Utils;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.dev_guy_jay.simpletodo.BroadcastReceiver.ToDoBroadcastReceiver;
import com.dev_guy_jay.simpletodo.R;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public class AlarmUtils {

    private Context context;
    private final static AtomicInteger ID = new AtomicInteger(0);
    private static AlarmManager manager = null;

    public AlarmUtils(Context context){
        this.context = context.getApplicationContext();
    }

    private PendingIntent pendingIntent;

    public void triggerAlarmManager(Uri todoUri, String titleToSend, String messgaeToSend, int ID, int year, int month, int day, int hourOfDay, int minute) {
        Intent alarmIntent = new Intent(context, ToDoBroadcastReceiver.class);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day,
                hourOfDay, minute, 0);
        alarmIntent.putExtra("title", titleToSend);
        alarmIntent.putExtra("content", messgaeToSend);
        alarmIntent.putExtra("ID", ID);
        alarmIntent.setData(todoUri);
        manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(context, ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 23){
            manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }else{
            manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        Toast.makeText(context, R.string.alarm_is_set , Toast.LENGTH_SHORT).show();
    }

    public void stopAlarmManager(int ID) throws NullPointerException {

        Intent alarmIntent = new Intent(context, ToDoBroadcastReceiver.class);
        manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(context, ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager == null){
            return;
        }else{
            notificationManager.cancel(ID);
        }
        Toast.makeText(context, context.getResources().getString(R.string.alarm_calceled) , Toast.LENGTH_SHORT).show();
    }

    public int getID() {
        return getAlarmId();
    }

    private int getAlarmId() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int name = preferences.getInt("Alarm", 0);
        if(name == 0)
        {
            name = ID.incrementAndGet();
            ID.set(name);
            storeNewID(name);

        }else{
            ID.set(name);
            name = ID.incrementAndGet();
            storeNewID(name);
        }

        return name;
    }

    private void storeNewID(int id){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("ALARM", id).apply();
        editor.apply();
    }
}
