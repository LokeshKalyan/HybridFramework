/****************************************************************************
 * File Name 		: PropertyHandler.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abc.project.constants.ErrorMsgConstants;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 11:31:38 am
 */
public class PropertyHandler {

	private static Map<String, ResourceBundle> resouceBundlesMap = new HashMap<String, ResourceBundle>();

	private final static Logger LOGGER = LogManager.getLogger(PropertyHandler.class.getName());

	private PropertyHandler() {
		super();
	}

	public static void mergeExternalResourceBundle(String strSrcResource, String strTargetResource) throws IOException {
		resouceBundlesMap.clear();
		File aTrgtFile = AppUtils.getFileFromPath(strTargetResource);
		try {
			ResourceBundle aSrcResourceBundle = getExternalResourceBundle(strSrcResource);
			ResourceBundle aTagetResourceBundle = getExternalResourceBundle(strTargetResource);
			List<ResourceBundle> lstBundles = new ArrayList<>();
			lstBundles.add(aSrcResourceBundle);
			lstBundles.add(aTagetResourceBundle);
			Map<String, String> combinedResources = new HashMap<>();
			lstBundles.forEach(bundle -> {
				Enumeration<String> keysEnumeration = bundle.getKeys();
				ArrayList<String> keysList = Collections.list(keysEnumeration);
				keysList.forEach(key -> combinedResources.put(key, bundle.getString(key)));
			});
			ResourceBundle aMergedResourceBundle = new ResourceBundle() {
				@Override
				protected Object handleGetObject(String key) {
					return combinedResources.get(key);
				}

				@Override
				public Enumeration<String> getKeys() {
					return Collections.enumeration(combinedResources.keySet());
				}
			};
			resouceBundlesMap.put(strTargetResource, aMergedResourceBundle);
		} catch (Exception e) {
			throw new IOException(MessageFormat.format(ErrorMsgConstants.FILENTFOUND, aTrgtFile.getPath()), e);
		}
	}

	public static ResourceBundle getExternalResourceBundle(String externalDataGroupName)
			throws IOException, URISyntaxException {
		ResourceBundle bundle = resouceBundlesMap.get(externalDataGroupName);
		if (resouceBundlesMap.get(externalDataGroupName) == null) {
			File aPropFile = AppUtils.getFileFromPath(externalDataGroupName);
			try (InputStream aConfPath = new FileInputStream(aPropFile)) {
				bundle = new PropertyResourceBundle(aConfPath);
				resouceBundlesMap.put(externalDataGroupName, bundle);
			}
		}
		return bundle;
	}

	/***
	 * Fetches Property values from the property files
	 * 
	 * @param key
	 * @param externalDataGroupName
	 * @return
	 */
	public final static String getExternalString(String key, String externalDataGroupName) {
		try {
			return getExternalResourceBundle(externalDataGroupName).getString(key);
		} catch (MissingResourceException mre) {
			LOGGER.error("Missing Property Key :[" + key + "] for the data Group :" + externalDataGroupName);
			return null;
		} catch (IOException | URISyntaxException ie) {
			LOGGER.error("Error while getting Translation for the Key :[" + key + "] for the data Group :"
					+ externalDataGroupName);
			return null;
		}
	}
}
