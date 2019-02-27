package com.pkit.launcher.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.AbsSeekBar;
import com.pkit.launcher.utils.APPLog;
public class VolumeSeekBar extends AbsSeekBar {

	private Drawable mThumb;

	public VolumeSeekBar(Context context) {
		this(context, null);
	}

	public VolumeSeekBar(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.seekBarStyle);
	}

	public VolumeSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public interface OnSeekBarChangeListener {
		void onProgressChanged(VolumeSeekBar VolumeSeekBar, int progress, boolean fromUser);

		void onStartTrackingTouch(VolumeSeekBar VolumeSeekBar);

		void onStopTrackingTouch(VolumeSeekBar VolumeSeekBar);
	}

	private OnSeekBarChangeListener mOnSeekBarChangeListener;

	public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {

		mOnSeekBarChangeListener = l;

	}

	void onProgressRefresh(float scale, boolean fromUser) {
		Drawable thumb = mThumb;
		if (thumb != null) {
			APPLog.printInfo("*****scale***"+scale);
			setThumbPos(getHeight(), thumb, scale, Integer.MIN_VALUE);
			invalidate();
		}
		if (mOnSeekBarChangeListener != null) {
			mOnSeekBarChangeListener.onProgressChanged(this, getProgress(), fromUser);
		}
	}

	/**
	 * must be fixed size
	 */
	// TODO:
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		width = getLayoutParams().width;
		height = getLayoutParams().height;
		this.setMeasuredDimension(width, height);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(h, w, oldw, oldh);
	}
	public synchronized void setProgressAndThumb(int progress) {
		APPLog.printDebug("setProgressAndThumb------");
		setProgress(getMax() - (getMax() - progress));
		onSizeChanged(getWidth(), getHeight(), 0, 0);
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		APPLog.printDebug("onDraw=======");
		canvas.rotate(-90);
		canvas.translate(-this.getHeight(), 0);
		super.onDraw(canvas);
	}
	private void setThumbPos(int w, Drawable thumb, float scale, int gap) {
		int available = w - getPaddingTop() - getPaddingBottom();
		int thumbWidth = thumb.getIntrinsicWidth();
		int thumbHeight = thumb.getIntrinsicHeight();
		int thumbPos = (int) ((scale*7/12) * available);
		int topBound, bottomBound;
		if (gap == Integer.MIN_VALUE) {
			Rect oldBounds = thumb.getBounds();
			topBound = oldBounds.top;
			bottomBound = oldBounds.bottom;
		} else {
			topBound = gap;
			bottomBound = gap + thumbHeight;
		}
		APPLog.printDebug("thumPos======"+thumbPos);
		thumb.setBounds(thumbPos, topBound, thumbPos + thumbWidth, bottomBound);
	}

	@Override
	public void setThumb(Drawable thumb) {
		mThumb = thumb;
		super.setThumb(thumb);
	}

	@SuppressWarnings("deprecation")
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			KeyEvent newEvent = null;
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_UP:
				newEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT);
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				newEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT);
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				newEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				newEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP);
				break;
			default:
				newEvent = new KeyEvent(KeyEvent.ACTION_DOWN, event.getKeyCode());
				break;
			}
			return newEvent.dispatch(this);
		}
		return false;
	}
}
