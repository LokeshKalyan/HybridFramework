/****************************************************************************
 * File Name 		: ActionFactory.java
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

import com.abc.project.constants.ErrorMsgConstants;
import com.abc.project.enums.GridActionType;
import com.abc.project.utils.AppUtils;

/**
 * @author pmusunuru2
 * @since May 31, 2021 3:16:17 pm
 */
public class ActionFactory {

	
	/**
	 * The factory class to return the actual action class (singleton object) based
	 * on the action parameter passed by user.
	 * 
	 * @param action <br>
	 *               action=submit : to submit the batch<br>
	 *               action=status : to check status of previous submit.<br>
	 *               action=wait4EndThenStatus : wait in loop till it completes or
	 *               abort or cancelled.<br>
	 * @return BaseAction object.
	 * @throws Exception : If no recognized action.
	 * @since May 31, 2021 3:16:17 pm
	 */
	public static BaseAction getAction(String strAction) throws Exception {
		
		GridActionType aGridActionType = GridActionType.getGridActionTypeByAction(strAction);
		switch (aGridActionType) {
		case CONTROLLER_SUTE:
			return ControllerSuiteAction.getInstance();
		case BROWSER_CONFIG:
			return BrowserConfigAction.getInstance();
		case HAS_SCENARIO_ASSIGNED:
			return ControllerSuiteAssignmentStatus.getInstance();
		case UPDATE_STATUS:
			return UpdateWorkStatus.getInstance();
		case TEST_DATA:
			return TestDataAction.getInstance();
		case TEST_STEP_REPORT:
			return TestStepReportAction.getInstance();
		case FLUSH_STEP_REPORT:
			return FlushTestStepReportAction.getInstance();
		default:
			throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.UNKNOWN_ACTION, strAction));
		}
	}
}
