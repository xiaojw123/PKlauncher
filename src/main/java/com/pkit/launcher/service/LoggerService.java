package com.pkit.launcher.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.pkit.launcher.common.Configuration;
import com.pkit.launcher.net.HttpHelper;

/**
 * Created by jiaxing on 2015/7/10.
 */
public class LoggerService extends IntentService {
    public static final String RECEIVER = "receiver";
    public static final String WATCHED_MESSAGE = "watch_message";
    public static final String CLICKED_MESSAGE = "click_message";
    private static final String DATA = "data";
    public static final int UPLOAD_WATCH_RECORD_SUCCESS = 1;
    public static final int UPLOAD_CLICK_RECORD_SUCCESS = 2;

    public LoggerService() {
        super("LoggerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
        final String message = intent.getStringExtra(WATCHED_MESSAGE);
        HttpHelper helper = new HttpHelper();
        try {
            if (!TextUtils.isEmpty(message)) {
                String data = helper.getPostContent(Configuration.getWatchSeriesUri(), message);
                if (!TextUtils.isEmpty(data)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(DATA, data);
                    receiver.send(UPLOAD_WATCH_RECORD_SUCCESS, bundle);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String message1 = intent.getStringExtra(CLICKED_MESSAGE);

        try {
            if (!TextUtils.isEmpty(message1)) {
                String data = helper.getPostContent(Configuration.getClickUri(), message1);
                if (!TextUtils.isEmpty(data)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(DATA, data);
                    receiver.send(UPLOAD_CLICK_RECORD_SUCCESS, bundle);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
