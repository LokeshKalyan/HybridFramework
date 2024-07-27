/****************************************************************************
 * File Name 		: UpdateWorkStatus.java
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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abc.project.beans.AppEnvConfigBean;
import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.beans.TestSuiteBean;
import com.abc.project.config.AppConfig;
import com.abc.project.config.AppContext;
import com.abc.project.config.MasterConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.SummaryReportConstants;
import com.abc.project.enums.StartFinish;
import com.abc.project.reports.ConsolidateTestReport;
import com.abc.project.utils.AppUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author pmusunuru2
 * @since May 31, 2021 5:47:16 pm
 */
public class UpdateWorkStatus implements BaseAction {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(UpdateWorkStatus.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	private static UpdateWorkStatus instance;

	public static UpdateWorkStatus getInstance() {
		if (null == instance) {
			synchronized (UpdateWorkStatus.class) {
				if (null == instance) {
					instance = new UpdateWorkStatus();
				}
			}
		}
		return instance;
	}

	@Override
	public String execute(HashMap<String, String> aMessageMap) {
		String strLogMessage = AppUtils.formatMessage("Updating Scenario status with data {0}", aMessageMap);
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
		String strTestSiteBean = aMessageMap.get(SCENARIO_BEAN_KEY);
		String strBrowserConfig = aMessageMap.get(BROWSER_CONFIG_BEAN_KEY);
		String strStatus = aMessageMap.get(SCENARIO_STATUS_KEY);
		try {
			Gson aGson = AppUtils.getDefaultGson();
			TestSuiteBean aTestSuiteBean = aGson.fromJson(strTestSiteBean, TestSuiteBean.class);
			AppConfig.getInstance().updateScenarioExecutionStatus(aTestSuiteBean, strStatus);
			if (!StringUtils.equalsIgnoreCase(strStatus, AppConstants.TEST_RESULT_OTHERS)) {
				BrowsersConfigBean aBrowsersConfigBean = aGson.fromJson(strBrowserConfig, BrowsersConfigBean.class);
				String[] strMessages = { SummaryReportConstants.SUMMARY_SHEETNAME,
						SummaryReportConstants.REF_NUM_SHEETNAME, SummaryReportConstants.QUOTE_SUMMARY_SHEETNAME,
						OTHER_SUMMARY_BEAN_KEY };
				for (String strMessageKey : strMessages) {
					addExecutionSummaryReport(aMessageMap, strMessageKey, aGson, aTestSuiteBean, aBrowsersConfigBean);
				}
				AppEnvConfigBean aPPRunEnv = MasterConfig.getInstance().getAppEnvConfigBean();
				ConsolidateTestReport.createExecutionReport(aTestSuiteBean, aPPRunEnv, aBrowsersConfigBean, strStatus);
			}
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

	private void addExecutionSummaryReport(HashMap<String, String> aMessageMap, String strMessageKey, Gson aGson,
			TestSuiteBean aTestSuiteBean, BrowsersConfigBean aBrowsersConfigBean) {
		Type aNumSheet = new TypeToken<LinkedHashMap<String, String>>() {
		}.getType();
		AppContext appConText = AppContext.getInstance();
		String strValue = getMessageValueByKey(aMessageMap, strMessageKey);
		if (StringUtils.isEmpty(strValue)) {
			return;
		}
		String strScenarioName = aTestSuiteBean.getScenarioName();
		switch (strMessageKey) {
		case SummaryReportConstants.SUMMARY_SHEETNAME:
			LinkedHashMap<String, String> mpSummarySheet = aGson.fromJson(strValue, aNumSheet);
			if (mpSummarySheet != null && !mpSummarySheet.isEmpty()) {
				mpSummarySheet.entrySet().stream().forEach(aConfig -> {
					appConText.addExecSurmmaryReport(strScenarioName, aBrowsersConfigBean, aConfig.getKey(),
							aConfig.getValue());
				});
			}
			break;
		case SummaryReportConstants.REF_NUM_SHEETNAME:
			LinkedHashMap<String, String> mpRefSheet = aGson.fromJson(strValue, aNumSheet);
			if (mpRefSheet != null && !mpRefSheet.isEmpty()) {
				mpRefSheet.entrySet().stream().forEach(aConfig -> {
					appConText.addExecRefSurmmaryReport(strScenarioName, aBrowsersConfigBean, aConfig.getKey(),
							aConfig.getValue());
				});
			}
			break;
		case SummaryReportConstants.QUOTE_SUMMARY_SHEETNAME:
			LinkedHashMap<String, String> mpQuoteSheet = aGson.fromJson(strValue, aNumSheet);
			if (mpQuoteSheet != null && !mpQuoteSheet.isEmpty()) {
				mpQuoteSheet.entrySet().stream().forEach(aConfig -> {
					appConText.addExecQuotationSurmmaryReport(strScenarioName, aBrowsersConfigBean, aConfig.getKey(),
							aConfig.getValue());
				});
			}
			break;
		case OTHER_SUMMARY_BEAN_KEY:
		default:
			Type aDefaultSheet = new TypeToken<LinkedHashMap<String, LinkedHashMap<String, LinkedList<String>>>>() {
			}.getType();
			LinkedHashMap<String, LinkedHashMap<String, LinkedList<String>>> mpOtherSummary = aGson.fromJson(strValue,
					aDefaultSheet);
			if (mpOtherSummary != null && !mpOtherSummary.isEmpty()) {
				mpOtherSummary.entrySet().stream().forEach(aConfig -> {
					LinkedHashMap<String, LinkedList<String>> mpOtherSummaryBean = aConfig.getValue();
					if (mpOtherSummaryBean != null && !mpOtherSummaryBean.isEmpty()) {
						mpOtherSummaryBean.entrySet().stream().forEach(aOtherSummaryBeanConfig -> {
							appConText.addOtherSummaryReport(strScenarioName, aBrowsersConfigBean, aConfig.getKey(),
									aOtherSummaryBeanConfig.getKey(), aOtherSummaryBeanConfig.getValue());
						});
					}
				});
			}
			break;
		}
	}

	private String getMessageValueByKey(HashMap<String, String> aMessageMap, String strMessageKey) {
		if (aMessageMap.containsKey(strMessageKey)) {
			return aMessageMap.get(strMessageKey);
		}
		return null;
	}

}
