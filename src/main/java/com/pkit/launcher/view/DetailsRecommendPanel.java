package com.pkit.launcher.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pkit.launcher.R;
import com.pkit.launcher.utils.APPLog;
import com.pkit.launcher.view.adapter.MediaBrowserItemAdapter.ItemViewHolder;

public class DetailsRecommendPanel extends RelativeLayout {
	public static interface OnItemClickListener {
		public void onItemClick(View view, int position);
	}

	private static final float ZOOM_IN = 1.15f;
	private static final float ZOOM_OUT = 1.0f;
	private OnItemClickListener onItemClickListener;
	private int selectedPosition;
	private int spacing;
	private ImageView allLookMask;
	private ImageView globalMask;
	public DetailsRecommendPanel(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		setChildrenDrawingOrderEnabled(true);
		setFocusable(true);
	}

	public DetailsRecommendPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		setChildrenDrawingOrderEnabled(true);
		setFocusable(true);
	}

	public DetailsRecommendPanel(Context context) {
		super(context);
		setChildrenDrawingOrderEnabled(true);
		setFocusable(true);
	}
	public void setMask(ImageView allLookMask, ImageView globalMask) {
		this.allLookMask = allLookMask;
		this.globalMask = globalMask;
	}
	public void setSpacing(int spacing) {
		this.spacing = spacing;
	}

	public OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		View view = getChildAt(selectedPosition);
		if (gainFocus) {
			APPLog.printDebug("gainFocus======"+gainFocus);
			hideAllLookMaskLayer();
			selected(view);
		} else {
			unSelected(view);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)
				&& onItemClickListener != null) {
			View view = getChildAt(selectedPosition);
			onItemClickListener.onItemClick(view, selectedPosition);
			return true;
		}else if(keyCode==KeyEvent.KEYCODE_DPAD_UP){
			showAllLookMaskLayer();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int action = event.getAction();
		int keyCode = event.getKeyCode();
		boolean hold = false;
		if (KeyEvent.ACTION_DOWN == action) {
			hold = executeKeyEvent(keyCode);
		}
		return super.dispatchKeyEvent(event) || hold;
	}

	private boolean executeKeyEvent(int keyCode) {
		int count = getChildCount();
		if (count <= 0) {
			return false;
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			View view = getChildAt(selectedPosition);
			unSelected(view);
			selectedPosition--;
			if (selectedPosition < 0) {
				selectedPosition = count - 1;
			}
			view = getChildAt(selectedPosition);
			selected(view);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			View view = getChildAt(selectedPosition);
			unSelected(view);
			selectedPosition++;
			if (selectedPosition >= count) {
				selectedPosition = 0;
			}
			view = getChildAt(selectedPosition);
			selected(view);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		if (childCount == 0) {
			return super.getChildDrawingOrder(childCount, i);
		}
		if (i == childCount - 1) {// 这是最后一个需要刷新的item
			return selectedPosition;
		}
		if (i == selectedPosition) {// 这是原本要在最后一个刷新的item
			return childCount - 1;
		}
		return super.getChildDrawingOrder(childCount, i);
	}

	void unSelected(View view) {
		APPLog.printInfo("unSelected:" + selectedPosition);
		if (view == null) {
			return;
		}
		view.setScaleX(ZOOM_OUT);
		view.setScaleY(ZOOM_OUT);
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			return;
		}
		holder.nameView.setBackgroundColor(Color.TRANSPARENT);
		holder.nameView.setSelected(false);
	}

	void selected(View view) {
		APPLog.printInfo("selected:" + selectedPosition);
		if (view == null) {
			return;
		}
		view.setScaleX(ZOOM_IN);
		view.setScaleY(ZOOM_IN);
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			return;
		}
		holder.nameView
				.setBackgroundResource(R.drawable.media_browser_item_selected);
		holder.nameView.setSelected(true);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		layoutChild();
		super.onLayout(changed, l, t, r, b);
	}

	protected void layoutChild() {
		int count = getChildCount();
		View previousView = null;
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			if (previousView != null) {
				LayoutParams preParams = (LayoutParams) previousView
						.getLayoutParams();
				int left = preParams.leftMargin + preParams.width + spacing;
				LayoutParams params = (LayoutParams) view.getLayoutParams();
				params.leftMargin = left;
			}
			previousView = view;
		}
	}
	private void hideAllLookMaskLayer() {
		if (allLookMask.getVisibility() == View.VISIBLE) {
			allLookMask.setVisibility(View.GONE);
		}
		if (globalMask.getVisibility() != View.VISIBLE) {
			globalMask.setVisibility(View.VISIBLE);
		}
	}
	private void showAllLookMaskLayer(){
		if (globalMask.getVisibility() == View.VISIBLE) {
			globalMask.setVisibility(View.GONE);
		}
		if (allLookMask.getVisibility() != View.VISIBLE) {
			allLookMask.setVisibility(View.VISIBLE);
		}
		
	}

}
