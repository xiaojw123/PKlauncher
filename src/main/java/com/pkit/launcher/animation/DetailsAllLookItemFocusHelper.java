package com.pkit.launcher.animation;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

public class DetailsAllLookItemFocusHelper {
	private static final int DURATION = 250;
	private static final float ZOOM_IN = 1.11f;
	private static final float ZOOM_OUT = 1.0f;

	public static void focusedAnimation(View view) {
		startAnimation(view, ZOOM_OUT, ZOOM_IN);
	}
	public static void unFocusedAnimation(View view) {
		startAnimation(view, ZOOM_IN, ZOOM_OUT);
	}
	public static void startAnimation(View target, float from, float to) {
		PropertyValuesHolder xHolder = PropertyValuesHolder.ofFloat("scaleX", from, to);
		PropertyValuesHolder yHolder = PropertyValuesHolder.ofFloat("scaleY", from, to);
		ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(target, xHolder, yHolder);
		animator.setDuration(DURATION);
		animator.start();
	}

}
