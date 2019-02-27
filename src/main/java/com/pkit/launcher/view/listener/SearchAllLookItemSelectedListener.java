package com.pkit.launcher.view.listener;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

public class SearchAllLookItemSelectedListener implements OnItemSelectedListener {
	private static final float ZOOM_OUT = 1.0f;
	private static final float ZOOM_IN = 1.05f;
	public View oldView;
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if (!parent.isFocused()){
			return;
		}
		Log.d("xiaojw", "selected---pos===="+position);
		if (oldView == null) {
			oldView = parent.getChildAt(0);
		}
		if(oldView!=null){
		setDullName(oldView);
	     zoomOut(oldView);
		}
		oldView=view;
		setLightName(oldView);
		zoomIn(oldView);
		parent.postInvalidate();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

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
