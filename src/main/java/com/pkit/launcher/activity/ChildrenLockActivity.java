package com.pkit.launcher.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.view.HomePagedView;

/**
 * Created by jiaxing on 2015/5/12.
 */
public class ChildrenLockActivity extends Activity {
    private ImageView mTitleImage;
    private TextView mTitleTextView;
    private HomePagedView mHomePagedView;
    private int mPage = 0;
    private boolean canBack = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.children_lock_layout);
        mTitleImage = (ImageView) findViewById(R.id.user_record_icon);
        mTitleImage.setVisibility(View.GONE);
        mTitleImage.setImageResource(R.drawable.check_update_title_icon);
        mTitleTextView = (TextView) findViewById(R.id.user_record_title);
        mTitleTextView.setVisibility(View.GONE);
        mTitleTextView.setText(R.string.children_lock);
        findViewById(R.id.user_record_title_layout).setVisibility(View.GONE);
        mHomePagedView = (HomePagedView) findViewById(R.id.switch_view);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (mPage < 2) {
                mPage++;
                mHomePagedView.snapToPage(mPage);
            }
        }else if(keyCode == KeyEvent.KEYCODE_BACK) {
            if (mPage > 0) {
                mPage--;
                mHomePagedView.snapToPage(mPage);
            }
        }
        if(mPage==0 && canBack){
            return super.onKeyDown(keyCode, event);
        }else{
            if(mPage==0){
                canBack = true;
            }else{
                canBack = false;
            }
            return true;
        }
    }
}
