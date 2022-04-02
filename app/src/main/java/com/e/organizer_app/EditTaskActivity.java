package com.e.organizer_app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.view.View;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class EditTaskActivity extends AppCompatActivity {

    private static final int OFFER_TIME_REQUEST_CODE = 1001;
    private TimePicker tp_start;
    private TimePicker tp_end;
    private Spinner start_spinner;
    private Spinner end_spinner;
    private Spinner time_end_spinner;

    private EditText editTaskName;
    private Date dateStart;
    private Date dateEnd;
    private String taskId;
    private String taskName;
    private Boolean concrete;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        setSpinners();
        setTimepickers();
        setElements();
        try {
            setTaskElements();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setElements(){
        editTaskName = (EditText) findViewById(R.id.editTaskName);

    }

    public void setSpinners(){
        // Spinner элементы
        start_spinner = (Spinner) findViewById(R.id.start_spinner);
        end_spinner = (Spinner) findViewById(R.id.end_spinner);
        time_end_spinner = (Spinner) findViewById(R.id.time_end_spinner);
        setDaysSpinner(start_spinner);
        setDaysSpinner(end_spinner);
        setTimeEndSpinner(time_end_spinner);
    }

    public void setTimepickers(){
        // TimePicker элементы задаем 24 часовой формат
        tp_start = (TimePicker) this.findViewById(R.id.tp_start);
        tp_start.setIs24HourView(true);
        tp_end = (TimePicker) this.findViewById(R.id.tp_end);
        tp_end.setIs24HourView(true);
        // Spinner click listener
        // start_spinner.setOnItemSelectedListener(this);
    }

    public void setDaysSpinner(Spinner spinner){
        ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this,
                R.array.days_of_the_week, android.R.layout.simple_spinner_item);

        // стиль для выпадющего списка
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // привязываем адаптер данных к спиннеру
        spinner.setAdapter(dataAdapter);
    }

    public void setTimeEndSpinner(Spinner spinner){
        ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this,
                R.array.time_end, android.R.layout.simple_spinner_item);

        // стиль для выпадющего списка
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // привязываем адаптер данных к спиннеру
        spinner.setAdapter(dataAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setTaskElements() throws ParseException {

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(TasksProvider.CONTENT_ITEM_TYPE);
        String taskFilter = DbHelper.TASK_ID + "=" + uri.getLastPathSegment();
        taskId = uri.getLastPathSegment();

        Cursor cursor = getContentResolver().query(uri,
                DbHelper.ALL_COLUMNS,
                taskFilter,
                null,
                null);

        cursor.moveToFirst();

        String taskName = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_NAME));
        String startDateString = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_START_DATE_TIME));
        String endDateString = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_END_DATE_TIME));
        String concrete = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_END_CONCRETE));


        EditText editTaskName = (EditText) findViewById(R.id.editTaskName);
        editTaskName.setText(taskName);

        dateStart = DateHelper.convertUTCStringToDate(startDateString);
        dateEnd = DateHelper.convertUTCStringToDate(endDateString);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateStart);

        // индекс дня недели
        int startDayIndex = calendar.get(Calendar.DAY_OF_WEEK);

        if (startDayIndex == 1) {
            startDayIndex = 6;
        } else {
            startDayIndex -= 2;
        }

        start_spinner.setSelection(startDayIndex);

        calendar.setTime(dateEnd);
        int endDayIndex = calendar.get(Calendar.DAY_OF_WEEK);

        if (endDayIndex == 1) {
            endDayIndex = 6;
        } else {
            endDayIndex -= 2;
        }

        end_spinner.setSelection(endDayIndex);

        Spinner concrete_spinner = (Spinner) findViewById(R.id.time_end_spinner);
        if (concrete.equals("true")){
            concrete_spinner.setSelection(0);
        } else {
            concrete_spinner.setSelection(1);
        }

        tp_start = (TimePicker) findViewById(R.id.tp_start);
        calendar.setTime(dateStart);
        tp_start.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        tp_start.setMinute(calendar.get(Calendar.MINUTE));

        tp_end = (TimePicker) findViewById(R.id.tp_end);
        calendar.setTime(dateEnd);
        tp_end.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        tp_end.setMinute(calendar.get(Calendar.MINUTE));
    }

    public void updateTask(){
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(TasksProvider.CONTENT_ITEM_TYPE);

        DbHelper dbHelper = new DbHelper(this.getApplicationContext());
        try{
            dbHelper.updateTask(taskName, dateStart, dateEnd, concrete, uri.getLastPathSegment());
            Toast.makeText(this, "Дело изменено", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);

            TasksAlarmManager.setAlarmsIfPossible(getApplicationContext());

            Intent newIntent = new Intent(this, ScheduleActivity.class);
            startActivity(newIntent);
        } catch (Exception e){
            Toast.makeText(this.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void removeTask(){
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(TasksProvider.CONTENT_ITEM_TYPE);
        String taskFilter = DbHelper.TASK_ID + "=" + uri.getLastPathSegment();
        getContentResolver().delete(TasksProvider.CONTENT_URI,
                taskFilter, null);
        Toast.makeText(this, "Задача удалена", Toast.LENGTH_SHORT).show();
    }

    public void onClickButtonBack(View v){
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickButtonChange(View v){
        prepareElementsForUpdate();
        updateTask();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void prepareElementsForUpdate(){
        // Название задачи
        taskName = editTaskName.getText().toString().trim();

        // экземпляр календаря
        Calendar instance = Calendar.getInstance();

        // Получаем из спиннера индекс для первого дня недели
        Spinner startSpinner = (Spinner) findViewById(R.id.start_spinner);
        int startDayIndex = startSpinner.getSelectedItemPosition();

        // индекс текущего дня недели
        int currentDayIndex = instance.get(Calendar.DAY_OF_WEEK);

        if (currentDayIndex == 1) {
            currentDayIndex = 6;
        } else {
            currentDayIndex -= 2;
        }

        if (currentDayIndex > startDayIndex){
            instance.add(Calendar.DAY_OF_MONTH, (7 - currentDayIndex + startDayIndex));
        } else {
            instance.add(Calendar.DAY_OF_MONTH, (startDayIndex - currentDayIndex));
        }

        TimePicker tp_start = (TimePicker) findViewById(R.id.tp_start);

        instance.set(Calendar.HOUR_OF_DAY, tp_start.getHour());
        instance.set(Calendar.MINUTE,  tp_start.getMinute());
        instance.set(Calendar.SECOND, 0);

        dateStart = instance.getTime();

        instance = Calendar.getInstance();

        // Получаем из спиннера индекс для второго дня недели
        Spinner endSpinner = (Spinner) findViewById(R.id.end_spinner);
        int endDayIndex = endSpinner.getSelectedItemPosition();

        // индекс текущего дня недели
        currentDayIndex = instance.get(Calendar.DAY_OF_WEEK);

        if (currentDayIndex == 1) {
            currentDayIndex = 6;
        } else {
            currentDayIndex -= 2;
        }

        if (currentDayIndex > endDayIndex){
            instance.add(Calendar.DAY_OF_MONTH, (7 - currentDayIndex + endDayIndex));
        } else {
            instance.add(Calendar.DAY_OF_MONTH, (endDayIndex - currentDayIndex));
        }

        TimePicker tp_end = (TimePicker) findViewById(R.id.tp_end);

        instance.set(Calendar.HOUR_OF_DAY, tp_end.getHour());
        instance.set(Calendar.MINUTE,  tp_end.getMinute());
        instance.set(Calendar.SECOND, 0);

        dateEnd = instance.getTime();

        // точное время завершения по умолчанию
        concrete = true;
        // заменяем на неточное в зависимости от значения спиннера
        Spinner concreteSpinner = (Spinner) findViewById(R.id.time_end_spinner);
        int concreteIndex = concreteSpinner.getSelectedItemPosition();
        if (concreteIndex != 0){
            concrete = false;
        }
    }

    public void onClickButtonRemove(View v){
        removeTask();
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }

    public void onClickOfferFreeTime(View v) {
        Intent intent = new Intent(this, FreeTimeActivity.class);
        startActivityForResult(intent, OFFER_TIME_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickPredictedTime(View v){
        String taskName = editTaskName.getText().toString();
//        Intent intent = getIntent();
//        Uri uri = intent.getParcelableExtra(TasksProvider.CONTENT_ITEM_TYPE);
//        String taskId = uri.getLastPathSegment();

        DbHelper dbHelper = new DbHelper(getApplicationContext());

        long time = dbHelper.getPredictedTimeForTask(taskName, taskId);

        dateEnd = DateUtil.addMilliseconds(dateStart, time);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateEnd);

        int dayEndIndex = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayEndIndex == 1) {
            dayEndIndex = 6;
        } else {
            dayEndIndex -= 2;
        }

        end_spinner.setSelection(dayEndIndex);

        tp_end.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        tp_end.setMinute(calendar.get(Calendar.MINUTE));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        String start = data.getStringExtra("start");
        String end = data.getStringExtra("end");

        Date startDate = DateHelper.convertUTCStringToDate(start);
        Date endDate = DateHelper.convertUTCStringToDate(end);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        tp_start.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        tp_start.setMinute(calendar.get(Calendar.MINUTE));
        // индекс дня недели
        int startDayIndex = calendar.get(Calendar.DAY_OF_WEEK);

        if (startDayIndex == 1) {
            startDayIndex = 6;
        } else {
            startDayIndex -= 2;
        }
        start_spinner.setSelection(startDayIndex);

        calendar.setTime(endDate);
        tp_end.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        tp_end.setMinute(calendar.get(Calendar.MINUTE));
        int endDayIndex = calendar.get(Calendar.DAY_OF_WEEK);

        if (endDayIndex == 1) {
            endDayIndex = 6;
        } else {
            endDayIndex -= 2;
        }
        end_spinner.setSelection(endDayIndex);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickButtonChangeWithMove(View v){
        prepareElementsForUpdate();
        DbHelper dbHelper = new DbHelper(getApplicationContext());
        dbHelper.updateTaskWithoutConditions(taskName, dateStart, dateEnd, concrete, taskId);
        dbHelper.moveTasksAfterTaskIfNecessary(taskId);
        Toast.makeText(this, "Дело изменено", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);

        TasksAlarmManager.setAlarmsIfPossible(getApplicationContext());

        Intent newIntent = new Intent(this, ScheduleActivity.class);
        startActivity(newIntent);
    }
}
