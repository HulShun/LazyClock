package com.example.lazyclock.others;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.bean.Ring;
import com.example.lazyclock.utils.LogUtil;

import java.io.IOException;

/**
 * Created by Administrator on 2016/1/28.
 */
public class MyMediaPlayer {

    /**
     * 预览模式
     */
    public final static int FLAG_PREVIEW = 0x0001;
    /**
     * 工作模式
     */
    public final static int FLAG_WORK = 0x0002;

    private MediaPlayer mMediaPlayer;

    private Context mContext;
    private AlarmBean mAlarm;

    private Ring mRing;
    private int mVolume;
    private int mFlag;
    private MyHandler handler;

    public MyMediaPlayer(Context context, AlarmBean bean, int flag) {
        mContext = context;
        mAlarm = bean;
        //比例计算
        mVolume = mAlarm.getVolume() * 15 / 100;
        mRing = mAlarm.getRing();
        mFlag = flag;

        handler = new MyHandler();
    }

    public void start() {
        Uri uri = Uri.parse(mRing.ringUri);
        if (uri == null) {
            uri = RingtoneManager.getActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_ALARM);
        }

        mMediaPlayer = MediaPlayer.create(mContext, uri);
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mediaPlayerSetting();
        }

        final Uri finalUri = uri;
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtil.e("media", "MediaPlayer错误");
                try {
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(mContext, finalUri);
                    mediaPlayerSetting();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer.start();
                //音量调节
                if (mFlag == FLAG_WORK && mAlarm.isVolumegradual()) {
                    handler.sendEmptyMessage(0);
                } else {
                    mMediaPlayer.setVolume(mVolume, mVolume);
                }

            }
        });
    }


    private void mediaPlayerSetting() {
        // mMediaPlayer.setAudioStreamType(STREAM_TYPE);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.prepareAsync();
    }

    class MyHandler extends Handler {
        private float nowVolume;

        public MyHandler() {
            nowVolume = 0.1f;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (mMediaPlayer.isPlaying()) {
                        if (nowVolume <= mVolume) {
                            mMediaPlayer.setVolume(nowVolume, nowVolume);
                            nowVolume += 0.1;
                            //  LogUtil.i("media", "闹钟音量" + nowVolume);
                            MyHandler.this.sendEmptyMessageDelayed(0, 500);
                        }
                    }
                    break;

            }
        }
    }


    public void stop() {
        mMediaPlayer.stop();
        // mMediaPlayer.release();
    }
}
