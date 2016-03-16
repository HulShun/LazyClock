package com.example.lazyclock.view.acitivities.addalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.example.lazyclock.Config;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.R;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.service.RingtongService;

/**
 * 响铃方式的选择界面
 * Created by Administrator on 2015/12/10.
 */
public class StopTpyeActivity extends Activity implements View.OnClickListener {

    private RelativeLayout mClickView, mShankView, mCodeView, mMathView;
    private Button ok_Btm;

    private int mOldViewId = R.id.stoptype_id_clickbox;   //初始状态默认点击关闭
    private AlarmBean mAlarm;
    private MyApplication mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertdialog_stoptype);

        initView();
        initData();
        initEvent();
    }

    private void initView() {

        ok_Btm = (Button) findViewById(R.id.stoptype_id_okbtm);

        mClickView = (RelativeLayout) findViewById(R.id.stoptype_id_clickview);
        mShankView = (RelativeLayout) findViewById(R.id.stoptype_id_shankview);
        mCodeView = (RelativeLayout) findViewById(R.id.stoptype_id_codeview);
        mMathView = (RelativeLayout) findViewById(R.id.stoptype_id_mathview);

    }

    private void initData() {
        mApp = (MyApplication) getApplication();
        //获取上个acitivity传过来的Intent对象
        Intent intent = getIntent();
        mAlarm = intent.getParcelableExtra("stoptype");
        if (mAlarm.getStopSleepType() == Config.STOPSELECT_CLICK) {
            boxStateChange(mOldViewId, R.id.stoptype_id_clickbox);
            mOldViewId = R.id.stoptype_id_clickbox;
        } else if (mAlarm.getStopSleepType() == Config.STOPSELECT_CODE) {
            boxStateChange(mOldViewId, R.id.stoptype_id_codebox);
            mOldViewId = R.id.stoptype_id_codebox;
        } else if (mAlarm.getStopSleepType() == Config.STOPSELECT_MATH) {
            boxStateChange(mOldViewId, R.id.stoptype_id_mathbox);
            mOldViewId = R.id.stoptype_id_mathbox;
        } else if (mAlarm.getStopSleepType() == Config.STOPSELECT_SHANK) {
            boxStateChange(mOldViewId, R.id.stoptype_id_shankbox);
            mOldViewId = R.id.stoptype_id_shankbox;
        }

    }

    private void initEvent() {

        ok_Btm.setOnClickListener(this);
        mMathView.setOnClickListener(this);
        mClickView.setOnClickListener(this);
        mCodeView.setOnClickListener(this);
        mShankView.setOnClickListener(this);
    }


    /**
     * RadioButton 单选控制
     *
     * @param oldViewId
     * @param newViewId
     * @return
     */
    private int boxStateChange(int oldViewId, int newViewId) {
        if (oldViewId == newViewId) {
            return newViewId;
        }
        RadioButton temp = (RadioButton) findViewById(oldViewId);
        temp.setChecked(false);
        temp = (RadioButton) findViewById(newViewId);
        temp.setChecked(true);

        return newViewId;

    }


    @Override
    public void onClick(View v) {
        int m = v.getId();
        switch (m) {
            //确定按键
            case R.id.stoptype_id_okbtm:
                Intent intent = getIntent();
                intent.putExtra("stoptype", mAlarm);
                //返回数据给前一个activity
                setResult(Config.ADDALARM_TYPE_STOPSELECT, intent);
                finish();
                return;

            case R.id.stoptype_id_clickview:
                m = R.id.stoptype_id_clickbox;
                mAlarm.setStopSleepType(Config.STOPSELECT_CLICK);
                break;
            case R.id.stoptype_id_shankview:
                m = R.id.stoptype_id_shankbox;
                mAlarm.setStopSleepType(Config.STOPSELECT_SHANK);
                break;
            case R.id.stoptype_id_codeview:
                m = R.id.stoptype_id_codebox;
                mAlarm.setStopSleepType(Config.STOPSELECT_CODE);
                break;
            case R.id.stoptype_id_mathview:
                m = R.id.stoptype_id_mathbox;
                mAlarm.setStopSleepType(Config.STOPSELECT_MATH);
                break;
        }
        //修改checkBox的选择状态（单选）
        mOldViewId = boxStateChange(mOldViewId, m);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE || keyCode == KeyEvent.KEYCODE_POWER) {
            //进入小睡处理
            RingtongService.stopMediaServer(StopTpyeActivity.this, mAlarm, 0);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}



