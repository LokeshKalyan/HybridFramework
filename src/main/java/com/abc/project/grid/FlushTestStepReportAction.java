/****************************************************************************
 * File Name 		: FlushTestStepReportAction.java
 * Package			: com.dxc.zurich.grid
 * Author			: adm-PRMUSUN1
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
package com.abc.project.grid;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.constants.AppConstants;
import com.abc.project.enums.StartFinish;
import com.abc.project.reports.TestStepReport;
import com.abc.project.utils.AppUtils;
import com.google.gson.Gson;

/**
 * @author adm-PRMUSUN1
 * @since Jun 10, 2021 8:32:27 AM
 */
public class FlushTestStepReportAction implements BaseAction {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(TestStepReportAction.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	private static FlushTestStepReportAction instance;

	public static FlushTestStepReportAction getInstance() {
		if (null == instance) {
			synchronized (FlushTestStepReportAction.class) {
				if (null == instance) {
					instance = new FlushTestStepReportAction();
				}
			}
		}
		return instance;
	}

	@Override
	public String execute(HashMap<String, String> aMessageMap) {
		String strLogMessage = AppUtils.formatMessage("Reporting Scenario test step with data {0}", aMessageMap);
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
		String strBrowserConfig = aMessageMap.get(BROWSER_CONFIG_BEAN_KEY);
		String testScenarioName = aMessageMap.get(TEST_STEP_SCENARIO_NAME);
		try {
			Gson aGson = AppUtils.getDefaultGson();
			BrowsersConfigBean aBrowsersConfigBean = aGson.fromJson(strBrowserConfig, BrowsersConfigBean.class);
			TestStepReport.flushReport(aBrowsersConfigBean, testScenarioName);
			return String.valueOf(true);
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
