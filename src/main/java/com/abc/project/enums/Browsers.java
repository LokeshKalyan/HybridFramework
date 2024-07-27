/****************************************************************************
 * File Name 		: Browsers.java
 * Package			: com.dxc.zurich.enums
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
package com.abc.project.enums;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * @author pmusunuru2
 * @since Feb 16, 2021 10:57:12 am
 */
public enum Browsers {

	WINDOWS_CHROME("WindowsGC", "Chrome", BrowserType.DESKTOP_WEB, 20), 
	WINDOWS_FIREFOX("WindowsMF", "FireFox", BrowserType.DESKTOP_WEB, 19),
	WINDOWS_EDGE("WindowsEdge", "Microsoft Edge", BrowserType.DESKTOP_WEB, 18),
	WINDOWS_IE("WindowsIE", "Internet Explorer", BrowserType.DESKTOP_WEB, 17), 
	WINDOWS_ELECTRON_CHROME("WindowsElectronGC", "Chrome", BrowserType.DESKTOP_WEB, 16), 
	BROWSER_STACK_DESKTOP("Browser_Stack_Desktop", "Browser Stack", BrowserType.DESKTOP_WEB, 15), 
	ANDROID_CHROME("Android_Chrome", "Chrome", BrowserType.MOBILE_WEB, 14), 
	ANDROID_FIREFOX("Android_Firefox", "FireFox", BrowserType.MOBILE_WEB, 13), 
	BROWSER_STACK_MOBILE("Browser_Stack_Mobile", "Browser Stack", BrowserType.MOBILE_WEB, 13), 
	ANDROID_NATIVE("Android_Native", "Android", BrowserType.MOBILE_NATIVE, 11), 
	BROWSER_STACK_ANDROID_NATIVE("Browser_Stack_Android_Native", "Browser Stack", BrowserType.MOBILE_NATIVE, 10), 
	BROWSER_STACK_IOS_NATIVE("Browser_Stack_IOS_Native", "Browser Stack", BrowserType.MOBILE_NATIVE, 9), 
	WINDOWS_NATIVE("Windows_Native", "Windows", BrowserType.DESKTOP_NATIVE, 8), 
	INVALID_BROWSER("UnSupported_Browser", "In-Valid", BrowserType.INVALID, -1);

	private String browserName;
	private String browserShortName;
	private BrowserType aBrowserType;
	private int browserPrority;

	Browsers(String browserName, String browserShortName, BrowserType aBrowserType, int browserPrority) {
		this.setBrowserName(browserName);
		this.setBrowserShortName(browserShortName);
		this.setBrowserType(aBrowserType);
		this.setBrowserPrority(browserPrority);
	}

	/**
	 * @return the browserName
	 */
	public String getBrowserName() {
		return browserName;
	}

	/**
	 * @param browserName the browserName to set
	 */
	public void setBrowserName(String browserName) {
		this.browserName = browserName;
	}

	/**
	 * @return the browserShortName
	 */
	public String getBrowserShortName() {
		return browserShortName;
	}

	/**
	 * @param browserShortName the browserShortName to set
	 */
	public void setBrowserShortName(String browserShortName) {
		this.browserShortName = browserShortName;
	}

	/**
	 * @return the isDesktop
	 */
	public BrowserType getBrowserType() {
		return aBrowserType;
	}

	/**
	 * @param isDesktop the isDesktop to set
	 */
	public void setBrowserType(BrowserType aBrowserType) {
		this.aBrowserType = aBrowserType;
	}

	/**
	 * @return the browserPrority
	 */
	public int getBrowserPrority() {
		return browserPrority;
	}

	/**
	 * @param browserPrority the browserPrority to set
	 */
	public void setBrowserPrority(int browserPrority) {
		this.browserPrority = browserPrority;
	}
	
	public boolean isNative() 
	{
		return getBrowserType()!= null && (getBrowserType() == BrowserType.DESKTOP_NATIVE || getBrowserType() == BrowserType.MOBILE_NATIVE);
	}

	@Override
	public String toString() {
		return String.format("Browser Name %s - with Proirty %s", getBrowserName(), getBrowserPrority());
	}

	public static Browsers getBrowserByName(String browserName) {
		List<Browsers> lstBrowsers = Arrays.asList(Browsers.values());
		Browsers aBrowser = lstBrowsers.stream().filter(aConfig -> StringUtils.equalsIgnoreCase(aConfig.getBrowserName(), browserName)).findFirst().orElse(Browsers.INVALID_BROWSER);
		return aBrowser;
	}
}
