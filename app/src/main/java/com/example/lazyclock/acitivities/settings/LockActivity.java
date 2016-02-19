package com.example.lazyclock.acitivities.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lazyclock.AlarmState;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.R;
import com.example.lazyclock.bean.User;
import com.example.lazyclock.utils.LogUtil;
import com.example.lazyclock.utils.MD5Util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 锁定密码界面
 * Created by Administrator on 2015/12/10.
 */
public class LockActivity extends Activity {
    //check之前设置的密码
    private static final int CHECKANDCHANGE = 0;
    //设置新密码
    private static final int CHANGEPASSWORDS = 1;
    private static final int CHECK = 2;

    private static final int MESSAGE_TOASH = 10;
    private static final int MESSAGE_TEXT = 11;

    private EditText mLockEditText1;
    private EditText mLockEditText2;
    private EditText mLockEditText3;
    private EditText mLockEditText4;
    private TextView mTextView;
    private Button mLockOk;
    private Button mLockNo;

    private String passwordsMd5;
    private MyApplication myApp;
    private User mUser;
    /**
     * 标记是输入原始密码还是修改密码
     */
    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moresetting_lock);
        initViews();
        initData();
        initEvent();
    }

    private void initData() {
        myApp = (MyApplication) getApplication();
        mUser = myApp.getMyUser();
        Intent intent = getIntent();
        //关闭锁定的时候用到
        if (intent.getFlags() == AlarmState.CHECK_PASSWORDS) {
            state = AlarmState.CHECK_PASSWORDS;
        }
        // 初次设定密码
        else if (mUser.passwordsMD5.equals("")) {
            mTextView.setText("请输入4位数锁定密码");
            state = CHANGEPASSWORDS;
        }
        //修改密码
        else {
            mTextView.setText("请输入已设置的4位数密码");
            state = CHECKANDCHANGE;
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //显示输入法
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mLockEditText1, InputMethodManager.SHOW_FORCED);
            }
        }, 500);
    }

    private void initEvent() {
        mLockEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mLockEditText1.clearFocus();
                mLockEditText2.requestFocus();
            }
        });
        mLockEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mLockEditText2.clearFocus();
                mLockEditText3.requestFocus();
            }
        });
        mLockEditText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mLockEditText3.clearFocus();
                mLockEditText4.requestFocus();
            }
        });
        mLockEditText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //不加这句会在setText("")之后造成死循环
                if (s.toString().equals("")) {
                    return;
                }
                //如果当前输入的四位数密码是为了：验证之前的密码
                if (state == CHECKANDCHANGE) {
                    //如果密码正确
                    if (checkPasswords()) {
                        //状态改为修改密码
                        myHandle.sendEmptyMessage(MESSAGE_TEXT);
                        state = CHANGEPASSWORDS;
                    } else {
                        Message message = Message.obtain();
                        message.what = MESSAGE_TOASH;
                        message.obj = "密码错误！";
                        myHandle.sendMessage(message);
                    }
                    mLockEditText1.setText("");
                    mLockEditText2.setText("");
                    mLockEditText3.setText("");
                    mLockEditText4.setText("");
                    mLockEditText1.requestFocus();
                }
                //输入新密码完,或者关闭锁定时验证密码后，关闭输入法
                else if (state == CHANGEPASSWORDS) {
                    hideInputMethod();
                } else if (state == AlarmState.CHECK_PASSWORDS) {
                    if (checkPasswords()) {
                        setResult(AlarmState.CHECK_PASSWORDS);
                        LockActivity.this.finish();
                    } else {
                        Message message = Message.obtain();
                        message.what = MESSAGE_TOASH;
                        message.obj = "密码错误！";
                        myHandle.sendMessage(message);
                        mLockEditText1.setText("");
                        mLockEditText2.setText("");
                        mLockEditText3.setText("");
                        mLockEditText4.setText("");
                        mLockEditText1.requestFocus();
                    }
                }

            }
        });

        mLockOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改密码 或者密码验证
                if (state == CHANGEPASSWORDS || state == CHECK) {
                    StringBuffer sb = new StringBuffer();
                    sb.append(mLockEditText1.getText().toString());
                    sb.append(mLockEditText2.getText().toString());
                    sb.append(mLockEditText3.getText().toString());
                    sb.append(mLockEditText4.getText().toString());
                    if (sb.toString().length() < 4) {
                        Message message = Message.obtain();
                        message.what = MESSAGE_TOASH;
                        message.obj = "请输入完整的4位数密码";
                        myHandle.sendMessage(message);
                    } else {
                        try {
                            String md5str = MD5Util.md5Encode(sb.toString());
                            mUser.passwordsMD5 = md5str;
                            Message message = Message.obtain();
                            message.what = MESSAGE_TOASH;
                            message.obj = "密码修改成功！";
                            myHandle.sendMessage(message);

                            LogUtil.d("save", "LockActivity中保存设置");
                            myApp.saveSetting();
                            LockActivity.this.finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Message message = Message.obtain();
                    message.what = MESSAGE_TOASH;
                    message.obj = "请输入密码";
                    myHandle.sendMessage(message);
                }
            }
        });

        mLockNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockActivity.this.finish();
            }
        });
    }

    private boolean hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) LockActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(mLockEditText4.getApplicationWindowToken(), 0);
            return true;
        } else if (!imm.isActive()) {
            return true;
        }
        return false;
    }

    private boolean checkPasswords() {
        StringBuffer sb = new StringBuffer();
        sb.append(mLockEditText1.getText());
        sb.append(mLockEditText2.getText());
        sb.append(mLockEditText3.getText());
        sb.append(mLockEditText4.getText());
        try {
            String md5Str = MD5Util.md5Encode(sb.toString());
            if (md5Str.equals(mUser.passwordsMD5)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private void initViews() {
        mLockEditText1 = (EditText) findViewById(R.id.lock_editText1);
        mLockEditText2 = (EditText) findViewById(R.id.lock_editText2);
        mLockEditText3 = (EditText) findViewById(R.id.lock_editText3);
        mLockEditText4 = (EditText) findViewById(R.id.lock_editText4);
        mTextView = (TextView) findViewById(R.id.lock_Text1);
        mLockOk = (Button) findViewById(R.id.lock_ok);
        mLockNo = (Button) findViewById(R.id.lock_no);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (hideInputMethod()) {
                if (state == AlarmState.CHECK_PASSWORDS) {
                    LockActivity.this.finish();
                } else {
                    LockActivity.this.finish();
                }

            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onPause() {
        super.onPause();
        hideInputMethod();
    }


    private Handler myHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOASH:
                    Toast.makeText(LockActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;

                case MESSAGE_TEXT:
                    mTextView.setText("请输入新的四位数密码");
                    break;
            }

        }
    };
}



