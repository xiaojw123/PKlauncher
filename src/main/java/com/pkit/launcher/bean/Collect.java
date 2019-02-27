package com.pkit.launcher.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jiaxing on 2015/6/16.
 */
public class Collect implements Parcelable {
    private String id;
    private String name;
    private String thumbnail;
    private String poster;
    private String stills;
    private int starLevel;
    private String actors;
    private String viewPoint;
    private int isNew;

    public Collect() {
        super();
    }

    public Collect(Parcel source) {
        id = source.readString();
        name = source.readString();
        thumbnail = source.readString();
        poster = source.readString();
        stills = source.readString();
        starLevel = source.readInt();
        actors = source.readString();
        viewPoint = source.readString();
        isNew = source.readInt();
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getStills() {
        return stills;
    }

    public void setStills(String stills) {
        this.stills = stills;
    }

    public int getStarLevel() {
        return starLevel;
    }

    public void setStarLevel(int starLevel) {
        this.starLevel = starLevel;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getViewPoint() {
        return viewPoint;
    }

    public void setViewPoint(String viewPoint) {
        this.viewPoint = viewPoint;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(thumbnail);
        dest.writeString(poster);
        dest.writeString(stills);
        dest.writeInt(starLevel);
        dest.writeString(actors);
        dest.writeString(viewPoint);
        dest.writeInt(isNew);
    }

    public static final Creator<Collect> CREATOR = new Creator<Collect>() {
        @Override
        public Collect createFromParcel(Parcel source) {
            return new Collect(source);
        }

        @Override
        public Collect[] newArray(int size) {
            return new Collect[size];
        }
    };
}
