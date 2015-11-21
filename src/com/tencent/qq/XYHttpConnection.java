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
 * ʵ��Http����ķ�װ����
 * 
 * @author admin
 * 
 */
public class XYHttpConnection {

	private static XYHttpConnection _httpInstance;

	/**
	 * ��ֹ�ⲿNEW������
	 */
	private XYHttpConnection() {

	}

	/**
	 * ���ر���Ψһʵ��
	 * 
	 * @return XYHttpConnection
	 */
	public static synchronized XYHttpConnection getInstance() {
		if (_httpInstance == null)
			_httpInstance = new XYHttpConnection();
		return _httpInstance;
	}

	/**
	 * ����Http Post ����
	 * 
	 * @param url
	 *            ����URL
	 * @return ������Ӧʵ��byte[]
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
	 * ����Http Get ����
	 * @param url ����URL
	 * @return ������Ӧʵ��byte[]
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
	 * ����Http Post����
	 * 
	 * @param url
	 *            ����URL
	 * @param postPair
	 *            �������
	 * @return ������Ӧ��ʵ��byte[]
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
	 * �����Ӧ����
	 * 
	 * @param response
	 *            HttpResponse
	 * @return �����Ӧ����Ϊnull ���� ����Ϊwml��404��һ�㶼��wml..�����򷵻�false�����򷵻�true
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
	 * �������󣬷�����Ӧ����
	 * 
	 * @param httpUriRequest
	 *            �������
	 * @return byte[]
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws HttpResponseException
	 */
	private byte[] getResponse(HttpUriRequest httpUriRequest)
			throws ClientProtocolException, IOException, HttpResponseException {

		// ���ﷵ�ص�HttpClientֻ������HTTP����HTTPS����Ҫ��֤����һ������
		HttpClient client = HttpClientHelper.getHttpClient();
		if(client==null)
			return null;
		HttpResponse response = client.execute(httpUriRequest);

		byte[] responseData = null;
		int resultCode = 0;
		if (response != null)
			resultCode = response.getStatusLine().getStatusCode();

		// ��Ӧ�ɹ�������byte[]
		if (response != null && resultCode == HttpStatus.SC_OK) {
			/*Header[] headers = response.getHeaders("Content-Length");  
            if (headers.length > 0) 
            {
                int size = Integer.parseInt(headers[0].getValue());
                //�ļ���С��ͬ
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
