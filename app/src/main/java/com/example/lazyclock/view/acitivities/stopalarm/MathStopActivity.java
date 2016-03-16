package com.example.lazyclock.view.acitivities.stopalarm;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lazyclock.Config;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.R;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.bean.Mathematic;
import com.example.lazyclock.bean.User;
import com.example.lazyclock.db.dao.MathDao;
import com.example.lazyclock.service.RingtongService;
import com.example.lazyclock.utils.AlarmUtil;
import com.example.lazyclock.utils.LogUtil;
import com.example.lazyclock.view.acitivities.MainActivity;
import com.example.lazyclock.view.acitivities.addalarm.AddAlarmActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 摇晃解锁
 * Created by Administrator on 2015/12/28.
 */
public class MathStopActivity extends BaseStopActivity {
    private static final int MESSAGE_WHAT_START = 0;
    private static final int MESSAGE_WHAT_EXITSHOW = 1;
    private static final int MESSAGE_WHAT_QIUCK = 2;
    private static final int MESSAGE_WHAT_STOP = 3;
    private static final int MESSAGE_WHAT_SHAKE = 4;
    private static final int MESSAGE_WHAT_ANIMI = 5;
    private static final int MESSAGE_WHAT_MATH = 6;


    private LinearLayout mClockLayout;
    private float mClockLayout_ScrollY;
    /**
     * 闹钟图标板块移动距离
     */
    private float mClockLayout_moveBy = 180.0f;
    /**
     * 闹钟板块缩放大小
     */
    private float mClockLayout_scale = 0.8f;


    /**
     * 预留 答题之后画面停留时间
     */
    private long updateMathTime = 500;

    private LinearLayout mMathBgView;
    private Button mStartBtm, mExitBtn;
    private ImageView mAImage, mBImage, mCImage, mDImage;
    private RelativeLayout mALayout, mBLayout, mCLayout, mDLayout;
    private TextView mTopicView, mAView, mBView, mCView, mDView;
    private TextView mTimeView, mMessgeText;

    private MyApplication mApp;
    private User myUser;
    private AlarmBean mAlarmBean;
    private Mathematic mMath;
    private MathDao mMathDao;

    /**
     * 答对题目数量
     */
    private int count = 0;
    private boolean isFromStartBtn = true;


    /**
     * 闹钟上移，并在动画完成后加载题目
     */
    private void clockMoveUp() {
        mClockLayout_ScrollY = mClockLayout.getScrollY() - mClockLayout_moveBy;
        Animator animator1 = ObjectAnimator.ofFloat(mClockLayout, "translationY", mClockLayout.getScrollY(), mClockLayout_ScrollY);
        animator1.setDuration(500);
        Animator animator3 = ObjectAnimator.ofFloat(mClockLayout, "scaleX", 1.0f, mClockLayout_scale);
        animator3.setDuration(500);
        Animator animator4 = ObjectAnimator.ofFloat(mClockLayout, "scaleY", 1.0f, mClockLayout_scale);
        animator4.setDuration(500);
        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //加载题目
                Message message = Message.obtain();
                message.arg1 = 0;
                message.what = MESSAGE_WHAT_MATH;
                myHandler.sendMessage(message);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet set = new AnimatorSet();
        set.play(animator1).with(animator3).with(animator4);
        set.start();
    }

    @Override
    protected void initData() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        mApp = (MyApplication) getApplication();
        myUser = mApp.getMyUser();

        Intent intent = getIntent();
        mAlarmBean = intent.getParcelableExtra("Alarm");
        mTimeView.setText(mAlarmBean.getTimeStr());

        mMathDao = new MathDao(this);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_stop_math);

        mTimeView = (TextView) findViewById(R.id.stop_math_timetext);
        mMessgeText = (TextView) findViewById(R.id.stop_math_message);
        mMathBgView = (LinearLayout) findViewById(R.id.stop_math_bg);
        mClockLayout = (LinearLayout) findViewById(R.id.stop_math_clockbg);
        mStartBtm = (Button) findViewById(R.id.stop_math_startbtn);
        mExitBtn = (Button) findViewById(R.id.stop_math_exitbtn);
        mMessgeText = (TextView) findViewById(R.id.stop_math_message);

        mTopicView = (TextView) findViewById(R.id.stop_math_topic);
        mAImage = (ImageView) findViewById(R.id.stop_math_checkA_image);
        mBImage = (ImageView) findViewById(R.id.stop_math_checkB_image);
        mCImage = (ImageView) findViewById(R.id.stop_math_checkC_image);
        mDImage = (ImageView) findViewById(R.id.stop_math_checkD_image);

        mAView = (TextView) findViewById(R.id.stop_math_checkA_text);
        mBView = (TextView) findViewById(R.id.stop_math_checkB_text);
        mCView = (TextView) findViewById(R.id.stop_math_checkC_text);
        mDView = (TextView) findViewById(R.id.stop_math_checkD_text);

        mALayout = (RelativeLayout) findViewById(R.id.stop_math_checkA);
        mBLayout = (RelativeLayout) findViewById(R.id.stop_math_checkB);
        mCLayout = (RelativeLayout) findViewById(R.id.stop_math_checkC);
        mDLayout = (RelativeLayout) findViewById(R.id.stop_math_checkD);
    }

    @Override
    protected void initEvents() {
        mStartBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myHandler.sendEmptyMessage(MESSAGE_WHAT_START);
                //闹钟移动向上并加载题目
                clockMoveUp();

            }
        });


        mExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAlarmBean.getFlag() == Config.AlARMTYPE_PREVIEW) {
                    Intent intent = new Intent(MathStopActivity.this, AddAlarmActivity.class);
                    intent.putExtra("Alarm", mAlarmBean);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MathStopActivity.this, MainActivity.class);
                    startActivity(intent);
                    MathStopActivity.this.finish();
                }
            }
        });

    }

    @Override
    protected AlarmBean getAlarm() {
        return mAlarmBean;
    }



    private void setMathToView() {
        setLayoutEnable(true);
        LogUtil.i("db", "加载题目");

        mMath = mMathDao.getMathematicInRandom();
        String topic = "题目" + (count + 1) + ":  " + mMath.getTopic();
        mTopicView.setText(topic);
        mAView.setText("A、" + mMath.getAnswerA());
        mBView.setText("B、" + mMath.getAnswerB());
        mCView.setText("C、" + mMath.getAnswerC());

        if (mMath.getAnswerD().equals("")) {
            mDLayout.setVisibility(View.GONE);
        } else {
            mDView.setText("D、" + mMath.getAnswerD());
        }
        setRightClick(mMath);

    }

    private void setRightClick(Mathematic math) {
        switch (math.getRightAnswer()) {
            case "A":
                mAImage.setImageDrawable(getResources().getDrawable(R.drawable.stop_math_right));
                mBImage.setImageDrawable(getResources().getDrawable(R.drawable.stop_math_wrong));
                mCImage.setImageDrawable(getResources().getDrawable(R.drawable.stop_math_wrong));
                mDImage.setImageDrawable(getResources().getDrawable(R.drawable.stop_math_wrong));
                mALayout.setOnClickListener(new RightClick(mAImage));
                mBLayout.setOnClickListener(new WrongClick(mBImage));
                mCLayout.setOnClickListener(new WrongClick(mCImage));
                mDLayout.setOnClickListener(new WrongClick(mDImage));

                break;
            case "B":
                mBImage.setImageDrawable(getResources().getDrawable(R.drawable.stop_math_right));

                mAImage.setImageDrawable(getResources().getDrawable(R.drawable.stop_math_wrong));
                mCImage.setImageDrawable(getResources().getDrawable(R.drawable.stop_math_wrong));
                mDImage.setImageDrawable(getResources().getDrawable(R.drawable.stop_math_wrong));

                mBLayout.setOnClickListener(new RightClick(mBImage));
                mALayout.setOnClickListener(new WrongClick(mAImage));
                mCLayout.setOnClickListener(new WrongClick(mCImage));
                mDLayout.setOnClickListener(new WrongClick(mDImage));
                break;
            case "C":
                mCImage.setImageDrawable(getResources().getDrawable(R.drawable.stop_math_right));

                mAImage.setImageDrawable(getResources().getDrawable(R.drawable.stop_math_wrong));
                mBImage.setImageDrawable(getResources().getDrawable(R.drawable.stop_math_wrong));
                mDImage.setImageDrawable(getResources().getDrawable(R.drawable.stop_math_wrong));

                mCLayout.setOnClickListener(new RightClick(mCImage));
                mALayout.setOnClickListener(new WrongClick(mAImage));
                mBLayout.setOnClickListener(new WrongClick(mBImage));
                mDLayout.setOnClickListener(new WrongClick(mDImage));
                break;
            case "D":
                mDImage.setImageDrawable(getResources().getDrawable(R.drawable.stop_math_right));

                mAImage.setImageDrawable(getResources().getDrawable(R.drawable.stop_math_wrong));
                mBImage.setImageDrawable(getResources().getDrawable(R.drawable.stop_math_wrong));
                mCImage.setImageDrawable(getResources().getDrawable(R.drawable.stop_math_wrong));

                mDLayout.setOnClickListener(new RightClick(mDImage));
                mALayout.setOnClickListener(new WrongClick(mAImage));
                mBLayout.setOnClickListener(new WrongClick(mBImage));
                mCLayout.setOnClickListener(new WrongClick(mCImage));
                break;
        }
        setImageVisible(View.INVISIBLE);
    }

    private void setLayoutEnable(boolean flag) {
        mALayout.setEnabled(flag);
        mBLayout.setEnabled(flag);
        mCLayout.setEnabled(flag);
        mDLayout.setEnabled(flag);
    }

    private void setImageVisible(int flag) {
        mBImage.setVisibility(flag);
        mAImage.setVisibility(flag);
        mCImage.setVisibility(flag);
        mDImage.setVisibility(flag);
    }


    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WHAT_START:
                    mStartBtm.setEnabled(false);
                    mStartBtm.setVisibility(View.GONE);
                    break;

                case MESSAGE_WHAT_EXITSHOW:
                    mExitBtn.setEnabled(true);
                    mExitBtn.setVisibility(View.VISIBLE);
                    mMessgeText.setVisibility(View.VISIBLE);
                    mMathBgView.setVisibility(View.GONE);

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
                    clockMoveDown();
                    break;

                case MESSAGE_WHAT_MATH:

                    if (msg.arg1 < 3) {
                        mathShowAnim();
                    }
                    mMessgeText.setText("已答对" + msg.arg1 + "题");

                    break;
            }


        }
    };

    /**
     * 数学题刷新及动画
     */
    private void mathShowAnim() {
        //进入动画
        Animator math_anim1 = ObjectAnimator.ofFloat(mMathBgView, "alpha", 0.0f, 1.0f);
        math_anim1.setDuration(300);
        //出去动画
        Animator math_anim2 = ObjectAnimator.ofFloat(mMathBgView, "alpha", 1.0f, 0.0f);
        math_anim2.setDuration(300);
        math_anim2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setMathToView();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        AnimatorSet set1 = new AnimatorSet();
        if (isFromStartBtn) {
            isFromStartBtn = false;
            setMathToView();
            set1.play(math_anim1);
        } else {
            set1.play(math_anim1).after(math_anim2);
        }
        set1.start();
    }

    /**
     * 闹钟板块下移动画 ，题目板块消失
     */
    private void clockMoveDown() {
        Animator animator1 = ObjectAnimator.ofFloat(mClockLayout, "translationY", mClockLayout_ScrollY, mClockLayout.getScrollY());
        animator1.setDuration(500);
        Animator animator5 = ObjectAnimator.ofFloat(mClockLayout, "scaleX", mClockLayout_scale, 1f);
        animator5.setDuration(500);
        Animator animator4 = ObjectAnimator.ofFloat(mClockLayout, "scaleY", mClockLayout_scale, 1f);
        animator4.setDuration(500);
        //题板消失
        Animator anim_alpha = ObjectAnimator.ofFloat(mMathBgView, "alpha", 1.0f, 0.0f);
        anim_alpha.setDuration(500);
        //退出按钮出现
        Animator anim_alpha1 = ObjectAnimator.ofFloat(mExitBtn, "alpha", 0.0f, 1.0f);
        anim_alpha.setDuration(500);
        AnimatorSet set = new AnimatorSet();
        set.play(animator1).with(animator4).with(animator5).with(anim_alpha).before(anim_alpha1);
        set.start();
    }


    class RightClick implements View.OnClickListener {
        private ImageView imageView;

        public RightClick(ImageView view) {
            imageView = view;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(MathStopActivity.this, "答对了！", Toast.LENGTH_SHORT).show();
            imageView.setVisibility(View.VISIBLE);

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    AlarmUtil util = AlarmUtil.getInstence();
                    count++;
                    if (count >= 3) {
                        Message message = Message.obtain();
                        message.arg1 = count;
                        message.what = MESSAGE_WHAT_MATH;
                        myHandler.sendMessage(message);
                        myHandler.sendEmptyMessage(MESSAGE_WHAT_EXITSHOW);
                        myHandler.sendEmptyMessage(MESSAGE_WHAT_ANIMI);
                        mMathDao.close();

                        //如果是快速闹钟，就把它从闹钟列表中删除
                        if (mAlarmBean.getFlag() == Config.QIUCKAlARM) {
                            util.deleteAlarm(mApp, mAlarmBean);
                            myHandler.sendEmptyMessage(MESSAGE_WHAT_QIUCK);
                        } else {
                            //根据闹钟对象来设置下一次闹铃，或者关闭闹铃
                            util.setNextAlarm(mApp, mAlarmBean);
                            myHandler.sendEmptyMessage(MESSAGE_WHAT_STOP);
                        }
                        //关闭铃声和震动
                        util.stopAlarmRingingServer(MathStopActivity.this);
                        mApp.getMyUser().getWeakupDays();
                    } else {
                        Message message = Message.obtain();
                        message.arg1 = count;
                        message.what = MESSAGE_WHAT_MATH;
                        myHandler.sendMessage(message);
                    }
                }
            }, updateMathTime);

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE || keyCode == KeyEvent.KEYCODE_POWER) {
            //进入小睡处理
            RingtongService.stopMediaServer(MathStopActivity.this, mAlarmBean, 0);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    class WrongClick implements View.OnClickListener {
        private ImageView imageView;

        public WrongClick(ImageView view) {
            imageView = view;
        }

        @Override
        public void onClick(View v) {
            //显示错误标识
            imageView.setVisibility(View.VISIBLE);
            //错误后所有选项不能选择
            setLayoutEnable(false);
            //1s后刷新新题目
            Toast.makeText(MathStopActivity.this, "答错了哟！", Toast.LENGTH_SHORT).show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = Message.obtain();
                    message.arg1 = count;
                    message.what = MESSAGE_WHAT_MATH;
                    myHandler.sendMessage(message);
                }
            }, updateMathTime);
        }
    }


} //all
