package com.e.organizer_app;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.RequiresApi;

import java.sql.Time;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class TaskActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private static final int OFFER_TIME_REQUEST_CODE = 1001;
    private TimePicker tp_start;
    private TimePicker tp_end;
    private Spinner start_spinner;
    private Spinner end_spinner;
    private Spinner time_end_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        setSpinners();
        setTimepickers();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void onClickOfferFreeTime(View v) {
        Intent intent = new Intent(this, FreeTimeActivity.class);
        startActivityForResult(intent, OFFER_TIME_REQUEST_CODE);
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
    public void onClickCreateButton(View v){
        // Название задачи
        EditText editTaskName = (EditText) findViewById(R.id.editTaskName);
        String taskName = editTaskName.getText().toString().trim();

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

        Date dateStart = instance.getTime();

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

        Date dateEnd = instance.getTime();

        Toast.makeText(this, DateHelper.convertToUTCDateString(dateEnd), Toast.LENGTH_SHORT).show();

        // точное время завершения по умолчанию
        boolean concrete = true;
        // заменяем на неточное в зависимости от значения спиннера
        Spinner concreteSpinner = (Spinner) findViewById(R.id.time_end_spinner);
        int concreteIndex = concreteSpinner.getSelectedItemPosition();
        if (concreteIndex != 0){
            concrete = false;
        }

        insertTask(taskName, dateStart, dateEnd, concrete);

        TasksAlarmManager.setAlarmsIfPossible(getApplicationContext());
    }


    public void insertTask(String taskName, Date startDateTime, Date endDateTime, boolean concrete) {
        DbHelper dbHelper = new DbHelper(this.getApplicationContext());
        try{
            dbHelper.insertTask(taskName, startDateTime, endDateTime, concrete);
            Toast.makeText(this, "Дело добавлено", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            Intent intent = new Intent(this, ScheduleActivity.class);
            startActivity(intent);
        } catch (Exception e){
            Toast.makeText(this.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onClickCancelButton(View v){
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }
}
