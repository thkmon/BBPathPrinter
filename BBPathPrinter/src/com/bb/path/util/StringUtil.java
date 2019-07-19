package com.bb.path.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import com.bb.path.data.StringList;


public class StringUtil {
	
	
	/**
	 * 파일 경로의 슬래시 문자열을 변경한다. (슬래시와 역슬래시만 지원)
	 * 슬래시 문자가 넘어오지 않았을 경우, 변경없이 패스를 그대로 리턴한다.
	 * 
	 * @param path
	 * @param slashChar
	 * @return
	 */
	public static String changePathSlash(String path, String slashChar) {
		if (path == null || path.length() == 0) {
			return "";
		}
		
		// 슬래시 문자가 넘어오지 않았을 경우, 변경없이 패스를 그대로 리턴한다.
		if (slashChar == null || slashChar.length() == 0) {
			return path;
		}
		
		if (slashChar.equals("\\") && path.indexOf("/") > -1) {
			path = path.replace("/", "\\");
			
		} else if (slashChar.equals("/") && path.indexOf("\\") > -1) {
			path = path.replace("\\", "/");
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
	
	
	public static StringList splitWithTrim(String str, String delimiter) {
		StringList resultList = null;
		
		if (str == null || str.trim().length() == 0) {
			return null;
			
		} else {
			str = str.trim();
		}
		
		if (str.indexOf(delimiter) < 0) {
			resultList = new StringList();
			resultList.add(str);
			return resultList;
		}
		
		String[] arr = str.split(delimiter);
		if (arr == null || arr.length == 0) {
			return null;
		}
		
		resultList = new StringList();
		
		int arrLen = arr.length;
		for (int i=0; i<arrLen; i++) {
			resultList.add(arr[i].trim());
		}
		
		return resultList;
	}
	
	
	public static String parseString(Object obj) {
		if (obj == null) {
			return "";
		}
		
		String result = "";
		try {
			result = String.valueOf(obj);
			
		} catch (Exception e) {
			return "";
		}
		
		return result;
	}
}