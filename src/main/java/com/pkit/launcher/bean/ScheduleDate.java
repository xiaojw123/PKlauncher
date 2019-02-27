package com.pkit.launcher.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by jiaxing on 2015/6/16.
 */
public class ScheduleDate implements Parcelable {

    private String startDate;
    private List<Schedule> scheduleList;

    public ScheduleDate() {
        super();
    }

    public ScheduleDate(Parcel source) {
        startDate = source.readString();
        scheduleList = source.readArrayList(Schedule.class.getClassLoader());
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public List<Schedule> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(startDate);
        dest.writeList(scheduleList);
    }

    public static final Creator<ScheduleDate> CREATOR = new Creator<ScheduleDate>() {
        @Override
        public ScheduleDate createFromParcel(Parcel source) {
            return new ScheduleDate(source);
        }

        @Override
        public ScheduleDate[] newArray(int size) {
            return new ScheduleDate[size];
        }
    };
}
