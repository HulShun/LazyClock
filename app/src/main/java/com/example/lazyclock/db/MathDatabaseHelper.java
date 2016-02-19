package com.example.lazyclock.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lazyclock.AlarmState;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

/**
 * 数学题库的数据库处理
 * Created by Administrator on 2016/1/8.
 */
public class MathDatabaseHelper extends SQLiteOpenHelper {


    private String CREATE_TABLE = "create table " +
            AlarmState.TABLE_NAME +
            "(" +
            "_id  integer primary key autoincrement ," +
            "topic varchar(120) not null ," +
            "answerA varchar(10)," +
            "answerB varchar(10)," +
            "answerC varchar(10)," +
            "answerD varchar(10)," +
            "rightAnswer varchar(20)" +
            ");";

    /**
     * 获取答案的线程池
     */
    ExecutorService mPoolService;
    List<Runnable> mTaskQueue;
    Semaphore mPoolServiceSemaphore;
    /**
     * 任务队列的轮询线程
     */
    private Thread mPollThread;

    public MathDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }


    //创建数据库时调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    //数据库版本更新
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + AlarmState.TABLE_NAME);
        onCreate(db);
    }


}
