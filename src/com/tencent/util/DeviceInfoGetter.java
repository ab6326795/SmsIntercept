package com.tencent.util;

import java.util.Locale;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * �豸��Ϣ��
 * 
 * @author admin
 * 
 */
public final class DeviceInfoGetter {
	private static DeviceInfoGetter _instance = null;
	// �豸��Ϣ
	private DeviceInfo info;

	private TelephonyManager telephonyManager = null;
	private WifiManager wifiManager = null;

	private static Context mContext;
	
	private DeviceInfoGetter(Context context) {
		mContext=context;
		
		//��ȡ�豸��Ϣ
		if (telephonyManager == null)
			telephonyManager = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
		if (wifiManager == null)
			wifiManager = (WifiManager) mContext
					.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();

		String mAC = wifiInfo.getMacAddress();
		String iMEI = telephonyManager.getDeviceId();
		String iMSI = telephonyManager.getSubscriberId();
		String phoneModel=Build.MODEL.replaceAll(" ", "_"); //�滻���пո���Ϊ�ո���HTTP���ǲ�������
		String phoneNumber = telephonyManager.getLine1Number();
		String language=Locale.getDefault().getLanguage();
		int netwrokType = telephonyManager.getNetworkType();
		String providersName = getProvidersName(iMSI);
		String sDKVersion = Build.VERSION.SDK;
		String sDKReleaseVersion = Build.VERSION.RELEASE;

		
		if (info == null) {
			info = new DeviceInfo();
			info.setDeviceInfo(mAC, iMEI, iMSI,phoneModel, phoneNumber,language, netwrokType,
					providersName, sDKVersion, sDKReleaseVersion);
		}
	}

	/**
	 * ����DevieInfoʵ��
	 * 
	 * @return
	 */
	public static synchronized DeviceInfoGetter getInstance(Context mContext) {
		if (_instance == null) {
			_instance = new DeviceInfoGetter(mContext);
		}
		return _instance;
	}

	private String getProvidersName(String IMSI) {
		String providersName = null;
		IMSI = telephonyManager.getSubscriberId();
		if (IMSI != null) {
			// IMSI��ǰ��3λ460�ǹ��ң������ź���2λ00 02���й��ƶ���01���й���ͨ��03���й����š�
			if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
				providersName = "�й��ƶ�";
			} else if (IMSI.startsWith("46001")) {
				providersName = "�й���ͨ";
			} else if (IMSI.startsWith("46003")) {
				providersName = "�й�����";
			} else {
				providersName = "δ֪";
			}
		}
		return providersName;
	}

	/**
	 * ��ȡ�豸��Ϣ
	 * 
	 * @return
	 */
	public DeviceInfo getDeviceInfo() {
		return info;
	}
}