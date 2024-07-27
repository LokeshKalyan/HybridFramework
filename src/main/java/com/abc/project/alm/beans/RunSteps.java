/****************************************************************************
 * File Name 		: RunSteps.java
 * Package			: com.dxc.zurich.alm.beans
 * Author			: LOKESH.DASARI
 * Creation Date	: Jun 12, 2023
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
 * @author LOKESH.DASARI
 * @since Jun 12, 2023 3:45:12 PM
 */
@XmlRootElement(name = "Entities")
@XmlAccessorType(XmlAccessType.FIELD)
public class RunSteps {

	@XmlElement(name = "Entity", required = true)
	private List<RunStep> entities;
	
	@XmlElement(name = "singleElementCollection", required = true)
	private boolean singleElementCollection;

	public RunSteps() {
		this(new ArrayList<RunStep>());
	}

	public RunSteps(List<RunStep> entities) {
		if (entities instanceof List) {
			this.entities = (List<RunStep>) entities;
		} else {
			this.entities = new ArrayList<RunStep>(entities);
		}
		this.singleElementCollection = false;
	}

	public List<RunStep> entities() {
		return entities;
	}

	public void entities(List<RunStep> entities) {
		this.entities = entities;
	}

	public void addEntity(RunStep entity) {
		entities.add(entity);
	}
}
