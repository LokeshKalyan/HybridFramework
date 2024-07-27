/****************************************************************************
 * File Name 		: AlmConnector.java
 * Package			: com.dxc.zurich.alm
 * Author			: pmusunuru2
 * Creation Date	: Dec 06, 2022
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.alm;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.abc.project.alm.beans.Response;

/**
 * @author pmusunuru2
 * @since Dec 06, 2022 2:40:41 pm
 */

public class AlmConnector {

	/**
	 * <p>
	 * Initializes / prepares a new connection to HP ALM using the provided details.
	 * A connection to ALM is realized using the class infrastructure.RestConnector.
	 * </p>
	 * <p>
	 * In order to open a connection prepared using this constructor you have to
	 * call the method <code>login</code> from this class and provide a user name as
	 * well as the corresponding password.
	 * </p>
	 * <p>
	 * Therefore connecting to ALM would look as follows.
	 * </p>
	 * <code>
	 * AlmConnector alm = new AlmConnector(new HashMap<String, String>(), Constants.HOST, Constants.DOMAIN, Constants.PROJECT); 
	 * <br/><br/> 
	 * alm.login("userName", "HP ALM Password");
	 * </code>
	 * 
	 * @param serverUrl - a String providing an URL following the format
	 *                  <code>"https://{HOST}/qcbin"</code>
	 * @param domain    - a String providing the domain a user wants to log onto.
	 * @param project   - a String providing the name of a project a user wants to
	 *                  log into.
	 */
	public AlmConnector(final String serverUrl, final String domain, final String project) {
		this.aRestConnector = RestConnector.getInstance().init(new HashMap<>(), serverUrl, domain, project, null);
	}

	/**
	 * <p>
	 * Once you initialized the class RestConnector, you can use this constructor to
	 * create a new object from AlmConnector since the referenced class
	 * RestConnector is keeping the connection details.
	 * </p>
	 */
	public AlmConnector() {
		this.aRestConnector = RestConnector.getInstance();
	}

	/**
	 * <p>
	 * Attempts to log a user into an ALM project. If a user is already
	 * authenticated, no action is applied but true will be returned.
	 * </p>
	 * <p>
	 * Calling <code>login</code> after being already authenticated will not logout
	 * the currently logged in user. You specifically have to call
	 * <code>logout</code> before logging in with other user credentials.
	 * </p>
	 * <p>
	 * To check if a user is authenticated call method
	 * <code>isAuthenticated()</code>.
	 * </p>
	 * 
	 * @param username - a String providing the name of a user in HP ALM.
	 * @param password - the HP ALM password corresponding a provided user name.
	 * @return true if user is successfully authenticated else false.
	 * @throws Exception
	 */
	public boolean login(String username, String password) throws Exception {
		/**
		 * Get the current authentication status.
		 * https://community.microfocus.com/adtd/sws-qc/f/itrc-895/124240/alm-rest-api-java-login-authentication
		 */
		boolean canLogin = MapUtils.isEmpty(aRestConnector.getCookies())
				|| StringUtils.isEmpty(StringUtils.trim(aRestConnector.getXSRFHeaderValue()));
		if (canLogin) {
			String oAuthURL = aRestConnector.buildUrl("rest/oauth2/login");
			return this.login(oAuthURL, username, password);
		}
		return true;
	}

	/**
	 * <p>
	 * Logging into HP ALM is standard HTTP login (basic authentication), where one
	 * must store the returned cookies for further use.
	 * <p>
	 * 
	 * @param loginUrl - a String providing an URL to authenticate at.
	 * @param username - an HP ALM user name.
	 * @param password - an HP ALM user password corresponding username.
	 * @return true if login is successful, else false.
	 * @throws Exception
	 */
	private boolean login(String loginUrl, String username, String password) throws Exception {
		// https://community.microfocus.com/adtd/sws-qc/f/itrc-895/124240/alm-rest-api-java-login-authentication
		LinkedHashMap<String, String> mpJsonData = new LinkedHashMap<>();
		mpJsonData.put("clientId", username);
		mpJsonData.put("secret", password);
		JSONObject aJsonObject = new JSONObject(mpJsonData);
		String strReq = aJsonObject.toString();
		byte[] reqBytes = strReq.getBytes("utf-8");
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Accept", "application/json");
		requestHeaders.put("Content-Type", "application/json");

		Response response = aRestConnector.httpPost(loginUrl, reqBytes, requestHeaders);

		boolean ret = response.getStatusCode() == HttpURLConnection.HTTP_OK;

		return ret;
	}

	/**
	 * Closes a session on a server and cleans session cookies on a client.
	 * 
	 * @return true if logout was successful.
	 * @throws Exception
	 */
	public boolean logout() throws Exception {

		/**
		 * note the get operation logs us out by setting authentication cookies to:
		 * LWSSO_COOKIE_KEY="" via server response header Set-Cookie
		 */
		Response response = aRestConnector.httpGet(aRestConnector.buildUrl("authentication-point/logout"), null, null);

		boolean bLogOut = (response.getStatusCode() == HttpURLConnection.HTTP_OK);
		if (bLogOut) {
			aRestConnector.setCookies(new HashMap<>());
			aRestConnector.setXSRFHeaderValue(null);
		}
		return bLogOut;
	}

	/**
	 * Indicates if a user is already authenticated and returns an URL to
	 * authenticate against if the user is not authenticated yet. Having this said
	 * the returned URL is always as follows.
	 * https://{host}/qcbin/authentication-point/authenticate
	 * 
	 * @return null if a user is already authenticated.<br>
	 *         else an URL to authenticate against.
	 * @throws Exception - an Exception occurs, if HTTP errors like 404 or 500 occur
	 *                   and the thrown Exception should reflect those errors.
	 */
	public String isAuthenticated() throws Exception {
		String isAuthenticateUrl = aRestConnector.buildUrl("rest/is-authenticated");
		String ret;

		Response response = aRestConnector.httpGet(isAuthenticateUrl, null, null);
		int responseCode = response.getStatusCode();

		/**
		 * If a user is already authenticated, the return value is set to null and the
		 * current connection is kept open.
		 */
		if (responseCode == HttpURLConnection.HTTP_OK) {
			ret = null;
		}
		/**
		 * If a user is not authenticated yet, return an URL at which he can
		 * authenticate himself via www-authenticate.
		 */
		else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
			ret = aRestConnector.buildUrl("authentication-point/authenticate");
		}
		/**
		 * If an error occurred during login, the function throws an Exception.
		 */
		else {
			throw response.getFailure();
		}

		return ret;
	}

	private RestConnector aRestConnector;

	public RestConnector getRestConnector() {
		return aRestConnector;
	}
}
