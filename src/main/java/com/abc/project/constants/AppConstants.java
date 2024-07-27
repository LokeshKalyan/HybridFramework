/****************************************************************************
 * File Name 		: AppConstants.java
 * Package			: com.dxc.zurich.constants
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
package com.abc.project.constants;

import com.dxc.enums.ExecutionStatus;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 11:10:21 am
 */
public final class AppConstants {

	public static final int DEFAULT_FILE_READ_SIZE = 1024;

	public static final long DEFAULT_MILLISECONDS = 1000;

	public static final long SERVER_DATA_READ_WAIT_TIME = 1000 * 2 * 60;

	public static final int SERVER_DATA_CONNECTION_WAIT_TIME = 1000 * 10;

	public static final String PROPERTIES_FILE_SUFFIX = ".properties";

	public static final String BROWSER_SAFARI = "Safari";

	public static final String HOST_NAME = "Host Name";

	public static final String EXECUTION_ENV = "Execution-Env";

	public static final String USER_NAME = "User Name";
	
	public static final String BUILD_NAME = "Build Name";

	public static final String SEPARATOR_COMMA = ",";

	public static final String SEPARATOR_AMPERSAND = "&";

	public static final String SEPARATOR_QUESTION = "?";

	public static final String SEPARATOR_PLUS = "+";

	public static final String SEPARATOR_SPACE = " ";

	public static final String SEPARATOR_MINUS = "-";

	public static final String SEPARATOR_SEMICOLON = ";";

	public static final String SEPARATOR_COLON = ":";

	public static final String SEPARATOR_UNDERSCORE = "_";

	public static final String SEPARATOR_CAP = "^";

	public static final String SEPARATOR_OR = "||";

	public static final String SEPARATOR_PERCENTAGE = "%";

	public static final String DEFAULT_TRUE = "Yes";

	public static final String DEFAULT_FALSE = "No";

	public static final String DEFAULT_END = "END";
	
	public static final String REGEX_NBSP_CHARACTER = "\u00a0";
	
	public static final String REGEX_QUESTIONMARK_CHARACTER = "\uFFFD";
	
	public static final String REGEX_LINE_BREAK_CHARACTER = "(\r\n|\n)";
	
	public static final String REGEX_SPACE_CHARACTER = "\\ ";
	
	public static final String REGEX_DOUBLE_QUOTE_CHARACTER = "\"";
	
	public static final String REGEX_SINGLE_QUOTE_CHARACTER = "\'";

	public static final String BROWSER_RUNNER_THERAD_POOL_FORMAT = "BROWSER-RUNNER-POOL-%d";

	public static final String DATA_RUNNER_THERAD_POOL_FORMAT = "DATA-RUNNER-POOL";

	public static final String DATA_SESSION_THERAD_NAME = "DATA-SESSION-POOL";

	public static final String PDF_FILE_EXTENTION = "PDF";

	public static final String PNG_FILE_EXTENTION = "png";

	public static final String PDF_COMPARE_RESULT_FOLDER = "PDFImageCompareResult";

	public static final String IMAGE_COMPARE_RESULT_FOLDER = "ImageCompareResult";

	public static final String TEST_RESULT_PASS = ExecutionStatus.PASS.getStatus();

	public static final String TEST_RESULT_FAIL = ExecutionStatus.FAIL.getStatus();

	public static final String TEST_RESULT_WARING = ExecutionStatus.WARING.getStatus();

	public static final String TEST_RESULT_OTHERS = ExecutionStatus.OTHERS_INVALID.getStatus();

	public static final String TEST_SNAP_FILE_EXTENTION = "png";

	public static final String TEST_RESULT_TOTALCOUNT = "Total Executed";

	public static final String ERROR_LOGNAME = "error_log";

	public static final String APP_NAME = "app.name";

	public static final String APP_ENV = "app.env";

	public static final String APP_CONFIG_KEY = "app.config";

	public static final String APP_PRIORITY_CONFIG_KEY = "app.priorityconfig";
	
	public static final String APP_PDF_VALIDATIONS_CONFIG_KEY = "app.pdfvalidationsconfig";

	public static final String APP_PRIORITY_FILTER_CONFIG_KEY = "app.priority.filterconfig";

	public static final String APP_PRIORITY_PICK_RUNMODE_KEY = "app.priority.pick.runmode";

	public static final String APP_ENV_CONFIG_SHEET_NAME = "ENV_CONFIG";
	
	public static final String APP_PDF_EXCLUSIONS_SHEET_NAME = "PDFExclusions";

	public static final String APP_PRIORITY_CONFIG_SHEET_NAME = "APP_PRIORITY";

	public static final String APP_PROPERTIES_NAME = "EnvConfigProperties/application.properties";

	public static final String APP_ENV_PROPERTIES_LOC = "EnvConfigProperties/";

	public static final String APP_ENV_CONFIG_KEY = "app.env.config";

	public static final String RESOURCE_PATH_KEY = "conf.path";

	public static final String REPORTS_PATH_KEY = "reports.path";

	public static final String WIN_CHROME_DRIVER_PATH_KEY = "win.chrome.webdriver.path";

	public static final String WIN_CHROME_ELECTRON_DRIVER_PATH_KEY = "win.chrome.electronapp.webdriver.path";

	public static final String WIN_CHROME_DRIVER_PROP_NAME = "webdriver.chrome.driver";

	public static final String WIN_FIREFOX_DRIVER_PATH_KEY = "win.firefox.webdriver.path";

	public static final String WIN_FIREFOX_DRIVER_PROP_NAME = "webdriver.gecko.driver";

	public static final String WIN_IE_DRIVER_PATH_KEY = "win.ie.webdriver.path";

	public static final String WIN_IE_DRIVER_SETTINGS_PATH_KEY = "win.ie.webdriver.settings.path";

	public static final String WIN_IE_DRIVER_PROP_NAME = "webdriver.ie.driver";

	public static final String WIN_EDGE_DRIVER_PATH_KEY = "win.edge.webdriver.path";

	public static final String WIN_BROWSERSTACK_LOCAL_DRIVER_PATH_KEY = "win.browserstacklocal.webdriver.path";

	public static final String WIN_EDGE_DRIVER_PROP_NAME = "webdriver.edge.driver";

	public static final String DRIVER_MAX_INSTANCE_PROP_NAME = "driver.device.maxinstancecount";

	public static final String DRIVER_APPIUM_LOCATION_PROP_NAME = "driver.device.appiumlocation";

	public static final String DRIVER_NODEJS_LOCATION_PROP_NAME = "driver.device.nodeJS";

	public static final String DRIVER_APPIUM_LOGS_LOCATION_PROP_NAME = "driver.device.logFileLocation";

	public static final String DRIVER_INITILIZE_EVERYTIME_PROP_NAME = "driver.initilize.foreachtest";

	public static final String DRIVER_DEVICE_MAX_INSTANCE_KEY = "maxInstances";

	public static final String DRIVER_DEVICE_NEWCOMMNAD_TIMEOUT_KEY = "driver.device.newCommandTimeout";

	public static final String DRIVER_DEVICE_LAUNCH_TIMEOUT_KEY = "driver.device.launchTimeout";

	public static final String DRIVER_DEVICE_BROWSER_STACK_LAUNCH_KEY = "driver.browserstack.url";

	public static final String DRIVER_DEVICE_DEBUG_KEY = "driver.device.debug";

	public static final String DEVICE_DRIVER_JSON_NAME = "%s_Device.json";

	public static final String EMAIL_CONFIG_PATH_KEY = "EMAIL.CONFIGPATH";

	public static final String ORDESKTOP_CONFIG_PATH_KEY = "ORDESKTOP.CONFIGPATH";

	public static final String ORMOBILE_CONFIG_PATH_KEY = "ORMOBILEDESKTOP.CONFIGPATH";

	public static final String ORMOBILE_NATIVE_CONFIG_PATH_KEY = "ORMOBILENATIVE.CONFIGPATH";

	public static final String ORDESKTOP_NATIVE_CONFIG_PATH_KEY = "ORDESKTOPNATIVE.CONFIGPATH";

	public static final String VERIFICATION_CONFIG_PATH_KEY = "VERIFICATION.CONFIGPATH";

	public static final String BROWSER_CONFIGFILE_KEY = "browser.configfile";

	public static final String BROWSER_SENDEMAIL_KEY = "browser.sendemail";

	public static final String BROWSER_SEND_TELEGRAM_NOTIFICATION_KEY = "browser.sendtelegramNotification";

	public static final String BROWSER_TELEGRAM_NOTIFICATION_URL_KEY = "browser.telegram.url";

	public static final String BROWSER_TELEGRAM_NOTIFICATION_FORMAT_KEY = "browser.telegram.messagefomat";

	public static final String BROWSER_TELEGRAMACESSTOKEN_KEY = "browser.telegramAcessToken";

	public static final String BROWSER_SEND_MSTEAMS_NOTIFICATION_KEY = "browser.sendMSTeamsNotification";

	public static final String BROWSER_UPDATE_ALMSTATUS_KEY = "browser.updateALMStatus";

	public static final String BROWSER_WINDOWS_SIZE_KEY = "browser.window.size";

	public static final String CONTROLLER_CONFIGFILE_KEY = "CTRL.CONFIGFILE";

	public static final String CONTROLLER_DATA_SHEETNAME = "TestData";

	public static final String CONTROLLER_SUITE_SHEETNAME = "ControllerSuite";

	public static final String BROWSER_CONFIG_SHEETNAME = "Browser";

	public static final String SYSTEM_DATE_SHEETNAME = "DataSource";

	public static final String DEVICE_CONFIG_SHEETNAME = "Device_Config";

	/** The Constant DATE_FORMAT_MM_FS_DD_FS_YYYY. */
	public static final String EXEC_REPORTS_FOLDER_DT_FORMAT = "MM-dd-yyyy";

	/** The Constant DATE_FORMAT_MM_FS_DD_FS_YYYY. */
	public static final String QUESTIONNAIRE_DT_FORMAT = "yyyyMMdd";

	public static final String HTML_REPORT_NAME_KEY = "HTML.REPORT.NAME";

	public static final String HTML_REPORT_DOC_TITLE_KEY = "HTML.REPORT.DOC.TITLE";

	public static final String HTML_REPORT_DOC_NAME_KEY = "HTML.REPORT.DOC.NAME";

	public static final String BROWSER_TASK_TOUT_KEY = "BROWSER.TASK.TIMEOUT";

	public static final String REF_FILE_NAME_KEY = "REFERENCE.FILE.NAME";

	public static final String JACOB_LIB_FILE_NAME_KEY = "JACOB.LIBRATY.PATH";

	public static final String JACOB_LIB_PROP_NAME = "jacob.dll.path";

	public static final String SELINIUM_GRID_FILE_NAME_KEY = "selinium.grid.library.path";
	
	public static final String SELINIUM_HUB_URL_FORMAT_KEY = "selinium.hub.url";

	public static final String SELINIUM_NODE_URL_FORMAT_KEY = "selinium.node.url";

	public static final String DOWNLOAD_FILE_LOCATION_KEY = "DOWNLOAD.FILE.LOCATION";

	public static final String DRIVER_HEADLESS_KEY = "driver.headless";

	public static final String DRIVER_EXPLICIT_WAIT_TIME_KEY = "driver.explicit.wait.time";

	public static final String DRIVER_IMPLICIT_WAIT_TIME_KEY = "driver.implicit.wait.time";

	public static final String DRIVER_SNAPSHOT_EXCLUSIONS_KEY = "driver.snapshot.exclusions";

	public static final int DRIVER_EXTERNAL_WAIT_TIME = 5000;
	
	public static final int DRIVER_EXPLICIT_WAIT_TIME = 5000;

	public static final int DRIVER_IMPLICIT_WAIT_TIME = 500;

	public static final int DRIVER_DEFAULT_SLEEP_TIMEOUT = 500;
	
	public static final int DRIVER_DEFAULT_PAGELOAD_TIMEOUT = 5000;
	
	public static final int DRIVER_DEFAULT_SCRIPT_TIMEOUT = 2000;

	public static final String DRIVER_EXTERNAL_SLEEP_TIME_KEY = "driver.external.sleep.time";
	
	public static final String DRIVER_SLEEP_TIME_KEY = "driver.sleep.time";
	
	public static final String DRIVER_PAGE_LOAD_TIMEOUT = "driver.page.load.timeout";
	
	public static final String DRIVER_SCRIPT_TIMEOUT = "driver.script.timeout";

	public static final String GENERATE_DOC_REPORT_KEY = "GENERATE.DOC.REPORT";

	public static final String GENERATE_DOC_REPORT_SCENARIO_KEY = "GENERATE.DOC.SCENARIO.REPORT";

	public static final String GENERATE_XML_REPORT_KEY = "GENERATE.XML.REPORT";

	public static final String XML_REPORT_FILENAME_KEY = "XML.REPORT.FILENAME";

	public static final String XML_REPORT_CONSILIDATED_ROOT_KEY = "XML.REPORT.CONSILATE.ROOT.TAG";

	public static final String XML_FOLDER = "XML-Report";

	public static final String DOC_FOLDER = "Snapshot-Document";

	public static final String XML_FILE_PREFIX = "TEST-";

	public static final String EMAIL_FROM_KEY = "mail.smtp.from";

	public static final String EMAIL_TO_KEY = "mail.smtp.to";

	public static final String EMAIL_CC_KEY = "mail.smtp.cc";

	public static final String EMAIL_MESSAGE_SUBJECT_KEY = "mail.message.subject";

	public static final String EMAIL_MESSAGE_BODY_KEY = "mail.message.body";

	public static final String EMAIL_USER_KEY = "mail.smtp.user";

	public static final String EMAIL_USER_PASSWORD_KEY = "mail.smtp.password";

	public static final String EMAIL_HOST_KEY = "mail.smtp.host";

	public static final String EMAIL_PORT_KEY = "mail.smtp.port";

	public static final String EMAIL_READ_HOST_KEY = "mail.imap.host";

	public static final String EMAIL_READ_PORT_KEY = "mail.imap.port";

	public static final String EMAIL_AUTHENTICATION_KEY = "mail.smtp.auth";

	public static final String EMAIL_STARTTLS_KEY = "mail.smtp.starttls.enable";

	public static final String EMAIL_DEBUG_KEY = "mail.debug";

	public static final String EMAIL_DEBUG_AUTHENTICATION_KEY = "mail.debug.auth";

	public static final String EMAIL_SSL_ENABLE_KEY = "mail.smtp.ssl.enable";

	public static final String EMAIL_SSL_SOCKET_FACTORY_PORT_KEY = "mail.smtp.socketFactory.port";

	public static final String EMAIL_SSL_SOCKET_FACTORY_CLASS_KEY = "mail.smtp.socketFactory.class";

	public static final String DATE_FORMAT_DDMMYYYY = "dd/MM/yyyy";

	public static final String DATE_FORMAT_DD_MM_YYYY = "dd-MM-yyyy";

	public static final String DATE_FORMAT_YYYYMMDD = "yyyy-MM-dd";

	public static final String DATE_FORMAT_DDMM_HHSS = "ddMM_hhmmss-SS";

	public static final String EXEC_DATEFORMAT = "MMM dd, yyyy hh:mm:ss a";
	
	public static final String DATE_FORMAT_MMMDDYYYY = "MMM dd, yyyy";

	public static final String BROWSER_MODE_HEADLESS = "Head Less";

	public static final String TEST_CASE_PART = "Part";

	public static final String APP_LAUNCH_SUCESSFULLY = "successfully registered with the the grid";

	public static final String DEFAULT_APP_DATE_TIME_CONV_FORMAT = "MMM dd, yyyy hh:mm:ss a";

	public static final String DB_CONFIG_PROPERTIES_LOC = "DbConfig/";

	public static final String PLATFORM_NAME_IOS = "IOS";

	public static final String REPORT_FILE_NAME_FORMAT = "%s.%s";

	public static final String ERR_PARSE_QRY = "Error in parse query!";

	public static final String ERR_INVALID_ACTION_IN_URL = "Action passed in URL is invalid. ?action=loadControllerSuiteBean|updateStatus|";

	public static final String NODE_CONFIG_JSON_FILE_NAME = "%s_Device.json";

	public static final String NODE_REGISTERED_SUCESSFULLY = "The node is registered to the hub and ready to use";

	public static final String GRID_REGISTERED_SUCESSFULLY = "Selenium Grid hub is up and running";

	public static final String ENV_URL_FORMAT = "{0}_{1}";

	public static final String SORTING_ASCENDING = "Ascending";

	public static final String SORTING_DESCENDING = "Descending";

	public static final String NOFICATION_HTML_TABLE_STYLE = "align=\"center\" style=\"margin-left:auto;margin-right:auto;border-collapse: collapse;  border: 1px solid black;border-spacing: 5px;width:75%;\"";

	public static final String NOFICATION_HTML_ROW_STYLE = "style=\"font-weight:bold;border-collapse: collapse;  border: 1px solid black; white-space: nowrap;\"";

	public static final String NOFICATION_HTML_HEADER_STYLE = "style=\"font-weight:bold;background-color:#FFFF00;border-collapse: collapse;  border: 1px solid black; white-space: nowrap;\"";

	public static final int SCROLL_TIMEOUT = 500;

	public static final String HEALENIUM_CONFPATH_KEY = "healenium.confpath";

	public static final String HEALENIUM_SELFHEAL_KEY = "heal-enabled";
	
	public static final String HEALENIUM_CONFIG_OVERRIDE_WITH_ENV_VARS_KEY="config.override_with_env_vars";
	
	public static final String HEALENIUM_SERVER_URL_KEY="hlm.server.url";
	
	public static final String HEALENIUM_IMITATOR_URL_KEY="hlm.imitator.url";

	public static final String FAILED_SCENARIOS_COPYINGTO_NEWFOLDER = "failed.scenarios.copyingto.newfolder";

	public static final String DRIVER_SIMPLE_SNAPSHOT_KEY = "driver.simple.snapshot";

	public static final String HTML_REPORT_EXTENSION = ".html";

	public static final String EXCEL_REPORT_EXTENSION = ".xlsx";

	public static final String DOCUMENT_REPORT_EXTENSION = ".docx";

	public static final String PDF_DOCUMENT_REPORT_EXTENSION = ".pdf";

	public static final String APP_RUN_STATUS_KEY = "app.runstatus.path";

	public static final int APP_RUN_START_STATUS = 1;

	public static final int APP_RUN_FINISH_STATUS = 0;

	public static final String APP_ALM_SESSION_CLEAR_TIME_KEY = "app.clear.alm.session.time";

	public static final String APP_ALM_SESSION_CLEAR_TIME = "15";

	public static final String ALM_CONFIG_PROPERTIES_NAME = "AppConfig/ALMConfig.properties";

	public static final String ALM_CONFIG_URL_KEY = "alm.url";

	public static final String ALM_CONFIG_USERNAME_KEY = "alm.username";

	public static final String ALM_CONFIG_PASSWORD_KEY = "alm.password";

	public static final String ALM_CONFIG_CLIENTID_KEY = "alm.clientId";

	public static final String ALM_CONFIG_SECRET_KEY = "alm.secret";

	public static final String ALM_CONFIG_DOMAIN_KEY = "alm.domain";

	public static final String ALM_CONFIG_PROJECT_KEY = "alm.project";

	public static final String ALM_CONFIG_TESTCASE_PREFIX_KEY = "alm.testcase.prefix";

	public static final String ALM_CONFIG_RUNNAME_KEY = "alm.runname";

	public static final String EMAIL_ATTACHMENT_CONTENT_TYPE = "multipart";

	public static final String APP_RERUN_FAILED_TESTCASES_KEY = "app.rerun.failedTC";

	public static final String ALM_TEST_CASE_PARTIAL_STATUS = "Partial";

	public static final String ALM_TEST_CASE_END_TO_END_STATUS = "End to End";

	public static final int ALM_REST_API_PAGINATION_MAX_LIMIT = 5000;
	
	public static final String REPORT_INITILIZE_HISTORIC_TESTS_KEY = "report.initilize.historic.tests";

	public static final String REPORT_HISTORIC_TESTS_DB_URL_KEY = "report.historic.tests.db.url";

	public static final String REPORT_HISTORIC_TESTS_URL_KEY = "report.historic.tests.url";
	
	public static final String REPORT_TESTS_BUILD_VERSION_KEY ="report.tests.build.version";
	
	public static final String HTML_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";

}
