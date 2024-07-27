/****************************************************************************
 * File Name 		: DocumentTestStepReport.java
 * Package			: com.dxc.zurich.reports
 * Author			: pmusunuru2
 * Creation Date	: Jul 16, 2021
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

import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor;

import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.beans.TestSuiteBean;
import com.abc.project.config.AppConfig;
import com.abc.project.config.AppContext;
import com.abc.project.config.MasterConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.ErrorMsgConstants;
import com.abc.project.enums.StartFinish;
import com.abc.project.utils.AppUtils;
import com.abc.project.utils.ClearSessions;
import com.abc.project.utils.PropertyHandler;

/**
 * @author pmusunuru2
 * @since Jul 16, 2021 10:03:53 am
 */
public class DocumentTestStepReport {

	private static final Logger LOGGER = LogManager.getLogger(DocumentTestStepReport.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	private static File getScreenShotDocument() {
		java.sql.Date dtExecution = AppConfig.getInstance().getExecutionDate();
		String strReportDate = AppUtils.getDateAsString(dtExecution, AppConstants.EXEC_REPORTS_FOLDER_DT_FORMAT);
		String strFileName = String.format("%s_%s_Results%s", MasterConfig.getInstance().getAppName(), strReportDate,
				AppConstants.DOCUMENT_REPORT_EXTENSION);
		String strDocFileLocation = AppConfig.getInstance().getDOCReportFolder().getPath();
		File aScreenShotDoc = Paths.get(strDocFileLocation, strFileName).toFile();
		if (!aScreenShotDoc.getParentFile().exists()) {
			aScreenShotDoc.getParentFile().mkdirs();
		}
		return aScreenShotDoc;
	}

	private static boolean canCreateSnapShotDoc() {
		String strSysStatus = System.getProperty(AppConstants.GENERATE_DOC_REPORT_KEY);
		String strStatus = StringUtils.isEmpty(StringUtils.trim(strSysStatus)) ? PropertyHandler
				.getExternalString(AppConstants.GENERATE_DOC_REPORT_KEY, AppConstants.APP_PROPERTIES_NAME)
				: StringUtils.trim(strSysStatus);
		return BooleanUtils.toBoolean(strStatus);
	}

	private static XWPFDocument getXWPFDocument(File aFile) throws IOException {
		if (aFile.exists()) {
			try {
				ClearSessions.closeFileByName(aFile);
				return new XWPFDocument(new BufferedInputStream(new FileInputStream(aFile)));
			} catch (Exception e) {
				throw new IOException(AppUtils.formatMessage(ErrorMsgConstants.FILENTFOUND, aFile.getPath()));
			}
		}
		return new XWPFDocument();
	}

	private static XWPFParagraph getDefaultXWPFParagraph(XWPFDocument aDocxDocument) {
		XWPFParagraph aXWPFParagraph;
		aXWPFParagraph = aDocxDocument.createParagraph();
		aXWPFParagraph.setSpacingBetween(1);
		aXWPFParagraph.setKeepNext(true);
		CTOnOff state = CTOnOff.Factory.newInstance();
		state.setVal(STOnOff1.ON);
		aXWPFParagraph.getCTP().getPPr().setKeepLines(state);
		aXWPFParagraph.getCTP().getPPr().setWordWrap(state);
		return aXWPFParagraph;
	}

	public static synchronized void createDocScreenShots(BrowsersConfigBean aBrowsersConfigBean,
			TestSuiteBean aTestSuiteBean) {

		if (!canCreateSnapShotDoc() && !AppUtils.canCreateSnapShotScenarioDoc()) {
			return;
		}
		AppContext aAppContext = AppContext.getInstance();
		String testScenarioName = aTestSuiteBean.getScenarioName();
		String strDescription = StringUtils.isEmpty(aTestSuiteBean.getDescription()) ? testScenarioName : aTestSuiteBean.getDescription();
		LinkedHashMap<String, String> documentTestReportBean = aAppContext.getDocumentTestReportBean(testScenarioName, aBrowsersConfigBean);
		String strLogMessgae = AppUtils.formatMessage("Creating ScreenShot Document for Scenario {0} and browser {1}",
				testScenarioName, AppUtils.removeIllegalCharacters(aBrowsersConfigBean.getBrowserDisplayName(), true));
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessgae));
		try {
			File aScreenShotFolder = TestStepReport.getSceenShotFolder(aBrowsersConfigBean, testScenarioName);
			if (aScreenShotFolder == null || !aScreenShotFolder.exists() || aScreenShotFolder.listFiles() == null
					|| aScreenShotFolder.listFiles().length <= 0) {
				return;
			}
			String strAPPENV = MasterConfig.getInstance().getAppENV();
			String strDate = AppUtils.getDateAsString(AppConfig.getInstance().getExecutionDate(), AppConstants.DATE_FORMAT_DD_MM_YYYY);
			String strFormatedDate = StringUtils.isEmpty(StringUtils.trim(strAPPENV)) ? strDate : String.format("%s in %s.", strDate,strAPPENV);
			String strHeaderText = String.format("Executed %s dated %s", testScenarioName,strFormatedDate);
			String strFileName = String.format("%s_%s_%s%s", MasterConfig.getInstance().getAppRunID(),
					AppUtils.getScenarioReportFileName(testScenarioName, 0), AppUtils.getFileDate(),
					AppConstants.DOCUMENT_REPORT_EXTENSION);
			File aScreenShotScenrioDoc = Paths.get(aScreenShotFolder.getParentFile().getAbsolutePath(), strFileName)
					.toFile();
			File aScreenShotDoc = AppUtils.canCreateSnapShotScenarioDoc() && !canCreateSnapShotDoc()
					? aScreenShotScenrioDoc
					: getScreenShotDocument();
			try (XWPFDocument aDocxDocument = getXWPFDocument(aScreenShotDoc)) {
				aDocxDocument.setZoomPercent(100);
				List<XWPFParagraph> lstParagraphs = aDocxDocument.getParagraphs();
				XWPFParagraph aBodyParagraph = lstParagraphs.stream()
						.filter(aParagraph -> StringUtils.containsIgnoreCase(aParagraph.getText(), strHeaderText))
						.findFirst().orElse(null);
				if (aBodyParagraph == null) {
					aBodyParagraph = getDefaultXWPFParagraph(aDocxDocument);
					aBodyParagraph.setAlignment(ParagraphAlignment.CENTER);
					XWPFRun aXWPFRun = aBodyParagraph.createRun();
					aXWPFRun.setBold(true);
					aXWPFRun.setCapitalized(true);
					aXWPFRun.setFontSize(10);
					aXWPFRun.setFontFamily("Arial");
					aXWPFRun.setText(strHeaderText);
					aXWPFRun.addBreak();
					XWPFParagraph aDescParagraph = getDefaultXWPFParagraph(aDocxDocument);
					aDescParagraph.getCTP().setPPr(aBodyParagraph.getCTP().getPPr());
					aDescParagraph.setAlignment(ParagraphAlignment.LEFT);
					XWPFRun aDescXWPFRun = aDescParagraph.createRun();
					aDescXWPFRun.getCTR().addNewRPr().addNewHighlight().setVal(STHighlightColor.YELLOW);
					aDescXWPFRun.setSmallCaps(true);
					aDescXWPFRun.setFontSize(8);
					aDescXWPFRun.setFontFamily("Arial");
					aDescXWPFRun.setText(strDescription);
					aDescXWPFRun.addBreak();
				}
				for (File aScreenShotFile : aScreenShotFolder.listFiles()) {
					Dimension aImageDim = AppUtils.getImageDimension(aScreenShotFile);
					double width = aImageDim.getWidth();
					double height = aImageDim.getHeight();
					double scaling = 1.0;
					if (width > Units.POINT_DPI * Units.DEFAULT_CHARACTER_WIDTH) {
						scaling = (Units.POINT_DPI * Units.DEFAULT_CHARACTER_WIDTH) / width;
					}
					XWPFParagraph aImageParagraph = getDefaultXWPFParagraph(aDocxDocument);
					aImageParagraph.getCTP().setPPr(aBodyParagraph.getCTP().getPPr());
					aImageParagraph.setAlignment(ParagraphAlignment.LEFT);
					XWPFRun aImageXWPFRun = aImageParagraph.createRun();
					aImageXWPFRun.setBold(false);
					aImageXWPFRun.setSmallCaps(true);
					aImageXWPFRun.setFontFamily("Arial");
					aImageXWPFRun.setFontSize(8);
					String strScreenMsg = StringUtils.isEmpty(documentTestReportBean.get(aScreenShotFile.getName())) ? FilenameUtils.getBaseName(aScreenShotFile.getName()):
						documentTestReportBean.get(aScreenShotFile.getName());
					aImageXWPFRun.setText(strScreenMsg);
					try (InputStream pictureData = new BufferedInputStream(new FileInputStream(aScreenShotFile))) {
						aImageXWPFRun.addPicture(pictureData, XWPFDocument.PICTURE_TYPE_PNG, aScreenShotFile.getPath(),
								Units.toEMU(width * scaling), Units.toEMU(height * scaling));
					}
					aImageXWPFRun.addBreak();
				}
				aBodyParagraph.setPageBreak(true);
				ClearSessions.closeFileByName(aScreenShotDoc);
				try (BufferedOutputStream outputStream = new BufferedOutputStream(
						new FileOutputStream(aScreenShotDoc))) {
					aDocxDocument.write(outputStream);
					outputStream.flush();
				}
			}
		} catch (Throwable th) {
			String strErrorMessage = AppUtils.formatMessage("Error While {0}", strLogMessgae);
			LOGGER.error(strErrorMessage);
			ERROR_LOGGER.error(strErrorMessage, th);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessgae));
		}
	}
}
