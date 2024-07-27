/****************************************************************************
 * File Name 		: DataBaseValidation.java
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

/**
 * @author pmusunuru2
 * @since May 03, 2021 2:44:48 pm
 */
public interface DataBaseValidation 
{
	
	String getDBConfigFilename();
	
	Properties getDBProperties();
	
	LinkedHashMap<String, Object> getDBTestData();
	
	Object getQueryToExecute();
	
	List<Object> getDataToValidate();
	
	List<LinkedHashMap<String, Object>> fetchDBData();
	
	String executeQuery();
}
