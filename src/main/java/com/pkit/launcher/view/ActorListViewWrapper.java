package com.pkit.launcher.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.utils.APPLog;
import com.pkit.launcher.view.listener.PlayerExitBtnFocusListener;

public class ActorListViewWrapper extends ListView {
	private int passColor;
	private int stayColor;
	protected View lastItem;
	public ActorListViewWrapper(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ActorListViewWrapper(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ActorListViewWrapper(Context context) {
		super(context);
		init();
	}

	private void init() {
		passColor = getResources().getColor(R.color.color_bfbfbf);
		stayColor = getResources().getColor(R.color.color_00cf55);
	}
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		View selectedView = getSelectedView();
		TextView textView = findTextViewFromParent(selectedView);
		if (textView != null) {
			if (gainFocus) {
				APPLog.printDebug("onFocusChanged===gainFocus==true");
				textView.setTextColor(stayColor);
				textView.setScaleX(1.08f);
				textView.setScaleY(1.08f);
				lastItem=selectedView;
			} else {
				APPLog.printDebug("onFocusChanged===gainFocus==false");
				textView.setTextColor(passColor);
				textView.setScaleX(1.0f);
				textView.setScaleY(1.0f);
			    setNextFocusLeftId(PlayerExitBtnFocusListener.btnMemoryId);
			}
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
