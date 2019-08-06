package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;

public class Alarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent=new Intent(context,ForegroundSrevice.class);
        serviceIntent.putExtra("action",false);
        context.startService(serviceIntent);
        int id=intent.getIntExtra("rc",-1);
        Toast.makeText(context,"alarm with id "+id,Toast.LENGTH_SHORT).show();
        Log.d("Id",id+"");
        DatabaseHelper databaseHelper=new DatabaseHelper(context);
        databaseHelper.updateAlarmStatus(id,"false");
        Intent intent1=new Intent(context,AlarmService.class);
        intent1.putExtra("isAlarmOn",true);
        context.startService(intent1);
        /*Intent intent1=new Intent(context,AlarmPage.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra("id",id);
        context.startActivity(intent1);*/
    }
}
