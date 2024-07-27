/****************************************************************************
 * File Name 		: RemoteMultiPlatformServerHelper.java
 * Package			: com.dxc.zurich.grid
 * Author			: pmusunuru2
 * Creation Date	: Jul 09, 2021
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.grid;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abc.project.beans.AppEnvConfigBean;
import com.abc.project.beans.TestSuiteBean;
import com.abc.project.config.AppConfig;
import com.abc.project.config.MasterConfig;
import com.abc.project.constants.SummaryReportConstants;
import com.abc.project.enums.StartFinish;
import com.abc.project.utils.AppUtils;
import com.dxc.base.RemoteMultiPlatformClient;
import com.dxc.constants.RemoteMultiPlatformConstants;
import com.dxc.dto.HostDTO;
import com.dxc.dto.QuatationSummaryDTO;
import com.dxc.dto.ReferenceNumberDTO;
import com.dxc.dto.RuntimeDataDTO;
import com.dxc.dto.ScenarioDTO;
import com.dxc.dto.embededid.HostEmbeddedDTO;
import com.dxc.dto.embededid.ScenarioEmbeddedDTO;
import com.dxc.enums.ExecutionStatus;
import com.dxc.utils.RemoteMultiPlatformUtils;

/**
 * @author pmusunuru2
 * @since Jul 09, 2021 11:48:20 am
 */
public class RemoteMultiPlatformServerHelper {

	private static final Logger LOGGER = LogManager.getLogger(RemoteMultiPlatformServerHelper.class);

	private static RemoteMultiPlatformServerHelper instance;
	private AppEnvConfigBean aPPRunEnv;

	private RemoteMultiPlatformServerHelper() {
		aPPRunEnv = MasterConfig.getInstance().getAppEnvConfigBean();
	}

	public static RemoteMultiPlatformServerHelper getInstance() {
		if (null == instance) {
			synchronized (RemoteMultiPlatformServerHelper.class) {

				if (null == instance) {
					instance = new RemoteMultiPlatformServerHelper();
				}
			}
		}
		return instance;
	}

	private HostDTO getDefautHostDTO() {
		HostEmbeddedDTO hostEmbeddedDTO = new HostEmbeddedDTO();
		hostEmbeddedDTO.setHostName(RemoteMultiPlatformUtils.getHostName());
		hostEmbeddedDTO.setApplicationName(aPPRunEnv.getAppName());
		hostEmbeddedDTO.setHostPriority(aPPRunEnv.getPriority().getHostPriority());
		HostDTO aHostDTO = new HostDTO();
		aHostDTO.setHostID(hostEmbeddedDTO);
		aHostDTO.setUpdatedBy(AppUtils.getSystemUserName());
		return aHostDTO;
	}

	public void registerHost() throws Exception {
		HostDTO aDefaultHostDTO = getDefautHostDTO();
		HostEmbeddedDTO hostEmbeddedDTO = aDefaultHostDTO.getHostID();
		String strLogMessgae = AppUtils.formatMessage("Register Host {0}", hostEmbeddedDTO.getHostName());
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessgae));
		try {
			HostDTO aHostDTO = RemoteMultiPlatformClient.registerHost(aPPRunEnv.getHostAddress(),
					aPPRunEnv.getHostPort(), aDefaultHostDTO);
			LOGGER.info(AppUtils.formatMessage("Sucessfully Register Host {0}", aHostDTO.getHostID().getHostName()));
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessgae));
		}
	}

	public void updateHostStatus(ExecutionStatus aExecutionStatus) throws Exception {
		HostDTO aDefaultHostDTO = getDefautHostDTO();
		aDefaultHostDTO.setStatus(aExecutionStatus.getStatus());
		HostEmbeddedDTO hostEmbeddedDTO = aDefaultHostDTO.getHostID();
		String strLogMessgae = AppUtils.formatMessage("Updating Host {0} Status {1}", hostEmbeddedDTO.getHostName(),
				aExecutionStatus);
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessgae));
		try {
			HostDTO aHostDTO = RemoteMultiPlatformClient.updateHostStatus(aPPRunEnv.getHostAddress(),
					aPPRunEnv.getHostPort(), aDefaultHostDTO);
			LOGGER.info(AppUtils.formatMessage("Sucessfully Updated Host {0} Status {1}",
					aHostDTO.getHostID().getHostName(), aHostDTO.getStatus()));
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessgae));
		}
	}

	public LinkedList<TestSuiteBean> registerScenario(LinkedList<TestSuiteBean> lstDefaltTestSuiteBean)
			throws Exception {
		HostDTO aHostDTO = getDefautHostDTO();
		HostEmbeddedDTO hostEmbeddedDTO = aHostDTO.getHostID();
		final List<ScenarioDTO> lstRegisterScenario = new LinkedList<>();
		if (CollectionUtils.isNotEmpty(lstDefaltTestSuiteBean)) {
			lstDefaltTestSuiteBean.stream().forEach(aDefaultTestSuiteBean -> {
				ScenarioEmbeddedDTO aScenarioEmbeddedDTO = new ScenarioEmbeddedDTO();
				aScenarioEmbeddedDTO.setScenarioName(aDefaultTestSuiteBean.getScenarioName());
				aScenarioEmbeddedDTO.setBrowserName(aDefaultTestSuiteBean.getBrowserDisplayName());
				ScenarioDTO aScenario = new ScenarioDTO();
				aScenario.setScenarioID(aScenarioEmbeddedDTO);
				aScenario.setDescription(
						StringUtils.isEmpty(aDefaultTestSuiteBean.getDescription()) ? SummaryReportConstants.NO_RESULT
								: aDefaultTestSuiteBean.getDescription());
				aScenario.setTestDataSheet(aDefaultTestSuiteBean.getTestDataSheetName());
				lstRegisterScenario.add(aScenario);
			});
		}
		aHostDTO.setScenarioList(lstRegisterScenario);
		String strLogMessgae = AppUtils.formatMessage("Register Scenarios for Host {0}", hostEmbeddedDTO.getHostName());
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessgae));
		try {
			LinkedList<TestSuiteBean> lstScenarioData = new LinkedList<>();
			List<ScenarioDTO> lstScenarios = RemoteMultiPlatformClient.registerScenario(aPPRunEnv.getHostAddress(),
					aPPRunEnv.getHostPort(), aHostDTO);
			LOGGER.info(AppUtils.formatMessage("Sucessfully Scenarios for Register Host {0}",
					aHostDTO.getHostID().getHostName()));
			if (CollectionUtils.isEmpty(lstScenarios)) {
				return lstScenarioData;
			}
			lstScenarios.stream().forEach(aRegisteredScenario -> {
				TestSuiteBean aTestSuiteBean = new TestSuiteBean();
				HostDTO aRegiterHostDTO = aHostDTO;
				HostEmbeddedDTO aRegiterHostEmbeddedDTO = aRegiterHostDTO.getHostID();
				ScenarioEmbeddedDTO aScenarioEmbeddedDTO = aRegisteredScenario.getScenarioID();
				aTestSuiteBean.setBrowserDisplayName(aScenarioEmbeddedDTO.getBrowserName());
				aTestSuiteBean.setScenarioName(aScenarioEmbeddedDTO.getScenarioName());
				aTestSuiteBean.setDescription(aRegisteredScenario.getDescription());
				aTestSuiteBean.setTestDataSheetName(aRegisteredScenario.getTestDataSheet());
				aTestSuiteBean.setHostAddress(aRegiterHostEmbeddedDTO.getHostName());
				aTestSuiteBean.setRunFlag(true);
				aTestSuiteBean.setStatus(aRegisteredScenario.getStatus());
				lstScenarioData.add(aTestSuiteBean);
			});
			return lstScenarioData;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessgae));
		}
	}

	public boolean hasScenarioAssigned(TestSuiteBean aTestSuiteBean) throws Exception {
		HostDTO aHostDTO = getDefautHostDTO();
		HostEmbeddedDTO hostEmbeddedDTO = aHostDTO.getHostID();
		ScenarioEmbeddedDTO aScenarioEmbeddedDTO = new ScenarioEmbeddedDTO();
		aScenarioEmbeddedDTO.setScenarioName(aTestSuiteBean.getScenarioName());
		aScenarioEmbeddedDTO.setBrowserName(aTestSuiteBean.getBrowserDisplayName());
		ScenarioDTO aScenario = new ScenarioDTO();
		aScenario.setScenarioID(aScenarioEmbeddedDTO);
		aScenario.setHost(aHostDTO);
		aScenario.setUpdatedBy(AppUtils.getSystemUserName());
		String strLogMessgae = AppUtils.formatMessage("Checking Scenarios status for Host {0}",
				hostEmbeddedDTO.getHostName());
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessgae));
		try {
			ScenarioDTO aScenarioDTO = RemoteMultiPlatformClient.hasScenarioAssigned(aPPRunEnv.getHostAddress(),
					aPPRunEnv.getHostPort(), aScenario);
			ScenarioEmbeddedDTO aScenarioID = aScenarioDTO.getScenarioID();
			boolean bAssignStatus = StringUtils.equalsIgnoreCase(aTestSuiteBean.getScenarioName(),
					aScenarioID.getScenarioName())
					&& StringUtils.equalsIgnoreCase(aTestSuiteBean.getBrowserDisplayName(),
							aScenarioID.getBrowserName());
			LOGGER.info(
					AppUtils.formatMessage("Scenario {0} status {1}", aScenarioID.getScenarioName(), bAssignStatus));
			return bAssignStatus;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessgae));
		}
	}

	public void updateScenarioStatus(TestSuiteBean aTestSuiteBean, String strExecResult) throws Exception {
		HostDTO aHostDTO = getDefautHostDTO();
		HostEmbeddedDTO hostEmbeddedDTO = aHostDTO.getHostID();
		ScenarioEmbeddedDTO aScenarioEmbeddedDTO = new ScenarioEmbeddedDTO();
		aScenarioEmbeddedDTO.setScenarioName(aTestSuiteBean.getScenarioName());
		aScenarioEmbeddedDTO.setBrowserName(aTestSuiteBean.getBrowserDisplayName());
		ScenarioDTO aScenario = new ScenarioDTO();
		aScenario.setScenarioID(aScenarioEmbeddedDTO);
		aScenario.setHost(aHostDTO);
		aScenario.setUpdatedBy(AppUtils.getSystemUserName());
		String strLogMessgae = AppUtils.formatMessage("Updating Scenario {0} status {1} for Host {2}",
				aTestSuiteBean.getScenarioName(), strExecResult, hostEmbeddedDTO.getHostName());
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessgae));
		try {
			ScenarioDTO aScenarioDTO = RemoteMultiPlatformClient.hasScenarioAssigned(aPPRunEnv.getHostAddress(),
					aPPRunEnv.getHostPort(), aScenario);
			ExecutionStatus aExecutionStatus = ExecutionStatus.getExecutionStatusByStatus(aScenarioDTO.getStatus());
			if (aExecutionStatus == ExecutionStatus.STOPED) {
				return;
			}
			aScenario.setStatus(ExecutionStatus.getExecutionStatusByStatus(strExecResult).getStatus());
			ScenarioDTO aUpdateScenarioDTO = RemoteMultiPlatformClient.updateScenarioStatus(aPPRunEnv.getHostAddress(),
					aPPRunEnv.getHostPort(), aScenario);
			LOGGER.info(AppUtils.formatMessage("Sucessfully Updated Scenario {0} to status {1}",
					aUpdateScenarioDTO.getScenarioID().getScenarioName(), aUpdateScenarioDTO.getStatus()));
			AppConfig aAppConfig = AppConfig.getInstance();
			aAppConfig.updateScenarioExecutionStatus(aTestSuiteBean, aUpdateScenarioDTO.getStatus());
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessgae));
		}
	}

	public LinkedHashMap<String, LinkedHashMap<String, String>> getRunTimeValues(Logger aErrorLogger) {
		LinkedHashMap<String, LinkedHashMap<String, String>> runTimeDataMap = new LinkedHashMap<>();
		java.sql.Date executionDate = AppConfig.getInstance().getExecutionDate();
		String strExecutionDate = AppUtils.getDateAsString(executionDate,
				RemoteMultiPlatformConstants.DEFAULT_APP_DATE_FORMAT);
		String strLogMessgae = AppUtils.formatMessage("Fetching RuntimeData for Date {0}", strExecutionDate);
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessgae));
		try {
			List<RuntimeDataDTO> lstRuntimeData = RemoteMultiPlatformClient.getRunTimeData(aPPRunEnv.getHostAddress(),
					aPPRunEnv.getHostPort(), executionDate);
			for (RuntimeDataDTO aRuntimeDataDTO : lstRuntimeData) {
				ScenarioEmbeddedDTO runtimeID = aRuntimeDataDTO.getRuntimeID();
				LinkedHashMap<String, String> aDataBean = new LinkedHashMap<>();
				aDataBean.putAll(aRuntimeDataDTO.getRuntimeDynamicData());
				runTimeDataMap.put(runtimeID.getScenarioName(), aDataBean);
			}
			return runTimeDataMap;
		} catch (Exception ex) {
			String strLogErr = AppUtils.formatMessage("Error While {0}", strLogMessgae);
			LOGGER.error(strLogErr);
			aErrorLogger.error(strLogErr, ex);
			return runTimeDataMap;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessgae));
		}

	}

	public void updateRuntimeData(TestSuiteBean aTestSuiteBean, LinkedHashMap<String, String> runtimeDynamicData)
			throws Exception {
		HostDTO aHostDTO = getDefautHostDTO();
		HostEmbeddedDTO hostEmbeddedDTO = aHostDTO.getHostID();
		ScenarioEmbeddedDTO aScenarioEmbeddedDTO = new ScenarioEmbeddedDTO();
		aScenarioEmbeddedDTO.setScenarioName(aTestSuiteBean.getScenarioName());
		aScenarioEmbeddedDTO.setBrowserName(aTestSuiteBean.getBrowserDisplayName());
		String strLogMessgae = AppUtils.formatMessage("Updating Scenario {0} Runtime Data {1} for Host {2}",
				aTestSuiteBean.getScenarioName(), runtimeDynamicData, hostEmbeddedDTO.getHostName());
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessgae));
		if (runtimeDynamicData == null || runtimeDynamicData.isEmpty()) {
			return;
		}
		try {
			runtimeDynamicData.remove(SummaryReportConstants.SECNARIO_HEADER);
			runtimeDynamicData.remove(SummaryReportConstants.SECNARIO_DESCRIPTION_HEADER);
			RuntimeDataDTO aRuntimeDataDTO = new RuntimeDataDTO();
			aRuntimeDataDTO.setExecutionDate(AppConfig.getInstance().getExecutionDate());
			aRuntimeDataDTO.setHost(aHostDTO);
			aRuntimeDataDTO.setRuntimeID(aScenarioEmbeddedDTO);
			aRuntimeDataDTO.setRuntimeDynamicData(runtimeDynamicData);
			aRuntimeDataDTO.setUpdatedBy(AppUtils.getSystemUserName());
			RuntimeDataDTO aUPdatedRuntimeDataDTO = RemoteMultiPlatformClient
					.updateRuntimeData(aPPRunEnv.getHostAddress(), aPPRunEnv.getHostPort(), aRuntimeDataDTO);
			LOGGER.info(AppUtils.formatMessage("Sucessfully Updated Scenario {0} Runtime Data {1} for Host {2}",
					aTestSuiteBean.getScenarioName(), aUPdatedRuntimeDataDTO.getRuntimeDynamicData(),
					hostEmbeddedDTO.getHostName()));
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessgae));
		}
	}

	public void updateReferenceNumber(TestSuiteBean aTestSuiteBean,
			LinkedHashMap<String, String> referenceNumbersDynamicData) throws Exception {
		HostDTO aHostDTO = getDefautHostDTO();
		HostEmbeddedDTO hostEmbeddedDTO = aHostDTO.getHostID();
		ScenarioEmbeddedDTO aScenarioEmbeddedDTO = new ScenarioEmbeddedDTO();
		aScenarioEmbeddedDTO.setScenarioName(aTestSuiteBean.getScenarioName());
		aScenarioEmbeddedDTO.setBrowserName(aTestSuiteBean.getBrowserDisplayName());
		ScenarioDTO aScenario = new ScenarioDTO();
		aScenario.setScenarioID(aScenarioEmbeddedDTO);
		String strLogMessgae = AppUtils.formatMessage("Updating Scenario {0} Reference Numbers Data {1} for Host {2}",
				aTestSuiteBean.getScenarioName(), referenceNumbersDynamicData, hostEmbeddedDTO.getHostName());
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessgae));
		try {
			if (referenceNumbersDynamicData == null || referenceNumbersDynamicData.isEmpty()) {
				return;
			}
			referenceNumbersDynamicData.remove(SummaryReportConstants.SECNARIO_HEADER);
			referenceNumbersDynamicData.remove(SummaryReportConstants.SECNARIO_DESCRIPTION_HEADER);
			ReferenceNumberDTO aReferenceNumberDTO = new ReferenceNumberDTO();
			aReferenceNumberDTO.setExecutionDate(AppConfig.getInstance().getExecutionDate());
			aReferenceNumberDTO.setHost(aHostDTO);
			aReferenceNumberDTO.setScenario(aScenario);
			aReferenceNumberDTO.setReferenceNumbersDynamicData(referenceNumbersDynamicData);
			aReferenceNumberDTO.setUpdatedBy(AppUtils.getSystemUserName());
			ReferenceNumberDTO aUPdatedReferenceNumber = RemoteMultiPlatformClient
					.updateReferenceNumber(aPPRunEnv.getHostAddress(), aPPRunEnv.getHostPort(), aReferenceNumberDTO);
			LOGGER.info(
					AppUtils.formatMessage("Sucessfully Updated Scenario {0} Reference Numbers Data {1} for Host {2}",
							aTestSuiteBean.getScenarioName(), aUPdatedReferenceNumber.getReferenceNumbersDynamicData(),
							hostEmbeddedDTO.getHostName()));
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessgae));
		}
	}

	public void updateQuatationSummary(TestSuiteBean aTestSuiteBean,
			LinkedHashMap<String, String> quatationSummaryDynamicData) throws Exception {
		HostDTO aHostDTO = getDefautHostDTO();
		HostEmbeddedDTO hostEmbeddedDTO = aHostDTO.getHostID();
		ScenarioEmbeddedDTO aScenarioEmbeddedDTO = new ScenarioEmbeddedDTO();
		aScenarioEmbeddedDTO.setScenarioName(aTestSuiteBean.getScenarioName());
		aScenarioEmbeddedDTO.setBrowserName(aTestSuiteBean.getBrowserDisplayName());
		ScenarioDTO aScenario = new ScenarioDTO();
		aScenario.setScenarioID(aScenarioEmbeddedDTO);
		String strLogMessgae = AppUtils.formatMessage("Updating Scenario {0} Quatation Summary Data {1} for Host {2}",
				aTestSuiteBean.getScenarioName(), quatationSummaryDynamicData, hostEmbeddedDTO.getHostName());
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessgae));
		try {
			if (quatationSummaryDynamicData == null || quatationSummaryDynamicData.isEmpty()) {
				return;
			}
			quatationSummaryDynamicData.remove(SummaryReportConstants.SECNARIO_HEADER);
			quatationSummaryDynamicData.remove(SummaryReportConstants.SECNARIO_DESCRIPTION_HEADER);
			QuatationSummaryDTO aQuatationSummaryDTO = new QuatationSummaryDTO();
			aQuatationSummaryDTO.setExecutionDate(AppConfig.getInstance().getExecutionDate());
			aQuatationSummaryDTO.setHost(aHostDTO);
			aQuatationSummaryDTO.setScenario(aScenario);
			aQuatationSummaryDTO.setQuatationSummaryDynamicData(quatationSummaryDynamicData);
			aQuatationSummaryDTO.setUpdatedBy(AppUtils.getSystemUserName());
			QuatationSummaryDTO aUPdatedQuatationSummary = RemoteMultiPlatformClient
					.updateQuatationSummary(aPPRunEnv.getHostAddress(), aPPRunEnv.getHostPort(), aQuatationSummaryDTO);
			LOGGER.info(
					AppUtils.formatMessage("Sucessfully Updated Scenario {0} Quatation Summary Data {1} for Host {2}",
							aTestSuiteBean.getScenarioName(), aUPdatedQuatationSummary.getQuatationSummaryDynamicData(),
							hostEmbeddedDTO.getHostName()));
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessgae));
		}
	}
}
