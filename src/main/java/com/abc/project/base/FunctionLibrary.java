/****************************************************************************
 * File Name 		: FunctionLibrary.java
 * Package			: com.dxc.zurich.base
 * Author			: pmusunuru2
 * Creation Date	: Feb 19, 2021
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

import java.awt.Color;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.SessionStorage;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.Zip;
import org.openqa.selenium.json.Json;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.remote.http.HttpClient;
import org.openqa.selenium.remote.http.HttpMethod;
import org.openqa.selenium.remote.http.HttpRequest;
import org.openqa.selenium.remote.http.HttpResponse;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.sikuli.api.ScreenLocation;
import org.sikuli.api.robot.Keyboard;
import org.sikuli.api.robot.Mouse;
import org.sikuli.api.robot.desktop.DesktopKeyboard;
import org.sikuli.api.robot.desktop.DesktopMouse;
import org.sikuli.script.Location;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import com.abc.project.alm.beans.ALMWrapperConfigBean;
import com.abc.project.beans.AppEnvConfigBean;
import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.beans.KeyWordConfigBean;
import com.abc.project.beans.PDFExclusions;
import com.abc.project.beans.TestSuiteBean;
import com.abc.project.config.AppConfig;
import com.abc.project.config.AppContext;
import com.abc.project.config.MasterConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.ErrorMsgConstants;
import com.abc.project.constants.ORConstants;
import com.abc.project.constants.RunTimeDataConstants;
import com.abc.project.constants.SummaryReportConstants;
import com.abc.project.enums.Browsers;
import com.abc.project.enums.IdentificationType;
import com.abc.project.enums.KeyWord;
import com.abc.project.enums.PDFFileType;
import com.abc.project.enums.StartFinish;
import com.abc.project.pdf.utils.PDFHighlightTextStripper;
import com.abc.project.pdf.utils.PDFHighlightTextStripperByArea;
import com.abc.project.reports.TestStepReport;
import com.abc.project.utils.AppUtils;
import com.abc.project.utils.EmailUtils;
import com.abc.project.utils.ExcelUtils;
import com.abc.project.utils.PropertyHandler;
import com.abc.project.utils.SessionManager;
import com.abc.project.utils.WebUtils;
import com.github.javafaker.Faker;
import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.github.romankh3.image.comparison.model.ImageComparisonState;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import de.redsix.pdfcompare.CompareResult;
import de.redsix.pdfcompare.PageArea;
import de.redsix.pdfcompare.PdfComparator;
import de.redsix.pdfcompare.env.SimpleEnvironment;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy.ByAccessibilityId;
import io.appium.java_client.MobileBy.ByAndroidDataMatcher;
import io.appium.java_client.MobileBy.ByAndroidUIAutomator;
import io.appium.java_client.MobileBy.ByAndroidViewMatcher;
import io.appium.java_client.MobileBy.ByCustom;
import io.appium.java_client.MobileBy.ByImage;
import io.appium.java_client.MobileBy.ByIosClassChain;
import io.appium.java_client.MobileBy.ByIosNsPredicate;
import io.appium.java_client.MobileBy.ByWindowsAutomation;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.TapOptions;
import io.appium.java_client.touch.offset.ElementOption;

/**
 * @author pmusunuru2
 * @since Feb 19, 2021 9:12:39 am
 */
public abstract class FunctionLibrary {

	private static Logger LOGGER;

	private static Logger ERROR_LOGGER;

	private WebDriver aWebDriver;

	private BrowsersConfigBean aBrowsersConfigBean;

	private TestSuiteBean aTestSuite;

	private SessionManager aSessionManager;

	public FunctionLibrary(TestSuiteBean aTestSuite, WebDriver aWebDriver) {
		setTestSuite(aTestSuite);
		setBrowsersConfigBean(aTestSuite.getBrowsersConfigBean());
		setWebDriver(aWebDriver);
		LOGGER = getLogger();
		ERROR_LOGGER = getErrorLogger();
	}

	public abstract Logger getLogger();

	public abstract Logger getErrorLogger();

	/**
	 * @return the aTestSuite
	 */
	public TestSuiteBean getTestSuite() {
		return aTestSuite;
	}

	/**
	 * @param aTestSuite the aTestSuite to set
	 */
	public void setTestSuite(TestSuiteBean aTestSuite) {
		this.aTestSuite = aTestSuite;
	}

	/**
	 * @return the aBrowser
	 */
	public Browsers getBrowser() {
		return getBrowsersConfigBean() == null ? Browsers.INVALID_BROWSER : getBrowsersConfigBean().getBrowser();
	}

	/**
	 * @return the aBrowsersConfigBean
	 */
	public BrowsersConfigBean getBrowsersConfigBean() {
		return aBrowsersConfigBean;
	}

	/**
	 * @param aBrowsersConfigBean the aBrowsersConfigBean to set
	 */
	public void setBrowsersConfigBean(BrowsersConfigBean aBrowsersConfigBean) {
		this.aBrowsersConfigBean = aBrowsersConfigBean;
	}

	/**
	 * @return the aWebDriver
	 */
	public WebDriver getWebDriver() {
		return aWebDriver;
	}

	public SessionManager getSessionManager() {
		if (aSessionManager == null) {
			aSessionManager = new SessionManager(getWebDriver());
		}
		return aSessionManager;
	}

	/**
	 * @param aWebDriver the aWebDriver to set
	 */
	public void setWebDriver(WebDriver aWebDriver) {
		// Set TimeOuts
		try {
			int iDriverImplicitWaitTime = getDriverImplicitWaitTime();
			if (iDriverImplicitWaitTime > 0) {
				aWebDriver.manage().timeouts().implicitlyWait(Duration.of(iDriverImplicitWaitTime, ChronoUnit.MILLIS));
			}
		} catch (Throwable th) {
			// do nothing
		}
		this.aWebDriver = aWebDriver;
	}

	/***
	 * Fetches the Application Context
	 * 
	 * @return
	 */
	public AppContext getApplicationContext() {
		return AppContext.getInstance();
	}

	protected File getScenarioDlownloadFile(String strTestCaseID, String strReportKeyWord, String strFileName) {
		String strModifiedFile = String.format("%s_%s_%s_%s_%s.%s", MasterConfig.getInstance().getAppRunID(),
				AppUtils.getScenarioReportFileName(strTestCaseID, TestStepReport.SCENARIOS_FILE_NAME_LENGTH),
				AppUtils.getScenarioReportFileName(FilenameUtils.getBaseName(strFileName), 5),
				AppUtils.getScenarioReportFileName(strReportKeyWord, 10), AppUtils.getFileDate(),
				FilenameUtils.getExtension(strFileName));
		File aTragtFile = Paths.get(AppConfig.getInstance().getBrowserExecutionReportFolder(getBrowsersConfigBean()),
				AppUtils.getScenarioReportFileName(strTestCaseID, TestStepReport.SCENARIOS_FILE_NAME_LENGTH),
				strModifiedFile).toFile();
		if (!aTragtFile.getParentFile().exists()) {
			aTragtFile.getParentFile().mkdirs();
		}
		return aTragtFile;
	}

	public abstract String getVerificationProperty(String strPropertyKey) throws Exception;

	public abstract String getObJectProperty(String strPropertyKey) throws Exception;

	public abstract String callExcelMacro(File file, String macroFunctionName, String... param) throws Exception;

	public abstract File getDlownloadFile(String strTestCaseID, String strReportKeyWord, String strFilePrefix)
			throws Exception;

	protected abstract long getDriverExternalWaitTime();

	protected abstract int getDriverImplicitWaitTime();

	protected abstract long getDriverExplicitWaitTime();

	protected abstract long getDriverSleepTime();

	protected abstract String getAppEnvValue(String strKey) throws Exception;

	private Wait<WebDriver> getDefaultWebDriverWait() {
		long lDriverExplicitWaitTime = getDriverExplicitWaitTime();
		return getWebDriverWait(lDriverExplicitWaitTime);
	}

	private Wait<WebDriver> getWebDriverWait(long lDriverExplicitWaitTime) {
		WebDriver aDriver = getWebDriver();
		FluentWait<WebDriver> aDriverWait = new FluentWait<WebDriver>(aDriver);
		// Specify the timout of the wait
//		aDriverWait.withTimeout(lDriverExplicitWaitTime, TimeUnit.MILLISECONDS);
		aDriverWait.withTimeout(Duration.of(lDriverExplicitWaitTime, ChronoUnit.MILLIS));
		// Sepcify polling time
//		aDriverWait.pollingEvery(getDriverSleepTime(), TimeUnit.MILLISECONDS);
		aDriverWait.pollingEvery(Duration.of(getDriverSleepTime(), ChronoUnit.MILLIS));
		// Specify what exceptions to ignore
		aDriverWait.ignoring(NoSuchElementException.class);
//		Wait<WebDriver> aDriverWait = new WebDriverWait(aDriver, getDriverExplicitWaitTime(), getDriverSleepTime());
		return aDriverWait;
	}

	/**
	 * Fetches Required runtime data value stored in cache
	 * 
	 * @param testScenarioName
	 * @param strKey
	 * @return
	 */
	public String getRunTimeDataValue(String testScenarioName, String strKey) {
		LinkedHashMap<String, String> mpRunTimeBean = getRunTimeData(testScenarioName);
		return mpRunTimeBean.get(strKey);
	}

	/**
	 * Fetches Required runtime data value stored in cache
	 * 
	 * @param testScenarioName
	 * @param strKey
	 * @return
	 */
	public String getExecRefSurmmaryReportValue(String testScenarioName, String strKey) {
		AppContext aPPContext = getApplicationContext();
		LinkedHashMap<String, String> mpRunTimeBean = aPPContext.getExecRefSurmmaryReport(testScenarioName,
				getBrowsersConfigBean());
		return mpRunTimeBean.get(strKey);
	}

	/****
	 * Fetches runtime data stored in cache
	 * 
	 * @param testScenarioName
	 * @return
	 */
	protected LinkedHashMap<String, String> getRunTimeData(String testScenarioName) {
		testScenarioName = AppUtils.getValidPartScenarioName(testScenarioName);
		AppContext aPPContext = getApplicationContext();
		String strAppContextKey = aPPContext.getAppContextKey(testScenarioName, getBrowsersConfigBean());
		LinkedHashMap<String, String> mpRunTimeBean = aPPContext.getRunTimeDataBean(strAppContextKey);
		return mpRunTimeBean;
	}

	protected boolean isTestSetpFailed(String strResult) {
		return StringUtils.equalsIgnoreCase(AppConstants.TEST_RESULT_FAIL, strResult);
	}

	protected String launchApp(KeyWordConfigBean aKeyword, String strTestData) throws Exception {
		if (StringUtils.isEmpty(strTestData)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessgae = AppUtils.formatMessage("Launching App for key word {0}", aKeyword.toString());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessgae));
			WebDriver aDriver = getWebDriver();
			if (aDriver instanceof AppiumDriver<?>) {
				AppiumDriver<?> aAppiumDriver = (AppiumDriver<?>) aDriver;
				boolean bCanClose = BooleanUtils.toBoolean(strTestData);
				if (bCanClose) {
					aAppiumDriver.closeApp();
				}
				aAppiumDriver.launchApp();
				return AppConstants.TEST_RESULT_PASS;
			}
			return AppConstants.TEST_RESULT_FAIL;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessgae);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessgae));
		}
	}

	/***
	 * Updates the excel file based on test data Example Test DATA
	 * {KEYN_COLUMNNAME}+{KEYNVALUE}^{VALUEN_COLNAME}+{VALUEN_DATA};{VALUEN+1_COLNAME}+{VALUEN+1_DATA};
	 * ||
	 * {KEYN+!_COLUMNNAME}+{KEYN+1VALUE}^{VALUEN_COLNAME}+{VALUEN_DATA};{VALUEN+1_COLNAME}+{VALUEN+1_DATA};
	 * 
	 * @param strTestCaseID
	 * @param aKeyword
	 * @param strPropertyValue
	 * @param strTestData
	 * @return
	 * @throws Exception
	 */
	protected String updateExcle(String strTestCaseID, String strReportKeyWord, KeyWordConfigBean aKeyword,
			String strPropertyValue, String strTestData) throws Exception {
		if (StringUtils.isEmpty(strTestCaseID) || StringUtils.isEmpty(strPropertyValue)
				|| StringUtils.isEmpty(strTestData)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}

		strTestData = StringUtils.trim(strTestData);

		String strLogMessgae = AppUtils.formatMessage("Updating excel file for test case {0} and {1}", strTestCaseID,
				aKeyword.toString());
		String strErrorMsg = AppUtils.formatMessage("Error while {0}", strLogMessgae);
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessgae));

		if (StringUtils.indexOf(strPropertyValue, AppConstants.SEPARATOR_COMMA) <= 0) {
			LOGGER.error(strErrorMsg);
			return AppConstants.TEST_RESULT_FAIL;
		}

		String[] strProperties = StringUtils.split(strPropertyValue, AppConstants.SEPARATOR_COMMA);
		if (strProperties == null || strProperties.length < 3 || strProperties.length > 3) {
			LOGGER.error(strErrorMsg);
			return AppConstants.TEST_RESULT_FAIL;
		}

		String strSheetName = strProperties[1];
		File aDwFile = getDlownloadFile(strTestCaseID, strReportKeyWord, strProperties[0]);
		if (!aDwFile.exists()) {
			LOGGER.error(strErrorMsg);
			return AppConstants.TEST_RESULT_FAIL;
		}
		String strHeaderRow = strProperties[2];
		int iHeaderRow = StringUtils.isNumeric(strHeaderRow) ? Integer.valueOf(strHeaderRow) : 0;
		File aModifiedFile = Paths.get(aDwFile.getParent(), "Modified", aDwFile.getName()).toFile();
		boolean isFileModifed = false;
		try (XSSFWorkbook aWorkbook = ExcelUtils.getWorkBook(aDwFile)) {
			FormulaEvaluator evaluator = aWorkbook.getCreationHelper().createFormulaEvaluator();
			Sheet aControllerSheet = aWorkbook.getSheet(strSheetName);
			if (aControllerSheet == null) {
				throw new IOException(
						MessageFormat.format(ErrorMsgConstants.SHEET_NOT_FOUND, strSheetName, aDwFile.getPath()));
			}
			Row aHeaderRow = ExcelUtils.getRow(aControllerSheet, iHeaderRow);
			if (aHeaderRow == null) {
				throw new Exception(MessageFormat.format(ErrorMsgConstants.ERR_ROW, iHeaderRow, strSheetName));
			}
			String[] strRowValues = StringUtils.split(strTestData, AppConstants.SEPARATOR_OR);
			for (String strRowValue : strRowValues) {

				if (StringUtils.indexOf(strRowValue, AppConstants.SEPARATOR_CAP) <= 0) {
					LOGGER.error(strErrorMsg);
					return AppConstants.TEST_RESULT_FAIL;
				}
				Row aDataRow = null;
				DATA_ROW_LOOP: for (int iRow = iHeaderRow + 1; iRow <= aControllerSheet.getLastRowNum(); iRow++) {
					aDataRow = ExcelUtils.getRow(aControllerSheet, iRow);

					if (aDataRow == null) {
						throw new Exception(MessageFormat.format(ErrorMsgConstants.ERR_ROW, iRow + 1, strSheetName));
					}
					String strDataRow = StringUtils.substringBefore(strRowValue, AppConstants.SEPARATOR_CAP);
					int lastCellNumber = aDataRow.getLastCellNum();
					String[] strCellValues = StringUtils.split(strDataRow, AppConstants.SEPARATOR_PLUS);
					if (strCellValues == null || strCellValues.length < 2 || strCellValues.length > 2) {
						LOGGER.error(strErrorMsg);
						return AppConstants.TEST_RESULT_FAIL;
					}
					for (int cellNum = 0; cellNum < lastCellNumber; cellNum++) {
						String strHeader = ExcelUtils.getStringValue(evaluator, aControllerSheet, aHeaderRow, cellNum);
						String strValue = ExcelUtils.getStringValue(evaluator, aControllerSheet, aDataRow, cellNum);
						if (StringUtils.equalsIgnoreCase(strHeader, StringUtils.trim(strCellValues[0]))
								&& StringUtils.equalsIgnoreCase(strValue, StringUtils.trim(strCellValues[1]))) {
							break DATA_ROW_LOOP;
						}
					}
				}
				if (aDataRow == null) {
					LOGGER.error(strErrorMsg);
					return AppConstants.TEST_RESULT_FAIL;
				}
				int lastCellNumber = aDataRow.getLastCellNum();
				String strColumns = StringUtils.substringAfter(strRowValue, AppConstants.SEPARATOR_CAP);
				if (StringUtils.indexOf(strColumns, AppConstants.SEPARATOR_SEMICOLON) <= 0) {
					LOGGER.error(strErrorMsg);
					return AppConstants.TEST_RESULT_FAIL;
				}
				String[] strColumnValues = StringUtils.split(strColumns, AppConstants.SEPARATOR_SEMICOLON);
				for (String strColumnValue : strColumnValues) {
					if (StringUtils.indexOf(strColumnValue, AppConstants.SEPARATOR_PLUS) <= 0) {
						LOGGER.error(strErrorMsg);
						return AppConstants.TEST_RESULT_FAIL;
					}
					String[] strCellValues = StringUtils.split(strColumnValue, AppConstants.SEPARATOR_PLUS);
					if (strCellValues == null || strCellValues.length < 2 || strCellValues.length > 2) {
						LOGGER.error(strErrorMsg);
						return AppConstants.TEST_RESULT_FAIL;
					}
					for (int cellNum = 0; cellNum < lastCellNumber; cellNum++) {
						String strHeader = ExcelUtils.getStringValue(evaluator, aControllerSheet, aHeaderRow, cellNum);
						if (!StringUtils.equalsIgnoreCase(StringUtils.trim(strHeader),
								StringUtils.trim(strCellValues[0]))) {
							continue;
						}
						ExcelUtils.setCellValue(aDataRow, cellNum, StringUtils.trim(strCellValues[1]));
						isFileModifed = true;
						break;
					}
				}
			}
			if (!isFileModifed) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			if (!aModifiedFile.getParentFile().exists()) {
				aModifiedFile.getParentFile().mkdirs();
			}
			ExcelUtils.writeWrokBook(aWorkbook, aModifiedFile);
			String strStatus = aModifiedFile == null || !aModifiedFile.exists() ? AppConstants.TEST_RESULT_FAIL
					: AppConstants.TEST_RESULT_PASS;
			AppContext aPPContext = getApplicationContext();
			if (!isTestSetpFailed(strStatus)) {
				aPPContext.addRunTimeData(strTestCaseID, getBrowsersConfigBean(),
						RunTimeDataConstants.MODIFIED_EXCEL_FILE, aModifiedFile.getName());
			}
			return strStatus;
		} catch (Exception ex) {
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessgae));
		}
	}

	/***
	 * Fetch Locator By IdentificationType
	 * 
	 * @param aKeyWorkType
	 * @param strPropertyValue
	 * @return
	 * @throws Exception
	 */
	private By getLocatorByIdentificationType(IdentificationType aKeyWorkType, String strPropertyValue)
			throws Exception {
		switch (aKeyWorkType) {
		case ID:
			return By.id(strPropertyValue);
		case NAME:
			return By.name(strPropertyValue);
		case XPATH:
			return By.xpath(strPropertyValue);
		case TAG_NAME:
			return By.tagName(strPropertyValue);
		case LINKTEXT:
			return By.linkText(strPropertyValue);
		case PLINK:
			return By.partialLinkText(strPropertyValue);
		case CLASSNAME:
			return By.className(strPropertyValue);
		case CSSSELECTOR:
			return By.cssSelector(strPropertyValue);
		case ACCESSIBILITY_ID:
			return ByAccessibilityId.AccessibilityId(strPropertyValue);
		case WINDOWS_UI_AUTOMATION:
			return ByWindowsAutomation.windowsAutomation(strPropertyValue);
		case IOS_PREDICATE_STRING:
			return ByIosNsPredicate.iOSNsPredicateString(strPropertyValue);
		case IOS_CLASS_CHAIN:
			return ByIosClassChain.iOSClassChain(strPropertyValue);
		case ANDROID_UI_AUTOMATOR:
			return ByAndroidUIAutomator.AndroidUIAutomator(strPropertyValue);
		case ANDROID_DATAMATCHER:
			return ByAndroidDataMatcher.androidDataMatcher(strPropertyValue);
		case ANDROID_VIEWMATCHER:
			return ByAndroidViewMatcher.androidViewMatcher(strPropertyValue);
		case IMAGE:
			return ByImage.image(WebUtils.getImageFileEncodeBase64(strPropertyValue));
		case CUSTOM:
			return ByCustom.custom(strPropertyValue);
		default:
			throw new Exception("Invalid Locator Type");
		}
	}

	/***
	 * Used to fetches the WebElement
	 * 
	 * Waits until visibility Of ElementLocated
	 * 
	 * @param strKeyWordType
	 * @param strPropertyValue
	 * @return
	 * @throws Exception
	 */
	protected WebElement getWebElement(IdentificationType aKeyWorkType, String strPropertyValue) throws Exception {
		WebDriver aDriver = getWebDriver();
		Wait<WebDriver> aDriverWait = getDefaultWebDriverWait();
		By aLocator = getLocatorByIdentificationType(aKeyWorkType, strPropertyValue);
		WebElement aWebElement = null;
		try {
			aWebElement = aDriver.findElement(aLocator);
		} catch (Throwable th) {
			aWebElement = null;
		}
		if (aWebElement != null) {
			return aWebElement;
		}
		aDriverWait.until(ExpectedConditions.visibilityOfElementLocated(aLocator));
		return aDriver.findElement(aLocator);
	}

	/***
	 * Used to fetches the WebElements
	 * 
	 * Waits until visibility Of ElementLocated
	 * 
	 * @param strKeyWordType
	 * @param strPropertyValue
	 * @return
	 * @throws Exception
	 */
	protected List<WebElement> getWebElements(IdentificationType aKeyWorkType, String strPropertyValue)
			throws Exception {
		WebDriver aDriver = getWebDriver();
		Wait<WebDriver> aDriverWait = getDefaultWebDriverWait();
		By aLocator = getLocatorByIdentificationType(aKeyWorkType, strPropertyValue);
		List<WebElement> lstWebElements = null;
		try {
			lstWebElements = aDriver.findElements(aLocator);
		} catch (Throwable th) {
			lstWebElements = null;
		}
		if (CollectionUtils.isNotEmpty(lstWebElements)) {
			return lstWebElements;
		}
		aDriverWait.until(ExpectedConditions.visibilityOfElementLocated(aLocator));
		return aDriver.findElements(aLocator);
	}

	/***
	 * Waits until the page loaded
	 * 
	 * @param aDriverWait
	 * @param aKeyWordConfigBean
	 * @param strPropertyValue
	 * @param strTestData
	 * @return
	 * @throws Exception
	 */
	protected String waitUntilPageLoad(KeyWordConfigBean aKeyWordConfigBean, String strPropertyValue,
			String strTestData) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		IdentificationType aKeyWorkType = aKeyWordConfigBean.getKeyWorkType();
		String strLogMessage = AppUtils.formatMessage("Waiting for page to load for {0} with property {1}",
				aKeyWordConfigBean.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			long lDriverExplicitWaitTime = getDriverExplicitWaitTime();
			if (StringUtils.isNotEmpty(StringUtils.trim(strTestData)) && NumberUtils.isParsable(strTestData)) {
				Double dWaitTime = Double.valueOf(strTestData);
				lDriverExplicitWaitTime = (long) (dWaitTime * AppConstants.DEFAULT_MILLISECONDS);
			}
			Wait<WebDriver> aDriverWait = getWebDriverWait(lDriverExplicitWaitTime);
			By aLocator = getLocatorByIdentificationType(aKeyWorkType, strPropertyValue);
			KeyWord aKeyWord = aKeyWordConfigBean.getKeyWord();
			switch (aKeyWord) {
			case SYNC_WAIT:
				aDriverWait.until(ExpectedConditions.elementToBeClickable(aLocator));
				break;
			default:
				aDriverWait.until(ExpectedConditions.invisibilityOfElementLocated(aLocator));
				break;
			}
			waitByTime(500);
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	/***
	 * Used to fetches the WebElement
	 * 
	 * waits until elementToBeClickable
	 * 
	 * @param strKeyWordType
	 * @param strPropertyValue
	 * @return
	 * @throws Exception
	 */
	protected WebElement getClickWebElement(IdentificationType aKeyWorkType, String strPropertyValue) throws Exception {
		WebDriver aDriver = getWebDriver();
		Wait<WebDriver> aDriverWait = getDefaultWebDriverWait();
		By aLocator = getLocatorByIdentificationType(aKeyWorkType, strPropertyValue);
		WebElement aWebElement = null;
		try {
			aWebElement = aDriver.findElement(aLocator);
		} catch (Throwable th) {
			aWebElement = null;
		}
		if (aWebElement != null) {
			return aWebElement;
		}
		aDriverWait.until(ExpectedConditions.elementToBeClickable(aLocator));
		return aDriver.findElement(aLocator);
	}

	/***
	 * Clears all the data captured in web diver
	 * 
	 * @param aKeyWord
	 */
	protected void clearData(KeyWordConfigBean aKeyWord) {
		Browsers aBrowser = getBrowser();
		String strClearData = String.format("Clearing WebDriver data using keyword %s in Browser %s with prority %s",
				aKeyWord.toString(), aBrowser.getBrowserName(), aBrowser.getBrowserPrority());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strClearData));
			WebDriver aWebDriver = getWebDriver();
			Options aDriverOptions = aWebDriver.manage();
			if (aDriverOptions != null) {
				aDriverOptions.deleteAllCookies();
			}
			try {
				Alert aAlert = aWebDriver.switchTo().alert();
				if (aAlert != null) {
					aAlert.accept();
				}
			} catch (Exception ex) {

			}
			if (!(aWebDriver instanceof WebStorage)) {
				return;
			}
			WebStorage aWebStorage = (WebStorage) aWebDriver;
			SessionStorage aSessionStorage = aWebStorage.getSessionStorage();
			if (aSessionStorage != null) {
				aSessionStorage.clear();
			}
			LocalStorage aLocalStorage = aWebStorage.getLocalStorage();
			if (aLocalStorage != null) {
				aLocalStorage.clear();
			}
		} catch (Throwable th) {
			String strErrorMessage = AppUtils.formatMessage("Error While {0}", strClearData);
			LOGGER.error(strErrorMessage);
			ERROR_LOGGER.error(strErrorMessage, th);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strClearData));
		}
	}

	private void performUnSafeOperations() {
		try {

			Browsers aBrowser = getBrowser();
			if (WebUtils.isSelfHealingRequire() && aBrowser.isNative()) {
				return;
			}
			WebDriver aDriver = getWebDriver();
			Wait<WebDriver> aDriverWait = getDefaultWebDriverWait();
			try {
				// To handle cookies section appearing at top of the screen
				WebElement linkAcceptAndContine = aDriver.findElement(By.linkText(ORConstants.LINK_ACCEPTNCONTINUE));
				performWebElementFocus(KeyWord.INVALID, linkAcceptAndContine);
				linkAcceptAndContine.click();
			} catch (Exception e) {
				// Ignore Exception as we don't get the cookies section screen very
				// often
			}

			try {
				aDriverWait.until(
						ExpectedConditions.invisibilityOfElementLocated(By.xpath(ORConstants.REFRESH_ANIMATE_XPATH)));
			} catch (Exception e) {
				// Ignore Exception as we don't get the cookies section screen very
				// often
			}
			try {
				aDriverWait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(ORConstants.OVERLAY_XPATH)));
			} catch (Exception e) {
				// Ignore Exception as we don't get the cookies section screen very
				// often
			}
			WebElement aBtnAdvance = aDriver.findElement(By.xpath(ORConstants.BTN_ADVANCE_TEXT_XPATH));
			performWebElementFocus(KeyWord.INVALID, aBtnAdvance);
			if (aBtnAdvance.isDisplayed()) {
				aBtnAdvance.click();
				delayInSeconds(2);
				WebElement linkProceed = aDriver.findElement(By.xpath(ORConstants.LINK_PROCEED_XPATH));
				performWebElementFocus(KeyWord.INVALID, linkProceed);
				linkProceed.click();
				delayInSeconds(1);
			}
		} catch (Throwable e) {
		}
	}

	/***
	 * Navigates to specified URL
	 * 
	 * @param strURL
	 * @return
	 * @throws Exception
	 */
	protected String openURL(KeyWordConfigBean aKeyWord, String strURL, boolean bClearData) throws Exception {
		if (StringUtils.isEmpty(strURL)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_OPEN_URL);
		}
		String strLogMessage = String.format("Navigating to URL %s", strURL);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			if (bClearData) {
				clearData(aKeyWord);
			}
			WebDriver aDriver = getWebDriver();
			waitByTime(200);
			aDriver.get(strURL);
			waitByTime(500);
			// aDriver.get(strURL);

			// performUnSafeOperations();

			List<String> lstLogOff = new ArrayList<>();
			lstLogOff.add(ORConstants.SIGNOUT_PROPERTY_NAME);
			lstLogOff.add(ORConstants.LOGOFF_PROPERTY_NAME);
			boolean bIsLogoutSucess = false;
			for (String strProperty : lstLogOff) {
				String strSignOutButton = getObJectProperty(strProperty);
				if (StringUtils.isEmpty(strSignOutButton)) {
					continue;
				}
				try {
					if (!bIsLogoutSucess && !isTestSetpFailed(clickWebElement(aKeyWord, strSignOutButton, false))) {
						bIsLogoutSucess = true;
						break;
					}
				} catch (Exception e) {
				}
			}
			if (bIsLogoutSucess) {
				delayInSeconds(2);
				openURL(aKeyWord, strURL, bClearData);
			}

			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected void performWebElementFocus(KeyWord aKeyWord, WebElement aElement) {
//      Browsers aBrowser = getBrowser();
		if (aElement == null) {
			return;
		}
		String strLogMessage = AppUtils.formatMessage("Focusing on web element for {0}", aKeyWord.toString());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
//          BrowsersConfigBean aBrowsersConfigBean = getBrowsersConfigBean();
//          Browsers aBrowsers = aBrowsersConfigBean == null ? Browsers.INVALID_BROWSER
//                  : aBrowsersConfigBean.getBrowser();
//          String strScrollTo = ORConstants.EXEC_JAVA_SCRIPT_SMOOTH_SCROLL_TO_ELEMENT_CMD;
//          if (aBrowsers == Browsers.WINDOWS_IE || StringUtils.containsIgnoreCase(aBrowsersConfigBean.getBrowserName(),
//                  AppConstants.BROWSER_SAFARI)) {
			boolean isIntoView = false;
			String strScrollTo = String.format(ORConstants.EXEC_JAVA_SCRIPT_SCROLL_TO_ELEMENT_CMD, isIntoView);
//          }
			WebDriver aDriver = getWebDriver();
			if (aDriver instanceof AppiumDriver<?> && aElement instanceof RemoteWebElement) {
				AppiumDriver<?> aAppiumDriver = (AppiumDriver<?>) aDriver;
				RemoteWebElement aRemoteWebElement = (RemoteWebElement) aElement;
				Point aLocation = aRemoteWebElement.getLocation();
				WebUtils.touchScroll(aAppiumDriver, aLocation.getX(), aLocation.getY(), getDriverSleepTime());
			} else {
				executeJavaScript(aElement, strScrollTo);
			}
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	/**
	 * Used to fetch WebElemet by keyword Type
	 * 
	 * @param aKeyWord
	 * @param strPropertyValue
	 * @return
	 * @throws Exception
	 */
	protected WebElement getWebElement(KeyWordConfigBean aKeyWord, String strPropertyValue) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Fetching on web element for {0} with property {1}",
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			performUnSafeOperations();
			WebElement aElement = getWebElement(aKeyWord.getKeyWorkType(), strPropertyValue);
			performWebElementFocus(aKeyWord.getKeyWord(), aElement);
			return aElement;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return null;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	/***
	 * Used to fetch click-able WebElemet by keyword Type
	 * 
	 * 
	 * @param aKeyWord
	 * @param strPropertyValue
	 * @return
	 * @throws Exception
	 */
	protected WebElement getClickWebElement(KeyWordConfigBean aKeyWord, String strPropertyValue) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Fetching Clickable web element for {0} with property {1}",
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			performUnSafeOperations();
			WebElement aElement = getClickWebElement(aKeyWord.getKeyWorkType(), strPropertyValue);
			performWebElementFocus(aKeyWord.getKeyWord(), aElement);
			return aElement;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return null;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected boolean isWebElementDisabled(KeyWordConfigBean aKeyWord, String strPropertyValue, WebElement aWebElement)
			throws Exception {
		if (aWebElement == null || StringUtils.isEmpty(strPropertyValue)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Verifying WebElement Disabled or not {0} with property {1}",
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			// https://stackoverflow.com/questions/38635539/selenium-webdriver-how-to-get-the-enabled-status-of-an-angularjs-li-element
			String strDisableValue = aWebElement == null ? ""
					: aWebElement.getCssValue(ORConstants.DISABLE_CSS_PROPERTY_VALUE);
			Object aDisableValue = false;
			try {
				aDisableValue = executeJavaScript(aWebElement, ORConstants.DISABLE_CMD);
			} catch (Exception ex) {
				aDisableValue = false;
			}
			boolean bIsWebElementDisabled = aWebElement != null && (!aWebElement.isEnabled()
					|| BooleanUtils.toBoolean(aDisableValue == null ? "false" : aDisableValue.toString())
					|| StringUtils.equals(ORConstants.DISABLE_CSS_VALUE, strDisableValue)) ? true : false;
			return bIsWebElementDisabled;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return false;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String clickWebElement(KeyWordConfigBean aKeyWord, String strPropertyValue, boolean isStrictCheck)
			throws Exception {
		if (StringUtils.isEmpty(strPropertyValue)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Cilcking on {0} with property {1}", aKeyWord.toString(),
				strPropertyValue);
		WebElement aWebElement = null;
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			aWebElement = getClickWebElement(aKeyWord, strPropertyValue);

			if (isStrictCheck && aWebElement != null && aWebElement.isDisplayed()) {
				aWebElement.click();
				waitByTime(500);
				return AppConstants.TEST_RESULT_PASS;
			}
			if (!isStrictCheck && aWebElement != null) {
				aWebElement.click();
				waitByTime(500);
				return AppConstants.TEST_RESULT_PASS;
			}
			return AppConstants.TEST_RESULT_FAIL;
		} catch (Exception ex) {
			String strRegex = AppUtils.formatMessage("Element {0} is not clickable at point",
					AppConstants.HTML_PATTERN);
			if ((AppUtils.isRegex(ex.getLocalizedMessage(), strRegex)
					|| ex instanceof org.openqa.selenium.ElementClickInterceptedException)
					&& (aWebElement != null && !isWebElementDisabled(aKeyWord, strPropertyValue, aWebElement))) {
				return executeJavaScript(aKeyWord, strPropertyValue, ORConstants.EXEC_JAVA_SCRIPT_CLICK_CMD, true);
			}
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected LinkedList<String> getTableHeaders(KeyWordConfigBean aKeyWord, String strPropertyValue,
			String strTestData) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(strTestData)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}

		String strLogMessage = AppUtils.formatMessage("Fetching table {0} headers with property {1}", strTestData,
				strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aTable = getWebElement(aKeyWord, strPropertyValue);
			List<WebElement> lstRows = aTable.findElements(By.tagName("tr"));
			LinkedList<String> lstData = new LinkedList<>();
			for (WebElement aTblRow : lstRows) {
				lstData.add(SummaryReportConstants.SECNARIO_HEADER);
				List<WebElement> lstColumns = aTblRow.findElements(By.tagName("td"));
				for (WebElement aColumn : lstColumns) {
					String strHeaderColumnText = aColumn.getText();
					lstData.add(StringUtils.trim(strHeaderColumnText));
				}
				break;
			}
			return lstData;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return null;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected LinkedHashMap<WebElement, String> getTableData(KeyWordConfigBean aKeyWord, String testScenarioName,
			String strPropertyValue, String strTestData) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(strTestData)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}

		String strLogMessage = AppUtils.formatMessage("Fetching table {0} data with property {1}", strTestData,
				strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			waitByTime(500);
			WebElement aTable = getWebElement(aKeyWord, strPropertyValue);
			List<WebElement> lstRows = aTable.findElements(By.tagName("tr"));
			LinkedHashMap<WebElement, String> mpTblData = new LinkedHashMap<>();
			for (WebElement aTblRow : lstRows) {
				mpTblData.put(aTblRow, testScenarioName);
				List<WebElement> lstColumns = aTblRow.findElements(By.tagName("td"));
				for (WebElement aColumn : lstColumns) {
					String strColumnText = aColumn.getText();
					if (StringUtils.isEmpty(StringUtils.trim(strColumnText))) {
						strColumnText = aColumn.getAttribute(ORConstants.ATTRIBUTE_NAME_VALUE);
					}
					if (StringUtils.isEmpty(StringUtils.trim(strColumnText))) {
						strColumnText = aColumn.getAttribute(ORConstants.ATTRIBUTE_NAME_ALT);
					}
					strColumnText = StringUtils.trim(strColumnText);
					mpTblData.put(aColumn, strColumnText);
				}
			}
			return mpTblData;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return null;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected int getTableRowByColumn(KeyWordConfigBean aKeyWord, String strPropertyValue, boolean bisTrict,
			String... strTestData) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue) || strTestData == null || strTestData.length <= 0) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Fetching row  by table column {0} with property {1}",
				Arrays.asList(strTestData), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aTable = getWebElement(aKeyWord, strPropertyValue);
			List<WebElement> lstRows = aTable.findElements(By.tagName("tr"));
			LinkedHashMap<Integer, LinkedList<String>> mpData = new LinkedHashMap<>();
			for (int i = 0; i < lstRows.size(); i++) {
				int iRow = i + 1;
				LinkedList<String> lstColumnData = new LinkedList<String>();
				WebElement aTblRow = lstRows.get(i);
				List<WebElement> lstColumns = aTblRow.findElements(By.tagName("td"));
				for (String strText : strTestData) {
					col_loop: for (WebElement aColumn : lstColumns) {
						String columnText = aColumn.getText();
						if (StringUtils.isEmpty(columnText)) {
							continue;
						}
						if (bisTrict && StringUtils.equalsIgnoreCase(columnText, strText)) {
							lstColumnData.add(strText);
							mpData.put(iRow, lstColumnData);
							LOGGER.info(AppUtils.formatMessage("Actual - {0} Expected - {1} data matched", strText,
									columnText));
							continue col_loop;
						}

						if (!bisTrict && StringUtils.containsIgnoreCase(columnText, strText)) {
							lstColumnData.add(strText);
							mpData.put(iRow, lstColumnData);
							LOGGER.info(AppUtils.formatMessage("Actual - {0} Expected - {1} data matched", strText,
									columnText));
							continue col_loop;
						}
						LOGGER.warn(AppUtils.formatMessage("Data not matched! Actual - {0} Expected - {1}", strText,
								columnText));
					}
				}
				LinkedList<String> lstData = mpData.get(iRow);
				if (CollectionUtils.isNotEmpty(lstData)
						&& CollectionUtils.isEqualCollection(lstData, Arrays.asList(strTestData))) {
					highLightWebElement(aTblRow, aKeyWord, ORConstants.YELLOW_COLOR);
					return iRow;
				}
			}
			return -1;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return -1;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected WebElement getTableByColumn(KeyWordConfigBean aKeyWord, String testScenarioName, String strPropertyValue,
			String strTestData, boolean isStrict) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(strTestData)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Fetching on table column {0} with property {1}", strTestData,
				strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aTable = getWebElement(aKeyWord, strPropertyValue);
			List<WebElement> lstRows = aTable.findElements(By.tagName("tr"));
			for (WebElement aTblRow : lstRows) {
				List<WebElement> lstColumns = aTblRow.findElements(By.tagName("td"));
				for (WebElement aColumn : lstColumns) {
					String columnText = aColumn.getText();
					columnText = StringUtils.trim(columnText);
					if (StringUtils.isEmpty(columnText)) {
						continue;
					}
					String strResult = verifyText(aColumn, aKeyWord, testScenarioName, columnText, strTestData,
							isStrict);
					if (!isTestSetpFailed(strResult)) {
						LOGGER.info(AppUtils.formatMessage("Actual - {0} Expected - {1} data matched", strTestData,
								columnText));
						return aColumn;
					}
				}
			}
			return null;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return null;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	/**
	 * Execution waits for seconds specified
	 * 
	 * @param iSeconds
	 * @return
	 * @throws Exception
	 */
	protected String delayInSeconds(int iSeconds) throws Exception {
		return waitByTime(iSeconds * 1000);
	}

	/**
	 * Execution waits for time{Milliseconds} specified
	 * 
	 * @param lTime
	 * @return
	 * @throws Exception
	 */
	protected String waitByTime(long lTime) throws Exception {
		if (lTime <= 0) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Test-Step Waiting for {0} milliseconds", lTime);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			Thread.sleep(lTime);
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String inputRobotText(KeyWordConfigBean aKeyWord, String strPropertyValue, String strTestData)
			throws Exception {
		if (StringUtils.isEmpty(strTestData)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		if (StringUtils.isEmpty(strPropertyValue)) {
			strPropertyValue = ErrorMsgConstants.ERR_DEFAULT;
		}
		String strLogMessage = AppUtils.formatMessage("Inputing  text {0} on {1} with property {2}", strTestData,
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			delayInSeconds(2);// Waiting time only to double check if env is slow
			RobotKeyboard aRobotKeyboard = new RobotKeyboard();
			aRobotKeyboard.type(strTestData);
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String inputText(KeyWordConfigBean aKeyWord, String strPropertyValue, String strTestData,
			boolean isStrictCheck) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(strTestData)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Inputing  text {0} on {1} with property {2}", strTestData,
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aWebElement = getWebElement(aKeyWord, strPropertyValue);
			if (aWebElement == null) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			clearInput(aWebElement);
			if (!isStrictCheck && aWebElement != null) {
				aWebElement.sendKeys(strTestData);
				return AppConstants.TEST_RESULT_PASS;
			}
			if (isStrictCheck && aWebElement != null && aWebElement.isDisplayed()) {
				aWebElement.sendKeys(strTestData);
				return AppConstants.TEST_RESULT_PASS;
			}
			return AppConstants.TEST_RESULT_FAIL;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String clearInput(WebElement aWebElement) throws Exception {
		if (aWebElement == null) {
			return AppConstants.TEST_RESULT_FAIL;
		}
//		aWebElement.click();
		Browsers aBrowsers = getBrowser();
		BrowsersConfigBean aBrowsersConfigBean = getBrowsersConfigBean();
		if (aBrowsersConfigBean.getPlatFormName().equalsIgnoreCase(AppConstants.PLATFORM_NAME_IOS)) {
			aWebElement.sendKeys(Keys.chord(Keys.CONTROL, Keys.SHIFT, Keys.HOME), Keys.DELETE,
					Keys.chord(Keys.CONTROL, Keys.SHIFT, Keys.END), Keys.DELETE);
		} else if (aBrowsers == Browsers.ANDROID_FIREFOX || aBrowsers == Browsers.WINDOWS_FIREFOX || StringUtils
				.equalsIgnoreCase(aBrowsersConfigBean.getBrowserName(), Browsers.WINDOWS_FIREFOX.getBrowserName())) {
			executeJavaScript(aWebElement, ORConstants.EXEC_JAVA_SCRIPT_CLEAR_CMD);
		} else {
			aWebElement.clear();
			aWebElement.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE, Keys.chord(Keys.SHIFT, Keys.END),
					Keys.BACK_SPACE);

//			aWebElement.sendKeys(Keys.SHIFT, Keys.HOME, Keys.DELETE, Keys.SHIFT, Keys.END, Keys.DELETE);
		}
		String strText = getWebElementText(aWebElement, false);
		return StringUtils.isEmpty(strText) ? AppConstants.TEST_RESULT_PASS : AppConstants.TEST_RESULT_FAIL;
	}

	protected String clearInput(KeyWordConfigBean aKeyWord, String strPropertyValue, String strTestData)
			throws Exception {
		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(strTestData)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Clearing text {0} on {1} with property {2}", strTestData,
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aWebElement = getWebElement(aKeyWord, strPropertyValue);
			if (aWebElement == null) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			return clearInput(aWebElement);
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String uploadFile(KeyWordConfigBean aKeyWord, String strPropertyValue, File aFile) throws Exception {
		if (aFile == null || !aFile.exists()) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Uploading file {0} to property {2}", aFile.getName(),
				strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aWebElement = getWebElement(aKeyWord, strPropertyValue);
			waitByTime(500);
			if (aWebElement.isDisplayed()) {
				waitByTime(1500);
				aWebElement.sendKeys(aFile.getAbsolutePath());
				return AppConstants.TEST_RESULT_PASS;
			}
			return AppConstants.TEST_RESULT_FAIL;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While Uploading file {0}", aFile.getName());
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	public String highLightWebElement(WebElement aWebElement, KeyWordConfigBean aKeyWord, String strBorderColor) {
		String strLogMessage = AppUtils.formatMessage("Highliting WebElement for {0}", aKeyWord.toString());
		try {
			if (aWebElement == null) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			String strHighliter = String.format(ORConstants.EXEC_HIGHLIGHT_CMD, strBorderColor);
			executeJavaScript(aWebElement, strHighliter);
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String getFormattedTestData(String testScenarioName, String strTestData) {
		LinkedHashMap<String, String> mpRunTimeBean = getRunTimeData(testScenarioName);
		for (Entry<String, String> entryRuntimeData : mpRunTimeBean.entrySet()) {
			String strKey = entryRuntimeData.getKey();
			String strValue = entryRuntimeData.getValue();
			String strSearchText = String.format("{%s}", strKey);
			if (!StringUtils.containsIgnoreCase(strTestData, strSearchText)) {
				continue;
			}
			strTestData = StringUtils.replaceIgnoreCase(strTestData, strSearchText, strValue);
		}
		return strTestData;
	}

	protected String verifyText(WebElement aWebElement, KeyWordConfigBean aKeyWord, String testScenarioName,
			String strSrcText, String strTestData, Boolean isStrict) {
		if (StringUtils.isEmpty(StringUtils.trim(testScenarioName)) || StringUtils.isEmpty(StringUtils.trim(strSrcText))
				|| StringUtils.isEmpty(StringUtils.trim(strTestData))) {
			LOGGER.error(
					AppUtils.formatMessage("Data not matched! Actual - {0} Expected - {1}", strSrcText, strTestData));
			return AppConstants.TEST_RESULT_FAIL;
		}

		strTestData = getFormattedTestData(testScenarioName, strTestData);

		if (isStrict == null && StringUtils.equals(strSrcText, strTestData)) {
			LOGGER.info(AppUtils.formatMessage("Actual - {0} Expected - {1} data matched", strSrcText, strTestData));
			highLightWebElement(aWebElement, aKeyWord, ORConstants.YELLOW_COLOR);
			return AppConstants.TEST_RESULT_PASS;
		}

		if (isStrict != null && isStrict && StringUtils.equalsIgnoreCase(strSrcText, strTestData)) {
			LOGGER.info(AppUtils.formatMessage("Actual - {0} Expected - {1} data matched", strSrcText, strTestData));
			highLightWebElement(aWebElement, aKeyWord, ORConstants.YELLOW_COLOR);
			return AppConstants.TEST_RESULT_PASS;
		}

		if (isStrict != null && !isStrict && StringUtils.containsIgnoreCase(strSrcText, strTestData)) {
			LOGGER.info(AppUtils.formatMessage("Actual - {0} Expected - {1} data matched", strSrcText, strTestData));
			highLightWebElement(aWebElement, aKeyWord, ORConstants.YELLOW_COLOR);
			return AppConstants.TEST_RESULT_PASS;
		}

		LOGGER.error(AppUtils.formatMessage("Data not matched! Actual - {0} Expected - {1}", strSrcText, strTestData));
		return AppConstants.TEST_RESULT_FAIL;
	}

	protected String verifyText(KeyWordConfigBean aKeyWord, String testScenarioName, String strPropertyValue,
			String strTestData, Boolean isStrict) throws Exception {

		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(StringUtils.trim(strTestData))) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Verify text {0} on {1} with property {2}", strTestData,
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aWebElement = getWebElement(aKeyWord, strPropertyValue);
			if (aWebElement == null) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			waitByTime(500);
			String strText = getWebElementText(aWebElement, isStrict == null ? false : isStrict);
			strText = StringUtils.trim(strText);
			strTestData = StringUtils.trim(strTestData);
			String[] strVerifyTextData = StringUtils.split(strTestData, AppConstants.SEPARATOR_CAP);
			for (String strVerifyText : strVerifyTextData) {
				strVerifyText = StringUtils.trim(strVerifyText);
				String strResult = verifyText(aWebElement, aKeyWord, testScenarioName, strText, strVerifyText,
						isStrict);
				if (isTestSetpFailed(strResult)) {
					return strResult;
				}
			}
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String verifyDropDownText(KeyWordConfigBean aKeyWord, String strPropertyValue, String testScenarioName,
			String strTestData, boolean isStrict) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(strTestData)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Verify Dropdown text {0} on {1} with property {2}", strTestData,
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aWebElement = getWebElement(aKeyWord, strPropertyValue);
			waitByTime(500);
			if (aWebElement == null) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			Select aDropDwon = new Select(aWebElement);
			List<WebElement> lstWebElements = aDropDwon.getOptions();
			if (lstWebElements == null) {
				return verifyText(null, aKeyWord, testScenarioName, null, strTestData, isStrict);
			}
			for (WebElement aDropDownWebElement : lstWebElements) {
				String strText = aDropDownWebElement.getText();
				if (StringUtils.isEmpty(StringUtils.trim(strText))) {
					strText = aDropDownWebElement.getAttribute(ORConstants.ATTRIBUTE_NAME_VALUE);
				}
				if (StringUtils.isEmpty(StringUtils.trim(strText))) {
					strText = aDropDownWebElement.getAttribute(ORConstants.ATTRIBUTE_NAME_ALT);
				}
				strText = StringUtils.trim(strText);
				strTestData = StringUtils.trim(strTestData);
				String strStatus = verifyText(aDropDownWebElement, aKeyWord, testScenarioName, strText, strTestData,
						isStrict);
				if (StringUtils.equalsIgnoreCase(strStatus, AppConstants.TEST_RESULT_PASS)) {
					return strStatus;
				}
			}
			return AppConstants.TEST_RESULT_FAIL;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String verifyBackgroundColor(KeyWordConfigBean aKeyWord, String testScenarioName, String strPropertyValue,
			String strTestData, boolean isStrict) throws Exception {

		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(StringUtils.trim(strTestData))) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Verify text {0} on {1} with property {2}", strTestData,
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aWebElement = getWebElement(aKeyWord, strPropertyValue);
			if (aWebElement == null) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			waitByTime(500);
			String strText = getWebElementColorByCCSValue(aWebElement, ORConstants.CCS_VALUE_BACKGROUND_COLOUR);
			if (StringUtils.isEmpty(StringUtils.trim(strText))) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			strText = StringUtils.trim(strText);
			strTestData = StringUtils.trim(strTestData);
			return verifyText(aWebElement, aKeyWord, testScenarioName, strText, strTestData, isStrict);
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String verifyTextColor(KeyWordConfigBean aKeyWord, String testScenarioName, String strPropertyValue,
			String strTestData, boolean isStrict) throws Exception {

		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(StringUtils.trim(strTestData))) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Verify text {0} on {1} with property {2}", strTestData,
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aWebElement = getWebElement(aKeyWord, strPropertyValue);
			if (aWebElement == null) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			waitByTime(500);
			String strText = getWebElementColorByCCSValue(aWebElement, ORConstants.CCS_VALUE_COLOUR);
			strText = StringUtils.trim(strText);
			if (StringUtils.isEmpty(StringUtils.trim(strText))) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			strTestData = StringUtils.trim(strTestData);
			return verifyText(aWebElement, aKeyWord, testScenarioName, strText, strTestData, isStrict);
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String verifyTextSize(KeyWordConfigBean aKeyWord, String testScenarioName, String strPropertyValue,
			String strTestData, boolean isStrict) throws Exception {

		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(StringUtils.trim(strTestData))) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Verify text {0} on {1} with property {2}", strTestData,
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aWebElement = getWebElement(aKeyWord, strPropertyValue);
			if (aWebElement == null) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			waitByTime(500);
			String textSize = aWebElement.getCssValue("font-size");
			textSize = StringUtils.trim(textSize);
			strTestData = StringUtils.trim(strTestData);
			return verifyText(aWebElement, aKeyWord, testScenarioName, textSize, strTestData, isStrict);
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	public String getWebElementColorByCCSValue(WebElement aWebElement, String strCssValue) throws Exception {
		if (StringUtils.isEmpty(StringUtils.trim(strCssValue))) {
			return null;
		}
		String color = aWebElement.getCssValue(strCssValue);
		if (StringUtils.isEmpty(StringUtils.trim(color))) {
			return null;
		}
		String[] numbers = color.replace("rgba(", "").replace(")", "").split(",");
		int r = Integer.parseInt(numbers[0].trim());
		int g = Integer.parseInt(numbers[1].trim());
		int b = Integer.parseInt(numbers[2].trim());
		String hex = String.format("#%02x%02x%02x", r, g, b);
		return hex;
	}

	public String getRandomNameWithSpecificSize(String rndName, int charSize) {

		StringBuffer sb = new StringBuffer(rndName);
		if (rndName.length() == charSize) {
			return rndName;
		}
		if (rndName.length() > charSize) {
			sb.setLength(charSize);
			return sb.toString();
		}
		if (rndName.length() < charSize) {
			sb.append("llllllll").setLength(charSize);
			return sb.toString();
		}
		return rndName;
	}

	public String getRandomText(Browsers aBrowser, String testScenarioName, String strReportKeyWord, int charSize) {
		String strLogMessage = AppUtils.formatMessage("Generating random text for {0}", strReportKeyWord);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			Faker aFaker = new Faker(Locale.UK);
			AppContext aPPContext = getApplicationContext();
			if (StringUtils.equalsIgnoreCase(strReportKeyWord, RunTimeDataConstants.CLIENT_EMAIL)) {
				String strEmail = aFaker.internet().emailAddress();
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), RunTimeDataConstants.CLIENT_EMAIL,
						strEmail);
				aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						RunTimeDataConstants.CLIENT_EMAIL, strEmail);
				return strEmail;
			}

			if (StringUtils.equalsIgnoreCase(strReportKeyWord, RunTimeDataConstants.PATNER_EMAIL)) {
				String strEmail = aFaker.internet().emailAddress();
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), RunTimeDataConstants.PATNER_EMAIL,
						strEmail);
				aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						RunTimeDataConstants.PATNER_EMAIL, strEmail);
				return strEmail;
			}

			if (StringUtils.equalsIgnoreCase(strReportKeyWord, RunTimeDataConstants.THIRDPARTY_EMAIL)) {
				String strEmail = aFaker.internet().emailAddress();
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
						RunTimeDataConstants.THIRDPARTY_EMAIL, strEmail);
				aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						RunTimeDataConstants.THIRDPARTY_EMAIL, strEmail);
				return strEmail;
			}

			if (!StringUtils.containsIgnoreCase(strReportKeyWord, RunTimeDataConstants.JOINTLIFE_FIRST_NAME)
					&& !StringUtils.containsIgnoreCase(strReportKeyWord, RunTimeDataConstants.THIRDPARTY_FIRST_NAME)
					&& StringUtils.containsIgnoreCase(strReportKeyWord, RunTimeDataConstants.FIRST_NAME)) {
				String strFirstName = aFaker.name().firstName();
				strFirstName = getRandomNameWithSpecificSize(strFirstName, charSize);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), RunTimeDataConstants.FIRST_NAME,
						strFirstName);
				aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						RunTimeDataConstants.FIRST_NAME, strFirstName);
				if (StringUtils.isEmpty(getRunTimeDataValue(testScenarioName, RunTimeDataConstants.LAST_NAME))) {
					aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), RunTimeDataConstants.LAST_NAME,
							null);
					aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.LAST_NAME, null);
				}
				if (StringUtils
						.isEmpty(getRunTimeDataValue(testScenarioName, RunTimeDataConstants.JOINTLIFE_FIRST_NAME))) {
					aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.JOINTLIFE_FIRST_NAME, null);
					aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.JOINTLIFE_FIRST_NAME, null);
				}
				if (StringUtils
						.isEmpty(getRunTimeDataValue(testScenarioName, RunTimeDataConstants.JOINTLIFE_LAST_NAME))) {
					aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.JOINTLIFE_LAST_NAME, null);
					aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.JOINTLIFE_LAST_NAME, null);
				}
				return strFirstName;
			}

			if (!StringUtils.containsIgnoreCase(strReportKeyWord, RunTimeDataConstants.JOINTLIFE_LAST_NAME)
					&& !StringUtils.containsIgnoreCase(strReportKeyWord, RunTimeDataConstants.THIRDPARTY_LAST_NAME)
					&& (StringUtils.containsIgnoreCase(strReportKeyWord, RunTimeDataConstants.LAST_NAME)
							|| StringUtils.containsIgnoreCase(strReportKeyWord, RunTimeDataConstants.SURNAME))) {
				String strLastName = aFaker.name().lastName();
				strLastName = getRandomNameWithSpecificSize(strLastName, charSize);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), RunTimeDataConstants.LAST_NAME,
						strLastName);
				aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						RunTimeDataConstants.LAST_NAME, strLastName);
				if (StringUtils.isEmpty(getRunTimeDataValue(testScenarioName, RunTimeDataConstants.FIRST_NAME))) {
					aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.FIRST_NAME, null);
					aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.FIRST_NAME, null);
				}
				if (StringUtils
						.isEmpty(getRunTimeDataValue(testScenarioName, RunTimeDataConstants.JOINTLIFE_FIRST_NAME))) {
					aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.JOINTLIFE_FIRST_NAME, null);
					aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.JOINTLIFE_FIRST_NAME, null);
				}
				if (StringUtils
						.isEmpty(getRunTimeDataValue(testScenarioName, RunTimeDataConstants.JOINTLIFE_LAST_NAME))) {
					aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.JOINTLIFE_LAST_NAME, null);
					aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.JOINTLIFE_LAST_NAME, null);
				}
				return strLastName;
			}
			if (StringUtils.containsIgnoreCase(strReportKeyWord, RunTimeDataConstants.JOINTLIFE_FIRST_NAME)) {
				String strFirstName = aFaker.name().firstName();
				strFirstName = getRandomNameWithSpecificSize(strFirstName, charSize);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
						RunTimeDataConstants.JOINTLIFE_FIRST_NAME, strFirstName);
				aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						RunTimeDataConstants.JOINTLIFE_FIRST_NAME, strFirstName);

				if (StringUtils.isEmpty(getRunTimeDataValue(testScenarioName, RunTimeDataConstants.FIRST_NAME))) {
					aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.FIRST_NAME, null);
					aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.FIRST_NAME, null);
				}
				if (StringUtils.isEmpty(getRunTimeDataValue(testScenarioName, RunTimeDataConstants.LAST_NAME))) {
					aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), RunTimeDataConstants.LAST_NAME,
							null);
					aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.LAST_NAME, null);
				}
				if (StringUtils
						.isEmpty(getRunTimeDataValue(testScenarioName, RunTimeDataConstants.JOINTLIFE_LAST_NAME))) {
					aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.JOINTLIFE_LAST_NAME, null);
					aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.JOINTLIFE_LAST_NAME, null);
				}

				return strFirstName;
			}
			if (StringUtils.containsIgnoreCase(strReportKeyWord, RunTimeDataConstants.JOINTLIFE_LAST_NAME)) {
				String strLastName = aFaker.name().lastName();
				strLastName = getRandomNameWithSpecificSize(strLastName, charSize);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
						RunTimeDataConstants.JOINTLIFE_LAST_NAME, strLastName);
				aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						RunTimeDataConstants.JOINTLIFE_LAST_NAME, strLastName);
				if (StringUtils.isEmpty(getRunTimeDataValue(testScenarioName, RunTimeDataConstants.FIRST_NAME))) {
					aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.FIRST_NAME, null);
					aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.FIRST_NAME, null);
				}
				if (StringUtils.isEmpty(getRunTimeDataValue(testScenarioName, RunTimeDataConstants.LAST_NAME))) {
					aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), RunTimeDataConstants.LAST_NAME,
							null);
					aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.LAST_NAME, null);
				}
				if (StringUtils
						.isEmpty(getRunTimeDataValue(testScenarioName, RunTimeDataConstants.JOINTLIFE_FIRST_NAME))) {
					aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.JOINTLIFE_FIRST_NAME, null);
					aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
							RunTimeDataConstants.JOINTLIFE_FIRST_NAME, null);
				}
				return strLastName;
			}
			if (StringUtils.containsIgnoreCase(strReportKeyWord, RunTimeDataConstants.THIRDPARTY_FIRST_NAME)) {
				String strFirstName = aFaker.name().firstName();
				strFirstName = getRandomNameWithSpecificSize(strFirstName, charSize);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
						RunTimeDataConstants.THIRDPARTY_FIRST_NAME, strFirstName);
				return strFirstName;
			}
			if (StringUtils.containsIgnoreCase(strReportKeyWord, RunTimeDataConstants.THIRDPARTY_LAST_NAME)) {
				String strLastName = aFaker.name().lastName();
				strLastName = getRandomNameWithSpecificSize(strLastName, charSize);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
						RunTimeDataConstants.THIRDPARTY_LAST_NAME, strLastName);
				return strLastName;
			}
			if (StringUtils.containsIgnoreCase(strReportKeyWord, RunTimeDataConstants.FIRST_LIFE_FULL_NAME)) {
				String strFirstName = aFaker.name().firstName();
				strFirstName = getRandomNameWithSpecificSize(strFirstName, charSize);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), RunTimeDataConstants.FIRST_NAME,
						strFirstName);
				String strLastName = aFaker.name().lastName();
				strLastName = getRandomNameWithSpecificSize(strLastName, charSize);
				String strFirstLifeFullName = strFirstName + " " + strLastName;
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), RunTimeDataConstants.LAST_NAME,
						strFirstName);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
						RunTimeDataConstants.FIRST_LIFE_FULL_NAME, strFirstLifeFullName);
				return strFirstLifeFullName;
			}

			if (StringUtils.containsIgnoreCase(strReportKeyWord, RunTimeDataConstants.JOINT_LIFE_FULL_NAME)) {
				String strFirstName = aFaker.name().firstName();
				strFirstName = getRandomNameWithSpecificSize(strFirstName, charSize);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
						RunTimeDataConstants.JOINTLIFE_FIRST_NAME, strFirstName);
				String strLastName = aFaker.name().lastName();
				strLastName = getRandomNameWithSpecificSize(strLastName, charSize);
				String strJointLifeFullName = strFirstName + " " + strLastName;
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
						RunTimeDataConstants.JOINTLIFE_LAST_NAME, strFirstName);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(),
						RunTimeDataConstants.JOINT_LIFE_FULL_NAME, strJointLifeFullName);
				return strJointLifeFullName;
			}

			return null;
		} catch (Throwable ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return null;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	// function to generate a random string of length n
	protected String getRandomAlphaNumericString(int iLength) {
		RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder().withinRange('0', 'z')
				.filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS).build();
		return randomStringGenerator.generate(iLength);
	}

	// function to generate a random number of length 6 digits
	protected String getRandomNumber() {
		Random rnd = new Random();
		String randomNumber = Integer.toString(rnd.nextInt(1000000));
		return randomNumber;
	}

	protected String sendKeys(KeyWordConfigBean aKeyWord, String strPropertyValue, CharSequence... aKeysToSend)
			throws Exception {
		if (aKeysToSend == null) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		StringBuilder aMessageKeys = new StringBuilder();
		for (CharSequence aKeys : aKeysToSend) {
			aMessageKeys.append(aKeys.toString()).append(AppConstants.SEPARATOR_COMMA);
		}
		String strKeys = aMessageKeys.toString();
		if (strKeys.endsWith(AppConstants.SEPARATOR_COMMA)) {
			strKeys = strKeys.substring(0, strKeys.length() - AppConstants.SEPARATOR_COMMA.length());
		}
		String strLogMessage = AppUtils.formatMessage("Sending keys {0} with property {1}", strKeys, strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			if (StringUtils.isEmpty(strPropertyValue)) {
				WebDriver aDriver = getWebDriver();
				Actions aSendKeysAction = new Actions(aDriver);
				aSendKeysAction.sendKeys(aKeysToSend).build().perform();
			} else {
				WebElement aWebElement = getWebElement(aKeyWord, strPropertyValue);
				if (aWebElement == null) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				aWebElement.sendKeys(aKeysToSend);
			}
			waitByTime(500);
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected Object executeJavaScript(WebElement aWebElement, String strJSCmd, Object... args) throws Exception {
		if (aWebElement == null || StringUtils.isEmpty(strJSCmd)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Executing JS CMD {0}", strJSCmd);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebDriver aDriver = getWebDriver();
			return ((JavascriptExecutor) aDriver).executeScript(strJSCmd, aWebElement, args);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String executeJavaScript(KeyWordConfigBean aKeyWord, String strPropertyValue, String strJSCmd,
			boolean isStrictCheck, Object... args) throws Exception {
		if ((isStrictCheck && StringUtils.isEmpty(strPropertyValue)) || StringUtils.isEmpty(strJSCmd)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Executing Java Script CMD {0} for {1} with property {2}",
				strJSCmd, aKeyWord.toString(), strPropertyValue);
		try {
			WebDriver aDriver = getWebDriver();
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			delayInSeconds(1);

			if (!isStrictCheck) {
				((JavascriptExecutor) aDriver).executeScript(strJSCmd, args);
				delayInSeconds(5);
				return AppConstants.TEST_RESULT_PASS;
			}

			WebElement aElement = getWebElement(aKeyWord, strPropertyValue);
			if (isStrictCheck && aElement != null) {
				executeJavaScript(aElement, strJSCmd, args);
				return AppConstants.TEST_RESULT_PASS;
			}
			return AppConstants.TEST_RESULT_FAIL;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected Object[] getOpenedTabs() {
		String strLogMessage = AppUtils.formatMessage("Fetching all window in browser {0}",
				getBrowser().getBrowserName());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebDriver aDriver = getWebDriver();
			return aDriver.getWindowHandles().toArray();
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String switchToParentWindow() {
		String strLogMessage = AppUtils.formatMessage("Navigating to parent window in browser {0}",
				getBrowser().getBrowserName());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebDriver aDriver = getWebDriver();
			Object strParentWins[] = getOpenedTabs();
			aDriver.switchTo().window((String) strParentWins[0]);
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String switchToNewWindow(boolean bMazimize) {
		String strLogMessage = AppUtils.formatMessage("Navigating to window in browser {0}",
				getBrowser().getBrowserName());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebDriver aDriver = getWebDriver();
			Object strWins[] = getOpenedTabs();
			for (int i = 1; i < strWins.length; i++) {
				String strWindow = (String) strWins[i];
				aDriver.switchTo().window(strWindow).navigate();
			}
			if (bMazimize) {
				try {
					aDriver.manage().window().maximize();
				} catch (Exception ex) {
				}
			}
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String closeWindow() {

		String strLogMessage = AppUtils.formatMessage("Closing all window in browser {0}",
				getBrowser().getBrowserName());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebDriver aDriver = getWebDriver();
			aDriver.close();
			Object strWin[] = getOpenedTabs();
			for (int i = 1; i < strWin.length; i++) {
				aDriver.switchTo().window((String) strWin[i]).navigate();
			}
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String closeAllWindows() {
		String strLogMessage = AppUtils.formatMessage("Closing all windows in browser {0}",
				getBrowser().getBrowserName());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebDriver aDriver = getWebDriver();
			Object strWin[] = getOpenedTabs();
			for (int i = 1; i < strWin.length; i++) {
				aDriver.switchTo().window((String) strWin[i]).close();
			}
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String getAttributesByNames(KeyWordConfigBean aKeyWord, String strPropertyValue, String strAttributeName)
			throws Exception {
		if (StringUtils.isEmpty(strPropertyValue)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Fetching  Attribute {0} {1} with property {2}", strAttributeName,
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aWebElement = getWebElement(aKeyWord, strPropertyValue);
			waitByTime(500);
			String strText = null;
			if (aWebElement != null && aWebElement.isDisplayed()) {
				strText = aWebElement.getAttribute(strAttributeName);
				strText = StringUtils.trim(strText);
			}
			return strText;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return null;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}

	}

	protected String verifyAttributesByNames(KeyWordConfigBean aKeyWord, String testScenarioName,
			String strPropertyValue, String strTestData, String strAttributeName) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(strTestData)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Verify text {0} on Attribute {1} {2} with property {3}",
				strTestData, strAttributeName, aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aWebElement = getWebElement(aKeyWord, strPropertyValue);
			waitByTime(500);
			String strText = null;
			if (aWebElement != null && aWebElement.isDisplayed()) {
				strText = aWebElement.getAttribute(strAttributeName);
			}
			strText = StringUtils.trim(strText);
			strTestData = StringUtils.trim(strTestData);
			return verifyText(aWebElement, aKeyWord, testScenarioName, strText, strTestData, true);
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	private Actions getWebFocusAction(WebElement aWebElement) throws Exception {
		WebDriver aDriver = getWebDriver();
		Actions aFocus = new Actions(aDriver);
		aFocus.moveToElement(aWebElement).build().perform();
		delayInSeconds(2);
		return aFocus;
	}

	protected Actions getWebFocusAction(KeyWordConfigBean aKeyWord, String strPropertyValue, boolean isStrictCheck)
			throws Exception {
		if (StringUtils.isEmpty(strPropertyValue)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Fetching web action {0} with property {1}", aKeyWord.toString(),
				strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aWebElement = getWebElement(aKeyWord, strPropertyValue);
			if (aWebElement == null) {
				return null;
			}
			if (!isStrictCheck) {
				return getWebFocusAction(aWebElement);
			}
			if (isStrictCheck && aWebElement.isDisplayed()) {
				return getWebFocusAction(aWebElement);
			}
			return null;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return null;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String performControlClick(KeyWordConfigBean aKeyWord, String strPropertyValue) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Performing web focus click action {0} with property {1}",
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aWebElement = getClickWebElement(aKeyWord, strPropertyValue);
			if (aWebElement == null || !aWebElement.isDisplayed()) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			WebDriver aDriver = getWebDriver();
			if (aDriver instanceof AppiumDriver<?>) {
				AppiumDriver<?> aAppiumDriver = (AppiumDriver<?>) aDriver;
				ElementOption aAppiumElement = new ElementOption().withElement(aWebElement);
				TapOptions aTapOptions = new TapOptions().withElement(aAppiumElement);
				TouchAction<?> aFocus = new TouchAction<>(aAppiumDriver);
				aFocus.tap(aTapOptions).perform();
				return AppConstants.TEST_RESULT_PASS;
			} else {
				Actions aFocus = getWebFocusAction(aWebElement);
				if (aFocus == null) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				aFocus.keyDown(Keys.CONTROL).click(aWebElement).keyUp(Keys.CONTROL).perform();
				delayInSeconds(3);
				return AppConstants.TEST_RESULT_PASS;
			}
		} catch (Exception ex) {
			return clickWebElement(aKeyWord, strPropertyValue, false);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String performFocusClick(KeyWordConfigBean aKeyWord, String strPropertyValue) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Performing web focus click action {0} with property {1}",
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aWebElement = getClickWebElement(aKeyWord, strPropertyValue);
			if (aWebElement == null || !aWebElement.isDisplayed()) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			WebDriver aDriver = getWebDriver();
			if (aDriver instanceof AppiumDriver<?>) {
				AppiumDriver<?> aAppiumDriver = (AppiumDriver<?>) aDriver;
				ElementOption aAppiumElement = new ElementOption().withElement(aWebElement);
				TapOptions aTapOptions = new TapOptions().withElement(aAppiumElement);
				TouchAction<?> aFocus = new TouchAction<>(aAppiumDriver);
				aFocus.tap(aTapOptions).perform();
				return AppConstants.TEST_RESULT_PASS;
			} else {
				Actions aFocus = getWebFocusAction(aWebElement);
				if (aFocus == null) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				aWebElement.click();
				delayInSeconds(3);
				return AppConstants.TEST_RESULT_PASS;
			}
		} catch (Exception ex) {
			return clickWebElement(aKeyWord, strPropertyValue, false);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String performFocusSendKeys(KeyWordConfigBean aKeyWord, String strPropertyValue, boolean isStrictCheck,
			CharSequence... aKeysToSend) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue) || aKeysToSend == null) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}

		StringBuilder aMessageKeys = new StringBuilder();
		for (CharSequence aKeys : aKeysToSend) {
			aMessageKeys.append(aKeys.toString()).append(AppConstants.SEPARATOR_COMMA);
		}
		String strKeys = aMessageKeys.toString();
		if (strKeys.endsWith(AppConstants.SEPARATOR_COMMA)) {
			strKeys = strKeys.substring(0, strKeys.length() - AppConstants.SEPARATOR_COMMA.length());
		}

		String strLogMessage = AppUtils.formatMessage(
				"Performing web focus click action {0} Sending keys {1} with property {2}", strKeys, aKeysToSend,
				strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			Actions aFocus = getWebFocusAction(aKeyWord, strPropertyValue, isStrictCheck);
			if (aFocus == null) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			aFocus.sendKeys(aKeysToSend);
			delayInSeconds(3);
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected List<WebElement> getWebElements(KeyWordConfigBean aKeyWord, String strPropertyValue) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Fetching on web elements for {0} with property {1}",
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			List<WebElement> lstWebElemts = getWebElements(aKeyWord.getKeyWorkType(), strPropertyValue);
			return lstWebElemts;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return new ArrayList<>();
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String clickClientOccupation(KeyWordConfigBean aKeyWord, String strTestData) throws Exception {
		if (StringUtils.isEmpty(strTestData)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strPropertyValue = String.format(ORConstants.OCCUPATION_TEXT_XPATH, strTestData);
		String strLogMessage = AppUtils.formatMessage("Cilcking on client occupation {0} with property {1}",
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			waitByTime(500);
			return clickWebElement(aKeyWord, strPropertyValue, false);
		} catch (Exception ex) {
			String strErrorMsg = "WebElement is not available in page.";
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String clickByTextWebElement(KeyWordConfigBean aKeyWord, String strPropertyValue, String strTestData,
			boolean isStrictCheck) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(strTestData)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Cilcking on {0} with property {1}", aKeyWord.toString(),
				strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aWebElement = getClickWebElement(aKeyWord, strPropertyValue);
			if (aWebElement == null || !StringUtils.containsIgnoreCase(aWebElement.getText(), strTestData)) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			if (isStrictCheck && aWebElement.isDisplayed()) {
				aWebElement.click();
				waitByTime(500);
				return AppConstants.TEST_RESULT_PASS;
			}
			if (!isStrictCheck && aWebElement != null) {
				aWebElement.click();
				waitByTime(500);
				return AppConstants.TEST_RESULT_PASS;
			}
			return AppConstants.TEST_RESULT_FAIL;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String getWebElementText(KeyWordConfigBean aKeyWord, String strPropertyValue, boolean isStrictCheck)
			throws Exception {
		if (StringUtils.isEmpty(strPropertyValue)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Fetching {0} values with property {1}", aKeyWord.toString(),
				strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aWebElement = getWebElement(aKeyWord, strPropertyValue);
			return getWebElementText(aWebElement, isStrictCheck);
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return null;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String getWebElementText(WebElement aWebElement, boolean isStrictCheck) {
		if (aWebElement == null) {
			return null;
		}
		String strText = aWebElement.getText();
		if (StringUtils.isEmpty(StringUtils.trim(strText))) {
			strText = aWebElement.getAttribute(ORConstants.ATTRIBUTE_NAME_VALUE);
		}
		if (StringUtils.isEmpty(StringUtils.trim(strText))) {
			strText = aWebElement.getAttribute(ORConstants.ATTRIBUTE_NAME_ALT);
		}
		if (StringUtils.isEmpty(StringUtils.trim(strText))) {
			strText = aWebElement.getAttribute(ORConstants.ATTRIBUTE_TOOLTIP_VALUE);
		}
		strText = StringUtils.trim(strText);
		if (!isStrictCheck) {
			return strText;
		}
		if (isStrictCheck && aWebElement.isDisplayed()) {
			return strText;
		}
		return null;
	}

	protected String captureWebElemetByText(String testScenarioName, KeyWordConfigBean aKeyWord, String strTestData,
			String strPropertyValue, String strReportKeyWord) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(strReportKeyWord)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Capturing Web Element {0} on {1} with property {2}",
				strReportKeyWord, aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			AppContext aAppContext = getApplicationContext();
			String strText = getWebElementText(aKeyWord, strPropertyValue, false);
			if (StringUtils.isEmpty(strText)) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			if (!StringUtils.isEmpty(strTestData)
					&& !StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_TRUE)) {
				String[] strSplitData = StringUtils.split(strTestData, AppConstants.SEPARATOR_CAP);
				String strModifiedText;
				if (strSplitData != null && strSplitData.length == 2) {
					strModifiedText = StringUtils.substringBetween(strText, strSplitData[0], strSplitData[1]);
				} else {
					strModifiedText = StringUtils.substringAfterLast(strText, strTestData);
					if (StringUtils.isEmpty(strModifiedText)) {
						strModifiedText = StringUtils.substringBeforeLast(strText, strTestData);
					}
				}
				if (StringUtils.isEmpty(strModifiedText)) {
					strModifiedText = strText;
				}
				strText = StringUtils.trim(strModifiedText);
			}
			aAppContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), strReportKeyWord, strText);
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String selectByText(KeyWordConfigBean aKeyWord, String strPropertyValue, String strTestData,
			boolean isStrictCheck) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(strTestData)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Selecting Web Element {0} on {1} with property {2}", strTestData,
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			delayInSeconds(1);
			WebElement aWebElement = getWebElement(aKeyWord, strPropertyValue);
			if (aWebElement == null) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			Select aDropDwon = new Select(aWebElement);
			List<WebElement> lstWebElements = aDropDwon.getOptions();
			WebElement aSelectedElement = lstWebElements.stream()
					.filter(aDropDownWebElement -> StringUtils.equalsIgnoreCase(
							getWebElementText(aDropDownWebElement, isStrictCheck), StringUtils.trim(strTestData)))
					.findFirst().orElse(null);
			if (aSelectedElement == null) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			String strDropDownText = getWebElementText(aSelectedElement, isStrictCheck);
			if (!isStrictCheck) {
				aDropDwon.selectByVisibleText(strDropDownText);
				delayInSeconds(1);
				return AppConstants.TEST_RESULT_PASS;
			}

			if (isStrictCheck && aWebElement.isDisplayed()) {
				aDropDwon.selectByVisibleText(strDropDownText);
				delayInSeconds(1);
				return AppConstants.TEST_RESULT_PASS;
			}
			return AppConstants.TEST_RESULT_FAIL;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String selectByTextNot(KeyWordConfigBean aKeyWord, String strPropertyValue, String strTestData,
			boolean isStrictCheck) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(strTestData)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Selecting Web Element {0} on {1} with property {2}", strTestData,
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			delayInSeconds(1);
			WebElement aWebElement = getWebElement(aKeyWord, strPropertyValue);
			if (aWebElement == null) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			Select aDropDwon = new Select(aWebElement);
			List<WebElement> lstWebElements = aDropDwon.getOptions();
			WebElement aSelectedElement = lstWebElements.stream()
					.filter(aDropDownWebElement -> StringUtils.equalsIgnoreCase(
							getWebElementText(aDropDownWebElement, isStrictCheck), StringUtils.trim(strTestData)))
					.findFirst().orElse(null);
			if (aSelectedElement == null) {
				return AppConstants.TEST_RESULT_PASS;
			}
			String strDropDownText = getWebElementText(aSelectedElement, isStrictCheck);
			if (isStrictCheck) {
				aDropDwon.selectByVisibleText(strDropDownText);
				delayInSeconds(1);
				return AppConstants.TEST_RESULT_PASS;
			}
			return AppConstants.TEST_RESULT_FAIL;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String selectByIndex(KeyWordConfigBean aKeyWord, String strPropertyValue, int iIndex,
			boolean isStrictCheck) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue) || iIndex < 0) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Selecting Web Element {0} on {1} with property {2}", iIndex,
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aWebElement = getWebElement(aKeyWord, strPropertyValue);
			waitByTime(1000);
			if (aWebElement == null) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			if (!isStrictCheck) {
				new Select(aWebElement).selectByIndex(iIndex);
				return AppConstants.TEST_RESULT_PASS;
			}

			if (isStrictCheck && aWebElement.isDisplayed()) {
				new Select(aWebElement).selectByIndex(iIndex);
				return AppConstants.TEST_RESULT_PASS;
			}
			return AppConstants.TEST_RESULT_FAIL;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String mWords(KeyWordConfigBean aKeyWord, String strPropertyValue) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Peroforming {0} with property {1}", aKeyWord.toString(),
				strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			String strFirstLetter = getWebElementText(aKeyWord, String.format(ORConstants.LBL_MEMORABLE_WORD_XPATH, 1),
					false);
			String strSecondLetter = getWebElementText(aKeyWord, String.format(ORConstants.LBL_MEMORABLE_WORD_XPATH, 2),
					false);
			String strThirdLetter = getWebElementText(aKeyWord, String.format(ORConstants.LBL_MEMORABLE_WORD_XPATH, 3),
					false);
			String strTotalNumbers = strFirstLetter + strSecondLetter + strThirdLetter;
			for (int i = 0; i < 7; i = i + 3) {
				int iPasswordletter = Character.getNumericValue(strTotalNumbers.charAt(i));
				int iIndex = i + 1;
				String strDynamicXPath = String.format(ORConstants.LBL_MEMORABLE_WORD_XPATH, iIndex >= 3 ? 3 : iIndex);
				if (iPasswordletter >= 1 && iPasswordletter <= 9) {
					sendKeys(aKeyWord, strDynamicXPath, "a");
				}
			}
			String strPerformSendKeys = performFocusSendKeys(aKeyWord, ORConstants.INPUT_CONFIRM_XPATH, false,
					Keys.ENTER);
			if (isTestSetpFailed(strPerformSendKeys)) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			WebElement aDivHeadingElement = getWebElement(aKeyWord, ORConstants.DIV_HEADING_XPATH);
			if (aDivHeadingElement == null) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String getFormattedQuoteSummarryResults(String strText) {
		if (StringUtils.isEmpty(StringUtils.trim(strText))) {
			return strText;
		}
		strText = StringUtils.replace(strText, "£", "");
		strText = RegExUtils.replacePattern(strText, "[a-z A-Z]", "");
		strText = StringUtils.trim(strText);
		return strText;
	}

	protected String captureQuotationSummary(KeyWordConfigBean aKeyWord, String testScenarioName, String strTestData)
			throws Exception {
		if (StringUtils.isEmpty(testScenarioName) || StringUtils.isEmpty(strTestData)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Capturing Quotation Summary for {0} with data {1}",
				aKeyWord.toString(), strTestData);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			String strNOOFPolcies = getWebElementText(aKeyWord, ORConstants.LBL_QUOTE_SUMMARY_NO_POLICIES_XPATH, false);
			if (StringUtils.isEmpty(strNOOFPolcies)) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			String strPolicyFormID = StringUtils.equalsIgnoreCase(strNOOFPolcies, "1") ? "" : "[1]";

			String strApplicationReference = getWebElementText(aKeyWord,
					ORConstants.DIV_QUOTE_SUMMARY_APPLICATION_SUMMARY_XPATH, false);
			if (StringUtils.isEmpty(strApplicationReference)) {
				return AppConstants.TEST_RESULT_FAIL;
			}

			String strQuoteReference = getWebElementText(aKeyWord,
					ORConstants.DIV_QUOTE_SUMMARY_QUOTE_APP_REFERENCE_XPATH, false);
			if (StringUtils.isEmpty(strQuoteReference)) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			String strQuoteNAppRef = String.format("%s||%s", strQuoteReference, strApplicationReference);
			AppContext aPPContext = getApplicationContext();
			String strTotalmonthlypremium = getWebElementText(aKeyWord,
					String.format(ORConstants.FORM_QUOTE_SUMMARY_QUOTE_TOT_MON_PREMIUM_XPATH, strPolicyFormID), false);

			String strPersonalProtectionDropDownResult = clickWebElement(aKeyWord,
					String.format(ORConstants.FORM_QUOTE_SUMMARY_PERSONALPROTECTION_DROPDOWN_XPATH, strPolicyFormID),
					false);
			if (isTestSetpFailed(strPersonalProtectionDropDownResult)) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			delayInSeconds(1);

			clickWebElement(aKeyWord,
					String.format(ORConstants.FORM_QUOTE_SUMMARY_ADVISERINFO_DROPDOWN_XPATH, strPolicyFormID), false);
//			if (isTestSetpFailed(strAdviserInfoDropDownResult)) {
//				return AppConstants.TEST_RESULT_FAIL;
//			}
			delayInSeconds(2);

			String strInitialCommissionValueOne = getWebElementText(aKeyWord,
					String.format(ORConstants.FORM_QUOTE_SUMMARY_INITIAL_COMMISSION_VALUE_XPATH, strPolicyFormID, 1),
					false);
			String strInitialCommissionValueTwo = null;
			String strInitialCommissionValueTWO01 = null;
			String strInitialCommissionValueTWO02 = null;
			String strRenewalCommissionOne = null;
			String strRenewalCommissionTwo = null;
			String strRenewalCommissionTwoOne = null;
			String strRenewalCommissionTwoTwo = null;
			String strSecondLineinFirstProduct = getWebElementText(aKeyWord,
					String.format(ORConstants.FORM_QUOTE_SUMMARY_INITIAL_COMMISSION_VALUE_XPATH, strPolicyFormID, 2),
					false);

			strInitialCommissionValueOne = getFormattedQuoteSummarryResults(strInitialCommissionValueOne);

			if (StringUtils.containsIgnoreCase(strSecondLineinFirstProduct, "Initial")) {
				strInitialCommissionValueTwo = getFormattedQuoteSummarryResults(strSecondLineinFirstProduct);
			}

			if (StringUtils.containsIgnoreCase(strSecondLineinFirstProduct, "Renewal")) {
				int iPoundIndex = StringUtils.indexOf(strSecondLineinFirstProduct, '£');
				strRenewalCommissionOne = StringUtils.substring(strSecondLineinFirstProduct, iPoundIndex + 1,
						iPoundIndex + 10);
				strRenewalCommissionOne = RegExUtils.replacePattern(strRenewalCommissionOne, "[a-z A-Z]", "");
			}

			String strThridLineinFirstProduct = getWebElementText(aKeyWord,
					String.format(ORConstants.FORM_QUOTE_SUMMARY_INITIAL_COMMISSION_VALUE_XPATH, strPolicyFormID, 3),
					false);

			if (!StringUtils.isEmpty(StringUtils.trim(strThridLineinFirstProduct))) {
				if (StringUtils.isEmpty(StringUtils.trim(strRenewalCommissionOne))) {
					int iPoundIndex = StringUtils.indexOf(strThridLineinFirstProduct, '£');
					strRenewalCommissionOne = StringUtils.substring(strThridLineinFirstProduct, iPoundIndex + 1,
							iPoundIndex + 10);
					strRenewalCommissionOne = RegExUtils.replacePattern(strThridLineinFirstProduct, "[a-z A-Z]", "");
				} else {
					int iPoundIndex = StringUtils.indexOf(strThridLineinFirstProduct, '£');
					strRenewalCommissionTwo = StringUtils.substring(strThridLineinFirstProduct, iPoundIndex + 1,
							iPoundIndex + 10);
					strRenewalCommissionTwo = RegExUtils.replacePattern(strThridLineinFirstProduct, "[a-z A-Z]", "");
				}
			}

			String strFourthLineinFirstProduct = getWebElementText(aKeyWord,
					String.format(ORConstants.FORM_QUOTE_SUMMARY_INITIAL_COMMISSION_VALUE_XPATH, strPolicyFormID, 4),
					false);
			if (!StringUtils.isEmpty(StringUtils.trim(strFourthLineinFirstProduct))) {
				int iPoundIndex = StringUtils.indexOf(strFourthLineinFirstProduct, '£');
				strRenewalCommissionTwo = StringUtils.substring(strFourthLineinFirstProduct, iPoundIndex + 1,
						iPoundIndex + 10);
				strRenewalCommissionTwo = RegExUtils.replacePattern(strFourthLineinFirstProduct, "[a-z A-Z]", "");
			}

			if (!StringUtils.isEmpty(strPolicyFormID)) {
				// ************************************* Second Product
				// *************************************
				strPolicyFormID = "[2]";
				strPersonalProtectionDropDownResult = clickWebElement(aKeyWord, String.format(
						ORConstants.FORM_QUOTE_SUMMARY_PERSONALPROTECTION_DROPDOWN_XPATH, strPolicyFormID), false);
				if (isTestSetpFailed(strPersonalProtectionDropDownResult)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				delayInSeconds(1);

				clickWebElement(aKeyWord,
						String.format(ORConstants.FORM_QUOTE_SUMMARY_ADVISERINFO_DROPDOWN_XPATH, strPolicyFormID),
						false);
//				if (isTestSetpFailed(strAdviserInfoDropDownResult)) {
//					return AppConstants.TEST_RESULT_FAIL;
//				}
				delayInSeconds(2);
				strInitialCommissionValueTWO01 = getWebElementText(aKeyWord, String.format(
						ORConstants.FORM_QUOTE_SUMMARY_INITIAL_COMMISSION_VALUE_XPATH, strPolicyFormID, 1), false);
				strInitialCommissionValueTWO01 = getFormattedQuoteSummarryResults(strInitialCommissionValueTWO01);

				strSecondLineinFirstProduct = getWebElementText(aKeyWord, String.format(
						ORConstants.FORM_QUOTE_SUMMARY_INITIAL_COMMISSION_VALUE_XPATH, strPolicyFormID, 2), false);
				if (StringUtils.containsIgnoreCase(strSecondLineinFirstProduct, "Initial")) {
					strInitialCommissionValueTWO01 = getFormattedQuoteSummarryResults(strSecondLineinFirstProduct);
				}

				if (StringUtils.containsIgnoreCase(strSecondLineinFirstProduct, "Renewal")) {
					int iPoundIndex = StringUtils.indexOf(strSecondLineinFirstProduct, '£');
					strRenewalCommissionTwoOne = StringUtils.substring(strSecondLineinFirstProduct, iPoundIndex + 1,
							iPoundIndex + 10);
					strRenewalCommissionTwoOne = RegExUtils.replacePattern(strRenewalCommissionOne, "[a-z A-Z]", "");
				}

				strThridLineinFirstProduct = getWebElementText(aKeyWord, String.format(
						ORConstants.FORM_QUOTE_SUMMARY_INITIAL_COMMISSION_VALUE_XPATH, strPolicyFormID, 3), false);

				if (!StringUtils.isEmpty(StringUtils.trim(strThridLineinFirstProduct))) {
					if (StringUtils.isEmpty(StringUtils.trim(strRenewalCommissionTwoOne))) {
						int iPoundIndex = StringUtils.indexOf(strThridLineinFirstProduct, '£');
						strRenewalCommissionTwoOne = StringUtils.substring(strThridLineinFirstProduct, iPoundIndex + 1,
								iPoundIndex + 10);
						strRenewalCommissionTwoOne = RegExUtils.replacePattern(strThridLineinFirstProduct, "[a-z A-Z]",
								"");
					} else {
						int iPoundIndex = StringUtils.indexOf(strThridLineinFirstProduct, '£');
						strRenewalCommissionTwoTwo = StringUtils.substring(strThridLineinFirstProduct, iPoundIndex + 1,
								iPoundIndex + 10);
						strRenewalCommissionTwoTwo = RegExUtils.replacePattern(strThridLineinFirstProduct, "[a-z A-Z]",
								"");
					}
				}

				strFourthLineinFirstProduct = getWebElementText(aKeyWord, String.format(
						ORConstants.FORM_QUOTE_SUMMARY_INITIAL_COMMISSION_VALUE_XPATH, strPolicyFormID, 4), false);
				if (!StringUtils.isEmpty(StringUtils.trim(strFourthLineinFirstProduct))) {
					int iPoundIndex = StringUtils.indexOf(strFourthLineinFirstProduct, '£');
					strRenewalCommissionTwoTwo = StringUtils.substring(strFourthLineinFirstProduct, iPoundIndex + 1,
							iPoundIndex + 10);
					strRenewalCommissionTwoTwo = RegExUtils.replacePattern(strFourthLineinFirstProduct, "[a-z A-Z]",
							"");
				}
			}
			aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
					SummaryReportConstants.SECNARIO_HEADER, testScenarioName);
			aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
					SummaryReportConstants.QUATATION_APPLICATION_REF_HEADER, strQuoteNAppRef);

			aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
					SummaryReportConstants.DEAL_NAME_HEADER, null);

			strTotalmonthlypremium = getFormattedQuoteSummarryResults(strTotalmonthlypremium);
			aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
					SummaryReportConstants.TOTAL_FREQ_PREM_HEADER, strTotalmonthlypremium);

			aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
					SummaryReportConstants.TOTAL_PREM_DISCLOSURE_HEADER, null);

			if (StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_TRUE)) {

				aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						AppUtils.formatMessage(SummaryReportConstants.IN_COM_ACC_PRODONE_HEADER, 1), null);

				aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						AppUtils.formatMessage(SummaryReportConstants.IN_COM_ACC_PRODONE_HEADER, 2), null);

				aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						AppUtils.formatMessage(SummaryReportConstants.IN_COM_ACC_PRODTWO_HEADER, 1), null);

				aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						AppUtils.formatMessage(SummaryReportConstants.IN_COM_ACC_PRODTWO_HEADER, 2), null);

				aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						AppUtils.formatMessage(SummaryReportConstants.IN_COM_IND_PRODONE_HEADER, 1),
						strInitialCommissionValueOne);

				aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						AppUtils.formatMessage(SummaryReportConstants.IN_COM_IND_PRODONE_HEADER, 2),
						strInitialCommissionValueTwo);

				aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						AppUtils.formatMessage(SummaryReportConstants.IN_COM_IND_PRODTWO_HEADER, 1),
						strInitialCommissionValueTWO01);

				aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						AppUtils.formatMessage(SummaryReportConstants.IN_COM_IND_PRODTWO_HEADER, 2),
						strInitialCommissionValueTWO02);
			} else {
				//
				aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						AppUtils.formatMessage(SummaryReportConstants.IN_COM_ACC_PRODONE_HEADER, 1),
						strInitialCommissionValueOne);

				aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						AppUtils.formatMessage(SummaryReportConstants.IN_COM_ACC_PRODONE_HEADER, 2),
						strInitialCommissionValueTwo);

				aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						AppUtils.formatMessage(SummaryReportConstants.IN_COM_ACC_PRODTWO_HEADER, 1),
						strInitialCommissionValueTWO01);

				aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						AppUtils.formatMessage(SummaryReportConstants.IN_COM_ACC_PRODTWO_HEADER, 2),
						strInitialCommissionValueTWO02);

				aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						AppUtils.formatMessage(SummaryReportConstants.IN_COM_IND_PRODONE_HEADER, 1), null);

				aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						AppUtils.formatMessage(SummaryReportConstants.IN_COM_IND_PRODONE_HEADER, 2), null);

				aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						AppUtils.formatMessage(SummaryReportConstants.IN_COM_IND_PRODTWO_HEADER, 1), null);

				aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
						AppUtils.formatMessage(SummaryReportConstants.IN_COM_IND_PRODTWO_HEADER, 2), null);
			}

			aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
					AppUtils.formatMessage(SummaryReportConstants.RENW_COM_PRODONE_HEADER, 1), strRenewalCommissionOne);

			aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
					AppUtils.formatMessage(SummaryReportConstants.RENW_COM_PRODONE_HEADER, 2), strRenewalCommissionTwo);

			aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
					AppUtils.formatMessage(SummaryReportConstants.RENW_COM_PRODTWO_HEADER, 1),
					strRenewalCommissionTwoOne);

			aPPContext.addExecQuotationSurmmaryReport(testScenarioName, getBrowsersConfigBean(),
					AppUtils.formatMessage(SummaryReportConstants.RENW_COM_PRODTWO_HEADER, 2),
					strRenewalCommissionTwoTwo);

			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String sendKeys(CharSequence aCharSequence) {
		String strLogMessage = AppUtils.formatMessage("Performing key event {0} on browser {1}",
				Arrays.asList(aCharSequence), getBrowser().getBrowserName());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			Actions aControlTab = new Actions(getWebDriver());
			delayInSeconds(1);
			aControlTab.sendKeys(aCharSequence).build().perform();
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String sendKeys(int... iKeyCodes) {
		String strLogMessage = AppUtils.formatMessage("Performing key event {0} on browser {1}",
				Arrays.asList(iKeyCodes), getBrowser().getBrowserName());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			Robot aRobot = new Robot();
			for (int iKeyCode : iKeyCodes) {
				aRobot.keyPress(iKeyCode);
			}
			for (int iKeyCode : iKeyCodes) {
				aRobot.keyRelease(iKeyCode);
			}
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected void capturePolicyNumber(int iPolicyIndex, String testScenarioName, String strPolicyNumber) {
		AppContext aPPContext = getApplicationContext();
		LinkedHashMap<String, String> mpPlolicies = new LinkedHashMap<>();
		LinkedHashMap<String, String> mpScenrioData = getRunTimeData(testScenarioName);
		mpScenrioData.entrySet().stream().forEach(aEntry -> {
			String strKey = aEntry.getKey();
			String strValue = aEntry.getValue();
			boolean isProductKey = AppUtils.isRegex(strKey, SummaryReportConstants.POLICY_NUMBER_HEADER_REGEX);
			if (isProductKey) {
				mpPlolicies.put(strKey, strValue);
			}
		});
		int iNewPolicyIndex = iPolicyIndex;
		int iValue = 0;
		for (Entry<String, String> aPoliciesEntry : mpPlolicies.entrySet()) {
			iNewPolicyIndex = iValue + 1;
			String strValue = aPoliciesEntry.getValue();
			if (StringUtils.equalsIgnoreCase(strValue, SummaryReportConstants.NO_RESULT)) {
				iPolicyIndex = iNewPolicyIndex;
				break;
			}
			iValue++;
		}

		String strPolicyHeader = AppUtils.formatMessage(SummaryReportConstants.POLICY_NUMBER_HEADER, iPolicyIndex);
		String strProductHeader = AppUtils.formatMessage(SummaryReportConstants.PRODUCT_DECISION_HEADER, iPolicyIndex);
		if (StringUtils.isEmpty(getExecRefSurmmaryReportValue(testScenarioName, strProductHeader))) {
			aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(), strProductHeader, null);
			aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), strProductHeader, null);
		}
		aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(), strPolicyHeader,
				strPolicyNumber);
		aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), strPolicyHeader, strPolicyNumber);
	}

	protected void captureProductDecision(int iProductDescIndex, String testScenarioName, String strProductDecision,
			String strProductDecisionPremium) {
		AppContext aPPContext = getApplicationContext();

		String strProductHeader = AppUtils.formatMessage(SummaryReportConstants.PRODUCT_DECISION_HEADER,
				iProductDescIndex);
		aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(), strProductHeader,
				strProductDecision);
		aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), strProductHeader, strProductDecision);

		strProductDecisionPremium = StringUtils.isEmpty(StringUtils.trim(strProductDecisionPremium))
				? SummaryReportConstants.NO_RESULT
				: strProductDecisionPremium;

		String strProductPremiumHeader = AppUtils.formatMessage(SummaryReportConstants.PRODUCT_DECISION_PREMIUM_HEADER,
				iProductDescIndex);
		aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(), strProductPremiumHeader,
				strProductDecisionPremium);
		aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), strProductPremiumHeader,
				strProductDecisionPremium);

		String strDefaultPolicy = SummaryReportConstants.NO_RESULT;
		if (StringUtils.containsAny(strProductDecision, SummaryReportConstants.NA_POLICIES)) {
			strDefaultPolicy = null;
		}
		String strPolicyHeader = AppUtils.formatMessage(SummaryReportConstants.POLICY_NUMBER_HEADER, iProductDescIndex);
		if (StringUtils.isEmpty(getExecRefSurmmaryReportValue(testScenarioName, strPolicyHeader))) {
			aPPContext.addExecRefSurmmaryReport(testScenarioName, getBrowsersConfigBean(), strPolicyHeader,
					strDefaultPolicy);
			aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), strPolicyHeader, strDefaultPolicy);
		}
	}

	protected void captureProductDecision(KeyWordConfigBean aKeyWordConfigBean, int iProductDescIndex,
			String testScenarioName, String strProductDecision, String strDecisionsCCSValue) {
		AppContext aPPContext = getApplicationContext();
		String strPropertyValue = null;
		String strProductDecisionText = null;
		String strProductHeader = null;

		if (StringUtils.equalsIgnoreCase(SummaryReportConstants.REFER_PRODUCT_DECISION_HEADER, strProductDecision)) {
			strPropertyValue = String.format(ORConstants.DIV_PRODUCT_DECISIONS_REFER1_XPATH, iProductDescIndex);
			try {
				strProductDecisionText = getWebElementText(aKeyWordConfigBean, strPropertyValue, false);
			} catch (Exception e) {
			}
			if (!StringUtils.isEmpty(strProductDecisionText)) {
				strProductHeader = AppUtils.formatMessage(SummaryReportConstants.PRODUCT_DECISION_REFER1_HEADER,
						iProductDescIndex);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), strProductHeader,
						strProductDecisionText);
			}

			strPropertyValue = String.format(ORConstants.DIV_PRODUCT_DECISIONS_REFER1_JL__XPATH, iProductDescIndex);
			try {
				strProductDecisionText = getWebElementText(aKeyWordConfigBean, strPropertyValue, false);
			} catch (Exception e) {
			}
			if (!StringUtils.isEmpty(strProductDecisionText)) {
				strProductHeader = AppUtils.formatMessage(SummaryReportConstants.PRODUCT_DECISION_REFER1_JL_HEADER,
						iProductDescIndex);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), strProductHeader,
						strProductDecisionText);
			}
			strPropertyValue = String.format(ORConstants.DIV_PRODUCT_DECISIONS_REFER_BENEFITS_XPATH, iProductDescIndex);
			try {
				strProductDecisionText = getWebElementText(aKeyWordConfigBean, strPropertyValue, false);
			} catch (Exception e) {
			}
			if (!StringUtils.isEmpty(strProductDecisionText)) {
				strProductHeader = AppUtils.formatMessage(SummaryReportConstants.PRODUCT_DECISION_REFER_BENIFITS_HEADER,
						iProductDescIndex);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), strProductHeader,
						strProductDecisionText);
			}

		}
		if (StringUtils.equalsIgnoreCase(SummaryReportConstants.REVISED_TERMS_PRODUCT_DECISION_HEADER,
				strProductDecision)) {
			strPropertyValue = StringUtils.equals(ORConstants.DISABLE_CSS_VALUE, strDecisionsCCSValue)
					? String.format(ORConstants.DIV_PRODUCT_DECISIONS_REVISED_TERMS1_XPATH, iProductDescIndex)
					: String.format(ORConstants.DIV_PRODUCT_DECISIONS_REVISED_TERMS2_XPATH, iProductDescIndex);
			try {
				strProductDecisionText = getWebElementText(aKeyWordConfigBean, strPropertyValue, false);
			} catch (Exception e) {
			}
			if (!StringUtils.isEmpty(strProductDecisionText)) {
				strProductHeader = AppUtils.formatMessage(SummaryReportConstants.PRODUCT_DECISION_REVISED_TERMS1_HEADER,
						iProductDescIndex);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), strProductHeader,
						strProductDecisionText);
			}

			strPropertyValue = StringUtils.equals(ORConstants.DISABLE_CSS_VALUE, strDecisionsCCSValue)
					? String.format(ORConstants.DIV_PRODUCT_DECISIONS_REVISED_TERMS_BENEFITS_XPATH, iProductDescIndex)
					: String.format(ORConstants.DIV_PRODUCT_DECISIONS_REVISED_TERMS_BENEFITS_BLOCK_XPATH,
							iProductDescIndex);
			try {
				strProductDecisionText = getWebElementText(aKeyWordConfigBean, strPropertyValue, false);
			} catch (Exception e) {
			}
			if (!StringUtils.isEmpty(strProductDecisionText)) {
				strProductHeader = AppUtils.formatMessage(
						SummaryReportConstants.PRODUCT_DECISION_REVISED_TERMS_BENIFITS_HEADER, iProductDescIndex);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), strProductHeader,
						strProductDecisionText);
			}
		}
		if (StringUtils.equalsIgnoreCase(SummaryReportConstants.UNABLE_TO_OFFER_TERMS_PRODUCT_DECISION_HEADER,
				strProductDecision)) {

			strPropertyValue = String.format(ORConstants.DIV_PRODUCT_DECISIONS_UNABLE_TO_OFFER_TERMS2_XPATH,
					iProductDescIndex, iProductDescIndex);
			try {
				strProductDecisionText = getWebElementText(aKeyWordConfigBean, strPropertyValue, false);
			} catch (Exception e) {
			}
			if (!StringUtils.isEmpty(strProductDecisionText)) {
				strProductHeader = AppUtils.formatMessage(
						SummaryReportConstants.PRODUCT_DECISION_UNABLE_TO_OFFER_TERMS1_HEADER, iProductDescIndex);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), strProductHeader,
						strProductDecisionText);
			}
			strPropertyValue = String.format(ORConstants.DIV_PRODUCT_DECISIONS_UNABLE_TO_OFFER_TERMS_BENEFITS_XPATH,
					iProductDescIndex);
			try {
				strProductDecisionText = getWebElementText(aKeyWordConfigBean, strPropertyValue, false);
			} catch (Exception e) {
			}
			if (!StringUtils.isEmpty(strProductDecisionText)) {
				strProductHeader = AppUtils.formatMessage(
						SummaryReportConstants.PRODUCT_DECISION_UNABLE_TO_OFFER_TERMS_BENIFITS_HEADER,
						iProductDescIndex);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), strProductHeader,
						strProductDecisionText);
			}
		}
		if (StringUtils.equalsIgnoreCase(SummaryReportConstants.STANDARD_TERMS_PRODUCT_DECISION_HEADER,
				strProductDecision)) {

			strPropertyValue = String.format(ORConstants.DIV_PRODUCT_DECISIONS_STANDARD_TERMS_BENEFITS_XPATH,
					iProductDescIndex);
			try {
				strProductDecisionText = getWebElementText(aKeyWordConfigBean, strPropertyValue, false);
			} catch (Exception e) {
			}
			if (!StringUtils.isEmpty(strProductDecisionText)) {
				strProductHeader = AppUtils.formatMessage(
						SummaryReportConstants.PRODUCT_DECISION_STANDARD_TERMS_BENIFITS_HEADER, iProductDescIndex);
				aPPContext.addRunTimeData(testScenarioName, getBrowsersConfigBean(), strProductHeader,
						strProductDecisionText);
			}
		}
	}

	protected String readEmailAndOpenLink(String strPropertyValue, KeyWordConfigBean aKeyWord, String strTestData,
			String strFirstName) throws Exception {

		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(strTestData)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Reading email for {0} with data {1}", aKeyWord.toString(),
				strTestData);
		String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			if (StringUtils.indexOf(strPropertyValue, AppConstants.SEPARATOR_COMMA) <= 0) {
				LOGGER.error(strErrorMsg);
				return AppConstants.TEST_RESULT_FAIL;

			}
			String strReadEmailDatas[] = StringUtils.split(strPropertyValue, AppConstants.SEPARATOR_COMMA);
			if (strReadEmailDatas == null || strReadEmailDatas.length < 3 || strReadEmailDatas.length > 3) {
				LOGGER.error(strErrorMsg);
				return AppConstants.TEST_RESULT_FAIL;
			}
			String strSubject = strReadEmailDatas[0];
			String strMessagePrefix = AppUtils.formatMessage(strReadEmailDatas[1], strFirstName);
			String strMessageText = EmailUtils.getEmailMessageByTest(strTestData, strSubject, strMessagePrefix);
			String strEmailPrefix = strReadEmailDatas[2];
			Set<String> stHref = AppUtils.getHrefText(strMessageText);
			Optional<String> aOPURl = stHref.stream()
					.filter(strEmailLinkUrl -> StringUtils.containsIgnoreCase(strEmailLinkUrl, strEmailPrefix))
					.findAny();
			if (!aOPURl.isPresent()) {
				LOGGER.error("Unbale to Fetch HTML Link");
				return AppConstants.TEST_RESULT_FAIL;
			}
			String strURL = aOPURl.get();
			strErrorMsg = AppUtils.formatMessage("Error While Navigating to {0}", strURL);
			return openURL(aKeyWord, strURL, true);
		} catch (Exception ex) {
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String verifyEmailMessage(String strPropertyValue, String testScenarioName, KeyWordConfigBean aKeyWord,
			String strTestData, String strFirstName) throws Exception {

		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(strTestData)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Verifying email for {0} with data {1}", aKeyWord.toString(),
				strTestData);
		String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			if (StringUtils.indexOf(strPropertyValue, AppConstants.SEPARATOR_COMMA) <= 0
					|| StringUtils.indexOf(strTestData, AppConstants.SEPARATOR_COMMA) <= 0) {
				LOGGER.error(strErrorMsg);
				return AppConstants.TEST_RESULT_FAIL;
			}
			String strReadEmailDatas[] = StringUtils.split(strPropertyValue, AppConstants.SEPARATOR_COMMA);
			if (strReadEmailDatas == null || strReadEmailDatas.length < 2 || strReadEmailDatas.length > 2) {
				LOGGER.error(strErrorMsg);
				return AppConstants.TEST_RESULT_FAIL;
			}
			String strTestDatas[] = StringUtils.split(strTestData, AppConstants.SEPARATOR_COMMA);
			if (strTestDatas == null || strTestDatas.length < 2) {
				LOGGER.error(strErrorMsg);
				return AppConstants.TEST_RESULT_FAIL;
			}
			String strSubject = strReadEmailDatas[0];
			String strMessagePrefix = AppUtils.formatMessage(strReadEmailDatas[1], strFirstName);
			String strMessageText = EmailUtils.getEmailMessageByTest(strTestDatas[0], strSubject, strMessagePrefix);
			return verifyText(null, aKeyWord, testScenarioName, strMessageText, strTestDatas[1], false);
		} catch (Exception ex) {
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String downloadEmailAttachment(String strPropertyValue, String testScenarioName,
			KeyWordConfigBean aKeyWord, String strReportKeyWord, String strTestData) throws Exception {

		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(strReportKeyWord)
				|| StringUtils.isEmpty(strTestData)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Verifying attachment email for {0} with data {1}",
				aKeyWord.toString(), strTestData);
		String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			if (StringUtils.indexOf(strPropertyValue, AppConstants.SEPARATOR_COMMA) <= 0
					|| StringUtils.indexOf(strTestData, AppConstants.SEPARATOR_COMMA) <= 0) {
				LOGGER.error(strErrorMsg);
				return AppConstants.TEST_RESULT_FAIL;
			}
			String strReadEmailDatas[] = StringUtils.split(strPropertyValue, AppConstants.SEPARATOR_COMMA);
			if (strReadEmailDatas == null || strReadEmailDatas.length < 3 || strReadEmailDatas.length > 3) {
				LOGGER.error(strErrorMsg);
				return AppConstants.TEST_RESULT_FAIL;
			}
			String strTestDatas[] = StringUtils.split(strTestData, AppConstants.SEPARATOR_COMMA);
			if (strTestDatas == null || strTestDatas.length < 3) {
				LOGGER.error(strErrorMsg);
				return AppConstants.TEST_RESULT_FAIL;
			}
			String strSubject = getFormattedTestData(testScenarioName, strReadEmailDatas[0]);
			String[] strSearchFileNames = StringUtils.split(strTestDatas[2], strReadEmailDatas[2]);
			String strMessagePrefix = AppUtils.formatMessage(strReadEmailDatas[1],
					getRunTimeDataValue(testScenarioName, strReportKeyWord));
			String strMessageText = EmailUtils.getEmailAttachMentMessageByTest(getBrowsersConfigBean(), strTestDatas[0],
					strSubject, strMessagePrefix, strSearchFileNames);
			return verifyText(null, aKeyWord, testScenarioName, strMessageText, strTestDatas[1], false);
		} catch (Exception ex) {
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String verifyImageFiles(String testScenarioName, KeyWordConfigBean aKeyWord, File aActualFile,
			File aExpectedFile) throws Exception {
		if (aActualFile == null || aExpectedFile == null) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		if (!aActualFile.exists()) {
			throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.FILENTFOUND, aActualFile.getPath()));
		}
		if (!aExpectedFile.exists()) {
			throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.FILENTFOUND, aExpectedFile.getPath()));
		}
		String strLogMessage = AppUtils.formatMessage("Verifying images file {0} with {1} on {2}",
				aExpectedFile.toPath(), aActualFile.toPath(), aKeyWord.toString());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			// load images to be compared:
			BufferedImage actualImage = ImageIO.read(aActualFile);
			BufferedImage expectedImage = ImageIO.read(aExpectedFile);
			long lCurrentTime = System.currentTimeMillis();
			String strFileName = String.format("%s_%s.%s", FilenameUtils.getBaseName(aActualFile.getName()),
					lCurrentTime, FilenameUtils.getExtension(aActualFile.getName()));
			File aResultFile = Paths
					.get(AppConfig.getInstance().getBrowserExecutionReportFolder(getBrowsersConfigBean()),
							testScenarioName, AppConstants.IMAGE_COMPARE_RESULT_FOLDER, strFileName)
					.toFile();
			// Create ImageComparison object and compare the images.
			ImageComparison aImageComparison = new ImageComparison(expectedImage, actualImage);

			ImageComparisonResult imageComparisonResult = aImageComparison.compareImages();
			ImageComparisonState aImageComparisonState = imageComparisonResult.getImageComparisonState();
			if (!aResultFile.getParentFile().exists()) {
				aResultFile.getParentFile().mkdirs();
			}
			ImageComparisonUtil.saveImage(aResultFile, imageComparisonResult.getResult());
			return aImageComparisonState == ImageComparisonState.MATCH ? AppConstants.TEST_RESULT_PASS
					: AppConstants.TEST_RESULT_FAIL;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String verifyTextFiles(String testScenarioName, KeyWordConfigBean aKeyWord, File aActualFile,
			File aExpectedFile) throws Exception {
		if (aActualFile == null || aExpectedFile == null) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		if (!aActualFile.exists()) {
			throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.FILENTFOUND, aActualFile.getPath()));
		}
		if (!aExpectedFile.exists()) {
			throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.FILENTFOUND, aExpectedFile.getPath()));
		}
		String strLogMessage = AppUtils.formatMessage("Verifying text file {0} with {1} on keyword",
				aExpectedFile.toPath(), aActualFile.toPath());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			try (BufferedReader actualFileReader = new BufferedReader(new FileReader(aActualFile));
					BufferedReader expectedFileReader = new BufferedReader(new FileReader(aExpectedFile))) {
				String strSrcText = actualFileReader.readLine();
				String strTestData = expectedFileReader.readLine();
				while (!StringUtils.isEmpty(strSrcText) || !StringUtils.isEmpty(strTestData)) {
					String strResult = verifyText(null, aKeyWord, testScenarioName, strSrcText, strTestData, true);
					if (isTestSetpFailed(strResult)) {
						return AppConstants.TEST_RESULT_FAIL;
					}
					strSrcText = actualFileReader.readLine();
					strTestData = expectedFileReader.readLine();
				}
			}
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	public String verifyPDFFiles(String testScenarioName, String stepDescription, KeyWordConfigBean aKeyWord,
			String strObJPropName, String strReportKeyWord) throws Exception {
		LinkedList<PDFExclusions> lstPDExclusions = getTestSuite() == null ? null : getTestSuite().getPDFExclusions();
		String pdfFiletype = strObJPropName;
		PDFFileType aPDfFileType = StringUtils.isEmpty(StringUtils.trim(pdfFiletype)) ? PDFFileType.INVALID
				: PDFFileType.getPDFFileType(pdfFiletype);
		if (CollectionUtils.isEmpty(lstPDExclusions) || aPDfFileType == PDFFileType.INVALID) {
			LOGGER.error("Invalid Test Data");
			return AppConstants.TEST_RESULT_FAIL;
		}

		PDFExclusions aPDFExclusions = lstPDExclusions.stream()
				.filter(thePDFExclusions -> thePDFExclusions.getFileType() == aPDfFileType).findFirst().orElse(null);
		if (aPDFExclusions == null) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String exclusionAreas = aPDFExclusions.getExclusionAreas();
		File aExpectedFile = aPDFExclusions.getExpectedPath();

		File aActualFile = getDlownloadFile(testScenarioName, strReportKeyWord,
				AppConstants.PDF_DOCUMENT_REPORT_EXTENSION);// Current scn dw file
		if (aActualFile == null || aExpectedFile == null || StringUtils.isEmpty(StringUtils.trim(exclusionAreas))) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		if (!aActualFile.exists()) {
			throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.FILENTFOUND, aActualFile.getPath()));
		}
		if (!aExpectedFile.exists()) {
			throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.FILENTFOUND, aExpectedFile.getPath()));
		}
		String strLogMessage = AppUtils.formatMessage("Verifying PDF file {0} with {1} on keyword",
				aExpectedFile.toPath(), aActualFile.toPath());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			exclusionAreas = StringUtils.endsWith(exclusionAreas, AppConstants.SEPARATOR_SEMICOLON)
					? StringUtils.substring(exclusionAreas, 0,
							exclusionAreas.length() - AppConstants.SEPARATOR_SEMICOLON.length())
					: exclusionAreas;
			String[] strExclusionAreas = StringUtils.split(exclusionAreas, AppConstants.SEPARATOR_SEMICOLON);
			SimpleEnvironment aSimpleEnvironment = new SimpleEnvironment();
			aSimpleEnvironment.setActualColor(Color.GREEN);
			aSimpleEnvironment.setExpectedColor(Color.RED);
			aSimpleEnvironment.setNrOfImagesToCache(30);
			aSimpleEnvironment.setMaxImageSize(100000);
			aSimpleEnvironment.setMergeCacheSize(100);
			aSimpleEnvironment.setSwapCacheSize(100);
			aSimpleEnvironment.setParallelProcessing(true);
			aSimpleEnvironment.setOverallTimeout(15);
			aSimpleEnvironment.setAllowedDiffInPercent(0.01);
			PdfComparator<?> aPdfComparator = new PdfComparator<>(aExpectedFile, aActualFile)
					.withEnvironment(aSimpleEnvironment);
			for (String strExclusion : strExclusionAreas) {
				strExclusion = StringUtils.endsWith(strExclusion, AppConstants.SEPARATOR_COMMA)
						? StringUtils.substring(strExclusion, 0,
								strExclusion.length() - AppConstants.SEPARATOR_COMMA.length())
						: strExclusion;
				strExclusion = StringUtils.trim(strExclusion);
				String strPageAreas[] = StringUtils.split(strExclusion, AppConstants.SEPARATOR_COMMA);
				if (StringUtils.isAllEmpty(strPageAreas)) {
					continue;
				}
				int page = 1;
				int x1 = 0;
				int y1 = 0;
				int x2 = 0;
				int y2 = 0;
				PageArea aPageArea = null;
				if (ArrayUtils.getLength(strPageAreas) == 1) {
					page = NumberUtils.toInt(StringUtils.trim(strPageAreas[0]), 1);
					aPageArea = new PageArea(page);
				}
				if (ArrayUtils.getLength(strPageAreas) == 4) {
					x1 = NumberUtils.toInt(StringUtils.trim(strPageAreas[0]), 0);
					y1 = NumberUtils.toInt(StringUtils.trim(strPageAreas[1]), 0);
					x2 = NumberUtils.toInt(StringUtils.trim(strPageAreas[2]), 0);
					y2 = NumberUtils.toInt(StringUtils.trim(strPageAreas[3]), 0);
					aPageArea = new PageArea(x1, y1, x2, y2);
				}
				if (ArrayUtils.getLength(strPageAreas) == 5) {
					page = NumberUtils.toInt(StringUtils.trim(strPageAreas[0]), 1);
					x1 = NumberUtils.toInt(StringUtils.trim(strPageAreas[1]), 0);
					y1 = NumberUtils.toInt(StringUtils.trim(strPageAreas[2]), 0);
					x2 = NumberUtils.toInt(StringUtils.trim(strPageAreas[3]), 0);
					y2 = NumberUtils.toInt(StringUtils.trim(strPageAreas[4]), 0);
					aPageArea = new PageArea(page, x1, y1, x2, y2);
				}
				if (aPageArea == null) {
					continue;
				}
				aPdfComparator = aPdfComparator.withIgnore(aPageArea);
			}
			CompareResult aResult = aPdfComparator.compare();

			String strFileName = String.format("%s_%s_%s_%s.%s", MasterConfig.getInstance().getAppRunID(),
					AppUtils.getScenarioReportFileName(testScenarioName, TestStepReport.SCENARIOS_FILE_NAME_LENGTH),
					AppUtils.getScenarioReportFileName(FilenameUtils.getBaseName(aActualFile.getName()), 5),
					AppUtils.getFileDate(), FilenameUtils.getExtension(aActualFile.getName()));

			String strExpectedFileName = String.format("%s_%s_%s_Excepted_%s.%s",
					MasterConfig.getInstance().getAppRunID(),
					AppUtils.getScenarioReportFileName(testScenarioName, TestStepReport.SCENARIOS_FILE_NAME_LENGTH),
					AppUtils.getScenarioReportFileName(pdfFiletype, 0), AppUtils.getFileDate(),
					FilenameUtils.getExtension(aExpectedFile.getName()));

			String strScenarioPath = AppUtils.getScenarioReportFileName(testScenarioName,
					TestStepReport.SCENARIOS_FILE_NAME_LENGTH);
			String strBrowserExecDir = AppConfig.getInstance().getBrowserExecutionReportFolder(getBrowsersConfigBean());
			File aResultFile = Paths
					.get(strBrowserExecDir, strScenarioPath, AppConstants.PDF_COMPARE_RESULT_FOLDER, strFileName)
					.toFile();
			if (!aResultFile.getParentFile().exists()) {
				aResultFile.getParentFile().mkdirs();
			}

			File aExpectedResultFile = Paths.get(strBrowserExecDir, strScenarioPath, strExpectedFileName).toFile();

			if (!aExpectedResultFile.getParentFile().exists()) {
				aExpectedResultFile.getParentFile().mkdirs();
			}

			try (FileOutputStream aResultStream = new FileOutputStream(aResultFile)) {
				aResult.writeTo(aResultStream);
			}

			FileUtils.copyFile(aExpectedFile, aExpectedResultFile);

			String strResult = aResult.isEqual() ? AppConstants.TEST_RESULT_PASS : AppConstants.TEST_RESULT_WARING;
			if (StringUtils.equalsIgnoreCase(AppConstants.TEST_RESULT_WARING, strResult)) {
				String strStepLogMessage = AppUtils.formatMessage(
						"PDF verification for file {0} with {1} failed at {2}", aExpectedFile.toPath(),
						aActualFile.toPath(), aResult.getDifferencesJson());
				TestStepReport.logWarning(getBrowsersConfigBean(), testScenarioName, strStepLogMessage, stepDescription,
						false, getWebDriver());
			}
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

	protected String verifyPDFText(String strReportKeyWord, KeyWordConfigBean aKeyWord, String testScenarioName,
			String strPropertyValue, String strTestData, boolean isStrict) throws Exception {
		if (StringUtils.isEmpty(StringUtils.trim(strTestData))) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}

		File aDWPDFFile = getDlownloadFile(testScenarioName, strReportKeyWord, AppConstants.PDF_FILE_EXTENTION);
		if (!aDWPDFFile.exists()) {
			return AppConstants.TEST_RESULT_FAIL;
		}
		strPropertyValue = StringUtils.isEmpty(StringUtils.trim(strPropertyValue)) ? AppConstants.SEPARATOR_CAP
				: strPropertyValue;
		String strLogMessage = AppUtils.formatMessage("Verifying Text in file {0} for {1}", aDWPDFFile.getName(),
				aKeyWord.toString());
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
		try (InputStream aFileInputStream = new FileInputStream(aDWPDFFile);
				PDDocument aPDFDocument = PDDocument.load(aFileInputStream);) {
			delayInSeconds(1);
			strTestData = AppUtils.removeInvisbleCharacters(strTestData);
			strTestData = getFormattedTestData(testScenarioName, strTestData);
			String[] strVerifyTextData = StringUtils.split(strTestData, strPropertyValue);
			PDFTextStripper aPDFStripper = new PDFHighlightTextStripper(strVerifyTextData, isStrict);
			String strParsedText = aPDFStripper.getText(aPDFDocument);
			String strSrcText = AppUtils.removeInvisbleCharacters(strParsedText);
			for (String strVerifyText : strVerifyTextData) {
				strVerifyText = AppUtils.removeInvisbleCharacters(strVerifyText);
				String strResult = verifyText(null, aKeyWord, testScenarioName, strSrcText, strVerifyText, isStrict);
				if (isTestSetpFailed(strResult)) {
					return strResult;
				}
			}
			aPDFDocument.save(aDWPDFFile);
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String verifyPDFTextByArea(String strReportKeyWord, KeyWordConfigBean aKeyWord, String testScenarioName,
			String strPropertyValue, String strTestData, boolean isStrict) throws Exception {
		if (StringUtils.isEmpty(StringUtils.trim(strPropertyValue))
				|| StringUtils.isEmpty(StringUtils.trim(strTestData))
				|| !StringUtils.contains(strTestData, AppConstants.SEPARATOR_COMMA)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		// https://www.mobilefish.com/services/record_mouse_coordinates/record_mouse_coordinates.php
		// (To find X & Y Cordinates of PDF)
		String[] strPropertyData = StringUtils.split(strTestData, AppConstants.SEPARATOR_COMMA);
		if (strPropertyData == null || strPropertyData.length < 6) {
			return AppConstants.TEST_RESULT_FAIL;
		}
		int iPageNumber = StringUtils.isNumeric(StringUtils.trim(strPropertyData[0]))
				? Integer.valueOf(StringUtils.trim(strPropertyData[0]))
				: 0;
		int iXOff = StringUtils.isNumeric(StringUtils.trim(strPropertyData[1]))
				? Integer.valueOf(StringUtils.trim(strPropertyData[1]))
				: 0;
		int iYOff = StringUtils.isNumeric(StringUtils.trim(strPropertyData[2]))
				? Integer.valueOf(StringUtils.trim(strPropertyData[2]))
				: 0;
		int iWidth = StringUtils.isNumeric(StringUtils.trim(strPropertyData[3]))
				? Integer.valueOf(StringUtils.trim(strPropertyData[3]))
				: 0;
		int iHeight = StringUtils.isNumeric(StringUtils.trim(strPropertyData[4]))
				? Integer.valueOf(StringUtils.trim(strPropertyData[4]))
				: 0;
		String strSeperator = StringUtils.isEmpty(StringUtils.trim(strPropertyData[5])) ? AppConstants.SEPARATOR_CAP
				: strPropertyData[5];
		File aDWPDFFile = getDlownloadFile(testScenarioName, strReportKeyWord, AppConstants.PDF_FILE_EXTENTION);
		if (!aDWPDFFile.exists()) {
			return AppConstants.TEST_RESULT_FAIL;
		}
		String strLogMessage = AppUtils.formatMessage("Verifying Text by area {0} in file {1} for {2}",
				strPropertyValue, aDWPDFFile.getName(), aKeyWord.toString());
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
		String strRegionName = "TextRegion";
		try (InputStream aFileInputStream = new FileInputStream(aDWPDFFile);
				PDDocument aPDFDocument = PDDocument.load(aFileInputStream);) {
			delayInSeconds(1);
			Rectangle2D.Float aTextRect = new Rectangle2D.Float(iXOff, iYOff, iWidth, iHeight); // coordinates of region
			if (iPageNumber > aPDFDocument.getNumberOfPages()) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			strTestData = AppUtils.removeInvisbleCharacters(strPropertyData[strPropertyData.length - 1]);
			strTestData = getFormattedTestData(testScenarioName, strTestData);
			String[] strVerifyTextData = StringUtils.split(strTestData, strSeperator);
			PDFTextStripperByArea aPDFStripper = new PDFHighlightTextStripperByArea(strVerifyTextData, isStrict);
			aPDFStripper.setSortByPosition(true);
			aPDFStripper.addRegion(strRegionName, aTextRect);
			PDPage aPDFPage = aPDFDocument.getPage(iPageNumber);
			aPDFStripper.extractRegions(aPDFPage);
			String strParsedText = aPDFStripper.getTextForRegion(strRegionName);
			String strSrcText = AppUtils.removeInvisbleCharacters(strParsedText);
			String strResult = AppConstants.TEST_RESULT_PASS;
			for (String strVerifyText : strVerifyTextData) {
				strVerifyText = AppUtils.removeInvisbleCharacters(strVerifyText);
				strResult = verifyText(null, aKeyWord, testScenarioName, strSrcText, strVerifyText, isStrict);
				if (isTestSetpFailed(strResult)) {
					strResult = AppConstants.TEST_RESULT_WARING;
					break;
				}
			}
			aPDFDocument.save(aDWPDFFile);
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

	protected String downloadFile(KeyWordConfigBean aKeyWord, File aHTMLFile, String strTestData) throws Exception {

		if (StringUtils.equalsIgnoreCase(strTestData, AppConstants.PDF_FILE_EXTENTION)) {
			return AppConstants.TEST_RESULT_PASS;
		}

		if (aHTMLFile == null) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}

		String strLogMessage = AppUtils.formatMessage("Downloading file {0} for {1}", aHTMLFile.getName(),
				aKeyWord.toString());
		try {
			String strURL = getWebDriver().getCurrentUrl();
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));

			String strDownLoadScript = "var url = arguments[0];" + "var callback = arguments[arguments.length - 1];"
					+ "var xhr = new XMLHttpRequest();" + "xhr.open('GET', url, true);"
					+ "xhr.responseType = \"arraybuffer\";" + // force the HTTP response, response-type header to be
																// array buffer
					"xhr.onload = function() {" + "  var arrayBuffer = xhr.response;"
					+ "  var byteArray = new Uint8Array(arrayBuffer);" + "  callback(byteArray);" + "};"
					+ "xhr.send();";
			Object response = ((JavascriptExecutor) getWebDriver()).executeAsyncScript(strDownLoadScript, strURL);
			// Selenium returns an Array of Long, we need byte[]
			@SuppressWarnings("unchecked")
			ArrayList<Long> byteList = (ArrayList<Long>) response;
			byte[] bytes = new byte[byteList.size()];
			for (int i = 0; i < byteList.size(); i++) {
				bytes[i] = (byte) (long) byteList.get(i);
			}
			try (InputStream aDDStream = new ByteArrayInputStream(bytes);
					OutputStream outStream = new FileOutputStream(aHTMLFile);) {
				byte[] buffer = new byte[8 * 1024];
				int bytesRead;
				while ((bytesRead = aDDStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}
				outStream.flush();
			}
			return aHTMLFile.exists() ? AppConstants.TEST_RESULT_PASS : AppConstants.TEST_RESULT_FAIL;
		} catch (Exception ex) {
			aHTMLFile.delete();
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String convertHtmlToPDF(KeyWordConfigBean aKeyWord, File aHTMLFile, String strTestData) throws Exception {

		if (StringUtils.equalsIgnoreCase(strTestData, AppConstants.PDF_FILE_EXTENTION)) {
			return AppConstants.TEST_RESULT_PASS;
		}

		if (StringUtils.isEmpty(StringUtils.trim(strTestData)) || !aHTMLFile.exists()) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}

		String strLogMessage = AppUtils.formatMessage("Converting HTML file {0} for {1}", aHTMLFile.getName(),
				aKeyWord.toString());
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
		String strFileNameWithoutExtention = FilenameUtils.getBaseName(aHTMLFile.getName());
		File aPDFFile = Paths.get(aHTMLFile.getParent(),
				String.format("%s.%s", strFileNameWithoutExtention, AppConstants.PDF_FILE_EXTENTION)).toFile();
		try (InputStream aHtmlFileInputStream = new FileInputStream(aHTMLFile);
				FileOutputStream aFileOutputStream = new FileOutputStream(aPDFFile);
				PdfWriter aPDFWriter = new PdfWriter(aFileOutputStream);
				PdfDocument aPDFDocument = new PdfDocument(aPDFWriter);) {
			java.awt.Dimension aScreenDim = Toolkit.getDefaultToolkit().getScreenSize();
			DefaultFontProvider aDefaultFontProvider = new DefaultFontProvider(false, true, true);
			ConverterProperties converterProperties = new ConverterProperties();
			converterProperties.setFontProvider(aDefaultFontProvider);
			aPDFDocument.setDefaultPageSize(new PageSize(aScreenDim.width, PageSize.A4.getHeight()));
			HtmlConverter.convertToPdf(aHtmlFileInputStream, aPDFDocument, converterProperties);
			return aPDFFile.exists() ? AppConstants.TEST_RESULT_PASS : AppConstants.TEST_RESULT_FAIL;
		} catch (Exception ex) {
			aPDFFile.delete();
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected int getPublicationsTableRowNumber(KeyWordConfigBean aKeyWord, String testScenarioName,
			String strPropertyValue, String strTestData, String strReportKeyWord) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(strTestData)
				|| StringUtils.isEmpty(strReportKeyWord)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Fetching Publications RowNumber for {0}", aKeyWord.toString());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aPublicationsWebElement = getWebElement(aKeyWord, String.format(strPropertyValue, strTestData));
			String strPublicationsPageCount = getWebElementText(aKeyWord, ORConstants.BACKOFFICE_DD_PAGE_COUNT, false);
			String[] strPublicationsPages = StringUtils.split(strPublicationsPageCount, "|");
			int iPublicationsPageNum = strPublicationsPages != null && strPublicationsPages.length > 0
					? Integer.valueOf(StringUtils.trim(strPublicationsPages[strPublicationsPages.length - 1]))
					: 0;
			int iPublicationsPageNo = 0;
			while (aPublicationsWebElement == null || !aPublicationsWebElement.isDisplayed()) {
				if (iPublicationsPageNo > iPublicationsPageNum) {
					return -1;
				}
				String strPageClick = clickWebElement(aKeyWord, ORConstants.BACKOFFICE_DD_NEXT_PAGE, false);
				if (isTestSetpFailed(strPageClick)) {
					return -1;
				}
				aPublicationsWebElement = getWebElement(aKeyWord, String.format(strPropertyValue, strTestData));
				iPublicationsPageNo++;
			}

			int iPublicationsTableRowNum = -1;
			String strRecipientName = getRunTimeDataValue(testScenarioName, strReportKeyWord);
			if (StringUtils.equalsAnyIgnoreCase(strReportKeyWord, ORConstants.DEFAUT_BO_USERS)) {
				iPublicationsTableRowNum = getTableRowByColumn(aKeyWord, ORConstants.BACKOFFICE_DD_TABLE, false,
						strTestData, strReportKeyWord);
			} else if (StringUtils.isEmpty(StringUtils.trim(strRecipientName))) {
				iPublicationsTableRowNum = getTableRowByColumn(aKeyWord, ORConstants.BACKOFFICE_DD_TABLE, false,
						strTestData);
			} else {
				iPublicationsTableRowNum = getTableRowByColumn(aKeyWord, ORConstants.BACKOFFICE_DD_TABLE, false,
						strTestData, strRecipientName);
			}
			return iPublicationsTableRowNum;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return -1;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected int getPublicationsTableRowNumberDD(KeyWordConfigBean aKeyWord, String testScenarioName,
			String strPropertyValue, String strTestData, String strReportKeyWord) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(strTestData)
				|| StringUtils.isEmpty(strReportKeyWord)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Fetching Publications RowNumber for {0}", aKeyWord.toString());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));

			int iValidTableRowNum = -1;
			String strPublicationItem = null;
			String strReceipantName = null;
			boolean isStrictCheck = false;
			String strRecipientName = null;

			String strPublicationsPageCount = getWebElementText(aKeyWord, ORConstants.BACKOFFICE_DD_PAGE_COUNT, false);
			String[] strPublicationsPages = StringUtils.split(strPublicationsPageCount, "|");
			int iPublicationsPageNum = strPublicationsPages != null && strPublicationsPages.length > 0
					? Integer.valueOf(StringUtils.trim(strPublicationsPages[strPublicationsPages.length - 1]))
					: 0;
			int iPublicationsPageNo = 0;

			if (!StringUtils.equalsAnyIgnoreCase(strReportKeyWord, ORConstants.DEFAUT_BO_USERS)) {
				strRecipientName = getRunTimeDataValue(testScenarioName, strReportKeyWord);
			} else {
				strRecipientName = strReportKeyWord;
			}
			outerloop: while (iPublicationsPageNo <= iPublicationsPageNum) {
				waitByTime(500);
				List<Integer> lstMachingData = new ArrayList<Integer>();
				WebElement aTable = getWebElement(aKeyWord, ORConstants.BACKOFFICE_DD_TABLE);
				List<WebElement> lstRows = aTable.findElements(By.tagName("tr"));
				for (int i = 0; i < lstRows.size(); i++) {
					int itblRow = i + 1;
					String strPubItm = "//*[@id='ctl00_WebPageHolder_grdSearchResult_ob_grdSearchResultBodyContainer']/div[1]/table/tbody/tr[%d]/td[3]/div/div";
					String strPubItmVal = String.format(strPubItm, itblRow);
					WebElement aWebElement = getWebElement(aKeyWord, strPubItmVal);
					strPublicationItem = getWebElementText(aWebElement, isStrictCheck);

					if (strPublicationItem.equalsIgnoreCase(strTestData)) {
						lstMachingData.add(itblRow);
					}

					itblRow++;
				}
				for (Integer i : lstMachingData) {
					String strRecName = "//*[@id='ctl00_WebPageHolder_grdSearchResult_ob_grdSearchResultBodyContainer']/div[1]/table/tbody/tr[%d]/td[4]/div/div";
					String strRecNameVal = String.format(strRecName, i);
					WebElement aWebElementRcp = getWebElement(aKeyWord, strRecNameVal);
					strReceipantName = getWebElementText(aWebElementRcp, isStrictCheck);
					if (strReceipantName.contains(strRecipientName)) {
						iValidTableRowNum = i;
						String strMatchRow = "//*[@id='ctl00_WebPageHolder_grdSearchResult_ob_grdSearchResultBodyContainer']/div[1]/table/tbody/tr[%d]";
						String strMatchRowVal = String.format(strMatchRow, i);
						WebElement aWebElementMatching = getWebElement(aKeyWord, strMatchRowVal);
						highLightWebElement(aWebElementMatching, aKeyWord, ORConstants.YELLOW_COLOR);
						break outerloop;
					}
				}
				String strPageClick = clickWebElement(aKeyWord, ORConstants.BACKOFFICE_DD_NEXT_PAGE, false);
				if (isTestSetpFailed(strPageClick)) {
					return -1;
				}
				iPublicationsPageNo++;
			}
			return iValidTableRowNum;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return -1;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected int getPublicationsFinaliseTableRowNumber(KeyWordConfigBean aKeyWord, String testScenarioName,
			String strPropertyValue, String strTestData, String strReportKeyWord) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue) || StringUtils.isEmpty(strTestData)
				|| StringUtils.isEmpty(strReportKeyWord)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Fetching Publications RowNumber for {0}", aKeyWord.toString());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aPublicationsWebElement = getWebElement(aKeyWord, String.format(strPropertyValue, strTestData));
			String strPublicationsPageCount = getWebElementText(aKeyWord, ORConstants.BACKOFFICE_DD_FINALIZE_PAGE_COUNT,
					false);
			String[] strPublicationsPages = StringUtils.split(strPublicationsPageCount, "|");
			int iPublicationsPageNum = strPublicationsPages != null && strPublicationsPages.length > 0
					? Integer.valueOf(StringUtils.trim(strPublicationsPages[strPublicationsPages.length - 1]))
					: 0;
			int iPublicationsPageNo = 0;
			while (aPublicationsWebElement == null || !aPublicationsWebElement.isDisplayed()) {
				if (iPublicationsPageNo > iPublicationsPageNum) {
					return -1;
				}
				String strPageClick = clickWebElement(aKeyWord, ORConstants.BACKOFFICE_DD_FINALIZE_NEXT_PAGE, false);
				if (isTestSetpFailed(strPageClick)) {
					return -1;
				}
				aPublicationsWebElement = getWebElement(aKeyWord, String.format(strPropertyValue, strTestData));
				iPublicationsPageNo++;
			}

			int iPublicationsTableRowNum = -1;
			String strRecipientName = getRunTimeDataValue(testScenarioName, strReportKeyWord);
			if (StringUtils.equalsAnyIgnoreCase(strReportKeyWord, ORConstants.DEFAUT_BO_USERS)) {
				iPublicationsTableRowNum = getTableRowByColumn(aKeyWord, ORConstants.BACKOFFICE_DD_FINALIZE_TABLE,
						false, strTestData, strReportKeyWord);
			} else if (StringUtils.isEmpty(StringUtils.trim(strRecipientName))) {
				iPublicationsTableRowNum = getTableRowByColumn(aKeyWord, ORConstants.BACKOFFICE_DD_FINALIZE_TABLE,
						false, strTestData);
			} else {
				iPublicationsTableRowNum = getTableRowByColumn(aKeyWord, ORConstants.BACKOFFICE_DD_FINALIZE_TABLE,
						false, strTestData, strRecipientName);
			}
			return iPublicationsTableRowNum;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return -1;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String getAnchorTagOnClickText(KeyWordConfigBean aKeyWord, String strPropertyValue) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Fetching AnchorTag for {0} with propertyvalue {1}",
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			WebElement aAnchorTagWebElement = getWebElement(aKeyWord, strPropertyValue);
			if (aAnchorTagWebElement == null) {
				return null;
			}
			String strAnchorTagText = aAnchorTagWebElement.getAttribute(ORConstants.ATTRIBUTE_NAME_ONCLICK);
			strAnchorTagText = StringUtils.isEmpty(StringUtils.trim(strAnchorTagText)) ? strAnchorTagText
					: org.jsoup.Jsoup.parse(strAnchorTagText).text();
			if (StringUtils.isEmpty(StringUtils.trim(strAnchorTagText))) {
				strAnchorTagText = getWebElementText(aAnchorTagWebElement, false);
			}
			return StringUtils.trim(strAnchorTagText);
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return null;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String verifyTable(KeyWordConfigBean aKeyWordConfigBean, String testScenarioName, String strPropertyValue,
			String strReportKeyWord, String strExpectedColumnVal) throws Exception {
		if (StringUtils.isEmpty(StringUtils.trim(strPropertyValue))
				|| StringUtils.isEmpty(StringUtils.trim(strReportKeyWord))
				|| StringUtils.isEmpty(StringUtils.trim(strExpectedColumnVal))) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Verifying table {0} for {1} with Expected {2}-{3} ",
				strPropertyValue, aKeyWordConfigBean.toString(), strReportKeyWord, strExpectedColumnVal);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			LinkedHashMap<WebElement, String> mpVerifyTblData = getTableData(aKeyWordConfigBean, testScenarioName,
					strPropertyValue, strReportKeyWord);
			if (mpVerifyTblData == null || mpVerifyTblData.isEmpty()) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			String strTableColumnKeyResult = AppConstants.TEST_RESULT_FAIL;
			for (Entry<WebElement, String> mpVerifyTableEntry : mpVerifyTblData.entrySet()) {
				WebElement aWebElement = mpVerifyTableEntry.getKey();
				String strColumnVal = mpVerifyTableEntry.getValue();
				strTableColumnKeyResult = verifyText(aWebElement, aKeyWordConfigBean, testScenarioName, strColumnVal,
						strExpectedColumnVal, false);
				if (!isTestSetpFailed(strTableColumnKeyResult)) {
					break;
				}
			}
			return strTableColumnKeyResult;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String scrollTo(KeyWordConfigBean aKeyWordConfigBean, String strTestData) throws Exception {
		if (StringUtils.isEmpty(StringUtils.trim(strTestData))) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Scrolling to {0} for keyword {1}", strTestData,
				aKeyWordConfigBean.toString());
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
		try {
			WebDriver aDriver = getWebDriver();
			if (aDriver instanceof AppiumDriver<?>) {
				AppiumDriver<?> aAppiumDriver = (AppiumDriver<?>) aDriver;
				WebUtils.scrollIntoView(aAppiumDriver, strTestData);
				return AppConstants.TEST_RESULT_PASS;
			} else {
				String[] strMoveData = StringUtils.split(strTestData, AppConstants.SEPARATOR_COMMA);
				if (strMoveData == null || strMoveData.length < 2 || strMoveData.length > 2) {
					throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
				}
				String strScrollToJSCmd = String.format(ORConstants.EXEC_JAVA_SCRIPT_SCROLL_TO, strTestData);
				return executeJavaScript(aKeyWordConfigBean, null, strScrollToJSCmd, false);
			}
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String scrollToElement(KeyWordConfigBean aKeyWordConfigBean, String strPropertyValue) throws Exception {
		if (StringUtils.isEmpty(StringUtils.trim(strPropertyValue))) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Scrolling into to {0} for keyword {1}", strPropertyValue,
				aKeyWordConfigBean.toString());
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
		try {
			WebDriver aDriver = getWebDriver();
			if (aDriver instanceof AppiumDriver<?>) {
				WebElement aWebElement = getWebElement(aKeyWordConfigBean, strPropertyValue);
				if (aWebElement == null || !(aWebElement instanceof RemoteWebElement)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				AppiumDriver<?> aAppiumDriver = (AppiumDriver<?>) aDriver;
				Point aLocation = aWebElement.getLocation();
				BrowsersConfigBean aBrowsersConfigBean = getBrowsersConfigBean();
				Browsers aBrowsers = aBrowsersConfigBean == null ? Browsers.INVALID_BROWSER
						: aBrowsersConfigBean.getBrowser();
				StringBuilder strCommand = new StringBuilder();
				if (aBrowsers == Browsers.WINDOWS_NATIVE) {
					strCommand.append(aLocation.getX()).append(AppConstants.SEPARATOR_COMMA).append(aLocation.getY());
				} else {
					strCommand.append(getWebElementText(aWebElement, false));
				}
				WebUtils.scrollIntoView(aAppiumDriver, strCommand.toString());
				return AppConstants.TEST_RESULT_PASS;
			} else {
				return executeJavaScript(aKeyWordConfigBean, strPropertyValue,
						ORConstants.EXEC_JAVA_SCRIPT_SCROLL_BAR_CMD, true);
			}
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String scrollToTop(KeyWordConfigBean aKeyWordConfigBean) throws Exception {
		String strLogMessage = AppUtils.formatMessage("Scrolling keyword {1}", aKeyWordConfigBean.toString());
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
		try {
			WebDriver aDriver = getWebDriver();
			Dimension dimension = aDriver.manage().window().getSize();
			int scrollStart = dimension.getHeight();
			int scrollEnd = 0;
			if (aDriver instanceof AppiumDriver<?>) {
				AppiumDriver<?> aAppiumDriver = (AppiumDriver<?>) aDriver;
				WebUtils.touchScroll(aAppiumDriver, scrollStart, scrollEnd, getDriverSleepTime());
				return AppConstants.TEST_RESULT_PASS;
			} else {
				return executeJavaScript(aKeyWordConfigBean, null, ORConstants.EXEC_JAVA_SCRIPT_SCROLL_TOP_CMD, false);
			}
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String getUnWrapedJsonOrProperty(String testScenarioName, String strObJPropName, String strTestData)
			throws Exception {
		if (StringUtils.isEmpty(StringUtils.trim(testScenarioName))
				|| StringUtils.isEmpty(StringUtils.trim(strObJPropName))
				|| StringUtils.isEmpty(StringUtils.trim(strTestData))) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strPropertyValue = getObJectProperty(strObJPropName);
		strPropertyValue = getFormattedTestData(testScenarioName, strPropertyValue);
		if (StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_TRUE)
				|| StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_FALSE)) {
			return strPropertyValue;
		}
		String strJsonTestData = AppUtils.getJsonData(strTestData);
		if (StringUtils.isEmpty(StringUtils.trim(strPropertyValue))
				|| StringUtils.isEmpty(StringUtils.trim(strJsonTestData))) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		Gson aGson = AppUtils.getDefaultGson();
		Type aTestDataType = new TypeToken<LinkedHashMap<String, Object>>() {
		}.getType();
		LinkedHashMap<String, Object> mpTestData = aGson.fromJson(strJsonTestData, aTestDataType);
		Object objOrPorpValues = mpTestData.get(ORConstants.FORMAT_TESTDATA_ORPROPERTY_JSONKEY);
		if (objOrPorpValues == null || StringUtils.isEmpty(StringUtils.trim(String.valueOf(objOrPorpValues)))) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		Object[] aOrProps = StringUtils.split(String.valueOf(objOrPorpValues), AppConstants.SEPARATOR_COMMA);
		return String.format(strPropertyValue, aOrProps);
	}

	protected String getUnWrapedJsonFormatTestData(String strTestData) throws Exception {
		if (StringUtils.isEmpty(StringUtils.trim(strTestData))) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		if (StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_TRUE)
				|| StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_FALSE)) {
			return strTestData;
		}
		String strJsonTestData = AppUtils.getJsonData(strTestData);
		if (StringUtils.isEmpty(StringUtils.trim(strJsonTestData))) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		Gson aGson = AppUtils.getDefaultGson();
		Type aTestDataType = new TypeToken<LinkedHashMap<String, Object>>() {
		}.getType();
		LinkedHashMap<String, Object> mpTestData = aGson.fromJson(strJsonTestData, aTestDataType);
		Object objOrPorpValues = mpTestData.get(ORConstants.FORMAT_TESTDATA_JSONKEY);
		if (objOrPorpValues == null || StringUtils.isEmpty(StringUtils.trim(String.valueOf(objOrPorpValues)))) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		return String.valueOf(objOrPorpValues);
	}

	protected Pattern getSikuliPattern(File aScreenFile, String strTestData) {
		Pattern aSukuliPattern = null;
		String strJsonTestData = AppUtils.getJsonData(strTestData);
		if (!StringUtils.isEmpty(StringUtils.trim(strJsonTestData))) {
			Gson aGson = AppUtils.getDefaultGson();
			Type aTestDataType = new TypeToken<LinkedHashMap<String, Object>>() {
			}.getType();
			LinkedHashMap<String, Object> mpTestData = aGson.fromJson(strJsonTestData, aTestDataType);
			String strTargetXPos = String.valueOf(mpTestData.get(ORConstants.FORMAT_XOFFSET_JSONKEY));
			String strTargetYPos = String.valueOf(mpTestData.get(ORConstants.FORMAT_YOFFSET_JSONKEY));
			int xPos = NumberUtils.isParsable(strTargetXPos) ? Integer.valueOf(strTargetXPos) : 0;
			int yPos = NumberUtils.isParsable(strTargetYPos) ? Integer.valueOf(strTargetYPos) : 0;
			Location aTargetLocation = new Location(xPos, yPos);
			aSukuliPattern = new Pattern(aScreenFile.getAbsolutePath()).targetOffset(aTargetLocation);
		} else {
			aSukuliPattern = new Pattern(aScreenFile.getAbsolutePath());
		}
		return aSukuliPattern;
	}

	protected String performSikuliActionClick(KeyWordConfigBean aKeyWord, String strPropertyValue, String strTestData)
			throws Exception {
		if (StringUtils.isEmpty(strPropertyValue)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Performing web sikuli action {0} with property {1}",
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			Screen aScreen = new Screen();
			File aScreenFile = AppUtils.getFileFromPath(strPropertyValue);
			if (aScreenFile == null || !aScreenFile.exists()) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			Pattern aSukuliPattern = getSikuliPattern(aScreenFile, strTestData);
			delayInSeconds(2);
			aScreen.click(aSukuliPattern);
			delayInSeconds(2);
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String performSikuliActionClickSpl(KeyWordConfigBean aKeyWord, String strPropertyValue,
			String strTestData) throws Exception {
		if (StringUtils.isEmpty(strPropertyValue)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Performing web sikuli input action {0} with property {1}",
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			File aScreenFile = AppUtils.getFileFromPath(strPropertyValue);
			if (aScreenFile == null || !aScreenFile.exists()) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			WebDriver aWebDriver = getWebDriver();
			WebDriverScreen aWebDriverScreen = new WebDriverScreen(getBrowsersConfigBean(), aWebDriver);
			ScreenLocation aScreenLocation = aWebDriverScreen.findImageElement(strTestData, aScreenFile,
					(int) getDriverExplicitWaitTime());
			if (aScreenLocation == null) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			delayInSeconds(2);
			Browsers aBrowsers = getBrowser();
			if (!WebUtils.isTouchScreenDriver(aWebDriver, aBrowsers)) {
				Mouse aMouse = new DesktopMouse();
				aMouse.click(aScreenLocation);
			} else {
				Actions aScroll = new Actions(aWebDriver);
				aScroll.moveByOffset(aScreenLocation.getX(), aScreenLocation.getY()).click().build().perform();
			}
			delayInSeconds(2);
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String performSikuliInput(KeyWordConfigBean aKeyWord, String strPropertyValue, String strTestData)
			throws Exception {
		if (StringUtils.isEmpty(strPropertyValue)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Performing web sikuli input action {0} with property {1}",
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			Screen aScreen = new Screen();
			File aScreenFile = AppUtils.getFileFromPath(strPropertyValue);
			if (aScreenFile == null || !aScreenFile.exists()) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			Pattern aSukuliPattern = getSikuliPattern(aScreenFile, strTestData);
			delayInSeconds(2);
			String strJsonTestData = AppUtils.getJsonData(strTestData);
			if (!StringUtils.isEmpty(StringUtils.trim(strJsonTestData))) {
				Gson aGson = AppUtils.getDefaultGson();
				Type aTestDataType = new TypeToken<LinkedHashMap<String, Object>>() {
				}.getType();
				LinkedHashMap<String, Object> mpTestData = aGson.fromJson(strJsonTestData, aTestDataType);
				strTestData = String.valueOf(mpTestData.get(ORConstants.FORMAT_TESTDATA_JSONKEY));
			}
			aScreen.type(aSukuliPattern, strTestData);
			delayInSeconds(2);
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String performSikuliInputSpl(KeyWordConfigBean aKeyWord, String strPropertyValue, String strTestData)
			throws Exception {
		if (StringUtils.isEmpty(strPropertyValue)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Performing web sikuli input action {0} with property {1}",
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			File aScreenFile = AppUtils.getFileFromPath(strPropertyValue);
			if (aScreenFile == null || !aScreenFile.exists()) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			WebDriver aWebDriver = getWebDriver();
			WebDriverScreen aWebDriverScreen = new WebDriverScreen(getBrowsersConfigBean(), aWebDriver);
			ScreenLocation aScreenLocation = aWebDriverScreen.findImageElement(strTestData, aScreenFile,
					(int) getDriverExplicitWaitTime());
			if (aScreenLocation == null) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			String strJsonTestData = AppUtils.getJsonData(strTestData);
			if (!StringUtils.isEmpty(StringUtils.trim(strJsonTestData))) {
				Gson aGson = AppUtils.getDefaultGson();
				Type aTestDataType = new TypeToken<LinkedHashMap<String, Object>>() {
				}.getType();
				LinkedHashMap<String, Object> mpTestData = aGson.fromJson(strJsonTestData, aTestDataType);
				strTestData = String.valueOf(mpTestData.get(ORConstants.FORMAT_TESTDATA_JSONKEY));
			}
			Browsers aBrowsers = getBrowser();
			if (!WebUtils.isTouchScreenDriver(aWebDriver, aBrowsers)) {
				Mouse aDesktopMouse = new DesktopMouse();
				aDesktopMouse.click(aScreenLocation);
				delayInSeconds(1);
				Keyboard aDesktopKeyboard = new DesktopKeyboard();
				aDesktopKeyboard.type(strTestData);
			} else {
				Actions aScroll = new Actions(aWebDriver);
				int iXoff = aScreenLocation.getX();
				int iYoff = aScreenLocation.getY();
				aScroll.moveByOffset(iXoff, iYoff).doubleClick().sendKeys(strTestData).build().perform();
			}
			delayInSeconds(2);
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String updateALMDetails(String strScenarioName, KeyWordConfigBean aKeyWordConfigBean,
			String strTestData) {
		MasterConfig aMasterConfig = MasterConfig.getInstance();
		AppEnvConfigBean aAppEnvConfig = aMasterConfig.getAppEnvConfigBean();
		TestSuiteBean aTestSuiteBean = getTestSuite();
		ALMWrapperConfigBean almWrapperConfigBean = aTestSuiteBean == null ? null
				: aTestSuiteBean.getALMWrapperConfigBean();
		String strLogMessage = AppUtils.formatMessage("Performing {0} Data {1}", aKeyWordConfigBean.toString(),
				strTestData);
		try {
			if (StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_TRUE)
					|| StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_FALSE) || aAppEnvConfig == null
					|| almWrapperConfigBean == null || aTestSuiteBean == null) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			switch (aKeyWordConfigBean.getKeyWord()) {
			case ALM_TESTSET_NAME:
				almWrapperConfigBean.setALMTestSetName(strTestData);
				return AppConstants.TEST_RESULT_PASS;
			case ALM_TESTSET_PATH:
				almWrapperConfigBean.setALMTestSetPath(strTestData);
				return AppConstants.TEST_RESULT_PASS;
			case ALM_TESTSET_ID:
				almWrapperConfigBean.setALMTestSetID(strTestData);
				return AppConstants.TEST_RESULT_PASS;
			case ALM_TESTCASE_STATUS:
				almWrapperConfigBean.setALMTestCaseStatus(strTestData);
				return AppConstants.TEST_RESULT_PASS;
			default:
				return AppConstants.TEST_RESULT_FAIL;
			}
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String loginUsingAPI(KeyWordConfigBean aKeyWord, String strPropertyValue, String strTestData)
			throws Exception {
		if (StringUtils.isEmpty(strPropertyValue)
				|| StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_TRUE)
				|| StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_FALSE)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Performing API Login action {0} with property {1}",
				aKeyWord.toString(), strPropertyValue);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			String[] strPropertyData = StringUtils.split(strPropertyValue, AppConstants.SEPARATOR_COMMA);
			String strJsonTestData = AppUtils.getJsonData(strTestData);
			if (strPropertyData == null || strPropertyData.length < 2
					|| StringUtils.isEmpty(StringUtils.trim(strJsonTestData))) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			String strUrl = strPropertyData[0];
			String strDomain = AppUtils.getHostNameFromURL(strPropertyData[1]);
			JSONObject payload = new JSONObject(strJsonTestData);
			Map<String, String> cookies = AppUtils.postCallGetCookies(payload, strUrl);
			SessionManager aBrowserSessionMager = getSessionManager();
			aBrowserSessionMager.byPassLoginUsingCookies(cookies, strDomain);
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected String verifyExcelColumns(String testScenarioName, String strReportKeyWord, KeyWordConfigBean aKeyword,
			String strPropertyValue, String strTestData) throws Exception {
		if (StringUtils.isEmpty(testScenarioName) || StringUtils.isEmpty(strPropertyValue)
				|| StringUtils.isEmpty(strTestData)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		String strLogMessage = AppUtils.formatMessage("Verifying excel file for test case {0} and {1}",
				testScenarioName, aKeyword.toString());

		String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);

		strTestData = StringUtils.trim(strTestData);
		if (StringUtils.isEmpty(strTestData) || StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_TRUE)
				|| StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_FALSE)
				|| StringUtils.indexOf(strTestData, AppConstants.SEPARATOR_COMMA) <= 0) {
			LOGGER.error(strErrorMsg);
			return AppConstants.TEST_RESULT_FAIL;
		}
		String[] strTestHeaders = StringUtils.split(strTestData, AppConstants.SEPARATOR_COMMA);
		boolean bHeadersMatch = false;
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			File aDwFile = getDlownloadFile(testScenarioName, strReportKeyWord, AppConstants.EXCEL_REPORT_EXTENSION); // need
			try (XSSFWorkbook aWorkbook = new XSSFWorkbook(aDwFile)) {
				String strSheetName = strPropertyValue;
				Sheet aSheet = aWorkbook.getSheet(strSheetName);
				if (aSheet == null) {
					throw new IOException(
							MessageFormat.format(ErrorMsgConstants.SHEET_NOT_FOUND, strSheetName, aDwFile.getPath()));
				}
				LinkedHashMap<String, Integer> colMapByName = ExcelUtils.getColumnNames(aDwFile, aSheet, strSheetName);
				if (MapUtils.isEmpty(colMapByName)) {
					return AppConstants.TEST_RESULT_FAIL;
				}
				Set<String> stColumnNames = colMapByName.keySet();
				bHeadersMatch = stColumnNames.containsAll(Arrays.asList(strTestHeaders));
			}
			return bHeadersMatch ? AppConstants.TEST_RESULT_PASS : AppConstants.TEST_RESULT_FAIL;
		} catch (Exception ex) {
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected synchronized String clickFileToDownload(String strReportKeyWord, KeyWordConfigBean aKeyWord,
			String testScenarioName, String strPropertyValue, String strTestData, boolean isStrict) throws Exception {
		if (StringUtils.isEmpty(testScenarioName) || StringUtils.isEmpty(strPropertyValue)
				|| StringUtils.isEmpty(strTestData)
				|| StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_TRUE)
				|| StringUtils.equalsIgnoreCase(strTestData, AppConstants.DEFAULT_FALSE)) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}

		String strLogMessage = AppUtils.formatMessage("Downloading file {0} for Scenario {1} using keyword {2} ",
				strTestData, testScenarioName, aKeyWord.toString());
		String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			Object oldopenTabs[] = getOpenedTabs();
			int iOldTabCount = oldopenTabs == null ? 0 : oldopenTabs.length;
			String strResult = clickWebElement(aKeyWord, strPropertyValue, isStrict);
			if (isTestSetpFailed(strResult)) {
				return strResult;
			}
			delayInSeconds(3);
			File aDWPDFFile = null;
			try {
				aDWPDFFile = getDlownloadFile(testScenarioName, strReportKeyWord, strTestData);
				if (aDWPDFFile == null || !aDWPDFFile.exists()) {
					strResult = AppConstants.TEST_RESULT_FAIL;
				}
			} catch (Exception ex) {
				strResult = AppConstants.TEST_RESULT_FAIL;
			}
			if (isTestSetpFailed(strResult)) {
				strResult = performControlClick(aKeyWord, strPropertyValue);
				if (isTestSetpFailed(strResult)) {
					return strResult;
				}
				aDWPDFFile = getDlownloadFile(testScenarioName, strReportKeyWord, strTestData);
				if (aDWPDFFile == null || !aDWPDFFile.exists()) {
					return AppConstants.TEST_RESULT_FAIL;
				}
			}

			if (aDWPDFFile == null || !aDWPDFFile.exists()) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			Object newopenTabs[] = getOpenedTabs();
			int iNewTabCount = newopenTabs == null ? 0 : newopenTabs.length;

			if (iNewTabCount > iOldTabCount) {
				switchToNewWindow(false);
				closeWindow();
				if (iOldTabCount == 1) {
					switchToParentWindow();
				}
			}
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	protected synchronized File getGridDlownloadFile(AppEnvConfigBean aPPRunEnv, String strTestCaseID,
			String strReportKeyWord, String strFilePrefix) throws Exception {

		String strHubBrowserHost = String.format("%s:%s", aPPRunEnv.getHostAddress(), aPPRunEnv.getHostPort());
		String strHUBFormat = PropertyHandler.getExternalString(AppConstants.SELINIUM_NODE_URL_FORMAT_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		RemoteWebDriver aRemoteWebDriver = (RemoteWebDriver) getWebDriver();
		String strHUBURL = String.format(strHUBFormat, strHubBrowserHost);
		SessionId aSessionId = aRemoteWebDriver.getSessionId();
		String strFilesEndPoint = String.format("/session/%s/se/files", aSessionId);
		URL aGridUrl = new URL(strHUBURL);
		JSONObject aJsonObject;
		try (HttpClient aGridClient = HttpClient.Factory.createDefault().createClient(aGridUrl);) {
			HttpRequest aReqGetFiles = new HttpRequest(HttpMethod.GET, strFilesEndPoint);
			HttpResponse aGetFileResponse = aGridClient.execute(aReqGetFiles);
			int iStatusCode = aGetFileResponse.getStatus();
			if (!aGetFileResponse.isSuccessful()) {
				String strErrorMessage = EnglishReasonPhraseCatalog.INSTANCE.getReason(iStatusCode, Locale.US);
				throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_TELEGRAM_NOTIFICATION,
						strFilesEndPoint, String.valueOf(iStatusCode), strErrorMessage));
			}
			String strResponseData = org.openqa.selenium.remote.http.Contents.string(aGetFileResponse);
			aJsonObject = new JSONObject(strResponseData);
			JSONObject aValue = aJsonObject.getJSONObject("value");
			JSONArray aFileNames = aValue.getJSONArray("names");
			if (aFileNames == null) {
				throw new IOException(MessageFormat.format(ErrorMsgConstants.FILENTFOUND, strFilesEndPoint));
			}
			List<String> lstFiles = new LinkedList<>();
			aFileNames.forEach(aFile -> {
				String strFileName = aFile.toString();
				lstFiles.add(strFileName);
			});
			LinkedList<String> lstGridDwFiles = getApplicationContext().getGridDownloadedFiles(strTestCaseID,
					aSessionId);
			int iLastElement = CollectionUtils.size(lstFiles) - 1;
			String strFileToDw = CollectionUtils.isEmpty(lstGridDwFiles) ? lstFiles.get(iLastElement)
					: lstFiles.stream().filter(strFileName -> {
						return !lstGridDwFiles.contains(strFileName);
					}).findFirst().orElse(null);
			if (StringUtils.isEmpty(StringUtils.trim(strFileToDw))) {
				throw new IOException(MessageFormat.format(ErrorMsgConstants.FILENTFOUND, strFilesEndPoint));
			}
			// Download the file
			HttpRequest aDwFileReq = new HttpRequest(HttpMethod.POST, strFilesEndPoint);
			String strFileDwPayLoad = new Json().toJson(Collections.singletonMap("name", strFileToDw));
			aDwFileReq.setContent(() -> new ByteArrayInputStream(strFileDwPayLoad.getBytes()));
			HttpResponse aPostResponse = aGridClient.execute(aDwFileReq);
			iStatusCode = aPostResponse.getStatus();
			if (!aPostResponse.isSuccessful()) {
				String strErrorMessage = EnglishReasonPhraseCatalog.INSTANCE.getReason(iStatusCode, Locale.US);
				throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_TELEGRAM_NOTIFICATION,
						strFilesEndPoint, String.valueOf(iStatusCode), strErrorMessage));
			}
			strResponseData = org.openqa.selenium.remote.http.Contents.string(aPostResponse);
			aJsonObject = new JSONObject(strResponseData);
			JSONObject aFileResponseValue = aJsonObject.getJSONObject("value");
			if (aFileResponseValue == null) {
				throw new IOException(MessageFormat.format(ErrorMsgConstants.FILENTFOUND, strFilesEndPoint));
			}
			String strFileName = aFileResponseValue.getString("filename");
			String strEncodedContents = aFileResponseValue.getString("contents");
			if (StringUtils.isEmpty(StringUtils.trim(strEncodedContents))) {
				throw new IOException(MessageFormat.format(ErrorMsgConstants.FILENTFOUND, strFilesEndPoint));
			}
			File aTrgtFile = getScenarioDlownloadFile(strTestCaseID, strReportKeyWord, strFileName);
			Zip.unzip(strEncodedContents, aTrgtFile.getParentFile());
			File aDwFile = Paths.get(aTrgtFile.getParent(), strFileName).toFile();
			if (!aDwFile.exists()) {
				throw new IOException(MessageFormat.format(ErrorMsgConstants.FILENTFOUND, aDwFile.getPath()));
			}
			if (!aDwFile.renameTo(aTrgtFile)) {
				throw new IOException(MessageFormat.format(ErrorMsgConstants.FILENTFOUND, aTrgtFile.getPath()));
			}
			getApplicationContext().addGridDownloadedFile(strTestCaseID, aSessionId, strFileName);
			getApplicationContext().addDownloadedFile(strTestCaseID, strReportKeyWord, aTrgtFile);
			return aTrgtFile;
		}
	}
}
