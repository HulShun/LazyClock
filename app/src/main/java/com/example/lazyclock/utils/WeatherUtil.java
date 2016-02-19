package com.example.lazyclock.utils;

import android.content.Context;
import android.widget.ImageView;

import com.example.lazyclock.R;

/**
 * Created by Administrator on 2016/1/20.
 */
public class WeatherUtil {
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

