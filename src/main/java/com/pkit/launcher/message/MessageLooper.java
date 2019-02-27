package com.pkit.launcher.message;

import java.util.HashMap;

import com.pkit.launcher.utils.APPLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

public class MessageLooper extends BroadcastReceiver {
	private static HashMap<String, Handler> msgListeners = new HashMap<String, Handler>();
	private static MessageLooper looper = new MessageLooper();
	private static IntentFilter filter = new IntentFilter();

	private MessageLooper() {
	}

	public static void registListener(Context context, String action, Handler handler) {
		msgListeners.put(action, handler);
		filter.addAction(action);
		context.registerReceiver(looper, filter);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		APPLog.printInfo("onReceive");
	}
}
