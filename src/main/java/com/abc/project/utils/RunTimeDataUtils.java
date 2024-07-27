/****************************************************************************
 * File Name 		: RunTimeDataUtils.java
 * Package			: com.dxc.zurich.reports
 * Author			: pmusunuru2
 * Creation Date	: Feb 24, 2021
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.abc.project.alm.beans.ALMWrapperConfigBean;
import com.abc.project.beans.AppEnvConfigBean;
import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.beans.TestSuiteBean;
import com.abc.project.config.AppConfig;
import com.abc.project.config.AppContext;
import com.abc.project.config.MasterConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.ErrorMsgConstants;
import com.abc.project.constants.RunTimeDataConstants;
import com.abc.project.constants.SummaryReportConstants;
import com.abc.project.enums.AppRunMode;
import com.abc.project.enums.StartFinish;
import com.abc.project.grid.RemoteMultiPlatformServerHelper;

/**
 * @author pmusunuru2
 * @since Feb 24, 2021 11:39:46 am
 */
public class RunTimeDataUtils {

	private static final Logger LOGGER = LogManager.getLogger(RunTimeDataUtils.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	private static File getRuntimeDataFile() {
		String strAPPENVName = MasterConfig.getInstance().getAppName();
		String strFileName = String.format(RunTimeDataConstants.FILE_NAME, strAPPENVName);
		File aRuntimeFile = Paths
				.get(AppConfig.getInstance().getExecutionReportFolder(), RunTimeDataConstants.FOLDER_NAME, strFileName)
				.toFile();
		if (!aRuntimeFile.getParentFile().exists()) {
			aRuntimeFile.getParentFile().mkdirs();
		}
		return aRuntimeFile;
	}

	public synchronized static void editRuntimeValues(TestSuiteBean aTestSuiteBean, BrowsersConfigBean aBrowsersConfigBean) {
		AppContext aPPContext = AppContext.getInstance();
		String testScenarioName = aTestSuiteBean.getScenarioName();
		String strAppContextKey = aPPContext.getAppContextKey(testScenarioName, aBrowsersConfigBean);
		LinkedHashMap<String, String> mpScenarioData = aPPContext.getRunTimeDataBean(strAppContextKey);
		if (mpScenarioData == null) {
			return;
		}
		File aRuntimeFile = getRuntimeDataFile();
		String strLogMessage = AppUtils.formatMessage("Writing runtime data for scenario {0} to file {1}",
				testScenarioName, aRuntimeFile.getPath());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));

			try (XSSFWorkbook aWorkbook = getWorkBook(aRuntimeFile)) {
				FormulaEvaluator evaluator = aWorkbook.getCreationHelper().createFormulaEvaluator();
				XSSFSheet aSheet = ExcelUtils.getSheet(aWorkbook, RunTimeDataConstants.SHEET_NAME);
				XSSFCellStyle aHeaderCellStyle = ExcelUtils.getReportHeaderDataCellStyle(aWorkbook);
				XSSFCellStyle aCellStyle = ExcelUtils.getReportDataCellStyle(aWorkbook);
				int iRow = 0;
				Row aHeaderRow = ExcelUtils.getRow(aSheet, iRow);
				if (aHeaderRow == null) {
					aHeaderRow = aSheet.createRow(iRow);
				}
				Row aDataRow = ExcelUtils.getValidScenarioRow(evaluator, aSheet, testScenarioName, aBrowsersConfigBean);
				if (aDataRow == null) {
					aDataRow = aSheet.createRow(aSheet.getLastRowNum() + 1);
				}
				Set<Entry<String, String>> stEntry = mpScenarioData.entrySet();
				for (Entry<String, String> aEntry : stEntry) {
					String strHeader = StringUtils.trim(aEntry.getKey());
					String strKey = aEntry.getValue();
					int iCell = ExcelUtils.getValidDataCellNo(aHeaderRow, strHeader);
					if (iCell < 0) {
						iCell = aHeaderRow.getLastCellNum() <= 0 ? 0 : aHeaderRow.getLastCellNum();
						ExcelUtils.setCellValue(aSheet, aHeaderRow, iCell, aHeaderCellStyle, strHeader);
					}
					ExcelUtils.setCellValue(aSheet, aDataRow, iCell, aCellStyle, strKey);
				}
				ExcelUtils.writeWrokBook(aWorkbook, aRuntimeFile);
			}
			AppEnvConfigBean aPPRunEnv = MasterConfig.getInstance().getAppEnvConfigBean();
			AppRunMode aAppRunMode = aPPRunEnv.getAppRunMode();
			if (AppRunMode.MUTLI_GRID_PLATFORM == aAppRunMode) {
				RemoteMultiPlatformServerHelper.getInstance().updateRuntimeData(aTestSuiteBean, mpScenarioData);
			}
		} catch (Exception ex) {
			String strLogErr = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strLogErr);
			ERROR_LOGGER.error(strLogErr, ex);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	private static XSSFWorkbook getWorkBook(File aFile) throws IOException {
		if (aFile.exists()) {
			return ExcelUtils.getWorkBook(aFile);
		}
		return new XSSFWorkbook();
	}

	public synchronized static LinkedHashMap<String, LinkedHashMap<String, String>> getRunTimeValues() {
		LinkedHashMap<String, LinkedHashMap<String, String>> runTimeDataMap = new LinkedHashMap<>();
		File aRuntimeFile = getRuntimeDataFile();
		String strLogMessage = AppUtils.formatMessage("Fetching runtime data from file {0}", aRuntimeFile.getPath());
		if (!aRuntimeFile.exists()) {
			return runTimeDataMap;
		}
		AppEnvConfigBean aPPRunEnv = MasterConfig.getInstance().getAppEnvConfigBean();
		AppRunMode aAppRunMode = aPPRunEnv.getAppRunMode();
		if (AppRunMode.MUTLI_GRID_PLATFORM == aAppRunMode) {
			return RemoteMultiPlatformServerHelper.getInstance().getRunTimeValues(ERROR_LOGGER);
		}
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));

		try (XSSFWorkbook aWorkbook = ExcelUtils.getWorkBook(aRuntimeFile)) {
			String strSheetName = RunTimeDataConstants.SHEET_NAME;
			FormulaEvaluator evaluator = aWorkbook.getCreationHelper().createFormulaEvaluator();
			XSSFSheet aSheet = aWorkbook.getSheet(strSheetName);
			if (aSheet == null) {
				return runTimeDataMap;
			}
			Row aHeaderRow = ExcelUtils.getRow(aSheet, 0);
			for (int i = 1; i <= aSheet.getLastRowNum() - aSheet.getFirstRowNum(); i++) {
				LinkedHashMap<String, String> aDataBean = new LinkedHashMap<>();
				Row aDataRow = ExcelUtils.getRow(aSheet, i);
				if (aDataRow == null) {
					continue;
				}
				int lastCellNumber = aDataRow.getLastCellNum();
				for (int cellNum = 0; cellNum < lastCellNumber; cellNum++) {
					Cell keyCell = ExcelUtils.getCell(aHeaderRow, cellNum);
					if (keyCell == null) {
						continue;
					}
					String key = ExcelUtils.getStringValue(keyCell);

					Cell valueCell = ExcelUtils.getCell(aDataRow, cellNum);
					String value = null;
					if (valueCell != null) {
						value = ExcelUtils.getStringValue(evaluator, aSheet, valueCell);
					}
					if (!StringUtils.isEmpty(value)) {
						aDataBean.put(key, value);
					}

				}
				String strScenarioName = aDataBean.get(RunTimeDataConstants.SECNARIO_HEADER);
				String strBrowserName = aDataBean.get(RunTimeDataConstants.BROWSER_DISPLAY_HEADER);
				String strKey = String.format("%s_%s", strScenarioName, strBrowserName);
				if (runTimeDataMap.get(strKey) != null) {
					LinkedHashMap<String, String> oldbean = runTimeDataMap.get(strKey);
					aDataBean.putAll(oldbean);
					runTimeDataMap.put(strKey, aDataBean);
				} else {
					runTimeDataMap.put(strKey, aDataBean);
				}
			}
		} catch (Exception ex) {
			String strLogErr = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strLogErr);
			ERROR_LOGGER.error(strLogErr, ex);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
		return runTimeDataMap;
	}

	public static void createRunTimeDataBackUp() throws IOException {
		File aRuntimeFile = getRuntimeDataFile();
		if (!aRuntimeFile.exists()) {
			return;
		}
		String strFileName = aRuntimeFile.getName();
		Path aTrgtPath = Paths.get(AppConfig.getInstance().getExecutionReportFolder(), "Runtime_Backup", strFileName);
		File aTrgtFile = aTrgtPath.toFile();
		if (!aTrgtFile.getParentFile().exists()) {
			aTrgtFile.getParentFile().mkdirs();
		}
		String strFileNameWithOutExt = FilenameUtils.getBaseName(strFileName);
		if (aTrgtFile.exists()) {
			for (int i = 1; i < Integer.MAX_VALUE; i++) {
				String strReNameFile = String.format("%s_Copy(%s).xlsx", strFileNameWithOutExt, i);
				Path aRenameTrgtPath = Paths.get(AppConfig.getInstance().getExecutionReportFolder(), "Runtime_Backup",
						strReNameFile);
				File aRenameTrgtFile = aRenameTrgtPath.toFile();
				if (aRenameTrgtFile.exists()) {
					continue;
				}
				if (aTrgtFile.renameTo(aRenameTrgtFile)) {
					break;
				}
			}
		}
		try {
			Files.copy(aRuntimeFile.toPath(), aTrgtPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new IOException(AppUtils.formatMessage(ErrorMsgConstants.FILENTFOUND, aRuntimeFile.getPath()));
		}
	}

	public synchronized static void editALMRuntimeValues(TestSuiteBean aTestSuiteBean, BrowsersConfigBean aBrowsersConfigBean) {
		ALMWrapperConfigBean almWrapperConfigBean = aTestSuiteBean == null ? null
				: aTestSuiteBean.getALMWrapperConfigBean();
		if (almWrapperConfigBean == null) {
			return;
		}
		File aRuntimeFile = getRuntimeDataFile();
		String strLogMessage = AppUtils.formatMessage("Writing ALM runtime data for scenario {0} to file {1}",
				aTestSuiteBean.getScenarioName(), aRuntimeFile.getPath());
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));

			try (XSSFWorkbook aWorkbook = getWorkBook(aRuntimeFile)) {
				FormulaEvaluator evaluator = aWorkbook.getCreationHelper().createFormulaEvaluator();
				XSSFSheet aSheet = ExcelUtils.getSheet(aWorkbook, RunTimeDataConstants.AML_CONFIG_SHEET_NAME);
				XSSFCellStyle aHeaderCellStyle = ExcelUtils.getReportHeaderDataCellStyle(aWorkbook);
				XSSFCellStyle aCellStyle = ExcelUtils.getReportDataCellStyle(aWorkbook);
				String testScenarioName = almWrapperConfigBean.getOriginalScenarioName();
				int iRow = 0;
				Row aHeaderRow = ExcelUtils.getRow(aSheet, iRow);
				if (aHeaderRow == null) {
					aHeaderRow = aSheet.createRow(iRow);
				}
				Row aDataRow = ExcelUtils.getValidScenarioRow(evaluator, aSheet, testScenarioName, aBrowsersConfigBean);
				if (aDataRow == null) {
					aDataRow = aSheet.createRow(aSheet.getLastRowNum() + 1);
				}
				String strHeaders[] = SummaryReportConstants.AML_CONFIG_HEADER;
				for (String strHeader : strHeaders) {
					int iCell = ExcelUtils.getValidDataCellNo(aHeaderRow, strHeader);
					if (iCell < 0) {
						iCell = aHeaderRow.getLastCellNum() <= 0 ? 0 : aHeaderRow.getLastCellNum();
						ExcelUtils.setCellValue(aSheet, aHeaderRow, iCell, aHeaderCellStyle, strHeader);
					}
				}
				String strUrl = almWrapperConfigBean.getALMURL();
				String strLoginID = almWrapperConfigBean.getALMUserName();
				String strALMClientId = almWrapperConfigBean.getALMClientId();
				String strALMClientSecret = almWrapperConfigBean.getALMClientSecret();
				String strDomain = almWrapperConfigBean.getALMDomain();
				String strProject = almWrapperConfigBean.getALMProject();
				String strTestSetID = almWrapperConfigBean.getALMTestSetID();
				String strTestSetPath = almWrapperConfigBean.getALMTestSetPath();
				String strTSName = almWrapperConfigBean.getALMTestSetName();
				String strTCPrefix = almWrapperConfigBean.getALMTestCasePrefix();
				String strRunName = almWrapperConfigBean.getALMRunName();
				String strStatus = almWrapperConfigBean.getALMExecutionStatus();
				List<File> lstALMAttachments = almWrapperConfigBean.getALMAttachments();
				StringBuilder strALMAttachments = new StringBuilder();
				for (File aALMAttachment : lstALMAttachments) {
					if (aALMAttachment.exists()) {
						strALMAttachments.append(aALMAttachment.getAbsolutePath())
								.append(AppConstants.SEPARATOR_SEMICOLON);
					}
				}
				String strALMReportAttachments = strALMAttachments.toString();
				if (StringUtils.endsWithIgnoreCase(strALMReportAttachments, AppConstants.SEPARATOR_SEMICOLON)) {
					strALMReportAttachments = StringUtils.substring(strALMReportAttachments, 0,
							strALMReportAttachments.length() - AppConstants.SEPARATOR_SEMICOLON.length());
				}
				ExcelUtils.setCellValue(aSheet, aDataRow, 0, aCellStyle, testScenarioName);
				ExcelUtils.setCellValue(aSheet, aDataRow, 1, aCellStyle, aBrowsersConfigBean.getBrowserDisplayName());
				ExcelUtils.setCellValue(aSheet, aDataRow, 2, aCellStyle, StringUtils.trim(strUrl));
				ExcelUtils.setCellValue(aSheet, aDataRow, 3, aCellStyle, StringUtils.trim(strLoginID));
				ExcelUtils.setCellValue(aSheet, aDataRow, 4, aCellStyle, StringUtils.trim(strALMClientId));
				ExcelUtils.setCellValue(aSheet, aDataRow, 5, aCellStyle, StringUtils.trim(strALMClientSecret));
				ExcelUtils.setCellValue(aSheet, aDataRow, 6, aCellStyle, StringUtils.trim(strDomain));
				ExcelUtils.setCellValue(aSheet, aDataRow, 7, aCellStyle, StringUtils.trim(strProject));
				ExcelUtils.setCellValue(aSheet, aDataRow, 8, aCellStyle, StringUtils.trim(strTestSetID));
				ExcelUtils.setCellValue(aSheet, aDataRow, 9, aCellStyle, StringUtils.trim(strTestSetPath));
				ExcelUtils.setCellValue(aSheet, aDataRow, 10, aCellStyle, StringUtils.trim(strTSName));
				ExcelUtils.setCellValue(aSheet, aDataRow, 11, aCellStyle, StringUtils.trim(strTCPrefix));
				ExcelUtils.setCellValue(aSheet, aDataRow, 12, aCellStyle, StringUtils.trim(strRunName));
				ExcelUtils.setCellValue(aSheet, aDataRow, 13, aCellStyle, StringUtils.trim(strStatus));
				ExcelUtils.setCellValue(aSheet, aDataRow, 14, aCellStyle, StringUtils.trim(strALMReportAttachments));
				ExcelUtils.writeWrokBook(aWorkbook, aRuntimeFile);
			}

		} catch (Exception ex) {
			String strLogErr = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strLogErr);
			ERROR_LOGGER.error(strLogErr, ex);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}
}
