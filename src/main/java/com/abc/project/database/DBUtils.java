/****************************************************************************
 * File Name 		: DBUtils.java
 * Package			: com.dxc.zurich.database
 * Author			: pmusunuru2
 * Creation Date	: May 04, 2021
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.database;

import java.io.File;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.beans.KeyWordConfigBean;
import com.abc.project.config.AppConfig;
import com.abc.project.config.MasterConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.DataBaseConstants;
import com.abc.project.enums.IdentificationType;
import com.abc.project.enums.StartFinish;
import com.abc.project.reports.TestStepReport;
import com.abc.project.utils.AppUtils;
import com.abc.project.utils.ExcelUtils;

/**
 * @author pmusunuru2
 * @since May 04, 2021 4:09:10 pm
 */
public class DBUtils {

	private static final Logger LOGGER = LogManager.getLogger(DBUtils.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	private static DataBaseValidation getDataBaseValidation(KeyWordConfigBean aKeyWord, String strTestData,
			String strORProperty, String strReportKeyWord) {
		IdentificationType aKeyWordType = aKeyWord.getKeyWorkType();
		switch (aKeyWordType) {
		case MANGODB:
			return new MongoDBValidation(strTestData, strORProperty, strReportKeyWord);
		case ORACLEDB:
			return new OracleDBValidation(strTestData, strORProperty, strReportKeyWord);
		default:
			return new BasicDBValidation(strTestData, strORProperty, strReportKeyWord);
		}
	}

	public static String validateDB(KeyWordConfigBean aKeyWord, String strTestData, String strORProperty,
			String strReportKeyWord) {
		String strLogMessage = AppUtils.formatMessage(
				"Validating DB {0} for keyword {1} with Test Data {2} and File {3}",
				aKeyWord.getKeyWorkType().getKeyWordType(), aKeyWord.getOriginalKeyWord(), strTestData, strORProperty);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			String strJsonTestData = AppUtils.getJsonData(strTestData);
			DataBaseValidation aDataBaseValidation = getDataBaseValidation(aKeyWord, strJsonTestData, strORProperty,
					strReportKeyWord);
			return aDataBaseValidation.executeQuery();
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	public static List<LinkedHashMap<String, Object>> fetchDBData(KeyWordConfigBean aKeyWord, String strTestData,
			String strORProperty, String strReportKeyWord) {
		String strLogMessage = AppUtils.formatMessage(
				"Validating DB {0} for keyword {1} with Test Data {2} and File {3}",
				aKeyWord.getKeyWorkType().getKeyWordType(), aKeyWord.getOriginalKeyWord(), strTestData, strORProperty);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			String strJsonTestData = AppUtils.getJsonData(strTestData);
			DataBaseValidation aDataBaseValidation = getDataBaseValidation(aKeyWord, strJsonTestData, strORProperty,
					strReportKeyWord);
			return aDataBaseValidation.fetchDBData();
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return new LinkedList<>();
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	public static synchronized String extractDBDataToExcel(String strTestCaseID, BrowsersConfigBean aBrowsersConfigBean,
			KeyWordConfigBean aKeyWord, String strTestData, String strORProperty, String strReportKeyWord) {
		String strLogMessage = AppUtils.formatMessage(
				"Extracting DB data to excel {0} for keyword {1} with Test Data {2} and File {3}",
				aKeyWord.getKeyWorkType().getKeyWordType(), aKeyWord.getOriginalKeyWord(), strTestData, strORProperty);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
			String strJsonTestData = AppUtils.getJsonData(strTestData);
			DataBaseValidation aDataBaseValidation = getDataBaseValidation(aKeyWord, strJsonTestData, strORProperty,
					strReportKeyWord);
			List<LinkedHashMap<String, Object>> lstRecords = aDataBaseValidation.fetchDBData();
			if (CollectionUtils.isEmpty(lstRecords)) {
				return AppConstants.TEST_RESULT_FAIL;
			}
			LinkedHashMap<String, Object> mpTestData = aDataBaseValidation.getDBTestData();
			String strSheetName = (String) mpTestData.get(DataBaseConstants.DATABASE_TABLENAME_KEY);
			String strFileName = String.format("%s_%s_%s.xlsx", MasterConfig.getInstance().getAppRunID(),
					AppUtils.getScenarioReportFileName(strTestCaseID, TestStepReport.SCENARIOS_FILE_NAME_LENGTH), AppUtils.getFileDate());
			String strReportFolder = AppConfig.getInstance().getBrowserExecutionReportFolder(aBrowsersConfigBean);
			File aDBReportFile = Paths.get(strReportFolder, strTestCaseID, strFileName).toFile();
			try (XSSFWorkbook aWorkbook = ExcelUtils.getWorkBook(aDBReportFile)) {
				XSSFSheet aSheet = ExcelUtils.getSheet(aWorkbook, strSheetName);
				XSSFCellStyle aHeaderCellStyle = ExcelUtils.getReportHeaderDataCellStyle(aWorkbook);
				XSSFCellStyle aCellStyle = ExcelUtils.getReportDataCellStyle(aWorkbook);
				int iHeaderRow = 0;
				Row aHeaderRow = ExcelUtils.getRow(aSheet, iHeaderRow);
				if (aHeaderRow == null) {
					aHeaderRow = aSheet.createRow(iHeaderRow);
				}
				for (int i = 0; i < lstRecords.size(); i++) {
					LinkedHashMap<String, Object> mpColumnData = lstRecords.get(i);
					Set<Entry<String, Object>> stEntry = mpColumnData.entrySet();
					int iDataCell = 0;
					int iDataRowNum = i + 1;
					Row aDataRow = ExcelUtils.getRow(aSheet, iDataRowNum);
					if (aDataRow == null) {
						aDataRow = aSheet.createRow(iDataRowNum);
					}
					for (Entry<String, Object> aEntry : stEntry) {
						String strKey = aEntry.getKey();// ColumnName
						String strValue = String.valueOf(aEntry.getValue());// ColumnValue
						ExcelUtils.setCellValue(aSheet, aHeaderRow, iDataCell, aHeaderCellStyle, strKey);
						ExcelUtils.setCellValue(aSheet, aDataRow, iDataCell, aCellStyle, strValue);
						iDataCell = iDataCell + 1;
					}
				}
				ExcelUtils.writeWrokBook(aWorkbook, aDBReportFile);
			}
			return AppConstants.TEST_RESULT_PASS;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.TEST_RESULT_FAIL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}
}
