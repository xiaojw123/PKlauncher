package com.pkit.launcher.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class PageGridView extends RelativeLayout {
	// private static final int DEFUALT_CACHE_SIZE = 30;
	private static final int DEFUALT_COLUMN = 5;
	private static final int DEFUALT_ROW = 2;
	private static final int DURATION = 1000;
	private int spacing;
	/**
	 * 有多少列
	 */
	private int columnCount;
	/**
	 * 每页有多少行
	 */
	private int rowCount;
	// private int viewCacheSize;
	// private SparseArray<View> recycleViews;
	private boolean dataChanged;
	private BaseAdapter adapter;
	private int count;
	/**
	 * 当前页第一个item的下标
	 */
	private int firstVisiblePosition;
	// private int deltaY;
	private ScrollThread scrollThead;
	private int selectedPosition;
	private View[] activatedViews;
	private View[] mScrapViews;
	private OnItemSelectedListener selectedListener;
	private OnItemClickListener clickListener;
	private OnScrollListener scrollListener;
	private AdapterDataSetObserver observer;

	public PageGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public PageGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PageGridView(Context context) {
		super(context);
		init();
	}

	private void init() {
		observer = new AdapterDataSetObserver();
		dataChanged = false;
		columnCount = DEFUALT_COLUMN;
		rowCount = DEFUALT_ROW;
		mScrapViews = new View[columnCount * rowCount];
		scrollThead = new ScrollThread();
		setChildrenDrawingOrderEnabled(true);
		firstVisiblePosition = 0;
		selectedPosition = 0;
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		int position = selectedPosition;
		position -= firstVisiblePosition;
		if (i == childCount - 1) {// 这是最后一个需要刷新的item
			return position;
		}
		if (i == position) {// 这是原本要在最后一个刷新的item
			return childCount - 1;
		}
		return super.getChildDrawingOrder(childCount, i);
	}

	public void setSpacing(int spacing) {
		this.spacing = spacing;
	}

	public void setColumn(int column) {
		this.columnCount = column;
		mScrapViews = new View[columnCount * rowCount];
	}

	public int getColumn() {
		return columnCount;
	}

	// public int getViewCacheSize() {
	// return viewCacheSize;
	// }
	//
	// public void setViewCacheSize(int viewCacheSize) {
	// this.viewCacheSize = viewCacheSize;
	// }

	public BaseAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(BaseAdapter adapter) {
		if (adapter == null) {
			return;
		}
		resetList();
		adapter.registerDataSetObserver(observer);
		this.adapter = adapter;
		dataChanged = true;
		requestLayout();
	}

	/**
	 * 返回一共有多少item
	 * 
	 * @return 返回一共有多少item
	 */
	public int getCount() {
		if (adapter == null) {
			count = 0;
		} else {
			count = adapter.getCount();
		}
		return count;
	}

	public void setOnItemSelectedListener(OnItemSelectedListener selectedListener) {
		this.selectedListener = selectedListener;
	}

	public void setOnItemClickListener(OnItemClickListener clickListener) {
		this.clickListener = clickListener;
	}

	public void setOnScrollListener(OnScrollListener scrollListener) {
		this.scrollListener = scrollListener;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (!scrollThead.isFinish()) {
			return true;
		}
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				return selectNextPosition(FOCUS_DOWN);
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
				return selectNextPosition(FOCUS_UP);
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				return selectNextPosition(FOCUS_LEFT);
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				return selectNextPosition(FOCUS_RIGHT);
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
				itemClick();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		layoutChild();
	}

	protected void layoutChild() {
		if (adapter == null) {
			return;
		}

		boolean arrowLayout = scrollThead.isFinish();
		if (dataChanged) {
			dataChanged = false;
			detachAllViewsFromParent();
			makeCurrentPage();
			// if (isFocused()) {
			// itemSelected(true);
			// }
		} else if (arrowLayout) {
			int len = activatedViews.length;
			for (int i = 0; i < len; i++) {
				LayoutParams params = getParamsByIndex(i, 0);
				int left = params.leftMargin;
				int top = params.topMargin;
				int right = params.leftMargin + params.width;
				int bottom = params.topMargin + params.height;
				activatedViews[i].layout(left, top, right, bottom);
			}
			// int selecetedIndex = selectedPosition % (rowCount * columnCount);
			// boolean focused = isFocused();
		}
	}

	private void itemClick() {
		if (!scrollThead.isFinish() || clickListener == null) {
			return;
		}
		int index = selectedPosition % (rowCount * columnCount);
		clickListener.onItemClick(this, activatedViews[index], selectedPosition);
	}

	private void itemSelected(boolean isSelected) {
		if (!scrollThead.isFinish() || selectedListener == null || activatedViews == null || activatedViews.length == 0) {
			return;
		}
		int index = selectedPosition % (rowCount * columnCount);
		if (isSelected) {
			selectedListener.onItemSelected(this, activatedViews[index], selectedPosition);
		} else {
			selectedListener.onNothingSelected(this);
		}
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		itemSelected(gainFocus);
	}

	private boolean selectNextPosition(int direction) {
		if (!scrollThead.isFinish()) {
			return true;
		}
		if (direction == FOCUS_UP) {
			int nextPosition = selectedPosition - columnCount;
			if (nextPosition < 0) {
				return false;
			}
			selectedPosition = nextPosition;
			if (nextPosition < firstVisiblePosition) {
				// 向上翻页
				selectedPosition = nextPosition - columnCount;
				scrollToNextPage(direction);
				return true;
			}
		} else if (direction == FOCUS_DOWN) {
			if (selectedPosition >= getCount() - 1) {
				return false;
			}
			int nextPosition = selectedPosition + columnCount;
			if (nextPosition > getCount() - 1) {
				nextPosition = getCount() - 1;
			}
			selectedPosition = nextPosition;
			int lastVisiblePosition = firstVisiblePosition + rowCount * columnCount - 1;
			if (nextPosition > lastVisiblePosition) {
				// 向下翻页
				selectedPosition = Math.min(getCount() - 1, nextPosition + columnCount);
				scrollToNextPage(direction);
				return true;
			}
		} else if (direction == FOCUS_LEFT) {
			if (selectedPosition % columnCount != 0) {
				selectedPosition--;
			} else {
				return false;
			}
		} else if (direction == FOCUS_RIGHT) {
			if (selectedPosition >= getCount() - 1) {
				return false;
			}
			selectedPosition++;
			if (selectedPosition - firstVisiblePosition > rowCount * columnCount - 1) {
				// 向下翻页
				scrollToNextPage(FOCUS_DOWN);
				return true;
			}
		}
		itemSelected(true);
//		APPLog.printDebug("PageGridView selectNextPosition selectedPosition:" + selectedPosition);
		return true;
	}

	private void scrollToNextPage(int direction) {
		int distance = 0;
		int height = getMeasuredHeight();
		if (direction == FOCUS_UP) {
			// 增加上一页
			makeTopPage();
			distance = height + spacing;
		} else if (direction == FOCUS_DOWN) {
			// 增加下一页
			makeBottomPage();
			distance = -height - spacing;
		}
		scrollThead.startScroll(distance, DURATION);
	}

	private void updateChild(int deltaY) {
		scrollChildView(deltaY);
		if (scrollListener != null) {
			scrollListener.onScroll(this);
		}
	}

	private void updateCurrentPage() {
		if (activatedViews == null || activatedViews.length == 0) {
			dataChanged = true;
			requestLayout();
			return;
		}
		int len = activatedViews.length;
		for (int i = 0; i < len; i++) {
			int position = firstVisiblePosition + i;
			adapter.getView(position, activatedViews[i], this);
		}
	}

	private void makeCurrentPage() {
		int pageCount = rowCount * columnCount;
		int tmpCount = getCount() - firstVisiblePosition;
		int count = Math.min(tmpCount, pageCount);
		activatedViews = new View[count];
		for (int i = 0; i < count; i++) {
			int position = firstVisiblePosition + i;
			LayoutParams params = getParamsByIndex(i, 0);
			View view = obtainView(position);
			view.setLayoutParams(params);
			activatedViews[i] = view;
			addViewInLayout(view, i, params, true);

			int left = params.leftMargin;
			int top = params.topMargin;
			int right = params.leftMargin + params.width;
			int bottom = params.topMargin + params.height;
			view.layout(left, top, right, bottom);
		}
	}

	private void makeTopPage() {
		int rowIndex = firstVisiblePosition / columnCount - rowCount;
		if (rowIndex < 0) {
			return;
		}
		int startPosition = rowIndex * columnCount;
		firstVisiblePosition = startPosition;
		int height = getMeasuredHeight();
		int deltaY = -height - spacing;
		int count = columnCount * rowCount;
		for (int i = 0; i < count; i++) {
			int position = startPosition + i;
			LayoutParams params = getParamsByIndex(i, deltaY);
			View view = obtainView(position);
			view.setLayoutParams(params);
			addViewInLayout(view, i, params, true);

			int left = params.leftMargin;
			int top = params.topMargin;
			int right = params.leftMargin + params.width;
			int bottom = params.topMargin + params.height;
			view.layout(left, top, right, bottom);
		}
	}

	private void makeBottomPage() {
		int tmpCount = getCount() - firstVisiblePosition - columnCount * rowCount;
		if (tmpCount <= 0) {
			return;
		}
		int pageCount = columnCount * rowCount;
		int count = Math.min(tmpCount, pageCount);

		int startPosition = firstVisiblePosition + rowCount * columnCount;
		firstVisiblePosition = startPosition;
		int height = getMeasuredHeight();
		int deltaY = height + spacing;
		int index = pageCount;
		for (int i = 0; i < count; i++) {
			int position = startPosition + i;
			LayoutParams params = getParamsByIndex(i, deltaY);
			View view = obtainView(position);
			view.setLayoutParams(params);
			addViewInLayout(view, index + i, params, true);

			int left = params.leftMargin;
			int top = params.topMargin;
			int right = params.leftMargin + params.width;
			int bottom = params.topMargin + params.height;
			view.layout(left, top, right, bottom);
		}
	}

	private void scrollChildView(int deltaY) {
		int count = getChildCount();
		int distance = getMeasuredHeight() + spacing;
		int arg1 = 5;
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			LayoutParams params = (LayoutParams) view.getLayoutParams();

			int absDeltaY = Math.abs(deltaY);
			int offset = ((distance - absDeltaY) / arg1) * (i % columnCount);
			int left = params.leftMargin;
			int top = Math.max(params.topMargin, params.topMargin + deltaY - offset);
			if (deltaY < 0) {
				top = Math.min(params.topMargin, params.topMargin + deltaY + offset);
			}
			int right = params.leftMargin + params.width;
			int bottom = top + params.height;
			view.layout(left, top, right, bottom);

			view.requestLayout();
		}
	}

	private LayoutParams getParamsByIndex(int childIndex, int deltaY) {
		int width = getMeasuredWidth() - ((columnCount - 1) * spacing);
		int height = getMeasuredHeight() - ((rowCount - 1) * spacing);
		int unitWidth = width / columnCount;
		int unitHeight = height / rowCount;
		int columnIndex = childIndex % columnCount;
		int rowIndex = childIndex / columnCount;
		int left = (unitWidth + spacing) * columnIndex;
		int top = (unitHeight + spacing) * rowIndex + deltaY;

		LayoutParams params = new LayoutParams(unitWidth, unitHeight);
		params.width = unitWidth;
		params.height = unitHeight;
		params.leftMargin = left;
		params.topMargin = top;
		return params;
	}

	private View obtainView(int position) {
		// int index = position % viewCacheSize;
		// View convertView = recycleViews.get(index);
		int index = position % (columnCount * rowCount);
		View convertView = mScrapViews[index];
		View view = adapter.getView(position, convertView, this);
		return view;
	}

	private void recycleView() {
		// APPLog.printInfo("PageGridView recycleView visibleCount:" +
		// getChildCount());
		int len = activatedViews.length;
		int pageSize = columnCount * rowCount;
		for (int i = 0; i < pageSize; i++) {
			View view = null;
			if (i < len) {
				view = activatedViews[i];
				detachViewFromParent(view);
			}
			mScrapViews[i] = view;
		}

		int count = getChildCount();
		activatedViews = new View[count];
		for (int i = 0; i < count; i++) {
			View childView = getChildAt(i);
			LayoutParams params = getParamsByIndex(i, 0);
			childView.setLayoutParams(params);
			activatedViews[i] = childView;
		}
		// APPLog.printInfo("PageGridView recycleView visibleCount end:" +
		// getChildCount() + "activatedViews size:" + activatedViews.length);
	}

	public void resetList() {
		activatedViews = new View[0];
		mScrapViews = new View[columnCount * rowCount];
		firstVisiblePosition = 0;
		selectedPosition = 0;
	}

	class AdapterDataSetObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			super.onChanged();
//			APPLog.printInfo("PageGridView AdapterDataSetObserver onChanged");
			updateCurrentPage();
		}

		@Override
		public void onInvalidated() {
			super.onInvalidated();
//			APPLog.printInfo("PageGridView AdapterDataSetObserver onInvalidated");
			updateCurrentPage();
		}
	}

	class ScrollThread implements Runnable {
		private Scroller scroller;
		private boolean isFinished;

		public ScrollThread() {
			isFinished = true;
			scroller = new Scroller(getContext());
		}

		public void startScroll(int dy, int duration) {
			itemSelected(false);
			isFinished = false;
			ViewGroup parentView = (ViewGroup) getParent();
			parentView.setClipChildren(true);
			scroller.startScroll(0, 0, 0, dy, duration);
			postOnAnimation(this);
		}

		public boolean isFinish() {
			return isFinished;
		}

		@Override
		public void run() {
			if (scroller.computeScrollOffset()) {
				int deltaY = scroller.getCurrY();
				postOnAnimation(this);
				updateChild(deltaY);
			} else {
				recycleView();
				ViewGroup parentView = (ViewGroup) getParent();
				parentView.setClipChildren(false);
				isFinished = true;
				itemSelected(true);
			}
		}
	}

	public static interface OnItemSelectedListener {
		public void onItemSelected(PageGridView parent, View view, int position);

		public void onNothingSelected(PageGridView parent);
	}

	public static interface OnItemClickListener {
		public void onItemClick(PageGridView parent, View view, int position);
	}

	public static interface OnScrollListener {
		public void onScroll(PageGridView pageGridView);
	}
}
