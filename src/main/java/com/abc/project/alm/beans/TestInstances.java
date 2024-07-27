/****************************************************************************
 * File Name 		: TestInstances.java
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
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author pmusunuru2
 * @since Dec 06, 2022 2:55:13 pm
 */
@XmlRootElement(name = "Entities")
@XmlAccessorType(XmlAccessType.FIELD)
public class TestInstances {


	@XmlElement(name = "Entity", required = true)
	private List<TestInstance> entities;

	@XmlElement(name = "singleElementCollection", required = true)
	private boolean singleElementCollection;

	public TestInstances() {
		this(new ArrayList<TestInstance>());
	}

	public TestInstances(Collection<TestInstance> entities) {
		if (entities instanceof List) {
			this.entities = (List<TestInstance>) entities;
		} else {
			this.entities = new ArrayList<TestInstance>(entities);
		}
		this.singleElementCollection = false;
	}

	public List<TestInstance> entities() {
		return entities;
	}

	public void entities(List<TestInstance> entities) {
		this.entities = entities;
	}

	public void addEntity(TestInstance entity) {
		entities.add(entity);
	}
}
