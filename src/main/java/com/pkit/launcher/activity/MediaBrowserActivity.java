package com.pkit.launcher.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.message.MediaBrowserMessageManager;
import com.pkit.launcher.service.ContentService;
import com.pkit.launcher.service.aidl.Container;
import com.pkit.launcher.service.aidl.IContentService;
import com.pkit.launcher.view.PageGridView;

/**
 * 
 * @author Richard
 *
 */
public class MediaBrowserActivity extends BaseActivity implements ServiceConnection {
	public static final String CONTAINER_PARAM = "container_param";
	private MediaBrowserMessageManager msgManager;
	private Container container;
	private IContentService contentService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media_browser_layout);
		init();
	}

	private void init() {
		// title
		TextView nameView = (TextView) findViewById(R.id.media_browser_name);
		TextView pageView = (TextView) findViewById(R.id.media_browser_page);
		TextView countView = (TextView) findViewById(R.id.media_browser_count);
		// left nav
		RelativeLayout navPanelView = (RelativeLayout) findViewById(R.id.media_browser_nav);

		// video item view
		PageGridView videoListView = (PageGridView) findViewById(R.id.media_browser_item_list);
		ProgressBar loadingBar = (ProgressBar) findViewById(R.id.media_browser_item_list_loading);

		container = getParentContainer();
		nameView.setText(container.name);

		msgManager = new MediaBrowserMessageManager(this, pageView, countView, navPanelView, videoListView, loadingBar);

		bindService();
	}

	@Override
	protected void onResume() {
		super.onResume();
		msgManager.setContentService(contentService);
	}

	private Container getParentContainer() {
		Container container = getIntent().getParcelableExtra(CONTAINER_PARAM);
		// Container container = new Container("00", "02", "电影", null);
		return container;
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
		msgManager.setContentService(contentService);
		Message msg = msgManager.obtainMessage(MediaBrowserMessageManager.LOAD_MENU_LIST, container);
		msgManager.sendMessage(msg);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {

	}
}
