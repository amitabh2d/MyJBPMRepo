package com.jbpm.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.remote.client.api.RemoteRuntimeEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author jaina2
 * 
 * This Service class is the starting point for getting RuntimeEngine instance that can interact with the remote API. 
 *  
 */
public class RemoteRuntimeService {
	
	private static final Logger logger = LoggerFactory.getLogger(RemoteRuntimeService.class);
	private String deploymentId="";
	private URL deploymentURL;
	private String password="";
	private RuntimeEngine engine;
	private Properties prop = new Properties();
	InputStream input = null;
	
	/**
	 * @return RuntimeEngine instance
	 */
	
		public RuntimeEngine getRemoteRuntimeEngine(String user) {
		
		try {
			
			input = new FileInputStream(AAMConstants.propertyFile);
			if (input != null) {
				// load a properties file
				prop.load(input);
				deploymentId=prop.getProperty(AAMConstants.deploymentId);
				deploymentURL= new URL(prop.getProperty(AAMConstants.deploymentURL));
				password= prop.getProperty(AAMConstants.password);
			} else {
				throw new FileNotFoundException("Properties file '" + AAMConstants.propertyFile + "' not found in the classpath");
			}
			
			engine = RemoteRuntimeEngineFactory.newRestBuilder().addDeploymentId(deploymentId)
  				.addUrl(deploymentURL)
 				.addUserName(user)
   			.addPassword(password)
  				.addTimeout(0)
  				.build();
		} catch (MalformedURLException e) {
			logger.error("Exception occured in RemoteRuntimeServices() : ", e);
			e.printStackTrace();
		}catch (IOException e) {
			logger.error("Exception occured in RemoteRuntimeServices() : ", e);
		} 
		catch (Exception e) {
			logger.error("Exception occured in RemoteRuntimeServices() : ", e);
			e.printStackTrace();
		}
		finally {
			try {
				input.close();
			} catch (IOException e) {
				logger.error("Exception occured in RemoteRuntimeServices() : ", e);
				e.printStackTrace();
			}
		}
		return engine;
	}
}
