package com.example.lazyclock.utils;

import android.content.Context;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by Administrator on 2016/1/14.
 */
public class LBSAcquire {

    private Context mContext;


    private LocationClient mLoactionClient;
    private String city;


    public LBSAcquire(Context context) {
        mContext = context;
        mLoactionClient = new LocationClient(context);
        initParams();
    }


    private void initParams() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.SetIgnoreCacheException(false);
        option.setScanSpan(5000);
        option.setTimeOut(10000);
        option.setLocationNotify(true);
        mLoactionClient.setLocOption(option);
    }

    /**
     * 设置监听器
     *
     * @param locationListener
     */
    public void setBDLocationListener(BDLocationListener locationListener) {
        mLoactionClient.registerLocationListener(locationListener);
    }

    public void requireLocation() {
        mLoactionClient.requestLocation();
    }

    /**
     * 返回城市信息
     */
    public String getCNLocationAcquire() {
        return city;
    }

    public void startAcquire() {
        if (mLoactionClient != null && !mLoactionClient.isStarted()) {
            mLoactionClient.start();
        }
        if (mLoactionClient != null && mLoactionClient.isStarted()) {
            mLoactionClient.requestLocation();
        } else {
            LogUtil.d("location", "百度地图定位开启失败");
        }

    }

    public void stopAcquire() {
        if (mLoactionClient != null && mLoactionClient.isStarted()) {
            mLoactionClient.stop();
        }
    }
}
