/****************************************************************************
 * File Name 		: EmailUtils.java
 * Package			: com.dxc.zurich.utils
 * Author			: pmusunuru2
 * Creation Date	: Feb 17, 2021
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abc.project.beans.AppEnvConfigBean;
import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.config.MasterConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.ErrorMsgConstants;
import com.abc.project.enums.StartFinish;

/**
 * @author pmusunuru2
 * @since Feb 17, 2021 1:51:52 pm
 */
public class EmailUtils {

	private static final Logger LOGGER = LogManager.getLogger(EmailUtils.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	private static String getEmailConfigPath() {
		String strEmailConfig = PropertyHandler.getExternalString(AppConstants.EMAIL_CONFIG_PATH_KEY,
				AppConstants.APP_PROPERTIES_NAME);

		if (StringUtils.equalsIgnoreCase(strEmailConfig, AppConstants.EMAIL_CONFIG_PATH_KEY)) {
			strEmailConfig = null;
		}
		return strEmailConfig;
	}

	public static String getEmailConfigByKey(String strKey) {
		String strEmailConfig = getEmailConfigPath();
		String strEmailProp = PropertyHandler.getExternalString(strKey, strEmailConfig);
		if (StringUtils.equalsIgnoreCase(strEmailProp, strKey)) {
			strEmailProp = null;
		}
		return strEmailProp;
	}

	private static Properties getEmailProperties(boolean isRead) {
		Properties emailProp = new Properties();
		emailProp.put(AppConstants.EMAIL_HOST_KEY, isRead ? getEmailConfigByKey(AppConstants.EMAIL_READ_HOST_KEY)
				: getEmailConfigByKey(AppConstants.EMAIL_HOST_KEY));
		emailProp.put(AppConstants.EMAIL_PORT_KEY, isRead ? getEmailConfigByKey(AppConstants.EMAIL_READ_PORT_KEY)
				: getEmailConfigByKey(AppConstants.EMAIL_PORT_KEY));
		emailProp.put(AppConstants.EMAIL_AUTHENTICATION_KEY,
				getEmailConfigByKey(AppConstants.EMAIL_AUTHENTICATION_KEY));
		emailProp.put(AppConstants.EMAIL_DEBUG_KEY, getEmailConfigByKey(AppConstants.EMAIL_DEBUG_KEY));
		emailProp.put(AppConstants.EMAIL_DEBUG_AUTHENTICATION_KEY, getEmailConfigByKey(AppConstants.EMAIL_DEBUG_KEY));
		emailProp.put(AppConstants.EMAIL_SSL_ENABLE_KEY, getEmailConfigByKey(AppConstants.EMAIL_SSL_ENABLE_KEY));
		if (isRead) {
			emailProp.put(AppConstants.EMAIL_SSL_SOCKET_FACTORY_PORT_KEY,
					emailProp.getProperty(AppConstants.EMAIL_PORT_KEY));
			emailProp.put(AppConstants.EMAIL_SSL_SOCKET_FACTORY_CLASS_KEY,
					getEmailConfigByKey(AppConstants.EMAIL_SSL_SOCKET_FACTORY_CLASS_KEY));
		}
		return emailProp;
	}

	public static void sendEmail(String strEmail, String strToEmail, String strCCEmail, String strSubject,
			String strMessage, File... aAttachMents) {

		String strUserName = StringUtils.isEmpty(strEmail) ? getEmailConfigByKey(AppConstants.EMAIL_USER_KEY)
				: strEmail;
		String strPassword = getEmailConfigByKey(AppConstants.EMAIL_USER_PASSWORD_KEY);
		String strFromEamil = StringUtils.isEmpty(strEmail) ? getEmailConfigByKey(AppConstants.EMAIL_FROM_KEY)
				: strEmail;
		strToEmail = StringUtils.isEmpty(strToEmail) ? getEmailConfigByKey(AppConstants.EMAIL_TO_KEY) : strToEmail;
		StringBuilder strAttachments = new StringBuilder();
		String strAttachmentName = null;
		if (aAttachMents != null) {
			for (File aAttachMent : aAttachMents) {
				strAttachments.append(aAttachMent.getName()).append(",");
			}
			strAttachmentName = strAttachments.toString();
			if (strAttachmentName.endsWith(AppConstants.SEPARATOR_COMMA)) {
				strAttachmentName = strAttachmentName.substring(0,
						strAttachmentName.length() - AppConstants.SEPARATOR_COMMA.length());
			}
		}
		String logMessage = String.format("Sending Email with subject [%s] and message [%s] with%s attachment(s)",
				strSubject, strMessage,
				(StringUtils.isEmpty(strAttachmentName)) ? "out" : " " + strAttachmentName + " as");
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(logMessage));
			if (StringUtils.isEmpty(strUserName) || StringUtils.isEmpty(strPassword)
					|| StringUtils.isEmpty(strFromEamil) || StringUtils.isEmpty(strToEmail)) {
				LOGGER.error(MessageFormat.format(ErrorMsgConstants.ERR_SEND_EMAIL_VALIDATION, strUserName,
						strFromEamil, strToEmail, getEmailConfigPath()));
				return;
			}
			Properties emailProp = getEmailProperties(false);
			emailProp.put(AppConstants.EMAIL_FROM_KEY, strFromEamil);
			emailProp.put(AppConstants.EMAIL_STARTTLS_KEY, getEmailConfigByKey(AppConstants.EMAIL_STARTTLS_KEY));
			Session aSession = Session.getInstance(emailProp, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(strUserName, strPassword);
				}
			});

			MimeMessage aMimeMessage = new MimeMessage(aSession);
			if (StringUtils.indexOf(strToEmail, ",") > 0) {
				aMimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(strToEmail));
			} else {
				aMimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(strToEmail));
			}
			if (StringUtils.indexOf(strCCEmail, ",") > 0) {
				aMimeMessage.setRecipients(Message.RecipientType.CC, InternetAddress.parse(strCCEmail));
			}
			if (!StringUtils.isEmpty(strCCEmail) && StringUtils.indexOf(strCCEmail, ",") < 0) {
				aMimeMessage.setRecipient(Message.RecipientType.CC, new InternetAddress(strCCEmail));
			}
			aMimeMessage.setSubject(strSubject);
			aMimeMessage.setSentDate(new Date());
			// Create the message part
			BodyPart aMessageBodyPart = new MimeBodyPart();

			// Now set the actual message
			aMessageBodyPart.setContent(strMessage, "text/html");

			// Create a multipar message
			Multipart aMultipart = new MimeMultipart();

			// Set text message part
			aMultipart.addBodyPart(aMessageBodyPart);
			if (aAttachMents != null) {
				for (File aAttachMent : aAttachMents) {
					if (aAttachMent.exists() && aAttachMent.isFile()) {
						// Part two is attachment
						MimeBodyPart aFileBodyPart = new MimeBodyPart();
						DataSource aAttachMentDataSource = new FileDataSource(aAttachMent);

						aFileBodyPart.setDataHandler(new DataHandler(aAttachMentDataSource));
						aFileBodyPart.setFileName(aAttachMent.getName());
						aMultipart.addBodyPart(aFileBodyPart);
					}
				}
			}
			// Send the complete message parts
			aMimeMessage.setContent(aMultipart);
			// Send message
			Transport.send(aMimeMessage);
		} catch (Exception ex) {
			String strErrorMessage = MessageFormat.format(ErrorMsgConstants.ERR_SEND_EMAIL, strToEmail, strFromEamil,
					strSubject);
			LOGGER.error(strErrorMessage);
			ERROR_LOGGER.error(strErrorMessage, ex);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(logMessage));
		}
	}

	public static String getEmailMessageByTest(String strEmail, String strSubject, String strMessagePrefix) {

		String strUserName = StringUtils.isEmpty(strEmail) ? getEmailConfigByKey(AppConstants.EMAIL_USER_KEY)
				: strEmail;
		String strPassword = getEmailConfigByKey(AppConstants.EMAIL_USER_PASSWORD_KEY);
		String strFromEamil = StringUtils.isEmpty(strEmail) ? getEmailConfigByKey(AppConstants.EMAIL_FROM_KEY)
				: strEmail;
		Store aStore = null;
		Folder aEmailFolder = null;
		String logMessage = String.format("Reading Email with subject [%s] and message prefix [%s]", strSubject,
				strMessagePrefix);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(logMessage));
			if (StringUtils.isEmpty(strUserName) || StringUtils.isEmpty(strPassword)
					|| StringUtils.isEmpty(strFromEamil)) {
				LOGGER.error(MessageFormat.format(ErrorMsgConstants.ERR_READ_EMAIL_VALIDATION, strUserName,
						strFromEamil, getEmailConfigPath()));
				return null;
			}
			Properties emailProp = getEmailProperties(true);
			emailProp.put(AppConstants.EMAIL_FROM_KEY, strFromEamil);
			Session aSession = Session.getInstance(emailProp, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(strUserName, strPassword);
				}
			});
			aStore = aSession.getStore("imaps");

			aStore.connect(emailProp.getProperty(AppConstants.EMAIL_HOST_KEY), strUserName, strPassword);

			aEmailFolder = aStore.getFolder("INBOX");
			aEmailFolder.open(Folder.READ_WRITE);

			// retrieve the messages from the folder in an array and print it
			Message[] aEmailFoderMessages = aEmailFolder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

			for (Message aMessage : aEmailFoderMessages) {
				String strMessageSubJect = aMessage.getSubject();
				String strMessageText = getTextFromMessage(aMessage);
				String strFormattedMessage = StringUtils.isEmpty(StringUtils.trim(strMessageText)) ? strMessageText
						: org.jsoup.Jsoup.parse(strMessageText).text();
				String strReceived = AppUtils.getDateAsString(aMessage.getReceivedDate(),
						AppConstants.EXEC_REPORTS_FOLDER_DT_FORMAT);
				String strReportDate = AppUtils.getDateAsString(new Date(), AppConstants.EXEC_REPORTS_FOLDER_DT_FORMAT);
				if (!StringUtils.equalsIgnoreCase(strMessageSubJect, strSubject)
						|| !StringUtils.containsIgnoreCase(strFormattedMessage, strMessagePrefix)
						|| !StringUtils.equalsIgnoreCase(strReportDate, strReceived)) {
					continue;
				}
				aMessage.setFlag(Flags.Flag.SEEN, true);
				return strMessageText;
			}
		} catch (Exception ex) {
			String strErrorMessage = MessageFormat.format(ErrorMsgConstants.ERR_READ_EMAIL, strFromEamil, strSubject);
			LOGGER.error(strErrorMessage);
			ERROR_LOGGER.error(strErrorMessage, ex);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(logMessage));
			try {
				// close the store and folder objects
				if (aEmailFolder != null) {
					aEmailFolder.close(false);
				}
				if (aStore != null) {
					aStore.close();
				}
			} catch (MessagingException e) {
			}

		}
		return null;
	}

	public static String getEmailAttachMentMessageByTest(BrowsersConfigBean aBrowsersConfigBean, String strEmail, String strSubject, String strMessagePrefix,
			String[] strSearchFileNames) {
		
		String strUserName = StringUtils.isEmpty(strEmail) ? getEmailConfigByKey(AppConstants.EMAIL_USER_KEY)
				: strEmail;
		String strPassword = getEmailConfigByKey(AppConstants.EMAIL_USER_PASSWORD_KEY);
		String strFromEamil = StringUtils.isEmpty(strEmail) ? getEmailConfigByKey(AppConstants.EMAIL_FROM_KEY)
				: strEmail;
		Store aStore = null;
		Folder aEmailFolder = null;
		String logMessage = String.format("Reading Email with subject [%s] and message prefix [%s]", strSubject,
				strMessagePrefix);
		try {
			LOGGER.info(StartFinish.START.getFormattedMsg(logMessage));
			if (StringUtils.isEmpty(strUserName) || StringUtils.isEmpty(strPassword)
					|| StringUtils.isEmpty(strFromEamil)) {
				LOGGER.error(MessageFormat.format(ErrorMsgConstants.ERR_READ_EMAIL_VALIDATION, strUserName,
						strFromEamil, getEmailConfigPath()));
				return null;
			}
			String strFileDownloadLocation = AppUtils.getDownloadFolder(aBrowsersConfigBean);
			Properties emailProp = getEmailProperties(true);
			emailProp.put(AppConstants.EMAIL_FROM_KEY, strFromEamil);
			Session aSession = Session.getInstance(emailProp, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(strUserName, strPassword);
				}
			});
			aStore = aSession.getStore("imaps");

			aStore.connect(emailProp.getProperty(AppConstants.EMAIL_HOST_KEY), strUserName, strPassword);

			aEmailFolder = aStore.getFolder("INBOX");
			aEmailFolder.open(Folder.READ_WRITE);

			// retrieve the messages from the folder in an array and print it
			Message[] aEmailFoderMessages = aEmailFolder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

			for (Message aMessage : aEmailFoderMessages) {
				String strMessageSubJect = aMessage.getSubject();
				String strMessageText = getTextFromMessage(aMessage);
				String strFormattedMessage = StringUtils.isEmpty(StringUtils.trim(strMessageText)) ? strMessageText
						: org.jsoup.Jsoup.parse(strMessageText).text();
				String strReceived = AppUtils.getDateAsString(aMessage.getReceivedDate(),
						AppConstants.EXEC_REPORTS_FOLDER_DT_FORMAT);
				String strReportDate = AppUtils.getDateAsString(new Date(), AppConstants.EXEC_REPORTS_FOLDER_DT_FORMAT);
				String strContentType = aMessage.getContentType();
				if (!StringUtils.equalsIgnoreCase(strMessageSubJect, strSubject)
						|| !StringUtils.containsIgnoreCase(strFormattedMessage, strMessagePrefix)
						|| !StringUtils.equalsIgnoreCase(strReportDate, strReceived) || !StringUtils
								.containsIgnoreCase(strContentType, AppConstants.EMAIL_ATTACHMENT_CONTENT_TYPE)) {
					continue;
				}
				Multipart aMultiPart = (Multipart) aMessage.getContent();
				for (int i = 0; i < aMultiPart.getCount(); i++) {
					MimeBodyPart aPart = (MimeBodyPart) aMultiPart.getBodyPart(i);
					if (!Part.ATTACHMENT.equalsIgnoreCase(aPart.getDisposition())) {
						continue;
					}
					String strFileName = aPart.getFileName();
					for (String strSearchFileName : strSearchFileNames) {
						if (!StringUtils.containsIgnoreCase(strFileName, strSearchFileName)) {
							continue;
						}
						File aTargetLoc = Paths.get(strFileDownloadLocation, strFileName).toFile();
						aPart.saveFile(aTargetLoc);
					}
				}
				aMessage.setFlag(Flags.Flag.SEEN, true);
				return strMessageText;
			}
		} catch (Exception ex) {
			String strErrorMessage = MessageFormat.format(ErrorMsgConstants.ERR_READ_EMAIL, strFromEamil, strSubject);
			LOGGER.error(strErrorMessage);
			ERROR_LOGGER.error(strErrorMessage, ex);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(logMessage));
			try {
				// close the store and folder objects
				if (aEmailFolder != null) {
					aEmailFolder.close(false);
				}
				if (aStore != null) {
					aStore.close();
				}
			} catch (MessagingException e) {
			}

		}
		return null;
	}

	private static String getTextFromMessage(Message aMessage) throws IOException, MessagingException {
		String result = "";
		if (aMessage.isMimeType("text/plain")) {
			result = aMessage.getContent().toString();
		} else if (aMessage.isMimeType("multipart/*")) {
			MimeMultipart mimeMultipart = (MimeMultipart) aMessage.getContent();
			result = getTextFromMimeMultipart(mimeMultipart);
		}
		return result;
	}

	private static String getTextFromMimeMultipart(MimeMultipart aMimeMultiPart)
			throws IOException, MessagingException {

		int count = aMimeMultiPart.getCount();
		if (count == 0)
			throw new MessagingException("Multipart with no body parts not supported.");
		boolean multipartAlt = new ContentType(aMimeMultiPart.getContentType()).match("multipart/alternative");
		if (multipartAlt)
			// alternatives appear in an order of increasing
			// faithfulness to the original content. Customize as req'd.
			return getTextFromBodyPart(aMimeMultiPart.getBodyPart(count - 1));
		String result = "";
		for (int i = 0; i < count; i++) {
			BodyPart bodyPart = aMimeMultiPart.getBodyPart(i);
			result += getTextFromBodyPart(bodyPart);
		}
		return result;
	}

	private static String getTextFromBodyPart(BodyPart aBodyPart) throws IOException, MessagingException {
		String result = "";
		if (aBodyPart.isMimeType("text/plain")) {
			result = (String) aBodyPart.getContent();
		} else if (aBodyPart.isMimeType("text/html")) {
			String html = (String) aBodyPart.getContent();
			result = html;// org.jsoup.Jsoup.parse(html).text();
		} else if (aBodyPart.getContent() instanceof MimeMultipart) {
			result = getTextFromMimeMultipart((MimeMultipart) aBodyPart.getContent());
		}
		return result;
	}

	public static void main(String[] args) {

		try {
			LOGGER.info(StartFinish.START.getFormattedMsg("mergeConfigProperties"));
			MasterConfig aMasterConfig = MasterConfig.getInstance();
			AppEnvConfigBean aPPRunEnv = aMasterConfig.getAppEnvConfigBean();
			if (aPPRunEnv == null) {
				String strAPPENV = MasterConfig.getInstance().getAppName();
				LOGGER.error(String.format("Please Modify UnKnown ENV :- %s and rerun application", strAPPENV));
				System.exit(0); // Exception case and application cannot proceed so just simply exit by logging
			}
			String strConfigPopFile = String.format("%s/%s%s", AppConstants.APP_ENV_PROPERTIES_LOC,
					aPPRunEnv.getPropertyName(), AppConstants.PROPERTIES_FILE_SUFFIX);
			PropertyHandler.mergeExternalResourceBundle(strConfigPopFile, AppConstants.APP_PROPERTIES_NAME);
			String strMSGText = getEmailMessageByTest(getEmailConfigByKey(AppConstants.EMAIL_USER_KEY),
					"Your Zurich application: medical consent required", "Hi Mr Pipelineggs onevcn");
			System.out.println(strMSGText);
			Set<String> stHref = AppUtils.getHrefText(strMSGText);
			for (String strHref : stHref) {
				System.out.println(strHref);
			}
			String strEmailPrefix = "https://demo.docusign.net/Member/EmailStart.aspx";
			Optional<String> aOPURl = stHref.stream()
					.filter(strEmailLinkUrl -> StringUtils.containsIgnoreCase(strEmailLinkUrl, strEmailPrefix))
					.findAny();
			System.out.println(aOPURl.get());
		} catch (Exception ex) {
			// TODO: handle exception
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg("mergeConfigProperties"));
		}
	}
}
