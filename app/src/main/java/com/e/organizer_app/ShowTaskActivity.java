package com.e.organizer_app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;

import static com.e.organizer_app.DateHelper.convertUTCDateStringToTSWithWeekDay;

public class ShowTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_task);

        try {
            setTaskElements();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setTaskElements() throws ParseException {
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(TasksProvider.CONTENT_ITEM_TYPE);
        String taskFilter = DbHelper.TASK_ID + "=" + uri.getLastPathSegment();

        Cursor cursor = getContentResolver().query(uri,
                DbHelper.ALL_COLUMNS,
                taskFilter,
                null,
                null);

        cursor.moveToFirst();

        String taskName = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_NAME));
        String startDateString = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_START_DATE_TIME));
        String endDateString = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_END_DATE_TIME));

        TextView textViewTaskName = (TextView) findViewById(R.id.textViewTaskName);
        textViewTaskName.setText(taskName);

        TextView textViewStartTime = (TextView) findViewById(R.id.textViewStartTime);
        textViewStartTime.setText("Время начала " + convertUTCDateStringToTSWithWeekDay(startDateString));

        TextView textViewEndTime = (TextView) findViewById(R.id.textViewEndTime);
        textViewEndTime.setText("Время завершения " + convertUTCDateStringToTSWithWeekDay(endDateString));
    }

    public void onClickCancelTaskButton(View v){
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(TasksProvider.CONTENT_ITEM_TYPE);
        String taskFilter = DbHelper.TASK_ID + "=" + uri.getLastPathSegment();
        getContentResolver().delete(TasksProvider.CONTENT_URI,
                taskFilter, null);
        Toast.makeText(this, "Дело отменено", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    public void onClickBackButton(View v){
        finish();
    }


}
