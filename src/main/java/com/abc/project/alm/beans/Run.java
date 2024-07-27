/****************************************************************************
 * File Name 		: Run.java
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
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author pmusunuru2
 * @since Dec 06, 2022 2:59:08 pm
 */
@XmlRootElement(name = "Entity")
@XmlAccessorType(XmlAccessType.FIELD)
public class Run {

	public static final String STATUS_BLOCKED = "Blocked";
	public static final String STATUS_FAILED = "Failed";
	public static final String STATUS_NA = "N/A";
	public static final String STATUS_NO_RUN = "No Run";
	public static final String STATUS_NOT_COMPLETED = "Not Completed";
	public static final String STATUS_PASSED = "Passed";

	public static final String TEST_TYPE_MANUAL = "hp.qc.run.MANUAL";

	@XmlElement(name = "Field", required = true)
	@XmlElementWrapper(name = "Fields")
	private List<Field> fields;

	@XmlAttribute(name = "Type", required = true)
	private String type;

	@XmlElement(name = "ChildrenCount", required = true)
	private ChildrenCount aChildrenCount;

	@XmlElement(name = "RelatedEntities", required = false)
	private RelatedEntities aRelatedEntities;

	public Run(Run entity) {
		this.aChildrenCount = entity.aChildrenCount;
		this.fields = entity.fields();
		this.type = entity.type();
		this.aRelatedEntities = entity.aRelatedEntities;
	}

	public Run() {
		type("run");
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

	public String testInstanceId() {
		return fieldValue("testcycl-id");
	}

	public void testInstanceId(String value) {
		fieldValue("testcycl-id", value);
	}

	public String testSetId() {
		return fieldValue("cycle-id");
	}

	public void testSetId(String value) {
		fieldValue("cycle-id", value);
	}

	public String testId() {
		return fieldValue("test-id");
	}

	public void testId(String value) {
		fieldValue("test-id", value);
	}

	public String testConfigId() {
		return fieldValue("test-config-id");
	}

	public void testConfigId(String value) {
		fieldValue("test-config-id", value);
	}

	public String status() {
		return fieldValue("status");
	}

	public void status(String value) {
		fieldValue("status", value);
	}

	public String owner() {
		return fieldValue("owner");
	}

	public void owner(String value) {
		fieldValue("owner", value);
	}

	public String testType() {
		return fieldValue("subtype-id");
	}

	public void testType(String value) {
		fieldValue("subtype-id", value);
	}

	public String host() {
		return fieldValue("host");
	}

	public void host(String value) {
		fieldValue("host", value);
	}

	public String comments() {
		return fieldValue("comments");
	}

	public void comments(String value) {
		fieldValue("comments", value);
	}

	public String duration() {
		return fieldValue("duration");
	}

	public void duration(String value) {
		fieldValue("duration", value);
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

	/**
	 * Remove ID field before an entity update
	 *
	 */
	public void clearBeforeUpdate() {
		removeField("id");
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
	 * Gets the id property.
	 *
	 * @return
	 */
	public String id() {
		return fieldValue("id");
	}

	/**
	 * Sets the value for id property.
	 *
	 * @param value
	 */
	public void id(String value) {
		fieldValue("id", value);
	}

	/**
	 * Gets the parent-id property.
	 *
	 * @return
	 */
	public String parentId() {
		return fieldValue("parent-id");
	}

	/**
	 * Sets the value for id property.
	 *
	 * @param value
	 */
	public void parentId(String value) {
		fieldValue("parent-id", value);
	}

	/**
	 * Gets the name property.
	 *
	 * @return
	 */
	public String name() {
		return fieldValue("name");
	}

	/**
	 * Sets the value for name property.
	 *
	 * @param value
	 */
	public void name(String value) {
		fieldValue("name", value);
	}
}
