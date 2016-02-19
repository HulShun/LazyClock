package com.example.lazyclock.acitivities.addalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.lazyclock.AlarmState;
import com.example.lazyclock.R;
import com.example.lazyclock.bean.AlarmBean;

/**
 * 响铃方式的选择界面
 * Created by Administrator on 2015/12/10.
 */
public class SleepTimeActivity extends Activity {
    private SeekBar mBar;
    private Button mOk_Btm;
    private AlarmBean mAlarm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertdialog_sleeptime);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mBar = (SeekBar) findViewById(R.id.sleeptime_id_seekBar);
        mOk_Btm = (Button) findViewById(R.id.sleeptime_id_button);
    }

    private void initData() {
        Intent intent = getIntent();
        mBar.setMax(100);
        mAlarm = intent.getParcelableExtra("sleeptime");
        mBar.setProgress(mAlarm.getMoreSleepMins() * 4 + mAlarm.getMoreSleepMins());

    }


    private void initEvent() {

        mBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    if (progress >= 0 && progress < 20) {
                        seekBar.setProgress(0);

                    } else if (progress >= 20 && progress < 40) {
                        seekBar.setProgress(25);

                    } else if (progress >= 40 && progress < 60) {
                        seekBar.setProgress(50);

                    } else if (progress >= 60 && progress < 80) {
                        seekBar.setProgress(75);

                    } else if (progress >= 80 && progress <= 100) {
                        seekBar.setProgress(100);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int m = 20 * seekBar.getProgress() / 100;
                mAlarm.setMoreSleepMins(m);
            }
        });


        mOk_Btm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("sleeptime", mAlarm);
                setResult(AlarmState.ADDALARM_TYPE_MORESLEEPTIME, intent);
                finish();
            }
        });
    }


}



