package com.pkit.launcher.view.listener;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.message.MediaDetailMessageManager;
import com.pkit.launcher.view.CustomScrollView;

public class DetailsOperateFocusListener implements OnFocusChangeListener {
	public static final float ZOOM_IN = 1.12f;
	public static final float ZOOM_OUT = 1.0f;
	private CustomScrollView scrollView;
	private TextView textView;
	private ImageView iconImg;
	private int lightTextColor;
	private int dullTextColor;
	private Context context;
	private String collectStr;
	private String followStr;
	private String playStr;
	private Resources mResources;
	private boolean isMultiSet;
	private int startY;

	public DetailsOperateFocusListener(Context context, CustomScrollView scrollView) {
		this.context = context;
		this.scrollView = scrollView;
		intParams();
	}

	private void intParams() {
		mResources = context.getResources();
		lightTextColor = mResources.getColor(R.color.color_fafafa);
		dullTextColor = mResources.getColor(R.color.color_8ec984);
		collectStr = mResources.getString(R.string.collect);
		followStr = mResources.getString(R.string.follow_episode);
		playStr = mResources.getString(R.string.play);
		startY = (int) mResources.getDimension(R.dimen.details_scrollview_y);

	}

	public void setSet(boolean isMultiSet) {
		this.isMultiSet = isMultiSet;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			String tag = (String) v.getTag();
			String btn_tag=isMultiSet==true?MediaDetailMessageManager.TAG_BUTTON3:MediaDetailMessageManager.TAG_BUTTON2;
			if (scrollView.isScrollUp && btn_tag.equals(tag)) {
				scrollView.isScrollUp = false;
			} else {
				scrollView.smoothScrollTo(0, startY, 0, -startY, 0);
			}
			zoomIn(v);
			setLightButton(v);
		} else {
			zoomOut(v);
			setDullButton(v);
		}

	}

	private void zoomOut(View view) {
		view.setScaleX(ZOOM_OUT);
		view.setScaleY(ZOOM_OUT);
		setLightButton(view);
	}

	private void zoomIn(View view) {
		view.setScaleX(ZOOM_IN);
		view.setScaleY(ZOOM_IN);
		setDullButton(view);
	}

	private void setLightButton(View v) {
		LinearLayout button = (LinearLayout) v;
		if (button.getChildCount() == 1) {
			textView = (TextView) button.getChildAt(0);
		} else {
			iconImg = (ImageView) button.getChildAt(0);
			textView = (TextView) button.getChildAt(1);
			String text = (String) textView.getText();
			setLightIcon(text);
		}
		textView.setTextColor(lightTextColor);
	}

	private void setDullButton(View v) {
		LinearLayout button = (LinearLayout) v;
		if (button.getChildCount() == 1) {
			textView = (TextView) button.getChildAt(0);
		} else {
			iconImg = (ImageView) button.getChildAt(0);
			textView = (TextView) button.getChildAt(1);
			String text = (String) textView.getText();
			setDullIcon(text);
		}
		textView.setTextColor(dullTextColor);

	}

	private void setLightIcon(String text) {
		if (iconImg == null) {
			return;
		}
		if (collectStr.equals(text)) {
			iconImg.setBackground(mResources.getDrawable(R.drawable.details_icon_collection_focus));
		} else if (playStr.equals(text)) {
			iconImg.setBackground(mResources.getDrawable(R.drawable.details_icon_play_focus));
		} else if (followStr.equals(text)) {
			iconImg.setBackground(mResources.getDrawable(R.drawable.details_icon_follow_episode_focus));
		}

	}

	private void setDullIcon(String text) {
		if (iconImg == null) {
			return;
		}
		if (collectStr.equals(text)) {
			iconImg.setBackground(mResources.getDrawable(R.drawable.details_icon_collection));
		} else if (playStr.equals(text)) {
			iconImg.setBackground(mResources.getDrawable(R.drawable.details_icon_play));
		} else if (followStr.equals(text)) {
			iconImg.setBackground(mResources.getDrawable(R.drawable.details_icon_follow_episode));
		}
	}
}
