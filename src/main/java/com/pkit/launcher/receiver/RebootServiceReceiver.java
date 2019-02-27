package com.pkit.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pkit.launcher.service.TimerService;

/**
 * Created by jiaxing on 2015/7/8.
 */
public class RebootServiceReceiver extends BroadcastReceiver {
    public static final String REBOOT_SERVICE_ACTION="com.pkit.launcher.action.REBOOT_SERVICE";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (REBOOT_SERVICE_ACTION.equals(action)) {
            Intent intent1 = new Intent(context, TimerService.class);
            context.startService(intent1);
        }
    }
}
