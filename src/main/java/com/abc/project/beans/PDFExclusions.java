/****************************************************************************
 * File Name 		: PDFExclusions.java
 * Package			: com.dxc.zurich.beans
 * Author			: pmusunuru2
 * Creation Date	: Jun 19, 2023
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.beans;

import java.io.File;

import com.abc.project.enums.PDFFileType;

/**
 * @author pmusunuru2
 * @since Jun 19, 2023 9:14:13 am
 */
public class PDFExclusions {

	private String strScenarioName;
	
	private PDFFileType aPdfFileType; 
	
	private File strExpectedPath;
	
	private String strExclusionAreas;
	
	

	/**
	 * @return the strScenarioName
	 */
	public String getScenarioName() {
		return strScenarioName;
	}

	/**
	 * @param strScenarioName the strScenarioName to set
	 */
	public void setScenarioName(String strScenarioName) {
		this.strScenarioName = strScenarioName;
	}

	/**
	 * @return the aPdfFileType
	 */
	public PDFFileType getFileType() {
		return aPdfFileType;
	}

	/**
	 * @param aPdfFileType the aPdfFileType to set
	 */
	public void setFileType(PDFFileType aPdfFileType) {
		this.aPdfFileType = aPdfFileType;
	}

	/**
	 * @return the strExpectedPath
	 */
	public File getExpectedPath() {
		return strExpectedPath;
	}

	/**
	 * @param strExpectedPath the strExpectedPath to set
	 */
	public void setExpectedPath(File strExpectedPath) {
		this.strExpectedPath = strExpectedPath;
	}

	/**
	 * @return the strExclusionAreas
	 */
	public String getExclusionAreas() {
		return strExclusionAreas;
	}

	/**
	 * @param strExclusionAreas the strExclusionAreas to set
	 */
	public void setExclusionAreas(String strExclusionAreas) {
		this.strExclusionAreas = strExclusionAreas;
	}

}
