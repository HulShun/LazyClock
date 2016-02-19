package com.example.lazyclock.acitivities.stopalarm;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lazyclock.AlarmState;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.R;
import com.example.lazyclock.acitivities.MainActivity;
import com.example.lazyclock.acitivities.addalarm.AddAlarmActivity;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.bean.User;
import com.example.lazyclock.utils.ShakeDetection;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 摇晃解锁
 * Created by Administrator on 2015/12/28.
 */
public class ShakeStopActivity extends Activity implements ShakeDetection.OnShakeListener {
    private static final int MESSAGE_WHAT_STARTSHAKE = 0;
    private static final int MESSAGE_WHAT_EXITSHOW = 1;
    private static final int MESSAGE_WHAT_QIUCK = 2;
    private static final int MESSAGE_WHAT_STOP = 3;
    private static final int MESSAGE_WHAT_SHAKE = 4;
    private static final int MESSAGE_WHAT_ANIMI = 5;


    private TextView mTimeView, mMessgeText;

    private LinearLayout mClockLayout;
    private float mClockLayout_ScrollY;

    private RelativeLayout mShakeBgView;
    private Button mStartBtm, mExitBtn;
    private ImageView mShakeImage;

    private MyApplication mApp;
    private User myUser;
    private AlarmBean mAlarmBean;

    /**
     * 摇晃检测
     */
    private ShakeDetection mShakeDetection;
    /**
     * 初始化震动标记
     */
    private boolean isShake = false;
    private Timer mTimer;
    private MyTimerTask myTimerTask;

    /**
     * 唤醒锁屏
     */
    private PowerManager.WakeLock mWakeLock;


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

        Log.d("hours", "服务成功开启activity");

        setContentView(R.layout.activity_stop_shake);
        initView();
        initData();
        initEvent();

    }


    private void initEvent() {
        mStartBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myHandler.sendEmptyMessage(0);

                mClockLayout_ScrollY = mClockLayout.getScrollY() - 250f;
                Animator animator1 = ObjectAnimator.ofFloat(mClockLayout, "translationY", mClockLayout.getScrollY(), mClockLayout_ScrollY);
                animator1.setDuration(500);
                Animator animator2 = ObjectAnimator.ofFloat(mShakeBgView, "alpha", 0.0f, 1.0f);
                animator2.setDuration(300);

                AnimatorSet set = new AnimatorSet();
                set.play(animator2).after(animator1);
                set.start();

                //后台监听摇晃情况
                mShakeDetection.start();

            }
        });

        mShakeDetection.registerOnShakeListener(this);


        mExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAlarmBean.getFlag() == AlarmState.AlARMTYPE_PREVIEW) {
                    Intent intent = new Intent(ShakeStopActivity.this, AddAlarmActivity.class);
                    intent.putExtra("Alarm", mAlarmBean);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ShakeStopActivity.this, MainActivity.class);
                    startActivity(intent);
                    ShakeStopActivity.this.finish();
                }
            }
        });

    }


    private void initData() {
        mApp = (MyApplication) getApplication();
        mApp.setStopModeActivity(this);
        myUser = mApp.getMyUser();
        Intent intent = getIntent();
        mAlarmBean = intent.getParcelableExtra("Alarm");
        mTimeView.setText(mAlarmBean.getTimeStr());
        mShakeDetection = new ShakeDetection(this);


    }

    private void initView() {
        mTimeView = (TextView) findViewById(R.id.stop_shake_timetext);
        mMessgeText = (TextView) findViewById(R.id.stop_shake_message);
        mShakeBgView = (RelativeLayout) findViewById(R.id.stop_shake_bg);
        mClockLayout = (LinearLayout) findViewById(R.id.stop_shake_clockbg);
        mStartBtm = (Button) findViewById(R.id.stop_shake_startbtn);
        mExitBtn = (Button) findViewById(R.id.stop_shake_exitbtn);
        mShakeImage = (ImageView) findViewById(R.id.stop_shake_shakeview);
    }


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

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WHAT_STARTSHAKE:
                    mStartBtm.setEnabled(false);
                    mStartBtm.setVisibility(View.GONE);
                    mMessgeText.setText("开始摇晃吧！");
                    ObjectAnimator anim = ObjectAnimator.ofFloat(mShakeImage, "rotate", 0.0f, 5.0f, 0.0f, -5.0f, 0.0f);
                    anim.setDuration(1000);
                    anim.setRepeatMode(ValueAnimator.INFINITE);
                    anim.start();
                    break;

                case MESSAGE_WHAT_EXITSHOW:
                    mExitBtn.setEnabled(true);
                    mExitBtn.setVisibility(View.VISIBLE);
                    mMessgeText.setVisibility(View.VISIBLE);
                    break;
                case MESSAGE_WHAT_QIUCK:
                    mMessgeText.setText("快速闹钟");
                    break;
                case MESSAGE_WHAT_STOP:
                    myUser.setWeakUpDays(myUser.getWeakupDays() + 1);
                    mMessgeText.setText("恭喜您已经坚持按时起床了" + myUser.getWeakupDays() + "天！");
                    break;
                case MESSAGE_WHAT_SHAKE:
                    Bundle bundle = msg.getData();
                    String str = (String) bundle.get("message");
                    Log.d("hour", str);
                    mMessgeText.setText(str);
                    break;
                case MESSAGE_WHAT_ANIMI:
                    Animator animator1 = ObjectAnimator.ofFloat(mClockLayout, "translationY", mClockLayout_ScrollY, mClockLayout.getScrollY());
                    animator1.setDuration(500);
                    Animator animator2 = ObjectAnimator.ofFloat(mShakeBgView, "alpha", 1.0f, 0.0f);
                    animator2.setDuration(300);
                    Animator animator3 = ObjectAnimator.ofFloat(mExitBtn, "alpha", 0.0f, 1.0f);
                    animator2.setDuration(300);

                    AnimatorSet set = new AnimatorSet();
                    set.play(animator3).after(animator1).after(animator2);
                    set.start();
                    break;
            }


        }
    };

    @Override
    public void onShake() {
        isShake = true;
        if (mTimer == null) {
            mTimer = new Timer();
            myTimerTask = new MyTimerTask();
            mTimer.schedule(myTimerTask, 0);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE || keyCode == KeyEvent.KEYCODE_POWER) {
            //进入小睡处理
            mApp.stopMediaServer(ShakeStopActivity.this, mAlarmBean, 0);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Calendar c = Calendar.getInstance();
            long mills = 0;
            Bundle bundle = new Bundle();
            int m = 0;
            while (true) {
                c.set(Calendar.SECOND, 0);
                while (isShake) {
                    if (m <= 10) {
                        bundle.putString("message", "已连续摇晃" + m + "秒...");
                        //每次都要重新获取message对象，因为在重复刷新过程中，message可能会被回收，会报错
                        Message message = myHandler.obtainMessage();
                        message.setData(bundle);
                        message.what = MESSAGE_WHAT_SHAKE;
                        myHandler.sendMessage(message);
                        m = c.get(Calendar.SECOND);
                        isShake = false;
                        try {
                            mills = c.getTimeInMillis() + 500;
                            c.setTimeInMillis(mills);
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //   if (m == 10) m++;
                    }

                    if (m > 10) {
                        //如果是快速闹钟，就把它从闹钟列表中删除
                        if (mAlarmBean.getFlag() == AlarmState.QIUCKAlARM) {
                            mApp.deleteAlarm(ShakeStopActivity.this, mAlarmBean);
                            myHandler.sendEmptyMessage(MESSAGE_WHAT_QIUCK);
                        } else {
                            //根据闹钟对象来设置下一次闹铃，或者关闭闹铃
                            mApp.setNextAlarm(ShakeStopActivity.this, mAlarmBean);

                            myHandler.sendEmptyMessage(MESSAGE_WHAT_STOP);
                        }

                        //关闭铃声和震动
                        mApp.stopAlarmRingingServer();
                        mApp.getMyUser().getWeakupDays();
                        //摇晃检测关闭
                        mShakeDetection.stop();

                        myHandler.sendEmptyMessage(MESSAGE_WHAT_EXITSHOW);
                        myHandler.sendEmptyMessage(MESSAGE_WHAT_ANIMI);
                        return;
                    }
                }//isShake
                Message message = myHandler.obtainMessage();
                message.what = MESSAGE_WHAT_SHAKE;
                bundle.putString("message", "摇晃中断了...重启开始摇吧!");
                message.setData(bundle);
                myHandler.sendMessage(message);
            }//true

        }//run
    } //task


} //all
