package com.example.lazyclock.view.acitivities.addalarm;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lazyclock.Config;
import com.example.lazyclock.R;
import com.example.lazyclock.bean.Ring;
import com.example.lazyclock.utils.FileUtil;

/**
 * 响铃方式的选择界面
 * Created by Administrator on 2015/12/10.
 */
public class RingTpyeActivity extends Activity implements View.OnClickListener {

    private RelativeLayout silentBtm, ringBtm;
    private Button ok_Btm;
    private TextView mRingtoneNameView;

    private int mOldViewId = R.id.alertdialog_id_ring;   //初始状态默认选择系统铃声方式

    private Ring mRing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.alertdialog_ringtype);
        initView();
        initData();
        initEvent();
    }

    private void initView() {

        ok_Btm = (Button) findViewById(R.id.alertdialog_id_ok);

        silentBtm = (RelativeLayout) findViewById(R.id.alertdialog_id_silentbtm);
        ringBtm = (RelativeLayout) findViewById(R.id.alertdialog_id_ringbtm);

        mRingtoneNameView = (TextView) findViewById(R.id.alertdialog_id_ringtonename);

    }

    private void initData() {

        //获取上个acitivity传过来的Intent对象
        Intent intent = getIntent();
        mRing = intent.getParcelableExtra("mRing");

        if (mRing.ringType == Config.RINGSELECT_RING) {
            mRingtoneNameView.setText(mRing.ringName);

        } else if (mRing.ringType == Config.RINGSELECT_SILENT) {
            boxStateChange(mOldViewId, R.id.alertdialog_id_silent);
            mOldViewId = R.id.alertdialog_id_silent;
        }


    }

    private void initEvent() {

        ok_Btm.setOnClickListener(this);
        silentBtm.setOnClickListener(this);
        ringBtm.setOnClickListener(this);
    }


    /**
     * 回传数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri ringtoneUri;
        if (resultCode != RESULT_OK) {
            return;
        }
        //根据不同请求码做相应的处理
        switch (requestCode) {

            case Config.RINGSELECT_RING:
                //从系统铃声选择器中返回的数据处理
                ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                if (ringtoneUri != null) {
                    mRing.ringName = getDisPlayNameForUri(ringtoneUri);
                    mRing.ringUri = FileUtil.getInstence().getPath(RingTpyeActivity.this, ringtoneUri);
                    mRingtoneNameView.setText(mRing.ringName);
                }
                break;
        }


    }


    /**
     * 选择系统铃声
     */
    private void doPickRingtone() {
        //开启铃声选择器
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);

        Uri ringtoneUri;
        //如果已经选择过了铃声，就会有个记录的uri
        ringtoneUri = Uri.parse(mRing.ringUri);

        //传递之前选择位置的uri给铃声选择器
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtoneUri);

        startActivityForResult(intent, Config.RINGSELECT_RING);

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


    /**
     * 从uri中获取文件名
     *
     * @param uri
     * @return
     */
    private String getDisPlayNameForUri(Uri uri) {
        String disPlayName = null;

        //从系统ContentProvider中获得uri
        if (uri.getScheme().equalsIgnoreCase("content")) {
            String[] projection = {
                    MediaStore.Audio.Media.TITLE
            };
            Cursor c = getContentResolver().query(uri, projection, null, null, null);
            if (c != null) {
                c.moveToNext();
                disPlayName = c.getString(0);
            }
        }
        //可能通过其他程序选择的音频文件
        else {
            String uriStr = uri.getPath();
            disPlayName = uriStr.substring(uriStr.lastIndexOf("/") + 1, uriStr.lastIndexOf("."));
        }


        return disPlayName;
    }

    /**
     * 从uri中获取文件绝对路径
     *
     * @param contentUri
     * @return
     */
    private String getRealPathFromURI(Uri contentUri) {

        if (contentUri.getScheme().equalsIgnoreCase("content")) {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        //通过其他程序设置铃声
        else {
            String uriStr = contentUri.getPath();
            return uriStr.substring(uriStr.indexOf("/") + 2);
        }

    }


    @Override
    public void onClick(View v) {
        int m = v.getId();
        switch (m) {
            //确定按键
            case R.id.alertdialog_id_ok:
                Intent intent = getIntent();
                intent.putExtra("mRing", mRing);
                //返回数据给前一个activity
                setResult(Config.ADDALARM_TYPE_RINGSELECT, intent);
                finish();
                return;

            //选择了静音
            case R.id.alertdialog_id_silentbtm:
                m = R.id.alertdialog_id_silent;
                mRing.ringType = Config.RINGSELECT_SILENT;
                break;


            //选择了铃声
            case R.id.alertdialog_id_ringbtm:
                m = R.id.alertdialog_id_ring;
                mRing.ringType = Config.RINGSELECT_RING;
                doPickRingtone();

                break;


        }
        //修改checkBox的选择状态（单选）
        mOldViewId = boxStateChange(mOldViewId, m);
    }


}



