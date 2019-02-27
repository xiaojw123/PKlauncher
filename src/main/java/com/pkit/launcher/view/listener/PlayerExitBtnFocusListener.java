package com.pkit.launcher.view.listener;

import android.os.Handler;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.pkit.launcher.view.MediaPlayerCtrl.CtrlMessage;

public class PlayerExitBtnFocusListener implements OnFocusChangeListener {
	private static final float ZOOM_IN=1.12f;
	private static final float ZOOM_OUT=1.00f;
	private static final int OffsetWidth = 26;
	private static final int offsetHeight = 14;
	private static final float lightBtnTextSize = 25;
	private static final float dullBtnTextSize = 22;
	private Handler handler;
	public static int btnMemoryId;
	private boolean isPlayEnd;

	public PlayerExitBtnFocusListener(Handler handler) {
		this.handler = handler;
	}

	public void setPlayEndType(boolean isPlayEnd) {
		this.isPlayEnd = isPlayEnd;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		Button button = (Button) v;
//		RelativeLayout.LayoutParams params = (LayoutParams) v.getLayoutParams();
		if (hasFocus) {
//			RelativeLayout.LayoutParams focusParams = getParamsByChildParams(params);
//			button.setLayoutParams(focusParams);
			zoomIn(button);
//			button.setTextSize(lightBtnTextSize);
		} else {
			btnMemoryId = button.getId();
			if (isPlayEnd) {
				if (handler.hasMessages(CtrlMessage.CANCEL_TIMER)) {
					handler.removeMessages(CtrlMessage.CANCEL_TIMER);
				}
				handler.sendEmptyMessage(CtrlMessage.CANCEL_TIMER);
			}
//			RelativeLayout.LayoutParams unfocusParams = getParamByFocusParams(params);
//			button.setLayoutParams(unfocusParams);
			zoomOut(button);
//			button.setTextSize(dullBtnTextSize);
		}

	}
    private void zoomIn(Button button){
    	button.setScaleX(ZOOM_IN);
    	button.setScaleY(ZOOM_IN);
    }
    private void zoomOut(Button button){
    	button.setScaleX(ZOOM_OUT);
    	button.setScaleY(ZOOM_OUT);
    }
	private RelativeLayout.LayoutParams getParamsByChildParams(RelativeLayout.LayoutParams childParams) {
		childParams.width = childParams.width + OffsetWidth;
		childParams.height = childParams.height + offsetHeight;
		childParams.leftMargin = childParams.leftMargin - OffsetWidth / 2;
		childParams.topMargin = childParams.topMargin - offsetHeight / 2;
		return childParams;
	}

	private RelativeLayout.LayoutParams getParamByFocusParams(RelativeLayout.LayoutParams focusParams) {
		focusParams.width = focusParams.width - OffsetWidth;
		focusParams.height = focusParams.height - offsetHeight;
		focusParams.leftMargin = focusParams.leftMargin + OffsetWidth / 2;
		focusParams.topMargin = focusParams.topMargin + offsetHeight / 2;
		return focusParams;
	}

}
