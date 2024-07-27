/****************************************************************************
 * File Name 		: ControllerScript.java
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

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;

import com.abc.project.beans.AppEnvConfigBean;
import com.abc.project.beans.BarcodeInfo;
import com.abc.project.beans.KeyWordConfigBean;
import com.abc.project.beans.TestSuiteBean;
import com.abc.project.config.AppConfig;
import com.abc.project.config.AppContext;
import com.abc.project.config.MasterConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.ErrorMsgConstants;
import com.abc.project.constants.ORConstants;
import com.abc.project.constants.RunTimeDataConstants;
import com.abc.project.constants.SummaryReportConstants;
import com.abc.project.database.DBUtils;
import com.abc.project.enums.AppRunMode;
import com.abc.project.enums.BrowserType;
import com.abc.project.enums.Browsers;
import com.abc.project.enums.KeyWord;
import com.abc.project.reports.TestStepReport;
import com.abc.project.utils.AppUtils;
import com.abc.project.utils.BarcodeImageDecoderUtil;
import com.abc.project.utils.ClearSessions;
import com.abc.project.utils.ExcelUtils;
import com.abc.project.utils.PropertyHandler;
import com.abc.project.utils.SessionManager;
import com.google.common.collect.Comparators;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 1:24:44 pm
 */
public class ControllerScript extends FunctionLibrary {

	private static final Logger LOGGER = LogManager.getLogger(ControllerScript.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	public ControllerScript(TestSuiteBean aTestSuite, WebDriver aWebDriver) {
		super(aTestSuite, aWebDriver);
	}

	/***
	 * Fetches the Application Context
	 * 
	 * @return
	 */
	@Override
	public AppContext getApplicationContext() {
		return super.getApplicationContext();
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	@Override
	public Logger getErrorLogger() {
		return ERROR_LOGGER;
	}

	/**
	 * Fetches the Verification Data from Verication config file
	 */
	@Override
	public String getVerificationProperty(String strPropertyKey) throws Exception {
		if (StringUtils.isEmpty(strPropertyKey)) {
			throw new Exception(ErrorMsgConstants.ERR_DEFAULT);
		}
		String strVerificationFile = PropertyHandler.getExternalString(AppConstants.VERIFICATION_CONFIG_PATH_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		String strVerificationProp = PropertyHandler.getExternalString(strPropertyKey, strVerificationFile);
		if (StringUtils.equalsIgnoreCase(strVerificationProp, strPropertyKey)) {
			strVerificationProp = null;
		}
		return strVerificationProp;
	}

	/**
	 * Fetches the Object property value based on browser type
	 * 
	 * @param strPropertyKey
	 * @return
	 */
	@Override
	public String getObJectProperty(String strPropertyKey) throws Exception {
		if (StringUtils.isEmpty(StringUtils.trim(strPropertyKey))) {
			return null;
		}
		BrowserType aBrowserType = getBrowser().getBrowserType();
		String strORProperty;
		switch (aBrowserType) {
		case DESKTOP_WEB:
			String strOrDesktopFile = PropertyHandler.getExternalString(AppConstants.ORDESKTOP_CONFIG_PATH_KEY,
					AppConstants.APP_PROPERTIES_NAME);
			strORProperty = PropertyHandler.getExternalString(strPropertyKey, strOrDesktopFile);
			break;
		case MOBILE_WEB:
			String strMobileOrDesktopFile = PropertyHandler.getExternalString(AppConstants.ORMOBILE_CONFIG_PATH_KEY,
					AppConstants.APP_PROPERTIES_NAME);
			strORProperty = PropertyHandler.getExternalString(strPropertyKey, strMobileOrDesktopFile);
			break;
		case MOBILE_NATIVE:
			String strMobileNativeOrDesktopFile = PropertyHandler
					.getExternalString(AppConstants.ORMOBILE_NATIVE_CONFIG_PATH_KEY, AppConstants.APP_PROPERTIES_NAME);
			strORProperty = PropertyHandler.getExternalString(strPropertyKey, strMobileNativeOrDesktopFile);
			break;
		case DESKTOP_NATIVE:
			String strDesktopNativeOrDesktopFile = PropertyHandler
					.getExternalString(AppConstants.ORDESKTOP_NATIVE_CONFIG_PATH_KEY, AppConstants.APP_PROPERTIES_NAME);
			strORProperty = PropertyHandler.getExternalString(strPropertyKey, strDesktopNativeOrDesktopFile);
			break;
		default:
			throw new Exception(BrowserType.INVALID.getBrowserType());
		}
		if (StringUtils.equalsIgnoreCase(strORProperty, strPropertyKey)) {
			strORProperty = null;
		}
		return strORProperty;
	}

	@Override
	protected String getAppEnvValue(String strKey) throws Exception {
		String strAppEnvConfigFile = PropertyHandler.getExternalString(AppConstants.APP_ENV_CONFIG_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		if (StringUtils.isEmpty(strAppEnvConfigFile)
				|| StringUtils.equalsIgnoreCase(strAppEnvConfigFile, AppConstants.APP_ENV_CONFIG_KEY)) {
			return null;
		}
		MasterConfig aMasterConfig = MasterConfig.getInstance();
		LinkedHashMap<String, String> mpEnvDeatils = aMasterConfig.getAppEnvConfigDeatils();
		String strAppEnvValue = MapUtils.getString(mpEnvDeatils, strKey);
		strAppEnvValue = StringUtils.trim(strAppEnvValue);
		if (StringUtils.isEmpty(strAppEnvValue)) {
			throw new Exception(
					MessageFormat.format(ErrorMsgConstants.ERR_ENV_DEATILS_NTFOUND, strKey, strAppEnvConfigFile));
		}
		return strAppEnvValue;
	}

	/***
	 * Calls the Excel Macro
	 * 
	 * @param file
	 * @param macroFunctionName
	 * @param param
	 * @return
	 */
	public String callExcelMacro(File file, String macroFunctionName, String... param) throws Exception {
		String result = null;
		result = AppConstants.TEST_RESULT_FAIL;
		if (file == null || !file.exists()) {
			return result;
		}
		// ComThread.InitSTA(true);
		String strJacobLibPath = PropertyHandler.getExternalString(AppConstants.JACOB_LIB_FILE_NAME_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		File aJacobLibFile = AppUtils.getFileFromPath(strJacobLibPath);
		System.setProperty(AppConstants.JACOB_LIB_PROP_NAME, aJacobLibFile.getPath());
		ComThread.InitSTA();
		final ActiveXComponent excel = new ActiveXComponent("Excel.Application");
		try {
			excel.setProperty("Visible", false);

			Dispatch workbooks = excel.getProperty("Workbooks").toDispatch();
			Dispatch workBook = Dispatch.call(workbooks, "Open", file.getAbsolutePath()).toDispatch();

			// Calls the macro
			String str_MacroFile_Function_Name = "\'" + file.getName() + "\'!" + macroFunctionName;
			com.jacob.com.Variant tempResult;
			if (param == null || param.length <= 0) {
				tempResult = Dispatch.call(excel, "Run", str_MacroFile_Function_Name);
			} else {
				tempResult = Dispatch.call(excel, "Run", str_MacroFile_Function_Name, param);
			}

			result = tempResult.toString();
			Object args = new Object[] { -1 };
			Dispatch.call(workBook, "Close", args);
		} catch (Throwable ex) {
			throw new Exception(
					AppUtils.formatMessage(ErrorMsgConstants.ERR_CALL_EXCEL_MACRO, file.getName(), macroFunctionName));
		} finally {
			excel.invoke("Quit", new com.jacob.com.Variant[0]);
			try {
				ComThread.Release();
			} catch (Throwable th) {
				// TODO: handle exception
			}
		}
		return result;
	}

	@Override
	protected long getDriverExternalWaitTime() {
		String strDriwerWaitTime = PropertyHandler.getExternalString(AppConstants.DRIVER_EXTERNAL_SLEEP_TIME_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		return !StringUtils.isEmpty(strDriwerWaitTime) && StringUtils.isNumeric(strDriwerWaitTime)
				? Long.valueOf(strDriwerWaitTime)
				: AppConstants.DRIVER_EXTERNAL_WAIT_TIME;
	}

	@Override
	protected int getDriverImplicitWaitTime() {
		String strDriwerWaitTime = PropertyHandler.getExternalString(AppConstants.DRIVER_IMPLICIT_WAIT_TIME_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		return !StringUtils.isEmpty(strDriwerWaitTime) && StringUtils.isNumeric(strDriwerWaitTime)
				? Integer.parseInt(strDriwerWaitTime)
				: AppConstants.DRIVER_IMPLICIT_WAIT_TIME;
	}

	@Override
	protected long getDriverExplicitWaitTime() {
		String strDriwerWaitTime = PropertyHandler.getExternalString(AppConstants.DRIVER_EXPLICIT_WAIT_TIME_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		return !StringUtils.isEmpty(strDriwerWaitTime) && StringUtils.isNumeric(strDriwerWaitTime)
				? Long.valueOf(strDriwerWaitTime)
				: AppConstants.DRIVER_EXPLICIT_WAIT_TIME;
	}

	@Override
	protected long getDriverSleepTime() {
		String strDriwerWaitTime = PropertyHandler.getExternalString(AppConstants.DRIVER_SLEEP_TIME_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		return !StringUtils.isEmpty(strDriwerWaitTime) && StringUtils.isNumeric(strDriwerWaitTime)
				? Long.valueOf(strDriwerWaitTime)
				: AppConstants.DRIVER_DEFAULT_SLEEP_TIMEOUT;
	}

	public String execute(String testScenarioName, String stepDescription, String strLogMessage,
			String strReportKeyWord, String strTestData, String strObJPropName, KeyWordConfigBean aKeyWordConfigBean) {
		String strPropertyValue = "";
		WebDriver aDriver = getWebDriver();
		Browsers aBrowser = getBrowser();
		AppContext aPPContext = getApplicationContext();
		String strErrorMsg = AppUtils.formatMessage("Error while perofrming action with keyword {0}",
				aKeyWordConfigBean.getKeyWord().toString());
		try {
			switch (aKeyWordConfigBean.getKeyWord()) {
			/* Launching URL */
			case OPENURL:
				strPropertyValue = getObJectProperty(strTestData);
				strErrorMsg = "Error While Navigating to " + strPropertyValue;
				return openURL(aKeyWordConfigBean, strPropertyValue, true);
			/* Launching URL using JSON(give env in runconfig-aruguments) */
			case OPENURL_ENV:
				String strENV = MasterConfig.getInstance().getAppENV();
				if (StringUtils.equalsIgnoreCase(AppConstants.DEFAULT_TRUE, strTestData)) {
					strTestData = strENV;
				} else {
					strTestData = AppUtils.formatMessage(AppConstants.ENV_URL_FORMAT, strTestData, strENV);
				}
				strPropertyValue = getObJectProperty(strTestData);
				strErrorMsg = "Error While Navigating to " + strPropertyValue;
				return openURL(aKeyWordConfigBean, strPropertyValue, true);
			/* Launching Application */
			case LAUNCH_APP:
				return launchApp(aKeyWordConfigBean, strTestData);
			/* Inputs data into specific fields */
			case INPUT:
			case PASTE:
				strErrorMsg = "Error Entering Data into " + strObJPropName;
				strPropertyValue = getObJectProperty(strObJPropName);
				LOGGER.info("Entering Data into " + strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
			/* Inputs data into specific fields using JSON */
			case INPUT_ENV:
				strErrorMsg = "Error Entering Data into " + strObJPropName;
				strPropertyValue = getObJectProperty(strObJPropName);
				LOGGER.info("Entering Data into " + strObJPropName);
				strTestData = getAppEnvValue(strTestData);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
			/* Formated testdata text input into particular field */
			case FORMAT_INPUT:
				strPropertyValue = getUnWrapedJsonOrProperty(testScenarioName, strObJPropName, strTestData);
				strTestData = getUnWrapedJsonFormatTestData(strTestData);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
			/* Text input using robot keyboard class into particular field */
			case INPUT_ROBOT:
				strErrorMsg = "Error Entering Data into " + strObJPropName;
				strPropertyValue = getObJectProperty(strObJPropName);
				LOGGER.info("Entering Data into " + strObJPropName);
				return inputRobotText(aKeyWordConfigBean, strPropertyValue, strTestData);
			/* Captures text input using robot keyboard class into particular field */
			case INPUT_CAPTURED_ROBOT:
				strErrorMsg = "Error Entering Data into " + strObJPropName;
				strPropertyValue = getObJectProperty(strObJPropName);
				LOGGER.info("Entering Data into " + strObJPropName);
				if (StringUtils.isEmpty(strReportKeyWord)) {
					throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
				}
				strTestData = getRunTimeDataValue(testScenarioName, strReportKeyWord);
				return inputRobotText(aKeyWordConfigBean, strPropertyValue, strTestData);
			/* Clears text field before entering value */
			case INPUTCLEAR:
				strPropertyValue = getObJectProperty(strObJPropName);
				LOGGER.info("clearing Data into " + strObJPropName);
				return clearInput(aKeyWordConfigBean, strPropertyValue, strTestData);
			/* Performs click action */
			case CLICK:
			case CLICKMAINTAIN:
			case CLICKON_LATESTNUM:
				strPropertyValue = getObJectProperty(strObJPropName);
				return clickWebElement(aKeyWordConfigBean, strPropertyValue, false);
			/* Performs click action on PDF LInks in synchronised manner and download pdf */
			case CLICK_DOWNLOAD_FILE:
				strPropertyValue = getObJectProperty(strObJPropName);
				return clickFileToDownload(strReportKeyWord, aKeyWordConfigBean, testScenarioName, strPropertyValue,
						strTestData, false);
			/* performs click action */
			case FORMAT_CLICK:
				strPropertyValue = getUnWrapedJsonOrProperty(testScenarioName, strObJPropName, strTestData);
				return clickWebElement(aKeyWordConfigBean, strPropertyValue, false);
			/* Scrolls page to the bottom */
			case CLICK_PAGEDOWN:
				strErrorMsg = "Error while Clicking on page down";
				strPropertyValue = getObJectProperty(strObJPropName);
				return sendKeys(aKeyWordConfigBean, strPropertyValue, Keys.PAGE_DOWN);
			/* Scrolls page to the UP */
			case CLICK_PAGEUP:
				strErrorMsg = "Error while Clicking on page up";
				strPropertyValue = getObJectProperty(strObJPropName);
				return sendKeys(aKeyWordConfigBean, strPropertyValue, Keys.PAGE_UP);
			/* Clicks on Escape key on keyboard */
			case CLICKESC:
				strErrorMsg = "Error while Clicking on escape";
				strPropertyValue = getObJectProperty(strObJPropName);
				return sendKeys(aKeyWordConfigBean, strPropertyValue, Keys.ESCAPE);
			/* Clicks on Tab key on keyboard */
			case CLICKTAB:
				strErrorMsg = "Error while Clicking on TAB";
				strPropertyValue = getObJectProperty(strObJPropName);
				return sendKeys(aKeyWordConfigBean, strPropertyValue, Keys.TAB);
			/* Clicks on Enter key on keyboard */
			case CLICKENTER:
				strErrorMsg = "Error while Clicking on ENTER";
				strPropertyValue = getObJectProperty(strObJPropName);
				return sendKeys(aKeyWordConfigBean, strPropertyValue, Keys.ENTER);
			/* Clicks on CTRL key on keyboard */
			case CLICK_CONTROL:
				strErrorMsg = "Error while Clicking on Control";
				strPropertyValue = getObJectProperty(strObJPropName);
				return sendKeys(aKeyWordConfigBean, strPropertyValue, Keys.CONTROL);
			/* Clicks on CTRL+Click key on keyboard */
			case CONTROL_CLICK:
				strErrorMsg = "Error while Control and Click";
				strPropertyValue = getObJectProperty(strObJPropName);
				return performControlClick(aKeyWordConfigBean, strPropertyValue);
			/* Focus on webelement and Clicks */
			case FOCUSNCLICK:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Exception occured in FocusClick method";
				return performFocusClick(aKeyWordConfigBean, strPropertyValue);
			/* Focus on webelement and Clicks enter key */
			case FOCUSNENTER:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Exception occured in Focusenter method";
				return performFocusSendKeys(aKeyWordConfigBean, strPropertyValue, true, Keys.ENTER);
			/* Focus on webelement if exist and Clicks enter key */
			case EXISTFOCUSNENTER:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Exception occured in Exit FocusEnter method";
				return performFocusSendKeys(aKeyWordConfigBean, strPropertyValue, false, Keys.ENTER);
			/* Focus on webelement and Clicks space key */
			case FOCUSNPRESSSPACE:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Exception occured in Exit FocusSpace method";
				return performFocusSendKeys(aKeyWordConfigBean, strPropertyValue, true, Keys.SPACE);
			/* Focus on webelement and Clicks using java script executer */
			case FOCUSN_SCRIPT_CLICK:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Exception occured in FocusScript method";
				WebElement aFocusNClickWebElement = getClickWebElement(aKeyWordConfigBean, strPropertyValue);
				return (aFocusNClickWebElement != null
						&& !isWebElementDisabled(aKeyWordConfigBean, strPropertyValue, aFocusNClickWebElement))
								? executeJavaScript(aKeyWordConfigBean, strPropertyValue,
										ORConstants.EXEC_JAVA_SCRIPT_CLICK_CMD, true)
								: AppConstants.TEST_RESULT_FAIL;
			case UNDER_WRITING: // Need to remove in future
				strPropertyValue = ORConstants.BTN_UNDER_WRITING_XPATH;
				strErrorMsg = "Exception occured in Under writing button method";
				return executeJavaScript(aKeyWordConfigBean, strPropertyValue, ORConstants.EXEC_JAVA_SCRIPT_CLICK_CMD,
						true);
			/* Verifies title of a particular webpage */
			case VERIFY_PAGETITLE:
				LOGGER.info("Getting Page Title");
				String strPageTitle = aDriver.getTitle();
				strTestData = getVerificationProperty(strObJPropName);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), RunTimeDataConstants.PAGE_TITLE,
						strPageTitle);
				return verifyText(null, aKeyWordConfigBean, testScenarioName, strPageTitle, strTestData, true);
			/* Randomly inputs string- used for life assured names(Faker class) */
			case INPUT_RANDOM_STRING:
				strPropertyValue = getObJectProperty(strObJPropName);
				LOGGER.info("Entering Data into " + strPropertyValue);
				String strRandomText = getRandomText(aBrowser, testScenarioName, strReportKeyWord, 5);
				if (StringUtils.isEmpty(strRandomText)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				return inputText(aKeyWordConfigBean, strPropertyValue, strRandomText, true);
			/*
			 * Randomly inputs alphanumeric- used for Smokepack-SAS portal during
			 * registration
			 */
			case INPUT_RANDOM_ALPHAMUMERIC:
				strPropertyValue = getObJectProperty(strObJPropName);
				LOGGER.info("Entering Data into " + strPropertyValue);
				String strRandomAlphanumeric = getRandomAlphaNumericString(10);
				if (StringUtils.isEmpty(strRandomAlphanumeric)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				return inputText(aKeyWordConfigBean, strPropertyValue, strRandomAlphanumeric, true);
			/*
			 * Randomly inputs number 6 digit num- used for Smokepack-SAS portal during
			 * registration
			 */
			case INPUT_RANDOM_NUMBER:
				strPropertyValue = getObJectProperty(strObJPropName);
				LOGGER.info("Entering Data into " + strPropertyValue);
				String randomSixDigitNumber = getRandomNumber();
				if (StringUtils.isEmpty(randomSixDigitNumber)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				return inputText(aKeyWordConfigBean, strPropertyValue, randomSixDigitNumber, true);
			/* Inputs client name */
			case INPUT_CLIENT_NAME:
				strPropertyValue = getObJectProperty(strObJPropName);
				LOGGER.info("Entering Data into " + strPropertyValue);
				String strClinetName = inputText(aKeyWordConfigBean, strPropertyValue, strTestData, true);
				if (!isTestSetpFailed(strClinetName)) {
					aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), strReportKeyWord, null);
					aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(), strReportKeyWord,
							null);
				}
				return strClinetName;
			/* Select Dropdown options */
			case SELECT:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error While Selecting " + strPropertyValue;
				return selectByText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
			/* Select Dropdown options using JSON */
			case SELECT_ENV:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error While Selecting " + strPropertyValue;
				strTestData = getAppEnvValue(strTestData);
				return selectByText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
			/* Select Dropdown options using visible text */
			case SELECT_NEGATIVE:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error While Selecting " + strPropertyValue;
				return selectByTextNot(aKeyWordConfigBean, strPropertyValue, strTestData, false);
			/* Select Dropdown options using visible text using JSON */
			case SELECT_NEGATIVE_ENV:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error While Selecting " + strPropertyValue;
				strTestData = getAppEnvValue(strTestData);
				return selectByTextNot(aKeyWordConfigBean, strPropertyValue, strTestData, false);
			/* Select Dropdown options used for client occupation */
			case SELECT_SPL:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error While Selecting " + strPropertyValue;
				if (StringUtils.isEmpty(strTestData)) {
					throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
				}
				String strFormatSPLResult = clickWebElement(aKeyWordConfigBean, strPropertyValue, false);
				if (isTestSetpFailed(strFormatSPLResult)) {
					return strFormatSPLResult;
				}
				delayInSeconds(1);
				String strFormatSPLPropertyValue = String.format(ORConstants.FORMAT_SELECT_SPL, strPropertyValue,
						strTestData);
				return clickWebElement(aKeyWordConfigBean, strFormatSPLPropertyValue, false);
			/* Select Dropdown options by index value(starts from "0") */
			case SELECT_BYINDEX:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error While Selecting Index " + strPropertyValue;
				int iSelectIndex = StringUtils.isNumeric(strTestData) ? Integer.valueOf(strTestData) : 1;
				return selectByIndex(aKeyWordConfigBean, strPropertyValue, iSelectIndex, false);
			/* Select Dropdown options by index value(starts from "0") using JSON */
			case SELECT_BYINDEX_ENV:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error While Selecting Index " + strPropertyValue;
				strTestData = getAppEnvValue(strTestData);
				int iSelectIndexEnv = StringUtils.isNumeric(strTestData) ? Integer.valueOf(strTestData) : 1;
				return selectByIndex(aKeyWordConfigBean, strPropertyValue, iSelectIndexEnv, false);
			/* Select Dropdown options if exists */
			case EXIST_SELECT:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error While Selecting " + strPropertyValue;
				selectByText(aKeyWordConfigBean, strPropertyValue, strTestData, true);
				return AppConstants.TEST_RESULT_PASS;
			/* Select Dropdown options if exists using JSON */
			case EXIST_SELECT_ENV:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error While Selecting " + strPropertyValue;
				strTestData = getAppEnvValue(strTestData);
				selectByText(aKeyWordConfigBean, strPropertyValue, strTestData, true);
				return AppConstants.TEST_RESULT_PASS;
			/* Select Life assued Dropdown options in product based on given option text */
			case SELECT_LIFEASSURED:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error While Selecting " + strPropertyValue;
				int iIndex = -1;
				if (StringUtils.equalsIgnoreCase(strTestData, "life1")) {
					iIndex = 1;
				}
				if (StringUtils.equalsIgnoreCase(strTestData, "life2")) {
					iIndex = 2;
				}
				if (StringUtils.equalsIgnoreCase(strTestData, "joint")) {
					iIndex = 3;
				}
				return selectByIndex(aKeyWordConfigBean, strPropertyValue, iIndex, false);
			/* performs scroll action up-down */
			case SCROLL:
				strPropertyValue = getObJectProperty(strObJPropName);
				return scrollToElement(aKeyWordConfigBean, strPropertyValue);
			/* performs scroll action until given web element is displayed */
			case SCROLL_TO:
				return scrollTo(aKeyWordConfigBean, strTestData);
			/* performs scrolls action to move to top page */
			case SCROLL_TOP:
				return scrollToTop(aKeyWordConfigBean);
			/* Captures the tables headers -used for reinsurance */
			case GET_TABLEHEADERS:
				strPropertyValue = getObJectProperty(strObJPropName);
				LinkedList<String> lstHeaderData = getTableHeaders(aKeyWordConfigBean, strPropertyValue, strTestData);
				if (CollectionUtils.isEmpty(lstHeaderData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				aPPContext.addOtherSummaryReport(testScenarioName, getBrowsersConfigBean(), strTestData,
						SummaryReportConstants.TABLE_HEADER, lstHeaderData);
				return AppConstants.TEST_RESULT_PASS;
			/* Captures the tables data -used for reinsurance */
			case GET_TABLEDATA:
				strPropertyValue = getObJectProperty(strObJPropName);
				LinkedHashMap<WebElement, String> mpTblData = getTableData(aKeyWordConfigBean, testScenarioName,
						strPropertyValue, strTestData);
				if (mpTblData == null || mpTblData.isEmpty()) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				LinkedList<String> lstTableData = new LinkedList<>();
				mpTblData.entrySet().stream().forEach(mpTblDataEntry -> {
					lstTableData.add(mpTblDataEntry.getValue());
				});
				if (CollectionUtils.isEmpty(lstTableData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				aPPContext.addOtherSummaryReport(testScenarioName, getBrowsersConfigBean(), strTestData,
						SummaryReportConstants.TABLE_DATA, lstTableData);
				return AppConstants.TEST_RESULT_PASS;
			/* clicks on link present in tables */
			case TABLE_LINKCLICK:
			case TABLE_BUTTONCLICK:
				LOGGER.info("Clicking Table link");
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error Table value " + strPropertyValue;
				WebElement aTblLinkColumn = getTableByColumn(aKeyWordConfigBean, testScenarioName, strPropertyValue,
						strTestData, false);
				if (aTblLinkColumn == null) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				aTblLinkColumn.click();
				return AppConstants.TEST_RESULT_PASS;
			/* verifies the required column in a tables */
			case VERIFY_TABLECOLUMN:
				strPropertyValue = getObJectProperty(strObJPropName);
				WebElement aTblCaptureColumn = getTableByColumn(aKeyWordConfigBean, testScenarioName, strPropertyValue,
						strTestData, false);
				if (aTblCaptureColumn == null) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				return AppConstants.TEST_RESULT_PASS;
			/* verifies the data present in a tables */
			case VERIFY_TABLE:
				strPropertyValue = getObJectProperty(strObJPropName);
				return verifyTable(aKeyWordConfigBean, testScenarioName, strPropertyValue, strReportKeyWord,
						strTestData);
			/* verifies the table data present in the form of key :value structure */
			case VERIFY_TABLE_BYCOLUMNKEY:
				strPropertyValue = getObJectProperty(strObJPropName);
				String[] strColumnTestData = StringUtils.split(strTestData, AppConstants.SEPARATOR_COLON);
				if (strColumnTestData == null || strColumnTestData.length != 2) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				String strExpectedColumnKeyVal = strColumnTestData[1];
				return verifyTable(aKeyWordConfigBean, testScenarioName, strPropertyValue, strColumnTestData[0],
						strExpectedColumnKeyVal);
			/* verifies the table data present in the form of key ;value structure */
			case VERIFY_TABLE_BYCOLUMNKEY_SEMICOLON:
				strPropertyValue = getObJectProperty(strObJPropName);
				String[] strColumnTestDataSemi = StringUtils.split(strTestData, AppConstants.SEPARATOR_SEMICOLON);
				if (strColumnTestDataSemi == null || strColumnTestDataSemi.length != 2) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				String strExpectedColumnKeyValSemi = strColumnTestDataSemi[1];
				return verifyTable(aKeyWordConfigBean, testScenarioName, strPropertyValue, strColumnTestDataSemi[0],
						strExpectedColumnKeyValSemi);
			/* implicit wait-waits until 15sec */
			case LONGWAIT:
				long lLongWait = getDriverExternalWaitTime() * 3;
				return waitByTime(lLongWait);
			/* implicit wait-waits until 5sec */
			case SHORTWAIT:
				long lShortWait = getDriverExternalWaitTime();
				return waitByTime(lShortWait);
			/* implicit wait-waits until 10sec */
			case MEDIUM_WAIT:
				long lMediumWait = getDriverExternalWaitTime() * 2;
				return waitByTime(lMediumWait);
			/* implicit wait-waits until given time in testdata */
			case DYNAMICWAIT:
				String strWaitTime = StringUtils.trim(strTestData);
				long lWait = StringUtils.isEmpty(strWaitTime) || !StringUtils.isNumeric(strWaitTime)
						? getDriverSleepTime()
						: Long.valueOf(strWaitTime);
				return waitByTime(lWait);
			/* Explicit wait-waits until given web element is found */
			case WAITUNTIL_PAGE_LOAD:
			case SYNC_WAIT:
				strErrorMsg = "Error While Until Page/Sync Wait";
				strPropertyValue = getObJectProperty(strObJPropName);
				return waitUntilPageLoad(aKeyWordConfigBean, strPropertyValue, strTestData);
			/* inputs value if exist */
			case EXISTINPUT:
				strPropertyValue = getObJectProperty(strObJPropName);
				inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
				return AppConstants.TEST_RESULT_PASS;
			/* inputs value if exist using JSON */
			case EXISTINPUT_ENV:
				strPropertyValue = getObJectProperty(strObJPropName);
				strTestData = getAppEnvValue(strTestData);
				inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
				return AppConstants.TEST_RESULT_PASS;
			case FORMAT_EXISTINPUT:
				strPropertyValue = getUnWrapedJsonOrProperty(testScenarioName, strObJPropName, strTestData);
				strTestData = getUnWrapedJsonFormatTestData(strTestData);
				inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
				return AppConstants.TEST_RESULT_PASS;
			/* click on webelement if exist */
			case EXISTCLICK:
				strPropertyValue = getObJectProperty(strObJPropName);
				clickWebElement(aKeyWordConfigBean, strPropertyValue, false);
				return AppConstants.TEST_RESULT_PASS;
			/* verifies toggle feature of a check box */
			case TOGGLE_CHECKBOX:
				strPropertyValue = getObJectProperty(strObJPropName);
				WebElement chkFBPersist = getWebElement(aKeyWordConfigBean, strPropertyValue);
				Boolean expected = true;
				Boolean actual;
				for (int i = 0; i < 2; i++) {
					chkFBPersist.click();
					actual = chkFBPersist.isSelected();
					Assert.assertEquals(actual, expected);
				}
				return AppConstants.TEST_RESULT_PASS;
			case FORMAT_EXISTCLICK:
				strPropertyValue = getUnWrapedJsonOrProperty(testScenarioName, strObJPropName, strTestData);
				clickWebElement(aKeyWordConfigBean, strPropertyValue, false);
				return AppConstants.TEST_RESULT_PASS;
			/* clicks on particular check box */
			case CHECKBOX:
				/* clicks on radiobutton if Object property is given as test data */
			case RADIO_BUTTON:
				strPropertyValue = getObJectProperty(strTestData);
				return clickWebElement(aKeyWordConfigBean, strPropertyValue, true);
			/* selects the radiobutton if Object property is given as test data */
			case RADIO_BUTTON_SELECT:
				strPropertyValue = String.format(ORConstants.LBL_FORMAT_TEXT_XPATH, strTestData);
				return clickWebElement(aKeyWordConfigBean, strPropertyValue, true);
			/* clicks on radiobutton based on header text */
			case DUAL_RADIO_BUTTON:
				strPropertyValue = String.format(ORConstants.DUAL_LBL_FORMAT_TEXT_XPATH, strReportKeyWord, strTestData);
				return clickWebElement(aKeyWordConfigBean, strPropertyValue, true);
			/* clicks on radiobutton based on header text used in webline */
			case DUAL_RADIO_BUTTON_PORTAL:
				strPropertyValue = String.format(ORConstants.DUAL_LBL_FORMAT_TEXT_XPATH_PORTAL, strReportKeyWord,
						strTestData);
				return clickWebElement(aKeyWordConfigBean, strPropertyValue, true);
			/* clicks on radiobutton based on header text used in Ipipeline */
			case DUAL_RADIO_BUTTON_IPIPELINE:
				strPropertyValue = String.format(ORConstants.DUAL_LBL_FORMAT_TEXT_XPATH_IPIPELINE, strReportKeyWord,
						strTestData);
				return clickWebElement(aKeyWordConfigBean, strPropertyValue, true);

			case UPDATE_EXCEL:
				strErrorMsg = "Error While Updating Questionare Country";
				strPropertyValue = getObJectProperty(strObJPropName);
				return updateExcle(testScenarioName, strReportKeyWord, aKeyWordConfigBean, strPropertyValue,
						strTestData);
			case UPLOAD_FILE:
				File aDWFile = getDlownloadFile(testScenarioName, strReportKeyWord, strTestData); // need to check
				strPropertyValue = getObJectProperty(strObJPropName);
				return uploadFile(aKeyWordConfigBean, strPropertyValue, aDWFile);
			case UPLOAD_FILE_LOCAL:
				strErrorMsg = "Error While Uploding Local File";
				String strUploadLocalFile = getObJectProperty(strTestData);
				strPropertyValue = getObJectProperty(strObJPropName);
				File aUpLocalFile = AppUtils.getFileFromPath(strUploadLocalFile);
				if (aUpLocalFile == null || !aUpLocalFile.getAbsoluteFile().exists()) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				return uploadFile(aKeyWordConfigBean, strPropertyValue, aUpLocalFile);
			case QUESTIONARE_SNOW_STATUS_UPDATE:
				strErrorMsg = "Error While Updating Questionare Snow Status";
				File eFile = getDlownloadFile(testScenarioName, strReportKeyWord, AppConstants.EXCEL_REPORT_EXTENSION); // need
				try (XSSFWorkbook aWorkbook = new XSSFWorkbook(eFile)) {
					FormulaEvaluator evaluator = aWorkbook.getCreationHelper().createFormulaEvaluator();
					String strSheetName = strObJPropName;
					Sheet aControllerSheet = aWorkbook.getSheet(strSheetName);
					if (aControllerSheet == null) {
						throw new IOException(
								MessageFormat.format(ErrorMsgConstants.SHEET_NOT_FOUND, strSheetName, eFile.getPath()));
					}
					String[] strValues = strTestData.split("-");
					String strCountry = strValues[1];
					String strServer = strValues[0];
					for (int iRow = 4; iRow <= aControllerSheet.getLastRowNum()
							- aControllerSheet.getFirstRowNum(); iRow++) {
						Row row = ExcelUtils.getRow(aControllerSheet, iRow);
						if (row == null) {
							throw new Exception(
									MessageFormat.format(ErrorMsgConstants.ERR_ROW, iRow + 1, strSheetName));
						}
						String strDataServerName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName,
								row, 0);
						if (StringUtils.equalsIgnoreCase(strDataServerName, strServer)) {
							ExcelUtils.setCellValue(aControllerSheet, row, 5,
									ExcelUtils.getReportDataCellStyle(aWorkbook), strCountry);
							break;
						}
					}
					File aModifiedFile = Paths.get(eFile.getParent(), "Modified", eFile.getName()).toFile();
					if (!aModifiedFile.getParentFile().exists()) {
						aModifiedFile.getParentFile().mkdirs();
					}
					ExcelUtils.writeWrokBook(aWorkbook, aModifiedFile);
				}
				return AppConstants.TEST_RESULT_PASS;

			case QUESTIONARE_COUNTRY_DELETE:
				strErrorMsg = "Error While Updating Questionare Snow Status";
				String strFile = PropertyHandler.getExternalString(AppConstants.DOWNLOAD_FILE_LOCATION_KEY,
						AppConstants.APP_PROPERTIES_NAME);
				File aControllerFile = AppUtils.getFileFromPath(strFile);
				if (!aControllerFile.exists()) {
					throw new IOException(
							MessageFormat.format(ErrorMsgConstants.FILENTFOUND, aControllerFile.getName()));
				}
				List<File> lstFiles = Arrays.asList(aControllerFile.listFiles()).stream()
						.sorted(Comparator.comparing(File::lastModified).reversed()).collect(Collectors.toList());
				lstFiles.stream().forEach(aDownloadFile -> aDownloadFile.delete());
				return AppConstants.TEST_RESULT_PASS;
			/* Verify particular text given in testdata-case insensitive */
			case VERIFY_TEXT:
				strPropertyValue = getObJectProperty(strObJPropName);
				return verifyText(aKeyWordConfigBean, testScenarioName, strPropertyValue, strTestData, false);
			/* Verify particular text given in testdata-case sensitive */
			case VERIFY_EXACT_TEXT:
				strPropertyValue = getObJectProperty(strObJPropName);
				return verifyText(aKeyWordConfigBean, testScenarioName, strPropertyValue, strTestData, null);
			case VERIFY_UWTEXT:
				strPropertyValue = String.format(ORConstants.UW_QUESTION_TEXT_VALIDATION, strReportKeyWord,
						strTestData);
				return verifyText(aKeyWordConfigBean, testScenarioName, strPropertyValue, strTestData, false);
			/* Verify particular text in dropdown */
			case VERIFY_DROPDOWN_TEXT:
				strPropertyValue = getObJectProperty(strObJPropName);
				return verifyDropDownText(aKeyWordConfigBean, testScenarioName, strPropertyValue, strTestData, true);
			case VERIFY_ROBOT_POPUP:
				strPropertyValue = getObJectProperty(strObJPropName);
				String strAnchorTagText = getAnchorTagOnClickText(aKeyWordConfigBean, strPropertyValue);
				strAnchorTagText = StringUtils.trim(strAnchorTagText);
				if (StringUtils.isEmpty(strAnchorTagText)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				return verifyText(null, aKeyWordConfigBean, testScenarioName, strAnchorTagText, strTestData, false);
			/* Verify particular background Color of a webelement */
			case VERIFY_BACKGROUND_COLOR:
				strPropertyValue = getObJectProperty(strObJPropName);
				return verifyBackgroundColor(aKeyWordConfigBean, testScenarioName, strPropertyValue, strTestData,
						false);
			/* Verify particular text Color */
			case VERIFY_TEXT_COLOR:
				strPropertyValue = getObJectProperty(strObJPropName);
				return verifyTextColor(aKeyWordConfigBean, testScenarioName, strPropertyValue, strTestData, false);
			/* Verify particular text Size */
			case VERIFY_TEXT_SIZE:
				strPropertyValue = getObJectProperty(strObJPropName);
				return verifyTextSize(aKeyWordConfigBean, testScenarioName, strPropertyValue, strTestData, false);
			/* Verify particular alert is displayed or not and fetch text from it */
			case VERIFY_ALERT:
				Alert aAlert = aDriver.switchTo().alert();
				if (aAlert == null) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = aAlert.getText();
				strTestData = getVerificationProperty(strObJPropName);
				return verifyText(null, aKeyWordConfigBean, testScenarioName, strPropertyValue, strTestData, true);
			/* Verify particular alert is displayed or not */
			case VERIFY_CAPTURED_WEB_ELEMENT_TEXT:
				strPropertyValue = getObJectProperty(strObJPropName);
				strTestData = getRunTimeDataValue(testScenarioName, strReportKeyWord);
				return verifyText(aKeyWordConfigBean, testScenarioName, strPropertyValue, strTestData, true);
			/* Verify captured life assured name in backoffice */
			case VERIFY_CAPTURED_BO_LIFE_ASSURED:
				strPropertyValue = getObJectProperty(strObJPropName);
				strTestData = getRunTimeDataValue(testScenarioName,
						RunTimeDataConstants.BACK_OFFICE_CUSTOMER_LIFE_ASSURED);
				return verifyText(aKeyWordConfigBean, testScenarioName, strPropertyValue, strTestData, true);
			/* Verify captured DOB name in backoffice */
			case VERIFY_CAPTURED_BO_DOB:
				strPropertyValue = getObJectProperty(strObJPropName);
				strTestData = getRunTimeDataValue(testScenarioName, RunTimeDataConstants.BACK_OFFICE_CUSTOMER_DOB);
				return verifyText(aKeyWordConfigBean, testScenarioName, strPropertyValue, strTestData, true);
			/* Verify captured BO application num in backoffice */
			case VERIFY_CAPTURED_BO_APPLICATION_NUMBER:
				strPropertyValue = getObJectProperty(strObJPropName);
				strTestData = getRunTimeDataValue(testScenarioName,
						RunTimeDataConstants.BACK_OFFICE_APPLICATION_NUMBER);
				return verifyText(aKeyWordConfigBean, testScenarioName, strPropertyValue, strTestData, true);
			/* Verify captured BO gender in backoffice */
			case VERIFY_CAPTURED_BO_GENDER:
				strPropertyValue = getObJectProperty(strObJPropName);
				strTestData = getRunTimeDataValue(testScenarioName, RunTimeDataConstants.BACK_OFFICE_GENDER);
				return verifyText(aKeyWordConfigBean, testScenarioName, strPropertyValue, strTestData, true);
			/* Verify captured BO Smokerstatus in backoffice */
			case VERIFY_CAPTURED_BO_SMOKER_STATUS:
				strPropertyValue = getObJectProperty(strObJPropName);
				strTestData = getRunTimeDataValue(testScenarioName, RunTimeDataConstants.BACK_OFFICE_SMOKER_STATUS);
				return verifyText(aKeyWordConfigBean, testScenarioName, strPropertyValue, strTestData, true);
			/* Verify captured BO UW no in backoffice */
			case VERIFY_CAPTURED_BO_UW_CASENO:
				strPropertyValue = getObJectProperty(strObJPropName);
				strTestData = getRunTimeDataValue(testScenarioName, RunTimeDataConstants.BACK_OFFICE_UW_CASENO);
				return verifyText(aKeyWordConfigBean, testScenarioName, strPropertyValue, strTestData, true);
			/* Verify Pageclick */
			case VERIFY_PAGECLICK:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while getting the list of column options " + strObJPropName;
				LOGGER.info("Verifying the list of column options " + strObJPropName);
				int iPages = StringUtils.isNumeric(strTestData) ? Integer.parseInt(strTestData) : 0;
				if (iPages <= 0) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				delayInSeconds(2);
				for (int i = 0; i < iPages; i++) {
					String strPage = String.format(strPropertyValue, (i + 1));
					String strPageClickReSult = clickWebElement(aKeyWordConfigBean, strPage, true);
					if (isTestSetpFailed(strPageClickReSult)) {
						return AppConstants.TEST_RESULT_FAIL;
					}
					delayInSeconds(2);
				}
				return AppConstants.TEST_RESULT_PASS;

			case ENTER_SELECTED_NUMBEROFRECORDS:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while entering the number of records" + strObJPropName;
				LOGGER.info("Verifying the number of records " + strObJPropName);
				if (StringUtils.indexOf(strTestData, AppConstants.SEPARATOR_COMMA) <= 0) {
					LOGGER.error(strErrorMsg);
					return AppConstants.TEST_RESULT_FAIL;
				}

				String strSelectedNumberOfRecs[] = StringUtils.split(strTestData, AppConstants.SEPARATOR_COMMA);
				if (strSelectedNumberOfRecs == null || strSelectedNumberOfRecs.length < 2
						|| strSelectedNumberOfRecs.length > 2) {
					LOGGER.error(strErrorMsg);
					return AppConstants.TEST_RESULT_FAIL;
				}
				int iStartPage = StringUtils.isNumeric(strSelectedNumberOfRecs[0])
						? Integer.parseInt(strSelectedNumberOfRecs[0])
						: 0;
				int iENDPage = StringUtils.isNumeric(strSelectedNumberOfRecs[1])
						? Integer.parseInt(strSelectedNumberOfRecs[1])
						: 0;
				if (iENDPage < iStartPage) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				delayInSeconds(2);
				for (int i = iStartPage; i < iENDPage; i++) {
					String strPage = String.format(strPropertyValue, (i++));
					String strPageRecEnterReSult = inputText(aKeyWordConfigBean, strPage, strSelectedNumberOfRecs[1],
							true);
					if (isTestSetpFailed(strPageRecEnterReSult)) {
						return AppConstants.TEST_RESULT_FAIL;
					}
					delayInSeconds(2);
				}
				return AppConstants.TEST_RESULT_PASS;
			/* Verify webelement is enabled or not */
			case VERIFY_IS_ENABLED:
			case ISENABLED:
				strPropertyValue = getObJectProperty(strObJPropName);
				WebElement aVerifyWebElement = getWebElement(aKeyWordConfigBean, strPropertyValue);
				return aVerifyWebElement == null || !aVerifyWebElement.isEnabled() ? AppConstants.TEST_RESULT_FAIL
						: AppConstants.TEST_RESULT_PASS;
			/* Verify whether particular field is empty or not */
			case VERIFY_TEXTFIELD_EMPTY:
				strPropertyValue = getObJectProperty(strObJPropName);
				String textInsideInputBox = getWebElementText(aKeyWordConfigBean, strPropertyValue, false);
				// Check whether input field is blank
				if (StringUtils.isEmpty(textInsideInputBox)) {
					return AppConstants.TEST_RESULT_PASS;
				}
				return AppConstants.TEST_RESULT_FAIL;
			/* Verify webelement is Disabled or not */
			case VERIFY_IS_DISABLED:
				strPropertyValue = getObJectProperty(strObJPropName);
				WebElement aDisableElement = getWebElement(aKeyWordConfigBean, strPropertyValue);
				return  isWebElementDisabled(aKeyWordConfigBean, strPropertyValue, aDisableElement) ? AppConstants.TEST_RESULT_PASS
						: AppConstants.TEST_RESULT_FAIL;
			/* Verify webelement is displayed or not */
			case ISDISPLAYED:
				strPropertyValue = getObJectProperty(strObJPropName);
				WebElement aDisplayedElement = getWebElement(aKeyWordConfigBean, strPropertyValue);
				return aDisplayedElement != null && aDisplayedElement.isDisplayed() ? AppConstants.TEST_RESULT_PASS
						: AppConstants.TEST_RESULT_FAIL;
			/* Verify webelement is not displayed */
			case ISNOTDISPLAYED:
				strPropertyValue = getObJectProperty(strObJPropName);
				WebElement aElementNotDisplayed = getWebElement(aKeyWordConfigBean, strPropertyValue);
				return aElementNotDisplayed == null ? AppConstants.TEST_RESULT_PASS : AppConstants.TEST_RESULT_FAIL;
			/*
			 * Verify webelement is not displayed in application but displayed in HTML(DOM)
			 */
			case WEBELEMENTISNOTDISPLAYED:
				strPropertyValue = getObJectProperty(strObJPropName);
				WebElement aWebElementNotDisplayed = getWebElement(aKeyWordConfigBean, strPropertyValue);
				return aWebElementNotDisplayed != null && aWebElementNotDisplayed.isDisplayed() == false
						? AppConstants.TEST_RESULT_PASS
						: AppConstants.TEST_RESULT_FAIL;
			/* Verify webelement is selected or not */
			case ISSELECTED:
				strPropertyValue = getObJectProperty(strObJPropName);
				WebElement aIsSelected = getWebElement(aKeyWordConfigBean, strPropertyValue);
				return aIsSelected != null && aIsSelected.isSelected() ? AppConstants.TEST_RESULT_PASS
						: AppConstants.TEST_RESULT_FAIL;
			/* Verify webelement is to be deselected */
			case ISDESELECTED:
				strPropertyValue = getObJectProperty(strObJPropName);
				WebElement aIsDeSelected = getWebElement(aKeyWordConfigBean, strPropertyValue);
				return aIsDeSelected != null && !aIsDeSelected.isSelected() ? AppConstants.TEST_RESULT_PASS
						: AppConstants.TEST_RESULT_FAIL;
			case VERIFY_COLUMN_OPTIONS:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while getting the list of column options " + strObJPropName;
				LOGGER.info("Verifying the list of column options " + strObJPropName);
				List<WebElement> cols = getWebDriver().findElements(By.xpath(strPropertyValue));
				cols.stream().forEach(config -> System.out.println(config.getText()));
				LOGGER.info("List of column options are displayed");
				return AppConstants.TEST_RESULT_PASS;
			case VERIFY_TABLE_RECORDS:
				// List<String> NoOfRecords=null;
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while getting the list of table records " + strObJPropName;
				LOGGER.info("Verifying the No of table records " + strObJPropName);
				/*
				 * do {
				 */
				List<WebElement> rows = getWebDriver().findElements(By.xpath(strPropertyValue));
				LOGGER.info("No of records size displayed by default is:" + rows == null ? 0 : rows.size());
				return rows == null ? AppConstants.TEST_RESULT_FAIL : AppConstants.TEST_RESULT_PASS;
			/* Mousehovers on given webelement */
			case MOUSEOVER:
				strPropertyValue = getObJectProperty(strObJPropName);
				LOGGER.info("Mouse Over to " + strObJPropName);
				strErrorMsg = "Error While mouse over for " + strPropertyValue;
				WebElement mousoverElement = getWebElement(aKeyWordConfigBean, strPropertyValue);
				if (mousoverElement == null) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				delayInSeconds(3);
				Actions act = new Actions(aDriver);
				act.moveToElement(mousoverElement).build().perform();
				return AppConstants.TEST_RESULT_PASS;
			/* Mousehovers on given webelement and perform right click */
			case MOUSE_RIGHT_CLICK:
				strPropertyValue = getObJectProperty(strObJPropName);
				LOGGER.info("Mouse Over to " + strObJPropName);
				strErrorMsg = "Error While mouse Right Click for " + strPropertyValue;
				WebElement mousRightClickElement = getWebElement(aKeyWordConfigBean, strPropertyValue);
				if (mousRightClickElement == null) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				delayInSeconds(3);
				Actions actMouseRightClick = new Actions(aDriver);
				actMouseRightClick.contextClick(mousRightClickElement).build().perform();
				return AppConstants.TEST_RESULT_PASS;
			case VERIFY_NUMERIC_SORTING:
				strPropertyValue = getObJectProperty(strObJPropName);
				List<WebElement> lstNumericSortting = getWebElements(aKeyWordConfigBean, strPropertyValue);
				if (CollectionUtils.isEmpty(lstNumericSortting)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				LinkedList<Double> lstNumericSortData = new LinkedList<>();
				lstNumericSortting.stream().forEach(aNumericSortConfig -> {
					String strNumericSortText = getWebElementText(aNumericSortConfig, false);
					String strRegexCuurency = "[^0-9.]";
					strNumericSortText = AppUtils.replaceRegex(strNumericSortText, strRegexCuurency);
					if (!StringUtils.isEmpty(strNumericSortText) && NumberUtils.isParsable(strNumericSortText)) {
						lstNumericSortData.add(Double.valueOf(strNumericSortText));
					}
				});
				boolean isNumericSorted = false;
				if (StringUtils.equalsIgnoreCase(strTestData, AppConstants.SORTING_ASCENDING)) {
					isNumericSorted = Comparators.isInOrder(lstNumericSortData, Comparator.<Double>naturalOrder());
				} else if (StringUtils.equalsIgnoreCase(strTestData, AppConstants.SORTING_DESCENDING)) {
					isNumericSorted = Comparators.isInOrder(lstNumericSortData, Comparator.<Double>reverseOrder());
				} else {
					isNumericSorted = CollectionUtils.isNotEmpty(lstNumericSortData);
				}
				return isNumericSorted ? AppConstants.TEST_RESULT_PASS : AppConstants.TEST_RESULT_FAIL;
			case VERIFY_SORTING:
				strPropertyValue = getObJectProperty(strObJPropName);
				List<WebElement> lstSortting = getWebElements(aKeyWordConfigBean, strPropertyValue);
				if (CollectionUtils.isEmpty(lstSortting)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				LinkedList<String> lstSortData = new LinkedList<>();
				lstSortting.stream().forEach(aSortConfig -> {
					String strSortText = getWebElementText(aSortConfig, false);
					if (!StringUtils.isEmpty(strSortText)) {
						lstSortData.add(strSortText);
					}
				});
				if (CollectionUtils.isEmpty(lstSortData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				boolean isSorted = false;
				if (StringUtils.equalsIgnoreCase(strTestData, AppConstants.SORTING_ASCENDING)) {
					isSorted = Comparators.isInOrder(lstSortData, Comparator.<String>naturalOrder());
				} else if (StringUtils.equalsIgnoreCase(strTestData, AppConstants.SORTING_DESCENDING)) {
					isSorted = Comparators.isInOrder(lstSortData, Comparator.<String>reverseOrder());
				} else {
					isSorted = CollectionUtils.isNotEmpty(lstSortData);
				}
				return isSorted ? AppConstants.TEST_RESULT_PASS : AppConstants.TEST_RESULT_FAIL;
			case VERIFY_SORTING_COLUMN:
				String strPropertyValues[] = StringUtils.split(getObJectProperty(strObJPropName),
						AppConstants.SEPARATOR_CAP);
				if (strPropertyValues == null || strPropertyValues.length != 2) {
					LOGGER.error("Invalid Property");
					return AppConstants.TEST_RESULT_FAIL;
				}
				String strTablePropertyValue = strPropertyValues[1];
				strPropertyValue = strPropertyValues[0];
				LOGGER.info("Verifying sorting " + strObJPropName);
				strErrorMsg = "Error While Verifying sorting  for " + strPropertyValue;
				LinkedHashMap<WebElement, String> aOriginalTableData = getTableData(aKeyWordConfigBean,
						testScenarioName, strTablePropertyValue, strTestData);
				if (aOriginalTableData == null || aOriginalTableData.isEmpty()) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				LinkedList<String> lstOriginalTableData = new LinkedList<>();
				aOriginalTableData.entrySet().stream().forEach(mpOriginalTblDataEntry -> {
					lstOriginalTableData.add(mpOriginalTblDataEntry.getValue());
				});
				String strHeaderClickStatus = clickWebElement(aKeyWordConfigBean, strPropertyValue, false);
				if (isTestSetpFailed(strHeaderClickStatus)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				LinkedHashMap<WebElement, String> aSortedTableData = getTableData(aKeyWordConfigBean, testScenarioName,
						strTablePropertyValue, strTestData);
				if (aSortedTableData == null || aSortedTableData.isEmpty()) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				LinkedList<String> lstSortedTableData = new LinkedList<>();
				aSortedTableData.entrySet().stream().forEach(mpSortedTblDataEntry -> {
					lstSortedTableData.add(mpSortedTblDataEntry.getValue());
				});
				if (CollectionUtils.isEqualCollection(lstOriginalTableData, lstSortedTableData)) {
					LOGGER.error("Elements are not in sorted order");
					return AppConstants.TEST_RESULT_FAIL;
				} else {
					LOGGER.info("Elements are in sorted order");
					return AppConstants.TEST_RESULT_PASS;
				}
			case VERIFY_ASCENDINGORDER_SORTING_DESIREDCOLUMNS:
				strPropertyValue = getObJectProperty(strObJPropName);
				LOGGER.info("Verifying the list of column options " + strObJPropName);
				strErrorMsg = "Error while Verify Descending Order Sorting Column" + strPropertyValue;
				int sortCol = StringUtils.isNumeric(strTestData) ? Integer.parseInt(strTestData) : 0;
				if (sortCol <= 0) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				delayInSeconds(2);
				for (int i = 2; i < sortCol; i++) {
					String strPage = String.format(strPropertyValue, (i + 1));
					// WebElement colName=getWebDriver().findElement(By.xpath(strPage));
					WebElement colName = getWebElement(aKeyWordConfigBean, strPage);
					if (colName == null) {
						return AppConstants.TEST_RESULT_FAIL;
					}
					Actions aAcending = new Actions(aDriver);
					aAcending.moveToElement(colName).perform();
					colName.click();
					delayInSeconds(8);
					List<WebElement> colNameList = getWebDriver().findElements(By.xpath(strPage));
					List<String> originalList1 = colNameList.stream().map(config -> config.getText())
							.collect(Collectors.toList());
					List<String> sortedList1 = originalList1.stream().sorted().collect(Collectors.toList());
					Assert.assertTrue(originalList1.equals(sortedList1));
					LOGGER.info("Elements are in ascending order for coulumn:[" + originalList1 + "]");
					colName.sendKeys(Keys.TAB);
					colName.sendKeys(Keys.TAB);
					colName.sendKeys(Keys.TAB);
					colName.sendKeys(Keys.TAB);
					delayInSeconds(2);

				}
				return AppConstants.TEST_RESULT_PASS;

			case VERIFY_DESCENDING_ORDER_SORTING_DESIREDCOLUMNS:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while Verify Descending Order Sorting Column" + strPropertyValue;
				LOGGER.info("Verifying the list of column options " + strObJPropName);
				int iSortColumn = StringUtils.isNumeric(strTestData) ? Integer.parseInt(strTestData) : 0;
				if (iSortColumn <= 0) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				delayInSeconds(2);
				for (int i = 2; i < iSortColumn; i++) {
					String strPage = String.format(strPropertyValue, (i + 1));
					// WebElement colName=getWebDriver().findElement(By.xpath(strPage));
					WebElement colName = getWebElement(aKeyWordConfigBean, strPage);
					if (colName == null) {
						return AppConstants.TEST_RESULT_FAIL;
					}
					Actions a = new Actions(aDriver);
					a.moveToElement(colName).perform();
					colName.click();
					delayInSeconds(8);
					colName.click();
					List<WebElement> colNameList = getWebDriver().findElements(By.xpath(strPage));
					List<String> originalList1 = colNameList.stream().map(config -> config.getText())
							.collect(Collectors.toList());
					List<String> sortedList1 = originalList1.stream().sorted().collect(Collectors.toList());
					Assert.assertTrue(originalList1.equals(sortedList1));
					LOGGER.info("Elements are in descending order for coulumn:[" + originalList1 + "]");
					colName.sendKeys(Keys.TAB);
					colName.sendKeys(Keys.TAB);
					colName.sendKeys(Keys.TAB);
					colName.sendKeys(Keys.TAB);
					delayInSeconds(2);

				}
				return AppConstants.TEST_RESULT_PASS;
				/*gets the text of given weelement obj property*/
			case FETCH_TEXT:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while Fetching text from Error Notification" + strPropertyValue;
				LOGGER.info("Getting the " + strObJPropName + " from Error Notification: " + strObJPropName);
				String strErrorNotification = getWebElementText(aKeyWordConfigBean, strPropertyValue, false);
				LOGGER.info("Getting the " + strObJPropName + " from Error Notification : " + strErrorNotification);
				return StringUtils.isEmpty(strErrorNotification) ? AppConstants.TEST_RESULT_FAIL
						: AppConstants.TEST_RESULT_PASS;
			case QUESTIONARE_SERVERIPADDRESS_UPDATE:

				File sFile = getDlownloadFile(testScenarioName, strReportKeyWord, AppConstants.EXCEL_REPORT_EXTENSION); // need
				try (XSSFWorkbook aWorkbook = new XSSFWorkbook(sFile)) {
					String strSheetName = strObJPropName;
					FormulaEvaluator evaluator = aWorkbook.getCreationHelper().createFormulaEvaluator();
					Sheet aControllerSheet = aWorkbook.getSheet(strSheetName);
					if (aControllerSheet == null) {
						throw new IOException(
								MessageFormat.format(ErrorMsgConstants.SHEET_NOT_FOUND, strSheetName, sFile.getPath()));
					}
					String[] strValues = strTestData.split("-");
					String strCountry = strValues[1];
					String strServer = strValues[0];
					for (int iRow = 4; iRow <= aControllerSheet.getLastRowNum()
							- aControllerSheet.getFirstRowNum(); iRow++) {
						Row row = ExcelUtils.getRow(aControllerSheet, iRow);
						if (row == null) {
							throw new Exception(
									MessageFormat.format(ErrorMsgConstants.ERR_ROW, iRow + 1, strSheetName));
						}
						String strDataServerName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName,
								row, 0);
						if (StringUtils.equalsIgnoreCase(strDataServerName, strServer)) {
							ExcelUtils.setCellValue(aControllerSheet, row, 14,
									ExcelUtils.getReportDataCellStyle(aWorkbook), strCountry);
							break;
						}
					}
					File aModifiedFile = Paths.get(sFile.getParent(), "Modified", sFile.getName()).toFile();
					if (!aModifiedFile.getParentFile().exists()) {
						aModifiedFile.getParentFile().mkdirs();
					}
					ExcelUtils.writeWrokBook(aWorkbook, aModifiedFile);
				}
				return AppConstants.TEST_RESULT_PASS;

			case QUESTIONARE_PHYSICALVIRTUAL_UPDATE:

				File cFile = getDlownloadFile(testScenarioName, strReportKeyWord, AppConstants.EXCEL_REPORT_EXTENSION); // need

				try (XSSFWorkbook aWorkbook = new XSSFWorkbook(cFile)) {
					String strSheetName = strObJPropName;
					FormulaEvaluator evaluator = aWorkbook.getCreationHelper().createFormulaEvaluator();
					Sheet aControllerSheet = aWorkbook.getSheet(strSheetName);
					if (aControllerSheet == null) {
						throw new IOException(
								MessageFormat.format(ErrorMsgConstants.SHEET_NOT_FOUND, strSheetName, cFile.getPath()));
					}
					String[] strValues = strTestData.split("-");
					String strCountry = strValues[1];
					String strServer = strValues[0];
					for (int iRow = 4; iRow <= aControllerSheet.getLastRowNum()
							- aControllerSheet.getFirstRowNum(); iRow++) {
						Row row = ExcelUtils.getRow(aControllerSheet, iRow);
						if (row == null) {
							throw new Exception(
									MessageFormat.format(ErrorMsgConstants.ERR_ROW, iRow + 1, strSheetName));
						}
						String strDataServerName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName,
								row, 0);
						if (StringUtils.equalsIgnoreCase(strDataServerName, strServer)) {
							ExcelUtils.setCellValue(aControllerSheet, row, 17,
									ExcelUtils.getReportDataCellStyle(aWorkbook), strCountry);
							break;
						}
					}
					File aModifiedFile = Paths.get(cFile.getParent(), "Modified", cFile.getName()).toFile();
					if (!aModifiedFile.getParentFile().exists()) {
						aModifiedFile.getParentFile().mkdirs();
					}
					ExcelUtils.writeWrokBook(aWorkbook, aModifiedFile);
				}
				return AppConstants.TEST_RESULT_PASS;
				/*Reads the given subject of email and performs actions*/
			case READ_EMAIL_OPEN_LINK:
				strPropertyValue = getObJectProperty(strObJPropName);
				return readEmailAndOpenLink(strPropertyValue, aKeyWordConfigBean, strTestData,
						getRunTimeDataValue(testScenarioName, RunTimeDataConstants.FIRST_NAME));
				/*Reads the given subject of email and performs actions-JL*/
			case READ_EMAIL_OPEN_LINK_JOINTLIFE:
				strPropertyValue = getObJectProperty(strObJPropName);
				return readEmailAndOpenLink(strPropertyValue, aKeyWordConfigBean, strTestData,
						getRunTimeDataValue(testScenarioName, RunTimeDataConstants.JOINTLIFE_FIRST_NAME));
			case VERIFY_EMAIL_MESSAGE:
				strPropertyValue = getObJectProperty(strObJPropName);
				return verifyEmailMessage(strPropertyValue, testScenarioName, aKeyWordConfigBean, strTestData,
						getRunTimeDataValue(testScenarioName, RunTimeDataConstants.FIRST_NAME));
			case VERIFY_EMAIL_MESSAGE_JOINTLIFE:
				strPropertyValue = getObJectProperty(strObJPropName);
				return verifyEmailMessage(strPropertyValue, testScenarioName, aKeyWordConfigBean, strTestData,
						getRunTimeDataValue(testScenarioName, RunTimeDataConstants.JOINTLIFE_FIRST_NAME));
			case DOWNLOAD_EMAIL_ATTACHMENT:
				strPropertyValue = getObJectProperty(strObJPropName);
				return downloadEmailAttachment(strPropertyValue, testScenarioName, aKeyWordConfigBean, strReportKeyWord,
						strTestData);
				/*captures quote summary values and writes in runtime sheet  */
			case GETQUOTENSUMMARY:
				return captureQuotationSummary(aKeyWordConfigBean, testScenarioName, strTestData);
				/*Performs accept action in an alert*/
			case ACCEPTALERT:
				LOGGER.info("Accepting Alert");
				getWebDriver().switchTo().alert().accept();
				return AppConstants.TEST_RESULT_PASS;
				/*closes entire browser*/
			case CLOSEBROWSER:
				return AppConstants.TEST_RESULT_PASS;
				/*closes a specific window in which driver is having control*/
			case CLOSEWINDOW:
				return closeWindow();
				/*closes a all window*/
			case CLOSEALLWINDOWS:
				return closeAllWindows();
				/*Switches the driver control to the new window*/
			case SWITCHTO_NEWWINDOW:
				return switchToNewWindow(true);
				/*Switches back to the parent window*/
			case SWITCHTO_PARENTWINDOW:
				return switchToParentWindow();
			case DESC:
				// DESC in string quotes
				return AppConstants.TEST_RESULT_PASS;
				/*Switches back to the parent window*/
			case DEFAULTCONTENT:
				strErrorMsg = "Error While Default content text";
				aDriver.switchTo().defaultContent();
				return AppConstants.TEST_RESULT_PASS;
				/*gets all links in present webpage*/
			case GETALLLINKS:
				strErrorMsg = "Error While Fetching Links";
				List<WebElement> aLinks = aDriver.findElements(By.tagName("a"));
				for (int i = 0; i < aLinks.size(); i++) {
					LOGGER.info(aLinks.get(i).getText());
				}
				return AppConstants.TEST_RESULT_PASS;
				/*gets a specific attribute value of a webelement*/
			case GETATTRIBUTE_VALUE:
				strPropertyValue = getObJectProperty(strObJPropName);
				strTestData = getVerificationProperty(strObJPropName);
				strErrorMsg = "Error While Verify Attribute Value";
				return verifyAttributesByNames(aKeyWordConfigBean, testScenarioName, strPropertyValue, strTestData,
						ORConstants.ATTRIBUTE_NAME_VALUE);
				/*Captures the username text and writes into refsheet*/
			case CAPTURE_USERNAME:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error While Capture Alttribute User Name Value";
				String strUserName = getWebElementText(aKeyWordConfigBean, strPropertyValue, false);
				if (StringUtils.isEmpty(strUserName)) {
					return AppConstants.TEST_RESULT_WARING;
				}
				aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						SummaryReportConstants.USER_NAME_HEADER, strUserName);
				return AppConstants.TEST_RESULT_PASS;
				/*Captures the Lastname text and writes into refsheet*/
			case CAPTURE_LASTNAME:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error While Capture Attribute Last Name Value";
				String strLastName = getWebElementText(aKeyWordConfigBean, strPropertyValue, false);
				if (StringUtils.isEmpty(strLastName)) {
					return AppConstants.TEST_RESULT_WARING;
				}
				aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						SummaryReportConstants.SURNAME_HEADER, strLastName);
				return AppConstants.TEST_RESULT_PASS;
				/*formats the date in required format-portal advisories PDFs*/
			case DATE_FORMAT_CAPTURED_DATA:
				strErrorMsg = "Error while formating date captured";
				strPropertyValue = strObJPropName;
				String strPrevCapturedData = getRunTimeDataValue(testScenarioName, strReportKeyWord);
				if (StringUtils.isEmpty(StringUtils.trim(strPrevCapturedData))
						|| StringUtils.indexOf(strTestData, AppConstants.SEPARATOR_COMMA) <= 0) {
					LOGGER.error(strErrorMsg);
					return AppConstants.TEST_RESULT_FAIL;
				}
				Date dtCaptured = AppUtils.parseDate(strPrevCapturedData, strPropertyValue, LOGGER);
				String[] strTestDataValues = StringUtils.split(strTestData, AppConstants.SEPARATOR_COMMA);
				if (dtCaptured == null || strTestDataValues == null || strTestDataValues.length < 2
						|| strTestDataValues.length > 2) {
					LOGGER.error(strErrorMsg);
					return AppConstants.TEST_RESULT_FAIL;
				}
				String strFrmtDate = strTestDataValues[0];
				String strNewRptKeyWord = strTestDataValues[1];
				String strFormatedDate = AppUtils.getDateAsString(dtCaptured, strFrmtDate);
				if (StringUtils.isEmpty(StringUtils.trim(strFormatedDate))) {
					LOGGER.error(strErrorMsg);
					return AppConstants.TEST_RESULT_FAIL;
				}
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), strNewRptKeyWord, strFormatedDate);
				return AppConstants.TEST_RESULT_PASS;
				/*Captures the text of webelment and writes in runtime sheet*/
			case CAPTURE_WEB_ELEMENT_TEXT:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while capturing web element text";
				return captureWebElemetByText(testScenarioName, aKeyWordConfigBean, strTestData, strPropertyValue,
						strReportKeyWord);
				/*Captures the text of decision-total perimum and writes in runtime sheet*/
			case CAPTURE_DECISION_PREMIUM:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error While Capture Decision Premium Amount";
				String strDecisionPremium = getWebElementText(aKeyWordConfigBean, strPropertyValue, false);
				if (StringUtils.isEmpty(strDecisionPremium)) {
					return AppConstants.TEST_RESULT_WARING;
				}
				strDecisionPremium = StringUtils.substringAfterLast(strDecisionPremium, "£");
				strDecisionPremium = RegExUtils.replacePattern(strDecisionPremium, "[a-z A-Z]", "");
				aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						SummaryReportConstants.DECISION_PREMIUM_AMOUNT, strDecisionPremium);
				return AppConstants.TEST_RESULT_PASS;
			case BACK_OFFICE_CAPTURE_LIFE_ASSURED:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while capturing BACK OFFICE Customer Life Assured";
				return captureWebElemetByText(testScenarioName, aKeyWordConfigBean, strTestData, strPropertyValue,
						RunTimeDataConstants.BACK_OFFICE_CUSTOMER_LIFE_ASSURED);
			case BACK_OFFICE_CAPTURE_CUSTO_REFNO:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while capturing BACK OFFICE Customer Reference no";
				return captureWebElemetByText(testScenarioName, aKeyWordConfigBean, strTestData, strPropertyValue,
						RunTimeDataConstants.BACK_OFFICE_CUSTOMER_REFNO);
			case BACK_OFFICE_CAPTURE_CUSTO_REFNO2:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while capturing BACK OFFICE Customer Reference no";
				return captureWebElemetByText(testScenarioName, aKeyWordConfigBean, strTestData, strPropertyValue,
						RunTimeDataConstants.BACK_OFFICE_CUSTOMER_REFNO2);
			case BACK_OFFICE_CAPTURE_CUSTO_CLAIM_APPNO:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while capturing BACK OFFICE Customer App no";
				return captureWebElemetByText(testScenarioName, aKeyWordConfigBean, strTestData, strPropertyValue,
						RunTimeDataConstants.BACK_OFFICE_CLAIM_APPNO);
			case BACK_OFFICE_CAPTURE_CLAIM_REFTEXT:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while capturing BACK OFFICE Reference text";
				return captureWebElemetByText(testScenarioName, aKeyWordConfigBean, strTestData, strPropertyValue,
						RunTimeDataConstants.BACK_OFFICE_CLAIM_APPNO);
			case BACK_OFFICE_CAPTURE_CLAIM_REFNO:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while capturing BACK OFFICE Reference no";
				return captureWebElemetByText(testScenarioName, aKeyWordConfigBean, strTestData, strPropertyValue,
						RunTimeDataConstants.BACK_OFFICE_CLAIM_REFNO);
			case BACK_OFFICE_CAPTURE_ADVISORYNO:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while capturing BACK OFFICE Customer App no";
				return captureWebElemetByText(testScenarioName, aKeyWordConfigBean, strTestData, strPropertyValue,
						RunTimeDataConstants.BACK_OFFICE_CAPTURE_ADVISORYNO);
			case BACK_OFFICE_CAPTURE_PAY_REFNO:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while capturing BACK OFFICE Reference text";
				return captureWebElemetByText(testScenarioName, aKeyWordConfigBean, strTestData, strPropertyValue,
						RunTimeDataConstants.BACK_OFFICE_CAPTURE_PAY_REFNO);
			case BACK_OFFICE_CAPTURE_CUST_ACCNO:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while capturing BACK OFFICE Reference no";
				return captureWebElemetByText(testScenarioName, aKeyWordConfigBean, strTestData, strPropertyValue,
						RunTimeDataConstants.BACK_OFFICE_CAPTURE_CUST_ACCNO);
			case BACK_OFFICE_CAPTURE_DOB:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while capturing BACK OFFICE Customer DOB";
				return captureWebElemetByText(testScenarioName, aKeyWordConfigBean, strTestData, strPropertyValue,
						RunTimeDataConstants.BACK_OFFICE_CUSTOMER_DOB);

			case BACK_OFFICE_CAPTURE_APPLICATION_NUMBER:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while capturing BACK OFFICE Application Number";
				return captureWebElemetByText(testScenarioName, aKeyWordConfigBean, strTestData, strPropertyValue,
						RunTimeDataConstants.BACK_OFFICE_APPLICATION_NUMBER);
			case BACK_OFFICE_CAPTURE_GENDER:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while capturing BACK OFFICE Gender";
				return captureWebElemetByText(testScenarioName, aKeyWordConfigBean, strTestData, strPropertyValue,
						RunTimeDataConstants.BACK_OFFICE_GENDER);
			case BACK_OFFICE_CAPTURE_SMOKER_STATUS:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while capturing BACK OFFICE Smoker Status";
				return captureWebElemetByText(testScenarioName, aKeyWordConfigBean, strTestData, strPropertyValue,
						RunTimeDataConstants.BACK_OFFICE_SMOKER_STATUS);
			case BACK_OFFICE_CAPTURE_UW_CASENO:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error while capturing BACK OFFICE Smoker Status";
				return captureWebElemetByText(testScenarioName, aKeyWordConfigBean, strTestData, strPropertyValue,
						RunTimeDataConstants.BACK_OFFICE_UW_CASENO);

			case BACK_OFFICE_CAPTURE_UW_CASENO_INPUT:
				strTestData = getRunTimeDataValue(testScenarioName, RunTimeDataConstants.BACK_OFFICE_UW_CASENO);
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);

			case CAPTURE_POLICYNUMS_SDP:
				strPropertyValue = ORConstants.ELMENT_POLICY_NUMBERS_SDP;
				List<WebElement> lstPolicyNumsdp = getWebElements(aKeyWordConfigBean, strPropertyValue);
				if (CollectionUtils.isEmpty(lstPolicyNumsdp)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				for (int i = 0; i < lstPolicyNumsdp.size(); i++) {
					int iPolicyIndex = i + 1;
					WebElement aPolyNoSDP = lstPolicyNumsdp.get(i);
					strPropertyValue = String.format(ORConstants.LBL_POLCY_NUMBER_XPATH, iPolicyIndex);
					String strPolicyNumbers[] = StringUtils.split(StringUtils.trim(aPolyNoSDP.getText()), ":");
					String strPolicyNumber = StringUtils.trim(strPolicyNumbers[strPolicyNumbers.length - 1]);
					capturePolicyNumber(iPolicyIndex, testScenarioName, strPolicyNumber);
				}
				return AppConstants.TEST_RESULT_PASS;
				/*Captures the policy no's from advisor portal*/
			case CAPTURE_POLICY_NUMS:
				strPropertyValue = ORConstants.ELMENT_POLICY_NUMBERS;
				List<WebElement> lstPolicyNums = getWebElements(aKeyWordConfigBean, strPropertyValue);
				if (CollectionUtils.isEmpty(lstPolicyNums)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				for (int i = 0; i < lstPolicyNums.size(); i++) {
					int iPolicyIndex = i + 1;
					strPropertyValue = String.format(ORConstants.LBL_POLCY_NUMBER_XPATH, iPolicyIndex);
					String strPolicyNumber = getWebElementText(aKeyWordConfigBean, strPropertyValue, false);
					capturePolicyNumber(iPolicyIndex, testScenarioName, strPolicyNumber);
				}
				return AppConstants.TEST_RESULT_PASS;
				/*Captures the decision from advisor portal*/
			case CAPTURE_DECISIONS:
				strPropertyValue = ORConstants.FORM_PRODUCT_DECISIONS_XPATH;
				List<WebElement> lstProductDecisions = getWebElements(aKeyWordConfigBean, strPropertyValue);
				if (CollectionUtils.isEmpty(lstProductDecisions)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				for (int i = 0; i < lstProductDecisions.size(); i++) {
					int iProductDescIndex = i + 1;
					strPropertyValue = String.format(ORConstants.DIV_FORM_PRODUCT_DECISIONS_XPATH, iProductDescIndex);
					String strProductDecisionPremium = null;
					String strProductDecision = getWebElementText(aKeyWordConfigBean, strPropertyValue, false);
					if (StringUtils.containsIgnoreCase(strProductDecision, "£")) {
						strProductDecisionPremium = StringUtils.substringAfterLast(strProductDecision, "£");
						strProductDecisionPremium = RegExUtils.replacePattern(strProductDecisionPremium, "[a-z A-Z]",
								"");
						strPropertyValue = String.format(ORConstants.DIV_PRODUCT_DECISIONS_XPATH, iProductDescIndex);
						strProductDecision = getWebElementText(aKeyWordConfigBean, strPropertyValue, false);
					}
					captureProductDecision(iProductDescIndex, testScenarioName, strProductDecision,
							strProductDecisionPremium);
					strPropertyValue = String.format(ORConstants.DIV_QUOTE_SUMMARY_XPATH, iProductDescIndex);
					String strDescisionsPropval = String.format(ORConstants.DIV_PRODUCT_DECISIONS_BLOCK_XPATH,
							iProductDescIndex);
					WebElement aCaptureDecisions = getWebElement(aKeyWordConfigBean, strDescisionsPropval);
					String strDecisionsCCSValue = aCaptureDecisions == null ? ""
							: aCaptureDecisions.getCssValue("display");
					if (StringUtils.equals(ORConstants.DISABLE_CSS_VALUE, strDecisionsCCSValue)) {
						clickWebElement(aKeyWordConfigBean, strPropertyValue, false);
						waitByTime(400);
					}
					String strDecisionsMoreInfoProp = String
							.format(ORConstants.DIV_PRODUCT_DECISIONS_BLOCK_SHOW_MORE_INFO_XPATH, iProductDescIndex);
					List<WebElement> lstDecisionsMoreInfos = getWebElements(aKeyWordConfigBean,
							strDecisionsMoreInfoProp);
					if (CollectionUtils.isNotEmpty(lstDecisionsMoreInfos)) {
						for (WebElement aDecisionsMoreInfo : lstDecisionsMoreInfos) {
							try {
								aDecisionsMoreInfo.click();
							} catch (Exception e) {
							}
						}
					}
					captureProductDecision(aKeyWordConfigBean, iProductDescIndex, testScenarioName, strProductDecision,
							strDecisionsCCSValue);
				}
				return AppConstants.TEST_RESULT_PASS;
				/*Captures the PAP no's  from advisor portal*/
			case CAPTURE_REF_NUMS:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error While Capturing Ref Numbers";
				String strApplicationRef = getWebElementText(aKeyWordConfigBean, strPropertyValue, false);
				if (StringUtils.isEmpty(strApplicationRef)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				if (StringUtils.length(strApplicationRef) >= 23) {// Only to work for :-Application reference:
					strApplicationRef = StringUtils.substring(strApplicationRef, 23, strApplicationRef.length());
				}
				aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						SummaryReportConstants.APPLICATION_REFERENCE_HEADER, strApplicationRef);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
						RunTimeDataConstants.APPLICATION_REFERENCE, strApplicationRef);
				strPropertyValue = ORConstants.DIV_QUOTATION_NUMBER_XPATH;
				String strQuotaionNumber = getWebElementText(aKeyWordConfigBean, strPropertyValue, false);
				if (StringUtils.isEmpty(strQuotaionNumber)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						SummaryReportConstants.QUOTE_REFERENCE_HEADER, strQuotaionNumber);
				aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						SummaryReportConstants.QUATATION_APPLICATION_REF_HEADER,
						AppUtils.formatMessage("{0} || {1}", strQuotaionNumber, strApplicationRef));
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
						RunTimeDataConstants.QUOTE_REFERENCE, strQuotaionNumber);
				return AppConstants.TEST_RESULT_PASS;
			case CAPTURE_PRE_APP_REF_NUMS:// used Only for Pre APP
				strPropertyValue = getObJectProperty(strObJPropName);
				String strPreAppApplicationRef = getWebElementText(aKeyWordConfigBean, strPropertyValue, false);
				if (StringUtils.isEmpty(strPreAppApplicationRef)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				if (StringUtils.length(strPreAppApplicationRef) >= 10) {
					strPreAppApplicationRef = StringUtils.substringAfter(strPreAppApplicationRef,
							AppConstants.SEPARATOR_COLON);
				}
				strPreAppApplicationRef = StringUtils.trim(strPreAppApplicationRef);
				aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						SummaryReportConstants.APPLICATION_REFERENCE_HEADER, strPreAppApplicationRef);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
						RunTimeDataConstants.APPLICATION_REFERENCE, strPreAppApplicationRef);
				return AppConstants.TEST_RESULT_PASS;
				/*expand all dropdowns in quote summary page*/
			case EXPAND_QUOTE_SUMMARY:
				strPropertyValue = ORConstants.FORM_PRODUCT_DECISIONS_XPATH;
				List<WebElement> lstQuoteSummary = getWebElements(aKeyWordConfigBean, strPropertyValue);
				if (CollectionUtils.isEmpty(lstQuoteSummary)) {
					return AppConstants.TEST_RESULT_PASS;
				}
				for (int i = 1; i <= lstQuoteSummary.size(); i++) {
					strPropertyValue = String.format(ORConstants.DIV_FORM_PRODUCT_DECISIONS_XPATH, i);
					WebElement aProductDecisionElement = getWebElement(aKeyWordConfigBean, strPropertyValue);
					if (aProductDecisionElement == null) {
						return AppConstants.TEST_RESULT_FAIL;
					}
					aProductDecisionElement.click();
					waitByTime(500);
				}
				return AppConstants.TEST_RESULT_PASS;
				/*selects all the available -confirmation and identity payment page checkboxes*/
			case SELECT_IDNTYPG_CHKBOXS:
				strPropertyValue = ORConstants.MUTIPLE_CHECKBOXS_XPATH;
				List<WebElement> lstSelectINDType = getWebElements(aKeyWordConfigBean, strPropertyValue);
				if (CollectionUtils.isEmpty(lstSelectINDType)) {
					return AppConstants.TEST_RESULT_PASS;
				}
				int j = 4;
				for (int i = 1; i <= lstSelectINDType.size(); i++) {
					strPropertyValue = String.format(ORConstants.MUTIPLE_CHECKBOX_XPATH, j);
					String strClickINDTypeResult = clickWebElement(aKeyWordConfigBean, strPropertyValue, false);
					if (isTestSetpFailed(strClickINDTypeResult)) {
						return AppConstants.TEST_RESULT_FAIL;
					}
					j = j + 3;
				}
				return AppConstants.TEST_RESULT_PASS;
				/*selects the occupation of Lifeassured after entering testdata*/
			case CLIENT_OCCUPATION:
				strPropertyValue = getObJectProperty(strObJPropName);
				String strOCCSendKeys = sendKeys(aKeyWordConfigBean, strPropertyValue, strTestData);
				if (isTestSetpFailed(strOCCSendKeys)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				String strOCCResult = clickClientOccupation(aKeyWordConfigBean, strTestData);
				if (isTestSetpFailed(strOCCResult)) {
					sendKeys(aKeyWordConfigBean, strPropertyValue, Keys.BACK_SPACE);
					sendKeys(aKeyWordConfigBean, strPropertyValue, Keys.BACK_SPACE);
					strOCCResult = clickClientOccupation(aKeyWordConfigBean, strTestData);
				}
				return strOCCResult;
				/*selects the occupation of Lifeassured2 after entering testdata*/
			case CLIENT_SPL_OCCUPATION:
				String[] strORProperties = StringUtils.split(getObJectProperty(strObJPropName),
						AppConstants.SEPARATOR_CAP);
				String strClientSPLOCCOR, strClientSPLOCCORClick;
				if (strORProperties == null || strORProperties.length != 2) {
					strClientSPLOCCOR = strORProperties == null ? getObJectProperty(strObJPropName)
							: strORProperties[0];
					strPropertyValue = strClientSPLOCCOR;
					strClientSPLOCCORClick = String.format(ORConstants.OCCUPATION_SELECT_XPATH, strPropertyValue,
							strTestData);
				} else {
					strClientSPLOCCOR = strORProperties[0];
					strPropertyValue = strClientSPLOCCOR;
					strClientSPLOCCORClick = String.format(strORProperties[1], strTestData);
				}
				String strOCCSPLSendKeys = sendKeys(aKeyWordConfigBean, strPropertyValue, strTestData);
				if (isTestSetpFailed(strOCCSPLSendKeys)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				String strOCCSPLResult = clickWebElement(aKeyWordConfigBean, strClientSPLOCCORClick, false);
				if (isTestSetpFailed(strOCCSPLResult)) {
					sendKeys(aKeyWordConfigBean, strPropertyValue, Keys.BACK_SPACE);
					sendKeys(aKeyWordConfigBean, strPropertyValue, Keys.BACK_SPACE);
					strOCCSPLResult = clickWebElement(aKeyWordConfigBean, strClientSPLOCCORClick, false);
				}
				return strOCCSPLResult;
				/*selects the occupation of Lifeassured after entering testdata*/
			case CLIENT_1_OCCUPATION:
				strPropertyValue = String.format(ORConstants.OCCUPATION_XPATH, 0);
				String strOCC1SendKeys = sendKeys(aKeyWordConfigBean, strPropertyValue, strTestData);
				if (isTestSetpFailed(strOCC1SendKeys)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				return clickClientOccupation(aKeyWordConfigBean, strTestData);
				/*selects the occupation of Lifeassured2 after entering testdata*/
			case CLIENT_1_SPL_OCCUPATION:
				strPropertyValue = String.format(ORConstants.OCCUPATION_XPATH, 0);
				String strOCC1SpecialSendKeys = sendKeys(aKeyWordConfigBean, strPropertyValue, strTestData);
				if (isTestSetpFailed(strOCC1SpecialSendKeys)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				return clickWebElement(aKeyWordConfigBean, ORConstants.OCCUPATION_CLLIENT1_SPECIAL_XPATH, false);
			case CLIENT_2_OCCUPATION:
				strPropertyValue = String.format(ORConstants.OCCUPATION_XPATH, 1);
				String strOCC2SendKeys = sendKeys(aKeyWordConfigBean, strPropertyValue, strTestData);
				if (isTestSetpFailed(strOCC2SendKeys)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				return clickClientOccupation(aKeyWordConfigBean, strTestData);
			case CLIENT_1_PMOCCUPATION:
				strPropertyValue = ORConstants.OCCUPATION_CLLIENT1_PM_XPATH;
				String strOCC1PMSendKeys = sendKeys(aKeyWordConfigBean, strPropertyValue, strTestData);
				if (isTestSetpFailed(strOCC1PMSendKeys)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				sendKeys(aKeyWordConfigBean, strPropertyValue, Keys.BACK_SPACE);
				sendKeys(aKeyWordConfigBean, strPropertyValue, Keys.BACK_SPACE);
				return clickClientOccupation(aKeyWordConfigBean, strTestData);
			case CLIENT_2_PMOCCUPATION:
				strPropertyValue = String.format(ORConstants.OCCUPATION_XPATH, 1);
				String strOCC2PMSendKeys = sendKeys(aKeyWordConfigBean, strPropertyValue, strTestData);
				if (isTestSetpFailed(strOCC2PMSendKeys)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				sendKeys(aKeyWordConfigBean, strPropertyValue, Keys.BACK_SPACE);
				sendKeys(aKeyWordConfigBean, strPropertyValue, Keys.BACK_SPACE);
				return clickClientOccupation(aKeyWordConfigBean, strTestData);
				/*gets attribute of particular webelement and verifies*/
			case GETATTRIBUTE_ALT:
				strPropertyValue = getObJectProperty(strObJPropName);
				strTestData = getVerificationProperty(strObJPropName);
				strErrorMsg = "Error While Verify Alltribute ALT";
				return verifyAttributesByNames(aKeyWordConfigBean, testScenarioName, strPropertyValue, strTestData,
						ORConstants.ATTRIBUTE_NAME_ALT);
				/*performs back operation in webpage*/
			case GOBACK:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error While Performing back operation";
				aDriver.navigate().back();
				return AppConstants.TEST_RESULT_PASS;
				/*performs forward operation in webpage*/
			case GOFORWARD:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error While Performing forward operation";
				aDriver.navigate().forward();
				return AppConstants.TEST_RESULT_PASS;
				/*performs key press operation in keyboard*/
			case KEYDOWN:
				delayInSeconds(2);
				return sendKeys(aKeyWordConfigBean, strPropertyValue, Keys.DOWN);
				/*Opens new window*/
			case OPENNEWTAB:
				return sendKeys(KeyEvent.VK_CONTROL, KeyEvent.VK_N);
				/*performs refresh operation*/
			case REFRESH:
				LOGGER.info("Refreshing Page........");
				strErrorMsg = "Error While Refreshing Page";
				aDriver.navigate().refresh();
				return AppConstants.TEST_RESULT_PASS;
			case CONVERT_DATE:
				strPropertyValue = getObJectProperty(strObJPropName);
				Date dtConvDate = AppUtils.parseDate(strTestData, AppConstants.DATE_FORMAT_DDMMYYYY, LOGGER);
				if (dtConvDate == null) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				String strConvDate = AppUtils.getDateAsString(dtConvDate, AppConstants.DATE_FORMAT_YYYYMMDD);
				return inputText(aKeyWordConfigBean, strPropertyValue, strConvDate, false);
				/*input the captutred data from runtime sheet to the fields application*/
			case INPUT_CAPTURED_DATA:
				strTestData = getRunTimeDataValue(testScenarioName, strReportKeyWord);
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
			case INPUT_PREVIOUS_CAPTURED_DATA:
				if (StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_TRUE)
						|| StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_FALSE)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strTestData = getRunTimeDataValue(strTestData, strReportKeyWord);
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
			case BACK_OFFICE_INPUT_REFERENCENO:
				strTestData = getRunTimeDataValue(testScenarioName, RunTimeDataConstants.BACK_OFFICE_CUSTOMER_REFNO);
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
			case BACK_OFFICE_INPUT_REFERENCENO2:
				strTestData = getRunTimeDataValue(testScenarioName, RunTimeDataConstants.BACK_OFFICE_CUSTOMER_REFNO2);
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
			case BACK_OFFICE_INPUT_APPLICATIONNO:
				strTestData = getRunTimeDataValue(testScenarioName, RunTimeDataConstants.BACK_OFFICE_CLAIM_APPNO);
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
			case BACK_OFFICE_INPUT_REFERENCE:
				strTestData = getRunTimeDataValue(testScenarioName, RunTimeDataConstants.BACK_OFFICE_CLAIM_REFNO);
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
			case BACK_OFFICE_INPUT_REFERENCETEXT:
				strTestData = getRunTimeDataValue(testScenarioName,
						RunTimeDataConstants.BACK_OFFICE_CAPTURE_CLAIM_REFTEXT);
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
			case BACK_OFFICE_INPUT_ADDREFERENCENO:
				strTestData = getRunTimeDataValue(testScenarioName,
						RunTimeDataConstants.BACK_OFFICE_CAPTURE_ADVISORYNO);
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
			case BACK_OFFICE_INPUT_PAY_REFERENCENO:
				strTestData = getRunTimeDataValue(testScenarioName, RunTimeDataConstants.BACK_OFFICE_CAPTURE_PAY_REFNO);
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
			case BACK_OFFICE_INPUT_CUST_ACCNO:
				strTestData = getRunTimeDataValue(testScenarioName,
						RunTimeDataConstants.BACK_OFFICE_CAPTURE_CUST_ACCNO);
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
				/*input the captured application no from advisor portal */
			case BKOFF_APPLICATION_INPUT:
				strTestData = getRunTimeDataValue(testScenarioName, RunTimeDataConstants.APPLICATION_REFERENCE);
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
				/*input the captured quoteref no from advisor portal */
			case BKOFF_QUOTEREF_INPUT:
				strTestData = getRunTimeDataValue(testScenarioName, RunTimeDataConstants.QUOTE_REFERENCE);
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
				/*input the captured Policy no from advisor portal */
			case BKOFF_POLICYINPUT:
				strTestData = getRunTimeDataValue(testScenarioName,
						AppUtils.formatMessage(SummaryReportConstants.POLICY_NUMBER_HEADER, 1));
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
				/*input the captured Policy no (product -2)from advisor portal */
			case BKOFF_POLICYINPUT2:
				strTestData = getRunTimeDataValue(testScenarioName,
						AppUtils.formatMessage(SummaryReportConstants.POLICY_NUMBER_HEADER, 2));
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
				/*input the captured Policy no (product -3)from advisor portal */
			case BKOFF_POLICYINPUT3:
				strTestData = getRunTimeDataValue(testScenarioName,
						AppUtils.formatMessage(SummaryReportConstants.POLICY_NUMBER_HEADER, 3));
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
				/*input the captured Policy no (product -4)from advisor portal */
			case BKOFF_POLICYINPUT4:
				strTestData = getRunTimeDataValue(testScenarioName,
						AppUtils.formatMessage(SummaryReportConstants.POLICY_NUMBER_HEADER, 4));
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
				/*input the captured Policy no (product -5)from advisor portal */
			case BKOFF_POLICYINPUT5:
				strTestData = getRunTimeDataValue(testScenarioName,
						AppUtils.formatMessage(SummaryReportConstants.POLICY_NUMBER_HEADER, 5));
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
				/*input the captured Policy no (product -6)from advisor portal */
			case BKOFF_POLICYINPUT6:
				strTestData = getRunTimeDataValue(testScenarioName,
						AppUtils.formatMessage(SummaryReportConstants.POLICY_NUMBER_HEADER, 6));
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);

			case FIRM_CHECK:
				strTestData = "Parent firm is not currently registered";
				strPropertyValue = getObJectProperty(strObJPropName);
				return clickByTextWebElement(aKeyWordConfigBean, strPropertyValue, strTestData, false);
				/*selects a paticular frame*/
			case SELECT_FRAME:
				strPropertyValue = getObJectProperty(strObJPropName);
				strErrorMsg = "Error While Selecting Frame";
				WebElement aFrameElement = getWebElement(aKeyWordConfigBean, strPropertyValue);
				if (aFrameElement == null) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				delayInSeconds(2);
				aDriver.switchTo().frame(aFrameElement);
				return AppConstants.TEST_RESULT_PASS;
				/*Deselects a paticular frame*/
			case DESELECT_FRAME:
				delayInSeconds(5);
				if (getBrowser() == Browsers.WINDOWS_IE) {
					aKeyWordConfigBean.setOriginalKeyWord(KeyWord.DEFAULTCONTENT.getKeyWord());
					return execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
							strObJPropName, aKeyWordConfigBean);
				}
				return AppConstants.TEST_RESULT_PASS;
			case ZOOM_BY_PERCENTAGE:
				strErrorMsg = "Exception occured in Zoom By Percent";
				return executeJavaScript(aKeyWordConfigBean, strPropertyValue,
						String.format(ORConstants.EXEC_JAVA_SCRIPT_ZOOM_BY_PERCENTAGE_CMD, strTestData), false);
				/*Capture entire screenshot of webpage by scrolling*/
			case TAKE_FULLWINDOW_SCREENSHOT:
				strErrorMsg = "Exception occured While Taking full Window ScreenShot";
				return TestStepReport.logInfo(getBrowsersConfigBean(), testScenarioName, stepDescription, strLogMessage,
						true, getWebDriver());
				/*Capture entire screenshot of particular page  and creates separate folder as pergiven  testdate---decision screenshot*/
			case COPY_FULLWINDOW_SCREENSHOT:
				strErrorMsg = "Exception occured While copying full Window ScreenShot";
				if (StringUtils.equalsIgnoreCase(AppConstants.DEFAULT_TRUE, strTestData)) {
					strTestData = "Dynamic-Data";
				}
				strTestData = AppUtils.removeIllegalCharacters(strTestData, true);
				File aCopyScreen = TestStepReport.captureFullScreen(getWebDriver(), getBrowsersConfigBean(),
						testScenarioName, strTestData);
				if (aCopyScreen == null || !aCopyScreen.exists()) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				return AppConstants.TEST_RESULT_PASS;
			case SDP_OCCUPTNINPUT:
				strPropertyValue = getObJectProperty(strObJPropName);
				String strOCCUPInput = inputText(aKeyWordConfigBean, strPropertyValue, strTestData, false);
				if (isTestSetpFailed(strOCCUPInput)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				String strBackSpace = sendKeys(aKeyWordConfigBean, strPropertyValue, Keys.BACK_SPACE);
				if (isTestSetpFailed(strBackSpace)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				return sendKeys(aKeyWordConfigBean, strPropertyValue, Keys.BACK_SPACE);
				/*Verifies barcode in pdf's*/
			case VERIFYPDF_BARCODE:
				File aBarCodePDFFile = getDlownloadFile(testScenarioName, strReportKeyWord,
						AppConstants.PDF_FILE_EXTENTION);
				if (!aBarCodePDFFile.exists()) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				String strPDFFiles[] = StringUtils.split(strTestData, "|");
				if (strPDFFiles == null || strPDFFiles.length < 2) {
					LOGGER.error("Invalid Test Data");
					return AppConstants.TEST_RESULT_FAIL;
				}
				List<BarcodeInfo> lstBarcodeInfo = BarcodeImageDecoderUtil.decodePDF(aBarCodePDFFile);
				if (CollectionUtils.isEmpty(lstBarcodeInfo)) {
					LOGGER.error("Invalid data");
					return AppConstants.TEST_RESULT_FAIL;
				}

				Optional<BarcodeInfo> opBarCodeInfo = lstBarcodeInfo.stream()
						.filter(aBarcodeInfo -> StringUtils.startsWithIgnoreCase(aBarcodeInfo.getText(), strPDFFiles[0])
								&& StringUtils.endsWithIgnoreCase(aBarcodeInfo.getText(), strPDFFiles[1]))
						.findFirst();
				if (opBarCodeInfo.isPresent()) {
					BarcodeInfo aBarcodeInfo = opBarCodeInfo.get();
					LOGGER.info(AppUtils.formatMessage(
							"Validating Barcode Data : Actual - {0} Expected - {1} Both are matched",
							aBarcodeInfo.getText(), strTestData));
				} else {
					LOGGER.warn(AppUtils.formatMessage("Data not matched! Actual - {0} Expected - {1}", "N/A",
							strTestData));
				}

				return opBarCodeInfo.isPresent() ? AppConstants.TEST_RESULT_PASS : AppConstants.TEST_RESULT_FAIL;
				/*Verifies full content of pdfs-case insensitive and downloads the file and moves to corresponding  scenario folder*/
			case VERIFY_PDF_TEXT:
				strPropertyValue = getObJectProperty(strObJPropName);
				return verifyPDFText(strReportKeyWord, aKeyWordConfigBean, testScenarioName, strPropertyValue,
						strTestData, false);
				/*Verifies  a specific content of pdfs with the given coordinates-casesensitive and downloads the file and moves to corresponding  scenario folder*/
			case VERIFY_PDF_TEXTBYAREA:
				strPropertyValue = getObJectProperty(strObJPropName);
				return verifyPDFTextByArea(strReportKeyWord, aKeyWordConfigBean, testScenarioName, strPropertyValue,
						strTestData, false);
				/*downlaods the required pdfs with .extentions(BO view publications*/
			case DOWNLOAD_DDINSTRUCTION_PDF:
				if (StringUtils.equalsIgnoreCase(strTestData, AppConstants.PDF_FILE_EXTENTION)) {
					return AppConstants.TEST_RESULT_PASS;
				}
				String strNewWindow = switchToNewWindow(true);
				if (isTestSetpFailed(strNewWindow)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				long lCurrentTime = System.currentTimeMillis();
				String strDDINSFile = String.format("%s_%s.%s", lCurrentTime,
						AppUtils.removeIllegalCharacters(strReportKeyWord, true), strTestData);
				File aDDInsLocation = Paths.get(AppUtils.getDownloadFolder(getBrowsersConfigBean()), strDDINSFile)
						.toFile();
				if (!aDDInsLocation.getParentFile().exists()) {
					aDDInsLocation.getParentFile().mkdirs();
				}

				String strDDDwFile = downloadFile(aKeyWordConfigBean, aDDInsLocation, strTestData);
				if (isTestSetpFailed(strDDDwFile)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				// Convert to PDF
				String strConvertPDF = convertHtmlToPDF(aKeyWordConfigBean, aDDInsLocation, strTestData);
				if (isTestSetpFailed(strConvertPDF)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				return closeWindow();
			case CAPTURE_PUBLICATIONS_CUSTOMER_DATA:
				String strReportKeyWords[] = StringUtils.split(strReportKeyWord, AppConstants.SEPARATOR_COMMA);
				if (strReportKeyWords == null || strReportKeyWords.length < 2) {
					LOGGER.error("Invalid Test Data");
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				int iPublicationsTableRowNum = getPublicationsTableRowNumber(aKeyWordConfigBean, testScenarioName,
						strPropertyValue, strTestData, strReportKeyWords[0]);
				if (iPublicationsTableRowNum < 0) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = String.format(ORConstants.BACKOFFICE_CUSTOMER_NUMBER_TABLE,
						iPublicationsTableRowNum);
				return captureWebElemetByText(testScenarioName, aKeyWordConfigBean, strTestData, strPropertyValue,
						strReportKeyWords[1]);
				/*identifies the given document name using recipient name in particular table in BO view publications*/
			case IDENTIFYDD_INSTRUCTION:
				strPropertyValue = getObJectProperty(strObJPropName);
				int iTableRowNum = getPublicationsTableRowNumberDD(aKeyWordConfigBean, testScenarioName,
						strPropertyValue, strTestData, strReportKeyWord);
				if (iTableRowNum < 0) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = String.format(ORConstants.BACKOFFICE_DD_PDF_TABLE, iTableRowNum);
				return performControlClick(aKeyWordConfigBean, strPropertyValue);
				/*identifies the given document name using recipient name in particular table in BO view publications in all pages*/
			case IDENTIFYDD_INSTRUCTION_NEGATIVE:
				strPropertyValue = getObJectProperty(strObJPropName);
				int iTableRowNumDDNeg = getPublicationsTableRowNumber(aKeyWordConfigBean, testScenarioName,
						strPropertyValue, strTestData, strReportKeyWord);
				if (iTableRowNumDDNeg < 0) {
					return AppConstants.TEST_RESULT_PASS;
				}
				return AppConstants.TEST_RESULT_FAIL;
			case VERIFY_IDENTIFYDD_INSTRUCTION:
				String strDDChannel[] = StringUtils.split(strTestData, AppConstants.SEPARATOR_COMMA);
				if (strDDChannel == null || strDDChannel.length < 2) {
					LOGGER.error("Invalid Test Data");
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				String strDDChannelProp[] = StringUtils.split(strPropertyValue, AppConstants.SEPARATOR_CAP);
				if (strDDChannelProp == null || strDDChannelProp.length < 2) {
					LOGGER.error("Invalid Property Values");
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = strDDChannelProp[0];
				strTestData = strDDChannel[0];
				int iTableDDChannelRowNum = getPublicationsTableRowNumberDD(aKeyWordConfigBean, testScenarioName,
						strPropertyValue, strTestData, strReportKeyWord);
				if (iTableDDChannelRowNum < 0) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = String.format(strDDChannelProp[1], iTableDDChannelRowNum);
				strTestData = strDDChannel[1];
				return verifyText(aKeyWordConfigBean, testScenarioName, strPropertyValue, strTestData, false);
				/*identifies the given document name using recipient name in particular table in BO view publications and modifies its status-actions coulmn*/
			case EDITDD_INSTRUCTION:
				String strDDEditDoc[] = StringUtils.split(strTestData, AppConstants.SEPARATOR_COMMA);
				if (strDDEditDoc == null || strDDEditDoc.length < 2) {
					LOGGER.error("Invalid Test Data");
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				int iDDEditTableRowNum = getPublicationsTableRowNumber(aKeyWordConfigBean, testScenarioName,
						strPropertyValue, strDDEditDoc[0], strReportKeyWord);
				if (iDDEditTableRowNum < 0) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = String.format(ORConstants.BACKOFFICE_DD_EDITDOC_TABLE, iDDEditTableRowNum);
				return selectByText(aKeyWordConfigBean, strPropertyValue, strDDEditDoc[1], false);
			case FINALIZE_DOC_DD_INSTRUCTION:
				strPropertyValue = getObJectProperty(strObJPropName);
				int iFinalizeDocDDTableRowNum = getPublicationsFinaliseTableRowNumber(aKeyWordConfigBean,
						testScenarioName, strPropertyValue, strTestData, strReportKeyWord);
				if (iFinalizeDocDDTableRowNum < 0) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = String.format(ORConstants.BACKOFFICE_DD_FINALIZE_DOC_TABLE,
						iFinalizeDocDDTableRowNum);
				return clickWebElement(aKeyWordConfigBean, strPropertyValue, true);
			case VERIFY_IMAGE_FILES:
				String strImageFiles[] = StringUtils.split(strTestData, AppConstants.SEPARATOR_COMMA);
				if (strImageFiles == null || strImageFiles.length < 2) {
					LOGGER.error("Invalid Test Data");
					return AppConstants.TEST_RESULT_FAIL;
				}
				File aActualImageFile = new File(strImageFiles[0]);
				File aExpectedImageFile = new File(strImageFiles[1]);
				return verifyImageFiles(testScenarioName, aKeyWordConfigBean, aActualImageFile, aExpectedImageFile);
			case VERIFY_TEXT_FILES:
				String strTextFiles[] = StringUtils.split(strTestData, AppConstants.SEPARATOR_COMMA);
				if (strTextFiles == null || strTextFiles.length < 2) {
					LOGGER.error("Invalid Test Data");
					return AppConstants.TEST_RESULT_FAIL;
				}
				File aActualTextFile = new File(strTextFiles[0]);
				File aExpectedTextFile = new File(strTextFiles[1]);
				return verifyTextFiles(testScenarioName, aKeyWordConfigBean, aActualTextFile, aExpectedTextFile);
				/* compares actual and expected pdf's and and identifies the differences and moves then to newly created folders in particular scenarios*/
			case VERIFY_PDF_FILES:
				return verifyPDFFiles(testScenarioName, stepDescription, aKeyWordConfigBean, strObJPropName,
						strReportKeyWord);
			case CAPTURE_DATABASE_SIZE:
				List<LinkedHashMap<String, Object>> lstCaptureDBValues = DBUtils.fetchDBData(aKeyWordConfigBean,
						strTestData, strObJPropName, strReportKeyWord);
				int iCaptureDbSize = CollectionUtils.size(lstCaptureDBValues);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), strReportKeyWord,
						String.valueOf(iCaptureDbSize));
				return AppConstants.TEST_RESULT_PASS;
			case VERIFY_DATABASE_SIZE:
			case VERIFY_LESS_DATABASE_SIZE:
			case VERIFY_CONATINS_DATABASE_SIZE:
				List<LinkedHashMap<String, Object>> lstVerifyDBValues = DBUtils.fetchDBData(aKeyWordConfigBean,
						strTestData, strObJPropName, strReportKeyWord);
				int iVerifyDbSize = CollectionUtils.size(lstVerifyDBValues);
				String strCaptureDBSize = getRunTimeDataValue(testScenarioName, strReportKeyWord);
				if (StringUtils.isEmpty(strCaptureDBSize) || !StringUtils.isNumeric(strCaptureDBSize)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				int iVerifyDBCaptureSize = Integer.valueOf(strCaptureDBSize);
				boolean isDbSizeVerfiy = aKeyWordConfigBean.getKeyWord() == KeyWord.VERIFY_LESS_DATABASE_SIZE
						? iVerifyDBCaptureSize < iVerifyDbSize
						: aKeyWordConfigBean.getKeyWord() == KeyWord.VERIFY_CONATINS_DATABASE_SIZE
								? iVerifyDBCaptureSize <= iVerifyDbSize
								: iVerifyDBCaptureSize == iVerifyDbSize;
				return isDbSizeVerfiy ? AppConstants.TEST_RESULT_PASS : AppConstants.TEST_RESULT_FAIL;
			case EXECUTE_DATABASE_QUERY:
				strPropertyValue = getObJectProperty(strObJPropName);
				strTestData = getFormattedTestData(testScenarioName, strTestData);
				return DBUtils.validateDB(aKeyWordConfigBean, strTestData, strPropertyValue, strReportKeyWord);
			case EXTRACT_DATABASE_DATA:
				strPropertyValue = getObJectProperty(strObJPropName);
				strTestData = getFormattedTestData(testScenarioName, strTestData);
				return DBUtils.extractDBDataToExcel(AppUtils.removeIllegalCharacters(testScenarioName, true),
						getBrowsersConfigBean(), aKeyWordConfigBean, strTestData, strPropertyValue, strReportKeyWord);
			case SIKULI_CLICK_ACTION:
				strPropertyValue = getObJectProperty(strObJPropName);
				return performSikuliActionClick(aKeyWordConfigBean, strPropertyValue, strTestData);
			case SIKULI_CLICK_SPL_ACTION:
				strPropertyValue = getObJectProperty(strObJPropName);
				return performSikuliActionClickSpl(aKeyWordConfigBean, strPropertyValue, strTestData);
			case SIKULI_INPUT_ACTION:
				strPropertyValue = getObJectProperty(strObJPropName);
				return performSikuliInput(aKeyWordConfigBean, strPropertyValue, strTestData);
			case SIKULI_INPUT_SPL_ACTION:
				strPropertyValue = getObJectProperty(strObJPropName);
				return performSikuliInputSpl(aKeyWordConfigBean, strPropertyValue, strTestData);
				/* ALM- references*/
			case ALM_TESTSET_NAME:
			case ALM_TESTSET_PATH:
			case ALM_TESTCASE_STATUS:
				return updateALMDetails(testScenarioName, aKeyWordConfigBean, strTestData);
				/* Captures SYstemk date in BO and updates the sysdate in datasource sheet*/
			case UPDATE_SYSTEM_DATE:
				strErrorMsg = "Error While Updating System Date";
				String strUpdateSystemDate[] = StringUtils.split(strTestData, AppConstants.SEPARATOR_COMMA);
				if (strUpdateSystemDate == null || strUpdateSystemDate.length < 3 || strUpdateSystemDate.length > 3) {
					LOGGER.error(strErrorMsg);
					return AppConstants.TEST_RESULT_FAIL;
				}
				if (!StringUtils.isNumeric(strUpdateSystemDate[0]) || !StringUtils.isNumeric(strUpdateSystemDate[1])
						|| !StringUtils.isNumeric(strUpdateSystemDate[2])) {
					LOGGER.error(strErrorMsg);
					return AppConstants.TEST_RESULT_FAIL;
				}
				int iDaysToAdd = Integer.valueOf(strUpdateSystemDate[2]);
				String strSystemDate = getRunTimeDataValue(testScenarioName, strReportKeyWord);
				if (StringUtils.isEmpty(strSystemDate)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				Date dtSystem = AppUtils.parseDate(strSystemDate, AppConstants.DATE_FORMAT_DDMMYYYY, LOGGER);
				if (iDaysToAdd > 0) {
					dtSystem = AppUtils.updateDays(dtSystem, iDaysToAdd);
				}
				int iFirstCell = Integer.valueOf(strUpdateSystemDate[0]);
				int iSecondCell = Integer.valueOf(strUpdateSystemDate[1]);
				AppConfig.getInstance().updateSystemDate(dtSystem, iFirstCell, iSecondCell);
				return AppConstants.TEST_RESULT_PASS;
			case CAPTURE_RUNTIME_DATA:
				if (StringUtils.isEmpty(strTestData)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				LinkedHashMap<String, String> mpOldRunTimeBean = getRunTimeData(
						AppUtils.removeIllegalCharacters(strTestData, true));
				if (mpOldRunTimeBean == null || mpOldRunTimeBean.isEmpty()) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				mpOldRunTimeBean.remove(RunTimeDataConstants.SECNARIO_HEADER);
				mpOldRunTimeBean.entrySet().stream().forEach(entryOldRunTimeData -> {
					aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), entryOldRunTimeData.getKey(),
							entryOldRunTimeData.getValue());
				});
				LinkedHashMap<String, String> mpNewRunTimeBean = getRunTimeData(testScenarioName);
				if (mpNewRunTimeBean == null || mpNewRunTimeBean.isEmpty()) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				return AppConstants.TEST_RESULT_PASS;
			case EXECUTE_JS_SCRIPT:
				strErrorMsg = "Error While Executing Java Script";
				if (StringUtils.isEmpty(strTestData)
						|| StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_TRUE)
						|| StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_FALSE)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return executeJavaScript(aKeyWordConfigBean, strPropertyValue, strTestData, true);
			case STORE_BROWSER_SESSION:
				strErrorMsg = "Error While Storing Previous Browser Session";
				if (StringUtils.isEmpty(strTestData)
						|| StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_TRUE)
						|| StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_FALSE)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				SessionManager aBrowserSessionMager = getSessionManager();
				aBrowserSessionMager.storeSessionFile(strTestData);
				return AppConstants.TEST_RESULT_PASS;
			case USE_PREVIOUS_SESSION:
				strErrorMsg = "Error While Using Previous Browser Session";
				if (StringUtils.isEmpty(strTestData)
						|| StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_TRUE)
						|| StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_FALSE)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				SessionManager aPrevBrowserSessionMager = getSessionManager();
				aPrevBrowserSessionMager.usePreviousLoggedInSession();
				return AppConstants.TEST_RESULT_PASS;
			case USE_API_SESSION:
				strErrorMsg = "Error While Using API Session";
				if (StringUtils.isEmpty(strTestData)
						|| StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_TRUE)
						|| StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_FALSE)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				strPropertyValue = getObJectProperty(strObJPropName);
				return loginUsingAPI(aKeyWordConfigBean, strPropertyValue, strTestData);
			case CAPTURE_URL:
				strErrorMsg = "Error While Capturing URL Session";
				String strCaptureURL = getWebDriver().getCurrentUrl();
				getApplicationContext().addRunTimeData(testScenarioName, getBrowsersConfigBean(), strReportKeyWord,
						strCaptureURL);
				return AppConstants.TEST_RESULT_PASS;
			case OPEN_CAPTURED_URL:
				strErrorMsg = "Error While Opening Capturing URL Session";
				strTestData = getRunTimeDataValue(testScenarioName, strReportKeyWord);
				return openURL(aKeyWordConfigBean, strTestData, false);
			case VERIFY_EXCEL_COLUMNS:
				strErrorMsg = "Error While Verifying Excel Columns";
				strPropertyValue = getObJectProperty(strObJPropName);
				return verifyExcelColumns(testScenarioName, strReportKeyWord, aKeyWordConfigBean, strPropertyValue,
						strTestData);
				/* if a Keyword from controller is not present/not matching as above its shows INVALID in console*/
			case INVALID:
			default:
				strErrorMsg = AppUtils.formatMessage("Unknown/Invalid keyword {0}",
						aKeyWordConfigBean.getOriginalKeyWord());
				TestStepReport.logFailure(getBrowsersConfigBean(), getTestSuite().getOriginalScenarioName(),
						stepDescription, strErrorMsg, true, getWebDriver());
				LOGGER.error(strErrorMsg);
				return AppConstants.TEST_RESULT_FAIL;
			}
		} catch (Throwable ex) {
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		}
	}

	@Override
	public synchronized File getDlownloadFile(String strTestCaseID, String strReportKeyWord, String strFilePrefix)
			throws Exception {
		File aDwFile = getApplicationContext().getDownloadedFile(strTestCaseID, strReportKeyWord);
		if (aDwFile != null && aDwFile.exists()) {
			return aDwFile;
		}
		MasterConfig aMasterConfig = MasterConfig.getInstance();
		AppEnvConfigBean aPPRunEnv = aMasterConfig.getAppEnvConfigBean();
		AppRunMode aAppRunMode = aPPRunEnv.getAppRunMode();
		if ((aAppRunMode == AppRunMode.SELENIUM_GRID || aAppRunMode == AppRunMode.APP_PRORITY_GRID)
				&& (getWebDriver() instanceof RemoteWebDriver)) {
			return getGridDlownloadFile(aPPRunEnv, strTestCaseID, strReportKeyWord, strFilePrefix);
		}
		String strFile = AppUtils.getDownloadFolder(getBrowsersConfigBean());
		File aDownloadFileLocation = AppUtils.getFileFromPath(strFile);
		if (!aDownloadFileLocation.exists()) {
			throw new IOException(MessageFormat.format(ErrorMsgConstants.FILENTFOUND, aDownloadFileLocation.getName()));
		}
		List<File> lstFiles = Arrays.asList(aDownloadFileLocation.listFiles()).stream()
				.sorted(Comparator.comparing(File::lastModified).reversed()).collect(Collectors.toList());
		File aFile = CollectionUtils.isEmpty(lstFiles) ? null : lstFiles.stream().filter(aConfFile -> {
			try {
				ClearSessions.closeFileByName(aConfFile);
			} catch (Throwable th) {
			}
			return StringUtils.containsIgnoreCase(aConfFile.getName(), strFilePrefix);
		}).findFirst().orElse(null);
		if (aFile == null || !aFile.exists()) {
			throw new IOException(MessageFormat.format(ErrorMsgConstants.FILENTFOUND, aDownloadFileLocation.getPath()));
		}
		if (aFile.length() <= 0) {
			throw new Exception(MessageFormat.format(ErrorMsgConstants.FILE_NT_DW_PROPERLY, aFile.getPath()));
		}

		File aTragtFile = getScenarioDlownloadFile(strTestCaseID, strReportKeyWord, aFile.getName());
		ClearSessions.closeFileByName(aFile);
		Path aTestCaseFile = null;
		try {
			aTestCaseFile = Files.move(aFile.toPath(), aTragtFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (Throwable th) {
			aTestCaseFile = Files.copy(aFile.toPath(), aTragtFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		aDwFile = aTestCaseFile.toFile();
		getApplicationContext().addDownloadedFile(strTestCaseID, strReportKeyWord, aDwFile);
		return aDwFile;
	}
}
