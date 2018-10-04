package com.bb.path.form;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.bb.path.date.DateUtil;
import com.bb.path.file.FileReadUtil;
import com.bb.path.file.FileWriteUtil;
import com.bb.path.util.StringUtil;


public class PathPrinterForm extends JFrame {

	
	private static JTextField dirPathField = null;
	private static JTextField delimiterField = null;
	
	private static JTextField messageBox = null;
	private static JButton printButton = null;
	

	public PathPrinterForm(String version) {
		String title = "BBPathPrinter";
		if (version != null && version.length() > 0) {
			title = title + "_" + version; 
		}
		
		this.setTitle(title);
		this.setBounds(0, 0, 800, 260);
		this.setLayout(null);
		
		Font basicFont = new Font("돋움", 0, 12);
		
		// 폴더 경로
		// 딜리미터
		String dirPath = "";
		String delimiter = "\\\\r\\n";
		
		// properties 파일 읽기
		try {
			HashMap<String, String> optionMap = FileReadUtil.readPropertiesFile("data/option.properties");
			if (optionMap != null) {
				String temp = null;
				
				temp = optionMap.get("dirPath");
				if (temp != null) {
					dirPath = temp;
				}
				
				temp = optionMap.get("delimiter");
				if (temp != null) {
					delimiter = temp;
				}
			}
			
		} catch (Exception e) {
			// 무시
		}
		
		JLabel label1 = new JLabel("Dir Path");
		label1.setFont(basicFont);
		label1.setBounds(10, 10, 100, 30);
		this.add(label1);
		
		JLabel label2 = new JLabel("Delimiter");
		label2.setFont(basicFont);
		label2.setBounds(10, 50, 100, 30);
		this.add(label2);
		
		dirPathField = new JTextField(dirPath);
		dirPathField.setFont(basicFont);
		dirPathField.setBounds(100, 10, 670, 30);
		this.add(dirPathField);
		
		delimiterField = new JTextField(delimiter);
		delimiterField.setFont(basicFont);
		delimiterField.setBounds(100, 50, 670, 30);
		this.add(delimiterField);
		
		messageBox = new JTextField("");
		messageBox.setFont(basicFont);
		messageBox.setBounds(10, 110, 760, 30);
		messageBox.setEditable(false);
		messageBox.setBackground(this.getBackground());
		this.add(messageBox);
		
		printButton = new JButton("PRINT");
		printButton.setFont(basicFont);
		printButton.setBounds(10, 160, 760, 30);
		
		printButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				executePrintButton();
			}
		});
		
		this.add(printButton);
		
		this.setVisible(true);
		
		String clipboardString = StringUtil.getClipboardString();
		if (dirPathField.getText() == null || dirPathField.getText().trim().length() == 0) {
			if (clipboardString != null && clipboardString.length() > 0) {
				if (clipboardString.length() > 1000) {
					clipboardString = clipboardString.substring(0, 1000);
				}
				
				dirPathField.setText(clipboardString);
			}
		}
		
		dirPathField.setFocusable(true);
		dirPathField.requestFocus();
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				super.windowClosing(arg0);
				System.exit(0);
			}
		});
	}
	
	
	private static void executePrintButton() {
		
		printButton.setEnabled(false);
		
		try {
			String dirPath = dirPathField.getText();
			if (dirPath == null || dirPath.length() == 0) {
				printMessage("폴더 경로(Dir Path)를 입력해주십시오.");
				return;
			}
			
			File dirObj = new File(dirPath);
			if (!dirObj.exists()) {
				printMessage("해당 경로가 존재하지 않습니다. Dir Path == [" + dirPath + "]");
				return;
			}
			
			if (!dirObj.isDirectory()) {
				printMessage("해당 경로는 폴더가 아닙니다. Dir Path == [" + dirPath + "]");
				return;
			}
			
			printMessage("폴더 분석을 시도합니다. 잠시 기다려주십시오.");
			
			ArrayList<String> fileList = new ArrayList<String>();
			
			addFileToList(fileList, dirObj, "/");
			fileList = sortFileList(fileList);
			
			
			String delimiter = delimiterField.getText();
			if (delimiter == null || delimiter.length() == 0) {
				delimiter = "\r\n";
			}
			
			while (delimiter.indexOf("\\r") > -1) {
				delimiter = delimiter.replace("\\r", "\r");
			}
			
			while (delimiter.indexOf("\\n") > -1) {
				delimiter = delimiter.replace("\\n", "\n");
			}
			
			while (delimiter.indexOf("\\t") > -1) {
				delimiter = delimiter.replace("\\t", "\t");
			}
			
			ArrayList<String> outputContent = new ArrayList<String>();
			
			if (fileList != null && fileList.size() > 0) {
				int fileCount = fileList.size();
				int lastIndex = fileCount - 1;
				String targetPath = StringUtil.revisePath(dirObj.getAbsolutePath(), "/");
				outputContent.add("대상 경로");
				outputContent.add("\r\n");
				outputContent.add(targetPath);
				outputContent.add("\r\n");
				outputContent.add("\r\n");
				
				outputContent.add("파일 개수");
				outputContent.add("\r\n");
				outputContent.add(String.valueOf(fileCount));
				outputContent.add("\r\n");
				outputContent.add("\r\n");
				
				outputContent.add("파일 목록");
				outputContent.add("\r\n");
				
				for (int i=0; i<fileCount; i++) {
					outputContent.add(fileList.get(i));
					
					if (i<lastIndex) {
						outputContent.add(delimiter);
					}
				}
				
			} else {
				outputContent.add("파일 개수 : 0");
			}
			
			File outputDir = new File("output");
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			
			String resultTxtFileName = DateUtil.getTodayDateTime() + ".txt"; 
			FileWriteUtil.writeFile("output/" + resultTxtFileName, outputContent, false);
			
			File resultFile = new File("output/" + resultTxtFileName);
			if (resultFile != null && resultFile.exists()) {
				printMessage("결과 파일이 생성되었습니다. [" + resultFile.getAbsolutePath() + "]");
				
				try {
					ProcessBuilder processBuilder = new ProcessBuilder();
					ArrayList commandList = new ArrayList();
					commandList.add("notepad.exe");
					commandList.add(resultFile.getAbsolutePath());
					processBuilder.command(commandList);
					processBuilder.start();
					
				} catch (Exception e) {
					// 무시
					// e.printStackTrace();
				}
				
			} else {
				printMessage("분석에 실패하였습니다.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			printButton.setEnabled(true);
		}
	}
	
	
	private static void printMessage(String str) {
		if (str == null) {
			str = "";
		}
		
		messageBox.setText(str);
	}
	
	
	private static void addFileToList(ArrayList<String> fileList, File file, String slashChar) {
		if (file == null || !file.exists()) {
			return;
		}
		
		if (file.isFile()) {
			fileList.add(StringUtil.revisePath(file.getAbsolutePath(), slashChar));
			
		} else if (file.isDirectory()) {
			
			File[] fileArr = file.listFiles();
			if (fileArr != null && fileArr.length > 0) {
				int fileCount = fileArr.length;
				for (int i=0; i<fileCount; i++) {
					addFileToList(fileList, fileArr[i], slashChar);
				}
			}
		}
	}
	
	
	/**
	* .java 파일, .class 파일 순으로 정렬한다.
	* @param fileList
	* @return
	*/
	private static ArrayList<String> sortFileList(ArrayList<String> fileList) {
		if (fileList == null || fileList.size() == 0) {
			return null;
		}
		
		ArrayList<String> resultList = new ArrayList<String>();
		
		String oneLine = null;
		String lowerCaseLine = null;
		
		int fileCount = fileList.size();
		for (int i=0; i<fileCount; i++) {
			oneLine = fileList.get(i);
			lowerCaseLine = oneLine.toLowerCase();
			if (lowerCaseLine.endsWith(".java")) {
				resultList.add(oneLine);
			}
		}
		
		for (int i=0; i<fileCount; i++) {
			oneLine = fileList.get(i);
			lowerCaseLine = oneLine.toLowerCase();
			if (lowerCaseLine.endsWith(".class")) {
				resultList.add(oneLine);
			}
		}
		
		for (int i=0; i<fileCount; i++) {
			oneLine = fileList.get(i);
			lowerCaseLine = oneLine.toLowerCase();
			if (!lowerCaseLine.endsWith(".java") && !lowerCaseLine.endsWith(".class")) {
				resultList.add(oneLine);
			}
		}
		
		return resultList;
	}
}