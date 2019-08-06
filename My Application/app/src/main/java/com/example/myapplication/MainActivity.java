package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private  DatabaseHelper databaseHelper;
    private  ArrayList<String> times=new ArrayList<>();
    private ArrayList<String> status=new ArrayList<>();
    private ArrayList<Integer> ids=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper=new DatabaseHelper(this);
        Cursor alarms=databaseHelper.getAlarm();


        while(alarms.moveToNext()){
            ids.add(alarms.getInt(0));
            times.add(alarms.getString(1));
            status.add(alarms.getString(2));
        }
        for(int i=0;i<times.size();i++)
            Log.d(ids.get(i)+"", times.get(i));
        PublicCustomAdapter pca=new PublicCustomAdapter(this,times,status);
        ListView listView=(ListView)findViewById(R.id.listview);
        listView.setAdapter(pca);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(),Update.class);
                intent.putExtra("time",times.get(position));
                intent.putExtra("status",status.get(position));
                intent.putExtra("id",ids.get(position));
                Log.d("DATA",times.get(position)+" "+status.get(position));
                startActivity(intent);
            }
        });
        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), AddAlarm.class);
                startActivity(intent);
            }
        });
    }
}
