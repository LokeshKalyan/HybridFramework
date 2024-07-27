/****************************************************************************
 * File Name 		: BrowserConfigAction.java
 * Package			: com.dxc.zurich.grid
 * Author			: pmusunuru2
 * Creation Date	: Jun 10, 2021
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.grid;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.config.AppConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.enums.StartFinish;
import com.abc.project.utils.AppUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author pmusunuru2
 * @since Jun 10, 2021 4:41:09 pm
 */
public class BrowserConfigAction implements BaseAction {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(BrowserConfigAction.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	private static BrowserConfigAction instance;

	public static BrowserConfigAction getInstance() {
		if (null == instance) {
			synchronized (BrowserConfigAction.class) {
				if (null == instance) {
					instance = new BrowserConfigAction();
				}
			}
		}
		return instance;
	}
	
	@Override
	public String execute(HashMap<String, String> aMessageMap) {
		String strLogMessage = AppUtils.formatMessage("Executing browserConfig with data {0}", aMessageMap);
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
		String strBrowserNames = aMessageMap.get(BROWSER_CONFIG_BEAN_KEY);
		Gson aGson = AppUtils.getDefaultGson();
		try {
			Type aBrowserNames = new TypeToken<LinkedHashSet<String>>() {
			}.getType();
			LinkedHashSet<String> stBrowserDisplayName = aGson.fromJson(strBrowserNames, aBrowserNames);
			LinkedList<BrowsersConfigBean> lstBorwserConfig = AppConfig.getInstance().getBrowserConfig(stBrowserDisplayName);
			if(CollectionUtils.isEmpty(lstBorwserConfig)) {
				return aGson.toJson(new LinkedList<>());
			}
			return aGson.toJson(lstBorwserConfig);
		} catch (Exception ex) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return aGson.toJson(new LinkedList<>());
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
		
	}

}
