/****************************************************************************
 * File Name 		: AppPriorityConfigBean.java
 * Package			: com.dxc.zurich.config
 * Author			: pmusunuru2
 * Creation Date	: Dec 05, 2022
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.config;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.abc.project.beans.AppEnvConfigBean;
import com.abc.project.beans.AppPriorityConfigBean;
import com.abc.project.constants.AppConstants;
import com.abc.project.enums.AppRunMode;

/**
 * @author pmusunuru2
 * @since Dec 05, 2022 2:40:38 pm
 */
public class AppPriorityConfig {

	private static AppPriorityConfig instance;

	private MasterConfig aMasterConfig;

	private LinkedHashSet<String> stEnvName;

	private AppPriorityConfig() {
		aMasterConfig = MasterConfig.getInstance();
		stEnvName = new LinkedHashSet<>();
	}

	public static AppPriorityConfig getInstance() {
		if (null == instance) {
			synchronized (AppPriorityConfig.class) {

				if (null == instance) {
					instance = new AppPriorityConfig();
				}
			}
		}
		return instance;
	}

	private boolean isPriorityMode(String strAppRunMode) {
		return StringUtils.equalsIgnoreCase(strAppRunMode, AppRunMode.APP_PRORITY.getAppRunMode())
				|| StringUtils.equalsIgnoreCase(strAppRunMode, AppRunMode.APP_PRORITY_GRID.getAppRunMode());
	}

	public AppPriorityConfigBean getFileredAppPriorityConfigByScenario(String strScenarioName) {
		AppEnvConfigBean aPPRunEnv = aMasterConfig.getAppEnvConfigBean();
		AppRunMode aAppRunMode = aPPRunEnv.getAppRunMode();
		if (isPriorityMode(aAppRunMode.getAppRunMode())) {
			LinkedList<AppPriorityConfigBean> lstAppPriorityConfig = aMasterConfig.getAppPriorityConfigBean();
			return lstAppPriorityConfig
					.stream().filter(aAppPriorityConfig -> StringUtils
							.equalsIgnoreCase(aAppPriorityConfig.getScenarioName(), strScenarioName)
							&& StringUtils
							.equalsIgnoreCase(aAppPriorityConfig.getAppName(), aPPRunEnv.getAppName()))
					.findFirst().orElse(null);
		} else {
			return null;
		}
	}

	private String getAppRunMode(String[] args) {
		String strAppRunMode = (args == null || args.length <= 0) ? AppRunMode.NORMAL.getAppRunMode() : args[0];
		return strAppRunMode;
	}

	public LinkedHashSet<String> getAppPriority(String[] args) {
		String strAppRunMode = getAppRunMode(args);
		boolean isPriorityMode = isPriorityMode(strAppRunMode);
		if (isPriorityMode) {
			List<AppPriorityConfigBean> lstAppPriorityConfig = aMasterConfig.getAppPriorityConfigBean();
			lstAppPriorityConfig.stream().forEach(aAppPriorityConfig -> {
				stEnvName.add(aAppPriorityConfig.getAppName());
			});
		} else {
			stEnvName.add(aMasterConfig.getAppName());
		}
		return stEnvName;
	}

	public void setAppName(String[] args, String strEnvName) {
		String strAppRunMode = getAppRunMode(args);
		boolean isPriorityMode = isPriorityMode(strAppRunMode);
		if (!isPriorityMode) {// APP Name Setting not required for Other modes
			return;
		}
		System.setProperty(AppConstants.APP_NAME, strEnvName);
	}
}
