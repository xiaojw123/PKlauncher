package com.pkit.launcher.view;

import com.pkit.launcher.utils.APPLog;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.OverScroller;

public class OverScrollerWrapper extends OverScroller {
	private int duration = 500;

	public OverScrollerWrapper(Context context, Interpolator interpolator, float bounceCoefficientX, float bounceCoefficientY, boolean flywheel) {
		super(context, interpolator, bounceCoefficientX, bounceCoefficientY, flywheel);
	}

	public OverScrollerWrapper(Context context, Interpolator interpolator, float bounceCoefficientX, float bounceCoefficientY) {
		super(context, interpolator, bounceCoefficientX, bounceCoefficientY);
	}

	public OverScrollerWrapper(Context context, Interpolator interpolator) {
		super(context, interpolator);
	}

	public OverScrollerWrapper(Context context) {
		super(context);
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy) {
		APPLog.printInfo("startScroll duration:" + duration);
		super.startScroll(startX, startY, dx, dy, this.duration);
	}
}
