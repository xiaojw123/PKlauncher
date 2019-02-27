package com.pkit.launcher.view;

import com.pkit.launcher.R;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class ListViewWrapper extends ListView {

	private float selectedSize;
	private ColorStateList colorList;
	private int stayColor;

	public ListViewWrapper(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ListViewWrapper(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ListViewWrapper(Context context) {
		super(context);
		init();
	}

	private void init() {
		selectedSize = getResources().getDimension(R.dimen.text_size_20_sp);
		colorList = getResources().getColorStateList(R.drawable.media_browser_text_color);
		stayColor = getResources().getColor(R.color.green_2);
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		View selectedView = getSelectedView();
		if (selectedView == null) {
			return;
		}
		TextView textView = findTextViewFromParent(selectedView);
		if (gainFocus) {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, selectedSize);
			textView.setTextColor(colorList);
			textView.requestFocus();
		} else {
			textView.setTextColor(stayColor);
		}
	}

	private TextView findTextViewFromParent(View view) {
		TextView textView = null;
		if (view != null) {
			if (view instanceof ViewGroup) {
				ViewGroup container = (ViewGroup) view;
				textView = (TextView) container.getChildAt(0);
			} else {
				textView = (TextView) view;
			}
		}
		return textView;
	}
}
