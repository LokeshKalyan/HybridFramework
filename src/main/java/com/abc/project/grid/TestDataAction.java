/****************************************************************************
 * File Name 		: TestDataAction.java
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

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abc.project.beans.TestDataBean;
import com.abc.project.beans.TestSuiteBean;
import com.abc.project.config.AppConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.enums.StartFinish;
import com.abc.project.utils.AppUtils;
import com.google.gson.Gson;

/**
 * @author pmusunuru2
 * @since Jun 10, 2021 9:58:32 am
 */
public class TestDataAction implements BaseAction {

	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(TestDataAction.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	private static TestDataAction instance;

	public static TestDataAction getInstance() {
		if (null == instance) {
			synchronized (TestDataAction.class) {
				if (null == instance) {
					instance = new TestDataAction();
				}
			}
		}
		return instance;
	}
	
	@Override
	public String execute(HashMap<String, String> aMessageMap) 
	{
		String strLogMessage = AppUtils.formatMessage("Fetching Scenario test Data with data {0}", aMessageMap);
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
		String strTestSiteBean = aMessageMap.get(SCENARIO_BEAN_KEY);
		try {
			Gson aGson = AppUtils.getDefaultGson();
			TestSuiteBean aTestSuiteBean = aGson.fromJson(strTestSiteBean, TestSuiteBean.class);
			TestDataBean aTestDataBean = AppConfig.getInstance().getTestDataBean(aTestSuiteBean);
			return aGson.toJson(aTestDataBean);
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return null;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

}
