package com.example.lazyclock.view.acitivities.stopalarm;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lazyclock.Config;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.R;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.bean.User;
import com.example.lazyclock.service.RingtongService;
import com.example.lazyclock.utils.AlarmUtil;
import com.example.lazyclock.view.acitivities.MainActivity;
import com.example.lazyclock.view.acitivities.addalarm.AddAlarmActivity;

/**
 * Created by Administrator on 2015/12/28.
 */
public class TouchStopActivity extends BaseStopActivity {
    private TextView mTextView;
    private MyApplication mApp;
    private User myUser;

    private AlarmBean mAlarmBean;
    private Button mExitBtm;


    @Override
    protected void initData() {
        mApp = (MyApplication) getApplication();
        myUser = mApp.getMyUser();
        Intent intent = getIntent();
        mAlarmBean = intent.getParcelableExtra("Alarm");
        mTextView.setText(mAlarmBean.getTimeStr());
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_stop_touch);

        mTextView = (TextView) findViewById(R.id.touchstop_id_textview);
        mExitBtm = (Button) findViewById(R.id.touchstop_id_button);
    }

    @Override
    protected void initEvents() {
        final AlarmUtil util = AlarmUtil.getInstence();
        mExitBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭铃声和震动
                util.stopAlarmRingingServer(TouchStopActivity.this);
                //根据闹钟对象来设置下一次闹铃，或者关闭闹铃
                if (mAlarmBean.getFlag() == Config.AlARMTYPE_PREVIEW) {
                    Intent intent = new Intent(TouchStopActivity.this, AddAlarmActivity.class);
                    intent.putExtra("Alarm", mAlarmBean);
                    startActivity(intent);
                } else {
                    util.setNextAlarm(mApp, mAlarmBean);
                    myUser.setWeakUpDays(myUser.getWeakupDays() + 1);
                    Intent intent = new Intent(TouchStopActivity.this, MainActivity.class);
                    startActivity(intent);
                    TouchStopActivity.this.finish();
                }

            }
        });
    }

    @Override
    protected AlarmBean getAlarm() {
        return mAlarmBean;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE || keyCode == KeyEvent.KEYCODE_POWER) {
            //进入小睡处理
            RingtongService.stopMediaServer(TouchStopActivity.this, mAlarmBean, 0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
