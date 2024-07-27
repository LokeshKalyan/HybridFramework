/****************************************************************************
 * File Name 		: ClearSessions.java
 * Package			: com.dxc.zurich.utils
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
package com.abc.project.utils;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abc.project.constants.AppConstants;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 11:13:33 am
 */
public class ClearSessions {

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	private static final String [] UNCLOSED_FILE_NAMES = {"Document"};
	/***
	 * Closes the ByName
	 * 
	 * @param strFile
	 */
	public static void closeFileByName(File aFile) {
		closeFileByName(aFile, false);
	}
	
	
	/***
	 * Closes the ByName
	 * 
	 * @param strFile
	 */
	public static void closeFileByName(File aFile , boolean bForce) {
		try {
			String strBaseFileName = FilenameUtils.getBaseName(aFile.getName());
			if(!bForce && StringUtils.containsAny(strBaseFileName, UNCLOSED_FILE_NAMES)) {
				return;
			}
			String strCommand = String.format("TASKKILL /F /FI \"WINDOWTITLE eq  %s*\" /T", strBaseFileName);
			Process aProcess = Runtime.getRuntime().exec(strCommand);
			aProcess.waitFor();
		} catch (Exception ex) {
			ERROR_LOGGER.error(String.format("Error While Closing excel file %s", aFile.toString()), ex);
		}
	}
}
