/****************************************************************************
 * File Name 		: EntityMarshallingUtils.java
 * Package			: com.dxc.zurich.utils
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
package com.abc.project.utils;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

/**
 * A utility class for converting between JAXB annotated objects and XML.
 * 
 * @author pmusunuru2
 * @since Dec 06, 2022 2:51:04 pm
 */
public class EntityMarshallingUtils {

	private EntityMarshallingUtils() {
	}

	/**
	 * @param <T> the type we want to convert the XML into
	 * @param c   the class of the parameterized type
	 * @param xml the instance XML description
	 * @return a deserialization of the XML into an object of type T of class class
	 *         <T>
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T marshal(Class<T> c, String xml) throws Exception {
		T res;
		try {
			if (c == xml.getClass()) {
				res = (T) xml;
			} else {
				JAXBContext ctx = JAXBContext.newInstance(c);
				Unmarshaller marshaller = ctx.createUnmarshaller();
				marshaller.setEventHandler(new ValidationEventHandler() {
					@Override
					public boolean handleEvent(ValidationEvent event) {
						throw new RuntimeException(event.getMessage(), event.getLinkedException());
					}
				});
				res = (T) marshaller.unmarshal(new StringReader(xml));
			}
		} catch (RuntimeException re) {
			throw new Exception(re);
		} catch (JAXBException jc) {
			throw jc;
		}
		return res;
	}

	/**
	 * @param <T> the type to serialize
	 * @param c   the class of the type to serialize
	 * @param o   the instance containing the data to serialize
	 * @return a string representation of the data.
	 * @throws Exception
	 */
	public static <T> String unmarshal(Class<T> c, Object o) throws Exception {
		JAXBContext ctx = JAXBContext.newInstance(c);
		Marshaller marshaller = ctx.createMarshaller();
		StringWriter entityXml = new StringWriter();
		marshaller.marshal(o, entityXml);
		String entityString = entityXml.toString();
		return entityString;
	}
}
