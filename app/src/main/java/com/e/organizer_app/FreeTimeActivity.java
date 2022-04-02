package com.e.organizer_app;

import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.text.ParseException;
import java.util.Date;

public class FreeTimeActivity extends AppCompatActivity {

    private TimePicker tp;
    private String foundStartTimeString;
    private String foundEndTimeString;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_time);

        // TimePicker элементы задаем 24 часовой формат
        tp = (TimePicker) this.findViewById(R.id.tp);
        tp.setIs24HourView(true);
        tp.setMinute(0);
        tp.setHour(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickOfferFreeTime(View v){
        int minutes = tp.getMinute();
        int hours = tp.getHour();

        DbHelper dbHelper = new DbHelper(getApplicationContext());
        foundStartTimeString = dbHelper.findTimeForTask(hours, minutes);

        Date endTime = DateHelper.convertUTCStringToDate(foundStartTimeString);
        endTime = DateUtil.addHours(endTime, hours);
        endTime = DateUtil.addMinutes(endTime, minutes);

        foundEndTimeString = DateHelper.convertToUTCDateString(endTime);

        TextView freeTimeTextView = (TextView) this.findViewById(R.id.free_time_text_view);
        freeTimeTextView.setText(foundStartTimeString + " - " + foundEndTimeString);
    }

    public void onClickOk(View v){
        Intent intent = new Intent();
        intent.putExtra("start", foundStartTimeString);
        intent.putExtra("end", foundEndTimeString);
        setResult(RESULT_OK, intent);
        finish();
    }
}
