package com.pkit.launcher.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.pkit.launcher.dao.DataHelper;

public class DataProvider extends ContentProvider {
	private static final UriMatcher uriMather = new UriMatcher(UriMatcher.NO_MATCH);
	private static final String author = "com.pkit.launcher.data";
	public static Uri  MODEL_SEAT_URI = Uri.parse("content://com.pkit.launcher.data/model_seat");
	public static Uri  DETAIL_URI = Uri.parse("content://com.pkit.launcher.data/detail");
	public static Uri  SOURCE_URI = Uri.parse("content://com.pkit.launcher.data/source");
	private static final int MODEL_SEAT_URI_CODE = 1;
	private static final int DETAIL_URI_CODE = 2;
	private static final int SOURCE_URI_CODE = 3;
	public static interface ModeSeat{
		//colume index
		public static final int COLUME_ID=0;
		public static final int COLUME_SEQUENCE=1;
		public static final int COLUME_NAME=2;
		public static final int COLUME_THUMBNAIL=3;
		public static final int COLUME_POSTER=4;
		public static final int COLUME_STILLS=5;
		public static final int COLUME_ACTION=6;
		public static final int COLUME_ACTION_VALUE=7;
		public static final int COLUME_PACKAGE_NAME=8;
		public static final int COLUME_CLASS_NAME_PACKAG=9;
		public static final int COLUME_PRAM=10;
		public static final int COLUME_LEFT=11;
		public static final int COLUME_TOP=12;
		public static final int COLUME_WIDTH=13;
		public static final int COLUME_HEIGHT=14;
		//colume name
		public static final String ID="id";
		public static final String  SEQUENCE ="sequence";
		public static final String NAME="name";
		public static final String THUMBNAIL="thumbnail";
		public static final String POSTER="poster";
		public static final String STILLS="stills";
		public static final String ACTION="action";
		public static final String ACTION_VALUE="actionValue";
		public static final String PACKAGE_NAME="packageName";
		public static final String CLASS_NAME_PACKAGE="classNamePackage";
		public static final String PRAM="pram";
		public static final String LEFT="left";
		public static final String TOP="top";
		public static final String WIDTH="width";
		public static final String HEIGHT="height";
		
	}


	public static interface Detail {
		// colume index
		public static final int COLUME_CONTENT_ID = 0;
		public static final int COLUME_PARENT_ID = 1;
		public static final int COLUME_NAME = 2;
		public static final int COLUME_DIRECTOR1 = 3;
		public static final int COLUME_DIRECTOR2 = 4;
		public static final int COLUME_DIRECTOR3 = 5;
		public static final int COLUME_DIRECTOR4 = 6;
		public static final int COLUME_ACTOR1 = 7;
		public static final int COLUME_ACTOR2 = 8;
		public static final int COLUME_ACTOR3 = 9;
		public static final int COLUME_ACTOR4 = 10;
		public static final int COLUME_ZONE = 11;
		public static final int COLUME_DESCRIPTION = 12;
		public static final int COLUME_POSITION = 13;
		public static final int COLUME_SEEK_POSITION = 14;
		public static final int COLUME_IS_FAVORITE = 15;
		public static final int COLUME_LAST_UPDATE_EPISODE = 16;
		public static final int COLUME_IMGR_URL1 = 17;
		public static final int COLUME_IMGR_URL2 = 18;
		public static final int COLUME_IMGR_URL3 = 19;
		public static final int COLUME_IMGR_URL4 = 20;
		// colume name
		public static final String CONTENT_ID = "contentID";
		public static final String PARENT_ID = "parentID";
		public static final String NAME = "name";
		public static final String DIRECTOR1 = "director1";
		public static final String DIRECTOR2 = "director2";
		public static final String DIRECTOR3 = "director3";
		public static final String DIRECTOR4 = "director4";
		public static final String ACTOR1 = "actor1";
		public static final String ACTOR2 = "actor2";
		public static final String ACTOR3 = "actor3";
		public static final String ACTOR4 = "actor4";
		public static final String ZONE = "zone";
		public static final String DESCRIPTION = "description";
		public static final String POSITION = "position";
		public static final String SEEK_POSITION = "seekPosition";
		public static final String IS_FAVORITE = "isFavorite";
		public static final String LAST_UPDATE_EPISODE = "lastUpdateEpisode";
		public static final String IMGR_URL1 = "imgUrl1";
		public static final String IMGR_URL2 = "imgUrl2";
		public static final String IMGR_URL3 = "imgUrl3";
		public static final String IMGR_URL4 = "imgUrl4";

	}

	public static interface Source {
		// colume index
		public static final int COLUME_ID = 0;
		public static final int COLUME_NAME = 1;
		public static final int COLUME_URL = 2;
		// colume name
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String URL = "url";
	}

	private DataHelper dbHelper;
	private SQLiteDatabase db;
	static {
		uriMather.addURI(author, DataHelper.TABLE_MODEL_SEART, MODEL_SEAT_URI_CODE);
		uriMather.addURI(author, DataHelper.TABLE_DETAIL, DETAIL_URI_CODE);
		uriMather.addURI(author, DataHelper.TABLE_SOURCE, SOURCE_URI_CODE);
	}

	@Override
	public boolean onCreate() {
		if (dbHelper == null) {
			dbHelper = new DataHelper(getContext());
		}
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		checkDb();
		int code = uriMather.match(uri);
		Cursor cursor = null;
		switch (code) {
		case MODEL_SEAT_URI_CODE:
			cursor = db.query(DataHelper.TABLE_MODEL_SEART, projection, selection, selectionArgs, null, null, null);
			break;
		case DETAIL_URI_CODE:
			cursor = db.query(DataHelper.TABLE_DETAIL, projection, selection, selectionArgs, null, null, null);
			break;
		case SOURCE_URI_CODE:
			cursor = db.query(DataHelper.TABLE_SOURCE, projection, selection, selectionArgs, null, null, null);
			break;
		}
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		checkDb();
		int code = uriMather.match(uri);
		Uri newUri = null;
		long id = 0;
		switch (code) {
		case MODEL_SEAT_URI_CODE:
			id = db.insert(DataHelper.TABLE_MODEL_SEART, null, values);
			newUri = ContentUris.withAppendedId(uri, id);
			break;
		case DETAIL_URI_CODE:
			id = db.insert(DataHelper.TABLE_DETAIL, null, values);
			newUri = ContentUris.withAppendedId(uri, id);
			break;
		case SOURCE_URI_CODE:
			id = db.insert(DataHelper.TABLE_SOURCE, null, values);
			newUri = ContentUris.withAppendedId(uri, id);
			break;
		default:
			break;
		}
		return newUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		checkDb();
		int code = uriMather.match(uri);
		int num = 0;
		switch (code) {
		case MODEL_SEAT_URI_CODE:
			num = db.delete(DataHelper.TABLE_MODEL_SEART, selection, selectionArgs);
			break;
		case DETAIL_URI_CODE:
			num = db.delete(DataHelper.TABLE_DETAIL, selection, selectionArgs);
			break;
		case SOURCE_URI_CODE:
			num = db.delete(DataHelper.TABLE_SOURCE, selection, selectionArgs);
			break;
		}
		return num;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		checkDb();
		int code = uriMather.match(uri);
		int num = 0;
		switch (code) {
		case MODEL_SEAT_URI_CODE:
			num = db.update(DataHelper.TABLE_MODEL_SEART, values, selection, selectionArgs);
			break;
		case DETAIL_URI_CODE:
			num = db.update(DataHelper.TABLE_DETAIL, values, selection, selectionArgs);
			break;
		case SOURCE_URI_CODE:
			num = db.update(DataHelper.TABLE_SOURCE, values, selection, selectionArgs);
			break;
		}
		return num;
	}

	public void checkDb() {
		if (db == null || !db.isOpen()) {
			db = dbHelper.getWritableDatabase();
		}
	}

}
