package com.pkit.launcher.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jiaxing on 2015/7/8.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "logger.db";
    public static final String TABLE_WATCH_LOGGER = "watch_logger";
    public static final String TABLE_CLICK_LOGGER = "click_logger";
    public static final int DATABSE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABSE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onUpgrade(db);
    }

    private void onUpgrade(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WATCH_LOGGER);
        db.execSQL("CREATE TABLE " + TABLE_WATCH_LOGGER + "(" +
                        "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "deviceId" + " TEXT NOT NULL," +
                        "contentId"+" TEXT NOT NULL,"+
                        "episodeId"+" TEXT NOT NULL,"+
                        "starttime"+" TEXT NOT NULL,"+
                        "endtime" + " TEXT)");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLICK_LOGGER);
        db.execSQL("CREATE TABLE " + TABLE_CLICK_LOGGER + "(" +
                "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                "deviceId" + " TEXT NOT NULL," +
                "categoryId"+" TEXT NOT NULL,"+
                "contentId"+" TEXT NOT NULL,"+
                "clickcount"+" TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db);
    }
}
