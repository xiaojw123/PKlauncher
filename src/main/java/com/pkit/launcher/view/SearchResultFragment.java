package com.pkit.launcher.view;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.pkit.launcher.R;
import com.pkit.launcher.activity.DetailsActivity;
import com.pkit.launcher.animation.MediaItemFocusHelper;
import com.pkit.launcher.message.SearchMessageManager;
import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.service.aidl.Item;
import com.pkit.launcher.utils.APPLog;
import com.pkit.launcher.view.PageGridView.OnItemClickListener;
import com.pkit.launcher.view.PageGridView.OnItemSelectedListener;
import com.pkit.launcher.view.PageGridView.OnScrollListener;
import com.pkit.launcher.view.adapter.MediaBrowserItemAdapter;
import com.pkit.launcher.view.adapter.MediaBrowserItemAdapter.ItemViewHolder;
import com.pkit.launcher.view.adapter.SearchResultAdapter;

public class SearchResultFragment extends Fragment implements OnItemSelectedListener, OnItemClickListener, OnScrollListener {
	public static final int PAGE_SIZE = 8;
	private static final int DELAY_MILLIS = 1000;
	private Activity activity;
	private RelativeLayout resultLayout;
	private int currentPageIndex;
	private int pageCount;
	private Handler msgQueue;
	private View lastSelectedView;
	private MediaBrowserItemAdapter itemAdapter;
	private PageGridView itemListView;
	private String currentKey;
	private Item[] itemList;
	private ProgressBar progressBar;
	private boolean focusable;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		resultLayout = (RelativeLayout) inflater.inflate(R.layout.search_result_layout, container, false);
		init();
		return resultLayout;
	}

	private void init() {
		int spacing = (int) activity.getResources().getDimension(R.dimen.z_search_result_item_list_spacing);

		itemListView = (PageGridView) resultLayout.findViewById(R.id.search_result_item_list);
		itemListView.setNextFocusUpId(R.id.search_result_item_list);
		itemListView.setNextFocusDownId(R.id.search_result_item_list);
		itemListView.setNextFocusRightId(R.id.search_result_item_list);

		itemListView.setOnItemSelectedListener(this);
		itemListView.setOnItemClickListener(this);
		itemListView.setOnScrollListener(this);
		itemListView.setSpacing(spacing);
		itemListView.setColumn(4);
		itemListView.setFocusable(focusable);

		progressBar = (ProgressBar) resultLayout.findViewById(R.id.search_result_item_list_loading);

		itemAdapter = new SearchResultAdapter(activity);
		itemListView.setAdapter(itemAdapter);
		itemAdapter.setItemList(itemList);
		itemAdapter.notifyDataSetChanged();

		if (itemList == null || itemList.length == 0) {
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressBar.setVisibility(View.GONE);
		}
	}

	public void setProgressVisibility(int visibility) {
		if (progressBar != null) {
			progressBar.setVisibility(visibility);
		}
	}

	public void setFocusable(boolean focusable) {
		this.focusable = focusable;
		if (itemListView != null) {
			itemListView.setFocusable(focusable);
		}
	}

	public void setHandler(Handler handler) {
		this.msgQueue = handler;
	}

	public void setCurrentKey(String currentKey) {
		this.currentKey = currentKey;
	}

	public void updateItemList(int startPage, List<Content> itemList) {
		if (itemAdapter == null) {
			return;
		}

		int startIndex = startPage * PAGE_SIZE;
		for (Content content : itemList) {
			itemAdapter.addItem((Item) content, startIndex);
			startIndex++;
		}
		itemAdapter.notifyDataSetChanged();
	}

	public void setResult(int count, List<Content> items) {
		if (itemAdapter == null) {
			itemList = new Item[count];
			if (items != null) {
				items.toArray(itemList);
			}
		} else {
			Item[] itemList = new Item[count];
			if (items != null) {
				items.toArray(itemList);
			}
			itemAdapter.setItemList(itemList);
			itemAdapter.notifyDataSetChanged();
		}
		pageCount = (count + PAGE_SIZE - 1) / PAGE_SIZE;
	}

	@Override
	public void onScroll(PageGridView pageGridView) {
		RelativeLayout parntView = (RelativeLayout) pageGridView.getParent();
		parntView.postInvalidate();
	}

	@Override
	public void onItemClick(PageGridView parent, View view, int position) {
		Item item = (Item) parent.getAdapter().getItem(position);
		if (item != null) {
			Intent intent = new Intent(activity, DetailsActivity.class);
			intent.putExtra(DetailsActivity.CONTENT_ID, item.contentID);
			activity.startActivity(intent);

			APPLog.printInfo("GridViewWrapper onItemClick:" + item.toString());
		}
	}

	@Override
	public void onItemSelected(PageGridView parent, View view, int position) {
		updatePageByPosition(position);
		unSelected(lastSelectedView);
		selected(view);
		parent.requestLayout();
	}

	@Override
	public void onNothingSelected(PageGridView parent) {
		unSelected(lastSelectedView);
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

		currentPageIndex = pageIndex;
		if (msgQueue != null) {
			Message msg = msgQueue.obtainMessage(SearchMessageManager.SEARCH_RESULT_PAGE_CHANGE, pageIndex, pageCount);
			msgQueue.removeMessages(SearchMessageManager.SEARCH_RESULT_PAGE_CHANGE);
			msgQueue.sendMessageDelayed(msg, DELAY_MILLIS);
		}
	}

	private void loadData(int pageIndex) {
		Message msg = msgQueue.obtainMessage(SearchMessageManager.SEARCH, pageIndex, 3 * PAGE_SIZE, this.currentKey);
		msgQueue.removeMessages(SearchMessageManager.SEARCH);
		msgQueue.sendMessageDelayed(msg, DELAY_MILLIS);
	}

	private void unSelected(View view) {
		if (view == null) {
			return;
		}
		// view.setScaleX(ZOOM_OUT);
		// view.setScaleY(ZOOM_OUT);
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
		// view.setScaleX(ZOOM_IN);
		// view.setScaleY(ZOOM_IN);
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			return;
		}
		holder.nameView.setBackgroundResource(R.drawable.media_browser_item_selected);
		holder.nameView.setSelected(true);
		lastSelectedView = view;
	}

}
