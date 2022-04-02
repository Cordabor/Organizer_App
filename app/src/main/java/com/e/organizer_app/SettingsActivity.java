package com.e.organizer_app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class SettingsActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_STORAGE = 1000;
    private static final int READ_REQUEST_CODE = 42;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String START_TASK_NOTIFICATION = "start_task_notification";
    public static final String END_TASK_NOTIFICATION = "end_task_notification";
    public static final String START_TASK_SOUND = "start_task_sound";
    public static final String END_TASK_SOUND = "end_task_sound";
    public static final String START_TASK_URI = "start_task_uri";
    public static final String END_TASK_URI = "end_task_uri";


    private Switch startTaskNotSwitch;
    private Switch endTaskNotSwitch;
    private Switch startTaskSoundSwitch;
    private Switch endTaskSoundSwitch;

    private TextView soundStartPathTextView;
    private TextView soundEndPathTextView;

    private String soundType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setElements();

        loadData();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.nav_settings);

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
                        startActivity(new Intent(getApplicationContext()
                                , ScheduleActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_settings:
                        return true;
                }
                return false;
            }
        });
    }

    private void setElements() {
        startTaskNotSwitch = (Switch) findViewById(R.id.switch_start_task_notification);
        endTaskNotSwitch = (Switch) findViewById(R.id.switch_end_task_notification);
        startTaskSoundSwitch = (Switch) findViewById(R.id.switch_start_task_sound);
        endTaskSoundSwitch = (Switch) findViewById(R.id.switch_end_task_sound);
        soundStartPathTextView = (TextView) findViewById(R.id.text_view_sound_start_path);
        soundEndPathTextView = (TextView) findViewById(R.id.text_view_sound_end_path);
    }

    public void onClickStartTaskNotification(View v){
        saveBooleanSetting(START_TASK_NOTIFICATION, startTaskNotSwitch);
    }

    public void onClickEndTaskNotification(View v){
        saveBooleanSetting(END_TASK_NOTIFICATION, endTaskNotSwitch);
    }

    public void onClickStartTaskSound(View v){
        saveBooleanSetting(START_TASK_SOUND, startTaskSoundSwitch);
    }

    public void onClickEndTaskSound(View v){
        saveBooleanSetting(END_TASK_SOUND, endTaskSoundSwitch);
    }

    public void saveBooleanSetting(String settingName, Switch selectedSwitch){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(settingName, selectedSwitch.isChecked());
        editor.commit();
        Toast.makeText(this, "Изменения сохранены", Toast.LENGTH_SHORT).show();
    }

    public void saveStringSetting(String settingName, String uriString){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(settingName, uriString);
        editor.commit();
        Toast.makeText(this, "Изменения сохранены", Toast.LENGTH_SHORT).show();
    }


    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        boolean boolStartTaskNotSwitch = sharedPreferences.getBoolean(START_TASK_NOTIFICATION, false);
        boolean boolEndTaskNotSwitch = sharedPreferences.getBoolean(END_TASK_NOTIFICATION, false);
        boolean boolStartTaskSoundSwitch = sharedPreferences.getBoolean(START_TASK_SOUND, false);
        boolean boolEndTaskSoundSwitch = sharedPreferences.getBoolean(END_TASK_SOUND, false);

        startTaskNotSwitch.setChecked(boolStartTaskNotSwitch);
        endTaskNotSwitch.setChecked(boolEndTaskNotSwitch);
        startTaskSoundSwitch.setChecked(boolStartTaskSoundSwitch);
        endTaskSoundSwitch.setChecked(boolEndTaskSoundSwitch);

        String startSoundUriString = sharedPreferences.getString(START_TASK_URI, "");
        String endSoundUriString = sharedPreferences.getString(END_TASK_URI, "");

        soundStartPathTextView.setText(startSoundUriString);
        soundEndPathTextView.setText(endSoundUriString);
    }


    private void performFileSearch(String sT){
        soundType = sT;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            if(data != null){
                Uri uri = data.getData();
                if (soundType.equals("start")){
                    soundStartPathTextView.setText(uri.toString());
                    saveStringSetting(START_TASK_URI, uri.toString());
                } else if (soundType.equals("end")){
                    soundEndPathTextView.setText(uri.toString());
                    saveStringSetting(END_TASK_URI, uri.toString());
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if (requestCode == PERMISSION_REQUEST_STORAGE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
            }else {
                finish();
            }
        }
    }

    public void onClickChooseStartSound(View v){
        performFileSearch("start");
    }

    public void onClickChooseEndSound(View v){
        performFileSearch("end");
    }
}
