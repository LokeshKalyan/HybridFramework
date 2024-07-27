/****************************************************************************
 * File Name 		: ALMTestCaseReport.java
 * Package			: com.dxc.zurich.reports
 * Author			: pmusunuru2
 * Creation Date	: Aug 12, 2021
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.reports;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abc.project.alm.ALMRestAPIQualityCenter;
import com.abc.project.alm.AlmConnector;
import com.abc.project.alm.RestConnector;
import com.abc.project.alm.beans.ALMWrapperConfigBean;
import com.abc.project.alm.beans.Run;
import com.abc.project.alm.beans.TestInstance;
import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.beans.TestSuiteBean;
import com.abc.project.config.MasterConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.enums.StartFinish;
import com.abc.project.utils.AppUtils;
import com.abc.project.utils.PropertyHandler;
import com.abc.project.utils.RunTimeDataUtils;
import com.dxc.enums.ExecutionStatus;

/**
 * @author pmusunuru2
 * @since Aug 12, 2021 12:58:06 pm
 */
public class ALMTestCaseReport {

	private static final Logger LOGGER = LogManager.getLogger(ALMTestCaseReport.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	private static boolean canUpdateStatus() {
		String strSysStatus = System.getProperty(AppConstants.BROWSER_UPDATE_ALMSTATUS_KEY);
		String strStatus = StringUtils.isEmpty(StringUtils.trim(strSysStatus)) ? PropertyHandler
				.getExternalString(AppConstants.BROWSER_UPDATE_ALMSTATUS_KEY, AppConstants.APP_PROPERTIES_NAME)
				: StringUtils.trim(strSysStatus);
		return BooleanUtils.toBoolean(strStatus);
	}

	public static void updateALMResults(TestSuiteBean aTestSuiteBean, String strExecResult) {

		BrowsersConfigBean aBrowsersConfigBean = aTestSuiteBean.getBrowsersConfigBean();
		String testScenarioName = aTestSuiteBean.getScenarioName();
		ALMWrapperConfigBean almWrapperConfigBean = aTestSuiteBean == null ? null
				: aTestSuiteBean.getALMWrapperConfigBean();
		if (almWrapperConfigBean == null) {
			return;
		}

		String strLogMessgae = AppUtils.formatMessage("Updating ALM Results for Scenario {0}", testScenarioName);
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessgae));
		String strStatus = getALMExecutionStatus(strExecResult);
		String strALMTestCaseStatus = almWrapperConfigBean.getALMTestCaseStatus();
		if (StringUtils.equalsIgnoreCase(strALMTestCaseStatus, AppConstants.ALM_TEST_CASE_PARTIAL_STATUS)
				&& StringUtils.equalsIgnoreCase(strStatus, getALMExecutionStatus(ExecutionStatus.PASS.getStatus()))) {
			strStatus = getALMExecutionStatus(ExecutionStatus.STARTED.getStatus());
		}
		almWrapperConfigBean.setALMExecutionStatus(strStatus);
		AlmConnector aALMConnector = null;
		try {
			File aScreenShotFilePath = TestStepReport.getSceenShotFolder(aBrowsersConfigBean, testScenarioName);
			if (aScreenShotFilePath == null || !aScreenShotFilePath.exists() || !aScreenShotFilePath.isDirectory()) {
				return;
			}
			
			Collection<File> collScnFile = FileUtils.listFiles(aScreenShotFilePath.getParentFile(), null, true);
			List<File> lstScreenShotDocFiles = collScnFile.stream()
					.filter(aFile -> !aFile.isDirectory()
							&& StringUtils.containsIgnoreCase(aFile.getName(), MasterConfig.getInstance().getAppRunID())
							&& (StringUtils.endsWithIgnoreCase(aFile.getName(), AppConstants.HTML_REPORT_EXTENSION)
									|| StringUtils.endsWithIgnoreCase(aFile.getName(),
											AppConstants.DOCUMENT_REPORT_EXTENSION)
									|| StringUtils.endsWithIgnoreCase(aFile.getName(),
											AppConstants.PDF_DOCUMENT_REPORT_EXTENSION)))
					.collect(Collectors.toList());

			almWrapperConfigBean.setALMAttachments(lstScreenShotDocFiles);
			// Update into RuntimeDocument
			RunTimeDataUtils.editALMRuntimeValues(aTestSuiteBean, aBrowsersConfigBean);

			if (!canUpdateStatus()) {
				return;
			}

			aALMConnector = ALMRestAPIQualityCenter.initAlmConnection(almWrapperConfigBean);
			RestConnector aRestConnector = aALMConnector.getRestConnector();
			aRestConnector.getQCSession();
			TestInstance aSelectedTestInstance = ALMRestAPIQualityCenter.getTestInstance(aRestConnector,
					almWrapperConfigBean);
			if (aSelectedTestInstance == null) {
				LOGGER.error(AppUtils.formatMessage("Scenario {0} is not found in ALM",
						almWrapperConfigBean.getOriginalScenarioName()));
				return;
			}
			Run aRun = ALMRestAPIQualityCenter.getALMRun(aRestConnector, almWrapperConfigBean, aSelectedTestInstance);
			if (aRun == null) {
				LOGGER.error(AppUtils.formatMessage("Failed to upload {0} To ALM",
						almWrapperConfigBean.getOriginalScenarioName()));
			}
		} catch (Throwable th) {
			String strErrorMessage = AppUtils.formatMessage("Error While {0}", strLogMessgae);
			LOGGER.error(strErrorMessage);
			ERROR_LOGGER.error(strErrorMessage, th);
		} finally {
			if (aALMConnector != null) {
				try {
					aALMConnector.logout();
				} catch (Exception e) {
				}
			}
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessgae));
		}
	}

	private static String getALMExecutionStatus(String strExecResult) {
		ExecutionStatus aExecutionStatus = ExecutionStatus.getExecutionStatusByStatus(strExecResult);
		switch (aExecutionStatus) {
		case PASS:
		case COMPLETED:
			return Run.STATUS_PASSED;
		case FAIL:
		case STOPED:
			return Run.STATUS_FAILED;
		case WARING:
		case STARTED:
			return Run.STATUS_NOT_COMPLETED;
		default:
			return Run.STATUS_NA;
		}
	}
}
