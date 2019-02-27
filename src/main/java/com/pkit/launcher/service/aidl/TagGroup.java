package com.pkit.launcher.service.aidl;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by jiaxing on 2015/6/16.
 */
public class TagGroup implements Parcelable {
    private String id;
    private String groupName;
    private List<Tag> tags;

    public TagGroup() {
        super();
    }

    public TagGroup(Parcel source) {
        id = source.readString();
        groupName = source.readString();
        tags = source.readArrayList(Tag.class.getClassLoader());
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

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(groupName);
        dest.writeList(tags);
    }

    public static final Creator<TagGroup> CREATOR = new Creator<TagGroup>() {
        @Override
        public TagGroup createFromParcel(Parcel source) {
            return new TagGroup(source);
        }

        @Override
        public TagGroup[] newArray(int size) {
            return new TagGroup[size];
        }
    };
}
