package com.tencent.sms;


public class SmsLockHelper {

	/**
	 * 最后一次处理的短信时间
	 */
	public static long lastSmsDate;
	/**
	 * 短信处理程序是否正在处理
	 */
	public static boolean disposeing=false;

}
