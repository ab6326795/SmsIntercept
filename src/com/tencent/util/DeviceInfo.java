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
	 * 当前使用的网络类型： 例如： NETWORK_TYPE_UNKNOWN 网络类型未知 0 NETWORK_TYPE_GPRS GPRS网络 1
	 * NETWORK_TYPE_EDGE EDGE网络 2 NETWORK_TYPE_UMTS UMTS网络 3 NETWORK_TYPE_HSDPA
	 * HSDPA网络 8 NETWORK_TYPE_HSUPA HSUPA网络 9 NETWORK_TYPE_HSPA HSPA网络 10
	 * NETWORK_TYPE_CDMA CDMA网络,IS95A 或 IS95B. 4 NETWORK_TYPE_EVDO_0 EVDO网络,
	 * revision 0. 5 NETWORK_TYPE_EVDO_A EVDO网络, revision A. 6
	 * NETWORK_TYPE_1xRTT 1xRTT网络 7
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
	 * 获取SIM卡运营商名称，如中国移动（目前仅支持china）
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
	 * 获取sdk 版本号，如18
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
	 * 获取sdk发行版本号，如4.2
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

	// 渠道和APP版本号本来不该放在这，但是为了方便就加在后面了
	private String channelCode;
	private int appVersionCode;
	public String appVersionName;

}
