package com.jbpm.api;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.task.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jbpm.util.RemoteRuntimeService;

/**
 * @author amitabh jain
 *
 */
public class CompleteTaskMain {

	private static final Logger logger = LoggerFactory.getLogger(CompleteTaskMain.class);
	public static void main(String[] args) {
		completeTask();

	}
	
	
	public static void completeTask(){
		
		RemoteRuntimeService remoteService= new RemoteRuntimeService();
		String userId="lender1";
		RuntimeEngine engine=remoteService.getRemoteRuntimeEngine(userId);
		TaskService taskService= engine.getTaskService();
		
		long taskId=304;
		Map<String, Object> resultMap= new HashMap<>();
		resultMap.put("out1", "user1");
		taskService.start(taskId, userId);
		taskService.complete(taskId, userId, resultMap);
		logger.info("Task Completed Succefully");
		
	}
}
