/****************************************************************************
 * File Name 		: AppPriority.java
 * Package			: com.dxc.zurich.enums
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
package com.abc.project.enums;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * @author pmusunuru2
 * @since Dec 05, 2022 11:19:33 am
 */
public enum AppPriority {

	APP_PRIORITY_HIGH("High", 100), APP_PRIORITY_MEDIUM("Medium", 50), APP_PRIORITY_LOW("Low", 30),
	APP_PRIORITY_ALL("ALL", 10), APP_PRIORITY_NORMAL("Normal", 0);

	private String strAppPriorityMode;
	private int iAPPProrityValue;

	AppPriority(String strAppPriorityMode, int iAPPProrityValue) {
		this.setAppPriorityMode(strAppPriorityMode);
		this.setAPPProrityValue(iAPPProrityValue);
	}

	/**
	 * @return the strAppPriorityMode
	 */
	public String getAppPriorityMode() {
		return strAppPriorityMode;
	}

	/**
	 * @param strAppPriorityMode the strAppPriorityMode to set
	 */
	public void setAppPriorityMode(String strAppPriorityMode) {
		this.strAppPriorityMode = strAppPriorityMode;
	}

	/**
	 * @return the iAPPProrityValue
	 */
	public int getAPPProrityValue() {
		return iAPPProrityValue;
	}

	/**
	 * @param iAPPProrityValue the iAPPProrityValue to set
	 */
	public void setAPPProrityValue(int iAPPProrityValue) {
		this.iAPPProrityValue = iAPPProrityValue;
	}

	@Override
	public String toString() {
		return String.format("App Running with Priority %s - value %s", getAppPriorityMode(), getAPPProrityValue());
	}

	public static AppPriority getAppPriorityByPriorityMode(String strAppPriorityMode) {
		List<AppPriority> lstAppPriorities = Arrays.asList(AppPriority.values());
		AppPriority aAPPriority = lstAppPriorities.stream()
				.filter(aConfig -> StringUtils.equalsIgnoreCase(aConfig.getAppPriorityMode(), strAppPriorityMode))
				.findFirst().orElse(AppPriority.APP_PRIORITY_NORMAL);
		return aAPPriority;
	}
}
