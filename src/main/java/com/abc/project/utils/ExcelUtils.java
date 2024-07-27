/****************************************************************************
 * File Name 		: ExcelUtils.java
 * Package			: com.dxc.zurich.utils
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
package com.abc.project.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.constants.ErrorMsgConstants;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 12:16:59 pm
 */
public class ExcelUtils {

	/**
	 * Gets the string value.
	 *
	 * @param cell the cell
	 * @return the string value
	 * @throws BaseException
	 */
	public static String getStringValue(Cell cell) {

		if (cell == null) {
			return null;
		}
		String outputString = null;
		Double val = null;

		if (cell.getCellType() == CellType.NUMERIC) {
			val = cell.getNumericCellValue();
			outputString = val.toString();
		} else if (cell.getCellType() == CellType.STRING) {
			outputString = cell.getStringCellValue();
		}
		return outputString;
	}

	public static String getStringValue(FormulaEvaluator evaluator, Sheet sheet, Cell aCell) {
		if (sheet == null || sheet.getWorkbook() == null || evaluator == null) {
			return null;
		}
		DataFormatter formatter = new DataFormatter();
		return formatter.formatCellValue(aCell, evaluator);
	}

	public static String getStringValue(FormulaEvaluator evaluator, Sheet sheet, String strSheetName, Row row,
			int colNum, boolean isDataCheckMandatory) throws Exception {
		if (sheet == null || sheet.getWorkbook() == null || evaluator == null) {
			if (isDataCheckMandatory) {
				throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_STRING, row.getRowNum() + 1,
						ExcelUtils.cellNumToAlphabetic(colNum), strSheetName));
			} else {
				return "";
			}
		}

		DataFormatter formatter = new DataFormatter();
		Cell aCell = ExcelUtils.getCell(row, colNum);
		if (aCell == null && isDataCheckMandatory) {
			throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_CELL, row.getRowNum() + 1,
					ExcelUtils.cellNumToAlphabetic(colNum), strSheetName));
		}

		String strVal = aCell == null ? "" : formatter.formatCellValue(aCell, evaluator);

		if (StringUtils.isEmpty(strVal) && isDataCheckMandatory) {
			throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_STRING, row.getRowNum() + 1,
					ExcelUtils.cellNumToAlphabetic(colNum), strSheetName));
		}
		return strVal;
	}

	public static String getStringValue(FormulaEvaluator evaluator, Sheet sheet, String strSheetName, Row row,
			int colNum) throws Exception {
		return getStringValue(evaluator, sheet, strSheetName, row, colNum, true);
	}

	public static String getStringValue(FormulaEvaluator evaluator, Sheet sheet, Row row, int colNum) throws Exception {
		if (sheet == null || sheet.getWorkbook() == null || evaluator == null) {
			return null;
		}
		DataFormatter formatter = new DataFormatter();
		// from 5.2.0 on the DataFormatter can set to use cached values for formula cells
		formatter.setUseCachedValuesForFormulaCells(true);
		Cell aCell = ExcelUtils.getCell(row, colNum);
		if (aCell == null) {
			return null;
		}
		return formatter.formatCellValue(aCell);
	}

	public static String getMergerdCellValue(FormulaEvaluator evaluator, Sheet sheet, String strSheetName, Cell aCell)
			throws Exception {
		if (aCell == null) {
			return null;
		}
		String strValue = null;
		List<CellRangeAddress> regionsList = new ArrayList<CellRangeAddress>();
		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
			regionsList.add(sheet.getMergedRegion(i));
		}

		for (CellRangeAddress region : regionsList) {

			// If the region does contain the cell you have just read from the row
			if (region.isInRange(aCell.getRowIndex(), aCell.getColumnIndex())) {
				// Now, you need to get the cell from the top left hand corner of this
				int rowNum = region.getFirstRow();
				Row aRow = getRow(sheet, rowNum);
				if (aRow == null) {
					throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_ROW, rowNum + 1, strSheetName));
				}
				int colIndex = region.getFirstColumn();
				Cell aMergedCell = ExcelUtils.getCell(aRow, colIndex);
				if (aMergedCell == null) {
					throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_CELL, rowNum + 1,
							ExcelUtils.cellNumToAlphabetic(colIndex), strSheetName));
				}
				strValue = getStringValue(evaluator, sheet, aMergedCell);
			}
		}

		return strValue;
	}

	public static Row getRow(Sheet sheet, int rowNum) {

		Row row = null;

		row = sheet.getRow(rowNum);

		return row;
	}

	public static Cell getCell(Row row, int cellNum) {

		Cell cell = null;

		cell = row.getCell(cellNum);

		return cell;
	}

	public static String cellNumToAlphabetic(int cellNum) {
		int quot = cellNum / 26;
		int rem = cellNum % 26;
		char letter = (char) ((int) 'A' + rem);

		return (quot == 0) ? "" + letter : cellNumToAlphabetic(quot - 1) + letter;
	}

	public static XSSFSheet getSheet(XSSFWorkbook aWorkbook, String strSheetName) {
		XSSFSheet sheet = aWorkbook.getSheet(strSheetName);
		if (sheet == null) {
			sheet = aWorkbook.createSheet(strSheetName);
		}
		sheet.addIgnoredErrors(new CellRangeAddress(0, 9999, 0, 9999), IgnoredErrorType.NUMBER_STORED_AS_TEXT);
		return sheet;
	}

	public static void writeWrokBook(XSSFWorkbook workbook, File aFile) throws Exception {
		try {
			if (!aFile.getParentFile().exists()) {
				aFile.getParentFile().mkdirs();
			}
			ClearSessions.closeFileByName(aFile);
			autoSizeColumns(workbook);
			try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(aFile))) {
				workbook.write(outputStream);
				outputStream.flush();
			}
		} catch (Exception ie) {
			throw new Exception(
					AppUtils.formatMessage(ErrorMsgConstants.ERR_WRITE_WORKBOOK, aFile.getName(), aFile.getParent()),
					ie);
		}
	}

	private static void autoSizeColumns(XSSFWorkbook workbook) {
		int numberOfSheets = workbook.getNumberOfSheets();
		for (int i = 0; i < numberOfSheets; i++) {
			Sheet aSheet = workbook.getSheetAt(i);
			if (aSheet.getPhysicalNumberOfRows() > 0) {
				Row aRow = aSheet.getRow(aSheet.getFirstRowNum());
				Iterator<Cell> aCellIterator = aRow.cellIterator();
				while (aCellIterator.hasNext()) {
					Cell cell = aCellIterator.next();
					int iColumnIndex = cell.getColumnIndex();
					aSheet.autoSizeColumn(iColumnIndex, true);
				}
			}
		}
	}

	public static void setCellValue(Row row, int cellNum, String data) {

		Cell cell = ExcelUtils.getCell(row, cellNum);

		if (cell == null) {
			cell = row.createCell(cellNum);
		}
		cell.setCellValue(data);
	}

	public static void setCellValue(Row row, int cellNum, CellStyle aCellStyle, String data) {

		Cell cell = ExcelUtils.getCell(row, cellNum);

		if (cell == null) {
			cell = row.createCell(cellNum);
		}
		cell.setCellValue(data);
		cell.setCellStyle(aCellStyle);
	}

	public static void setCellValue(Sheet aSheet, Row row, int cellNum, CellStyle aCellStyle, String data) {

		Cell cell = ExcelUtils.getCell(row, cellNum);

		if (cell == null) {
			cell = row.createCell(cellNum);
		}
		cell.setCellValue(data);
		cell.setCellStyle(aCellStyle);
//		aSheet.autoSizeColumn(cellNum, true);
	}

	/**
	 * Generates default cell style
	 * 
	 * @param aWorkbook
	 * @return
	 */
	private static XSSFCellStyle getDeFaultCellStyle(XSSFWorkbook aWorkbook) {
		XSSFCellStyle aCellStyle = aWorkbook.createCellStyle();
		aCellStyle.setBorderBottom(BorderStyle.THIN);
		aCellStyle.setBorderTop(BorderStyle.THIN);
		aCellStyle.setBorderLeft(BorderStyle.MEDIUM);
		aCellStyle.setBorderRight(BorderStyle.MEDIUM);
		aCellStyle.setAlignment(HorizontalAlignment.CENTER);
		aCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		aCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		aCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		aCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		return aCellStyle;
	}

	/***
	 * Generates the APP Default Header Cell Style
	 * 
	 * @param aWorkbook
	 * @return
	 */
	public static XSSFCellStyle getReportHeaderDataCellStyle(XSSFWorkbook aWorkbook) {

		XSSFCellStyle aCellStyle = getDeFaultCellStyle(aWorkbook);
		aCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		aCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFFont aFont = aWorkbook.createFont();
		aFont.setFontHeight(10);
		aFont.setFontName("Arial");
		aFont.setColor(IndexedColors.BLACK.getIndex());
		aFont.setBold(true);
		aFont.setItalic(false);
		aCellStyle.setFont(aFont);

		return aCellStyle;
	}

	/**
	 * Generates the app Default Value Data Cell Style
	 * 
	 * @param aWorkbook
	 * @return
	 */
	public static XSSFCellStyle getReportDataCellStyle(XSSFWorkbook aWorkbook) {
		XSSFCellStyle aCellStyle = getDeFaultCellStyle(aWorkbook);

		XSSFFont aFont = aWorkbook.createFont();
		aFont.setFontHeight(8.5);
		aFont.setFontName("Arial");
		aFont.setColor(IndexedColors.BLACK.getIndex());
		aFont.setBold(false);
		aFont.setItalic(false);
		aCellStyle.setFont(aFont);

		return aCellStyle;
	}

	public static void setHeaderCellValue(XSSFWorkbook workBook, Row row, int cellNum, String data) {

		XSSFCellStyle aCellStyle = workBook.createCellStyle();
		aCellStyle.setBorderBottom(BorderStyle.THIN);
		aCellStyle.setBorderTop(BorderStyle.THIN);
		aCellStyle.setBorderLeft(BorderStyle.MEDIUM);
		aCellStyle.setBorderRight(BorderStyle.MEDIUM);
		aCellStyle.setAlignment(HorizontalAlignment.CENTER);
		aCellStyle.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		aCellStyle.setFillPattern(FillPatternType.FINE_DOTS);

		XSSFFont aFont = workBook.createFont();
		aFont.setFontHeight(10);
		aFont.setFontName("Arial");
		aFont.setColor(IndexedColors.WHITE.getIndex());
		aFont.setBold(true);
		aFont.setItalic(false);
		aCellStyle.setFont(aFont);

		Cell cell = ExcelUtils.getCell(row, cellNum);

		if (cell == null) {
			cell = row.createCell(cellNum);
		}
		cell.setCellValue(data);
		cell.setCellStyle(aCellStyle);

	}

	public static Row getValidScenarioRow(FormulaEvaluator evaluator, XSSFSheet aSheet, String testScenarioName,
			BrowsersConfigBean aBrowsersConfigBean) {

		for (int i = 1; i <= aSheet.getLastRowNum() - aSheet.getFirstRowNum(); i++) {
			Row aRow = aSheet.getRow(i);
			if (aRow == null) {
				continue;
			}
			Cell scenarioCell = ExcelUtils.getCell(aRow, 0);
			if (scenarioCell == null) {
				return null;
			}
			Cell browserDisplayName = ExcelUtils.getCell(aRow, 1);
			if (browserDisplayName == null) {
				return null;
			}
			String scenarioNamekey = ExcelUtils.getStringValue(evaluator, aSheet, scenarioCell);
			String browserDisplayNamekey = ExcelUtils.getStringValue(evaluator, aSheet, browserDisplayName);
			if (StringUtils.equalsIgnoreCase(scenarioNamekey, testScenarioName) && StringUtils
					.equalsIgnoreCase(browserDisplayNamekey, aBrowsersConfigBean.getBrowserDisplayName())) {
				return aRow;
			}
		}
		return null;
	}

	public static int getValidDataCellNo(Row aHeaderRow, String strHeader) {

		int lastCellNumber = aHeaderRow.getLastCellNum();

		for (int cellNum = 0; cellNum < lastCellNumber; cellNum++) {
			Cell keyCell = ExcelUtils.getCell(aHeaderRow, cellNum);
			if (keyCell == null) {
				continue;
			}
			String key = ExcelUtils.getStringValue(keyCell);

			if (StringUtils.equalsIgnoreCase(key, strHeader)) {
				return cellNum;
			}
		}
		return -1;
	}
	
	public static LinkedHashMap<String, Integer> getColumnNames(File aFile, Sheet aSheet , String strSheetName) throws Exception {
		LinkedHashMap<String, Integer> colMapByName = new LinkedHashMap<>();
		if (aSheet == null) {
			throw new IOException(MessageFormat.format(ErrorMsgConstants.SHEET_NOT_FOUND, strSheetName,
					aFile.getPath()));
		}
		int iRow = 0;
		Row aHeaderRow = getRow(aSheet, iRow);
		if (aHeaderRow == null) {
			throw new Exception(MessageFormat.format(ErrorMsgConstants.ERR_ROW, iRow + 1, strSheetName));
		}
		int colNum = aHeaderRow == null ? 0 : aHeaderRow.getLastCellNum();
		  if (aHeaderRow != null && aHeaderRow.cellIterator().hasNext()) {
              for (int j = 0; j < colNum; j++) {
            	  String strColumnName = getStringValue(aHeaderRow.getCell(j));
                  colMapByName.put(StringUtils.trim(strColumnName), j);
              }
          }
		return colMapByName;
	}

	public static XSSFWorkbook getWorkBook(File aFile) throws IOException {
		try {
			ClearSessions.closeFileByName(aFile);
			ZipSecureFile.setMinInflateRatio(0);
			return new XSSFWorkbook(new BufferedInputStream(new FileInputStream(aFile)));
		} catch (Exception e) {
			throw new IOException(AppUtils.formatMessage(ErrorMsgConstants.FILENTFOUND, aFile.getPath()));
		}
	}
}
