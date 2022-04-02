package com.e.organizer_app;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class TasksAlarmManager {

    public static void setAlarmsIfPossible(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SettingsActivity.SHARED_PREFS, MODE_PRIVATE);

        boolean alarmStartTasks = sharedPreferences.getBoolean(SettingsActivity.START_TASK_NOTIFICATION, false);
        boolean alarmEndTasks = sharedPreferences.getBoolean(SettingsActivity.END_TASK_NOTIFICATION, false);


        setTimerAlarms(context);

        if (alarmStartTasks){
            try {
                setStartAlarms(context);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (alarmEndTasks){
            try {
                setEndAlarms(context);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static  void setTimerAlarms(Context context){
        // DbHelper - SQLiteOpenHelper класс, соединяющий с базой данных SQLite
        DbHelper helper = new DbHelper(context);

        Cursor cursor = helper.getAllTasksBeforeNow();

        List<String> taskNames = new ArrayList<>();
        List<String> taskIDs = new ArrayList<>();
        List<Long> seconds = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                String concrete = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_END_CONCRETE));
                if (concrete.equals("false")){
                    String id = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_ID));
                    taskIDs.add(id);

                    String taskName = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_NAME));
                    taskNames.add(taskName);

                    String startDateString = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_START_DATE_TIME));

                    Date startDate = DateHelper.convertUTCStringToDate(startDateString);
                    seconds.add(startDate.getTime());
                }
            } while(cursor.moveToNext());
        }

        android.app.AlarmManager[] alarmManagers = new android.app.AlarmManager[seconds.size()];

        Intent intents[] = new Intent[alarmManagers.length];

        for(int i=0;i<alarmManagers.length;i++){
            intents[i] = new Intent(context, TimerAlarmReceiver.class);
            intents[i].putExtra("TASK_ID", taskIDs.get(i));
            intents[i].putExtra("TASK_NAME", taskNames.get(i));
            intents[i].putExtra("TITLE", "Начало дела c неточным временем завершения");
            intents[i].putExtra("TYPE", "start");
            intents[i].setAction("START_TIMER_TASK");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,i,intents[i],0);
            alarmManagers[i] = (android.app.AlarmManager)context.getSystemService(ALARM_SERVICE);
            alarmManagers[i].set(android.app.AlarmManager.RTC_WAKEUP, seconds.get(i), pendingIntent);
        }
    }

    public static void setStartAlarms(Context context) throws ParseException {
        // DbHelper - SQLiteOpenHelper класс, соединяющий с базой данных SQLite
        DbHelper helper = new DbHelper(context);

        Cursor cursor = helper.getAllTasksBeforeNow();

        List<String> taskNames = new ArrayList<>();
        List<Long> seconds = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                String concrete = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_END_CONCRETE));
                if (concrete.equals("true")){
                    String taskName = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_NAME));
                    taskNames.add(taskName);

                    String startDateString = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_START_DATE_TIME));

                    Date startDate = DateHelper.convertUTCStringToDate(startDateString);
                    seconds.add(startDate.getTime());
                }
            } while(cursor.moveToNext());
        }

        android.app.AlarmManager[] alarmManagers = new android.app.AlarmManager[seconds.size()];

        Intent intents[] = new Intent[alarmManagers.length];

        for(int i=0;i<alarmManagers.length;i++){
            intents[i] = new Intent(context, AlarmReceiver.class);
            intents[i].putExtra("TASK_NAME", taskNames.get(i));
            intents[i].putExtra("TITLE", "Начало дела");
            intents[i].putExtra("TYPE", "start");
            intents[i].setAction("START_TASK");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,i,intents[i],0);
            alarmManagers[i] = (android.app.AlarmManager)context.getSystemService(ALARM_SERVICE);
            alarmManagers[i].set(android.app.AlarmManager.RTC_WAKEUP, seconds.get(i), pendingIntent);
        }
    }

    public static void setEndAlarms(Context context) throws ParseException {
        // DbHelper - SQLiteOpenHelper класс, соединяющий с базой данных SQLite
        DbHelper helper = new DbHelper(context);
        // Запрос для получения нужных записей из БД через курсор
        Cursor cursor = helper.getAllTasksBeforeNow();

        List<String> taskNames = new ArrayList<>();
        List<Long> seconds = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                String concrete = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_END_CONCRETE));
                if (concrete.equals("true")){
                    String taskName = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_NAME));
                    taskNames.add(taskName);

                    String endDateString = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_END_DATE_TIME));

                    Date startDate = DateHelper.convertUTCStringToDate(endDateString);
                    seconds.add(startDate.getTime());
                }
            } while(cursor.moveToNext());
        }

        android.app.AlarmManager[] alarmManagers = new android.app.AlarmManager[seconds.size()];

        Intent intents[] = new Intent[alarmManagers.length];

        for(int i=0;i<alarmManagers.length;i++){
            intents[i] = new Intent(context, AlarmReceiver.class);
            intents[i].putExtra("TASK_NAME", taskNames.get(i));
            intents[i].putExtra("TITLE", "Окончание дела");
            intents[i].putExtra("TYPE", "end");
            intents[i].setAction("END_TASK");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,i,intents[i],0);
            alarmManagers[i] = (android.app.AlarmManager)context.getSystemService(ALARM_SERVICE);
            alarmManagers[i].set(android.app.AlarmManager.RTC_WAKEUP, seconds.get(i), pendingIntent);
        }
    }
}
