package com.pkit.launcher.receiver;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by jiaxing on 2015/5/14.
 */
public class RequestResultReceiver extends ResultReceiver {

    private Receiver mReceiver;

    public RequestResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        mReceiver.onReceiveResult(resultCode, resultData);
    }
}
