package com.example.lazyclock.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lazyclock.AlarmState;
import com.example.lazyclock.bean.AlarmBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2015/12/8.
 */
public class TimeUtil {
    private static TimeUtil util;
    private SimpleDateFormat mFormatter;

    private TimeUtil() {
        mFormatter = new SimpleDateFormat(AlarmState.ALARM_DATEFORMAT);
    }


    /**
     * 获得实例
     *
     * @return
     */
    public static TimeUtil getInstance() {
        if (util == null) {
            synchronized (TimeUtil.class) {
                if (util == null) {
                    util = new TimeUtil();
                }
            }
        }
        return util;
    }


    /**
     * 复制闹钟对象
     * * @return 返回一个数据一样，但是地址不一样的闹钟对象
     *
     * @param oldBean 参考对象
     * @param newBean 新对象
     */
    public void copyAlarm(AlarmBean oldBean, AlarmBean newBean) {
        newBean.setTimeInMills(oldBean.getTimeInMills());
        newBean.setVolumegradual(oldBean.isVolumegradual());
        newBean.setTimeStr(oldBean.getTimeStr());
        newBean.setWork(oldBean.isWork());
        newBean.setMoreSleepMins(oldBean.getMoreSleepMins());
        newBean.setMoreSleep(oldBean.isMoreSleep());
        newBean.setAlarmChanged(oldBean.isAlarmChanged());
        newBean.setDays(oldBean.getDays());
        newBean.setFlag(oldBean.getFlag());
        newBean.setRepeat(oldBean.isrepeat());
        newBean.setRing(oldBean.getRing());
        newBean.setVolume(oldBean.getVolume());
        newBean.setName(oldBean.getName());
        newBean.setNextDayPosition(oldBean.getNextDayPosition());
        newBean.setRemainingTime(oldBean.getRemainingTime());
        newBean.setShank(oldBean.isShank());
        newBean.setSleepType(oldBean.getSleepType());
        newBean.setStopSleepType(oldBean.getStopSleepType());
    }

    /**
     * 计算当前的时间与设定的闹铃时间的差值
     *
     * @param time     时间 如：yyyy年MM月dd日 a HH时mm分ss秒 EEEE
     * @param starTime 闹铃开始设置的时间点 (毫秒)
     * @return 最近的  “xx天xx时xx分(前/后)”
     */
    public String calculateTime(long time, long starTime) {

        long endtime = 0;
        String differTimeStr;
        //计算时间差，毫秒

        long temp = time - starTime;
        //毫秒转换成时间格式化
        if (temp > 0) {
            differTimeStr = differToFormat(temp);
            return differTimeStr + "后";
        }
        temp = Math.abs(temp);
        differTimeStr = differToFormat(temp);
        return differTimeStr + "前";
    }


    /**
     * 将相差的时间转换
     *
     * @param millis 相差时间
     * @return xx天xx小时xx分
     */
    private String differToFormat(long millis) {
        int day = 0, hours = 0, mins = 0;

        //有时候这里存在1分钟计算误差
        mins = (int) (millis / 1000 / 60);

        if (mins < 60) {
            return mins + "分";
        }

        //剩余几小时几分钟
        hours = mins / 60;
        mins = mins % 60;
        if (hours < 24) {
            return hours + "小时 " + mins + "分";
        }

        //剩余几天几小时几分钟
        day = hours / 24;
        hours = hours - day * 24;
        return day + "天 " + hours + "小时 " + mins + "分";
    }


    /**
     * 根据 xx时xx分 获取完整的时间
     *
     * @param time
     * @param week
     * @return
     */
    public long getFullTime(String time, int week) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
        int nowMin = calendar.get(Calendar.MINUTE);
        int hours = Integer.valueOf(time.substring(0, time.indexOf(":")));
        int mins = Integer.valueOf(time.substring(time.indexOf(":") + 1));
        int weekInt = -1;
        if (week != 0) {
            weekInt = week;
        }

        int systemWeekInt;
        systemWeekInt = calendar.get(Calendar.DAY_OF_WEEK) % 7 - 1;

        if (weekInt == systemWeekInt || weekInt == -1) {
            if (nowHour > hours || (nowHour == hours && mins < nowMin)) {
                calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
            }
        } else if (weekInt >= 7) {
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + weekInt);
        } else {
            weekInt = 7 - systemWeekInt + weekInt;
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + weekInt);
        }
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, mins);
        calendar.set(Calendar.SECOND, 0);
        Log.d("hours", "完整的时间为:" + new SimpleDateFormat(AlarmState.ALARM_DATEFORMAT).format(calendar.getTime()));
        return calendar.getTimeInMillis();
    }


    /**
     * 星期字符串转化成int
     *
     * @param list
     * @return
     */
    public void Strs2Intergers(List<String> list, Set<Integer> set) {
        if (list == null) {
            return;
        }
        for (String temp : list) {
            switch (temp) {

                case "星期一":
                    set.add(1);
                    break;
                case "星期二":
                    set.add(2);
                    break;
                case "星期三":
                    set.add(3);
                    break;
                case "星期四":
                    set.add(4);
                    break;
                case "星期五":
                    set.add(5);
                    break;
                case "星期六":
                    set.add(6);
                    break;
                case "星期日":
                    set.add(7);
                    break;
            }
        }
    }

    /**
     * 星期int转化成str
     *
     * @param list
     * @return
     */
    public List<String> Intergers2Strs(Set<Integer> list) {
        List<String> data = new ArrayList<>();
        if (list == null) {
            return null;
        }
        for (int temp : list) {
            switch (temp) {
                case 0:
                    data.add("不重复");
                    break;
                case 1:
                    data.add("星期一");
                    break;
                case 2:
                    data.add("星期二");
                    break;
                case 3:
                    data.add("星期三");
                    break;
                case 4:
                    data.add("星期四");
                    break;
                case 5:
                    data.add("星期五");
                    break;
                case 6:
                    data.add("星期六");
                    break;
                case 7:
                    data.add("星期日");
                    break;
            }
        }
        return data;
    }


    /**
     * 通过对比当前到23：59：59 与 闹铃时间到23：59：59的差值大小来判断是今天还是明天
     *
     * @param dayMillis 完整时间毫秒
     * @return 返回“后天”，“今天”或“明天” 或“昨天”，“前天” 或者星期几
     */
    public String todayOrToMorrow(long dayMillis) {


        //当前时间
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(System.currentTimeMillis());

        //闹钟时间
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(dayMillis);

        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR)) {
            return c2.get(Calendar.YEAR) + "-" + c2.get(Calendar.MONTH) + "-" + c2.get(Calendar.DAY_OF_MONTH);
        } else {
            if ((c1.get(Calendar.DAY_OF_YEAR) - c2.get(Calendar.DAY_OF_YEAR) >= 3) ||
                    (c1.get(Calendar.DAY_OF_YEAR) - c2.get(Calendar.DAY_OF_YEAR) <= -3)) {
                return c2.get(Calendar.MONTH) + 1 + "-" + c2.get(Calendar.DAY_OF_MONTH);
            } else if ((c1.get(Calendar.DAY_OF_YEAR) - c2.get(Calendar.DAY_OF_YEAR) >= 2)) {
                return "前天";
            } else if ((c1.get(Calendar.DAY_OF_YEAR) - c2.get(Calendar.DAY_OF_YEAR) >= 1)) {
                return "昨天";
            } else if ((c1.get(Calendar.DAY_OF_YEAR) - c2.get(Calendar.DAY_OF_YEAR) <= -2)) {
                return "后天";
            } else if ((c1.get(Calendar.DAY_OF_YEAR) - c2.get(Calendar.DAY_OF_YEAR) <= -1)) {
                return "明天";
            } else {
                return "今天";
            }
        }

    }

    /**
     * 小睡或者快速闹钟时候对时钟对象的时间进行处理
     *
     * @param mills 需要添加的时间毫秒差值（与当前时间的差值）
     * @param bean
     * @return
     */
    public void addMillsToAlarmBean(long mills, AlarmBean bean) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + mills);
        //设置具体时间毫秒
        bean.setTimeInMills(calendar.getTimeInMillis());
        int hour = calendar.get(Calendar.HOUR);
        int mins0 = calendar.get(Calendar.MINUTE);
        String timeStr = getTimeWithZero(hour, mins0);
        bean.setTimeStr(timeStr);

    }


    /**
     * 00:00
     *
     * @param num1
     * @param num2
     * @return
     */
    @NonNull
    public String getTimeWithZero(int num1, int num2) {
        //修改一下时间文本
        StringBuffer timeStr = new StringBuffer();
        if (num1 < 10) {
            timeStr.append("0");
        }
        timeStr.append(String.valueOf(num1))
                .append(":");
        if (num2 < 10) {
            timeStr.append("0");
        }
        timeStr.append(String.valueOf(num2));
        return timeStr.toString();
    }


    /**
     * Str的时间转化成long
     *
     * @param time
     * @return
     */
    public long getLongFromStr(String time) {
        long longTime = 0;
        try {
            Date parse = mFormatter.parse(time);
            longTime = parse.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return longTime;
    }


}
