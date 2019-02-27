package com.pkit.launcher.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jiaxing on 2015/6/16.
 */
public class PhysicalChannel implements Parcelable {
    private String id;
    private int bitRateType;
    private String channelURL;
    private String timeShiftURL;

    public PhysicalChannel(Parcel source) {
        id = source.readString();
        bitRateType = source.readInt();
        channelURL = source.readString();
        timeShiftURL = source.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBitRateType() {
        return bitRateType;
    }

    public void setBitRateType(int bitRateType) {
        this.bitRateType = bitRateType;
    }

    public String getChannelURL() {
        return channelURL;
    }

    public void setChannelURL(String channelURL) {
        this.channelURL = channelURL;
    }

    public String getTimeShiftURL() {
        return timeShiftURL;
    }

    public void setTimeShiftURL(String timeShiftURL) {
        this.timeShiftURL = timeShiftURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(bitRateType);
        dest.writeString(channelURL);
        dest.writeString(timeShiftURL);
    }

    public static final Creator<PhysicalChannel> CREATOR = new Creator<PhysicalChannel>() {
        @Override
        public PhysicalChannel createFromParcel(Parcel source) {
            return new PhysicalChannel(source);
        }

        @Override
        public PhysicalChannel[] newArray(int size) {
            return new PhysicalChannel[size];
        }
    };
}
