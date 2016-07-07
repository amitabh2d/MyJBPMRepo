package com.bpm.util;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;



public class FileController {
	static Logger sysLog = Logger.getLogger(FileController.class.getName());
	public static Properties getExternalProperties() {
		Properties prop = null;
		try {
			prop = new Properties();
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			InputStream stream = loader
					.getResourceAsStream("ExternalConfiguration.properties");
			prop.load(stream);
		} catch (Exception e) {
			sysLog.error("Error in FileController method for FileController in Common  ",e);
			e.printStackTrace();
			System.out.println("Error occured in getExternalProperties : "
					+ e.getMessage());
		}
		return prop;
	}
	
	public static Properties getProductMapping() {
		Properties prop = null;
		try {
			prop = new Properties();
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			InputStream stream = loader
					.getResourceAsStream("ProductMapping.properties");
			prop.load(stream);
		} catch (Exception e) {
			sysLog.error("Error in FileController method for FileController in Common  ",e);
			e.printStackTrace();
			System.out.println("Error occured in getProductMapping : "
					+ e.getMessage());
		}
		return prop;
	}
	
	public static Properties getCollateralMapping() {
		Properties prop = null;
		try {
			prop = new Properties();
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			InputStream stream = loader.getResourceAsStream("CollateralMapping.properties");
			prop.load(stream);
		} catch (Exception e) {
			sysLog.error("Error in FileController method for FileController in Common  ",e);
			e.printStackTrace();
			System.out.println("Error occured in getProductMapping : "
					+ e.getMessage());
		}
		return prop;
	}
	
	public static InputStream readPropertyOrderMapping() {
		InputStream stream   = null;
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
		    stream = loader.getResourceAsStream("propertyordermapping.xml");
		 
		} catch (Exception e) {
			sysLog.error("Error in InputStream readPropertyOrderMapping method for FileController in Common  ",e);
			e.printStackTrace();
			System.out.println("Error occured in getExternalProperties : "
					+ e.getMessage());
		}
		return stream;
	}
	
	public static InputStream readConstraintXmlMapping() {
		InputStream stream   = null;
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
		    stream = loader.getResourceAsStream("ConstraintFields.xml");
		 
		} catch (Exception e) {
			sysLog.error("Error in InputStream readConstraintXmlMapping method for FileController in Common  ",e);
			e.printStackTrace();
			System.out.println("Error occured in getExternalProperties : "
					+ e.getMessage());
		}
		return stream;
	}
	
	public static List<String>   findAllWorkFlowFileName() {
		//Properties externalProperties = new FileController().getExternalProperties();
		List<String> fileNameList = new ArrayList<String>();
		
		Properties externalProperties = getExternalProperties();
		if(externalProperties!=null){
		String bpmnFilePath = (String) externalProperties.get("BPMN_FILE_PATH");
	
		File file = new File(bpmnFilePath);
		try {
			if (file.isDirectory()) {
				File[] listFiles = file.listFiles();
				for (File fileName : listFiles) {
				   String name = fileName.getName();
				   if(name.indexOf(".")!=-1){
					   String substring = name.substring(name.indexOf(".")+1, name.length());
					   if(substring.equalsIgnoreCase("bpmn"))
					      fileNameList.add(name);
				   }
					
				}
			}
		} catch (Exception e) {
			sysLog.error("Error in fetching the filenameList for FileController in Common  ",e);
			 System.out.println("error in findAllWorkFlowFileName");
			 e.printStackTrace();
			
		}
		}
		return fileNameList;
	}
	


}
