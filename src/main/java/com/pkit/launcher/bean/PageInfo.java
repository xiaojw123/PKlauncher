package com.pkit.launcher.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jiaxing on 2015/6/16.
 */
public class PageInfo implements Parcelable{
    private int pageNumber;
    private int pageSize;
    private int totalPage;
    private int totalNumber;

    public PageInfo() {
        super();
    }

    public PageInfo(Parcel source) {
        pageNumber = source.readInt();
        pageSize = source.readInt();
        totalPage = source.readInt();
        totalNumber = source.readInt();
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pageNumber);
        dest.writeInt(pageSize);
        dest.writeInt(totalPage);
        dest.writeInt(totalNumber);
    }

    public static final Creator<PageInfo> CREATOR = new Creator<PageInfo>() {
        @Override
        public PageInfo createFromParcel(Parcel source) {
            return new PageInfo(source);
        }

        @Override
        public PageInfo[] newArray(int size) {
            return new PageInfo[size];
        }
    };
}
