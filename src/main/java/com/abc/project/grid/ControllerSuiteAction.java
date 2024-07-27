/****************************************************************************
 * File Name 		: ControllerSuiteAction.java
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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abc.project.beans.AppEnvConfigBean;
import com.abc.project.beans.TestSuiteBean;
import com.abc.project.config.AppConfig;
import com.abc.project.config.MasterConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.enums.StartFinish;
import com.abc.project.utils.AppUtils;
import com.google.gson.Gson;

/**
 * @author pmusunuru2
 * @since May 31, 2021 3:16:46 pm
 */
public class ControllerSuiteAction implements BaseAction {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(ControllerSuiteAction.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	private static ControllerSuiteAction instance;

	public static ControllerSuiteAction getInstance() {
		if (null == instance) {
			synchronized (ControllerSuiteAction.class) {
				if (null == instance) {
					instance = new ControllerSuiteAction();
				}
			}
		}
		return instance;
	}

	@Override
	public String execute(HashMap<String, String> aMessageMap) {
		String strLogMessage = AppUtils.formatMessage("Executing ControllerSuite with data {0}", aMessageMap);
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
		LinkedList<TestSuiteBean> lstResultTestSuiteData = new LinkedList<>();
		try {
			AppEnvConfigBean aPPRunEnv = MasterConfig.getInstance().getAppEnvConfigBean();
			String strClientHostName = aMessageMap.get(HOME_NAME_KEY);
			List<TestSuiteBean> lstTestSuiteData = AppConfig.getInstance().getFilteredControllerSuiteByStatus();
			Set<String> stHosts = new HashSet<>();
			stHosts.add(strClientHostName);
			stHosts.add(aPPRunEnv.getHostAddress());
			lstTestSuiteData.stream().forEach(aConfig -> {
				stHosts.add(aConfig.getHostAddress());
			});
			int iTestDataSize = CollectionUtils.size(lstTestSuiteData);
			int iScenarioCount = Math.floorDiv(iTestDataSize, stHosts.size());
			int iScenarioIndex = 0;
			for (String strHost : stHosts) {
				for (int i = 0; i < iTestDataSize; i++) {
					int iValueIndex = iScenarioIndex + i;
					if (iValueIndex > CollectionUtils.size(lstTestSuiteData)) {
						break;
					}
					TestSuiteBean aTestSuiteBean = lstTestSuiteData.get(iValueIndex);
					aTestSuiteBean.setHostAddress(strHost);
					if (StringUtils.equalsIgnoreCase(strHost, strClientHostName)) {
						lstResultTestSuiteData.add(aTestSuiteBean);
					}
				}
				iScenarioIndex = iScenarioIndex + iScenarioCount;
			}
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
		Gson aGson = AppUtils.getDefaultGson();
		return aGson.toJson(lstResultTestSuiteData);
	}
}
