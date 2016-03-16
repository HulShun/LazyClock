package com.example.lazyclock.view.acitivities.headportrait;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.lazyclock.MyApplication;
import com.example.lazyclock.R;
import com.example.lazyclock.bean.User;

/**
 * 显示头像大图
 * Created by Administrator on 2016/1/4.
 */
public class ShowLargeHeadActivity extends Activity {
    private LinearLayout backLayout;
    private ImageView imageView;
    private MyApplication mApp;
    private User myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.largeheadimageview);
        imageView = (ImageView) findViewById(R.id.largeimage);
        backLayout = (LinearLayout) findViewById(R.id.largeimage_back);

        mApp = (MyApplication) getApplication();
        myUser = mApp.getMyUser();
        String path = myUser.mHeadPath;

        Bitmap bitmap;
        if (path.equals("")) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.main_defaulthead);
        } else {
            bitmap = BitmapFactory.decodeFile(path);
        }

        imageView.setImageBitmap(bitmap);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowLargeHeadActivity.this.finish();
            }
        });
    }
}
