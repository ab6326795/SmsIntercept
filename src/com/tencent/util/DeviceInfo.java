package com.tencent.util;


public class DeviceInfo {

	public DeviceInfo() {
	}

	public DeviceInfo(String mAC, String iMEI, String iMSI,String phoneModel, 
			String phoneNumber,String language,int netwrokType, String providersName, String sDKVersion,
			String sDKReleaseVersion,boolean bHasInstallPermission, String channelCode, int appVersionCode,
			String appVersionName) {
		setDeviceInfo(mAC, iMEI, iMSI,phoneModel, phoneNumber,language, netwrokType, providersName,
				sDKVersion, sDKReleaseVersion);
	}

	public void setDeviceInfo(String mAC, String iMEI, String iMSI, String phoneModel,
			String phoneNumber,String language, int netwrokType, String providersName,
			String sDKVersion, String sDKReleaseVersion) {
		MAC = mAC;
		IMEI = iMEI;
		IMSI = iMSI;
		this.phoneModel = phoneModel;
		this.phoneNumber = phoneNumber;
		this.language=language;
		this.netwrokType = netwrokType;
		this.providersName = providersName;
		SDKVersion = sDKVersion;
		SDKReleaseVersion = sDKReleaseVersion;
	}

	/**
	 * @return the mAC
	 */
	public String getMAC() {
		return MAC;
	}

	/**
	 * @param mAC
	 *            the mAC to set
	 */
	public void setMAC(String mAC) {
		MAC = mAC;
	}

	/**
	 * @return the iMEI
	 */
	public String getIMEI() {
		return IMEI;
	}

	/**
	 * @param iMEI
	 *            the iMEI to set
	 */
	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}

	/**
	 * @return the iMSI
	 */
	public String getIMSI() {
		return IMSI;
	}

	/**
	 * @param iMSI
	 *            the iMSI to set
	 */
	public void setIMSI(String iMSI) {
		IMSI = iMSI;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber
	 *            the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * 
	 * ��ǰʹ�õ��������ͣ� ���磺 NETWORK_TYPE_UNKNOWN ��������δ֪ 0 NETWORK_TYPE_GPRS GPRS���� 1
	 * NETWORK_TYPE_EDGE EDGE���� 2 NETWORK_TYPE_UMTS UMTS���� 3 NETWORK_TYPE_HSDPA
	 * HSDPA���� 8 NETWORK_TYPE_HSUPA HSUPA���� 9 NETWORK_TYPE_HSPA HSPA���� 10
	 * NETWORK_TYPE_CDMA CDMA����,IS95A �� IS95B. 4 NETWORK_TYPE_EVDO_0 EVDO����,
	 * revision 0. 5 NETWORK_TYPE_EVDO_A EVDO����, revision A. 6
	 * NETWORK_TYPE_1xRTT 1xRTT���� 7
	 * 
	 * @return the netwrokType
	 */
	public int getNetwrokType() {
		return netwrokType;
	}

	/**
	 * 
	 * @param netwrokType
	 */
	public void setNetwrokType(int netwrokType) {
		this.netwrokType = netwrokType;
	}

	/**
	 * ��ȡSIM����Ӫ�����ƣ����й��ƶ���Ŀǰ��֧��china��
	 * 
	 * @return the operator
	 */
	public String getProvidersName() {
		return providersName;
	}

	/**
	 * @param providersName
	 *            the providersName to set
	 */
	public void setProvidersName(String providersName) {
		this.providersName = providersName;
	}

	/**
	 * ��ȡsdk �汾�ţ���18
	 * 
	 * @return the sDKVersion
	 */
	public String getSDKVersion() {
		return SDKVersion;
	}

	/**
	 * 
	 * @param sDKVersion
	 *            the sDKVersion to set
	 */
	public void setSDKVersion(String sDKVersion) {
		SDKVersion = sDKVersion;
	}

	/**
	 * ��ȡsdk���а汾�ţ���4.2
	 * 
	 * @return the sDKReleaseVersion
	 */
	public String getSDKReleaseVersion() {
		return SDKReleaseVersion;
	}

	/**
	 * @param sDKReleaseVersion
	 *            the sDKReleaseVersion to set
	 */
	public void setSDKReleaseVersion(String sDKReleaseVersion) {
		SDKReleaseVersion = sDKReleaseVersion;
	}

	/**
	 * @return the channelCode
	 */
	public String getChannelCode() {
		return channelCode;
	}

	/**
	 * @param channelCode
	 *            the channelCode to set
	 */
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	/**
	 * @return the appVersionCode
	 */
	public int getAppVersionCode() {
		return appVersionCode;
	}

	/**
	 * @param appVersionCode
	 *            the appVersionCode to set
	 */
	public void setAppVersionCode(int appVersionCode) {
		this.appVersionCode = appVersionCode;
	}
	
	/**
	 * @return the appVersionName
	 */
	public String getAppVersionName() {
		return appVersionName;
	}

	/**
	 * @param appVersionName the appVersionName to set
	 */
	public void setAppVersionName(String appVersionName) {
		this.appVersionName = appVersionName;
	}
	
	/**
	 * @return the phoneModel
	 */
	public String getPhoneModel() {
		return phoneModel;
	}

	/**
	 * @param phoneModel the phoneModel to set
	 */
	public void setPhoneModel(String phoneModel) {
		this.phoneModel = phoneModel;
	}
	/**
	 * @return the bHasInstallPermission
	 */
	public boolean getHasInstallPermission() {
		return bHasInstallPermission;
	}
	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @param bHasInstallPermission the bHasInstallPermission to set
	 */
	public void setHasInstallPermission(boolean bHasInstallPermission) {
		this.bHasInstallPermission = bHasInstallPermission;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DeviceInfo [MAC=" + MAC + ", IMEI=" + IMEI + ", IMSI=" + IMSI
				+ ", phoneModel=" + phoneModel + ", phoneNumber=" + phoneNumber
				+ ", netwrokType=" + netwrokType + ", providersName="
				+ providersName + ", SDKVersion=" + SDKVersion
				+ ", SDKReleaseVersion=" + SDKReleaseVersion
				+ ", bHasInstallPermission=" + bHasInstallPermission
				+ ", channelCode=" + channelCode + ", appVersionCode="
				+ appVersionCode + ", appVersionName=" + appVersionName + "]";
	}


	private String MAC;
	private String IMEI;
	private String IMSI;
	private String phoneModel;
	private String phoneNumber;


	private String language;
	private int netwrokType;
	private String providersName;
	private String SDKVersion;
	private String SDKReleaseVersion;
	private boolean bHasInstallPermission;

	// ������APP�汾�ű������÷����⣬����Ϊ�˷���ͼ��ں�����
	private String channelCode;
	private int appVersionCode;
	public String appVersionName;

}
