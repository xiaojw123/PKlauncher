package com.pkit.launcher.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.pkit.launcher.utils.APPLog;

public class GridViewWrapper extends GridView {
	// private static final float ZOOM_IN = 1.15f;
	// private static final float ZOOM_OUT = 1.0f;
	private View lastSelected;

	public GridViewWrapper(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setChildrenDrawingOrderEnabled(true);
	}

	public GridViewWrapper(Context context, AttributeSet attrs) {
		super(context, attrs);
		setChildrenDrawingOrderEnabled(true);
	}

	public GridViewWrapper(Context context) {
		super(context);
		setChildrenDrawingOrderEnabled(true);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		int position = (getSelectedItemPosition() >= 0 ? getSelectedItemPosition() : 0);
		position -= getFirstVisiblePosition();
		if (i == childCount - 1) {// 这是最后一个需要刷新的item
			return position;
		}
		if (i == position) {// 这是原本要在最后一个刷新的item
			return childCount - 1;
		}
		return super.getChildDrawingOrder(childCount, i);
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		int position = getSelectedItemPosition();
		APPLog.printInfo("GridViewWrapper onFocusChanged:" + gainFocus + "/position:" + position);
		OnItemSelectedListener selectedListener = getOnItemSelectedListener();
		if (selectedListener == null) {
			return;
		}
		if (gainFocus && position >= 0) {
			selectedListener.onItemSelected(this, getSelectedView(), getSelectedItemPosition(), getSelectedItemId());
			setSelection(position);
		} else {
			getOnItemSelectedListener().onNothingSelected(this);
			// setSelection(-1);
		}
	}

	// void unSelected(View view) {
	// if (view == null) {
	// return;
	// }
	// view.setScaleX(ZOOM_OUT);
	// view.setScaleY(ZOOM_OUT);
	// ItemViewHolder holder = (ItemViewHolder) view.getTag();
	// if (holder == null) {
	// return;
	// }
	// holder.nameView.setBackgroundResource(R.drawable.media_browser_item_normal);
	// holder.nameView.setSelected(false);
	// }
	//
	// void selected(View view) {
	// lastSelected = view;
	// if (view == null) {
	// return;
	// }
	// view.setScaleX(ZOOM_IN);
	// view.setScaleY(ZOOM_IN);
	// ItemViewHolder holder = (ItemViewHolder) view.getTag();
	// if (holder == null) {
	// return;
	// }
	// holder.nameView.setBackgroundResource(R.drawable.media_browser_item_selected);
	// holder.nameView.setSelected(true);
	// }

	View getLastSelectedView() {
		return lastSelected;
	}
}
