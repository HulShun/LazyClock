package com.example.lazyclock.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.lazyclock.Config;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.utils.AlarmUtil;
import com.example.lazyclock.utils.LogUtil;

/**
 * 通知栏通知的点击后，开启服务来删除闹铃
 */
public class AlarmService extends Service {
    private MyApplication mApp;
    private AlarmBean mAlarm;

    public AlarmService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d("service", "开启了删除闹钟的服务");
        mAlarm = intent.getParcelableExtra("mAlarm");
        mApp = (MyApplication) getApplication();
        AlarmUtil.getInstence().deleteAlarm(mApp, mAlarm);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(Config.SERVICE_DEL);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
