package com.example.lazyclock.others;

import com.example.lazyclock.bean.AlarmBean;

import java.util.List;

/**
 * Created by Administrator on 2015/12/8.
 */
public interface OnTimeChangedListener {

    void onTimeChange(List<AlarmBean> data);
}
