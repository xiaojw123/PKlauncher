package com.pkit.launcher.activity;

import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.message.SearchMessageManager;
import com.pkit.launcher.service.ContentService;
import com.pkit.launcher.service.aidl.IContentService;
import com.pkit.launcher.utils.APPLog;
import com.pkit.launcher.view.SearchSelectorPop;

public class SearchActivity extends BaseActivity implements ServiceConnection {
	public static final String START_MODE = "start_mode";
	public static final String SEARCH = "search";
	public static final String SELECTOR = "selector";
	public static final String CONTENT_ID_ARG = "contentID";
	private SearchSelectorPop popWindows;
	private RelativeLayout searchWindows;
	private SearchMessageManager messageManager;
	private IContentService contentService;
	private String mode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_layout);
		init();
	}

	private void init() {
		TextView textView1 = (TextView) findViewById(R.id.search_result_text_1);
		TextView textView2 = (TextView) findViewById(R.id.search_result_text_2);
		mode = getIntent().getStringExtra(START_MODE);
		String contentID = getIntent().getStringExtra(CONTENT_ID_ARG);

		FragmentManager fragmentManager = getFragmentManager();
		messageManager = new SearchMessageManager(this, contentID, fragmentManager);
		messageManager.setTextView1(textView1);
		messageManager.setTextView2(textView2);

		DisplayMetrics outMetrics = new DisplayMetrics();
		Display display = getWindowManager().getDefaultDisplay();
		display.getMetrics(outMetrics);

		searchWindows = (RelativeLayout) findViewById(R.id.search_windows);
		int menuWidth = outMetrics.widthPixels;
		int menuHeight = (int) getResources().getDimension(R.dimen.z_search_menu_height);
		popWindows = new SearchSelectorPop(messageManager, this);
		popWindows.setWidth(menuWidth);
		popWindows.setHeight(menuHeight);

		bindService();
	}

	@Override
	protected void onResume() {
		super.onResume();
		messageManager.setContentService(contentService);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU && !popWindows.isShowing()) {
			int top = (int) getResources().getDimension(R.dimen.z_search_menu_top);
			popWindows.setMode(messageManager.getCurrentMode());
			popWindows.showAtLocation(searchWindows, Gravity.CENTER, 0, top);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void bindService() {
		Intent intent = new Intent(this, ContentService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(this);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		contentService = IContentService.Stub.asInterface(service);
		messageManager.setContentService(contentService);
		APPLog.printInfo("START_MODE:" + mode);
		if (SEARCH.equals(mode)) {
			messageManager.sendEmptyMessage(SearchMessageManager.SEARCH_MODE_1);
		} else if (SELECTOR.equals(mode)) {
			messageManager.sendEmptyMessage(SearchMessageManager.SEARCH_MODE_2);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {

	}
}
