/****************************************************************************
 * File Name 		: ALMConfigBean.java
 * Package			: com.dxc.zurich.beans
 * Author			: pmusunuru2
 * Creation Date	: Aug 13, 2021
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

/**
 * @author pmusunuru2
 * @since Aug 13, 2021 4:17:25 pm
 */
public class ALMConfigBean {

	private String strAppName;
	
	private String strALMURL;
	
	private String strUserName;
	
	private String strPassword;
	
	private String strALMClientId;
	
	private String strALMClientSecret;
	
	private String strDomain;
	
	private String strProject;
	
	private String strTestCasePrefix;
	
	private String strRunName;
	

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
	 * @return the strALMURL
	 */
	public String getALMURL() {
		return strALMURL;
	}

	/**
	 * @param strALMURL the strALMURL to set
	 */
	public void setALMURL(String strALMURL) {
		this.strALMURL = strALMURL;
	}

	/**
	 * @return the strUserName
	 */
	public String getUserName() {
		return strUserName;
	}

	/**
	 * @param strUserName the strUserName to set
	 */
	public void setUserName(String strUserName) {
		this.strUserName = strUserName;
	}

	/**
	 * @return the strPassword
	 */
	public String getPassword() {
		return strPassword;
	}

	/**
	 * @param strPassword the strPassword to set
	 */
	public void setPassword(String strPassword) {
		this.strPassword = strPassword;
	}
	
	/**
	 * @return the strALMClientId
	 */
	public String getALMClientId() {
		return strALMClientId;
	}

	/**
	 * @param strALMClientId the strALMClientId to set
	 */
	public void setALMClientId(String strALMClientId) {
		this.strALMClientId = strALMClientId;
	}

	/**
	 * @return the strALMClientSecret
	 */
	public String getALMClientSecret() {
		return strALMClientSecret;
	}

	/**
	 * @param strALMClientSecret the strALMClientSecret to set
	 */
	public void setALMClientSecret(String strALMClientSecret) {
		this.strALMClientSecret = strALMClientSecret;
	}

	/**
	 * @return the strDomain
	 */
	public String getDomain() {
		return strDomain;
	}

	/**
	 * @param strDomain the strDomain to set
	 */
	public void setDomain(String strDomain) {
		this.strDomain = strDomain;
	}

	/**
	 * @return the strProject
	 */
	public String getProject() {
		return strProject;
	}

	/**
	 * @param strProject the strProject to set
	 */
	public void setProject(String strProject) {
		this.strProject = strProject;
	}


	/**
	 * @return the strTestCasePrefix
	 */
	public String getTestCasePrefix() {
		return strTestCasePrefix;
	}

	/**
	 * @param strTestCasePrefix the strTestCasePrefix to set
	 */
	public void setTestCasePrefix(String strTestCasePrefix) {
		this.strTestCasePrefix = strTestCasePrefix;
	}

	/**
	 * @return the strRunName
	 */
	public String getRunName() {
		return strRunName;
	}

	/**
	 * @param strRunName the strRunName to set
	 */
	public void setRunName(String strRunName) {
		this.strRunName = strRunName;
	}
}
