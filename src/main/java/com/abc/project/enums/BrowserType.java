/****************************************************************************
 * File Name 		: BrowserType.java
 * Package			: com.dxc.zurich.enums
 * Author			: pmusunuru2
 * Creation Date	: Mar 29, 2021
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

/**
 * @author pmusunuru2
 * @since Mar 29, 2021 9:57:15 am
 */
public enum BrowserType {

	DESKTOP_WEB("Desktop-Web"),
	DESKTOP_NATIVE("Destop-Native"),
	MOBILE_WEB("Mobile-Web"),
	MOBILE_NATIVE("Mobile-Native"),
	INVALID("In-Valid");
	
	private String strBrowserType;
	
	BrowserType(String strBrowserType)
	{
		this.setBrowserType(strBrowserType);
	}
	/**
	 * @return the strBrowserType
	 */
	public String getBrowserType() {
		return strBrowserType;
	}
	/**
	 * @param strBrowserType the strBrowserType to set
	 */
	public void setBrowserType(String strBrowserType) {
		this.strBrowserType = strBrowserType;
	}
	
	@Override
	public String toString() {
		return getBrowserType();
	}
}
