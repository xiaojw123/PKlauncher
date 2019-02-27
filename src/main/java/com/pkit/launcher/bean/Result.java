package com.pkit.launcher.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jiaxing on 2015/6/16.
 */
public class Result<T extends Parcelable> implements Parcelable{
    private int code;
    private String errorCode;
    private String errorMsg;
    private T entity;

    public Result() {
        super();
    }

    public Result(Parcel source) {
        code = source.readInt();
        errorCode = source.readString();
        errorMsg = source.readString();
        entity = source.readParcelable(entity.getClass().getClassLoader());
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(errorCode);
        dest.writeString(errorMsg);
        dest.writeParcelable(entity,PARCELABLE_WRITE_RETURN_VALUE);
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel source) {
            return new Result(source);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };
}
