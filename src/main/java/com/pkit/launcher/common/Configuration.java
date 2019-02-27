package com.pkit.launcher.common;

import java.util.Locale;

public class Configuration {

	public static String templateId;
	public static String deviceId;
	public static int makeType;
	public static long timestamp;

	/**
	 * 服务器地址
	 */
	private static final String ADDR = "http://180.169.102.150:7070";
//	private static final String ADDR = "http://192.168.2.51:8080";
	/**
	 * 首页
	 */
	private static final String HOME_RECOMMEND_PATH = "/loms/epg/getMainModel?templateId=%1$s&maketype=%2$d";
	/**
	 * 列表页面,左边栏目
	 */
	private static final String MENU_PATH = "/loms/epg/getMenuPageList?parentMenuId=%1$s";
	/**
	 * 列表页面,右边影片列表
	 */
	private static final String ITEM_PATH = "/loms/epg/getContentPageListByMenu?menuId=%1$s&pageNumber=%2$d&pageSize=%3$d";
	/**
	 * 某个栏目下所有更新内容
	 */
	private static final String UPDATE_ITEM_PATH = "/loms/epg/getContentNewList?menuId=%1$s";
	/**
	 * 详情
	 */
	private static final String DETAIL_PATH = "/loms/epg/getContentById?contentId=%1$s";
	/**
	 * 详情页面,推荐内容
	 */
	private static final String DETAIL_RECOMMEND_PATH = "/loms/epg/getContentListByRelation?contentId=%1$s&templateId=%2$s&pageNumber=%3$d&pageSize=%4$d";
	/**
	 * 搜索
	 */
	private static final String SEARCH_PATH = "/loms/epg/getContentPageListBySearch?menuId=%1$s&keycode=%2$s&types=%3$d&templateId=%4$s&pageNumber=%5$d&pageSize=%6$d";
	/**
	 * 筛选条件
	 */
	private static final String SELECTOR_GROUP_PATH = "/loms/epg/getTagList?templateId=%1$s&menuId=%2$s";
	/**
	 * 筛选
	 */
	private static final String SELECTOR_PATH = "/loms/epg/getContentPageListByTags?menuId=%1$s&type=%2$s&templateId=%3$s&pageNumber=%4$d&pageSize=%5$d";

	/**
	 * 点播日志
	 */
	private static final String WATCH_SERIES = "/loms/logger/getWatchSeries";

	/**
	 * 点击次数
	 */
	private static final String CLICK_SERIES = "/loms/logger/getClick";

	/**
	 * 认证
	 */
	private static final String DEVICE_LOGIN = "/loms/terminal/deviceLogin?mac=%1$s";

	/**
	 * 更新
	 */
	private static final String UPGRADE_LIST = "/loms/terminal/getUpgradeInfor?deviceId=%1$s&ruleId=%2$s&versionSeq=%3$d";


	public static String getHomeRecommendUri() {
		String pathAndQuery = String.format(HOME_RECOMMEND_PATH, templateId, makeType);
		return ADDR + pathAndQuery;
	}

	public static String getMenuUri(String parentMenuId) {
		String pathAndQuery = String.format(MENU_PATH, parentMenuId);
		return ADDR + pathAndQuery;
	}

	public static String getItemListUri(String menuId, int pageNumber, int pageSize) {
		String pathAndQuery = String.format(Locale.getDefault(), ITEM_PATH, menuId, pageNumber, pageSize);
		return ADDR + pathAndQuery;
	}

	public static String getItemUpdateListUri(String menuId) {
		String pathAndQuery = String.format(UPDATE_ITEM_PATH, menuId);
		return ADDR + pathAndQuery;
	}

	public static String getDetailUri(String contentId) {
		String pathAndQuery = String.format(DETAIL_PATH, contentId);
		return ADDR + pathAndQuery;
	}

	public static String getDetailRecommendUri(String contentId, int pageNumber, int pageSize) {
		String pathAndQuery = String.format(Locale.getDefault(), DETAIL_RECOMMEND_PATH, contentId, templateId, pageNumber, pageSize);
		return ADDR + pathAndQuery;
	}

	public static String getSearchUri(String menuId, String keycode, int types, int pageNumber, int pageSize) {
		String pathAndQuery = String.format(Locale.getDefault(), SEARCH_PATH, menuId, keycode, types, templateId, pageNumber, pageSize);
		return ADDR + pathAndQuery;
	}

	public static String getSelectorGroupUri(String menuId) {
		String pathAndQuery = String.format(SELECTOR_GROUP_PATH, templateId, menuId);
		return ADDR + pathAndQuery;
	}

	public static String getSelectorUri(String menuId, String types, int pageNumber, int pageSize) {
		String pathAndQuery = String.format(Locale.getDefault(), SELECTOR_PATH, menuId, types, templateId, pageNumber, pageSize);
		return ADDR + pathAndQuery;
	}

	public static String getWatchSeriesUri() {
		return ADDR + WATCH_SERIES;
	}

	public static String getClickUri() {
		return ADDR + CLICK_SERIES;
	}

	public static String getDeviceLoginUri(String mac) {
		String pathAndQuery = String.format(DEVICE_LOGIN, mac);
		return ADDR + pathAndQuery;
	}

	public static String getUpgradeListUri(String deviceId, String ruleId, int versionSeq) {
		String pathAndQuery = String.format(UPGRADE_LIST, deviceId, ruleId, versionSeq);
		return ADDR + pathAndQuery;
	}
}
