package com.pkit.launcher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;
import android.widget.Scroller;

public class CustomScrollView extends ScrollView {
	private Scroller mScroller;
	public boolean isScrollUp;
	public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScroller = new Scroller(context);
	}
	public CustomScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller = new Scroller(context);
	}
	public CustomScrollView(Context context) {
		super(context);
		mScroller = new Scroller(context);
	}
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			int curX = mScroller.getCurrX();
			int curY = mScroller.getCurrY();
			smoothScrollTo(curX, curY);
		}
	}

	public void smoothScrollTo(int x, int y,int dx,int dy, int dur) {
		smoothScrollBy(x, y,dx,dy, dur);
	}

	public void smoothScrollBy(int x,int y,int dx, int dy, int dur) {
		if (mScroller==null||getChildCount() == 0) {
			return;
		}
		if(!mScroller.isFinished()){
			mScroller.abortAnimation();
		}
		mScroller.startScroll(x, y, dx, dy, dur);
		invalidate();
	}

}
