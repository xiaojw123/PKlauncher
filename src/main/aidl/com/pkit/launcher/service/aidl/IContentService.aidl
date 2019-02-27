// IContentService.aidl
package com.pkit.launcher.service.aidl;

import com.pkit.launcher.service.aidl.Content;
import com.pkit.launcher.service.aidl.Container;
import com.pkit.launcher.service.aidl.Item;
import com.pkit.launcher.service.aidl.Detail;
import com.pkit.launcher.service.aidl.IServiceCallback;
import com.pkit.launcher.service.aidl.TagGroup;

// Declare any non-default types here with import statements

interface IContentService {
   /**
    * 获取首页推荐内容
    * @param contentId
    */
   void loadRecommendContents(int index);
   
   /**
    * 参数待定
    * 获取指定栏目下的子栏目
    */
   void loadContainers(String contentID);
   
   /**
    * 参数待定
    * 获取指定栏目下的影片列表
    */
   void loadItems(String contentID,int startPageIndex, int count);
   
   /**
    * 参数待定
    * 获取影片详情信息
    */
   void loadDetail(String contentID);
   
   /**
    * 参数待定
    * 获取相关联的影片
    */
   void loadRecommendItems(String contentID);
   
   /**
    * 搜索
    */
   void search(String menuId, String key, int type, int startPageIndex, int count);
   
   /**
    * 筛选
    */
   void selector(String menuId, String type, int startPageIndex, int count);
   
   /**
    * 参数待定
    * 收藏记录
    */
   List<Item> getFavorites();
   
   /**
    * 参数待定
    * 增加收藏记录
    */
   void addFavorite(in Detail detail);
   
   /**
    * 参数待定
    * 删除收藏记录
    */
   void deleteFavorite(String contentID);
   
   /**
    * 参数待定
    * 播放记录
    */
   List<Item> getHistories();
   
   /**
    * 参数待定
    * 增加播放记录
    */
   void addHistory(in Detail detail);
   
   /**
    * 参数待定
    * 删除播放记录
    */
   void deleteHistory(String contentID);

   /**
   *设备认证
   */
   void deviceLogin(String mac);
   void checkUpgrade(String url);

   void registCallback(IServiceCallback callback);
   
   void getTagGroups(String menuId);
   
   void loadUpdates(String menuId);
}
