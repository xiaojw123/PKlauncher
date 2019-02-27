package com.pkit.launcher.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.activity.DetailsActivity;
import com.pkit.launcher.activity.PlayerActivity;
import com.pkit.launcher.activity.WebActivity;
import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.service.aidl.Item;
import com.pkit.launcher.service.aidl.Source;
import com.pkit.launcher.utils.APPLog;
import com.pkit.launcher.view.MediaPlayerCtrl.PlayerCtrHander;
import com.pkit.launcher.view.adapter.ActorAdaper;
import com.pkit.launcher.view.listener.PlayerExitBtnFocusListener;
import com.pkit.utils.DrawableUtil;

public class PlayQuitDiloag extends AlertDialog implements View.OnClickListener {
	private static final float ZOOM_OUT = 1.0f;
	private static final float ZOOM_IN = 1.11f;
	protected Resources mResources;
	protected String name;
	protected int type;
	protected PlayerCtrHander handler;
	protected LayoutInflater inflater;
	protected View dialogView;
	private ActorListViewWrapper actor_lv;
	private View lastActor;
	protected Context context;
	protected MediaPlayerCtrl ctrl;
	private List<String> actors;
	protected List<Source> sources;
	protected int position;
	private ArrayList<Content> recommends;
	protected int width, height, left, spacing;
	protected float selectedSize, unselectedSize;
	protected int selectedLeftPad, unselectedLeftPad;
	protected int selectedBottomPad, unselectedBottomPad;
	protected int btn1TopMargin, btn2TopMargin, btn3TopMargin;

	public PlayQuitDiloag(Context context, String name, int type) {
		super(context, R.style.wrong_dialog);
		this.name = name;
		this.type = type;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public void setHandler(PlayerCtrHander handler) {
		this.handler = handler;
	}

	public void setCtrl(MediaPlayerCtrl ctrl) {
		this.ctrl = ctrl;
	}

	public void setActorList(List<String> actors) {
		this.actors = actors;
	}

	public void setMediaInfo(List<Source> sources, int position) {
		this.sources = sources;
		this.position = position;
	}

	public void setRecommends(ArrayList<Content> recommends) {
		this.recommends = recommends;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addContentView(dialogView, params);
		Window window = getWindow();
		// window.setGravity(Gravity.FILL);
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}

	private void init() {
		initParams();
		initContentView();
	}

	private void initParams() {
		mResources = context.getResources();
		left = (int) mResources.getDimension(R.dimen.player_exit_recommend_left_bounder);
		spacing = (int) mResources.getDimension(R.dimen.player_exit_recommend_spacing);
		width = (int) mResources.getDimension(R.dimen.player_exit_recommend_item_width);
		height = (int) mResources.getDimension(R.dimen.player_exit_recommend_item_height);
		selectedSize = mResources.getDimension(R.dimen.text_size_18_sp);
		unselectedSize = mResources.getDimension(R.dimen.text_size_15_sp);
		selectedLeftPad = (int) mResources.getDimension(R.dimen.player_exit_recommend_name_selected_left_padding);
		selectedBottomPad = (int) mResources.getDimension(R.dimen.player_exit_recommend_name_selected_bottom_padding);
		unselectedLeftPad = (int) mResources.getDimension(R.dimen.player_exit_recommend_name_unselected_left_padding);
		unselectedBottomPad = (int) mResources.getDimension(R.dimen.player_exit_recommend_name_unselected_bottom_padding);
		btn1TopMargin = (int) mResources.getDimension(R.dimen.player_exit_btn1_top_margin);
		btn2TopMargin = (int) mResources.getDimension(R.dimen.player_exit_btn2_top_margin);
		btn3TopMargin = (int) mResources.getDimension(R.dimen.player_exit_btn3_top_margin);
	}

	protected void initContentView() {
		APPLog.printInfo("播放退出initContentView=====");
		if (type==PlayerActivity.SINGLE_FILM_TYPE) {
			// 主动退出-单集
			initPlayQuitSingleSetView();
		} else if (type==PlayerActivity.MULTI_FILM_TYPE) {
			// 主动退出-多集
			initPlayQuitMultiSetView();
		}
		initActorListView();
		initRecommendList();
	}

	// 焦点效果待测验，点击事件待完善，播放缓冲需完善
	protected void initActorListView() {
		APPLog.printDebug("initActorListView====");
		if (actors == null || actors.size() < 1) {
			return;
		}
		ImageView arrowIcon = (ImageView) dialogView.findViewById(R.id.player_exit_arrow_down_icon);
		actor_lv = (ActorListViewWrapper) dialogView.findViewById(R.id.player_exit_recommend_actors_listview);
		if (actors.size() > 6) {
			arrowIcon.setVisibility(View.VISIBLE);
		}
		ActorAdaper adapter = new ActorAdaper(context, actors);
		actor_lv.setAdapter(adapter);
		actor_lv.setNextFocusUpId(R.id.player_exit_recommend_actors_listview);
		actor_lv.setNextFocusDownId(R.id.player_exit_recommend_actors_listview);
		actor_lv.setOnItemClickListener(actorItemClickListener);
		actor_lv.setOnItemSelectedListener(actorItemSelectedListener);
	}

	protected void initRecommendList() {
		if (recommends == null || recommends.size() < 1) {
			return;
		}
		RelativeLayout recommendContainer = (RelativeLayout) dialogView.findViewById(R.id.player_exit_recommend_container);
		List<Content> recommendlist = new ArrayList<Content>();
		if (recommends.size() > 3) {
			recommendlist.add(recommends.get(0));
			recommendlist.add(recommends.get(1));
			recommendlist.add(recommends.get(2));
		} else {
			recommendlist = recommends;
		}
		for (int i = 0; i < recommendlist.size(); i++) {
			Item item = (Item) recommendlist.get(i);
			View recommendView = getView(item);
			recommendView.setId(i);
			recommendView.setTag(item.contentID);
			recommendView.setNextFocusUpId(recommendView.getId());
			recommendView.setNextFocusDownId(recommendView.getId());
			if (i == 0) {
				if(actor_lv!=null){
					recommendView.setNextFocusLeftId(actor_lv.getId());
				}
			}
			recommendView.setOnClickListener(recommendClickListener);
			recommendView.setOnFocusChangeListener(recommendFocusListener);
			recommendContainer.addView(recommendView);
		}
	}

	// 播放退出--单集
	@SuppressLint("InflateParams")
	private void initPlayQuitSingleSetView() {
		dialogView = inflater.inflate(R.layout.play_active_exit_single_set_layout, null);
		TextView name_tv = (TextView) dialogView.findViewById(R.id.play_active_exit_single_set_movice_name_textview);
		Button finishWatch_btn = (Button) dialogView.findViewById(R.id.play_active_exit_single_set_finish_watch_btn);
		Button continueWatch_btn = (Button) dialogView.findViewById(R.id.play_active_exit_single_set_continue_watch_btn);
		name_tv.setText(name);
		finishWatch_btn.setOnClickListener(this);
		continueWatch_btn.setOnClickListener(this);
		PlayerExitBtnFocusListener btnFocusListener = new PlayerExitBtnFocusListener(handler);
		finishWatch_btn.setOnFocusChangeListener(btnFocusListener);
		continueWatch_btn.setOnFocusChangeListener(btnFocusListener);
		finishWatch_btn.setNextFocusRightId(R.id.player_exit_recommend_actors_listview);
		continueWatch_btn.setNextFocusRightId(R.id.player_exit_recommend_actors_listview);
		finishWatch_btn.requestFocus();
	}

	// 播放退出--多集
	@SuppressLint("InflateParams")
	private void initPlayQuitMultiSetView() {
		dialogView = inflater.inflate(R.layout.play_active_exit_multi_set_layout, null);
		TextView name_tv = (TextView) dialogView.findViewById(R.id.play_active_exit_multi_set_episode_name);
		Button preSet__btn = (Button) dialogView.findViewById(R.id.play_active_exit_multi_set_pre_set_btn);
		Button nextSet__btn = (Button) dialogView.findViewById(R.id.play_active_exit_multi_set_next_set_btn);
		Button finishWatch_btn = (Button) dialogView.findViewById(R.id.play_active_exit_multi_set_finish_watch);
		Button continueWatch__btn = (Button) dialogView.findViewById(R.id.play_active_exit_multi_set_continue_watch);
		LayoutParams pre_params = (LayoutParams) preSet__btn.getLayoutParams();
		LayoutParams next_params = (LayoutParams) nextSet__btn.getLayoutParams();
		LayoutParams finish_params = (LayoutParams) finishWatch_btn.getLayoutParams();
		LayoutParams continue_params = (LayoutParams) continueWatch__btn.getLayoutParams();
		name_tv.setText(name);
		if (position == 0) {
			nextSet__btn.setVisibility(View.VISIBLE);
			nextSet__btn.setNextFocusUpId(nextSet__btn.getId());
			nextSet__btn.setNextFocusDownId(finishWatch_btn.getId());
			nextSet__btn.setNextFocusRightId(R.id.player_exit_recommend_actors_listview);
			next_params.topMargin = btn1TopMargin;
			finish_params.topMargin = btn2TopMargin;
			continue_params.topMargin = btn3TopMargin;
		} else if (position == sources.size() - 1) {
			preSet__btn.setVisibility(View.VISIBLE);
			preSet__btn.setNextFocusUpId(nextSet__btn.getId());
			preSet__btn.setNextFocusDownId(finishWatch_btn.getId());
			preSet__btn.setNextFocusRightId(R.id.player_exit_recommend_actors_listview);
			pre_params.topMargin = btn1TopMargin;
			finish_params.topMargin = btn2TopMargin;
			continue_params.topMargin = btn3TopMargin;
		} else {
			preSet__btn.setVisibility(View.VISIBLE);
			nextSet__btn.setVisibility(View.VISIBLE);
			preSet__btn.setNextFocusDownId(nextSet__btn.getId());
			preSet__btn.setNextFocusRightId(R.id.player_exit_recommend_actors_listview);
			nextSet__btn.setNextFocusRightId(R.id.player_exit_recommend_actors_listview);
			nextSet__btn.setNextFocusUpId(preSet__btn.getId());
			nextSet__btn.setNextFocusDownId(finishWatch_btn.getId());
		}
		finishWatch_btn.setNextFocusRightId(R.id.player_exit_recommend_actors_listview);
		continueWatch__btn.setNextFocusRightId(R.id.player_exit_recommend_actors_listview);
		finishWatch_btn.setNextFocusDownId(continueWatch__btn.getId());
		continueWatch__btn.setNextFocusDownId(continueWatch__btn.getId());
		preSet__btn.setOnClickListener(this);
		nextSet__btn.setOnClickListener(this);
		finishWatch_btn.setOnClickListener(this);
		continueWatch__btn.setOnClickListener(this);
		PlayerExitBtnFocusListener btnFocusListener = new PlayerExitBtnFocusListener(handler);
		preSet__btn.setOnFocusChangeListener(btnFocusListener);
		nextSet__btn.setOnFocusChangeListener(btnFocusListener);
		finishWatch_btn.setOnFocusChangeListener(btnFocusListener);
		continueWatch__btn.setOnFocusChangeListener(btnFocusListener);
		finishWatch_btn.requestFocus();
	}

	@SuppressLint("InflateParams")
	private View getView(Item item) {
		View view = inflater.inflate(R.layout.player_exit_recommend_item_layout, null);
		LayoutParams params = new LayoutParams(width, height);
		params.leftMargin = left;
		left += (width - spacing);
		view.setLayoutParams(params);
		view.setFocusable(true);
		TextView name = (TextView) view.findViewById(R.id.player_exit_recommend_name);
		ImageView poster = (ImageView) view.findViewById(R.id.player_exit_recommend_poster);
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

	public void setRecommendLayoutParams(LayoutParams params, Item item, View view) {
		params.leftMargin = left;
		left += (width - spacing);
		TextView name = (TextView) view.findViewById(R.id.player_exit_recommend_name);
		ImageView poster = (ImageView) view.findViewById(R.id.player_exit_recommend_poster);
		if (item == null) {
			poster.setBackgroundResource(R.drawable.media_browser_item_loading);
			return;
		}
		String url = null;
		if (item.imgPaths != null && item.imgPaths.size() > 0) {
			url = item.imgPaths.get(0);
		}
		DrawableUtil.displayDrawable(url, R.drawable.media_browser_item_loading, poster, 0);
		name.setText(item.name);
	}

	private View.OnClickListener recommendClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// 跳转到详情页
			dismiss();
			String contentID=(String) v.getTag();
			updateDetailsInfo(contentID);
		}
	};
	public void updateDetailsInfo(String contentID){
		Intent intent = new Intent((PlayerActivity) context, DetailsActivity.class);
		intent.putExtra(DetailsActivity.CONTENT_ID, contentID);
		intent.putExtra(PlayerActivity.MEDIA_POSITION, position);
		intent.putExtra(PlayerActivity.MEDIA_SEEK_POSITION, ctrl.getCurrentSeekPosion());
		((PlayerActivity) context).setResult(DetailsActivity.FINISH, intent);
		((PlayerActivity) context).startActivity(intent);
		((PlayerActivity) context).finish();
	}
	private OnFocusChangeListener recommendFocusListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			RelativeLayout recommendItem = (RelativeLayout) v;
			ImageView fousImg = (ImageView) recommendItem.getChildAt(1);
			TextView name = (TextView) recommendItem.getChildAt(2);
			View view = (View) v.getParent();
			if (hasFocus) {
				fousImg.setSelected(true);
				setLightName(name);
				zoomIn(v);
				v.bringToFront();
				view.postInvalidate();
			} else {
				fousImg.setSelected(false);
				setDullName(name);
				zoomOut(v);
			}
		}

		private void setLightName(TextView name) {
			name.setSelected(true);
			name.setPadding(selectedLeftPad, 0, 0, selectedBottomPad);
			// name.setTextSize(TypedValue.COMPLEX_UNIT_PX, selectedSize);
		}

		private void setDullName(TextView name) {
			name.setSelected(false);
			name.setPadding(unselectedLeftPad, 0, 0, unselectedBottomPad);
			// name.setTextSize(TypedValue.COMPLEX_UNIT_PX, unselectedSize);
		}

		private void zoomIn(View v) {
			v.setScaleX(ZOOM_IN);
			v.setScaleY(ZOOM_IN);
		}

		private void zoomOut(View v) {
			v.setScaleX(ZOOM_OUT);
			v.setScaleY(ZOOM_OUT);
		}
	};
	private OnItemClickListener actorItemClickListener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		     dismiss();
		     ctrl.release();
			Intent intent = new Intent((PlayerActivity)context, WebActivity.class);
            intent.putExtra(WebActivity.WEB_URL,"http://180.169.102.150:7070/lweb/topic/page/figure.html");
			// 人物-专题url
            context.startActivity(intent);
            finishPlayer();
		}
	};
	protected void finishPlayer(){
		
	}
	private OnItemSelectedListener actorItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			APPLog.printDebug("onItemSelected----hasFocus====" + parent.hasFocus());
			boolean hasFocus = parent.hasFocus();
			if (!hasFocus) {
				return;
			}
			if (lastActor == null) {
				lastActor = actor_lv.lastItem;
			}
			unSelected(lastActor);
			selected(view);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			APPLog.printDebug("onNothingSelected======");
			unSelected(lastActor);
		}

		private void selected(View view) {
			APPLog.printDebug("selected====");
			TextView selectedView = findTextViewFromParent(view);
			if (selectedView == null) {
				return;
			}
			selectedView.setTextColor(mResources.getColor(R.color.color_00cf55));
			selectedView.setScaleX(1.08f);
			selectedView.setScaleY(1.08f);
			lastActor = view;
		}

		private void unSelected(View view) {
			APPLog.printDebug("unSelected====");
			TextView unSelectedView = findTextViewFromParent(view);
			APPLog.printDebug("unSelectedView=====" + unSelectedView);
			if (unSelectedView == null) {
				return;
			}
			unSelectedView.setTextColor(mResources.getColor(R.color.color_bfbfbf));
			unSelectedView.setScaleX(1.00f);
			unSelectedView.setScaleY(1.00f);
		}

		private TextView findTextViewFromParent(View view) {
			TextView textView = null;
			if (view != null) {
				if (view instanceof ViewGroup) {
					ViewGroup viewGroup = (ViewGroup) view;
					textView = (TextView) viewGroup.getChildAt(0);
				} else {
					textView = (TextView) view;
				}
			}
			return textView;
		}
	};

	@Override
	public void onClick(View v) {
		dismiss();
		int id = v.getId();
		switch (id) {
		case R.id.play_active_exit_single_set_continue_watch_btn:
			ctrl.continuePlay();
			break;
		case R.id.play_active_exit_multi_set_pre_set_btn:
			try {
				ctrl.playPreSet();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;
		case R.id.play_active_exit_multi_set_next_set_btn:
			try {
				ctrl.playNextSet();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.play_active_exit_multi_set_continue_watch:
			ctrl.continuePlay();
			break;
		case R.id.play_active_exit_single_set_finish_watch_btn:
		case R.id.play_active_exit_multi_set_finish_watch:
			ctrl.finishActivityForQuit();
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dismiss();
			ctrl.continuePlay();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
