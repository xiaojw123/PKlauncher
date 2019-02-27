package com.pkit.launcher.utils;

import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;

//web JS接口
public class WebJsInterface {
	
	private Handler mainHandler;
	
	public WebJsInterface(Handler mainHandler){
		this.mainHandler=mainHandler;
	}
	
	//打开详情页
	@JavascriptInterface
	public void openDetail(String id){
		Message msg = new Message();
		msg.what = MessageStatus.OpenDetail;
		msg.obj = id;
		mainHandler.sendMessage(msg);        
	}
	
	//网页返回
	@JavascriptInterface
	public void webBack(){
		mainHandler.sendEmptyMessage(MessageStatus.WebBack); 
	}
		
}
