package com.pkit.launcher.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import com.pkit.launcher.R;
import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.service.aidl.Detail;
import com.pkit.launcher.service.aidl.Source;
import com.pkit.launcher.view.MediaPlayerCtrl;
import com.pkit.launcher.view.VolumeSeekBar;

public class PlayerActivity extends Activity implements Callback, OnClickListener {
	public static final String MEDIA_DETAILS = "media_details";
	public static final String MEDIA_SEEK_POSITION = "media_seek_positon";
	public static final String MEDIA_POSITION = "media_position";
	public static final String MEDIA_RECOMMENDS = "media_recommends";
	public static final int SINGLE_FILM_TYPE = 0x00;
	public static final int MULTI_FILM_TYPE = 0x01;
	private RelativeLayout playerWindow;
	private SurfaceHolder holder;
	private MediaPlayer mediaPlayer;
	private MediaPlayerCtrl control;
	private View player_volume_view;
	private VolumeSeekBar player_volume_skb;
	protected AudioManager audioManager;
	protected int maxVolume;
	protected int curVolume;
	protected int stepVolume;
	public int lastSeekPosition;
	public int lastPosition;
	public boolean isRestarted;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_layout);
		init();
	}

	private void init() {
		initVolume();
		playerWindow = (RelativeLayout) findViewById(R.id.player_container);
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.player_sv);
		holder = surfaceView.getHolder();
		holder.setKeepScreenOn(true);
		holder.addCallback(this);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (!isRestarted) {
			isRestarted = true;
		}
	}

	// callback
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (control != null) {
			control.release();
		}
	}

	private void play() throws Exception {
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setDisplay(holder);
		Detail detail = getDetails();
		ArrayList<Content> recommends = getRecommendList();
		int seekPosition = getSeekPosition();
		int position = getPosition();
		seekPosition = isRestarted ? lastSeekPosition : seekPosition;
		position = isRestarted ? lastPosition : position;
		if (detail != null) {
			List<Source> sources = detail.sources;
			if (sources != null && position < sources.size()) {
				control = new MediaPlayerCtrl(this, player_volume_view);
				control.setPlayer(mediaPlayer);
				control.setParentView(playerWindow);
				if (sources.size() == 1) {
					control.setPlayType(SINGLE_FILM_TYPE);
				} else {
					control.setPlayType(MULTI_FILM_TYPE);
				}
				control.setMedaSoruce(sources);
				control.setPosition(position);
				control.setSeekPosition(seekPosition);
				control.setDetails(detail);
				control.setRecommends(recommends);
				control.prepareMediaSource(sources.get(position));
			} else {
				showErrorDialog(getResources().getString(R.string.no_sources));
			}
		}
	}

	public void ctrlPlayerVoulme(int keyCode, KeyEvent event) {
		control.showVoumeView();
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			increaseVolume();
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			decreaseVolume();
		}
	}

	// 初始化音量
	protected void initVolume() {
		player_volume_view = findViewById(R.id.player_volume_view);
		player_volume_skb = (VolumeSeekBar) findViewById(R.id.player_volume_skb);
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		player_volume_skb.setMax(maxVolume);
		curVolume = maxVolume / 2;
		stepVolume = maxVolume / 6;
	}

	// 音量+
	protected void increaseVolume() {
		curVolume += stepVolume;
		if (curVolume >= maxVolume) {
			curVolume = maxVolume;
		}
		player_volume_skb.setProgress(curVolume);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, AudioManager.FLAG_PLAY_SOUND);
	}

	// 音量-
	protected void decreaseVolume() {
		curVolume -= stepVolume;
		if (curVolume <= 0) {
			curVolume = 0;
		}
		player_volume_skb.setProgress(curVolume);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, AudioManager.FLAG_PLAY_SOUND);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (control == null || control.status == MediaPlayerCtrl.Status.INIT) {
			return super.dispatchKeyEvent(event);
		}
		int keyCode = event.getKeyCode();
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				lastSeekPosition = control.getCurrentSeekPosion();
				lastPosition = control.getCurrentPosition();
				control.showPlayQuitDialog();
			}
			return true;
		} else {
			switch (keyCode) {
			case KeyEvent.KEYCODE_ENTER:
			case KeyEvent.KEYCODE_DPAD_RIGHT:
			case KeyEvent.KEYCODE_DPAD_LEFT:
				try {
					control.ctrl(keyCode, event);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case KeyEvent.KEYCODE_VOLUME_UP:
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				ctrlPlayerVoulme(keyCode, event);
				break;
			default:
				control.holdCtrlView(false);
				break;
			}
			return true;
		}
	}

	private Detail getDetails() {
		return (Detail) getIntent().getParcelableExtra(MEDIA_DETAILS);
	}

	private int getSeekPosition() {
		return getIntent().getIntExtra(MEDIA_SEEK_POSITION, 0);
	}

	private int getPosition() {
		return getIntent().getIntExtra(MEDIA_POSITION, 0);
	}

	private ArrayList<Content> getRecommendList() {
		return getIntent().getParcelableArrayListExtra(MEDIA_RECOMMENDS);
	}

	public void showErrorDialog(String text) {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setMessage(text);
		ab.setNeutralButton("确定", this);
		ab.setCancelable(false);
		ab.create();
		ab.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
		finish();
	}

}
