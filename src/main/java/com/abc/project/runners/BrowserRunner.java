/****************************************************************************
 * File Name 		: BrowserRunner.java
 * Package			: com.dxc.zurich.runners
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
package com.abc.project.runners;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.exec.OS;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.abc.project.base.TestStep;
import com.abc.project.beans.AppEnvConfigBean;
import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.beans.TestDataBean;
import com.abc.project.beans.TestSuiteBean;
import com.abc.project.config.AppConfig;
import com.abc.project.config.AppContext;
import com.abc.project.config.MasterConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.enums.AppRunMode;
import com.abc.project.enums.Browsers;
import com.abc.project.enums.StartFinish;
import com.abc.project.grid.GridClientHelper;
import com.abc.project.grid.RemoteMultiPlatformServerHelper;
import com.abc.project.reports.ALMTestCaseReport;
import com.abc.project.reports.ConsolidateTestReport;
import com.abc.project.reports.DocumentTestStepReport;
import com.abc.project.reports.TestStepReport;
import com.abc.project.utils.AppUtils;
import com.abc.project.utils.ClearSessions;
import com.abc.project.utils.PropertyHandler;
import com.abc.project.utils.RunTimeDataUtils;
import com.abc.project.utils.TelegramNotifier;
import com.abc.project.utils.WebUtils;
import com.dxc.enums.ExecutionStatus;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.AndroidServerFlag;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 2:07:01 pm
 */
public abstract class BrowserRunner implements Callable<Boolean> {

	private WebDriver aWebDriver;

	private BrowsersConfigBean aBrowsersConfigBean;

	private static Logger LOGGER;

	private static Logger ERROR_LOGGER;

	private LinkedList<AppiumDriverLocalService> lstAppiumLocalService;

	private AppEnvConfigBean aPPRunEnv;

	public BrowserRunner(BrowsersConfigBean aBrowsersConfigBean) {
		this.setBrowsersConfigBean(aBrowsersConfigBean);
		aPPRunEnv = MasterConfig.getInstance().getAppEnvConfigBean();
		LOGGER = getLogger();
		ERROR_LOGGER = getErrorLogger();
		lstAppiumLocalService = new LinkedList<>();
	}

	protected String getBrowserEnvConfig(String strKey, String strValue) {
		return String.format("%s=%s", strKey, strValue);
	}

	@Override
	public Boolean call() {
		BrowsersConfigBean aBrowsersConfigBean = getBrowsersConfigBean();
		Browsers aBrowser = aBrowsersConfigBean.getBrowser();
		String logMessage = String.format("Running Steps for Browser %s with prority %s and headless mode %s ",
				aBrowser.getBrowserName(), getPriority(), WebUtils.isHeadLess());
		try {
			// Enable the Java 11+ HTTP client
			// https://github.com/SeleniumHQ/selenium/issues/11750
			System.setProperty("webdriver.http.factory", "jdk-http-client");
			boolean bIntilzieForEachTest = WebUtils.canIntilizeDriverForEveryTest();
			boolean bIsBrowserStack = WebUtils.isIsBrowserStack(aBrowser) || bIntilzieForEachTest;
			LOGGER.info(StartFinish.START.getFormattedMsg(logMessage));
			List<TestSuiteBean> lstTestSuite = aBrowsersConfigBean.getTestSuiteData();
			if (CollectionUtils.isEmpty(lstTestSuite)) {
				LOGGER.error(StartFinish.END.getFormattedMsg(logMessage));
				return false;
			}
			if (!bIsBrowserStack) {
				initizeDrivers();
			}
			AppConfig aAppConfig = AppConfig.getInstance();
			for (TestSuiteBean aTestSuite : lstTestSuite) {
				if (!hasTestStepAssigned(aTestSuite)) {
					continue;
				}
				aTestSuite.setBrowsersConfigBean(aBrowsersConfigBean);
				String testScenarioName = aTestSuite.getScenarioName();
				String strTestStepLogMessage = String.format(
						"Running Scenario %s in Browser %s with prority %s and headless mode %s", testScenarioName,
						aBrowser.getBrowserName(), getPriority(), WebUtils.isHeadLess());
				String strExecResult = ExecutionStatus.STARTED.getStatus();
				try {

					LOGGER.info(StartFinish.TESTSTEP_START_BANNER.getDefaultFormattedMsg(testScenarioName));
					LOGGER.info(StartFinish.START.getFormattedMsg(strTestStepLogMessage));
					updateTestSetupStatus(aTestSuite, strExecResult);
					TestDataBean aTestDataBean = getTestDataBean(aAppConfig, aTestSuite);
					if (aTestDataBean == null) {
						LOGGER.error(StartFinish.END.getFormattedMsg(strTestStepLogMessage));
						LOGGER.error(StartFinish.TESTSTEP_END_BANNER.getDefaultFormattedMsg(testScenarioName));
						strExecResult = AppConstants.TEST_RESULT_WARING;
						continue;
					}
					if (bIsBrowserStack) {
						if (WebUtils.isIsBrowserStack(aBrowser)) {
							LinkedHashMap<String, Object> mpExtraCapabilities = aBrowsersConfigBean
									.getExtraCapabilities();
							mpExtraCapabilities.put("sessionName", testScenarioName);
						}
						initizeDrivers();
					}
					TestStep aTestStep = new TestStep(aTestSuite, aTestDataBean, getDriver());
					strExecResult = aTestStep.executeTestStep();
					RunTimeDataUtils.editRuntimeValues(aTestSuite, getBrowsersConfigBean());
					LOGGER.info(StartFinish.END
							.getFormattedMsg(String.format("%s - Result %s", strTestStepLogMessage, strExecResult)));
					LOGGER.info(StartFinish.TESTSTEP_END_BANNER.getDefaultFormattedMsg(testScenarioName));
				} catch (Throwable th) {
					LOGGER.error(StartFinish.END.getFormattedMsg(strTestStepLogMessage));
					LOGGER.error(StartFinish.TESTSTEP_END_BANNER.getDefaultFormattedMsg(testScenarioName));
					ERROR_LOGGER.error("UnCaught Error", th);
					strExecResult = AppConstants.TEST_RESULT_FAIL;
				} finally {
					updateTestSetupStatus(aTestSuite, strExecResult);
					if (bIsBrowserStack) {
						closeDriver();
					}
				}
			}
			LOGGER.info(StartFinish.END.getFormattedMsg(logMessage));
			return true;
		} catch (Throwable th) {
			LOGGER.error(StartFinish.END.getFormattedMsg(AppUtils.formatMessage("Error While {0}", logMessage)));
			ERROR_LOGGER.error(AppUtils.formatMessage("Error While {0}", logMessage), th);
			return false;
		} finally {
			flushBrowserData();
		}
	}

	public abstract void initizeDrivers() throws Exception;

	public abstract Logger getLogger();

	public abstract Logger getErrorLogger();

	private TestDataBean getTestDataBean(AppConfig aAppConfig, TestSuiteBean aTestSuite) throws Exception {
		AppRunMode aAppRunMode = aPPRunEnv.getAppRunMode();
		switch (aAppRunMode) {
		case SELENIUM_NODE:
			return GridClientHelper.getTestDataBean(LOGGER, aPPRunEnv, aTestSuite);
		default:
			return aAppConfig.getTestDataBean(aTestSuite);
		}
	}

	private boolean hasTestStepAssigned(TestSuiteBean aTestSuiteBean) throws Exception {
		AppRunMode aAppRunMode = aPPRunEnv.getAppRunMode();
		switch (aAppRunMode) {
		case SELENIUM_NODE:
			return GridClientHelper.hasTestStepAssigned(LOGGER, aPPRunEnv, aTestSuiteBean);
		case SELENIUM_GRID:
		case APP_PRORITY_GRID:
			return StringUtils.equalsIgnoreCase(getBrowsersConfigBean().getBrowserHost(),
					aTestSuiteBean.getHostAddress());
		case MUTLI_GRID_PLATFORM:
			return RemoteMultiPlatformServerHelper.getInstance().hasScenarioAssigned(aTestSuiteBean);
		default:
			return StringUtils.equalsIgnoreCase(aPPRunEnv.getHostAddress(), aTestSuiteBean.getHostAddress());
		}
	}

	private void updateTestSetupStatus(TestSuiteBean aTestSuiteBean, String strExecResult) throws Exception {
		AppRunMode aAppRunMode = aPPRunEnv.getAppRunMode();
		AppConfig aAppConfig = AppConfig.getInstance();
		BrowsersConfigBean aBrowsersConfigBean = getBrowsersConfigBean();
		if (!StringUtils.equalsIgnoreCase(strExecResult, ExecutionStatus.STARTED.getStatus())) {
			String strSCNDesc = String.format("%s%s%s", aTestSuiteBean.getScenarioName(),
					AppConstants.SEPARATOR_SEMICOLON, aTestSuiteBean.getDescription());
			TestStepReport.flushReport(aBrowsersConfigBean, strSCNDesc);
			ConsolidateTestReport.createExecutionReport(aTestSuiteBean, aPPRunEnv, aBrowsersConfigBean, strExecResult);
			DocumentTestStepReport.createDocScreenShots(aBrowsersConfigBean, aTestSuiteBean);
			ALMTestCaseReport.updateALMResults(aTestSuiteBean, strExecResult);
		}
		if (MasterConfig.getInstance().canReRunFailedTestCases() && aAppConfig.isExecStatusFail(strExecResult)) {
			return;
		}
		switch (aAppRunMode) {
		case SELENIUM_NODE:
			GridClientHelper.updateScenarioStatus(LOGGER, aPPRunEnv, aTestSuiteBean, aBrowsersConfigBean,
					strExecResult);
			aAppConfig.updateScenarioExecutionStatus(aTestSuiteBean, strExecResult);
			break;
		case MUTLI_GRID_PLATFORM:
			RemoteMultiPlatformServerHelper.getInstance().updateScenarioStatus(aTestSuiteBean, strExecResult);
			break;
		default:
			aAppConfig.updateScenarioExecutionStatus(aTestSuiteBean, strExecResult);
			break;
		}
	}

	/**
	 * @return the driver
	 */
	public WebDriver getDriver() {
		return aWebDriver;
	}

	/**
	 * @param driver the driver to set
	 */
	public void setDriver(WebDriver aWebDriver) {
		try {
			aWebDriver.manage().window().maximize();
		} catch (Exception ex) {
		}
		try {
			String strPageLoadTimeOut = AppUtils.getEnvPropertyValue(AppConstants.DRIVER_PAGE_LOAD_TIMEOUT,
					AppConstants.APP_PROPERTIES_NAME);
			long lPageLoadTimeOut = !StringUtils.isEmpty(strPageLoadTimeOut)
					&& StringUtils.isNumeric(strPageLoadTimeOut) ? Long.valueOf(strPageLoadTimeOut)
							: AppConstants.DRIVER_DEFAULT_PAGELOAD_TIMEOUT;
			if (lPageLoadTimeOut > 0) {
				aWebDriver.manage().timeouts().pageLoadTimeout(Duration.of(lPageLoadTimeOut, ChronoUnit.MILLIS));
			}
		} catch (Exception ex) {
		}
		
		try {
			String strScriptTimeOut = AppUtils.getEnvPropertyValue(AppConstants.DRIVER_SCRIPT_TIMEOUT,
					AppConstants.APP_PROPERTIES_NAME);
			long lScriptTimeout = !StringUtils.isEmpty(strScriptTimeOut)
					&& StringUtils.isNumeric(strScriptTimeOut) ? Long.valueOf(strScriptTimeOut)
							: AppConstants.DRIVER_DEFAULT_SCRIPT_TIMEOUT;
			if (lScriptTimeout > 0) {
				aWebDriver.manage().timeouts().scriptTimeout(Duration.of(lScriptTimeout, ChronoUnit.MILLIS));
			}
		} catch (Exception ex) {
		}
		this.aWebDriver = aWebDriver;
	}

	/**
	 * @return the aSupportedbrowser
	 */
	public BrowsersConfigBean getBrowsersConfigBean() {
		return aBrowsersConfigBean;
	}

	/**
	 * @param aSupportedbrowser the aSupportedbrowser to set
	 */
	public void setBrowsersConfigBean(BrowsersConfigBean aBrowsersConfigBean) {
		this.aBrowsersConfigBean = aBrowsersConfigBean;
	}

	protected String getBrowserStackURL() {
		BrowsersConfigBean aBrowsersConfigBean = getBrowsersConfigBean();
		String strStackURL = String.format(
				PropertyHandler.getExternalString(AppConstants.DRIVER_DEVICE_BROWSER_STACK_LAUNCH_KEY,
						AppConstants.APP_PROPERTIES_NAME),
				aBrowsersConfigBean.getBrowserStackUserName(), aBrowsersConfigBean.getBrowserStackPassword());
		return strStackURL;
	}

	public int getPriority() {
		return getBrowsersConfigBean().getBrowserPrority();
	}

	protected String getNodeConfigURL(MutableCapabilities aMutableCapabilities) throws Exception {
		AppEnvConfigBean aPPRunEnv = getAppEnvConfigBean();
		AppRunMode aAppRunMode = aPPRunEnv.getAppRunMode();
		if (aAppRunMode == AppRunMode.SELENIUM_GRID || aAppRunMode == AppRunMode.APP_PRORITY_GRID) {
			aMutableCapabilities.setCapability("se:downloadsEnabled", true);
			String strHubBrowserHost = String.format("%s:%s", aPPRunEnv.getHostAddress(), aPPRunEnv.getHostPort());
			String strNodeRunFormat = PropertyHandler.getExternalString(AppConstants.SELINIUM_NODE_URL_FORMAT_KEY,
					AppConstants.APP_PROPERTIES_NAME);
			String strNodeRunURL = String.format(strNodeRunFormat, strHubBrowserHost);
			return strNodeRunURL;
		}
		return null;
	}

	protected URL getAppiumDriverUrl(MutableCapabilities aMutableCapabilities) throws Exception {
		String strNodeURl = getNodeConfigURL(aMutableCapabilities);
		if (!StringUtils.isEmpty(StringUtils.trim(strNodeURl))) {
			return new URL(strNodeURl);
		}

		String strLaunchTimeOut = PropertyHandler.getExternalString(AppConstants.DRIVER_DEVICE_LAUNCH_TIMEOUT_KEY,
				AppConstants.APP_PROPERTIES_NAME);

		String strAppiumLocation = PropertyHandler.getExternalString(AppConstants.DRIVER_APPIUM_LOCATION_PROP_NAME,
				AppConstants.APP_PROPERTIES_NAME);

		String strNodeJsLocation = PropertyHandler.getExternalString(AppConstants.DRIVER_NODEJS_LOCATION_PROP_NAME,
				AppConstants.APP_PROPERTIES_NAME);
		boolean canDebug = BooleanUtils.toBoolean(PropertyHandler
				.getExternalString(AppConstants.DRIVER_DEVICE_DEBUG_KEY, AppConstants.APP_PROPERTIES_NAME));
		String strDeviceName = getBrowsersConfigBean().getDeviceName();
		String strDeviceID = getBrowsersConfigBean().getDeviceID();
		Browsers aBrowsers = getBrowsersConfigBean().getBrowser();
		String strAppiumLogFileLocation = PropertyHandler.getExternalString(
				AppConstants.DRIVER_APPIUM_LOGS_LOCATION_PROP_NAME, AppConstants.APP_PROPERTIES_NAME);
		String strFileName = AppUtils.formatMessage("{0}_{1}_AppiumLog_{2}", strDeviceName, aBrowsers.getBrowserName(),
				MasterConfig.getInstance().getAppRunID());
		File aFileAppiumLogs = Paths
				.get(AppUtils.getFileFromPath(strAppiumLogFileLocation).getPath(),
						String.format("%s.log", AppUtils.removeIllegalCharacters(strFileName, true)))
				.toAbsolutePath().normalize().toFile();
		if (!aFileAppiumLogs.exists()) {
			aFileAppiumLogs.getParentFile().mkdirs();
			aFileAppiumLogs.createNewFile();
		}
		DesiredCapabilities aServerCapabilities = new DesiredCapabilities();
		aServerCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, strDeviceName);
		aServerCapabilities.setCapability(MobileCapabilityType.UDID, strDeviceID);
		aServerCapabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,
				getBrowsersConfigBean().getPlatFormName());
		aServerCapabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, getBrowsersConfigBean().getVersion());
		aServerCapabilities.setJavascriptEnabled(true);
		AppiumServiceBuilder aAppiumServiceBuilder = new AppiumServiceBuilder();
		aAppiumServiceBuilder.withCapabilities(aServerCapabilities, OS.isFamilyWindows());
		aAppiumServiceBuilder.withStartUpTimeOut(Long.valueOf(strLaunchTimeOut), TimeUnit.MILLISECONDS);
		aAppiumServiceBuilder.withIPAddress(AppUtils.getHostIpAddress());
		aAppiumServiceBuilder.withLogFile(aFileAppiumLogs);
		aAppiumServiceBuilder.withArgument(GeneralServerFlag.SESSION_OVERRIDE);
		long lBootStrapPort = getBrowsersConfigBean() == null ? 0 : getBrowsersConfigBean().getBrowserBootStrapPort();
		if (lBootStrapPort > 0) {
			aAppiumServiceBuilder.withArgument(AndroidServerFlag.BOOTSTRAP_PORT_NUMBER, String.valueOf(lBootStrapPort));
		}
		File aFileAppiumJS = AppUtils.getFileFromPath(strAppiumLocation);
		if (aFileAppiumJS != null && aFileAppiumJS.exists()) {
			aAppiumServiceBuilder.withAppiumJS(aFileAppiumJS);
		}
		File aFileNodeJS = AppUtils.getFileFromPath(strNodeJsLocation);
		if (aFileNodeJS != null && aFileNodeJS.exists()) {
			aAppiumServiceBuilder.usingDriverExecutable(aFileNodeJS);
		}
		aAppiumServiceBuilder.usingAnyFreePort();
		AppiumDriverLocalService aAppiumDriverLocalService = aAppiumServiceBuilder.build();
		if (!canDebug) {
			aAppiumDriverLocalService.clearOutPutStreams();
		}
		aAppiumDriverLocalService.start();
		lstAppiumLocalService.add(aAppiumDriverLocalService);
		return aAppiumDriverLocalService.getUrl();
	}

	protected synchronized void closeDriver() {
		try {
			WebDriver aDriver = getDriver();
			if (aDriver != null) {
				if (aDriver instanceof AppiumDriver<?>) {
					try {
						AppiumDriver<?> aAppiumDriver = (AppiumDriver<?>) aDriver;
						aAppiumDriver.closeApp();
					} catch (Exception ex) {
					}
				}
				aDriver.quit();
			}
		} catch (Exception ex) {
		}
		try {
			String strFile = AppUtils.getDownloadFolder(getBrowsersConfigBean());
			File aDownloadFileLocation = AppUtils.getFileFromPath(strFile);
			if (aDownloadFileLocation.exists()) {
				try {
					List<File> lstFiles = Arrays.asList(aDownloadFileLocation.listFiles()).stream()
							.collect(Collectors.toList());
					lstFiles.stream().forEach(aDwFile -> {
						try {
							if (aDwFile.isFile()) {
								ClearSessions.closeFileByName(aDwFile, true);
							}
						} catch (Throwable th) {
						}
					});
				} catch (Throwable th) {
				}
				try {
					FileUtils.deleteDirectory(aDownloadFileLocation);
				} catch (Throwable th) {
				}
			}
		} catch (Throwable th) {
		}
	}

	protected AppEnvConfigBean getAppEnvConfigBean() {
		return aPPRunEnv;
	}

	private void sendBrowserNotification(BrowsersConfigBean aBrowsersConfigBean) {
		AppEnvConfigBean aPPRunEnv = getAppEnvConfigBean();
		String strBrowserDisplayName = AppUtils
				.getBrowserExecutionFileName(aBrowsersConfigBean.getBrowserDisplayName());
		AppContext appConText = AppContext.getInstance();
		LinkedHashMap<String, Long> resultsBean = appConText.getStepResults(strBrowserDisplayName);
		long lTotalCount = appConText.getStepResultCount(resultsBean, AppConstants.TEST_RESULT_TOTALCOUNT);
		long lPassCount = appConText.getStepResultCount(resultsBean, AppConstants.TEST_RESULT_PASS);
		long lFailCount = appConText.getStepResultCount(resultsBean, AppConstants.TEST_RESULT_FAIL);
		long lWarningCount = appConText.getStepResultCount(resultsBean, AppConstants.TEST_RESULT_WARING);
		long lothersCount = appConText.getStepResultCount(resultsBean, AppConstants.TEST_RESULT_OTHERS);
		TelegramNotifier.sendNotification(aPPRunEnv.getTelegramGroupId(), aPPRunEnv.getAppName(), strBrowserDisplayName,
				lTotalCount, lPassCount, lFailCount, lWarningCount, lothersCount, AppUtils.getHostName(),
				AppUtils.getSystemUserName());
	}

	protected void flushBrowserData() {
		if (CollectionUtils.isNotEmpty(lstAppiumLocalService)) {
			lstAppiumLocalService.stream().forEach(aProcess -> {
				try {
					aProcess.stop();
				} catch (Exception ex) {
				}
			});
		}

		try {
			BrowsersConfigBean aBrowsersConfigBean = getBrowsersConfigBean();
			TestStepReport.flushReport(aBrowsersConfigBean, null);
			sendBrowserNotification(aBrowsersConfigBean);
			closeDriver();
			aBrowsersConfigBean.clearTestData();
		} catch (Exception e) {
		}
	}
}
