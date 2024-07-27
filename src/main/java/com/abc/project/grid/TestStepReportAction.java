/****************************************************************************
 * File Name 		: TestStepReportAction.java
 * Package			: com.dxc.zurich.grid
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
package com.abc.project.grid;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.constants.AppConstants;
import com.abc.project.enums.StartFinish;
import com.abc.project.reports.TestStepReport;
import com.abc.project.utils.AppUtils;
import com.abc.project.utils.FileUtility;
import com.aventstack.extentreports.Status;
import com.google.gson.Gson;

/**
 * @author pmusunuru2
 * @since Jun 10, 2021 10:46:58 am
 */
public class TestStepReportAction implements BaseAction {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(TestStepReportAction.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	private static TestStepReportAction instance;

	public static TestStepReportAction getInstance() {
		if (null == instance) {
			synchronized (TestStepReportAction.class) {
				if (null == instance) {
					instance = new TestStepReportAction();
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
		String strStatus = aMessageMap.get(SCENARIO_STATUS_KEY);
		String strFileName = aMessageMap.get(TEST_STEP_FILE_NAME);
		String strFile = aMessageMap.get(TEST_STEP_FILE);
		String testScenarioName = aMessageMap.get(TEST_STEP_SCENARIO_NAME);
		String stepDescription = aMessageMap.get(TEST_STEP_SCENARIO_DESCRIPTION);
		String strTestStepLogMessage = aMessageMap.get(TEST_STEP_LOG_MESSAGE);
		try {
			Gson aGson = AppUtils.getDefaultGson();
			Status aStatus = aGson.fromJson(strStatus, Status.class);
			BrowsersConfigBean aBrowsersConfigBean = aGson.fromJson(strBrowserConfig, BrowsersConfigBean.class);
			File aSnapShotFile = null;
			if(!StringUtils.isEmpty(StringUtils.trim(strFileName)) && !StringUtils.isEmpty(StringUtils.trim(strFile)))
			{
				aSnapShotFile = TestStepReport.getSceenShotFile(aBrowsersConfigBean, testScenarioName);
				byte[] aFileData = aGson.fromJson(strFile, byte[].class);
				FileUtility.writeDataToFile(aFileData, aSnapShotFile);				
			}
			String strResult = TestStepReport.defaultlogReport(aStatus, aBrowsersConfigBean, testScenarioName, stepDescription, strTestStepLogMessage, aSnapShotFile);
			return strResult;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

}
