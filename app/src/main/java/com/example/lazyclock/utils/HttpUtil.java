package com.example.lazyclock.utils;

import com.example.lazyclock.AlarmState;
import com.example.lazyclock.others.CommonException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 网络连接工具类
 * Created by Administrator on 2016/1/8.
 */
public class HttpUtil {
    private static HttpUtil util;
    private final String urlStr1 = "http://www.tiku.cn/questions/index/sid/3168/hard/0.html";


    public static HttpUtil getInstence() {
        if (util == null) {
            synchronized (FileUtil.class) {
                if (util == null) {
                    util = new HttpUtil();

                }
            }
        }
        return util;
    }

    public String doGet(String urlStr) throws CommonException {
        StringBuffer sBuffer = new StringBuffer();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setDoInput(true);
            con.connect();

            if (con.getResponseCode() == 200) {
                InputStream in = con.getInputStream();
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) != -1) {
                    sBuffer.append(new String(buffer, 0, len, "UTF-8"));
                }
                in.close();
            } else {
                throw new CommonException("网络连接异常：" + con.getResponseCode());
            }
        } catch (MalformedURLException e) {
            throw new CommonException("URL异常01");
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommonException("openConnection异常01");
        }
        return sBuffer.toString();
    }

    public String doGetWeather(String city) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        String httpUrl = AlarmState.WEATHER_PATH + "?city=" + city;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey", AlarmState.WEATHER_KAY);
            connection.connect();
            int m = connection.getResponseCode();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
