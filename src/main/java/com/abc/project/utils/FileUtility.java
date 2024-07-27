/****************************************************************************
 * File Name 		: FileUtility.java
 * Package			: com.dxc.zurich.utils
 * Author			: pmusunuru2
 * Creation Date	: Jun 11, 2021
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.abc.project.constants.AppConstants;
import com.abc.project.constants.ErrorMsgConstants;

/**
 * @author pmusunuru2
 * @since Jun 11, 2021 3:05:37 pm
 */
public class FileUtility {

	public static byte[] getFileData(File aFile) throws Exception {
		if (aFile == null) {
			throw new Exception(ErrorMsgConstants.ERR_DEFAULT);
		}
		if (!aFile.exists()) {
			throw new Exception(AppUtils.formatMessage(ErrorMsgConstants.FILENTFOUND, aFile.getPath()));
		}
		try (InputStream aFileInputStream = new FileInputStream(aFile);
				ByteArrayOutputStream aFileByteStream = new ByteArrayOutputStream();) {
			byte[] aFileReadBytes = new byte[AppConstants.DEFAULT_FILE_READ_SIZE];
			int iFileData;
			while ((iFileData = aFileInputStream.read(aFileReadBytes)) != -1) {
				aFileByteStream.write(aFileReadBytes, 0, iFileData);
			}
			aFileByteStream.flush();
			return aFileByteStream.toByteArray();
		}
	}
	
	public static boolean writeDataToFile(byte[] aFileData, File aFile) throws Exception {
		if(!aFile.getParentFile().exists()) {
			aFile.getParentFile().mkdirs();
		}
		try(FileOutputStream aFileOutputStream = new FileOutputStream(aFile);
				ByteArrayInputStream aByteArrayInputStream = new ByteArrayInputStream(aFileData))
		{
			byte[] aFileReadBytes = new byte[AppConstants.DEFAULT_FILE_READ_SIZE];
			int iFileData;
			while ((iFileData = aByteArrayInputStream.read(aFileReadBytes)) != -1) {
				aFileOutputStream.write(aFileReadBytes, 0, iFileData);
			}
			aFileOutputStream.flush();
		}
		return aFile.exists();
	}
}
