package com.tencent.qq;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
/*
 * 用于接收开机启动的广播类
 */
public class BootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		String getAction=intent.getAction();

		Intent serviceIntent=new Intent(context,MainService.class);
		context.startService(serviceIntent);
		Log.v("开机启动", "OK");
	}

}
