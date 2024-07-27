/****************************************************************************
 * File Name 		: IdentificationType.java
 * Package			: com.dxc.zurich.enums
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
package com.abc.project.enums;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 10:58:10 am
 */
public enum IdentificationType {

	XPATH("XPATH"), 
	ID("ID"), 
	NAME("NAME"), 
	LINKTEXT("LINKTEXT"), 
	TAG_NAME("TAGNAME"), 
	PLINK("PLINK"), 
	CLASSNAME("CLASSNAME"), 
	CSSSELECTOR("CSSSELECTOR"), 
	ACCESSIBILITY_ID("ACCESSIBILITYID"), 
	WINDOWS_UI_AUTOMATION("WINDOWSUIAUTOMATION"),
	IOS_PREDICATE_STRING("IOSPREDICATESTRING"),
    IOS_CLASS_CHAIN("IOSCLASSCHAIN"),
	ANDROID_UI_AUTOMATOR("ANDROIDUIAUTOMATOR"), 
	ANDROID_DATAMATCHER("ANDROIDDATAMATCHER"), 
	ANDROID_VIEWMATCHER("ANDROIDVIEWMATCHER"), 
	IMAGE("IMAGE"),
	CUSTOM("CUSTOM"),
	MANGODB("MANGODB"), 
	ORACLEDB("ORACLEDB"), 
	MSSQLDB("MSSQLDB"), //MICROSOFTSQLDB
	MYSQLDB("MYSQLDB"), 
	INVALID("InValid");
	
	private String strKeyWordType;
	
	IdentificationType(String strKeyWordType){
		this.setKeyWordType(strKeyWordType);
	}

	/**
	 * @return the strKeyWordType
	 */
	public String getKeyWordType() {
		return strKeyWordType;
	}

	/**
	 * @param strKeyWordType the strKeyWordType to set
	 */
	public void setKeyWordType(String strKeyWordType) {
		this.strKeyWordType = strKeyWordType;
	}
	
	@Override
	public String toString() {
		return getKeyWordType();
	}
	
	public static IdentificationType getKeyWordType(final String strKeyWordType) {
		List<IdentificationType> lstKeyWordTypes = Arrays.asList(IdentificationType.values());
		IdentificationType aKeyWordType = lstKeyWordTypes.stream().filter(objKeyWordType -> StringUtils.equalsIgnoreCase(objKeyWordType.getKeyWordType(), strKeyWordType)).findFirst().orElse(IdentificationType.INVALID);
		return aKeyWordType;
	}
}
