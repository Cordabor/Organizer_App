package com.e.organizer_app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static com.e.organizer_app.DateHelper.convertToUTCDateString;
import static com.e.organizer_app.DateHelper.convertUTCStringToDate;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        try {
            setDays();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.nav_calendar);

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
                        return true;
                    case R.id.nav_schedule:
                        startActivity(new Intent(getApplicationContext()
                                , ScheduleActivity.class));
                        overridePendingTransition(0,0);
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

    public void dateSelected(String dateString){
        Intent intent = new Intent(this, DayTasksActivity.class);
        // передача объекта с ключом "dateString" и значением dateString
        intent.putExtra("dateString", dateString);
        // запуск Activity
        startActivity(intent);
    }

    public void setDays() throws ParseException {

        MaterialCalendarView materialCalendarView = findViewById(R.id.calendarView);

        materialCalendarView.setTitleMonths(R.array.material_calendar_months_array);

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, date.getYear());
                cal.set(Calendar.MONTH, date.getMonth() - 1);
                cal.set(Calendar.DAY_OF_MONTH, date.getDay());
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date dateRepresentation = cal.getTime();

                String dateNew = convertToUTCDateString(dateRepresentation);
                dateSelected(dateNew);
            }
        });

        // DbHelper - SQLiteOpenHelper класс, соединяющий с базой данных SQLite
        DbHelper helper = new DbHelper(this);
        Cursor cursor = helper.getAllTasks();

        Calendar instance = Calendar.getInstance();
        if(cursor.moveToFirst()){
            do{
                String startDateString = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_START_DATE_TIME));
                String endDateString = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_END_DATE_TIME));

                Date startDate = convertUTCStringToDate(startDateString);
                Date endDate = convertUTCStringToDate(endDateString);

                instance.setTime(startDate);

                int monthNum = instance.MONTH;

                materialCalendarView.setDateSelected(CalendarDay.from(instance.get(Calendar.YEAR),
                        instance.get(Calendar.MONTH) + 1, instance.get(Calendar.DAY_OF_MONTH)), true);

            } while(cursor.moveToNext());
        }
    }
}
