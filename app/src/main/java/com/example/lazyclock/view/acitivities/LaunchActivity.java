package com.example.lazyclock.view.acitivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.lazyclock.MyApplication;
import com.example.lazyclock.R;

/**
 * Created by Administrator on 2015/12/16.
 */
public class LaunchActivity extends Activity {
    private MyApplication mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        //程序初始化
        mApp = (MyApplication) getApplication();
        mApp.initialize();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                startActivity(intent);
                LaunchActivity.this.finish();
            }
        }, 1000);

    }
}
