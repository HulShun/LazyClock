package com.example.lazyclock.others.Task;

import android.app.Application;

import com.example.lazyclock.MyApplication;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.others.OnTimeChangedListener;
import com.example.lazyclock.utils.TimeUtil;

import java.util.List;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016-03-15.
 */
public class DifferTimeTask extends TimerTask {
    private List<AlarmBean> list;
    private TimeUtil util;
    private OnTimeChangedListener listener;

    private final boolean flag[] = {true};

    public DifferTimeTask(Application app) {
        if (app instanceof MyApplication) {
            list = ((MyApplication) app).getAlarDates();
            util = TimeUtil.getInstance();
        }
    }

    public void setOnTimeChangedListener(OnTimeChangedListener listener) {
        this.listener = listener;
    }


    public void setWait(boolean b) {
        if (!b) {
            synchronized (flag) {
                flag.notifyAll();
            }
        }
        flag[0] = b;
    }

    @Override
    public void run() {
        synchronized (flag) {
            if (flag[0]) {
                try {
                    flag.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (list == null || list.isEmpty()) {
            return;
        }
        for (AlarmBean bean : list) {
            bean.setRemainingTime(util.calculateTime(bean.getTimeInMills(), System.currentTimeMillis()));
        }
        if (listener != null) {
            listener.onTimeChange(list);
        }
    }
}
