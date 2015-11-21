package com.tencent.qq;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

/**
 * 为整个应用程序提供唯一的一个HttpClient对象。 这个对象有一些初始化的属性连接属性，
 * 这些属性可以被HttpGet、HttpPost的属性覆盖
 * 
 * @author admin
 * 
 */
public class HttpClientHelper {

	private static HttpClient _httpClient;

	private HttpClientHelper() {

	}

	/**
	 * 这里返回的HttpClient只能用于HTTP请求，HTTPS还需要对证书做一番处理
	 * @return HttpClient
	 */
	public static synchronized HttpClient getHttpClient() {
		if (_httpClient == null) {
			// 初始化
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setUseExpectContinue(params, true);
			// HttpProtocolParams.setUserAgent(params, )

			// 设置连接管理器的超时
			ConnManagerParams.setTimeout(params, 5000);
			// 设置连接的超时
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			// 设置Socket的超时
			HttpConnectionParams.setSoTimeout(params, 10000);						

			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			schReg.register(new Scheme("https", SSLSocketFactory
					.getSocketFactory(), 443));
			ClientConnectionManager conManager = new ThreadSafeClientConnManager(
					params, schReg);
			_httpClient = new DefaultHttpClient(conManager, params);
		}
		return _httpClient;
	}
}
