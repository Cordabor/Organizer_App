package com.e.organizer_app;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static com.e.organizer_app.DateHelper.convertUTCDateStringToTimeString;

public class TasksCursorAdapter extends CursorAdapter {

    public TasksCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Чтение и и инфлатинг макета для table_task_item и передача его обратно всякий раз, когда newView
        // метод вызывается
        return LayoutInflater.from(context).inflate(
                R.layout.table_task_item,
                parent,
                false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // когда мы связываем представление, мы получаем экземпляр объекта курсора, который будет указывать на
        // конкретную строку нашей базы данных, которая должна отображаться
        int position = cursor.getPosition();

        String taskName = cursor.getString(
                cursor.getColumnIndex(DbHelper.TASK_NAME)
        );

        String startStr = cursor.getString(
                cursor.getColumnIndex(DbHelper.TASK_START_DATE_TIME)
        );

        String endStr = cursor.getString(
                cursor.getColumnIndex(DbHelper.TASK_END_DATE_TIME)
        );

        Date dateStart = DateHelper.convertUTCStringToDate(startStr);
        Date dateEnd = DateHelper.convertUTCStringToDate(endStr);

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        if (now.after(dateEnd)){
            TextView tvCompleted = view.findViewById(R.id.tvCompleted);
            tvCompleted.setText("Выполнено");
        }

        calendar.setTime(dateStart);
        int day_start = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTime(dateEnd);
        int day_end = calendar.get(Calendar.DAY_OF_MONTH);

        if (day_start != day_end){
            Calendar cal = Calendar.getInstance();
            if (position == 0){
                cal.setTime(dateStart);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                dateStart = cal.getTime();
                startStr = DateHelper.convertToUTCDateString(dateStart);
            } else {
                cal.setTime(dateEnd);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                dateEnd = cal.getTime();
                endStr = DateHelper.convertToUTCDateString(dateEnd);
            }
        }


        String taskInterval = convertUTCDateStringToTimeString(startStr) +
                " - " + convertUTCDateStringToTimeString(endStr);

        TextView tvTaskName = view.findViewById(R.id.tvTaskName);
        TextView tvDateInterval = view.findViewById(R.id.tvDateInterval);

        tvTaskName.setText(taskName);
        tvDateInterval.setText(taskInterval);
    }
}
