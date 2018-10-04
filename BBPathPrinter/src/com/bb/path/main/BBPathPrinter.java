package com.bb.path.main;

import com.bb.path.form.PathPrinterForm;


public class BBPathPrinter {

	
	public static void main(String[] args) {
		
		String version = "181005";
		PathPrinterForm pathPrinterForm = null;
		
		try {
			pathPrinterForm = new PathPrinterForm(version);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
