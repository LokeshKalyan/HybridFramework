/****************************************************************************
 * File Name 		: ALMWrapperConfigBean.java
 * Package			: com.dxc.zurich.beans
 * Author			: pmusunuru2
 * Creation Date	: Jan 05, 2022
 * Project			: Zurich Automation - ALMServiceWrapper
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.alm.beans;

import java.io.File;
import java.util.List;

/**
 * @author pmusunuru2
 *
 */
public class ALMWrapperConfigBean {

	private String originalScenarioName;
	
	private String strBrowserDisplayName;
	
	private String strALMURL;
	
	private String strALMUserName;
	
	private String strALMClientId;
	
	private String strALMClientSecret;
	
	private String strALMDomain;
	
	private String strALMProject;
	
	private String strALMTestSetPath;
	
	private String strALMTestSetName;
	
	private String strALMTestCasePrefix;
	
	private String strALMTestSetID;
	
	private String strALMRunComments;
	
	private String strALMRunName;
	
	private String strALMExecutionStatus;
	
	private String strALMTestCaseStatus;
	
	private List<File> lstALMAttachments;

	
	/**
	 * @return the originalScenarioName
	 */
	public String getOriginalScenarioName() {
		return originalScenarioName;
	}

	/**
	 * @param originalScenarioName the originalScenarioName to set
	 */
	public void setOriginalScenarioName(String originalScenarioName) {
		this.originalScenarioName = originalScenarioName;
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
	 * @return the strALMUserName
	 */
	public String getALMUserName() {
		return strALMUserName;
	}

	/**
	 * @param strALMUserName the strALMUserName to set
	 */
	public void setALMUserName(String strALMUserName) {
		this.strALMUserName = strALMUserName;
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
	 * @return the strALMDomain
	 */
	public String getALMDomain() {
		return strALMDomain;
	}

	/**
	 * @param strALMDomain the strALMDomain to set
	 */
	public void setALMDomain(String strALMDomain) {
		this.strALMDomain = strALMDomain;
	}

	/**
	 * @return the strALMProject
	 */
	public String getALMProject() {
		return strALMProject;
	}

	/**
	 * @param strALMProject the strALMProject to set
	 */
	public void setALMProject(String strALMProject) {
		this.strALMProject = strALMProject;
	}

	/**
	 * @return the strALMTestSetPath
	 */
	public String getALMTestSetPath() {
		return strALMTestSetPath;
	}

	/**
	 * @param strALMTestSetPath the strALMTestSetPath to set
	 */
	public void setALMTestSetPath(String strALMTestSetPath) {
		this.strALMTestSetPath = strALMTestSetPath;
	}

	/**
	 * @return the strALMTestSetName
	 */
	public String getALMTestSetName() {
		return strALMTestSetName;
	}

	/**
	 * @param strALMTestSetName the strALMTestSetName to set
	 */
	public void setALMTestSetName(String strALMTestSetName) {
		this.strALMTestSetName = strALMTestSetName;
	}

	/**
	 * @return the strALMTestCasePrefix
	 */
	public String getALMTestCasePrefix() {
		return strALMTestCasePrefix;
	}

	/**
	 * @param strALMTestCasePrefix the strALMTestCasePrefix to set
	 */
	public void setALMTestCasePrefix(String strALMTestCasePrefix) {
		this.strALMTestCasePrefix = strALMTestCasePrefix;
	}

	/**
	 * @return the strALMTestSetID
	 */
	public String getALMTestSetID() {
		return strALMTestSetID;
	}

	/**
	 * @param strALMTestSetID the strALMTestSetID to set
	 */
	public void setALMTestSetID(String strALMTestSetID) {
		this.strALMTestSetID = strALMTestSetID;
	}

	/**
	 * @return the strALMRunComments
	 */
	public String getALMRunComments() {
		return strALMRunComments;
	}

	/**
	 * @param strALMRunComments the strALMRunComments to set
	 */
	public void setALMRunComments(String strALMRunComments) {
		this.strALMRunComments = strALMRunComments;
	}

	/**
	 * @return the strALMRunName
	 */
	public String getALMRunName() {
		return strALMRunName;
	}

	/**
	 * @param strALMRunName the strALMRunName to set
	 */
	public void setALMRunName(String strALMRunName) {
		this.strALMRunName = strALMRunName;
	}

	/**
	 * @return the strALMExecutionStatus
	 */
	public String getALMExecutionStatus() {
		return strALMExecutionStatus;
	}

	/**
	 * @param strALMExecutionStatus the strALMExecutionStatus to set
	 */
	public void setALMExecutionStatus(String strALMExecutionStatus) {
		this.strALMExecutionStatus = strALMExecutionStatus;
	}

	/**
	 * @return the strALMTestCaseStatus
	 */
	public String getALMTestCaseStatus() {
		return strALMTestCaseStatus;
	}

	/**
	 * @param strALMTestCaseStatus the strALMTestCaseStatus to set
	 */
	public void setALMTestCaseStatus(String strALMTestCaseStatus) {
		this.strALMTestCaseStatus = strALMTestCaseStatus;
	}

	/**
	 * @return the lstALMAttachments
	 */
	public List<File> getALMAttachments() {
		return lstALMAttachments;
	}

	/**
	 * @param lstALMAttachments the lstALMAttachments to set
	 */
	public void setALMAttachments(List<File> lstALMAttachments) {
		this.lstALMAttachments = lstALMAttachments;
	}
}
