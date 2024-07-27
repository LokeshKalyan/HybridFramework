/****************************************************************************
 * File Name 		: WebDriverScreen.java
 * Package			: com.dxc.zurich.base
 * Author			: pmusunuru2
 * Creation Date	: Aug 10, 2021
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.base;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.sikuli.api.DefaultScreenRegion;
import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.Screen;
import org.sikuli.api.ScreenLocation;
import org.sikuli.api.ScreenRegion;

import com.abc.project.beans.BrowsersConfigBean;
import com.abc.project.constants.ORConstants;
import com.abc.project.enums.Browsers;
import com.abc.project.reports.TestStepReport;
import com.abc.project.utils.AppUtils;
import com.abc.project.utils.WebUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author pmusunuru2
 * @since Aug 10, 2021 3:50:12 pm
 */
public class WebDriverScreen implements Screen {

	private double similarityScore = 1.0;

	private double targetminScore = 0.40;

	private WebDriver aWebDriver;

	private BrowsersConfigBean aBrowsersConfigBean;

	private Dimension aDimension;

	public WebDriverScreen(BrowsersConfigBean aBrowsersConfigBean, WebDriver aWebDriver) {
		this.aBrowsersConfigBean = aBrowsersConfigBean;
		this.aWebDriver = aWebDriver;
		aDimension = aWebDriver.manage().window().getSize();
	}

	@Override
	public BufferedImage getScreenshot(int x, int y, int width, int height) {
		BufferedImage aScreenImage = TestStepReport.getScreenShotImage(aWebDriver, aBrowsersConfigBean);
		BufferedImage cropped = crop(aScreenImage, x, y, width, height);
		aDimension = new Dimension(aScreenImage.getWidth(), aScreenImage.getHeight());
		return cropped;
	}

	private BufferedImage crop(BufferedImage src, int x, int y, int width, int height) {
		BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics graphics = dest.getGraphics();
		graphics.drawImage(src, 0, 0, width, height, x, y, x + width, y + height, null);
		graphics.dispose();
		return dest;
	}

	@Override
	public java.awt.Dimension getSize() {
		return new java.awt.Dimension(aDimension.getWidth(), aDimension.getHeight());
	}
	
	/**
	 * @return the aBrowser
	 */
	private Browsers getBrowser() {
		return aBrowsersConfigBean == null ? Browsers.INVALID_BROWSER : aBrowsersConfigBean.getBrowser();
	}

	public ScreenLocation findImageElement(String strTestData, File aImageFile, int iWaitMilliSeconds,
			double similarityScore, double targetminScore) {
		ScreenRegion webdriverRegion = null;
		Browsers aBrowsers = getBrowser();
		if (!WebUtils.isTouchScreenDriver(aWebDriver, aBrowsers)) {
			webdriverRegion = new DesktopScreenRegion();
		} else {
			webdriverRegion = new DefaultScreenRegion(this);
		}
		webdriverRegion.setScore(similarityScore);
		ImageTarget aImageTarget = new ImageTarget(aImageFile);
		aImageTarget.setMinScore(targetminScore);
		ScreenRegion aImageRegion = webdriverRegion.wait(aImageTarget, iWaitMilliSeconds);
		if (aImageRegion != null) {
			return getScreenLocation(aImageRegion, strTestData);
		}
		return null;
	}

	public ScreenLocation findImageElement(String strTestData, File aImageFile, int iWaitMilliSeconds,
			double similarityScore) {
		return findImageElement(strTestData, aImageFile, iWaitMilliSeconds, similarityScore, targetminScore);
	}

	public ScreenLocation findImageElement(String strTestData, File aImageFile, int iWaitMilliSeconds) {
		return findImageElement(strTestData, aImageFile, iWaitMilliSeconds, similarityScore);
	}

	private ScreenLocation getScreenLocation(ScreenRegion aScreenRegion, String strTestData) {
		if (aScreenRegion == null) {
			return null;
		}
		String strJsonTestData = AppUtils.getJsonData(strTestData);
		if (StringUtils.isEmpty(StringUtils.trim(strJsonTestData))) {
			return aScreenRegion.getCenter();
		}
		Gson aGson = AppUtils.getDefaultGson();
		Type aTestDataType = new TypeToken<LinkedHashMap<String, Object>>() {
		}.getType();
		LinkedHashMap<String, Object> mpTestData = aGson.fromJson(strJsonTestData, aTestDataType);
		String strTargetXPos = String.valueOf(mpTestData.get(ORConstants.FORMAT_XOFFSET_JSONKEY));
		String strTargetYPos = String.valueOf(mpTestData.get(ORConstants.FORMAT_YOFFSET_JSONKEY));
		int xPos = NumberUtils.isParsable(strTargetXPos) ? Integer.valueOf(strTargetXPos) : 0;
		int yPos = NumberUtils.isParsable(strTargetYPos) ? Integer.valueOf(strTargetYPos) : 0;
		ScreenLocation aScreenLocation = aScreenRegion.getCenter();
		return aScreenLocation.getRelativeScreenLocation(xPos, yPos);
	}
}
