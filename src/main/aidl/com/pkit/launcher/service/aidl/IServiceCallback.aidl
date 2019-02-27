// IContentService.aidl
package com.pkit.launcher.service.aidl;

import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.service.aidl.Detail;
import com.pkit.launcher.service.aidl.HomeItem;
import com.pkit.launcher.service.aidl.TagGroup;

// Declare any non-default types here with import statements

interface IServiceCallback {
	/**
	* @param contentID 完成的id
	* @param startPageIndex 数据起始页面数据,以0开始计数
	* @param count 该id下有多少条记录
	* @param contents 返回记录集合
	 */
	void onLoadComplete(String contentID, int pageNumber,int count, in List<Content> contents);
	
	void onDetailLoadComplete(in Detail detail);
	
	void onLoading(String contentID);
	
	void onFailed(String contentID);

	void onDeviceLoginComplete(in Bundle bundle);

	void onCheckUpgradeComplete(in Bundle bundle);

	void onTagGroupComplete(in List<TagGroup> tagGroups);
}
