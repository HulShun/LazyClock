package com.example.lazyclock.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015/12/10.
 */
public class Ring implements Parcelable {

    public String ringUri;
    public String ringName;
    public int ringType;

    public Ring() {
        ringName = "默认";
    }


    protected Ring(Parcel in) {
        ringUri = in.readString();
        ringName = in.readString();
        ringType = in.readInt();
    }

    public static final Creator<Ring> CREATOR = new Creator<Ring>() {
        @Override
        public Ring createFromParcel(Parcel in) {
            return new Ring(in);
        }

        @Override
        public Ring[] newArray(int size) {
            return new Ring[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ringUri);
        dest.writeString(ringName);
        dest.writeInt(ringType);
    }
}