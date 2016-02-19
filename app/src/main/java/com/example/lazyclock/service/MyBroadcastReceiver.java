package com.example.lazyclock.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 在Notificiaton对闹钟列表进行删除后，广播通知主界面刷新UI
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    public MyBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.


        throw new UnsupportedOperationException("Not yet implemented");
    }
}
