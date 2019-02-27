package com.pkit.launcher.service.aidl;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 返回数据整体类型分为:栏目,影片,影片基本信息.Content是返回各类型数据的基类.
 * @author Richard
 *
 */
public class Content implements Parcelable {
	/**
	 * 父栏目id
	 */
	public String parentID;
	/**
	 * id
	 */
	public String contentID;
	/**
	 * name
	 */
	public String name;
	/**
	 * 图片集合,为了实现一些效果,一个栏目或影片的海报可能是多张图片合成的
	 */
	public ArrayList<String> imgPaths;

	public Content() {
		this.imgPaths = new ArrayList<String>();
	}

	public Content(String parentID, String contentID, String name, ArrayList<String> imgPaths) {
		this.parentID = parentID;
		this.contentID = contentID;
		this.name = name;
		this.imgPaths = imgPaths;
	}

	// ---------------------Parcelable implements------------

	@Override
	public String toString() {
		return "Content [parentID=" + parentID + ", contentID=" + contentID + ", name=" + name + ", imgPaths=" + imgPaths + "]";
	}

	public static final Creator<Content> CREATOR = new Creator<Content>() {

		@Override
		public Content createFromParcel(Parcel source) {
			String parentID = source.readString();
			String contentID = source.readString();
			String name = source.readString();
			ArrayList<String> imgPaths = new ArrayList<String>();
			source.readStringList(imgPaths);
			
			Content content = new Content(parentID, contentID, name, imgPaths);
			return content;
		}

		@Override
		public Content[] newArray(int size) {
			return new Content[size];
		}

	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(parentID);
		dest.writeString(contentID);
		dest.writeString(name);
		dest.writeStringList(imgPaths);
	}
}
