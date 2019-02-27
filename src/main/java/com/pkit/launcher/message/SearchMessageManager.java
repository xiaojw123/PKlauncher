package com.pkit.launcher.message;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pkit.launcher.R;
import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.service.aidl.IContentService;
import com.pkit.launcher.service.aidl.TagGroup;
import com.pkit.launcher.utils.APPLog;
import com.pkit.launcher.view.SearchEmptyResultFragment;
import com.pkit.launcher.view.SearchKeyboardFragment;
import com.pkit.launcher.view.SearchResultFragment;
import com.pkit.launcher.view.SearchSelectorFragment;

public class SearchMessageManager extends Handler {
	public static final int SEARCH_CONTENT_TYPE = 0x01;
	public static final int SEARCH_ACTOR_TYPE = 0x02;
	public static final int SEARCH_ALL_TYPE = 0x03;
	/**
	 * 执行搜索
	 */
	public static final int SEARCH = 0x02;
	/**
	 * 完成搜索
	 */
	public static final int RESULT = 0x04;
	/**
	 * 获取相应ID下的筛选条件
	 */
	public static final int LOAD_SELECTOR = 0x0B;
	/**
	 * 搜索模式
	 */
	public static final int SEARCH_MODE_1 = 0x06;
	/**
	 * 筛选模式
	 */
	public static final int SEARCH_MODE_2 = 0x07;
	/**
	 * 更新左上角页码
	 */
	public static final int SEARCH_RESULT_PAGE_CHANGE = 0x0A;
	/**
	 * 更新筛选条件
	 */
	public static final int SEARCH_SELECTOR = 0x0C;
	private FragmentManager fragmentManager;
	private SearchKeyboardFragment keyboardFragment;
	private SearchEmptyResultFragment emptyResultFragment;
	private SearchResultFragment searchResultFragment;
	private SearchSelectorFragment searchSelectorFragment;
	private int currentMode = 0;
	// private Context context;
	/**
	 * 页数
	 */
	private TextView textView1;
	/**
	 * 右上角提示
	 */
	private TextView textView2;
	private String selectorID;
	private String currentSelectionArg;
	private IContentService contentService;
	private ContentServiceCallback callback;
	private List<Content> recommends;

	public SearchMessageManager(Context context, String id, FragmentManager fragmentManager) {
		this.fragmentManager = fragmentManager;
		// this.context = context;
		this.selectorID = id;
	}

	public void setContentService(IContentService contentService) {
		if (contentService == null) {
			return;
		}

		this.contentService = contentService;
		try {
			if (callback == null) {
				callback = new ContentServiceCallback();
			}
			this.contentService.registCallback(callback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void setTextView1(TextView textView1) {
		this.textView1 = textView1;
	}

	public void setTextView2(TextView textView2) {
		this.textView2 = textView2;
	}

	public int getCurrentMode() {
		return currentMode;
	}

	public void setCurrentMode(int currentMode) {
		this.currentMode = currentMode;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(Message msg) {
		int what = msg.what;
		switch (what) {
		case SEARCH:
			String searchKey = (String) msg.obj;
			int pageIndex = msg.arg1;
			int count = msg.arg2;
			search(pageIndex, count, searchKey);
			break;
		case SEARCH_MODE_1:
			if (currentMode != SEARCH_MODE_1) {
				initSearch();
			}
			break;
		case SEARCH_MODE_2:
			if (currentMode != SEARCH_MODE_2) {
				initSelector();
			}
			break;
		case RESULT:
			int startPageIndex = msg.arg1;
			int resultCount = msg.arg2;
			List<Content> resultList = (List<Content>) msg.obj;
			updateResult(startPageIndex, resultCount, resultList);
			break;
		case SEARCH_RESULT_PAGE_CHANGE:
			updatePage(msg.arg1, msg.arg2);
			break;
		case LOAD_SELECTOR:
			loadSelector();
			break;
		case SEARCH_SELECTOR:
			updateSelector((List<TagGroup>) msg.obj);
			break;
		default:
			break;
		}
	}

	private void search(int pageIndex, int count, String key) {
		APPLog.printInfo("search pageIndex:" + pageIndex + "/count:" + count + "/key:" + key + "/searchKey:" + currentSelectionArg);
		if (searchResultFragment != null) {
			searchResultFragment.setFocusable(false);
		}
		if (TextUtils.isEmpty(key)) {
			textView1.setText("大家都在看");
			updateText2(View.VISIBLE);
			if (recommends != null) {
				currentSelectionArg = key;
				searchResultFragment.setCurrentKey(currentSelectionArg);
				searchResultFragment.setResult(recommends.size(), recommends);
				return;
			}
			if (searchResultFragment != null) {
				searchResultFragment.setProgressVisibility(View.VISIBLE);
			}
		}

		try {
			if (currentMode == SEARCH_MODE_1) {
				contentService.search(selectorID, key, SEARCH_CONTENT_TYPE, pageIndex, count);
			} else {
				contentService.selector(selectorID, key, pageIndex, count);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void updateResult(int startPageIndex, int count, List<Content> items) {
		APPLog.printInfo("updateResult startPageIndex:" + startPageIndex + "/count:" + count);
		int page = (count + SearchResultFragment.PAGE_SIZE - 1) / SearchResultFragment.PAGE_SIZE;
		if (TextUtils.isEmpty(currentSelectionArg)) {
			recommends = items;
		} else {
			updatePage(startPageIndex, page);
		}
		updateText2(View.VISIBLE);
		if (count == 0 && !TextUtils.isEmpty(currentSelectionArg)) {
			showEmpty(recommends);
		} else if (startPageIndex == 0) {
			showResult(count, items);
		} else if (startPageIndex > 0) {
			updateResult(startPageIndex, items);
		}
		if (searchResultFragment != null) {
			searchResultFragment.setProgressVisibility(View.GONE);
		}
		if (searchResultFragment != null) {
			searchResultFragment.setFocusable(true);
		}
	}

	private void showEmpty(List<Content> items) {
		boolean isExists = isTopFragment(R.id.search_result_fragment, emptyResultFragment);
		if (!isExists) {
			emptyResultFragment = new SearchEmptyResultFragment();
			addFragment(R.id.search_result_fragment, emptyResultFragment);
		}
		emptyResultFragment.setRecommends(items);
		emptyResultFragment.setMode(currentMode);
		if (currentMode == SEARCH_MODE_1) {
			String source = "搜索结果            <font color='#00d360' size='24'>0</font>";
			Spanned exampleSpanned = Html.fromHtml(source);
			textView1.setText(exampleSpanned);
		} else {
			String source = "筛选结果           <font color='#00d360' size='24'>0</font>";
			Spanned exampleSpanned = Html.fromHtml(source);
			textView1.setText(exampleSpanned);
		}
		updateText2(View.INVISIBLE);
	}

	private void showResult(int count, List<Content> items) {
		boolean isExists = isTopFragment(R.id.search_result_fragment, searchResultFragment);
		if (!isExists) {
			searchResultFragment = new SearchResultFragment();
			searchResultFragment.setHandler(this);
			addFragment(R.id.search_result_fragment, searchResultFragment);
		}
		searchResultFragment.setResult(count, items);
		searchResultFragment.setCurrentKey(currentSelectionArg);
	}

	private void updateResult(int startPageIndex, List<Content> items) {
		searchResultFragment.updateItemList(startPageIndex, items);
	}

	private void initResult() {
		searchResultFragment = new SearchResultFragment();
		searchResultFragment.setHandler(this);
		addFragment(R.id.search_result_fragment, searchResultFragment);
	}

	private void initSearch() {
		initResult();
		recommends = null;
		currentMode = SEARCH_MODE_1;
		keyboardFragment = new SearchKeyboardFragment();
		keyboardFragment.setHandler(this);
		search(0, 8, "");
		textView1.setText("大家都在看");
		updateText2(View.VISIBLE);
		addFragment(R.id.search_keyboard_fragment, keyboardFragment);
	}

	private void initSelector() {
		initResult();
		recommends = null;
		currentMode = SEARCH_MODE_2;
		searchSelectorFragment = new SearchSelectorFragment();
		searchSelectorFragment.setHandler(this);
		search(0, 8, "");
		textView1.setText("大家都在看");
		updateText2(View.VISIBLE);
		addFragment(R.id.search_keyboard_fragment, searchSelectorFragment);
	}

	private void loadSelector() {
		APPLog.printInfo("loadSelector selectorID:" + selectorID);
		if (selectorID == null) {
			return;
		}

		try {
			contentService.getTagGroups(selectorID);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	private void updateSelector(List<TagGroup> tagGroups){
		if (tagGroups != null && tagGroups.size() != 0) {
			searchSelectorFragment.setZoneList(tagGroups.get(0).getTags());
			searchSelectorFragment.setCatList(tagGroups.get(1).getTags());
			searchSelectorFragment.setYearList(tagGroups.get(2).getTags());
		}
	}

	private void updateText2(int visibility) {
		ViewGroup parent = (ViewGroup) textView2.getParent();
		parent.setVisibility(visibility);
		// ImageSpan imgSpan = new ImageSpan(context,
		// R.drawable.search_icon_menu);
		// SpannableStringBuilder spannableBuilder = new
		// SpannableStringBuilder();
		// if (currentMode == SEARCH_MODE_1) {
		// spannableBuilder.append("按 菜单键切换筛选");
		// spannableBuilder.setSpan(imgSpan, 1, 2,
		// SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
		// } else if (currentMode == SEARCH_MODE_2) {
		// spannableBuilder.append("按 菜单键切换搜索");
		// spannableBuilder.setSpan(imgSpan, 1, 2,
		// SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
		// }

		SpannableStringBuilder spannableBuilder = new SpannableStringBuilder();
		if (currentMode == SEARCH_MODE_1) {
			spannableBuilder.append("菜单键切换筛选");
		} else if (currentMode == SEARCH_MODE_2) {
			spannableBuilder.append("菜单键切换搜索");
		}
		textView2.setText(spannableBuilder);
	}

	private void updatePage(int currentpage, int page) {
		if (page == 0) {
			page = 1;
		}
		String currentPageStr = "<font color='#00d360'>" + (currentpage + 1) + "</font>";
		String pageText = currentPageStr + "/" + page;
		textView1.setText(Html.fromHtml(pageText));
	}

	private boolean isTopFragment(int id, Fragment fragment) {
		Fragment preFragment = fragmentManager.findFragmentById(id);
		if (preFragment == null) {
			return false;
		}
		return fragment == preFragment;
	}

	private void addFragment(int id, Fragment fragment) {
		Fragment preFragment = fragmentManager.findFragmentById(id);
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		if (preFragment == null) {
			transaction.add(id, fragment);
		} else if (preFragment != fragment) {
			transaction.replace(id, fragment);
		}
		transaction.commit();
	}

	class ContentServiceCallback extends BaseCallBack {

		@Override
		public void onLoadComplete(String contentID, int startPageIndex, int count, List<Content> contents) throws RemoteException {
			APPLog.printInfo("onLoadComplete key:" + contentID + "/startPageIndex:" + startPageIndex + "/count:" + count);
			currentSelectionArg = contentID;
			Message msg = obtainMessage(RESULT, startPageIndex, count, contents);
			sendMessage(msg);
		}
		
		@Override
		public void onTagGroupComplete(List<TagGroup> tagGroups) throws RemoteException {
			Message msg = obtainMessage(SEARCH_SELECTOR, tagGroups);
			sendMessage(msg);
		}
	}
}
