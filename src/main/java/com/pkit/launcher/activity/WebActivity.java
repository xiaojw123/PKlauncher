package com.pkit.launcher.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pkit.launcher.R;
import com.pkit.launcher.utils.MessageStatus;
import com.pkit.launcher.utils.WebJsInterface;


public class WebActivity extends BaseActivity {
	public  WebView mWebView = null;
	private static final String TAG = "WebActivity";
	private ProgressBar pb;  
	private String url;
	public static final String WEB_URL = "webUrl";
	
	private Handler mainHandler = new Handler(){  
        @Override  
        public void handleMessage(Message msg){  
        	int re = msg.what;      	
        	switch(re){
	        	case MessageStatus.OpenDetail:
	        		String id = msg.obj.toString();
	        		openDetail(id);
	        		break;
	        	case MessageStatus.WebBack:
	        		webBack();
	        		break;
        	}

        }
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_layout);
		
		pb = (ProgressBar) findViewById(R.id.pb);  
	    pb.setMax(100);
	    
		mWebView = (WebView) findViewById(R.id.web_webView);
		
		url = getIntent().getStringExtra(WEB_URL);
		
		initLoadWeb();
	}
	
	
	//初始化加载网页
	private void initLoadWeb(){		
		Log.i(TAG,"webUrl="+url);
		//mWebView.set
		setWebView(url, this);	
	}
	
	//调用JS 方法
	private void runJsFunction(String funStr){
		Log.i(TAG,"runJsFunction="+funStr);
		mWebView.loadUrl("javascript:"+funStr);
	}
	
	// 捕捉返回键
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			Log.d(TAG, "keyCode="+keyCode);
			// 为xxx的javascript方法    
			switch(keyCode){
				case KeyEvent.KEYCODE_VOLUME_UP:
				case KeyEvent.KEYCODE_VOLUME_DOWN:
				case KeyEvent.KEYCODE_MEDIA_PLAY:
				case KeyEvent.KEYCODE_MENU:
					mWebView.loadUrl("javascript:XEpg.Nav.keyHandle("+keyCode+")"); 
					return true;
				case KeyEvent.KEYCODE_BACK:
					mWebView.loadUrl("javascript:XEpg.Nav.keyHandle(8)"); 
					return true;
				default:
					return super.onKeyDown(keyCode, event);					
			}
			/*
			KeyEvent tempEvent=null;
			if(keyCode == KeyEvent.KEYCODE_BACK){			
					if(mWebView.canGoBack()) {
						mWebView.goBack();
						return false;
					}else{
						finish();// 关闭activity
						return true;
					} 
			}
			return super.onKeyDown(keyCode, event);
			*/
		}
		
	    
		
		// 装载浏览器
		@SuppressLint("NewApi")
		public void setWebView(final String url, final Context context) {
			WebSettings webSettings = mWebView.getSettings();
			webSettings.setJavaScriptEnabled(true);

			//自动适应屏幕宽高
			webSettings.setUseWideViewPort(true); 
			webSettings.setLoadWithOverviewMode(true); 
			
			mWebView.addJavascriptInterface(new WebJsInterface(mainHandler),"AppJs");
			// 设置浏览器为透明
			mWebView.setBackgroundColor(0); 
			//加载需要显示的网页 
			mWebView.loadUrl(url);
			// 不让连接跳出浏览器
			mWebView.setWebViewClient(new WebViewClient(){ 
				public boolean shouldOverrideUrlLoading(WebView view, String url) { 	            
					pb.setVisibility(View.VISIBLE);
					Log.i(TAG, "loadUrl:"+url);
					view.loadUrl(url); 
					return true; 
				} 
				
	            public void onPageStarted(WebView view, String url, Bitmap favicon) {
	                Log.i(TAG, "-MyWebViewClient->onPageStarted()--");
					view.getSettings().setBlockNetworkImage(true); //阻塞图片
	                super.onPageStarted(view, url, favicon);
	            }
	            
	            public void onPageFinished(WebView view, String url) {
		             Log.i(TAG, "-MyWebViewClient->onPageFinished()--");
					 view.getSettings().setBlockNetworkImage(false); //加载图片
		            super.onPageFinished(view, url);
	            }
	            
	            
	            public void onReceivedError(WebView view, int errorCode,
	                            String description, String failingUrl) {
		            if(errorCode == -2){
		            	if(mWebView.canGoBack()) {
		            		Log.i(TAG, "-MyWebViewClient->onReceivedError() go back");
							mWebView.goBack();
						}else{
							Log.i(TAG, "-MyWebViewClient->onReceivedError() ConfirmExit");
							Toast.makeText(WebActivity.this,"网页无法打开",Toast.LENGTH_LONG).show();
							//ConfirmExit();// 按了返回键，但已经不能返回，则执行退出确认
						}
		            }else{
		              Log.i(TAG, "-MyWebViewClient->onReceivedError()--\n errorCode="+errorCode+" \ndescription="+description+" \nfailingUrl="+failingUrl);
		              //这里进行无网络或错误处理，具体可以根据errorCode的值进行判断，做跟详细的处理。
		              String errorHtml = "<html><body><h1>Page not find!</h1><br/><div>"+"errorCode="+errorCode+" ,description="+description+"</div></body></html>";
		              view.loadData(errorHtml, "text/html", "UTF-8");  
		            }
		            super.onReceivedError(view, errorCode, description, failingUrl);
	            }
			});


			
			mWebView.setWebChromeClient(new WebChromeClient() {
				//加载进度条
				public void onProgressChanged(WebView view, int progress) {
			        pb.setProgress(progress);  
			        if(progress==100){  
			            pb.setVisibility(View.GONE); 
			        }  
			        super.onProgressChanged(view, progress);  
				}

				// 弹出alert
				@Override
				public boolean onJsAlert(WebView view, String url, String message,
						final android.webkit.JsResult result) {
					new AlertDialog.Builder(context)
							.setMessage(message)
							.setPositiveButton(android.R.string.ok,
									new AlertDialog.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int which) {
											result.confirm();
										}
									}).setCancelable(false).create().show();

					return true;
				};
				
				 @Override
				 public void onShowCustomView(View view, CustomViewCallback callback) {
				       // TODO Auto-generated method stub
					 Log.i(TAG,"onShowCustomView");
				 }   
			});
			// 同步cookie
			/*
			 * CookieSyncManager.createInstance(this); CookieManager cookieManager =
			 * CookieManager.getInstance(); cookieManager.setCookie(url, "");
			 * cookieManager.setAcceptCookie(true);
			 * CookieSyncManager.getInstance().sync();
			 */
		}
		
		//打开详情页
		private void openDetail(String id){
			// 跳转到详情页
			Intent intent = new Intent(WebActivity.this, DetailsActivity.class);
			intent.putExtra(DetailsActivity.CONTENT_ID,id);
			startActivity(intent);
		}
		
		//网页返回
		private void webBack(){
			if(mWebView.canGoBack()) {
				mWebView.goBack();
			}else{
				finish();// 关闭activity
			} 
		}
}
