package com.pkit.launcher.service.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 播放源封装
 * 
 * @author Richard
 *
 */
public class Source implements Parcelable {
	/**
	 * 播放源名称
	 */
	public String name;
	/**
	 * 播放源地址
	 */
	public String url;
	/**
	 * 节目集ID
	 */
	public String id;
	/**
	 * 预览图片
	 */
	public String previewImg;

	public Source() {
	}

	public Source(String name, String url) {
		this.name = name;
		this.url = url;
	}

	@Override
	public String toString() {
		return "Source [name=" + name + ", url=" + url + "]";
	}

	// ---------------------Parcelable implements------------
	public static final Creator<Source> CREATOR = new Creator<Source>() {

		@Override
		public Source createFromParcel(Parcel source) {
			String name = source.readString();
			String url = source.readString();
			String id = source.readString();
			String previewImg = source.readString();

			Source sourceObj = new Source(name, url);
			sourceObj.id = id;
			sourceObj.previewImg = previewImg;
			return sourceObj;
		}

		@Override
		public Source[] newArray(int size) {
			return new Source[size];
		}

	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(url);
		dest.writeString(id);
		dest.writeString(previewImg);
	}
}
