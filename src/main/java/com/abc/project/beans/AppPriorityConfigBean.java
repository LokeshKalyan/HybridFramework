/****************************************************************************
 * File Name 		: AppPriorityConfigBean.java
 * Package			: com.dxc.zurich.beans
 * Author			: pmusunuru2
 * Creation Date	: Dec 05, 2022
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

import com.abc.project.enums.AppPriority;

/**
 * @author pmusunuru2
 * @since Dec 05, 2022 11:13:50 am
 */
public class AppPriorityConfigBean {

	// App Name or Set Name
	private String strAppName;
	
	// Should Match with Controller Suite 
	private String strScenarioName;
	
	private String strBrowserDisplayName;
	
	private AppPriority aAppPriority;
	
	private boolean runFlag;

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
	 * @return the strScenarioName
	 */
	public String getScenarioName() {
		return strScenarioName;
	}

	/**
	 * @param strScenarioName the strScenarioName to set
	 */
	public void setScenarioName(String strScenarioName) {
		this.strScenarioName = strScenarioName;
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
	 * @return the aAppPriority
	 */
	public AppPriority getAppPriority() {
		return aAppPriority;
	}

	/**
	 * @param aAppPriority the aAppPriority to set
	 */
	public void setAppPriority(AppPriority aAppPriority) {
		this.aAppPriority = aAppPriority;
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
}
