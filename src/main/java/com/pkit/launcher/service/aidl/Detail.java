package com.pkit.launcher.service.aidl;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * 影片基本信息封装
 * 
 * @author Richard
 *
 */
public class Detail extends Content {
	/**
	 * 电影,默认Detail为电影信息
	 */
	public static final int FILM_TYPE = 0x01;
	/**
	 * 电视剧
	 */
	public static final int TELE_TV_TYPE = 0x02;
	/**
	 * 综艺
	 */
	public static final int VARIETY_TYPE = 0x03;
	/**
	 * 内容分类,如:电影,电视剧,综艺等
	 */
	public int type;

	/**
	 * 影片类别,如:战争,爱情,其它等
	 */
	public String category;
	/**
	 * 导演
	 */
	public String director;
	/**
	 * 区域
	 */
	public String zone;
	/**
	 * 描述
	 */
	public String description;
	/**
	 * 演员列表
	 */
	public List<String> actors;
	/**
	 * 片源
	 */
	public List<Source> sources;

	/**
	 * 上次播放结束时间
	 */
	public int seekPosition = 0;

	/**
	 * 上次播放到第几集
	 */
	public int position = 0;

	/**
	 * 是否收藏,0:表示没收藏,1:表示已收藏
	 */
	public int isFavorite = 0;
	public int lastUpdateEpisode;

	public Detail() {
		super();
		actors = new ArrayList<String>();
		sources = new ArrayList<Source>();
	}

	public Detail(String parentID, String contentID, String name, ArrayList<String> imgPaths, String director, String zone, String description,
			List<String> actors, List<Source> sources) {
		this.type = FILM_TYPE;
		this.category = "";
		this.parentID = (parentID == null ? "" : parentID);
		this.contentID = (contentID == null ? "" : contentID);
		this.name = (name == null ? "" : name);
		this.imgPaths = (imgPaths == null ? new ArrayList<String>() : imgPaths);
		this.director = (director == null ? "" : director);
		this.zone = (zone == null ? "" : zone);
		this.description = (description == null ? "" : description);
		this.actors = (actors == null ? new ArrayList<String>() : actors);
		this.sources = (sources == null ? new ArrayList<Source>() : sources);
	}

	@Override
	public String toString() {
		return "Detail [type=" + type + ", category=" + category + ", director=" + director + ", zone=" + zone + ", description=" + description + ", actors="
				+ actors + ", sources=" + sources + ", seekPosition=" + seekPosition + ", position=" + position + ", isFavorite=" + isFavorite + "]";
	}

	// ---------------------Parcelable implements------------
	public static final Creator<Detail> CREATOR = new Creator<Detail>() {

		@Override
		public Detail createFromParcel(Parcel source) {
			ArrayList<String> imgPaths = new ArrayList<String>();
			ArrayList<String> actors = new ArrayList<String>();
			ArrayList<Source> sources = new ArrayList<Source>();
			String parentID = source.readString();
			String contentID = source.readString();
			String name = source.readString();
			source.readStringList(imgPaths);
			String director = source.readString();
			String zone = source.readString();
			int lastUpdateEpisode = source.readInt();
			String description = source.readString();
			source.readStringList(actors);
			source.readTypedList(sources, Source.CREATOR);
			int seekPosition = source.readInt();
			int position = source.readInt();
			int isFavorite = source.readInt();

			Detail detail = new Detail(parentID, contentID, name, imgPaths, director, zone, description, actors, sources);
			detail.seekPosition = seekPosition;
			detail.position = position;
			detail.isFavorite = isFavorite;
			detail.lastUpdateEpisode = lastUpdateEpisode;
			return detail;
		}

		@Override
		public Detail[] newArray(int size) {
			return new Detail[size];
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
		dest.writeString(director);
		dest.writeString(zone);
		dest.writeInt(lastUpdateEpisode);
		dest.writeString(description);
		dest.writeStringList(actors);
		dest.writeTypedList(sources);
		dest.writeInt(seekPosition);
		dest.writeInt(position);
		dest.writeInt(isFavorite);
	}
}
