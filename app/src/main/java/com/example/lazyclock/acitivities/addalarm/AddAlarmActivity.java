package com.example.lazyclock.acitivities.addalarm;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lazyclock.AlarmState;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.R;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.bean.Ring;
import com.example.lazyclock.utils.FileUtil;
import com.example.lazyclock.utils.TimeUtil;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class AddAlarmActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private final static int SHANKSWICTH = 1;
    private final static int MORSLEEPSWICTH = 1;
    private final static int RESETDATE = 3;

    private MyApplication mApp;
    private NumberPicker mHourPicker, mMinsPicker;

    private ScrollView mScrollView;
    private EditText mMyNameView;
    private RelativeLayout mRingTpyeView, mVolumeView, mSleepTimeView, mStopTpyeView, mPreviewView;
    private Button mResetBtm, mSetBtm, mBackBtm;
    private TextView mRingTypeTextView, mStopTypeTextView;
    private TextView mVolumeTextView, mMoreSleepTextView, mSleepTimeTextView, mShakeSwicthTextView;
    private CheckBox mShankSwicth, mMoreSleepSwitch, mWeekSwitch;
    private CheckBox mMonSwicth, mTueSwicth, mWenSwicth, mThuSwicth, mFriSwicth, mSatSwicth, mSunSwicth;
    private TextView mMonText, mThuText, mWenText, mTueText, mFriText, mSatText, mSunText;
    /**
     * 当前闹钟对象
     */
    private AlarmBean mAlarmBean;
    /**
     * 记录所选的星期
     */
    private Set<Integer> days;
    /**
     * 记录小时和分钟
     */
    private String hours, mins;

    /**
     * 所修改的闹钟在闹钟列表中的位置，如果是新建闹钟，就在队尾
     */
    private int changedItemPosition = 0;
    /**
     * 记录铃声选择方式
     */
    private Ring mRing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addalarm);
        initView();

        mApp = (MyApplication) getApplication();
        //获得上个activity传过来的数据
        Intent intent = getIntent();
        if (intent.getFlags() == AlarmState.AlARMTYPE_ADD) {
            mAlarmBean = new AlarmBean(this);
        } else {
            mAlarmBean = intent.getParcelableExtra("Alarm");
        }

        setDate(mAlarmBean);
        initEvent();

        float width= (float) (getResources().getDisplayMetrics().widthPixels* getResources().getDisplayMetrics().density+0.5);

    }


    private void initView() {

        mScrollView = (ScrollView) findViewById(R.id.addalarm_id_scrollview);
        //时间选择
        mHourPicker = (NumberPicker) findViewById(R.id.addalarm_id_hours);
        mMinsPicker = (NumberPicker) findViewById(R.id.addalarm_id_mins);
        mWeekSwitch = (CheckBox) findViewById(R.id.addalarm_id_repeatwicth);

        mMonSwicth = (CheckBox) findViewById(R.id.addalarm_id_mon);
        mTueSwicth = (CheckBox) findViewById(R.id.addalarm_id_tue);
        mWenSwicth = (CheckBox) findViewById(R.id.addalarm_id_wen);
        mThuSwicth = (CheckBox) findViewById(R.id.addalarm_id_thu);
        mFriSwicth = (CheckBox) findViewById(R.id.addalarm_id_fri);
        mSatSwicth = (CheckBox) findViewById(R.id.addalarm_id_sat);
        mSunSwicth = (CheckBox) findViewById(R.id.addalarm_id_sun);

        mMonText = (TextView) findViewById(R.id.addalarm_id_montext);
        mTueText = (TextView) findViewById(R.id.addalarm_id_tuetext);
        mWenText = (TextView) findViewById(R.id.addalarm_id_wentext);
        mThuText = (TextView) findViewById(R.id.addalarm_id_thutext);
        mFriText = (TextView) findViewById(R.id.addalarm_id_fritext);
        mSatText = (TextView) findViewById(R.id.addalarm_id_sattext);
        mSunText = (TextView) findViewById(R.id.addalarm_id_suntext);

        //闹钟名称
        mMyNameView = (EditText) findViewById(R.id.addalarm_id_alarmname);
        mRingTypeTextView = (TextView) findViewById(R.id.addalarm_id_alarmtype);
        mStopTypeTextView = (TextView) findViewById(R.id.addalarm_id_alarmstop);


        //是否开启震动
        mShankSwicth = (CheckBox) findViewById(R.id.addalarm_id_shankswicth);
        mShakeSwicthTextView = (TextView) findViewById(R.id.addalarm_id_shank_text);

        //提醒方式
        mRingTpyeView = (RelativeLayout) findViewById(R.id.addalarm_id_ringselect);

        //闹铃音量
        mVolumeView = (RelativeLayout) findViewById(R.id.addalarm_id_volume);
        mVolumeTextView = (TextView) findViewById(R.id.addalarm_id_volumetext);

        //小睡方式开关
        mMoreSleepSwitch = (CheckBox) findViewById(R.id.addalarm_id_sleepswicth);
        mMoreSleepTextView = (TextView) findViewById(R.id.addalarm_id_sleep_text);

        //小睡时间
        mSleepTimeView = (RelativeLayout) findViewById(R.id.addalarm_id_sleeptime);
        mSleepTimeTextView = (TextView) findViewById(R.id.addalarm_id_sleeptimetext);

        //停止闹铃方式：
        mStopTpyeView = (RelativeLayout) findViewById(R.id.addalarm_id_alarmstoptype);

        mResetBtm = (Button) findViewById(R.id.addalarm_id_reset);
        mSetBtm = (Button) findViewById(R.id.addalarm_id_ok);
        mBackBtm = (Button) findViewById(R.id.addalarm_id_back);

        //闹钟预览
        mPreviewView = (RelativeLayout) findViewById(R.id.addalarm_id_preview);
    }

    private void setDate(AlarmBean bean) {
        days = new TreeSet<>();

        initNumPicker();
        int hours = Integer.valueOf(bean.getTimeStr().substring(0, bean.getTimeStr().indexOf(":")));
        int mins = Integer.valueOf(bean.getTimeStr().substring(bean.getTimeStr().indexOf(":") + 1));
        //设置时间
        mHourPicker.setValue(hours);
        mMinsPicker.setValue(mins);
        //设置日期选择情况

        setToday();
        TimeUtil.getInstance().Strs2Intergers(bean.getDays(), days);
        setDaysView(days);
        //设置闹钟标签名
        mMyNameView.setText(bean.getName());
        //设置震动开关情况
        mShankSwicth.setChecked(bean.isShank());
        myHandler.sendEmptyMessage(SHANKSWICTH);
        //设置响铃方式文本
        mRing = bean.getRing();
        setRingTypeText(bean.getRing().ringType);
        //设置音量文本
        mVolumeTextView.setText(bean.getVolume() + "%");
        //设置停止响铃方式文本
        setStopTypeText(bean.getStopSleepType());
        //设置是否开启小睡情况
        mMoreSleepSwitch.setChecked(bean.isMoreSleep());
        myHandler.sendEmptyMessage(MORSLEEPSWICTH);
        //设置小睡时间
        mSleepTimeTextView.setText(bean.getMoreSleepMins() + "分钟");
        setResetBtmStata(false);      //重置按键 不可点击
    }


    private void initNumPicker() {
        NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                String str;
                if (value < 10) {
                    str = "0" + value;
                } else {
                    str = "" + value;
                }
                return str;
            }
        };


        mHourPicker.setMinValue(0);
        mHourPicker.setMaxValue(23);
        setNumberPickerAttr(mHourPicker, "mSelectionDivider", new ColorDrawable(this.getResources().getColor(R.color.numberpicker_divider)));
        // mHourPicker.setDividerDrawable(null);
        mHourPicker.setValue(8);
        mHourPicker.setFormatter(formatter);

        mMinsPicker.setMinValue(0);
        mMinsPicker.setMaxValue(59);
        setNumberPickerAttr(mMinsPicker, "mSelectionDivider", new ColorDrawable(this.getResources().getColor(R.color.numberpicker_divider)));
        mMinsPicker.setValue(0);
        mMinsPicker.setFormatter(formatter);

        hours = "08";
        mins = "00";
    }

    private void initEvent() {

        mHourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (picker.getValue() < 10) {
                    hours = "0" + picker.getValue();
                } else {
                    hours = "" + picker.getValue();
                }

                setResetBtmStata(true);
            }
        });
        mMinsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (picker.getValue() < 10) {
                    mins = "0" + picker.getValue();
                } else {
                    mins = "" + picker.getValue();
                }

                setResetBtmStata(true);
            }
        });

        mBackBtm.setOnClickListener(this);
        mSetBtm.setOnClickListener(this);
        mResetBtm.setOnClickListener(this);

        //星期选择
        mMonSwicth.setOnCheckedChangeListener(this);
        mTueSwicth.setOnCheckedChangeListener(this);
        mWenSwicth.setOnCheckedChangeListener(this);
        mThuSwicth.setOnCheckedChangeListener(this);
        mFriSwicth.setOnCheckedChangeListener(this);
        mSatSwicth.setOnCheckedChangeListener(this);
        mSunSwicth.setOnCheckedChangeListener(this);


        //闹钟重复开关
        mWeekSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int m = isChecked ? -1 : 0;
                mAlarmBean.setNextDayPosition(m);
            }
        });

        //闹钟标签
        mMyNameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (v.getText() != "") {
                    setResetBtmStata(true);
                }
                return false;
            }
        });
        mMyNameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mScrollView.scrollBy(0, 100);
                } else {
                    mScrollView.scrollBy(0, -100);
                }
            }
        });

        //震动开关
        mShankSwicth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAlarmBean.setShank(isChecked);
                myHandler.sendEmptyMessage(1);
                setResetBtmStata(true);
            }
        });

        //铃声选择
        mRingTpyeView.setOnClickListener(this);
        //音量调整
        mVolumeView.setOnClickListener(this);
        //关闭闹铃方式
        mStopTpyeView.setOnClickListener(this);
        //小睡开关
        mMoreSleepSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAlarmBean.setMoreSleep(isChecked);
                myHandler.sendEmptyMessage(2);
            }
        });
        //贪睡时间选择
        mSleepTimeView.setOnClickListener(this);

        //预览闹钟
        mPreviewView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回按钮
            case R.id.addalarm_id_back:
                this.finish();
                break;
            //确定按钮
            case R.id.addalarm_id_ok:
                boolean flag = safeAlarm();
                if (!flag) {
                    return;
                }
                Intent intent4 = getIntent();
                intent4.setFlags(AlarmState.AlARMTYPE_EDIT);
                intent4.putExtra("alarm", changedItemPosition);
                setResult(AlarmState.AlARMTYPE_EDIT, intent4);
                this.finish();
                break;
            //重置按钮
            case R.id.addalarm_id_reset:
                myHandler.sendEmptyMessage(RESETDATE);
                break;

            //铃声选择
            case R.id.addalarm_id_ringselect:
                Intent intent = new Intent(AddAlarmActivity.this, RingTpyeActivity.class);
                intent.putExtra("mRing", mRing);
                startActivityForResult(intent, mAlarmBean.getRing().ringType);
                break;
            //音量选择
            case R.id.addalarm_id_volume:
                Intent intent1 = new Intent(AddAlarmActivity.this, VolumeActivity.class);
                intent1.putExtra("Alarm", mAlarmBean);
                startActivityForResult(intent1, AlarmState.ADDALARM_TYPE_ALARMVOLUME);
                break;

            //贪睡时间
            case R.id.addalarm_id_sleeptime:
                Intent intent2 = new Intent(AddAlarmActivity.this, SleepTimeActivity.class);
                intent2.putExtra("sleeptime", mAlarmBean);
                startActivityForResult(intent2, AlarmState.ADDALARM_TYPE_MORESLEEPTIME);
                break;

            //闹铃终止方式
            case R.id.addalarm_id_alarmstoptype:
                Intent intent3 = new Intent(AddAlarmActivity.this, StopTpyeActivity.class);
                intent3.putExtra("stoptype", mAlarmBean);
                startActivityForResult(intent3, AlarmState.ADDALARM_TYPE_STOPSELECT);
                break;
            //闹钟预览
            case R.id.addalarm_id_preview:
                TimeUtil.getInstance().addMillsToAlarmBean(0, mAlarmBean);
                mAlarmBean.setFlag(AlarmState.AlARMTYPE_PREVIEW);
                mApp.setAlarmToSystem(AddAlarmActivity.this, mAlarmBean);
                break;
        }
    }

    /**
     * @return
     */
    private boolean safeAlarm() {
        TimeUtil util = TimeUtil.getInstance();
        String lastTimeStr;
        //闹钟是否重复保存
        mAlarmBean.setRepeat(mWeekSwitch.isChecked());
        //时间保存
        mAlarmBean.setStartTime(System.currentTimeMillis());
        mAlarmBean.setTimeStr(hours + ":" + mins);   //仅 xx:xx 的时间
        //设置星期
        mAlarmBean.setDays(TimeUtil.getInstance().Intergers2Strs(days));

        //设置下一次闹铃的完整时间
        int weekInt = (int) days.toArray()[mAlarmBean.getNextDayPosition()];
        long time = util.getFullTime(mAlarmBean.getTimeStr(), weekInt);
        mAlarmBean.setTimeInMills(time);
        //设置下一次闹铃day的下标
        mAlarmBean.setNextDayPosition(mAlarmBean.getNextDayPosition() + 1);

        //设置距离下一次还有多久
        lastTimeStr = util.calculateTime(mAlarmBean.getTimeInMills(), mAlarmBean.getStartTime());
        mAlarmBean.setRemainingTime(lastTimeStr);
        //闹钟名称保存
        if (mMyNameView.getText().toString().equals("")) {
            int m = mApp.getDefaultAlarmCount();
            mApp.setDefaultAlarmCount(m + 1);
            mAlarmBean.setName(mMyNameView.getHint().toString());
        } else {
            mAlarmBean.setName(mMyNameView.getText().toString());
        }
        //保存铃声对象
        mAlarmBean.setRing(mRing);


        List<AlarmBean> lists = mApp.getAlarDates();

        //如果是新添加的闹铃，只需查询已存在的闹铃是不是存在名称一样的
        if (mAlarmBean.getFlag() == AlarmState.AlARMTYPE_ADD) {
            if (lists != null) {
                for (AlarmBean temp : lists) {
                    if (temp.getName().equals(mAlarmBean.getName())) {
                        Toast.makeText(this, "闹铃名称与其他闹铃重名，请更换。", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
            mAlarmBean.setFlag(AlarmState.AlARMTYPE_EDIT);
            mApp.addAlarmToList(mAlarmBean);
        }

        //闹铃传给系统闹钟管理器
        mApp.setAlarmToSystem(this, mAlarmBean);
        mApp.saveToFile(getExternalFilesDir(null).getPath(), FileUtil.FLAG_ALARM);

        Toast.makeText(getApplicationContext(), "下一次闹铃是" + lastTimeStr, Toast.LENGTH_LONG).show();
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED || resultCode == RESULT_FIRST_USER) {
            return;
        }

        //每个activity带数据跳转的时候都携带一个id标识，用来判断是哪个acitivity跳回来
        int tpye = resultCode;
        switch (tpye) {

            //铃声方式选择
            case AlarmState.ADDALARM_TYPE_RINGSELECT:
                mRing = data.getParcelableExtra("mRing");
                mAlarmBean.setRing(mRing);
                setRingTypeText(mRing.ringType);
                break;
            //音量选择
            case AlarmState.ADDALARM_TYPE_ALARMVOLUME:
                mAlarmBean = data.getParcelableExtra("volume");
                mVolumeTextView.setText(mAlarmBean.getVolume() + "%");
                break;
            //小睡唤醒模式选择
            case AlarmState.ADDALARM_TYPE_MORESLEEPTYPE:

                break;
            //小睡时长选择
            case AlarmState.ADDALARM_TYPE_MORESLEEPTIME:
                mAlarmBean = data.getParcelableExtra("sleeptime");
                mSleepTimeTextView.setText(mAlarmBean.getMoreSleepMins() + "分钟");
                break;
            //停止闹铃方式选择
            case AlarmState.ADDALARM_TYPE_STOPSELECT:
                mAlarmBean = data.getParcelableExtra("stoptype");
                mAlarmBean.setStopSleepType(mAlarmBean.getStopSleepType());
                setStopTypeText(mAlarmBean.getStopSleepType());
                break;
        }
        setResetBtmStata(true);

    }


    private void setStopTypeText(int stopSleepType) {
        switch (stopSleepType) {
            case AlarmState.STOPSELECT_CLICK:
                mStopTypeTextView.setText(getResources().getString(R.string.stoptype_touch));
                break;
            case AlarmState.STOPSELECT_CODE:
                mStopTypeTextView.setText(getResources().getString(R.string.stoptype_code));
                break;
            case AlarmState.STOPSELECT_MATH:
                mStopTypeTextView.setText(getResources().getString(R.string.stoptype_math));
                break;

            case AlarmState.STOPSELECT_SHANK:
                mStopTypeTextView.setText(getResources().getString(R.string.stoptype_shake));
                break;
        }

    }


    /**
     * 根据参数来修改【响铃方式】的文本显示
     *
     * @param ringTpye 响铃方式
     */
    private void setRingTypeText(int ringTpye) {

        switch (ringTpye) {
            case AlarmState.RINGSELECT_SILENT:
                mRingTypeTextView.setText(getResources().getString(R.string.ringtype_silent));
                break;

            case AlarmState.RINGSELECT_RING:
                mRingTypeTextView.setText(getResources().getString(R.string.ringtype_ring));
                // getRingtone(data);
                break;
        }
    }


    /**
     * 改变重置按钮的状态
     *
     * @param isreset
     */
    private void setResetBtmStata(boolean isreset) {
        if (isreset) {
            // mResetBtm.setBackground();
            mResetBtm.setClickable(true);
            mAlarmBean.setAlarmChanged(true);
        } else {
            // mResetBtm.setBackground();
            mResetBtm.setClickable(false);
            mAlarmBean.setAlarmChanged(false);
        }
    }


    /**
     * 通过反射机制设置NumberPicker的属性值
     *
     * @param numberPicker
     * @param name         要修改的属性值的名称 如："mSelectionDivider"
     */
    private void setNumberPickerAttr(NumberPicker numberPicker, String name, Object object) {
        NumberPicker picker = numberPicker;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals(name)) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(picker, object);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


    private void setToday() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int weekInt = c.get(Calendar.DAY_OF_WEEK);
        weekInt = weekInt % 7 - 1;
        if (days == null) {
            days = new TreeSet<>();
        }
        switch (weekInt) {
            case 1:
                mMonSwicth.setBackgroundResource(R.drawable.addalarm_week_check_today);
                days.add(1);
                break;
            case 2:
                mTueSwicth.setBackgroundResource(R.drawable.addalarm_week_check_today);
                days.add(2);
                break;
            case 3:
                mWenSwicth.setBackgroundResource(R.drawable.addalarm_week_check_today);
                days.add(3);
                break;
            case 4:
                mThuSwicth.setBackgroundResource(R.drawable.addalarm_week_check_today);
                days.add(4);
                break;

            case 5:
                mFriSwicth.setBackgroundResource(R.drawable.addalarm_week_check_today);
                days.add(5);
                break;
            case 6:
                mSatSwicth.setBackgroundResource(R.drawable.addalarm_week_check_today);
                days.add(6);
                break;
            case 7:
                mSunSwicth.setBackgroundResource(R.drawable.addalarm_week_check_today);
                days.add(7);
                break;
        }
    }


    private void setDaysView(Set<Integer> days) {
        for (int temp : days) {
            switch (temp) {
                case 1:
                    mMonSwicth.setChecked(true);
                    mMonText.setTextColor(getResources().getColor(R.color.white));
                    break;
                case 2:
                    mTueSwicth.setChecked(true);
                    mTueText.setTextColor(getResources().getColor(R.color.white));
                    break;
                case 3:
                    mWenSwicth.setChecked(true);
                    mWenText.setTextColor(getResources().getColor(R.color.white));
                    break;
                case 4:
                    mThuSwicth.setChecked(true);
                    mThuText.setTextColor(getResources().getColor(R.color.white));
                    break;
                case 5:
                    mFriSwicth.setChecked(true);
                    mFriText.setTextColor(getResources().getColor(R.color.white));
                    break;
                case 6:
                    mSatSwicth.setChecked(true);
                    mSatText.setTextColor(getResources().getColor(R.color.white));
                    break;
                case 7:
                    mSunSwicth.setChecked(true);
                    mSunText.setTextColor(getResources().getColor(R.color.white));
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.addalarm_id_mon:
                if (isChecked) {
                    days.add(1);
                    mMonText.setTextColor(getResources().getColor(R.color.white));
                } else {
                    days.remove(1);
                    mMonText.setTextColor(getResources().getColor(R.color.addalarm_week_unselect));
                }
                break;
            case R.id.addalarm_id_tue:
                if (isChecked) {
                    days.add(2);
                    mTueText.setTextColor(getResources().getColor(R.color.white));
                } else {
                    days.remove(2);
                    mTueText.setTextColor(getResources().getColor(R.color.addalarm_week_unselect));
                }
                break;
            case R.id.addalarm_id_wen:
                if (isChecked) {
                    days.add(3);
                    mWenText.setTextColor(getResources().getColor(R.color.white));
                } else {
                    days.remove(3);
                    mWenText.setTextColor(getResources().getColor(R.color.addalarm_week_unselect));
                }
                break;
            case R.id.addalarm_id_thu:
                if (isChecked) {
                    mThuText.setTextColor(getResources().getColor(R.color.white));
                    days.add(4);
                } else {
                    days.remove(4);
                    mThuText.setTextColor(getResources().getColor(R.color.addalarm_week_unselect));
                }
                break;
            case R.id.addalarm_id_fri:
                if (isChecked) {
                    days.add(5);
                    mFriText.setTextColor(getResources().getColor(R.color.white));
                } else {
                    days.remove(5);
                    mFriText.setTextColor(getResources().getColor(R.color.addalarm_week_unselect));
                }
                break;
            case R.id.addalarm_id_sat:
                if (isChecked) {
                    days.add(6);
                    mSatText.setTextColor(getResources().getColor(R.color.white));
                } else {
                    days.remove(6);
                    mSatText.setTextColor(getResources().getColor(R.color.addalarm_week_unselect));
                }
                break;
            case R.id.addalarm_id_sun:
                if (isChecked) {
                    days.add(7);
                    mSunText.setTextColor(getResources().getColor(R.color.white));
                } else {
                    days.remove(7);
                    mSunText.setTextColor(getResources().getColor(R.color.addalarm_week_unselect));

                }
                break;
        }

    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (mShankSwicth.isChecked()) {
                        mShakeSwicthTextView.setText("开启");
                    } else {
                        mShakeSwicthTextView.setText("关闭");
                    }
                    break;

                case 2:
                    if (mMoreSleepSwitch.isChecked()) {
                        mMoreSleepTextView.setText("开启");
                    } else {
                        mMoreSleepTextView.setText("关闭");
                    }
                    break;
                case 3:
                    mAlarmBean = new AlarmBean(AddAlarmActivity.this);
                    setDate(mAlarmBean);
            }
        }
    };


}
