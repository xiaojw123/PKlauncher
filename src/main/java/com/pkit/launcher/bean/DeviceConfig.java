package com.pkit.launcher.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by jiaxing on 2015/6/17.
 */
public class DeviceConfig implements Parcelable {

    private String deviceId;
    private String accountId;
    private String userId;
    private String password;
    private String regionId;
    private String templateId;
    private String state;
    private String token;
    private int makeType;
    private long timestamp;
    private String ruleId;
    private List<Address> addressList;

    public DeviceConfig() {
        super();
    }

    public DeviceConfig(Parcel source) {
        deviceId = source.readString();
        accountId = source.readString();
        userId = source.readString();
        password = source.readString();
        regionId = source.readString();
        templateId = source.readString();
        state = source.readString();
        token = source.readString();
        makeType = source.readInt();
        timestamp = source.readLong();
        ruleId = source.readString();
        addressList = source.readArrayList(Address.class.getClassLoader());
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getMakeType() {
        return makeType;
    }

    public void setMakeType(int makeType) {
        this.makeType = makeType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deviceId);
        dest.writeString(accountId);
        dest.writeString(userId);
        dest.writeString(password);
        dest.writeString(regionId);
        dest.writeString(templateId);
        dest.writeString(state);
        dest.writeString(token);
        dest.writeInt(makeType);
        dest.writeLong(timestamp);
        dest.writeString(ruleId);
        dest.writeList(addressList);
    }

    public static final Creator<DeviceConfig> CREATOR = new Creator<DeviceConfig>() {
        @Override
        public DeviceConfig createFromParcel(Parcel source) {
            return new DeviceConfig(source);
        }

        @Override
        public DeviceConfig[] newArray(int size) {
            return new DeviceConfig[size];
        }
    };
}
