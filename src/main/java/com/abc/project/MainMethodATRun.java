/****************************************************************************
 * File Name 		: ZurichATRun.java
 * Package			: com.dxc.zurich
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
package com.abc.project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abc.project.base.PriorityJobScheduler;
import com.abc.project.beans.AppEnvConfigBean;
import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.beans.TestSuiteBean;
import com.abc.project.config.AppConfig;
import com.abc.project.config.AppContext;
import com.abc.project.config.AppPriorityConfig;
import com.abc.project.config.MasterConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.enums.AppRunMode;
import com.abc.project.enums.BrowserType;
import com.abc.project.enums.Browsers;
import com.abc.project.enums.StartFinish;
import com.abc.project.grid.RemoteMultiPlatformServerHelper;
import com.abc.project.grid.ServerHelper;
import com.abc.project.reports.TestStepReport;
import com.abc.project.reports.XMLTestStepReport;
import com.abc.project.runners.BrowserRunner;
import com.abc.project.runners.DeskTopWebBrowserRunner;
import com.abc.project.runners.MobileWebBrowserRunner;
import com.abc.project.runners.ServerRunner;
import com.abc.project.utils.AppUtils;
import com.abc.project.utils.PropertyHandler;
import com.dxc.enums.ExecutionStatus;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 10:53:27 am
 */
public class MainMethodATRun {

	static {
		ch.qos.logback.classic.LoggerContext loggerContext = (ch.qos.logback.classic.LoggerContext)org.slf4j.LoggerFactory.getILoggerFactory();
		ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger("io.netty");
		rootLogger.setLevel(ch.qos.logback.classic.Level.OFF);
		ch.qos.logback.classic.Logger asyncLogger = loggerContext.getLogger("org.asynchttpclient");
		asyncLogger.setLevel(ch.qos.logback.classic.Level.OFF);
		ch.qos.logback.classic.Logger apacheLogger = loggerContext.getLogger("org.apache");
		apacheLogger.setLevel(ch.qos.logback.classic.Level.OFF);
		ch.qos.logback.classic.Logger gitHubLogger = loggerContext.getLogger("io.github");
		gitHubLogger.setLevel(ch.qos.logback.classic.Level.OFF);
		ch.qos.logback.classic.Logger mongodbLogger = loggerContext.getLogger("org.mongodb");
		mongodbLogger.setLevel(ch.qos.logback.classic.Level.OFF);
		ch.qos.logback.classic.Logger ePamhealenium = loggerContext.getLogger("com.epam");
		ePamhealenium.setLevel(ch.qos.logback.classic.Level.OFF);
		ch.qos.logback.classic.Logger healenium = loggerContext.getLogger("healenium");
		healenium.setLevel(ch.qos.logback.classic.Level.OFF);
	}
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(MainMethodATRun.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Set Flag to Start
		MasterConfig aMasterConfig = MasterConfig.getInstance();
		AppPriorityConfig aAppPriorityConfig = AppPriorityConfig.getInstance();
		updateRunStatus(AppConstants.APP_RUN_START_STATUS, new Date(), null);
		LinkedHashSet<String> stEnvName = aAppPriorityConfig.getAppPriority(args);
		// Do Loop For Each App name
		for (String strEnvName : stEnvName) {
			LOGGER.info(StartFinish.APP_START_BANNER.getDefaultFormattedMsg(strEnvName));
			AppConfig appConfig = AppConfig.getInstance();
			AppContext appContext = AppContext.getInstance();
			boolean canAppExist = false;
			aAppPriorityConfig.setAppName(args, strEnvName);
			AppEnvConfigBean aPPRunEnv = aMasterConfig.getAppEnvConfigBean();
			try {
				loadConfig(aPPRunEnv, args);
				AppRunMode aAppRunMode = aPPRunEnv.getAppRunMode();
				switch (aAppRunMode) {
				case SELENIUM_SERVER:
					ServerRunner aServerRunner = ServerRunner.getInstance();
					aServerRunner.startServer();
					canAppExist = false;
					break;
				default:
					canAppExist = true;
					break;
				}
				List<BrowsersConfigBean> lstBrowers = appConfig.getFilteredBrowserConFigs();
				try {
					runJobs(lstBrowers);
				} finally {
					if (aMasterConfig.canReRunFailedTestCases()) {
						runJobs(lstBrowers);
					}
				}
				if (AppRunMode.MUTLI_GRID_PLATFORM == aAppRunMode) {
					RemoteMultiPlatformServerHelper.getInstance().updateHostStatus(ExecutionStatus.COMPLETED);
				}
			} catch (Throwable ex) {
				LOGGER.error(StringUtils.isEmpty(ex.getMessage()) ? ex.toString() : ex.getMessage());
				ERROR_LOGGER.error(" UnCaught Exception.", ex);
			} finally {
				if (!canAppExist) {
					List<TestSuiteBean> lstTestSuiteData = appConfig.getFilteredControllerSuiteByStatus();
					while (!CollectionUtils.isEmpty(lstTestSuiteData)) {
						try {
							lstTestSuiteData.stream().forEach(aConfig -> {
								String strTestDataLogMessage = AppUtils.formatMessage(
										"Waiting for scenario {0} browser {1} to complete in machine ",
										aConfig.getScenarioName(), aConfig.getBrowserDisplayName(),
										aConfig.getHostAddress());
								LOGGER.info(strTestDataLogMessage);
							});

							Thread.sleep(AppConstants.SERVER_DATA_READ_WAIT_TIME);
							lstTestSuiteData = appConfig.getFilteredControllerSuiteByStatus();
						} catch (Exception e) {
						}
					}
				}
				ServerHelper.getInstance().closeAllCommandLineProcess();
				appConfig.stopBrowserStackLocalInstances();
				AppUtils.emailExecutionReports();
				XMLTestStepReport.flushXmlReport();
				String strMessage = AppUtils.getExecutionTableFormat();
				LOGGER.info(
						AppUtils.formatMessage("{0} - Execution Status \r\n{1}", aPPRunEnv.getAppName(), strMessage));
				LOGGER.info(StartFinish.APP_END_BANNER.getDefaultFormattedMsg(strEnvName));
				TestStepReport.clearTestStepReport();
				aMasterConfig.clearAppEnvConfigBean();
				appConfig.initializeAppConfig();
				appContext.initializeContextData();
			}
		}
		// Set Flag to finish
		updateRunStatus(AppConstants.APP_RUN_FINISH_STATUS, new Date(), new Date());
		System.exit(0);// Exists When APP Completes Execution
	}

	private static void runJobs(List<BrowsersConfigBean> lstBrowers) throws Exception {
		PriorityJobScheduler aJobScheduler = new PriorityJobScheduler();
		try {
			for (BrowsersConfigBean aBrowsersConfigBean : lstBrowers) {
				Browsers aBrowsers = aBrowsersConfigBean.getBrowser();
				BrowserRunner aBrowserRunner = null;
				BrowserType aBrowserType = aBrowsers.getBrowserType();
				switch (aBrowserType) {
				case MOBILE_NATIVE:
				case MOBILE_WEB:
					aBrowserRunner = new MobileWebBrowserRunner(aBrowsersConfigBean);
					break;
				default:
					aBrowserRunner = new DeskTopWebBrowserRunner(aBrowsersConfigBean);
					break;
				}

				aJobScheduler.addJobstoQue(aBrowserRunner);
			}
			aJobScheduler.scheduleJob();
			aJobScheduler.waitQueueToComplete();
		} finally {
			aJobScheduler.shutdownExecutorService();
		}
	}

	private static void updateRunStatus(int iRunStatus, Date dtStartDate, Date dtEndDate) {
		StringBuilder strText = new StringBuilder();
		strText.append(iRunStatus).append(AppConstants.SEPARATOR_COMMA)
				.append(AppUtils.getDateAsString(new Date(), AppConstants.EXEC_DATEFORMAT));
		if (dtEndDate != null) {
			strText.append(AppConstants.SEPARATOR_COMMA)
					.append(AppUtils.getDateAsString(new Date(), AppConstants.EXEC_DATEFORMAT));
		}
		File aRunFile = AppUtils.getFileFromPath(
				PropertyHandler.getExternalString(AppConstants.APP_RUN_STATUS_KEY, AppConstants.APP_PROPERTIES_NAME));
		if (iRunStatus == AppConstants.APP_RUN_START_STATUS && aRunFile.exists()) {
			aRunFile.delete();
		}
		try (BufferedWriter aBufferedWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(aRunFile), StandardCharsets.UTF_8));) {
			aBufferedWriter.write(strText.toString());
		} catch (Exception ex) {
			LOGGER.error(StringUtils.isEmpty(ex.getMessage()) ? ex.toString() : ex.getMessage());
			ERROR_LOGGER.error("Error While Updating RunStatus:-", ex);
		}
	}

	private static void loadConfig(AppEnvConfigBean aPPRunEnv, String[] args) {
		LOGGER.info(StartFinish.START.getFormattedMsg("loadConfig"));
		try {
			mergeConfigProperties(aPPRunEnv);
			AppConfig appConfig = AppConfig.getInstance();
			appConfig.setAppVmArgs(aPPRunEnv, args);
			appConfig.loadConfig();
		} catch (Exception ex) {
			LOGGER.error(StringUtils.isEmpty(ex.getMessage()) ? ex.toString() : ex.getMessage());
			ERROR_LOGGER.error(" UnCaught Exception.While Loading Config ", ex);
			System.exit(0); // Exception case and application cannot proceed so just simply exit by logging
			// information in above line.
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg("loadConfig"));
		}
	}

	private static void mergeConfigProperties(AppEnvConfigBean aPPRunEnv) throws Exception {
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg("mergeConfigProperties"));
			if (aPPRunEnv == null) {
				String strAPPENV = MasterConfig.getInstance().getAppName();
				LOGGER.error(String.format("Please Modify UnKnown ENV :- %s and rerun application", strAPPENV));
				System.exit(0); // Exception case and application cannot proceed so just simply exit by logging
			}
			String strConfigPopFile = String.format("%s%s%s", AppConstants.APP_ENV_PROPERTIES_LOC,
					aPPRunEnv.getPropertyName(), AppConstants.PROPERTIES_FILE_SUFFIX);
			PropertyHandler.mergeExternalResourceBundle(strConfigPopFile, AppConstants.APP_PROPERTIES_NAME);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg("mergeConfigProperties"));
		}
	}

}
