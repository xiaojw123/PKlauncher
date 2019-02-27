package com.pkit.launcher.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * 
 * @author Richard
 *
 */
public class DBHelper extends SQLiteOpenHelper {
	private static final String TAG = "DBHelper";
	private static final String MENU_DB = "Pkit_Content.db";
	private static final int VERSION = 1;

	public DBHelper(Context context) {
		this(context, MENU_DB, null, VERSION);
	}

	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "DBHelper onCreate");
		String sql = "create table Container("
					+ "parentID CHAR(128) NOT NULL,"
					+ "contentID CHAR(128) NOT NULL,"
					+ "name CHAR(128) NOT NULL,"
					+ "childCount INT NOT NULL,"
					+ "isLocked INT NOT NULL,"
					+ "imgUrl1 CHAR(256),"
					+ "imgUrl2 CHAR(256),"
					+ "imgUrl3 CHAR(256),"
					+ "imgUrl4 CHAR(256),"
					+ "imgUrl5 CHAR(256))";
		db.execSQL(sql);
		
		sql = "create table Item("
					+ "parentID CHAR(128) NOT NULL,"
					+ "contentID CHAR(128) NOT NULL,"
					+ "name CHAR(128) NOT NULL,"
					+ "isFavorite BOOLEAN NOT NULL,"
					+ "isHistory BOOLEAN NOT NULL,"
					+ "isLocked BOOLEAN NOT NULL,"
					+ "insertTime LONG NOT NULL,"
					+ "playTime INT NOT NULL,"
					+ "position INT NOT NULL,"
					+ "imgUrl1 CHAR(256),"
					+ "imgUrl2 CHAR(256),"
					+ "imgUrl3 CHAR(256),"
					+ "imgUrl4 CHAR(256),"
					+ "imgUrl5 CHAR(256))";
		db.execSQL(sql);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "DBHelper onDowngrade oldVersion:" + oldVersion + "/newVersion:" + newVersion);
		String sql = "DROP TABLE Container";
		db.execSQL(sql);
		sql = "DROP TABLE Item";
		db.execSQL(sql);
		onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "DBHelper onUpgrade oldVersion:" + oldVersion + "/newVersion:" + newVersion);
		String sql = "DROP TABLE Container";
		db.execSQL(sql);
		sql = "DROP TABLE Item";
		db.execSQL(sql);
		onCreate(db);
	}

}
