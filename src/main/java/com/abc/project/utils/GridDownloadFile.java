/****************************************************************************
 * File Name 		: GridDownloadFile.java
 * Package			: com.dxc.zurich.utils
 * Author			: RAVITHEJA.KORLAGUNTA
 * Creation Date	: Aug 10, 2023
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.io.Zip;
import org.openqa.selenium.json.Json;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.remote.http.HttpClient;
import org.openqa.selenium.remote.http.HttpMethod;
import org.openqa.selenium.remote.http.HttpRequest;
import org.openqa.selenium.remote.http.HttpResponse;

import com.abc.project.beans.AppEnvConfigBean;
import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.config.AppContext;
import com.abc.project.config.MasterConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.ErrorMsgConstants;
import com.abc.project.enums.Browsers;

public class GridDownloadFile {

	static {
		ch.qos.logback.classic.LoggerContext loggerContext = (ch.qos.logback.classic.LoggerContext) org.slf4j.LoggerFactory
				.getILoggerFactory();
		ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger("io.netty");
		rootLogger.setLevel(ch.qos.logback.classic.Level.OFF);
		ch.qos.logback.classic.Logger asyncLogger = loggerContext.getLogger("org.asynchttpclient");
		asyncLogger.setLevel(ch.qos.logback.classic.Level.OFF);
		ch.qos.logback.classic.Logger apacheLogger = loggerContext.getLogger("org.apache");
		apacheLogger.setLevel(ch.qos.logback.classic.Level.OFF);
		ch.qos.logback.classic.Logger gitHubLogger = loggerContext.getLogger("io.github");
		gitHubLogger.setLevel(ch.qos.logback.classic.Level.OFF);
		ch.qos.logback.classic.Logger mongodbLogger = loggerContext.getLogger("org.mongodb");
		mongodbLogger.setLevel(ch.qos.logback.classic.Level.OFF);
		ch.qos.logback.classic.Logger ePamhealenium = loggerContext.getLogger("com.epam");
		ePamhealenium.setLevel(ch.qos.logback.classic.Level.OFF);
		ch.qos.logback.classic.Logger healenium = loggerContext.getLogger("healenium");
		healenium.setLevel(ch.qos.logback.classic.Level.OFF);
	}

	private WebDriver driver;

	// NOTE: find these credentials in your Gridlastic dashboard after launching
	// your selenium grid (get a free account).
	String hub = "http://10.45.55.128:4444/wd/hub";

	public void setUp() throws Exception {
		String strChromeDriverPath = PropertyHandler.getExternalString(AppConstants.WIN_CHROME_DRIVER_PATH_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		File aChromeDriverFile = AppUtils.getFileFromPath(strChromeDriverPath);
		BrowsersConfigBean aBrowsersConfigBean = new BrowsersConfigBean();
		aBrowsersConfigBean.setBrowser(Browsers.WINDOWS_CHROME);
		aBrowsersConfigBean.setBrowserSequence(1);
		aBrowsersConfigBean.setBrowserDisplayName(Browsers.WINDOWS_CHROME.getBrowserName());
		aBrowsersConfigBean.setBrowserPrority(200);
		aBrowsersConfigBean.setScrollTimeOut(500);
		aBrowsersConfigBean.setPlatFormName("Windows");
		aBrowsersConfigBean.setCanAutoUpdateDriver(true);
		ChromeOptions aChromeBrowserOptions = WebUtils.getChromeOptions(aBrowsersConfigBean, aChromeDriverFile);
		aChromeBrowserOptions.setCapability("se:downloadsEnabled", true);
		driver = new RemoteWebDriver(new URL(hub), aChromeBrowserOptions);
	}

	public void test_download_file_selenium_version() throws Exception {
		driver.get("https://foundation.mozilla.org/en/who-we-are/public-records/");
//		String fileName = "mf-articles-of-incorporation.pdf";
		WebElement element = driver.findElement(By.linkText("Articles of Incorporation of the Mozilla Foundation"));
		element.click();
		TimeUnit.SECONDS.sleep(30);

		URL gridUrl = new URL(hub);
		String strTestCaseID ="1";
		SessionId aSessionId = ((RemoteWebDriver) driver).getSessionId();
		String strFilesEndPoint = String.format("/session/%s/se/files", aSessionId);
		// Get downloaded file names
		HttpRequest request = new HttpRequest(HttpMethod.GET, strFilesEndPoint);
		AppContext appContext = AppContext.getInstance();
		try (HttpClient client = HttpClient.Factory.createDefault().createClient(gridUrl);) {
			HttpResponse response = client.execute(request);
			int iStatusCode = response.getStatus();
			if (!response.isSuccessful()) {
				String strErrorMessage = EnglishReasonPhraseCatalog.INSTANCE.getReason(iStatusCode, Locale.US);
				throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_TELEGRAM_NOTIFICATION, hub,
						String.valueOf(iStatusCode), strErrorMessage));
			}
			String strResponseData = org.openqa.selenium.remote.http.Contents.string(response);
			JSONObject jsonObject = new JSONObject(strResponseData);
			JSONObject aValue = jsonObject.getJSONObject("value");
			JSONArray aFileNames = aValue.getJSONArray("names");
			List<String> lstFiles = new LinkedList<>();
			if(aFileNames == null) {
				throw new IOException(MessageFormat.format(ErrorMsgConstants.FILENTFOUND, strFilesEndPoint));
			}
			aFileNames.forEach(aFile -> lstFiles.add(aFile.toString()));

			LinkedList<String> lstGridDwFiles = appContext.getGridDownloadedFiles(strTestCaseID,
					aSessionId);
			int iLastElement = CollectionUtils.size(lstFiles) - 1;
			String strFileToDw = CollectionUtils.isEmpty(lstGridDwFiles) ? lstFiles.get(iLastElement)
					: lstFiles.stream().filter(strFileName -> {
						return !lstGridDwFiles.contains(strFileName);
					}).findFirst().orElse(null);
			if (StringUtils.isEmpty(StringUtils.trim(strFileToDw))) {
				throw new IOException(MessageFormat.format(ErrorMsgConstants.FILENTFOUND, strFilesEndPoint));
			}
			
			if (lstFiles.contains(strFileToDw)) {
				System.out.println("Found downloaded file: " + strFileToDw);
			}

			// Download the file
			TimeUnit.SECONDS.sleep(30);
			HttpRequest req = new HttpRequest(HttpMethod.POST, strFilesEndPoint);
			String payload = new Json().toJson(Collections.singletonMap("name", strFileToDw));
			req.setContent(() -> new ByteArrayInputStream(payload.getBytes()));
			HttpResponse aPostResponse = client.execute(req);
			if (!aPostResponse.isSuccessful()) {
				String strErrorMessage = EnglishReasonPhraseCatalog.INSTANCE.getReason(iStatusCode, Locale.US);
				throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_TELEGRAM_NOTIFICATION, hub,
						String.valueOf(iStatusCode), strErrorMessage));
			}
			strResponseData = org.openqa.selenium.remote.http.Contents.string(aPostResponse);
			JSONObject aFileResposne = new JSONObject(strResponseData);
			JSONObject aFileResponseValue = aFileResposne.getJSONObject("value");
			String strFileName = aFileResponseValue.getString("filename");
			String encodedContents = aFileResponseValue.getString("contents");

			// The file contents would always be a zip file and has to be unzipped.
			String home = System.getProperty("user.home");
			File dirToCopyTo = new File(
					home + "/downloads/gridnodes/" + aSessionId + "/");
			Zip.unzip(encodedContents, dirToCopyTo);
			appContext.addGridDownloadedFile(strTestCaseID, aSessionId, strFileName);
		}

	}

	public void tearDown() throws Exception {
		driver.quit();
	}

	public static void main(String[] args) {
		GridDownloadFile aGridDownloadFile = new GridDownloadFile();
		System.setProperty("conf.path", "C:\\UKLifeAuto\\GitLocalRepo\\UKLifeAutomation\\src\\main\\resources");
		System.setProperty("app.name", "UnderWriting_Adviser");
		System.setProperty("app.env", "NEW_SIT03");

		try {
			MasterConfig aMasterConfig = MasterConfig.getInstance();
			AppEnvConfigBean aPPRunEnv = aMasterConfig.getAppEnvConfigBean();
			if (aPPRunEnv == null) {
				System.exit(0); // Exception case and application cannot proceed so just simply exit by logging
			}
			String strConfigPopFile = String.format("%s/%s%s", AppConstants.APP_ENV_PROPERTIES_LOC,
					aPPRunEnv.getPropertyName(), AppConstants.PROPERTIES_FILE_SUFFIX);
			PropertyHandler.mergeExternalResourceBundle(strConfigPopFile, AppConstants.APP_PROPERTIES_NAME);
			aGridDownloadFile.setUp();
			aGridDownloadFile.test_download_file_selenium_version();
			aGridDownloadFile.test_download_file_selenium_version();
			aGridDownloadFile.test_download_file_selenium_version();
		} catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		} finally {
			try {
				aGridDownloadFile.tearDown();
			} catch (Exception e) {
			}
		}
	}

}
