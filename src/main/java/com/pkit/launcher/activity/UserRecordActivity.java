package com.pkit.launcher.activity;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkit.launcher.R;

/**
 * Created by jiaxing on 2015/3/20.
 */
public class UserRecordActivity extends FragmentActivity {

    public static final String TAG = "UserRecordActivity";
    private WallpaperManager mWallpaperManager;
    private WallpaperReceiver mWallpaperReceiver;
    public static final String USER_RECORD_TYPE = "record_type";

    public static final String USER_RECORD_RECENTLY_WATCHED = "recently_watched";
    public static final String USER_RECORD_COLLECTION = "collection";
    public static final String USER_RECORD_BUY_RECORD = "buy_record";

    public static final int RECENTLY_WATCHED_LAYOUT = R.layout.recently_watched_layout;
    public static final int COLLECTION_LAYOUT = R.layout.collection_layout;
    public static final int BUY_RECORD_LAYOUT = R.layout.buy_record_layout;


    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private String mUserRecordType;

    private ImageView mUserRecordIcon;
    private TextView mUserRecordTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_record_layout);

        mWallpaperReceiver = new WallpaperReceiver();
        mWallpaperManager = WallpaperManager.getInstance(this);
        registerBroadcast();
        updateWallpaper();

        mUserRecordIcon = (ImageView) findViewById(R.id.user_record_icon);
        mUserRecordTitle = (TextView) findViewById(R.id.user_record_title);

        Intent intent = getIntent();
        mUserRecordType = intent.getStringExtra(USER_RECORD_TYPE);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        setUserRecordTitle(mUserRecordType);
        setupFragment(getRecordLayout());
    }

    private void setUserRecordTitle(String mUserRecordType) {
        if (TextUtils.isEmpty(mUserRecordType)) {
            mUserRecordIcon.setImageResource(R.drawable.recently_watched_icon);
            mUserRecordTitle.setText(R.string.recently_watched_title);
        }

        if (USER_RECORD_RECENTLY_WATCHED.equals(mUserRecordType)) {
            mUserRecordIcon.setImageResource(R.drawable.recently_watched_icon);
            mUserRecordTitle.setText(R.string.recently_watched_title);
        } else if (USER_RECORD_COLLECTION.equals(mUserRecordType)) {
            mUserRecordIcon.setImageResource(R.drawable.collection_icon);
            mUserRecordTitle.setText(R.string.collection_title);
        } else if (USER_RECORD_BUY_RECORD.equals(mUserRecordType)) {
        }
    }

    private int getRecordLayout() {
        int layoutId = 0;
        if (TextUtils.isEmpty(mUserRecordType)) {
            return RECENTLY_WATCHED_LAYOUT;
        }

        if (USER_RECORD_RECENTLY_WATCHED.equals(mUserRecordType)) {
            layoutId = RECENTLY_WATCHED_LAYOUT;
        } else if (USER_RECORD_COLLECTION.equals(mUserRecordType)) {
            layoutId = COLLECTION_LAYOUT;
        } else if (USER_RECORD_BUY_RECORD.equals(mUserRecordType)) {
            layoutId = BUY_RECORD_LAYOUT;
        }

        return layoutId;
    }

    private void setupFragment(int layout) {
        UserRecordFragment fragment = new UserRecordFragment();
        Bundle args = new Bundle();
        args.putInt(UserRecordFragment.LAYOUT_ID, layout);
        fragment.setArguments(args);
        mFragmentTransaction.add(R.id.user_record_content, fragment);
        mFragmentTransaction.commit();

    }


    public void updateWallpaper() {
        SharedPreferences sharedPreferences = getSharedPreferences("config", 0);
        int wallpaperResource = sharedPreferences.getInt("wallpaper",0);
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
        this.registerReceiver(mWallpaperReceiver,filter);
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
