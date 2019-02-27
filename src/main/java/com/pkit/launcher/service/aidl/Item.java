package com.pkit.launcher.service.aidl;

import android.os.Parcel;

import java.util.ArrayList;

/**
 * 影片数据封装
 * 
 * @author Richard
 *
 */
public class Item extends Content {
	/**
	 * 打开栏目列表
	 */
	public static final int TYPE_1 = 1;
	/**
	 * 打开内容详情
	 */
	public static final int TYPE_2 = 2;
	/**
	 * 打开url
	 */
	public static final int TYPE_3 = 3;
	/**
	 * 打开第3方APK
	 */
	public static final int TYPE_4 = 4;
	/**
	 * 打开功能
	 */
	public static final int TYPE_5 = 5;

	public int type;
	public long insertTime;
	public int playTime;
	public int position;
	public String lastUpdateEpisode;

	public Item() {
		super();
	}

	public Item(String parentID, String contentID, String name, ArrayList<String> imgPaths) {
		super(parentID, contentID, name, imgPaths);
	}

	// ---------------------Parcelable implements------------

	@Override
	public String toString() {
		return "Item [type=" + type + ", insertTime=" + insertTime + ", playTime=" + playTime + ", position=" + position + ", lastUpdateEpisode="
				+ lastUpdateEpisode + "]";
	}

	public static final Creator<Item> CREATOR = new Creator<Item>() {
		@Override
		public Item createFromParcel(Parcel source) {
			ArrayList<String> imgPaths = new ArrayList<String>();
			String parentID = source.readString();
			String contentID = source.readString();
			String name = source.readString();
			long insertTime = source.readLong();
			int playTime = source.readInt();
			int position = source.readInt();
			String lastUpdateEpisode = source.readString();
			int type = source.readInt();
			source.readStringList(imgPaths);

			Item item = new Item(parentID, contentID, name, imgPaths);
			item.insertTime = insertTime;
			item.playTime = playTime;
			item.position = position;
			item.type = type;
			item.lastUpdateEpisode = lastUpdateEpisode;

			return item;
		}

		@Override
		public Item[] newArray(int size) {
			return new Item[size];
		}
	};

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(parentID);
		dest.writeString(contentID);
		dest.writeString(name);
		dest.writeLong(insertTime);
		dest.writeInt(playTime);
		dest.writeInt(position);
		dest.writeString(lastUpdateEpisode);
		dest.writeInt(type);
		dest.writeStringList(imgPaths);
	};
}
