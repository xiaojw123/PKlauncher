package com.pkit.launcher.activity;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.pkit.launcher.R;

/**
 * Created by lijiaxing on 2015/4/12.
 */
public class BaseActivity extends Activity {
    private WallpaperManager mWallpaperManager;
    private WallpaperReceiver mWallpaperReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWallpaperReceiver = new WallpaperReceiver();
        mWallpaperManager = WallpaperManager.getInstance(this);
        registerBroadcast();
        updateWallpaper();
    }

    public void updateWallpaper() {
        SharedPreferences sharedPreferences = getSharedPreferences("config", 0);
        int wallpaperResource = sharedPreferences.getInt("wallpaper", 0);
        if (wallpaperResource == 0) {
            wallpaperResource = R.drawable.wallpaper_01;
        }
        this.getWindow().setBackgroundDrawable(this.getResources().getDrawable(wallpaperResource));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();

    }

    public void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.WALLPAPER_CHANGED");
        this.registerReceiver(mWallpaperReceiver, filter);
    }

    public void unRegisterBroadcast() {
        this.unregisterReceiver(mWallpaperReceiver);
    }

    private class WallpaperReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == "android.intent.action.WALLPAPER_CHANGED") {
                updateWallpaper();
            }
        }
    }
}
