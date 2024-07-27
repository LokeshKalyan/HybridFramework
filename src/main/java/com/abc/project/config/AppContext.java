/****************************************************************************
 * File Name 		: AppContext.java
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
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.apache.commons.collections4.CollectionUtils;
import org.openqa.selenium.remote.SessionId;

import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.beans.TestSuiteBean;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.SummaryReportConstants;
import com.abc.project.utils.AppUtils;
import com.dxc.enums.ExecutionStatus;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 12:51:11 pm
 */
public class AppContext {

	private static AppContext instance;

	private LinkedHashMap<String, LinkedHashMap<String, String>> runTimeDataMap;

	private LinkedHashMap<String, LinkedHashMap<String, String>> execSurmmaryReport;

	private LinkedHashMap<String, LinkedHashMap<String, String>> execRefSummaryReport;

	private LinkedHashMap<String, LinkedHashMap<String, String>> execQuotationSurmmaryReport;

	private LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, LinkedList<String>>>> mpOtherScenarioReport;

	private LinkedHashMap<String, LinkedHashMap<String, String>> documentTestReportMap;

	private LinkedList<File> lstExecutionReports;

	private LinkedHashMap<String, LinkedHashMap<String, Long>> stepsResultMap;

	private LinkedHashMap<String, File> downloadedMap;
	
	private LinkedHashMap<String, LinkedList<String>> mpGridDownloadFiles;

	
	private AppContext() {
		initializeContextData();
	}

	public static AppContext getInstance() {
		if (null == instance) {
			synchronized (AppContext.class) {
				if (null == instance) {
					instance = new AppContext();
				}
			}
		}
		return instance;
	}
	
	public void initializeContextData() {
		runTimeDataMap = new LinkedHashMap<>();
		execSurmmaryReport = new LinkedHashMap<>();
		execRefSummaryReport = new LinkedHashMap<>();
		execQuotationSurmmaryReport = new LinkedHashMap<>();
		mpOtherScenarioReport = new LinkedHashMap<>();
		lstExecutionReports = new LinkedList<>();
		stepsResultMap = new LinkedHashMap<>();
		documentTestReportMap = new LinkedHashMap<>();
		downloadedMap = new LinkedHashMap<>();
		mpGridDownloadFiles = new LinkedHashMap<>();
	}

	/***
	 * Adds RunTime Data
	 * 
	 * @param strScenarioName
	 * @param strKey
	 * @param strValue
	 */
	public void addRunTimeData(String strScenarioName, BrowsersConfigBean aBrowsersConfigBean, String strKey,
			String strValue) {
		strScenarioName = AppUtils.getValidPartScenarioName(strScenarioName);
		String strAppContextKey = getAppContextKey(strScenarioName, aBrowsersConfigBean);
		LinkedHashMap<String, String> fieldValue = getRunTimeDataBean(strAppContextKey);
		if (fieldValue == null) {
			fieldValue = new LinkedHashMap<>();
			fieldValue.put(strKey, strValue);
			runTimeDataMap.put(strAppContextKey, fieldValue);
		} else {
			fieldValue.put(strKey, strValue);
			runTimeDataMap.put(strAppContextKey, fieldValue);
		}
	}

	public LinkedHashMap<String, LinkedHashMap<String, String>> getRunTimeDataMap() {
		return runTimeDataMap;
	}

	public void setRunTimeDataMap(LinkedHashMap<String, LinkedHashMap<String, String>> runTimeDataMap) {
		this.runTimeDataMap = runTimeDataMap;
	}

	public LinkedHashMap<String, String> getRunTimeDataBean(String strScenarioName) {
		LinkedHashMap<String, String> fieldValue = null;
		if (runTimeDataMap.containsKey(strScenarioName)) {
			fieldValue = runTimeDataMap.get(strScenarioName);
		}
		return fieldValue;
	}

	public void addStepResults(String strBrowserDisplayName, String strTestStepStatus) {
		LinkedHashMap<String, Long> resultsBean = getStepResults(strBrowserDisplayName);
		if (resultsBean == null) {
			resultsBean = new LinkedHashMap<>();
		}
		ExecutionStatus aExecutionStatus = ExecutionStatus.getExecutionStatusByStatus(strTestStepStatus);
		long lPassCount = getStepResultCount(resultsBean, AppConstants.TEST_RESULT_PASS);
		long lFailCount = getStepResultCount(resultsBean, AppConstants.TEST_RESULT_FAIL);
		long lWarningCount = getStepResultCount(resultsBean, AppConstants.TEST_RESULT_WARING);
		long lothersCount = getStepResultCount(resultsBean, AppConstants.TEST_RESULT_OTHERS);
		long lTotalCount = 0;
		switch (aExecutionStatus) {
		case PASS:
			lPassCount = lPassCount + 1;
			break;
		case FAIL:
			lFailCount = lFailCount + 1;
			break;
		case WARING:
			lWarningCount = lWarningCount + 1;
			break;
		default:
			lothersCount = lothersCount + 1;
			break;
		}
		lTotalCount = lPassCount + lFailCount + lWarningCount + lothersCount;
		resultsBean.put(AppConstants.TEST_RESULT_TOTALCOUNT, lTotalCount);
		resultsBean.put(AppConstants.TEST_RESULT_PASS, lPassCount);
		resultsBean.put(AppConstants.TEST_RESULT_FAIL, lFailCount);
		resultsBean.put(AppConstants.TEST_RESULT_WARING, lWarningCount);
		resultsBean.put(AppConstants.TEST_RESULT_OTHERS, lothersCount);
		stepsResultMap.put(strBrowserDisplayName, resultsBean);
	}

	public long getStepResultCount(LinkedHashMap<String, Long> resultsBean, String strKey) {
		if (resultsBean != null && resultsBean.get(strKey) != null) {
			return resultsBean.get(strKey);
		}
		return 0;
	}

	public LinkedHashMap<String, Long> getStepResults(String strBrowserDisplayName) {
		LinkedHashMap<String, Long> resultsBean = null;
		if (stepsResultMap.containsKey(strBrowserDisplayName)) {
			resultsBean = stepsResultMap.get(strBrowserDisplayName);
		}
		return resultsBean;
	}

	public LinkedHashMap<String, LinkedHashMap<String, Long>> getStepResults() {
		return stepsResultMap;
	}
	

	/***
	 * Adds ExecSurmmaryReport Data
	 * 
	 * @param strKey
	 * @param strValue
	 */
	public void addExecSurmmaryReport(String strScenarioName, BrowsersConfigBean aBrowsersConfigBean, String strKey,
			String strValue) {
		String strAppContextKey = getAppContextKey(strScenarioName, aBrowsersConfigBean);
		LinkedHashMap<String, String> fieldValue = getExecSurmmaryReport(strScenarioName, aBrowsersConfigBean);
		if (fieldValue == null) {
			fieldValue = new LinkedHashMap<>();
			TestSuiteBean aTestSuiteBean = AppConfig.getInstance()
					.getFilteredControllerSuiteByScenarioAndBrowser(strScenarioName, aBrowsersConfigBean);
			fieldValue.put(SummaryReportConstants.SERIAL_NUMBER_HEADER, aTestSuiteBean == null ? String.valueOf(-1)
					: String.valueOf(aTestSuiteBean.getScenarioSerialNumber()));
			fieldValue.put(SummaryReportConstants.SECNARIO_HEADER, strScenarioName);
			fieldValue.put(strKey, strValue);
			execSurmmaryReport.put(strAppContextKey, fieldValue);
		} else {
			fieldValue.put(strKey, strValue);
			execSurmmaryReport.put(strAppContextKey, fieldValue);
		}
	}

	/***
	 * Fetches the execution summary report data
	 * 
	 * @return
	 */
	public LinkedHashMap<String, String> getExecSurmmaryReport(String strScenarioName,
			BrowsersConfigBean aBrowsersConfigBean) {
		LinkedHashMap<String, String> fieldValue = null;
		String strAppContextKey = getAppContextKey(strScenarioName, aBrowsersConfigBean);
		if (execSurmmaryReport.containsKey(strAppContextKey)) {
			fieldValue = execSurmmaryReport.get(strAppContextKey);
		}
		return fieldValue;
	}

	/***
	 * Adds Exec reference surmmaryReport Data
	 * 
	 * @param aBrowsersConfigBean
	 * @param strKey
	 * @param strValue
	 */
	public void addExecRefSurmmaryReport(String strScenarioName, BrowsersConfigBean aBrowsersConfigBean, String strKey,
			String strValue) {
		String strAppContextKey = getAppContextKey(strScenarioName, aBrowsersConfigBean);
		LinkedHashMap<String, String> fieldValue = getExecRefSurmmaryReport(strScenarioName, aBrowsersConfigBean);
		if (fieldValue == null) {
			fieldValue = new LinkedHashMap<>();
			TestSuiteBean aTestSuiteBean = AppConfig.getInstance()
					.getFilteredControllerSuiteByScenarioAndBrowser(strScenarioName, aBrowsersConfigBean);
			fieldValue.put(SummaryReportConstants.SERIAL_NUMBER_HEADER, aTestSuiteBean == null ? String.valueOf(-1)
					: String.valueOf(aTestSuiteBean.getScenarioSerialNumber()));
			fieldValue.put(SummaryReportConstants.SECNARIO_HEADER, strScenarioName);
			fieldValue.put(strKey, strValue);
			execRefSummaryReport.put(strAppContextKey, fieldValue);
		} else {
			fieldValue.put(strKey, strValue);
			execRefSummaryReport.put(strAppContextKey, fieldValue);
		}
	}

	/***
	 * Fetches the execution reference summary report data
	 * 
	 * @return
	 */
	public LinkedHashMap<String, String> getExecRefSurmmaryReport(String strScenarioName,
			BrowsersConfigBean aBrowsersConfigBean) {
		LinkedHashMap<String, String> fieldValue = null;
		String strAppContextKey = getAppContextKey(strScenarioName, aBrowsersConfigBean);
		if (execRefSummaryReport.containsKey(strAppContextKey)) {
			fieldValue = execRefSummaryReport.get(strAppContextKey);
		}
		return fieldValue;
	}

	/***
	 * Adds Exec Quotation SurmmaryReport Data
	 * 
	 * @param strKey
	 * @param strValue
	 */
	public void addExecQuotationSurmmaryReport(String strScenarioName, BrowsersConfigBean aBrowsersConfigBean,
			String strKey, String strValue) {
		String strAppContextKey = getAppContextKey(strScenarioName, aBrowsersConfigBean);
		LinkedHashMap<String, String> fieldValue = getExecQuotationSurmmaryReport(strScenarioName, aBrowsersConfigBean);
		if (fieldValue == null) {
			fieldValue = new LinkedHashMap<>();
			TestSuiteBean aTestSuiteBean = AppConfig.getInstance()
					.getFilteredControllerSuiteByScenarioAndBrowser(strScenarioName, aBrowsersConfigBean);
			fieldValue.put(SummaryReportConstants.SERIAL_NUMBER_HEADER, aTestSuiteBean == null ? String.valueOf(-1)
					: String.valueOf(aTestSuiteBean.getScenarioSerialNumber()));
			fieldValue.put(SummaryReportConstants.SECNARIO_HEADER, strScenarioName);
			fieldValue.put(strKey, strValue);
			execQuotationSurmmaryReport.put(strAppContextKey, fieldValue);
		} else {
			fieldValue.put(strKey, strValue);
			execQuotationSurmmaryReport.put(strAppContextKey, fieldValue);
		}
	}

	/***
	 * Fetches the execution Quotation summary report data
	 * 
	 * @return
	 */
	public LinkedHashMap<String, String> getExecQuotationSurmmaryReport(String strScenarioName,
			BrowsersConfigBean aBrowsersConfigBean) {
		LinkedHashMap<String, String> fieldValue = null;
		String strAppContextKey = getAppContextKey(strScenarioName, aBrowsersConfigBean);
		if (execQuotationSurmmaryReport.containsKey(strAppContextKey)) {
			fieldValue = execQuotationSurmmaryReport.get(strAppContextKey);
		}
		return fieldValue;
	}

	public void addOtherSummaryReport(String strScenarioName, BrowsersConfigBean aBrowsersConfigBean,
			String strSheetName, String strDataKey, LinkedList<String> lstValue) {
		// Key format --> SheetName
		String strAppContextKey = getAppContextKey(strScenarioName, aBrowsersConfigBean);
		LinkedHashMap<String, LinkedHashMap<String, LinkedList<String>>> fieldValue = getOtherSummaryReport(
				strScenarioName, aBrowsersConfigBean);
		if (fieldValue == null) {
			fieldValue = new LinkedHashMap<>();
		}
		LinkedHashMap<String, LinkedList<String>> mpSummaryReportBean = fieldValue.get(strSheetName);
		if (mpSummaryReportBean == null) {
			mpSummaryReportBean = new LinkedHashMap<>();
		}
		LinkedList<String> lstOldData = mpSummaryReportBean.get(strDataKey);
		LinkedList<String> lstData = new LinkedList<>();
		if (!CollectionUtils.isEmpty(lstOldData)) {
			lstData.addAll(lstOldData);
		}
		if (!CollectionUtils.isEmpty(lstValue)) {
			lstData.addAll(lstValue);
		}
		mpSummaryReportBean.put(strDataKey, lstData);
		fieldValue.put(strSheetName, mpSummaryReportBean);
		mpOtherScenarioReport.put(strAppContextKey, fieldValue);
	}

	public LinkedHashMap<String, LinkedHashMap<String, LinkedList<String>>> getOtherSummaryReport(
			String strScenarioName, BrowsersConfigBean aBrowsersConfigBean) {
		LinkedHashMap<String, LinkedHashMap<String, LinkedList<String>>> fieldValue = null;
		String strAppContextKey = getAppContextKey(strScenarioName, aBrowsersConfigBean);
		if (mpOtherScenarioReport.containsKey(strAppContextKey)) {
			fieldValue = mpOtherScenarioReport.get(strAppContextKey);
		}
		return fieldValue;
	}

	/**
	 * @return the lstExecutionReports
	 */
	public LinkedList<File> getExecutionReports() {
		return lstExecutionReports;
	}

	/**
	 * @param lstExecutionReports the lstExecutionReports to set
	 */
	public void setExecutionReports(LinkedList<File> lstExecutionReports) {
		this.lstExecutionReports = lstExecutionReports;
	}

	public void addExecutionReports(File aReportFile) {
		if (CollectionUtils.isEmpty(getExecutionReports())) {
			setExecutionReports(new LinkedList<>());
		}
		if (aReportFile == null) {
			return;
		}
		if (!getExecutionReports().contains(aReportFile)) {
			getExecutionReports().add(aReportFile);
		}
	}

	public String getAppContextKey(String strScenarioName, BrowsersConfigBean aBrowsersConfigBean) {
		String strBrowserName = aBrowsersConfigBean.getBrowserDisplayName();
		return String.format("%s_%s", strScenarioName, strBrowserName);
	}

	/***
	 * Adds RunTime Data
	 * 
	 * @param strScenarioName
	 * @param strKey
	 * @param strValue
	 */
	public void addDocumentTestReport(String strScenarioName, BrowsersConfigBean aBrowsersConfigBean, String strKey,
			String strValue) {
		strScenarioName = AppUtils.getValidPartScenarioName(strScenarioName);
		String strAppContextKey = getAppContextKey(strScenarioName, aBrowsersConfigBean);
		LinkedHashMap<String, String> fieldValue = getDocumentTestReportBean(strScenarioName, aBrowsersConfigBean);
		if (fieldValue == null) {
			fieldValue = new LinkedHashMap<>();
			fieldValue.put(strKey, strValue);
			documentTestReportMap.put(strAppContextKey, fieldValue);
		} else {
			fieldValue.put(strKey, strValue);
			documentTestReportMap.put(strAppContextKey, fieldValue);
		}
	}

	public LinkedHashMap<String, String> getDocumentTestReportBean(String strScenarioName,
			BrowsersConfigBean aBrowsersConfigBean) {
		LinkedHashMap<String, String> fieldValue = null;
		strScenarioName = AppUtils.getValidPartScenarioName(strScenarioName);
		String strAppContextKey = getAppContextKey(strScenarioName, aBrowsersConfigBean);
		if (documentTestReportMap.containsKey(strAppContextKey)) {
			fieldValue = documentTestReportMap.get(strAppContextKey);
		}
		return fieldValue;
	}

	public void addDownloadedFile(String strScenarioName, String strReportKeyWord, File aDwFile) {
		strScenarioName = AppUtils.getValidPartScenarioName(strScenarioName);
		String strAppContextKey =  getAppContextDownloadedFileKey(strScenarioName, strReportKeyWord);
		downloadedMap.put(strAppContextKey, aDwFile);
	}
	
	public File getDownloadedFile(String strScenarioName, String strReportKeyWord) {
		strScenarioName = AppUtils.getValidPartScenarioName(strScenarioName);
		String strAppContextKey =  getAppContextDownloadedFileKey(strScenarioName, strReportKeyWord);
		return downloadedMap.get(strAppContextKey);
	}
	
	
	public void addGridDownloadedFile(String strScenarioName, SessionId aSessionId, String strFileName) {
		LinkedList<String> lstGridDwFiles = getGridDownloadedFiles(strScenarioName, aSessionId);
		if(CollectionUtils.isEmpty(lstGridDwFiles)) {
			lstGridDwFiles = new LinkedList<>();
		}
		lstGridDwFiles.add(strFileName);
		strScenarioName = AppUtils.getValidPartScenarioName(strScenarioName);
		String strAppContextKey =  getAppContextDownloadedFileKey(strScenarioName, aSessionId.toString());
		mpGridDownloadFiles.put(strAppContextKey, lstGridDwFiles);
	}
	
	public LinkedList<String> getGridDownloadedFiles(String strScenarioName, SessionId aSessionId) {
		strScenarioName = AppUtils.getValidPartScenarioName(strScenarioName);
		String strAppContextKey =  getAppContextDownloadedFileKey(strScenarioName, aSessionId.toString());
		return mpGridDownloadFiles.get(strAppContextKey);
	}

	private String getAppContextDownloadedFileKey(String strScenarioName, String strReportKeyWord) {
		return String.format("%s-%s", strScenarioName, strReportKeyWord);
	}
}
