/****************************************************************************
 * File Name 		: OracleDBValidation.java
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

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abc.project.constants.AppConstants;
import com.abc.project.constants.DataBaseConstants;

/**
 * @author pmusunuru2
 * @since May 04, 2021 2:39:16 pm
 */
public class OracleDBValidation extends BasicDBValidation {

	private static final Logger LOGGER = LogManager.getLogger(OracleDBValidation.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	/**
	 * @param strTestData
	 * @param strORProperty
	 * @param strReportKeyWord
	 */
	public OracleDBValidation(String strTestData, String strORProperty, String strReportKeyWord) {
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
	protected void initDataSource(Properties aDBProperties) {
		super.initDataSource(aDBProperties);
		System.setProperty(DataBaseConstants.ORACLE_DATABASE_AUTOCOMMITSPECCOMPLIANT_KEY,
				aDBProperties.getProperty(DataBaseConstants.DATABASE_AUTOCOMMITSPECCOMPLIANT_KEY));
	}

}
