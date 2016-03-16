package com.example.lazyclock.others.Task;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.example.lazyclock.Config;
import com.example.lazyclock.R;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.service.AlarmService;
import com.example.lazyclock.utils.AlarmUtil;
import com.example.lazyclock.utils.TimeUtil;

import java.util.TimerTask;

/**
 * Created by Administrator on 2016-03-15.
 */
public class StopTask extends TimerTask {
    private Context mContext;
    private AlarmBean mAlarm;

    public StopTask(Context context, AlarmBean bean) {
        mContext = context;
        mAlarm = bean;
    }

    @Override
    public void run() {
        AlarmUtil.getInstence().stopAlarmRingingServer(mContext);
        //如果不是快速闹钟，并且开启了小睡
        if (mAlarm.getFlag() != Config.QIUCKAlARM && mAlarm.isMoreSleep()) {
            AlarmBean bean = new AlarmBean(mContext);
            TimeUtil.getInstance().copyAlarm(mAlarm, bean);
            TimeUtil.getInstance().addMillsToAlarmBean(bean.getMoreSleepMins() * 60 * 1000, bean);
            AlarmUtil.getInstence().setAlarmToSystem(mContext, bean);

            //在通知栏显示提醒
            NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
            builder.setTicker("(点击取消)进入小睡时间")
                    .setContentTitle("下次闹铃时间：")
                    .setContentText(bean.getTimeStr() + "")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_LIGHTS)
                    .setPriority(Notification.PRIORITY_DEFAULT);
            //点击通知栏的事件
            Intent intent = new Intent(mContext, AlarmService.class);
            intent.putExtra("mAlarm", mAlarm);
            PendingIntent pendingintent = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            builder.setContentIntent(pendingintent);

            Notification notification = builder.build();
            notification.flags = Notification.FLAG_NO_CLEAR;    //不被清理按键清理
            mNotificationManager.notify(Config.SERVICE_DEL, notification);
        }
        if (mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
    }

}
