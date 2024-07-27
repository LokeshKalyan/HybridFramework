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
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author LOKESH.DASARI
 * @since Jun 12, 2023 3:30:45 PM
 */
@XmlRootElement(name = "Entity")
@XmlAccessorType(XmlAccessType.FIELD)
public class RunStep 
{
	@XmlElement(name = "Field", required = true)
	@XmlElementWrapper(name = "Fields")
	private List<Field> fields;
	
	@XmlAttribute(name = "Type", required = true)
	private String type;


	@XmlElement(name = "ChildrenCount", required = true)
	private ChildrenCount aChildrenCount;
	
	@XmlElement(name = "RelatedEntities", required = false)
	private RelatedEntities aRelatedEntities;
	
	public RunStep(RunStep aRunStep) {
		this.aChildrenCount = aRunStep.aChildrenCount;
		this.fields = aRunStep.fields();
		this.type = aRunStep.type();
		this.aRelatedEntities = aRunStep.aRelatedEntities;
		
	}
	
	public RunStep() {
		type("run-step");
	}

	/**
	 * Gets the value of the type property.
	 *
	 * @return
	 */
	public String type() {
		return type;
	}

	/**
	 * Sets the value of the type property.
	 *
	 * @param value
	 */
	public void type(String value) {
		type = value;
	}

	/**
	 * Gets the value of the field using the name the field to be retrieved.
	 *
	 * @param name
	 * @return
	 */
	public String fieldValue(String name) {
		Field field = field(name);

		return field != null ? field.value() : null;
	}
	
	/**
	 * Sets the value of field.
	 *
	 * @param name
	 * @param value
	 */
	public void fieldValue(String name, String value) {
		Field field = field(name);

		if (field == null) {
			field = new Field(name);
			fields().add(field);
		}

		field.value(value);
	}
	/**
	 * Gets the value of the fields property.
	 *
	 * @return
	 */
	public List<Field> fields() {
		if (fields == null) {
			fields = new ArrayList<Field>();
		}

		return fields;
	}
	
    public void clearBeforeUpdate()
    {
        removeField("parent-id");

        removeField("id");
    }

    public String runId()
    {
        return fieldValue("parent-id");
    }

    public void runId(String value)
    {
        fieldValue("parent-id", value);
    }

    public String description()
    {
        return fieldValue("description");
    }

    public void description(String value)
    {
        fieldValue("description", value);
    }

    public String status()
    {
        return fieldValue("status");
    }

    public void status(String value)
    {
        fieldValue("status", value);
    }

    public String testId()
    {
        return fieldValue("test-id");
    }

    public void testId(String value)
    {
        fieldValue("test-id", value);
    }

    public String actual()
    {
        return fieldValue("actual");
    }

    public void actual(String value)
    {
        fieldValue("actual", value);
    }

    public String expected()
    {
        return fieldValue("expected");
    }

    public void expected(String value)
    {
        fieldValue("expected", value);
    }

    public String executionTime()
    {
        return fieldValue("execution-time");
    }

    public void executionTime(String value)
    {
        fieldValue("execution-time", value);
    }
    
    /**
     * Gets the id property.
     *
     * @return
     */
    public String id()
    {
        return fieldValue("id");
    }

    /**
     * Sets the value for id property.
     *
     * @param value
     */
    public void id(String value)
    {
        fieldValue("id", value);
    }

    /**
     * Gets the parent-id property.
     *
     * @return
     */
    public String parentId()
    {
        return fieldValue("parent-id");
    }

    /**
     * Sets the value for id property.
     *
     * @param value
     */
    public void parentId(String value){
        fieldValue("parent-id", value);
    }

    /**
     * Gets the name property.
     *
     * @return
     */
    public String name(){
        return fieldValue("name");
    }

    /**
     * Sets the value for name property.
     *
     * @param value
     */
    public void name(String value){
        fieldValue("name", value);
    }
    /**
	 * Gets the field using name of the field to be retrieved.
	 *
	 * @param name
	 * @return
	 */
	public Field field(String name) {
		for (Field field : fields()) {
			if (name.equals(field.name())) {
				return field;
			}
		}

		return null;
	}

	/**
	 * Removes the field using name of the field to be removed
	 *
	 * @param name
	 */
	public void removeField(String name) {
		Iterator<Field> it = fields().iterator();

		while (it.hasNext()) {
			Field field = it.next();

			if (name.equals(field.name())) {
				it.remove();
			}
		}
	}
}
