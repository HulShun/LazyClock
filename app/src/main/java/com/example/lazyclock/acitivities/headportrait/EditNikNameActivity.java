package com.example.lazyclock.acitivities.headportrait;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.lazyclock.AlarmState;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.R;
import com.example.lazyclock.bean.User;

/**
 * Created by Administrator on 2016/1/4.
 */
public class EditNikNameActivity extends Activity implements View.OnClickListener {
    private Button mBackBtm, mOkBtm;
    private EditText mEditText;
    private MyApplication mApp;
    private User myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edithead_name);

        mBackBtm = (Button) findViewById(R.id.edithead_name_id_back);
        mOkBtm = (Button) findViewById(R.id.edithead_name_id_ok);
        mEditText = (EditText) findViewById(R.id.edithead_name_id_edit);

        mApp = (MyApplication) getApplication();
        myUser = mApp.getMyUser();
        mEditText.setText(myUser.mName);

        //当文本修改之后，保存按键可以操作
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mOkBtm.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mOkBtm.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    mOkBtm.setEnabled(true);
                } else {
                    mOkBtm.setEnabled(false);
                }
            }
        });

        mBackBtm.setOnClickListener(this);
        mOkBtm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edithead_name_id_back:
                finish();
                break;

            case R.id.edithead_name_id_ok:
                myUser.mName = mEditText.getText().toString();
                mApp.saveSetting();
                setResult(AlarmState.ALARMTYPE_SETHEAD);
                finish();
                break;
        }
    }
}
