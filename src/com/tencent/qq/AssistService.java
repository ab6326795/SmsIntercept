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
    	//���ʹ�����رշ���
    	this.stopSelf();
    }
	//��������ʱ���� ,��android 2.0��ʼ��ȡ��onStart
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