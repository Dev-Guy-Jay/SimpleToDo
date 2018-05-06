package com.dev_guy_jay.simpletodo.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.dev_guy_jay.simpletodo.Notification.ToDoNotification;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

public class ToDoBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        ComponentName componentName = new ComponentName(context.getPackageName(),
                ToDoNotification.class.getName());
        startWakefulService(context, (intent.setComponent(componentName)));
    }
}
