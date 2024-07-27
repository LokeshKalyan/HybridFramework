/****************************************************************************
 * File Name 		: SummaryReportConstants.java
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
 * @since Feb 16, 2021 11:12:01 am
 */
public final class SummaryReportConstants {

	public static final String SUMMARY_SHEETNAME = "Summary";

	public static final String REF_NUM_SHEETNAME = "RefNumbers";

	public static final String QUOTE_SUMMARY_SHEETNAME = "QuoteSummary";

	public static final String REINSURANCE_DETAILS_SHEETNAME = "ReinsuranceDetails";

	public static final String REINSURANCE_SHEETNAME = "Reinsurance";

	public static final String APPILCATION_RESULTS_SHEET_NAME = "ApplicationResults";

	public static final String[] SUMMARY_REPORT_SHEETS = { SUMMARY_SHEETNAME, REF_NUM_SHEETNAME,
			QUOTE_SUMMARY_SHEETNAME, REINSURANCE_DETAILS_SHEETNAME, REINSURANCE_SHEETNAME,
			APPILCATION_RESULTS_SHEET_NAME };

	public static final String SERIAL_NUMBER_HEADER = "S.NO.";

	public static final String SECNARIO_HEADER = "TestScenarioName";

	public static final String SECNARIO_DESCRIPTION_HEADER = "TestScenarioDescription";

	public static final String BROWSER_HEADER = "Browser";

	public static final String MACHINE_HEADER = "Machine";

	public static final String EXECUTION_STATUS_HEADER = "ExecutionStatus";

	public static final String USER_NAME_HEADER = "UserName";

	public static final String SURNAME_HEADER = "Surname";

	public static final String QUOTE_REFERENCE_HEADER = "QuoteReference";

	public static final String APPLICATION_REFERENCE_HEADER = "ApplicationReference";

	public static final String QUATATION_APPLICATION_REF_HEADER = "QuoteNAppRefNumber";

	public static final String PRODUCT_DECISION_HEADER = "Product{0}-Decision";
	
	public static final String PRODUCT_DECISION_PREMIUM_HEADER = "Product{0}-Premium";
	
	public static final String PRODUCT_DECISION_REFER1_HEADER = "Product{0}-Refer1-Decision";
	
	public static final String PRODUCT_DECISION_REFER2_HEADER = "Product{0}-Refer2-Decision";
	
	public static final String PRODUCT_DECISION_REFER1_JL_HEADER = "Product{0}-Refer1-JL-Decision";
	
	public static final String PRODUCT_DECISION_REFER2_JL_HEADER = "Product{0}-Refer2-JL-Decision";
	
	public static final String PRODUCT_DECISION_REVISED_TERMS1_HEADER = "Product{0}-Revised terms1-Decision";
	
	public static final String PRODUCT_DECISION_REVISED_TERMS_BENIFITS_HEADER = "Product{0}-Revised terms-Benifits-Decision";
	
	public static final String PRODUCT_DECISION_UNABLE_TO_OFFER_TERMS1_HEADER = "Product{0}-Unable to offer terms1-Decision";
	
	public static final String PRODUCT_DECISION_UNABLE_TO_OFFER_TERMS2_HEADER = "Product{0}-Unable to offer terms2-Decision";
	
	public static final String PRODUCT_DECISION_UNABLE_TO_OFFER_TERMS_BENIFITS_HEADER = "Product{0}-Unable to offer terms-Benifits-Decision";
	
	public static final String PRODUCT_DECISION_REFER_BENIFITS_HEADER = "Product{0}-Refer-Benifits-Decision";
	
	public static final String PRODUCT_DECISION_STANDARD_TERMS_BENIFITS_HEADER = "Product{0}-Standard terms-Benifits-Decision";

	public static final String PRODUCT_DECISION_HEADER_REGEX = "Product\\d+-Decision$";

	public static final String POLICY_NUMBER_HEADER_REGEX = "Policy \\d+$";

	public static final String POLICY_NUMBER_HEADER = "Policy {0}";

	public static final String CLIENT_FULLNAME_HEADER = "ClientFullName";

	public static final String DEAL_NAME_HEADER = "DEAL_NAME";

	public static final String TOTAL_FREQ_PREM_HEADER = "Total_Freq_Prem";

	public static final String TOTAL_PREM_DISCLOSURE_HEADER = "Total_Prem_Disclosure";

	public static final String IN_COM_ACC_PRODONE_HEADER = "In_com_Acc_ProdONE_{0}";

	public static final String IN_COM_ACC_PRODTWO_HEADER = "In_com_Acc_ProdTWO_{0}";

	public static final String IN_COM_IND_PRODONE_HEADER = "In_com_Ind_ProdONE_{0}";

	public static final String IN_COM_IND_PRODTWO_HEADER = "In_com_Ind_ProdTWO_{0}";

	public static final String RENW_COM_PRODONE_HEADER = "RenW_Com_ProdONE_{0}";

	public static final String RENW_COM_PRODTWO_HEADER = "RenW_Com_ProdTWO_{0}";

	public static final String REINSURER_HEADER = "Reinsurer";

	public static final String TREATY_HEADER = "Treaty";

	public static final String RATES_VARIATION_GROUP_HEADER = "Rates Variation Group";

	public static final String SEQUENCE_NO_HEADER = "Sequence No";

	public static final String PERCENTAGE_RETAINED_HEADER = "Percentage Retained";

	public static final String SUM_REASSURED_HEADER = "Sum Reassured";

	public static final String INITIAL_REBATE_HEADER = "Initial Rebate";

	public static final String RENEWAL_REBATE_HEADER = "Renewal Rebate";

	public static final String INITIAL_REBATE_PERIOD_HEADER = "Initial Rebate Period";

	public static final String INITIAL_DISCOUNT_PERIOD_TYPE_HEADER = "Initial Discount Period Type";

	public static final String NET_PREMIUM_HEADER = "Net Premium";

	public static final String COST_TYPE_HEADER = "Cost Type";

	public static final String START_DATE_HEADER = "Start Date";

	public static final String END_DATE_HEADER = "End Date";

	public static final String MONETARY_AMOUNT_HEADER = "Monetary Amount";

	public static final String CURRENCY_CODE_HEADER = "Currency Code";

	public static final String FREQUENCY_HEADER = "Frequency";

	public static final String TABLE_HEADER = "HEADER";

	public static final String TABLE_DATA = "DATA";

	public static final String[] NA_POLICIES = { "Refer", "Unable to offer terms" };

	public static final String NO_RESULT = "N/A";

	public static final String AML_CONFIG_HEADER[] = { SECNARIO_HEADER, BROWSER_HEADER, "ALM URL", "UserName",
			"ALM ClientId","ALM Client Secret", "Domain", "Project","TestSetID", "TestSetPath", "TestSetName", "TestCasePrefix", "RunName",
			EXECUTION_STATUS_HEADER, "ALM Attachment(s)" };
	
	public static final String REFER_PRODUCT_DECISION_HEADER = "Refer";
	
	public static final String REVISED_TERMS_PRODUCT_DECISION_HEADER = "Revised terms";
	
	public static final String UNABLE_TO_OFFER_TERMS_PRODUCT_DECISION_HEADER = "Unable to offer terms";
	
	public static final String STANDARD_TERMS_PRODUCT_DECISION_HEADER = "Standard terms";
	
	public static final String DECISION_PREMIUM_AMOUNT = "Decision Page - Total Premium Amount";
	
}
