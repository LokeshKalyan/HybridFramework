/****************************************************************************
 * File Name 		: TestStepReport.java
 * Package			: com.dxc.zurich.reports
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
package com.abc.project.reports;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.abc.project.beans.AppEnvConfigBean;
import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.config.AppConfig;
import com.abc.project.config.AppContext;
import com.abc.project.config.MasterConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.ErrorMsgConstants;
import com.abc.project.constants.SummaryReportConstants;
import com.abc.project.enums.AppRunMode;
import com.abc.project.enums.Browsers;
import com.abc.project.enums.StartFinish;
import com.abc.project.grid.GridClientHelper;
import com.abc.project.utils.AppUtils;
import com.abc.project.utils.PropertyHandler;
import com.abc.project.utils.WebUtils;
import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Log;
import com.aventstack.extentreports.model.Media;
import com.aventstack.extentreports.model.ScreenCapture;
import com.aventstack.extentreports.model.Test;
import com.aventstack.extentreports.observer.ExtentObserver;
import com.aventstack.extentreports.reporter.ExtentKlovReporter;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import com.epam.healenium.SelfHealingDriver;
import com.mongodb.MongoClientURI;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategy;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 12:57:07 pm
 */
public class TestStepReport {

	private static Map<String, ExtentTest> extentTestMap = new HashMap<>();
	private static Map<String, ExtentReports> extentMap = new HashMap<>();

	private static final String SCREEN_FOLDERNAME = "Snapshots";
	private static final String FAILED_SCREEN_FOLDERNAME = "FailedScenarios-Snapshots";
	private static final String VIEW_SCREENSHOT = "View Screenshot";

	public static final int SCENARIOS_FILE_NAME_LENGTH = 70;

	private static final Logger LOGGER = LogManager.getLogger(TestStepReport.class);
	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	private static ExtentReports getDefaultExtentReport(BrowsersConfigBean aBrowsersConfigBean, File aReportFile,
			boolean bKolvReport) {
		Browsers aBrowser = aBrowsersConfigBean.getBrowser();
		LinkedList<ExtentObserver<?>> lstExtentObservers = new LinkedList<>();
		ExtentSparkReporter aExtentSparkReporter = getSparkReporter(aReportFile);
		lstExtentObservers.add(aExtentSparkReporter);
		String strReqHistoricData = AppUtils.getEnvPropertyValue(AppConstants.REPORT_INITILIZE_HISTORIC_TESTS_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		if (bKolvReport && BooleanUtils.toBoolean(strReqHistoricData)) {// Check for historical reports is enabled
			ExtentKlovReporter klovReporter = getKlovReporter();
			lstExtentObservers.add(klovReporter);
		}
		ExtentReports aExtentReport = new ExtentReports();
		aExtentReport.attachReporter(lstExtentObservers.toArray(new ExtentObserver[lstExtentObservers.size()]));
		aExtentReport.setAnalysisStrategy(AnalysisStrategy.TEST);
		List<String> lstMediaResolverPath = new LinkedList<>();
		lstMediaResolverPath.add(AppConfig.getInstance().getBrowserExecutionReportFolder(aBrowsersConfigBean));
		if (bKolvReport && BooleanUtils.toBoolean(strReqHistoricData)) {// Check for historical reports is enabled
			aExtentReport.setMediaResolverPath(lstMediaResolverPath.toArray(new String[lstMediaResolverPath.size()]));
		}
//		List<Status> statusHierarchy = Arrays.asList(Status.FATAL, Status.FAIL, Status.ERROR, Status.WARNING,
//				Status.SKIP, Status.PASS, Status.DEBUG, Status.INFO);
//		aExtentReport.config().statusConfigurator().setStatusHierarchy(statusHierarchy);
		//
		String strBuildVersion = PropertyHandler.getExternalString(AppConstants.REPORT_TESTS_BUILD_VERSION_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		aExtentReport.setSystemInfo(AppConstants.HOST_NAME, AppUtils.getHostName());
		aExtentReport.setSystemInfo(SummaryReportConstants.BROWSER_HEADER, aBrowser.getBrowserShortName());
		aExtentReport.setSystemInfo(AppConstants.EXECUTION_ENV, getExecutionEnv());
		aExtentReport.setSystemInfo(AppConstants.USER_NAME, AppUtils.getSystemUserName());
		aExtentReport.setSystemInfo(AppConstants.BUILD_NAME, strBuildVersion);
		return aExtentReport;
	}

	/***
	 * Fetches the Execution ENV
	 * 
	 * @return
	 */
	private static String getExecutionEnv() {

		String strAPPName = MasterConfig.getInstance().getAppName();
		String strAPPENV = MasterConfig.getInstance().getAppENV();
		if (StringUtils.isEmpty(StringUtils.trim(strAPPENV))) {
			return strAPPName;
		}
		return String.format("%s - %s", strAPPName, strAPPENV);
	}

	/**
	 * Fetches Klov Reporter to save historical reports
	 * 
	 * @return
	 */
	private static ExtentKlovReporter getKlovReporter() {
		String strDBURL = PropertyHandler.getExternalString(AppConstants.REPORT_HISTORIC_TESTS_DB_URL_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		String strServerURL = PropertyHandler.getExternalString(AppConstants.REPORT_HISTORIC_TESTS_URL_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		MasterConfig aMasterConfig = MasterConfig.getInstance();
		ExtentKlovReporter klovReporter = new ExtentKlovReporter(aMasterConfig.getAppName(),
				aMasterConfig.getAppRunID());
		MongoClientURI aMongoClientURI = new MongoClientURI(strDBURL);
		klovReporter.initMongoDbConnection(aMongoClientURI);
		klovReporter.setProjectName(aMasterConfig.getAppName());
		klovReporter.setReportName(aMasterConfig.getAppRunID());
		klovReporter.initKlovServerConnection(strServerURL);
		return klovReporter;
	}

	/***
	 * Fetches Spark Reporter to save HTML reports
	 * 
	 * @param strReportFileName
	 * @return
	 */
	private static ExtentSparkReporter getSparkReporter(File aReportFile) {
		String strHTMLDocTitle = PropertyHandler.getExternalString(AppConstants.HTML_REPORT_DOC_TITLE_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		String strHTMLReportName = PropertyHandler.getExternalString(AppConstants.HTML_REPORT_DOC_NAME_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		ExtentSparkReporter aExtentSparkReporter = new ExtentSparkReporter(aReportFile);
		aExtentSparkReporter.config().setDocumentTitle(strHTMLDocTitle);
		aExtentSparkReporter.config().setReportName(strHTMLReportName);
		aExtentSparkReporter.config().setTheme(Theme.DARK);
		aExtentSparkReporter.config().setTimelineEnabled(true);
		aExtentSparkReporter.config().enableOfflineMode(false);
		aExtentSparkReporter.config().setOfflineMode(false);
		aExtentSparkReporter.config().thumbnailForBase64(true);
		aExtentSparkReporter.viewConfigurer().viewOrder().as(new ViewName[] { ViewName.DASHBOARD, ViewName.TEST,
				ViewName.AUTHOR, ViewName.DEVICE, ViewName.EXCEPTION, ViewName.LOG }).apply();
		return aExtentSparkReporter;
	}

	/***
	 * Fetches the extent report based on browser name
	 * 
	 * @param strBrowserName
	 * @return
	 */
	private static ExtentReports getExtentReport(BrowsersConfigBean aBrowsersConfigBean, String strScenarioName) {
		File aReportFile = getReportFile(aBrowsersConfigBean, strScenarioName);
		ExtentReports aExtentReport = getDefaultExtentReport(aBrowsersConfigBean, aReportFile, false);
		return aExtentReport;
	}

	/***
	 * Fetches the extent report based on browser name
	 * 
	 * @param strBrowserName
	 * @return
	 */
	private static synchronized ExtentReports getExtentReport(BrowsersConfigBean aBrowsersConfigBean) {
		String strBrowserName = aBrowsersConfigBean.getReportBrowserName();
		ExtentReports aExtentReport = extentMap.get(strBrowserName);
		if (aExtentReport == null) {
			File aReportFile = getReportFile(aBrowsersConfigBean);
			aExtentReport = getDefaultExtentReport(aBrowsersConfigBean, aReportFile, true);
			extentMap.put(strBrowserName, aExtentReport);
		}
		return aExtentReport;
	}

	/***
	 * Creates report file name configured in application properties
	 * 
	 * @param strBrowserName
	 * @return
	 */
	private static File getReportFile(BrowsersConfigBean aBrowsersConfigBean) {
		String strReportFolder = AppConfig.getInstance().getBrowserExecutionReportFolder(aBrowsersConfigBean);
		String strReportName = PropertyHandler.getExternalString(AppConstants.HTML_REPORT_NAME_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		Browsers aBrowser = aBrowsersConfigBean.getBrowser();
		String strExtentReportName = String.format("%s_%s_%s", MasterConfig.getInstance().getAppRunID(),
				AppUtils.getBrowserExecutionFileName(aBrowser.getBrowserName()), strReportName);
		String strBaseName = FilenameUtils.getBaseName(strExtentReportName);
		String strExtension = FilenameUtils.getExtension(strExtentReportName);
		strExtentReportName = String.format(AppConstants.REPORT_FILE_NAME_FORMAT,
				AppUtils.removeIllegalCharacters(strBaseName, true), strExtension);
		AppContext aAppContext = AppContext.getInstance();
		Path aReportPath = Paths.get(strReportFolder, strExtentReportName);
		File aReportFile = aReportPath.toFile();
		aAppContext.addExecutionReports(aReportFile);
		return aReportFile;
	}

	/***
	 * Creates report file name configured in application properties
	 * 
	 * @param strBrowserName
	 * @return
	 */
	private static File getReportFile(BrowsersConfigBean aBrowsersConfigBean, String testScenarioName) {
		String strReportFolder = AppConfig.getInstance().getBrowserExecutionReportFolder(aBrowsersConfigBean);
		String strReportName = String.format("%s_%s_%s%s", MasterConfig.getInstance().getAppRunID(),
				AppUtils.getScenarioReportFileName(testScenarioName, 0), AppUtils.getFileDate(),
				AppConstants.HTML_REPORT_EXTENSION);
		String strBaseName = FilenameUtils.getBaseName(strReportName);
		String strExtension = FilenameUtils.getExtension(strReportName);
		strReportName = String.format(AppConstants.REPORT_FILE_NAME_FORMAT,
				AppUtils.removeIllegalCharacters(strBaseName, true), strExtension);
		String strScenarioPath = AppUtils.getScenarioReportFileName(testScenarioName, SCENARIOS_FILE_NAME_LENGTH);
		Path aReportPath = Paths.get(strReportFolder, AppUtils.removeIllegalCharacters(strScenarioPath, true),
				strReportName);
		File aReportFile = aReportPath.toFile();
		return aReportFile;
	}

	private static ExtentTest getTest(BrowsersConfigBean aBrowsersConfigBean, String testScenarioName,
			String stepDescription) {
		ExtentTest test = extentTestMap.get(testScenarioName);
		ExtentReports extent = getExtentReport(aBrowsersConfigBean);
		if (test == null) {
			test = getExtentTest(extent, aBrowsersConfigBean, testScenarioName, stepDescription);
			extentTestMap.put(testScenarioName, test);
		}
		return test;
	}

	private static String getAssignCategory() {
		String strBuildVersion = PropertyHandler.getExternalString(AppConstants.REPORT_TESTS_BUILD_VERSION_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		String strAPPENV = MasterConfig.getInstance().getAppENV();
		String strReportRunDate = AppUtils.getDateAsString(new Date(), AppConstants.DATE_FORMAT_DD_MM_YYYY);
		if (StringUtils.isEmpty(StringUtils.trim(strAPPENV))) {
			return String.format("%s - Run Date (%s)", strBuildVersion, strReportRunDate);
		}
		return String.format("%s - %s - Run Date (%s)", strBuildVersion, strAPPENV, strReportRunDate);
	}

	/***
	 * Fetches Extent Test
	 * 
	 * @param extent
	 * @param aBrowsersConfigBean
	 * @param testScenarioName
	 * @param stepDescription
	 * @return
	 */
	private static ExtentTest getExtentTest(ExtentReports extent, BrowsersConfigBean aBrowsersConfigBean,
			String testScenarioName, String stepDescription) {
		ExtentTest test = extent.createTest(testScenarioName, AppUtils.removeInvisbleCharacters(stepDescription))
				.assignAuthor(AppUtils.getSystemUserName()).assignCategory(getAssignCategory())
				.assignDevice(aBrowsersConfigBean.getBrowserHost());
		return test;
	}

	/***
	 * Used to generate report
	 * 
	 * @param aBrowser
	 */
	private static synchronized void defaultflushReport(BrowsersConfigBean aBrowsersConfigBean,
			String strScenarioName) {
		try {

			ExtentReports extent = getExtentReport(aBrowsersConfigBean);
			extent.flush();
			String[] strSplitData = StringUtils.split(strScenarioName, AppConstants.SEPARATOR_SEMICOLON);
			if (StringUtils.isNotEmpty(StringUtils.trim(strScenarioName)) && strSplitData != null
					&& strSplitData.length == 2 && AppUtils.canCreateSnapShotScenarioDoc()) {
				final String strScnName = strSplitData[0];
				strScenarioName = strScnName;
				String stepDescription = strSplitData[1];
				ExtentReports scenarioExtent = getExtentReport(aBrowsersConfigBean, strScenarioName);
				ExtentTest test = extentTestMap.get(strScenarioName);
				if (test != null) {
					ExtentTest scnText = getExtentTest(scenarioExtent, aBrowsersConfigBean, strScenarioName,
							stepDescription);
					Test aTest = test.getModel();
					Test aScnTest = scnText.getModel();
					if (aTest != null && aScnTest != null) {
						List<Log> lstTestLogs = aTest.getLogs();
						List<Log> lstTestGenLogs = aTest.getGeneratedLog();
						lstTestLogs.forEach(aLog -> {
							Log aModiFiedLog = getScenarioFormatedLog(aLog, strScnName);
							aScnTest.addLog(aModiFiedLog);
						});
						lstTestGenLogs.forEach(aLog -> {
							Log aModiFiedLog = getScenarioFormatedLog(aLog, strScnName);
							aScnTest.addLog(aModiFiedLog);
						});
						scenarioExtent.flush();
					}
				}
			}
		} catch (Exception ex) {
			String strMessage = "";
			if (StringUtils.isEmpty(strScenarioName)) {
				strMessage = MessageFormat.format(ErrorMsgConstants.ERR_REPORT_GENERATION,
						aBrowsersConfigBean.getBrowserDisplayName());
			} else {
				strMessage = MessageFormat.format(ErrorMsgConstants.ERR_SCN_REPORT_GENERATION, strScenarioName,
						aBrowsersConfigBean.getBrowserDisplayName());
			}
			LOGGER.error(strMessage);
			ERROR_LOGGER.error(strMessage, ex);
		}
	}

	private synchronized static Log getScenarioFormatedLog(final Log aLog, String strScenarioName) {

		if (aLog.getMedia() != null && aLog.getMedia().getPath() != null) {
			String strMediaPath = aLog.getMedia().getPath();
			String strRemoveScn = String.format("%s/%s",
					AppUtils.getScenarioReportFileName(strScenarioName, SCENARIOS_FILE_NAME_LENGTH), SCREEN_FOLDERNAME);
			String strReplaceTest = String.format("./%s", SCREEN_FOLDERNAME);
			String strReleativePath = RegExUtils.replaceFirst(strMediaPath, strRemoveScn, strReplaceTest);
//			Media aMedia = ScreenCapture.builder().path(strFromatedMediaPath).build();
			String strFormatedLog = String.format("%s<a href=%s target=\"_blank\"> %s</a>", aLog.getDetails(),
					strReleativePath, VIEW_SCREENSHOT);
			Log aFormatedLog = new Log(aLog.getTimestamp(), aLog.getStatus(), strFormatedLog, aLog.getSeq(), null,
					aLog.getException());
			return aFormatedLog;
		}
		return aLog;

	}

	public static void clearTestStepReport() {
		if (extentTestMap != null) {
			extentTestMap.clear();
			extentTestMap = new HashMap<>();
		}
		if (extentMap != null) {
			extentMap.clear();
			extentMap = new HashMap<>();
		}
	}

	/***
	 * Used to generate report
	 * 
	 * @param aBrowser
	 */
	public static synchronized void flushReport(BrowsersConfigBean aBrowsersConfigBean, String strScenarioName) {
		try {
			AppEnvConfigBean aPPRunEnv = MasterConfig.getInstance().getAppEnvConfigBean();
			AppRunMode aAppRunMode = aPPRunEnv.getAppRunMode();
			switch (aAppRunMode) {
			case SELENIUM_NODE:
				GridClientHelper.flushReport(ERROR_LOGGER, aPPRunEnv, aBrowsersConfigBean, strScenarioName);
				defaultflushReport(aBrowsersConfigBean, strScenarioName);
				break;
			default:
				defaultflushReport(aBrowsersConfigBean, strScenarioName);
				break;
			}
		} catch (Exception ex) {
			String strMessage = "";
			if (StringUtils.isEmpty(strScenarioName)) {
				strMessage = MessageFormat.format(ErrorMsgConstants.ERR_REPORT_GENERATION,
						aBrowsersConfigBean.getBrowserDisplayName());
			} else {
				strMessage = MessageFormat.format(ErrorMsgConstants.ERR_SCN_REPORT_GENERATION, strScenarioName,
						aBrowsersConfigBean.getBrowserDisplayName());
			}
			LOGGER.error(strMessage);
			ERROR_LOGGER.error(strMessage, ex);
		}
	}

	private static synchronized String logReport(Status aStatus, BrowsersConfigBean aBrowsersConfigBean,
			String testScenarioName, String stepDescription, String strLogMessage, boolean isSnapRequired,
			WebDriver driver) {
		if (isSnapRequired) {
			File aScreenShotFile = captureFullScreen(driver, aBrowsersConfigBean, testScenarioName);
			if (aScreenShotFile != null && aScreenShotFile.exists()) {
				AppContext aAppContext = AppContext.getInstance();
				aAppContext.addDocumentTestReport(testScenarioName, aBrowsersConfigBean, aScreenShotFile.getName(),
						strLogMessage);
			} else {
				aStatus = aStatus != Status.FAIL ? Status.FAIL : aStatus;
				strLogMessage = String.format("%s >> Failed to captureFullScreen for Browser %s", strLogMessage,
						aBrowsersConfigBean.getBrowserDisplayName());
			}
			return logReport(aStatus, aBrowsersConfigBean, testScenarioName, stepDescription, strLogMessage,
					aScreenShotFile);
		} else {
			return logReport(aStatus, aBrowsersConfigBean, testScenarioName, stepDescription, strLogMessage, null);
		}

	}

	public static synchronized String defaultlogReport(Status aStatus, BrowsersConfigBean aBrowsersConfigBean,
			String testScenarioName, String stepDescription, String strLogMessage, File aSnapShotFile) {

		ExtentTest test = getTest(aBrowsersConfigBean, testScenarioName, stepDescription);
		XMLTestStepReport.logXMLReport(aStatus, aBrowsersConfigBean, testScenarioName, stepDescription, strLogMessage);
		if (aSnapShotFile != null) {
			if (!aSnapShotFile.exists()) {
				test.log(Status.FAIL, strLogMessage);
				return AppConstants.TEST_RESULT_FAIL;
			}
			String strBrowserReportFolder = AppConfig.getInstance()
					.getBrowserExecutionReportFolder(aBrowsersConfigBean);
			File aBaseFile = new File(strBrowserReportFolder);
			String strReleativePath = aBaseFile.toURI().relativize(aSnapShotFile.toURI()).getPath();
//			String strFormatedLog = String.format("%s<a href=%s target=\"_blank\"> %s</a>", strLogMessage,
//					strReleativePath, VIEW_SCREENSHOT);

			Media aMedia = ScreenCapture.builder().path(strReleativePath)
					.title(FilenameUtils.getBaseName(aBaseFile.getName())).build();
			test.log(aStatus, strLogMessage, aMedia);
//			test.log(aStatus, strFormatedLog);
			return AppConstants.TEST_RESULT_PASS;
		} else {
			test.log(aStatus, strLogMessage);
			return AppConstants.TEST_RESULT_PASS;
		}
	}

	private static synchronized String logReport(Status aStatus, BrowsersConfigBean aBrowsersConfigBean,
			String testScenarioName, String stepDescription, String strLogMessage, File aSnapShotFile) {
		AppEnvConfigBean aPPRunEnv = MasterConfig.getInstance().getAppEnvConfigBean();
		AppRunMode aAppRunMode = aPPRunEnv.getAppRunMode();
		switch (aAppRunMode) {
		case SELENIUM_NODE:
			GridClientHelper.logTestReport(LOGGER, ERROR_LOGGER, aPPRunEnv, aStatus, aBrowsersConfigBean,
					testScenarioName, stepDescription, strLogMessage, aSnapShotFile);
			return defaultlogReport(aStatus, aBrowsersConfigBean, testScenarioName, stepDescription, strLogMessage,
					aSnapShotFile);
		default:
			return defaultlogReport(aStatus, aBrowsersConfigBean, testScenarioName, stepDescription, strLogMessage,
					aSnapShotFile);
		}

	}

	public static synchronized File getSceenShotFolder(BrowsersConfigBean aBrowsersConfigBean,
			String testScenarioName) {
		String strReportFolder = AppConfig.getInstance().getBrowserExecutionReportFolder(aBrowsersConfigBean);
		testScenarioName = AppUtils.getScenarioReportFileName(testScenarioName, SCENARIOS_FILE_NAME_LENGTH);
		File aScreenShotFile = Paths.get(strReportFolder, testScenarioName, SCREEN_FOLDERNAME).toFile();
		if (!aScreenShotFile.exists()) {
			aScreenShotFile.mkdirs();
		}
		return aScreenShotFile;
	}

	public static synchronized File getFailedSceenShotFolder(BrowsersConfigBean aBrowsersConfigBean,
			String testScenarioName) {
		String strReportFolder = AppConfig.getInstance().getBrowserExecutionReportFolder(aBrowsersConfigBean);
		testScenarioName = AppUtils.getScenarioReportFileName(testScenarioName, SCENARIOS_FILE_NAME_LENGTH);
		File aScreenShotFile = Paths.get(strReportFolder, FAILED_SCREEN_FOLDERNAME, testScenarioName).toFile();
		if (!aScreenShotFile.exists()) {
			aScreenShotFile.mkdirs();
		}
		return aScreenShotFile;
	}

	public static synchronized File getSceenShotFile(BrowsersConfigBean aBrowsersConfigBean, String testScenarioName) {
		String strScenarioFileName = AppUtils.getScenarioReportFileName(testScenarioName, 5);
		String snapshotfileName = String.format("%s_%s.%s", strScenarioFileName,
				AppUtils.getFileDate(), AppConstants.TEST_SNAP_FILE_EXTENTION);

		String strReportFolder = getSceenShotFolder(aBrowsersConfigBean, testScenarioName).getPath();
		File aScreenShotFile = Paths.get(strReportFolder, snapshotfileName).toFile();
		if (!aScreenShotFile.getParentFile().exists()) {
			aScreenShotFile.getParentFile().mkdirs();
		}
		return aScreenShotFile;
	}

	public synchronized static BufferedImage getScreenShotImage(WebDriver driver,
			BrowsersConfigBean aBrowsersConfigBean) {
		Browsers aBrowser = aBrowsersConfigBean.getBrowser();
		if (WebUtils.isSelfHealingRequire() && driver instanceof SelfHealingDriver) {
			SelfHealingDriver aSelfHealingDriver = (SelfHealingDriver) driver;
			driver = aSelfHealingDriver.getDelegate();
		}
		ShootingStrategy aStrategy = WebUtils.getShootingStrategy(aBrowser, driver,
				aBrowsersConfigBean.getScrollTimeOut());
		Screenshot fpScreenshot = new AShot().shootingStrategy(aStrategy).coordsProvider(new WebDriverCoordsProvider())
				.takeScreenshot(driver);
		return fpScreenshot.getImage();
	}

	private synchronized static File captureFullScreen(WebDriver driver, BrowsersConfigBean aBrowsersConfigBean,
			String testScenarioName) {
		String strLogMessage = String.format("captureFullScreen for Browser %s",
				aBrowsersConfigBean.getBrowserDisplayName());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			File aScreenShotFile = getSceenShotFile(aBrowsersConfigBean, testScenarioName);
			if (!aScreenShotFile.getParentFile().exists()) {
				aScreenShotFile.getParentFile().mkdirs();
			}
			BufferedImage aScreenShotImage = getScreenShotImage(driver, aBrowsersConfigBean);
			ImageIO.write(aScreenShotImage, "PNG", aScreenShotFile);
			return aScreenShotFile;
		} catch (Exception ex) {
			LOGGER.error(String.format("Failed to %s", strLogMessage));
			ERROR_LOGGER.error(
					String.format("Error while capturing screen for %s", aBrowsersConfigBean.getBrowserDisplayName()),
					ex);
			return null;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	public synchronized static File captureFullScreen(WebDriver driver, BrowsersConfigBean aBrowsersConfigBean,
			String testScenarioName, String strTestData) {
		String strLogMessage = String.format("captureFullScreen for Browser %s",
				aBrowsersConfigBean.getBrowserDisplayName());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			Browsers aBrowser = aBrowsersConfigBean.getBrowser();
			long lCurrentTime = System.currentTimeMillis();
			String strScenarioFileName = AppUtils.getScenarioReportFileName(testScenarioName,
					SCENARIOS_FILE_NAME_LENGTH);
			String snapshotfileName = String.format("%s_%s_%s.%s", aBrowser.getBrowserName(), strScenarioFileName,
					lCurrentTime, AppConstants.TEST_SNAP_FILE_EXTENTION);

			String strReportFolder = AppConfig.getInstance().getExecutionReportFolder();
			File aScreenShotFile = Paths.get(strReportFolder, strTestData, snapshotfileName).toFile();
			if (!aScreenShotFile.getParentFile().exists()) {
				aScreenShotFile.getParentFile().mkdirs();
			}

			BufferedImage aScreenShotImage = getScreenShotImage(driver, aBrowsersConfigBean);
			ImageIO.write(aScreenShotImage, "PNG", aScreenShotFile);
			return aScreenShotFile;
		} catch (Exception ex) {
			LOGGER.error(String.format("Failed to %s", strLogMessage));
			ERROR_LOGGER.error(
					String.format("Error while capturing screen for %s", aBrowsersConfigBean.getBrowserDisplayName()),
					ex);
			return null;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	/**
	 * Used to log Information Report
	 * 
	 * @param aBrowsersConfigBean
	 * @param testScenarioName
	 * @param stepDescription
	 * @param strLogMessage
	 * @param isSnapRequired
	 * @param driver
	 * @return
	 */
	public static synchronized String logInfo(BrowsersConfigBean aBrowsersConfigBean, String testScenarioName,
			String stepDescription, String strLogMessage, boolean isSnapRequired, WebDriver driver) {
		return logReport(Status.INFO, aBrowsersConfigBean, testScenarioName, stepDescription, strLogMessage,
				isSnapRequired, driver);
	}

	/**
	 * Used to log Success Report
	 * 
	 * @param aBrowsersConfigBean
	 * @param testScenarioName
	 * @param stepDescription
	 * @param strLogMessage
	 * @param isSnapRequired
	 * @param driver
	 * @return
	 */
	public static synchronized String logSucess(BrowsersConfigBean aBrowsersConfigBean, String testScenarioName,
			String stepDescription, String strLogMessage, boolean isSnapRequired, WebDriver driver) {
		return logReport(Status.PASS, aBrowsersConfigBean, testScenarioName, stepDescription, strLogMessage,
				isSnapRequired, driver);
	}

	/**
	 * Used to log Failure Report
	 * 
	 * @param aBrowsersConfigBean
	 * @param testScenarioName
	 * @param stepDescription
	 * @param strLogMessage
	 * @param isSnapRequired
	 * @param driver
	 * @return
	 */
	public static synchronized String logFailure(BrowsersConfigBean aBrowsersConfigBean, String testScenarioName,
			String stepDescription, String strLogMessage, boolean isSnapRequired, WebDriver driver) {
		return logReport(Status.FAIL, aBrowsersConfigBean, testScenarioName, stepDescription, strLogMessage,
				isSnapRequired, driver);
	}

	/**
	 * Used to log logWarning Report
	 * 
	 * @param aBrowsersConfigBean
	 * @param testScenarioName
	 * @param strLogMessage
	 * @param stepDescription
	 * @param isSnapRequired
	 * @param driver
	 * @return
	 */
	public static synchronized String logWarning(BrowsersConfigBean aBrowsersConfigBean, String testScenarioName,
			String strLogMessage, String stepDescription, boolean isSnapRequired, WebDriver driver) {
		return logReport(Status.WARNING, aBrowsersConfigBean, testScenarioName, stepDescription, strLogMessage,
				isSnapRequired, driver);
	}
}
