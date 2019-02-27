package com.pkit.launcher.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by jiaxing on 2015/6/16.
 */
public class ChannelGroup implements Parcelable {

    private String id;
    private String groupName;
    private List<Channel> channels;

    public ChannelGroup() {
        super();
    }

    public ChannelGroup(Parcel source) {
        id = source.readString();
        groupName = source.readString();
        channels = source.readArrayList(Channel.class.getClassLoader());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(groupName);
        dest.writeList(channels);
    }

    public static final Creator<ChannelGroup> CREATOR = new Creator<ChannelGroup>() {
        @Override
        public ChannelGroup createFromParcel(Parcel source) {
            return new ChannelGroup(source);
        }

        @Override
        public ChannelGroup[] newArray(int size) {
            return new ChannelGroup[size];
        }
    };
}
