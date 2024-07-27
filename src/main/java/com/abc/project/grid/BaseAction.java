/****************************************************************************
 * File Name 		: BaseAction.java
 * Package			: com.dxc.zurich.grid
 * Author			: pmusunuru2
 * Creation Date	: May 31, 2021
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.grid;

import java.util.HashMap;

/**
 * @author pmusunuru2
 * @since May 31, 2021 3:15:15 pm
 */
public interface BaseAction 
{
	
	String ACTION_KEY = "action";
	
	String HOME_NAME_KEY = "HOST_NAME";
	
	String SCENARIO_BEAN_KEY = "TEST_SUITE";
	
	String BROWSER_CONFIG_BEAN_KEY = "BROWSER_SUITE";
	
	String OTHER_SUMMARY_BEAN_KEY = "DEFAULT_SUMMARY_SUITE";
	
	String SCENARIO_STATUS_KEY = "STATUS";
	
	String TEST_STEP_FILE_NAME = "FILE_NAME";
	
	String TEST_STEP_FILE = "FILE";
	
	String TEST_STEP_SCENARIO_NAME = "SCENARIO_NAME";
	
	String TEST_STEP_SCENARIO_DESCRIPTION = "SCENARIO_DESCRIPTION";
	
	String TEST_STEP_LOG_MESSAGE = "LOG_MESSAGE";
	
	String execute(HashMap<String, String> aMessageMap);
}
