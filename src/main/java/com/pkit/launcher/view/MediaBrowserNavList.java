package com.pkit.launcher.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.activity.SearchActivity;
import com.pkit.launcher.message.MediaBrowserMessageManager;
import com.pkit.launcher.service.aidl.Container;
import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.utils.APPLog;
import com.pkit.launcher.view.adapter.MediaBrowserMenuAdapter;

public class MediaBrowserNavList implements OnFocusChangeListener, OnItemSelectedListener, OnClickListener, OnHierarchyChangeListener {
	private static final int DURATION = 250;
	private static final int DELAY_MILLIS = 1000;
	/**
	 * 左边选择向导条ParentView
	 */
	private RelativeLayout navPanelView;
	/**
	 * 搜索选项
	 */
	private LinearLayout searchView;
	/**
	 * 筛选选项
	 */
	private LinearLayout selectorView;
	/**
	 * 分类列表
	 */
	private ListView categoryListView;
	private RelativeLayout categoryHeader;
	/**
	 * 分类列表适配
	 */
	private int firstPosition;
	private MediaBrowserMenuAdapter menuAdapter;

	private View lastCategory;
	private View searchLine;
	private View selectorLine;
	private Context context;
	private ScrollerThread scrollerThread;
	// private float ZOOM_IN = 1.15f;
	// private float ZOOM_OUT = 1.0f;
	private int hideScrollDistance;
	private ColorStateList colorList;
	private int stayColor;
	private float normalSize;
	private float selectedSize;
	private RelativeLayout categoryListContainer;
	private Container parentContainer;
	private Handler msgQueue;
	/**
	 * 影片列表
	 */
	private MediaBrowserGridView itemList;
	private AnimatorsHelper animHelper;
	private boolean isInit;

	public MediaBrowserNavList(Context context, RelativeLayout navPanelView, Handler msgQueue) {
		this.context = context;
		this.navPanelView = navPanelView;
		this.msgQueue = msgQueue;
		init();
	}

	private void init() {
		isInit = true;
		hideScrollDistance = (int) context.getResources().getDimension(R.dimen.media_browser_menu_list_top);
		stayColor = context.getResources().getColor(R.color.green_2);
		colorList = context.getResources().getColorStateList(R.drawable.media_browser_text_color);
		normalSize = context.getResources().getDimension(R.dimen.text_size_18_sp);
		selectedSize = context.getResources().getDimension(R.dimen.text_size_20_sp);

		searchView = (LinearLayout) navPanelView.findViewById(R.id.media_browser_search);
		searchView.setOnFocusChangeListener(this);
		searchView.setOnClickListener(this);
		searchView.setNextFocusLeftId(R.id.media_browser_search);
		searchView.setNextFocusUpId(R.id.media_browser_search);
		searchView.setNextFocusRightId(R.id.media_browser_search);
		searchView.setVisibility(View.INVISIBLE);
		searchLine = navPanelView.findViewById(R.id.media_browser_search_line);

		selectorView = (LinearLayout) navPanelView.findViewById(R.id.media_browser_selector);
		selectorView.setOnFocusChangeListener(this);
		selectorView.setOnClickListener(this);
		selectorView.setNextFocusLeftId(R.id.media_browser_selector);
		selectorView.setNextFocusRightId(R.id.media_browser_selector);
		selectorView.setVisibility(View.INVISIBLE);
		selectorLine = navPanelView.findViewById(R.id.media_browser_selector_line);

		categoryListContainer = (RelativeLayout) navPanelView.findViewById(R.id.media_browser_menu_list_container);

		categoryHeader = getCategoryHeader();

		categoryListView = (ListView) navPanelView.findViewById(R.id.media_browser_menu_list);
		categoryListView.addHeaderView(categoryHeader);
		categoryListView.setOnFocusChangeListener(this);
		categoryListView.setOnItemSelectedListener(this);
		categoryListView.setOnHierarchyChangeListener(this);
		categoryListView.setNextFocusDownId(R.id.media_browser_menu_list);
		categoryListView.setNextFocusLeftId(R.id.media_browser_menu_list);
		categoryListView.setNextFocusRightId(R.id.media_browser_item_list);

		scrollerThread = new ScrollerThread();
		firstPosition = categoryListView.getFirstVisiblePosition();
		animHelper = new AnimatorsHelper();
		animHelper.addAnimView(searchView);
		animHelper.addAnimView(selectorView);
	}

	public boolean requestFocus() {
		return categoryListView.requestFocus();
	}

	public Container getParentContainer() {
		return parentContainer;
	}

	public void setParentContainer(Container parentContainer) {
		this.parentContainer = parentContainer;
	}

	public void setItemList(MediaBrowserGridView itemList) {
		this.itemList = itemList;
	}

	private RelativeLayout getCategoryHeader() {
		String text = context.getResources().getString(R.string.all_text);
		int textHeight = (int) context.getResources().getDimension(R.dimen.media_browser_all_height);
		RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, textHeight);

		TextView allCategoryView = new TextView(context);
		allCategoryView.setTextSize(TypedValue.COMPLEX_UNIT_PX, normalSize);
		allCategoryView.setTextColor(colorList);
		allCategoryView.setGravity(Gravity.CENTER);
		allCategoryView.setLayoutParams(textParams);
		allCategoryView.setText(text);

		int color = context.getResources().getColor(R.color.white_1);
		int lineWidth = (int) context.getResources().getDimension(R.dimen.media_browser_line_width);
		int lineHeight = (int) context.getResources().getDimension(R.dimen.media_browser_line_height_2);
		RelativeLayout.LayoutParams lineParams = new RelativeLayout.LayoutParams(lineWidth, lineHeight);
		lineParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		lineParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

		View view = new View(context);
		view.setBackgroundColor(color);
		view.setLayoutParams(lineParams);

		int headerHeight = (int) context.getResources().getDimension(R.dimen.media_browser_header_height);
		AbsListView.LayoutParams headerParams = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, headerHeight);

		RelativeLayout categoryHeader = new RelativeLayout(context);
		categoryHeader.setLayoutParams(headerParams);
		categoryHeader.setGravity(Gravity.CENTER);
		categoryHeader.addView(allCategoryView);
		categoryHeader.addView(view);
		return categoryHeader;
	}

	public void addCategoryList(ArrayList<Content> categoryListData) {
		if (menuAdapter == null) {
			menuAdapter = new MediaBrowserMenuAdapter(context);
		}
		menuAdapter.setCategoryList(categoryListData);
		categoryListView.setAdapter(menuAdapter);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		int id = v.getId();
		if (id == R.id.media_browser_search || id == R.id.media_browser_selector) {
			aboutSearchOrSelector(v, hasFocus);
		} else if (id == R.id.media_browser_menu_list) {
			aboutCatList(hasFocus);
		}
	}

	private void aboutSearchOrSelector(View v, boolean hasFocus) {
		LinearLayout container = (LinearLayout) v;
		ImageView img = (ImageView) container.getChildAt(0);
		TextView text = (TextView) container.getChildAt(1);
		text.setSelected(hasFocus);
		img.setSelected(hasFocus);
		if (hasFocus) {
			text.setTextSize(TypedValue.COMPLEX_UNIT_PX, selectedSize);
			msgQueue.removeMessages(MediaBrowserMessageManager.LOAD_ITEM_LIST);
		} else {
			text.setTextSize(TypedValue.COMPLEX_UNIT_PX, normalSize);
		}
	}

	private void aboutCatList(boolean hasFocus) {
		View selectedView = categoryListView.getSelectedView();
		if (selectedView == null) {
			return;
		}
		TextView textView = findTextViewFromParent(selectedView);
		if (hasFocus) {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, selectedSize);
			textView.setTextColor(colorList);
			textView.requestFocus();
		} else {
			textView.setTextColor(stayColor);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		Intent intent = new Intent(context, SearchActivity.class);
		if (id == R.id.media_browser_search) {
			APPLog.printInfo("media_browser_search");
			intent.putExtra(SearchActivity.START_MODE, SearchActivity.SEARCH);
		} else if (id == R.id.media_browser_selector) {
			APPLog.printInfo("media_browser_selector");
			intent.putExtra(SearchActivity.START_MODE, SearchActivity.SELECTOR);
		}
		intent.putExtra(SearchActivity.CONTENT_ID_ARG, parentContainer.contentID);
		context.startActivity(intent);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//		APPLog.printInfo("onItemSelected position:" + position);
		boolean hasFocus = parent.hasFocus();
		if (!hasFocus) {
			parent.requestFocus();
		}
		updateNavListByPosition(position);
		unSelected(lastCategory);
		selected(view);
		loadItemList(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public void onChildViewAdded(View parent, View child) {
		if (isInit) {
			child.setVisibility(View.INVISIBLE);
		}
		animHelper.addAnimView(child).startAnim();
	}

	@Override
	public void onChildViewRemoved(View parent, View child) {
	}

	private void unSelected(View view) {
		TextView textView = findTextViewFromParent(view);
		if (textView != null) {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, normalSize);
		}
	}

	private void selected(View view) {
		TextView textView = findTextViewFromParent(view);
		if (textView != null) {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, selectedSize);
		}
		lastCategory = view;
	}

	private void updateNavListByPosition(int position) {
		if (position > 0) {
			hideSearchAndSelector();
		} else {
			showSearchAndSelector();
		}

		int firstVisiblePosition = categoryListView.getFirstVisiblePosition();
		if (firstVisiblePosition > firstPosition) {
			int tmp = Math.min(position + 8, categoryListView.getCount());
			firstPosition = tmp - 8;
			categoryListView.smoothScrollToPositionFromTop(position, 0, DURATION);
		} else if (firstVisiblePosition < firstPosition) {
			int tmp = Math.max(position - 7, 0);
			firstPosition = tmp;
			categoryListView.smoothScrollToPositionFromTop(tmp, 0, DURATION);
		}
	}

	private void hideSearchAndSelector() {
		scrollerThread.stop();
		scrollerThread.scrollerTo(-hideScrollDistance, DURATION);
	}

	private void showSearchAndSelector() {
		scrollerThread.stop();
		scrollerThread.scrollerTo(0, DURATION);
	}

	private TextView findTextViewFromParent(View view) {
		TextView textView = null;
		if (view != null) {
			if (view == categoryHeader) {
				textView = (TextView) categoryHeader.getChildAt(0);
			} else {
				textView = (TextView) view;
			}
		}
		return textView;
	}

	private void loadItemList(int position) {
		Message msg = null;
		Container container = null;
		if (position > 0) {
			container = (Container) menuAdapter.getItem(position - 1);
		} else {
			container = this.parentContainer;
		}
		boolean flag = false;
		if (this.itemList != null) {
			flag = (this.itemList.getContainer() == container);
			this.itemList.setFocusable(flag);
		}
		msgQueue.removeMessages(MediaBrowserMessageManager.LOAD_ITEM_LIST);
		if(flag){
			return;
		}
		msg = msgQueue.obtainMessage(MediaBrowserMessageManager.LOAD_ITEM_LIST, 0, 3 * MediaBrowserGridView.PAGE_SIZE, container);
		msgQueue.sendMessageDelayed(msg, DELAY_MILLIS);
	}

	class ScrollerThread implements Runnable {
		private int scrollerY;
		private Scroller scroller;
		private RelativeLayout.LayoutParams searchParams;
		private RelativeLayout.LayoutParams searchLineParams;
		private RelativeLayout.LayoutParams selectorParams;
		private RelativeLayout.LayoutParams selectorLineParams;
		private RelativeLayout.LayoutParams categoryListContainerParams;

		public ScrollerThread() {
			scrollerY = 0;
			scroller = new Scroller(context);
			searchParams = (RelativeLayout.LayoutParams) searchView.getLayoutParams();
			searchLineParams = (RelativeLayout.LayoutParams) searchLine.getLayoutParams();
			selectorParams = (RelativeLayout.LayoutParams) selectorView.getLayoutParams();
			selectorLineParams = (RelativeLayout.LayoutParams) selectorLine.getLayoutParams();
			categoryListContainerParams = (RelativeLayout.LayoutParams) categoryListContainer.getLayoutParams();
		}

		void scrollerTo(int scrollerY, int duration) {
			if (this.scrollerY == scrollerY) {
				return;
			}
			int dy = scrollerY - this.scrollerY;
			scroller.startScroll(0, this.scrollerY, 0, dy, duration);
			navPanelView.postOnAnimation(this);
		}

		void stop() {
			int offsetY = scroller.getFinalY();
			if (scrollerY != offsetY) {
				scroll(offsetY);
			}
			scroller.abortAnimation();
			navPanelView.removeCallbacks(this);
		}

		@Override
		public void run() {
			if (scroller.computeScrollOffset()) {
				int offsetY = scroller.getCurrY();
				if (scrollerY != offsetY) {
					scroll(offsetY);
				}
				navPanelView.postOnAnimation(this);
			} else {
				stop();
			}
		}

		private void scroll(int offsetY) {
			int offset = offsetY - scrollerY;
			searchParams.topMargin += offset;
			searchLineParams.topMargin += offset;
			selectorParams.topMargin += offset;
			selectorLineParams.topMargin += offset;
			categoryListContainerParams.topMargin += offset;
			navPanelView.requestLayout();
			scrollerY = offsetY;
		}
	}
	public class AnimatorsHelper implements AnimationListener {
		private ArrayList<View> viewQueue;
		private boolean isStarted = false;
		private View animView;

		public AnimatorsHelper() {
			viewQueue = new ArrayList<View>();
		}

		public AnimatorsHelper addAnimView(View animView) {
			this.viewQueue.add(animView);
			return this;
		}

		public void startAnim() {
			animView = this.viewQueue.get(0);
			if (animView == null || isStarted || !isInit) {
				return;
			}
			isStarted = true;
			animView.setVisibility(View.VISIBLE);
			Animation anim = AnimationUtils.loadAnimation(animView.getContext(), R.anim.media_browse_nav_in_anim);
			anim.setAnimationListener(this);
			animView.startAnimation(anim);
			this.viewQueue.remove(0);
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			isStarted = false;
			if (this.viewQueue.size() != 0) {
				startAnim();
			} else {
				int position = categoryListView.getLastVisiblePosition();
				View view = categoryListView.getChildAt(position);
				if (animView == view) {
					categoryListView.setSelector(R.drawable.media_browser_menu_bg);
					isInit = false;
				}
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	}
}
