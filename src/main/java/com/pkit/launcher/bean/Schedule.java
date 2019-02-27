package com.pkit.launcher.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by jiaxing on 2015/6/16.
 */
public class Schedule implements Parcelable {
    private String id;
    private String name;
    private String startTime;
    private int duration;
    private String description;
    private List<ScheduleSource> sources;

    public Schedule() {
       super();
    }

    public Schedule(Parcel source) {
        id = source.readString();
        name = source.readString();
        startTime = source.readString();
        duration = source.readInt();
        description = source.readString();
        sources = source.readArrayList(ScheduleSource.class.getClassLoader());
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ScheduleSource> getSources() {
        return sources;
    }

    public void setSources(List<ScheduleSource> sources) {
        this.sources = sources;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(startTime);
        dest.writeInt(duration);
        dest.writeString(description);
        dest.writeList(sources);
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel source) {
            return new Schedule(source);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };
}
