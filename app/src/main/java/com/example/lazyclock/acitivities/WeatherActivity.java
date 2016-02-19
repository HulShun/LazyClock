package com.example.lazyclock.acitivities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lazyclock.AlarmState;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.R;
import com.example.lazyclock.adapter.WeatherAdapter;
import com.example.lazyclock.bean.Weather;
import com.example.lazyclock.customview.DividerItemDecoration;
import com.example.lazyclock.utils.LogUtil;
import com.example.lazyclock.utils.WeatherUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/1/13.
 */
public class WeatherActivity extends Activity {
    private static final int MESSAGE_UPDATE = 1;
    private static final int MESSAGE_SHOWTOAST = 2;

    private Button mWeatherBack;
    private TextView mWeatherTodayText;
    private TextView mWeatherTmp;
    private TextView mWeatherText;
    private TextView mWeatherAirquality;
    private TextView mWeatherCity;
    private Button mUpdataBtn;
    private TextView mWeatherMessage;
    private ImageView mWeatherImage;
    private RecyclerView mWeatherListview;
    private ProgressDialog mDialog;

    private MyApplication mApp;
    private Weather mWeather;
    private WeatherAdapter mAdapter;
    private WeatherUtil mWeatherUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weather);
        initViews();
        initData();
        initEvent();
    }

    private void initEvent() {

        mWeatherBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(AlarmState.WEATHER_FLAG);
                WeatherActivity.this.finish();
            }
        });
        mUpdataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.clearWeather();
                showDialogAndDownload();
            }
        });
    }


    private void initViews() {
        mWeatherBack = (Button) findViewById(R.id.weather_id_back);
        mWeatherTodayText = (TextView) findViewById(R.id.weather_today_text);
        mWeatherTmp = (TextView) findViewById(R.id.weather_tmp);
        mWeatherText = (TextView) findViewById(R.id.weather_text);
        mUpdataBtn = (Button) findViewById(R.id.weather_update);
        mWeatherAirquality = (TextView) findViewById(R.id.weather_airquality);
        mWeatherCity = (TextView) findViewById(R.id.weather_city);
        mWeatherMessage = (TextView) findViewById(R.id.weather_message);
        mWeatherImage = (ImageView) findViewById(R.id.weather_image);
        mWeatherListview = (RecyclerView) findViewById(R.id.weather_listview);
    }

    private void initData() {
        mApp = (MyApplication) getApplication();
        mWeather = mApp.getWeather();
        if (mWeather != null) {
            setData();
        } else {
            showDialogAndDownload();
        }
    }

    private void setData() {

        if (mAdapter == null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mWeatherListview.setLayoutManager(layoutManager);
            mWeatherListview.setItemAnimator(new DefaultItemAnimator());

            DividerItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
            mWeatherListview.addItemDecoration(itemDecoration);
            mAdapter = new WeatherAdapter(this, mWeather);
            mWeatherListview.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
        mWeatherUtil = new WeatherUtil(this);
        String date = mWeather.getNow().getDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.CHINA);
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        try {
            Date date1 = formatter1.parse(date);
            date = formatter.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //今日日期
        mWeatherTodayText.setText(date);
        //气温
        String temp = mWeather.getNow().getTemp();
        mWeatherTmp.setText(temp);
        String temp1 = mWeather.getNow().getText();
        mWeatherText.setText(temp1);
        //提示
        mWeatherMessage.setText(mWeather.getNow().getFluMessage());
        //天气图标
        mWeatherUtil.setLargeImageToWeather(temp1, mWeatherImage);
        //城市
        mWeatherCity.setText(mWeather.getBasic().getCity());
        //空气质量
        mWeatherAirquality.setText(mWeather.getNow().getQlty());

    }

    private void showDialogAndDownload() {

        mDialog = new ProgressDialog(this);
        mDialog.setCancelable(false);
        mDialog.setMessage("加载中...");
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setIndeterminate(true);
        mDialog.show();
        mApp.downloadWeatherInfo();
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Looper.prepare();
                while (true) {
                    if (mApp.getWeather() != null) {
                        myHandler.sendEmptyMessage(MESSAGE_UPDATE);
                        mDialog.dismiss();
                        timer.cancel();
                        return;
                    }
                    //网络不可用
                    if (!mApp.checkNetWorkState()) {
                        Message message = Message.obtain();
                        message.what = MESSAGE_SHOWTOAST;
                        message.obj = "当前网络不可用，请检查网络！";
                        myHandler.sendMessage(message);
                        mDialog.dismiss();
                        timer.cancel();
                        return;
                    }
                }
            }
        }, 0);

        //超时检测
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mApp.getWeather() == null) {
                    Message message = Message.obtain();
                    message.what = MESSAGE_SHOWTOAST;
                    message.obj = "数据获取失败！请检查网络或者定位服务。";
                    myHandler.sendMessage(message);
                    mDialog.dismiss();
                }
                timer.cancel();
            }
        }).start();


    }


    /**
     * 界面完成渲染后调用
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.d("key", "按键被点击了~！");
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_BACKSLASH) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATE:
                    initData();
                    break;
                case MESSAGE_SHOWTOAST:
                    String message = (String) msg.obj;
                    Toast.makeText(WeatherActivity.this, message, Toast.LENGTH_SHORT).show();
            }

        }
    };

}
