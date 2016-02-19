package com.example.lazyclock.acitivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lazyclock.AlarmState;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.R;
import com.example.lazyclock.acitivities.addalarm.AddAlarmActivity;
import com.example.lazyclock.bean.AlarmBean;

/**
 * 响铃方式的选择界面
 * Created by Administrator on 2015/12/10.
 */
public class ItemClickActivity extends Activity {

    private LinearLayout mStopOrStart_Btm, mDele_Btm, mEdit_Btm;
    private AlarmBean mAlarm;
    private TextView mOnOffText;
    private Button mOkBtm, mNoBtm;
    private MyApplication mApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        setContentView(R.layout.alertdialog_recylcerview_itemclick);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mEdit_Btm = (LinearLayout) findViewById(R.id.recyclerview_id_editAlarm);
        mStopOrStart_Btm = (LinearLayout) findViewById(R.id.recyclerview_id_closealarm);
        mDele_Btm = (LinearLayout) findViewById(R.id.recyclerview_id_delectlarm);
        mOnOffText = (TextView) findViewById(R.id.recyclerview_id_closealarm_text);
    }

    private void initData() {
        Intent intent = getIntent();
        mAlarm = intent.getParcelableExtra("Alarm");
        mApp = (MyApplication) getApplication();
        if (!mAlarm.isWork()) {
            mOnOffText.setText(getResources().getString(R.string.recyclerview_selection2_1));
        } else {
            mOnOffText.setText(getResources().getString(R.string.recyclerview_selection2));
        }

    }

    private void initEvent() {
        mEdit_Btm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemClickActivity.this, AddAlarmActivity.class);
                intent.putExtra("Alarm", mAlarm);
                intent.setFlags(AlarmState.AlARMTYPE_EDIT);
                startActivityForResult(intent, AlarmState.AlARMTYPE_EDIT);
                finish();
            }
        });


        mDele_Btm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(ItemClickActivity.this, R.style.Dialog)).create();
                View view = LayoutInflater.from(ItemClickActivity.this).inflate(R.layout.alertdialog_recylcerview_itemclick_check, null);
                dialog.setView(view, 0, 0, 0, 0);
                dialog.show();

                mOkBtm = (Button) view.findViewById(R.id.main_list_check_ok);
                mNoBtm = (Button) view.findViewById(R.id.main_list_check_no);

                mOkBtm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApp.deleteAlarm(ItemClickActivity.this, mAlarm);

                        dialog.cancel();
                        setResult(AlarmState.AlARMTYPE_EDIT);
                        ItemClickActivity.this.finish();
                    }
                });
                mNoBtm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.cancel();
                    }
                });

            }
        });

        mStopOrStart_Btm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAlarm.isWork()) {
                    mAlarm.setWork(false);
                    mApp.deleteAlarm(ItemClickActivity.this, mAlarm);
                } else {
                    mAlarm.setWork(true);
                    //如果是过去的时间
                    if (mAlarm.getTimeInMills() < System.currentTimeMillis()) {

                    }
                    mApp.setAlarmToSystem(ItemClickActivity.this, mAlarm);
                }

                setResult(AlarmState.AlARMTYPE_ONOFF);
                ItemClickActivity.this.finish();
            }
        });
    }


}



