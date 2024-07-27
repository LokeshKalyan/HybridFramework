/****************************************************************************
 * File Name 		: TestStep.java
 * Package			: com.dxc.zurich.base
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
package com.abc.project.base;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import com.abc.project.beans.KeyWordConfigBean;
import com.abc.project.beans.TestDataBean;
import com.abc.project.beans.TestSuiteBean;
import com.abc.project.config.AppContext;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.RunTimeDataConstants;
import com.abc.project.constants.SummaryReportConstants;
import com.abc.project.reports.TestStepReport;
import com.abc.project.utils.PropertyHandler;
import com.dxc.enums.ExecutionStatus;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 2:05:08 pm
 */
public class TestStep extends ControllerScript {

	private TestDataBean aTestDataBean;
	private boolean bWarning;

	/**
	 * 
	 * @param aBrowser
	 * @param aWebDriver
	 * @param aTestDataBean
	 */
	public TestStep(TestSuiteBean aTestSuite, TestDataBean aTestDataBean, WebDriver aWebDriver) {
		super(aTestSuite, aWebDriver);
		setTestDataBean(aTestDataBean);
		bWarning = false;
	}

	/**
	 * @return the aTestDataBean
	 */
	public TestDataBean getTestDataBean() {
		return aTestDataBean;
	}

	/**
	 * @param aTestDataBean the aTestDataBean to set
	 */
	public void setTestDataBean(TestDataBean aTestDataBean) {
		this.aTestDataBean = aTestDataBean;
	}

	/**
	 * Executes the test step
	 * 
	 * @return
	 * @throws Exception
	 */
	public String executeTestStep() throws Exception {
		String strResult = AppConstants.TEST_RESULT_FAIL;
		String testScenarioName = "Un-Known";
		String stepDescription = "Un-Known";
		if (getTestSuite() == null || getTestDataBean() == null || getWebDriver() == null) {
			return strResult;
		}
		testScenarioName = getTestDataBean().getScenarioName();
		stepDescription = getTestSuite().getDescription();
		AppContext aPPContext = getApplicationContext();
		aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), RunTimeDataConstants.SECNARIO_HEADER,
				testScenarioName);
		aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
				RunTimeDataConstants.BROWSER_DISPLAY_HEADER, getBrowsersConfigBean().getBrowserDisplayName());

		aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
				SummaryReportConstants.SECNARIO_HEADER, testScenarioName);
		aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
				SummaryReportConstants.SECNARIO_DESCRIPTION_HEADER, stepDescription);
		for (int i = 0; i < getTestDataBean().getControlData().size(); i++) {
			String strTestData = getTestDataBean().getControlData().get(i);
			String strLogMessage = getTestDataBean().getMessageDescription().get(i);
			boolean isSnapRequired = getTestDataBean().getScreenShot().get(i);
			String strObJPropName = getTestDataBean().getObjectProperty().get(i);
			String strReportKeyWord = getTestDataBean().getReportKeyWord().get(i);
			KeyWordConfigBean aKeyWordConfigBean = getTestDataBean().getKeyWord().get(i);
			if (StringUtils.isEmpty(strTestData) || aKeyWordConfigBean == null) {
				continue;
			}
			strResult = execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
					strObJPropName, aKeyWordConfigBean);
			ExecutionStatus aExecutionStatus = ExecutionStatus.getExecutionStatusByStatus(strResult);
			boolean isWaitKeyWord = aKeyWordConfigBean.canLogsExcluded();
			if (!isWaitKeyWord || (isWaitKeyWord && aExecutionStatus != ExecutionStatus.PASS)) {

				switch (aExecutionStatus) {
				case PASS:
					TestStepReport.logSucess(getBrowsersConfigBean(), testScenarioName, stepDescription, strLogMessage,
							isSnapRequired, getWebDriver());
					break;
				case FAIL:
					TestStepReport.logFailure(getBrowsersConfigBean(), testScenarioName, stepDescription, strLogMessage,
							true, getWebDriver());
					break;
				case WARING:
					TestStepReport.logWarning(getBrowsersConfigBean(), testScenarioName, stepDescription, strLogMessage,
							true, getWebDriver());
					bWarning = true;
					break;
				default:
					TestStepReport.logInfo(getBrowsersConfigBean(), testScenarioName, stepDescription, strLogMessage,
							isSnapRequired, getWebDriver());
					break;
				}
			}
			if (isTestSetpFailed(strResult)) {
				if (canCreateFailedFolder()) {
					File aSrcFile = TestStepReport.getSceenShotFolder(getBrowsersConfigBean(), testScenarioName);
					File aDestFile = TestStepReport.getFailedSceenShotFolder(getBrowsersConfigBean(), testScenarioName);
					FileUtils.copyDirectory(aSrcFile, aDestFile);
				}
				break;
			}
		}
		if (bWarning && !isTestSetpFailed(strResult)) {
			strResult = AppConstants.TEST_RESULT_WARING;
		}
		return strResult;
	}

	public static boolean canCreateFailedFolder() {
		return BooleanUtils.toBoolean(PropertyHandler.getExternalString(
				AppConstants.FAILED_SCENARIOS_COPYINGTO_NEWFOLDER, AppConstants.APP_PROPERTIES_NAME));
	}
}
