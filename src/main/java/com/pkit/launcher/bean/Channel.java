package com.pkit.launcher.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by jiaxing on 2015/6/16.
 */
public class Channel implements Parcelable{

    private String id;
    private String channelName;
    private String logoUrl;
    private String channelNumber;
    private List<PhysicalChannel> physicalChannels;

    public Channel() {
        super();
    }

    public Channel(Parcel source) {
        id = source.readString();
        channelName = source.readString();
        logoUrl = source.readString();
        channelNumber = source.readString();
        physicalChannels = source.readArrayList(PhysicalChannel.class.getClassLoader());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(String channelNumber) {
        this.channelNumber = channelNumber;
    }

    public List<PhysicalChannel> getPhysicalChannels() {
        return physicalChannels;
    }

    public void setPhysicalChannels(List<PhysicalChannel> physicalChannels) {
        this.physicalChannels = physicalChannels;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(channelName);
        dest.writeString(logoUrl);
        dest.writeString(channelNumber);
        dest.writeList(physicalChannels);
    }

    public static final Creator<Channel> CREATOR = new Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel source) {
            return new Channel(source);
        }

        @Override
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };
}
