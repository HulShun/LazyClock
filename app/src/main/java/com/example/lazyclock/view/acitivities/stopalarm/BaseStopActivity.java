package com.example.lazyclock.view.acitivities.stopalarm;

import android.app.Activity;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.others.Task.StopTask;

import java.util.Timer;

/**
 * Created by Administrator on 2016-03-15.
 */
public abstract class BaseStopActivity extends Activity {
    private static final long STOP_RINGTONE_TIME = 5 * 1000;
    private PowerManager.WakeLock mWakeLock;
    private Timer mTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取唤醒锁屏服务
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "AlarmWakeUp");

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        initViews();
        initData();
        initEvents();

        Log.d("hours", "服务成功开启activity");
        mTimer = new Timer();
        StopTask task = new StopTask(BaseStopActivity.this, getAlarm());
        mTimer.schedule(task, 0, STOP_RINGTONE_TIME);

    }

    protected abstract void initData();

    protected abstract void initViews();

    protected abstract void initEvents();

    protected abstract AlarmBean getAlarm();

    @Override
    protected void onResume() {
        super.onResume();
        if (mWakeLock != null) {
            mWakeLock.acquire();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWakeLock.release();
    }

}
