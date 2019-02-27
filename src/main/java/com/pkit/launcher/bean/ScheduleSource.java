package com.pkit.launcher.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jiaxing on 2015/6/16.
 */
public class ScheduleSource implements Parcelable {
    private String id;
    private String name;
    private String fileSize;
    private int bitRateType;
    private int closedCaptioning;
    private String screenFormat;
    private String definition;
    private String playUrl;

    public ScheduleSource() {
        super();
    }

    public ScheduleSource(Parcel source) {
        id = source.readString();
        name = source.readString();
        fileSize = source.readString();
        bitRateType = source.readInt();
        closedCaptioning = source.readInt();
        screenFormat = source.readString();
        definition = source.readString();
        playUrl = source.readString();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public int getBitRateType() {
        return bitRateType;
    }

    public void setBitRateType(int bitRateType) {
        this.bitRateType = bitRateType;
    }

    public int getClosedCaptioning() {
        return closedCaptioning;
    }

    public void setClosedCaptioning(int closedCaptioning) {
        this.closedCaptioning = closedCaptioning;
    }

    public String getScreenFormat() {
        return screenFormat;
    }

    public void setScreenFormat(String screenFormat) {
        this.screenFormat = screenFormat;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(fileSize);
        dest.writeInt(bitRateType);
        dest.writeInt(closedCaptioning);
        dest.writeString(screenFormat);
        dest.writeString(definition);
        dest.writeString(playUrl);
    }

    public static final Creator<ScheduleSource> CREATOR = new Creator<ScheduleSource>() {
        @Override
        public ScheduleSource createFromParcel(Parcel source) {
            return new ScheduleSource(source);
        }

        @Override
        public ScheduleSource[] newArray(int size) {
            return new ScheduleSource[size];
        }
    };
}
