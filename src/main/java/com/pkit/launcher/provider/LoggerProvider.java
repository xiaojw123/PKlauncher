package com.pkit.launcher.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.pkit.launcher.dao.DatabaseHelper;

/**
 * Created by jiaxing on 2015/7/10.
 */
public class LoggerProvider extends ContentProvider {

    private DatabaseHelper mDatabaseHelper;
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static Uri WATCH_URI = Uri.parse("content://" + "com.pkit.logger" + "/" + "watch");
    public static Uri CLICK_URI = Uri.parse("content://" + "com.pkit.logger" + "/" + "click");

    static {
        sUriMatcher.addURI("com.pkit.logger", "watch", 1);
        sUriMatcher.addURI("com.pkit.logger", "click", 2);
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(this.getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int type = sUriMatcher.match(uri);
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (type) {
            case 1:
                cursor = db.query(DatabaseHelper.TABLE_WATCH_LOGGER, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case 2:
                cursor = db.query(DatabaseHelper.TABLE_CLICK_LOGGER, projection, selection, selectionArgs, null, null, sortOrder);
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
        int type = sUriMatcher.match(uri);
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        switch (type) {
            case 1:
                db.insert(DatabaseHelper.TABLE_WATCH_LOGGER, null, values);
                break;
            case 2:
                db.insert(DatabaseHelper.TABLE_CLICK_LOGGER, null, values);
                break;
        }

        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int type = sUriMatcher.match(uri);
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int returnCode = 0;
        switch (type) {
            case 1:
                returnCode = db.delete(DatabaseHelper.TABLE_WATCH_LOGGER, selection, selectionArgs);
                break;
            case 2:
                returnCode = db.delete(DatabaseHelper.TABLE_CLICK_LOGGER, selection, selectionArgs);
                break;
        }
        return returnCode;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int type = sUriMatcher.match(uri);
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int returnCode = 0;
        switch (type) {
            case 1:
                returnCode = db.update(DatabaseHelper.TABLE_WATCH_LOGGER, values, selection, selectionArgs);
                break;
            case 2:
                returnCode = db.update(DatabaseHelper.TABLE_CLICK_LOGGER, values, selection, selectionArgs);
                break;
        }
        return returnCode;
    }
}
