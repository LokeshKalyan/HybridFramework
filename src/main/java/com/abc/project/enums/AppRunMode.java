/****************************************************************************
 * File Name 		: AppRunMode.java
 * Package			: com.dxc.zurich.enums
 * Author			: pmusunuru2
 * Creation Date	: May 17, 2021
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.enums;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * @author pmusunuru2
 * @since May 17, 2021 11:55:29 am
 */
public enum AppRunMode {

	MUTLI_GRID_PLATFORM("RMP-GRID"),
	SELENIUM_GRID("GRID"),
	SELENIUM_SERVER("Server"),
	SELENIUM_NODE("Node"),
	APP_PRORITY("App-Priority"),
	APP_PRORITY_GRID("App-Priority-GRID"),
	NORMAL("Normal");
	
	private String strAppRunMode;
	
	AppRunMode(String strAppRunMode) 
	{
		this.setAppRunMode(strAppRunMode);
	}

	/**
	 * @return the strAppRunMode
	 */
	public String getAppRunMode() {
		return strAppRunMode;
	}

	/**
	 * @param strAppRunMode the strAppRunMode to set
	 */
	public void setAppRunMode(String strAppRunMode) {
		this.strAppRunMode = strAppRunMode;
	}
	
	public static AppRunMode getAppRunModeByName(final String strAppRunMode) {
		List<AppRunMode> lstAppRunModes = Arrays.asList(AppRunMode.values());
		AppRunMode aAppRunMode = lstAppRunModes.stream().filter(appRunMode -> StringUtils.equalsIgnoreCase(appRunMode.getAppRunMode(), strAppRunMode)).findFirst().orElse(AppRunMode.NORMAL);
		return aAppRunMode;
	}
}
