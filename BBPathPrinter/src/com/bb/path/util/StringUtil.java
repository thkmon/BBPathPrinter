package com.bb.path.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;


public class StringUtil {

	public static String revisePath(String path, String slashChar) {
		if (path == null || path.length() == 0) {
			return "";
		}
		
		if (slashChar == null || slashChar.length() == 0) {
			slashChar = "/";
		}
		
		if (path.indexOf("\\") > -1) {
			path = path.replace("\\", "/");
		}
		
		while (path.indexOf("//") > -1) {
			path = path.replace("//", "/");
		}
		
		if (path.indexOf("/") > -1 && !slashChar.equals("/")) {
			path = path.replace("/", slashChar);
		}
		
		return path;
	}
	
	
	/**
	 * 클립보드 데이터를 가져온다.
	 * 
	 * @return
	 */
	public static String getClipboardString() {
		
		String clipboardString = "";
		
		Clipboard clipboard = null;
		Transferable contents = null;
		
		try {
			clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			contents = clipboard.getContents(clipboard);
	
			if (contents != null) {
				clipboardString = (String)(contents.getTransferData(DataFlavor.stringFlavor));
			}
			
		} catch (Exception e) {
			// 무시
			// e.printStackTrace();
		}
		
		return clipboardString;
	}
}