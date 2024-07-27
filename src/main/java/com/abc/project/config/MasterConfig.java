/****************************************************************************
 * File Name 		: MasterConfig.java
 * Package			: com.dxc.zurich.config
 * Author			: pmusunuru2
 * Creation Date	: Mar 18, 2021
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
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.abc.project.beans.ALMConfigBean;
import com.abc.project.beans.AppEnvConfigBean;
import com.abc.project.beans.AppPriorityConfigBean;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.ErrorMsgConstants;
import com.abc.project.enums.AppPriority;
import com.abc.project.enums.StartFinish;
import com.abc.project.utils.AppUtils;
import com.abc.project.utils.EmailUtils;
import com.abc.project.utils.ExcelUtils;
import com.abc.project.utils.PropertyHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author pmusunuru2
 * @since Mar 18, 2021 10:13:40 am
 */
public class MasterConfig {

	private static final Logger LOGGER = LogManager.getLogger(MasterConfig.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	private static MasterConfig instance;

	private LinkedList<AppPriorityConfigBean> lstAppPriorityConfig;

	private AppEnvConfigBean aAppEnvConfig;

	private UUID aUniqueBuildID;

	private String strAppRunID;

	private LinkedHashMap<String, String> mpEnvDeatils;

	private MasterConfig() {
		aUniqueBuildID = UUID.randomUUID();
		Random aRandom = new Random();
		strAppRunID = String.format("%04d", aRandom.nextInt(10000));
		lstAppPriorityConfig = new LinkedList<>();
	}

	public static MasterConfig getInstance() {
		if (null == instance) {
			synchronized (AppConfig.class) {

				if (null == instance) {
					instance = new MasterConfig();
				}
			}
		}
		return instance;
	}

	public boolean canBrowserSendEmail() {
		String sendEmail = AppUtils.getEnvPropertyValue(AppConstants.BROWSER_SENDEMAIL_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		return BooleanUtils.toBoolean(sendEmail);
	}

	public String getAppName() {
		String strAPPName = AppUtils.getEnvPropertyValue(AppConstants.APP_NAME, AppConstants.APP_PROPERTIES_NAME);
		return strAPPName;
	}

	public String getAppENV() {
		String strAPPEnv = AppUtils.getEnvPropertyValue(AppConstants.APP_ENV, AppConstants.APP_PROPERTIES_NAME);
		return strAPPEnv;
	}

	public boolean canReRunFailedTestCases() {
		String strReRunFailedTC = AppUtils.getEnvPropertyValue(AppConstants.APP_RERUN_FAILED_TESTCASES_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		return BooleanUtils.toBoolean(strReRunFailedTC);
	}

	private boolean canPickAppPriorityRunMode() {
		String strPickAppPriorityRunMode = AppUtils.getEnvPropertyValue(AppConstants.APP_PRIORITY_PICK_RUNMODE_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		return BooleanUtils.toBoolean(strPickAppPriorityRunMode);
	}

	private LinkedHashMap<String, LinkedList<String>> getAppPriorityFilterConfig() throws Exception {
		String strAppPriorityFilterConfigFile = AppUtils
				.getEnvPropertyValue(AppConstants.APP_PRIORITY_FILTER_CONFIG_KEY, AppConstants.APP_PROPERTIES_NAME);
		File aAppPriorityFilterConfigFile = AppUtils.getFileFromPath(strAppPriorityFilterConfigFile);
		if (!aAppPriorityFilterConfigFile.exists()) {
			throw new IOException(
					MessageFormat.format(ErrorMsgConstants.FILENTFOUND, aAppPriorityFilterConfigFile.getName()));
		}
		try (java.io.Reader aAppPriorityFilterReader = Files
				.newBufferedReader(aAppPriorityFilterConfigFile.toPath());) {
			Gson aGson = AppUtils.getDefaultGson();
			Type aAppPriorityFilterType = new TypeToken<LinkedHashMap<String, LinkedList<String>>>() {
			}.getType();
			return aGson.fromJson(aAppPriorityFilterReader, aAppPriorityFilterType);
		}
	}

	public String getUniqueBuildId() {
		return aUniqueBuildID.toString();
	}

	public String getAppRunID() {
		return strAppRunID;
	}

	public LinkedList<AppPriorityConfigBean> getAppPriorityConfigBean() {
		if (!CollectionUtils.isEmpty(lstAppPriorityConfig)) {
			return lstAppPriorityConfig;
		}
		String strMethodName = "loadAppPriorityEnvConfig";
		String strErrorMsg = "Error while Fetching App Priority Config";
		LOGGER.info(StartFinish.START.getFormattedMsg(strMethodName));
		String strAppPriorityConfigFile = System.getProperty(AppConstants.APP_PRIORITY_CONFIG_KEY);
		strAppPriorityConfigFile = StringUtils.isEmpty(strAppPriorityConfigFile)
				? PropertyHandler.getExternalString(AppConstants.APP_PRIORITY_CONFIG_KEY,
						AppConstants.APP_PROPERTIES_NAME)
				: strAppPriorityConfigFile;
		if (StringUtils.isEmpty(strAppPriorityConfigFile)
				|| StringUtils.equalsIgnoreCase(strAppPriorityConfigFile, AppConstants.APP_PRIORITY_CONFIG_KEY)) {
			return lstAppPriorityConfig;
		}
		try {
			File aControllerFile = AppUtils.getFileFromPath(strAppPriorityConfigFile);
			if (!aControllerFile.exists()) {
				throw new IOException(MessageFormat.format(ErrorMsgConstants.FILENTFOUND, aControllerFile.getName()));
			}
			boolean bPickAppPriorityRunMode = canPickAppPriorityRunMode();
			LinkedHashMap<String, LinkedList<String>> mpAppPriorityFilterConfig = bPickAppPriorityRunMode
					? new LinkedHashMap<>()
					: getAppPriorityFilterConfig();
			String strSheetName = AppConstants.APP_PRIORITY_CONFIG_SHEET_NAME;
			try (XSSFWorkbook aWorkbook = ExcelUtils.getWorkBook(aControllerFile)) {
				FormulaEvaluator evaluator = aWorkbook.getCreationHelper().createFormulaEvaluator();
				Sheet aEnvConfig = aWorkbook.getSheet(strSheetName);
				if (aEnvConfig == null) {
					throw new IOException(MessageFormat.format(ErrorMsgConstants.SHEET_NOT_FOUND, strSheetName,
							aControllerFile.getPath()));
				}
				LinkedHashMap<String, Integer> colMapByName = ExcelUtils.getColumnNames(aControllerFile, aEnvConfig,
						strSheetName);
				for (int iRow = 1; iRow <= aEnvConfig.getLastRowNum() - aEnvConfig.getFirstRowNum(); iRow++) {
					Row row = ExcelUtils.getRow(aEnvConfig, iRow);
					if (row == null) {
						throw new Exception(MessageFormat.format(ErrorMsgConstants.ERR_ROW, iRow + 1, strSheetName));
					}
					String strRunFlag = ExcelUtils.getStringValue(evaluator, aEnvConfig, strSheetName, row,
							colMapByName.getOrDefault("Runmode", 4), false);

					boolean runFlag = (bPickAppPriorityRunMode && MapUtils.isEmpty(mpAppPriorityFilterConfig))
							? BooleanUtils.toBoolean(strRunFlag)
							: (!bPickAppPriorityRunMode && MapUtils.isNotEmpty(mpAppPriorityFilterConfig)) ? true
									: false;
					if (!runFlag) {
						continue;
					}
					if (!bPickAppPriorityRunMode && MapUtils.isNotEmpty(mpAppPriorityFilterConfig)) {
						Set<Entry<String, LinkedList<String>>> entryAppPriorityFilterConfig = mpAppPriorityFilterConfig
								.entrySet();
						boolean bHasFilterConfigMatched = entryAppPriorityFilterConfig.stream().allMatch(entry -> {
							try {
								String strKey = entry.getKey();
								LinkedList<String> lstValues = entry.getValue();
								String strColumnValue = (MapUtils.isNotEmpty(colMapByName)
										&& colMapByName.containsKey(strKey))
												? ExcelUtils.getStringValue(evaluator, aEnvConfig, strSheetName, row,
														colMapByName.get(strKey))
												: "";
								strColumnValue = StringUtils.trim(strColumnValue);
								return (CollectionUtils.isNotEmpty(lstValues) && StringUtils.isNotEmpty(strColumnValue)
										&& lstValues.contains(strColumnValue));
							} catch (Exception ex) {
								LOGGER.error(strErrorMsg);
								ERROR_LOGGER.error(strErrorMsg, ex);
								return false;
							}
						});

						if (!bHasFilterConfigMatched) {
							continue;
						}
					}
					AppPriorityConfigBean aAppPriorityConfig = new AppPriorityConfigBean();
					String strConfigAppName = ExcelUtils.getStringValue(evaluator, aEnvConfig, strSheetName, row,
							colMapByName.getOrDefault("Set Name / App Name", 0));
					String strScenarioName = ExcelUtils.getStringValue(evaluator, aEnvConfig, strSheetName, row,
							colMapByName.getOrDefault("Scenario Name", 1));
					String strBrowserDisplayName = ExcelUtils.getStringValue(evaluator, aEnvConfig, strSheetName, row,
							colMapByName.getOrDefault("Browser Name", 2));
					String strAppPriorityMode = ExcelUtils.getStringValue(evaluator, aEnvConfig, strSheetName, row,
							colMapByName.getOrDefault("Priority", 3));
					aAppPriorityConfig.setAppName(strConfigAppName);
					aAppPriorityConfig.setScenarioName(strScenarioName);
					aAppPriorityConfig.setBrowserDisplayName(strBrowserDisplayName);
					aAppPriorityConfig.setAppPriority(AppPriority.getAppPriorityByPriorityMode(strAppPriorityMode));
					aAppPriorityConfig.setRunFlag(runFlag);
					lstAppPriorityConfig.add(aAppPriorityConfig);
				}
			}
			return lstAppPriorityConfig;
		} catch (Exception ex) {
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strMethodName));
		}
		return lstAppPriorityConfig;

	}

	public AppEnvConfigBean getAppEnvConfigBean() {
		if (aAppEnvConfig != null) {
			return aAppEnvConfig;
		}

		String strMethodName = "loadAppEnvConfig";
		String strAPPName = getAppName();
		if (StringUtils.isEmpty(strAPPName)) {
			return aAppEnvConfig;
		}
		LOGGER.info(StartFinish.START.getFormattedMsg(strMethodName));
		String strAppEnvConfigFile = PropertyHandler.getExternalString(AppConstants.APP_CONFIG_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		if (StringUtils.isEmpty(strAppEnvConfigFile)
				|| StringUtils.equalsIgnoreCase(strAppEnvConfigFile, AppConstants.APP_CONFIG_KEY)) {
			return aAppEnvConfig;
		}
		try {
			File aControllerFile = AppUtils.getFileFromPath(strAppEnvConfigFile);
			if (!aControllerFile.exists()) {
				throw new IOException(MessageFormat.format(ErrorMsgConstants.FILENTFOUND, aControllerFile.getName()));
			}
			String strSheetName = AppConstants.APP_ENV_CONFIG_SHEET_NAME;
			try (XSSFWorkbook aWorkbook = ExcelUtils.getWorkBook(aControllerFile)) {
				FormulaEvaluator evaluator = aWorkbook.getCreationHelper().createFormulaEvaluator();
				Sheet aEnvConfig = aWorkbook.getSheet(strSheetName);
				if (aEnvConfig == null) {
					throw new IOException(MessageFormat.format(ErrorMsgConstants.SHEET_NOT_FOUND, strSheetName,
							aControllerFile.getPath()));
				}
				for (int iRow = 1; iRow <= aEnvConfig.getLastRowNum() - aEnvConfig.getFirstRowNum(); iRow++) {
					Row row = ExcelUtils.getRow(aEnvConfig, iRow);
					if (row == null) {
						throw new Exception(MessageFormat.format(ErrorMsgConstants.ERR_ROW, iRow + 1, strSheetName));
					}
					String strConfigAppName = ExcelUtils.getStringValue(evaluator, aEnvConfig, strSheetName, row, 0);
					strConfigAppName = StringUtils.trim(strConfigAppName);
					if (!StringUtils.equalsIgnoreCase(strAPPName, strConfigAppName)) {
						continue;
					}
					String strEnvDescription = ExcelUtils.getStringValue(evaluator, aEnvConfig, strSheetName, row, 1,
							false);
					String strPropertyFileName = ExcelUtils.getStringValue(evaluator, aEnvConfig, strSheetName, row, 2);
					if (StringUtils.endsWithIgnoreCase(strPropertyFileName, AppConstants.PROPERTIES_FILE_SUFFIX)) {
						strPropertyFileName = StringUtils.substringBeforeLast(strPropertyFileName, ".");
					}
					String strSpocName = ExcelUtils.getStringValue(evaluator, aEnvConfig, strSheetName, row, 3, false);
					String strSpocEmail = ExcelUtils.getStringValue(evaluator, aEnvConfig, strSheetName, row, 4, false);
					String strTelegramGroupId = ExcelUtils.getStringValue(evaluator, aEnvConfig, strSheetName, row, 5,
							false);
					String strMSTeamsGroupURL = ExcelUtils.getStringValue(evaluator, aEnvConfig, strSheetName, row, 6,
							false);
					aAppEnvConfig = new AppEnvConfigBean();
					aAppEnvConfig.setAppName(strConfigAppName);
					aAppEnvConfig.setAppDescription(strEnvDescription);
					aAppEnvConfig.setPropertyName(strPropertyFileName);
					aAppEnvConfig.setSpocName(strSpocName);
					aAppEnvConfig.setSpocEmail(StringUtils.isEmpty(strSpocEmail)
							? EmailUtils.getEmailConfigByKey(AppConstants.EMAIL_TO_KEY)
							: strSpocEmail);
					aAppEnvConfig.setTelegramGroupId(strTelegramGroupId);
					aAppEnvConfig.setMSTeamsGroupURL(strMSTeamsGroupURL);
					ALMConfigBean aAlmConfig = getALMConfigBean(strAPPName);
					aAppEnvConfig.setAlmConfig(aAlmConfig);
					return aAppEnvConfig;
				}
			}
		} catch (Exception ex) {
			String strErrorMsg = "Error while Fetching App Env Config";
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strMethodName));
		}
		return aAppEnvConfig;
	}

	public void clearAppEnvConfigBean() {
		aAppEnvConfig = null;
	}

	private ALMConfigBean getALMConfigBean(String strAPPName) {
		String strMethodName = "loadALMConfig";
		LOGGER.info(StartFinish.START.getFormattedMsg(strMethodName));

		try {
			String strALMCongfile = AppConstants.ALM_CONFIG_PROPERTIES_NAME;
			String strALMURL = PropertyHandler.getExternalString(AppConstants.ALM_CONFIG_URL_KEY, strALMCongfile);
			String strUserName = PropertyHandler.getExternalString(AppConstants.ALM_CONFIG_USERNAME_KEY,
					strALMCongfile);
			String strPassword = PropertyHandler.getExternalString(AppConstants.ALM_CONFIG_PASSWORD_KEY,
					strALMCongfile);
			String strALMClientId = PropertyHandler.getExternalString(AppConstants.ALM_CONFIG_CLIENTID_KEY,
					strALMCongfile);
			String strALMClientSecret = PropertyHandler.getExternalString(AppConstants.ALM_CONFIG_SECRET_KEY,
					strALMCongfile);
			String strDomain = PropertyHandler.getExternalString(AppConstants.ALM_CONFIG_DOMAIN_KEY, strALMCongfile);
			String strProject = PropertyHandler.getExternalString(AppConstants.ALM_CONFIG_PROJECT_KEY, strALMCongfile);
			String strTestCasePrefix = PropertyHandler.getExternalString(AppConstants.ALM_CONFIG_TESTCASE_PREFIX_KEY,
					strALMCongfile);
			String strRunName = PropertyHandler.getExternalString(AppConstants.ALM_CONFIG_RUNNAME_KEY, strALMCongfile);

			if (StringUtils.isEmpty(StringUtils.trim(strALMURL)) || StringUtils.isEmpty(StringUtils.trim(strUserName))
					|| StringUtils.isEmpty(StringUtils.trim(strPassword))
					|| StringUtils.isEmpty(StringUtils.trim(strALMClientId))
					|| StringUtils.isEmpty(StringUtils.trim(strALMClientSecret))
					|| StringUtils.isEmpty(StringUtils.trim(strDomain))
					|| StringUtils.isEmpty(StringUtils.trim(strProject))
					|| StringUtils.isEmpty(StringUtils.trim(strTestCasePrefix))
					|| StringUtils.isEmpty(StringUtils.trim(strRunName))) {
				return null;
			}

			ALMConfigBean aAlmConfig = new ALMConfigBean();
			aAlmConfig.setAppName(strAPPName);
			aAlmConfig.setALMURL(StringUtils.trim(strALMURL));
			aAlmConfig.setUserName(StringUtils.trim(strUserName));
			aAlmConfig.setPassword(StringUtils.trim(strPassword));
			aAlmConfig.setALMClientId(StringUtils.trim(strALMClientId));
			aAlmConfig.setALMClientSecret(StringUtils.trim(strALMClientSecret));
			aAlmConfig.setDomain(StringUtils.trim(strDomain));
			aAlmConfig.setProject(StringUtils.trim(strProject));
			aAlmConfig.setTestCasePrefix(StringUtils.trim(strTestCasePrefix));
			aAlmConfig.setRunName(StringUtils.trim(AppUtils.formatMessage(strRunName, getAppRunID())));
			return aAlmConfig;
		} catch (Exception ex) {
			String strErrorMsg = "Error while Fetching ALM Config";
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strMethodName));
		}
		return null;
	}

	public LinkedHashMap<String, String> getAppEnvConfigDeatils() throws Exception {

		String strAppENV = getAppENV();

		String strAppEnvConfigFile = PropertyHandler.getExternalString(AppConstants.APP_ENV_CONFIG_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		if (StringUtils.isEmpty(strAppEnvConfigFile)
				|| StringUtils.equalsIgnoreCase(strAppEnvConfigFile, AppConstants.APP_ENV_CONFIG_KEY)) {
			return mpEnvDeatils;
		}

		if (StringUtils.isNotEmpty(StringUtils.trim(strAppENV)) && MapUtils.isEmpty(mpEnvDeatils)) {
			File aEnvConfigFile = AppUtils.getFileFromPath(strAppEnvConfigFile);
			if (!aEnvConfigFile.exists()) {
				throw new IOException(MessageFormat.format(ErrorMsgConstants.FILENTFOUND, strAppEnvConfigFile));
			}
			try (java.io.Reader aAppEnvConfigReader = Files.newBufferedReader(aEnvConfigFile.toPath());) {
				Gson aGson = AppUtils.getDefaultGson();
				Type envConfigDeatilsType = new TypeToken<LinkedHashMap<String, LinkedHashMap<String, String>>>() {
				}.getType();
				LinkedHashMap<String, LinkedHashMap<String, String>> mpConfigDeatils = aGson
						.fromJson(aAppEnvConfigReader, envConfigDeatilsType);
				mpEnvDeatils = MapUtils.getObject(mpConfigDeatils, strAppENV);
				if (mpEnvDeatils == null) {
					throw new Exception(MessageFormat.format(ErrorMsgConstants.ERR_ENV_DEATILS_NTFOUND, strAppENV,
							strAppEnvConfigFile));
				}

			}
		}

		return mpEnvDeatils;
	}
}
