/****************************************************************************
 * File Name 		: BasicDBValidation.java
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abc.project.constants.AppConstants;
import com.abc.project.constants.DataBaseConstants;
import com.abc.project.enums.StartFinish;
import com.abc.project.utils.AppUtils;

/**
 * @author pmusunuru2
 * @since May 04, 2021 3:46:18 pm
 */
public class BasicDBValidation extends AbstractDataBaseValidation {

	private static final Logger LOGGER = LogManager.getLogger(BasicDBValidation.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	private BasicDataSource aDataSource;

	/**
	 * @param strTestData
	 * @param strORProperty
	 * @param strReportKeyWord
	 */
	public BasicDBValidation(String strTestData, String strORProperty, String strReportKeyWord) {
		super(strTestData, strORProperty, strReportKeyWord);
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	@Override
	public Logger getErrorLogger() {
		return ERROR_LOGGER;
	}

	@Override
	public List<LinkedHashMap<String, Object>> fetchDBData() {
		List<LinkedHashMap<String, Object>> lstRecords = new ArrayList<>();
		String strTableNameKey = DataBaseConstants.DATABASE_TABLENAME_KEY;
		LinkedHashMap<String, Object> mpTestData = getDBTestData();
		String strSQl = (String) getQueryToExecute();
		if (StringUtils.isEmpty(strSQl) || !mpTestData.containsKey(strTableNameKey)
				|| mpTestData.get(strTableNameKey) == null) {
			return lstRecords;
		}
		String strTableName = (String) mpTestData.get(strTableNameKey);
		Properties aDBProperties = getDBProperties();
		String strLogMessage = String.format("Fetching data from table %s", strTableName);
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
		try (Connection aConnection = getConnection(aDBProperties);
				Statement aStatement = aConnection.createStatement();
				ResultSet aResultSet = aStatement.executeQuery(strSQl)) {
			ResultSetMetaData aMetaData = aResultSet.getMetaData();
			int numberOfColumns = aMetaData.getColumnCount();

			while (aResultSet.next()) {
				LinkedHashMap<String, Object> mpRecord = new LinkedHashMap<String, Object>();
				for (int i = 1; i <= numberOfColumns; i++) {
					String strColumnName = aMetaData.getColumnName(i);
					Object aValue = aResultSet.getObject(strColumnName);
					if (aValue == null) {
						aValue = "";
					}
					mpRecord.put(strColumnName, aValue);
				}
				lstRecords.add(mpRecord);
			}
			return lstRecords;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return lstRecords;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	@Override
	public Connection getConnection(Properties aDBProperties) throws SQLException {
		if (aDataSource == null) {
			initDataSource(aDBProperties);
		}
		return aDataSource.getConnection();
	}

	protected void initDataSource(Properties aDBProperties) {
		aDataSource = new BasicDataSource();
		aDataSource.setUsername(aDBProperties.getProperty(DataBaseConstants.DATABASE_USERNAME_KEY));
		aDataSource.setPassword(aDBProperties.getProperty(DataBaseConstants.DATABASE_PASSWORD_KEY));
		aDataSource.setDriverClassName(aDBProperties.getProperty(DataBaseConstants.DATABASE_CLASSNAME_KEY));
		aDataSource.setUrl(aDBProperties.getProperty(DataBaseConstants.DATABASE_URL_KEY));
		aDataSource.setMinIdle(Integer.valueOf(aDBProperties.getProperty(DataBaseConstants.DATABASE_MINIDLE_KEY)));
		aDataSource.setMaxIdle(Integer.valueOf(aDBProperties.getProperty(DataBaseConstants.DATABASE_MAXIDLE_KEY)));
		aDataSource
				.setMaxWaitMillis(Long.valueOf(aDBProperties.getProperty(DataBaseConstants.DATABASE_MAXWAIT_TIME_KEY)));
		aDataSource
				.setInitialSize(Integer.valueOf(aDBProperties.getProperty(DataBaseConstants.DATABASE_INITIALSIZE_KEY)));
		aDataSource.setMaxTotal(Integer.valueOf(aDBProperties.getProperty(DataBaseConstants.DATABASE_MAXTOTAL_KEY)));
//		aDataSource.setValidationQuery(aDBProperties.getProperty(DataBaseConstants.DATABASE_VALIDATIONQUERY_KEY));
	}
}
