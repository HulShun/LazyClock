package com.example.lazyclock.acitivities.stopalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.lazyclock.AlarmState;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.R;
import com.example.lazyclock.acitivities.MainActivity;
import com.example.lazyclock.acitivities.addalarm.AddAlarmActivity;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.bean.User;

/**
 * Created by Administrator on 2015/12/28.
 */
public class TouchStopActivity extends Activity {
    private TextView mTextView;
    private MyApplication mApp;
    private User myUser;

    private AlarmBean mAlarmBean;
    private Button mExitBtm;

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

        setContentView(R.layout.activity_stop_touch);
        initView();
        initData();
        initEvent();

    }


    private void initEvent() {
        mExitBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭铃声和震动
                mApp.stopAlarmRingingServer();
                //根据闹钟对象来设置下一次闹铃，或者关闭闹铃
                if (mAlarmBean.getFlag() == AlarmState.AlARMTYPE_PREVIEW) {
                    Intent intent = new Intent(TouchStopActivity.this, AddAlarmActivity.class);
                    intent.putExtra("Alarm", mAlarmBean);
                    startActivity(intent);
                } else {
                    mApp.setNextAlarm(TouchStopActivity.this, mAlarmBean);
                    myUser.setWeakUpDays(myUser.getWeakupDays() + 1);
                    Intent intent = new Intent(TouchStopActivity.this, MainActivity.class);
                    startActivity(intent);
                    TouchStopActivity.this.finish();
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
        mTextView.setText(mAlarmBean.getTimeStr());
    }

    private void initView() {
        mTextView = (TextView) findViewById(R.id.touchstop_id_textview);
        mExitBtm = (Button) findViewById(R.id.touchstop_id_button);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE || keyCode == KeyEvent.KEYCODE_POWER) {
            //进入小睡处理
            mApp.stopMediaServer(TouchStopActivity.this, mAlarmBean, 0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
