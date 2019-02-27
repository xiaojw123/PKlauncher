package com.pkit.launcher.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.pkit.launcher.common.Configuration;
import com.pkit.launcher.provider.LoggerProvider;
import com.pkit.launcher.receiver.RebootServiceReceiver;
import com.pkit.launcher.receiver.RequestResultReceiver;
import com.pkit.launcher.utils.APPLog;
import com.pkit.launcher.utils.JsonUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jiaxing on 2015/7/8.
 */
public class TimerService extends Service implements RequestResultReceiver.Receiver {

    private Timer mTimer = new Timer();
    private TimerTask mTimerTask;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e("test", "hanlder test");
            reportWatch();
            reportClick();
        }
    };

    private void reportClick() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(LoggerProvider.CLICK_URI, null, null, null, null);
        try {
            String message = JsonUtils.generateClickJson(cursor);
            APPLog.printInfo("report message:" + message);
            Intent intent = new Intent(TimerService.this, LoggerService.class);
            if (!TextUtils.isEmpty(message)) {
                intent.putExtra(LoggerService.RECEIVER, mReceiver);
                intent.putExtra(LoggerService.CLICKED_MESSAGE, message);
                startService(intent);
            }
        } finally {
            cursor.close();
        }
    }

    private void reportWatch() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(LoggerProvider.WATCH_URI, null, null, null, null);
        try {
            String message = JsonUtils.generateWatchJson(cursor);
            Intent intent = new Intent(TimerService.this, LoggerService.class);
            if (!TextUtils.isEmpty(message)) {
                intent.putExtra(LoggerService.RECEIVER, mReceiver);
                intent.putExtra(LoggerService.WATCHED_MESSAGE, message);
                startService(intent);
            }
        } finally {
            cursor.close();
        }
    }

    private RequestResultReceiver mReceiver;

    public TimerService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mReceiver = new RequestResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        };

        mTimer.schedule(mTimerTask, 0, Configuration.timestamp);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        intent.setAction(RebootServiceReceiver.REBOOT_SERVICE_ACTION);
        sendBroadcast(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        APPLog.printInfo("report response:" + resultData.getString("data"));
        String data = resultData.getString("data");
        ContentResolver resolver = getContentResolver();
        if (resultCode == LoggerService.UPLOAD_WATCH_RECORD_SUCCESS) {
            resolver.delete(LoggerProvider.WATCH_URI, null, null);
        } else if (resultCode == LoggerService.UPLOAD_CLICK_RECORD_SUCCESS) {
            resolver.delete(LoggerProvider.CLICK_URI, null, null);
        }
    }
}
