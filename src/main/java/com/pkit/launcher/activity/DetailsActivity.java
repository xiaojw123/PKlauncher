package com.pkit.launcher.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.animation.LoadHelper;
import com.pkit.launcher.message.MediaDetailMessageManager;
import com.pkit.launcher.service.ContentService;
import com.pkit.launcher.service.aidl.IContentService;
import com.pkit.launcher.utils.APPLog;
import com.pkit.launcher.view.CustomScrollView;

/**
 * 
 * @author xiaojiwei
 * 
 */
public class DetailsActivity extends BaseActivity implements ServiceConnection {
	public static final int UPDATE_DATA = 0x1001;
	public static final int FINISH = 0x1002;
	public static final String CONTENT_ID = "content_id";
	private MediaDetailMessageManager msgManager;
	private IContentService contentService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_media_layout);
		init();
	}

	private void init() {
		RelativeLayout parentWindow = (RelativeLayout) findViewById(R.id.details_media_window);
		TextView mediaName = (TextView) findViewById(R.id.details_media_name_tv);
		TextView mediaUpdate = (TextView) findViewById(R.id.details_media_update_tv);
		TextView director = (TextView) findViewById(R.id.details_media_director_tv);
		TextView actor = (TextView) findViewById(R.id.details_media_actor_tv);
		TextView area = (TextView) findViewById(R.id.details_media_area_tv);
		TextView type = (TextView) findViewById(R.id.details_media_category_tv);
		TextView intro = (TextView) findViewById(R.id.details_media_intro_tv);
		ImageView poster = (ImageView) findViewById(R.id.details_media_poster_img);
		LinearLayout button1 = (LinearLayout) findViewById(R.id.details_media_btn1);
		LinearLayout button2 = (LinearLayout) findViewById(R.id.details_media_btn2);
		LinearLayout button3 = (LinearLayout) findViewById(R.id.details_media_btn3);
		CustomScrollView scrollView = (CustomScrollView) findViewById(R.id.details_media_scrollview);
		ImageView globalMask = (ImageView) findViewById(R.id.details_global_mask);
		ImageView allLookMask = (ImageView) findViewById(R.id.details_recommend_mask_img);
		RelativeLayout allLookContainer = (RelativeLayout) findViewById(R.id.details_media_all_look_container);
		View loadingView = findViewById(R.id.loading_view);
		ImageView loadingImg = (ImageView) findViewById(R.id.loading_img);
		LoadHelper loadHelper = new LoadHelper(this, loadingView, loadingImg);
		msgManager = new MediaDetailMessageManager(this, parentWindow, scrollView);
		msgManager.setLoadingHepler(loadHelper);
		msgManager.setMask(globalMask, allLookMask);
		msgManager.setTitleView(mediaName, mediaUpdate);
		msgManager.setMediaButton(button1, button2, button3);
		msgManager.setMediaAttrView(director, actor, area, type, intro, poster);
		msgManager.setAllLookContainer(allLookContainer);
		bindService();
	}

	private void bindService() {
		Intent intent = new Intent(this, ContentService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		APPLog.printDebug("contentID======" + getContentID());
		contentService = IContentService.Stub.asInterface(service);
		msgManager.setContentService(contentService);
		Message msg = msgManager.obtainMessage(MediaDetailMessageManager.LOAD_DETAIL);
		msg.obj = getContentID();
		msgManager.sendMessage(msg);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {

	}

	private String getContentID() {
//		return "000000517";//电视剧
//		return "000001532";//综艺
		return getIntent().getStringExtra(CONTENT_ID);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (data == null) {
				return;
			}
			int seekPos = data.getIntExtra(PlayerActivity.MEDIA_SEEK_POSITION, 0);
			int pos = data.getIntExtra(PlayerActivity.MEDIA_POSITION, 0);
			msgManager.updatePlayInfo(pos, seekPos);
			if (resultCode == FINISH) {
				finish();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
