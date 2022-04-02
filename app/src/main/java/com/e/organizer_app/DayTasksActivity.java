package com.e.organizer_app;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.text.ParseException;
import java.util.Date;

public class DayTasksActivity extends AppCompatActivity {

    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_tasks);

        Bundle arguments = getIntent().getExtras();
        String dateString = arguments.get("dateString").toString();

        try {
            loadTasks(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void loadTasks(String dateString) throws ParseException {
        TextView textView = ((TextView) findViewById(R.id.day_name));
        textView.setText(DateHelper.convertUTCDateStringToDay(dateString));

        ListView list = ((ListView) findViewById(R.id.day_tasks_list));

        // DbHelper - наследующий SQLiteOpenHelper класс, соединяющий с базой данных SQLite
        DbHelper helper = new DbHelper(this);

        Cursor cursor = helper.getAllDayTasks(dateString);

        cursorAdapter = new TasksCursorAdapter(this,
                cursor, 0);

        list.setAdapter(cursorAdapter);
    }
}
