/****************************************************************************
 * File Name 		: BrowsersConfigBean.java
 * Package			: com.dxc.zurich.beans
 * Author			: pmusunuru2
 * Creation Date	: Feb 16, 2021
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.beans;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.CaseUtils;

import com.abc.project.config.AppConfig;
import com.abc.project.config.MasterConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.enums.Browsers;
import com.abc.project.utils.AppUtils;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 11:06:58 am
 */
public class BrowsersConfigBean {

	private Browsers aBrowser;

	private int iBrowserSequence;

	private boolean runFlag;

	private boolean sendEmail;

	private String strBrowserDisplayName;

	private int iBrowserPrority;

	private int iScrollTimeOut;

	private String strDeviceName;

	private String strDeviceID;

	private String strPlatFormName;

	private String strVersion;

	private String strActivityName;

	private String strPackageName;

	private long lBrowserPort;

	private long lBrowserBootStrapPort;

	private String strBrowserStackUserName;

	private String strBrowserStackPassword;

	private String strBrowserName;

	private boolean isBrowserStackLocal;

	private String strBrowserAddress;

	private boolean canAutoUpdateDriver;

	private LinkedHashMap<String, Object> mpExtraCapabilities;

	private List<TestSuiteBean> lstTestSuiteData;

	/**
	 * @return the aBrowser
	 */
	public Browsers getBrowser() {
		return aBrowser;
	}

	/**
	 * @param aBrowser the aBrowser to set
	 */
	public void setBrowser(Browsers aBrowser) {
		this.aBrowser = aBrowser;
	}

	/**
	 * @return the iBrowserSequence
	 */
	public int getBrowserSequence() {
		return iBrowserSequence;
	}

	/**
	 * @param iBrowserSequence the iBrowserSequence to set
	 */
	public void setBrowserSequence(int iBrowserSequence) {
		this.iBrowserSequence = iBrowserSequence;
	}

	/**
	 * @return the strBrowserDisplayName
	 */
	public String getBrowserDisplayName() {
		return strBrowserDisplayName;
	}

	/**
	 * @param strBrowserDisplayName the strBrowserDisplayName to set
	 */
	public void setBrowserDisplayName(String strBrowserDisplayName) {
		this.strBrowserDisplayName = strBrowserDisplayName;
	}

	/**
	 * @return the runFlag
	 */
	public boolean isRunFlag() {
		return runFlag;
	}

	/**
	 * @param runFlag the runFlag to set
	 */
	public void setRunFlag(boolean runFlag) {
		this.runFlag = runFlag;
	}

	/**
	 * @return the sendEmail
	 */
	public boolean isSendEmail() {
		return sendEmail;
	}

	/**
	 * @param sendEmail the sendEmail to set
	 */
	public void setSendEmail(boolean sendEmail) {
		this.sendEmail = sendEmail;
	}

	/**
	 * @return the iBrowserPrority
	 */
	public int getBrowserPrority() {
		return iBrowserPrority;
	}

	/**
	 * @param iBrowserPrority the iBrowserPrority to set
	 */
	public void setBrowserPrority(int iBrowserPrority) {
		this.iBrowserPrority = iBrowserPrority;
	}

	/**
	 * @return the iScrollTimeOut
	 */
	public int getScrollTimeOut() {
		return iScrollTimeOut;
	}

	/**
	 * @param iScrollTimeOut the iScrollTimeOut to set
	 */
	public void setScrollTimeOut(int iScrollTimeOut) {
		this.iScrollTimeOut = iScrollTimeOut;
	}

	/**
	 * @return the strDeviceName
	 */
	public String getDeviceName() {
		return strDeviceName;
	}

	/**
	 * @param strDeviceName the strDeviceName to set
	 */
	public void setDeviceName(String strDeviceName) {
		this.strDeviceName = strDeviceName;
	}

	/**
	 * @return the strDeviceID
	 */
	public String getDeviceID() {
		return strDeviceID;
	}

	/**
	 * @param strDeviceID the strDeviceID to set
	 */
	public void setDeviceID(String strDeviceID) {
		this.strDeviceID = strDeviceID;
	}

	/**
	 * @return the strPlatFormName
	 */
	public String getPlatFormName() {
		return strPlatFormName;
	}

	/**
	 * @param strPlatFormName the strPlatFormName to set
	 */
	public void setPlatFormName(String strPlatFormName) {
		this.strPlatFormName = strPlatFormName;
	}

	/**
	 * @return the strVersion
	 */
	public String getVersion() {
		return strVersion;
	}

	/**
	 * @param strVersion the strVersion to set
	 */
	public void setVersion(String strVersion) {
		this.strVersion = strVersion;
	}

	/**
	 * @return the strActivityName
	 */
	public String getActivityName() {
		return strActivityName;
	}

	/**
	 * @param strActivityName the strActivityName to set
	 */
	public void setActivityName(String strActivityName) {
		this.strActivityName = strActivityName;
	}

	/**
	 * @return the strPackageName
	 */
	public String getPackageName() {
		return strPackageName;
	}

	/**
	 * @param strPackageName the strPackageName to set
	 */
	public void setPackageName(String strPackageName) {
		this.strPackageName = strPackageName;
	}

	/**
	 * @return the lBrowserPort
	 */
	public long getBrowserPort() {
		return lBrowserPort;
	}

	/**
	 * @param lBrowserPort the lBrowserPort to set
	 */
	public void setBrowserPort(long lDevicePort) {
		this.lBrowserPort = lDevicePort;
	}

	/**
	 * @return the lDeviceBootStrapPort
	 */
	public long getBrowserBootStrapPort() {
		return lBrowserBootStrapPort;
	}

	/**
	 * @param lBrowserBootStrapPort the lBrowserBootStrapPort to set
	 */
	public void setBrowserBootStrapPort(long lBrowserBootStrapPort) {
		this.lBrowserBootStrapPort = lBrowserBootStrapPort;
	}

	/**
	 * @return the strBrowserStackUserName
	 */
	public String getBrowserStackUserName() {
		return strBrowserStackUserName;
	}

	/**
	 * @param strBrowserStackUserName the strBrowserStackUserName to set
	 */
	public void setBrowserStackUserName(String strBrowserStackUserName) {
		this.strBrowserStackUserName = strBrowserStackUserName;
	}

	/**
	 * @return the strBrowserStackPassword
	 */
	public String getBrowserStackPassword() {
		return strBrowserStackPassword;
	}

	/**
	 * @param strBrowserStackPassword the strBrowserStackPassword to set
	 */
	public void setBrowserStackPassword(String strBrowserStackPassword) {
		this.strBrowserStackPassword = strBrowserStackPassword;
	}

	/**
	 * @return the strBrowserName
	 */
	public String getBrowserName() {
		return strBrowserName;
	}

	/**
	 * @param strBrowserName the strBrowserName to set
	 */
	public void setBrowserName(String strBrowserName) {
		this.strBrowserName = strBrowserName;
	}

	/**
	 * @return the isBrowserStackLocal
	 */
	public boolean isBrowserStackLocal() {
		return isBrowserStackLocal;
	}

	/**
	 * @param isBrowserStackLocal the isBrowserStackLocal to set
	 */
	public void setBrowserStackLocal(boolean isBrowserStackLocal) {
		this.isBrowserStackLocal = isBrowserStackLocal;
	}

	/**
	 * @return the strBrowserAddress
	 */
	public String getBrowserHost() {
		return strBrowserAddress;
	}

	/**
	 * @param strBrowserAddress the strBrowserAddress to set
	 */
	public void setBrowserHost(String strBrowserAddress) {
		this.strBrowserAddress = strBrowserAddress;
	}

	/**
	 * @return the canAutoUpdateDriver
	 */
	public boolean canAutoUpdateDriver() {
		return canAutoUpdateDriver;
	}

	/**
	 * @param canAutoUpdateDriver the canAutoUpdateDriver to set
	 */
	public void setCanAutoUpdateDriver(boolean canAutoUpdateDriver) {
		this.canAutoUpdateDriver = canAutoUpdateDriver;
	}

	public String getReportBrowserName() {
		Browsers aBrowser = getBrowser();
		String strReportBrowserName = AppUtils.getBrowserExecutionFileName(
				aBrowser == null ? CaseUtils.toCamelCase(getBrowserName(), true, ' ') : aBrowser.getBrowserName());
		return strReportBrowserName;
	}

	/**
	 * @return the mpExtraCapabilities
	 */
	public LinkedHashMap<String, Object> getExtraCapabilities() {
		if (mpExtraCapabilities == null) {
			mpExtraCapabilities = new LinkedHashMap<>();
		}
		return mpExtraCapabilities;
	}

	/**
	 * @param mpExtraCapabilities the mpExtraCapabilities to set
	 */
	public void setExtraCapabilities(LinkedHashMap<String, Object> mpExtraCapabilities) {
		if (mpExtraCapabilities == null) {
			mpExtraCapabilities = new LinkedHashMap<>();
		}
		boolean bIsBrowserStack = getBrowser() == Browsers.BROWSER_STACK_DESKTOP
				|| getBrowser() == Browsers.BROWSER_STACK_MOBILE || getBrowser() == Browsers.BROWSER_STACK_IOS_NATIVE
				|| getBrowser() == Browsers.BROWSER_STACK_ANDROID_NATIVE;
		MasterConfig aMasterConfig = MasterConfig.getInstance();
		if (bIsBrowserStack && isBrowserStackLocal()) {
			String strLocalIdentifier = AppUtils.formatMessage(AppConstants.ENV_URL_FORMAT, aMasterConfig.getAppName(),
					aMasterConfig.getAppRunID());
			mpExtraCapabilities.put("browserstack.localIdentifier", strLocalIdentifier);
		}
		if (bIsBrowserStack) {
			String strBuildName = String.format("%s-%s", aMasterConfig.getAppName(), aMasterConfig.getUniqueBuildId());
			mpExtraCapabilities.put("buildName", strBuildName);
		}
		this.mpExtraCapabilities = mpExtraCapabilities;
	}

	public List<TestSuiteBean> getTestSuiteData() {
		if (CollectionUtils.isEmpty(lstTestSuiteData)) {
			lstTestSuiteData = AppConfig.getInstance().getFilteredControllerSuiteByBrowerName(getBrowserDisplayName());
		}
		return lstTestSuiteData;
	}

	public void clearTestData() {
		if (!CollectionUtils.isEmpty(lstTestSuiteData)) {
			lstTestSuiteData.clear();
		}
	}
}
