package com.pkit.launcher.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.DateUtils;

import com.pkit.launcher.R;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by jiaxing on 2015/4/3.
 */
public class Utils {
    public static String getRelativeTimeDisplayString(Context context, long watchTime) {
        long now = System.currentTimeMillis();
        long difference = now - watchTime;
        CharSequence cs;
        if (difference >= 0 && difference <= DateUtils.DAY_IN_MILLIS) {
            cs = context.getString(R.string.today);
        } else if ((difference > DateUtils.DAY_IN_MILLIS && difference <= DateUtils.DAY_IN_MILLIS * 2)) {
            cs = context.getString(R.string.yesterday);
        } else if ((difference > DateUtils.DAY_IN_MILLIS * 2 && difference <= DateUtils.DAY_IN_MILLIS * 3)) {
            cs = context.getString(R.string.day_before_yesterday);
        } else {
            cs = DateUtils.getRelativeTimeSpanString(
                    watchTime,
                    now,
                    DateUtils.DAY_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE);
        }
        return cs.toString();
    }

    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    public static ArrayList<String> getLocalMac() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) nis
                        .nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (null != mac) {
                    StringBuffer sb = new StringBuffer();
                    if (mac != null && mac.length > 1) {
                        sb.append(parseByte(mac[0])).append(":")
                                .append(parseByte(mac[1])).append(":")
                                .append(parseByte(mac[2])).append(":")
                                .append(parseByte(mac[3])).append(":")
                                .append(parseByte(mac[4])).append(":")
                                .append(parseByte(mac[5]));
                        list.add(sb.toString());
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String parseByte(byte b) {
        int intValue = 0;
        String s = "";
        if (b >= 0) {
            intValue = b;
        } else {
            intValue = 256 + b;
        }
        s = Integer.toHexString(intValue);
        s = "00"+s;
        return s.substring(s.length() - 2);
    }
}
