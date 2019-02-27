package com.pkit.launcher.animation;

import android.animation.ObjectAnimator;
import android.view.View;

public class DetailsSelectionsAnimator {
	private static final int DURATION = 500;
	private static final float MAX_ALPHA = 1.0f;
	private static final float MIN_ALPHA = 0.0f;

	public static void startFadeInAnimator(View view) {
		startAnim(view, MIN_ALPHA, MAX_ALPHA);
	}

	public static void startFadeOutAnimator(View view) {
		startAnim(view, MAX_ALPHA, MIN_ALPHA);
	}
	public static void startAnim(View view, float from, float to) {
		ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", from, to);
		animator.setDuration(DURATION);
		animator.start();
	}

}
