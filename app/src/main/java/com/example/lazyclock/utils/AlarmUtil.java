package com.example.lazyclock.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Vibrator;

import com.example.lazyclock.Config;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.db.dao.AlarmDao;
import com.example.lazyclock.others.Task.QuickTimeTextTask;
import com.example.lazyclock.service.RingtongService;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TreeSet;

/**
 * Created by Administrator on 2016-03-15.
 */
public class AlarmUtil {
    private static AlarmUtil util;

    private AlarmUtil() {
    }

    public static AlarmUtil getInstence() {
        if (util == null) {
            synchronized (AlarmUtil.class) {
                if (util == null) {
                    util = new AlarmUtil();
                }
            }
        }
        return util;
    }


    /**
     * 将用户闹铃添加到系统闹钟管理器当中
     *
     * @param context   上下文对象
     * @param alarmBean 闹钟对象
     */
    public void setAlarmToSystem(Context context, AlarmBean alarmBean) {
        Intent intent = new Intent(context, RingtongService.class);
        intent.setAction("android.intent.action.ALARM_SERVER");
        intent.putExtra("Alarm", alarmBean);
        PendingIntent pendingIntent = PendingIntent.getService(context, alarmBean.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, alarmBean.getTimeInMills(), pendingIntent);
    }


    /**
     * 删除闹钟 （action和请求码作为识别闹钟的标识符）
     *
     * @param app
     * @param bean
     */
    public void deleteAlarm(MyApplication app, AlarmBean bean) {
        List<AlarmBean> lists = app.getAlarDates();
        AlarmDao dao = new AlarmDao(app);
        if (bean != null && lists != null && !lists.isEmpty()) {
            //数据库中删除对应的闹钟
            int flag = dao.delete(bean);
            if (flag == 1) {
                Intent intent = new Intent(app, RingtongService.class);
                intent.setAction("android.intent.action.ALARM_SERVER");
                intent.putExtra("Alarm", bean);
                PendingIntent pendingIntent = PendingIntent.getService(app, bean.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmManager = (AlarmManager) app.getSystemService(app.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
            }

            dao.close();
        }
    }

    public boolean insertAlarm(MyApplication app, AlarmBean bean) {
        AlarmDao dao = new AlarmDao(app);
        boolean flag;
        if (bean != null) {
            flag = dao.insert(bean);
            if (flag) {
                AlarmUtil.getInstence().getAllAlarms(dao);
                return flag;
            }
        }
        return false;
    }

    /**
     * @param context 上下文
     * @param i       定时时长
     * @param l       剩余时间回调
     * @return 后台计算剩余时间的Task
     */
    public Timer setQuickAlram(Context context, long i, QuickTimeTextTask.OnTimeFreshListener l) {
        AlarmBean bean = new AlarmBean(context);
        bean.setMoreSleepMins(0);
        bean.setName("快速闹钟");
        bean.setFlag(Config.QIUCKAlARM);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis() + i);
        int hour = c.get(Calendar.HOUR);
        int mins = c.get(Calendar.MINUTE);
        String srt1 = TimeUtil.getInstance().getTimeWithZero(hour, mins);
        bean.setTimeInMills(c.getTimeInMillis());
        bean.setTimeStr(srt1);

        AlarmDao dao = new AlarmDao(context);
        dao.insert(bean);
        AlarmUtil.getInstence().setAlarmToSystem(context, bean);
        Timer timer = new Timer();
        QuickTimeTextTask task = new QuickTimeTextTask(l);
        timer.schedule(task, 0, 1000);

        return timer;
    }

    public void setNextAlarm(MyApplication app, AlarmBean bean) {
        //本次闹钟周期结束，且不重复的闹钟，关闭闹钟
        TimeUtil util = TimeUtil.getInstance();
        long time;
        //闹铃不重复情况
        if (!bean.isrepeat()) {
            bean.setWork(false);
            return;
        }
        //Daylist到尾巴了
        else if (bean.getNextDayPosition() + 2 > bean.getDays().size()) {
            bean.setNextDayPosition(0);
        }
        Set<Integer> set = new TreeSet<>();
        util.Strs2Intergers(bean.getDays(), set);
        int temp;
        if (set.size() <= 1) {
            temp = 7;
        } else {
            temp = (int) set.toArray()[bean.getNextDayPosition()];
        }
        time = util.getFullTime(bean.getTimeStr(), temp);
        bean.setTimeInMills(time);
        bean.setRemainingTime(util.calculateTime(bean.getTimeInMills(), System.currentTimeMillis()));
        bean.setNextDayPosition(bean.getNextDayPosition() + 1);

        AlarmDao dao = new AlarmDao(app);
        //更新数据库
        dao.update(bean);
        //添加到系统闹钟
        setAlarmToSystem(app, bean);
        getAllAlarms(dao);

    }

    /**
     * 全局List重新获取数据库中的闹钟
     */
    public void getAllAlarms(MyApplication app) {
        AlarmDao dao = new AlarmDao(app);
        getAllAlarms(dao);
    }

    public void getAllAlarms(final AlarmDao dao) {
        final Context context = dao.getContext();

        Cursor c = dao.queryAll();
        List<AlarmBean> list = dao.getListFromCursor(c);

        if (context instanceof MyApplication) {
            ((MyApplication) context).setAlarDates(list);
        }

    }


    /**
     * 关闭响铃和震动服务
     */

    public void stopAlarmRingingServer(Context context) {

        //关闭震动
        Vibrator vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            ((Vibrator) context.getSystemService(context.VIBRATOR_SERVICE)).cancel();
        }

        //关闭闹铃
        Intent intent = new Intent("com.example.aciton.BGMediaPlyer");
        intent.setPackage(context.getPackageName());
        context.stopService(intent);
    }

    /**
     * 通过flag来获取闹钟对象
     *
     * @param flag
     * @return
     */
    public AlarmBean getAlarmByFlag(MyApplication app, int flag) {
        List<AlarmBean> list = app.getAlarDates();
        if (list == null || list.isEmpty()) {
            return null;
        }
        for (AlarmBean bean : list) {
            if (bean.getFlag() == flag) {
                return bean;
            }
        }
        return null;
    }

    /**
     * @return 返回距离目前时间最近的闹钟
     */
    public AlarmBean getRecently(MyApplication app) {
        List<AlarmBean> list = app.getAlarDates();
        long nowMills = System.currentTimeMillis();
        AlarmBean bean;
        if (list == null || list.isEmpty()) {
            return null;
        }
        bean = list.get(0);
        if (list.size() == 1) {
            return bean;
        }

        for (int i = 1; i < list.size(); i++) {
            bean = Math.abs(nowMills - bean.getTimeInMills()) <
                    Math.abs(nowMills - list.get(i).getTimeInMills()) ?
                    bean : list.get(i);
        }
        return bean;
    }

}
