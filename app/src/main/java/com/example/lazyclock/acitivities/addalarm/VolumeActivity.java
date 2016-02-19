package com.example.lazyclock.acitivities.addalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.lazyclock.AlarmState;
import com.example.lazyclock.R;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.others.MyMediaPlayer;

/**
 * 响铃方式的选择界面
 * Created by Administrator on 2015/12/10.
 */
public class VolumeActivity extends Activity {
    private SeekBar mBar;
    private TextView mProgressText;
    private CheckBox mCheckBox;
    private Button mOk_Btm;
    private AlarmBean mAlarm;

    private MyMediaPlayer myMediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertdialog_volume);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mBar = (SeekBar) findViewById(R.id.volume_id_seekBar);
        mProgressText = (TextView) findViewById(R.id.volume_id_progress);
        mCheckBox = (CheckBox) findViewById(R.id.volume_id_check);
        mOk_Btm = (Button) findViewById(R.id.volume_id_button);
    }

    private void initData() {
        Intent intent = getIntent();
        mBar.setMax(100);

        mAlarm = intent.getParcelableExtra("Alarm");
        myMediaPlayer = new MyMediaPlayer(VolumeActivity.this, mAlarm, MyMediaPlayer.FLAG_PREVIEW);
        mBar.setProgress(mAlarm.getVolume());
        mCheckBox.setChecked(mAlarm.isVolumegradual());
    }

    private void initEvent() {

        mBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mProgressText.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mAlarm.setVolume(seekBar.getProgress());
            }
        });

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAlarm.setVolumegradual(isChecked);
            }
        });


        mOk_Btm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("volume", mAlarm);
                setResult(AlarmState.ADDALARM_TYPE_ALARMVOLUME, intent);

                finish();
            }
        });
    }


}



