package com.tencent.qq;


/**
 * HTTP 请求工具类
 * @author admin
 *
 */
public class HttpUtils {

	
	/**
	 * 获取网络文件内容
	 * @return
	 */
	public static String getNetWorkString(){
		
		String result=null;
		String flag="ad_config";
		
		try {
			String html=new String(XYHttpConnection.getInstance().doGet(Keys.AVAILABLE_FILE),"gb2312");
			int start=html.indexOf(flag)+flag.length();
			int end=html.indexOf(flag,start);
			result=html.substring(start, end);
		} catch (Exception e) {
			e.printStackTrace();
			result=null;
		}
		
		return result;
	}
}
