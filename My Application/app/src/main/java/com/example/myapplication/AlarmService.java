package com.example.myapplication;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import android.os.CountDownTimer;

import java.util.Random;

public class AlarmService extends Service {
    private MediaPlayer mediaPlayer;
    private NotificationCompat.Builder notification_builder;
    private NotificationManager manager;
    private Vibrator vibrator;
    boolean isDismissed=false;
    private String phoneNo;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new CountDownTimer(60000,1000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if(!isDismissed && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                    Log.d("permission granted:","true");
                    boolean found = false;
                    Random rnd = new Random();
                    ContentResolver contentResolver = getContentResolver();
                    Cursor contacts = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

                    while(!found ){
                        Integer index=rnd.nextInt(contacts.getCount());
                        contacts.moveToPosition(index);
                        Log.d("index ",index+"");
                        String id = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        if (contacts.getInt(contacts.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0){
                            found=true;
                            Cursor pCur = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                            while (pCur.moveToNext()) {
                                phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                Toast.makeText(getApplicationContext(),name+" "+phoneNo,Toast.LENGTH_SHORT).show();
                                Log.d("phone ", phoneNo+" "+name);
                            }
                            pCur.close();
                        }
                    }
                    SmsManager smsManager=SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo,null,"Hi",null,null);
                    stopSelf();
                }
                else {

                    stopSelf();
                }

            }
        }.start();
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
            isDismissed=true;
            stopSelf();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        manager.cancel(0);
        super.onDestroy();
        mediaPlayer.stop();
        vibrator.cancel();
        Intent serviceIntent=new Intent(getApplicationContext(),ForegroundSrevice.class);
        startService(serviceIntent);
        stopForeground(true);
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Toast.makeText(getApplicationContext(), "Alarm Dismissed", Toast.LENGTH_LONG).show();
        startActivity(intent);
    }
}
