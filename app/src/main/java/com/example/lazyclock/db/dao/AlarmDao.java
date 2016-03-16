package com.example.lazyclock.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lazyclock.Config;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.bean.Ring;
import com.example.lazyclock.db.AlarmDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/2.
 */
public class AlarmDao {

    // 数据库版本号
    private static final int DATABASE_VERSION = 1;

    private AlarmDatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDb;
    private Context mContext;

    public AlarmDao(Context context) {
        mContext = context;
        String dbName = context.getExternalFilesDir(null).getPath() + "/" + Config.ALARM_DATABASE_NAME;
        mDatabaseHelper = new AlarmDatabaseHelper(mContext, dbName, null, DATABASE_VERSION);
    }


    public Context getContext() {
        return mContext;
    }

    public boolean insert(AlarmBean bean) {
        if (bean == null) {
            return false;
        }
        ContentValues cv = getContentValues(bean);
        mDb = mDatabaseHelper.getWritableDatabase();
        mDb.insert(Config.TABLE_NAME_ALARM, null, cv);
        return true;
    }


    public boolean update(AlarmBean bean) {
        if (bean == null) {
            return false;
        }
        mDb = mDatabaseHelper.getWritableDatabase();
        ContentValues cv = getContentValues(bean);
        String where = " _id = ? ";
        String[] wherearg = {
                String.valueOf(bean.getId())
        };
        mDb.update(Config.TABLE_NAME_ALARM, cv, where, wherearg);

        return true;
    }

    public Cursor queryAll() {
        Cursor c = query(null, null, null);
        return c;
    }

    public Cursor queryById(int id) {
        mDb = mDatabaseHelper.getReadableDatabase();
        String seleciton = AlarmDatabaseHelper._ID + " = ?";
        String[] args = {
                String.valueOf(id)
        };
        Cursor c = mDb.query(Config.TABLE_NAME_ALARM, null, seleciton, args, null, null, AlarmDatabaseHelper._ID);

        return c;
    }

    public Cursor query(String[] columns, String selection, String[] args) {
        mDb = mDatabaseHelper.getReadableDatabase();
        Cursor c = mDb.query(Config.TABLE_NAME_ALARM, columns, selection, args, null, null, AlarmDatabaseHelper._ID);

        return c;
    }


    public int delete(AlarmBean bean) {
        mDb = mDatabaseHelper.getWritableDatabase();
        String selection = AlarmDatabaseHelper._ID + "= ?";
        String[] args = {
                String.valueOf(bean.getId())
        };
        int flag = mDb.delete(Config.TABLE_NAME_ALARM, selection, args);
        return flag;
    }


    public AlarmBean getAlarmFromCursor(Cursor c) {
        if (c == null) {
            return null;
        }
        AlarmBean bean = new AlarmBean(mContext);
        bean.setId(c.getInt(0));
        bean.setName(c.getString(1));
        bean.setWork(str2Boolean(c.getString(2)));
        bean.setRepeat(str2Boolean(c.getString(3)));
        bean.setTimeStr(c.getString(4));
        bean.setTimeInMills(c.getLong(5));
        Ring ring = new Ring();
        ring.ringUri = c.getString(6);
        ring.ringName = c.getString(7);
        ring.ringType = c.getInt(8);
        bean.setRing(ring);
        bean.setRemainingTime(c.getString(9));
        bean.setDays(str2List(c.getString(10)));
        bean.setShank(str2Boolean(c.getString(11)));
        bean.setMoreSleep(str2Boolean(c.getString(12)));
        bean.setVolumegradual(str2Boolean(c.getString(13)));
        bean.setFlag(c.getInt(14));
        bean.setStartTime(c.getLong(15));
        bean.setVolume(c.getInt(16));
        bean.setSleepType(c.getInt(17));
        bean.setMoreSleepMins(c.getInt(18));

        c.close();
        return bean;
    }

    public List<AlarmBean> getListFromCursor(Cursor c) {
        if (c == null) {
            return null;
        }
        List<AlarmBean> list = new ArrayList<>();
        while (c.moveToNext()) {
            AlarmBean bean = getAlarmFromCursor(c);
            list.add(bean);
        }
        c.close();
        return list;
    }


    private String boolean2Str(boolean b) {
        if (b) {
            return "true";
        } else {
            return "false";
        }
    }

    private boolean str2Boolean(String s) {
        if (s.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    private String list2Str(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (String s : list) {
            if (sb.length() != 0) {
                sb.append(",");
            }
            sb.append(s);
        }
        return sb.toString();
    }

    private List<String> str2List(String s) {
        List<String> list = new ArrayList<>();
        StringBuffer sb = new StringBuffer(s);
        int end = sb.indexOf(",");
        //如果end!=-1,意味着有两个“星期”以上
        while (end != -1) {
            String m = (String) sb.subSequence(0, end);
            sb.delete(0, end);
            end = sb.indexOf(",");
            list.add(m);
        }
        list.add(sb.toString());
        return list;
    }

    private ContentValues getContentValues(AlarmBean bean) {
        if (bean == null) {
            return null;
        }
        ContentValues cv = new ContentValues();
        cv.put("name", bean.getName());
        cv.put("isWork", boolean2Str(bean.isWork()));
        cv.put("isRepeat", boolean2Str(bean.isrepeat()));
        cv.put("time", bean.getTimeStr());
        cv.put("datelong", bean.getTimeInMills());
        cv.put("ringUri", bean.getRing().ringUri);
        cv.put("ringName", bean.getRing().ringName);
        cv.put("ringType", bean.getRing().ringType);
        cv.put("remainTime", bean.getRemainingTime());
        cv.put("days", list2Str(bean.getDays()));
        cv.put("isShank", boolean2Str(bean.isShank()));
        cv.put("isMoreSleep", bean.isMoreSleep());
        cv.put("isVolumegradual", boolean2Str(bean.isVolumegradual()));
        cv.put("flag", bean.getFlag());
        cv.put("startTime", bean.getStartTime());
        cv.put("volume", bean.getVolume());
        cv.put("sleepType", bean.getSleepType());
        cv.put("timeOffset", bean.getMoreSleepMins());
        return cv;
    }

    public void close() {

        mDb.close();
        mDatabaseHelper.close();
    }
}
