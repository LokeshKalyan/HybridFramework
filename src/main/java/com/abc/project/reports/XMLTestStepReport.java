/****************************************************************************
 * File Name 		: XMLTestStepReport.java
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
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.beans.XMLReportBean;
import com.abc.project.config.AppConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.ErrorMsgConstants;
import com.abc.project.enums.Browsers;
import com.abc.project.utils.AppUtils;
import com.abc.project.utils.PropertyHandler;
import com.aventstack.extentreports.Status;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 12:58:16 pm
 */
public class XMLTestStepReport {

	private static Map<String, List<XMLReportBean>> xmlReportMap = new HashMap<>();

	private static final Logger LOGGER = LogManager.getLogger(XMLTestStepReport.class);
	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	public static synchronized void flushReport(BrowsersConfigBean aBrowsersConfigBean, String strScenarioName) {
		Browsers aBrowser = aBrowsersConfigBean.getBrowser();
		String strBrowserName = aBrowsersConfigBean.getReportBrowserName();
		List<XMLReportBean> lstXmlReport = xmlReportMap.containsKey(strBrowserName) ? xmlReportMap.get(aBrowsersConfigBean.getBrowserDisplayName())
				: new ArrayList<>();
		if (CollectionUtils.isEmpty(lstXmlReport)) {
			return;
		}
		File aXmlExtentFile = new File(getExtentReportFileName(aBrowsersConfigBean));
		if (!aXmlExtentFile.getParentFile().exists()) {
			aXmlExtentFile.getParentFile().mkdirs();
		}
		try (FileOutputStream aFileOutputStream = new FileOutputStream(aXmlExtentFile)) {

			Document document = getXmlDocument(strBrowserName, lstXmlReport);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(aFileOutputStream);
			transformer.transform(domSource, streamResult);
		} catch (Exception ex) {
			String strMessage = "";
			if (StringUtils.isEmpty(strScenarioName)) {
				strMessage = MessageFormat.format(ErrorMsgConstants.ERR_REPORT_GENERATION, aBrowser.getBrowserName());
			} else {
				strMessage = MessageFormat.format(ErrorMsgConstants.ERR_SCN_REPORT_GENERATION, strScenarioName,
						aBrowser.getBrowserName());
			}
			LOGGER.error(strMessage);
			ERROR_LOGGER.error(strMessage, ex);
		}
	}

	public static synchronized void flushXmlReport() {

		String strGenerateTestReport = PropertyHandler.getExternalString(AppConstants.GENERATE_XML_REPORT_KEY,
				AppConstants.APP_PROPERTIES_NAME);

		if (!StringUtils.equalsIgnoreCase(strGenerateTestReport, "Y")) {
			return;
		}
		String strRootElementName = PropertyHandler.getExternalString(AppConstants.XML_REPORT_CONSILIDATED_ROOT_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		LinkedList<XMLReportBean> lstXmlReport = new LinkedList<>();
		xmlReportMap.entrySet().forEach(aXmlReportEntry -> lstXmlReport.addAll(aXmlReportEntry.getValue()));
		if (CollectionUtils.isEmpty(lstXmlReport)) {
			return;
		}
		File aXmlExtentFile = new File(getConsolidatedReportFileName());
		if (!aXmlExtentFile.getParentFile().exists()) {
			aXmlExtentFile.getParentFile().mkdirs();
		}
		try (FileOutputStream aFileOutputStream = new FileOutputStream(aXmlExtentFile)) {
			Document document = getXmlDocument(strRootElementName, lstXmlReport);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(aFileOutputStream);
			transformer.transform(domSource, streamResult);
		} catch (Exception ex) {
			String strMessage = MessageFormat.format(ErrorMsgConstants.ERR_REPORT_GENERATION, strRootElementName);
			LOGGER.error(strMessage);
			ERROR_LOGGER.error(strMessage, ex);
		}
	}

	private static Document getXmlDocument(String strRootElementName, List<XMLReportBean> lstXmlReport)
			throws Exception {
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		Element rootElement = document.createElement(AppUtils.removeIllegalCharacters(strRootElementName, true));
		document.appendChild(rootElement);

		for (XMLReportBean aXmlReportBean : lstXmlReport) {
			Element aTestCaseID = document.createElement("TLID");
			rootElement.appendChild(aTestCaseID);
			Attr attrTlid = document.createAttribute("tlid");
			attrTlid.setValue(AppUtils.removeIllegalCharacters(aXmlReportBean.getTestCaseID(), true));
			aTestCaseID.setAttributeNode(attrTlid);

			Element aBrowserName = document.createElement("BrowserName");
			aBrowserName.appendChild(document.createCDATASection(aXmlReportBean.getBrowserName()));
			aTestCaseID.appendChild(aBrowserName);

			Element aTestCaseDescription = document.createElement("TestCaseDescription");
			aTestCaseDescription.appendChild(document.createCDATASection(aXmlReportBean.getTestCaseDescription()));
			aTestCaseID.appendChild(aTestCaseDescription);

			Element aMessage = document.createElement("LogMessage");
			aMessage.appendChild(document.createCDATASection(aXmlReportBean.getLogMessage()));
			aTestCaseID.appendChild(aMessage);

			Element aStatus = document.createElement("Status");
			aStatus.appendChild(document.createCDATASection(aXmlReportBean.getStatus()));
			aTestCaseID.appendChild(aStatus);
		}

		return document;
	}

	/***
	 * Creates report file name configured in application properties
	 * 
	 * @param strBrowserName
	 * @return
	 */
	private static String getExtentReportFileName(BrowsersConfigBean aBrowsersConfigBean) {
		String strReportFolder = AppConfig.getInstance().getBrowserExecutionReportFolder(aBrowsersConfigBean);
		String strReportName = PropertyHandler.getExternalString(AppConstants.XML_REPORT_FILENAME_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		String strBrowserName = aBrowsersConfigBean.getReportBrowserName();
		String strXmlReportName = String.format("%s_%s", strBrowserName, strReportName);
		String strBaseName = FilenameUtils.getBaseName(strXmlReportName);
		String strExtension = FilenameUtils.getExtension(strXmlReportName);
		strXmlReportName = String.format(AppConstants.REPORT_FILE_NAME_FORMAT, AppUtils.removeIllegalCharacters(strBaseName, true),strExtension);
		return Paths.get(strReportFolder, strXmlReportName).toString();
	}

	private static String getConsolidatedReportFileName() {
		String strXmlReportFile = AppConfig.getInstance().getXMLReportFolder().getPath();
		String strReportName = PropertyHandler.getExternalString(AppConstants.XML_REPORT_FILENAME_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		String strXMLReportName = String.format("%s%s", AppConstants.XML_FILE_PREFIX, strReportName);
		String strBaseName = FilenameUtils.getBaseName(strXMLReportName);
		String strExtension = FilenameUtils.getExtension(strXMLReportName);
		strXMLReportName = String.format(AppConstants.REPORT_FILE_NAME_FORMAT, AppUtils.removeIllegalCharacters(strBaseName, true),strExtension);
		return Paths.get(strXmlReportFile, strXMLReportName).toString();
	}

	public static synchronized void logXMLReport(Status aStatus, BrowsersConfigBean aBrowsersConfigBean, String testScenarioName,
			String stepDescription, String strLogMessage) {
		String strGenerateTestReport = PropertyHandler.getExternalString(AppConstants.GENERATE_XML_REPORT_KEY,
				AppConstants.APP_PROPERTIES_NAME);

		if (!StringUtils.equalsIgnoreCase(strGenerateTestReport, "Y")) {
			return;
		}
		String strBrowserName = aBrowsersConfigBean.getReportBrowserName();
		List<XMLReportBean> lstXmlReport = xmlReportMap.get(strBrowserName);
		if (lstXmlReport == null || lstXmlReport.isEmpty()) {
			lstXmlReport = new ArrayList<>();
		}
		XMLReportBean aXmlReport = new XMLReportBean();
		aXmlReport.setTestCaseID(testScenarioName);
		aXmlReport.setBrowserName(strBrowserName);
		aXmlReport.setTestCaseDescription(stepDescription);
		aXmlReport.setLogMessage(strLogMessage);
		aXmlReport.setStatus(aStatus);
		lstXmlReport.add(aXmlReport);
		xmlReportMap.put(strBrowserName, lstXmlReport);
	}
}
