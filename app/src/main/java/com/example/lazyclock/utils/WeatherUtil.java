package com.example.lazyclock.utils;

import android.content.Context;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.example.lazyclock.Config;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.R;
import com.example.lazyclock.bean.Weather;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/1/20.
 */
public class WeatherUtil {

    /**
     * 下载天气
     */
    public static void downloadWeatherInfo(final MyApplication app) {

        final LBSAcquire lbs = new LBSAcquire(app.getApplicationContext());
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
                            FileUtil fileUtil = FileUtil.getInstence();
                            String data = HttpUtil.getInstence().doGetWeather(param);
                            String data2 = data.substring(data.indexOf("[") + 1, data.lastIndexOf("]"));
                            Weather weather = JsonUtil.getInstence().JsonToWeather(data2);
                            app.setWeather(weather);
                            app.getMyUser().mWeatherDay = weather.getNow().getDate();
                            LogUtil.d("save", weather.getNow().getDate());
                            LogUtil.d("save", "app类中：" + app.getMyUser().mWeatherDay);
                            LogUtil.d("save", "在downloadWeatherInfo()中调用保存文件");
                            fileUtil.saveToFile(MyApplication.getInstance(), app.getDefalut_path(), FileUtil.FLAG_WEATHER);
                            fileUtil.saveSetting(app);
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
    public static boolean isTodayWeather(MyApplication app) {
        SimpleDateFormat formatter = new SimpleDateFormat(Config.WEATHER_DATEFORMAT);
        Date date = new Date(System.currentTimeMillis());
        String today = formatter.format(date);
        if (today.equals(app.getMyUser().mWeatherDay)) {
            return true;
        } else {
            return false;
        }
    }

    private Context mContext;

    public WeatherUtil(Context context) {
        mContext = context;
    }

    /**
     * 根据天气文字，设置天气图标
     *
     * @param text
     * @param imageView
     */
    public void setLargeImageToWeather(String text, ImageView imageView) {
        if (text.equals("晴")) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather_image003));
        } else if (text.equals("多云")) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather_image001));
        } else if (text.equals("少云")) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather_image005));
        } else if (text.equals("晴间多云")) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather_image004));
        } else if (text.equals("小雨") || text.equals("中雨") || text.equals("大雨") || text.equals("阵雨") || text.equals("强阵雨")) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather_image009));
        } else if (text.equals("雷阵雨") || text.equals("强雷阵雨") || text.equals("雷阵雨伴有冰雹")) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather_image002));
        } else if (
                text.equals("暴雨") || text.equals("大暴雨") || text.equals("特大暴雨")) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather_image002));
        } else if (text.equals("小雪") || text.equals("中雪") || text.equals("大雪") ||
                text.equals("暴雪") || text.equals("雨雪天气") || text.equals("阵雨夹雪") || text.equals("雨夹雪")) {
            //先用多云的图标来代替
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather_image006));
        } else {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather_image001));
        }
    }

    public void setImageToWeather(String text, ImageView imageView) {
        if (text.equals("晴")) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather__small_image003));
        } else if (text.equals("多云")) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather__small_image001));
        } else if (text.equals("少云")) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather__small_image005));
        } else if (text.equals("晴间多云")) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather__small_image004));
        } else if (text.equals("阵雨") || text.equals("强阵雨")) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather__small_image010));
        } else if (text.equals("雷阵雨") || text.equals("强雷阵雨") || text.equals("雷阵雨伴有冰雹")) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather__small_image002));
        } else if (text.equals("小雨") || text.equals("中雨") || text.equals("大雨") ||
                text.equals("暴雨") || text.equals("大暴雨") || text.equals("特大暴雨")) {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather__small_image002));
        } else if (text.equals("小雪") || text.equals("中雪") || text.equals("大雪") ||
                text.equals("暴雪") || text.equals("雨雪天气") || text.equals("阵雨夹雪") || text.equals("雨夹雪")) {
            //先用多云的图标来代替
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather__small_image006));
        } else {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather__small_image001));
        }
    }

}

