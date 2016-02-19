package com.example.lazyclock.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lazyclock.AlarmState;

/**
 * Created by Administrator on 2016/2/2.
 */
public class AlarmDatabaseHelper extends SQLiteOpenHelper {
    public static final String _ID = "_id";

    private final String CREATE_TABLE = "create table " +
            AlarmState.TABLE_NAME_ALARM +
            "(" +
            _ID + "integer primary key autoincrement ," +
            "name varchar(30) not null ," +
            "isWork varchar(5)," +
            "isRepeat varchar(5)," +
            "time varchar(10)," +
            "datelong bigint," +
            "ringUri nvarchar," +
            "ringName varchar(20)," +
            "ringType int," +
            "days varchar(60)" +
            "isShank varchar(5)," +
            "isMoreSleep varchar(5)," +
            "isVolumegradual varchar(5)," +
            "flag int," +
            "startTime bigint," +
            "volume int," +
            "sleepType int," +
            "timeOffset int," +
            "isMoreSleep varchar(5)," +
            ");";

    public AlarmDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + AlarmState.TABLE_NAME_ALARM);
        onCreate(db);
    }
}
