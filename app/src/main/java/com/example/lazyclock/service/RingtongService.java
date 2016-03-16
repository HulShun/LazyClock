package com.example.lazyclock.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import com.example.lazyclock.Config;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.bean.Ring;
import com.example.lazyclock.others.MyMediaPlayer;
import com.example.lazyclock.others.Task.StopTask;
import com.example.lazyclock.view.acitivities.stopalarm.CodeStopActivity;
import com.example.lazyclock.view.acitivities.stopalarm.MathStopActivity;
import com.example.lazyclock.view.acitivities.stopalarm.ShakeStopActivity;
import com.example.lazyclock.view.acitivities.stopalarm.TouchStopActivity;

import java.util.Timer;


/**
 * 控制闹铃音乐后台播放
 * Created by Administrator on 2015/12/28.
 */
public class RingtongService extends Service {

    private final static int STREAM_TYPE = AudioManager.STREAM_ALARM;
    /**
     * 响铃多久未处理，自动关闭（或者根据是否开始小睡来处理）
     */
    private static final long RING_TIME = 3 * 60 * 1000;

    private MyMediaPlayer mMediaPlayer;

    private AlarmBean mAlarm;
    private Vibrator mVibrator;
    private MyApplication mApp;
    private Timer mTimer;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("hours", "成功开启闹铃到时服务");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }

        if (mVibrator != null) {
            mVibrator.cancel();
        }
        mTimer.cancel();
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mApp = (MyApplication) getApplication();
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        mAlarm = intent.getParcelableExtra("Alarm");

        //响铃时间过长
        mTimer = stopMediaServer(RingtongService.this, mAlarm, RING_TIME);

        //闹钟对象为空或者过期的闹钟，就不响铃
        long temp = mAlarm.getTimeInMills() - System.currentTimeMillis();
        if (mAlarm == null || temp < -60 * 1000) {
            return super.onStartCommand(intent, flags, startId);
        }

        Ring ring = mAlarm.getRing();
        mMediaPlayer = new MyMediaPlayer(RingtongService.this, mAlarm, MyMediaPlayer.FLAG_WORK);
        mMediaPlayer.start();

        //震动震起
        if (mAlarm.isShank()) {
            //开启震动
            mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = {1000, 1000};
            mVibrator.vibrate(pattern, 0);
        }

        //跳转响铃页面
        Intent intent1 = new Intent();
        switch (mAlarm.getStopSleepType()) {
            case Config.STOPSELECT_CLICK:
                intent1.setClass(RingtongService.this, TouchStopActivity.class);
                break;
            case Config.STOPSELECT_CODE:
                intent1.setClass(RingtongService.this, CodeStopActivity.class);
                break;
            case Config.STOPSELECT_SHANK:
                intent1.setClass(RingtongService.this, ShakeStopActivity.class);
                break;
            case Config.STOPSELECT_PIC:
                intent1.setClass(RingtongService.this, TouchStopActivity.class);
                break;
            case Config.STOPSELECT_MATH:
                intent1.setClass(RingtongService.this, MathStopActivity.class);
                break;
        }
        intent1.putExtra("Alarm", mAlarm);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent1);

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Timer stopMediaServer(Context context, AlarmBean bean, long delay) {
        Timer timer = new Timer();
        timer.schedule(new StopTask(context, bean), delay);
        return timer;
    }

}
