package com.example.lazyclock.bean;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.lazyclock.Config;
import com.example.lazyclock.utils.FileUtil;

import java.util.List;

/**
 * Created by Administrator on 2015/12/8.
 */
public class AlarmBean implements Parcelable {

    private int id;
    private String mAlarmName;

    /**
     * 记录闹铃是开启还是关闭状态
     */
    private boolean isWork;
    /**
     * 闹铃时间 XX时XX分
     */
    private String mTime;
    /**
     * 下一次闹铃的完整时间毫秒
     */
    private long mDateLong;

    /**
     * 闹钟是否重复
     */
    private boolean isrepeat;
    private String remainingTime;   //距离最近闹铃的剩余时间

    private List<String> days;
    private int mNextDayPosition;    //下一次闹铃在days中的下标

    private Ring mRing;

    private boolean isShank;      //震动
    /**
     * 小睡
     */
    private boolean isMoreSleep;

    private boolean isVolumegradual;   //铃声音量渐强
    private boolean isAlarmChanged;   //闹铃改动标记

    private int flag;      //修改或者新添加闹铃的标记

    private long startTime;    //开始设置闹铃的时间

    private int mVolume;         //闹钟音量
    private int sleepType;      //小睡唤醒方式
    private int mTimeOffset;   //延迟响铃时间
    private int stopSleepType;     //停止闹铃方式


    public AlarmBean(Context context) {

        // id = new Random().nextInt(1000);
        mTime = "08:00";
        setFlag(Config.AlARMTYPE_ADD);
        mRing = new Ring();
        //设置默认闹铃
        mRing.ringType = Config.RINGSELECT_RING;
        Uri u = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM);
        mRing.ringUri = FileUtil.getInstence().getPath(context, u);
        mRing.ringName = "默认";
        //days = new ArrayList<>();           //初始化重复方式
        //days.add("不重复");
        setWork(true);
        setShank(true);
        setVolumegradual(true);   //音量渐变
        setVolume(75);
        setMoreSleep(false);
        setSleepType(Config.MORESLEEP_SHAKE);
        setMoreSleepMins(5);
        setStopSleepType(Config.STOPSELECT_CLICK);
    }


    protected AlarmBean(Parcel in) {
        id = in.readInt();
        mAlarmName = in.readString();
        isWork = in.readByte() != 0;
        mTime = in.readString();
        mDateLong = in.readLong();
        isrepeat = in.readByte() != 0;
        remainingTime = in.readString();
        days = in.createStringArrayList();
        mRing = in.readParcelable(Ring.class.getClassLoader());
        isShank = in.readByte() != 0;
        isVolumegradual = in.readByte() != 0;
        isAlarmChanged = in.readByte() != 0;
        isMoreSleep = in.readByte() != 0;
        flag = in.readInt();
        startTime = in.readLong();
        mVolume = in.readInt();
        sleepType = in.readInt();
        mTimeOffset = in.readInt();
        stopSleepType = in.readInt();
        mNextDayPosition = in.readInt();
    }

    public static final Creator<AlarmBean> CREATOR = new Creator<AlarmBean>() {
        @Override
        public AlarmBean createFromParcel(Parcel in) {
            return new AlarmBean(in);
        }

        @Override
        public AlarmBean[] newArray(int size) {
            return new AlarmBean[size];
        }
    };

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> data) {
        days = data;
    }

    public String getName() {
        return mAlarmName;
    }

    public void setName(String mAlarmName) {
        this.mAlarmName = mAlarmName;
    }

    public int getNextDayPosition() {
        return mNextDayPosition;
    }

    public void setNextDayPosition(int mNextDayPosition) {
        if (days == null || days.isEmpty()) {
            this.mNextDayPosition = 0;
        } else {
            this.mNextDayPosition = mNextDayPosition % days.size();
        }
    }

    public boolean isrepeat() {
        return isrepeat;
    }

    public void setRepeat(boolean isrepeat) {
        this.isrepeat = isrepeat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isWork() {
        return isWork;
    }

    public void setWork(boolean work) {
        isWork = work;
    }

    public int getFlag() {
        return flag;
    }

    public boolean isMoreSleep() {
        return isMoreSleep;
    }

    public void setMoreSleep(boolean moreSleep) {
        isMoreSleep = moreSleep;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(String remainingTime) {
        this.remainingTime = remainingTime;
    }

    public boolean isAlarmChanged() {
        return isAlarmChanged;
    }

    public void setAlarmChanged(boolean alarmChanged) {
        isAlarmChanged = alarmChanged;
    }

    public Ring getRing() {
        return mRing;
    }

    public void setRing(Ring mRing) {
        this.mRing = mRing;
    }

    public void setShank(boolean shank) {
        isShank = shank;
    }

    public boolean isShank() {
        return isShank;
    }

    public boolean isVolumegradual() {
        return isVolumegradual;
    }

    public void setVolumegradual(boolean volumegradual) {
        isVolumegradual = volumegradual;
    }


    public String getTimeStr() {
        return mTime;
    }

    public void setTimeStr(String mTime) {
        this.mTime = mTime;
    }

    public int getMoreSleepMins() {
        return mTimeOffset;
    }

    public void setMoreSleepMins(int mTimeOffset) {
        this.mTimeOffset = mTimeOffset;
    }

    public long getTimeInMills() {
        return mDateLong;
    }

    public void setTimeInMills(long mData) {
        this.mDateLong = mData;
    }

    public int getVolume() {
        return mVolume;
    }

    public void setVolume(int mVolume) {
        this.mVolume = mVolume;
    }

    public int getSleepType() {
        return sleepType;
    }

    public void setSleepType(int sleepType) {
        this.sleepType = sleepType;
    }

    public int getStopSleepType() {
        return stopSleepType;
    }

    public void setStopSleepType(int stopSleepType) {
        this.stopSleepType = stopSleepType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(mAlarmName);
        dest.writeByte((byte) (isWork ? 1 : 0));
        dest.writeString(mTime);
        dest.writeLong(mDateLong);
        dest.writeByte((byte) (isrepeat ? 1 : 0));
        dest.writeString(remainingTime);
        dest.writeStringList(days);
        dest.writeParcelable(mRing, flags);
        dest.writeByte((byte) (isShank ? 1 : 0));
        dest.writeByte((byte) (isVolumegradual ? 1 : 0));
        dest.writeByte((byte) (isAlarmChanged ? 1 : 0));
        dest.writeByte((byte) (isMoreSleep ? 1 : 0));
        dest.writeInt(flag);
        dest.writeLong(startTime);
        dest.writeInt(mVolume);
        dest.writeInt(sleepType);
        dest.writeInt(mTimeOffset);
        dest.writeInt(stopSleepType);
        dest.writeInt(mNextDayPosition);
    }
}
