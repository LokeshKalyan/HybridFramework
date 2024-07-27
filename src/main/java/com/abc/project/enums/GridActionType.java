/****************************************************************************
 * File Name 		: GridActionType.java
 * Package			: com.dxc.zurich.enums
 * Author			: pmusunuru2
 * Creation Date	: Jun 10, 2021
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
 * @since Jun 10, 2021 10:19:52 am
 */
public enum GridActionType 
{
	//Action Name
	BROWSER_CONFIG("loadBrowserConfig"),
	CONTROLLER_SUTE("loadControllerSuiteBean"),
	HAS_SCENARIO_ASSIGNED("workAssign"),
	UPDATE_STATUS("updateStatus"),
	TEST_DATA("testData"),
	TEST_STEP_REPORT("testStepReport"),
	FLUSH_STEP_REPORT("flushStepReport"),
	INVALID("INVALID-ACTION");
	private String strAction;

	GridActionType(String strAction){
		this.setAction(strAction);
	}
	
	/**
	 * @return the strAction
	 */
	public String getAction() {
		return strAction;
	}

	/**
	 * @param strAction the strAction to set
	 */
	public void setAction(String strAction) {
		this.strAction = strAction;
	}
	
	
	@Override
	public String toString() {
		return getAction();
	}
	
	public static GridActionType getGridActionTypeByAction(final String strAction) {
		List<GridActionType> lstGridActionTypes = Arrays.asList(GridActionType.values());
		GridActionType aGridActionType = lstGridActionTypes.stream().filter(aConfig -> StringUtils.equalsIgnoreCase(aConfig.getAction(), strAction)).findFirst().orElse(GridActionType.INVALID);
		return aGridActionType;	
	}
}
