package com.pkit.launcher.service;

import com.pkit.launcher.common.Configuration;
import com.pkit.launcher.provider.LoggerProvider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

/**
 * Created by jiaxing on 2015/7/10.
 */
public class LoggerRecordService {
    /**
     * 记录点播日志到数据库
     *
     * @param context
     * @param contentId 节目集ID
     * @param episodeId 节目ID
     * @param starttime 开始时间
     * @param endtime   结束时间
     */
    public static void setWatchRecord(Context context, String contentId, String episodeId, String starttime, String endtime) {
        ContentValues values = new ContentValues();
        values.put("deviceId", Configuration.deviceId);
        values.put("contentId", contentId);
        values.put("episodeId", episodeId);
        values.put("starttime", starttime);
        values.put("endtime", endtime);
        ContentResolver resolver = context.getContentResolver();
        resolver.insert(LoggerProvider.WATCH_URI, values);
    }

    /**
     * 更新点播日志（主要是结束时间）
     *
     * @param context
     * @param contentId 节目集ID
     * @param episodeId 节目ID
     * @param starttime 开始时间
     * @param endtime   结束时间
     */
    public static void updateWatchRecord(Context context, String contentId, String episodeId, String starttime, String endtime) {
        ContentValues values = new ContentValues();
        if (!TextUtils.isEmpty(episodeId)) {
            values.put("episodeId", episodeId);
        }
        if (!TextUtils.isEmpty(starttime)) {
            values.put("starttime", starttime);
        }
        if (!TextUtils.isEmpty(endtime)) {
            values.put("endtime", endtime);
        }
        ContentResolver resolver = context.getContentResolver();
        resolver.update(LoggerProvider.WATCH_URI, values, "contentId=?", new String[]{contentId});
    }

    /**
     * 栏目，影片详情点击次数日志的记录
     *
     * @param context
     * @param categoryId 栏目ID
     * @param contentId  节目集ID
     */
    public static void setClickRecord(Context context, String categoryId, String contentId) {
        ContentValues values = new ContentValues();
        values.put("deviceId", Configuration.deviceId);
        values.put("categoryId", categoryId);
        values.put("contentId", contentId);
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(LoggerProvider.CLICK_URI, null, "categoryId=? and contentId=?", new String[]{categoryId, contentId}, null);
        if (cursor.moveToFirst()) {
            int clickcount = cursor.getInt(cursor.getColumnIndexOrThrow("clickcount"));
            values.put("clickcount", clickcount + 1);
            resolver.update(LoggerProvider.CLICK_URI, values, "categoryId=? and contentId=?", new String[]{categoryId, contentId});
        } else {
            values.put("clickcount", 1);
            resolver.insert(LoggerProvider.CLICK_URI, values);
        }
        cursor.close();
    }
}


