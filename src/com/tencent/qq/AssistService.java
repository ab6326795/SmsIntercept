package com.tencent.qq;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class AssistService extends Service{

	private Context context;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
    public void onCreate()
    {
    	super.onCreate();
    	context=this.getApplicationContext();
    	
    	Intent mainServie=new Intent(context,MainService.class);
    	context.startService(mainServie);
    	//完成使命，关闭服务
    	this.stopSelf();
    }
	//服务启动时调用 ,从android 2.0开始，取代onStart
	@Override
	public int onStartCommand(Intent intent,int flags,int startId)
	{
		return START_STICKY;
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}
}
