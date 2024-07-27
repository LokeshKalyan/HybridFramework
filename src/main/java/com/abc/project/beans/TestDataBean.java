/****************************************************************************
 * File Name 		: TestDataBean.java
 * Package			: com.dxc.zurich.beans
 * Author			: pmusunuru2
 * Creation Date	: Feb 16, 2021
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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 11:07:40 am
 */
public class TestDataBean {
	private String scenarioName;

	private String description;

	private List<String> messageDescription;

	private List<String> reportkeyWord;

	private List<KeyWordConfigBean> keyWord;

	private List<String> objectProperty;

	private List<String> controlData;

	private List<Boolean> screenShot;

	/**
	 * @return the scenarioName
	 */
	public String getScenarioName() {
		return scenarioName;
	}

	/**
	 * @param scenarioName the scenarioName to set
	 */
	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the messageDescription
	 */
	public List<String> getMessageDescription() {
		return messageDescription;
	}

	/**
	 * @param messageDescription the messageDescription to set
	 */
	public void setMessageDescription(List<String> messageDescription) {
		this.messageDescription = messageDescription;
	}

	public void addMessageDescription(String strValue) {
		if (CollectionUtils.isEmpty(getMessageDescription())) {
			setMessageDescription(new LinkedList<>());
		}
		if (strValue == null) {
			strValue = "";
		}
		getMessageDescription().add(strValue);
	}

	/**
	 * @return the reportkeyWord
	 */
	public List<String> getReportKeyWord() {
		return reportkeyWord;
	}

	/**
	 * @param reportkeyWord the reportkeyWord to set
	 */
	public void setReportKeyWord(List<String> reportkeyWord) {
		this.reportkeyWord = reportkeyWord;
	}

	/**
	 * @return the keyWord
	 */
	public List<KeyWordConfigBean> getKeyWord() {
		return keyWord;
	}

	/**
	 * @param keyWord the keyWord to set
	 */
	public void setKeyWord(List<KeyWordConfigBean> keyWord) {
		this.keyWord = keyWord;
	}

	public void addReportKeyWord(String strValue) {
		if (CollectionUtils.isEmpty(getKeyWord())) {
			setReportKeyWord(new LinkedList<>());
		}
		if (strValue == null) {
			strValue = "";
		}
		getReportKeyWord().add(strValue);
	}

	public void addKeyWord(KeyWordConfigBean aKeyWordConfigBean) {
		if (CollectionUtils.isEmpty(getKeyWord())) {
			setKeyWord(new LinkedList<>());
		}
		getKeyWord().add(aKeyWordConfigBean);
	}

	/**
	 * @return the objectProperty
	 */
	public List<String> getObjectProperty() {
		return objectProperty;
	}

	/**
	 * @param objectProperty the objectProperty to set
	 */
	public void setObjectProperty(List<String> objectProperty) {
		this.objectProperty = objectProperty;
	}

	public void addObjectProperty(String strValue) {
		if (CollectionUtils.isEmpty(getObjectProperty())) {
			setObjectProperty(new LinkedList<>());
		}
		if (strValue == null) {
			strValue = "";
		}
		getObjectProperty().add(strValue);
	}

	/**
	 * @return the controlData
	 */
	public List<String> getControlData() {
		return controlData;
	}

	/**
	 * @param controlData the controlData to set
	 */
	public void setControlData(List<String> controlData) {
		this.controlData = controlData;
	}

	public void addControlData(String strValue) {
		if (CollectionUtils.isEmpty(getControlData())) {
			setControlData(new LinkedList<>());
		}
		if (strValue == null) {
			strValue = "";
		}
		getControlData().add(strValue);
	}

	/**
	 * @return the screenShot
	 */
	public List<Boolean> getScreenShot() {
		return screenShot;
	}

	/**
	 * @param screenShot the screenShot to set
	 */
	public void setScreenShot(List<Boolean> screenShot) {
		this.screenShot = screenShot;
	}

	public void addScreenShot(String strValue) {
		if (CollectionUtils.isEmpty(getScreenShot())) {
			setScreenShot(new LinkedList<>());
		}
		boolean isSnapRequired = BooleanUtils.toBoolean(strValue);
		getScreenShot().add(isSnapRequired);
	}
}
