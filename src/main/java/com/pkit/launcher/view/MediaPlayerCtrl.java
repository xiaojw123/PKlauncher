package com.pkit.launcher.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.activity.DetailsActivity;
import com.pkit.launcher.activity.PlayerActivity;
import com.pkit.launcher.animation.LoadHelper;
import com.pkit.launcher.common.Configuration;
import com.pkit.launcher.service.LoggerRecordService;
import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.service.aidl.Detail;
import com.pkit.launcher.service.aidl.Source;
import com.pkit.launcher.utils.APPLog;
import com.pkit.launcher.view.PlayEndDiloag.PlayEndCountDownTimer;

public class MediaPlayerCtrl implements OnPreparedListener, OnSeekCompleteListener, OnInfoListener, OnErrorListener, OnCompletionListener {
	protected static final String INIT_TIME_LEN = "00:00:00";
	protected static final String PLAY_TIME_FORMAT = "%1$s:%2$s:%3$s";
	protected static final String SYSTEM_TIME_FORMAT = "%1$s-%2$s-%3$s  %4$s:%5$s:%6$s";
	protected static final int AD_TIME = 10 * 1000;
	protected static final int PERIOD = 1 * 1000;
	protected static final int SHOW_TIME = 5 * 1000;
	protected static final int INIT_TIME = 8 * 1000;
	protected static final int SHOW_VOLUME_TIME = 5 * 1000;
	protected static final int CLOCK_PERIOD = 60 * 1000;
	protected static final String MUTIL_SET = "multi_set";
	protected static final String SINGLE_SET = "single_set";
	public int status;
	private int playType;
	protected int velocity = 5 * 1000;
	protected int currentSeekPos;
	protected int currentPosition;
	protected List<String> actors;
	protected ArrayList<Content> recommends;
	protected MediaPlayer player;
	protected Context context;
	protected LayoutInflater inflater;
	protected PlayerCtrHander handler;
	protected Timer progressTimer;
	protected Timer clockTimer;
	protected Timer adCountDownTimer;
	protected RestartPlayCountTimer restartTimer;
	protected PlayQuitDiloag showingDialog;
	protected View ctrlView;
	protected View volumeView;
	protected TextView player_hight_definition_tv;
	protected LinearLayout player_top_tips_llt;
	protected TextView player_clock_time_tv;
	protected TextView mediaName_tv;
	protected View player_loading_view;
	protected ImageView player_loading_img;
	protected ImageView player_status_img;
	protected ImageView player_progressbar_icon_img;
	protected LinearLayout player_play_from_beginning_llt;
	protected TextView player_play_from_beginning_count_down_tv;
	protected SeekBar mediaProgress_skb;
	protected TextView mediaCurPos_tv;
	protected TextView mediaLen_tv;
	protected RelativeLayout playerWindow;
	protected Source source;
	protected Resources resources;
	protected List<Source> sources;
	protected LoadHelper loadHelper;
	protected boolean isWaitingRestart = false;
	private String startTime;
	private String endTime;
	private String contentId;

	public MediaPlayerCtrl(Context context, View volumeView) {
		this.context = context;
		this.volumeView = volumeView;
		init();
	}

	protected void init() {
		initCtrView();
		handler = new PlayerCtrHander();
		resources = context.getResources();
	}

	@SuppressLint("InflateParams")
	protected void initCtrView() {
		inflater = LayoutInflater.from(context);
		ctrlView = inflater.inflate(R.layout.player_ctrl_layout, null);
		mediaName_tv = (TextView) ctrlView.findViewById(R.id.player_ctrl_name_textview);
		player_hight_definition_tv = (TextView) ctrlView.findViewById(R.id.player_ctrl_high_definition_textview);
		player_top_tips_llt = (LinearLayout) ctrlView.findViewById(R.id.player_ctrl_top_tips_llt);
		player_clock_time_tv = (TextView) ctrlView.findViewById(R.id.player_ctrl_current_time);
		player_loading_view = ctrlView.findViewById(R.id.loading_view);
		player_loading_img = (ImageView) ctrlView.findViewById(R.id.loading_img);
		player_status_img = (ImageView) ctrlView.findViewById(R.id.player_ctrl_status_imgView);
		player_progressbar_icon_img = (ImageView) ctrlView.findViewById(R.id.player_play_progressbar_icon_img);
		player_play_from_beginning_llt = (LinearLayout) ctrlView.findViewById(R.id.player_ctrl_play_from_beginning_llt);
		player_play_from_beginning_count_down_tv = (TextView) ctrlView.findViewById(R.id.player_ctrl_play_from_beginning_count_down_textview);
		mediaProgress_skb = (SeekBar) ctrlView.findViewById(R.id.player_ctrl_playback_process_skb);
		mediaCurPos_tv = (TextView) ctrlView.findViewById(R.id.player_ctrl_playback_time_textview);
		mediaLen_tv = (TextView) ctrlView.findViewById(R.id.player_ctrl_playback_duration_textview);
		loadHelper = new LoadHelper(context, player_loading_view, player_loading_img);
		AssetManager manager = context.getAssets();
		Typeface tf = Typeface.createFromAsset(manager, "HelveticaNeueLTPro-ThEx.otf");
		player_clock_time_tv.setTypeface(tf);
		ctrlView.setFocusable(true);
		mediaCurPos_tv.setText(INIT_TIME_LEN);
		mediaLen_tv.setText(INIT_TIME_LEN);
		mediaProgress_skb.setProgress(0);
		mediaProgress_skb.setMax(100);
	}

	public void setPlayer(MediaPlayer player) {
		this.player = player;
		initPlayer();
	}

	protected void initPlayer() {
		if (player == null) {
			return;
		}
		player.setOnCompletionListener(this);
		player.setOnErrorListener(this);
		player.setOnInfoListener(this);
		player.setOnPreparedListener(this);
		player.setOnSeekCompleteListener(this);
	}

	public void setParentView(ViewGroup parent) {
		playerWindow = (RelativeLayout) parent;
		if (parent != null && parent != ctrlView.getParent()) {
			parent.addView(ctrlView);
		}
	}

	public void showVoumeView() {
		if (volumeView.getVisibility() != View.VISIBLE) {
			volumeView.setVisibility(View.VISIBLE);
		}
		if (handler.hasMessages(CtrlMessage.HIDE_VOLUME)) {
			handler.removeMessages(CtrlMessage.HIDE_VOLUME);
		}
		handler.sendEmptyMessageDelayed(CtrlMessage.HIDE_VOLUME, SHOW_VOLUME_TIME);
	}

	public void setPlayType(int playType) {
		this.playType = playType;
	}

	public void setMedaSoruce(List<Source> sources) {
		this.sources = sources;
	}

	public void setPosition(int position) {
		currentPosition = position;
	}

	public void setSeekPosition(int seekPosition) {
		currentSeekPos = seekPosition;
	}

	public void setDetails(Detail detail) {
		actors = detail.actors;
		contentId = detail.contentID;
	}

	public void setRecommends(ArrayList<Content> recommends) {
		this.recommends = recommends;
	}

	public void prepareMediaSource(Source source) throws Exception {
		if (player == null) {
			return;
		}
		this.source = source;
		initCurrentView();
		player.reset();
		player.setDataSource(source.url);
		player.prepareAsync();
		status = Status.INIT;
	}

	private void initCurrentView() {
		holdCtrlView(true);
		startLoading();
		updateProgress(null);
		if (player_status_img.getVisibility() == View.VISIBLE) {
			player_status_img.setVisibility(View.GONE);
		}
		if (player_progressbar_icon_img.getBackground() != resources.getDrawable(R.drawable.player_progressbar_icon_stop)) {
			player_progressbar_icon_img.setBackgroundResource(R.drawable.player_progressbar_icon_stop);
		}
		mediaProgress_skb.setProgress(0);
		mediaName_tv.setText(source.name);
	}

	// player PreparedListener
	@Override
	public void onPrepared(MediaPlayer mp) {
		if (player == null) {
			return;
		}
		int len = player.getDuration();
		mediaCurPos_tv.setText(formartTime(currentSeekPos));
		mediaLen_tv.setText(formartTime(len));
		mediaProgress_skb.setMax(len);
		mediaProgress_skb.setProgress(currentSeekPos);
		velocity = len / 100;
		seekTo(currentSeekPos);
		startTime = getCurrentSystmeTime();
	}

	public String getCurrentSystmeTime() {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		month += 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		int h = c.get(Calendar.HOUR_OF_DAY);
		int m = c.get(Calendar.MINUTE);
		int s = c.get(Calendar.SECOND);
		String yearStr = "" + year;
		String monthStr = month < 10 ? "0" + month : "" + month;
		String dayStr = day < 10 ? "0" + day : "" + day;
		String hStr = h < 10 ? "0" + h : "" + h;
		String mStr = m < 10 ? "0" + m : "" + m;
		String sStr = s < 10 ? "0" + s : "" + s;
		return String.format(SYSTEM_TIME_FORMAT, yearStr, monthStr, dayStr, hStr, mStr, sStr);
	}

	public void holdCtrlView(boolean flag) {
		if (ctrlView.getVisibility() != View.VISIBLE) {
			ctrlView.setVisibility(View.VISIBLE);
		}
		if (clockTimer != null) {
			clockTimer.cancel();
			clockTimer = null;
		}
		SystemClockUpdateTask clockTask = new SystemClockUpdateTask(handler);
		clockTimer = new Timer();
		clockTimer.schedule(clockTask, 0, CLOCK_PERIOD);
		if (handler.hasMessages(CtrlMessage.HIDE_CTRL_VIEW)) {
			handler.removeMessages(CtrlMessage.HIDE_CTRL_VIEW);
		}
		if (!flag) {
			handler.sendEmptyMessageDelayed(CtrlMessage.HIDE_CTRL_VIEW, SHOW_TIME);
		}
	}

	protected void updateProgress(TimerTask task) {
		if (progressTimer != null) {
			progressTimer.cancel();
			progressTimer = null;
		}
		if (task != null) {
			progressTimer = new Timer();
			progressTimer.scheduleAtFixedRate(task, 0, PERIOD);
		}
	}

	protected void restartPlayCountDown() {
		if (handler.hasMessages(CtrlMessage.HIDE_CTRL_VIEW)) {
			handler.removeMessages(CtrlMessage.HIDE_CTRL_VIEW);
		}
		if (!isWaitingRestart) {
			isWaitingRestart = true;
		}
		if (ctrlView.getVisibility() != View.VISIBLE) {
			ctrlView.setVisibility(View.VISIBLE);
		}
		ctrlView.setBackgroundResource(R.color.color_80000000);
		if (player_play_from_beginning_llt.getVisibility() != View.VISIBLE) {
			player_play_from_beginning_llt.setVisibility(View.VISIBLE);
		}
		restartTimer = new RestartPlayCountTimer(SHOW_TIME, PERIOD);
		restartTimer.start();
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		if (currentSeekPos > 0) {
			currentSeekPos = 0;
			restartPlayCountDown();
		}
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
			APPLog.printDebug("MEDIA_INFO_BUFFERING_START=====");
			if (!isShowingDialog()) {
				holdCtrlView(true);
			}
			updateProgress(null);
			startLoading();
		} else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
			APPLog.printDebug("MEDIA_INFO_BUFFERING_END=====");
			if (!isShowingDialog()) {
				holdCtrlView(false);
			}
			ProgressUpdateTask task = new ProgressUpdateTask(handler);
			updateProgress(task);
			stopLoading();
		}
		return true;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		return true;
	}

	// player CompletionListener
	@Override
	public void onCompletion(MediaPlayer mp) {
		APPLog.printInfo("onCompletion======");
		stop();
		showPlayEndDialog();
	}

	private String formartTime(int mesc) {
		mesc /= 1000;
		int hour = mesc / 3600;
		int minute = (mesc - 3600 * hour) / 60;
		int second = (mesc - 3600 * hour - 60 * minute);
		String str1 = hour < 10 ? String.valueOf("0" + hour) : String.valueOf(hour);
		String str2 = minute < 10 ? String.valueOf("0" + minute) : String.valueOf(minute);
		String str3 = second < 10 ? String.valueOf("0" + second) : String.valueOf(second);
		return String.format(PLAY_TIME_FORMAT, str1, str2, str3);
	}

	private void updateReStartView() {
		if (!isWaitingRestart) {
			return;
		}
		player_play_from_beginning_llt.setVisibility(View.GONE);
		ctrlView.setBackgroundResource(android.R.color.transparent);
		if (ctrlView.getVisibility() == View.VISIBLE) {
			ctrlView.setVisibility(View.GONE);
		}
		isWaitingRestart = false;
	}

	public boolean ctrl(int keyCode, KeyEvent event) throws Exception {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_ENTER) {
				if (isWaitingRestart) {
					updateReStartView();
					if (restartTimer != null) {
						restartTimer.cancel();
						restartTimer = null;
					}
					prepareMediaSource(source);
				} else {
					if (player.isPlaying()) {
						pause();
					} else {
						play();
					}
				}
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				forwardBack();
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				speed();
			}
		} else {
			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				int pos = mediaProgress_skb.getProgress();
				seekTo(pos);
			}
		}
		return true;
	}

	protected void seekTo(int pos) {
		if (player == null) {
			return;
		}
		try {
			play();
			player.seekTo(pos);
		} catch (Exception e) {
			APPLog.printDebug("seek failed=======" + e.getMessage());
			e.printStackTrace();
		}
	}

	protected void play() {
		if (player.isPlaying()) {
			return;
		}
		player.start();
		status = Status.PLAYING;
		stopLoading();
		if (player_status_img.getVisibility() == View.VISIBLE) {
			player_status_img.setVisibility(View.GONE);
		}
		if (player_progressbar_icon_img.getBackground() != resources.getDrawable(R.drawable.player_progressbar_icon_stop)) {
			player_progressbar_icon_img.setBackgroundResource(R.drawable.player_progressbar_icon_stop);
		}
		ProgressUpdateTask task = new ProgressUpdateTask(handler);
		updateProgress(task);
		holdCtrlView(false);
	}

	protected void pause() {
		if (!player.isPlaying()) {
			return;
		}
		player.pause();
		status = Status.PAUSE;
		stopLoading();
		if (player_status_img.getVisibility() != View.VISIBLE) {
			player_status_img.setVisibility(View.VISIBLE);
		}
		if (player_progressbar_icon_img.getBackground() != resources.getDrawable(R.drawable.player_progressbar_icon_play)) {
			player_progressbar_icon_img.setBackgroundResource(R.drawable.player_progressbar_icon_play);
		}
		player_status_img.setBackgroundResource(R.drawable.player_conent_icon_play);
		updateProgress(null);
		holdCtrlView(true);
	}

	// be modified 04/19
	protected void speed() {
		if (player.isPlaying()) {
			player.pause();
		}
		updatePlaybackProcess(velocity);
		status = Status.SPEED;
		stopLoading();
		if (player_status_img.getVisibility() != View.VISIBLE) {
			player_status_img.setVisibility(View.VISIBLE);
		}
		if (player_progressbar_icon_img.getBackground() != resources.getDrawable(R.drawable.player_progressbar_icon_fast)) {
			player_progressbar_icon_img.setBackgroundResource(R.drawable.player_progressbar_icon_fast);
		}
		player_status_img.setBackgroundResource(R.drawable.player_conent_icon_fast);
		updateProgress(null);
		holdCtrlView(true);
	}

	// be modified 04/19
	protected void forwardBack() {
		if (player.isPlaying()) {
			player.pause();
		}
		updatePlaybackProcess(-velocity);
		status = Status.FORWARDBACK;
		stopLoading();
		if (player_status_img.getVisibility() != View.VISIBLE) {
			player_status_img.setVisibility(View.VISIBLE);
		}
		if (player_progressbar_icon_img.getBackground() != resources.getDrawable(R.drawable.player_progressbar_icon_retreat)) {
			player_progressbar_icon_img.setBackgroundResource(R.drawable.player_progressbar_icon_retreat);
		}
		player_status_img.setBackgroundResource(R.drawable.player_conent_icon_retreat);
		updateProgress(null);
		holdCtrlView(true);

	}

	// 播放过程实时更新进度条
	private void updatePlaybackProcess(int value) {
		if (mediaProgress_skb == null) {
			return;
		}
		int process = mediaProgress_skb.getProgress();
		int max = mediaProgress_skb.getMax();
		process += value;
		if (process < 0) {
			process = 0;
		} else if (process > max) {
			process = max;
		}
		mediaProgress_skb.setProgress(process);
		mediaCurPos_tv.setText(formartTime(process));
	}

	public void sendMessage(int message) {
		if (handler.hasMessages(message)) {
			handler.removeMessages(message);
		}
		handler.sendEmptyMessage(message);
	}

	public void stop() {
		if (status == Status.STOP) {
			return;
		}
		cancelTask();
		if (player != null) {
			player.stop();
		}
		status = Status.STOP;
	}

	public void release() {
		if (status == Status.RELEASE) {
			return;
		}
		setWathRcord();
		stopLoading();
		if (showingDialog != null && showingDialog.isShowing()) {
			showingDialog.dismiss();
		}
		if (ctrlView.getVisibility() == View.VISIBLE) {
			ctrlView.setVisibility(View.GONE);
		}
		cancelTask();
		if (player != null) {
			player.release();
			player = null;
		}
		status = Status.RELEASE;
	}

	public void setWathRcord() {
		if (startTime == null) {
			return;
		}
		endTime = getCurrentSystmeTime();
		LoggerRecordService.setWatchRecord(context, contentId, source.id, startTime, endTime);
	}

	public void cancelTask() {
		if (handler.hasMessages(CtrlMessage.HIDE_CTRL_VIEW)) {
			handler.removeMessages(CtrlMessage.HIDE_CTRL_VIEW);
		}
		if (clockTimer != null) {
			clockTimer.cancel();
			clockTimer = null;
		}
		updateProgress(null);
	}

	public void returnDetails() {
		Intent intent = new Intent();
		intent.putExtra(PlayerActivity.MEDIA_POSITION, currentPosition);
		((PlayerActivity) context).setResult(DetailsActivity.UPDATE_DATA, intent);
		((PlayerActivity) context).finish();
	}

	public int getCurrentSeekPosion() {
		return player.getCurrentPosition();
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public void continuePlay() {
		play();
	}

	protected boolean isShowingDialog() {
		if (showingDialog == null) {
			return false;
		} else {
			if (showingDialog.isShowing()) {
				return true;
			} else {
				return false;
			}
		}
	}

	public void playPreSet() throws Exception {
		--currentPosition;
		currentSeekPos = 0;
		prepareMediaSource(sources.get(currentPosition));
	}

	public void playNextSet() throws Exception {
		++currentPosition;
		currentSeekPos = 0;
		prepareMediaSource(sources.get(currentPosition));
	}

	protected void showPlayEndDialog() {
		if (isShowingDialog()) {
			return;
		}
		if (ctrlView.getVisibility() == View.VISIBLE) {
			ctrlView.setVisibility(View.GONE);
		}
		PlayEndDiloag dialog = new PlayEndDiloag(context, source.name, playType);
		showingDialog = dialog;
		dialog.setHandler(handler);
		dialog.setActorList(actors);
		dialog.setRecommends(recommends);
		dialog.setMediaInfo(sources, currentPosition);
		dialog.setCtrl(this);
		dialog.show();
	}

	public void showPlayQuitDialog() {
		if (isShowingDialog()) {
			return;
		}
		if (ctrlView.getVisibility() == View.VISIBLE) {
			ctrlView.setVisibility(View.GONE);
		}
		if (player.isPlaying()) {
			player.pause();
		}
		PlayQuitDiloag dialog = new PlayQuitDiloag(context, source.name, playType);
		showingDialog = dialog;
		dialog.setHandler(handler);
		dialog.setMediaInfo(sources, currentPosition);
		dialog.setActorList(actors);
		APPLog.printDebug("recommends====" + recommends);
		dialog.setRecommends(recommends);
		dialog.setCtrl(this);
		dialog.show();
	}

	public void finishActivityForQuit() {
		Intent intent = new Intent();
		intent.putExtra(PlayerActivity.MEDIA_POSITION, currentPosition);
		intent.putExtra(PlayerActivity.MEDIA_SEEK_POSITION, getCurrentSeekPosion());
		((PlayerActivity) context).setResult(DetailsActivity.UPDATE_DATA, intent);
		((PlayerActivity) context).finish();
	}

	public void finishActvityForEnd() {
		Intent intent = new Intent();
		intent.putExtra(PlayerActivity.MEDIA_POSITION, currentPosition);
		((PlayerActivity) context).setResult(DetailsActivity.UPDATE_DATA, intent);
		((PlayerActivity) context).finish();
	}

	static class ProgressUpdateTask extends TimerTask {
		private Handler handler;

		public ProgressUpdateTask(Handler handler) {
			super();
			this.handler = handler;
		}

		@Override
		public void run() {
			handler.sendEmptyMessage(CtrlMessage.PLAY);
		}
	}

	static class SystemClockUpdateTask extends TimerTask {
		private Handler handler;

		public SystemClockUpdateTask(Handler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			handler.sendEmptyMessage(CtrlMessage.UPDATE_CLOCK);
		}
	}

	class RestartPlayCountTimer extends CountDownTimer {
		public RestartPlayCountTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			player_play_from_beginning_count_down_tv.setText(String.valueOf(millisUntilFinished / 1000));
		}

		@Override
		public void onFinish() {
			updateReStartView();
		}
	}

	@SuppressLint("HandlerLeak")
	class PlayerCtrHander extends Handler {
		private PlayEndCountDownTimer endTimer;
		private LinearLayout endTimerView;

		public void setTimer(PlayEndCountDownTimer endTimer) {
			this.endTimer = endTimer;
		}

		public void setTimerView(LinearLayout endTimerView) {
			this.endTimerView = endTimerView;
		}

		private void updateSystemClock() {
			if (player_clock_time_tv.getVisibility() != View.VISIBLE) {
				player_clock_time_tv.setVisibility(View.VISIBLE);
			}
			Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int mine = c.get(Calendar.MINUTE);
			String h = String.valueOf(hour);
			String m = String.valueOf(mine);
			if (hour < 10) {
				h = "0" + h;
			}
			if (mine < 10) {
				m = "0" + mine;
			}
			player_clock_time_tv.setText(h + ":" + m);
		}

		private void cancelPlayEndTimer() {
			if (endTimerView == null) {
				return;
			}
			if (endTimer != null) {
				endTimer.cancel();
				endTimer = null;
			}
			if (endTimerView.getVisibility() == View.VISIBLE) {
				endTimerView.setVisibility(View.GONE);
			}
		}

		public void handleMessage(Message msg) {
			int code = msg.what;
			switch (code) {
			case CtrlMessage.CANCEL_TIMER:
				cancelPlayEndTimer();
				break;
			case CtrlMessage.PLAY:
				updatePlaybackProcess(1000);
				break;
			case CtrlMessage.HIDE_CTRL_VIEW:
				ctrlView.setVisibility(View.INVISIBLE);
				if (clockTimer != null) {
					clockTimer.cancel();
					clockTimer = null;
				}
				break;
			case CtrlMessage.HIDE_VOLUME:
				volumeView.setVisibility(View.GONE);
				break;
			case CtrlMessage.UPDATE_CLOCK:
				updateSystemClock();
				break;
			}
		}
	}

	public void startLoading() {
		loadHelper.startLoading();
	}

	public void stopLoading() {
		loadHelper.stopLoading();
	}

	public static interface Status {
		int INIT = 0x010;
		int SPEED = 0x011;
		int FORWARDBACK = 0x012;
		int PLAYING = 0x013;
		int PAUSE = 0x014;
		int STOP = 0x015;
		int RELEASE = 0x016;
	}

	public static interface CtrlMessage {
		int HIDE_CTRL_VIEW = 0x01;
		int PLAY = 0x02;
		int COUNT_DOWN = 0x05;
		int HIDE_VOLUME = 0x06;
		int UPDATE_CLOCK = 0x07;
		int CANCEL_TIMER = 0x10;
	}
}
