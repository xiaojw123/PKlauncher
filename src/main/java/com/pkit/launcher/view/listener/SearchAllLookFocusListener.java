package com.pkit.launcher.view.listener;

import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchAllLookFocusListener implements OnFocusChangeListener {
	private static final float ZOOM_OUT = 1.0f;
	private static final float ZOOM_IN = 1.1f;
	boolean flag=true;
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		Log.d("xiaojw", "onFocusChange====");
		GridView gridview = (GridView) v;
		if (hasFocus) {
			if(flag){
				View child = gridview.getChildAt(0);
				if (child == null) {
					return;
				}
				zoomIn(child);
				setLightName(child);	
				flag=false;
			}else{
				View view = gridview.getSelectedView();
				zoomIn(view);
				setLightName(view);
			}
		} else {
			View view = gridview.getSelectedView();
			zoomOut(view);
			setDullName(view);
		}
		RelativeLayout container=(RelativeLayout) v.getParent();
		container.postInvalidate();
	}
	private void setLightName(View v) {
		TextView name=(TextView) ((ViewGroup)v).getChildAt(2);
		name.setTextSize(19);
	}
	private void setDullName(View v) {
		TextView name=(TextView) ((ViewGroup)v).getChildAt(2);
		name.setTextSize(18);
	}
	public void zoomIn(View v) {
		v.setScaleX(ZOOM_IN);
		v.setScaleY(ZOOM_IN);
	}
	public void zoomOut(View v) {
		v.setScaleX(ZOOM_OUT);
		v.setScaleY(ZOOM_OUT);
	}

}
