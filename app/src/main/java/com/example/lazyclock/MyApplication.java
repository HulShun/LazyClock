package com.example.lazyclock;

import android.app.Application;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.bean.User;
import com.example.lazyclock.bean.Weather;
import com.example.lazyclock.db.dao.MathDao;
import com.example.lazyclock.others.CommonException;
import com.example.lazyclock.utils.AlarmUtil;
import com.example.lazyclock.utils.FileUtil;
import com.example.lazyclock.utils.HttpUtil;
import com.example.lazyclock.utils.LogUtil;

import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by Administrator on 2015/12/8.
 */
public class MyApplication extends Application {
    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }


    private User myUser;

    private List<AlarmBean> mAlarDates;

    private boolean isFirstTime = true;


    /**
     * 本地数据加载的信号量
     */
    private Semaphore notifyDataSetSemahore;


    private Weather mWeather;


    /**
     * 程序的总初始化
     */
    public void initialize() {

        //读取应用设置信息
        FileUtil.getInstence().readSettings(this);
        //如果不是第一次启动程序，就加载闹钟对象
        if (!isFirstTime) {
            //加载文件
            AlarmUtil.getInstence().getAllAlarms(this);
        }
        //应用安装后第一次启动
        else {
            isFirstTime = false;
            //加载数学题
            downloadMathFromAsset();
        }


    }

    public void setAlarDates(List<AlarmBean> alarDates) {
        this.mAlarDates = alarDates;
    }

    public List<AlarmBean> getAlarDates() {
        return mAlarDates;
    }

    public User getMyUser() {
        return myUser;
    }

    public void setMyUser(User user) {
        myUser = user;
    }

    public void setFirstTime(boolean firstTime) {
        isFirstTime = firstTime;
    }

    public boolean isFirstTime() {
        return isFirstTime;
    }

    public Weather getWeather() {
        return mWeather;
    }


    public String getDefalut_path() {
        return getExternalFilesDir(null).getAbsolutePath();
    }

    /**
     * 清空天气数据
     */
    public void clearWeather() {
        mWeather = null;
    }

    public void setWeather(Weather weather) {
        this.mWeather = weather;
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
                    String path = Config.MATH_PATH + i;
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
            FileUtil.getInstence().dbToPath(this, getDefalut_path());
        } else {
            Toast.makeText(this, "内存卡不存在！", Toast.LENGTH_SHORT).show();
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


} //end
