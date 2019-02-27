package com.pkit.launcher.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.activity.DetailsActivity;
import com.pkit.launcher.animation.DetailsAllLookItemFocusHelper;
import com.pkit.launcher.animation.LoadHelper;
import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.service.aidl.Detail;
import com.pkit.launcher.service.aidl.IContentService;
import com.pkit.launcher.service.aidl.Item;
import com.pkit.launcher.service.aidl.Source;
import com.pkit.launcher.utils.APPLog;
import com.pkit.launcher.view.CustomScrollView;
import com.pkit.launcher.view.listener.DetailsOperateClickListener;
import com.pkit.launcher.view.listener.DetailsOperateFocusListener;
import com.pkit.utils.DrawableUtil;

public class MediaDetailMessageManager extends Handler implements DialogInterface.OnClickListener {
	public static final int LOAD_DETAIL = 0x01;
	public static final int DETAIL_LOAD_COMPLETE = 0x02;
	public static final int RECOMMEND_LOAD_COMPLETE = 0x03;

	public static final int DETAIL_LOAD_FAILED = 0x04;

	public static final int lOADING = 0x05;

	public static final String TAG_BUTTON1 = "btn1";
	public static final String TAG_BUTTON2 = "btn2";
	public static final String TAG_BUTTON3 = "btn3";
	private static final int DURATION = 500;
	private IContentService contentService;
	private Context context;
	private Resources mResources;
	private Detail detail;
	private LoadHelper loadHelper;
	private TextView mediaName, mediaUpdate;
	private TextView director, actor, area, category, intro;
	private ImageView poster;
	private LinearLayout button1, button2, button3;
	private CustomScrollView scrollView;
	private ImageView globalMask, allLookMask;
	private boolean isCollected, isPlayed;
	private RelativeLayout parentWindow;
	private RelativeLayout allLookContainer;
	private int width, height, left, spacing;
	private float selectedSize, unselectedSize;
	private int selectedLeftPad, unselectedLeftPad;
	private int selectedBottomPad, unselectedBottomPad;
	private LayoutInflater inflater;
	private ContentCallBack callback;
	private DetailsOperateFocusListener focuslistener;
	private DetailsOperateClickListener clickListener;
	private AlertDialog.Builder ab;
	private String promptMsg;
	private int startY;

	public MediaDetailMessageManager(Context context, RelativeLayout parentWindow, CustomScrollView scrollView) {
		this.context = context;
		this.scrollView = scrollView;
		this.parentWindow = parentWindow;
		initParams();
	}

	private void initParams() {
		mResources = context.getResources();
		promptMsg = mResources.getString(R.string.no_sources);
		startY = (int) mResources.getDimension(R.dimen.details_scrollview_y);
		left = (int) mResources.getDimension(R.dimen.details_recommend_left_bounder);
		spacing = (int) mResources.getDimension(R.dimen.details_recommend_item_spacing);
		width = (int) mResources.getDimension(R.dimen.details_recommend_item_width);
		height = (int) mResources.getDimension(R.dimen.details_recommend_item_height);
		selectedSize = mResources.getDimension(R.dimen.text_size_18_sp);
		unselectedSize = mResources.getDimension(R.dimen.text_size_19_sp);
		selectedLeftPad = (int) mResources.getDimension(R.dimen.details_recommend_name_selected_left_padding);
		selectedBottomPad = (int) mResources.getDimension(R.dimen.details_recommend_name_selected_bottom_padding);
		unselectedLeftPad = (int) mResources.getDimension(R.dimen.details_recommend_name_unselected_left_padding);
		unselectedBottomPad = (int) mResources.getDimension(R.dimen.details_recommend_name_unselected_bottom_padding);
	}

	public void setLoadingHepler(LoadHelper loadHelper) {
		this.loadHelper = loadHelper;
	}

	public void setMask(ImageView globalMask, ImageView allLookMask) {
		this.globalMask = globalMask;
		this.allLookMask = allLookMask;
	}

	public void setTitleView(TextView mediaName, TextView mediaUpdate) {
		this.mediaName = mediaName;
		this.mediaUpdate = mediaUpdate;
	}

	public void setMediaAttrView(TextView director, TextView actor, TextView area, TextView type, TextView intro, ImageView poster) {
		this.director = director;
		this.actor = actor;
		this.area = area;
		this.category = type;
		this.intro = intro;
		this.poster = poster;
	}

	public void setMediaButton(LinearLayout button1, LinearLayout button2, LinearLayout button3) {
		this.button1 = button1;
		this.button2 = button2;
		this.button3 = button3;
	}

	public void setAllLookContainer(RelativeLayout allLookContainer) {
		this.allLookContainer = allLookContainer;
	}

	public void setContentService(IContentService contentService) {
		this.contentService = contentService;
		if (this.contentService == null) {
			return;
		}
		try {
			if (callback == null) {
				callback = new ContentCallBack();
			}
			this.contentService.registCallback(callback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(Message msg) {
		int what = msg.what;
		if (what == LOAD_DETAIL) {
			String contentID = (String) msg.obj;
			if (contentID == null) {
				showPromptDialog();
				return;
			}
			loadHelper.startLoading();
			loadDetail(contentID);
		} else if (what == DETAIL_LOAD_COMPLETE) {
			Detail detail = (Detail) msg.obj;
			loadHelper.stopLoading();
			if (detail == null) {
				showPromptDialog();
				return;
			}
			this.detail = detail;
			updateTitleName(detail);
			updateNewest(detail);
			updateButton(detail);
			updateInformation(detail);
			loadRecommends(detail.contentID);
		} else if (what == RECOMMEND_LOAD_COMPLETE) {
			ArrayList<Content> contentList = (ArrayList<Content>) msg.obj;
			if (contentList == null || contentList.size() < 1) {
				return;
			}
			ArrayList<Content> recommendlist = new ArrayList<Content>();
			if (contentList.size() > 6) {
				for (int i = 0; i < 6; i++) {
					Content content = contentList.get(i);
					recommendlist.add(content);
				}
			} else {
				recommendlist = contentList;
			}
			clickListener.setRecommendList(recommendlist);
			updateRecommends(recommendlist);
		} else if (what == DETAIL_LOAD_FAILED) {
			showPromptDialog();
		}
	}

	private void updateTitleName(Detail detail) {
		mediaName.setText(detail.name);
	}

	private void updateNewest(Detail detail) {
		String updateStr = "更新到" + detail.lastUpdateEpisode;
		if (detail.type == Detail.TELE_TV_TYPE) {
			updateStr += "集";
		} else if (detail.type == Detail.VARIETY_TYPE) {
			updateStr += "期";
		} else {
			return;
		}
		mediaUpdate.setText(updateStr);
	}

	private void updateButton(Detail detail) {
		focuslistener = new DetailsOperateFocusListener(context, scrollView);
		clickListener = new DetailsOperateClickListener(context, contentService, parentWindow, detail);
		isCollected = detail.isFavorite == 1 ? true : false;
		isPlayed = detail.seekPosition == 0 ? false : true;
		APPLog.printDebug("type====" + detail.type);
		if (detail.type == Detail.FILM_TYPE) {
			updetaFilmButton(button1, button2);
		} else {
			updateEpisodeButton(button1, button2, button3);
		}
		initButton();
	}

	private void initButton() {
		button1.setTag(TAG_BUTTON1);
		button2.setTag(TAG_BUTTON2);
		button3.setTag(TAG_BUTTON3);
		button1.setOnClickListener(clickListener);
		button2.setOnClickListener(clickListener);
		button3.setOnClickListener(clickListener);
		button1.setOnFocusChangeListener(focuslistener);
		button2.setOnFocusChangeListener(focuslistener);
		button3.setOnFocusChangeListener(focuslistener);
		button1.setNextFocusRightId(button1.getId());
		button2.setNextFocusRightId(button2.getId());
		button3.setNextFocusRightId(button3.getId());
		button2.requestFocus();
	}

	private void updetaFilmButton(LinearLayout button1, LinearLayout button2) {
		if (isCollected) {
			TextView cancel_collect_tv = getTextView(false);
			cancel_collect_tv.setText(mResources.getString(R.string.cancel_collect));
			button1.addView(cancel_collect_tv);
		} else {
			TextView collect_tv = getTextView(true);
			collect_tv.setText(mResources.getString(R.string.collect));
			button1.addView(getCollectIconImg());
			button1.addView(collect_tv);
		}
		if (!isPlayed) {
			TextView play_tv = getTextView(true);
			play_tv.setText(mResources.getString(R.string.play));
			button2.addView(getPlayIconImg());
			button2.addView(play_tv);
		} else {
			TextView continue_play_tv = getTextView(false);
			continue_play_tv.setText(mResources.getString(R.string.continue_play));
			button2.addView(continue_play_tv);
		}
		button1.setVisibility(View.VISIBLE);
		button2.setVisibility(View.VISIBLE);
		button2.setOnKeyListener(btnKeyListener);
		focuslistener.setSet(false);
	}

	private void updateEpisodeButton(LinearLayout button1, LinearLayout button2, LinearLayout button3) {
		if (isCollected) {
			TextView cancel_follow_tv = getTextView(false);
			cancel_follow_tv.setText(mResources.getString(R.string.cancel_follow));
			button1.addView(cancel_follow_tv);
		} else {
			TextView follow_tv = getTextView(true);
			follow_tv.setText(mResources.getString(R.string.follow_episode));
			button1.addView(getFollowIconImg());
			button1.addView(follow_tv);
		}
		if (isPlayed) {
			TextView continue_play_tv = getTextView(false);
			continue_play_tv.setText(mResources.getString(R.string.episode_continue_play));
			button2.addView(continue_play_tv);
		} else {
			int setPosition = detail.position + 1;
			TextView first_set_tv = getTextView(false);
			first_set_tv.setText("第" + setPosition + "集");
			button2.addView(first_set_tv);
		}
		TextView selections_tv = getTextView(false);
		selections_tv.setText(mResources.getString(R.string.selection));
		button3.addView(selections_tv);
		button1.setVisibility(View.VISIBLE);
		button2.setVisibility(View.VISIBLE);
		button3.setVisibility(View.VISIBLE);
		button3.setOnKeyListener(btnKeyListener);
		focuslistener.setSet(true);
	}

	private ImageView getPlayIconImg() {
		int width = (int) mResources.getDimension(R.dimen.details_icon_play_width);
		int height = (int) mResources.getDimension(R.dimen.details_icon_play_height);
		Drawable iconDrawable = mResources.getDrawable(R.drawable.details_icon_play);
		LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(width, height);
		ImageView iconImg = new ImageView(context);
		iconImg.setLayoutParams(iconParams);
		iconImg.setBackground(iconDrawable);
		return iconImg;
	}

	private ImageView getFollowIconImg() {
		int width = (int) mResources.getDimension(R.dimen.details_icon_follow_width);
		int height = (int) mResources.getDimension(R.dimen.details_icon_follow_height);
		Drawable iconDrawable = mResources.getDrawable(R.drawable.details_icon_follow_episode);
		LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(width, height);
		ImageView iconImg = new ImageView(context);
		iconImg.setLayoutParams(iconParams);
		iconImg.setBackground(iconDrawable);
		return iconImg;
	}

	private ImageView getCollectIconImg() {
		int width = (int) mResources.getDimension(R.dimen.details_icon_collect_width);
		int height = (int) mResources.getDimension(R.dimen.details_icon_collect_height);
		Drawable iconDrawable = mResources.getDrawable(R.drawable.details_icon_collection);
		LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(width, height);
		ImageView iconImg = new ImageView(context);
		iconImg.setLayoutParams(iconParams);
		iconImg.setBackground(iconDrawable);
		return iconImg;
	}

	private TextView getTextView(boolean hasIcon) {
		LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
		if (hasIcon) {
			textParams.leftMargin = (int) mResources.getDimension(R.dimen.details_button_text_left_margin);
		}
		TextView textView = new TextView(context);
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(mResources.getColor(R.color.color_8ec984));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mResources.getDimension(R.dimen.text_size_22_sp));
		textView.setLayoutParams(textParams);
		return textView;
	}

	private void updateInformation(Detail detail) {
		StringBuffer directorStr = new StringBuffer();
		StringBuffer actorStr = new StringBuffer();
		StringBuffer areaStr = new StringBuffer();
		StringBuffer categoryStr = new StringBuffer();
		StringBuffer introStr = new StringBuffer();
		directorStr.append(mResources.getString(R.string.director));
		append(directorStr, null, detail.director, "    ");
		actorStr.append(mResources.getString(R.string.actor));
		append(actorStr, detail.actors, null, "    ");
		categoryStr.append(mResources.getString(R.string.category));
		append(categoryStr, null, detail.category, "  ");
		areaStr.append(mResources.getString(R.string.area));
		append(areaStr, null, detail.zone, "  ");
		introStr.append(mResources.getString(R.string.intro));
		introStr.append(detail.description);
		SpannableStringBuilder directorStyle = new SpannableStringBuilder(directorStr);
		SpannableStringBuilder actorStyle = new SpannableStringBuilder(actorStr);
		SpannableStringBuilder areaStyle = new SpannableStringBuilder(areaStr);
		SpannableStringBuilder categoryStyle = new SpannableStringBuilder(categoryStr);
		SpannableStringBuilder introStyle = new SpannableStringBuilder(introStr);
		ForegroundColorSpan colorSpan = new ForegroundColorSpan(mResources.getColor(R.color.color_ffd133));
		directorStyle.setSpan(colorSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		actorStyle.setSpan(colorSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		areaStyle.setSpan(colorSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		categoryStyle.setSpan(colorSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		introStyle.setSpan(colorSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		director.setText(directorStyle);
		actor.setText(actorStyle);
		area.setText(areaStyle);
		category.setText(categoryStyle);
		intro.setText(introStyle);
		String url = null;
		if (detail.imgPaths != null && detail.imgPaths.size() > 0) {
			url = detail.imgPaths.get(0);
		}
		DrawableUtil.displayDrawable(url, R.drawable.media_browser_item_loading, poster, 0);
	}

	public void append(StringBuffer sb, List<String> attrs, String attr, String spaceStr) {
		if (attr != null && !"".equals(attr)) {
			String a[] = attr.split(",");
			attrs = Arrays.asList(a);
		}
		if (attrs != null && attrs.size() > 0) {
			for (String item : attrs) {
				sb.append(item.trim());
				sb.append(spaceStr);
			}
		}
	}

	@SuppressLint("InflateParams")
	private void updateRecommends(List<Content> recommends) {
		for (Content content : recommends) {
			Item item = (Item) content;
			View view = getView(item);
			view.setOnFocusChangeListener(allLookFocusChangeListener);
			view.setOnKeyListener(allLookKeyListener);
			view.setOnClickListener(allLookClickListener);
			view.setTag(item.contentID);
			allLookContainer.addView(view);
		}
	}

	@SuppressLint("InflateParams")
	private View getView(Item item) {
		if (inflater == null) {
			inflater = LayoutInflater.from(context);
		}
		View view = inflater.inflate(R.layout.details_recommend_item_layout, null);
		LayoutParams params = new LayoutParams(width, height);
		params.leftMargin = left;
		left += (width - spacing);
		view.setLayoutParams(params);
		view.setFocusable(true);
		TextView name = (TextView) view.findViewById(R.id.details_recommend_name);
		ImageView poster = (ImageView) view.findViewById(R.id.details_recommend_poster);
		if (item == null) {
			poster.setBackgroundResource(R.drawable.media_browser_item_loading);
			return view;
		}
		String url = null;
		if (item.imgPaths != null && item.imgPaths.size() > 0) {
			url = item.imgPaths.get(0);
		}
		DrawableUtil.displayDrawable(url, R.drawable.media_browser_item_loading, poster, 0);
		name.setText(item.name);
		return view;
	}

	private void loadDetail(String contentID) {
		try {
			contentService.loadDetail(contentID);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void loadRecommends(String contentID) {
		try {
			contentService.loadRecommendItems(contentID);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	class ContentCallBack extends BaseCallBack {

		@Override
		public void onLoadComplete(String contentID, int pageNumber, int count, List<Content> contents) throws RemoteException {
			Message msg = obtainMessage(RECOMMEND_LOAD_COMPLETE, contents);
			sendMessage(msg);
		}

		@Override
		public void onDetailLoadComplete(Detail detail) throws RemoteException {
			Message msg = obtainMessage(DETAIL_LOAD_COMPLETE, detail);
			sendMessage(msg);
		}

		@Override
		public void onFailed(String contentID) throws RemoteException {
			Message msg = obtainMessage(DETAIL_LOAD_FAILED, contentID);
			sendMessage(msg);
		}
	}

	private OnKeyListener btnKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
					scrollView.smoothScrollTo(0, 0, 0, startY, DURATION);
				}
			}
			return false;
		}
	};
	private OnClickListener allLookClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String contentID = (String) v.getTag();
			Intent intent = new Intent((DetailsActivity) context, DetailsActivity.class);
			intent.putExtra(DetailsActivity.CONTENT_ID, contentID);
			context.startActivity(intent);
			((DetailsActivity) context).finish();
		}
	};
	private OnKeyListener allLookKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
					scrollView.isScrollUp = true;
					scrollView.smoothScrollTo(0, startY, 0, -startY, DURATION);
					showAllLookMask();
				}
			}
			return false;
		}
	};

	private OnFocusChangeListener allLookFocusChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			RelativeLayout allLookItem = (RelativeLayout) v;
			ImageView fousImg = (ImageView) allLookItem.getChildAt(1);
			TextView name = (TextView) allLookItem.getChildAt(2);
			View view = (View) v.getParent();
			if (hasFocus) {
				hideAllLookMask();
				fousImg.setSelected(true);
				setLightName(name);
				DetailsAllLookItemFocusHelper.focusedAnimation(v);
				v.bringToFront();
				view.postInvalidate();
			} else {
				fousImg.setSelected(false);
				setDullName(name);
				DetailsAllLookItemFocusHelper.unFocusedAnimation(v);
			}
		}

		private void setLightName(TextView name) {
			name.setSelected(true);
			name.setPadding(selectedLeftPad, 0, 0, selectedBottomPad);
			name.setTextSize(TypedValue.COMPLEX_UNIT_PX, selectedSize);
		}

		private void setDullName(TextView name) {
			name.setSelected(false);
			name.setPadding(unselectedLeftPad, 0, 0, unselectedBottomPad);
			name.setTextSize(TypedValue.COMPLEX_UNIT_PX, unselectedSize);
		}
	};

	public void hideAllLookMask() {
		if (allLookMask.getVisibility() == View.VISIBLE) {
			allLookMask.setVisibility(View.INVISIBLE);
		}
		if (globalMask.getVisibility() != View.VISIBLE) {
			globalMask.setVisibility(View.VISIBLE);
		}

	}

	public void showAllLookMask() {
		if (allLookMask.getVisibility() != View.VISIBLE) {
			allLookMask.setVisibility(View.VISIBLE);
		}
		if (globalMask.getVisibility() == View.VISIBLE) {
			globalMask.setVisibility(View.INVISIBLE);
		}
	}

	public void updatePlayInfo(int position, int seekPosition) throws RemoteException {
		if (detail == null) {
			return;
		}
		List<Source> sources = detail.sources;
		if (detail.type == Detail.FILM_TYPE) {
			if (button2.getChildCount() == 1) {
				TextView textview = (TextView) button2.getChildAt(0);
				if (seekPosition == 0) {
					textview.setText(mResources.getString(R.string.play));
				} else {
					textview.setText(mResources.getString(R.string.continue_play));
				}
			} else {
				ImageView iconImg = (ImageView) button2.getChildAt(0);
				TextView iconText = (TextView) button2.getChildAt(1);
				LinearLayout.LayoutParams textParams = (LinearLayout.LayoutParams) iconText.getLayoutParams();
				if (seekPosition == 0) {
					iconImg.setVisibility(View.VISIBLE);
					textParams.leftMargin = (int) mResources.getDimension(R.dimen.details_button_icon_drawable_padding);
					iconText.setText(mResources.getString(R.string.play));
				} else {
					// 继续播放
					iconImg.setVisibility(View.GONE);
					textParams.leftMargin = 0;
					iconText.setText(mResources.getString(R.string.continue_play));
				}
			}
		} else {
			clickListener.hidePopWindow();
			TextView text = (TextView) button2.getChildAt(0);
			if (seekPosition > 0) {
				text.setText(mResources.getString(R.string.episode_continue_play));
			} else {
				int nextPosition = position + 1;//
				if (nextPosition <= sources.size() - 1) {
					position = nextPosition;
					text.setText("第" + (nextPosition + 1) + "集");
				} else {
					position = 0;
					text.setText("第1集");
				}
			}
		}
		detail.seekPosition = seekPosition;
		detail.position = position;
		contentService.addHistory(detail);
		clickListener.setPosition(position);
		clickListener.setSeekPosition(seekPosition);
	}

	public void showPromptDialog() {
		ab = new AlertDialog.Builder(context);
		ab.setCancelable(false);
		ab.setMessage(promptMsg);
		ab.setNeutralButton("确定", this);
		ab.create();
		ab.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
		DetailsActivity activity = (DetailsActivity) context;
		activity.finish();
	}

}
