/****************************************************************************
 * File Name 		: ClipboardManager.java
 * Package			: com.dxc.zurich.utils
 * Author			: pmusunuru2
 * Creation Date	: Aug 05, 2021
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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

/**
 * @author pmusunuru2
 * @since Aug 05, 2021 10:22:31 am
 */
public class ClipboardManager {

	private static Transferable lastCopiedData;

	public static void clearClipboard(Clipboard objSystemClipBoard) {
		lastCopiedData = objSystemClipBoard.getContents(null);
		objSystemClipBoard.setContents(new Transferable() {
			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[0];
			}

			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return false;
			}

			@Override
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
				throw new UnsupportedFlavorException(flavor);
			}
		}, null);
	}


	public static Transferable getTransferable(Clipboard objSystemClipBoard) {
		if ((objSystemClipBoard.getContents(null) == null
				|| objSystemClipBoard.getContents(null).getTransferDataFlavors() == null
				|| objSystemClipBoard.getContents(null).getTransferDataFlavors().length == 0)) {
			return null;
		} else {
			lastCopiedData = objSystemClipBoard.getContents(null);
			return lastCopiedData;
		}
	}


	public static Clipboard getSystemClipboard() {
		return Toolkit.getDefaultToolkit().getSystemClipboard();
	}
}
