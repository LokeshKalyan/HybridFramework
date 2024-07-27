/****************************************************************************
 * File Name 		: AppUtils.java
 * Package			: com.dxc.zurich.utils
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
package com.abc.project.utils;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.exec.OS;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.abc.project.beans.AppEnvConfigBean;
import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.config.AppConfig;
import com.abc.project.config.AppContext;
import com.abc.project.config.MasterConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.DataBaseConstants;
import com.abc.project.constants.ErrorMsgConstants;
import com.abc.project.constants.SummaryReportConstants;
import com.abc.project.enums.AppRunMode;
import com.abc.project.grid.GridClientHelper;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 11:15:01 am
 */
public class AppUtils {

	/***
	 * Fetches the file path from resources and system path
	 * 
	 * @param strFilePath
	 * @return
	 */
	public static File getFileFromPath(String strFilePath) {
		File aFilePath = new File(strFilePath);
		if (aFilePath != null && aFilePath.exists()) {
			return aFilePath;
		}
		String strResourcePath = System.getProperty(AppConstants.RESOURCE_PATH_KEY);
		File aConfigPath = StringUtils.isEmpty(StringUtils.trim(strResourcePath))
				|| StringUtils.isEmpty(StringUtils.trim(strFilePath)) ? null
						: Paths.get(strResourcePath, strFilePath).normalize().toFile();
		if (aConfigPath != null && aConfigPath.exists()) {
			return aConfigPath;
		}
		URL aResourcePath = Thread.currentThread().getContextClassLoader().getResource(strFilePath);
		if (aResourcePath != null) {
			String strResoucePath = StringUtils.replace(aResourcePath.getPath(), "%20", " ");
			return new File(strResoucePath);
		}
		return aFilePath;
	}

	/**
	 * Gets the date as string.
	 *
	 * @param inputDate the input date
	 * @param format    the format
	 * @return the date as string
	 */
	public static String getDateAsString(Date inputDate, String format) {
		if (inputDate == null) {
			return "";
		}
		DateFormat dateFormat = new SimpleDateFormat(format);
		String stringDate = dateFormat.format(inputDate);
		return stringDate;
	}

	public static String getUTCDateAsString(Date inputDate, String format) {
		if (inputDate == null) {
			return "";
		}
		DateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String stringDate = dateFormat.format(inputDate);
		return stringDate;
	}

	/**
	 * Parses the date String for the given format.
	 *
	 * @param dateString the date string
	 * @param format     the format
	 * @return the parsed date
	 */
	public static Date parseDate(String dateString, String format, Logger aLogger) {

		Date outputDate = null;
		try {
			DateFormat dateFormat = new SimpleDateFormat(format);
			outputDate = dateFormat.parse(dateString);
		} catch (java.text.ParseException e) {
			aLogger.error("Error while parsing the Date String:{} using the format {}", dateString, format);
		}
		return outputDate;
	}

	public static Date updateDays(Date date, int dDays) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, dDays);
		return calendar.getTime();
	}

	/***
	 * Format message by values specified
	 * 
	 * @param strMessage
	 * @param values
	 * @return
	 */
	public static String formatMessage(String strMessage, Object... values) {
		if (StringUtils.isEmpty(strMessage)) {
			strMessage = ErrorMsgConstants.ERR_DEFAULT;
			values = null;
		}
		if (values == null || values.length <= 0) {
			return strMessage;
		}
		return MessageFormat.format(strMessage, values);
	}

	/**
	 * Returns a list with all links contained in the input
	 */
	public static List<String> extractUrls(String strText) {
		List<String> containedUrls = new ArrayList<String>();
		if (StringUtils.isEmpty(StringUtils.trim(strText))) {
			return containedUrls;
		}
		String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
		Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
		Matcher urlMatcher = pattern.matcher(strText);

		while (urlMatcher.find()) {
			String strLink = StringUtils.substring(strText, urlMatcher.start(0), urlMatcher.end(0));
			containedUrls.add(strLink);
		}
		return containedUrls;
	}

	/**
	 * Returns a list with all links contained in the input
	 */
	public static Set<String> getHrefText(String strHtml) {
		Set<String> stHref = new HashSet<>();
		if (StringUtils.isEmpty(StringUtils.trim(strHtml))) {
			return stHref;
		}
		Pattern aPattern = Pattern.compile("href=\"(.*?)\"", Pattern.DOTALL);
		Matcher aMacher = aPattern.matcher(strHtml);
		while (aMacher.find()) {
			String strURL = aMacher.group(1);
			stHref.add(StringUtils.replace(strURL, "&amp;", "&"));
		}
		return stHref;
	}

	public static String getValidPartScenarioName(String testScenarioName) {
		String strRegex = AppUtils.formatMessage("_{0}[A-Za-z0-9]*$", AppConstants.TEST_CASE_PART);
		return replaceRegex(testScenarioName, strRegex);
	}

	public static boolean isRegex(String strKey, String strRegex) {
		Pattern aPattern = Pattern.compile(strRegex, Pattern.CASE_INSENSITIVE);
		Matcher aMatcher = aPattern == null ? null : aPattern.matcher(strKey);
		return aMatcher != null && aMatcher.find();
	}

	public static String replaceRegex(String strText, String strRegexPattern) {
		return replaceRegex(strText, strRegexPattern, null);
	}

	public static String replaceRegex(String strText, String strRegexPattern, String strReplaceText) {
		if (StringUtils.isEmpty(StringUtils.trim(strText)) || StringUtils.isEmpty(StringUtils.trim(strRegexPattern))) {
			return strText;
		}
		Pattern aPattern = Pattern.compile(strRegexPattern, Pattern.CASE_INSENSITIVE);
		Matcher aMatcher = aPattern == null ? null : aPattern.matcher(strText);
		if (aMatcher != null && aMatcher.find()) {
			if (StringUtils.isEmpty(StringUtils.trim(strReplaceText))) {
				return RegExUtils.removeAll(strText, aPattern);
			} else {
				return RegExUtils.replaceAll(strText, aPattern, strReplaceText);
			}
		}
		return strText;
	}

	/**
	 * Removes all illegal filename characters from a given String
	 * 
	 * @param strName
	 * @param singleSpaces if true, no double spaces are allowed; they get removed.
	 * @return String
	 * @see "http://en.wikipedia.org/wiki/Filename#Reserved_characters_and_words"
	 */
	public static final String removeIllegalCharacters(String strName, final boolean singleSpaces) {
		// remove illegal characters and replace with a more friendly char ;)
		String strSafeName = StringUtils.trim(strName);

		// remove illegal characters
//		safe = RegExUtils.replaceAll(safe, "[\\/|\\\\|\\*|\\:|\\||\"|\'|\\<|\\>|\\{|\\}|\\?|\\%|,]", "");
		strSafeName = RegExUtils.replaceAll(strSafeName, "[^a-zA-Z0-9.-]", AppConstants.SEPARATOR_UNDERSCORE);

		// replace . dots with _ and remove the _ if at the end
		strSafeName = RegExUtils.replaceAll(strSafeName, "\\.", AppConstants.SEPARATOR_UNDERSCORE);
		if (StringUtils.endsWithIgnoreCase(strSafeName, AppConstants.SEPARATOR_UNDERSCORE)) {
			strSafeName = StringUtils.substring(strSafeName, 0, strSafeName.length() - 1);
		}

		// replace whitespace characters with _
		strSafeName = RegExUtils.replaceAll(strSafeName, "\\s+", AppConstants.SEPARATOR_UNDERSCORE);

		// replace double or more spaces with a single one
		if (singleSpaces) {
			strSafeName = RegExUtils.replaceAll(strSafeName, "_{2,}", AppConstants.SEPARATOR_UNDERSCORE);
		}
		strSafeName = StringUtils.endsWith(strSafeName, AppConstants.SEPARATOR_UNDERSCORE)
				? StringUtils.substring(strSafeName, 0,
						strSafeName.length() - AppConstants.SEPARATOR_UNDERSCORE.length())
				: strSafeName;
		return strSafeName;
	}

	public static String getHostIpAddress() {
		String strIPAddress = "127.0.0.1";
		try {
			InetAddress ipAddress = InetAddress.getLocalHost();
			if (ipAddress != null) {
				strIPAddress = ipAddress.getHostAddress();
			}
		} catch (Exception e) {
		}

		return strIPAddress;
	}

	public static String getHostName() {
		String strHostName = "Invalid-Host";
		try {
			InetAddress ipAddress = InetAddress.getLocalHost();
			if (ipAddress != null) {
				strHostName = ipAddress.getHostName();
			}
		} catch (Exception e) {
			strHostName = "UnKnown-Host";
		}

		return strHostName;
	}

	public static String getSystemUserName() {
		String strUser = "UnKnown-User";
		try {
			String osName = System.getProperty("os.name").toLowerCase();
			String strClassName = null;
			String methodName = "getUsername";

			if (OS.isFamilyWindows()) {
				strClassName = "com.sun.security.auth.module.NTSystem";
				methodName = "getName";
			}
			if (StringUtils.containsIgnoreCase(osName, "linux")) {
				strClassName = "com.sun.security.auth.module.UnixSystem";
			}
			if (StringUtils.containsIgnoreCase(osName, "solaris") || StringUtils.containsIgnoreCase(osName, "sunos")) {
				strClassName = "com.sun.security.auth.module.SolarisSystem";
			}
			if (strClassName != null) {
				Class<?> aClass = Class.forName(strClassName);
				Method method = aClass.getDeclaredMethod(methodName);
				Object theClass = aClass.getDeclaredConstructor().newInstance();
				Object theUser = method.invoke(theClass);
				strUser = theUser != null ? theUser.toString() : strUser;
			}
			return strUser;
		} catch (Exception e) {
			return strUser;
		}
	}

	public static String getDownloadFolder(BrowsersConfigBean aBrowsersConfigBean) {
		String strDWPATH = PropertyHandler.getExternalString(AppConstants.DOWNLOAD_FILE_LOCATION_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		File aDwFolder = new File(strDWPATH);
		if (!aDwFolder.exists()) {
			aDwFolder.mkdirs();
		}
		AppEnvConfigBean aPPRunEnv = MasterConfig.getInstance().getAppEnvConfigBean();
		AppRunMode aAppRunMode = aPPRunEnv.getAppRunMode();
		Path dwDirPath = aDwFolder.toPath().toAbsolutePath();
		String strDownloadDir = dwDirPath.normalize().toString();
		String strReportDate = AppUtils.getDateAsString(AppConfig.getInstance().getExecutionDate(),
				AppConstants.EXEC_REPORTS_FOLDER_DT_FORMAT);
		String strBrowserFolderName = AppUtils.removeIllegalCharacters(
				aBrowsersConfigBean == null || StringUtils.isEmpty(aBrowsersConfigBean.getBrowserDisplayName())
						? aAppRunMode.getAppRunMode()
						: aBrowsersConfigBean.getBrowserDisplayName(),
				true);
		String strAPPName = MasterConfig.getInstance().getAppName();
		File aExecReportFolder = Paths.get(strDownloadDir, strReportDate, strAPPName, strBrowserFolderName)
				.toAbsolutePath().toFile();
		if (!aExecReportFolder.exists()) {
			aExecReportFolder.mkdirs();
		}
		Path dwBrowserPath = aExecReportFolder.toPath().toAbsolutePath();
		strDownloadDir = dwBrowserPath.normalize().toString();
		return strDownloadDir;
	}

	public static File getSeleniumServerFile() throws IOException {
		String strSeleniumGridLibPath = PropertyHandler.getExternalString(AppConstants.SELINIUM_GRID_FILE_NAME_KEY,
				AppConstants.APP_PROPERTIES_NAME);
		File aSeleniumServerFile = getFileFromPath(strSeleniumGridLibPath);
		if (aSeleniumServerFile == null || !aSeleniumServerFile.exists()) {
			throw new IOException(formatMessage(ErrorMsgConstants.FILENTFOUND, strSeleniumGridLibPath));
		}
		return aSeleniumServerFile.toPath().toAbsolutePath().toFile();
	}

	public static String getDataFromFile(Path aPath, Charset aCharset) {
		StringBuilder contentBuilder = new StringBuilder();
		try (Stream<String> aStream = Files.lines(aPath, aCharset)) {
			aStream.forEach(aContent -> contentBuilder.append(aContent).append(System.lineSeparator()));
			return contentBuilder.toString();
		} catch (Exception ex) {
			return null;
		}
	}

	public static Gson getDefaultGson() {
		Gson aGson = gsonBuilder().create();
		return aGson;
	}

	private static GsonBuilder gsonBuilder() {
		GsonBuilder aGSonBuilder = new GsonBuilder();
		Type aDeFaultMapToken = new TypeToken<LinkedHashMap<String, Object>>() {
		}.getType();
		DateFormat dateFormat = new SimpleDateFormat(AppConstants.DEFAULT_APP_DATE_TIME_CONV_FORMAT);
		aGSonBuilder.registerTypeAdapter(java.util.Date.class, new JsonSerializer<java.util.Date>() {
			@Override
			public JsonElement serialize(java.util.Date date, Type typeOfSrc, JsonSerializationContext context) {
				// convert date to long
				return new JsonPrimitive(dateFormat.format(date));
			}
		});
		aGSonBuilder.registerTypeAdapter(java.util.Date.class, new JsonDeserializer<java.util.Date>() {
			@Override
			public java.util.Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException {
				try {
					return dateFormat.parse(json.getAsString());
				} catch (Exception e) {
					return null;
				}
			}
		});

		aGSonBuilder.registerTypeAdapter(java.io.File.class, new JsonSerializer<java.io.File>() {
			@Override
			public JsonElement serialize(java.io.File aFile, Type typeOfSrc, JsonSerializationContext context) {
				byte[] aFileData = null;
				if (aFile == null || !aFile.exists()) {
					aFileData = new byte[0];
				} else {
					try {
						aFileData = FileUtility.getFileData(aFile);
					} catch (Exception e) {
						aFileData = new byte[0];
					}
				}
				byte[] aJsonFileData = Base64.encodeBase64(aFileData);
				return new JsonPrimitive(new String(aJsonFileData));
			}
		});

		aGSonBuilder.registerTypeAdapter(byte[].class, new JsonDeserializer<byte[]>() {
			@Override
			public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
				String strData = json.getAsString();
				return Base64.decodeBase64(strData);
			}
		});

		aGSonBuilder.registerTypeAdapter(aDeFaultMapToken, new JsonDeserializer<LinkedHashMap<String, Object>>() {

			@Override
			@SuppressWarnings("unchecked")
			public LinkedHashMap<String, Object> deserialize(JsonElement json, Type typeOfT,
					JsonDeserializationContext context) throws JsonParseException {
				return (LinkedHashMap<String, Object>) read(json);
			}

			private Object read(JsonElement json) {
				if (json.isJsonArray()) {
					List<Object> lstJson = new ArrayList<Object>();
					JsonArray arr = json.getAsJsonArray();
					for (JsonElement anArr : arr) {
						lstJson.add(read(anArr));
					}
					return lstJson;
				} else if (json.isJsonObject()) {
					LinkedHashMap<String, Object> mpJson = new LinkedHashMap<String, Object>();
					JsonObject obj = json.getAsJsonObject();
					Set<Map.Entry<String, JsonElement>> entitySet = obj.entrySet();
					for (Map.Entry<String, JsonElement> entry : entitySet) {
						mpJson.put(entry.getKey(), read(entry.getValue()));
					}
					return mpJson;
				} else if (json.isJsonPrimitive()) {
					JsonPrimitive aJsonPrimitive = json.getAsJsonPrimitive();
					if (aJsonPrimitive.isBoolean()) {
						return aJsonPrimitive.getAsBoolean();
					} else if (aJsonPrimitive.isString()) {
						return aJsonPrimitive.getAsString();
					} else if (aJsonPrimitive.isNumber()) {

						Number aNumber = aJsonPrimitive.getAsNumber();
						// here you can handle double int/long values
						// and return any type you want
						// this solution will transform 3.0 float to long values
						if (Math.ceil(aNumber.doubleValue()) == aNumber.intValue()) {
							return aNumber.intValue();
						} else if (Math.ceil(aNumber.doubleValue()) == aNumber.longValue()) {
							return aNumber.longValue();
						} else {
							return aNumber.doubleValue();
						}
					}
				}
				return null;
			}
		});

		return aGSonBuilder;
	}

	public static void emailExecutionReports() {
		try {
			MasterConfig aMasterConfig = MasterConfig.getInstance();
			AppEnvConfigBean aPPRunEnv = aMasterConfig.getAppEnvConfigBean();
			MSTeamsNotifier.sendNotification(aPPRunEnv.getMSTeamsGroupURL(), aPPRunEnv.getAppName());
			AppContext aAppContext = AppContext.getInstance();
			LinkedList<File> lstFiles = aAppContext.getExecutionReports();
			if (CollectionUtils.isEmpty(lstFiles)) {
				return;
			}
			File[] aFiles = lstFiles.stream().toArray(File[]::new);
			String strToEmail = aPPRunEnv == null ? "" : aPPRunEnv.getSpocEmail();

			if (aMasterConfig.canBrowserSendEmail() && aPPRunEnv != null && !StringUtils.isEmpty(strToEmail)) {
				String strEmail = EmailUtils.getEmailConfigByKey(AppConstants.EMAIL_USER_KEY);
				String strSubjectFomat = EmailUtils.getEmailConfigByKey(AppConstants.EMAIL_MESSAGE_SUBJECT_KEY);
				String strMessageFomat = EmailUtils.getEmailConfigByKey(AppConstants.EMAIL_MESSAGE_BODY_KEY);
				StringBuilder strTableMessage = new StringBuilder();
				String strTableMessageFormat = AppUtils.getExecutionHTMLTableFormat();
				strTableMessage.append("<br>");
				strTableMessage.append(AppUtils.formatMessage(strTableMessageFormat, aPPRunEnv.getAppName()));
				strTableMessage.append("<br>");
				String strMessage = AppUtils.formatMessage(strMessageFomat, strTableMessage.toString());
				String strSubject = AppUtils.formatMessage(strSubjectFomat, aPPRunEnv.getAppName());
				EmailUtils.sendEmail(strEmail, strToEmail, "", strSubject, strMessage, aFiles);
			}
		} catch (Exception ex) {
		}
	}

	public static InetAddress getValidAddress(String strHost) {
		try {
			return InetAddress.getByName(strHost);
		} catch (Exception ex) {
			return null;
		}
	}

	public static HashMap<String, String> parseUrl(String url) throws URISyntaxException {
		List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), StandardCharsets.UTF_8);
		HashMap<String, String> mapped = (HashMap<String, String>) params.stream()
				.collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
		return mapped;
	}

	public static final String getExecutionHTMLTableFormat() {
		AppContext aAppContext = AppContext.getInstance();
		StringBuilder strHTMLMessageFormat = new StringBuilder();
		LinkedHashMap<String, LinkedHashMap<String, Long>> stepsResultMap = aAppContext.getStepResults();
		if (stepsResultMap == null || stepsResultMap.isEmpty()) {
			return strHTMLMessageFormat.toString();
		}
		strHTMLMessageFormat.append("<table ").append(AppConstants.NOFICATION_HTML_TABLE_STYLE).append(">");
		strHTMLMessageFormat.append("<tr ").append(AppConstants.NOFICATION_HTML_ROW_STYLE).append(">");
		strHTMLMessageFormat.append("<th colspan=\"8\" style=\"text-align:center\">{0} - Execution Status");
		strHTMLMessageFormat.append("</th>");
		strHTMLMessageFormat.append("</tr>");
		strHTMLMessageFormat.append("<tr ").append(AppConstants.NOFICATION_HTML_HEADER_STYLE).append(">");
		strHTMLMessageFormat.append("<th ").append(AppConstants.NOFICATION_HTML_ROW_STYLE).append(">Browser</th>");
		strHTMLMessageFormat.append("<th ").append(AppConstants.NOFICATION_HTML_ROW_STYLE)
				.append(">Total Executed</th>");
		strHTMLMessageFormat.append("<th ").append(AppConstants.NOFICATION_HTML_ROW_STYLE).append(">Pass</th>");
		strHTMLMessageFormat.append("<th ").append(AppConstants.NOFICATION_HTML_ROW_STYLE).append(">Fail</th>");
		strHTMLMessageFormat.append("<th ").append(AppConstants.NOFICATION_HTML_ROW_STYLE).append(">Warning</th>");
		strHTMLMessageFormat.append("<th ").append(AppConstants.NOFICATION_HTML_ROW_STYLE).append(">Others</th>");
		strHTMLMessageFormat.append("<th ").append(AppConstants.NOFICATION_HTML_ROW_STYLE).append(">HostName</th>");
		strHTMLMessageFormat.append("<th ").append(AppConstants.NOFICATION_HTML_ROW_STYLE).append(">ExecutedBy</th>");
		strHTMLMessageFormat.append("</tr>");

		for (Entry<String, LinkedHashMap<String, Long>> aStepResults : stepsResultMap.entrySet()) {
			String strBrowserDisplayName = aStepResults.getKey();
			LinkedHashMap<String, Long> resultsBean = aStepResults.getValue();
			long lTotalCount = resultsBean.get(AppConstants.TEST_RESULT_TOTALCOUNT);
			long lPassCount = resultsBean.get(AppConstants.TEST_RESULT_PASS);
			long lFailCount = resultsBean.get(AppConstants.TEST_RESULT_FAIL);
			long lWarningCount = resultsBean.get(AppConstants.TEST_RESULT_WARING);
			long lothersCount = resultsBean.get(AppConstants.TEST_RESULT_OTHERS);
			strHTMLMessageFormat.append("<tr ").append(AppConstants.NOFICATION_HTML_ROW_STYLE).append(">");
			strHTMLMessageFormat.append("<td ").append(AppConstants.NOFICATION_HTML_ROW_STYLE).append(">")
					.append(strBrowserDisplayName).append("</td>");
			strHTMLMessageFormat.append("<td ").append(AppConstants.NOFICATION_HTML_ROW_STYLE).append(">")
					.append(lTotalCount).append("</td>");
			strHTMLMessageFormat.append("<td ").append(AppConstants.NOFICATION_HTML_ROW_STYLE).append(">")
					.append(lPassCount).append("</td>");
			strHTMLMessageFormat.append("<td ").append(AppConstants.NOFICATION_HTML_ROW_STYLE).append(">")
					.append(lFailCount).append("</td>");
			strHTMLMessageFormat.append("<td ").append(AppConstants.NOFICATION_HTML_ROW_STYLE).append(">")
					.append(lWarningCount).append("</td>");
			strHTMLMessageFormat.append("<td ").append(AppConstants.NOFICATION_HTML_ROW_STYLE).append(">")
					.append(lothersCount).append("</td>");
			strHTMLMessageFormat.append("<td ").append(AppConstants.NOFICATION_HTML_ROW_STYLE).append(">")
					.append(AppUtils.getHostName()).append("</td>");
			strHTMLMessageFormat.append("<td ").append(AppConstants.NOFICATION_HTML_ROW_STYLE).append(">")
					.append(AppUtils.getSystemUserName()).append("</td>");
			strHTMLMessageFormat.append("</tr>");
		}
		strHTMLMessageFormat.append("</table>");
		return strHTMLMessageFormat.toString();
	}

	public static final String getExecutionTableFormat() {
		StringBuilder strMessage = new StringBuilder();
		AppContext appConText = AppContext.getInstance();

		LinkedHashMap<String, LinkedHashMap<String, Long>> stepsResultMap = appConText.getStepResults();
		if (stepsResultMap == null || stepsResultMap.isEmpty()) {
			return strMessage.toString();
		}
		List<String> lstHeader = new LinkedList<>();
		lstHeader.add("Browser");
		lstHeader.add("Total Executed");
		lstHeader.add("Pass");
		lstHeader.add("Fail");
		lstHeader.add("Warning");
		lstHeader.add("Others");
		lstHeader.add("HostName");
		lstHeader.add("ExecutedBy");
		List<List<String>> lstRow = new LinkedList<>();
		for (Entry<String, LinkedHashMap<String, Long>> aStepResults : stepsResultMap.entrySet()) {
			String strBrowserDisplayName = aStepResults.getKey();
			LinkedHashMap<String, Long> resultsBean = aStepResults.getValue();
			long lTotalCount = resultsBean.get(AppConstants.TEST_RESULT_TOTALCOUNT);
			long lPassCount = resultsBean.get(AppConstants.TEST_RESULT_PASS);
			long lFailCount = resultsBean.get(AppConstants.TEST_RESULT_FAIL);
			long lWarningCount = resultsBean.get(AppConstants.TEST_RESULT_WARING);
			long lothersCount = resultsBean.get(AppConstants.TEST_RESULT_OTHERS);
			List<String> lstRowData = new ArrayList<>();
			lstRowData.add(strBrowserDisplayName);
			lstRowData.add(String.valueOf(lTotalCount));
			lstRowData.add(String.valueOf(lPassCount));
			lstRowData.add(String.valueOf(lFailCount));
			lstRowData.add(String.valueOf(lWarningCount));
			lstRowData.add(String.valueOf(lothersCount));
			lstRowData.add(AppUtils.getHostName());
			lstRowData.add(AppUtils.getSystemUserName());
			lstRow.add(lstRowData);
		}
		TableGenerator aTableGenerator = new TableGenerator();
		strMessage.append(aTableGenerator.generateTable(lstHeader, lstRow));
		strMessage.append(System.lineSeparator());
		return strMessage.toString();
	}

	public static <T> T getFirstElement(final Iterable<T> elements) {
		return elements.iterator().next();
	}

	public static <T> T getLastElement(final Iterable<T> elements) {
		T lastElement = null;

		for (T element : elements) {
			lastElement = element;
		}

		return lastElement;
	}

	public static Dimension getImageDimension(File imgFile) throws IOException {
		BufferedImage img = ImageIO.read(imgFile);
		return new Dimension(img.getWidth(), img.getHeight());
	}

	public static String getJsonData(String strTestData) {
		String strJsonTestData = null;
		Gson aGson = AppUtils.getDefaultGson();
		Type dbJsonObJectType = null;
		try {
			dbJsonObJectType = new TypeToken<LinkedHashMap<String, Object>>() {
			}.getType();
			LinkedHashMap<String, Object> mpJsonTestData = aGson.fromJson(strTestData, dbJsonObJectType);
			strJsonTestData = aGson.toJson(mpJsonTestData, dbJsonObJectType);
		} catch (Exception ex) {
		}
		if (StringUtils.isEmpty(strJsonTestData)) {
			try {
				JSONObject aDBJsonObject = new JSONObject(strTestData);
				Map<String, Object> mpJsonTestData = aDBJsonObject.toMap();
				dbJsonObJectType = new TypeToken<Map<String, Object>>() {
				}.getType();
				strJsonTestData = aGson.toJson(mpJsonTestData, dbJsonObJectType);
			} catch (Exception ex) {
			}
		}
		return strJsonTestData;
	}

	public static String getBrowserExecutionFileName(String strBrowserDisplayName) {
		AppEnvConfigBean aPPRunEnv = MasterConfig.getInstance().getAppEnvConfigBean();
		AppRunMode aAppRunMode = aPPRunEnv.getAppRunMode();
		switch (aAppRunMode) {
		case SELENIUM_GRID:
		case SELENIUM_SERVER:
		case SELENIUM_NODE:
		case APP_PRORITY_GRID:
			return AppUtils.removeIllegalCharacters(aAppRunMode.getAppRunMode(), true);
		default:
			return AppUtils.removeIllegalCharacters(strBrowserDisplayName, true);
		}
	}

	public static boolean canCreateSnapShotScenarioDoc() {
		String strSysStatus = System.getProperty(AppConstants.GENERATE_DOC_REPORT_SCENARIO_KEY);
		String strStatus = StringUtils.isEmpty(StringUtils.trim(strSysStatus)) ? PropertyHandler
				.getExternalString(AppConstants.GENERATE_DOC_REPORT_SCENARIO_KEY, AppConstants.APP_PROPERTIES_NAME)
				: StringUtils.trim(strSysStatus);
		return BooleanUtils.toBoolean(strStatus);
	}

	public static String getScenarioReportFileName(String testScenarioName, int iLength) {
		final int iMaxLength = iLength <= 0 ? 20 : iLength;
		String strFormatedScn = StringUtils.length(testScenarioName) > iMaxLength
				? StringUtils.substring(AppUtils.removeIllegalCharacters(testScenarioName, true), 0, iMaxLength)
				: AppUtils.removeIllegalCharacters(testScenarioName, true);
		return StringUtils.endsWith(strFormatedScn, AppConstants.SEPARATOR_UNDERSCORE)
				? StringUtils.substring(strFormatedScn, 0,
						strFormatedScn.length() - AppConstants.SEPARATOR_UNDERSCORE.length())
				: strFormatedScn;
	}

	/**
	 *
	 * @param startDate
	 * @param endDate
	 * @return hours between two dates rounded down
	 */
	public static long minutesBetween(Date startDate, Date endDate) {
		if (startDate == null || endDate == null) {
			return -1;
		}
		Duration total = Duration.ofMinutes(ChronoUnit.MINUTES.between(startDate.toInstant(), endDate.toInstant()));
		return total.toMinutes();
	}

	public static String getFileDate() {
		return AppUtils.getDateAsString(new Date(), AppConstants.DATE_FORMAT_DDMM_HHSS);
	}

	public static File getEnvFilePath(String strFilePath) {
		String strEnvKey = StringUtils.substringBetween(strFilePath, AppConstants.SEPARATOR_PERCENTAGE,
				AppConstants.SEPARATOR_PERCENTAGE);
		if (StringUtils.isEmpty(strEnvKey)) {
			return getFileFromPath(strFilePath);
		}
		String strEnvPath = System.getenv(strEnvKey);
		return Paths.get(strEnvPath, StringUtils.substringAfterLast(strFilePath, AppConstants.SEPARATOR_PERCENTAGE))
				.toFile();
	}

	public static String getEnvPropertyValue(String strPropKey, String strPropertyFileName) {
		String strSysEnvPropertyValue = System.getProperty(strPropKey);
		String strEnvPropertyValue = StringUtils.isEmpty(StringUtils.trim(strSysEnvPropertyValue))
				? PropertyHandler.getExternalString(strPropKey, strPropertyFileName)
				: strSysEnvPropertyValue;
		strEnvPropertyValue = StringUtils.trim(strEnvPropertyValue);
		return strEnvPropertyValue;
	}

	public static Map<String, String> postCallGetCookies(JSONObject jsonPayload, String Uri) {
		return io.restassured.RestAssured.given().baseUri(Uri).accept(io.restassured.http.ContentType.JSON)
				.body(jsonPayload.toString()).when().post().getCookies();

	}

	public static void writeJSONObjectToFile(JSONObject jsonObject, File aFile) throws IOException {
		// Create a new FileWriter object
		try (FileWriter fileWriter = new FileWriter(aFile);) {
			fileWriter.write(jsonObject.toString());
			fileWriter.flush();
		}
	}

	public static JSONObject parseJsonFile(File aFile) {
		String content = null;
		try {
			content = new String(Files.readAllBytes(aFile.toPath()));
		} catch (Exception e) {
		}
		assert content != null;
		return new JSONObject(content);
	}

	public static String getHostNameFromURL(String strURL) {
		try {
			URI uri = new URI(strURL);
			String strHost = uri.getHost();
			if (StringUtils.isEmpty(StringUtils.trim(strHost))) {
				return strURL;
			}
			return strHost;
		} catch (Exception ex) {
			return strURL;
		}
	}

	public static String removeInvisbleCharacters(String strText) {
		strText = RegExUtils.replaceAll(strText, AppConstants.REGEX_LINE_BREAK_CHARACTER,
				AppConstants.REGEX_SPACE_CHARACTER);
		strText = RegExUtils.replaceAll(strText, AppConstants.REGEX_NBSP_CHARACTER, AppConstants.REGEX_SPACE_CHARACTER);
		strText = RegExUtils.replaceAll(strText, AppConstants.REGEX_QUESTIONMARK_CHARACTER, AppConstants.REGEX_DOUBLE_QUOTE_CHARACTER);
		return StringUtils.trim(strText);
	}

	public static void main(String[] args) {
		try {
			String strUserName = "SSitTestUser3";
			String strPassword = "XhL-6u_81";
			JsonObject aJsonObject = new JsonObject();
			aJsonObject.addProperty("username", strUserName);
			aJsonObject.addProperty("password", strPassword);
			System.out.println(aJsonObject.toString());
			String strBinaryPath = "%LocalAppData%\\Programs\\Esitrack Surveyor\\Esitrack Surveyor.exe";
			String strEnvPath = System.getenv(StringUtils.substringBetween(strBinaryPath, "%", "%"));
			System.out.println(Paths.get(strEnvPath, StringUtils.substringAfterLast(strBinaryPath, "%")));
			System.out.println(minutesBetween(updateDays(new Date(), -1), new Date()));
			Faker aFaker = new Faker(Locale.UK);
			System.out.println(aFaker.phoneNumber().cellPhone());
			System.out.println(aFaker.phoneNumber().phoneNumber());
			System.out.println(aFaker.internet().emailAddress());

			System.out.println(aFaker.address().fullAddress());
			System.out.println(aFaker.address().buildingNumber());
			System.out.println(aFaker.address().city());
			System.out.println(aFaker.address().zipCode());
			System.out.println(aFaker.address().streetAddress());
			System.out.println(AppUtils.removeIllegalCharacters(
					"TC01_Verify= >. ? the reasons picklist is\r\n" + " displayed when Non Disclosure is selected",
					true));
			String strTesta = "Server Name,ACTIVEDIRECTORY.QBE-BRZ.LOCAL_\r\n"
					+ "Current OS (based upon SNOW/GIS scan),NetApp Data ONTAP 8,Windows 2012 R2 Datacenter-SNOW Operating Status,Operating,Decommissioned";
			strTesta = StringUtils.trim(strTesta);
			System.out.println(strTesta);
			String strDataRow = StringUtils.substringBefore(strTesta, AppConstants.SEPARATOR_UNDERSCORE);
			System.out.println(strDataRow);
			System.out.println(StringUtils.substringAfter(strTesta, AppConstants.SEPARATOR_UNDERSCORE));
			String strTestCaseId = "TestCase_Part121";
			System.out.println(StringUtils.contains(strTestCaseId, AppConstants.TEST_CASE_PART));
			System.out.println(getValidPartScenarioName(strTestCaseId));
			String sendEmail = null;
			System.out.println(BooleanUtils.toBoolean(sendEmail));
			// UW_OtherInformation_ExistingLifeOrCICoverExceeds_No
			String strOrDesktopFile = "Properties/BackOffice_OR_Desktop.properties";
			System.out.println(PropertyHandler.getExternalString("UW_OtherInformation_ExistingLifeOrCICoverExceeds_No",
					strOrDesktopFile));
			String strTestData = "{\"tableName\": \"LogAudit\", \"QUERY\":\"Select COUNT(*) from com.tp\",\"DATA\":[100,\"Tue May 04 13:56:38 IST 2021\"]}";
			Gson aGson = AppUtils.getDefaultGson();
			Type capaBilityType = new TypeToken<LinkedHashMap<String, Object>>() {
			}.getType();
			LinkedHashMap<String, Object> mpTestData = aGson.fromJson(strTestData, capaBilityType);
			@SuppressWarnings("unchecked")
			List<Object> lstData = (List<Object>) mpTestData.get(DataBaseConstants.DB_CONFIG_DATA_KEY);
			System.out.println(lstData);
			System.out.println(ExcelUtils.cellNumToAlphabetic(2132));
			System.out.println(AppUtils.formatMessage(ErrorMsgConstants.ERR_SERVER_CMD_ARGS, "Test"));
//			System.out.println(InetAddress.getByName("NUKAX6615271.emea.zurich.corp"));
//			System.out.println(InetAddress.getByName(AppUtils.getHostIpAddress()).getHostName());
			System.out.println(InetAddress.getLocalHost().getHostName());
			System.out.println(StandardCharsets.UTF_8.name());
			System.out.println(new Date());
			System.out.println(GridClientHelper.isHostReachable("NUKAX6615271.emea.zurich.corp", 8888));
			System.out.println(new Date());
			System.out.println(Math.round(11 * 0.25));
			String testScenarioName = "Policy 1";
			String strRegex = "Policy \\d+$";
			Pattern aPattern = Pattern.compile(strRegex, Pattern.CASE_INSENSITIVE);
			Matcher aMatcher = aPattern == null ? null : aPattern.matcher(testScenarioName);
			System.out.println(aMatcher.find());
			System.out.println(StringUtils.containsAny("Unable to offer terms", SummaryReportConstants.NA_POLICIES));
//			String[] strValues = StringUtils.split("1,2", ",");
			Object[] aOrProprgs = StringUtils.split("1,2", ",");
			System.out.println(String.format("//*'%s'//Dataitem//'%s'", aOrProprgs));
			int xPos = -10;
			System.out.println(900 + xPos);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
