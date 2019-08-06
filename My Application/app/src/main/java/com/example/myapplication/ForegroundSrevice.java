package com.example.myapplication;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class ForegroundSrevice extends Service {
    private NotificationCompat.Builder notification;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String time=intent.getStringExtra("time");
        Boolean action=intent.getBooleanExtra("action",true);
        Integer id = intent.getIntExtra("rc",-1);
        long timeinmillis=intent.getLongExtra("timeinmillis",-1);
        Log.d("Action","Foreground service has started");
        if(action) {
            AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent i=new Intent(getApplicationContext(),Alarm.class);
            i.putExtra("rc",id);
            Log.d("RC",id+"");
            //int id=databaseHelper.getId(fh+":"+fm,"true");
            PendingIntent pi=PendingIntent.getBroadcast(getApplicationContext(),id,i,0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,timeinmillis,pi);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel nc = new NotificationChannel("2", "AlarmReminder", NotificationManager.IMPORTANCE_LOW);
                nc.setDescription("This is a notification channel for upcoming alarms");
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(nc);
                notification = new NotificationCompat.Builder(getApplicationContext(), "2");
            } else {
                notification = new NotificationCompat.Builder(getApplicationContext());
            }
            notification.setSmallIcon(R.drawable.ic_alarm_black_24dp)
                    .setOngoing(true)
                    .setContentTitle("Upcoming Alarm")
                    .setContentText("Upcoming alarm at " + time)
                    .setAutoCancel(true);
            startForeground(id, notification.build());
        }else{
            stopForeground(true);

        }
        return super.onStartCommand(intent, flags, startId);

    }
}
