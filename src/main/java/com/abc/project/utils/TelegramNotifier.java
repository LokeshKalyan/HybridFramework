/****************************************************************************
 * File Name 		: TelegramNotifier.java
 * Package			: com.dxc.zurich.utils
 * Author			: pmusunuru2
 * Creation Date	: Jun 17, 2021
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.abc.project.constants.AppConstants;
import com.abc.project.constants.ErrorMsgConstants;
import com.abc.project.enums.StartFinish;
import com.dxc.constants.RemoteMultiPlatformConstants;

/**
 * @author pmusunuru2
 * @since Jun 17, 2021 11:06:58 am
 */
public class TelegramNotifier {

	private static final Logger LOGGER = LogManager.getLogger(TelegramNotifier.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	private static final String CHAT_ID_KEY = "chat_id";

	private static final String PARSE_MODE_KEY = "parse_mode";

	private static final String PARSE_MODE_VALUE = "html";

	private static final String TEXT_KEY = "text";

	private static String getAcessToken() {
		String strAcessToken = PropertyHandler.getExternalString(AppConstants.BROWSER_TELEGRAMACESSTOKEN_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		return strAcessToken;
	}

	private static boolean canSendNotification() {
		String strSysStatus = System.getProperty(AppConstants.BROWSER_SEND_TELEGRAM_NOTIFICATION_KEY);
		String strStatus = StringUtils.isEmpty(StringUtils.trim(strSysStatus))
				? PropertyHandler.getExternalString(AppConstants.BROWSER_SEND_TELEGRAM_NOTIFICATION_KEY,
						AppConstants.APP_PROPERTIES_NAME)
				: StringUtils.trim(strSysStatus);
		return BooleanUtils.toBoolean(strStatus);
	}

	private static String getMessageFormat() {
		String strMessageFormat = PropertyHandler.getExternalString(AppConstants.BROWSER_TELEGRAM_NOTIFICATION_FORMAT_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		return strMessageFormat;
	}

	private static RequestConfig getRequestConfig() {
		int timeout = RemoteMultiPlatformConstants.CONNECTION_TIMEOUT;
		RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * 1000)
				.setConnectionRequestTimeout(timeout * 1000).setSocketTimeout(timeout * 1000).build();
		return config;
	}

	private static CloseableHttpClient getDefaultHttpClient() throws Exception  {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				// No need to implement.
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				// No need to implement.
			}
		} };
		SSLContext aSSLContext = SSLContext.getInstance(SSLConnectionSocketFactory.SSL);
		aSSLContext.init(null, trustAllCerts, new java.security.SecureRandom());
		SSLConnectionSocketFactory aSslConnectionSocketFactory = new SSLConnectionSocketFactory(aSSLContext);
		return HttpClients.custom().setSSLSocketFactory(aSslConnectionSocketFactory)
				.setSSLHostnameVerifier(new NoopHostnameVerifier()).setDefaultRequestConfig(getRequestConfig()).build();
	}

	public synchronized static void sendNotification(String strChatID, Object... values) {
		if (!canSendNotification()) {
			return;
		}
		String strAcessToken = getAcessToken();
		String strMessageFormat = getMessageFormat();
		String strAPIURL = PropertyHandler.getExternalString(AppConstants.BROWSER_TELEGRAM_NOTIFICATION_URL_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		if (StringUtils.isEmpty(StringUtils.trim(strAcessToken)) || StringUtils.isEmpty(StringUtils.trim(strAcessToken))
				|| StringUtils.isEmpty(StringUtils.trim(strMessageFormat))
				|| StringUtils.isEmpty(StringUtils.trim(strChatID)) || values == null || values.length == 0) {
			return;
		}
		String strMessage = AppUtils.formatMessage(strMessageFormat, values);
		String strBotAPI = String.format(strAPIURL, strAcessToken);
		String strLogMessage = AppUtils.formatMessage("Sending Message {0} to Group {1}", strMessage, strChatID);
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
		try (CloseableHttpClient httpclient = getDefaultHttpClient()) {
			HttpPost request = new HttpPost(strBotAPI);
			request.addHeader(HTTP.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
			JSONObject aMessage = new JSONObject();
			aMessage.put(CHAT_ID_KEY, strChatID);
			aMessage.put(PARSE_MODE_KEY, PARSE_MODE_VALUE);
			aMessage.put(TEXT_KEY,strMessage);
			StringEntity aEntity = new StringEntity(aMessage.toString());
			request.setEntity(aEntity);
			HttpResponse aResponse = httpclient.execute(request);
			HttpEntity aResponseEntity = aResponse.getEntity();
			StringBuilder response = new StringBuilder();
			try (BufferedReader aReader = new BufferedReader(new InputStreamReader(aResponseEntity.getContent()))) {
				String inputLine = "";
				while ((inputLine = aReader.readLine()) != null) {
					response.append(inputLine);
				}
				int iStatusCode = aResponse.getStatusLine().getStatusCode();
				String strErrorMessage = EnglishReasonPhraseCatalog.INSTANCE.getReason(iStatusCode, Locale.US);
				if (iStatusCode != HttpStatus.SC_OK && iStatusCode != HttpStatus.SC_CONFLICT
						&& iStatusCode != HttpStatus.SC_RESET_CONTENT) {
					throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_TELEGRAM_NOTIFICATION, strBotAPI,
							String.valueOf(iStatusCode), strErrorMessage));
				}
				LOGGER.info(AppUtils.formatMessage("Sucessfully Sent Message to Group {0} Response {1}", strChatID,
						response.toString()));
			}
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}
}
