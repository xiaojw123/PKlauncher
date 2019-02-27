package com.pkit.launcher.service.aidl;

import android.os.Parcel;

/**
 * Created by jiaxing on 2015/6/17.
 */
public class ModelSeat extends Content {
	private String id;
	private int sequence;
	private String name;
	private String thumbnail;
	private String poster;
	private String stills;
	private int action;
	private String actionValue;
	private String packageName;
	private String classNamePackage;
	private String pram;
	private int left;
	private int top;
	private int width;
	private int height;

	public ModelSeat() {
		super();
	}

	public ModelSeat(Parcel source) {
		id = source.readString();
		sequence = source.readInt();
		name = source.readString();
		thumbnail = source.readString();
		poster = source.readString();
		stills = source.readString();
		action = source.readInt();
		actionValue = source.readString();
		packageName = source.readString();
		classNamePackage = source.readString();
		pram = source.readString();
		left = source.readInt();
		top = source.readInt();
		width = source.readInt();
		height = source.readInt();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getStills() {
		return stills;
	}

	public void setStills(String stills) {
		this.stills = stills;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public String getActionValue() {
		return actionValue;
	}

	public void setActionValue(String actionValue) {
		this.actionValue = actionValue;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassNamePackage() {
		return classNamePackage;
	}

	public void setClassNamePackage(String classNamePackage) {
		this.classNamePackage = classNamePackage;
	}

	public String getPram() {
		return pram;
	}

	public void setPram(String pram) {
		this.pram = pram;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeInt(sequence);
		dest.writeString(name);
		dest.writeString(thumbnail);
		dest.writeString(poster);
		dest.writeString(stills);
		dest.writeInt(action);
		dest.writeString(actionValue);
		dest.writeString(packageName);
		dest.writeString(classNamePackage);
		dest.writeString(pram);
		dest.writeInt(left);
		dest.writeInt(top);
		dest.writeInt(width);
		dest.writeInt(height);
	}

	public static final Creator<ModelSeat> CREATOR = new Creator<ModelSeat>() {
		@Override
		public ModelSeat createFromParcel(Parcel source) {
			return new ModelSeat(source);
		}

		@Override
		public ModelSeat[] newArray(int size) {
			return new ModelSeat[size];
		}
	};
}
