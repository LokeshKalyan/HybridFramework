/****************************************************************************
 * File Name 		: ChildrenCount.java
 * Package			: com.dxc.zurich.alm.beans
 * Author			: pmusunuru2
 * Creation Date	: Dec 06, 2022
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.alm.beans;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author pmusunuru2
 * @since Dec 06, 2022 2:57:34 pm
 */
@XmlRootElement(name = "ChildrenCount")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChildrenCount {

	@XmlElement(name = "Value", required = true)
	private List<String> values;

	public ChildrenCount() {
	}

	public ChildrenCount(String value) {
		value(value);
	}

	/**
	 * Gets the value of the values property.
	 *
	 * @return
	 */
	public List<String> values() {
		if (values == null) {
			values = new ArrayList<String>();
		}

		return values;
	}

	/**
	 * Gets the first value of the values property.
	 *
	 * @return
	 */
	public String value() {
		return values().isEmpty() ? null : values().get(0);
	}

	/**
	 * Add a new item to values property.
	 *
	 * @param value
	 */
	public void value(String value) {
		values().add(value);
	}
}
