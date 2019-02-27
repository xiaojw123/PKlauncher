package com.pkit.launcher.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.activity.DetailsActivity;
import com.pkit.launcher.activity.PlayerActivity;
import com.pkit.launcher.view.listener.PlayerExitBtnFocusListener;

public class PlayEndDiloag extends PlayQuitDiloag{
	protected static final int PERIOD = 1000;
	private boolean hasNext = true;
	private boolean isCancel;
	private PlayEndCountDownTimer timer;
	private TextView time_tv;
	LinearLayout time_llt;
	public PlayEndDiloag(Context context, String name, int type) {
		super(context, name, type);
	}
	@Override
	protected void initContentView() {
		if (type==PlayerActivity.SINGLE_FILM_TYPE) {
			initPlayEndSingleSet();
		} else if (type==PlayerActivity.MULTI_FILM_TYPE) {
			initPlayEndMultiSet();
		}
		initActorListView();
		initRecommendList();
		initTimer();
	}

	@SuppressLint("InflateParams")
	private void initPlayEndSingleSet() {
		dialogView = inflater.inflate(R.layout.play_normal_end_single_set_layout, null);
		TextView name_tv = (TextView) dialogView.findViewById(R.id.play_normal_end_single_set_movice_name_textview);
		Button sure_btn = (Button) dialogView.findViewById(R.id.play_normal_end_single_set_sure_btn);
		time_llt = (LinearLayout) dialogView.findViewById(R.id.play_normal_end_single_set_time_llt);
		time_tv=(TextView) dialogView.findViewById(R.id.play_normal_end_single_set_time_number_textview
				);
		PlayerExitBtnFocusListener btnFocusListener = new PlayerExitBtnFocusListener(handler);
		btnFocusListener.setPlayEndType(true);
		sure_btn.setOnFocusChangeListener(btnFocusListener);
		sure_btn.setNextFocusUpId(sure_btn.getId());
		sure_btn.setNextFocusDownId(sure_btn.getId());
		sure_btn.setNextFocusRightId(R.id.player_exit_recommend_actors_listview);
		sure_btn.setOnClickListener(this);
		sure_btn.requestFocus();
		name_tv.setText(name);
		handler.setTimerView(time_llt);
	}

	@SuppressLint("InflateParams")
	private void initPlayEndMultiSet() {
		dialogView = inflater.inflate(R.layout.play_normal_end_multi_set_layout, null);
		TextView name_tv = (TextView) dialogView.findViewById(R.id.play_normal_end_multi_set_episode_name_textview);
		TextView timerText = (TextView) dialogView.findViewById(R.id.play_normal_end_multi_set_play_next_set_text_textview);
		time_tv=(TextView) dialogView.findViewById(R.id.play_normal_end_multi_set_time_number_textview
				);
		time_llt = (LinearLayout) dialogView.findViewById(R.id.play_normal_end_multi_set_time_llt);
		Button nextSet_btn = (Button) dialogView.findViewById(R.id.play_normal_end_multi_set_next_set_btn);
		Button finishWatch_btn = (Button) dialogView.findViewById(R.id.play_normal_end_multi_set_finish_watch);
		name_tv.setText(name);
		int nextPosition=position+1;
		hasNext =(nextPosition<=(sources.size() - 1)) ? true : false;
		if (hasNext) {
			if (nextSet_btn.getVisibility() != View.VISIBLE) {
				nextSet_btn.setVisibility(View.VISIBLE);
			}
			nextSet_btn.setNextFocusUpId(nextSet_btn.getId());
			nextSet_btn.setNextFocusRightId(R.id.player_exit_recommend_actors_listview);
			nextSet_btn.setNextFocusDownId(finishWatch_btn.getId());
			timerText.setText(mResources.getString(R.string.play_next_set));
		} else {
			if (nextSet_btn.getVisibility() == View.VISIBLE) {
				nextSet_btn.setVisibility(View.GONE);
			}
			finishWatch_btn.setNextFocusUpId(finishWatch_btn.getId());
			timerText.setText(mResources.getString(R.string.return_details));
		}
		finishWatch_btn.setNextFocusRightId(R.id.player_exit_recommend_actors_listview);
		finishWatch_btn.setNextFocusDownId(finishWatch_btn.getId());
		PlayerExitBtnFocusListener btnFocusListener = new PlayerExitBtnFocusListener(handler);
		btnFocusListener.setPlayEndType(true);
		nextSet_btn.setOnClickListener(this);
		finishWatch_btn.setOnClickListener(this);
		nextSet_btn.setOnFocusChangeListener(btnFocusListener);
		finishWatch_btn.setOnFocusChangeListener(btnFocusListener);
		finishWatch_btn.requestFocus();
		handler.setTimerView(time_llt);
	}

	public void initTimer() {
		timer=new PlayEndCountDownTimer(6000, 1000);
		handler.setTimer(timer);
		timer.start();
		
	}
    public class PlayEndCountDownTimer extends CountDownTimer{
		public PlayEndCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}
		@Override
		public void onTick(long millisUntilFinished) {
			time_tv.setText(String.valueOf(millisUntilFinished/1000));
		}
		@Override
		public void onFinish() {
			if(isCancel){
				isCancel=false;
				return;
			}else{
				time_llt.setVisibility(View.GONE);
				dismiss();
				if(type==PlayerActivity.SINGLE_FILM_TYPE){
					ctrl.returnDetails();
				}else if(type==PlayerActivity.MULTI_FILM_TYPE){
					if(hasNext){
						try {
							ctrl.playNextSet();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						ctrl.returnDetails();
					}
				}	
			}
			
		}
    }
    
	@Override
	public void dismiss() {
		if (timer != null) {
			isCancel=true;
			timer.cancel();
			timer = null;
		}
		super.dismiss();
	}
	@Override
	protected void finishPlayer() {
		ctrl.finishActvityForEnd();
	}
	public void updateDetailsInfo(String contentID){
		Intent intent = new Intent((PlayerActivity) context, DetailsActivity.class);
		intent.putExtra(DetailsActivity.CONTENT_ID, contentID);
		intent.putExtra(PlayerActivity.MEDIA_POSITION, position);
		((PlayerActivity) context).setResult(DetailsActivity.FINISH, intent);
		((PlayerActivity) context).startActivity(intent);
		((PlayerActivity) context).finish();
	}
	@Override
	public void onClick(View v) {
		dismiss();
		int id = v.getId();
		switch (id) {
		case R.id.play_normal_end_multi_set_next_set_btn:
			try {
				ctrl.playNextSet();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.play_normal_end_single_set_sure_btn:
		case R.id.play_normal_end_multi_set_finish_watch:
			ctrl.finishActvityForEnd();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dismiss();
			ctrl.returnDetails();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


}
