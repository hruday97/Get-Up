package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class AlarmService extends Service {
    private MediaPlayer mediaPlayer;
    private NotificationCompat.Builder notification_builder;
    private NotificationManager manager;
    private Vibrator vibrator;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isAlarmOn= (boolean) intent.getBooleanExtra("isAlarmOn",false);
        if(isAlarmOn) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), Settings.System.DEFAULT_ALARM_ALERT_URI);
            Toast.makeText(getApplicationContext(), "alarm!!!!!!!!!!!!!!!!!!!!!!!!", Toast.LENGTH_LONG).show();
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            long[] pattern={1000,300,300,1000,200,200};
            vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE) ;
            if(vibrator.hasVibrator()){
                if(Build.VERSION.SDK_INT>=26)
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern,0));
                else
                    vibrator.vibrate(pattern,0);
            }
            Intent intent1 = new Intent(getApplicationContext(), AlarmService.class);
            intent1.putExtra("isAlarmOn", false);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pi=PendingIntent.getService(getApplicationContext(),0,intent1,0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String chanel_id = "1";
                CharSequence name = "Alarm";
                String description = "This Channel is for alarm";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(chanel_id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.GREEN);
                //mChannel.setVibrationPattern(new long[]{1000,1000,1000,1000});
                NotificationManager notificationManager=getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(mChannel);
                notification_builder= new NotificationCompat.Builder(this, chanel_id);
            } else {

                notification_builder = new NotificationCompat.Builder(this);
            }
            notification_builder.setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Alarm")
                    .setContentText("Click to dismiss")
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .setOngoing(true);
            manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
            //manager.notify(0, notification_builder.build());
            startForeground(1,notification_builder.build());
        }
        else
        {
           stopSelf();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        manager.cancel(0);
        super.onDestroy();
        mediaPlayer.stop();
        Intent serviceIntent=new Intent(getApplicationContext(),ForegroundSrevice.class);
        startService(serviceIntent);
        stopForeground(true);
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Toast.makeText(getApplicationContext(), "Alarm Dismissed", Toast.LENGTH_LONG).show();
        startActivity(intent);
    }
}
