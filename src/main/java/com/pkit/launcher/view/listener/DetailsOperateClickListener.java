package com.pkit.launcher.view.listener;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.activity.DetailsActivity;
import com.pkit.launcher.activity.PlayerActivity;
import com.pkit.launcher.animation.DetailsSelectionsAnimator;
import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.service.aidl.Detail;
import com.pkit.launcher.service.aidl.IContentService;
import com.pkit.launcher.view.SelectionsPop;

public class DetailsOperateClickListener implements OnClickListener {
	public static final int DURATION = 1000;
	private Context context;
	private Resources mResources;
	private IContentService contentService;
	private RelativeLayout parantWindow;
	private SelectionsPop pop;
	RelativeLayout selections_window;
	private Detail detail;
	private ArrayList<Content> contentlist;
	private int position, seekPosition;
	private int iconTextleftMargin;
	private String collectStr, cancelCollectStr;
	private String followStr, cancelFollowStr;

	public DetailsOperateClickListener(Context context, IContentService contentService, RelativeLayout parentWindow, Detail detail) {
		this.context = context;
		this.contentService = contentService;
		this.parantWindow = parentWindow;
		this.detail = detail;
		seekPosition = detail.seekPosition;
		position = detail.position;
		initParams();
	}

	public void setRecommendList(ArrayList<Content> contentList) {
		this.contentlist = contentList;
	}

	public void setSeekPosition(int seekPosition) {
		this.seekPosition = seekPosition;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	private void initParams() {
		mResources = context.getResources();
		iconTextleftMargin = (int) mResources.getDimension(R.dimen.details_button_text_left_margin);
		collectStr = mResources.getString(R.string.collect);
		followStr = mResources.getString(R.string.follow_episode);
		cancelCollectStr = mResources.getString(R.string.cancel_collect);
		cancelFollowStr = mResources.getString(R.string.cancel_follow);
	}

	public void showSelectionsPopWindow() {
		if (pop == null) {
			pop = new SelectionsPop(context, detail.sources, detail, contentlist);
		}
		View contentView = pop.getContentView();
		pop.showAtLocation(parantWindow, Gravity.FILL, 0, 0);
		DetailsSelectionsAnimator.startFadeInAnimator(contentView);
	}

	public void hidePopWindow() {
		if (pop != null && pop.isShowing()) {
			pop.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		LinearLayout button = (LinearLayout) v;
		switch (button.getId()) {
		case R.id.details_media_btn1:
			try {
				setMediaButton1(button);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		case R.id.details_media_btn2:
			playMedia();
			break;
		case R.id.details_media_btn3:
			showSelectionsPopWindow();
			break;

		default:
			break;
		}

	}

	private void setMediaButton1(LinearLayout button1) throws RemoteException {
		String contentID = detail.contentID;
		boolean isFocused = button1.hasFocus();
		int childSize = button1.getChildCount();
		if (childSize == 1) {
			TextView textview = (TextView) button1.getChildAt(0);
			if (textview == null) {
				return;
			}
			LinearLayout.LayoutParams textParams = (LinearLayout.LayoutParams) textview.getLayoutParams();
			String text = (String) textview.getText();
			if (cancelCollectStr.equals(text)) {
				textview.setText(collectStr);
				button1.addView(getCollectIconImg(isFocused), 0);
			} else if (cancelFollowStr.equals(text)) {
				textview.setText(followStr);
				button1.addView(getFollowIconImg(isFocused), 0);
			}
			textParams.leftMargin = iconTextleftMargin;
			contentService.deleteFavorite(contentID);
		} else if (childSize == 2) {
			ImageView iconImg = (ImageView) button1.getChildAt(0);
			TextView iconText = (TextView) button1.getChildAt(1);
			LinearLayout.LayoutParams iconTextParams = (LinearLayout.LayoutParams) iconText.getLayoutParams();
			if (iconImg == null || iconText == null) {
				return;
			}
			String text = (String) iconText.getText();
			if (collectStr.equals(text)) {
				iconImg.setVisibility(View.GONE);
				iconTextParams.leftMargin = 0;
				iconText.setText(cancelCollectStr);
				contentService.addFavorite(detail);
			} else if (followStr.equals(text)) {
				iconImg.setVisibility(View.GONE);
				iconTextParams.leftMargin = 0;
				iconText.setText(cancelFollowStr);
				contentService.addFavorite(detail);
			} else if (cancelCollectStr.equals(text)) {
				iconImg.setVisibility(View.VISIBLE);
				iconTextParams.leftMargin = iconTextleftMargin;
				iconText.setText(collectStr);
				contentService.deleteFavorite(contentID);
			} else if (cancelFollowStr.equals(text)) {
				iconImg.setVisibility(View.VISIBLE);
				iconTextParams.leftMargin = iconTextleftMargin;
				iconText.setText(followStr);
				contentService.deleteFavorite(contentID);
			}
		} else {
			return;
		}
	}

	private ImageView getFollowIconImg(boolean isFocused) {
		int width = (int) mResources.getDimension(R.dimen.details_icon_follow_width);
		int height = (int) mResources.getDimension(R.dimen.details_icon_follow_height);
		Drawable iconDrawable;
		if (isFocused) {
			iconDrawable = mResources.getDrawable(R.drawable.details_icon_follow_episode_focus);
		} else {
			iconDrawable = mResources.getDrawable(R.drawable.details_icon_follow_episode);
		}

		LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(width, height);
		ImageView iconImg = new ImageView(context);
		iconImg.setLayoutParams(iconParams);
		iconImg.setBackground(iconDrawable);
		return iconImg;
	}

	private ImageView getCollectIconImg(boolean isFocused) {
		int width = (int) mResources.getDimension(R.dimen.details_icon_collect_width);
		int height = (int) mResources.getDimension(R.dimen.details_icon_collect_height);
		Drawable iconDrawable;
		if (isFocused) {
			iconDrawable = mResources.getDrawable(R.drawable.details_icon_collection_focus);
		} else {
			iconDrawable = mResources.getDrawable(R.drawable.details_icon_collection);
		}
		LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(width, height);
		ImageView iconImg = new ImageView(context);
		iconImg.setLayoutParams(iconParams);
		iconImg.setBackground(iconDrawable);
		return iconImg;
	}

	public void playMedia() {
		Intent intent = new Intent(((DetailsActivity) context), PlayerActivity.class);
		// intent数据
		intent.putExtra(PlayerActivity.MEDIA_DETAILS, detail);
		intent.putExtra(PlayerActivity.MEDIA_SEEK_POSITION, seekPosition);
		intent.putExtra(PlayerActivity.MEDIA_POSITION, position);
		intent.putParcelableArrayListExtra(PlayerActivity.MEDIA_RECOMMENDS, contentlist);
		((DetailsActivity) context).startActivityForResult(intent, DetailsActivity.UPDATE_DATA);
	}

	public class AnimThread extends AsyncTask<Void, Point, Void> {
		View view;
		OverScroller scroller;

		public AnimThread(OverScroller scroller) {
			this.scroller = scroller;
		}

		public void setView(View view) {
			this.view = view;
		}

		@Override
		protected Void doInBackground(Void... params) {
			while (scroller.computeScrollOffset()) {
				int curX = scroller.getCurrX();
				int curY = scroller.getCurrY();
				Point point = new Point(curX, curY);
				publishProgress(point);
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Point... values) {
			Point point = values[0];
			int x = point.x;
			int y = point.y;
			view.setTranslationX(x);
			view.setTranslationY(y);
		}

	}
}
