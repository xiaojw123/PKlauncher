package com.pkit.launcher.animation;

import com.pkit.launcher.R;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class LoadHelper {
	private Context context;
	private View loadingView;
	private ImageView loadingImg;

	public LoadHelper(Context context, View loadingView, ImageView loadingImg) {
		this.context = context;
		this.loadingView = loadingView;
		this.loadingImg = loadingImg;
	}

	public void startLoading() {
		if (context != null && loadingView != null && loadingImg != null) {
			if (loadingView.getVisibility() != View.VISIBLE) {
				loadingView.setVisibility(View.VISIBLE);
			}
			Animation roateAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_anim);
			loadingImg.startAnimation(roateAnimation);
		}
	}

	public void stopLoading() {
		if (loadingView != null && loadingImg != null) {
			if (loadingView.getVisibility() != View.VISIBLE) {
				return;
			}
			loadingView.setVisibility(View.GONE);
			loadingImg.clearAnimation();
		}
	}
}
