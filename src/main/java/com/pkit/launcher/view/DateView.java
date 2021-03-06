/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pkit.launcher.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateView extends TextView {

    private final Date mCurrentTime = new Date();

    private SimpleDateFormat mDateFormat;
    private String mLastText;
    private String mFormat;

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_TIME_TICK.equals(action)
                    || Intent.ACTION_TIME_CHANGED.equals(action)
                    || Intent.ACTION_TIMEZONE_CHANGED.equals(action)
                    || Intent.ACTION_LOCALE_CHANGED.equals(action)) {
                if (Intent.ACTION_LOCALE_CHANGED.equals(action)
                        || Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {
                    // need to get a fresh date format
                    mDateFormat = null;
                }
                updateClock();
            }
        }
    };

    public DateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        getContext().registerReceiver(mIntentReceiver, filter, null, null);

        updateClock();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mDateFormat = null;
        getContext().unregisterReceiver(mIntentReceiver);
    }

    public void setFormat(String format){
        mFormat = format;
        updateClock();
    }

    public String getFormat() {
        return mFormat;
    }

    protected void updateClock() {
        if (mDateFormat == null) {
            final Locale l = Locale.getDefault();
            final String fmt;

            /*if(android.os.Build.VERSION.SDK_INT >= 18) {
                fmt = DateFormat.getBestDateTimePattern(l, "E");
            }else{
                fmt = "EEE";
            }*/
            if(TextUtils.isEmpty(getFormat())){
                fmt = "EEE";
            }else{
                fmt = getFormat();
            }
            mDateFormat = new SimpleDateFormat(fmt, l);
        }

        mCurrentTime.setTime(System.currentTimeMillis());

        final String text = mDateFormat.format(mCurrentTime);
        if (!text.equals(mLastText)) {
            setText(text);
            mLastText = text;
        }
    }
}
