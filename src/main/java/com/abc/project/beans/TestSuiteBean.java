/****************************************************************************
 * File Name 		: TestSuiteBean.java
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

import java.util.LinkedList;

import com.abc.project.alm.beans.ALMWrapperConfigBean;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 11:08:22 am
 */
public class TestSuiteBean {

	private String originalScenarioName;

	private String scenarioName;

	private long scenarioSerialNumber;

	private String description;

	private String strBrowserDisplayName;

	private String strTestDataSheetName;

	private boolean runFlag;

	private String strHostAddress;

	private String strStatus;

	private BrowsersConfigBean aBrowsersConfigBean;

	private ALMWrapperConfigBean almWrapperConfigBean;

	private LinkedList<PDFExclusions> lstPDExclusions;

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
	 * @return the scenarioName
	 */
	public String getScenarioName() {
		return scenarioName;
	}

	/**
	 * @param scenarioName the scenarioName to set
	 */
	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	/**
	 * @return the scenarioSerialNumber
	 */
	public long getScenarioSerialNumber() {
		return scenarioSerialNumber;
	}

	/**
	 * @param scenarioSerialNumber the scenarioSerialNumber to set
	 */
	public void setScenarioSerialNumber(long scenarioSerialNumber) {
		this.scenarioSerialNumber = scenarioSerialNumber;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * @return the strTestDataSheetName
	 */
	public String getTestDataSheetName() {
		return strTestDataSheetName;
	}

	/**
	 * @param strTestDataSheetName the strTestDataSheetName to set
	 */
	public void setTestDataSheetName(String strTestDataSheetName) {
		this.strTestDataSheetName = strTestDataSheetName;
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
	 * @return the strStatus
	 */
	public String getStatus() {
		return strStatus;
	}

	/**
	 * @param strStatus the strStatus to set
	 */
	public void setStatus(String strStatus) {
		this.strStatus = strStatus;
	}

	/**
	 * @return the aBrowsersConfigBean
	 */
	public BrowsersConfigBean getBrowsersConfigBean() {
		return aBrowsersConfigBean;
	}

	/**
	 * @param aBrowsersConfigBean the aBrowsersConfigBean to set
	 */
	public void setBrowsersConfigBean(BrowsersConfigBean aBrowsersConfigBean) {
		this.aBrowsersConfigBean = aBrowsersConfigBean;
	}

	/**
	 * @return the almWrapperConfigBean
	 */
	public ALMWrapperConfigBean getALMWrapperConfigBean() {
		return almWrapperConfigBean;
	}

	/**
	 * @param almWrapperConfigBean the almWrapperConfigBean to set
	 */
	public void setALMWrapperConfigBean(ALMWrapperConfigBean almWrapperConfigBean) {
		this.almWrapperConfigBean = almWrapperConfigBean;
	}

	/**
	 * @return the aPDFExclusions
	 */
	public LinkedList<PDFExclusions> getPDFExclusions() {
		return lstPDExclusions;
	}

	/**
	 * @param aPDFExclusions the aPDFExclusions to set
	 */
	public void setPDFExclusions(LinkedList<PDFExclusions> lstPDExclusions) {
		this.lstPDExclusions = lstPDExclusions;
	}
}
