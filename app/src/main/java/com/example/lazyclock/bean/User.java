package com.example.lazyclock.bean;

/**
 * Created by Administrator on 2016/1/12.
 */
public class User {

    public String mWeatherDay;

    /**
     * 坚持起床7天就升一级
     */
    private final int LEVEL_UPDATE_REQUIRE = 7;

    /**
     * 锁定开关
     */
    public boolean isLock;
    /**
     * 用户名
     */
    public String mName;
    /**
     * 用户头像的位置
     */
    public String mHeadPath;
    /**
     * 用户等级
     */
    public int level = 0;
    /**
     * 坚持起床的天数
     */
    private int weakupDays = 0;

    public String passwordsMD5;

    public void UpdateWeakUpDays() {
        weakupDays++;
        if (weakupDays % LEVEL_UPDATE_REQUIRE == 0) {
            level++;
        }
    }

    public void setWeakUpDays(int m) {
        weakupDays = m;

    }

    public int getWeakupDays() {
        return weakupDays;
    }


    public void setPasswordsMD5(String passwordsMD5) {
        this.passwordsMD5 = passwordsMD5;
    }
}
