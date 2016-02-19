package com.example.lazyclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.bean.User;
import com.example.lazyclock.bean.Weather;
import com.example.lazyclock.dao.MathDao;
import com.example.lazyclock.others.CommonException;
import com.example.lazyclock.others.OnTimeChangedListener;
import com.example.lazyclock.service.AlarmService;
import com.example.lazyclock.service.RingtongService;
import com.example.lazyclock.utils.FileUtil;
import com.example.lazyclock.utils.HttpUtil;
import com.example.lazyclock.utils.JsonUtil;
import com.example.lazyclock.utils.LBSAcquire;
import com.example.lazyclock.utils.LogUtil;
import com.example.lazyclock.utils.PinyinUtil;
import com.example.lazyclock.utils.TimeUtil;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

/**
 * Created by Administrator on 2015/12/8.
 */
public class MyApplication extends Application {
    private String defalut_path;

    private User myUser;

    /**
     * 闹钟列表的游标
     */
    private Cursor mCursor;

    private List<AlarmBean> mAlarDates;
    /**
     * 用来保存响铃界面的activity，便于在服务中将其关闭
     */
    private Activity stopModeActivity;

    private boolean isFirstTime = true;

    /**
     * 记录默认名称的闹铃个数
     */
    private int defaultAlarmCount = 0;

    /**
     * 本地数据加载的信号量
     */
    private Semaphore notifyDataSetSemahore;

    /**
     * 闹铃剩余时间改变监听
     */
    private OnTimeChangedListener mListener;

    private Weather mWeather;


    /**
     * 程序的总初始化
     */
    public void initialize() {
        notifyDataSetSemahore = new Semaphore(1);
        //读取应用设置信息
        readSetting();
        //如果不是第一次启动程序，就加载闹钟对象
        if (!isFirstTime) {
            //加载文件
            readFromFile();
        }
        //应用安装后第一次启动
        else {
            isFirstTime = false;
            //加载数学题
            downloadMathFromAsset();
        }

        //启动后台刷新时间
        differAlarmsLastTime();
        try {
            notifyDataSetSemahore.acquire();
            notifyDataSetSemahore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        defalut_path = getExternalFilesDir(null).getAbsolutePath();
    }

    public User getMyUser() {
        return myUser;
    }

    public Weather getWeather() {
        return mWeather;
    }

    /**
     * 清空天气数据
     */
    public void clearWeather() {
        mWeather = null;
    }

    public Activity getStopModeActivity() {
        return stopModeActivity;
    }

    public void setStopModeActivity(Activity stopModeActivity) {
        this.stopModeActivity = stopModeActivity;
    }

    public void setOnAlarmChangedListener(OnTimeChangedListener listener) {
        mListener = listener;
    }


    public List<AlarmBean> getAlarDates() {
        return mAlarDates;
    }

    /**
     * 添加闹钟元素到列表中
     *
     * @param alarm
     */
    public void addAlarmToList(AlarmBean alarm) {
        if (mAlarDates == null) {
            mAlarDates = new ArrayList<>();
        }
        mAlarDates.add(alarm);
    }

    public AlarmBean getAlarmFromID(int id) {
        for (AlarmBean bean : mAlarDates) {
            if (bean.getId() == id) {
                return bean;
            }
        }
        return null;
    }

    public int getDefaultAlarmCount() {
        return defaultAlarmCount;
    }

    public void setDefaultAlarmCount(int defaultAlarmCount) {
        this.defaultAlarmCount = defaultAlarmCount;
    }


    /**
     * 通过flag来获取闹钟对象
     *
     * @param flag
     * @return
     */
    public AlarmBean getAlarmBean(int flag) {
        if (mAlarDates == null || mAlarDates.isEmpty()) {
            return null;
        }
        for (AlarmBean bean : mAlarDates) {
            if (bean.getFlag() == flag) {
                return bean;
            }
        }
        return null;
    }


    /**
     * 开启后台线程，每30s计算一次所有闹铃的剩余时间
     */
    private void differAlarmsLastTime() {
        final TimeUtil util = TimeUtil.getInstance();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //信号量
                try {
                    notifyDataSetSemahore.acquire();
                    notifyDataSetSemahore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (mAlarDates == null || mAlarDates.isEmpty()) {
                    return;
                }
                for (AlarmBean bean : mAlarDates) {
                    bean.setRemainingTime(util.calculateTime(bean.getTimeInMills(), System.currentTimeMillis()));
                }
                if (mListener != null) {
                    mListener.onTimeChange(mAlarDates);
                }
            }
        };
        timer.schedule(task, 0, 30000);
    }


    /**
     * 将用户闹铃添加到系统闹钟管理器当中
     *
     * @param context   上下文对象
     * @param alarmBean 闹钟对象
     */
    public void setAlarmToSystem(Context context, AlarmBean alarmBean) {
        Intent intent = new Intent(context, RingtongService.class);
        intent.setAction("android.intent.action.ALARM_SERVER");
        intent.putExtra("Alarm", alarmBean);
        PendingIntent pendingIntent = PendingIntent.getService(context, alarmBean.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, alarmBean.getTimeInMills(), pendingIntent);

    }

    /**
     * 删除闹钟 （action和请求码作为识别闹钟的标识符）
     *
     * @param context
     * @param bean
     */
    public void deleteAlarm(Context context, AlarmBean bean) {
        if (bean != null && mAlarDates != null && !mAlarDates.isEmpty()) {
            for (AlarmBean temp : mAlarDates) {
                if (temp.getId() == bean.getId()) {
                    Intent intent = new Intent(context, RingtongService.class);
                    intent.setAction("android.intent.action.ALARM_SERVER");
                    intent.putExtra("Alarm", bean);
                    PendingIntent pendingIntent = PendingIntent.getService(context, bean.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    mAlarDates.remove(temp);
                    if (mListener != null) {
                        mListener.onTimeChange(mAlarDates);
                    }
                    LogUtil.d("save", "在deleteAlarm()中调用保存文件");
                    saveToFile(defalut_path, FileUtil.FLAG_ALARM);
                }
            }
        }
    }

    public void setNextAlarm(Context context, AlarmBean bean) {
        //本次闹钟周期结束，且不重复的闹钟，关闭闹钟
        TimeUtil util = TimeUtil.getInstance();
        long time;
        //闹铃不重复情况
        if (!bean.isrepeat()) {
            bean.setWork(false);
            return;
        }
        //Daylist到尾巴了
        else if (bean.getNextDayPosition() + 2 > bean.getDays().size()) {
            bean.setNextDayPosition(0);
        }
        Set<Integer> set = new TreeSet<>();
        util.Strs2Intergers(bean.getDays(), set);
        int temp;
        if (set.size() <= 1) {
            temp = 7;
        } else {
            temp = (int) set.toArray()[bean.getNextDayPosition()];
        }
        time = util.getFullTime(bean.getTimeStr(), temp);
        bean.setTimeInMills(time);
        bean.setRemainingTime(util.calculateTime(bean.getTimeInMills(), System.currentTimeMillis()));
        bean.setNextDayPosition(bean.getNextDayPosition() + 1);
        //因为地址引用出现问题，采用复制的方法更新对象数据
        AlarmBean bean2 = getAlarmFromID(bean.getId());
        //如果没有这个id对应的闹钟对象，就直接添加到队列中
        if (bean2 == null) {
            addAlarmToList(bean);
        } else {
            util.copyAlarm(bean, bean2);
            bean = bean2;
        }
        setAlarmToSystem(context, bean);
    }


    /**
     * 关闭响铃和震动服务
     */
    public void stopAlarmRingingServer() {
        //关闭震动
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).cancel();
        }

        //关闭闹铃
        Intent intent = new Intent("com.example.aciton.BGMediaPlyer");
        intent.setPackage(getPackageName());
        stopService(intent);
    }

    /**
     * @return 返回距离目前时间最近的闹钟
     */
    public AlarmBean getRecently() {
        long nowMills = System.currentTimeMillis();
        AlarmBean bean;
        if (mAlarDates == null || mAlarDates.isEmpty()) {
            return null;
        }
        bean = mAlarDates.get(0);
        if (mAlarDates.size() == 1) {
            return bean;
        }

        for (int i = 1; i < mAlarDates.size(); i++) {
            bean = Math.abs(nowMills - bean.getTimeInMills()) <
                    Math.abs(nowMills - mAlarDates.get(i).getTimeInMills()) ?
                    bean : mAlarDates.get(i);
        }
        return bean;
    }


    /**
     * 保存用户设置到本地
     */
    public void saveSetting() {
        SharedPreferences settting = getSharedPreferences(AlarmState.PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settting.edit();
        editor.putBoolean("isFirstTime", isFirstTime);
        editor.putInt("defaultAlarmCount", defaultAlarmCount);
        editor.putString("name", myUser.mName);
        editor.putString("headPath", myUser.mHeadPath);
        editor.putString("weatherDay", myUser.mWeatherDay);
        editor.putInt("level", myUser.level);
        editor.putInt("weakupDays", myUser.getWeakupDays());
        editor.putBoolean("isLock", myUser.isLock);
        editor.putString("passwords", myUser.passwordsMD5);
        editor.apply();
    }

    public void readSetting() {
        myUser = new User();
        SharedPreferences settting = getSharedPreferences(AlarmState.PREFERENCES_NAME, Activity.MODE_PRIVATE);
        isFirstTime = settting.getBoolean("isFirstTime", true);
        myUser.mName = settting.getString("name", "用户名");
        myUser.mHeadPath = settting.getString("headPath", "");
        myUser.mWeatherDay = settting.getString("weatherDay", "");
        myUser.isLock = settting.getBoolean("isLock", false);
        myUser.level = settting.getInt("level", 0);
        int m = settting.getInt("weakupDays", 0);
        myUser.setWeakUpDays(m);
        myUser.passwordsMD5 = settting.getString("passwords", "");
        defaultAlarmCount = settting.getInt("defaultAlarmCount", 0);
    }


    /**
     * @param path  路径
     * @param flags 保存的标识，是保存闹钟还是天气信息
     */
    public boolean saveToFile(final String path, final int flags) {
        final FileUtil fileUtil = FileUtil.getInstence();
        final JsonUtil jsonUtil = JsonUtil.getInstence();
        final boolean flag[] = new boolean[1];
        try {
            notifyDataSetSemahore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String json;
                //闹钟列表写出文件
                if (flags == FileUtil.FLAG_ALARM) {
                    json = jsonUtil.listToJson(mAlarDates);
                    if (json != null) {
                        flag[0] = fileUtil.jsonToFile(json, path, FileUtil.dataJsonPath);
                    }
                }
                if (flags == FileUtil.FLAG_WEATHER) {
                    //天气情况写出文件
                    json = jsonUtil.beanToJson(mWeather);
                    if (json != null) {
                        flag[0] = fileUtil.jsonToFile(json, path, FileUtil.weatherJsonPath);
                    }
                }
                notifyDataSetSemahore.release();
            }
        }).start();

        try {
            notifyDataSetSemahore.acquire();
            notifyDataSetSemahore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return flag[0];
    }

    /**
     * 将所有保存在本地的闹钟读取   （线程处理）
     */
    public void readFromFile() {
        final FileUtil fileUtil = FileUtil.getInstence();
        final JsonUtil jsonUtil = JsonUtil.getInstence();
        try {
            LogUtil.d("hours", "文件加载【请求】信号量");
            notifyDataSetSemahore.acquire();
            LogUtil.d("hours", "文件加载【截获】信号量");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                //加载闹钟列表
                String json = fileUtil.jsonFromFile(defalut_path, FileUtil.dataJsonPath);
                if (!json.equals("")) {
                    mAlarDates = jsonUtil.toObjects(json, new TypeToken<ArrayList<AlarmBean>>() {
                    }.getType());
                }

                //加载天气
                if (isTodayWeather()) {
                    LogUtil.d("weather", "去加载天气");
                    json = fileUtil.jsonFromFile(defalut_path, FileUtil.weatherJsonPath);
                    if (json != null) {
                        mWeather = jsonUtil.toObjects(json, Weather.class);
                    }
                }
                notifyDataSetSemahore.release();
                LogUtil.d("hours", "文件加载【释放】信号量");
            }
        }).start();
        try {

            notifyDataSetSemahore.acquire();
            notifyDataSetSemahore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 下载数学题
     */
    public void downloadMathFromNet() {
        final HttpUtil httpUtil = HttpUtil.getInstence();
        final MathDao mathDao = new MathDao(MyApplication.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 50; i++) {
                    String path = AlarmState.MATH_PATH + i;
                    LogUtil.d("db", path);
                    try {
                        String htmlStr = httpUtil.doGet(path);
                        mathDao.insertDataFromStr(htmlStr);
                    } catch (CommonException e) {
                        e.printStackTrace();
                    }
                }
                mathDao.close();
            }
        }).start();

    }

    /**
     * 从资源文件中提取数学题库
     */
    public void downloadMathFromAsset() {
        FileUtil util = FileUtil.getInstence();
        if (util.isExtralDir()) {
            String path = defalut_path;
            FileUtil.getInstence().dbToPath(this, path);
        } else {
            Toast.makeText(this, "内存卡不存在！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 下载天气
     */
    public void downloadWeatherInfo() {


        final LBSAcquire lbs = new LBSAcquire(getApplicationContext());
        lbs.setBDLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                String city = bdLocation.getCity();
                int m = bdLocation.getLocType();
                //如果没有获取到城市信息，就继续监听
                if (city == null) {
                    lbs.requireLocation();
                } else {
                    lbs.stopAcquire();
                    city = city.substring(0, city.lastIndexOf("市"));
                    //汉字转拼音
                    final String param = PinyinUtil.getInstence().getPinyin(city);
                    //网络天气请求
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            String data = HttpUtil.getInstence().doGetWeather(param);
                            String data2 = data.substring(data.indexOf("[") + 1, data.lastIndexOf("]"));
                            mWeather = JsonUtil.getInstence().JsonToWeather(data2);
                            myUser.mWeatherDay = mWeather.getNow().getDate();
                            LogUtil.d("save", "在downloadWeatherInfo()中调用保存文件");
                            saveToFile(defalut_path, FileUtil.FLAG_WEATHER);
                            saveSetting();
                        }
                    }, 0);
                }
            }
        });
        lbs.startAcquire();
    }

    /**
     * 判断本地缓存的天气情况是否是今天的
     *
     * @return
     */
    public boolean isTodayWeather() {
        SimpleDateFormat formatter = new SimpleDateFormat(AlarmState.WEATHER_DATEFORMAT);
        Date date = new Date(System.currentTimeMillis());
        String today = formatter.format(date);
        if (today.equals(myUser.mWeatherDay)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检测网络状态
     *
     * @return 是否联网
     */
    public boolean checkNetWorkState() {
        boolean flag = false;
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        return flag;
    }

    /**
     * 备份闹钟列表
     *
     * @param context
     * @return
     */
    public boolean backupList(Context context) {
        boolean flag;
        if (!FileUtil.getInstence().isExtralDir()) {
            Toast.makeText(context, "外部储存设备不存在！", Toast.LENGTH_LONG).show();
        }
        String path = Environment.getExternalStorageDirectory().getPath() + "/LazyClok";
        flag = saveToFile(path, FileUtil.FLAG_ALARM);
        return flag;
    }

    /**
     * 恢复闹钟列表
     *
     * @param context
     * @return
     */
    public boolean recoverList(Context context) {
        boolean flag;
        if (!FileUtil.getInstence().isExtralDir()) {
            Toast.makeText(context, "外部储存设备不存在！", Toast.LENGTH_LONG).show();
        }
        String path = Environment.getExternalStorageDirectory().getPath() + "/LazyClok";
        try {
            flag = FileUtil.getInstence().copyFile(path, defalut_path);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return flag;
    }


    public Timer stopMediaServer(Context context, AlarmBean bean, long delay) {
        Timer timer = new Timer();
        timer.schedule(new StopTask(context, bean), delay);
        return timer;
    }

    /**
     * 响铃时间过长，自动关闭
     */
    class StopTask extends TimerTask {

        private Context mContext;
        private AlarmBean mAlarm;

        public StopTask(Context context, AlarmBean bean) {
            mContext = context;
            mAlarm = bean;
        }

        @Override
        public void run() {
            stopAlarmRingingServer();
            Activity activity = getStopModeActivity();
            //如果不是快速闹钟，并且开启了小睡
            if (mAlarm.getFlag() != AlarmState.QIUCKAlARM && mAlarm.isMoreSleep()) {
                AlarmBean bean = new AlarmBean(mContext);
                TimeUtil.getInstance().copyAlarm(mAlarm, bean);
                TimeUtil.getInstance().addMillsToAlarmBean(bean.getMoreSleepMins() * 60 * 1000, bean);
                setAlarmToSystem(mContext, bean);

                //在通知栏显示提醒
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
                builder.setTicker("(点击取消)进入小睡时间")
                        .setContentTitle("下次闹铃时间：")
                        .setContentText(bean.getTimeStr() + "")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(Notification.DEFAULT_LIGHTS)
                        .setPriority(Notification.PRIORITY_DEFAULT);
                //点击通知栏的事件
                Intent intent = new Intent(mContext, AlarmService.class);
                intent.putExtra("mAlarm", mAlarm);
                PendingIntent pendingintent = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                builder.setContentIntent(pendingintent);

                Notification notification = builder.build();
                notification.flags = Notification.FLAG_NO_CLEAR;    //不被清理按键清理
                mNotificationManager.notify(AlarmState.SERVICE_DEL, notification);
            }

            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }

} //end
