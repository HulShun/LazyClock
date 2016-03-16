package com.example.lazyclock.view.acitivities.stopalarm;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lazyclock.Config;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.R;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.bean.User;
import com.example.lazyclock.service.RingtongService;
import com.example.lazyclock.utils.AlarmUtil;
import com.example.lazyclock.utils.BPUtil;
import com.example.lazyclock.view.acitivities.MainActivity;
import com.example.lazyclock.view.acitivities.addalarm.AddAlarmActivity;

/**
 * Created by Administrator on 2015/12/28.
 */
public class CodeStopActivity extends BaseStopActivity {
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


    @Override
    protected void initData() {
        mApp = (MyApplication) getApplication();
        myUser = mApp.getMyUser();
        Intent intent = getIntent();
        mAlarmBean = intent.getParcelableExtra("Alarm");
        mTimeView.setText(mAlarmBean.getTimeStr());
        mBpUtil = BPUtil.getInstance();
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_stop_code);

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
    protected void initEvents() {
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
                AlarmUtil util = AlarmUtil.getInstence();
                //如果验证码输入正确
                if (mEditText.getText().toString().equalsIgnoreCase(mCodeStr)) {
                    //如果是快速闹钟，就把它从闹钟列表中删除
                    if (mAlarmBean.getFlag() == Config.QIUCKAlARM) {
                        util.deleteAlarm(mApp, mAlarmBean);
                        myHandler.sendEmptyMessage(3);
                    } else {
                        //根据闹钟对象来设置下一次闹铃，或者关闭闹铃
                        util.setNextAlarm(mApp, mAlarmBean);
                        myUser.setWeakUpDays(myUser.getWeakupDays() + 1);
                        myHandler.sendEmptyMessage(4);
                    }
                    mApp.getMyUser().getWeakupDays();

                    //关闭铃声和震动
                    util.stopAlarmRingingServer(CodeStopActivity.this);
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
                if (mAlarmBean.getFlag() == Config.AlARMTYPE_PREVIEW) {
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

    @Override
    protected AlarmBean getAlarm() {
        return mAlarmBean;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE || keyCode == KeyEvent.KEYCODE_POWER) {
            //进入小睡处理
            RingtongService.stopMediaServer(CodeStopActivity.this, mAlarmBean, 0);
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
