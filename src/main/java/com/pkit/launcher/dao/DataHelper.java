package com.pkit.launcher.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * created by xiaojw  2015/07/17
 * 
 */
public class DataHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "pkit_data.db";
	private static final int DB_VERSION = 1;
	public static final String TABLE_SOURCE = "source";
	public static final String TABLE_DETAIL = "detail";
	public static final String TABLE_MODEL_SEART = "model_seat";

	public DataHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	public DataHelper(Context context, CursorFactory factory, int version) {
		super(context, DB_NAME, factory, version);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// ModelSeat
		db.execSQL("create table " + TABLE_MODEL_SEART + "( id TEXT NOT NULL PRIMARY KEY," + "sequence INTEGER NOT NULL," + "name TEXT NOT NULL,"
				+ "thumbnail TEXT NOT NULL," + "poster TEXT NOT NULL," + "stills TEXT NOT NULL," + "action INTEGER NOT NULL," + "actionValue TEXT NOT NULL,"
				+ "packageName TEXT NOT NULL," + "classNamePackage TEXT NOT NULL," + "pram TEXT NOT NULL," + "left INTEGER NOT NULL," + "top INTEGER NOT NULL,"
				+ "width INTEGER NOT NULL," + "height INTEGER NOT NULL)");
		// Detail
		db.execSQL("create  table " + TABLE_DETAIL + "( contentID TEXT NOT NULL PRIMARY KEY," + "parentID TEXT NOT NULL," + "name TEXT NOT NULL,"
				+ "director1 TEXT NOT NULL,director2 TEXT NOT NULL,director3 TEXT NOT NULL,director4 TEXT NOT NULL,"
				+ "actor1 TEXT NOT NULL,actor2 TEXT NOT NULL,actor3 TEXT NOT NULL,actor4 TEXT NOT NULL," + "zone TEXT NOT NULL," + "description TEXT NOT NULL,"
				+ "position INTEGER NOT NULL,seekPosition INTEGER NOT NULL," + "isFavorite INTEGER NOT NULL," + "lastUpdateEpisode INTEGER NOT NULL,"
				+ "imgUrl1 TEXT NOT NULL,imgUrl2 TEXT NOT NULL,imgUrl3 TEXT NOT NULL,imgUrl4 TEXT NOT NULL,imgUrl5 TEXT NOT NULL)");
		// Source
		db.execSQL("create table " + TABLE_SOURCE + "( id TEXT NOT NULL PRIMARY KEY,name TEXT NOT NULL,url TEXT NOT NULL)");
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_MODEL_SEART);
		db.execSQL("drop table if  exists " + TABLE_DETAIL);
		db.execSQL("drop table if exists " + TABLE_SOURCE);
		onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_MODEL_SEART);
		db.execSQL("drop table if  exists " + TABLE_DETAIL);
		db.execSQL("drop table if exists " + TABLE_SOURCE);
		onCreate(db);
	}

}
