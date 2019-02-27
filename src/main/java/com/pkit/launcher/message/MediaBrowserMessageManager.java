package com.pkit.launcher.message;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pkit.launcher.service.aidl.Container;
import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.service.aidl.IContentService;
import com.pkit.launcher.view.MediaBrowserGridView;
import com.pkit.launcher.view.MediaBrowserNavList;
import com.pkit.launcher.view.PageGridView;

public class MediaBrowserMessageManager extends Handler {
	/**
	 * 加载左边栏目列表消息
	 */
	public static final int LOAD_MENU_LIST = 0x01;
	/**
	 * 加载指定栏目下影片列表消息
	 */
	public static final int LOAD_ITEM_LIST = 0x02;
	// public static final int UPDATE_PAGE_NUMBER = 0x03;
	/**
	 * 更新页数信息消息
	 */
	public static final int UPDATE_PAGE = 0x04;
	/**
	 * 更新影片总数消息
	 */
	// public static final int UPDATE_ITEM_COUNT = 0x05;
	/**
	 * 更新栏目列表消息
	 */
	public static final int UPDATE_MENU_LIST = 0x06;
	/**
	 * 更新影片列表消息
	 */
	public static final int UPDATE_ITEM_LIST = 0x07;
	/**
	 * 页数提示
	 */
	private TextView pageView;
	/**
	 * 影片数提示
	 */
	private TextView itemCountView;
	/**
	 * 左边选择向导
	 */
	private MediaBrowserNavList navList;
	/**
	 * 影片列表
	 */
	private MediaBrowserGridView itemList;

	private IContentService contentService;
	private Container currentMenu;
	private Container currentParentMenu;
	private ContentServiceCallback callback;
	private ProgressBar loadingBar;

	public MediaBrowserMessageManager(Context context, TextView pageView, TextView itemCountView, RelativeLayout navPanelView, PageGridView videoListView,
			ProgressBar loadingBar) {
		super();
		this.pageView = pageView;
		this.itemCountView = itemCountView;
		this.loadingBar = loadingBar;
		this.navList = new MediaBrowserNavList(context, navPanelView, this);
		this.itemList = new MediaBrowserGridView(context, videoListView, this);
		navList.setItemList(itemList);
	}

	public void setContentService(IContentService service) {
		this.contentService = service;
		if (this.contentService == null) {
			return;
		}
		try {
			if (callback == null) {
				callback = new ContentServiceCallback();
			}
			this.contentService.registCallback(callback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(Message msg) {
		if (this.contentService == null) {
			return;
		}
		int what = msg.what;
		switch (what) {
		case LOAD_MENU_LIST:
			currentParentMenu = (Container) msg.obj;
			loadMenuList(currentParentMenu);
			break;
		case LOAD_ITEM_LIST:
			currentMenu = (Container) msg.obj;
			loadItemList(currentMenu, msg.arg1, msg.arg2);
			break;
		case UPDATE_PAGE:
			updatePage(msg.arg1, msg.arg2);
			break;
		case UPDATE_ITEM_LIST:
			updateItemList(msg.arg1, msg.arg2, (ArrayList<Content>) msg.obj);
			break;
		case UPDATE_MENU_LIST:
			updateMenuList((ArrayList<Content>) msg.obj);
			break;
		default:
			break;
		}
	}

	private void loadMenuList(Container container) {
		try {
			navList.setParentContainer(container);
			contentService.loadContainers(container.contentID);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void loadItemList(Container container, int startPageIndex, int count) {
		try {
			itemList.setContainer(container);
			contentService.loadItems(container.contentID, startPageIndex, count);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (loadingBar.getVisibility() != View.VISIBLE) {
			loadingBar.setVisibility(View.VISIBLE);
			loadingBar.bringToFront();
		}
	}

	private void updatePage(int currentpage, int page) {
		if (page == 0) {
			page = 1;
		}
		String currentPageStr = "<font color='#00d360'>" + (currentpage + 1) + "</font>";
		String pageText = currentPageStr + "/" + page;
		pageView.setText(Html.fromHtml(pageText));
	}

	private void updateCount(int count) {
		String countText = "共" + count + "部";
		itemCountView.setText(countText);
	}

	private void updateMenuList(ArrayList<Content> menus) {
		navList.addCategoryList(menus);
	}

	private void updateItemList(int startPageIndex, int count, ArrayList<Content> items) {
		int pageCount = (count + MediaBrowserGridView.PAGE_SIZE - 1) / MediaBrowserGridView.PAGE_SIZE;
		updatePage(startPageIndex, pageCount);
		updateCount(count);
		itemList.updateItemList(startPageIndex, count, items);
		if (startPageIndex == 0) {
			navList.requestFocus();
		}
		itemList.setFocusable(true);
		if (loadingBar.getVisibility() == View.VISIBLE) {
			loadingBar.setVisibility(View.GONE);
		}
	}

	class ContentServiceCallback extends BaseCallBack {

		@Override
		public void onLoadComplete(String contentID, int startPageIndex, int count, List<Content> contents) throws RemoteException {
			if (contentID == null) {
				return;
			}

			if (currentMenu != null && contentID.equals(currentMenu.contentID)) {
				Message msg = obtainMessage(UPDATE_ITEM_LIST, startPageIndex, count, contents);
				sendMessage(msg);
			} else if (currentParentMenu != null && contentID.equals(currentParentMenu.contentID)) {
				Message msg = obtainMessage(UPDATE_MENU_LIST, contents);
				sendMessage(msg);
			}
		}
	}
}
