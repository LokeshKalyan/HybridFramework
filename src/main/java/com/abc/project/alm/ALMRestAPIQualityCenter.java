/****************************************************************************
 * File Name 		: ALMRestAPIQualityCenter.java
 * Package			: com.dxc.zurich.alm
 * Author			: pmusunuru2
 * Creation Date	: Dec 06, 2022
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.alm;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.abc.project.alm.beans.ALMWrapperConfigBean;
import com.abc.project.alm.beans.Attachment;
import com.abc.project.alm.beans.Field;
import com.abc.project.alm.beans.Response;
import com.abc.project.alm.beans.Run;
import com.abc.project.alm.beans.RunStep;
import com.abc.project.alm.beans.RunSteps;
import com.abc.project.alm.beans.TestInstance;
import com.abc.project.alm.beans.TestInstances;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.ErrorMsgConstants;
import com.abc.project.constants.RunTimeDataConstants;
import com.abc.project.constants.SummaryReportConstants;
import com.abc.project.utils.AppUtils;
import com.abc.project.utils.EntityMarshallingUtils;
import com.abc.project.utils.ExcelUtils;

/**
 * @author pmusunuru2
 * @since Dec 06, 2022 3:04:13 pm
 */
public class ALMRestAPIQualityCenter {

	// https://github.com/alonso05/ALM/tree/master/src/qc/rest/examples
	// https://stackoverflow.com/questions/33249059/get-all-test-sets-given-a-test-set-folder-path-in-alm-rest-api
	// https://github.com/okean/alm-rest-api/blob/master/src/java/org/alm/Dao.java
	public static void main(String[] args) {
		if (args != null && args.length >= 1) {
			// bulk Upload from File
			File aALMConfig = AppUtils.getFileFromPath(args[0]);
			String strLogMessage = AppUtils.formatMessage("Uploading ALM Results with args {0}", Arrays.toString(args));
			System.out.println(strLogMessage);
			AlmConnector aALMConnector = null;
			try {
				LinkedList<ALMWrapperConfigBean> lstALMAlmWrapperConfigBean = getAlmWrapperConfigBean(aALMConfig);
				for (ALMWrapperConfigBean aALMWrapperConfigBean : lstALMAlmWrapperConfigBean) {
					try {
						aALMConnector = initAlmConnection(aALMWrapperConfigBean);
						RestConnector aRestConnector = aALMConnector.getRestConnector();
						aRestConnector.getQCSession();
						TestInstance aSelectedTestInstance = getTestInstance(aRestConnector, aALMWrapperConfigBean);
						if (aSelectedTestInstance == null) {
							System.err.println(AppUtils.formatMessage("Scenario {0} is not found in ALM",
									aALMWrapperConfigBean.getOriginalScenarioName()));
							continue;
						}
						Run aRun = getALMRun(aRestConnector, aALMWrapperConfigBean, aSelectedTestInstance);
						if (aRun == null) {
							System.err.println(AppUtils.formatMessage("Failed to upload {0} To ALM",
									aALMWrapperConfigBean.getOriginalScenarioName()));
							continue;
						}
						System.out.println(AppUtils.formatMessage("SuccessFully Updated {0} To ALM",
								aALMWrapperConfigBean.getOriginalScenarioName()));
					} catch (Exception ex) {
						System.err.println(
								"Failed to Update " + aALMWrapperConfigBean.getOriginalScenarioName() + " To ALM");
						ex.printStackTrace();
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (aALMConnector != null) {
					try {
						aALMConnector.logout();
					} catch (Exception e) {
					}
				}
			}
		}
	}

	public static void uploadStaticALMData() {
		ALMWrapperConfigBean aALMWrapperConfigBean = new ALMWrapperConfigBean();
		aALMWrapperConfigBean.setALMURL("https://almzurich16beta.saas.microfocus.com/qcbin");
		aALMWrapperConfigBean.setALMDomain("UK_LIFE");
		aALMWrapperConfigBean.setALMProject("StrategicProtection");
		aALMWrapperConfigBean.setALMUserName("vbezawada");
		aALMWrapperConfigBean.setALMClientId("apikey-iimniemfabaloaocdnkc");
		aALMWrapperConfigBean.setALMClientSecret("plmhjjlillamgfmp");
		aALMWrapperConfigBean.setOriginalScenarioName("Test_Scenarios_BO_Authorisingaclaim_TC04");
		aALMWrapperConfigBean.setALMTestSetID("82029");
		aALMWrapperConfigBean.setALMExecutionStatus(Run.STATUS_FAILED);
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		final String runName = "Run " + dateFormat.format(new Date());
		aALMWrapperConfigBean.setALMRunName(runName);
		File aFile = new File(
				"C:\\UkLifeAutomation\\GitLocalRepo\\UKLifeAutomation\\ExecutionReport\\06-12-2023\\CapitaGoldSilverBronze\\WindowsGC\\Test_Scenarios_BO_Authorisingaclaim_TC04\\2730_Test_Scenarios_BO_Au_1206_011336-452.docx");
		List<File> lstFiles = new ArrayList<>();
		lstFiles.add(aFile);
		aFile = new File(
				"C:\\UkLifeAutomation\\GitLocalRepo\\UKLifeAutomation\\ExecutionReport\\06-12-2023\\CapitaGoldSilverBronze\\WindowsGC\\Test_Scenarios_BO_Authorisingaclaim_TC04\\2730_Test_Scenarios_BO_Au_1206_011335-212.html");
		lstFiles.add(aFile);
		aALMWrapperConfigBean.setALMAttachments(lstFiles);
		aALMWrapperConfigBean.setALMRunComments("ALM Testing With Rest API");
		AlmConnector aALMConnector = null;
		try {
			aALMConnector = initAlmConnection(aALMWrapperConfigBean);
			RestConnector aRestConnector = aALMConnector.getRestConnector();
			aRestConnector.getQCSession();
			TestInstance aSelectedTestInstance = getTestInstance(aRestConnector, aALMWrapperConfigBean);
			Run aRun = getALMRun(aRestConnector, aALMWrapperConfigBean, aSelectedTestInstance);
			if (aRun == null) {
				return;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (aALMConnector != null) {
				try {
					aALMConnector.logout();
				} catch (Exception e) {
				}
			}
		}
	}

	public static AlmConnector initAlmConnection(ALMWrapperConfigBean aALMWrapperConfigBean) throws Exception {
		AlmConnector aALMConnector = new AlmConnector(aALMWrapperConfigBean.getALMURL(),
				aALMWrapperConfigBean.getALMDomain(), aALMWrapperConfigBean.getALMProject());
		if (!aALMConnector.login(aALMWrapperConfigBean.getALMClientId(), aALMWrapperConfigBean.getALMClientSecret())) {
			throw new IOException(
					AppUtils.formatMessage(ErrorMsgConstants.ERR_ALM_HOST_CONNECT, aALMWrapperConfigBean.getALMURL()));
		}

		return aALMConnector;
	}

	private static TestInstances getTestInstances(RestConnector aRestConnector, String strALMTestSetID)
			throws Exception {
		String testInstanceUrl = aRestConnector.buildEntityCollectionUrl("test-instance");
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Accept", "application/xml");
		requestHeaders.put("Content-Type", "application/xml");
		String cycle = String.format("login-form-required=y&query={cycle-id[%s]}&page-size=%s", strALMTestSetID,
				AppConstants.ALM_REST_API_PAGINATION_MAX_LIMIT); // Test-SetID
		Response aResponse = aRestConnector.httpGet(testInstanceUrl, cycle, requestHeaders);
		if (aResponse.getStatusCode() != HttpURLConnection.HTTP_OK) {
			throw new IOException(AppUtils.formatMessage(ErrorMsgConstants.ERR_ALM_INVALID_TEST_ID, strALMTestSetID));
		}
		String strResponse = aResponse.toString();
		return EntityMarshallingUtils.marshal(TestInstances.class, strResponse);
	}

	public static TestInstance getTestInstance(RestConnector aRestConnector, ALMWrapperConfigBean aALMWrapperConfigBean)
			throws Exception {
		TestInstances aTestInstances = getTestInstances(aRestConnector, aALMWrapperConfigBean.getALMTestSetID());
		List<TestInstance> lstTestInstances = aTestInstances.entities();
		String strTSName = String.format("%s %s", aALMWrapperConfigBean.getOriginalScenarioName(),aALMWrapperConfigBean.getALMTestCasePrefix());
		for (TestInstance aTestInstance : lstTestInstances) {
			List<Field> lstFields = aTestInstance.fields();
			for (Field aField : lstFields) {
				if (!aField.name().equals("name")) {
					continue;
				}
				if (StringUtils.equals(aField.value(), strTSName)) {
					return aTestInstance;
				}
			}
		}
		return null;
	}

	public static boolean updateRunStepStatus(RestConnector aRestConnector, ALMWrapperConfigBean aALMWrapperConfigBean,
			String strRunID) throws Exception {
		String testInstanceUrl = aRestConnector.buildEntityUrl("run", strRunID) + "/run-steps";
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Accept", "application/xml");
		requestHeaders.put("Content-Type", "application/xml");
		Response aResponse = aRestConnector.httpGet(testInstanceUrl, null, requestHeaders);
		if (aResponse.getStatusCode() != HttpURLConnection.HTTP_OK) {
			throw new IOException(AppUtils.formatMessage(ErrorMsgConstants.ERR_ALM_INVALID_RUN_ID,
					aALMWrapperConfigBean.getOriginalScenarioName()));
		}
		String strResponse = aResponse.toString();
		RunSteps aRunSteps = EntityMarshallingUtils.marshal(RunSteps.class, strResponse);
		List<RunStep> lstRunSteps = aRunSteps.entities();
		for (RunStep aRunStep : lstRunSteps) {
			String runStepUrl = aRestConnector.buildEntityUrl("run", aRunStep.runId()) + "/run-steps/" + aRunStep.id();
			aRunStep.removeField("status");
			aRunStep.fieldValue("status", aALMWrapperConfigBean.getALMExecutionStatus());
			String strReq = EntityMarshallingUtils.unmarshal(RunStep.class, aRunStep);
			byte[] reqBytes = strReq.getBytes();
			requestHeaders = new HashMap<String, String>();
			requestHeaders.put("Accept", "application/xml");
			requestHeaders.put("Content-Type", "application/xml");
			Response aRunStepResponse = aRestConnector.httpPut(runStepUrl, reqBytes, requestHeaders);
			if (aRunStepResponse.getStatusCode() != HttpURLConnection.HTTP_OK) {
				throw new IOException(AppUtils.formatMessage(ErrorMsgConstants.ERR_ALM_INVALID_RUN_ID,
						aALMWrapperConfigBean.getOriginalScenarioName()));
			}
		}
		return true;
	}

	public static Run getALMRun(RestConnector aRestConnector, ALMWrapperConfigBean aALMWrapperConfigBean,
			TestInstance aTestInstance) throws Exception {

		// https://community.microfocus.com/adtd/sws-qc/f/itrc-895/179840/how-to-create-new-run-using-rest-api
		Run aRequestRun = new Run();
		aRequestRun.testInstanceId(aTestInstance.id());
		aRequestRun.name(aALMWrapperConfigBean.getALMRunName());
		aRequestRun.testId(aTestInstance.testId());
		aRequestRun.owner(aALMWrapperConfigBean.getALMUserName());
		aRequestRun.testSetId(aTestInstance.testSetId());
		aRequestRun.testType(Run.TEST_TYPE_MANUAL);
		// aRequestRun.status(aALMWrapperConfigBean.getALMExecutionStatus());
		aRequestRun.host(AppUtils.getHostName().toUpperCase());
		aRequestRun.comments(aALMWrapperConfigBean.getALMRunComments());

		String testInstanceUrl = aRestConnector.buildEntityCollectionUrl("run");
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Accept", "application/xml");
		requestHeaders.put("Content-Type", "application/xml");
		String cycle = "login-form-required=y";
		String strReq = EntityMarshallingUtils.unmarshal(Run.class, aRequestRun);
		byte[] reqBytes = strReq.getBytes();
		Response aResponse = aRestConnector.httpPost(testInstanceUrl, cycle, reqBytes, requestHeaders);
		if (aResponse.getStatusCode() != HttpURLConnection.HTTP_OK
				&& aResponse.getStatusCode() != HttpURLConnection.HTTP_CREATED) {
			throw new IOException(AppUtils.formatMessage(ErrorMsgConstants.ERR_ALM_INVALID_RUN_ID,
					aALMWrapperConfigBean.getOriginalScenarioName()));
		}
		String strResponse = aResponse.toString();
		Run aCreateResponseRun = EntityMarshallingUtils.marshal(Run.class, strResponse);
//		boolean bUpdateTestSetStatus = updateRunStepStatus(aRestConnector, aALMWrapperConfigBean, aResPonseRun.id());
//		if (!bUpdateTestSetStatus) {
//			return null;
//		}
		// Re-Updating the Test case status to No Run as the attachments are getting
		// deleted automatically
		Run aResponseRun = updateRunStatus(aRestConnector, aALMWrapperConfigBean, aCreateResponseRun);
		List<File> lstALMAttachments = aALMWrapperConfigBean.getALMAttachments();
		for (File aALMFile : lstALMAttachments) {
			if (!hasAttachmentUpload(aRestConnector, aResponseRun, aALMWrapperConfigBean, aALMFile)) {
				// ReSetting Status To No RUn
//				aALMWrapperConfigBean.setALMTestCaseStatus(Run.STATUS_NO_RUN);
//				updateRunStatus(aRestConnector, aALMWrapperConfigBean, aResponseRun);
				return null;
			}
		}
		return aResponseRun;
	}

	public static Run updateRunStatus(RestConnector aRestConnector, ALMWrapperConfigBean aALMWrapperConfigBean,
			Run aTestEntityRun) throws Exception {
		String runUrl = aRestConnector.buildEntityUrl("run", aTestEntityRun.id());
		aTestEntityRun.removeField("parent-id");
		aTestEntityRun.removeField("status");
		aTestEntityRun.clearBeforeUpdate();
		aTestEntityRun.status(aALMWrapperConfigBean.getALMExecutionStatus());
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Accept", "application/xml");
		requestHeaders.put("Content-Type", "application/xml");
		// String cycle = "login-form-required=y";
		String strReq = EntityMarshallingUtils.unmarshal(Run.class, aTestEntityRun);
		byte[] reqBytes = strReq.getBytes();
		Response aResponse = aRestConnector.httpPut(runUrl, reqBytes, requestHeaders);
		if (aResponse.getStatusCode() != HttpURLConnection.HTTP_OK
				&& aResponse.getStatusCode() != HttpURLConnection.HTTP_CREATED) {
			throw new IOException(AppUtils.formatMessage(ErrorMsgConstants.ERR_ALM_INVALID_RUN_ID,
					aALMWrapperConfigBean.getOriginalScenarioName()));
		}
		String strResponse = aResponse.toString();
		Run aResPonseRun = EntityMarshallingUtils.marshal(Run.class, strResponse);
		return aResPonseRun;
	}

	public static boolean hasAttachmentUpload(RestConnector aRestConnector, Run aRun,
			ALMWrapperConfigBean aALMWrapperConfigBean, File aFile) throws Exception {
		try {
			String attachmentsUrl = aRestConnector.buildEntityUrl("run", aRun.id()) + "/attachments";
			Map<String, String> requestHeaders = new HashMap<String, String>();
			requestHeaders.put("Accept", "application/xml");
			requestHeaders.put("Content-Type", "application/octet-stream");
			requestHeaders.put("Slug", aFile.getName());
			String cycle = "login-form-required=y";
			byte[] aFileBytes = Files.readAllBytes(aFile.toPath());
			Response aResponse = aRestConnector.httpPost(attachmentsUrl, cycle, aFileBytes, requestHeaders);
			if (aResponse.getStatusCode() != HttpURLConnection.HTTP_OK
					&& aResponse.getStatusCode() != HttpURLConnection.HTTP_CREATED) {
				throw new IOException(
						AppUtils.formatMessage(ErrorMsgConstants.ERR_ALM_INVALID_ATTACHMENT, aFile.getName(),
								aRun.testInstanceId(), aRun.id(), aALMWrapperConfigBean.getOriginalScenarioName()));
			}
			Attachment aALMAttachment = EntityMarshallingUtils.marshal(Attachment.class, aResponse.toString());
			if (!StringUtils.equalsIgnoreCase(aALMAttachment.name(), aFile.getName())) {
				throw new IOException(
						AppUtils.formatMessage(ErrorMsgConstants.ERR_ALM_INVALID_ATTACHMENT, aFile.getName(),
								aRun.testInstanceId(), aRun.id(), aALMWrapperConfigBean.getOriginalScenarioName()));
			}
			return true;
		} catch (Exception ex) {
			// ReSetting Status To No Run when Attachment Upload Fails
			aALMWrapperConfigBean.setALMExecutionStatus(Run.STATUS_NO_RUN);
			updateRunStatus(aRestConnector, aALMWrapperConfigBean, aRun);
			throw ex;
		}
	}

	public static LinkedList<ALMWrapperConfigBean> getAlmWrapperConfigBean(File aALMConfig) throws Exception {
		LinkedList<ALMWrapperConfigBean> lstALMAlmWrapperConfigBean = new LinkedList<>();
		if (!aALMConfig.exists()) {
			throw new IOException(MessageFormat.format(ErrorMsgConstants.FILENTFOUND, aALMConfig.getName()));
		}
		String strSheetName = RunTimeDataConstants.AML_CONFIG_SHEET_NAME;
		try (XSSFWorkbook aWorkbook = ExcelUtils.getWorkBook(aALMConfig)) {
			FormulaEvaluator evaluator = aWorkbook.getCreationHelper().createFormulaEvaluator();
			Sheet aControllerSheet = aWorkbook.getSheet(strSheetName);
			if (aControllerSheet == null) {
				throw new IOException(
						MessageFormat.format(ErrorMsgConstants.SHEET_NOT_FOUND, strSheetName, aALMConfig.getPath()));
			}

			LinkedHashMap<String, Integer> colMapByName = ExcelUtils.getColumnNames(aALMConfig, aControllerSheet,
					strSheetName);
			// Loop through the ExportTables sheet to retrieve DBConFig
			for (int iRow = 1; iRow <= aControllerSheet.getLastRowNum() - aControllerSheet.getFirstRowNum(); iRow++) {
				Row row = ExcelUtils.getRow(aControllerSheet, iRow);
				if (row == null) {
					throw new Exception(MessageFormat.format(ErrorMsgConstants.ERR_ROW, iRow + 1, strSheetName));
				}
				String originalScenarioName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row,
						colMapByName.getOrDefault(SummaryReportConstants.SECNARIO_HEADER, 0));
				String strBrowserDisplayName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row,
						colMapByName.getOrDefault(SummaryReportConstants.BROWSER_HEADER, 1));
				String strUrl = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row,
						colMapByName.getOrDefault("ALM URL", 2));
				String strLoginID = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row,
						colMapByName.getOrDefault("UserName", 3));
				String strALMClientId = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row,
						colMapByName.getOrDefault("ALM ClientId", 4));
				String strALMClientSecret = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row,
						colMapByName.getOrDefault("ALM Client Secret", 5));
				String strDomain = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row,
						colMapByName.getOrDefault("Domain", 6));
				String strProject = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row,
						colMapByName.getOrDefault("Project", 7));
				String strTestSetID = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row,
						colMapByName.getOrDefault("TestSetID", 8), false);
				String strTestSetPath = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row,
						colMapByName.getOrDefault("TestSetPath", 9), false);
				String strTSName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row,
						colMapByName.getOrDefault("TestSetName", 10), false);
				String strTCPrefix = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row,
						colMapByName.getOrDefault("TestCasePrefix", 11));
				String strRunName = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row,
						colMapByName.getOrDefault("RunName", 12));
				String strStatus = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row,
						colMapByName.getOrDefault("ExecutionStatus", 13));
				String strALMAttachment = ExcelUtils.getStringValue(evaluator, aControllerSheet, strSheetName, row,
						colMapByName.getOrDefault("ALM Attachment(s)", 14));
				if (StringUtils.isEmpty(StringUtils.trim(strTestSetPath))
						|| StringUtils.isEmpty(StringUtils.trim(strTestSetID))
						|| StringUtils.isEmpty(StringUtils.trim(strTSName))) {
					continue;
				}
				String[] strALMAttachments = StringUtils.split(strALMAttachment, AppConstants.SEPARATOR_SEMICOLON);
				List<File> lstALMAttachments = new ArrayList<>();
				if (strALMAttachments != null) {
					for (String strALMReportAttachment : strALMAttachments) {
						File aALMReportAttachment = AppUtils.getFileFromPath(strALMReportAttachment);
						if (aALMReportAttachment.exists()) {
							lstALMAttachments.add(aALMReportAttachment);
						}
					}
				}
				ALMWrapperConfigBean almWrapperConfigBean = new ALMWrapperConfigBean();
				almWrapperConfigBean.setOriginalScenarioName(originalScenarioName);
				almWrapperConfigBean.setBrowserDisplayName(strBrowserDisplayName);
				almWrapperConfigBean.setALMURL(strUrl);
				almWrapperConfigBean.setALMUserName(strLoginID);
				almWrapperConfigBean.setALMClientId(strALMClientId);
				almWrapperConfigBean.setALMClientSecret(strALMClientSecret);
				almWrapperConfigBean.setALMDomain(strDomain);
				almWrapperConfigBean.setALMProject(strProject);
				almWrapperConfigBean.setALMTestSetID(strTestSetID);
				almWrapperConfigBean.setALMTestSetPath(strTestSetPath);
				almWrapperConfigBean.setALMTestSetName(strTSName);
				almWrapperConfigBean.setALMTestCasePrefix(strTCPrefix);
				almWrapperConfigBean.setALMRunName(strRunName);
				almWrapperConfigBean.setALMExecutionStatus(strStatus);
				almWrapperConfigBean.setALMAttachments(lstALMAttachments);
				lstALMAlmWrapperConfigBean.add(almWrapperConfigBean);
			}
		}
		return lstALMAlmWrapperConfigBean;
	}
}
