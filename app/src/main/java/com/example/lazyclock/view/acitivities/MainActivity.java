package com.example.lazyclock.view.acitivities;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lazyclock.Config;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.R;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.bean.User;
import com.example.lazyclock.bean.Weather;
import com.example.lazyclock.others.OnTimeChangedListener;
import com.example.lazyclock.others.Task.DifferTimeTask;
import com.example.lazyclock.others.Task.QuickTimeTextTask;
import com.example.lazyclock.utils.AlarmUtil;
import com.example.lazyclock.utils.FileUtil;
import com.example.lazyclock.utils.TimeUtil;
import com.example.lazyclock.utils.WeatherUtil;
import com.example.lazyclock.view.acitivities.addalarm.AddAlarmActivity;
import com.example.lazyclock.view.acitivities.headportrait.EditHeadActivity;
import com.example.lazyclock.view.acitivities.settings.LockActivity;
import com.example.lazyclock.view.acitivities.settings.MainSettingActivity;
import com.example.lazyclock.view.adapter.AlarmAdapter;
import com.example.lazyclock.view.customview.DividerItemDecoration;
import com.example.lazyclock.view.customview.RoundImageView;

import java.io.File;
import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    private MyApplication mApp;
    private User myUser;
    private Weather mWeather;
    private WeatherUtil mWeatherUtil;

    private Button mSettingBtm, mAddAlarmBtm;

    /**
     * 主页面控件
     */
    private TextView mUserName, mMainText1, mWeakupDay, mMainText2, mLevelView;
    private RoundImageView myHeadView;
    private TextView mTempView, mMessageView;
    private ImageView mTempImageView;
    private RelativeLayout mWeatherLayout;
    private ViewStub mViewStub;
    private View mbgView;

    /**
     * 最近闹钟
     */
    private TextView mRecentlyTimeView, mRecenntlyLastView;
    private RecyclerView mRecyclerView;
    private AlarmAdapter mAdapter;

    /**
     * 快速闹铃
     */
    private PopupWindow mLastingwindow;
    private CheckBox mQiuckBtn1, mQiuckBtn2, mQiuckBtn3, mQiuckBtn4, mQiuckBtn5, mQiuckBtn6, mQiuckBtn7, mQiuckBtn8;
    private TextView mQiuckLastTime;
    /**
     * 快速闹铃的计时器
     */
    private Timer mTimer;
    private boolean isTimerWork;
    /**
     * 后台计算闹铃剩余时间的计时器
     */
    private Timer mLastTimer;
    private DifferTimeTask mDifferTimeTask;
    private boolean isTaskWork;

    private QuickTimeTextTask mTask;
    /**
     * 侧边栏
     */
    private DrawerLayout mDrawerLayout;

    private CheckBox mMenuQiuckClock, mMoreSettingBtn, mLockBtn;
    private ImageButton mExitBtm;
    private List<AlarmBean> mAlarmBeanList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initEvent();

    }


    private void initData() {
        mApp = (MyApplication) getApplication();
        mAlarmBeanList = mApp.getAlarDates();

        //后台剩余时间计算
        mLastTimer = new Timer();
        mDifferTimeTask = new DifferTimeTask(mApp);

        mLastTimer.schedule(mDifferTimeTask, 0, 30 * 1000);

        if (mAlarmBeanList != null && !mAlarmBeanList.isEmpty()) {
            initRecyclerView(mAlarmBeanList);
        } else {
            showViewSub();
        }
        myUser = mApp.getMyUser();
        mWeather = mApp.getWeather();
        mWeatherUtil = new WeatherUtil(this);

        myHandler.sendEmptyMessage(Config.UPDATE_UI_LISTVIEW);
        //坚持起床了几天
        String temp = String.valueOf(myUser.getWeakupDays());
        if (temp.equals("")) {
            mWeakupDay.setText("0");
        } else {
            mWeakupDay.setText(temp);
        }

        mLevelView.setText("Lv" + myUser.level);
        //用户名
        mUserName.setText(myUser.mName);
        String path = myUser.mHeadPath;
        if (path != null && !path.equals("")) {
            myHeadView.setImageURI(Uri.fromFile(new File(myUser.mHeadPath)));
        } else {
            myHeadView.setImageDrawable(getResources().getDrawable(R.drawable.main_defaulthead));
        }
        if (mWeather != null) {
            setData2WeatherView();
        }

    }

    private void showViewSub() {
        if (mbgView == null) {
            mbgView = mViewStub.inflate();
        }
        mbgView.setVisibility(View.VISIBLE);
    }

    private void hideViewSub() {
        if (mbgView != null) {
            mbgView.setVisibility(View.GONE);
        }
    }

    private void setData2WeatherView() {
        String temp = mWeather.getNow().getTemp();
        mTempView.setText(temp + "  " + mWeather.getNow().getText());
        mMessageView.setText(mWeather.getNow().getFluMessage());
        mWeatherUtil.setLargeImageToWeather(mWeather.getNow().getText(), mTempImageView);
    }

    private void initRecyclerView(List<AlarmBean> list) {
        mAdapter = new AlarmAdapter(list, this);
        if (mAdapter.getItemCount() != 0) {
            hideViewSub();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);   //布局管理

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //listView的Item
        mAdapter.setOnItemClickListener(new AlarmAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                if (myUser.isLock) {
                    Toast.makeText(MainActivity.this, "闹铃设置被锁定，请到设置中解锁。", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent in = new Intent(MainActivity.this, ItemClickActivity.class);
                in.putExtra("Alarm", mAlarmBeanList.get(position));
                startActivityForResult(in, Config.AlARMTYPE_EDIT);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initEvent() {

        mDifferTimeTask.setOnTimeChangedListener(new OnTimeChangedListener() {
            @Override
            public void onTimeChange(List<AlarmBean> data) {
                mAlarmBeanList = data;
                //刷新UI
                Log.d("时间改变", "剩余时间修改");
                myHandler.sendEmptyMessage(Config.UPDATE_UI_LISTVIEW);
                myHandler.sendEmptyMessage(Config.UPDATE_UI_LISTVIEWBG);
            }
        });

        mSettingBtm.setOnClickListener(this);
        mAddAlarmBtm.setOnClickListener(this);


        myHeadView.setOnClickListener(this);

        mWeatherLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                startActivityForResult(intent, Config.WEATHER_FLAG);
            }
        });

        //侧边栏的按钮
        mExitBtm.setOnClickListener(this);

        mMenuQiuckClock.setOnClickListener(this);
        mMenuQiuckClock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mLastingwindow.isShowing()) {
                    mMenuQiuckClock.setChecked(true);
                } else {
                    mMenuQiuckClock.setChecked(false);
                }
            }
        });
        mMoreSettingBtn.setOnClickListener(this);
        //锁定按钮
        mLockBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //防止setChecked()触发事件
                if (!buttonView.isPressed()) {
                    return;
                }
                if (myUser.passwordsMD5.equals("")) {
                    Toast.makeText(MainActivity.this, "请先在[更多设置]中设置锁定密码！", Toast.LENGTH_LONG).show();
                    mLockBtn.setChecked(false);
                    return;
                }

                if (isChecked) {
                    myUser.isLock = true;
                } else {
                    mLockBtn.setChecked(true);
                    Intent intent = new Intent(MainActivity.this, LockActivity.class);
                    intent.setFlags(Config.CHECK_PASSWORDS);
                    startActivityForResult(intent, Config.CHECK_PASSWORDS);
                }
            }
        });

    }

    private void initView() {
        mAddAlarmBtm = (Button) findViewById(R.id.main_id_add);
        mSettingBtm = (Button) findViewById(R.id.main_id_settings);
        mRecyclerView = (RecyclerView) findViewById(R.id.main_id_recyclerview);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_id_drawer_layout);

        initPopWinow();
        mRecentlyTimeView = (TextView) findViewById(R.id.main_id_alarmtime);
        mRecenntlyLastView = (TextView) findViewById(R.id.main_id_alarmlast);
        myHeadView = (RoundImageView) findViewById(R.id.main_id_myhead);
        mTempImageView = (ImageView) findViewById(R.id.main_id_tempimage);
        mTempView = (TextView) findViewById(R.id.main_id_temperatrue);
        mMessageView = (TextView) findViewById(R.id.main_id_tempmessage);
        mWeatherLayout = (RelativeLayout) findViewById(R.id.main_bottompanel);

        mMainText1 = (TextView) findViewById(R.id.main_text1);
        mMainText2 = (TextView) findViewById(R.id.main_text2);
        mWeakupDay = (TextView) findViewById(R.id.main_id_message);
        mUserName = (TextView) findViewById(R.id.main_id_username);
        mLevelView = (TextView) findViewById(R.id.main_id_level);

        mViewStub = (ViewStub) findViewById(R.id.main_viewsub_listbg);
        mMenuQiuckClock = (CheckBox) findViewById(R.id.menus_id_qiuckclock);

        mExitBtm = (ImageButton) findViewById(R.id.menus_id_exit);
        mMoreSettingBtn = (CheckBox) findViewById(R.id.menus_id_moresetting);
        mLockBtn = (CheckBox) findViewById(R.id.menus_id_lock);
    }

    private void initPopWinow() {
        View v = LayoutInflater.from(this).inflate(R.layout.pupwindows_main, null);
        mLastingwindow = new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mQiuckBtn1 = (CheckBox) v.findViewById(R.id.popup_id_1);
        mQiuckBtn2 = (CheckBox) v.findViewById(R.id.popup_id_2);
        mQiuckBtn3 = (CheckBox) v.findViewById(R.id.popup_id_3);
        mQiuckBtn4 = (CheckBox) v.findViewById(R.id.popup_id_4);
        mQiuckBtn5 = (CheckBox) v.findViewById(R.id.popup_id_5);
        mQiuckBtn6 = (CheckBox) v.findViewById(R.id.popup_id_6);
        mQiuckBtn7 = (CheckBox) v.findViewById(R.id.popup_id_7);
        mQiuckBtn8 = (CheckBox) v.findViewById(R.id.popup_id_8);
        mQiuckLastTime = (TextView) v.findViewById(R.id.popup_id_text);

        mQiuckBtn1.setOnCheckedChangeListener(this);
        mQiuckBtn2.setOnCheckedChangeListener(this);
        mQiuckBtn3.setOnCheckedChangeListener(this);
        mQiuckBtn4.setOnCheckedChangeListener(this);
        mQiuckBtn5.setOnCheckedChangeListener(this);
        mQiuckBtn6.setOnCheckedChangeListener(this);
        mQiuckBtn7.setOnCheckedChangeListener(this);
        mQiuckBtn8.setOnCheckedChangeListener(this);

    }

    private void showPop(View parent) {
        int[] xy = new int[2];   //记录父布局的xy
        parent.getLocationOnScreen(xy);
        mLastingwindow.setFocusable(true);
        mLastingwindow.setOutsideTouchable(true);
        mLastingwindow.setBackgroundDrawable(new BitmapDrawable());
        mLastingwindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, xy[0] * 4, xy[1]);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //三杠按钮
            case R.id.main_id_settings:
                //在左边屏幕
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            //闹铃添加
            case R.id.main_id_add:
                if (myUser.isLock) {
                    Toast.makeText(MainActivity.this, "闹铃设置被锁定，请到设置中解锁。", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this, AddAlarmActivity.class);
                intent.setFlags(Config.AlARMTYPE_ADD);
                startActivityForResult(intent, Config.AlARMTYPE_ADD);
                break;
            //头像
            case R.id.main_id_myhead:
                Intent intent1 = getIntent();
                intent1.setClass(MainActivity.this, EditHeadActivity.class);
                startActivityForResult(intent1, Config.ALARMTYPE_SETHEAD);
                break;
            //快速闹钟
            case R.id.menus_id_qiuckclock:
                if (myUser.isLock) {
                    Toast.makeText(MainActivity.this, "闹铃设置被锁定，请到设置中解锁。", Toast.LENGTH_SHORT).show();
                    return;
                }
                showPop(v);
                break;
            //退出
            case R.id.menus_id_exit:
                FileUtil util = FileUtil.getInstence();
                util.saveSetting(mApp);
                util.saveToFile(mApp, getExternalFilesDir(null).getPath(), FileUtil.FLAG_ALARM);
                this.finish();
                System.exit(0);
                break;
            //更多设置
            case R.id.menus_id_moresetting:
                Intent intent2 = new Intent(MainActivity.this, MainSettingActivity.class);
                startActivity(intent2);
                break;

        }
    }


    private void setAllQiuckCheched(boolean b) {
        mQiuckBtn1.setChecked(b);
        mQiuckBtn2.setChecked(b);
        mQiuckBtn3.setChecked(b);
        mQiuckBtn4.setChecked(b);
        mQiuckBtn5.setChecked(b);
        mQiuckBtn6.setChecked(b);
        mQiuckBtn7.setChecked(b);
        mQiuckBtn8.setChecked(b);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setAllQiuckCheched(false);
        AlarmUtil util = AlarmUtil.getInstence();
        if (isChecked) {
            if (mTimer != null) {
                Message message = Message.obtain();
                message.what = Config.QIUCKAlARM;
                Bundle bundle = new Bundle();
                bundle.putString("time", "00:00");
                message.setData(bundle);
                myHandler.sendMessage(message);
                mTimer.cancel();
            }

            long time = 0;
            switch (buttonView.getId()) {
                case R.id.popup_id_1:
                    mQiuckBtn1.setChecked(true);
                    time = 30 * 1000;
                    break;
                case R.id.popup_id_2:
                    mQiuckBtn2.setChecked(true);
                    time = 360 * 1000;
                    break;
                case R.id.popup_id_3:
                    mQiuckBtn3.setChecked(true);
                    time = 3 * 60 * 1000;
                    break;
                case R.id.popup_id_4:
                    mQiuckBtn4.setChecked(true);
                    time = 5 * 60 * 1000;
                    break;
                case R.id.popup_id_5:
                    mQiuckBtn5.setChecked(true);
                    time = 10 * 60 * 1000;
                    break;
                case R.id.popup_id_6:
                    mQiuckBtn6.setChecked(true);
                    time = 15 * 60 * 1000;
                    break;
                case R.id.popup_id_7:
                    mQiuckBtn7.setChecked(true);
                    time = 20 * 60 * 1000;
                    break;
                case R.id.popup_id_8:
                    mQiuckBtn8.setChecked(true);
                    time = 30 * 60 * 1000;
                    break;
            }
            util.setQuickAlram(MainActivity.this, time, new QuickTimeTextTask.OnTimeFreshListener() {
                @Override
                public void onTimeChanged(String timeStr) {
                    Message message = Message.obtain();
                    message.what = Config.QIUCKAlARM;
                    Bundle bundle = new Bundle();
                    bundle.putString("time", timeStr);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                }
            });

        } else {
            util.deleteAlarm(mApp, util.getAlarmByFlag(mApp, Config.QIUCKAlARM));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            //从闹钟设置界面返回
            case Config.AlARMTYPE_EDIT:
                mAlarmBeanList = mApp.getAlarDates();
                myHandler.sendEmptyMessage(Config.UPDATE_UI_LISTVIEWBG);
                if (mAdapter == null) {
                    initRecyclerView(mAlarmBeanList);
                }
                myHandler.sendEmptyMessage(Config.UPDATE_UI_LISTVIEW);
                break;
            //头像设置返回
            case Config.ALARMTYPE_SETHEAD:
                myUser = mApp.getMyUser();
                if(myUser.mHeadPath!=null){
                    myHeadView.setImageURI(Uri.fromFile(new File(myUser.mHeadPath)));
                }
                mUserName.setText(myUser.mName);
                break;
            //从闹铃item返回
            case Config.AlARMTYPE_ONOFF:
                myHandler.sendEmptyMessage(Config.UPDATE_UI_LISTVIEW);
                break;
            //从天气页面返回
            case Config.WEATHER_FLAG:
                mWeather = mApp.getWeather();
                setData2WeatherView();
                break;
            //从密码界面返回
            case Config.CHECK_PASSWORDS:

                mLockBtn.setChecked(false);
                myUser.isLock = false;
        }
    }


    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //有无闹钟时，LisetView的背景显示
                case Config.UPDATE_UI_LISTVIEWBG:
                    if (mAlarmBeanList == null || mAlarmBeanList.size() == 0) {
                        showViewSub();
                    } else {
                        hideViewSub();
                    }
                    break;

                //刷新闹钟剩余时间
                case Config.UPDATE_UI_LISTVIEW:
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    AlarmBean bean = AlarmUtil.getInstence().getRecently(mApp);
                    if (bean == null || bean.getFlag() == Config.QIUCKAlARM) {
                        mRecentlyTimeView.setText("没有闹钟！");
                        mRecenntlyLastView.setText(" ");
                    } else {
                        mRecenntlyLastView.setText("剩余时间：" + bean.getRemainingTime());
                        mRecentlyTimeView.setText(TimeUtil.getInstance().
                                todayOrToMorrow(bean.getTimeInMills()) + " " + bean.getTimeStr());
                    }
                    break;

                //快速闹钟那里
                case Config.QIUCKAlARM:
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    if (!mQiuckLastTime.getText().equals("倒计时: 00:00"))
                        mQiuckLastTime.setText("倒计时: " + msg.getData().get("time"));
                    break;


            }

        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        if (mLastTimer != null) {
            mDifferTimeTask.setWait(false);
        }
        if (mTimer != null) {
            mTimer.notifyAll();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            //不在MainActivity的时候就暂停后台进程
            if (mLastTimer != null) {
                mDifferTimeTask.setWait(true);
            }
            if (mTimer != null) {
                mTimer.wait();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}



