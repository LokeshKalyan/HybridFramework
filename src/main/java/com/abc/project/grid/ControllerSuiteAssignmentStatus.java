/****************************************************************************
 * File Name 		: ControllerSuiteAssignmentStatus.java
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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abc.project.beans.TestSuiteBean;
import com.abc.project.config.AppConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.enums.StartFinish;
import com.abc.project.utils.AppUtils;
import com.google.gson.Gson;

/**
 * @author pmusunuru2
 * @since May 31, 2021 5:28:41 pm
 */
public class ControllerSuiteAssignmentStatus implements BaseAction {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(ControllerSuiteAssignmentStatus.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	private static ControllerSuiteAssignmentStatus instance;

	public static ControllerSuiteAssignmentStatus getInstance() {
		if (null == instance) {
			synchronized (ControllerSuiteAssignmentStatus.class) {
				if (null == instance) {
					instance = new ControllerSuiteAssignmentStatus();
				}
			}
		}
		return instance;
	}

	@Override
	public String execute(HashMap<String, String> aMessageMap) {
		String strLogMessage = AppUtils.formatMessage("Checking Assignment status with data {0}", aMessageMap);
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
		String strClientHostName = aMessageMap.get(HOME_NAME_KEY);
		String strTestSiteBean = aMessageMap.get(SCENARIO_BEAN_KEY);
		try {
			Gson aGson = AppUtils.getDefaultGson();
			TestSuiteBean aTestSuiteBean =  aGson.fromJson(strTestSiteBean, TestSuiteBean.class);
			TestSuiteBean aResultTestSuiteBean = AppConfig.getInstance().getFilteredControllerSuiteByScenario(aTestSuiteBean);
			boolean isAssigned = aTestSuiteBean != null && StringUtils.equalsIgnoreCase(aResultTestSuiteBean.getHostAddress(), strClientHostName);
			return String.valueOf(isAssigned);
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return String.valueOf(false);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}

		
	}

}
