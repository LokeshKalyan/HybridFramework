/****************************************************************************
 * File Name 		: ZipUtils.java
 * Package			: com.dxc.zurich.utils
 * Author			: pmusunuru2
 * Creation Date	: Aug 12, 2021
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

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

/**
 * @author pmusunuru2
 * @since Aug 12, 2021 12:14:06 pm
 */
public class ZipUtils {

	private static ZipParameters getDefaultZipParameters() {
		ZipParameters zipParameters = new ZipParameters();
		zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
		zipParameters.setCompressionLevel(CompressionLevel.PRE_ULTRA);
		return zipParameters;
	}

	public static boolean createPasswordProtected(File aZipFile, String strPassword, List<File> lstFiles)
			throws Exception {
		if (lstFiles == null || lstFiles.isEmpty() || !StringUtils.isEmpty(StringUtils.trim(strPassword))) {
			return false;
		}
		if (aZipFile != null && aZipFile.exists()) {
			aZipFile.delete();
		}
		@SuppressWarnings("resource")
		ZipFile zipFile = new ZipFile(aZipFile);
		ZipParameters zipParameters = getDefaultZipParameters();
		zipParameters.setEncryptFiles(true);
		zipParameters.setEncryptionMethod(EncryptionMethod.AES);
		zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
		zipFile.setPassword(strPassword.toCharArray());
		zipFile.addFiles(lstFiles, zipParameters);
		return zipFile.isValidZipFile() && zipFile.isEncrypted();
	}

	public static boolean extractPasswordProtected(File aSrcFile, String strExtractionPath, String strPassword)
			throws Exception {
		@SuppressWarnings("resource")
		ZipFile aZipFile = new ZipFile(aSrcFile);
		if (aZipFile.isEncrypted() && StringUtils.isNotEmpty(StringUtils.trim(strPassword))) {
			aZipFile.setPassword(strPassword.toCharArray());
		}
		aZipFile.extractAll(strExtractionPath);
		return aZipFile.isValidZipFile();
	}

	public static boolean createZipFile(File aZipFile, List<File> lstFiles) throws Exception {
		if (lstFiles == null || lstFiles.isEmpty()) {
			return false;
		}
		if (aZipFile != null && aZipFile.exists()) {
			aZipFile.delete();
		}
		@SuppressWarnings("resource")
		ZipFile zipFile = new ZipFile(aZipFile);
		ZipParameters zipParameters = getDefaultZipParameters();
		zipFile.addFiles(lstFiles, zipParameters);
		return zipFile.isValidZipFile();
	}

}
