/****************************************************************************
 * File Name 		: DeskTopWebBrowserRunner.java
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
import java.util.LinkedHashMap;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.config.AppConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.ErrorMsgConstants;
import com.abc.project.enums.Browsers;
import com.abc.project.enums.StartFinish;
import com.abc.project.utils.AppUtils;
import com.abc.project.utils.PropertyHandler;
import com.abc.project.utils.WebUtils;
import com.epam.healenium.SelfHealingDriver;

import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.WindowsElement;
import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 2:07:59 pm
 */
public class DeskTopWebBrowserRunner extends BrowserRunner {

	private static final Logger LOGGER = LogManager.getLogger(DeskTopWebBrowserRunner.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	/**
	 * @param aSupportedbrowser
	 */
	public DeskTopWebBrowserRunner(BrowsersConfigBean aBrowsersConfigBean) {
		super(aBrowsersConfigBean);
	}

	@Override
	public synchronized void initizeDrivers() throws Exception {
		BrowsersConfigBean aBrowsersConfigBean = getBrowsersConfigBean();
		String strDeviceName = aBrowsersConfigBean.getDeviceName();
		Browsers aBrowser = getBrowsersConfigBean().getBrowser();
		String logMessage = String.format("Web driver Initizing %s", aBrowser.toString());
//		AppEnvConfigBean aPPRunEnv = getAppEnvConfigBean();
//		AppRunMode aAppRunMode = aPPRunEnv.getAppRunMode();
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(logMessage));
			String strDownloadDir = AppUtils.getDownloadFolder(getBrowsersConfigBean());
			String strNodeURl = null;
			switch (aBrowser) {
			case WINDOWS_CHROME:
				String strChromeDriverPath = PropertyHandler.getExternalString(AppConstants.WIN_CHROME_DRIVER_PATH_KEY,
						AppConstants.APP_PROPERTIES_NAME);
				File aChromeDriverFile = AppUtils.getFileFromPath(strChromeDriverPath);
				ChromeOptions aChromeBrowserOptions = WebUtils.getChromeOptions(aBrowsersConfigBean, aChromeDriverFile);
				strNodeURl = getNodeConfigURL(aChromeBrowserOptions);
				WebDriver windowsGC;
				if (StringUtils.isEmpty(StringUtils.trim(strNodeURl))) {
					windowsGC = new ChromeDriver(aChromeBrowserOptions);
				} else {
					windowsGC = new RemoteWebDriver(new URL(strNodeURl), aChromeBrowserOptions);
				}
				setDriver(windowsGC);
				break;
			case WINDOWS_ELECTRON_CHROME:
				String strChromeElectronDriverPath = PropertyHandler.getExternalString(
						AppConstants.WIN_CHROME_ELECTRON_DRIVER_PATH_KEY, AppConstants.APP_PROPERTIES_NAME);
				File aChromeElectronDriverFile = AppUtils.getFileFromPath(strChromeElectronDriverPath);
				ChromeOptions aChromeElectronBrowserOptions = WebUtils.getChromeOptions(aBrowsersConfigBean,
						aChromeElectronDriverFile);
				strNodeURl = getNodeConfigURL(aChromeElectronBrowserOptions);
				WebDriver windowsElectronGC;
				if (StringUtils.isEmpty(StringUtils.trim(strNodeURl))) {
					windowsElectronGC = new ChromeDriver(aChromeElectronBrowserOptions);
				} else {
					windowsElectronGC = new RemoteWebDriver(new URL(strNodeURl), aChromeElectronBrowserOptions);
				}
				setDriver(windowsElectronGC);
				break;
			case WINDOWS_FIREFOX:
				String strFireFoxPath = PropertyHandler.getExternalString(AppConstants.WIN_FIREFOX_DRIVER_PATH_KEY,
						AppConstants.APP_PROPERTIES_NAME);
				File aFireFoxFile = AppUtils.getFileFromPath(strFireFoxPath);
				FirefoxOptions aFireFoxOptions = WebUtils.getFirefoxOptions(aBrowsersConfigBean, aFireFoxFile);
				strNodeURl = getNodeConfigURL(aFireFoxOptions);
				WebDriver windowsMF;
				if (StringUtils.isEmpty(StringUtils.trim(strNodeURl))) {
					windowsMF = new FirefoxDriver(aFireFoxOptions);
				} else {
					windowsMF = new RemoteWebDriver(new URL(strNodeURl), aFireFoxOptions);
				}
				windowsMF.manage().window().maximize();
				setDriver(windowsMF);
				break;
			case WINDOWS_IE:
				if (WebUtils.isHeadLess()) {
					throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_UNSUPPORTED_MODE,
							aBrowser.getBrowserName(), AppConstants.BROWSER_MODE_HEADLESS));
				}
				try {
					String strCMDIE = "REG ADD \"HKEY_CURRENT_USER\\Software\\Microsoft\\Internet Explorer\\Main\" /F /V \"Default Download Directory\" /T REG_SZ /D "
							+ strDownloadDir;
					Runtime.getRuntime().exec(strCMDIE);
				} catch (Exception ex) {
					LOGGER.error("Coulnd't change the registry for default directory for IE");
					ERROR_LOGGER.error("Coulnd't change the registry for default directory for IE", ex);
				}

				try {
					String strIESettingsPath = PropertyHandler.getExternalString(
							AppConstants.WIN_IE_DRIVER_SETTINGS_PATH_KEY, AppConstants.APP_PROPERTIES_NAME);
					File aIESettingsFile = AppUtils.getFileFromPath(strIESettingsPath);
					String scriptPath = aIESettingsFile.getPath();
					String system32Path = System.getenv("SystemRoot") + "\\system32";
					String executable = system32Path + "\\wscript.exe";
					String cmdArr[] = { executable, scriptPath };
					Runtime.getRuntime().exec(cmdArr);
				} catch (Exception ex) {
					LOGGER.error("Coulnd't Run IEProtectedModeSettings");
					ERROR_LOGGER.error("Coulnd't Run IEProtectedModeSettings", ex);
				}

				String strIEPath = PropertyHandler.getExternalString(AppConstants.WIN_IE_DRIVER_PATH_KEY,
						AppConstants.APP_PROPERTIES_NAME);
				File aIEFile = AppUtils.getFileFromPath(strIEPath);
				if (aBrowsersConfigBean.canAutoUpdateDriver()) {
					WebDriverManager aIEDriverManager = WebDriverManager.iedriver();
					aIEDriverManager.setup();
					aIEFile = Paths.get(aIEFile.getParent(),
							WebUtils.getDriverFileName(aIEDriverManager.getDownloadedDriverPath())).toFile();
					WebUtils.updateDriver(aIEDriverManager.getDownloadedDriverPath(), aIEFile);
				}
				System.setProperty(AppConstants.WIN_IE_DRIVER_PROP_NAME, aIEFile.getPath());
				InternetExplorerOptions aIEOptions = new InternetExplorerOptions();
				aIEOptions.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
				aIEOptions.setCapability(InternetExplorerDriver.ENABLE_ELEMENT_CACHE_CLEANUP, true);
				aIEOptions.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
				aIEOptions.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
				aIEOptions.disableNativeEvents();
				aIEOptions.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);
				aIEOptions.setCapability("disable-popup-blocking", true);
				aIEOptions.takeFullPageScreenshot();
				aIEOptions.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);
				aIEOptions.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
				aIEOptions.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);

				aBrowsersConfigBean.getExtraCapabilities().entrySet().stream().forEach(conFig -> {
					aIEOptions.setCapability(conFig.getKey(), conFig.getValue());
				});
				strNodeURl = getNodeConfigURL(aIEOptions);
				WebDriver windowsIE;
				if (StringUtils.isEmpty(StringUtils.trim(strNodeURl))) {
					windowsIE = new InternetExplorerDriver(aIEOptions);
				} else {
					windowsIE = new RemoteWebDriver(new URL(strNodeURl), aIEOptions);
				}
				windowsIE.manage().window().maximize();
				setDriver(windowsIE);
				break;
			case WINDOWS_EDGE:
				// https://docs.microsoft.com/en-us/microsoft-edge/webdriver-chromium/?tabs=java
				String strEdgeDriverPath = PropertyHandler.getExternalString(AppConstants.WIN_EDGE_DRIVER_PATH_KEY,
						AppConstants.APP_PROPERTIES_NAME);
				File aEdgeDriverFile = AppUtils.getFileFromPath(strEdgeDriverPath);
				EdgeOptions aEdgeOptions = WebUtils.getEdgeOptions(aBrowsersConfigBean, aEdgeDriverFile);
				strNodeURl = getNodeConfigURL(aEdgeOptions);
				WebDriver aEdgeDriver;
				if (StringUtils.isEmpty(StringUtils.trim(strNodeURl))) {
					aEdgeDriver = new EdgeDriver(aEdgeOptions);
				} else {
					aEdgeDriver = new RemoteWebDriver(new URL(strNodeURl), aEdgeOptions);
				}
				setDriver(aEdgeDriver);
				break;
			case WINDOWS_NATIVE:
				String strWindowsAppNewCommnadTimeOut = PropertyHandler.getExternalString(
						AppConstants.DRIVER_DEVICE_NEWCOMMNAD_TIMEOUT_KEY, AppConstants.APP_PROPERTIES_NAME);
				DesiredCapabilities aWindowsdNative = new DesiredCapabilities();
				aWindowsdNative.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.WINDOWS);
				aWindowsdNative.setCapability(MobileCapabilityType.PLATFORM_VERSION, aBrowsersConfigBean.getVersion());
				aWindowsdNative.setCapability(MobileCapabilityType.DEVICE_NAME, strDeviceName);
				aWindowsdNative.setCapability(MobileCapabilityType.APP, aBrowsersConfigBean.getActivityName());
				aWindowsdNative.setCapability("ms:experimental-webdriver", true);
				aWindowsdNative.setCapability(MobileCapabilityType.FORCE_MJSONWP, true);
				aWindowsdNative.setCapability(MobileCapabilityType.AUTOMATION_NAME, MobilePlatform.WINDOWS);
				aWindowsdNative.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, strWindowsAppNewCommnadTimeOut);
				aBrowsersConfigBean.getExtraCapabilities().entrySet().stream().forEach(conFig -> {
					aWindowsdNative.setCapability(conFig.getKey(), conFig.getValue());
				});
				WindowsDriver<WindowsElement> aWindowsNative = new WindowsDriver<>(getAppiumDriverUrl(aWindowsdNative),
						aWindowsdNative);
				setDriver(aWindowsNative);
				break;
			case BROWSER_STACK_DESKTOP:
				LinkedHashMap<String, Object> browserstackOptions  = new LinkedHashMap<>();
				DesiredCapabilities aBrowserStackCapabilities = new DesiredCapabilities();
				browserstackOptions.put("userName", aBrowsersConfigBean.getBrowserStackUserName());
				browserstackOptions.put("accessKey",aBrowsersConfigBean.getBrowserStackPassword());
				browserstackOptions.put("appiumLogs", "false");
				browserstackOptions.put("debug", "true");
				browserstackOptions.put("os", aBrowsersConfigBean.getPlatFormName());
				browserstackOptions.put("osVersion", aBrowsersConfigBean.getVersion());
				browserstackOptions.put("browserName", aBrowsersConfigBean.getBrowserName());
				browserstackOptions.put("networkLogs", true);
				browserstackOptions.put(CapabilityType.ACCEPT_INSECURE_CERTS, true);
				browserstackOptions.put("local",
						String.valueOf(aBrowsersConfigBean.isBrowserStackLocal()));
				browserstackOptions.put("video",
						String.valueOf(!aBrowsersConfigBean.isBrowserStackLocal()));
				browserstackOptions.put("consoleLogs", "disable");
				aBrowsersConfigBean.getExtraCapabilities().entrySet().stream().forEach(conFig -> {
					browserstackOptions.put(RegExUtils.replaceAll(conFig.getKey(),"browserstack.",""), conFig.getValue());
				});
				aBrowserStackCapabilities.setCapability("bstack:options", browserstackOptions);
				AppConfig.getInstance().startBrowserStackLocalInstance(aBrowsersConfigBean);
				RemoteWebDriver browserStack = new RemoteWebDriver(new URL(getBrowserStackURL()),
						aBrowserStackCapabilities);
				browserStack.manage().window().maximize();
				setDriver(browserStack);
				break;
			case INVALID_BROWSER:
			default:
				throw new Exception(Browsers.INVALID_BROWSER.getBrowserName());
			}
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(logMessage));
		}
	}

	@Override
	public void setDriver(WebDriver aWebDriver) {
		if (WebUtils.isSelfHealingRequire()) {
			SelfHealingDriver aSelfHealingDriver = WebUtils.createSelfHealingWebDriver(aWebDriver);
			if (aSelfHealingDriver != null) {
				aWebDriver = aSelfHealingDriver;
			}
		}
		if (WebUtils.isHeadLess()) {
			String strBrowserWindowSize = WebUtils.getBrowserWindowSize();
			String strBrowserWindowDim[] = StringUtils.isEmpty(StringUtils.trim(strBrowserWindowSize)) ? null
					: StringUtils.split(strBrowserWindowSize, AppConstants.SEPARATOR_COMMA);
			if (strBrowserWindowDim != null && strBrowserWindowDim.length == 2) {
				String strWidth = StringUtils.trim(strBrowserWindowDim[0]);
				String strHeight = StringUtils.trim(strBrowserWindowDim[1]);
				int width = NumberUtils.isCreatable(strWidth) ? Integer.valueOf(StringUtils.trim(strWidth)) : 1920;
				int height = NumberUtils.isCreatable(strHeight) ?  Integer.valueOf(StringUtils.trim(strHeight)) : 1080;
				try {
					Dimension dimBrowserWindow = new Dimension(width, height);
					aWebDriver.manage().window().setSize(dimBrowserWindow);
				} catch (Exception ex) {
				}
			}
		}
		super.setDriver(aWebDriver);
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	@Override
	public Logger getErrorLogger() {
		return ERROR_LOGGER;
	}

}
