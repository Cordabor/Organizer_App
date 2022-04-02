package com.e.organizer_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class DbHelper extends SQLiteOpenHelper {

    //Constants for db name and version
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;


    //Constants for identifying table and columns
    public static final String TABLE_TASKS = "tasks";
    public static final String TASK_ID = "_id";
    public static final String TASK_NAME = "task";
    public static final String TASK_START_DATE_TIME = "taskStartDateTime";
    public static final String TASK_END_DATE_TIME = "taskEndDateTime";
    public static final String TASK_END_CONCRETE = "taskEndConcrete";

    //SQL to create table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_TASKS + " (" +
                    TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TASK_NAME + " TEXT, " +
                    TASK_START_DATE_TIME + " TEXT, " +
                    TASK_END_DATE_TIME + " TEXT, " +
                    TASK_END_CONCRETE + " TEXT " +
                    ") ";

    public static final String[] ALL_COLUMNS = {TASK_ID, TASK_NAME, TASK_START_DATE_TIME, TASK_END_DATE_TIME, TASK_END_CONCRETE};

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    // DbHelper - наследующий SQLiteOpenHelper класс, соединяющий с базой данных SQLite

    public Cursor getAllTasks(){
        // Получение доступа для работы с базой данных
        SQLiteDatabase db = this.getWritableDatabase();
        // Запрос для получения нужных записей из БД (возвращает объект класса курсор)
        return db.rawQuery("SELECT * FROM tasks ORDER BY taskStartDateTime", null);
    }

    public Cursor getAllTasksByDate(Date date){
        // Получение доступа для работы с базой данных
        SQLiteDatabase db = this.getWritableDatabase();
        // Запрос для получения нужных записей из БД (возвращает объект класса курсор)
        return db.rawQuery("SELECT * FROM tasks WHERE date(taskStartDateTime) = " +
                "date('" + DateHelper.convertToUTCDateString(date) + "') OR date(taskEndDateTime) = " +
                "date('" + DateHelper.convertToUTCDateString(date) + "') " +
                " ORDER BY taskStartDateTime", null);
    }

    public Cursor getAllDayTasks(String dateString) throws ParseException {
        // Получение доступа для работы с базой данных
        SQLiteDatabase db = this.getWritableDatabase();

        Date datePlusDay = DateHelper.convertUTCStringToDate(dateString);
        datePlusDay = DateUtil.addDays(datePlusDay, 1);
        datePlusDay = DateUtil.addMinutes(datePlusDay, -1);
        String datePlusDayString = DateHelper.convertToUTCDateString(datePlusDay);

        return db.rawQuery("SELECT * FROM tasks WHERE date(taskStartDateTime) BETWEEN " +
                "date('" + dateString + "') AND date('" + datePlusDayString + "') " +
                " ORDER BY taskStartDateTime", null);
    }

    public Cursor getAllTasksBeforeNow() {
        // Получение доступа для работы с базой данных
        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery("SELECT * FROM tasks WHERE date('now') < date(taskStartDateTime)" +
                " OR (date('now') = date(taskStartDateTime) " +
                "AND time('now') <= time(taskStartDateTime))", null);
    }

    public void insertTask(String taskName, Date startDateTime, Date endDateTime, boolean concrete) throws Exception {
        if (startDateTime.after(endDateTime)){
            throw new Exception("Дата и время начала дела должны быть меньше чем дата и время окончания дела");
        }
        else {
            SQLiteDatabase db = this.getWritableDatabase();

            String startDateTimeSting = DateHelper.convertToUTCDateString(startDateTime);
            String endDateTimeSting = DateHelper.convertToUTCDateString(endDateTime);

            Cursor cursor = findTasksInInterval(startDateTimeSting, endDateTimeSting);
            int count = cursor.getCount();

            if (count != 0){
                throw new Exception("Этот интервал времени уже занят! Создать дело с этим временем невозможно!");
            } else {
                ContentValues values = new ContentValues();
                values.put(TASK_NAME, taskName);
                values.put(TASK_START_DATE_TIME, DateHelper.convertToUTCDateString(startDateTime));
                values.put(TASK_END_DATE_TIME, DateHelper.convertToUTCDateString(endDateTime));
                values.put(TASK_END_CONCRETE, Boolean.toString(concrete));

                db.insert(TABLE_TASKS, null, values);
            }
        }
    }

    public void updateTaskWithoutConditions(String taskName, Date startDateTime, Date endDateTime, boolean concrete, String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String taskFilter = TASK_ID + "=" + id;

        ContentValues values = new ContentValues();
        values.put(TASK_NAME, taskName);
        values.put(TASK_START_DATE_TIME, DateHelper.convertToUTCDateString(startDateTime));
        values.put(TASK_END_DATE_TIME, DateHelper.convertToUTCDateString(endDateTime));
        values.put(TASK_END_CONCRETE, Boolean.toString(concrete));

        db.update(TABLE_TASKS, values, taskFilter, null);
    }

    public void updateTask(String taskName, Date startDateTime, Date endDateTime, boolean concrete, String id) throws Exception {
        if (startDateTime.after(endDateTime)){
            throw new Exception("Дата и время начала дела должны быть меньше чем дата и время окончания дела");
        } else {
            SQLiteDatabase db = this.getWritableDatabase();

            String taskFilter = TASK_ID + "=" + id;

            String startDateTimeSting = DateHelper.convertToUTCDateString(startDateTime);
            String endDateTimeSting = DateHelper.convertToUTCDateString(endDateTime);

            Cursor cursor = db.rawQuery("SELECT * FROM tasks WHERE _id != '" + id +"' AND " +
                    "(((datetime('" + startDateTimeSting + "') BETWEEN  datetime(taskStartDateTime) " +
                    "AND datetime(taskEndDateTime)) OR (datetime('" + endDateTimeSting +"')" +
                    " BETWEEN  datetime(taskStartDateTime) AND datetime(taskEndDateTime)))" +
                    "OR ((datetime(taskStartDateTime) BETWEEN  datetime('" + startDateTimeSting +"') " +
                    "AND datetime('" + endDateTimeSting + "')) OR (datetime(taskEndDateTime) " +
                    "BETWEEN  datetime('" + startDateTimeSting +"') " +
                    "AND datetime('" + endDateTimeSting + "'))))", null);

            int count = cursor.getCount();

            if (count != 0){
                throw new Exception("Этот интервал времени уже занят! Создать дело с этим временем невозможно!");
            } else{
                ContentValues values = new ContentValues();
                values.put(TASK_NAME, taskName);
                values.put(TASK_START_DATE_TIME, DateHelper.convertToUTCDateString(startDateTime));
                values.put(TASK_END_DATE_TIME, DateHelper.convertToUTCDateString(endDateTime));
                values.put(TASK_END_CONCRETE, Boolean.toString(concrete));

                db.update(TABLE_TASKS, values, taskFilter, null);
            }
        }
    }

    private Cursor findTasksInInterval(String startDateTimeSting, String endDateTimeSting){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery("SELECT * FROM tasks WHERE ((datetime('" + startDateTimeSting + "') BETWEEN " +
                " datetime(taskStartDateTime) AND datetime(taskEndDateTime)) OR (datetime('" + endDateTimeSting +"') " +
                "BETWEEN  datetime(taskStartDateTime) AND datetime(taskEndDateTime)))" +
                "OR ((datetime(taskStartDateTime) BETWEEN  datetime('" + startDateTimeSting +"') " +
                "AND datetime('" + endDateTimeSting + "')) OR (datetime(taskEndDateTime) " +
                "BETWEEN  datetime('" + startDateTimeSting +"') " +
                "AND datetime('" + endDateTimeSting + "')))", null);
    }

    private Cursor findTasksInIntervalWithException(String startDateTimeSting, String endDateTimeSting, String taskID){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM tasks WHERE _id != '" + taskID +"' AND " +
                "(((datetime('" + startDateTimeSting + "') BETWEEN  datetime(taskStartDateTime) " +
                "AND datetime(taskEndDateTime)) OR (datetime('" + endDateTimeSting +"')" +
                " BETWEEN  datetime(taskStartDateTime) AND datetime(taskEndDateTime)))" +
                "OR ((datetime(taskStartDateTime) BETWEEN  datetime('" + startDateTimeSting +"') " +
                "AND datetime('" + endDateTimeSting + "')) OR (datetime(taskEndDateTime) " +
                "BETWEEN  datetime('" + startDateTimeSting +"') " +
                "AND datetime('" + endDateTimeSting + "'))))", null);
    }

    public String findTimeForTask(int hours, int minutes){
        Calendar instance = Calendar.getInstance();

        Date startDate = instance.getTime();
        startDate = DateUtil.addMinutes(startDate, 1);

        Date endDate = startDate;

        endDate = DateUtil.addHours(endDate, hours);
        endDate = DateUtil.addMinutes(endDate, minutes++);

        String startDateTimeSting = DateHelper.convertToUTCDateString(startDate);
        String endDateTimeSting = DateHelper.convertToUTCDateString(endDate);

        Cursor cursor = findTasksInInterval(startDateTimeSting, endDateTimeSting);

        int count = cursor.getCount();

        if (count == 0){
            return startDateTimeSting + " - " + endDateTimeSting;
        } else {
            if(cursor.moveToFirst()){
                do{
                    String tempStartDateString = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_END_DATE_TIME));

                    Date tempStartDate = DateHelper.convertUTCStringToDate(tempStartDateString);
                    tempStartDate = DateUtil.addMinutes(tempStartDate, 1);

                    Date tempEndDate = tempStartDate;
                    tempEndDate = DateUtil.addHours(tempEndDate, hours);
                    tempEndDate = DateUtil.addMinutes(tempEndDate, minutes);

                    tempStartDateString = DateHelper.convertToUTCDateString(tempStartDate);
                    String tempEndDateString = DateHelper.convertToUTCDateString(tempEndDate);

                    cursor = findTasksInInterval(tempStartDateString, tempEndDateString);

                    count = cursor.getCount();

                    if (count == 0){
                        return tempStartDateString;
                    }
                }while(cursor.moveToNext());
            }
            return "Время не найдено";
        }

    }

    public void updateTaskEndTime(String taskId, Date newEndDateTime){
        SQLiteDatabase db = this.getWritableDatabase();

        String taskFilter = TASK_ID + "=" + taskId;

        ContentValues values = new ContentValues();
        values.put(TASK_END_DATE_TIME, DateHelper.convertToUTCDateString(newEndDateTime));
        db.update(TABLE_TASKS, values, taskFilter, null);
    }

    public void moveTasksAfterTaskIfNecessary(String taskId){
        String taskID = TASK_ID + "=" + taskId;

        SQLiteDatabase db = this.getWritableDatabase();

        String startDateTimeString = null;
        String endDateTimeString = null;

        Cursor cursor = db.rawQuery("SELECT * FROM tasks WHERE _id = '" + taskId + "'", null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                startDateTimeString = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_START_DATE_TIME));
                endDateTimeString = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_END_DATE_TIME));
                cursor.moveToNext();
            }
            cursor.close();
        }

        Date startDateTime = DateHelper.convertUTCStringToDate(startDateTimeString);
        Date endDateTime = DateHelper.convertUTCStringToDate(endDateTimeString);

        cursor = findTasksInIntervalWithException(startDateTimeString, endDateTimeString, taskId);
        if (cursor.getCount() == 0){
            return;
        }

        if(cursor.moveToFirst()){
            do{
                String tempTaskId = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_ID));
                String tempTaskFilter = TASK_ID + "=" + tempTaskId;

                String tempStartDateTimeString = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_START_DATE_TIME));
                String tempEndDateTimeString = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_END_DATE_TIME));

                Date tempStartDate = DateHelper.convertUTCStringToDate(tempStartDateTimeString);
                Date tempEndDate = DateHelper.convertUTCStringToDate(tempEndDateTimeString);

                long diffInMilliseconds = Math.abs(tempEndDate.getTime() - tempStartDate.getTime());

                startDateTime = DateUtil.addMinutes(endDateTime, 1);
                endDateTime = DateUtil.addMilliseconds(startDateTime, diffInMilliseconds);

                startDateTimeString = DateHelper.convertToUTCDateString(startDateTime);
                endDateTimeString = DateHelper.convertToUTCDateString(endDateTime);

                ContentValues values = new ContentValues();
                values.put(TASK_START_DATE_TIME, startDateTimeString);
                values.put(TASK_END_DATE_TIME, endDateTimeString);
                db.update(TABLE_TASKS, values, tempTaskFilter, null);

                cursor = findTasksInIntervalWithException(startDateTimeString, endDateTimeString, tempTaskId);
                if (cursor.getCount() == 0){
                    return;
                }
            }while(cursor.moveToNext());
        }
    }

    public Cursor findAllTasksWithNameExceptTask(String taskName, String taskId){
        int a = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM tasks WHERE" +
                " _id!= '" + taskId + "' AND task = '" + taskName + "'", null);
    }

    public long getPredictedTimeForTask(String taskName, String taskId){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tasks WHERE " +
                "_id!= '" + taskId + "' AND task = '" + taskName + "'", null);

        long time_sum = 0;
        int count = cursor.getCount();

        if(cursor.moveToFirst()){
            do{
                String tempStartDateTimeString = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_START_DATE_TIME));
                String tempEndDateTimeString = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_END_DATE_TIME));

                Date tempStartDate = DateHelper.convertUTCStringToDate(tempStartDateTimeString);
                Date tempEndDate = DateHelper.convertUTCStringToDate(tempEndDateTimeString);

                long diffInMilliseconds = Math.abs(tempEndDate.getTime() - tempStartDate.getTime());

                time_sum += diffInMilliseconds;

            }while(cursor.moveToNext());
        }
        long result = 0;
        if (count > 0){
            result = (long)(time_sum / cursor.getCount());
        }
        return result;
    }
}
