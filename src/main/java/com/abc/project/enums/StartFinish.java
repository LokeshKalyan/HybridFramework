/****************************************************************************
 * File Name 		: StartFinish.java
 * Package			: com.dxc.zurich.enums
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
package com.abc.project.enums;

import com.abc.project.utils.AppUtils;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 11:06:06 am
 */
public enum StartFinish 
{

	START("START"),END ("END"),	
	APP_START_BANNER     ("************ {0} - A T : S T A R T *********************"),
	APP_END_BANNER		 ("************ {0} - A T : E N D     *********************"),
	TESTSTEP_START_BANNER("************ {0} - T E S T - S T E P : S T A R T *********************"),
	TESTSTEP_END_BANNER  ("************ {0} - T E S T - S T E P : E N D     *********************");
	
	private String strMsg;
	
	private StartFinish(String strMsg) {
		this.strMsg = strMsg;
	}
	
	public String getMsg() {
		return this.strMsg;
	}
	
	public String getDefaultFormattedMsg(Object... msgDesc) 
	{
		return AppUtils.formatMessage(getMsg(), msgDesc);
	}
	
	public String getFormattedMsg(String strMsgDesc) 
	{
		return String.format("%s - %s", strMsgDesc,getMsg());
	}	
}
