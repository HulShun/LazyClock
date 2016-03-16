package com.example.lazyclock.view.acitivities.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lazyclock.MyApplication;
import com.example.lazyclock.R;
import com.example.lazyclock.utils.FileUtil;

/**
 * Created by Administrator on 2016/1/25.
 */
public class MainSettingActivity extends Activity {
    private Button mMoresettingBack;
    private LinearLayout mMoresettingLock;
    private LinearLayout mMoresettingBackup;
    private LinearLayout mMoresettingRecover;
    private LinearLayout mMoresettingOpinion;
    private LinearLayout mMoresettingAbout;

    private MyApplication mApp;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moresetting);
        initViews();
        mApp = (MyApplication) getApplication();

        initEvent();
    }

    private void initViews() {
        mMoresettingBack = (Button) findViewById(R.id.moresetting_back);
        mMoresettingLock = (LinearLayout) findViewById(R.id.moresetting_lock);
        mMoresettingBackup = (LinearLayout) findViewById(R.id.moresetting_backup);
        mMoresettingRecover = (LinearLayout) findViewById(R.id.moresetting_recover);
        mMoresettingOpinion = (LinearLayout) findViewById(R.id.moresetting_opinion);
        mMoresettingAbout = (LinearLayout) findViewById(R.id.moresetting_about);
    }

    private void initEvent() {
        mMoresettingBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainSettingActivity.this.finish();
            }
        });

        mMoresettingLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSettingActivity.this, LockActivity.class);
                startActivity(intent);
            }
        });

        //备份
        mMoresettingBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSettingActivity.this);
                View view = LayoutInflater.from(MainSettingActivity.this).inflate(R.layout.activity_moresetting_backup, null);
                builder.setView(view);
                setBtmEvent(mApp, view);
                dialog = builder.show();

            }

            /**
             * 备份对话框中的按钮处理时间
             *
             * @param view
             */
            private void setBtmEvent(final MyApplication app, View view) {
                Button okBtn = (Button) view.findViewById(R.id.backup_ok);
                Button nokBtn = (Button) view.findViewById(R.id.backup_no);

                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ProgressDialog _Pdialog = new ProgressDialog(MainSettingActivity.this);
                        showDialog(_Pdialog);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean b;
                                b = FileUtil.getInstence().backupList(app);
                                Message message = Message.obtain();
                                message.what = 0;
                                if (b) {
                                    message.obj = "备份成功。";
                                } else {
                                    message.obj = "备份失败。";
                                }
                                myHanler.sendMessage(message);
                                //保存框
                                _Pdialog.dismiss();
                                //保存对话框
                                dialog.dismiss();
                            }
                        }).start();

                    }
                });
                nokBtn.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

            private void showDialog(ProgressDialog dialog) {
                dialog.setMessage("正在保存...");
                dialog.setIndeterminate(true);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }
        });
        //恢复
        mMoresettingRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSettingActivity.this);
                View view = LayoutInflater.from(MainSettingActivity.this).inflate(R.layout.activity_moresetting_backup, null);
                builder.setView(view);
                setBtmEvent(mApp, view);
                dialog = builder.show();
            }

            /**
             * 备份对话框中的按钮处理时间
             *
             * @param view
             */
            private void setBtmEvent(final MyApplication app, View view) {
                Button okBtn = (Button) view.findViewById(R.id.backup_ok);
                Button nokBtn = (Button) view.findViewById(R.id.backup_no);
                TextView textView = (TextView) view.findViewById(R.id.backup_Text1);
                textView.setText("是否确定要恢复闹钟列表？");
                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ProgressDialog _Pdialog = new ProgressDialog(MainSettingActivity.this);
                        showDialog(_Pdialog);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean b;
                                b = FileUtil.getInstence().recoverList(app);
                                Message message = Message.obtain();
                                message.what = 0;
                                if (b) {
                                    message.obj = "恢复成功。";
                                } else {
                                    message.obj = "恢复失败。";
                                }
                                myHanler.sendMessage(message);
                                //保存框
                                _Pdialog.dismiss();
                                //保存对话框
                                dialog.dismiss();
                            }
                        }).start();

                    }
                });
                nokBtn.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

            private void showDialog(ProgressDialog dialog) {
                dialog.setMessage("正在恢复...");
                dialog.setIndeterminate(true);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }
        });

        mMoresettingAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSettingActivity.this);
                View view = LayoutInflater.from(MainSettingActivity.this).inflate(R.layout.activity_moresetting_backup, null);
                builder.setView(view);
                setBtmEvent(view);
                dialog = builder.show();
            }

            /**
             * 备份对话框中的按钮处理时间
             *
             * @param view
             */
            private void setBtmEvent(View view) {
                Button okBtn = (Button) view.findViewById(R.id.backup_ok);
                Button nokBtn = (Button) view.findViewById(R.id.backup_no);
                TextView textView = (TextView) view.findViewById(R.id.backup_Text1);
                textView.setText("个人小作品。作者：To老顺");
                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                nokBtn.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

        });
    }


    private Handler myHanler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String message = (String) msg.obj;
                    Toast.makeText(MainSettingActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


}
