package com.pkit.launcher.utils;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by jiaxing on 2015/7/10.
 */
public class JsonUtils {
    public static String generateWatchJson(Cursor cursor) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            while (cursor.moveToNext()) {
                jsonObject.put("deviceId", cursor.getString(cursor.getColumnIndexOrThrow("deviceId")));
                JSONObject jo1 = new JSONObject();
                jo1.put("contentId", cursor.getString(cursor.getColumnIndexOrThrow("contentId")));
                jo1.put("episodeId", cursor.getString(cursor.getColumnIndexOrThrow("episodeId")));
                jo1.put("starttime", cursor.getString(cursor.getColumnIndexOrThrow("starttime")));
                jo1.put("endtime", cursor.getString(cursor.getColumnIndexOrThrow("endtime")));
                jsonArray.put(jo1);
            }
            if (jsonArray.length() > 0) {
                jsonObject.put("getWatchSerieslist", jsonArray);
            }
            if(jsonObject.isNull("deviceId")){
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String generateClickJson(Cursor cursor) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            while (cursor.moveToNext()) {
                jsonObject.put("deviceId", cursor.getString(cursor.getColumnIndexOrThrow("deviceId")));
                JSONObject jo1 = new JSONObject();
                jo1.put("categoryId", cursor.getString(cursor.getColumnIndexOrThrow("categoryId")));
                jo1.put("contentId", cursor.getString(cursor.getColumnIndexOrThrow("contentId")));
                jo1.put("clickcount", cursor.getInt(cursor.getColumnIndexOrThrow("clickcount")));
                jsonArray.put(jo1);
            }
            if (jsonArray.length() > 0) {
                jsonObject.put("getClicklist", jsonArray);
            }else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
