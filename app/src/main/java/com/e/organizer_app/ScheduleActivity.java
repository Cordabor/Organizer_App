package com.e.organizer_app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScheduleActivity extends AppCompatActivity{

    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        setWeekDaysDates();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.nav_schedule);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_week_tasks:
                        startActivity(new Intent(getApplicationContext()
                        , WeekTasksActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_calendar:
                        startActivity(new Intent(getApplicationContext()
                                , CalendarActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_schedule:
                        return true;
                    case R.id.nav_settings:
                        startActivity(new Intent(getApplicationContext()
                                , SettingsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    public void setWeekDaysDates(){
        // экземпляр календаря
        Calendar instance = Calendar.getInstance();
        // Текущий день недели
        int dayOfWeek = instance.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
            dayOfWeek = 6;
        } else {
            dayOfWeek -= 2;
        }

        for(int i = dayOfWeek; i < 7; i++){
            instance = setDayDateTextView(instance, i);
        }

        for (int i = 0; i < dayOfWeek; i++){
            instance = setDayDateTextView(instance, i);
        }
    }

    // метод для загрузки задач за определенный день
    private void loadDayTasks(int dayNum, Date date){
        // Получаем строковое id для списка с делами для дня
        String listViewId = "tasks_list_" + dayNum;
        // Поулачем id для списка дел дня
        int daylistViewId = getResources().getIdentifier(listViewId, "id", getPackageName());
        // Получаем списковое представление для дня для размещения задач
        ListView list = ((ListView) findViewById(daylistViewId));

        list.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Запретить ScrollView перехватывать ListView события.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Разрешить ScrollView перехватывать ListView события.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Обработка событий onTouch ListView.
                v.onTouchEvent(event);
                return true;
            }
        });

        // DbHelper - наследующий SQLiteOpenHelper класс, соединяющий с базой данных SQLite
        DbHelper helper = new DbHelper(this);
        Cursor cursor = helper.getAllTasksByDate(date);

        cursorAdapter = new TasksCursorAdapter(this,
                cursor, 0);

        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Uri uri = Uri.parse(TasksProvider.CONTENT_URI + "/" + id);
                startEditTaskActivity(id);
            }
        });
    }

    private Calendar setDayDateTextView(Calendar instance, int index){
        String textViewId;
        int dayNowDateTextId;
        TextView textView;
        // Паттерн для вывода даты
        String pattern = "dd.MM.yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        // Получаем строковое id для строки с датой текущего дня
        textViewId = "date_" + index;
        // Поулачем id для строки с датой текущего дня
        dayNowDateTextId = getResources().getIdentifier(textViewId, "id", getPackageName());
        // Получаем текстовое представление для даты
        textView = ((TextView) findViewById(dayNowDateTextId));
        // Получаем текущую дату
        Date date = instance.getTime();
        // Выводим текущую дату
        textView.setText(df.format(date));

        loadDayTasks(index, date);
        // Увеличиваем дату на 1 день
        instance.add(Calendar.DAY_OF_MONTH, 1);

        return instance;
    }


    private void loadTasksList(){
        // DbHelper - наследующий SQLiteOpenHelper класс, соединяющий с базой данных SQLite
        DbHelper helper = new DbHelper(this);
        Cursor cursor = helper.getAllTasks();

        cursorAdapter = new TasksCursorAdapter(this,
                cursor, 0);

        ListView list = findViewById(R.id.tasks_list_0);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Uri uri = Uri.parse(TasksProvider.CONTENT_URI + "/" + id);
                startEditTaskActivity(id);
            }
        });
    }

    public void startEditTaskActivity(long id){
        Intent intent = new Intent(this, EditTaskActivity.class);

        Uri uri = Uri.parse(TasksProvider.CONTENT_URI + "/" + id);
        intent.putExtra(TasksProvider.CONTENT_ITEM_TYPE, uri);
        startActivity(intent);
    }

    public void onClickAddButton(View v){
        Intent intent = new Intent(this, TaskActivity.class);
        startActivity(intent);
    }

}
