/****************************************************************************
 * File Name 		: AbstractDataBaseValidation.java
 * Package			: com.dxc.zurich.database
 * Author			: pmusunuru2
 * Creation Date	: May 03, 2021
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
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import com.abc.project.constants.AppConstants;
import com.abc.project.constants.DataBaseConstants;
import com.abc.project.enums.StartFinish;
import com.abc.project.utils.AppUtils;
import com.abc.project.utils.PropertyHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author pmusunuru2
 * @since May 03, 2021 3:04:16 pm
 */
public abstract class AbstractDataBaseValidation implements DataBaseValidation {

	private String strTestData;
	private String strORProperty;
	private String strReportKeyWord;
	private static Logger LOGGER;
	private static Logger ERROR_LOGGER;

	private LinkedHashMap<String, Object> mpTestData;

	public AbstractDataBaseValidation(String strTestData, String strORProperty, String strReportKeyWord) {
		this.strTestData = strTestData;
		this.strORProperty = strORProperty;
		this.strReportKeyWord = StringUtils.isEmpty(strReportKeyWord) ? "Un-Known" : strReportKeyWord;
		LOGGER = getLogger();
		ERROR_LOGGER = getErrorLogger();
	}

	public abstract Logger getLogger();

	public abstract Logger getErrorLogger();

	public abstract Object getConnection(Properties aDBProperties) throws Exception;

	public String getReportKeyWord() {
		return strReportKeyWord;
	}

	@Override
	public String getDBConfigFilename() {
		return PropertyHandler.getExternalString(strORProperty, AppConstants.APP_PROPERTIES_NAME);
	}

	@Override
	public Properties getDBProperties() {
		String strLogMessage = String.format("Fetching DB Properties for %s", strReportKeyWord);
		Properties aDBProperties = new Properties();
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
		String strFilename = getDBConfigFilename();
		String strConfigPopFile = String.format("%s%s", AppConstants.DB_CONFIG_PROPERTIES_LOC, strFilename);
		File aDBConfigFile = AppUtils.getFileFromPath(strConfigPopFile);
		if (!aDBConfigFile.exists()) {
			return aDBProperties;
		}
		try (FileInputStream aFileInputStream = new FileInputStream(aDBConfigFile)) {
			aDBProperties.load(aFileInputStream);
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
		return aDBProperties;
	}

	@Override
	public LinkedHashMap<String, Object> getDBTestData() {
		if (mpTestData == null) {
			String strLogMessage = String.format("Fetching DB TestData for %s", strReportKeyWord);
			try {
				LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
				if (StringUtils.isEmpty(strTestData)) {
					mpTestData = new LinkedHashMap<String, Object>();
				} else {
					Gson aGson = AppUtils.getDefaultGson();
					Type capaBilityType = new TypeToken<LinkedHashMap<String, Object>>() {}.getType();
					mpTestData = aGson.fromJson(strTestData, capaBilityType);
				}
			} catch (Exception ex) {
				String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
				LOGGER.error(strErrorMsg);
				ERROR_LOGGER.error(strErrorMsg, ex);
				mpTestData = new LinkedHashMap<String, Object>();
			} finally {
				LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
			}
		}
		return mpTestData;
	}

	@Override
	public Object getQueryToExecute() {
		return getDBTestData().get(DataBaseConstants.DB_CONFIG_QUERY_KEY);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Object> getDataToValidate() {
		return (List<Object>) getDBTestData().get(DataBaseConstants.DB_CONFIG_DATA_KEY);
	}

	
	@Override
	public String executeQuery() 
	{
		List<LinkedHashMap<String, Object>> lstRecords = fetchDBData();
		if (CollectionUtils.isEmpty(lstRecords)) {
			return AppConstants.TEST_RESULT_FAIL;
		}
		List<Object> lstData = getDataToValidate();
		if (CollectionUtils.isEmpty(lstData)) {
			return AppConstants.TEST_RESULT_PASS;
		}
		boolean bDataMatched = lstRecords.stream()
				.anyMatch(mpRecord -> CollectionUtils.containsAny(mpRecord.values(), lstData));
		return bDataMatched ? AppConstants.TEST_RESULT_PASS : AppConstants.TEST_RESULT_FAIL;
	}
}
