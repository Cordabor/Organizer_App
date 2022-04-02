package com.e.organizer_app;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class EndTaskReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int notificationId = intent.getIntExtra("NOTIFICATION_ID", 0);
        String taskId = intent.getStringExtra("TASK_ID");

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        DbHelper dbHelper = new DbHelper(context);
        dbHelper.updateTaskEndTime(taskId, now);
        dbHelper.moveTasksAfterTaskIfNecessary(taskId);

        // Закрываем оповещение
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
        // Сдвигаем шторку вверх
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);

        // Обновляем список оповещений
        TasksAlarmManager.setAlarmsIfPossible(context);
    }
}
