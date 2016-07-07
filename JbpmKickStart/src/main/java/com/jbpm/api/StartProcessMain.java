package com.jbpm.api;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jbpm.util.RemoteRuntimeService;

/**
 * @author Amitabh Jain
 *
 */
public class StartProcessMain {

	private static final Logger logger = LoggerFactory.getLogger(StartProcessMain.class);
	
	public static void main(String[] args) {
		
		startProcess();
	}
	
		
	/**
	 *  This Method will start a new Instance of the process deployed onto Jboss server
	 */
	public static void startProcess(){
		
		RemoteRuntimeService remoteService= new RemoteRuntimeService();
		String userId= "lender1";  
		RuntimeEngine engine=remoteService.getRemoteRuntimeEngine(userId);
		KieSession kieSession= engine.getKieSession();
		String processId="deplymentId.ProcessId";
		Map<String, Object> inputParam= new HashMap<>();
		inputParam.put("input1", "01465");
		ProcessInstance instance =kieSession.startProcess(processId, inputParam);
		Long processInstanceId=instance.getId();
		instance.getProcessId();
		logger.info("Process Instance Id recieved is  : "+processInstanceId);
		
	}
}
