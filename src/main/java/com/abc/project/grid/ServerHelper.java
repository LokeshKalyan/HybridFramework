/****************************************************************************
 * File Name 		: ServerHelper.java
 * Package			: com.dxc.zurich.utils
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
package com.abc.project.grid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.os.CommandLine;

import com.abc.project.constants.AppConstants;
import com.abc.project.enums.StartFinish;
import com.abc.project.utils.AppUtils;

/**
 * The class which does the task of processing client request.
 * 
 * @author pmusunuru2
 * @since May 31, 2021 2:05:27 pm
 */
public class ServerHelper {

	private static final Logger LOGGER = LogManager.getLogger(ServerHelper.class);

	private static final Logger ERROR_LOGGER = LogManager.getLogger(AppConstants.ERROR_LOGNAME);

	private static ServerHelper instance;
	
	private LinkedList<CommandLine> lstCommandProcess;
	
	private ServerHelper() 
	{
		lstCommandProcess = new LinkedList<>();
	}
	
	public static ServerHelper getInstance() {
		if (null == instance) {
			synchronized (ServerHelper.class) {
				if (null == instance) {
					instance = new ServerHelper();
				}
			}
		}
		return instance;
	}
	
	/**
	 * This is asynchronous way to handle server processing
	 * 
	 * @param aClientSocket      : The socket on which the client got connected.
	 * @param aDataServerThreads : The passed in executor will be used.
	 * @return Future<Boolean>
	 */
	public Future<Boolean> asyncServeRequest(Socket aClientSocket, Executor aDataServerThreads) {

		return CompletableFuture.supplyAsync(() -> {
			InetAddress aInetAddress = aClientSocket.getInetAddress();
			String strLogMessage = AppUtils.formatMessage("Processing Request from {0}:{1}",
					(aInetAddress == null ? "Un-Known" : aInetAddress.getHostName()), aClientSocket.getPort());
			try {
				LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
				serveRequest(aClientSocket);
				return Boolean.TRUE;
			} catch (Exception ex) {
				String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
				LOGGER.error(strErrorMsg);
				ERROR_LOGGER.error(strErrorMsg, ex);
				return Boolean.FALSE;
			} finally {
				LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
			}
		}, aDataServerThreads);
	}

	/**
	 * This method takes the message from the client. Then, it serves the client
	 * action and respond back.
	 * 
	 * @param aClientSocket : The socket on which the client got connected.
	 * @return true: if completed else false.
	 * @throws IOException : Some error happens on reading the socket.
	 */
	private void serveRequest(Socket aClientSocket) throws IOException {
		String strLogMessage = "Serving client action";
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
		try (PrintWriter outToClient = new PrintWriter(aClientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(aClientSocket.getInputStream()));) {
			String msgFromClient = in.readLine();
			LOGGER.info("Msg From Client: " + msgFromClient);
			String strResult = handleClientMsg(msgFromClient);
			outToClient.print(strResult);
			outToClient.flush();
		} catch (SocketException se) {
			String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, se);
		} finally {
			closeSocketConnection(aClientSocket);
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}

	/**
	 * The message received from client. It is expected to be in URL form. E.g:
	 * Expected URL string:
	 * "?action=status&bn=batchname&bizD=20190224&uid=jpacpr&tout=1". The quote is
	 * just to say it is string, in url this is not needed. It also expects the
	 * string terminates with "bye" characters.
	 * 
	 * @param strMessage : The url string
	 * @return The result after handling the url according to the action in it. If
	 *         the URL is not as expected it will return error message.
	 */
	private String handleClientMsg(String strMessage) {
		String strLogMessage = AppUtils.formatMessage("Handling ClientMsg {0}", strMessage);
		LOGGER.info(StartFinish.START.getFormattedMsg(strLogMessage));
		String strErrorMsg = AppUtils.formatMessage("Error While {0}", strLogMessage);
		try {
			HashMap<String, String> aMessageMap = AppUtils.parseUrl(strMessage);
			BaseAction aBaseAction = ActionFactory.getAction(aMessageMap.get(BaseAction.ACTION_KEY));
			return aBaseAction.execute(aMessageMap);
		} catch (URISyntaxException e) {
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, e);
			return AppConstants.ERR_PARSE_QRY;
		} catch (Exception ex) {
			LOGGER.error(strErrorMsg);
			ERROR_LOGGER.error(strErrorMsg, ex);
			return AppConstants.ERR_INVALID_ACTION_IN_URL;
		} finally {
			LOGGER.info(StartFinish.END.getFormattedMsg(strLogMessage));
		}
	}
	
	private void closeSocketConnection(Socket clientSocket) {
		try {
			clientSocket.close();
		} catch (Exception ex) {
		}
	}
	
	private LinkedList<CommandLine> getCommandLineProcess() {
		return lstCommandProcess;
	}
	
	private void setCommandLineProcess(LinkedList<CommandLine> lstCommandProcess) {
		this.lstCommandProcess = lstCommandProcess;
	}
	
	public void addCommandLine(CommandLine aCommandLine) 
	{
		if(CollectionUtils.isEmpty(getCommandLineProcess())) {
			setCommandLineProcess(new LinkedList<>());
		}
		getCommandLineProcess().add(aCommandLine);
	}
	
	public void closeAllCommandLineProcess() {
		try {
			if(CollectionUtils.isEmpty(getCommandLineProcess())) {
				setCommandLineProcess(new LinkedList<>());
			}
			getCommandLineProcess().stream().forEach(aCommandLine -> {
				try {
					aCommandLine.destroy();
				}catch (Exception e) {
				}
			});
		}catch (Exception e) {
		}
	}
}
