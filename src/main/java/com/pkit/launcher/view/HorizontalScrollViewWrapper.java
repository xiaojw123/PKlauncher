package com.pkit.launcher.view;

import java.lang.reflect.Field;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class HorizontalScrollViewWrapper extends HorizontalScrollView {
	private OverScrollerWrapper scroller;

	public HorizontalScrollViewWrapper(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initScroller();
	}

	public HorizontalScrollViewWrapper(Context context, AttributeSet attrs) {
		super(context, attrs);
		initScroller();
	}

	public HorizontalScrollViewWrapper(Context context) {
		super(context);
		initScroller();
	}

	private void initScroller() {
		try {
			scroller = new OverScrollerWrapper(getContext());
			Field field = getClass().getField("mScroller");
			field.setAccessible(true);
			field.set(this, scroller);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
