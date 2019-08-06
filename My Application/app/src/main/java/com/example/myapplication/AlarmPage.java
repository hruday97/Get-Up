package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AlarmPage extends AppCompatActivity {
    private PendingIntent pendingIntent;
    private int id;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_page);

        final Intent intent=getIntent();
        id=intent.getIntExtra("id",-1);
        mediaPlayer=(MediaPlayer)intent.getSerializableExtra("mp");
        Log.d("ID",id+"");
        Button dismiss=(Button)findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("button","clicked");
                mediaPlayer.stop();
                finish();
            }
        });
    }
}
