package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Update extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private int id;
    private String time,status;
    private int fhour,fmin;
    private TextView timePicker;
    private Switch switch1;
    private DatabaseHelper databaseHelper;
    private long timeinmillis;
    private Calendar calendar;
    private String fh,fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        databaseHelper=new DatabaseHelper(Update.this);
        Intent intent=getIntent();
        id=intent.getIntExtra("id",-1);
        Log.d("ID:",id+"");
        time=intent.getStringExtra("time");
        status=intent.getStringExtra("status");
        timePicker=(TextView)findViewById(R.id.timePickerDialog);
        timePicker.setText(time);
        switch1=(Switch)findViewById(R.id.switch2);
        if(status.equals("true")){
            switch1.setChecked(true);
        }
        else{
            switch1.setChecked(false);
        }
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c=Calendar.getInstance();
                int ihour,imin;
                ihour=c.get(Calendar.HOUR_OF_DAY);
                imin=c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog=new TimePickerDialog(Update.this,Update.this,ihour,imin,true);
                timePickerDialog.show();
            }
        });
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(switch1.isChecked()){
                    status="true";
                }
                else
                    status="false";
            }
        });
        final Button update=(Button)findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if(status=="true") {
                    Intent serviceIntent=new Intent(getApplicationContext(),ForegroundSrevice.class);
                    serviceIntent.putExtra("timeinmillis",calendar.getTimeInMillis());
                    serviceIntent.putExtra("time",fh+":"+fm);
                    serviceIntent.putExtra("rc",id);
                    startService(serviceIntent);
                }
                else{
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent i = new Intent(getApplicationContext(), Alarm.class);
                    PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), id, i, 0);
                    alarmManager.cancel(pi);

                }
                    boolean result = databaseHelper.updateAlarm(id, time, status);
                if(result) {
                    Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT);
                    Intent intent1 = new Intent(Update.this, MainActivity.class);
                    startActivity(intent1);
                }
                else
                    Toast.makeText(getApplicationContext(),"Update Failed",Toast.LENGTH_SHORT).show();

            }
        });
        Button delete=(Button)findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent i = new Intent(getApplicationContext(), Alarm.class);
                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), id, i, 0);
                alarmManager.cancel(pi);
                boolean result=databaseHelper.deleteAlarm(id);
                if(result==true)
                {
                    Toast.makeText(getApplicationContext(),"Alarm Deleted",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Delete Failed",Toast.LENGTH_SHORT).show();
                }
                Intent intent2=new Intent(Update.this,MainActivity.class);
                startActivity(intent2);
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        fhour=hourOfDay;
        fmin=minute;
        calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,fhour);
        calendar.set(Calendar.MINUTE,fmin);
        calendar.set(android.icu.util.Calendar.SECOND,0);
        if(fhour<10)
            fh="0"+fhour;
        else
            fh=""+fhour;
        if(fmin<10)
            fm="0"+fmin;
        else
            fm=""+fmin;
        time=fh+":"+fm;
        timePicker.setText(fh+":"+fm+" hours");
    }
}
