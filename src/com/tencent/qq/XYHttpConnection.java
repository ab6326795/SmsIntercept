package com.tencent.qq;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * 实现Http请求的封装发送
 * 
 * @author admin
 * 
 */
public class XYHttpConnection {

	private static XYHttpConnection _httpInstance;

	/**
	 * 禁止外部NEW出对象
	 */
	private XYHttpConnection() {

	}

	/**
	 * 返回本类唯一实例
	 * 
	 * @return XYHttpConnection
	 */
	public static synchronized XYHttpConnection getInstance() {
		if (_httpInstance == null)
			_httpInstance = new XYHttpConnection();
		return _httpInstance;
	}

	/**
	 * 发起Http Post 请求
	 * 
	 * @param url
	 *            请求URL
	 * @return 返回响应实体byte[]
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws HttpResponseException
	 */
	public byte[] doPost(String url) throws ClientProtocolException,
			IOException, HttpResponseException {
		HttpPost _httpPost = new HttpPost(url);
		return getResponse(_httpPost);
	}

	/**
	 * 发起Http Get 请求
	 * @param url 请求URL
	 * @return 返回响应实体byte[]
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws HttpResponseException
	 */
	public byte[] doGet(String url) throws ClientProtocolException,
		    IOException, HttpResponseException {
		HttpGet _httpGet = new HttpGet(url);
		return getResponse(_httpGet);
	}
	
	/**
	 * 发起Http Post请求
	 * 
	 * @param url
	 *            请求URL
	 * @param postPair
	 *            请求参数
	 * @return 返回响应的实体byte[]
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws HttpResponseException
	 */
	public byte[] doPost(String url, HashMap<String, String> postPair)
			throws ClientProtocolException, IOException, HttpResponseException {
		HttpPost _httpPost = new HttpPost(url);
		if (postPair != null && postPair.size() > 0) {
			ArrayList<NameValuePair> _list = new ArrayList<NameValuePair>();
			try {
				for (String s : postPair.keySet()) {
					_list.add(new BasicNameValuePair(s, postPair.get(s)));
				}
				_httpPost.setEntity(new UrlEncodedFormEntity(_list, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return getResponse(_httpPost);
	}

	/**
	 * 检查响应内容
	 * 
	 * @param response
	 *            HttpResponse
	 * @return 如果响应内容为null 或者 内容为wml（404等一般都是wml..），则返回false。否则返回true
	 */
	private boolean checkResponseEntity(HttpResponse response) {
		if (response == null)
			return false;
		if (response.getEntity().getContentLength() == 0
				|| (response.getEntity().getContentType() != null && response
						.getEntity().getContentType().getValue()
						.contains("wml"))) {
			return false;
		}
		return true;
	}

	/**
	 * 发起请求，返回响应内容
	 * 
	 * @param httpUriRequest
	 *            请求对象
	 * @return byte[]
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws HttpResponseException
	 */
	private byte[] getResponse(HttpUriRequest httpUriRequest)
			throws ClientProtocolException, IOException, HttpResponseException {

		// 这里返回的HttpClient只能用于HTTP请求，HTTPS还需要对证书做一番处理
		HttpClient client = HttpClientHelper.getHttpClient();
		if(client==null)
			return null;
		HttpResponse response = client.execute(httpUriRequest);

		byte[] responseData = null;
		int resultCode = 0;
		if (response != null)
			resultCode = response.getStatusLine().getStatusCode();

		// 响应成功，返回byte[]
		if (response != null && resultCode == HttpStatus.SC_OK) {
			/*Header[] headers = response.getHeaders("Content-Length");  
            if (headers.length > 0) 
            {
                int size = Integer.parseInt(headers[0].getValue());
                //文件大小相同
                size++;
            }*/
			responseData = EntityUtils.toByteArray(response.getEntity());
		}

		if (response == null) {
			throw new HttpResponseException(resultCode, "http response is null");
		}
		if (responseData == null) {

			throw new HttpResponseException(resultCode, "http connect error : "
					+ resultCode);
		}

		return responseData;
	}

}
