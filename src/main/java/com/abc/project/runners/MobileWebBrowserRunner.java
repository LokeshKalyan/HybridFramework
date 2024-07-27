/****************************************************************************
 * File Name 		: MobileWebBrowserRunner.java
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
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.RegExUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.config.AppConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.enums.Browsers;
import com.abc.project.enums.StartFinish;
import com.abc.project.utils.AppUtils;
import com.abc.project.utils.PropertyHandler;
import com.abc.project.utils.WebUtils;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 2:08:40 pm
 */
public class MobileWebBrowserRunner extends BrowserRunner {

	private static final Logger LOGGER = LogManager.getLogger(MobileWebBrowserRunner.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	/**
	 * @param aBrowsersConfigBean
	 */
	public MobileWebBrowserRunner(BrowsersConfigBean aBrowsersConfigBean) {
		super(aBrowsersConfigBean);
	}

	private DesiredCapabilities getWebViewDefaultCapabilities(BrowsersConfigBean aDeviceConfigBean,
			String strDriverPath) {
		String strNewCommnadTimeOut = PropertyHandler
				.getExternalString(AppConstants.DRIVER_DEVICE_NEWCOMMNAD_TIMEOUT_KEY, AppConstants.APP_PROPERTIES_NAME);
		String strLaunchTimeOut = PropertyHandler.getExternalString(AppConstants.DRIVER_DEVICE_LAUNCH_TIMEOUT_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		DesiredCapabilities aWebViewDefaultCap = new DesiredCapabilities();
		aWebViewDefaultCap.setCapability(MobileCapabilityType.PLATFORM_NAME, aDeviceConfigBean.getPlatFormName());
		aWebViewDefaultCap.setCapability(MobileCapabilityType.PLATFORM_VERSION, aDeviceConfigBean.getVersion());
		aWebViewDefaultCap.setCapability(MobileCapabilityType.DEVICE_NAME, aDeviceConfigBean.getDeviceID());
		aWebViewDefaultCap.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, aDeviceConfigBean.getPackageName());
		aWebViewDefaultCap.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, aDeviceConfigBean.getActivityName());
		aWebViewDefaultCap.setCapability(AppConstants.DRIVER_DEVICE_MAX_INSTANCE_KEY, PropertyHandler
				.getExternalString(AppConstants.DRIVER_MAX_INSTANCE_PROP_NAME, AppConstants.APP_PROPERTIES_NAME));
		aWebViewDefaultCap.setCapability(AndroidMobileCapabilityType.NATIVE_WEB_SCREENSHOT, true);
		aWebViewDefaultCap.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
		aWebViewDefaultCap.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
		aWebViewDefaultCap.setCapability(AndroidMobileCapabilityType.CHROMEDRIVER_EXECUTABLE, strDriverPath);
		aWebViewDefaultCap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		aWebViewDefaultCap.setAcceptInsecureCerts(true);
		aWebViewDefaultCap.setJavascriptEnabled(true);
		aWebViewDefaultCap.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, strNewCommnadTimeOut);
		aWebViewDefaultCap.setCapability("launchTimeout", strLaunchTimeOut);
		aWebViewDefaultCap.setCapability(CapabilityType.SUPPORTS_APPLICATION_CACHE, false);
		aDeviceConfigBean.getExtraCapabilities().entrySet().stream().forEach(conFig -> {
			aWebViewDefaultCap.setCapability(conFig.getKey(), conFig.getValue());
		});
		return aWebViewDefaultCap;
	}

	private DesiredCapabilities getBrowserStackDefaultCapabilities(BrowsersConfigBean aDeviceConfigBean) {
		LinkedHashMap<String, Object> browserstackOptions  = new LinkedHashMap<>();
		DesiredCapabilities aBrowserStackCapabilities = new DesiredCapabilities();
		browserstackOptions.put("userName", aDeviceConfigBean.getBrowserStackUserName());
		browserstackOptions.put("accessKey",aDeviceConfigBean.getBrowserStackPassword());
		browserstackOptions.put("realMobile", "true");
		browserstackOptions.put("appiumLogs", "false");
		browserstackOptions.put("debug", "true");
		browserstackOptions.put("os", aDeviceConfigBean.getPlatFormName());
		browserstackOptions.put("osVersion", aDeviceConfigBean.getVersion());
		browserstackOptions.put("deviceName", aDeviceConfigBean.getDeviceID());
		browserstackOptions.put("networkLogs", true);
		browserstackOptions.put(CapabilityType.ACCEPT_INSECURE_CERTS, true);
		browserstackOptions.put("local",
				String.valueOf(aDeviceConfigBean.isBrowserStackLocal()));
		browserstackOptions.put("video",
				String.valueOf(!aDeviceConfigBean.isBrowserStackLocal()));
		browserstackOptions.put("consoleLogs", "disable");
		aDeviceConfigBean.getExtraCapabilities().entrySet().stream().forEach(conFig -> {
			browserstackOptions.put(RegExUtils.replaceAll(conFig.getKey(),"browserstack.",""), conFig.getValue());
		});
		Browsers aBrowser = aDeviceConfigBean.getBrowser();
		switch (aBrowser) {
		case BROWSER_STACK_ANDROID_NATIVE:
			browserstackOptions.put("app_name", aDeviceConfigBean.getActivityName());
			browserstackOptions.put("app_id", aDeviceConfigBean.getBrowserName());
			break;
		case BROWSER_STACK_IOS_NATIVE:
			browserstackOptions.put("app_name", aDeviceConfigBean.getActivityName());
			browserstackOptions.put("app_id", aDeviceConfigBean.getBrowserName());
			aBrowserStackCapabilities.setCapability(MobileCapabilityType.FORCE_MJSONWP, true);
			aBrowserStackCapabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME,
					AutomationName.IOS_XCUI_TEST);
			break;
		default:
			browserstackOptions.put("browserName", aDeviceConfigBean.getBrowserName());
			break;
		}
		aBrowserStackCapabilities.setCapability("bstack:options", browserstackOptions);
		return aBrowserStackCapabilities;
	}

	@Override
	public synchronized void initizeDrivers() throws Exception {
		BrowsersConfigBean aDeviceConfigBean = getBrowsersConfigBean();
		Browsers aBrowser = aDeviceConfigBean.getBrowser();
		String logMessage = String.format("Mobile driver Initizing %s", aBrowser.toString());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(logMessage));
			switch (aBrowser) {
			case ANDROID_CHROME:
				String strChromeDriverPath = PropertyHandler.getExternalString(AppConstants.WIN_CHROME_DRIVER_PATH_KEY,
						AppConstants.APP_PROPERTIES_NAME);
				File aChromeDriverFile = AppUtils.getFileFromPath(strChromeDriverPath);
				ChromeOptions aChromeBrowserOptions = WebUtils.getChromeOptions(aDeviceConfigBean, aChromeDriverFile);
				DesiredCapabilities aAndroidChrome = getWebViewDefaultCapabilities(aDeviceConfigBean,
						aChromeDriverFile.getPath());
				Map<String, Object> mpAndroidChromeOptions = aChromeBrowserOptions.asMap();
				aAndroidChrome.setCapability(AndroidMobileCapabilityType.CHROME_OPTIONS, mpAndroidChromeOptions);
				RemoteWebDriver windowsGC = new RemoteWebDriver(getAppiumDriverUrl(aChromeBrowserOptions), aAndroidChrome);
				setDriver(windowsGC);
				break;
			case ANDROID_FIREFOX:
				String strFireFoxPath = PropertyHandler.getExternalString(AppConstants.WIN_FIREFOX_DRIVER_PATH_KEY,
						AppConstants.APP_PROPERTIES_NAME);
				File aFireFoxFile = AppUtils.getFileFromPath(strFireFoxPath);
				FirefoxOptions aFireFoxOptions = WebUtils.getFirefoxOptions(aDeviceConfigBean, aFireFoxFile);
				DesiredCapabilities aAndroidFireFox = getWebViewDefaultCapabilities(aDeviceConfigBean,
						aFireFoxFile.getPath());
				Map<String, Object> mpAndroidFireFoxOptions = aFireFoxOptions.toJson();
				aAndroidFireFox.setCapability(FirefoxOptions.FIREFOX_OPTIONS, mpAndroidFireFoxOptions);
				RemoteWebDriver windowsMF = new RemoteWebDriver(getAppiumDriverUrl(aAndroidFireFox), aAndroidFireFox);
				setDriver(windowsMF);
				break;
			case BROWSER_STACK_MOBILE:
				DesiredCapabilities aBrowserStackCapabilities = getBrowserStackDefaultCapabilities(aDeviceConfigBean);
				AppConfig.getInstance().startBrowserStackLocalInstance(aDeviceConfigBean);
				RemoteWebDriver browserStack = new RemoteWebDriver(new URL(getBrowserStackURL()),
						aBrowserStackCapabilities);
				setDriver(browserStack);
				break;
			case ANDROID_NATIVE:
				String strAndroidAppNewCommnadTimeOut = PropertyHandler.getExternalString(
						AppConstants.DRIVER_DEVICE_NEWCOMMNAD_TIMEOUT_KEY, AppConstants.APP_PROPERTIES_NAME);
				DesiredCapabilities aAndroidNative = new DesiredCapabilities();
				aAndroidNative.setCapability(MobileCapabilityType.PLATFORM_NAME, aDeviceConfigBean.getPlatFormName());
				aAndroidNative.setCapability(MobileCapabilityType.PLATFORM_VERSION, aDeviceConfigBean.getVersion());
				aAndroidNative.setCapability(MobileCapabilityType.DEVICE_NAME, aDeviceConfigBean.getDeviceID());
				aAndroidNative.setCapability(AndroidMobileCapabilityType.APP_PACKAGE,
						aDeviceConfigBean.getPackageName());
				aAndroidNative.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY,
						aDeviceConfigBean.getActivityName());
				aAndroidNative.setCapability(MobileCapabilityType.FORCE_MJSONWP, true);
				aAndroidNative.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.ANDROID_UIAUTOMATOR2);
				aAndroidNative.setJavascriptEnabled(true);
				aAndroidNative.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, strAndroidAppNewCommnadTimeOut);
				aDeviceConfigBean.getExtraCapabilities().entrySet().stream().forEach(conFig -> {
					aAndroidNative.setCapability(conFig.getKey(), conFig.getValue());
				});
				AndroidDriver<AndroidElement> aAndroidDriver = new AndroidDriver<>(getAppiumDriverUrl(aAndroidNative),
						aAndroidNative);
				setDriver(aAndroidDriver);
				break;
			case BROWSER_STACK_IOS_NATIVE:
				DesiredCapabilities aIOSBrowserStackCapabilities = getBrowserStackDefaultCapabilities(
						aDeviceConfigBean);
				IOSDriver<IOSElement> aIOSBrowserStack = new IOSDriver<IOSElement>(new URL(getBrowserStackURL()),
						aIOSBrowserStackCapabilities);
				setDriver(aIOSBrowserStack);
				break;
			case BROWSER_STACK_ANDROID_NATIVE:
				DesiredCapabilities aAndroidBrowserStackCapabilities = getBrowserStackDefaultCapabilities(
						aDeviceConfigBean);
				AndroidDriver<AndroidElement> aAndroidBrowserStack = new AndroidDriver<AndroidElement>(
						new URL(getBrowserStackURL()), aAndroidBrowserStackCapabilities);
				setDriver(aAndroidBrowserStack);
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
	public Logger getLogger() {
		return LOGGER;
	}

	@Override
	public Logger getErrorLogger() {
		return ERROR_LOGGER;
	}
}
