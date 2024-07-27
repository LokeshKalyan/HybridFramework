/****************************************************************************
 * File Name 		: BarcodeImageDecoder.java
 * Package			: com.dxc.zurich.utils
 * Author			: pmusunuru2
 * Creation Date	: Feb 23, 2021
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.abc.project.beans.BarcodeInfo;
import com.abc.project.constants.ErrorMsgConstants;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.multi.MultipleBarcodeReader;

/**
 * @author pmusunuru2
 * @since Feb 23, 2021 11:34:35 am
 */
public class BarcodeImageDecoderUtil {

	public static List<BarcodeInfo> decodePDF(File aFile) throws Exception {
		if (!aFile.exists()) {
			throw new IOException(AppUtils.formatMessage(ErrorMsgConstants.FILENTFOUND, aFile.getPath()));
		}
		List<BarcodeInfo> lstBarcodeInfo = new LinkedList<>();
		try (InputStream aFileInputStream = new FileInputStream(aFile);
			PDDocument aPDFDocument = PDDocument.load(aFileInputStream);) {
			PDFRenderer aPDFRenderer = new PDFRenderer(aPDFDocument);
			for (int i = 0; i < aPDFDocument.getNumberOfPages(); i++) {
				BufferedImage aPDFImage = aPDFRenderer.renderImage(i);
				BinaryBitmap aBitMap = getBinaryBitmap(aPDFImage);
				lstBarcodeInfo.addAll(decode(aBitMap));
			}
			return lstBarcodeInfo;
		} catch (Exception e) {
			throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_IMG_DECODE, aFile.getPath()), e);
		}
	}

	public static List<BarcodeInfo> decodeImage(File aFile) throws Exception {
		if (!aFile.exists()) {
			throw new IOException(AppUtils.formatMessage(ErrorMsgConstants.FILENTFOUND, aFile.getPath()));
		}
		try {
			BufferedImage aImage = ImageIO.read(aFile);
			BinaryBitmap aBitMap = getBinaryBitmap(aImage);
			if (aBitMap.getWidth() < aBitMap.getHeight()) {
				if (aBitMap.isRotateSupported()) {
					aBitMap = aBitMap.rotateCounterClockwise();
				}
			}
			return decode(aBitMap);
		} catch (Exception e) {
			throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.ERR_IMG_DECODE, aFile.getPath()), e);
		}
	}
	
	private static BinaryBitmap getBinaryBitmap(BufferedImage aImage) {
		BinaryBitmap aBitMap = new BinaryBitmap(
				new HybridBinarizer(new BufferedImageLuminanceSource(aImage)));
		if (aBitMap.getWidth() < aBitMap.getHeight()) {
			if (aBitMap.isRotateSupported()) {
				aBitMap = aBitMap.rotateCounterClockwise();
			}
		}
		return aBitMap;
	}

	private static List<BarcodeInfo> decode(BinaryBitmap aBitMap) throws Exception {
		Reader aReader = new MultiFormatReader();
		MultipleBarcodeReader aMultipleBarcodeReader = new GenericMultipleBarcodeReader(aReader);
		Map<DecodeHintType, Object> hints = new HashMap<>();
		hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		Result[] aResults = aMultipleBarcodeReader.decodeMultiple(aBitMap, hints);
		List<BarcodeInfo> lstBarcodeInfo = new LinkedList<>();
		for (Result aResult : aResults) {
			BarcodeInfo aBarCode = new BarcodeInfo(aResult.getText(), aResult.getBarcodeFormat().toString());
			lstBarcodeInfo.add(aBarCode);
		}
		return lstBarcodeInfo;
	}
}
