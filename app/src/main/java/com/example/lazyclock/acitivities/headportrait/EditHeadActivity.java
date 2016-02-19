package com.example.lazyclock.acitivities.headportrait;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lazyclock.AlarmState;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.R;
import com.example.lazyclock.bean.User;
import com.example.lazyclock.customview.RoundImageView;
import com.soundcloud.android.crop.Crop;

import java.io.File;

/**
 * 头像和昵称的设置入口
 * Created by Administrator on 2015/12/31.
 */
public class EditHeadActivity extends Activity implements View.OnClickListener {
    private RelativeLayout mHeadView, mNameView;
    private RoundImageView mImageView;
    private Button mBackBtm;
    private TextView mNameTextView;
    private MyApplication mApp;
    private User myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edithead);

        mHeadView = (RelativeLayout) findViewById(R.id.edithead_id_headselect);
        mNameView = (RelativeLayout) findViewById(R.id.edithead_id_name);
        mImageView = (RoundImageView) findViewById(R.id.edithead_id_headimage);
        mBackBtm = (Button) findViewById(R.id.edithead_id_back);
        mNameTextView = (TextView) findViewById(R.id.edithead_id_nametext);

        mApp = (MyApplication) getApplication();
        myUser = mApp.getMyUser();
        mNameTextView.setText(myUser.mName);

        //加载图片
        Uri uri = Uri.fromFile(new File(myUser.mHeadPath));
        mImageView.setImageURI(uri);

        mImageView.setOnClickListener(this);
        mNameView.setOnClickListener(this);
        mBackBtm.setOnClickListener(this);
        mHeadView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edithead_id_back:
                setResult(AlarmState.ALARMTYPE_SETHEAD);
                EditHeadActivity.this.finish();
                break;
            //头像选择
            case R.id.edithead_id_headselect:
                Crop.pickImage(this, Crop.REQUEST_PICK);

                break;
            //头像大图
            case R.id.edithead_id_headimage:
                Intent intent2 = getIntent();
                intent2.setClass(EditHeadActivity.this, ShowLargeHeadActivity.class);
                startActivity(intent2);
                break;
            //昵称修改
            case R.id.edithead_id_name:
                Intent intent = getIntent();
                intent.setClass(EditHeadActivity.this, EditNikNameActivity.class);
                startActivityForResult(intent, AlarmState.ALARMTYPE_SETHEAD);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case AlarmState.ALARMTYPE_SETHEAD:
                myUser = mApp.getMyUser();
                mNameTextView.setText(myUser.mName);
                break;
            //获取照片然后裁剪
            case Crop.REQUEST_PICK:
                if (data != null) {
                    startPhotoZoom(data.getData());
                }
                break;
            //显示
            case Crop.REQUEST_CROP:
                mImageView.setImageURI(Crop.getOutput(data));
                mApp.saveSetting();
                break;
        }


    }

    /**
     * 裁剪
     *
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        File file = new File(getExternalFilesDir(null).getAbsolutePath(), "cropped.jpg");
        myUser.mHeadPath = file.getAbsolutePath();
        mApp.saveSetting();
        Uri destination = Uri.fromFile(file);
        Crop.of(uri, destination).asSquare().start(this, Crop.REQUEST_CROP);
    }
}
