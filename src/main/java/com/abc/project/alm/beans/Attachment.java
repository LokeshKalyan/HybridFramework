/****************************************************************************
 * File Name 		: Attachment.java
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
 * @since Dec 06, 2022 3:00:03 pm
 */
@XmlRootElement(name = "Entity")
@XmlAccessorType(XmlAccessType.FIELD)
public class Attachment {

	@XmlElement(name = "Field", required = true)
	@XmlElementWrapper(name = "Fields")
	private List<Field> fields;

	@XmlElement(name = "ChildrenCount", required = true)
	private ChildrenCount aChildrenCount;

	@XmlElement(name = "RelatedEntities", required = false)
	private RelatedEntities aRelatedEntities;

	@XmlAttribute(name = "Type", required = true)
	private String type;

	public Attachment(Attachment entity) {
		this.aChildrenCount = entity.aChildrenCount;
		this.aRelatedEntities = entity.aRelatedEntities;
		this.fields = entity.fields();
		this.type = entity.type();
	}

	public Attachment() {
		type("attachment");
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

	public String description() {
		return fieldValue("description");
	}

	public void description(String value) {
		fieldValue("description", value);
	}

	public String fileSize() {
		return fieldValue("file-size");
	}

	public void fileSize(String value) {
		fieldValue("file-size", value);
	}

	public String refType() {
		return fieldValue("ref-type");
	}

	public void refType(String value) {
		fieldValue("ref-type", value);
	}

	public String parentType() {
		return fieldValue("parent-type");
	}

	public void parentType(String value) {
		fieldValue("parent-type", value);
	}
}
