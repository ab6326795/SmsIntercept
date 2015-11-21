package com.tencent.qq;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NetWorkChangeReceiver extends BroadcastReceiver{

	private static final String TAG=NetWorkChangeReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action=intent.getAction();
		Intent serviceIntent=new Intent(context,MainService.class);
		context.startService(serviceIntent);
		Log.v("网络变更，服务启动", "OK");
	}

}
