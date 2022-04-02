package com.e.organizer_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;


public class AlarmReceiver extends BroadcastReceiver {

    private static String CHANNEL_ID;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String START_TASK_URI = "start_task_uri";
    public static final String END_TASK_URI = "end_task_uri";
    public static final String START_TASK_SOUND = "start_task_sound";
    public static final String END_TASK_SOUND = "end_task_sound";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("TITLE");
        String taskName = intent.getStringExtra("TASK_NAME");
        String type = intent.getStringExtra("TYPE");
        boolean sound_allowed = false;
        String name;

        int notificationID = NotificationID.getID();
        CHANNEL_ID = Integer.toString(notificationID);

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Uri soundUri;
        if (type.equals("start")){
            soundUri = Uri.parse(sharedPreferences.getString(START_TASK_URI, ""));
            sound_allowed = sharedPreferences.getBoolean(START_TASK_SOUND, false);
            name = "Уведомления о начале дела";
        } else{
            soundUri = Uri.parse(sharedPreferences.getString(END_TASK_URI, ""));
            sound_allowed = sharedPreferences.getBoolean(END_TASK_SOUND, false);
            name = "Уведомления о окончании дела";
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(taskName)
                .setChannelId(CHANNEL_ID)
                ;

        if (sound_allowed){
            builder.setGroup("sound");
        } else{
            builder.setGroup("no_sound");
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (sound_allowed){
                builder.setSound(soundUri);
            } else{
                builder.setSound(null);
            }
        }

        Notification notification = builder.build();

        createChannelIfNeeded(notificationManager, soundUri, sound_allowed, name);
        notificationManager.notify(NotificationID.getID(), notification);
    }

    public static void createChannelIfNeeded(NotificationManager manager, Uri uri, boolean sound_allowed, String name){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name,
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (sound_allowed){
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                notificationChannel.setSound(uri, audioAttributes);
            } else {
                notificationChannel.setSound(null, null);
            }
            manager.createNotificationChannel(notificationChannel);
        }
    }
}
