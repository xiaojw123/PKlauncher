package com.pkit.launcher.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jiaxing on 2015/6/17.
 */
public class LoginStatus implements Parcelable {

    private int code;
    private String desc;

    public LoginStatus() {
        super();
    }

    public LoginStatus(Parcel source) {
        code = source.readInt();
        desc = source.readString();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(desc);
    }

    public static final Creator<LoginStatus> CREATOR = new Creator<LoginStatus>() {
        @Override
        public LoginStatus createFromParcel(Parcel source) {
            return new LoginStatus(source);
        }

        @Override
        public LoginStatus[] newArray(int size) {
            return new LoginStatus[size];
        }
    };
}
