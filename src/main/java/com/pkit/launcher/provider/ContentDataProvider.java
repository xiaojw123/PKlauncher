package com.pkit.launcher.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.pkit.launcher.dao.DBHelper;
import com.pkit.launcher.utils.APPLog;

public class ContentDataProvider extends ContentProvider {
	public static interface Container {
		// column index
		public static final int PARENT_ID_COLUMN = 0;
		public static final int CONTENT_ID_COLUMN = 1;
		public static final int NAME_COLUMN = 2;
		public static final int CHILD_COUNT_COLUMN = 3;
		public static final int IS_LOCKED_COLUMN = 4;
		public static final int IMG_URL_1_COLUMN = 5;
		public static final int IMG_URL_2_COLUMN = 6;
		public static final int IMG_URL_3_COLUMN = 7;
		public static final int IMG_URL_4_COLUMN = 8;
		public static final int IMG_URL_5_COLUMN = 9;

		// column name
		public static final String PARENT_ID = "parentID";
		public static final String CONTENT_ID = "contentID";
		public static final String NAME = "name";
		public static final String CHILD_COUNT = "childCount";
		public static final String IS_LOCKED = "isLocked";
		public static final String IMG_URL_1 = "imgUrl1";
		public static final String IMG_URL_2 = "imgUrl2";
		public static final String IMG_URL_3 = "imgUrl3";
		public static final String IMG_URL_4 = "imgUrl4";
		public static final String IMG_URL_5 = "imgUrl5";
	}

	public static interface Item {
		// column index
		public static final int PARENT_ID_COLUMN = 0;
		public static final int CONTENT_ID_COLUMN = 1;
		public static final int NAME_COLUMN = 2;
		public static final int IS_FAVORITE_COLUMN = 3;
		public static final int IS_HISTORY_COLUMN = 4;
		public static final int IS_LOCKED_COLUMN = 5;
		public static final int INSERT_TIME_COLUMN = 6;
		public static final int PLAY_TIME_COLUMN = 7;
		public static final int POSITION_COLUMN = 8;
		public static final int IMG_URL_1_COLUMN = 9;
		public static final int IMG_URL_2_COLUMN = 10;
		public static final int IMG_URL_3_COLUMN = 11;
		public static final int IMG_URL_4_COLUMN = 12;
		public static final int IMG_URL_5_COLUMN = 13;

		// column name
		public static final String PARENT_ID = "parentID";
		public static final String CONTENT_ID = "contentID";
		public static final String NAME = "name";
		public static final String IS_FAVORITE = "isFavorite";
		public static final String IS_HISTORY = "isHistory";
		public static final String IS_LOCKED = "isLocked";
		public static final String INSERT_TIME = "insertTime";
		public static final String PLAY_TIME = "playTime";
		public static final String POSITION = "position";
		public static final String IMG_URL_1 = "imgUrl1";
		public static final String IMG_URL_2 = "imgUrl2";
		public static final String IMG_URL_3 = "imgUrl3";
		public static final String IMG_URL_4 = "imgUrl4";
		public static final String IMG_URL_5 = "imgUrl5";
	}

	public static String AUTHORITIES = "com.pkit.launcher.content";
	public static Uri CONTAINER_URI = Uri.parse("content://com.pkit.launcher.content/container");
	public static Uri ITEM_URI = Uri.parse("content://com.pkit.launcher.content/item");
	private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	private static int CONTAINER = 1;
	private static int ITEM = 2;
	private DBHelper dbHelper;
	private SQLiteDatabase sqlDB;

	static {
		matcher.addURI(AUTHORITIES, "container", CONTAINER);
		matcher.addURI(AUTHORITIES, "item", ITEM);
	}

	@Override
	public boolean onCreate() {
		Context context = getContext();
		dbHelper = new DBHelper(context);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		int code = matcher.match(uri);
		APPLog.printInfo("query matcher code:" + code);
		checkDb();
		Cursor cursor = null;
		if (code == CONTAINER) {
			cursor = sqlDB.query("Container", projection, selection, selectionArgs, null, null, sortOrder);
		} else if (code == ITEM) {
			cursor = sqlDB.query("Item", projection, selection, selectionArgs, null, null, sortOrder);
		}
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int code = matcher.match(uri);
		APPLog.printInfo("query matcher code:" + code);
		checkDb();
		if (code == CONTAINER) {
			sqlDB.insert("Container", "0", values);
		} else if (code == ITEM) {
			sqlDB.insert("Item", "0", values);
		}
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int code = matcher.match(uri);
		APPLog.printInfo("query matcher code:" + code);
		checkDb();
		int num = 0;
		if (code == CONTAINER) {
			num = sqlDB.delete("Container", selection, selectionArgs);
		} else if (code == ITEM) {
			num = sqlDB.delete("Item", selection, selectionArgs);
		}
		return num;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int code = matcher.match(uri);
		APPLog.printInfo("query matcher code:" + code);
		checkDb();
		int num = 0;
		if (code == CONTAINER) {
			num = sqlDB.update("Container", values, selection, selectionArgs);
		} else if (code == ITEM) {
			num = sqlDB.update("Item", values, selection, selectionArgs);
		}
		return num;
	}

	private void checkDb() {
		if (sqlDB == null || !sqlDB.isOpen()) {
			sqlDB = dbHelper.getWritableDatabase();
		}
	}
}
