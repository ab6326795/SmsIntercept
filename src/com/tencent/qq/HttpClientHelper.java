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
 * Ϊ����Ӧ�ó����ṩΨһ��һ��HttpClient���� ���������һЩ��ʼ���������������ԣ�
 * ��Щ���Կ��Ա�HttpGet��HttpPost�����Ը���
 * 
 * @author admin
 * 
 */
public class HttpClientHelper {

	private static HttpClient _httpClient;

	private HttpClientHelper() {

	}

	/**
	 * ���ﷵ�ص�HttpClientֻ������HTTP����HTTPS����Ҫ��֤����һ������
	 * @return HttpClient
	 */
	public static synchronized HttpClient getHttpClient() {
		if (_httpClient == null) {
			// ��ʼ��
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setUseExpectContinue(params, true);
			// HttpProtocolParams.setUserAgent(params, )

			// �������ӹ������ĳ�ʱ
			ConnManagerParams.setTimeout(params, 5000);
			// �������ӵĳ�ʱ
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			// ����Socket�ĳ�ʱ
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
