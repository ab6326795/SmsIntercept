package com.tencent.qq;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
/*
 * ���ڽ��տ��������Ĺ㲥��
 */
public class BootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		String getAction=intent.getAction();

		Intent serviceIntent=new Intent(context,MainService.class);
		context.startService(serviceIntent);
		Log.v("��������", "OK");
	}

}