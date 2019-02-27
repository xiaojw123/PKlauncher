package com.pkit.launcher.service.aidl;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * 栏目数据封装
 * 
 * @author Richard
 *
 */
public class Container extends Content {
	/**
	 * 该栏目下有多少条数据
	 */
	public int childCount;
	/**
	 * 该栏目下热剧
	 */
	public List<Item> topHots;
	/**
	 * 该栏目下更新的影片
	 */
	public List<Item> updates;

	public Container() {
		super();
	}

	public Container(ModelSeat modelSeat) {
		this.contentID = modelSeat.getActionValue();
		this.name = modelSeat.getName();
		this.imgPaths = new ArrayList<String>();
		this.imgPaths.add(modelSeat.getPoster());
	}

	public Container(String parentID, String contentID, String name, ArrayList<String> imgPaths) {
		super(parentID, contentID, name, imgPaths);
	}

	public Container(String parentID, String contentID, String name, ArrayList<String> imgPaths, ArrayList<Item> topHots, ArrayList<Item> updates) {
		this.parentID = parentID;
		this.contentID = contentID;
		this.name = name;
		this.imgPaths = imgPaths;
		this.topHots = topHots;
		this.updates = updates;
	}

	// ---------------------Parcelable implements------------
	public static final Creator<Container> CREATOR = new Creator<Container>() {
		@Override
		public Container createFromParcel(Parcel source) {
			ArrayList<String> imgPaths = new ArrayList<String>();
			ArrayList<Item> topHots = new ArrayList<Item>();
			ArrayList<Item> updates = new ArrayList<Item>();

			String parentID = source.readString();
			String contentID = source.readString();
			String name = source.readString();

			source.readStringList(imgPaths);
			source.readTypedList(topHots, Item.CREATOR);
			source.readTypedList(updates, Item.CREATOR);

			Container container = new Container(parentID, contentID, name, imgPaths, topHots, updates);
			return container;
		}

		@Override
		public Container[] newArray(int size) {
			return new Container[size];
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
		dest.writeTypedList(topHots);
		dest.writeTypedList(updates);
	}
}
