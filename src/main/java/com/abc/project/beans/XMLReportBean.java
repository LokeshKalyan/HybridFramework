/****************************************************************************
 * File Name 		: XMLReportBean.java
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

import com.aventstack.extentreports.Status;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 11:50:21 am
 */
public class XMLReportBean {

	private String strTestCaseID;
	private String strBrowserName;
	private String strTestCaseDescription;
	private String strLogMessage;
	private String strStatus;

	/**
	 * @return the strTestCaseID
	 */
	public String getTestCaseID() {
		return strTestCaseID;
	}

	/**
	 * @param strTestCaseID the strTestCaseID to set
	 */
	public void setTestCaseID(String strTestCaseID) {
		this.strTestCaseID = strTestCaseID;
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
	 * @return the strTestCaseDescription
	 */
	public String getTestCaseDescription() {
		return strTestCaseDescription;
	}

	/**
	 * @param strTestCaseDescription the strTestCaseDescription to set
	 */
	public void setTestCaseDescription(String strTestCaseDescription) {
		this.strTestCaseDescription = strTestCaseDescription;
	}

	/**
	 * @return the strLogMessage
	 */
	public String getLogMessage() {
		return strLogMessage;
	}

	/**
	 * @param strLogMessage the strLogMessage to set
	 */
	public void setLogMessage(String strLogMessage) {
		this.strLogMessage = strLogMessage;
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
	public void setStatus(Status aStatus) {
		switch (aStatus) {
		case PASS:
			this.strStatus = "Pass";
			break;
		case FAIL:
			this.strStatus = "Fail";
			break;
//		case FATAL:
//			this.strStatus = "Fatal";
//			break;
//		case ERROR:
//			this.strStatus = "Error";
//			break;
		case WARNING:
			this.strStatus = "Warning";
			break;
		case INFO:
			this.strStatus = "Info";
			break;
//		case DEBUG:
//			this.strStatus = "Debug";
//			break;
		case SKIP:
			this.strStatus = "Skip";
			break;
		default:
			this.strStatus = "Unknown";
			break;
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Report{");
		sb.append("TestCaseID='").append(getTestCaseID()).append('\'');
		sb.append(", BrowserName='").append(getBrowserName()).append('\'');
		sb.append(", TestCaseDescription='").append(getTestCaseDescription()).append('\'');
		sb.append(", LogMessage='").append(getLogMessage()).append('\'');
		sb.append(", Status='").append(getStatus()).append('\'');
		sb.append('}');
		return sb.toString();
	}

}
