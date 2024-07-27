/****************************************************************************
 * File Name 		: ConsolidateTestReport.java
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.abc.project.beans.AppEnvConfigBean;
import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.beans.TestSuiteBean;
import com.abc.project.config.AppConfig;
import com.abc.project.config.AppContext;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.SummaryReportConstants;
import com.abc.project.enums.AppRunMode;
import com.abc.project.enums.Browsers;
import com.abc.project.grid.RemoteMultiPlatformServerHelper;
import com.abc.project.utils.AppUtils;
import com.abc.project.utils.ExcelUtils;
import com.abc.project.utils.PropertyHandler;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 12:52:58 pm
 */
public class ConsolidateTestReport {

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	/***
	 * Generates the reports file name
	 * 
	 * @param aBrowser
	 * @return
	 */
	private static String getReportFileName(Browsers aBrowser) {
		java.sql.Date dtExecution = AppConfig.getInstance().getExecutionDate();
		String strReportDate = AppUtils.getDateAsString(dtExecution, AppConstants.EXEC_REPORTS_FOLDER_DT_FORMAT);
		String strReportFileName = MessageFormat.format(
				PropertyHandler.getExternalString(AppConstants.REF_FILE_NAME_KEY, AppConstants.APP_PROPERTIES_NAME),
				strReportDate);
		String strBaseName = FilenameUtils.getBaseName(strReportFileName);
		String strExtension = FilenameUtils.getExtension(strReportFileName);
		strReportFileName = String.format(AppConstants.REPORT_FILE_NAME_FORMAT,
				AppUtils.removeIllegalCharacters(strBaseName, true), strExtension);
		return String.format("%s_%s", AppUtils.getBrowserExecutionFileName(aBrowser.getBrowserName()),
				strReportFileName);
	}

	/***
	 * Fetches the report File
	 * 
	 * @param aBrowser
	 * @return
	 */
	private static File getReportsFile(BrowsersConfigBean aBrowsersConfigBean) {
		String strReportFolder = AppConfig.getInstance().getBrowserExecutionReportFolder(aBrowsersConfigBean);
		return Paths.get(strReportFolder, getReportFileName(aBrowsersConfigBean.getBrowser())).toFile();
	}

	/**
	 * Creates the execution report
	 * 
	 * @param strScenarioName
	 * @param strExecResult
	 */
	public static synchronized void createExecutionReport(TestSuiteBean aTestSuiteBean, AppEnvConfigBean aPPRunEnv,
			BrowsersConfigBean aBrowsersConfigBean, String strExecResult) {
		AppContext appConText = AppContext.getInstance();
		String strScenarioName = aTestSuiteBean.getScenarioName();
		appConText.addExecSurmmaryReport(strScenarioName, aBrowsersConfigBean, SummaryReportConstants.SECNARIO_HEADER,
				strScenarioName);
		appConText.addExecSurmmaryReport(strScenarioName, aBrowsersConfigBean, SummaryReportConstants.BROWSER_HEADER,
				aBrowsersConfigBean.getBrowserDisplayName());
		appConText.addExecSurmmaryReport(strScenarioName, aBrowsersConfigBean, SummaryReportConstants.MACHINE_HEADER,
				aTestSuiteBean.getHostAddress());
		appConText.addExecSurmmaryReport(strScenarioName, aBrowsersConfigBean,
				SummaryReportConstants.EXECUTION_STATUS_HEADER, strExecResult);
		String strBrowserName = aBrowsersConfigBean.getReportBrowserName();
		appConText.addStepResults(strBrowserName, strExecResult);
		try {
			AppRunMode aAppRunMode = aPPRunEnv.getAppRunMode();
			if (AppRunMode.MUTLI_GRID_PLATFORM == aAppRunMode) {
				RemoteMultiPlatformServerHelper aRemoteMultiPlatformServerHelper = RemoteMultiPlatformServerHelper
						.getInstance();
				String[] strReportingSheets = { SummaryReportConstants.REF_NUM_SHEETNAME,
						SummaryReportConstants.QUOTE_SUMMARY_SHEETNAME };
				for (String strSheet : strReportingSheets) {
					switch (strSheet) {
					case SummaryReportConstants.REF_NUM_SHEETNAME:
						aRemoteMultiPlatformServerHelper.updateReferenceNumber(aTestSuiteBean,
								getSummaryReportData(strSheet, strScenarioName, aBrowsersConfigBean));
						break;
					case SummaryReportConstants.QUOTE_SUMMARY_SHEETNAME:
						aRemoteMultiPlatformServerHelper.updateQuatationSummary(aTestSuiteBean,
								getSummaryReportData(strSheet, strScenarioName, aBrowsersConfigBean));
						break;
					}
				}

			}
		} catch (Exception ex) {
			ERROR_LOGGER.error("Error While Creating Execution Report", ex);
		}
		createSummaryReport(aBrowsersConfigBean, strScenarioName);
	}

	private static XSSFWorkbook getWorkBook(File aFile) throws IOException {
		if (aFile.exists()) {
			return ExcelUtils.getWorkBook(aFile);
		}
		return new XSSFWorkbook();
	}

	/**
	 * Creates the summary report
	 * 
	 * @param aBrowser
	 */
	private static synchronized void createSummaryReport(BrowsersConfigBean aBrowsersConfigBean,
			String strScenarioName) {
		try {
			File aReportFile = getReportsFile(aBrowsersConfigBean);
			try (XSSFWorkbook aWorkbook = getWorkBook(aReportFile)) {
				for (String strSheet : SummaryReportConstants.SUMMARY_REPORT_SHEETS) {
					XSSFCellStyle aHeaderCellStyle = ExcelUtils.getReportHeaderDataCellStyle(aWorkbook);
					XSSFCellStyle aCellStyle = ExcelUtils.getReportDataCellStyle(aWorkbook);
					switch (strSheet) {
					case SummaryReportConstants.SUMMARY_SHEETNAME:
						createExecutionReport(aWorkbook, strSheet, aHeaderCellStyle, aCellStyle, aBrowsersConfigBean,
								strScenarioName);
						break;
					case SummaryReportConstants.REINSURANCE_DETAILS_SHEETNAME:
					case SummaryReportConstants.REINSURANCE_SHEETNAME:
					case SummaryReportConstants.APPILCATION_RESULTS_SHEET_NAME:
						createOtherSummaryReport(aWorkbook, strSheet, aHeaderCellStyle, aCellStyle, strScenarioName,
								aBrowsersConfigBean);
						break;
					default:
						createDefaultSummaryReport(aWorkbook, strSheet, aHeaderCellStyle, aCellStyle, strScenarioName,
								aBrowsersConfigBean);
						break;
					}
				}
				ExcelUtils.writeWrokBook(aWorkbook, aReportFile);
			}
			AppContext aAppContext = AppContext.getInstance();
			aAppContext.addExecutionReports(aReportFile);
		} catch (Exception ex) {
			ERROR_LOGGER.error("Error While Creating Execution Report", ex);
		}
	}

	/****
	 * Fetches the execution Summary Report
	 * 
	 * @param strSheetName
	 * @param strScenarioName
	 * @return
	 */
	public static LinkedHashMap<String, String> getSummaryReportData(String strSheetName, String strScenarioName,
			BrowsersConfigBean aBrowsersConfigBean) {
		AppContext appConText = AppContext.getInstance();
		switch (strSheetName) {
		case SummaryReportConstants.SUMMARY_SHEETNAME:
			return appConText.getExecSurmmaryReport(strScenarioName, aBrowsersConfigBean);
		case SummaryReportConstants.REF_NUM_SHEETNAME:
			return appConText.getExecRefSurmmaryReport(strScenarioName, aBrowsersConfigBean);
		case SummaryReportConstants.QUOTE_SUMMARY_SHEETNAME:
			return appConText.getExecQuotationSurmmaryReport(strScenarioName, aBrowsersConfigBean);
		default:
			return new LinkedHashMap<>();
		}
	}

	/***
	 * Creates the default summary report
	 * 
	 * @param aWorkbook
	 * @param strSheetName
	 * @param aHeaderCellStyle
	 * @param aCellStyle
	 * @param strScenarioName
	 */
	private static void createDefaultSummaryReport(XSSFWorkbook aWorkbook, String strSheetName,
			XSSFCellStyle aHeaderCellStyle, XSSFCellStyle aCellStyle, String strScenarioName,
			BrowsersConfigBean aBrowsersConfigBean) {

		LinkedHashMap<String, String> fieldValue = getSummaryReportData(strSheetName, strScenarioName,
				aBrowsersConfigBean);
		if (fieldValue == null || fieldValue.isEmpty()) {
			return;
		}
		XSSFSheet aSheet = ExcelUtils.getSheet(aWorkbook, strSheetName);
		int iRow = 0;
		Row aHeaderRow = ExcelUtils.getRow(aSheet, iRow);
		if (aHeaderRow == null) {
			aHeaderRow = aSheet.createRow(iRow);
		}

		int iDataRowNum = aSheet.getLastRowNum() + 1;
		Row aDataRow = ExcelUtils.getRow(aSheet, iDataRowNum);
		if (aDataRow == null) {
			aDataRow = aSheet.createRow(iDataRowNum);
		}

		Set<Entry<String, String>> stEntry = fieldValue.entrySet();
		for (Entry<String, String> aEntry : stEntry) {
			String strHeader = StringUtils.trim(aEntry.getKey());
			String strKey = aEntry.getValue();
			int iCell = ExcelUtils.getValidDataCellNo(aHeaderRow, strHeader);
			if (iCell < 0) {
				iCell = aHeaderRow.getLastCellNum() <= 0 ? 0 : aHeaderRow.getLastCellNum();
				ExcelUtils.setCellValue(aSheet, aHeaderRow, iCell, aHeaderCellStyle, strHeader);
			}
			if (!StringUtils.isEmpty(strKey)) {
				ExcelUtils.setCellValue(aSheet, aDataRow, iCell, aCellStyle, strKey);
			}
			aSheet.autoSizeColumn(iCell, true);
		}
	}

	/**
	 * Creates the Summary Execution Report
	 * 
	 * @param aWorkbook
	 * @param aSheet
	 * @param aHeaderCellStyle
	 * @param aCellStyle
	 * @param aBrowser
	 * @param strScenarioName
	 */
	private static void createExecutionReport(XSSFWorkbook aWorkbook, String strSheetName,
			XSSFCellStyle aHeaderCellStyle, XSSFCellStyle aCellStyle, BrowsersConfigBean aBrowsersConfigBean,
			String strScenarioName) {
		try {
			AppContext appConText = AppContext.getInstance();
			String strBrowserName = aBrowsersConfigBean.getReportBrowserName();
			Map<String, Long> resultsBean = appConText.getStepResults(strBrowserName);
			if (resultsBean == null) {
				return;
			}
			createDefaultSummaryReport(aWorkbook, strSheetName, aHeaderCellStyle, aCellStyle, strScenarioName,
					aBrowsersConfigBean);
			XSSFSheet aSheet = ExcelUtils.getSheet(aWorkbook, strSheetName);
			// Create Consolidated Summary
			int iRow = 0;
			Row headerRow = ExcelUtils.getRow(aSheet, iRow);
			if (headerRow == null) {
				headerRow = aSheet.createRow(iRow);
			}
			iRow = iRow + 1;
			int iCell = 7;
			Row valueRow = ExcelUtils.getRow(aSheet, iRow);
			if (valueRow == null) {
				valueRow = aSheet.createRow(iRow);
			}
			for (String strKey : resultsBean.keySet()) {
				ExcelUtils.setCellValue(aSheet, headerRow, iCell, aHeaderCellStyle, StringUtils.trim(strKey));
				long lFiledValue = resultsBean.get(strKey);
				ExcelUtils.setCellValue(aSheet, valueRow, iCell, aCellStyle,
						lFiledValue <= 0 ? "0" : String.valueOf(lFiledValue));
				aSheet.autoSizeColumn(iCell, true);
				iCell = iCell + 1;
			}

		} catch (Exception ex) {
			ERROR_LOGGER.error("Error While Creating Execution Report", ex);
		}
	}

	private static void createOtherSummaryReport(XSSFWorkbook aWorkbook, String strSheetName,
			XSSFCellStyle aHeaderCellStyle, XSSFCellStyle aCellStyle, String strScenarioName,
			BrowsersConfigBean aBrowsersConfigBean) {
		AppContext appConText = AppContext.getInstance();
		LinkedHashMap<String, LinkedHashMap<String, LinkedList<String>>> mpOtherScenarioReport = appConText
				.getOtherSummaryReport(strScenarioName, aBrowsersConfigBean);
		if (mpOtherScenarioReport == null || mpOtherScenarioReport.isEmpty()) {
			return;
		}
		LinkedHashMap<String, LinkedList<String>> mpSummaryReport = mpOtherScenarioReport.get(strSheetName);
		if (mpSummaryReport == null || mpSummaryReport.isEmpty()) {
			return;
		}

		XSSFSheet aSheet = ExcelUtils.getSheet(aWorkbook, strSheetName);
		Set<Entry<String, LinkedList<String>>> stEntry = mpSummaryReport.entrySet();
		int iHeaderSize = 0;
		for (Entry<String, LinkedList<String>> aEntry : stEntry) {
			String strKey = aEntry.getKey();
			LinkedList<String> lstData = aEntry.getValue();
			int iRow;
			XSSFCellStyle aDataCellStyle;
			switch (strKey) {
			case SummaryReportConstants.TABLE_HEADER:
				iRow = 0;
				aDataCellStyle = aHeaderCellStyle;
				break;
			default:
				iRow = aSheet.getLastRowNum() + 1;
				aDataCellStyle = aCellStyle;
				break;
			}
			Row aRow = ExcelUtils.getRow(aSheet, iRow);
			if (aRow == null) {
				aRow = aSheet.createRow(iRow);
			}
			int iDataCell = 0;
			for (int i = 0; i < lstData.size(); i++) {
				String strCellValue = lstData.get(i);
				int iCell = i;
				if (StringUtils.equalsIgnoreCase(strKey, SummaryReportConstants.TABLE_HEADER)) 
				{
					int iHeaderVal = ExcelUtils.getValidDataCellNo(aRow, strCellValue);
					iCell = iHeaderVal == -1 ? iDataCell : iHeaderVal;
					ExcelUtils.setCellValue(aSheet, aRow, iCell, aDataCellStyle, strCellValue);
					iDataCell = iCell + 1;
					iHeaderSize = iCell;
				} else if (!StringUtils.equalsIgnoreCase(strKey, SummaryReportConstants.TABLE_HEADER)
						&& iHeaderSize > 0) {
					if (iDataCell > iHeaderSize) {
						iDataCell = 0;
						iRow = aSheet.getLastRowNum() + 1;
						aRow = ExcelUtils.getRow(aSheet, iRow);
						if (aRow == null) {
							aRow = aSheet.createRow(iRow);
						}
					}
					ExcelUtils.setCellValue(aSheet, aRow, iDataCell, aDataCellStyle, strCellValue);
					iDataCell = iDataCell + 1;
				} else {
					iCell = i;
					ExcelUtils.setCellValue(aSheet, aRow, iCell, aDataCellStyle, strCellValue);
				}
			}
		}
	}
}
