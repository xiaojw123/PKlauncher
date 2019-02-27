package com.pkit.launcher.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.SparseArray;

import com.pkit.launcher.bean.DeviceConfig;
import com.pkit.launcher.bean.PageInfo;
import com.pkit.launcher.bean.Upgrade;
import com.pkit.launcher.common.Configuration;
import com.pkit.launcher.net.HttpHelper;
import com.pkit.launcher.provider.ContentDataProvider;
import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.service.aidl.Detail;
import com.pkit.launcher.service.aidl.IContentService;
import com.pkit.launcher.service.aidl.IServiceCallback;
import com.pkit.launcher.service.aidl.Item;
import com.pkit.launcher.service.aidl.TagGroup;
import com.pkit.launcher.utils.APPLog;
import com.pkit.utils.ParserUtils;

/**
 * @author Richard
 */
public class ContentService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return new ContentInterface();
	}

	class ContentInterface extends IContentService.Stub {
		private IServiceCallback callback;
		private ScheduledThreadPoolExecutor executor;
		private static final int CORE_POOL_SIZE = 2;

		public ContentInterface() {
			executor = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE);
		}

		@Override
		public void registCallback(IServiceCallback callback) throws RemoteException {
			this.callback = callback;
		}

		@Override
		public void loadRecommendContents(int index) throws RemoteException {
			RecommendLoadTask task = new RecommendLoadTask(Configuration.getHomeRecommendUri());
			task.registCallback(callback);
			task.setIndex(index);
			executor.execute(task);
		}

		@Override
		public void loadContainers(String contentID) throws RemoteException {
			MenuLoadTask task = new MenuLoadTask(Configuration.getMenuUri(contentID));
			task.registCallback(callback);
			task.setContentID(contentID);
			executor.execute(task);
		}

		@Override
		public void loadItems(String contentID, int startPageIndex, int count) throws RemoteException {

			ContentLoadTask task = new ContentLoadTask(Configuration.getItemListUri(contentID, startPageIndex, count));
			task.registCallback(callback);
			task.setContentID(contentID);
			task.setStartPageIndex(startPageIndex);
			executor.execute(task);
		}

		@Override
		public void loadDetail(String contentID) throws RemoteException {
			DetailLoadTask task = new DetailLoadTask(Configuration.getDetailUri(contentID));
			task.registCallback(callback);
			task.setContext(getBaseContext());
			executor.execute(task);
		}

		@Override
		public void loadRecommendItems(String contentID) throws RemoteException {
			RelationContentLoadTask task = new RelationContentLoadTask(Configuration.getDetailRecommendUri(contentID, 0, 6));
			task.registCallback(callback);
			task.setContentID(contentID);
			executor.execute(task);
		}

		@Override
		public void search(String menuId, String key, int type, int startPageIndex, int count) throws RemoteException {
			SearchContentLoadTask task = new SearchContentLoadTask(Configuration.getSearchUri(menuId, key, type, startPageIndex, count));
			task.registCallback(callback);
			task.setKey(key);
			task.setPageIndex(startPageIndex);
			executor.execute(task);
		}

		@Override
		public void selector(String menuId, String type, int startPageIndex, int count) throws RemoteException {
			SearchContentLoadTask task = new SearchContentLoadTask(Configuration.getSelectorUri(menuId, type, startPageIndex, count));
			task.registCallback(callback);
			task.setKey(type);
			task.setPageIndex(startPageIndex);
			executor.execute(task);
		}

		@Override
		public List<Item> getFavorites() throws RemoteException {
			String selection = "isFavorite=?";
			String[] selectionArgs = new String[] { "1" };
			String sortOrder = "insertTime DESC";
			Cursor cursor = getContentResolver().query(ContentDataProvider.ITEM_URI, null, selection, selectionArgs, sortOrder);

			ArrayList<Item> items = new ArrayList<Item>();
			boolean flag = cursor.moveToFirst();
			while (flag) {
				String parentID = cursor.getString(ContentDataProvider.Item.PARENT_ID_COLUMN);
				String contentID = cursor.getString(ContentDataProvider.Item.CONTENT_ID_COLUMN);
				String name = cursor.getString(ContentDataProvider.Item.NAME_COLUMN);
				long insertTime = cursor.getLong(ContentDataProvider.Item.INSERT_TIME_COLUMN);
				int playTime = cursor.getInt(ContentDataProvider.Item.PLAY_TIME_COLUMN);
				String img1 = cursor.getString(ContentDataProvider.Item.IMG_URL_1_COLUMN);
				String img2 = cursor.getString(ContentDataProvider.Item.IMG_URL_2_COLUMN);
				String img3 = cursor.getString(ContentDataProvider.Item.IMG_URL_3_COLUMN);
				String img4 = cursor.getString(ContentDataProvider.Item.IMG_URL_4_COLUMN);
				String img5 = cursor.getString(ContentDataProvider.Item.IMG_URL_5_COLUMN);

				ArrayList<String> imgList = new ArrayList<String>();
				if (img1 != null || !"".equals(img1)) {
					imgList.add(img1);
				}
				if (img2 != null || !"".equals(img2)) {
					imgList.add(img2);
				}
				if (img3 != null || !"".equals(img3)) {
					imgList.add(img3);
				}
				if (img4 != null || !"".equals(img4)) {
					imgList.add(img4);
				}
				if (img5 != null || !"".equals(img5)) {
					imgList.add(img5);
				}
				Item item = new Item();
				item.parentID = parentID;
				item.contentID = contentID;
				item.name = name;
				item.insertTime = insertTime;
				item.playTime = playTime;
				item.imgPaths = imgList;
				items.add(item);
				flag = cursor.moveToNext();
			}
			cursor.close();
			return items;
		}

		@Override
		public void addFavorite(Detail detail) throws RemoteException {
			String selection = "isFavorite=? and contentID=?";
			String[] selectionArgs = new String[] { "1", detail.contentID };
			String sortOrder = "insertTime DESC";
			Cursor cursor = getContentResolver().query(ContentDataProvider.ITEM_URI, null, selection, selectionArgs, sortOrder);
			if (cursor.moveToFirst()) {
				cursor.close();
				return;
			}
			ContentValues values = new ContentValues();
			values.put(ContentDataProvider.Item.PARENT_ID, detail.contentID);
			values.put(ContentDataProvider.Item.CONTENT_ID, detail.contentID);
			values.put(ContentDataProvider.Item.NAME, detail.name);
			values.put(ContentDataProvider.Item.IS_FAVORITE, "1");
			values.put(ContentDataProvider.Item.IS_HISTORY, "0");
			values.put(ContentDataProvider.Item.IS_LOCKED, "0");
			values.put(ContentDataProvider.Item.INSERT_TIME, System.currentTimeMillis());
			values.put(ContentDataProvider.Item.PLAY_TIME, detail.seekPosition);
			values.put(ContentDataProvider.Item.POSITION, detail.position);
			int size = (detail.imgPaths == null ? 0 : detail.imgPaths.size());
			for (int i = 0; i < size; i++) {
				String url = detail.imgPaths.get(i);
				if (i == 0) {
					values.put(ContentDataProvider.Item.IMG_URL_1, url);
				}
				if (i == 1) {
					values.put(ContentDataProvider.Item.IMG_URL_2, url);
				}
				if (i == 2) {
					values.put(ContentDataProvider.Item.IMG_URL_3, url);
				}
				if (i == 3) {
					values.put(ContentDataProvider.Item.IMG_URL_4, url);
				}
				if (i == 4) {
					values.put(ContentDataProvider.Item.IMG_URL_5, url);
				}
			}
			getContentResolver().insert(ContentDataProvider.ITEM_URI, values);
		}

		@Override
		public void deleteFavorite(String contentID) throws RemoteException {
			String selection = "contentID=?";
			String[] selectionArgs = new String[] { contentID };
			Cursor cursor = getContentResolver().query(ContentDataProvider.ITEM_URI, null, selection, selectionArgs, null);
			boolean isHistory = false;
			boolean flag = cursor.moveToFirst();
			if (flag) {
				isHistory = (cursor.getInt(ContentDataProvider.Item.IS_HISTORY_COLUMN) == 1);
				cursor.close();
			}

			if (isHistory) {
				ContentValues values = new ContentValues();
				values.put(ContentDataProvider.Item.IS_FAVORITE, "0");
				getContentResolver().update(ContentDataProvider.ITEM_URI, values, selection, selectionArgs);
			} else {
				getContentResolver().delete(ContentDataProvider.ITEM_URI, selection, selectionArgs);
			}
		}

		@Override
		public List<Item> getHistories() throws RemoteException {
			String selection = "isHistory=?";
			String[] selectionArgs = new String[] { "1" };
			String sortOrder = "insertTime DESC";
			Cursor cursor = getContentResolver().query(ContentDataProvider.ITEM_URI, null, selection, selectionArgs, sortOrder);

			ArrayList<Item> items = new ArrayList<Item>();
			boolean flag = cursor.moveToFirst();
			while (flag) {
				String parentID = cursor.getString(ContentDataProvider.Item.PARENT_ID_COLUMN);
				String contentID = cursor.getString(ContentDataProvider.Item.CONTENT_ID_COLUMN);
				String name = cursor.getString(ContentDataProvider.Item.NAME_COLUMN);
				long insertTime = cursor.getLong(ContentDataProvider.Item.INSERT_TIME_COLUMN);
				int playTime = cursor.getInt(ContentDataProvider.Item.PLAY_TIME_COLUMN);
				String img1 = cursor.getString(ContentDataProvider.Item.IMG_URL_1_COLUMN);
				String img2 = cursor.getString(ContentDataProvider.Item.IMG_URL_2_COLUMN);
				String img3 = cursor.getString(ContentDataProvider.Item.IMG_URL_3_COLUMN);
				String img4 = cursor.getString(ContentDataProvider.Item.IMG_URL_4_COLUMN);
				String img5 = cursor.getString(ContentDataProvider.Item.IMG_URL_5_COLUMN);

				ArrayList<String> imgList = new ArrayList<String>();
				if (img1 != null || !"".equals(img1)) {
					imgList.add(img1);
				}
				if (img2 != null || !"".equals(img2)) {
					imgList.add(img2);
				}
				if (img3 != null || !"".equals(img3)) {
					imgList.add(img3);
				}
				if (img4 != null || !"".equals(img4)) {
					imgList.add(img4);
				}
				if (img5 != null || !"".equals(img5)) {
					imgList.add(img5);
				}
				Item item = new Item();
				item.parentID = parentID;
				item.contentID = contentID;
				item.name = name;
				item.insertTime = insertTime;
				item.playTime = playTime;
				item.imgPaths = imgList;
				items.add(item);
				flag = cursor.moveToNext();
			}
			cursor.close();
			return items;
		}

		@Override
		public void addHistory(Detail detail) throws RemoteException {
			APPLog.printInfo("addHistory detail:" + detail.toString());
			ContentValues values = new ContentValues();
			String selection = "contentID=?";
			String[] selectionArgs = new String[] { detail.contentID };
			Cursor cursor = getContentResolver().query(ContentDataProvider.ITEM_URI, null, selection, selectionArgs, null);
			if (cursor.moveToFirst()) {
				values.put(ContentDataProvider.Item.PLAY_TIME, detail.seekPosition);
				values.put(ContentDataProvider.Item.POSITION, detail.position);
				values.put(ContentDataProvider.Item.IS_HISTORY, 1);
				values.put(ContentDataProvider.Item.INSERT_TIME, System.currentTimeMillis());
				getContentResolver().update(ContentDataProvider.ITEM_URI, values, selection, selectionArgs);
				cursor.close();
				return;
			}
			values.put(ContentDataProvider.Item.PARENT_ID, detail.contentID);
			values.put(ContentDataProvider.Item.CONTENT_ID, detail.contentID);
			values.put(ContentDataProvider.Item.NAME, detail.name);
			values.put(ContentDataProvider.Item.IS_HISTORY, "1");
			values.put(ContentDataProvider.Item.IS_FAVORITE, "0");
			values.put(ContentDataProvider.Item.IS_LOCKED, "0");
			values.put(ContentDataProvider.Item.INSERT_TIME, System.currentTimeMillis());
			values.put(ContentDataProvider.Item.PLAY_TIME, detail.seekPosition);
			values.put(ContentDataProvider.Item.POSITION, detail.position);
			int size = (detail.imgPaths == null ? 0 : detail.imgPaths.size());
			for (int i = 0; i < size; i++) {
				String url = detail.imgPaths.get(i);
				if (i == 0) {
					values.put(ContentDataProvider.Item.IMG_URL_1, url);
				}
				if (i == 1) {
					values.put(ContentDataProvider.Item.IMG_URL_2, url);
				}
				if (i == 2) {
					values.put(ContentDataProvider.Item.IMG_URL_3, url);
				}
				if (i == 3) {
					values.put(ContentDataProvider.Item.IMG_URL_4, url);
				}
				if (i == 4) {
					values.put(ContentDataProvider.Item.IMG_URL_5, url);
				}
			}
			getContentResolver().insert(ContentDataProvider.ITEM_URI, values);
		}

		@Override
		public void deleteHistory(String contentID) throws RemoteException {
			String selection = "contentID=?";
			String[] selectionArgs = new String[] { contentID };
			Cursor cursor = getContentResolver().query(ContentDataProvider.ITEM_URI, null, selection, selectionArgs, null);
			boolean isFavorite = false;
			if (cursor.moveToFirst()) {
				isFavorite = (cursor.getInt(ContentDataProvider.Item.IS_FAVORITE_COLUMN) == 1);
				cursor.close();
			}
			if (isFavorite) {
				ContentValues values = new ContentValues();
				values.put(ContentDataProvider.Item.IS_HISTORY, "0");
				getContentResolver().update(ContentDataProvider.ITEM_URI, values, selection, selectionArgs);
			} else {
				getContentResolver().delete(ContentDataProvider.ITEM_URI, selection, selectionArgs);
			}
		}

		@Override
		public void deviceLogin(String mac) throws RemoteException {
			DeviceLoginTask task = new DeviceLoginTask(Configuration.getDeviceLoginUri(mac));
			task.registCallback(callback);
			executor.execute(task);
		}

		@Override
		public void checkUpgrade(String url) throws RemoteException {
			CheckUpgradeTask task = new CheckUpgradeTask(url);
			task.registCallback(callback);
			executor.execute(task);
		}

		public boolean isFavorite(Context context, Detail detail) {
			String selection = "contentID=? and isFavorite=?";
			String[] selectionArgs = new String[] { detail.contentID, "1" };
			Cursor cursor = context.getContentResolver().query(ContentDataProvider.ITEM_URI, null, selection, selectionArgs, null);
			boolean flag = cursor.moveToFirst();
			if (flag) {
				detail.isFavorite = 1;
			}
			return flag;
		}

		public boolean isHistory(Context context, Detail detail) {
			String selection = "contentID=? and isHistory=?";
			String[] selectionArgs = new String[] { detail.contentID, "1" };
			Cursor cursor = context.getContentResolver().query(ContentDataProvider.ITEM_URI, null, selection, selectionArgs, null);
			boolean flag = cursor.moveToFirst();
			if (flag) {
				int time = cursor.getInt(ContentDataProvider.Item.PLAY_TIME_COLUMN);
				int position = cursor.getInt(ContentDataProvider.Item.POSITION_COLUMN);

				detail.seekPosition = time;
				detail.position = position;
			}
			return flag;
		}

		@Override
		public void getTagGroups(String menuId) throws RemoteException {
			SelectorroupLoadTask task = new SelectorroupLoadTask(Configuration.getSelectorGroupUri(menuId));
			task.setCallback(callback);
			executor.execute(task);
		}

		@Override
		public void loadUpdates(String menuId) throws RemoteException {
			ContentNewLoadTask task = new ContentNewLoadTask(Configuration.getItemUpdateListUri(menuId));
			task.setMenuID(menuId);
			task.registCallback(callback);
			executor.execute(task);
		}
	}

	/**
	 * 首页推荐位
	 */
	class RecommendLoadTask implements Runnable {
		private IServiceCallback callback;
		private String url;
		private int index;

		public RecommendLoadTask(String url) {
			super();
			this.url = url;
			APPLog.printInfo("首页:" + url);
		}

		public void registCallback(IServiceCallback callback) throws RemoteException {
			this.callback = callback;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		@Override
		public void run() {
			try {
				HttpHelper helper = new HttpHelper();
				String data = helper.getContent(url);
				SparseArray<ArrayList<Content>> modelSeats = ParserUtils.parseHomeData(data);
				if (modelSeats == null && callback != null) {
					callback.onFailed("0");
					return;
				}
				List<Content> contents = modelSeats.get(index);
				if (callback != null) {
					callback.onLoadComplete("0", 0, contents.size(), contents);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 影片列表
	 */
	class ContentLoadTask implements Runnable {
		private IServiceCallback callback;
		private String url;
		private String contentID;
		private int pageIndex;

		public ContentLoadTask(String url) {
			super();
			this.url = url;
			APPLog.printInfo("影片列表:" + url);
		}

		public void registCallback(IServiceCallback callback) throws RemoteException {
			this.callback = callback;
		}

		public void setContentID(String id) {
			contentID = id;
		}

		public void setStartPageIndex(int startPageIndex) {
			pageIndex = startPageIndex;
		}

		@Override
		public void run() {
			try {
				HttpHelper helper = new HttpHelper();
				String data = helper.getContent(url);
				PageInfo info = ParserUtils.convertDataToPageInfo(data);
				List<Content> contents = ParserUtils.convertDataToContent(data);
				if (callback != null) {
					callback.onLoadComplete(contentID, pageIndex, info.getTotalNumber(), contents);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 详情
	 */
	class DetailLoadTask implements Runnable {
		private IServiceCallback callback;
		private String url;
		private Context context;
		private String contentID;

		public DetailLoadTask(String url) {
			super();
			this.url = url;
			APPLog.printInfo("详情:" + url);
		}

		public void registCallback(IServiceCallback callback) throws RemoteException {
			this.callback = callback;
		}

		public void setContext(Context context) {
			this.context = context;
		}

		public void setContentID(String contentID) {
			this.contentID = contentID;
		}

		@Override
		public void run() {
			try {
				HttpHelper helper = new HttpHelper();
				String data = helper.getContent(url);
				Detail detail = ParserUtils.parseDetail(data);
				if (callback != null) {
					if (detail != null) {
						isHistory(context, detail);
						isFavorite(context, detail);
						callback.onDetailLoadComplete(detail);
					} else {
						callback.onFailed(contentID);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private boolean isFavorite(Context context, Detail detail) {
			String selection = "contentID=? and isFavorite=?";
			String[] selectionArgs = new String[] { detail.contentID, "1" };
			Cursor cursor = context.getContentResolver().query(ContentDataProvider.ITEM_URI, null, selection, selectionArgs, null);
			boolean flag = cursor.moveToFirst();
			if (flag) {
				detail.isFavorite = 1;
			}
			return flag;
		}

		private boolean isHistory(Context context, Detail detail) {
			String selection = "contentID=? and isHistory=?";
			String[] selectionArgs = new String[] { detail.contentID, "1" };
			Cursor cursor = context.getContentResolver().query(ContentDataProvider.ITEM_URI, null, selection, selectionArgs, null);
			boolean flag = cursor.moveToFirst();
			if (flag) {
				int time = cursor.getInt(ContentDataProvider.Item.PLAY_TIME_COLUMN);
				int position = cursor.getInt(ContentDataProvider.Item.POSITION_COLUMN);

				detail.seekPosition = time;
				detail.position = position;
			}
			return flag;
		}

	}

	/**
	 * 左边列表
	 */
	class MenuLoadTask implements Runnable {
		private IServiceCallback callback;
		private String url;
		private String contentID;

		public MenuLoadTask(String url) {
			super();
			this.url = url;
			APPLog.printInfo("列表栏目:" + url);
		}

		public void registCallback(IServiceCallback callback) throws RemoteException {
			this.callback = callback;
		}

		public void setContentID(String contentID) {
			this.contentID = contentID;
		}

		@Override
		public void run() {
			try {
				HttpHelper helper = new HttpHelper();
				String data = helper.getContent(url);
				PageInfo pageInfo = ParserUtils.convertDataToPageInfo(data);
				List<Content> menus = ParserUtils.converDataToMenu(data);
				if (callback != null) {
					callback.onLoadComplete(contentID, 0, pageInfo.getTotalNumber(), menus);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 某个栏目下的更新内容
	 */
	class ContentNewLoadTask implements Runnable {
		private IServiceCallback callback;
		private String url;
		private String menuID;

		public ContentNewLoadTask(String url) {
			super();
			this.url = url;
			APPLog.printInfo("更新列表:" + url);
		}

		public void registCallback(IServiceCallback callback) throws RemoteException {
			this.callback = callback;
		}

		public void setMenuID(String menuID) {
			this.menuID = menuID;
		}

		@Override
		public void run() {
			try {
				HttpHelper helper = new HttpHelper();
				String data = helper.getContent(url);
				List<Content> newContents = ParserUtils.convertDataToNewContent(data);
				if (callback != null) {
					callback.onLoadComplete(menuID, 0, newContents.size(), newContents);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 搜索内容
	 */
	class SearchContentLoadTask implements Runnable {
		private IServiceCallback callback;
		private String url;
		private String key;
		private int pageIndex;

		public SearchContentLoadTask(String url) {
			super();
			this.url = url;
			APPLog.printInfo("搜索:" + url);
		}

		public void registCallback(IServiceCallback callback) throws RemoteException {
			this.callback = callback;
		}

		public void setPageIndex(int pageIndex) {
			this.pageIndex = pageIndex;
		}

		@Override
		public void run() {
			try {
				HttpHelper helper = new HttpHelper();
				String data = helper.getContent(url);
				PageInfo pageInfo = ParserUtils.convertDataToPageInfo(data);
				List<Content> searchContents = ParserUtils.convertDataToContent(data);
				if (callback != null) {
					callback.onLoadComplete(key, pageIndex, pageInfo.getTotalNumber(), searchContents);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void setKey(String key) {
			this.key = key;
		}
	}

	/**
	 * 详情推荐位
	 */
	class RelationContentLoadTask implements Runnable {
		private IServiceCallback callback;
		private String url;
		private String contentID;

		public RelationContentLoadTask(String url) {
			super();
			this.url = url;
			APPLog.printInfo("相关影片:" + url);
		}

		public void registCallback(IServiceCallback callback) throws RemoteException {
			this.callback = callback;
		}

		@Override
		public void run() {
			try {
				HttpHelper helper = new HttpHelper();
				String data = helper.getContent(url);
				// PageInfo pageInfo = ParserUtils.convertDataToPageInfo(data);
				List<Content> recommendContents = ParserUtils.convertDataToContent(data);
				if (callback != null) {
					callback.onLoadComplete(contentID, 0, recommendContents.size(), recommendContents);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void setContentID(String contentID) {
			this.contentID = contentID;
		}
	}

	/**
	 * 筛选条件
	 */
	class SelectorroupLoadTask implements Runnable {
		private String url;
		private List<TagGroup> tagGroups;
		private IServiceCallback callback;

		public SelectorroupLoadTask(String url) {
			super();
			this.url = url;
			APPLog.printInfo("筛选条件:" + url);
		}

		public void setCallback(IServiceCallback callback) {
			this.callback = callback;
		}

		public List<TagGroup> getTagGroups() {
			return tagGroups;
		}

		@Override
		public void run() {
			try {
				HttpHelper helper = new HttpHelper();
				String data = helper.getContent(url);
				tagGroups = ParserUtils.convertDataToTagGroup(data);
				if (callback != null) {
					callback.onTagGroupComplete(tagGroups);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	class DeviceLoginTask implements Runnable {
		private IServiceCallback callback;
		private String url;

		public DeviceLoginTask(String url) {
			super();
			this.url = url;
			APPLog.printInfo("登录:" + url);
		}

		public void registCallback(IServiceCallback callback) throws RemoteException {
			this.callback = callback;
		}

		@Override
		public void run() {
			try {
				HttpHelper helper = new HttpHelper();
				String data = helper.getContent(url);
				DeviceConfig deviceConfig = ParserUtils.convertDataToDeviceConfig(data);
				Bundle bundle = new Bundle();
				Configuration.templateId = deviceConfig.getTemplateId();
				Configuration.deviceId = deviceConfig.getDeviceId();
				Configuration.makeType = deviceConfig.getMakeType();
				Configuration.timestamp = deviceConfig.getTimestamp();
				bundle.putParcelable("deviceLogin", deviceConfig);
				callback.onDeviceLoginComplete(bundle);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class CheckUpgradeTask implements Runnable {
		private  IServiceCallback callback;
		private String url;

		public CheckUpgradeTask(String url) {
			super();
			this.url = url;
		}
		public void registCallback(IServiceCallback callback) throws RemoteException {
			this.callback = callback;
		}
		@Override
		public void run() {
			try {
				HttpHelper helper = new HttpHelper();
				String data = helper.getContent(url);
				Bundle bundle = new Bundle();
				if (!TextUtils.isEmpty(data) && data.contains("\"code\":1")) {
					Upgrade upgrade = ParserUtils.parseUpgrade(data);
					bundle.putParcelable("upgradeinfo",upgrade);
				}
				callback.onCheckUpgradeComplete(bundle);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
