package com.pkit.launcher.view.listener;

import com.pkit.launcher.R;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PlayerExitRecommendItemFocusListener implements OnFocusChangeListener {
    public static final float ZOOM_IN_X = 1.11f;
    public static final float ZOOM_IN_Y = 1.11f;
    public static final float ZOOM_OUT_X = 1.0f;
    public static final float ZOOM_OUT_Y = 1.0f;
    private Resources mResources;
    public PlayerExitRecommendItemFocusListener(Context context) {
	mResources = context.getResources();
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
	RelativeLayout parentView = (RelativeLayout) v.getParent();
	RelativeLayout recommendView = (RelativeLayout) v;
	ImageView recommendFocusImg = (ImageView) recommendView.getChildAt(1);
	TextView recommendNameText = (TextView) recommendView.getChildAt(2);
	if (hasFocus) {
		setLightName(recommendNameText);
	    recommendFocusImg.setVisibility(View.VISIBLE);
	    zoomIn(v);
	    v.bringToFront();
	    parentView.postInvalidate();
	} else {
		setDullName(recommendNameText);
	    recommendFocusImg.setVisibility(View.GONE);
	    zoomOut(v);
	}

    }
private void setLightName(TextView name) {
	name.setSelected(true);
	name.setTextSize(18);
	name.setTextColor(mResources
		    .getColor(R.color.color_fafafa));
	name.setBackgroundColor(mResources.getColor(android.R.color.transparent));
		
	}
    private void setDullName(TextView name) {
    	name.setSelected(false);
    	name.setTextSize(15);
    	name.setTextColor(mResources
   		    .getColor(R.color.color_979797));
    	name.setBackground(mResources.getDrawable(R.drawable.player_recommend_text_bg));
		
	}
	private void zoomOut(View view) {
	view.setScaleX(ZOOM_OUT_X);
	view.setScaleY(ZOOM_OUT_Y);
    }
    private void zoomIn(View view) {
	view.setScaleX(ZOOM_IN_X);
	view.setScaleY(ZOOM_IN_Y);
    }
    }

