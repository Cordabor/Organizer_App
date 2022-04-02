package com.e.organizer_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;


public class TimerAlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "CHANNEL_ID";

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        int notificationID = NotificationID.getID();

        String taskID = intent.getStringExtra("TASK_ID");
        String title = intent.getStringExtra("TITLE");
        String taskName = intent.getStringExtra("TASK_NAME");

        //This is the intent of PendingIntent
        Intent intentAction = new Intent(context, EndTaskReceiver.class);

        //This is optional if you have more than one buttons and want to differentiate between two
        intentAction.putExtra("action","actionName");
        intentAction.putExtra("NOTIFICATION_ID", notificationID);
        intentAction.putExtra("TASK_ID", taskID);


        PendingIntent pIntent = PendingIntent.getBroadcast(context,1,intentAction, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID")
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Времени прошло")
                .setWhen(System.currentTimeMillis())
                .setUsesChronometer(true)
                .setContentTitle(title)
                .setContentText(taskName)
                .addAction(R.drawable.ic_schedule_black_24dp, "Завершить дело", pIntent)
                ;

        Notification notification = builder.build();

        String name = "Уведомления о задачах с неточным временем выполнения";
        createChannelIfNeeded(notificationManager, name);
        notificationManager.notify(notificationID, notification);
    }

    public static void createChannelIfNeeded(NotificationManager manager, String name){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name,
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }
}
