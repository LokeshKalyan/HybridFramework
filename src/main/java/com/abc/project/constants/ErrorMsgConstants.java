/****************************************************************************
 * File Name 		: ErrorMsgConstants.java
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

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 11:11:18 am
 */
public final class ErrorMsgConstants {

	public static final String ERR_DEFAULT = "Un-Known Message / InValid Message";
	
	public static final String ERR_ALM_HOST_CONNECT = "Unable to connect {0}";
	
	public static final String ERR_ALM_INVALID_TEST_ID = "ERROR : Invalid TestSet ID - {0}!";
	
	public static final String ERR_ALM_INVALID_RUN_ID = "Unable to update run status in ALM for scenario {0}!";
	
	public static final String ERR_ALM_INVALID_ATTACHMENT = "Unable to upload attachment {0} for TestSet ID - {1} , Run id {2} & scenario {3}!";
	
	public static final String DEVICE_INITIALIZE_ERROR = "Error while initialing browser {0}";

	public static final String FILENTFOUND = "Unable to find the file at {0}";
	
	public static final String FILE_NT_DW_PROPERLY = "File {0} Not Dowonloaded Properly";

	public static final String SHEET_NOT_FOUND = "The {0} sheet is not available in {1} file";

	public static final String ERR_ROW = "Cannot get data from the Row where the Row Number is {0} in sheet {1}";

	public static final String ERR_CELL = "Cannot get Cell data where the row number is {0} and column is {1} in sheet {2}";

	public static final String ERR_STRING = "Cannot get String Value where the row number is {0} and column is {1} in sheet {2}";
	
	public static final String DATA_LIST_SIZE_MISSMATCH = "SCN_ID = {0} STEP_ID = {1}: TestDataList - Key Size Mismatch where the row number is {2}";
	
	public static final String ERR_REPORT_GENERATION = "Error in report generating for {0}.";
	
	public static final String ERR_SCN_REPORT_GENERATION = "SCN_ID = {0} Error in report generating for {0}.";
	
	public static final String SHUT_DOWN = "Error while shutting down executor";
	
	public static final String TH_INTERRUPT = "Current Thread was Interrupted";
	
	public static final String  ERR_WRITE_WORKBOOK = "Unable to write data to  {0} file at {1}";
	
	public static final String ERR_SEND_EMAIL = "Error while sending email to {0} from {1} with subject {2}";

	public static final String ERR_SEND_EMAIL_VALIDATION = "Unable to send email as User Name {0}/Password/From Email {1}/ To Email {2} is empty.Please modify email config{3} and try again";
	
	public static final String ERR_READ_EMAIL = "Error while reading email {0} with subject {1}";

	public static final String ERR_READ_EMAIL_VALIDATION = "Unable to send email as User Name {0}/Password/From Email {1} is empty.Please modify email config{2} and try again";
	
	public static final String ERR_CALL_EXCEL_MACRO = "Error while executing excel macro {0} function {1}";
	
	public static final String ERR_EMPTY_OPEN_URL = "Unable to open Unknow-URL";
	
	public static final String ERR_EMPTY_WEB_PROPERTY = "Web Property is empty..";
	
	public static final String ERR_IMG_DECODE = "Error While decoding image file {0}";
	
	public static final String ERR_UNSUPPORTED_MODE = "Un Supported {0} Mode {1}";
	
	public static final String ERR_SERVER_CMD_ARGS = "CommandLine Error Usage: {0} <hostport>. Example: {0} 4444";
	
	public static final String ERR_NODE_CMD_ARGS = "CommandLine Error Usage: {0} <serverIP/hostname> <serverport>. Example: {0} 10.10.10.10 8888";
	
	public static final String ERR_GRID_CMD_ARGS = "CommandLine Error Usage: {0} <serverIP/hostname> <serverport> <nodes>. Example: {0} 10.10.10.10 4444 10.10.10.1:5555,10.10.10.2:5555...";
	
	public static final String ERR_INVALID_HOST_ADD = "CommandLine Error Usage: invalid  HostName/IP Address {0}. Example: 10.10.10.10(Or Machine Full Name)";
	
	public static final String ERR_INVALID_GRID_HOST = "CommandLine Error Usage: invalid  HostName/IP Address and PORT {0}. Example: 10.10.10.10(Or Machine Full Name):4444(Only Numeric Values Accepted)";
	
	public static final String ERR_INVALID_PORT = "CommandLine Error Usage: invalid  Port {0}. Example: 4444(Only Numeric Values Accepted)";
	
	public static final String UNKNOWN_ACTION = "No Recognized Action Passed! {0}";
	
	public static final String UNKNOWN_ARGUMENT = "No Recognized Argument Passed! {0}";
	
	public static final String ERR_SERVER_CREATE_IO = "Error in server create! IO failed at {0}";
	
	public static final String ERR_NODE_CMD_REGISTER_ARGS = "CommandLine Error Usage: Cannot run server and clinet on same machine {0}";
	
	public static final String ERR_HOST_CONNECT = "Unable to connect {0}:{1}";
	
	public static final String ERR_TELEGRAM_NOTIFICATION = "Error while sending message to endpoint {0} with status code {1} and message {2}";
	
	public static final String ERR_COPY_FILE = "Error while copying file from {0} to {1}";
	
	public static final String ERR_ENV_DEATILS_NTFOUND = "Unable to find {0} deatils in env config deatails.Please modify env deatils config{1} and try again ";

}
