/****************************************************************************
 * File Name 		: DataBaseConstants.java
 * Package			: com.dxc.zurich.constants
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
package com.abc.project.constants;

/**
 * @author pmusunuru2
 * @since May 03, 2021 4:11:52 pm
 */
public interface DataBaseConstants {

	String DB_CONFIG_QUERY_KEY = "QUERY";

	String DB_CONFIG_DATA_KEY = "DATA";

	String DATABASE_URL_KEY = "database.url";

	String DATABASE_DATABASENAME_KEY = "db.database";
	
	String DATABASE_CLASSNAME_KEY = "database.driverClassName";
	
	String DATABASE_USERNAME_KEY = "database.username";
	
	String DATABASE_PASSWORD_KEY = "database.password";
	
	String DATABASE_INITIALSIZE_KEY = "database.initialSize";
	
	String DATABASE_MAXTOTAL_KEY = "database.maxTotal";
	
	String DATABASE_MINIDLE_KEY = "database.minIdle";
	
	String DATABASE_MAXIDLE_KEY = "database.maxIdle";
	
	String DATABASE_MAXWAIT_TIME_KEY = "database.maxWaitTime";
	
	String DATABASE_VALIDATIONQUERY_KEY = "database.validationQuery";
	
	String DATABASE_AUTOCOMMITSPECCOMPLIANT_KEY = "database.autoCommitSpecCompliant";
	
	String ORACLE_DATABASE_AUTOCOMMITSPECCOMPLIANT_KEY = "oracle.jdbc.autoCommitSpecCompliant";

	String MANGO_DB_FIND_KEY = "$gte";

	String DATABASE_TABLENAME_KEY = "tableName";
}
