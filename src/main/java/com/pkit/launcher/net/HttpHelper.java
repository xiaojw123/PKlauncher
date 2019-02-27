package com.pkit.launcher.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class HttpHelper {
	protected static final String UTF8 = "utf-8";
	protected static final int SO_TIMEOUT = 30000;
	protected static final int CONNECT_TIMEOUT = 5000;
	protected static final int TIMEOUT = 2000;

	public String getContent(String urlStr) throws Exception {
		DefaultHttpClient httpClient = getHttpClient();
		HttpGet httpGet = new HttpGet(urlStr);
		HttpResponse resp = httpClient.execute(httpGet);
		return EntityUtils.toString(resp.getEntity(), UTF8);
	}

	public String getPostContent(String urlStr, String data) throws Exception {
		NameValuePair valuePair = new BasicNameValuePair("message", data);
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(valuePair);
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters);
		HttpPost httpPost = new HttpPost(urlStr);
		httpPost.setEntity(entity);
		DefaultHttpClient httpClient = getHttpClient();
		HttpResponse resp = httpClient.execute(httpPost);
		return EntityUtils.toString(resp.getEntity(), UTF8);
	}

	public InputStream getContentInputStream(String urlStr) throws IOException {
		DefaultHttpClient httpClient = getHttpClient();
		HttpGet httpGet = new HttpGet(urlStr);
		HttpResponse resp = httpClient.execute(httpGet);
		return resp.getEntity().getContent();
	}

	protected DefaultHttpClient getHttpClient() {
		HttpParams params = new BasicHttpParams();
		// params.setParameter("http.protocol.cookie-policy",
		// CookiePolicy.BROWSER_COMPATIBILITY);
		// 设置一些基本参数
		HttpProtocolParams.setHttpElementCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpProtocolParams.setUserAgent(params, "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
				+ "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
		// 超时设置
		/* 从连接池中取连接的超时时间 */
		ConnManagerParams.setTimeout(params, TIMEOUT);
		/* 连接超时 */
		HttpConnectionParams.setConnectionTimeout(params, CONNECT_TIMEOUT);
		/* 请求超时 */
		HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);
		// 设置我们的HttpClient支持HTTP和HTTPS两种模式
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		// 使用线程安全的连接管理来创建HttpClient
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
		DefaultHttpClient client = new DefaultHttpClient(conMgr, params);
		// client.setHttpRequestRetryHandler(new
		// DefaultHttpRequestRetryHandler(2, true)) ;
		return client;
	}
}
