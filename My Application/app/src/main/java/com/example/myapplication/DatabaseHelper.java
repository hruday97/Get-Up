package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;

import java.security.KeyStore;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String name = "Alarms";
    private static final int version =1 ;


    public DatabaseHelper(Context context) {
        super(context, name, null,1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String create="CREATE TABLE "+name+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, TIME TEXT, STATUS TEXT)";
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean addAlarm(String time){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("TIME",time);
        contentValues.put("STATUS","true");
        long result=db.insert(name,null,contentValues);
        if(result==-1)
            return false;
        else{
            return true;
        }

    }
    public boolean updateAlarm(Integer id,String time,String status){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("TIME",time);
        contentValues.put("STATUS",status);
        long result= db.update(name,contentValues,"ID=?", new String[]{String.valueOf(id)});
        if(result==-1)
            return false;
        else
            return true;
    }
    public boolean updateAlarmStatus(Integer id,String status){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("STATUS",status);
        long result= db.update(name,contentValues,"ID=?", new String[]{String.valueOf(id)});
        if(result==-1)
            return false;
        else
            return true;
    }
    public int returnLastId(){
        SQLiteDatabase db=this.getWritableDatabase();
        String query="Select * FROM "+name;
        Cursor alarms=db.rawQuery(query,null);
        alarms.moveToLast();
        int id=alarms.getInt(0);
        return id;
    }

    public Cursor getAlarm(){
        SQLiteDatabase db=this.getWritableDatabase();
        String query="SELECT * FROM "+name+" ORDER BY TIME";
        Cursor alarms=db.rawQuery(query,null);
        return alarms;
    }
    public boolean deleteAlarm(Integer id){
        SQLiteDatabase db=this.getWritableDatabase();
        long result=db.delete(name,"ID=?", new String[]{String.valueOf(id)});
        if(result==-1){
            return false;
        }
        else{
            return true;
        }
    }
    public Cursor getActiveAlarm(){
        SQLiteDatabase db=this.getWritableDatabase();
        String query="SELECT * FROM "+name+" WHERE STATUS = ? ";
        Cursor alarms=db.rawQuery(query,new String[]{"true"});
        //Log.d("TAAG",alarms.getString(1));
        return alarms;
    }
}
