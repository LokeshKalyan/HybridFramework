/****************************************************************************
 * File Name 		: ServerRunner.java
 * Package			: com.dxc.zurich.runners
 * Author			: pmusunuru2
 * Creation Date	: May 31, 2021
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.runners;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abc.project.beans.AppEnvConfigBean;
import com.abc.project.config.MasterConfig;
import com.abc.project.constants.AppConstants;
import com.abc.project.constants.ErrorMsgConstants;
import com.abc.project.enums.StartFinish;
import com.abc.project.grid.ServerHelper;
import com.abc.project.utils.AppUtils;

/**
 * @author pmusunuru2
 * @since May 31, 2021 1:37:23 pm
 */
public class ServerRunner {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(ServerRunner.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	private static ServerRunner instance;

	private AppEnvConfigBean aPPRunEnv;

	private Executor aDataServerThreads;

	private ServerSocket aSrvSocket;

	private static final int NO_OF_DATA_THREADS = 1;

	private ServerRunner() {
		aPPRunEnv = MasterConfig.getInstance().getAppEnvConfigBean();
		aDataServerThreads = Executors.newFixedThreadPool(NO_OF_DATA_THREADS, (Runnable aDataRunnable) -> {
			Thread aDataThread = new Thread(aDataRunnable);
			aDataThread.setName(AppConstants.DATA_SESSION_THERAD_NAME);
			aDataThread.setDaemon(true);
			return aDataThread;
		});
	}

	public static ServerRunner getInstance() {
		if (null == instance) {
			synchronized (ServerRunner.class) {
				if (null == instance) {
					instance = new ServerRunner();
				}
			}
		}
		return instance;
	}
	

	private void startDataServerSession() {
		int iDataServerPort = aPPRunEnv.getHostPort();
		try {
			ServerHelper aServerHelper = ServerHelper.getInstance();
			aSrvSocket = new ServerSocket(iDataServerPort);
			Socket aClientSocket = null;
			while (true) { // The server will repeat listening on the port.
				String strLogMessage = "Client Connection";
				try {
					LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
					aClientSocket = aSrvSocket.accept();
					aServerHelper.asyncServeRequest(aClientSocket, aDataServerThreads);
				} catch (Exception ex) {
					LOGGER.error(AppUtils.formatMessage(ErrorMsgConstants.ERR_SERVER_CREATE_IO, iDataServerPort));
					ERROR_LOGGER.error(AppUtils.formatMessage(ErrorMsgConstants.ERR_SERVER_CREATE_IO, iDataServerPort),
							ex);
				} finally {
					LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
				}
			}
		} catch (Exception ex) {
			LOGGER.error(AppUtils.formatMessage(ErrorMsgConstants.ERR_SERVER_CREATE_IO, iDataServerPort));
			ERROR_LOGGER.error(AppUtils.formatMessage(ErrorMsgConstants.ERR_SERVER_CREATE_IO, iDataServerPort), ex);
		}
	}

	public void startServer() throws Exception {

		Thread aDataServerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				startDataServerSession();
			}
		});
		aDataServerThread.setName(AppConstants.DATA_RUNNER_THERAD_POOL_FORMAT);
		aDataServerThread.start();
	}
}
