/****************************************************************************
 * File Name 		: PDFFileType.java
 * Package			: com.dxc.zurich.enums
 * Author			: pmusunuru2
 * Creation Date	: Jun 15, 2023
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.enums;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * @author pmusunuru2
 * @since Jun 15, 2023 11:54:04 am
 */
public enum PDFFileType {

	PORTAL_PDF("Portal_QI_PDF"), 
	ADVISER_PDF("Adviser_QI_PDF"),
	ADVISER_AMENDED_PDF("Adviser_Amended_QI_PDF"),
	/*MANASA*/
	BO_GIODECLINED_User("BO_Giodeclined_User"),
	BO_GIODECLINED_Sl("BO_Giodeclined_SL"),
	BO_GIODECLINED_Jl("BO_Giodeclined_JL"),
	BO_DDCONFIRMATIONNOTICE("BO_DDConfirmationNotice"),
	BO_INDEMNITYCLMPBLCN("BO_IndemnityClmPblcn"),
	BO_DIRECTCREDITPAYMENT("BO_DirectCreditPayment"),
	BO_POWERATTORNEY("BO_PowerAttorney"),
	BO_ASSIGNMENTQUERY("BO_AssignmentQuery"),
	BO_ADDTRUSTEEPACKCVRLTR("BO_AddTrusteePackCvrLtr"),
	BO_PSNLDTLSCFMAN("BO_PsnlDtlsCfman"),
	BO_RETURNORIGINAL("BO_ReturnOriginal"),
	BO_TRUSTORDEEDISSUE("BO_TrustorDeedIssue"),
	BO_CANCELLATION("BO_Cancellation"),
	BO_GNRLCOMMUNICATION("BO_GnrlCommunication"),
	/*Bharath*/
	Adviser_QI_PDF("Adviser_QI_PDF"),
	Adviser_COT_PDF("Adviser_Cot_PDF"),
	Adviser_APPSUM_PDF("Adviser_Appsum_PDF"),
	BO_POLICYSCHEDULE_PDF("BO_Policyschedule_PDF"),
	BO_CLAIMSUMCITITPD_PDF("BO_Claimsumcititpd_PDF"),
	BO_CLAIMSUMCITIORTPDINTRUST_PDF("BO_Claimsumcitiortpdintrust_PDF"),
	BO_CLAIMFORMTICIORTPD_PDF("BO_Claimformticiortpd_PDF"),
	BO_GENRALMEDCOMM_PDF("BO_GENRALMEDCOMM_PDF"),
	BO_CLAIMFORMTICIORTPDINTRUST_PDF("BO_Claimfromticiortpdintrust_PDF"),
	BO_MEDRECORDREQ_PDF("BO_Medrecordreq_PDF"),
	BO_MEDCONRPTFOLUP_PDF("BO_Medcornptfolup_PDF"),
	BO_GPRPTCASLETFORM_PDF("BO_Gprptcasleform_PDF"),
	/*SOWJANYA AND SAGAR*/
	BO_PLCY_SCHD_PDF("BO_Plcy_Schd_PDF"), 
	BO_PLCY_SCHD_JL_PDF("BO_Plcy_Schd_JL_PDF"),
	BO_PLCY_CVRLTR_PDF("BO_Plcy_CvrLtr_PDF"),
	BO_COT_PDF("BO_COT_PDF"),
	ADVISER_COT_PDF("Adviser_COT_PDF"),
	ADVISER_COT_CC_PDF("Adviser_COT_CC_PDF"),
	/*Lavanya*/
	ADVISER_APPSUMMARY_PDF("Adviser_AppSummary_PDF"),
	ADVISER_AMRA_PDF("Adviser_AMRA_PDF"),
	ADVISER_TRUSTFORM_PDF("Adviser_TrustForm_PDF"),
	ADVISER_GUIDETRUSTEE_PDF("Adviser_GuideTrustee_PDF"),
	BO_TRUSTFORM_PDF("BO_TrustForm_PDF"),
	BO_GUIDETRUSTEE_PDF("BO_GuideTrustee_PDF"),
	/*kavin*/
	ADVISER_AMRACONSENT_SL_PDF("Adviser_Amraconsent_SL_PDF"),
	ADVISER_AMRACONSENT_JL_PDF("Adviser_Amraconsent_JL_PDF"),
	ADVISER_DEC_COT_SL_PDF("Adviser_Dec_COT_SL_PDF"),
	ADVISER_AMENDED_COT_SL_PDF("Adviser_Amended_COT_SL_PDF"),
	ADVISER_POLICY_COT_SL_PDF("Adviser_Policy_COT_SL_PDF"),
	ADVISER_DEC_COT_JL_PDF("Adviser_Dec_COT_JL_PDF"),
	ADVISER_AMENDED_COT_JL_PDF("Adviser_Amended_COT_JL_PDF"),
	ADVISER_POLICY_COT_JL_PDF("Adviser_Policy_COT_JL_PDF"),
	/*Customer Notification Of GP Evidence Request*/
	BO_CUS_NOT_GPEVIDENCEREQ_PDF("Bo_CusNotGpRep_PDF"),
	/*Your Zurich application - medical consent required*/
	BO_YOURZURICH_MEDCONS_PDF("Bo_YourZurichMedcons_PDF"),
	/*GP Report*/
	BO_GPREPORT_PDF("Bo_GPreport_PDF"),
	/*Targeted GP Report Cover Letter*/
	BO_TARGETGPREPORTCVR_PDF("Bo_TargetGpreportcvr_PDF"),
	/*Additional Info Needed from GP*/
	BO_ADDINFOGP_PDF("Bo_AddinfoGP_PDF"),
	/*AMRA Consent*/
	BO_AMRACONSENT_SL_PDF("Bo_Amraconsent_SL_PDF"),
	BO_AMRACONSENT_JL_PDF("Bo_Amraconsent_JL_PDF"),
	/*Your Zurich policy is being re-assessed*/
	BO_YOURZURICH_REASSESS_PDF("Bo_YourZurichreassess_PDF"),
	/*PDC Pack*/
	BO_PDC_SL_PDF("Bo_PDC_SL_PDF"),
	BO_PDC_JL_PDF("Bo_PDC_JL_PDF"),
	BO_PDC_CVRLTR_SL_PDF("Bo_PDC_CvrLtlr_SL_PDF"),
	BO_PDC_CVRLTR_JL_PDF("Bo_PDC_CvrLtlr_JL_PDF"),
	/*TGR Report Blood NML*/
	BO_TGRBLOODNML_PDF("Bo_TgrbloodNml_PDF"),
	/*TGR Report Cancer NML*/
	BO_TGRCNCRNML_PDF("Bo_TgrcncrNml_PDF"),
	/*Guide to being a Trustee*/
	/*Additional trustee pack cover letter*/
	BO_ADDTRUSTEECVRLET_PDF("Bo_AddTrusteecvr_PDF"),
	/*COT*/
	BO_POLICY_COT_PDF("Bo_Policy_COT_PDF"),
	BO_CLOSEDAPP_COT_PDF("Bo_ClosedApp_COT_PDF"),
	BO_CC_AMRACONSENT_SL_PDF("Cc_Amraconsent_SL_PDF"),
	BO_CC_AMRACONSENT_JL_PDF("Cc_Amraconsent_JL_PDF"),
	/*Bhargavi*/
	BO_Claims_PDF("BO_Claims_PDF"),
	INVALID("InValid PDF"),
	/*sagar*/
	BO_KEYFEATURE("BO_Kfd_PDF"),
	BO_TERMANDCONDITIONS("BO_TC_PDF");

	private String strPDFAppName;

	PDFFileType(String strPDFFileName) {
		this.setPDFAppName(strPDFFileName);
	}

	/**
	 * @return the strPDFAppName
	 */
	public String getPDFAppName() {
		return strPDFAppName;
	}

	/**
	 * @param strPDFAppName the strPDFAppName to set
	 */
	public void setPDFAppName(String strPDFAppName) {
		this.strPDFAppName = strPDFAppName;
	}

	public static PDFFileType getPDFFileType(final String strPDFAppName) {
		List<PDFFileType> lstPdfFileTypes = Arrays.asList(PDFFileType.values());
		PDFFileType aPDFileType = lstPdfFileTypes.stream()
				.filter(objPDFFileType -> StringUtils.equalsIgnoreCase(strPDFAppName, objPDFFileType.getPDFAppName()))
				.findFirst().orElse(PDFFileType.INVALID);
		return aPDFileType;
	}
}
