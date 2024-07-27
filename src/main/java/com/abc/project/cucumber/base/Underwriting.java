/****************************************************************************
 * File Name 		: Underwriting.java
 * Package			: com.dxc.zurich.cucumber.base
 * Author			: pmusunuru2
 * Creation Date	: Jul 11, 2022
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.cucumber.base;

import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import com.abc.project.base.ControllerScript;
import com.abc.project.base.TestStep;
import com.abc.project.beans.AppEnvConfigBean;
import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.beans.KeyWordConfigBean;
import com.abc.project.beans.TestDataBean;
import com.abc.project.beans.TestSuiteBean;
import com.abc.project.config.AppConfig;
import com.abc.project.config.MasterConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.RunTimeDataConstants;
import com.abc.project.enums.AppRunMode;
import com.abc.project.enums.BrowserType;
import com.abc.project.enums.Browsers;
import com.abc.project.enums.IdentificationType;
import com.abc.project.enums.KeyWord;
import com.abc.project.reports.ALMTestCaseReport;
import com.abc.project.reports.ConsolidateTestReport;
import com.abc.project.reports.TestStepReport;
import com.abc.project.runners.BrowserRunner;
import com.abc.project.runners.DeskTopWebBrowserRunner;
import com.abc.project.runners.MobileWebBrowserRunner;
import com.abc.project.utils.PropertyHandler;
import com.abc.project.utils.RunTimeDataUtils;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * @author pmusunuru2
 * @since Jul 11, 2022 11:00:51 am
 */
public class Underwriting {

	private BrowserRunner aBrowserRunner;
	private TestSuiteBean aTestSuiteBean;
	private ControllerScript aControllerScript;
	WebDriver driver;
	Select dropDown;
	KeyWordConfigBean aKeyWordConfigBean;
	String stepDescription;
	public String testScenarioName;
	
	@Given("^User initiates browser$")
	public void user_initiates_browser() throws Throwable {
		System.setProperty(AppConstants.APP_NAME, "Cucumber_UnderWriting_Adviser");
		AppEnvConfigBean aPPRunEnv = MasterConfig.getInstance().getAppEnvConfigBean();
		aPPRunEnv.setAppRunMode(AppRunMode.NORMAL);
		String strConfigPopFile = String.format("%s%s%s", AppConstants.APP_ENV_PROPERTIES_LOC,
				aPPRunEnv.getPropertyName(), AppConstants.PROPERTIES_FILE_SUFFIX);
		PropertyHandler.mergeExternalResourceBundle(strConfigPopFile, AppConstants.APP_PROPERTIES_NAME);
	}

	@When("^User Selects desired browser$")
	public void user_selects_desired_browser(DataTable dataTable) throws Throwable {
		List<List<String>> data = dataTable.cells();
		AppConfig aAppConfig = AppConfig.getInstance();
		LinkedHashSet<String> stBrowserDisplayName = new LinkedHashSet<>();
		stBrowserDisplayName.add(data.get(0).get(0));
		List<BrowsersConfigBean> lstBrowers = aAppConfig.getBrowserConfig(stBrowserDisplayName);
		BrowsersConfigBean aBrowsersConfigBean = lstBrowers.get(0);
		Browsers aBrowsers = aBrowsersConfigBean.getBrowser();
		BrowserType aBrowserType = aBrowsers.getBrowserType();
		if (aBrowserType == BrowserType.DESKTOP_WEB || aBrowserType == BrowserType.DESKTOP_NATIVE) {
			aBrowserRunner = new DeskTopWebBrowserRunner(aBrowsersConfigBean);
		} else {
			aBrowserRunner = new MobileWebBrowserRunner(aBrowsersConfigBean);
		}
		aBrowserRunner.initizeDrivers();
		driver = aBrowserRunner.getDriver();
		aTestSuiteBean = new TestSuiteBean();
		aTestSuiteBean.setScenarioName("UnderWriting Test");
		aTestSuiteBean.setBrowsersConfigBean(aBrowsersConfigBean);
		aControllerScript = new ControllerScript(aTestSuiteBean, driver);
	}
	
	@Given("^User is on Home Page with specified Scenarioname,Description and environment \"([^\"]*)\",\"([^\"]*)\",\"([^\"]*)\"$")
	public void user_is_on_Home_Page_with_specified_Scenarioname_Description_and_environment(String testScenarioName, String stepDescription, String environment) throws Throwable {
		Thread.sleep(2000);
		this.testScenarioName=testScenarioName; 
		this.stepDescription=stepDescription;
		aTestSuiteBean.setTestDataSheetName(AppConstants.CONTROLLER_DATA_SHEETNAME);
		aTestSuiteBean.setScenarioName(testScenarioName);
		aTestSuiteBean.setDescription(stepDescription);
		String strTestData = environment;
		String strLogMessage = "Loging Into HomePage";
		String strObJPropName = "";
		String strReportKeyWord = "None";
        aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.OPENURL.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		capureScreenshot(strLogMessage);
	}

	@Given("^User enters \"([^\"]*)\" and \"([^\"]*)\"$")
		public void user_enters_and(String usernane, String password) throws Throwable {		
			String strTestData = usernane;
			String strLogMessage = "Loging Into HomePage";
			String strObJPropName = "UserName";
			String strReportKeyWord = "None";
			 aKeyWordConfigBean = new KeyWordConfigBean();
			 //define the keyword 
			aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
			//define the object type
			aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
			String unameResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
					strObJPropName, aKeyWordConfigBean);
			checkResult(unameResult, "user_enters_Credentials_to_LogIn");
			strObJPropName = "Password";
			strTestData = password;
			String pswdResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
					strObJPropName, aKeyWordConfigBean);
			checkResult(pswdResult, "user_enters_Credentials_to_LogIn");
			Thread.sleep(1000);
			capureScreenshot(strLogMessage);
			
			strObJPropName = "Logon";
			strTestData = "Yes";
			 aKeyWordConfigBean = new KeyWordConfigBean();
			aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
			aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
			String loginResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
					strObJPropName, aKeyWordConfigBean);
			checkResult(loginResult, "user_enters_Credentials_to_LogIn");
			Thread.sleep(2000);
			capureScreenshot(strLogMessage);
		}

	@When("^User is on Home Page$")
	public void user_is_on_Home_Page(DataTable arg1) throws Throwable {
		List<List<String>> data = arg1.cells();
		Thread.sleep(2000);
		String strTestData = data.get(0).get(0);
		String strLogMessage = "Loging Into HomePage";
		String strObJPropName = "";
		String strReportKeyWord = "None";
        aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.OPENURL.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
	}

	@When("^User enters Credentials to LogIn$")
	public void user_enters_Credentials_to_LogIn(DataTable arg1) throws Throwable {
		List<List<String>> data = arg1.cells();
		String strTestData = data.get(0).get(0);
		String strLogMessage = "Loging Into HomePage";
		String strObJPropName = "UserName";
		String strReportKeyWord = "None";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		 //define the keyword 
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
		//define the object type
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		strObJPropName = "Password";
		strTestData = data.get(0).get(1);
		aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		Thread.sleep(1000);
		
		strObJPropName = "Logon";
		strTestData = "Yes";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		Thread.sleep(2000);
	}

	@Then("^Advisory dashboard Should display$")
	public void advisory_dashboard_Should_display() throws Throwable {
		String strLogMessage = "Advisory dashboard displayed Successfully";
		System.out.println("Advisory dashboard displayed Successfully");
		capureScreenshot(strLogMessage);
	}

	@Then("^Click On Start Quote button$")
	public void click_On_Start_Quote_button() throws Throwable {

		Thread.sleep(500);
		String strObJPropName = "AdvisorPortal_StartQuote";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Tracking overview - Adviser portal";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String startquoteResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(startquoteResult, "click_On_Start_Quote_button");
		Thread.sleep(500);
	}

	@Then("^Click the Personal Protection Option$")
	public void click_the_Personal_Protection_Option() throws Throwable {
		String strObJPropName = "AdvisorPortal_PersonalProtection";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Start quote";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String ppResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(ppResult, "click_the_Personal_Protection_Option");
		Thread.sleep(500);
	}

	@Then("^Select the Agency value from the Dropdown$")
	public void select_the_Agency_value_from_the_Dropdown(DataTable arg1) throws Throwable {
		List<List<String>> data = arg1.cells();
		Thread.sleep(500);
		String strTestData = data.get(0).get(0);
		String strLogMessage = "Panel selection";
		String strObJPropName = "PanelSelection_Agency_Select";
		String strReportKeyWord = "None";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.SELECT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String agencyResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(agencyResult, "select_the_Agency_value_from_the_Dropdown");
	}

	@Given("^Select the panel\"([^\"]*)\" value from the Dropdown$")
	public void select_the_panel_value_from_the_Dropdown(String panelselection) throws Throwable {
		Thread.sleep(500);
		String strTestData = panelselection;
		String strLogMessage = "Panel selection";
		String strObJPropName = "PanelSelection_Panel";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.SELECT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String panelResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(panelResult, "select_the_panel_value_from_the_Dropdown");
	}
	
	@Then("^Click on Interactive Radio Button$")
	public void click_on_Interactive_Radio_Button() throws Throwable {
		String strObJPropName = "PanelSelection_Interactive";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Panel selection";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String interactiveResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(interactiveResult, "click_on_Interactive_Radio_Button");
	}

	@Then("^Click On Panel Continue Button$")
	public void click_On_Panel_Continue_Button() throws Throwable {
		Thread.sleep(500);
		String strObJPropName = "PanelSelection_Next";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Panel selection";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String continueButtonResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(continueButtonResult, "click_On_Panel_Continue_Button");
	}

	@Given("^Select the title\"([^\"]*)\" from the Dropdown$")
	public void select_the_title_from_the_Dropdown(String title) throws Throwable {
		Thread.sleep(500);
		String strTestData = title;
		String strLogMessage = "Life assured";
		String strObJPropName = "ClientSelection_Client1_Title";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.SELECT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String titleDropdownResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(titleDropdownResult, titleDropdownResult);
	}

	@Given("^Enter the firstname\"([^\"]*)\" of the Client$")
	public void enter_the_firstname_of_the_Client(String firstname) throws Throwable {	
		String strTestData = firstname;
		String strLogMessage = "Life assured";
		String strObJPropName = "ClientSelection_Client1_FirstName";
		aKeyWordConfigBean = new KeyWordConfigBean();			
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT_RANDOM_STRING.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String firstnameResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, RunTimeDataConstants.FIRST_NAME, strTestData,
				strObJPropName, aKeyWordConfigBean);	
		checkResult(firstnameResult, "enter_the_firstname_of_the_Client");
	}

	@Given("^Enter the lastname\"([^\"]*)\" of the Client$")
	public void enter_the_lastname_of_the_Client(String lastname) throws Throwable {
		String strTestData = lastname;
		String strLogMessage = "Life assured";
		String strObJPropName = "ClientSelection_Client1_LastName";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT_RANDOM_STRING.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String lastnameResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, RunTimeDataConstants.LAST_NAME, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(lastnameResult, "enter_the_lastname_of_the_Client");
	}

	@Given("^Enter the DOB as \"([^\"]*)\" , \"([^\"]*)\" , \"([^\"]*)\"$")
	public void enter_the_DOB_as(String date, String month, String year) throws Throwable {
		String strTestData = date;
		String strLogMessage = "Life assured";
		String strObJPropName = "ClientSelection_Client1_DayofBirth";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String dateResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(dateResult, "error while entering DOB");
		
		strTestData = month;
		strObJPropName = "ClientSelection_Client1_MonthofBirth";
		String monthResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(monthResult, "error while entering DOB");
		
		strTestData = year;
		strObJPropName = "ClientSelection_Client1_YearofBirth";
		String yearResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(yearResult, "error while entering DOB");
	}

	@Then("^Click the Gender Radio Button as Male$")
	public void click_the_Gender_Radio_Button_as_Male() throws Throwable {
		String strObJPropName = "ClientSelection_Client1_Gender_Male";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Life assured";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String genderResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(genderResult, "click_the_Gender_Radio_Button_as_Male");
	}
	
	@Given("^Select the smokerstatus\"([^\"]*)\" from the Dropdown$")
	public void select_the_smokerstatus_from_the_Dropdown(String smokerstatus) throws Throwable {
		String strTestData = smokerstatus;
		String strLogMessage = "Life assured";
		String strObJPropName = "ClientSelection_Client1_SmokerStatus";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.SELECT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String smokerResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(smokerResult, "select_the_smokerstatus_from_the_Dropdown");
	}

	@Given("^Enter the occupation\"([^\"]*)\" of the Client$")
	public void enter_the_occupation_of_the_Client(String occupation) throws Throwable {
		String strTestData = occupation;
		String strLogMessage = "Life assured";
		String strObJPropName = "ClientSelection_Client1_Occupation";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLIENT_SPL_OCCUPATION.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String occupationResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(occupationResult, "enter_the_occupation_of_the_Client");
	}

	@Given("^Enter the postcode\"([^\"]*)\" of the Client$")
	public void enter_the_postcode_of_the_Client(String postcode) throws Throwable {
		Thread.sleep(500);
		String strTestData = postcode;
		String strLogMessage = "Life assured";
		String strObJPropName = "ClientSelection_Client1_Postcode";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String postcodeResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(postcodeResult, "enter_the_postcode_of_the_Client");
		
	}

	@Then("^Click On Continue Button$")
	public void click_On_Continue_Button() throws Throwable {
		String strObJPropName = "ClientSelection_Continue";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Life assured";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String continueResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(continueResult, "click_On_Continue_Button");

	}
	
	@Given("^select the Personal product as product selection$")
	public void select_the_Personal_product_as_product_selection() throws Throwable {
		String strObJPropName = "ProductSelection_Add_PersProtProduct";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Product Selection";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String prodselResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(prodselResult, "select_the_Personal_product_as_product_selection");
	}

	@Given("^Click On Product selection Continue Button$")
	public void click_On_Product_selection_Continue_Button() throws Throwable {
		String strObJPropName = "ProductSelection_ZLIProduct_Next";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Product Selection";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String prodContinueResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(prodContinueResult, "click_On_Product_selection_Continue_Button");
	}

	@Given("^Enter sumassured\"([^\"]*)\" details$")
	public void enter_sumassured_details(String sumassured) throws Throwable {
		Thread.sleep(500);
		String strTestData = sumassured;
		String strLogMessage = "Product details";
		String strObJPropName = "ProductDetails_ZLIProduct_SumAssured";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String sumassuredResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(sumassuredResult, "enter_sumassured_details");
	}

	@Given("^Enter term\"([^\"]*)\" in years$")
	public void enter_term_in_years(String term) throws Throwable {
		Thread.sleep(500);
		String strTestData = term;
		String strLogMessage = "Product details";
		String strObJPropName = "ProductDetails_ZLIProduct_Term";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String termResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(termResult, "enter_term_in_years");
	}

	@Given("^select Premium frequency as Monthly$")
	public void select_Premium_frequency_as_Monthly() throws Throwable {
		String strObJPropName = "ProductDetails_ZLIProduct_PremFreq_Monthly";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Product Selection";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String frequencyResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(frequencyResult, "select_Premium_frequency_as_Monthly");
	}

	@Given("^select Waver of premium as No$")
	public void select_Waver_of_premium_as_No() throws Throwable {
		String strObJPropName = "ProductDetails_ZLIProduct_Life1_Waiver_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Product Selection";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String wopResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(wopResult, "select_Waver_of_premium_as_No");
	}

	@Given("^select Multi fracture cover as No$")
	public void select_Multi_fracture_cover_as_No() throws Throwable {
		String strObJPropName = "ProductDetails_ZLIProduct_FractureCover_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Product Selection";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String mfcResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(mfcResult, "select_Multi_fracture_cover_as_No");
	}

	@Given("^select Conversion option as No$")
	public void select_Conversion_option_as_No() throws Throwable {
		String strObJPropName = "ProductDetails_ZLIProduct_Conversion_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Product Selection";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String convResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(convResult, "select_Conversion_option_as_No");
	}

	@Given("^Click on Get quote Button$")
	public void click_on_Get_quote_Button() throws Throwable {
		String strObJPropName = "ProductDetails_GetQuote";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Product Selection";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String getQuoteResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(getQuoteResult, "click_on_Get_quote_Button");
	}

	@Given("^Capture the Application reference Number$")
	public void capture_the_Application_reference_Number() throws Throwable {
		String strObJPropName = "QuoteSummary_ApplicationReference";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Product Selection";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CAPTURE_REF_NUMS.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String apprefResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(apprefResult, "capture_the_Application_reference_Number");
	}

	@Given("^Check the Confirm all statements Check Box$")
	public void check_the_Confirm_all_statements_Check_Box() throws Throwable {
		String strObJPropName = "QuoteSummary_AcceptCheckBox";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Product Selection";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String cnfrmCheckResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(cnfrmCheckResult, "check_the_Confirm_all_statements_Check_Box");
	}

	@Given("^click on Quote summary Apply Button$")
	public void click_on_Quote_summary_Apply_Button() throws Throwable {
		String strObJPropName = "QuoteSummary_Apply";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Product Selection";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String summaryApplyResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(summaryApplyResult, "click_on_Quote_summary_Apply_Button");
	}

	@Given("^Select the nationality\"([^\"]*)\"$")
	public void select_the_nationality(String nationality) throws Throwable {
		String strTestData = nationality;
		String strLogMessage = "Life assured";
		String strObJPropName = "ClientDetails_Nationality";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.SELECT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String nationalityResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(nationalityResult, "select_the_nationality");
	}

	@Given("^Enter emailaddress\"([^\"]*)\"$")
	public void enter_emailaddress(String email) throws Throwable {
		Thread.sleep(500);
		String strTestData = email;
		String strLogMessage = "Product details";
		String strObJPropName = "ClientDetails_Email";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String emailaddressResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(emailaddressResult, "enter_emailaddress");
	}

	@Given("^Enter phonenumber\"([^\"]*)\"$")
	public void enter_phonenumber(String phonenumber) throws Throwable {
		Thread.sleep(500);
		String strTestData = phonenumber;
		String strLogMessage = "Product details";
		String strObJPropName = "ClientDetails_DaytimeTelNumber";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String phonenoresult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(phonenoresult, "enter_phonenumber");
	}

	@Given("^Click on Find Address Button of Post Code$")
	public void click_on_Find_Address_Button_of_Post_Code() throws Throwable {
		String strObJPropName = "ClientDetails_SearchAddress";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Product Selection";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String findaddResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(findaddResult, "click_on_Find_Address_Button_of_Post_Code");
	}

	@Given("^Select the First address form the List$")
	public void select_the_First_address_form_the_List() throws Throwable {
		String strObJPropName = "ClientDetails_SearchAddress_Select1st";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Product Selection";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String slctaddResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(slctaddResult, "select_the_First_address_form_the_List");
	}

	@Given("^click on Add address manually hyperlink$")
	public void click_on_Add_address_manually_hyperlink() throws Throwable {
		String strObJPropName = "LifeAssuredDetails_GPDetails_AddAddressmanually";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Product Selection";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String addressmanuallyResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(addressmanuallyResult, "click_on_Add_address_manually_hyperlink");
	}

	@Given("^Enter surgeryname\"([^\"]*)\" of GP deatils$")
	public void enter_surgeryname_of_GP_deatils(String surgeryname) throws Throwable {
		Thread.sleep(500);
		String strTestData = surgeryname;
		String strLogMessage = "Product details";
		String strObJPropName = "LifeAssuredDetails_GPDetails_AddAddressmanually_SurgeryName";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String surgeryResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(surgeryResult, "enter_surgeryname_of_GP_deatils");
	}

	@Given("^Enter telephonenumber\"([^\"]*)\" of GP deatils$")
	public void enter_telephonenumber_of_GP_deatils(String telephone) throws Throwable {
		Thread.sleep(500);
		String strTestData = telephone;
		String strLogMessage = "Product details";
		String strObJPropName = "LifeAssuredDetails_GPDetails_AddAddressmanually_Telephone";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String telephoneResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(telephoneResult, "enter_telephonenumber_of_GP_deatils");
	}

	@Given("^Enter postcode\"([^\"]*)\" of GP deatils$")
	public void enter_postcode_of_GP_deatils(String postcode) throws Throwable {
		Thread.sleep(500);
		String strTestData = postcode;
		String strLogMessage = "Product details";
		String strObJPropName = "LifeAssuredDetails_GPDetails_AddAddressmanually_PostCode";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String postcodeResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(postcodeResult, "enter_postcode_of_GP_deatils");
	}

	@Given("^Enter addressline\"([^\"]*)\" of GP deatils$")
	public void enter_addressline_of_GP_deatils(String addressline) throws Throwable {
		Thread.sleep(500);
		String strTestData = addressline;
		String strLogMessage = "Product details";
		String strObJPropName = "LifeAssuredDetails_GPDetails_AddAddressmanually_AddressLine1";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String addrLineResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(addrLineResult, "enter_addressline_of_GP_deatils");
	}

	@Given("^Enter townorcity\"([^\"]*)\" of GP deatils$")
	public void enter_townorcity_of_GP_deatils(String townorcity) throws Throwable {
		Thread.sleep(500);
		String strTestData = townorcity;
		String strLogMessage = "Product details";
		String strObJPropName = "LifeAssuredDetails_GPDetails_AddAddressmanually_Town";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String cityResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(cityResult, "enter_townorcity_of_GP_deatils");
	}

	@Given("^Enter country\"([^\"]*)\" of GP deatils$")
	public void enter_country_of_GP_deatils(String country) throws Throwable {
		Thread.sleep(500);
		String strTestData = country;
		String strLogMessage = "Product details";
		String strObJPropName = "LifeAssuredDetails_GPDetails_AddAddressmanually_Country";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String countryResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(countryResult, "enter_country_of_GP_deatils");
	}

	@Given("^click on Life Assured details Continue Buttton$")
	public void click_on_Life_Assured_details_Continue_Buttton() throws Throwable {
		String strObJPropName = "GPDetails_ContinueButton";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Product Selection";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String LAContinueResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(LAContinueResult, "click_on_Life_Assured_details_Continue_Buttton");
	}

	@Given("^click on Policy holderss Continue Buttton$")
	public void click_on_Policy_holderss_Continue_Buttton() throws Throwable {
		String strObJPropName = "Policyholders_ContinueButton";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Product Selection";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String phContinueResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(phContinueResult, "click_on_Policy_holderss_Continue_Buttton");
	}
	
	@Given("^Select Yes for Do you consent to your medical records being shared with Zurich as explained in the AMRA\\?$")
	public void select_Yes_for_Do_you_consent_to_your_medical_records_being_shared_with_Zurich_as_explained_in_the_AMRA() throws Throwable {
		String strObJPropName = "UW_MedicalConsent_DoYouConsentToYourMedicalRecords_Yes";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Medical Consent";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_Yes_for_Do_you_consent_to_your_medical_records_being_shared_with_Zurich_as_explained_in_the_AMRA");
	}

	@Given("^Select No for If we do ask your doctor for a medical report, would you like to see it before your doctor sends it to us\\?$")
	public void select_No_for_If_we_do_ask_your_doctor_for_a_medical_report_would_you_like_to_see_it_before_your_doctor_sends_it_to_us() throws Throwable {
		String strObJPropName = "UW_MedicalConsent_AskYourDoctorForAMedicalReport_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Medical Consent";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_If_we_do_ask_your_doctor_for_a_medical_report_would_you_like_to_see_it_before_your_doctor_sends_it_to_us");
	}

	@Given("^Click on Medical Consent Next Button$")
	public void click_on_Medical_Consent_Next_Button() throws Throwable {
		String strObJPropName = "Underwriting_NextButton";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Medical Consent";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwContinueResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwContinueResult, "click_on_Medical_Consent_Next_Button");
	}

	@Given("^Enter your height as \"([^\"]*)\" , \"([^\"]*)\"$")
	public void enter_your_height_as(String ft, String inches) throws Throwable {
		Thread.sleep(500);
		String strTestData = ft;
		String strLogMessage = "Height, weight and habits";
		String strObJPropName = "Underwriting_Height_Feet";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String heightFeetResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(heightFeetResult, "enter_your_height_as");
		
		strTestData = inches;
		strObJPropName = "Underwriting_Height_Inches";
		String heightInchesResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(heightInchesResult, "enter_your_height_as");
	}

	@Given("^Enter your weight as \"([^\"]*)\" , \"([^\"]*)\"$")
	public void enter_your_weight_as(String st, String lbs) throws Throwable {
		Thread.sleep(500);
		String strTestData = st;
		String strLogMessage = "Height, weight and habits";
		String strObJPropName = "Underwriting_Weight_Stone";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String waitStoneResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(waitStoneResult, "enter_your_weight_as");
		
		strTestData = lbs;
		strObJPropName = "Underwriting_Weight_Pounds";
		String waiLbsResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(waiLbsResult, "enter_your_weight_as");
	}

	@Given("^Select No for Do you drink alcohol\\?$")
	public void select_No_for_Do_you_drink_alcohol() throws Throwable {
		String strObJPropName = "UW_Habits_Doyoudrinkalchohol_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Height, weight and habits";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_Do_you_drink_alcohol");
	}

	@Given("^Select No for have you attended or been advised to attend an alcohol support group or counselling, or have you been told you have any liver damage\\?$")
	public void select_No_for_have_you_attended_or_been_advised_to_attend_an_alcohol_support_group_or_counselling_or_have_you_been_told_you_have_any_liver_damage() throws Throwable {
		String strObJPropName = "UW_Habits_Haveyoubeeneveradvisedortreated_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Height, weight and habits";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_have_you_attended_or_been_advised_to_attend_an_alcohol_support_group_or_counselling_or_have_you_been_told_you_have_any_liver_damage");
		
	}

	@Given("^Select No for have you used recreational drugs such as cannabis, ecstasy, cocaine, heroin, amphetamines, or anabolic steroids\\?$")
	public void select_No_for_have_you_used_recreational_drugs_such_as_cannabis_ecstasy_cocaine_heroin_amphetamines_or_anabolic_steroids() throws Throwable {
		String strObJPropName = "UW_Habits_Inlast10yearsuseddrugs_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Height, weight and habits";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_have_you_used_recreational_drugs_such_as_cannabis_ecstasy_cocaine_heroin_amphetamines_or_anabolic_steroids");
	}

	@Given("^Click on Height & Weight Next Button$")
	public void click_on_Height_Weight_Next_Button() throws Throwable {
		String strObJPropName = "Underwriting_NextButton";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Height, weight and habits";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwNextResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwNextResult, "click_on_Height_Weight_Next_Button");
	}

	@Given("^Select No for Does your occupation involve working externally at heights over Fifty ft\\?$")
	public void select_No_for_Does_your_occupation_involve_working_externally_at_heights_over_Fifty_ft() throws Throwable {
		String strObJPropName = "UW_Occupation_OccInvolvesWorkingExtremeHeight_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Occupation";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_Does_your_occupation_involve_working_externally_at_heights_over_Fifty_ft");
	}

	@Given("^Click on occupationt Next Button$")
	public void click_on_occupationt_Next_Button() throws Throwable {
		String strObJPropName = "UW_Occupation_NextButton";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Occupation";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "click_on_occupationt_Next_Button");
	}

	@Given("^Select No for diabetes, raised blood glucose, or sugar in the urine\\?$")
	public void select_No_for_diabetes_raised_blood_glucose_or_sugar_in_the_urine() throws Throwable {
		String strObJPropName = "UW_PastHealth_Diabetesorsugarurine_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Past Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_diabetes_raised_blood_glucose_or_sugar_in_the_urine");
	}

	@Given("^Select No for any heart disease or disorder, such as heart attack or any other heart condition\\?$")
	public void select_No_for_any_heart_disease_or_disorder_such_as_heart_attack_or_any_other_heart_condition() throws Throwable {
		String strObJPropName = "UW_PastHealth_Anyheartdeceaseordisorder_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Past Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_any_heart_disease_or_disorder_such_as_heart_attack_or_any_other_heart_condition");
	}

	@Given("^Select No for a disorder or abnormality of the blood vessels or arteries such as narrowing, blood clots or deep vein thrombosis \\(DVT\\)\\?$")
	public void select_No_for_a_disorder_or_abnormality_of_the_blood_vessels_or_arteries_such_as_narrowing_blood_clots_or_deep_vein_thrombosis_DVT() throws Throwable {
		String strObJPropName = "UW_PastHealth_Adisorderofbloodvessels_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Past Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_a_disorder_or_abnormality_of_the_blood_vessels_or_arteries_such_as_narrowing_blood_clots_or_deep_vein_thrombosis_DVT");
	}

	@Given("^Select No for a stroke, transient ischaemic attack \\(TIA\\), brain aneurysm or any damage or surgery to the brain\\?$")
	public void select_No_for_a_stroke_transient_ischaemic_attack_TIA_brain_aneurysm_or_any_damage_or_surgery_to_the_brain() throws Throwable {
		String strObJPropName = "UW_PastHealth_Anybraindiseaseordisorder_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Past Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_a_stroke_transient_ischaemic_attack_TIA_brain_aneurysm_or_any_damage_or_surgery_to_the_brain");
	}

	@Given("^Select No for cancer, leukaemia, Hodgkins disease, melanoma, lymphoma, brain or spinal tumours or growths\\?$")
	public void select_No_for_cancer_leukaemia_Hodgkins_disease_melanoma_lymphoma_brain_or_spinal_tumours_or_growths() throws Throwable {
		String strObJPropName = "UW_PastHealth_CancerLukemia_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Past Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_cancer_leukaemia_Hodgkins_disease_melanoma_lymphoma_brain_or_spinal_tumours_or_growths");
	}

	@Given("^Select No for schizophrenia, bi-polar disorder, manic depression,any other mental health condition hospital or referral to a psychiatrist\\?$")
	public void select_No_for_schizophrenia_bi_polar_disorder_manic_depression_any_other_mental_health_condition_hospital_or_referral_to_a_psychiatrist() throws Throwable {
		String strObJPropName = "UW_PastHealth_SchizophreniaBbipolardisorder_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Past Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_schizophrenia_bi_polar_disorder_manic_depression_any_other_mental_health_condition_hospital_or_referral_to_a_psychiatrist");
	}

	@Given("^Select No for any disorder of the nervous system such as multiple sclerosis, dementia or memory loss\\?$")
	public void select_No_for_any_disorder_of_the_nervous_system_such_as_multiple_sclerosis_dementia_or_memory_loss() throws Throwable {
		String strObJPropName = "UW_PastHealth_Anydiseaseordisorderofnervoussystem_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Past Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_any_disorder_of_the_nervous_system_such_as_multiple_sclerosis_dementia_or_memory_loss");
	}

	@Given("^Select No for any disease or disorder of the liver or pancreas such as any form of hepatitis, cirrhosis or pancreatitis\\?$")
	public void select_No_for_any_disease_or_disorder_of_the_liver_or_pancreas_such_as_any_form_of_hepatitis_cirrhosis_or_pancreatitis() throws Throwable {
		String strObJPropName = "UW_PastHealth_Anydiseaseordisorderofliverorpancreas_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Past Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_any_disease_or_disorder_of_the_liver_or_pancreas_such_as_any_form_of_hepatitis_cirrhosis_or_pancreatitis");
	}

	@Given("^Select No for a positive test for HIV or are you awaiting the results of an HIV test\\?$")
	public void select_No_for_a_positive_test_for_HIV_or_are_you_awaiting_the_results_of_an_HIV_test() throws Throwable {
		String strObJPropName = "UW_PastHealth_PositivetestforHIV_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Past Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_a_positive_test_for_HIV_or_are_you_awaiting_the_results_of_an_HIV_test");
	}

	@Given("^Click on pasthealth Next Button$")
	public void click_on_pasthealth_Next_Button() throws Throwable {
		String strObJPropName = "UW_Occupation_NextButton";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Past Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwNextResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwNextResult, "click_on_pasthealth_Next_Button");
	}

	@Given("^Select No for raised blood pressure or raised cholesterol\\?$")
	public void select_No_for_raised_blood_pressure_or_raised_cholesterol() throws Throwable {
		String strObJPropName = "UW_RecentHealth_RaisedBPorCholestrol_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Recent Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_raised_blood_pressure_or_raised_cholesterol");
	}

	@Given("^Select No for anxiety, stress, depression, chronic fatigue, obsessive compulsive disorder, or other mental health condition\\?$")
	public void select_No_for_anxiety_stress_depression_chronic_fatigue_obsessive_compulsive_disorder_or_other_mental_health_condition() throws Throwable {
		String strObJPropName = "UW_RecentHealth_AnxietyStressDepression_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Recent Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_anxiety_stress_depression_chronic_fatigue_obsessive_compulsive_disorder_or_other_mental_health_condition");
	}

	@Given("^Select No for any respiratory or lung disease or disorder such as asthma, bronchitis, or COPD\\?$")
	public void select_No_for_any_respiratory_or_lung_disease_or_disorder_such_as_asthma_bronchitis_or_COPD() throws Throwable {
		String strObJPropName = "UW_RecentHealth_AnyRespiratoryOrLungDisease_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Recent Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_any_respiratory_or_lung_disease_or_disorder_such_as_asthma_bronchitis_or_COPD");
	}

	@Given("^Select No for any kidney disease or disorder such as any form of nephritis, cysts or recurrent kidney stones\\?$")
	public void select_No_for_any_kidney_disease_or_disorder_such_as_any_form_of_nephritis_cysts_or_recurrent_kidney_stones() throws Throwable {
		String strObJPropName = "UW_RecentHealth_AnyKidneyDiseaseOrDisorder_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Recent Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_any_kidney_disease_or_disorder_such_as_any_form_of_nephritis_cysts_or_recurrent_kidney_stones");
	}

	@Given("^Select No for any thyroid disorder\\?$")
	public void select_No_for_any_thyroid_disorder() throws Throwable {
		String strObJPropName = "UW_RecentHealth_AnyThyroidDisorder_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Recent Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_any_thyroid_disorder");
	}

	@Given("^Select No for any disease or disorder of the stomach, bowel or digestive system such as ulcers, ulcerative colitis, or Crohns disease\\?$")
	public void select_No_for_any_disease_or_disorder_of_the_stomach_bowel_or_digestive_system_such_as_ulcers_ulcerative_colitis_or_Crohns_disease() throws Throwable {
		String strObJPropName = "UW_RecentHealth_AnyDiseaseOrDisorderOfStomach_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Recent Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_any_disease_or_disorder_of_the_stomach_bowel_or_digestive_system_such_as_ulcers_ulcerative_colitis_or_Crohns_disease");
	}

	@Given("^Select No for any tremor, numbness, loss of feeling or tingling in the limbs or face, blurred or double vision, seizure, or loss of muscle power\\?$")
	public void select_No_for_any_tremor_numbness_loss_of_feeling_or_tingling_in_the_limbs_or_face_blurred_or_double_vision_seizure_or_loss_of_muscle_power() throws Throwable {
		String strObJPropName = "UW_RecentHealth_AnyTremorNumbness_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Recent Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_any_tremor_numbness_loss_of_feeling_or_tingling_in_the_limbs_or_face_blurred_or_double_vision_seizure_or_loss_of_muscle_power");
	}

	@Given("^Select No for any disease or disorder of the prostate or testicle, such as raised Prostate Specific Antigen \\(PSA\\)\\?$")
	public void select_No_for_any_disease_or_disorder_of_the_prostate_or_testicle_such_as_raised_Prostate_Specific_Antigen_PSA() throws Throwable {
		String strObJPropName = "UW_RecentHealth_AnyDiseaseOrDisorderOfProstate_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Recent Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_any_disease_or_disorder_of_the_prostate_or_testicle_such_as_raised_Prostate_Specific_Antigen_PSA");
	}

	@Given("^Select No for any disease or disorder of the bladder or urinary tract such as recurrent infections or protein or blood in the urine\\?$")
	public void select_No_for_any_disease_or_disorder_of_the_bladder_or_urinary_tract_such_as_recurrent_infections_or_protein_or_blood_in_the_urine() throws Throwable {
		String strObJPropName = "UW_RecentHealth_AnyDiseaseOrDisorderOfBladder_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Recent Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_any_disease_or_disorder_of_the_bladder_or_urinary_tract_such_as_recurrent_infections_or_protein_or_blood_in_the_urine");
	}

	@Given("^Select No for any lump, cyst, growth or polyp, or a mole or freckle that has bled or changed in appearance\\?$")
	public void select_No_for_any_lump_cyst_growth_or_polyp_or_a_mole_or_freckle_that_has_bled_or_changed_in_appearance() throws Throwable {
		String strObJPropName = "UW_RecentHealth_LumpCyst_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Recent Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_any_lump_cyst_growth_or_polyp_or_a_mole_or_freckle_that_has_bled_or_changed_in_appearance");
	}

	@Given("^Select No for anaemia or other blood disorders such as haemochromatosis or haemophilia\\?$")
	public void select_No_for_anaemia_or_other_blood_disorders_such_as_haemochromatosis_or_haemophilia() throws Throwable {
		String strObJPropName = "UW_RecentHealth_AnaemiaOrOtherBloodDisorders_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Recent Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_anaemia_or_other_blood_disorders_such_as_haemochromatosis_or_haemophilia");
	}

	@Given("^Select No for any disease or disorder of the back, bones or joints, such as arthritis, whiplash, sciatica, slipped disc, psoriasis or gout\\?$")
	public void select_No_for_any_disease_or_disorder_of_the_back_bones_or_joints_such_as_arthritis_whiplash_sciatica_slipped_disc_psoriasis_or_gout() throws Throwable {
		String strObJPropName = "UW_RecentHealth_Gout_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Recent Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_any_disease_or_disorder_of_the_back_bones_or_joints_such_as_arthritis_whiplash_sciatica_slipped_disc_psoriasis_or_gout");
	}

	@Given("^Select No for any disease or disorder of the eyes or ears such as visual impairment in one or both eyes, ringing in one or both ears, tinnitus, labyrinthitis or Menieres disease\\?$")
	public void select_No_for_any_disease_or_disorder_of_the_eyes_or_ears_such_as_visual_impairment_in_one_or_both_eyes_ringing_in_one_or_both_ears_tinnitus_labyrinthitis_or_Menieres_disease() throws Throwable {
		String strObJPropName = "UW_RecentHealth_AnyDiseaseOrDisorderOfMeniere_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Recent Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_any_disease_or_disorder_of_the_eyes_or_ears_such_as_visual_impairment_in_one_or_both_eyes_ringing_in_one_or_both_ears_tinnitus_labyrinthitis_or_Menieres_disease");
	}

	@Given("^Click on recenthealth Next Button$")
	public void click_on_recenthealth_Next_Button() throws Throwable {
		String strObJPropName = "UW_Occupation_NextButton";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Recent Health";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String rhnextResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(rhnextResult, "click_on_recenthealth_Next_Button");
	}

	@Given("^Select No for In the last (\\d+) years, appointments or investigations with your doctor or other medical professional\\?$")
	public void select_No_for_In_the_last_years_appointments_or_investigations_with_your_doctor_or_other_medical_professional(int arg1) throws Throwable {
		String strObJPropName = "UW_CurHealthFamilyHistory_AwareOfAnySymptomsIntendMedAdvice_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Current Health & family History";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_In_the_last_years_appointments_or_investigations_with_your_doctor_or_other_medical_professional");
	}

	@Given("^Select No for In the last (\\d+) years, have you had any medication or treatment that lasted more than (\\d+) weeks\\?$")
	public void select_No_for_In_the_last_years_have_you_had_any_medication_or_treatment_that_lasted_more_than_weeks(int arg1, int arg2) throws Throwable {
		String strObJPropName = "UW_CurrentHealth_inthelast2years_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Current Health & family History";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_In_the_last_years_have_you_had_any_medication_or_treatment_that_lasted_more_than_weeks");
	}

	@Given("^Select No for In the last month have you had a positive test for Coronavirus \\(COVID-(\\d+)\\), Long COVID or Post-COVID syndrome\\?$")
	public void select_No_for_In_the_last_month_have_you_had_a_positive_test_for_Coronavirus_COVID_Long_COVID_or_Post_COVID_syndrome(int arg1) throws Throwable {
		String strObJPropName = "UW_CurrentHealth_Covid19_No_1";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Current Health & family History";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_In_the_last_month_have_you_had_a_positive_test_for_Coronavirus_COVID_Long_COVID_or_Post_COVID_syndrome");
	}

	@Given("^Select No for In the last (\\d+) months, have you had any symptoms of ill health, such as unexplained bleeding, weight loss, change of bowel habit, or a cough thats lasted for (\\d+) weeks or more\\?$")
	public void select_No_for_In_the_last_months_have_you_had_any_symptoms_of_ill_health_such_as_unexplained_bleeding_weight_loss_change_of_bowel_habit_or_a_cough_thats_lasted_for_weeks_or_more(int arg1, int arg2) throws Throwable {
		String strObJPropName = "UW_CurrentHealth_last3month_symptoms_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Current Health & family History";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_In_the_last_months_have_you_had_any_symptoms_of_ill_health_such_as_unexplained_bleeding_weight_loss_change_of_bowel_habit_or_a_cough_thats_lasted_for_weeks_or_more");
	}

	@Given("^Select No for Are you aware of any other symptoms that you are planning to seek medical advice for\\?$")
	public void select_No_for_Are_you_aware_of_any_other_symptoms_that_you_are_planning_to_seek_medical_advice_for() throws Throwable {
		String strObJPropName = "UW_RecentHealth_PlanningToSeekMedicalAdvice";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Current Health & family History";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_Are_you_aware_of_any_other_symptoms_that_you_are_planning_to_seek_medical_advice_for");
	}

	@Given("^Select No for breast, bowel/colon, ovarian, prostate or other cancer\\?$")
	public void select_No_for_breast_bowel_colon_ovarian_prostate_or_other_cancer() throws Throwable {
		String strObJPropName = "UW_CurHealthFamilyHistory_BreastBowelColonCancer_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Current Health & family History";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_breast_bowel_colon_ovarian_prostate_or_other_cancer");
	}

	@Given("^Select No for diabetes, heart attack, angina, stroke or heart disease\\?$")
	public void select_No_for_diabetes_heart_attack_angina_stroke_or_heart_disease() throws Throwable {
		String strObJPropName = "UW_CurHealthFamilyHistory_DiabetesHeartAttack_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Current Health & family History";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_diabetes_heart_attack_angina_stroke_or_heart_disease");
	}

	@Given("^Select No for multiple sclerosis, dementia or Alzheimers disease, Parkinsons disease, polyposis coli or any other hereditary disorder\\?$")
	public void select_No_for_multiple_sclerosis_dementia_or_Alzheimers_disease_Parkinsons_disease_polyposis_coli_or_any_other_hereditary_disorder() throws Throwable {
		String strObJPropName = "UW_CurHealthFamilyHistory_MultipleSclerosisDementia_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Current Health & family History";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_multiple_sclerosis_dementia_or_Alzheimers_disease_Parkinsons_disease_polyposis_coli_or_any_other_hereditary_disorder");
	}

	@Given("^Click on currenthealthandfamilyhistory Next Button$")
	public void click_on_currenthealthandfamilyhistory_Next_Button() throws Throwable {
		String strObJPropName = "UW_Occupation_NextButton";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Current Health & family History";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String chafhNextResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(chafhNextResult, "click_on_currenthealthandfamilyhistory_Next_Button");
	}

	@Given("^Select No for In the last (\\d+) years, have you spent more than (\\d+) consecutive days in Africa, Iraq, Syria or an area of civil unrest\\?$")
	public void select_No_for_In_the_last_years_have_you_spent_more_than_consecutive_days_in_Africa_Iraq_Syria_or_an_area_of_civil_unrest(int arg1, int arg2) throws Throwable {
		String strObJPropName = "UW_TravelAndActivities_Spent30ConsecutiveDaysInAreaOfCivilUnrest_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Travel & Activities";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_In_the_last_years_have_you_spent_more_than_consecutive_days_in_Africa_Iraq_Syria_or_an_area_of_civil_unrest");
	}

	@Given("^Select No for In the next (\\d+) years, do you expect to travel outside the UK, EU, Commonwealth and Development Office \\(FCDO\\) have advised of travel restrictions\\?$")
	public void select_No_for_In_the_next_years_do_you_expect_to_travel_outside_the_UK_EU_Commonwealth_and_Development_Office_FCDO_have_advised_of_travel_restrictions(int arg1) throws Throwable {
		String strObJPropName = "UW_TravelAndActivities_Next2YrsExpectToWorkOrTravelOutofUK_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Travel & Activities";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_In_the_next_years_do_you_expect_to_travel_outside_the_UK_EU_Commonwealth_and_Development_Office_FCDO_have_advised_of_travel_restrictions");
	}

	@Given("^Select No for Do you take part, or intend to take part in diving, caving, potholing, climbing or mountaineering, motor sport, or other hazardous pursuit\\?$")
	public void select_No_for_Do_you_take_part_or_intend_to_take_part_in_diving_caving_potholing_climbing_or_mountaineering_motor_sport_or_other_hazardous_pursuit() throws Throwable {
		String strObJPropName = "UW_TravelAndActivities_DoYouTakePartInDiving_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Travel & Activities";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_Do_you_take_part_or_intend_to_take_part_in_diving_caving_potholing_climbing_or_mountaineering_motor_sport_or_other_hazardous_pursuit");
	}

	@Given("^Select No for In the last (\\d+) years, have you been banned from driving\\?$")
	public void select_No_for_In_the_last_years_have_you_been_banned_from_driving(int arg1) throws Throwable {
		String strObJPropName = "UW_TravelAndActivities_InLast5YrsBannedFromDiving_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Travel & Activities";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_In_the_last_years_have_you_been_banned_from_driving");
	}

	@Given("^Click on travelandactivities Next Button$")
	public void click_on_travelandactivities_Next_Button() throws Throwable {
		String strObJPropName = "UW_Occupation_NextButton";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Travel & Activities";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String taNextResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(taNextResult, "click_on_travelandactivities_Next_Button");
	}

	@Then("^Select No for Apart from this application, have you applied to Zurich for any life insurance, critical illness cover or income protection in the last years\\?$")
	public void select_No_for_Apart_from_this_application_have_you_applied_to_Zurich_for_any_life_insurance_critical_illness_cover_or_income_protection_in_the_last_years() throws Throwable {
		String strObJPropName = "UW_OtherInformation_HaveAnyExistingCICoverWithUs_No_1";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Other Cover";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_Apart_from_this_application_have_you_applied_to_Zurich_for_any_life_insurance_critical_illness_cover_or_income_protection_in_the_last_years");
	}

	@Then("^Select No for Will the amount of cover you are now applying for, exceed million life cover critical illness cover\\?$")
	public void select_No_for_Will_the_amount_of_cover_you_are_now_applying_for_exceed_million_life_cover_critical_illness_cover() throws Throwable {
		String strObJPropName = "UW_OtherInformation_ExistingLifeOrCICoverExceeds_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Other Cover";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String uwQuestionResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(uwQuestionResult, "select_No_for_Will_the_amount_of_cover_you_are_now_applying_for_exceed_million_life_cover_critical_illness_cover");
	}

	@Given("^Click on othercover Next Button$")
	public void click_on_othercover_Next_Button() throws Throwable {
		String strObJPropName = "UW_Occupation_NextButton";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Other Cover";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String ocNextResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(ocNextResult, "click_on_othercover_Next_Button");
	}

	@Given("^Click on summary Confirm Button$")
	public void click_on_summary_Confirm_Button() throws Throwable {
		String strObJPropName = "ApplicationSummary_Confirm";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Summary page";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String summarycnfrmResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(summarycnfrmResult, "click_on_summary_Confirm_Button");
	}
	
	@Given("^Capture Decisions from decision page$")
	public void capture_Decisions_from_decision_page() throws Throwable {
		String strObJPropName = "";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Decision Screen";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CAPTURE_DECISIONS.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String cptDecResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(cptDecResult, "capture_Decisions_from_decision_page");
	}
	
	@Then("^Click on decision continue Button$")
	public void click_on_decision_continue_Button() throws Throwable {
		String strObJPropName = "DecisionPage_ContinueButton";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Decision Screen";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String decisionNextResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(decisionNextResult, "click_on_decision_continue_Button");
	}

	@Given("^Select the Preferred \"([^\"]*)\" from the Dropdown$")
	public void select_the_Preferred_from_the_Dropdown(String collectionday) throws Throwable {
		Thread.sleep(500);
		String strTestData = collectionday;
		String strLogMessage = "Payment details";
		String strObJPropName = "PaymentDetails_PrefCollectionDay";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.SELECT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String preferdayResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(preferdayResult, "select_the_Preferred_from_the_Dropdown");
	}

	@Then("^Click on Payer Lifeone Radio Button$")
	public void click_on_Payer_Lifeone_Radio_Button() throws Throwable {
		String strObJPropName = "PaymentDetails_Payer_Life1_RadioBtn";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Payment details";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String lfOneradioResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(lfOneradioResult, "click_on_Payer_Lifeone_Radio_Button");
	}

	@Given("^Enter the accholdername\"([^\"]*)\"$")
	public void enter_the_accholdername(String accholdername) throws Throwable {
		Thread.sleep(500);
		String strTestData = accholdername;
		String strLogMessage = "Payment details";
		String strObJPropName = "PaymentDetails_NameOfAccHolder";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String acchnameResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(acchnameResult, "enter_the_accholdername");
	}

	@Given("^Enter Sortcode as \"([^\"]*)\" , \"([^\"]*)\" , \"([^\"]*)\"$")
	public void enter_Sortcode_as(String sortone, String sorttwo, String sortthree) throws Throwable {
		Thread.sleep(500);
		String strTestData = sortone;
		String strLogMessage = "Payment details";
		String strObJPropName = "PaymentDetails_SortCode1";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String sort1Result = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(sort1Result, "enter_Sortcode_as");
		
		strTestData = sorttwo;
		strObJPropName = "PaymentDetails_SortCode2";
		String sort2Result = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(sort2Result, "enter_Sortcode_as");
		
		strTestData = sortthree;
		strObJPropName = "PaymentDetails_SortCode3";
		String sort3Result = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(sort3Result, "enter_Sortcode_as");
	}

	@Given("^Enter accno\"([^\"]*)\"$")
	public void enter_accno(String accno) throws Throwable {
		Thread.sleep(500);
		String strTestData = accno;
		String strLogMessage = "Payment details";
		String strObJPropName = "PaymentDetails_AccountNumber";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String accNoResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(accNoResult, "enter_accno");
	}

	@Then("^Check the I confirm that the payer is the account holder Check Box$")
	public void check_the_I_confirm_that_the_payer_is_the_account_holder_Check_Box() throws Throwable {
		String strObJPropName = "PaymentDetails_ConfirmCheckBox";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Payment details";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String acchldrCheck = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(acchldrCheck, "check_the_I_confirm_that_the_payer_is_the_account_holder_Check_Box");
	}

	@Then("^Click on paymentdeatils Validate Button$")
	public void click_on_paymentdeatils_Validate_Button() throws Throwable {
		String strObJPropName = "PaymentDetails_ValidateButton";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Payment details";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String paymntvalidateresult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(paymntvalidateresult, "click_on_paymentdeatils_Validate_Button");
	}

	@Then("^Click on paymentdeatils Continue Button$")
	public void click_on_paymentdeatils_Continue_Button() throws Throwable {
		String strObJPropName = "PaymentDetails_ContinueButton";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Payment details";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String paymntDetailsResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(paymntDetailsResult, "click_on_paymentdeatils_Continue_Button");
	}

	@Then("^Click on directdebitconfirmation Continue Button$")
	public void click_on_directdebitconfirmation_Continue_Button() throws Throwable {
		String strObJPropName = "DirectDebitConfirmation_ContinueButton";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Direct Debit Confirmation Page";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String ddcontinueResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(ddcontinueResult, "click_on_directdebitconfirmation_Continue_Button");
	}
	
	@Then("^Check the Confirmation and Verification of Identity Check Box$")
	public void check_the_Confirmation_and_Verification_of_Identity_Check_Box() throws Throwable {
		String strObJPropName = "ConVerIdnty_ConfirmCheckBox_1";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Confirmation and verification Page";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String cavcheckResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(cavcheckResult, "check_the_Confirmation_and_Verification_of_Identity_Check_Box");
	}

	@Then("^Click on Confirmation and Verificatio Continue Button$")
	public void click_on_Confirmation_and_Verificatio_Continue_Button() throws Throwable {
		String strObJPropName = "ConfirmationandVerificationofIdentity_ContinueButton";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Confirmation and verification Page";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String cavcontinueResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(cavcontinueResult, "click_on_Confirmation_and_Verificatio_Continue_Button");
	}

	@Then("^Click on Trust details No Radio Button$")
	public void click_on_Trust_details_No_Radio_Button() throws Throwable {
		String strObJPropName = "Policy1_SubjectToTrust_No";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Trust Details";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String trstradioResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(trstradioResult, "click_on_Trust_details_No_Radio_Button");
	}

	@Then("^Click on Trustdetails Continue Button$")
	public void click_on_Trustdetails_Continue_Button() throws Throwable {
		String strObJPropName = "TrustDetails_Continue_Button";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Trust Details";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String trustContinueResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(trustContinueResult, "click_on_Trustdetails_Continue_Button");
	}


	@Given("^Enter the start date as \"([^\"]*)\" , \"([^\"]*)\" , \"([^\"]*)\"$")
	public void enter_the_start_date_as(String day, String month, String year) throws Throwable {
		Thread.sleep(500);
		String strTestData = day;
		String strLogMessage = "Payment details";
		String strObJPropName = "Policy1_StartDate_Day";
		String strReportKeyWord = "None";
		aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.INPUT.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String strtdeatilsDayResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(strtdeatilsDayResult, "Error while entering start details");
		
		strTestData = month;
		strObJPropName = "Policy1_StartDate_Month";
		String strtdeatilsMonthResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(strtdeatilsMonthResult, "Error while entering start details");
		
		strTestData = year;
		strObJPropName = "Policy1_StartDate_Year";
		String strtdeatilsYearResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(strtdeatilsYearResult, "Error while entering start details");
	}

	@Then("^Click the no marketing Check Box$")
	public void click_the_no_marketing_Check_Box() throws Throwable {
		String strObJPropName = "StartDateDetails_NoMarketingCheckBox_Life1";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Start date page";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String marktngCheckResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(marktngCheckResult, "click_the_no_marketing_Check_Box");
	}

	@Then("^Please tick the box to confirm the above Check Box$")
	public void please_tick_the_box_to_confirm_the_above_Check_Box() throws Throwable {
		String strObJPropName = "IssueMyPolicy_ClickOnCheckBox";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Start date page";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String confrmCheckresult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(confrmCheckresult, "please_tick_the_box_to_confirm_the_above_Check_Box");
	}

	@Then("^Click on startdate Issue my policy Button$")
	public void click_on_startdate_Issue_my_policy_Button() throws Throwable {
		String strObJPropName = "StartDateDetails_IssuemyPolicyButton";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Start date page";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String issueMypolicyResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(issueMypolicyResult, "click_on_startdate_Issue_my_policy_Button");
	}

	@Then("^Capture the Application Policy Number$")
	public void capture_the_Application_Policy_Number() throws Throwable {
		String strObJPropName = "";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Start date page";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CAPTURE_POLICY_NUMS.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String cptrePlcyNos = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(strLogMessage, "capture_the_Application_Policy_Number");
	}

	@Then("^Click on confirmation Return to dashboard Button$")
	public void click_on_confirmation_Return_to_dashboard_Button() throws Throwable {
		String strObJPropName = "ConfirmationPage_ReturnButton";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Confirmation Page";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String rtnnToDashboardResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(rtnnToDashboardResult, "click_on_confirmation_Return_to_dashboard_Button");
	}
	
	@Given("^Capture Screenshot$")
	public void capure_Screenshot() throws Throwable {
		String strObJPropName = "";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Capturing Screenshot";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.TAKE_FULLWINDOW_SCREENSHOT.getKeyWord());
		aControllerScript.execute(testScenarioName, stepDescription,strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
	}
	
	public void capureScreenshot(String strLogMessage) throws Throwable {
		String strObJPropName = "";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.TAKE_FULLWINDOW_SCREENSHOT.getKeyWord());
		aControllerScript.execute(testScenarioName, stepDescription,strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
	}

	@Then("^Click On Save&Exit Button$")
	public void click_On_Save_Exit_Button() throws Throwable {
		Thread.sleep(1000);
		String strObJPropName = "SaveAndExitButton";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Life assured";
		 aKeyWordConfigBean = new KeyWordConfigBean();
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String savenExitResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		capureScreenshot(strLogMessage);
		checkResult(savenExitResult, "click_On_Save_Exit_Button");
	}

	@Then("^Click On Logout Button$")
	public void click_On_Logout_Button() throws Throwable {
		Thread.sleep(2000);	
		String strObJPropName = "SignOut";
		String strTestData = "Yes";
		String strReportKeyWord = "None";
		String strLogMessage = "Dashboard";
		 aKeyWordConfigBean = new KeyWordConfigBean();	
		aKeyWordConfigBean.setOriginalKeyWord(KeyWord.CLICK.getKeyWord());
		aKeyWordConfigBean.setOriginalKeyWordType(IdentificationType.XPATH.getKeyWordType());
		String logoutResult = aControllerScript.execute(testScenarioName, stepDescription, strLogMessage, strReportKeyWord, strTestData,
				strObJPropName, aKeyWordConfigBean);
		checkResult(logoutResult, "click_On_Logout_Button");
	}
	
	@After
	public void generateReports() throws Throwable {
		RunTimeDataUtils.editRuntimeValues(aTestSuiteBean, aControllerScript.getBrowsersConfigBean());
		ConsolidateTestReport.createExecutionReport(aTestSuiteBean, MasterConfig.getInstance().getAppEnvConfigBean(), aControllerScript.getBrowsersConfigBean(), AppConstants.TEST_RESULT_PASS);
		TestStepReport.flushReport(aControllerScript.getBrowsersConfigBean(), aTestSuiteBean.getOriginalScenarioName());	
		ALMTestCaseReport.updateALMResults(aTestSuiteBean, AppConstants.TEST_RESULT_PASS);
	      
        //Closing the browser
       //driver.quit();
       //System.out.println("Browser Closed Successfully");
	}
	
	@Given("^User enters mandatory details$")
	public void user_enters_mandatory_details() throws Throwable {
		TestDataBean aTestDataBean = AppConfig.getInstance().getTestDataBean(aTestSuiteBean);
        if (aTestDataBean == null) {
            return;
        }
       
        TestStep aTestStep = new TestStep(aTestSuiteBean, aTestDataBean, driver);
        String resultValue = aTestStep.executeTestStep();
        checkResult(resultValue, "Error in Mandatory fields");
	}
	
	public void checkResult(String strResult, String exceptionText) throws Exception {
		if(StringUtils.equals(strResult, AppConstants.TEST_RESULT_FAIL)) {
			throw new Exception(exceptionText);
		}
		
	}
	

}
