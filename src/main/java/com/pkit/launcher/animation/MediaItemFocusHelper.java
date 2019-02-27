package com.pkit.launcher.animation;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.View;

public class MediaItemFocusHelper {
	private static final int DURATION = 250;
	private static final float ZOOM_IN = 1.15f;
	private static final float ZOOM_OUT = 1.0f;

	public static void selectedAnim(View view) {
		startAnim(view, ZOOM_OUT, ZOOM_IN);
	}

	public static void unSelectedAnim(View view) {
		startAnim(view, ZOOM_IN, ZOOM_OUT);
	}

	private static void startAnim(View view, float from, float to) {
		AnimListener listener = new AnimListener(view);
		ValueAnimator anim = ValueAnimator.ofFloat(from, to);
		anim.setDuration(DURATION);
		anim.addUpdateListener(listener);
		anim.start();
	}

	static class AnimListener implements AnimatorUpdateListener {
		private View view;

		public AnimListener(View view) {
			super();
			this.view = view;
		}

		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			float value = (Float) animation.getAnimatedValue();
			this.view.setScaleX(value);
			this.view.setScaleY(value);
		}
	}

}
