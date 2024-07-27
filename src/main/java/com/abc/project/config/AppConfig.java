/****************************************************************************
 * File Name 		: AppConfig.java
 * Package			: com.dxc.zurich.config
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
package com.abc.project.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.abc.project.alm.beans.ALMWrapperConfigBean;
import com.abc.project.beans.ALMConfigBean;
import com.abc.project.beans.AppEnvConfigBean;
import com.abc.project.beans.AppPriorityConfigBean;
import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.beans.KeyWordConfigBean;
import com.abc.project.beans.PDFExclusions;
import com.abc.project.beans.TestDataBean;
import com.abc.project.beans.TestSuiteBean;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.ErrorMsgConstants;
import com.abc.project.enums.AppRunMode;
import com.abc.project.enums.Browsers;
import com.abc.project.enums.PDFFileType;
import com.abc.project.enums.StartFinish;
import com.abc.project.grid.GridClientHelper;
import com.abc.project.grid.RemoteMultiPlatformServerHelper;
import com.abc.project.utils.AppUtils;
import com.abc.project.utils.ExcelUtils;
import com.abc.project.utils.PropertyHandler;
import com.abc.project.utils.RunTimeDataUtils;
import com.browserstack.local.Local;
import com.dxc.constants.RemoteMultiPlatformErrorMsgConstants;
import com.dxc.enums.ExecutionStatus;
import com.dxc.enums.HostPriority;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 12:08:09 pm
 */
public class AppConfig {

	private static final Logger LOGGER = LogManager.getLogger(AppConfig.class);

	private static AppConfig instance;

	private List<BrowsersConfigBean> lstBrowserConFig;

	private List<TestSuiteBean> lstTestSuiteData;

	private LinkedHashMap<String, LinkedHashSet<String>> mpHostBrowsers;

	private String strExecutionReportFolder;

	private LinkedHashMap<String, Local> mpBrowserStackLocal;

	private LinkedHashSet<String> stGridRegisterdHosts;

	private java.sql.Date dtExecutionDate;

	private AppConfig() {
		dtExecutionDate = new java.sql.Date(new java.util.Date().getTime());
		initializeAppConfig();
	}

	public static AppConfig getInstance() {
		if (null == instance) {
			synchronized (AppConfig.class) {

				if (null == instance) {
					instance = new AppConfig();
				}
			}
		}
		return instance;
	}

	public void initializeAppConfig() {
		mpBrowserStackLocal = new LinkedHashMap<>();
		mpHostBrowsers = new LinkedHashMap<>();
		stGridRegisterdHosts = new LinkedHashSet<>();
		strExecutionReportFolder = null;
		if (CollectionUtils.isNotEmpty(lstBrowserConFig)) {
			lstBrowserConFig.clear();
			lstBrowserConFig = null;
		}
		if (CollectionUtils.isNotEmpty(lstTestSuiteData)) {
			lstTestSuiteData.clear();
			lstTestSuiteData = null;
		}
	}

	public void setAppVmArgs(AppEnvConfigBean aPPRunEnv, String[] strRunArgs) throws Exception {
		AppRunMode aAppRunMode;
		if (strRunArgs == null || strRunArgs.length <= 0) {
			aAppRunMode = AppRunMode.NORMAL;
		} else {
			aAppRunMode = AppRunMode.getAppRunModeByName(strRunArgs[0]);
		}
		// Validate Args
		switch (aAppRunMode) {
		case MUTLI_GRID_PLATFORM:
			if (strRunArgs.length != 4) {
				throw new Exception(
						AppUtils.formatMessage(ErrorMsgConstants.ERR_SERVER_CMD_ARGS, aAppRunMode.getAppRunMode()));
			}
			String strRMPGServerIP = strRunArgs[1];
			InetAddress aServerRMPGInetAddress = AppUtils.getValidAddress(strRMPGServerIP);
			if (aServerRMPGInetAddress == null) {
				throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_INVALID_HOST_ADD, strRMPGServerIP));
			}
			String strRMPGServerPort = strRunArgs[2];
			if (!StringUtils.isNumeric(strRMPGServerPort)) {
				throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_INVALID_PORT, strRMPGServerPort));
			}
			String strRMPGPrority = strRunArgs[3];
			HostPriority aHostPriority = HostPriority.getHostPriorityByPriority(strRMPGPrority);
			if (HostPriority.INVALID_NORMAL == aHostPriority) {
				throw new Exception(AppUtils.formatMessage(RemoteMultiPlatformErrorMsgConstants.INVALID_HOST_PROTITY,
						strRMPGPrority));
			}
			aPPRunEnv.setHostAddress(aServerRMPGInetAddress.getHostName());
			aPPRunEnv.setHostPort(Integer.parseInt(strRMPGServerPort));
			aPPRunEnv.setPriority(aHostPriority);
			RemoteMultiPlatformServerHelper.getInstance().registerHost();
			break;
		case SELENIUM_SERVER:
			if (strRunArgs.length != 2) {
				throw new Exception(
						AppUtils.formatMessage(ErrorMsgConstants.ERR_SERVER_CMD_ARGS, aAppRunMode.getAppRunMode()));
			}
			String strServerIP = AppUtils.getHostIpAddress();
			InetAddress aServerInetAddress = AppUtils.getValidAddress(strServerIP);
			if (aServerInetAddress == null) {
				throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_INVALID_HOST_ADD, strServerIP));
			}
			String strServerPort = strRunArgs[1];
			if (!StringUtils.isNumeric(strServerPort)) {
				throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_INVALID_PORT, strServerPort));
			}
			aPPRunEnv.setHostAddress(aServerInetAddress.getHostName());
			aPPRunEnv.setHostPort(Integer.parseInt(strServerPort));
			aPPRunEnv.setPriority(HostPriority.INVALID_NORMAL);
			break;
		case SELENIUM_NODE:
			if (strRunArgs.length != 3) {
				throw new Exception(
						AppUtils.formatMessage(ErrorMsgConstants.ERR_NODE_CMD_ARGS, aAppRunMode.getAppRunMode()));
			}
			String strNodeAddress = strRunArgs[1];
			InetAddress aNodeInetAddress = AppUtils.getValidAddress(strNodeAddress);
			if (aNodeInetAddress == null) {
				throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_INVALID_HOST_ADD, strNodeAddress));
			}
			aPPRunEnv.setHostAddress(aNodeInetAddress.getHostName());
			if (StringUtils.equalsIgnoreCase(aPPRunEnv.getHostAddress(), AppUtils.getHostName())) {
				throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_NODE_CMD_REGISTER_ARGS,
						aPPRunEnv.getHostAddress()));
			}
			String strClientHubPort = strRunArgs[2];
			if (!StringUtils.isNumeric(strClientHubPort)) {
				throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_INVALID_PORT, strClientHubPort));
			}
			aPPRunEnv.setHostPort(Integer.parseInt(strClientHubPort));
			aPPRunEnv.setPriority(HostPriority.INVALID_NORMAL);
			break;
		case SELENIUM_GRID:
		case APP_PRORITY_GRID:
			if (strRunArgs.length != 4) {
				throw new Exception(
						AppUtils.formatMessage(ErrorMsgConstants.ERR_NODE_CMD_ARGS, aAppRunMode.getAppRunMode()));
			}
			String strGridAddress = strRunArgs[1];
			InetAddress aGridAddress = AppUtils.getValidAddress(strGridAddress);
			if (aGridAddress == null) {
				throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_INVALID_HOST_ADD, strGridAddress));
			}
			String strGridPort = strRunArgs[2];
			if (!StringUtils.isNumeric(strGridPort)) {
				throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_INVALID_PORT, strGridPort));
			}
			aPPRunEnv.setHostAddress(aGridAddress.getHostName());
			aPPRunEnv.setHostPort(Integer.parseInt(strGridPort));
			if (!GridClientHelper.isHostReachable(aPPRunEnv.getHostAddress(), aPPRunEnv.getHostPort())) {
				throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_HOST_CONNECT,
						aPPRunEnv.getHostAddress(), aPPRunEnv.getHostPort()));
			}
			String strNodes = strRunArgs[3];
			String[] strGridHosts = StringUtils.split(strNodes, AppConstants.SEPARATOR_COMMA);
			if (strGridHosts == null || strGridHosts.length <= 0) {
				throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.UNKNOWN_ARGUMENT, strNodes));
			}
			for (String strNode : strGridHosts) {
				String[] strGridNodes = StringUtils.split(strNode, AppConstants.SEPARATOR_COLON);
				if (strGridNodes == null || strGridNodes.length != 2) {
					throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_INVALID_GRID_HOST, strNode));
				}
				String strGridNodeAddress = strGridNodes[0];
				InetAddress aGridNodeAddress = AppUtils.getValidAddress(strGridNodeAddress);
				if (aGridNodeAddress == null) {
					throw new Exception(
							AppUtils.formatMessage(ErrorMsgConstants.ERR_INVALID_HOST_ADD, aGridNodeAddress));
				}
				String strGridNodeHostName = aGridNodeAddress.getHostName();
				if (StringUtils.containsIgnoreCase(strGridNodeHostName, AppUtils.getHostName())) {
					continue;
				}
				String strGridNodePort = strGridNodes[1];
				if (!StringUtils.isNumeric(strGridNodePort)) {
					throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_INVALID_PORT, strGridNodePort));
				}
				if (!GridClientHelper.isHostReachable(strGridNodeHostName, Integer.valueOf(strGridNodePort))) {
					throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_HOST_CONNECT, strGridNodeHostName,
							Integer.valueOf(strGridNodePort)));
				}
				stGridRegisterdHosts.add(String.format("%s:%s", strGridNodeHostName, strGridNodePort));
			}
			stGridRegisterdHosts.add(AppUtils.getHostName());
			aPPRunEnv.setPriority(HostPriority.INVALID_NORMAL);
			break;
		case APP_PRORITY:// This Case will be handled in APP Priority Runner
		default:
			String strSystemIP = AppUtils.getHostName();
			InetAddress aInetAddress = AppUtils.getValidAddress(strSystemIP);
			if (aInetAddress == null) {
				throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_INVALID_HOST_ADD, strSystemIP));
			}
			aPPRunEnv.setHostAddress(aInetAddress.getHostName());
			aPPRunEnv.setPriority(HostPriority.INVALID_NORMAL);
			break;
		}
		aPPRunEnv.setAppRunMode(aAppRunMode);
	}

	public void loadConfig() throws Exception {
		loadControllerSuiteBean();
		loadBrowserConfig();
		RunTimeDataUtils.createRunTimeDataBackUp();
		AppContext aPPContext = AppContext.getInstance();
		LinkedHashMap<String, LinkedHashMap<String, String>> runTimeDataMap = RunTimeDataUtils.getRunTimeValues();
		aPPContext.setRunTimeDataMap(runTimeDataMap);
	}

	private void loadBrowserConfig() throws Exception {
		String strMethodName = "loadBrowserConfig";
		AppEnvConfigBean aPPRunEnv = MasterConfig.getInstance().getAppEnvConfigBean();
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strMethodName));
			AppRunMode aAppRunMode = aPPRunEnv.getAppRunMode();
			Set<Map.Entry<String, LinkedHashSet<String>>> stHostEntry = mpHostBrowsers.entrySet();
			for (Entry<String, LinkedHashSet<String>> aEntry : stHostEntry) {
				switch (aAppRunMode) {
				case SELENIUM_NODE:
					LinkedList<BrowsersConfigBean> lstNodeBorwserConfig = GridClientHelper.getBrowserConfig(LOGGER,
							aPPRunEnv, getBrowserDisplayNames(aEntry.getKey()));
					lstNodeBorwserConfig.stream().forEach(aConfig -> {
						aConfig.setBrowserHost(aEntry.getKey());
						addBrowserConFig(aConfig);
					});
					break;
				default:
					LinkedList<BrowsersConfigBean> lstBorwserConfig = getBrowserConfig(
							getBrowserDisplayNames(aEntry.getKey()));
					lstBorwserConfig.stream().forEach(aConfig -> {
						aConfig.setBrowserHost(aEntry.getKey());
						addBrowserConFig(aConfig);
					});
					break;
				}
			}
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strMethodName));
		}
	}

	public LinkedList<BrowsersConfigBean> getBrowserConfig(LinkedHashSet<String> stBrowserDisplayName)
			throws Exception {
		// FileOpening
		String strBrowserConfigFile = PropertyHandler.getExternalString(AppConstants.BROWSER_CONFIGFILE_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		if (StringUtils.isEmpty(strBrowserConfigFile)) {
			return new LinkedList<>();
		}
		LinkedList<BrowsersConfigBean> lstBorwserConfig = new LinkedList<>();
		File aBrowserConfigFFile = AppUtils.getFileFromPath(strBrowserConfigFile);
		if (!aBrowserConfigFFile.exists()) {
			throw new IOException(MessageFormat.format(ErrorMsgConstants.FILENTFOUND, aBrowserConfigFFile.getName()));
		}
		try (XSSFWorkbook aWorkbook = ExcelUtils.getWorkBook(aBrowserConfigFFile)) {
			FormulaEvaluator evaluator = aWorkbook.getCreationHelper().createFormulaEvaluator();
			String[] strSheetsToRead = { AppConstants.BROWSER_CONFIG_SHEETNAME, AppConstants.DEVICE_CONFIG_SHEETNAME };
			for (String strSheetName : strSheetsToRead) {
				Sheet aControllerSheet = aWorkbook.getSheet(strSheetName);
				if (aControllerSheet == null) {
					throw new IOException(MessageFormat.format(ErrorMsgConstants.SHEET_NOT_FOUND, strSheetName,
							aBrowserConfigFFile.getPath()));
				}
				switch (strSheetName) {
				case AppConstants.BROWSER_CONFIG_SHEETNAME:
					LinkedList<BrowsersConfigBean> lstBrowserConfigs = getBrowserConfigBean(stBrowserDisplayName,
							evaluator, aControllerSheet);
					lstBorwserConfig.addAll(lstBrowserConfigs);
					break;
				case AppConstants.DEVICE_CONFIG_SHEETNAME:
					LinkedList<BrowsersConfigBean> lstDeviceConfigs = getDeviceConfigBean(stBrowserDisplayName,
							evaluator, aControllerSheet);
					lstBorwserConfig.addAll(lstDeviceConfigs);
					break;
				default:
					break;
				}
			}
		}
		return lstBorwserConfig;
	}

	private LinkedList<BrowsersConfigBean> getBrowserConfigBean(LinkedHashSet<String> stBrowserDisplayName,
			FormulaEvaluator evaluator, Sheet aControllerSheet) throws Exception {
		String strMethodName = "loadBrowserConfigBean";
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strMethodName));
			LinkedList<BrowsersConfigBean> lstBrowserConfigs = new LinkedList<>();
			String strSheetName = aControllerSheet.getSheetName();
			Gson aGson = AppUtils.getDefaultGson();
			Type capaBilityType = new TypeToken<LinkedHashMap<String, Object>>() {
			}.getType();
			for (int iRow = 1; iRow <= aControllerSheet.getLastRowNum() - aControllerSheet.getFirstRowNum(); iRow++) {
				Row row = ExcelUtils.getRow(aControllerSheet, iRow);
				if (row == null) {
					throw new Exception(MessageFormat.format(ErrorMsgConstants.ERR_ROW, iRow + 1, strSheetName));
				}
				String strBrowserDisplayName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row,
						1);
				boolean runFlag = !StringUtils
						.isEmpty(getFiltredBrowserName(stBrowserDisplayName, strBrowserDisplayName));
				if (!runFlag) {
					continue;
				}
				String browserName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 0);
				Browsers aBrowser = Browsers.getBrowserByName(browserName);
				String strBrowserPrority = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 2);
				String strScollTimeOut = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 3);
				String strPlatFormName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 4);
				String strPlatFormVersion = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 5,
						false);
				String strActivityName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 6,
						false);
				String strPackageName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 7,
						false);
				String strDevicePort = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 8);
				String strDeviceBootStrapPort = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName,
						row, 9);
				boolean isBrowserStack = aBrowser == Browsers.BROWSER_STACK_DESKTOP;
				String strBrowserStackUserName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName,
						row, 10, isBrowserStack);
				String strBrowserStackPassword = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName,
						row, 11, isBrowserStack);
				String strBrowserName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 12,
						isBrowserStack);
				String strIsBrowserStackLocal = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName,
						row, 13, isBrowserStack);
				String strBrowserExtraCapabilities = ExcelUtils.getStringValue(evaluator, aControllerSheet,
						strSheetName, row, 14, false);
				String strcanAutoUpdateDriver = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName,
						row, 15, false);
				strBrowserExtraCapabilities = RegExUtils.replaceAll(strBrowserExtraCapabilities, "(\r\n|\n)", "");
				strBrowserExtraCapabilities = StringUtils.trim(strBrowserExtraCapabilities);
				BrowsersConfigBean aBrowsersConfigBean = new BrowsersConfigBean();
				aBrowsersConfigBean.setBrowser(aBrowser);
				aBrowsersConfigBean
						.setBrowserDisplayName(AppUtils.removeIllegalCharacters(strBrowserDisplayName, true));
				aBrowsersConfigBean.setBrowserPrority(StringUtils.isNumeric(strBrowserPrority)
						&& StringUtils.isNotEmpty(StringUtils.trim(strBrowserPrority))
								? Integer.valueOf(strBrowserPrority)
								: aBrowser.getBrowserPrority());
				aBrowsersConfigBean.setScrollTimeOut(StringUtils.isNumeric(strScollTimeOut)
						&& StringUtils.isNotEmpty(StringUtils.trim(strScollTimeOut)) ? Integer.valueOf(strScollTimeOut)
								: AppConstants.SCROLL_TIMEOUT);
				aBrowsersConfigBean.setBrowserSequence(iRow);
				aBrowsersConfigBean.setRunFlag(runFlag);
				aBrowsersConfigBean.setSendEmail(MasterConfig.getInstance().canBrowserSendEmail());
				aBrowsersConfigBean.setPlatFormName(strPlatFormName);
				aBrowsersConfigBean.setVersion(
						StringUtils.isEmpty(StringUtils.trim(strPlatFormVersion)) ? "0" : strPlatFormVersion);
				aBrowsersConfigBean.setDeviceName(AppUtils.getHostName());
				aBrowsersConfigBean.setDeviceID(AppUtils.getHostName());
				aBrowsersConfigBean.setActivityName(strActivityName);
				aBrowsersConfigBean.setPackageName(strPackageName);
				aBrowsersConfigBean
						.setBrowserPort(StringUtils.isNumeric(strDevicePort) ? Long.valueOf(strDevicePort) : 0);
				aBrowsersConfigBean.setBrowserBootStrapPort(
						StringUtils.isNumeric(strDeviceBootStrapPort) ? Long.valueOf(strDeviceBootStrapPort) : 0);
				aBrowsersConfigBean.setBrowserStackUserName(strBrowserStackUserName);
				aBrowsersConfigBean.setBrowserStackPassword(strBrowserStackPassword);
				aBrowsersConfigBean.setBrowserName(strBrowserName);
				aBrowsersConfigBean.setBrowserStackLocal(BooleanUtils.toBoolean(strIsBrowserStackLocal));
				LinkedHashMap<String, Object> mpExtraCapabilities = aGson.fromJson(strBrowserExtraCapabilities,
						capaBilityType);
				aBrowsersConfigBean.setExtraCapabilities(mpExtraCapabilities);
				aBrowsersConfigBean.setCanAutoUpdateDriver(BooleanUtils.toBoolean(strcanAutoUpdateDriver));
				lstBrowserConfigs.add(aBrowsersConfigBean);
			}
			return lstBrowserConfigs;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strMethodName));
		}
	}

	private LinkedList<BrowsersConfigBean> getDeviceConfigBean(LinkedHashSet<String> stBrowserDisplayName,
			FormulaEvaluator evaluator, Sheet aControllerSheet) throws Exception {
		String strMethodName = "loadDeviceConfigBean";
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strMethodName));
			LinkedList<BrowsersConfigBean> lstDeviceConfigs = new LinkedList<>();
			String strSheetName = aControllerSheet.getSheetName();
			Gson aGson = AppUtils.getDefaultGson();
			Type capaBilityType = new TypeToken<LinkedHashMap<String, Object>>() {
			}.getType();
			for (int iRow = 1; iRow <= aControllerSheet.getLastRowNum() - aControllerSheet.getFirstRowNum(); iRow++) {
				Row row = ExcelUtils.getRow(aControllerSheet, iRow);
				if (row == null) {
					throw new Exception(MessageFormat.format(ErrorMsgConstants.ERR_ROW, iRow + 1, strSheetName));
				}
				String strBrowserDisplayName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row,
						1);
				boolean runFlag = !StringUtils
						.isEmpty(getFiltredBrowserName(stBrowserDisplayName, strBrowserDisplayName));
				if (!runFlag) {
					continue;
				}
				String browserName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 0);
				Browsers aBrowser = Browsers.getBrowserByName(browserName);
				String strBrowserPrority = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 2);
				String strScollTimeOut = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 3);
				String strDeviceName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 4);
				String strDeviceID = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 5);
				String strPlatFormName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 6);
				String strDeviceVersion = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 7);
				String strActivityName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 8,
						false);
				String strPackageName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 9,
						false);
				String strDevicePort = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 10);
				String strDeviceBootStrapPort = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName,
						row, 11);
				boolean isBrowserStack = aBrowser == Browsers.BROWSER_STACK_MOBILE;
				String strBrowserStackUserName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName,
						row, 12, isBrowserStack);
				String strBrowserStackPassword = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName,
						row, 13, isBrowserStack);
				String strBrowserName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 14,
						isBrowserStack);
				String strIsBrowserStackLocal = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName,
						row, 15, isBrowserStack);
				String strBrowserExtraCapabilities = ExcelUtils.getStringValue(evaluator, aControllerSheet,
						strSheetName, row, 16, false);
				String strcanAutoUpdateDriver = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName,
						row, 17, false);
				strBrowserExtraCapabilities = RegExUtils.replaceAll(strBrowserExtraCapabilities, "(\r\n|\n)", "");
				strBrowserExtraCapabilities = StringUtils.trim(strBrowserExtraCapabilities);
				BrowsersConfigBean aDeviceConfigBean = new BrowsersConfigBean();
				aDeviceConfigBean.setBrowserSequence(iRow);
				aDeviceConfigBean.setBrowser(aBrowser);
				aDeviceConfigBean.setBrowserPrority(StringUtils.isNumeric(strBrowserPrority)
						&& StringUtils.isNotEmpty(StringUtils.trim(strBrowserPrority))
								? Integer.valueOf(strBrowserPrority)
								: aBrowser.getBrowserPrority());
				aDeviceConfigBean.setScrollTimeOut(StringUtils.isNumeric(strScollTimeOut)
						&& StringUtils.isNotEmpty(StringUtils.trim(strScollTimeOut)) ? Integer.valueOf(strScollTimeOut)
								: AppConstants.SCROLL_TIMEOUT);
				aDeviceConfigBean.setBrowserDisplayName(AppUtils.removeIllegalCharacters(strBrowserDisplayName, true));
				aDeviceConfigBean.setRunFlag(runFlag);
				aDeviceConfigBean.setSendEmail(MasterConfig.getInstance().canBrowserSendEmail());
				aDeviceConfigBean.setDeviceName(strDeviceName);
				aDeviceConfigBean.setDeviceID(strDeviceID);
				aDeviceConfigBean.setPlatFormName(strPlatFormName);
				aDeviceConfigBean.setVersion(strDeviceVersion);
				aDeviceConfigBean.setActivityName(strActivityName);
				aDeviceConfigBean.setPackageName(strPackageName);
				aDeviceConfigBean
						.setBrowserPort(StringUtils.isNumeric(strDevicePort) ? Long.valueOf(strDevicePort) : 0);
				aDeviceConfigBean.setBrowserBootStrapPort(
						StringUtils.isNumeric(strDeviceBootStrapPort) ? Long.valueOf(strDeviceBootStrapPort) : 0);
				aDeviceConfigBean.setBrowserStackUserName(strBrowserStackUserName);
				aDeviceConfigBean.setBrowserStackPassword(strBrowserStackPassword);
				aDeviceConfigBean.setBrowserName(strBrowserName);
				aDeviceConfigBean.setBrowserStackLocal(BooleanUtils.toBoolean(strIsBrowserStackLocal));
				LinkedHashMap<String, Object> mpExtraCapabilities = aGson.fromJson(strBrowserExtraCapabilities,
						capaBilityType);
				aDeviceConfigBean.setExtraCapabilities(mpExtraCapabilities);
				aDeviceConfigBean.setCanAutoUpdateDriver(BooleanUtils.toBoolean(strcanAutoUpdateDriver));
				lstDeviceConfigs.add(aDeviceConfigBean);
			}
			return lstDeviceConfigs;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strMethodName));
		}
	}

	public synchronized TestDataBean getTestDataBean(TestSuiteBean aTestSuiteBean) throws Exception {
		TestDataBean aTestData = null;
		String strControllerFile = PropertyHandler.getExternalString(AppConstants.CONTROLLER_CONFIGFILE_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		if (StringUtils.isEmpty(strControllerFile)) {
			return aTestData;
		}
		String testScenarioName = aTestSuiteBean == null ? "UnKnown" : aTestSuiteBean.getScenarioName();
		File aControllerFile = AppUtils.getFileFromPath(strControllerFile);
		if (!aControllerFile.exists()) {
			throw new IOException(MessageFormat.format(ErrorMsgConstants.FILENTFOUND, aControllerFile.getName()));
		}
		String strMethodName = AppUtils.formatMessage("{0} - loadTestDataBean", testScenarioName);
		String strSheetName = aTestSuiteBean.getTestDataSheetName();
		String strScreenShotData = String.format("%s*", AppConstants.DEFAULT_TRUE);
		try (XSSFWorkbook aWorkbook = ExcelUtils.getWorkBook(aControllerFile)) {
			FormulaEvaluator evaluator = aWorkbook.getCreationHelper().createFormulaEvaluator();
			LOGGER.info(StartFinish.START.getFormattedMsg(strMethodName));

			Sheet aControllerSheet = aWorkbook.getSheet(strSheetName);
			Row testStepRow = ExcelUtils.getRow(aControllerSheet, 0);
			if (testStepRow == null) {
				throw new Exception(MessageFormat.format(ErrorMsgConstants.ERR_ROW, 1, strSheetName));
			}

			Row testStepKeyWordRow = ExcelUtils.getRow(aControllerSheet, 1);
			if (testStepKeyWordRow == null) {
				throw new Exception(MessageFormat.format(ErrorMsgConstants.ERR_ROW, 2, strSheetName));
			}

			Row keyWordRow = ExcelUtils.getRow(aControllerSheet, 2);
			if (keyWordRow == null) {
				throw new Exception(MessageFormat.format(ErrorMsgConstants.ERR_ROW, 3, strSheetName));
			}

			Row aKeyWordType = ExcelUtils.getRow(aControllerSheet, 3);
			if (aKeyWordType == null) {
				throw new Exception(MessageFormat.format(ErrorMsgConstants.ERR_ROW, 4, strSheetName));
			}

			Row obJectPropRow = ExcelUtils.getRow(aControllerSheet, 4);
			if (obJectPropRow == null) {
				throw new Exception(MessageFormat.format(ErrorMsgConstants.ERR_ROW, 5, strSheetName));
			}

			// Loop through the Controller sheet to retrieve DBConFig

			for (int iRow = 5; iRow <= aControllerSheet.getLastRowNum(); iRow++) {
				Row row = ExcelUtils.getRow(aControllerSheet, iRow);

				if (row == null) {
					throw new Exception(MessageFormat.format(ErrorMsgConstants.ERR_ROW, iRow + 1, strSheetName));
				}

				int lastCellNumber = row.getLastCellNum();

				String scenarioName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 0);
				scenarioName = AppUtils.removeIllegalCharacters(scenarioName, true);
				if (!StringUtils.equalsIgnoreCase(scenarioName, testScenarioName)) {
					continue;
				}
				String description = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 1,
						false);
				if (StringUtils.isEmpty(StringUtils.trim(description))) {
					description = aTestSuiteBean.getDescription();
				}
				aTestData = new TestDataBean();
				aTestData.setScenarioName(scenarioName);
				aTestData.setDescription(description);
				for (int cellNum = 2; cellNum <= lastCellNumber; cellNum++) {
					String controlData = ExcelUtils.getStringValue(evaluator, aControllerSheet, row, cellNum);
					controlData = StringUtils.trim(controlData);
					if (StringUtils.equalsIgnoreCase(AppConstants.DEFAULT_END, controlData)) {
						break;
					}
					if (StringUtils.isEmpty(controlData)) {
						continue;
					}

					Cell testStepCell = ExcelUtils.getCell(testStepRow, cellNum);
					String strTestStepName = ExcelUtils.getMergerdCellValue(evaluator, aControllerSheet, strSheetName,
							testStepCell);
					if (StringUtils.isEmpty(strTestStepName)) {
						strTestStepName = "Un-Known SCREEN / Cell(s) Not Merged Properly";
					}
					String reportKeyWord = ExcelUtils.getStringValue(evaluator, aControllerSheet, testStepKeyWordRow,
							cellNum);
					reportKeyWord = StringUtils.trim(reportKeyWord);
					String strKeyWord = ExcelUtils.getStringValue(evaluator, aControllerSheet, keyWordRow, cellNum);
					strKeyWord = StringUtils.trim(strKeyWord);
					if (StringUtils.isEmpty(strKeyWord)) {
						continue;
					}
					String strKeyWordType = ExcelUtils.getStringValue(evaluator, aControllerSheet, aKeyWordType,
							cellNum);
					String objectProperty = ExcelUtils.getStringValue(evaluator, aControllerSheet, obJectPropRow,
							cellNum);
					objectProperty = StringUtils.trim(objectProperty);
					String strScreenShot = null;
					if (!StringUtils.isEmpty(reportKeyWord) && reportKeyWord.contains("*")) {
						strScreenShot = AppConstants.DEFAULT_TRUE;
						reportKeyWord = reportKeyWord.replace("*", "");
					}
					if (StringUtils.equalsIgnoreCase(strScreenShotData, controlData)) {
						strScreenShot = AppConstants.DEFAULT_TRUE;
						controlData = StringUtils.replace(controlData, strScreenShotData, AppConstants.DEFAULT_TRUE);
					}

					aTestData.addControlData(controlData);
					KeyWordConfigBean aKeyWordConfigBean = new KeyWordConfigBean();
					aKeyWordConfigBean.setOriginalKeyWord(strKeyWord);
					aKeyWordConfigBean.setOriginalKeyWordType(strKeyWordType);
					String strMessageDescRiption = String.format(
							"SCREEN -> %s ; FIELD -> %s ; Row (%d) Column (%s)  VALUE -> %s",
							StringUtils.trim(strTestStepName), reportKeyWord, iRow + 1,
							ExcelUtils.cellNumToAlphabetic(cellNum), controlData);
					aTestData.addMessageDescription(strMessageDescRiption);
					aTestData.addScreenShot(strScreenShot);
					aTestData.addReportKeyWord(reportKeyWord);
					aTestData.addKeyWord(aKeyWordConfigBean);
					aTestData.addObjectProperty(objectProperty);
				}

				int iControlDataSize = CollectionUtils.size(aTestData.getControlData());
				int iMessageDescSize = CollectionUtils.size(aTestData.getMessageDescription());
				int iScreenShotSize = CollectionUtils.size(aTestData.getScreenShot());
				int iKeyWordSize = CollectionUtils.size(aTestData.getKeyWord());
				int iObjectPropertySize = CollectionUtils.size(aTestData.getObjectProperty());

				if (iControlDataSize != iMessageDescSize && iControlDataSize != iScreenShotSize
						&& iControlDataSize != iKeyWordSize && iControlDataSize != iObjectPropertySize) {
					throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.DATA_LIST_SIZE_MISSMATCH, scenarioName,
							description, row.getRowNum() + 1));
				}
				return aTestData;
			}
			return aTestData;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strMethodName));
		}
	}

	private LinkedList<PDFExclusions> getScenarioPDFExclusions(File aControllerFile, XSSFWorkbook aWorkbook,
			FormulaEvaluator evaluator, String scenarioName) throws Exception {
		LinkedList<PDFExclusions> lstPDExclusions = new LinkedList<>();
		String strMethodName = AppUtils.formatMessage("fetch PDF Exclusions for scenario {0}", scenarioName);
		String strSheetName = AppConstants.APP_PDF_EXCLUSIONS_SHEET_NAME;
		LOGGER.info(StartFinish.START.getFormattedMsg(strMethodName));
		Sheet aEnvConfig = aWorkbook.getSheet(strSheetName);
		if (aEnvConfig == null) {
			return lstPDExclusions;
		}
		LinkedHashMap<String, Integer> colMapByName = ExcelUtils.getColumnNames(aControllerFile, aEnvConfig,
				strSheetName);
		// Loop through the ExportTables sheet to retrieve DBConFig
		for (int iRow = 1; iRow <= aEnvConfig.getLastRowNum() - aEnvConfig.getFirstRowNum(); iRow++) {
			Row row = ExcelUtils.getRow(aEnvConfig, iRow);
			if (row == null) {
				throw new Exception(MessageFormat.format(ErrorMsgConstants.ERR_ROW, iRow + 1, strSheetName));
			}
			String strScenarioName = ExcelUtils.getStringValue(evaluator, aEnvConfig, strSheetName, row,
					colMapByName.getOrDefault("Scenario Name", 0));
			if (!StringUtils.equalsIgnoreCase(strScenarioName, AppUtils.getValidPartScenarioName(scenarioName))) {
				continue;
			}
			String strPDFFileType = ExcelUtils.getStringValue(evaluator, aEnvConfig, strSheetName, row,
					colMapByName.getOrDefault("PDF File", 1));
			String strExpectedPath = ExcelUtils.getStringValue(evaluator, aEnvConfig, strSheetName, row,
					colMapByName.getOrDefault("Expected PDF", 2), false);
			String strExclusionAreas = ExcelUtils.getStringValue(evaluator, aEnvConfig, strSheetName, row,
					colMapByName.getOrDefault("Exclusion Coordinates", 3), false);
			PDFFileType aPDfFileType = PDFFileType.getPDFFileType(strPDFFileType);
			if (!StringUtils.isEmpty(StringUtils.trim(strExpectedPath))
					&& !StringUtils.isEmpty(StringUtils.trim(strExclusionAreas))
					&& (aPDfFileType == null || aPDfFileType == PDFFileType.INVALID)) {
				continue;
			}

			PDFExclusions aPDExclusions = new PDFExclusions();
			aPDExclusions.setScenarioName(strScenarioName);
			aPDExclusions.setFileType(aPDfFileType);
			if (!StringUtils.isEmpty(StringUtils.trim(strExpectedPath))) {
				aPDExclusions.setExpectedPath(AppUtils.getFileFromPath(strExpectedPath));
			}
			aPDExclusions.setExclusionAreas(StringUtils.trim(strExclusionAreas));
			lstPDExclusions.add(aPDExclusions);
		}
		return lstPDExclusions;
	}

	private void loadControllerSuiteBean() throws Exception {
		String strMethodName = "loadControllerSuiteBean";
		AppEnvConfigBean aPPRunEnv = MasterConfig.getInstance().getAppEnvConfigBean();
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strMethodName));

			AppRunMode aAppRunMode = aPPRunEnv.getAppRunMode();
			switch (aAppRunMode) {
			case SELENIUM_NODE:
				LinkedList<TestSuiteBean> lstNodeTestSuiteBean = GridClientHelper.getTestSuiteData(LOGGER, aPPRunEnv);
				lstNodeTestSuiteBean.stream().forEach(aTestSuiteBean -> {
					addControllerSuite(aTestSuiteBean);
					addBrowserName(aTestSuiteBean.getHostAddress(), aTestSuiteBean.getBrowserDisplayName());
				});
				break;
			case SELENIUM_GRID:
			case APP_PRORITY_GRID:
				LinkedList<TestSuiteBean> lstGridTestSuiteBean = getControllerSuiteBean();
				LinkedHashSet<String> stBrowsers = new LinkedHashSet<>();
				lstGridTestSuiteBean.stream().forEach(aTestSuiteConfig -> {
					stBrowsers.add(AppUtils.removeIllegalCharacters(aTestSuiteConfig.getBrowserDisplayName(), true));
				});
				for (String strBrowser : stBrowsers) {
					List<TestSuiteBean> lstBrowserTestData = lstGridTestSuiteBean.stream()
							.filter(aConFig -> StringUtils.equalsIgnoreCase(
									AppUtils.removeIllegalCharacters(aConFig.getBrowserDisplayName(), true),
									AppUtils.removeIllegalCharacters(strBrowser, true)) && aConFig.isRunFlag())
							.collect(Collectors.toList());
					int iTestDataSize = CollectionUtils.size(lstBrowserTestData);
					int iScenarioCount = Math.floorDiv(iTestDataSize, stGridRegisterdHosts.size());
					int iScenarioIndex = 0;
					String strLastElement = AppUtils.getLastElement(stGridRegisterdHosts);
					LinkedHashMap<String, LinkedList<TestSuiteBean>> mpHostData = new LinkedHashMap<>();
					for (String strHost : stGridRegisterdHosts) {
						for (int i = 0; i < iTestDataSize; i++) {
							int iValueIndex = iScenarioIndex + i;
							if (iValueIndex >= iTestDataSize) {
								break;
							}
							TestSuiteBean aTestSuiteBean = lstBrowserTestData.get(iValueIndex);
							aTestSuiteBean.setHostAddress(strHost);
							LinkedList<TestSuiteBean> lstHostData = mpHostData.get(strHost);
							if (CollectionUtils.isEmpty(lstHostData)) {
								lstHostData = new LinkedList<>();
							}
							addControllerSuite(aTestSuiteBean);
							addBrowserName(aTestSuiteBean.getHostAddress(), aTestSuiteBean.getBrowserDisplayName());
							lstHostData.add(aTestSuiteBean);
							mpHostData.put(strHost, lstHostData);
							if (CollectionUtils.size(lstHostData) == iScenarioCount
									&& !StringUtils.equalsIgnoreCase(strLastElement, strHost)) {
								break;
							}
						}
						iScenarioIndex = iScenarioIndex + iScenarioCount;
					}
				}
				break;
			case MUTLI_GRID_PLATFORM:
				LinkedList<TestSuiteBean> lstMPGDefaltTestSuiteBean = RemoteMultiPlatformServerHelper.getInstance()
						.registerScenario(getControllerSuiteBean());
				lstMPGDefaltTestSuiteBean.stream().forEach(aTestSuiteBean -> {
					addControllerSuite(aTestSuiteBean);
					addBrowserName(aTestSuiteBean.getHostAddress(), aTestSuiteBean.getBrowserDisplayName());
				});
				break;
			default:
				LinkedList<TestSuiteBean> lstDefaltTestSuiteBean = getControllerSuiteBean();
				lstDefaltTestSuiteBean.stream().forEach(aTestSuiteBean -> {
					addControllerSuite(aTestSuiteBean);
					addBrowserName(aTestSuiteBean.getHostAddress(), aTestSuiteBean.getBrowserDisplayName());
				});
				break;
			}
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strMethodName));
		}
	}

	public LinkedList<TestSuiteBean> getControllerSuiteBean() throws Exception {
		String strMethodName = "getControllerSuiteBean";
		AppEnvConfigBean aPPRunEnv = MasterConfig.getInstance().getAppEnvConfigBean();
		AppPriorityConfig aAppPriorityConfig = AppPriorityConfig.getInstance();
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strMethodName));
			LinkedList<TestSuiteBean> lstTestSuiteBeans = new LinkedList<>();
			// FileOpening
			String strControllerFile = PropertyHandler.getExternalString(AppConstants.CONTROLLER_CONFIGFILE_KEY,
					AppConstants.APP_PROPERTIES_NAME);
			if (StringUtils.isEmpty(strControllerFile)) {
				return lstTestSuiteBeans;
			}

			File aControllerFile = AppUtils.getFileFromPath(strControllerFile);
			if (!aControllerFile.exists()) {
				throw new IOException(MessageFormat.format(ErrorMsgConstants.FILENTFOUND, aControllerFile.getName()));
			}
			String strSheetName = AppConstants.CONTROLLER_SUITE_SHEETNAME;
			try (XSSFWorkbook aWorkbook = ExcelUtils.getWorkBook(aControllerFile)) {
				FormulaEvaluator evaluator = aWorkbook.getCreationHelper().createFormulaEvaluator();
				Sheet aControllerSheet = aWorkbook.getSheet(strSheetName);
				if (aControllerSheet == null) {
					throw new IOException(MessageFormat.format(ErrorMsgConstants.SHEET_NOT_FOUND, strSheetName,
							aControllerFile.getPath()));
				}

				// Loop through the ExportTables sheet to retrieve DBConFig
				for (int iRow = 1; iRow <= aControllerSheet.getLastRowNum()
						- aControllerSheet.getFirstRowNum(); iRow++) {
					Row row = ExcelUtils.getRow(aControllerSheet, iRow);
					if (row == null) {
						throw new Exception(MessageFormat.format(ErrorMsgConstants.ERR_ROW, iRow + 1, strSheetName));
					}
					String scenarioName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 0);
					AppPriorityConfigBean aAPPriorityConfigBean = aAppPriorityConfig
							.getFileredAppPriorityConfigByScenario(scenarioName);
					String strRunFlag = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 4,
							false);
					boolean runFlag = aAPPriorityConfigBean == null ? BooleanUtils.toBoolean(strRunFlag)
							: aAPPriorityConfigBean.isRunFlag();
					if (!runFlag) {
						continue;
					}
					String description = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 1);
					String strBrowserDisplayName = aAPPriorityConfigBean == null
							? ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row, 2)
							: aAPPriorityConfigBean.getBrowserDisplayName();
					String strTestDataSheetName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName,
							row, 3, false);
					TestSuiteBean aTestSuiteBean = new TestSuiteBean();
					aTestSuiteBean.setOriginalScenarioName(scenarioName);
					aTestSuiteBean.setScenarioName(AppUtils.removeIllegalCharacters(scenarioName, true));
					aTestSuiteBean.setScenarioSerialNumber(iRow);
					aTestSuiteBean.setDescription(description);
					aTestSuiteBean.setBrowserDisplayName(strBrowserDisplayName);
					aTestSuiteBean.setTestDataSheetName(StringUtils.isEmpty(StringUtils.trim(strTestDataSheetName))
							? AppConstants.CONTROLLER_DATA_SHEETNAME
							: strTestDataSheetName);

					aTestSuiteBean.setHostAddress(aPPRunEnv.getHostAddress());
					aTestSuiteBean.setRunFlag(runFlag);
					LinkedList<PDFExclusions> lstPDExclusions = getScenarioPDFExclusions(aControllerFile, aWorkbook, evaluator,
							scenarioName);
					aTestSuiteBean.setPDFExclusions(lstPDExclusions);
					MasterConfig aMasterConfig = MasterConfig.getInstance();
					ALMConfigBean aAlmConfig = aMasterConfig.getAppEnvConfigBean() == null ? null
							: aMasterConfig.getAppEnvConfigBean().getAlmConfig();
					if (aAlmConfig != null) {
						ALMWrapperConfigBean almWrapperConfigBean = new ALMWrapperConfigBean();
						almWrapperConfigBean.setOriginalScenarioName(aTestSuiteBean.getOriginalScenarioName());
						almWrapperConfigBean.setBrowserDisplayName(aTestSuiteBean.getBrowserDisplayName());
						almWrapperConfigBean.setALMURL(aAlmConfig.getALMURL());
						almWrapperConfigBean.setALMUserName(aAlmConfig.getUserName());
						almWrapperConfigBean.setALMClientId(aAlmConfig.getALMClientId());
						almWrapperConfigBean.setALMClientSecret(aAlmConfig.getALMClientSecret());
						almWrapperConfigBean.setALMDomain(aAlmConfig.getDomain());
						almWrapperConfigBean.setALMProject(aAlmConfig.getProject());
						almWrapperConfigBean.setALMTestCasePrefix(aAlmConfig.getTestCasePrefix());
						almWrapperConfigBean.setALMRunName(aAlmConfig.getRunName());
						aTestSuiteBean.setALMWrapperConfigBean(almWrapperConfigBean);
					}
					lstTestSuiteBeans.add(aTestSuiteBean);
				}
			}
			return lstTestSuiteBeans;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strMethodName));
		}
	}

	public void updateSystemDate(Date dtSystemDate, int iFirstCell, int iSecondCell) throws Exception {
		if (dtSystemDate == null) {
			return;
		}
		String strMethodName = "updateSystemDate";
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strMethodName));
			// FileOpening
			String strControllerFile = PropertyHandler.getExternalString(AppConstants.CONTROLLER_CONFIGFILE_KEY,
					AppConstants.APP_PROPERTIES_NAME);
			if (StringUtils.isEmpty(strControllerFile)) {
				return;
			}

			File aControllerFile = AppUtils.getFileFromPath(strControllerFile);
			if (!aControllerFile.exists()) {
				throw new IOException(MessageFormat.format(ErrorMsgConstants.FILENTFOUND, aControllerFile.getName()));
			}
			String strSheetName = AppConstants.SYSTEM_DATE_SHEETNAME;
			try (XSSFWorkbook aWorkbook = ExcelUtils.getWorkBook(aControllerFile)) {
				aWorkbook.setForceFormulaRecalculation(true);
				Sheet aControllerSheet = aWorkbook.getSheet(strSheetName);
				if (aControllerSheet == null) {
					throw new IOException(MessageFormat.format(ErrorMsgConstants.SHEET_NOT_FOUND, strSheetName,
							aControllerFile.getPath()));
				}
				int iRow = 2;
				Row row = ExcelUtils.getRow(aControllerSheet, iRow);
				if (row == null) {
					throw new Exception(MessageFormat.format(ErrorMsgConstants.ERR_ROW, iRow + 1, strSheetName));
				}

				int cellNum = iFirstCell;
				Cell cell = ExcelUtils.getCell(row, cellNum);

				if (cell == null) {
					throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_CELL, iRow + 1,
							ExcelUtils.cellNumToAlphabetic(cellNum), strSheetName));
				}
				cell.setCellValue(dtSystemDate);

				cellNum = iSecondCell;
				cell = ExcelUtils.getCell(row, cellNum);

				if (cell == null) {
					throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_CELL, iRow + 1,
							ExcelUtils.cellNumToAlphabetic(cellNum), strSheetName));
				}
				cell.setCellValue(dtSystemDate);
				ExcelUtils.writeWrokBook(aWorkbook, aControllerFile);
			}
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strMethodName));
		}
	}

	private List<BrowsersConfigBean> getBrowsersConFigs() {
		if (CollectionUtils.isEmpty(lstBrowserConFig)) {
			this.lstBrowserConFig = new LinkedList<>();
		}
		return lstBrowserConFig;
	}

	private List<TestSuiteBean> getControllerSuiteConfig() {
		if (CollectionUtils.isEmpty(lstTestSuiteData)) {
			this.lstTestSuiteData = new LinkedList<>();
		}
		return lstTestSuiteData;
	}

	private void addBrowserConFig(BrowsersConfigBean bean) {
		if (bean != null) {
			getBrowsersConFigs().add(bean);
		}
	}

	private void addControllerSuite(TestSuiteBean bean) {
		if (bean != null) {
			getControllerSuiteConfig().add(bean);
		}
	}

	private LinkedHashSet<String> getBrowserDisplayNames(String strHost) {
		if (mpHostBrowsers == null || mpHostBrowsers.isEmpty()) {
			this.mpHostBrowsers = new LinkedHashMap<>();
		}
		return mpHostBrowsers.get(strHost);
	}

	private void addBrowserName(String strHost, String strBrowserDisplayName) {
		LinkedHashSet<String> stBrowserDisplayNames = getBrowserDisplayNames(strHost);
		if (stBrowserDisplayNames == null) {
			stBrowserDisplayNames = new LinkedHashSet<>();
		}
		stBrowserDisplayNames.add(strBrowserDisplayName);
		mpHostBrowsers.put(strHost, stBrowserDisplayNames);
	}

	private String getFiltredBrowserName(LinkedHashSet<String> stBrowserDisplayName, String strBrowserDisplayName) {
		if (stBrowserDisplayName == null) {
			return null;
		}
		return stBrowserDisplayName.stream()
				.filter(strConfigName -> StringUtils.equalsIgnoreCase(strConfigName, strBrowserDisplayName)).findFirst()
				.orElse(null);
	}

	/**
	 * Fetches the filtered Browsers configurations Based on runFlag
	 * 
	 * @return
	 */
	public List<BrowsersConfigBean> getFilteredBrowserConFigs() {
		if (CollectionUtils.isNotEmpty(getBrowsersConFigs())) {
			return getBrowsersConFigs().stream().filter(aConFig -> aConFig.isRunFlag()).collect(Collectors.toList());
		}
		return new LinkedList<>();
	}

	public boolean isExecStatusFail(String strExecResult) {
		boolean bExecFail = StringUtils.equalsIgnoreCase(strExecResult, ExecutionStatus.FAIL.getStatus())
				|| StringUtils.equalsIgnoreCase(strExecResult, ExecutionStatus.WARING.getStatus())
				|| StringUtils.equalsIgnoreCase(strExecResult, ExecutionStatus.STOPED.getStatus());
		return bExecFail;
	}

	public boolean isExecStatusPass(String strExecResult) {
		boolean bExecFail = StringUtils.equalsIgnoreCase(strExecResult, ExecutionStatus.PASS.getStatus())
				|| StringUtils.equalsIgnoreCase(strExecResult, ExecutionStatus.COMPLETED.getStatus());
		return bExecFail;
	}

	/***
	 * Fetches the filtered controller suite by browser name
	 * 
	 * @param strBrowserName
	 * @return
	 */
	public List<TestSuiteBean> getFilteredControllerSuiteByBrowerName(String strBrowserOrSequence) {
		if (CollectionUtils.isNotEmpty(getControllerSuiteConfig())
				&& !StringUtils.equalsIgnoreCase(Browsers.INVALID_BROWSER.getBrowserName(), strBrowserOrSequence)) {
			return getControllerSuiteConfig().stream()
					.filter(aConFig -> StringUtils.equalsIgnoreCase(
							AppUtils.removeIllegalCharacters(aConFig.getBrowserDisplayName(), true),
							AppUtils.removeIllegalCharacters(strBrowserOrSequence, true)) && aConFig.isRunFlag()
							&& (StringUtils.isEmpty(aConFig.getStatus()) || !isExecStatusPass(aConFig.getStatus())))
					.collect(Collectors.toList());
		}
		return new LinkedList<>();
	}

	public List<TestSuiteBean> getFilteredControllerSuiteByStatus() {
		if (CollectionUtils.isNotEmpty(getControllerSuiteConfig())) {
			return getControllerSuiteConfig().stream()
					.filter(aConFig -> StringUtils.isEmpty(aConFig.getStatus()) && aConFig.isRunFlag())
					.collect(Collectors.toList());
		}

		return new LinkedList<>();
	}

	public TestSuiteBean getFilteredControllerSuiteByScenario(TestSuiteBean aTestSuiteBean) {
		if (CollectionUtils.isNotEmpty(getControllerSuiteConfig()) && aTestSuiteBean != null) {
			return getControllerSuiteConfig().stream().filter(
					aConFig -> StringUtils.equalsIgnoreCase(aConFig.getScenarioName(), aTestSuiteBean.getScenarioName())
							&& StringUtils.equalsIgnoreCase(
									AppUtils.removeIllegalCharacters(aConFig.getBrowserDisplayName(), true),
									AppUtils.removeIllegalCharacters(aTestSuiteBean.getBrowserDisplayName(), true))
							&& aConFig.isRunFlag())
					.findFirst().orElse(null);
		}
		return null;
	}

	public TestSuiteBean getFilteredControllerSuiteByScenarioAndBrowser(String strScenarioName,
			BrowsersConfigBean aBrowsersConfigBean) {
		if (CollectionUtils.isNotEmpty(getControllerSuiteConfig()) && aBrowsersConfigBean != null) {
			return getControllerSuiteConfig().stream()
					.filter(aConFig -> StringUtils.equalsIgnoreCase(
							AppUtils.removeIllegalCharacters(aConFig.getScenarioName(), true),
							AppUtils.removeIllegalCharacters(strScenarioName, true))
							&& StringUtils.equalsIgnoreCase(
									AppUtils.removeIllegalCharacters(aConFig.getBrowserDisplayName(), true),
									AppUtils.removeIllegalCharacters(aBrowsersConfigBean.getBrowserDisplayName(), true))
							&& aConFig.isRunFlag())
					.findFirst().orElse(null);
		}
		return null;
	}

	public void updateScenarioExecutionStatus(TestSuiteBean aTestSuiteBean, String strExecResult) {
		if (aTestSuiteBean == null) {
			return;
		}
		getControllerSuiteConfig().stream().forEach(aConFig -> {
			if (StringUtils.equalsIgnoreCase(strExecResult, AppConstants.TEST_RESULT_OTHERS)
					&& StringUtils.equalsIgnoreCase(AppUtils.getValidPartScenarioName(aConFig.getScenarioName()),
							aTestSuiteBean.getScenarioName())
					&& aConFig.isRunFlag()) {
				aConFig.setHostAddress(aTestSuiteBean.getHostAddress());
				aConFig.setStatus(strExecResult);
			}
			if (StringUtils.equalsIgnoreCase(aConFig.getScenarioName(), aTestSuiteBean.getScenarioName())
					&& StringUtils.equalsIgnoreCase(aConFig.getTestDataSheetName(),
							aTestSuiteBean.getTestDataSheetName())
					&& StringUtils.equalsIgnoreCase(
							AppUtils.removeIllegalCharacters(aConFig.getBrowserDisplayName(), true),
							AppUtils.removeIllegalCharacters(aTestSuiteBean.getBrowserDisplayName(), true))) {
				aConFig.setHostAddress(aTestSuiteBean.getHostAddress());
				aConFig.setStatus(strExecResult);
			}
		});
	}

	public java.sql.Date getExecutionDate() {
		if (dtExecutionDate == null) {
			dtExecutionDate = new java.sql.Date(new Date().getTime());
		}
		return dtExecutionDate;
	}

	/**
	 * Fetch the Execution ReportMain Folder
	 * 
	 * @return
	 */
	public String getExecutionReportFolder() {
		if (StringUtils.isEmpty(strExecutionReportFolder)) {
			String strReportsLoc = PropertyHandler.getExternalString(AppConstants.REPORTS_PATH_KEY,
					AppConstants.APP_PROPERTIES_NAME);
			String strReportDate = AppUtils.getDateAsString(getExecutionDate(),
					AppConstants.EXEC_REPORTS_FOLDER_DT_FORMAT);
			String strAPPName = MasterConfig.getInstance().getAppName();
			File aExecReportFolder = Paths.get(strReportsLoc, strReportDate, strAPPName).toAbsolutePath().toFile();
			if (!aExecReportFolder.exists()) {
				aExecReportFolder.mkdirs();
			}
			strExecutionReportFolder = aExecReportFolder.toString();
		}
		return strExecutionReportFolder;
	}

	public String getBrowserExecutionReportFolder(BrowsersConfigBean aBrowsersConfigBean) {
		// Grid Remove Browser Specific Folder
		AppEnvConfigBean aPPRunEnv = MasterConfig.getInstance().getAppEnvConfigBean();
		AppRunMode aAppRunMode = aPPRunEnv.getAppRunMode();
		File aBrowserExecDir = null;
		switch (aAppRunMode) {
		case SELENIUM_GRID:
		case SELENIUM_SERVER:
		case SELENIUM_NODE:
		case APP_PRORITY_GRID:
			aBrowserExecDir = Paths.get(getExecutionReportFolder(),
					AppUtils.removeIllegalCharacters(aAppRunMode.getAppRunMode(), true)).toFile();
			break;
		default:
			aBrowserExecDir = Paths.get(getExecutionReportFolder(), AppUtils.removeIllegalCharacters(
					aBrowsersConfigBean == null || StringUtils.isEmpty(aBrowsersConfigBean.getReportBrowserName())
							? aAppRunMode.getAppRunMode()
							: aBrowsersConfigBean.getReportBrowserName(),
					true)).toFile();
			break;
		}

		if (!aBrowserExecDir.exists()) {
			aBrowserExecDir.mkdirs();
		}
		return aBrowserExecDir.toString();
	}

	public File getXMLReportFolder() {
		String strReportsLoc = getExecutionReportFolder();

		File aXMLReportFolder = Paths.get(strReportsLoc, AppConstants.XML_FOLDER).toFile();
		if (!aXMLReportFolder.exists()) {
			aXMLReportFolder.mkdirs();
		}
		return aXMLReportFolder;
	}

	public File getDOCReportFolder() {
		String strReportsLoc = getExecutionReportFolder();

		File aDOCReportFolder = Paths.get(strReportsLoc, AppConstants.DOC_FOLDER).toFile();
		if (!aDOCReportFolder.exists()) {
			aDOCReportFolder.mkdirs();
		}
		return aDOCReportFolder;
	}

	public synchronized void startBrowserStackLocalInstance(BrowsersConfigBean aBrowsersConfigBean) throws Exception {
		String strKey = aBrowsersConfigBean.getBrowserStackPassword();
		if (!aBrowsersConfigBean.isBrowserStackLocal() || mpBrowserStackLocal.get(strKey) != null) {
			return;
		}
		MasterConfig aMasterConfig = MasterConfig.getInstance();
		Local browserStackLocal = new Local();
		String strBrowserStackPath = PropertyHandler.getExternalString(
				AppConstants.WIN_BROWSERSTACK_LOCAL_DRIVER_PATH_KEY, AppConstants.APP_PROPERTIES_NAME);
		File aBrowserStackFile = AppUtils.getFileFromPath(strBrowserStackPath);
		HashMap<String, String> bsLocalArgs = new HashMap<String, String>();
		bsLocalArgs.put("key", strKey);
		bsLocalArgs.put("forcelocal", "true");
		bsLocalArgs.put("onlyAutomate", "true");
		bsLocalArgs.put("f", AppConfig.getInstance().getBrowserExecutionReportFolder(aBrowsersConfigBean));
		bsLocalArgs.put("binarypath", aBrowserStackFile.getPath());
		String strLocalIdentifier = AppUtils.formatMessage(AppConstants.ENV_URL_FORMAT, aMasterConfig.getAppName(),
				aMasterConfig.getAppRunID());
		bsLocalArgs.put("localIdentifier", strLocalIdentifier);
		// starts the Local instance with the required arguments
		browserStackLocal.start(bsLocalArgs);
		while (!browserStackLocal.isRunning()) {
			LOGGER.warn("Waiting to Start Browser Stack Local");
		}
		mpBrowserStackLocal.put(strKey, browserStackLocal);
	}

	public void stopBrowserStackLocalInstances() {
		try {
			mpBrowserStackLocal.entrySet().stream().forEach(config -> {
				try {
					Local browserStackLocal = config.getValue();
					browserStackLocal.stop();
				} catch (Exception ex) {
				}
			});
			mpBrowserStackLocal.clear();
		} catch (Exception ex) {
		}
	}
}
