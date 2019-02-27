package com.pkit.launcher.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.PopupWindow;

import com.pkit.launcher.R;
import com.pkit.launcher.message.SearchMessageManager;
import com.pkit.launcher.view.FocusScaleHelper.BrowseItemFocusHighlight;

public class SearchSelectorPop extends PopupWindow implements OnClickListener, OnKeyListener, OnFocusChangeListener {
	private Handler handler;
	private View searchBtn;
	private FocusScaleHandler mFocusHighlight;
	private int mode;
	private View selector;

	public SearchSelectorPop(Handler handler, Context context) {
		super();
		this.handler = handler;
		init(context);
	}

	@SuppressLint("InflateParams")
	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.search_menu_layout, null);
		setContentView(view);

		searchBtn = view.findViewById(R.id.search_menu_search_buton);
		searchBtn.setOnClickListener(this);
		searchBtn.setOnFocusChangeListener(this);

		selector = view.findViewById(R.id.search_menu_selector_buton);
		selector.setOnClickListener(this);
		selector.setOnFocusChangeListener(this);

		setFocusable(true);
		searchBtn.setOnKeyListener(this);
		selector.setOnKeyListener(this);

		mFocusHighlight = new BrowseItemFocusHighlight(FocusScale.ZOOM_FACTOR_SMALL);
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		if (mode == SearchMessageManager.SEARCH_MODE_1) {
			selector.requestFocus();
		} else {
			searchBtn.requestFocus();
		}
		super.showAtLocation(parent, gravity, x, y);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		if (id == R.id.search_menu_search_buton) {
			handler.sendEmptyMessage(SearchMessageManager.SEARCH_MODE_1);
		} else if (id == R.id.search_menu_selector_buton) {
			handler.sendEmptyMessage(SearchMessageManager.SEARCH_MODE_2);
		}
		dismiss();
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			dismiss();
			return true;
		}
		return false;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		mFocusHighlight.onItemFocused(v, hasFocus);
	}
}
