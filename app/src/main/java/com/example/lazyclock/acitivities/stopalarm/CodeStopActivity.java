package com.example.lazyclock.acitivities.stopalarm;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lazyclock.AlarmState;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.R;
import com.example.lazyclock.acitivities.MainActivity;
import com.example.lazyclock.acitivities.addalarm.AddAlarmActivity;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.bean.User;
import com.example.lazyclock.utils.BPUtil;

/**
 * Created by Administrator on 2015/12/28.
 */
public class CodeStopActivity extends Activity {
    private TextView mTimeView, mMessgeText;

    private LinearLayout mClockLayout;
    private float mClockLayout_ScrollY;

    private RelativeLayout mCodeBgView;
    private EditText mEditText;
    private Button mOkBtn, mChangeBtn;
    private Button mStartBtm, mExitBtn;
    private ImageView mCodeView;

    private MyApplication mApp;
    private AlarmBean mAlarmBean;
    private User myUser;

    /**
     * 验证码
     */
    private BPUtil mBpUtil;
    private String mCodeStr;
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

        setContentView(R.layout.activity_stop_code);
        initView();
        initData();
        initEvent();

    }


    private void initEvent() {
        mStartBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myHandler.sendEmptyMessage(0);
                //验证码
                myHandler.sendEmptyMessage(1);
                mClockLayout_ScrollY = mClockLayout.getScrollY() - 250f;
                Animator animator1 = ObjectAnimator.ofFloat(mClockLayout, "translationY", mClockLayout.getScrollY(), mClockLayout_ScrollY);
                animator1.setDuration(500);
                Animator animator2 = ObjectAnimator.ofFloat(mCodeBgView, "alpha", 0.0f, 1.0f);
                animator2.setDuration(300);

                AnimatorSet set = new AnimatorSet();
                set.play(animator2).after(animator1);
                set.start();
            }
        });

        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果验证码输入正确
                if (mEditText.getText().toString().equalsIgnoreCase(mCodeStr)) {
                    //如果是快速闹钟，就把它从闹钟列表中删除
                    if (mAlarmBean.getFlag() == AlarmState.QIUCKAlARM) {
                        mApp.deleteAlarm(CodeStopActivity.this, mAlarmBean);
                        myHandler.sendEmptyMessage(3);
                    } else {
                        //根据闹钟对象来设置下一次闹铃，或者关闭闹铃
                        mApp.setNextAlarm(CodeStopActivity.this, mAlarmBean);
                        myUser.setWeakUpDays(myUser.getWeakupDays() + 1);
                        myHandler.sendEmptyMessage(4);
                    }
                    mApp.getMyUser().getWeakupDays();

                    //关闭铃声和震动
                    mApp.stopAlarmRingingServer();
                    myHandler.sendEmptyMessage(2);
                    Animator animator1 = ObjectAnimator.ofFloat(mClockLayout, "translationY", mClockLayout_ScrollY, mClockLayout.getScrollY());
                    animator1.setDuration(500);
                    Animator animator2 = ObjectAnimator.ofFloat(mCodeBgView, "alpha", 1.0f, 0.0f);
                    animator2.setDuration(300);
                    Animator animator3 = ObjectAnimator.ofFloat(mExitBtn, "alpha", 0.0f, 1.0f);
                    animator2.setDuration(300);

                    AnimatorSet set = new AnimatorSet();
                    set.play(animator3).after(animator1).after(animator2);
                    set.start();

                } else if (mEditText.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(CodeStopActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CodeStopActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                    myHandler.sendEmptyMessage(1);
                }

            }
        });

        mExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAlarmBean.getFlag() == AlarmState.AlARMTYPE_PREVIEW) {
                    Intent intent = new Intent(CodeStopActivity.this, AddAlarmActivity.class);
                    intent.putExtra("Alarm", mAlarmBean);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CodeStopActivity.this, MainActivity.class);
                    startActivity(intent);
                    CodeStopActivity.this.finish();
                }

            }
        });

        mChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myHandler.sendEmptyMessage(1);
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
        mBpUtil = BPUtil.getInstance();
    }

    private void initView() {
        mTimeView = (TextView) findViewById(R.id.stop_code_timetext);
        mMessgeText = (TextView) findViewById(R.id.stop_code_message);
        mCodeBgView = (RelativeLayout) findViewById(R.id.stop_code_bg);
        mClockLayout = (LinearLayout) findViewById(R.id.stop_code_clockbg);
        mOkBtn = (Button) findViewById(R.id.stop_code_ok);
        mEditText = (EditText) findViewById(R.id.stop_code_edittexi1);
        mCodeView = (ImageView) findViewById(R.id.stop_code_codeview);
        mChangeBtn = (Button) findViewById(R.id.stop_code_change);
        mStartBtm = (Button) findViewById(R.id.stop_code_startbtn);
        mExitBtn = (Button) findViewById(R.id.stop_code_exitbtn);
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE || keyCode == KeyEvent.KEYCODE_POWER) {
            //进入小睡处理
            mApp.stopMediaServer(CodeStopActivity.this, mAlarmBean, 0);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    mStartBtm.setEnabled(false);
                    mStartBtm.setVisibility(View.GONE);
                    mMessgeText.setText("请您输入验证码");
                    mMessgeText.setVisibility(View.INVISIBLE);
                    mMessgeText.setEnabled(false);
                    break;

                case 1:
                    Bitmap m = mBpUtil.createBitmap();
                    mCodeStr = mBpUtil.getCode();
                    mCodeView.setImageBitmap(m);
                    break;
                case 2:
                    mExitBtn.setEnabled(true);
                    mExitBtn.setVisibility(View.VISIBLE);
                    mMessgeText.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    mMessgeText.setText("快速闹钟");
                    break;
                case 4:
                    mMessgeText.setText("恭喜您已经坚持按时起床了" + myUser.getWeakupDays() + "天！");
                    break;
            }


        }
    };
}
