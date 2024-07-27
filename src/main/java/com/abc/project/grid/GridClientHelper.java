/****************************************************************************
 * File Name 		: GridClientHelper.java
 * Package			: com.dxc.zurich.grid
 * Author			: pmusunuru2
 * Creation Date	: Jun 01, 2021
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.grid;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.remote.CapabilityType;

import com.abc.project.beans.AppEnvConfigBean;
import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.beans.TestDataBean;
import com.abc.project.beans.TestSuiteBean;
import com.abc.project.config.AppConfig;
import com.abc.project.config.AppContext;
import com.abc.project.config.MasterConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.ErrorMsgConstants;
import com.abc.project.constants.SummaryReportConstants;
import com.abc.project.enums.Browsers;
import com.abc.project.enums.GridActionType;
import com.abc.project.enums.StartFinish;
import com.abc.project.reports.ConsolidateTestReport;
import com.abc.project.utils.AppUtils;
import com.aventstack.extentreports.Status;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.appium.java_client.remote.MobileCapabilityType;

/**
 * @author pmusunuru2
 * @since Jun 01, 2021 11:58:39 am
 */
public class GridClientHelper {

	/***
	 * A sample client to connect to the server. Usage Example:
	 * action=loadControllerSuiteBean HostName=Test
	 * action=<loadControllerSuiteBean|workAssign|updateStatus> 1. client = * new
	 * Client(); <br>
	 * String msg = build the string in the format URL query. The start character
	 * has to be ?, and key=value separated by &. E.g:
	 * ?action=loadControllerSuiteBean&HostName=Test <br>
	 * 3. send message by using client.sendMessage(msg). This returns the response
	 * as String. <br>
	 * 4. stop the connection by calling client.stopConnection().<br>
	 * action=workAssign : to check test step is assigned<br>
	 * action=updateStatus : to check update teststep Execution status.<br>
	 * 
	 * @param aLOGGER
	 * @param strServerAddress
	 * @param iServerDataPort
	 * @param strMessages
	 * @return
	 * @throws Exception
	 */
	private static String runClient(Logger aLOGGER, String strServerAddress, int iServerDataPort,
			GridActionType aGridActionType, List<NameValuePair> lstQueryParams) throws Exception {

		if (aGridActionType == null || aGridActionType == GridActionType.INVALID
				|| CollectionUtils.isEmpty(lstQueryParams)) {
			throw new Exception("Usage Example:  action=loadControllerSuiteBean HostName=Test");
		}
		String strLogMessage = "Client Sending Message!";
		aLOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
		try (Socket aClientSocket = new Socket(strServerAddress, iServerDataPort);
				PrintWriter aMessageWriter = new PrintWriter(aClientSocket.getOutputStream(), true);
				BufferedReader aResposneReader = new BufferedReader(
						new InputStreamReader(aClientSocket.getInputStream()));) {
			StringBuilder aMessageBuilder = new StringBuilder();
			aMessageBuilder.append(AppConstants.SEPARATOR_QUESTION);
			aMessageBuilder.append(String.format("%s=%s", BaseAction.ACTION_KEY, aGridActionType.getAction()))
					.append(AppConstants.SEPARATOR_AMPERSAND);
			aMessageBuilder.append(URLEncodedUtils.format(lstQueryParams, StandardCharsets.UTF_8.name()));
			String strMessage = aMessageBuilder.toString();

			aMessageWriter.println(strMessage);
			aMessageWriter.flush();
			String strResponse = aResposneReader.readLine();
			return strResponse;
		} finally {
			aLOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	public static LinkedList<BrowsersConfigBean> getBrowserConfig(Logger aLOGGER, AppEnvConfigBean aPPRunEnv,
			LinkedHashSet<String> stBrowserDisplayName) throws Exception {
		List<NameValuePair> lstQueryParams = new ArrayList<>();
		lstQueryParams.add(new BasicNameValuePair(BaseAction.HOME_NAME_KEY, AppUtils.getHostName()));
		Gson aGson = AppUtils.getDefaultGson();
		lstQueryParams
				.add(new BasicNameValuePair(BaseAction.BROWSER_CONFIG_BEAN_KEY, aGson.toJson(stBrowserDisplayName)));
		String strResponse = runClient(aLOGGER, aPPRunEnv.getHostAddress(), aPPRunEnv.getHostPort(),
				GridActionType.BROWSER_CONFIG, lstQueryParams);
		Type aBrowserConfigBeanType = new TypeToken<LinkedList<BrowsersConfigBean>>() {
		}.getType();
		LinkedList<BrowsersConfigBean> lstBrowserConfigBeans = aGson.fromJson(strResponse, aBrowserConfigBeanType);
		if (CollectionUtils.isEmpty(lstBrowserConfigBeans)) {
			return new LinkedList<>();
		}
		return lstBrowserConfigBeans;
	}

	public static LinkedList<TestSuiteBean> getTestSuiteData(Logger aLOGGER, AppEnvConfigBean aPPRunEnv)
			throws Exception {
		List<NameValuePair> lstQueryParams = new ArrayList<>();
		lstQueryParams.add(new BasicNameValuePair(BaseAction.HOME_NAME_KEY, AppUtils.getHostName()));
		String strResponse = runClient(aLOGGER, aPPRunEnv.getHostAddress(), aPPRunEnv.getHostPort(),
				GridActionType.CONTROLLER_SUTE, lstQueryParams);
		Gson aGson = AppUtils.getDefaultGson();
		Type aTestSuteBeanType = new TypeToken<LinkedList<TestSuiteBean>>() {
		}.getType();
		LinkedList<TestSuiteBean> lstTestSuiteBeans = aGson.fromJson(strResponse, aTestSuteBeanType);
		if (CollectionUtils.isEmpty(lstTestSuiteBeans)) {
			return new LinkedList<>();
		}
		return lstTestSuiteBeans;
	}

	public static boolean hasTestStepAssigned(Logger aLOGGER, AppEnvConfigBean aPPRunEnv, TestSuiteBean aTestSuiteBean)
			throws Exception {
		Gson aGson = AppUtils.getDefaultGson();
		List<NameValuePair> lstQueryParams = new ArrayList<>();
		lstQueryParams.add(new BasicNameValuePair(BaseAction.HOME_NAME_KEY, AppUtils.getHostName()));
		lstQueryParams.add(new BasicNameValuePair(BaseAction.SCENARIO_BEAN_KEY, aGson.toJson(aTestSuiteBean)));
		String strResponse = runClient(aLOGGER, aPPRunEnv.getHostAddress(), aPPRunEnv.getHostPort(),
				GridActionType.HAS_SCENARIO_ASSIGNED, lstQueryParams);
		return BooleanUtils.toBoolean(strResponse);
	}

	public static boolean updateScenarioStatus(Logger aLOGGER, AppEnvConfigBean aPPRunEnv, TestSuiteBean aTestSuiteBean,
			BrowsersConfigBean aBrowsersConfigBean, String strExecResult) throws Exception {
		Gson aGson = AppUtils.getDefaultGson();
		List<NameValuePair> lstQueryParams = new ArrayList<>();
		lstQueryParams.add(new BasicNameValuePair(BaseAction.HOME_NAME_KEY, AppUtils.getHostName()));
		lstQueryParams.add(new BasicNameValuePair(BaseAction.SCENARIO_BEAN_KEY, aGson.toJson(aTestSuiteBean)));
		lstQueryParams.add(new BasicNameValuePair(BaseAction.SCENARIO_STATUS_KEY, strExecResult));
		lstQueryParams
				.add(new BasicNameValuePair(BaseAction.BROWSER_CONFIG_BEAN_KEY, aGson.toJson(aBrowsersConfigBean)));
		if (!StringUtils.equalsIgnoreCase(strExecResult, AppConstants.TEST_RESULT_OTHERS)) {
			LinkedHashMap<String, String> mpSummarySheet = ConsolidateTestReport.getSummaryReportData(
					SummaryReportConstants.SUMMARY_SHEETNAME, aTestSuiteBean.getScenarioName(), aBrowsersConfigBean);

			LinkedHashMap<String, String> mpRefSheet = ConsolidateTestReport.getSummaryReportData(
					SummaryReportConstants.REF_NUM_SHEETNAME, aTestSuiteBean.getScenarioName(), aBrowsersConfigBean);

			LinkedHashMap<String, String> mpQuoteSheet = ConsolidateTestReport.getSummaryReportData(
					SummaryReportConstants.QUOTE_SUMMARY_SHEETNAME, aTestSuiteBean.getScenarioName(),
					aBrowsersConfigBean);

			if (mpSummarySheet != null && !mpSummarySheet.isEmpty()) {
				lstQueryParams.add(
						new BasicNameValuePair(SummaryReportConstants.SUMMARY_SHEETNAME, aGson.toJson(mpSummarySheet)));
			}
			if (mpRefSheet != null && !mpRefSheet.isEmpty()) {
				lstQueryParams.add(
						new BasicNameValuePair(SummaryReportConstants.REF_NUM_SHEETNAME, aGson.toJson(mpRefSheet)));
			}

			if (mpQuoteSheet != null && !mpQuoteSheet.isEmpty()) {
				lstQueryParams.add(new BasicNameValuePair(SummaryReportConstants.QUOTE_SUMMARY_SHEETNAME,
						aGson.toJson(mpQuoteSheet)));
			}
			LinkedHashMap<String, LinkedHashMap<String, LinkedList<String>>> mpOtherSummary = AppContext.getInstance()
					.getOtherSummaryReport(aTestSuiteBean.getScenarioName(), aBrowsersConfigBean);
			if (mpOtherSummary != null && !mpOtherSummary.isEmpty()) {
				lstQueryParams
						.add(new BasicNameValuePair(BaseAction.OTHER_SUMMARY_BEAN_KEY, aGson.toJson(mpOtherSummary)));
			}
		}
		String strResponse = runClient(aLOGGER, aPPRunEnv.getHostAddress(), aPPRunEnv.getHostPort(),
				GridActionType.UPDATE_STATUS, lstQueryParams);
		return BooleanUtils.toBoolean(strResponse);
	}

	public static TestDataBean getTestDataBean(Logger aLOGGER, AppEnvConfigBean aPPRunEnv, TestSuiteBean aTestSuiteBean)
			throws Exception {
		Gson aGson = AppUtils.getDefaultGson();
		List<NameValuePair> lstQueryParams = new ArrayList<>();
		lstQueryParams.add(new BasicNameValuePair(BaseAction.HOME_NAME_KEY, AppUtils.getHostName()));
		lstQueryParams.add(new BasicNameValuePair(BaseAction.SCENARIO_BEAN_KEY, aGson.toJson(aTestSuiteBean)));
		String strResponse = runClient(aLOGGER, aPPRunEnv.getHostAddress(), aPPRunEnv.getHostPort(),
				GridActionType.TEST_DATA, lstQueryParams);
		return aGson.fromJson(strResponse, TestDataBean.class);
	}

	public static String logTestReport(Logger aLOGGER, Logger aErrorLOGGER, AppEnvConfigBean aPPRunEnv, Status aStatus,
			BrowsersConfigBean aBrowsersConfigBean, String testScenarioName, String stepDescription,
			String strLogMessage, File aSnapShotFile) {
		Gson aGson = AppUtils.getDefaultGson();
		List<NameValuePair> lstQueryParams = new ArrayList<>();
		lstQueryParams
				.add(new BasicNameValuePair(BaseAction.BROWSER_CONFIG_BEAN_KEY, aGson.toJson(aBrowsersConfigBean)));
		lstQueryParams.add(new BasicNameValuePair(BaseAction.SCENARIO_STATUS_KEY, aGson.toJson(aStatus)));
		lstQueryParams.add(new BasicNameValuePair(BaseAction.TEST_STEP_SCENARIO_NAME, testScenarioName));
		lstQueryParams.add(new BasicNameValuePair(BaseAction.TEST_STEP_SCENARIO_DESCRIPTION, stepDescription));
		lstQueryParams.add(new BasicNameValuePair(BaseAction.TEST_STEP_LOG_MESSAGE, strLogMessage));
		try {
			if (aSnapShotFile != null && aSnapShotFile.exists()) {
				lstQueryParams.add(new BasicNameValuePair(BaseAction.TEST_STEP_FILE_NAME, aSnapShotFile.getName()));
				lstQueryParams.add(new BasicNameValuePair(BaseAction.TEST_STEP_FILE, aGson.toJson(aSnapShotFile)));
			}
			String strResponse = runClient(aLOGGER, aPPRunEnv.getHostAddress(), aPPRunEnv.getHostPort(),
					GridActionType.TEST_STEP_REPORT, lstQueryParams);
			return strResponse;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			aLOGGER.error(strErrorMsg);
			aErrorLOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		}
	}

	public static void flushReport(Logger aLOGGER, AppEnvConfigBean aPPRunEnv, BrowsersConfigBean aBrowsersConfigBean,
			String testScenarioName) throws Exception {
		if (StringUtils.isEmpty(StringUtils.trim(testScenarioName))) {
			return;
		}
		Gson aGson = AppUtils.getDefaultGson();
		List<NameValuePair> lstQueryParams = new ArrayList<>();
		lstQueryParams
				.add(new BasicNameValuePair(BaseAction.BROWSER_CONFIG_BEAN_KEY, aGson.toJson(aBrowsersConfigBean)));
		lstQueryParams.add(new BasicNameValuePair(BaseAction.TEST_STEP_SCENARIO_NAME, testScenarioName));
		runClient(aLOGGER, aPPRunEnv.getHostAddress(), aPPRunEnv.getHostPort(), GridActionType.FLUSH_STEP_REPORT,
				lstQueryParams);
	}

	public static boolean isHostReachable(String strHostAddress, int iPort) {
		try (Socket aSocket = new Socket()) {
			// This limits the time allowed to establish a connection in the case
			// that the connection is refused or server doesn't exist.
			aSocket.connect(new InetSocketAddress(strHostAddress, iPort),
					AppConstants.SERVER_DATA_CONNECTION_WAIT_TIME);
			return aSocket != null && aSocket.isConnected()
					&& aSocket.getInetAddress().isReachable(AppConstants.SERVER_DATA_CONNECTION_WAIT_TIME);
		} catch (Exception e) {
			return false;
		}
	}

	public static File getAppiumNodeConfig(BrowsersConfigBean aBrowserConfigBean, AppEnvConfigBean aPPRunEnv)
			throws Exception {
		Browsers aBrowser = aBrowserConfigBean.getBrowser();
		JSONObject aCapabiltiesData = new JSONObject();
		aCapabiltiesData.put("applicationName", MasterConfig.getInstance().getAppName());
		aCapabiltiesData.put(MobileCapabilityType.PLATFORM_NAME, aBrowserConfigBean.getPlatFormName());
		aCapabiltiesData.put(MobileCapabilityType.PLATFORM_VERSION, aBrowserConfigBean.getVersion());
		aCapabiltiesData.put("maxInstances", 1);
		aCapabiltiesData.put(CapabilityType.BROWSER_NAME, aBrowserConfigBean.getBrowserName());
		aCapabiltiesData.put(MobileCapabilityType.DEVICE_NAME, aBrowserConfigBean.getDeviceID());

		JSONArray capArray = new JSONArray();
		capArray.put(aCapabiltiesData);

		JSONObject aParentData = new JSONObject();
		JSONObject aConfigData = new JSONObject();

		aConfigData.put("nodeTimeout", 120);
		aConfigData.put("cleanUpCycle", 2000);
		aConfigData.put("timeout", 399000);
		aConfigData.put(CapabilityType.PROXY, "org.openqa.grid.selenium.proxy.DefaultRemoteProxy");
		aConfigData.put("maxSession", 1);
		aConfigData.put("nodePolling", 2000);
		aConfigData.put("register", true);
		aConfigData.put("registerCycle", 5000);
		aConfigData.put("hubPort", aPPRunEnv.getHostPort());
		aConfigData.put("hubHost", aPPRunEnv.getHostAddress());

		aParentData.put("capabilities", capArray);
		aParentData.put("configuration", aConfigData);
		String strJsonFileName = String.format(AppConstants.NODE_CONFIG_JSON_FILE_NAME, aBrowser.getBrowserShortName());
		File aNodeConfigFile = Paths
				.get(AppConfig.getInstance().getBrowserExecutionReportFolder(aBrowserConfigBean), strJsonFileName)
				.toFile();
		if (!aNodeConfigFile.getParentFile().exists()) {
			aNodeConfigFile.getParentFile().mkdirs();
		}
		FileUtils.writeStringToFile(aNodeConfigFile, aParentData.toString(), StandardCharsets.UTF_8);
		if (aNodeConfigFile.exists()) {
			return aNodeConfigFile;
		}
		throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.FILENTFOUND, aNodeConfigFile.getPath()));
	}
}
