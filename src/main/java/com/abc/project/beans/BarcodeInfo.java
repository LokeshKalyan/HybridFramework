/****************************************************************************
 * File Name 		: BarcodeInfo.java
 * Package			: com.dxc.zurich.beans
 * Author			: pmusunuru2
 * Creation Date	: Feb 23, 2021
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

/**
 * @author pmusunuru2
 * @since Feb 23, 2021 11:35:49 am
 */
public class BarcodeInfo {

	 private final String text;
	 
	 private final String format;
	 
	 public BarcodeInfo(String text, String format) {
         this.text = text;
         this.format = format;
     }

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}
}
