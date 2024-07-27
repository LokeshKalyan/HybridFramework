/****************************************************************************
 * File Name 		: PDFHighlightTextStripperByArea.java
 * Package			: com.dxc.zurich.utils
 * Author			: pmusunuru2
 * Creation Date	: Dec 14, 2022
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

import java.io.IOException;
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
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import com.abc.project.utils.AppUtils;

/**
 * @author pmusunuru2
 * @since Dec 14, 2022 10:42:47 am
 */
public class PDFHighlightTextStripper extends PDFTextStripper {

	/**
	 * @throws IOException
	 */
	private List<String> lstTextToHighlight;

	private boolean isStrictVerify;

	public PDFHighlightTextStripper() throws IOException {
		super();
	}

	public PDFHighlightTextStripper(String[] strTextToHighlight) throws IOException {
		this(strTextToHighlight, false);
	}

	public PDFHighlightTextStripper(String[] strTextToHighlight, boolean isStrictVerify) throws IOException {
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

	/**
	 * Override the default functionality of PDFTextStripper.writeString()
	 */

	@Override
	protected void writeString(String strText, List<TextPosition> textPositions) throws IOException {
		super.writeString(strText, textPositions);
		boolean isFound = CollectionUtils.isEmpty(lstTextToHighlight) ? false
				: lstTextToHighlight.stream().anyMatch(strTestData -> verifyText(strText, strTestData));

		float posXInit = 0, posXEnd = 0, posYInit = 0, posYEnd = 0, height = 0;

		if (isFound) {
			posXInit = textPositions.get(0).getXDirAdj();
			posXEnd = textPositions.get(textPositions.size() - 1).getXDirAdj()
					+ textPositions.get(textPositions.size() - 1).getWidth();
			posYInit = textPositions.get(0).getPageHeight() - textPositions.get(0).getYDirAdj();
			posYEnd = textPositions.get(0).getPageHeight() - textPositions.get(textPositions.size() - 1).getYDirAdj();
			height = textPositions.get(0).getHeightDir();

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

			PDColor yellow = new PDColor(new float[] { 1, 1, 1 / 255F }, PDDeviceRGB.INSTANCE);
			highlight.setColor(yellow);
			annotations.add(highlight);
		}
	}
}
