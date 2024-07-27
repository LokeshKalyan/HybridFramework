/****************************************************************************
 * File Name 		: AppEnvConfigBean.java
 * Package			: com.dxc.zurich.beans
 * Author			: pmusunuru2
 * Creation Date	: Mar 18, 2021
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

import com.abc.project.enums.AppRunMode;
import com.dxc.enums.HostPriority;

/**
 * @author pmusunuru2
 * @since Mar 18, 2021 9:55:40 am
 */
public class AppEnvConfigBean 
{

	private String strAppName;
	
	private String strAppDescription;
	
	private String strPropertyName;
	
	private String strSpocName;
	
	private String strSpocEmail;
	
	private String strTelegramGroupId;
	
	private String strMSTeamsGroupURL;
	
	private AppRunMode aAppRunMode;
	
	private String strHostAddress;
	
	private int iHostPort;
	
	private HostPriority aPriority;
	
	private ALMConfigBean aAlmConfig;

	/**
	 * @return the strAppName
	 */
	public String getAppName() {
		return strAppName;
	}

	/**
	 * @param strAppName the strAppName to set
	 */
	public void setAppName(String strAppName) {
		this.strAppName = strAppName;
	}

	/**
	 * @return the strAppDescription
	 */
	public String getAppDescription() {
		return strAppDescription;
	}

	/**
	 * @param strAppDescription the strAppDescription to set
	 */
	public void setAppDescription(String strAppDescription) {
		this.strAppDescription = strAppDescription;
	}

	/**
	 * @return the strPropertyName
	 */
	public String getPropertyName() {
		return strPropertyName;
	}

	/**
	 * @param strPropertyName the strPropertyName to set
	 */
	public void setPropertyName(String strPropertyName) {
		this.strPropertyName = strPropertyName;
	}

	/**
	 * @return the strSpocName
	 */
	public String getSpocName() {
		return strSpocName;
	}

	/**
	 * @param strSpocName the strSpocName to set
	 */
	public void setSpocName(String strSpocName) {
		this.strSpocName = strSpocName;
	}

	/**
	 * @return the strSpocEmail
	 */
	public String getSpocEmail() {
		return strSpocEmail;
	}

	/**
	 * @param strSpocEmail the strSpocEmail to set
	 */
	public void setSpocEmail(String strSpocEmail) {
		this.strSpocEmail = strSpocEmail;
	}

	/**
	 * @return the strTelegramGroupId
	 */
	public String getTelegramGroupId() {
		return strTelegramGroupId;
	}

	/**
	 * @param strTelegramGroupId the strTelegramGroupId to set
	 */
	public void setTelegramGroupId(String strTelegramGroupId) {
		this.strTelegramGroupId = strTelegramGroupId;
	}

	/**
	 * @return the strMSTeamsGroupURL
	 */
	public String getMSTeamsGroupURL() {
		return strMSTeamsGroupURL;
	}

	/**
	 * @param strMSTeamsGroupURL the strMSTeamsGroupURL to set
	 */
	public void setMSTeamsGroupURL(String strMSTeamsGroupURL) {
		this.strMSTeamsGroupURL = strMSTeamsGroupURL;
	}

	/**
	 * @return the aAppRunMode
	 */
	public AppRunMode getAppRunMode() {
		return aAppRunMode;
	}

	/**
	 * @param aAppRunMode the aAppRunMode to set
	 */
	public void setAppRunMode(AppRunMode aAppRunMode) {
		this.aAppRunMode = aAppRunMode;
	}

	/**
	 * @return the strHostAddress
	 */
	public String getHostAddress() {
		return strHostAddress;
	}

	/**
	 * @param strHostAddress the strHostAddress to set
	 */
	public void setHostAddress(String strHostAddress) {
		this.strHostAddress = strHostAddress;
	}

	/**
	 * @return the iHostPort
	 */
	public int getHostPort() {
		return iHostPort;
	}

	/**
	 * @param iHostPort the iHostPort to set
	 */
	public void setHostPort(int iHostPort) {
		this.iHostPort = iHostPort;
	}

	/**
	 * @return the aHostPriority
	 */
	public HostPriority getPriority() {
		return aPriority;
	}

	/**
	 * @param aHostPriority the aHostPriority to set
	 */
	public void setPriority(HostPriority aPriority) {
		this.aPriority = aPriority;
	}

	/**
	 * @return the aAlmConfig
	 */
	public ALMConfigBean getAlmConfig() {
		return aAlmConfig;
	}

	/**
	 * @param aAlmConfig the aAlmConfig to set
	 */
	public void setAlmConfig(ALMConfigBean aAlmConfig) {
		this.aAlmConfig = aAlmConfig;
	}
}
