/****************************************************************************
 * File Name 		: WebUtils.java
 * Package			: com.dxc.zurich.utils
 * Author			: pmusunuru2
 * Creation Date	: Jul 26, 2021
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.utils;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.CapabilityType;

import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.ErrorMsgConstants;
import com.abc.project.constants.ORConstants;
import com.abc.project.enums.BrowserType;
import com.abc.project.enums.Browsers;
import com.epam.healenium.SelfHealingDriver;
import com.google.common.collect.ImmutableList;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import io.appium.java_client.windows.WindowsDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import ru.yandex.qatools.ashot.shooting.ShootingStrategy;

/**
 * @author pmusunuru2
 * @since Jul 26, 2021 10:02:02 am
 */
public class WebUtils {

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	public static String getDriverFileName(String strDownloadedDriverPath) {
		return FilenameUtils.getName(strDownloadedDriverPath);
	}

	public static synchronized FirefoxOptions getFirefoxOptions(BrowsersConfigBean aBrowsersConfigBean,
			File aFireFoxFile) {
		String strDownloadDir = AppUtils.getDownloadFolder(aBrowsersConfigBean);
		if (aBrowsersConfigBean.canAutoUpdateDriver()) {
			WebDriverManager aChromeDriverManager = WebDriverManager.firefoxdriver();
			aChromeDriverManager.setup();
			aFireFoxFile = Paths
					.get(aFireFoxFile.getParent(), getDriverFileName(aChromeDriverManager.getDownloadedDriverPath()))
					.toFile();
			updateDriver(aChromeDriverManager.getDownloadedDriverPath(), aFireFoxFile);
		}
		System.setProperty(AppConstants.WIN_FIREFOX_DRIVER_PROP_NAME, aFireFoxFile.getPath());
		// Creating Firefox profile
		FirefoxOptions aFireFoxOptions = new FirefoxOptions();
//		aFireFoxOptions.setLegacy(false);
		boolean legacy = false;
		aFireFoxOptions.setHeadless(isHeadLess());
		aFireFoxOptions.setCapability("marionette", !legacy);// setLegacyValue
		aFireFoxOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
		aFireFoxOptions.setLogLevel(FirefoxDriverLogLevel.FATAL);
		// Instructing Firefox to use custom download location
		aFireFoxOptions.addPreference("browser.download.folderList", 2);
		// Setting custom download directory
		aFireFoxOptions.addPreference("browser.download.dir", strDownloadDir);
//		aFireFoxOptions.addPreference("plugin.disable_full_page_plugin_for_types",
//				"application/pdf,application/vnd.adobe.xfdf,application/vnd.fdf,application/vnd.adobe.xdp+xml");
		aFireFoxOptions.addPreference("browser.download.useDownloadDir", true);
//		aFireFoxOptions.addPreference("browser.download.viewableInternally.enabledTypes", "");
		aFireFoxOptions.addPreference("browser.helperApps.alwaysAsk.force", false);
		aFireFoxOptions.addPreference("browser.download.manager.showWhenStarting", false);
		aFireFoxOptions.addPreference("browser.helperApps.neverAsk.saveToDisk",
				"application/download, application/octet-stream, text/csv, images/jpeg, application/pdf, application/xml, text/xml, application/text, text/plain");
		aFireFoxOptions.addPreference("pdfjs.disabled", true);  // disable the built-in PDF viewer
		aBrowsersConfigBean.getExtraCapabilities().entrySet().stream().forEach(conFig -> {
			aFireFoxOptions.setCapability(conFig.getKey(), conFig.getValue());
		});

		return aFireFoxOptions;
	}

	public static String getBrowserWindowSize() {
		String strSysBrowserWindowSize = System.getProperty(AppConstants.BROWSER_WINDOWS_SIZE_KEY);
		String strBrowserWindowSize = StringUtils.isEmpty(StringUtils.trim(strSysBrowserWindowSize)) ? PropertyHandler
				.getExternalString(AppConstants.BROWSER_WINDOWS_SIZE_KEY, AppConstants.APP_PROPERTIES_NAME)
				: strSysBrowserWindowSize;
		strBrowserWindowSize = StringUtils.trim(strBrowserWindowSize);
		return strBrowserWindowSize;
	}

	public static synchronized ChromeOptions getChromeOptions(BrowsersConfigBean aBrowsersConfigBean,
			File aDriverPath) {
		String strDownloadDir = AppUtils.getDownloadFolder(aBrowsersConfigBean);
		if (aBrowsersConfigBean.canAutoUpdateDriver()) {
			WebDriverManager aChromeDriverManager = WebDriverManager.chromedriver();
			aChromeDriverManager.setup();
			aDriverPath = Paths
					.get(aDriverPath.getParent(), getDriverFileName(aChromeDriverManager.getDownloadedDriverPath()))
					.toFile();
			updateDriver(aChromeDriverManager.getDownloadedDriverPath(), aDriverPath);
		}
		System.setProperty(AppConstants.WIN_CHROME_DRIVER_PROP_NAME, aDriverPath.getPath());
		ChromeOptions aChromeBrowserOptions = new ChromeOptions();
		if (WebUtils.isHeadLess()) {
			aChromeBrowserOptions.addArguments("--headless");
			aChromeBrowserOptions.addArguments(String.format("--window-size=%s", getBrowserWindowSize()));
		}
		if (!StringUtils.isEmpty(aBrowsersConfigBean.getPackageName())) {
			File aUserDir = AppUtils.getEnvFilePath(aBrowsersConfigBean.getPackageName());
			if (aUserDir != null && aUserDir.exists()) {
				aChromeBrowserOptions.addArguments(String.format("user-data-dir=%s", aUserDir.getPath()));
			}
		}
		if (!StringUtils.isEmpty(aBrowsersConfigBean.getActivityName())) {
			File aBinaryFile = AppUtils.getEnvFilePath(aBrowsersConfigBean.getActivityName());
			if (aBinaryFile != null && aBinaryFile.exists()) {
				aChromeBrowserOptions.setBinary(aBinaryFile);
			}
		}
		aChromeBrowserOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
		aChromeBrowserOptions.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
		aChromeBrowserOptions.setCapability(CapabilityType.SUPPORTS_APPLICATION_CACHE, false);
		aChromeBrowserOptions.addArguments("test-type");
		aChromeBrowserOptions.addArguments("--start-maximized"); // open Browser in maximized mode
		aChromeBrowserOptions.addArguments("disable-infobars"); // disabling infobars
		aChromeBrowserOptions.addArguments("--disable-extensions"); // disabling extensions
		aChromeBrowserOptions.addArguments("--disable-gpu"); // applicable to windows os only
		aChromeBrowserOptions.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
		aChromeBrowserOptions.addArguments("--disable-popup-blocking");
		aChromeBrowserOptions.addArguments("--disable-default-apps");
		aChromeBrowserOptions.addArguments("--ignore-certificate-errors");
		aChromeBrowserOptions.addArguments("test-type=browser");

		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.default_directory", strDownloadDir);
		chromePrefs.put("plugins.always_open_pdf_externally", true);
		// disable flash and the PDF viewer
		chromePrefs.put("plugins.plugins_disabled", new String[] { "Adobe Flash Player", "Chrome PDF Viewer" });
		aChromeBrowserOptions.setExperimentalOption("prefs", chromePrefs);
		aBrowsersConfigBean.getExtraCapabilities().entrySet().stream().forEach(conFig -> {
			aChromeBrowserOptions.setCapability(conFig.getKey(), conFig.getValue());
		});
		return aChromeBrowserOptions;
	}

	public static synchronized EdgeOptions getEdgeOptions(BrowsersConfigBean aBrowsersConfigBean,
			File aEdgeDriverFile) {

		String strDownloadDir = AppUtils.getDownloadFolder(aBrowsersConfigBean);
		if (aBrowsersConfigBean.canAutoUpdateDriver()) {
			WebDriverManager aEdgeDriverManager = WebDriverManager.edgedriver();
			aEdgeDriverManager.setup();
			aEdgeDriverFile = Paths
					.get(aEdgeDriverFile.getParent(), getDriverFileName(aEdgeDriverManager.getDownloadedDriverPath()))
					.toFile();
			updateDriver(aEdgeDriverManager.getDownloadedDriverPath(), aEdgeDriverFile);
		}
		System.setProperty(AppConstants.WIN_EDGE_DRIVER_PROP_NAME, aEdgeDriverFile.getPath());

		HashMap<String, Object> edgePrefs = new HashMap<String, Object>();
		edgePrefs.put("profile.default_content_settings.popups", 0);
		edgePrefs.put("download.default_directory", strDownloadDir);
		edgePrefs.put("plugins.always_open_pdf_externally", true);
		edgePrefs.put("plugins.pdf.restore_view", false);
		edgePrefs.put("download.prompt_for_download", false);
		edgePrefs.put("download.directory_upgrade", true);
		edgePrefs.put("download.open_pdf_in_system_reader", false);
		edgePrefs.put("safebrowsing.enabled", true);
		edgePrefs.put("download.extensions_to_open_by_policy", new String[] { "ica" });
		// disable flash and the PDF viewer
		edgePrefs.put("plugins.plugins_disabled",
				new String[] { "Chrome PDF Viewer", "Edge", "Microsoft Edge", "Microsoft Edge PDF Viewer" });
//		List<String> lstEdgeArgs = new ArrayList<>();
		EdgeOptions aEdgeOptions = new EdgeOptions();
		aEdgeOptions.setHeadless(isHeadLess());
		if (isHeadLess()) {
			aEdgeOptions.addArguments(String.format("--window-size=%s", getBrowserWindowSize()));
		}
		aEdgeOptions.addArguments("test-type");
		aEdgeOptions.addArguments("--start-maximized"); // open Browser in maximized mode
		aEdgeOptions.addArguments("--disable-infobars"); // disabling infobars
		aEdgeOptions.addArguments("--disable-extensions"); // disabling extensions
		aEdgeOptions.addArguments("--disable-gpu"); // applicable to windows os only
		aEdgeOptions.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
		aEdgeOptions.addArguments("--no-sandbox"); // Bypass OS security model
		aEdgeOptions.addArguments("--disable-popup-blocking");
		aEdgeOptions.addArguments("--disable-default-apps");
		aEdgeOptions.addArguments("--ignore-certificate-errors");
		aEdgeOptions.addArguments("test-type=browser");
		// Map<String, Object> mpEdgeOptions = new HashMap<>();
		// mpEdgeOptions.put("args", lstEdgeArgs);
		aEdgeOptions.setExperimentalOption("prefs", edgePrefs);
		aEdgeOptions.setCapability("ms:edgeChromium", true);
		// aEdgeOptions.setCapability("ms:edgeOptions", mpEdgeOptions);
		// aEdgeOptions.setCapability(CapabilityType.BROWSER_NAME,
		// org.openqa.selenium.remote.BrowserType.EDGE);
//		aEdgeOptions.setCapability("visual", true); // to enable screenshots
		aEdgeOptions.setCapability("console", true); // to enable console logs
//		aEdgeOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
		aEdgeOptions.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
//		aEdgeOptions.setCapability(CapabilityType.SUPPORTS_APPLICATION_CACHE, false);
		aBrowsersConfigBean.getExtraCapabilities().entrySet().stream().forEach(conFig -> {
			aEdgeOptions.setCapability(conFig.getKey(), conFig.getValue());
		});
		return aEdgeOptions;
	}

	public static boolean isHeadLess() {
		String strSysHeadLess = System.getProperty(AppConstants.DRIVER_HEADLESS_KEY);
		String strHeadLess = StringUtils.isEmpty(StringUtils.trim(strSysHeadLess))
				? PropertyHandler.getExternalString(AppConstants.DRIVER_HEADLESS_KEY, AppConstants.APP_PROPERTIES_NAME)
				: strSysHeadLess;
		return BooleanUtils.toBoolean(strHeadLess);
	}

	public static boolean canIntilizeDriverForEveryTest() {
		String strSysInitDriver = System.getProperty(AppConstants.DRIVER_INITILIZE_EVERYTIME_PROP_NAME);
		String strInitDriver = StringUtils.isEmpty(StringUtils.trim(strSysInitDriver))
				? PropertyHandler.getExternalString(AppConstants.DRIVER_INITILIZE_EVERYTIME_PROP_NAME,
						AppConstants.APP_PROPERTIES_NAME)
				: strSysInitDriver;
		return BooleanUtils.toBoolean(strInitDriver);
	}

	public static boolean isDriverScreenShotExclusions() {
		String strScreenShotExclusions = PropertyHandler.getExternalString(AppConstants.DRIVER_SNAPSHOT_EXCLUSIONS_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		if (StringUtils.isEmpty(StringUtils.trim(strScreenShotExclusions))) {
			return false;
		}
		List<String> lstScreenShotExclusions = Arrays
				.asList(StringUtils.split(StringUtils.trim(strScreenShotExclusions), AppConstants.SEPARATOR_COMMA));
		if (CollectionUtils.isEmpty(lstScreenShotExclusions)) {
			return false;
		}
		String strLocalHost = AppUtils.getHostName();
		String strLocalIP = AppUtils.getHostIpAddress();
		boolean bisDriverScreenShotExclusions = lstScreenShotExclusions.stream()
				.anyMatch(strHost -> StringUtils.startsWithIgnoreCase(strLocalHost, strHost)
						|| StringUtils.containsIgnoreCase(strLocalHost, strHost)
						|| StringUtils.equalsIgnoreCase(strHost, strLocalHost)
						|| StringUtils.startsWithIgnoreCase(strLocalIP, strHost)
						|| StringUtils.containsIgnoreCase(strLocalIP, strHost)
						|| StringUtils.equalsIgnoreCase(strHost, strLocalIP));
		return bisDriverScreenShotExclusions;
	}

	public static float getDefaultDevicePixelRatio() {
		float fScaling = 1.0f;
		try {
			int iScreenRes = Toolkit.getDefaultToolkit().getScreenResolution();
			if (iScreenRes > 100) {
				fScaling = (float) iScreenRes / 100;
			}
		} catch (Throwable th) {
		}
		return fScaling;
	}

	public static float getWebDiverDevicePixelRatio(WebDriver aWebDriver) {
		Double aDevicePixelRatio = Double.valueOf(1.0D);

		try {
			JavascriptExecutor aJavascriptExecutor = (JavascriptExecutor) aWebDriver;
			Object devicePixelRatio = aJavascriptExecutor
					.executeScript(ORConstants.EXEC_JAVA_SCRIPT_DEVICE_PIXELRATIO_CMD, new Object[0]);
			aDevicePixelRatio = Double
					.valueOf((devicePixelRatio instanceof Double) ? ((Double) devicePixelRatio).doubleValue()
							: (((Long) devicePixelRatio).longValue() * 1.0D));
		} catch (Throwable th) {
			return getDefaultDevicePixelRatio();
		}
		return aDevicePixelRatio.floatValue();
	}

	public static ShootingStrategy getShootingStrategy(Browsers aBrowser, WebDriver aWebDriver, int iScrollTimeOut) {
		if (BooleanUtils.toBoolean(PropertyHandler.getExternalString(AppConstants.DRIVER_SIMPLE_SNAPSHOT_KEY,
				AppConstants.APP_PROPERTIES_NAME))) {
			return ShootingStrategies.simple();
		}
		switch (aBrowser) {
		case WINDOWS_NATIVE:
			float fWindowsNativeScaling = getDefaultDevicePixelRatio();
			return ShootingStrategies.scaling(fWindowsNativeScaling);
		case ANDROID_NATIVE:
		case BROWSER_STACK_ANDROID_NATIVE:
		case BROWSER_STACK_IOS_NATIVE:
		case BROWSER_STACK_DESKTOP:
			return ShootingStrategies.simple();
		case WINDOWS_CHROME:
		case WINDOWS_FIREFOX:
		case WINDOWS_IE:
		case WINDOWS_EDGE:
			float fWindowsScaling = isHeadLess() || isDriverScreenShotExclusions()
					? getWebDiverDevicePixelRatio(aWebDriver)
					: getDefaultDevicePixelRatio();
			return ShootingStrategies.viewportPasting(ShootingStrategies.scaling(fWindowsScaling), iScrollTimeOut);
		default:
			float fScaling = getWebDiverDevicePixelRatio(aWebDriver);
			return ShootingStrategies.viewportPasting(ShootingStrategies.scaling(fScaling), iScrollTimeOut);
		}
	}

	public static void scrollIntoView(AppiumDriver<?> aAppiumDriver, String strWebElementText) throws Exception {
		if (StringUtils.isEmpty(StringUtils.trim(strWebElementText))) {
			throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
		}
		if (aAppiumDriver instanceof AndroidDriver<?>) {
			String strSelectorText = "new UiSelector().text(\"" + strWebElementText + "\").instance(0)";
			String strExecuteCommand = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView("
					+ strSelectorText + ");";
			((AndroidDriver<?>) aAppiumDriver).findElementByAndroidUIAutomator(strExecuteCommand);
		} else if (aAppiumDriver instanceof IOSDriver<?>) {
			final HashMap<String, String> scrollObject = new HashMap<String, String>();
			scrollObject.put("predicateString", "value == '" + strWebElementText + "'");
			scrollObject.put("toVisible", "true");
			((IOSDriver<?>) aAppiumDriver).executeScript("mobile: scroll", scrollObject);
		} else {
			String[] strMoveData = StringUtils.split(strWebElementText, AppConstants.SEPARATOR_COMMA);
			if (strMoveData == null || strMoveData.length < 2 || strMoveData.length > 2) {
				throw new Exception(ErrorMsgConstants.ERR_EMPTY_WEB_PROPERTY);
			}
			Dimension dimension = aAppiumDriver.manage().window().getSize();
			int scrollStart = StringUtils.isNumeric(StringUtils.trim(strMoveData[0]))
					? Integer.valueOf(StringUtils.trim(strMoveData[0]))
					: (int) (dimension.getHeight() * 0.5);
			int scrollEnd = StringUtils.isNumeric(StringUtils.trim(strMoveData[1]))
					? Integer.valueOf(StringUtils.trim(strMoveData[1]))
					: (int) (dimension.getHeight() * 0.2);
			Actions aScroll = new Actions(aAppiumDriver);
			aScroll.moveByOffset(scrollStart, scrollEnd).build().perform();
		}
	}

	public static Point getCenterPoint(Point aPoint, Dimension aSize) {
		return new Point(aPoint.getX() + aSize.getWidth() / 2, aPoint.getY() + aSize.getHeight() / 2);
	}

	public static void touchScroll(AppiumDriver<?> aAppiumDriver, int scrollStart, int scrollEnd, long lDriverSleepTime)
			throws Exception {

		// https://github.com/sunilpatro1985/AppiumTest_Java_And_iOS/blob/388e05d0033f4a75d8dd820f9d6d518310fd4c3e/src/main/java/base/Util.java#L76
		Duration awaitDuration = Duration.of(lDriverSleepTime, ChronoUnit.MILLIS);
		if (aAppiumDriver instanceof WindowsDriver<?>) {
			Actions aScroll = new Actions(aAppiumDriver);
			aScroll.moveByOffset(scrollStart, scrollEnd).pause(awaitDuration).release().perform();
		} else {
			TouchAction<?> aScroll = new TouchAction<>(aAppiumDriver);
			aScroll.press(PointOption.point(0, scrollStart)).waitAction(WaitOptions.waitOptions(awaitDuration))
					.moveTo(PointOption.point(0, scrollEnd)).release().perform();
		}
	}

	public static void mouseDrag(WebDriver aWebDriver, WebElement aWebElement, long lDriverSleepTime) throws Exception {
		Point aLocation = aWebElement.getLocation();
		Dimension size = aWebElement.getSize();
		Point midPoint = getCenterPoint(aLocation, size);
		Duration awaitDuration = Duration.of(lDriverSleepTime, ChronoUnit.MILLIS);
		double distance = 0.25;
		int top = midPoint.y - (int) ((size.height * distance) * 0.5);
		int bottom = midPoint.y + (int) ((size.height * distance) * 0.5);
		int left = midPoint.x - (int) ((size.width * distance) * 0.5);
		int right = midPoint.x + (int) ((size.width * distance) * 0.5);
		if (aWebDriver instanceof AndroidDriver<?> || aWebDriver instanceof IOSDriver<?>) {
			swipe((AppiumDriver<?>) aWebDriver, new Point(midPoint.x, top), new Point(midPoint.x, bottom),
					lDriverSleepTime);
		} else if (aWebDriver instanceof WindowsDriver<?>) {
			Actions aScroll = new Actions(aWebDriver);
			aScroll.moveToElement(aWebElement).perform();
			aScroll.clickAndHold(aWebElement).perform();
			aScroll.moveByOffset(150, 50).perform();
			aScroll.moveToElement(aWebElement).perform();
			aScroll.clickAndHold(aWebElement).perform();
			aScroll.moveByOffset(100, 50).perform();
			aScroll.moveToElement(aWebElement).perform();
			aScroll.release(aWebElement).perform();
		} else {
			Actions aScroll = new Actions(aWebDriver);
			aScroll.dragAndDropBy(aWebElement, left, midPoint.y).pause(awaitDuration)
					.dragAndDropBy(aWebElement, right, midPoint.y).build().perform();
		}
	}

	public static boolean isTouchScreenDriver(WebDriver aWebDriver, Browsers aBrowsers) {
		return (aBrowsers.getBrowserType() != BrowserType.DESKTOP_WEB
				|| aBrowsers.getBrowserType() != BrowserType.DESKTOP_NATIVE)
				&& (aWebDriver instanceof AndroidDriver<?> || aWebDriver instanceof IOSDriver<?>);
	}

	public static void swipe(AppiumDriver<?> aAppiumDriver, Point start, Point end, long lDriverSleepTime)
			throws Exception {
		if (aAppiumDriver instanceof WindowsDriver<?>) {
			throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.UNKNOWN_ACTION, "Swipe"));
		}
		// https://github.com/sunilpatro1985/AppiumTest_Java_And_iOS/blob/388e05d0033f4a75d8dd820f9d6d518310fd4c3e/src/main/java/base/Util.java#L76
		Duration awaitDuration = Duration.of(lDriverSleepTime, ChronoUnit.MILLIS);
		PointerInput aPointerInput = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
		Sequence aSwipe = new Sequence(aPointerInput, 0);
		aSwipe.addAction(aPointerInput.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), start.getX(),
				start.getY()));
		aSwipe.addAction(aPointerInput.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
		if (aAppiumDriver instanceof AndroidDriver<?>) {
			awaitDuration = awaitDuration.dividedBy(3);
		} else {
			aSwipe.addAction(new Pause(aPointerInput, awaitDuration));
			awaitDuration = Duration.ZERO;
		}
		aSwipe.addAction(
				aPointerInput.createPointerMove(awaitDuration, PointerInput.Origin.viewport(), end.getX(), end.getY()));
		aSwipe.addAction(aPointerInput.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
		aAppiumDriver.perform(ImmutableList.of(aSwipe));
	}

	public static String getImageFileEncodeBase64(String strPropertyValue) throws IOException {
		File aScreenFile = AppUtils.getFileFromPath(strPropertyValue);
		if (aScreenFile == null || !aScreenFile.exists()) {
			throw new FileNotFoundException(AppUtils.formatMessage(ErrorMsgConstants.FILENTFOUND, strPropertyValue));
		}
		try {
			byte[] aFileData = FileUtility.getFileData(aScreenFile);
			return Base64.encodeBase64String(aFileData);
		} catch (Exception ex) {
			throw new IOException(
					StringUtils.isEmpty(ex.getMessage()) ? ErrorMsgConstants.ERR_DEFAULT : ex.getMessage(), ex);
		}
	}

	public static synchronized void updateDriver(String strDownloadedDriverPath, File aDriverPath) {
		File aWebDriverCachePath = new File(strDownloadedDriverPath);
		if (!aWebDriverCachePath.exists() || !aDriverPath.getParentFile().exists()) {
			return;
		}
		Path dwWebDriverCacheDirPath = aWebDriverCachePath.toPath().toAbsolutePath();
		Path diverPath = aDriverPath.toPath();
		try {
			Files.copy(dwWebDriverCacheDirPath, diverPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception ex) {
			ERROR_LOGGER.error(AppUtils.formatMessage(ErrorMsgConstants.ERR_COPY_FILE, strDownloadedDriverPath,
					diverPath.toString()), ex);
		}
	}

	public static synchronized SelfHealingDriver createSelfHealingWebDriver(WebDriver delegate) {
		String strHealeniumConfigPath = PropertyHandler.getExternalString(AppConstants.HEALENIUM_CONFPATH_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		File aHealeniumConfigFile = AppUtils.getFileFromPath(strHealeniumConfigPath);
		if (!aHealeniumConfigFile.exists()) {
			return null;
		}
		Path aHealeniumConfigPath = aHealeniumConfigFile.toPath().toAbsolutePath();
		try {
			ResourceBundle aResourceBundle = PropertyHandler.getExternalResourceBundle(strHealeniumConfigPath);
			aResourceBundle.getKeys().asIterator().forEachRemaining(strKey -> 
			{
				System.setProperty(strKey, aResourceBundle.getString(strKey));
			});
		} catch (Exception ex) {
			String strConfOrrWithEnv = PropertyHandler.getExternalString(AppConstants.HEALENIUM_CONFIG_OVERRIDE_WITH_ENV_VARS_KEY,
					strHealeniumConfigPath);
			String strServerURL = PropertyHandler.getExternalString(AppConstants.HEALENIUM_SERVER_URL_KEY,
					strHealeniumConfigPath);
			String strImitatorURL = PropertyHandler.getExternalString(AppConstants.HEALENIUM_IMITATOR_URL_KEY,
					strHealeniumConfigPath);
			System.setProperty(AppConstants.HEALENIUM_CONFIG_OVERRIDE_WITH_ENV_VARS_KEY, strConfOrrWithEnv);
			System.setProperty(AppConstants.HEALENIUM_SERVER_URL_KEY, strServerURL);
			System.setProperty(AppConstants.HEALENIUM_IMITATOR_URL_KEY, strImitatorURL);
		} 
		com.typesafe.config.Config aHealeniumConfig = com.typesafe.config.ConfigFactory.defaultOverrides()
				.withFallback(com.typesafe.config.ConfigFactory.load())
				.withFallback(com.typesafe.config.ConfigFactory.load(aHealeniumConfigPath.normalize().toString()));
		return SelfHealingDriver.create(delegate, aHealeniumConfig);
	}

	public static boolean isSelfHealingRequire() {
		String strHealeniumConfigPath = PropertyHandler.getExternalString(AppConstants.HEALENIUM_CONFPATH_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		File aHealeniumConfigFile = AppUtils.getFileFromPath(strHealeniumConfigPath);
		if (!aHealeniumConfigFile.exists()) {
			return false;
		}
		return BooleanUtils.toBoolean(
				PropertyHandler.getExternalString(AppConstants.HEALENIUM_SELFHEAL_KEY, strHealeniumConfigPath));
	}

	public static boolean isIsBrowserStack(Browsers aBrowser) {
		return aBrowser == Browsers.BROWSER_STACK_DESKTOP || aBrowser == Browsers.BROWSER_STACK_MOBILE
				|| aBrowser == Browsers.BROWSER_STACK_IOS_NATIVE || aBrowser == Browsers.BROWSER_STACK_ANDROID_NATIVE;
	}
}
