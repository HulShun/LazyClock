package com.example.lazyclock;

/**
 * 程序使用的各种常量
 * Created by Administrator on 2015/12/9.
 */
public class Config {

    public static final int RINGSELECT_SILENT = 10;     //静音
    public static final int RINGSELECT_RING = 11;       //响铃

    public static final int ADDALARM_TYPE_RINGSELECT = 20;         //选择铃声方式
    public static final int ADDALARM_TYPE_ALARMVOLUME = 21;         //选择闹铃音量
    public static final int ADDALARM_TYPE_MORESLEEPTYPE = 22;       //选择小睡方式
    public static final int ADDALARM_TYPE_MORESLEEPTIME = 23;       //选择小睡时间
    public static final int ADDALARM_TYPE_STOPSELECT = 24;          //选择停止闹铃方式

    public static final int AlARMTYPE_ADD = 30;       //添加的标识
    public static final int AlARMTYPE_EDIT = 31;       //修改的标识
    public static final int AlARMTYPE_ONOFF = 32;
    public static final int AlARMTYPE_PREVIEW = 33;       //闹钟预览的标识
    public static final int MORESLEEP_SHAKE = 40;

    public static final int STOPSELECT_CLICK = 50;
    public static final int STOPSELECT_SHANK = 51;
    public static final int STOPSELECT_CODE = 52;
    public static final int STOPSELECT_PIC = 53;
    public static final int STOPSELECT_MATH = 54;


    public static final int ALARMTYPE_SETHEAD = 60;

    public static final int UPDATE_UI_LISTVIEW = 70;        //刷新主界面的ListView
    public static final int UPDATE_UI_LISTVIEWBG = 71;        //刷新主界面的ListView背景
    public static final int CHECK_PASSWORDS = 72;    //验证锁定密码

    public static final int SERVICE_DEL = 80;

    public static final int QIUCKAlARM = 101;
    public static final int WEATHER_FLAG = 102;

    public static final String PREFERENCES_NAME = "settings";
    public static final String ALARM_DATEFORMAT = "yyyy年MM月dd日 a HH时mm分ss秒 EEEE";
    public static final String WEATHER_DATEFORMAT = "yyyy-MM-dd";

    public static final String TABLE_NAME = "math";
    public static final String MATH_DATABASE_NAME = "MathDB.db";

    public static final String TABLE_NAME_ALARM = "alarm";
    public static final String ALARM_DATABASE_NAME = "alarm.db";


    public static final String MATH_PATH = "http://www.tiku.cn/questions/index/sid/3168/p/";
    public static final String WEATHER_KAY = "b6a9a8eb83a23e67479eb27b2d3ea365";
    public static final String WEATHER_PATH = "http://apis.baidu.com/heweather/weather/free";

}
