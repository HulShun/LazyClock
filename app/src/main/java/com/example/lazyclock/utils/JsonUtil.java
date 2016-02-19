package com.example.lazyclock.utils;

import com.example.lazyclock.bean.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Administrator on 2015/12/8.
 */
public class JsonUtil {
    private static JsonUtil util;


    private JsonUtil() {

    }

    /**
     * 单例  获得该实例
     *
     * @return
     */
    public static JsonUtil getInstence() {

        if (util == null) {
            synchronized (JsonUtil.class) {
                if (util == null) {
                    util = new JsonUtil();
                }
            }
        }
        return util;
    }

    public String listToJson(List<?> datas) {
        String json = null;
        Gson gson = new Gson();
        json = gson.toJson(datas);
        return json;
    }


    public String beanToJson(Object object) {
        String json = null;
        Gson gson = new Gson();
        json = gson.toJson(object);
        return json;
    }

    public <T> T toObjects(String json, Type classofT) {
        Gson gson = new Gson();
        return gson.fromJson(json, classofT);
    }

    public Weather JsonToWeather(String json) {
        Weather temp = new Weather();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject jsonObject_api = jsonObject.getJSONObject("aqi");

            JSONObject jsonObject_city = jsonObject_api.getJSONObject("city");
            temp.getNow().setQlty(jsonObject_city.getString("qlty"));

            JSONObject jsonObject_basic = jsonObject.getJSONObject("basic");
            temp.getBasic().setCity(jsonObject_basic.getString("city"));
            temp.getBasic().setCnty(jsonObject_basic.getString("cnty"));

            JSONObject jsonObject_suggestion = jsonObject.getJSONObject("suggestion");
            JSONObject jsonObject_suggestion_flu = jsonObject_suggestion.getJSONObject("flu");
            temp.getNow().setFluMessage(jsonObject_suggestion_flu.getString("txt"));

            JSONArray jsonArray_daily = jsonObject.getJSONArray("daily_forecast");
            JSONObject jsonObject1 = (JSONObject) jsonArray_daily.get(0);
            temp.getNow().setDate(jsonObject1.getString("date"));
            JSONObject jsonObject1_cond = jsonObject1.getJSONObject("cond");
            temp.getNow().setText(jsonObject1_cond.getString("txt_d"));
            JSONObject jsonObject1_tmp = jsonObject1.getJSONObject("tmp");
            temp.getNow().setTemp(jsonObject1_tmp.getString("min") + "~" + jsonObject1_tmp.getString("max") + "℃");

            List<Weather.DailyBean> daliys = temp.getDaily();
            for (int i = 1; i < jsonArray_daily.length(); i++) {
                Weather.DailyBean _bean = new Weather.DailyBean();
                JSONObject object = (JSONObject) jsonArray_daily.get(i);
                _bean.setDate(object.getString("date"));
                JSONObject object_cond = object.getJSONObject("cond");
                _bean.setText(object_cond.getString("txt_d"));
                JSONObject object_tmp = object.getJSONObject("tmp");
                _bean.setTemp(object_tmp.getString("min") + "~" + object_tmp.getString("max") + "℃");
                daliys.add(_bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return temp;
    }
}
