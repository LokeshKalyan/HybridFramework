/****************************************************************************
 * File Name 		: KeyWordConfigBean.java
 * Package			: com.dxc.zurich.beans
 * Author			: pmusunuru2
 * Creation Date	: May 05, 2021
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


import org.apache.commons.lang3.StringUtils;

import com.abc.project.enums.IdentificationType;
import com.abc.project.enums.KeyWord;

/**
 * @author pmusunuru2
 * @since May 05, 2021 10:48:06 am
 */
public class KeyWordConfigBean {

	private String strOriginalKeyWord;

	private String strOriginalKeyWordType;

	private KeyWord aKeyWord;

	private IdentificationType aKeyWordType;

	/**
	 * @return the strOriginalKeyWord
	 */
	public String getOriginalKeyWord() {
		return strOriginalKeyWord;
	}

	/**
	 * @param strOriginalKeyWord the strOriginalKeyWord to set
	 */
	public void setOriginalKeyWord(String strOriginalKeyWord) {
		this.strOriginalKeyWord = strOriginalKeyWord;
	}

	/**
	 * @return the strOriginalKeyWordType
	 */
	public String getOriginalKeyWordType() {
		return strOriginalKeyWordType;
	}

	/**
	 * @param strOriginalKeyWordType the strOriginalKeyWordType to set
	 */
	public void setOriginalKeyWordType(String strOriginalKeyWordType) {
		strOriginalKeyWordType = StringUtils.trim(strOriginalKeyWordType);
		if (StringUtils.isEmpty(strOriginalKeyWordType)) {
			strOriginalKeyWordType = IdentificationType.XPATH.getKeyWordType();
		}
		this.strOriginalKeyWordType = strOriginalKeyWordType;
	}

	public KeyWord getKeyWord() {
		if (aKeyWord == null) {
			aKeyWord = KeyWord.getKeyWordByName(strOriginalKeyWord);
		}
		return aKeyWord;
	}

	public IdentificationType getKeyWorkType() {
		if (aKeyWordType == null) {
			aKeyWordType = IdentificationType.getKeyWordType(strOriginalKeyWordType);
		}
		return aKeyWordType;
	}

	public boolean canLogsExcluded() {
		return StringUtils.containsIgnoreCase(getKeyWord().getKeyWord(), "Wait")
				|| getKeyWord() == KeyWord.ALM_TESTSET_NAME || getKeyWord() == KeyWord.ALM_TESTSET_PATH
				|| getKeyWord() == KeyWord.ALM_TESTSET_ID || getKeyWord() == KeyWord.ALM_TESTCASE_STATUS
				|| getKeyWord() == KeyWord.TAKE_FULLWINDOW_SCREENSHOT;
	}

	@Override
	public String toString() {
		return String.format("Keyword:-%s and KeyWordType:-%s", getOriginalKeyWord(), getOriginalKeyWordType());
	}
}
