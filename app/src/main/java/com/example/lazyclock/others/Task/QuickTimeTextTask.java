package com.example.lazyclock.others.Task;

import com.example.lazyclock.Config;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.utils.AlarmUtil;
import com.example.lazyclock.utils.TimeUtil;

import java.util.Calendar;
import java.util.TimerTask;

/**
 * 快速闹铃设置
 * Created by Administrator on 2016-03-15.
 */
public class QuickTimeTextTask extends TimerTask {


    private OnTimeFreshListener listener;

    public interface OnTimeFreshListener {
        void onTimeChanged(String timeStr);
    }

    public QuickTimeTextTask(OnTimeFreshListener listener) {
        this.listener = listener;
    }

    @Override

    public void run() {
        AlarmBean bean1 = AlarmUtil.getInstence().getAlarmByFlag(MyApplication.getInstance(),Config.QIUCKAlARM);
        if (bean1 != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(bean1.getTimeInMills() - System.currentTimeMillis());
            String timeStr = TimeUtil.getInstance().getTimeWithZero(calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
            listener.onTimeChanged(timeStr);

        } else {
            this.cancel();
        }
    }

}
