package com.pkit.launcher.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jiaxing on 2015/6/17.
 */
public class Upgrade implements Parcelable {

    private String md5;
    private String packageLocation;
    private String upgradeState;
    private String versionSeq;
    private String versionName;

    public Upgrade() {
        super();
    }

    public Upgrade(Parcel source) {
        md5 = source.readString();
        packageLocation = source.readString();
        upgradeState = source.readString();
        versionSeq = source.readString();
        versionName = source.readString();
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getPackageLocation() {
        return packageLocation;
    }

    public void setPackageLocation(String packageLocation) {
        this.packageLocation = packageLocation;
    }

    public String getUpgradeState() {
        return upgradeState;
    }

    public void setUpgradeState(String upgradeState) {
        this.upgradeState = upgradeState;
    }

    public String getVersionSeq() {
        return versionSeq;
    }

    public void setVersionSeq(String versionSeq) {
        this.versionSeq = versionSeq;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(md5);
        dest.writeString(packageLocation);
        dest.writeString(upgradeState);
        dest.writeString(versionSeq);
        dest.writeString(versionName);
    }

    public static final Creator<Upgrade> CREATOR = new Creator<Upgrade>() {
        @Override
        public Upgrade createFromParcel(Parcel source) {
            return new Upgrade(source);
        }

        @Override
        public Upgrade[] newArray(int size) {
            return new Upgrade[size];
        }
    };
}
