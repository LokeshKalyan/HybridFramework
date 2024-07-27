/****************************************************************************
 * File Name 		: MongoDBValidation.java
 * Package			: com.dxc.zurich.database
 * Author			: pmusunuru2
 * Creation Date	: May 03, 2021
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.database;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.abc.project.constants.AppConstants;
import com.abc.project.constants.DataBaseConstants;
import com.abc.project.enums.StartFinish;
import com.abc.project.utils.AppUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * @author pmusunuru2
 * @since May 03, 2021 3:59:45 pm
 */
public class MongoDBValidation extends AbstractDataBaseValidation {

	private static final Logger LOGGER = LogManager.getLogger(MongoDBValidation.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	/**
	 * @param strTestData
	 * @param strORProperty
	 * @param strReportKeyWord
	 */
	public MongoDBValidation(String strTestData, String strORProperty, String strReportKeyWord) {
		super(strTestData, strORProperty, strReportKeyWord);
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	@Override
	public Logger getErrorLogger() {
		return ERROR_LOGGER;
	}

	@Override
	public MongoClient getConnection(Properties aDBProperties) {
		String strDBURL = aDBProperties.getProperty(DataBaseConstants.DATABASE_URL_KEY);
		MongoClientURI aMongoClientURI = new MongoClientURI(strDBURL);
		return new MongoClient(aMongoClientURI);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<LinkedHashMap<String, Object>> fetchDBData() {
		List<LinkedHashMap<String, Object>> lstRecords = new ArrayList<>();
		String strTableNameKey = DataBaseConstants.DATABASE_TABLENAME_KEY;
		LinkedHashMap<String, Object> mpTestData = getDBTestData();
		if (!mpTestData.containsKey(strTableNameKey) || mpTestData.get(strTableNameKey) == null) {
			return lstRecords;
		}
		Gson aGson = AppUtils.getDefaultGson();
		Type capaBilityType = new TypeToken<LinkedHashMap<String, Object>>() {
		}.getType();
		String strTableName = (String) mpTestData.get(strTableNameKey);
		MongoCursor<Document> aMangoDBCursor = null;
		Properties aDBProperties = getDBProperties();
		String strDBName = aDBProperties.getProperty(DataBaseConstants.DATABASE_DATABASENAME_KEY);
		String strLogMessage = String.format("Fetching data from table %s", strTableName);
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
		try (MongoClient aMongoClient = getConnection(aDBProperties);) {
			MongoDatabase aMangoDB = aMongoClient.getDatabase(strDBName);
			MongoCollection<Document> aMangoDBTable = aMangoDB.getCollection(strTableName);
			Object objQuery = getQueryToExecute();
			if (objQuery == null || !(objQuery instanceof HashMap)) {
				aMangoDBCursor = aMangoDBTable.find().iterator();
			} else {
				Map<String, Object> mpKeyValues = (HashMap<String, Object>) objQuery;
				Map<String, Object> mpParams = new HashMap<>();
				mpKeyValues.entrySet().stream().forEach(aEntry -> {
					mpParams.put(aEntry.getKey(), new Document(DataBaseConstants.MANGO_DB_FIND_KEY, aEntry.getValue()));
				});
				Document aFindQuery = new Document(mpParams);
				aMangoDBCursor = aMangoDBTable.find(aFindQuery).iterator();
			}
			while (aMangoDBCursor.hasNext()) {
				Document aDocument = aMangoDBCursor.next();
				LinkedHashMap<String, Object> mpRecord = aGson.fromJson(aDocument.toJson(), capaBilityType);
				lstRecords.add(mpRecord);
			}
			return lstRecords;
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return lstRecords;
		} finally {
			try {
				if (aMangoDBCursor != null) {
					aMangoDBCursor.close();
				}
			} catch (Exception e) {
			}
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}
}
