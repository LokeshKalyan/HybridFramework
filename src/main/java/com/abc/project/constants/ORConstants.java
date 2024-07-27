/****************************************************************************
 * File Name 		: ORConstants.java
 * Package			: com.dxc.zurich.constants
 * Author			: pmusunuru2
 * Creation Date	: Feb 26, 2021
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.constants;

/**
 * @author pmusunuru2
 * @since Feb 26, 2021 10:20:37 am
 */
public interface ORConstants {

	String EXEC_JAVA_SCRIPT_SCROLL_BAR_CMD = "arguments[0].scrollIntoView(true);";

	String EXEC_JAVA_SCRIPT_SCROLL_TO_ELEMENT_CMD = "arguments[0].scrollIntoView(%s);";

	String EXEC_JAVA_SCRIPT_SMOOTH_SCROLL_TO_ELEMENT_CMD = "arguments[0].scrollIntoView({block: \"center\",inline: \"center\",behavior: \"smooth\"});";

	String EXEC_JAVA_SCRIPT_SCROLL_TOP_CMD = "window.scrollTo(document.body.scrollHeight, 0)";

	String EXEC_JAVA_SCRIPT_DEVICE_PIXELRATIO_CMD = "return window.devicePixelRatio;";

	String EXEC_JAVA_SCRIPT_SCROLL_TO = "window.scrollBy(%s);";

	String EXEC_JAVA_SCRIPT_CLICK_CMD = "arguments[0].click();";

	String EXEC_JAVA_SCRIPT_CLEAR_CMD = "arguments[0].value ='';";

	String EXEC_JAVA_SCRIPT_ZOOM_BY_PERCENTAGE_CMD = "document.body.style.zoom='%s'";

	String EXEC_JAVA_SCRIPT_MOUSE_OVER_CMD = "var evObj = document.createEvent('MouseEvents');"
			+ "evObj.initMouseEvent(\"mouseover\",true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
			+ "arguments[0].dispatchEvent(evObj);";

	String EXEC_HIGHLIGHT_CMD = "arguments[0].setAttribute('style', 'border: 5px solid %s;');";

	String ATTRIBUTE_NAME_VALUE = "value";

	String ATTRIBUTE_NAME_ALT = "alt";

	String ATTRIBUTE_NAME_ONCLICK = "onClick";

	String ATTRIBUTE_TOOLTIP_VALUE = "ng-reflect-tooltip-value";

	String SIGNOUT_PROPERTY_NAME = "SignOut";

	String LOGOFF_PROPERTY_NAME = "Logoff";

	String BTN_ADVANCE_TEXT_XPATH = "//button[contains(text(), 'Advanced')]";

	String LINK_ACCEPTNCONTINUE = "Accept & Continue";

	String REFRESH_ANIMATE_XPATH = "//div[@class='refresh-animate-wrapper']";

	String OVERLAY_XPATH = "//div[@class='overlay']";

	String LINK_PROCEED_XPATH = "//a[@id='proceed-link']";

	String FORMAT_CONTAINS_TEXT_XPATH = "//*[contains(text(), '%s')]";

	String LBL_FORMAT_TEXT_XPATH = "//label[contains(text(),'%s')]";

	String DUAL_LBL_FORMAT_TEXT_XPATH = "//label[contains(text(),\"%s\")]/parent::div//child::label[contains(text(),'%s')]";

	String DUAL_LBL_FORMAT_TEXT_XPATH_PORTAL = "//span[contains(text(),'%s')]/parent::legend/parent::fieldset//label[./text()='%s']";

	String DUAL_LBL_FORMAT_TEXT_XPATH_IPIPELINE = "//span[contains(text(),'%s')]/parent::div//parent::div//label[./text()='%s']";

	String UW_QUESTION_TEXT_VALIDATION = "//*[contains(text(),'%s')]/following-sibling::*[contains(text(),'%s')]";

	String BTN_UNDER_WRITING_XPATH = "//button[@id='no_Med_1a']";

	String ELMENT_POLICY_NUMBERS = "//div[@class='confirmation-container__Datarow']";

	String ELMENT_POLICY_NUMBERS_SDP = "//ul[@class='sales-confirmation__product-details']/li[contains(text(),'Policy')]";

	String LBL_POLCY_NUMBER_XPATH = "//div[@class='confirmation-container__Datarow'][%d]/div[3]";

	String DIV_QUOTATION_NUMBER_XPATH = "//div[@class='form-content']/div[1]/div[1]/ul/li[1]/span";

	String FORM_PRODUCT_DECISIONS_XPATH = "//div[@class='form-content']/form";

	String DIV_FORM_PRODUCT_DECISIONS_XPATH = "//div[@class='form-content']/form[%d]/div/div[1]/div";

	String DIV_PRODUCT_DECISIONS_XPATH = "//div[@class='form-content']/form[%d]/div/div[1]/div[2]";
	
	String DIV_PRODUCT_DECISIONS_BLOCK_XPATH = "//*[@id='body']/div[3]/div[2]/form[%d]/div/div[2]";
	
	String DIV_PRODUCT_DECISIONS_BLOCK_SHOW_MORE_INFO_XPATH = "//*[@id='body']/div[3]/div[2]/form[%d]/div/div[2]/parent::div//child::a[contains(text(),'Show more information')]/span";
	
	String DIV_PRODUCT_DECISIONS_REFER1_XPATH = "//*[@id=\"body\"]/div[3]/div[2]/form[%d]/div/div[2]/div[2]";
	
	String DIV_PRODUCT_DECISIONS_REFER1_JL__XPATH = "//*[@id=\"body\"]/div[3]/div[2]/form[%d]/div/div[2]/div[3]";
	
	String DIV_PRODUCT_DECISIONS_REVISED_TERMS1_XPATH = "//*[@id=\"body\"]/div[3]/div[2]/form[%d]/div/div[2]/div[2]";
	
	String DIV_PRODUCT_DECISIONS_REVISED_TERMS2_XPATH = "//*[@id=\"body\"]/div[3]/div[2]/form[%d]/div/div[2]/div[1]/div[4]";
	
	String DIV_PRODUCT_DECISIONS_REVISED_TERMS_BENEFITS_BLOCK_XPATH = "//*[@id='body']/div[3]/div[2]/form[%d]/div/div[2]/div[1]/div[2]";
	
	String DIV_PRODUCT_DECISIONS_REVISED_TERMS_BENEFITS_XPATH = "//*[@id='body']/div[3]/div[2]/form[%d]/div/div[2]/div[1]";
	
	String DIV_PRODUCT_DECISIONS_UNABLE_TO_OFFER_TERMS2_XPATH = "//*[@id=\"body\"]/div[3]/div[2]/form[%d]/div/div[2]/div[2]/div/div[%d]";
	
	String DIV_PRODUCT_DECISIONS_UNABLE_TO_OFFER_TERMS_BENEFITS_XPATH = "//*[@id='body']/div[3]/div[2]/form[%d]/div/div[2]/div[1]";
	
	String DIV_PRODUCT_DECISIONS_REFER_BENEFITS_XPATH = "//*[@id='body']/div[3]/div[2]/form[%d]/div/div[2]/div[1]";
	
	String DIV_PRODUCT_DECISIONS_STANDARD_TERMS_BENEFITS_XPATH = "//*[@id='body']/div[3]/div[2]/form[%d]/div/div[2]/div[1]";

	String DIV_QUOTE_SUMMARY_XPATH = "//div[@class='form-content']/form[%d]/div/div[1]/a/span";

	String MUTIPLE_CHECKBOXS_XPATH = "//label[contains(@class,'input-container__label--checkbox')] [contains(text(),'I confirm the information above was obtained by me in relation to the third party')]";

	String MUTIPLE_CHECKBOX_XPATH = "//div[@class='layout-container__main-content']/div[3]/form/div[%d]/div/label";

	String OCCUPATION_XPATH = "//input[@name='Clients[%d].OccupationDescription']";

	String OCCUPATION_CLLIENT1_SPECIAL_XPATH = ".//*[@id='OccupationSearchResults']/li[7]";

	String OCCUPATION_CLLIENT1_PM_XPATH = "//input[@id='OccupationSearchInput']";

	String OCCUPATION_CLLIENT2_PM_XPATH = "//input[@name='Clients[1].OccupationDescription']";

	String OCCUPATION_TEXT_XPATH = "//*[text()='%s']";

	String OCCUPATION_SELECT_XPATH = "%s/parent::div//child::*[text()='%s']";

	String LBL_MEMORABLE_WORD_XPATH = "//label[@for='letter%d']";

	String INPUT_CONFIRM_XPATH = "//input[@name='confirm']";

	String DIV_HEADING_XPATH = ".//*[@id='body']/div[1]/h2";

	String LBL_QUOTE_SUMMARY_NO_POLICIES_XPATH = "//label[contains(.,'Number of policies')]/following-sibling::span[1]";

	String DIV_QUOTE_SUMMARY_APPLICATION_SUMMARY_XPATH = "//*[@id='FormContent']/div/div/ul[1]/li[3]/span";

	String DIV_QUOTE_SUMMARY_QUOTE_APP_REFERENCE_XPATH = "//div[@class='form-content']/div[1]/div[1]/ul/li[1]/span";

	String FORM_QUOTE_SUMMARY_QUOTE_TOT_MON_PREMIUM_XPATH = "//*[@id='FormContent']/form%s/div/div[1]/div[1]";

	String FORM_QUOTE_SUMMARY_PERSONALPROTECTION_DROPDOWN_XPATH = "//*[@id='FormContent']/form%s/div/div[1]/a/span";

	String FORM_QUOTE_SUMMARY_ADVISERINFO_DROPDOWN_XPATH = "//*[@id='FormContent']/form%s/div/div[2]/div[2]/a";

	String FORM_QUOTE_SUMMARY_INITIAL_COMMISSION_VALUE_XPATH = "//*[@id='FormContent']/form%s/div/div[2]/div[2]/div[2]/ul/li[%d]";

	String BACKOFFICE_DD_PAGE_COUNT = "//*[@id='ctl00_WebPageHolder_grdSearchResult_ob_grdSearchResultFooterContainer']/div[2]/div[1]/div[3]";

	String BACKOFFICE_DD_NEXT_PAGE = "//*[@id='ctl00_WebPageHolder_grdSearchResult_ob_grdSearchResultFooterContainer']/div[2]/div[1]/div[2]/img";

	String BACKOFFICE_DD_HOME_PAGE = "//*[@id='ctl00_WebPageHolder_grdSearchResult_ob_grdSearchResultFooterContainer']/div[2]/div[1]/div[5]/img";

	String BACKOFFICE_DD_TABLE = "//*[@id='ctl00_WebPageHolder_grdSearchResult_ob_grdSearchResultBodyContainer']/div[1]/table/tbody";

	String BACKOFFICE_DD_PDF_TABLE = "//*[@id='ctl00_WebPageHolder_grdSearchResult_ob_grdSearchResultBodyContainer']/div[1]/table/tbody/tr[%d]/td[13]/div/div[1]/a";

	String BACKOFFICE_CUSTOMER_NUMBER_TABLE = "//*[@id='ctl00_WebPageHolder_grdSearchResult_ob_grdSearchResultBodyContainer']/div[1]/table/tbody/tr[%d]/td[5]/div/div";

	String YELLOW_COLOR = "yellow";

	String DISABLE_CSS_VALUE = "none";

	String DISABLE_CSS_PROPERTY_VALUE = "pointer-events";

	String DISABLE_CMD = "return arguments[0].hasAttribute(\"disabled\");";

	String FORMAT_TESTDATA_ORPROPERTY_JSONKEY = "OrProperty";

	String FORMAT_TESTDATA_JSONKEY = "TestData";

	String FORMAT_XOFFSET_JSONKEY = "xOff";

	String FORMAT_YOFFSET_JSONKEY = "yOff";

	String BACKOFFICE_DD_EDITDOC_TABLE = "//*[@id='ctl00_WebPageHolder_grdSearchResult_ob_grdSearchResultBodyContainer']/div[1]/table/tbody/tr[%d]/td[14]/div/div[1]/select";

	String BACKOFFICE_DD_FINALIZE_DOC_TABLE = "//*[@id='ctl00_WebPageHolder_gridEditDoc_ob_gridEditDocBodyContainer']/div[1]/table/tbody/tr[%d]/td[9]/div/div[1]/span/input";

	String BACKOFFICE_DD_FINALIZE_NEXT_PAGE = "//*[@id='ctl00_WebPageHolder_gridEditDoc_ob_gridEditDocFooterContainer']/div[2]/div[1]/div[2]/img";

	String BACKOFFICE_DD_FINALIZE_TABLE = "//*[@id='ctl00_WebPageHolder_gridEditDoc_ob_gridEditDocBodyContainer']/div[1]/table/tbody";

	String BACKOFFICE_DD_FINALIZE_PAGE_COUNT = "//*[@id='ctl00_WebPageHolder_gridEditDoc_ob_gridEditDocFooterContainer']/div[2]/div[1]/div[3]";

	String[] DEFAUT_BO_USERS = { "QA User", "ZD FN ZD SN", "Iress FN Iress SN", "smith adviser", "ipipeline IFA sn",
			"Testing One", "Test Company", "Your Adviser", "Practice Manager", "Trustee One", "Trustee Two","Webline FN Webline SN","Hans New Test","Claim Informanat","Direct user","web line"};

	String FORMAT_SELECT_SPL = "%s//following-sibling::*//*[contains(text(),'%s')]";
	
	String CCS_VALUE_COLOUR ="color";
	
	String CCS_VALUE_BACKGROUND_COLOUR ="background-color";

}
