package com.pkit.launcher.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

import com.pkit.launcher.R;
import com.pkit.launcher.activity.DetailsActivity;
import com.pkit.launcher.animation.MediaItemFocusHelper;
import com.pkit.launcher.message.MediaBrowserMessageManager;
import com.pkit.launcher.service.LoggerRecordService;
import com.pkit.launcher.service.aidl.Container;
import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.service.aidl.Item;
import com.pkit.launcher.utils.APPLog;
import com.pkit.launcher.view.PageGridView.OnItemClickListener;
import com.pkit.launcher.view.PageGridView.OnItemSelectedListener;
import com.pkit.launcher.view.PageGridView.OnScrollListener;
import com.pkit.launcher.view.adapter.MediaBrowserItemAdapter;
import com.pkit.launcher.view.adapter.MediaBrowserItemAdapter.ItemViewHolder;

/**
 * 
 * @author Richard
 *
 */
public class MediaBrowserGridView implements OnItemSelectedListener, OnItemClickListener, OnScrollListener, OnHierarchyChangeListener {
	public static final int PAGE_SIZE = 10;
	private static final int DELAY_MILLIS = 1000;
	private PageGridView itemListPanelView;
	private Context context;
	private int currentPageIndex;
	private int pageCount;
	private Container container;
	private Handler msgQueue;
	private MediaBrowserItemAdapter itemAdapter;
	private View lastSelectedView;
	private boolean isInit;
	private AnimatorsHelper animHelper;

	public MediaBrowserGridView(Context context, PageGridView itemListPanelView, Handler msgQueue) {
		this.itemListPanelView = itemListPanelView;
		this.context = context;
		this.msgQueue = msgQueue;
		init();
	}

	private void init() {
		isInit = true;
		int spacing = (int) context.getResources().getDimension(R.dimen.media_browser_item_spacing);
		itemAdapter = new MediaBrowserItemAdapter(context);

		this.itemListPanelView.setNextFocusLeftId(R.id.media_browser_menu_list);
		this.itemListPanelView.setNextFocusUpId(R.id.media_browser_item_list);
		this.itemListPanelView.setNextFocusDownId(R.id.media_browser_item_list);
		this.itemListPanelView.setNextFocusRightId(R.id.media_browser_item_list);
		// this.itemListPanelView.setOnHierarchyChangeListener(this);
		this.itemListPanelView.setOnItemSelectedListener(this);
		this.itemListPanelView.setOnItemClickListener(this);
		this.itemListPanelView.setOnScrollListener(this);
		this.itemListPanelView.setSpacing(spacing);
		this.itemListPanelView.setAdapter(itemAdapter);

		animHelper = new AnimatorsHelper();
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		if (container != null && this.container != container) {
			itemListPanelView.setFocusable(false);
			currentPageIndex = 0;
			this.container = container;
			reloadItemList(container);
		}
	}

	public void setPage(int pageNumber) {
		this.currentPageIndex = pageNumber;
	}

	public void updateItemList(int startPage, int count, ArrayList<Content> itemList) {
		pageCount = (count + PAGE_SIZE - 1) / PAGE_SIZE;
		if (itemAdapter == null) {
			return;
		}
		itemListPanelView.setFocusable(true);
		if (container.childCount != count) {
			container.childCount = count;
			itemAdapter.setCount(container.childCount);
		}

		int startIndex = startPage * PAGE_SIZE;
		for (Content content : itemList) {
			itemAdapter.addItem((Item) content, startIndex);
			startIndex++;
		}
		itemAdapter.notifyDataSetChanged();
	}

	public void setFocusable(boolean focusable) {
		itemListPanelView.setFocusable(focusable);
	}

	private void reloadItemList(Container container) {
		itemListPanelView.resetList();
		itemAdapter.setCount(container.childCount);
		itemAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemSelected(PageGridView parent, View view, int position) {
		boolean isFocused = parent.isFocused();
		APPLog.printInfo("onItemSelected position:" + position + " /isFocused:" + isFocused);
		if (!isFocused) {
			return;
		}
		if (lastSelectedView == null) {
			lastSelectedView = view;
		}
		unSelected(lastSelectedView);
		selected(view);
		updatePageByPosition(position);
	}

	@Override
	public void onNothingSelected(PageGridView parent) {
		APPLog.printInfo("GridViewWrapper onNothingSelected");
		unSelected(lastSelectedView);
	}

	@Override
	public void onItemClick(PageGridView parent, View view, int position) {
		Item item = (Item) parent.getAdapter().getItem(position);
		if (item != null) {
			LoggerRecordService.setClickRecord(context, item.parentID, item.contentID);
			Intent intent = new Intent(context, DetailsActivity.class);
			intent.putExtra(DetailsActivity.CONTENT_ID, item.contentID);
			context.startActivity(intent);

			APPLog.printInfo("GridViewWrapper onItemClick:" + item.toString());
		}
	}

	private void unSelected(View view) {
		if (view == null) {
			return;
		}
		MediaItemFocusHelper.unSelectedAnim(view);
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			return;
		}
		holder.nameView.setBackgroundResource(R.drawable.media_browser_item_normal);
		holder.nameView.setSelected(false);
	}

	private void selected(View view) {
		if (view == null) {
			return;
		}
		MediaItemFocusHelper.selectedAnim(view);
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			return;
		}
		holder.nameView.setBackgroundResource(R.drawable.media_browser_item_selected);
		holder.nameView.setSelected(true);
		lastSelectedView = view;
	}

	private void updatePageByPosition(int position) {
		int pageIndex = position / PAGE_SIZE;
		if (pageIndex == currentPageIndex) {
			return;
		}

		Item item = (Item) itemAdapter.getItem(position);
		if (item == null) {
			loadData(pageIndex);
		}

		Message msg = msgQueue.obtainMessage(MediaBrowserMessageManager.UPDATE_PAGE, pageIndex, pageCount);
		msgQueue.removeMessages(MediaBrowserMessageManager.UPDATE_PAGE);
		msgQueue.sendMessageDelayed(msg, DELAY_MILLIS);
		currentPageIndex = pageIndex;
	}

	private void loadData(int pageIndex) {
		// int position = itemListPanelView.getFirstVisiblePosition();
		// Item item = (Item) itemAdapter.getItem(position);
		// if (item == null) {
		// pageIndex = position / PAGE_SIZE;
		// }
		Message msg = msgQueue.obtainMessage(MediaBrowserMessageManager.LOAD_ITEM_LIST, pageIndex, 3 * MediaBrowserGridView.PAGE_SIZE, container);
		msgQueue.removeMessages(MediaBrowserMessageManager.LOAD_ITEM_LIST);
		msgQueue.sendMessageDelayed(msg, DELAY_MILLIS);
	}

	@Override
	public void onScroll(PageGridView pageGridView) {
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
			int fromXDelta = 0 - animView.getWidth() - animView.getLeft();

			int fromYDelta = animView.getHeight() + itemListPanelView.getHeight();

			TranslateAnimation translateAnim = new TranslateAnimation(fromXDelta, 0, fromYDelta, 0);
			// Animation anim =
			// AnimationUtils.loadAnimation(animView.getContext(),
			// R.anim.media_browse_nav_in_anim);
			translateAnim.setDuration(100);
			translateAnim.setAnimationListener(this);
			animView.startAnimation(translateAnim);
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
				int count = itemListPanelView.getCount();
				int index = count > 10 ? 9 : count - 1;
				View view = itemListPanelView.getChildAt(index);
				if (view == animView) {
					isInit = false;
				}
			}
			APPLog.printInfo("onAnimationEnd isInit:" + isInit);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	}
}
