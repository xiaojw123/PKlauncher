package com.pkit.launcher.service.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 天气封装
 * @author Richard
 *
 */
public class Weather implements Parcelable {

	// ---------------------Parcelable implements------------

	public static final Creator<Weather> CREATOR = new Creator<Weather>() {

		@Override
		public Weather[] newArray(int size) {
			return null;
		}

		@Override
		public Weather createFromParcel(Parcel source) {
			return null;
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

	}
}
