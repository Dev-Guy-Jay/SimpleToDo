package com.dev_guy_jay.simpletodo.Notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import com.dev_guy_jay.simpletodo.Activity.DetailsActivity;
import com.dev_guy_jay.simpletodo.Activity.MainActivity;
import com.dev_guy_jay.simpletodo.R;

public class ToDoNotification extends IntentService {

    public ToDoNotification() {
        super("ToDoNotification");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        Bundle extra = intent.getExtras();
        String title = extra.getString("title");
        Uri todoUri = intent.getData();
        int ID = extra.getInt("ID");
        try {
            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), defaultSoundUri);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendNotification(todoUri, title, ID);
    }

    private void sendNotification(Uri todoUri, String title, int ID) {
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent =  new Intent(context, DetailsActivity.class);
        intent.setData(todoUri);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String CHANNEL_ID = String.valueOf(ID);
        CharSequence name = "todo_channel";
        String Description = "Todo_Notficaition_channel";
        Notification.Builder builder;


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);

            builder = new Notification.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.border)
                    .setContentTitle(title)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true);
        }else{
            builder = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.border)
                    .setContentTitle(title)
                    .setContentIntent(contentIntent)
                    .setSound(null)
                    .setAutoCancel(true);
        }
        notificationManager.notify(ID, builder.build());
    }

}
