package com.example.myapplication;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

public class AddAlarm extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private int fhour,fmin;
    private TextView timePicker;
    private DatabaseHelper databaseHelper;
    private long timeinmillis;
    private Calendar calendar;
    Integer READ_CONTACTS_CODE=1;
    private String fh,fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        databaseHelper=new DatabaseHelper(this);
        timePicker=(TextView)findViewById(R.id.timePicker);
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c=Calendar.getInstance();
                int ihour,imin;
                ihour=c.get(Calendar.HOUR_OF_DAY);
                imin=c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog=new TimePickerDialog(AddAlarm.this,AddAlarm.this,ihour,imin,true);
                timePickerDialog.show();
            }
        });
        Button add=(Button)findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.SEND_SMS)==PackageManager.PERMISSION_GRANTED) {
                    addAlarm();
                }
                else{
                    if(ActivityCompat.shouldShowRequestPermissionRationale(AddAlarm.this,Manifest.permission.READ_CONTACTS)){
                        new AlertDialog.Builder(AddAlarm.this)
                                .setTitle("Permission needed")
                                .setMessage("You cannot add alarm without permission")
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(AddAlarm.this,new String[] {Manifest.permission.READ_CONTACTS,Manifest.permission.SEND_SMS},READ_CONTACTS_CODE);
                                    }
                                })
                                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                    }
                    else if(ActivityCompat.shouldShowRequestPermissionRationale(AddAlarm.this,Manifest.permission.READ_CONTACTS)){
                        new AlertDialog.Builder(AddAlarm.this)
                                .setTitle("Permission needed")
                                .setMessage("You cannot add alarm without permission")
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(AddAlarm.this,new String[] {Manifest.permission.READ_CONTACTS,Manifest.permission.SEND_SMS},READ_CONTACTS_CODE);
                                    }
                                })
                                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                    }
                    else{
                        ActivityCompat.requestPermissions(AddAlarm.this,new String[] {Manifest.permission.READ_CONTACTS,Manifest.permission.SEND_SMS},READ_CONTACTS_CODE);

                    }

                }

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
        timePicker.setText(fh+":"+fm+" hours");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==READ_CONTACTS_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"granted",Toast.LENGTH_SHORT).show();
                //addAlarm();
            }
            else{
                Toast.makeText(this,"denied",Toast.LENGTH_SHORT).show();
            }
            addAlarm();
        }
    }
    public void addAlarm() {
        if (fh != null && fm != null) {
            boolean result = databaseHelper.addAlarm(fh + ":" + fm);
            if (result)
                Toast.makeText(getApplicationContext(), "Alarm added", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "Task failed", Toast.LENGTH_SHORT).show();
            int requestCode = databaseHelper.returnLastId();
            Intent serviceIntent = new Intent(getApplicationContext(), ForegroundSrevice.class);
            serviceIntent.putExtra("timeinmillis", calendar.getTimeInMillis());
            serviceIntent.putExtra("time", fh + ":" + fm);
            serviceIntent.putExtra("rc", requestCode);
            startService(serviceIntent);
                /*AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent i=new Intent(getApplicationContext(),Alarm.class);
                i.putExtra("rc",requestCode);
                Log.d("RC",requestCode+"");
                //int id=databaseHelper.getId(fh+":"+fm,"true");
                PendingIntent pi=PendingIntent.getBroadcast(getApplicationContext(),requestCode,i,0);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pi);
                Log.d("RC:",requestCode+"");*/
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(),"time cannot be null",Toast.LENGTH_SHORT).show();
        }
    }

}
