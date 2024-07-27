/****************************************************************************
 * File Name 		: PDFHighlightTextStripperByArea.java
 * Package			: com.dxc.zurich.pdf.utils
 * Author			: RAVITHEJA.KORLAGUNTA
 * Creation Date	: Jul 19, 2023
 * Project			: Zurich Automation - UKLife
 * CopyRight		: DXC
 * Description		: 
 * ****************************************************************************
 * | Mod Date	| Mod Desc									| Mod Ticket Id   |
 * ****************************************************************************
 * |			|											|				  |
 * | 			|		*									|	*			  |
 * ***************************************************************************/
package com.abc.project.pdf.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.text.TextPosition;

import com.abc.project.utils.AppUtils;

/**
 * @author RAVITHEJA.KORLAGUNTA
 * @since Jul 19, 2023 14:55:21
 */
public class PDFHighlightTextStripperByArea extends PDFTextStripperByArea {

	/**
	 * @throws IOException
	 */
	private List<String> lstTextToHighlight;

	private boolean isStrictVerify;
	
	public PDFHighlightTextStripperByArea(String[] strTextToHighlight) throws IOException {
		this(strTextToHighlight, false);
	}

	public PDFHighlightTextStripperByArea(String[] strTextToHighlight, boolean isStrictVerify)
			throws IOException {
		super();
		this.lstTextToHighlight = strTextToHighlight == null ? new ArrayList<>() : Arrays.asList(strTextToHighlight);
		this.isStrictVerify = isStrictVerify;
	}
	
	private boolean verifyText(String strSrcText, String strTestData) {
		strSrcText = AppUtils.removeInvisbleCharacters(strSrcText);
		if (isStrictVerify && StringUtils.equalsIgnoreCase(strSrcText, strTestData)) {
			return true;
		}
		if (!isStrictVerify && StringUtils.containsIgnoreCase(strSrcText, strTestData)) {
			return true;
		}
		return false;
	}

	public List<List<TextPosition>> getCharactersByArticle() {
		return super.getCharactersByArticle();
	}

	public void stripPage(int pageNr) throws IOException {
		stripPage(pageNr, pageNr);
	}
	
	
	public void stripPage(int startPageValue, int endPageValue) throws IOException {
		this.setStartPage(startPageValue + 1);
		this.setEndPage(endPageValue + 1);
		try (Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());) {
			this.writeText(document, dummy);
		}
	}

	/**
	 * Override the default functionality of PDFTextStripper.writeString()
	 */

	@Override
	protected void writeString(String strText, List<TextPosition> textPositions) throws IOException {
		super.writeString(strText, textPositions);
		boolean isFound = CollectionUtils.isEmpty(lstTextToHighlight) ? false
				: lstTextToHighlight.stream().anyMatch(strTestData -> verifyText(strText, strTestData));
		
		PDColor aPDColor =null;
		if (isFound) {
			aPDColor = new PDColor(new float[] { 0, 1, 1 / 255F }, PDDeviceRGB.INSTANCE);
		}else {
			aPDColor =  new PDColor(new float[] { 1, 0, 0 }, PDDeviceRGB.INSTANCE);
		}
		float posXInit = textPositions.get(0).getXDirAdj();
		float posXEnd = textPositions.get(textPositions.size() - 1).getXDirAdj()
				+ textPositions.get(textPositions.size() - 1).getWidth();
		float posYInit = textPositions.get(0).getPageHeight() - textPositions.get(0).getYDirAdj();
		float posYEnd = textPositions.get(0).getPageHeight() - textPositions.get(textPositions.size() - 1).getYDirAdj();
		float height = textPositions.get(0).getHeightDir();

		/* numeration is index-based. Starts from 0 */

		float quadPoints[] = { posXInit, posYEnd + height + 2, posXEnd, posYEnd + height + 2, posXInit,
				posYInit - 2, posXEnd, posYEnd - 2 };
		
		List<PDAnnotation> annotations = this.getCurrentPage().getAnnotations();
		PDAnnotationTextMarkup highlight = new PDAnnotationTextMarkup(PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT);

		PDRectangle position = new PDRectangle();
		position.setLowerLeftX(posXInit);
		position.setLowerLeftY(posYEnd);
		position.setUpperRightX(posXEnd);
		position.setUpperRightY(posYEnd + height);

		highlight.setRectangle(position);

		// quadPoints is array of x,y coordinates in Z-like order (top-left, top-right,
		// bottom-left,bottom-right)
		// of the area to be highlighted

		highlight.setQuadPoints(quadPoints);
		highlight.setColor(aPDColor);
		annotations.add(highlight);
	}

}
