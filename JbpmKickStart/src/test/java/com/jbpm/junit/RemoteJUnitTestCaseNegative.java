package com.jbpm.junit;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jbpm.util.RemoteRuntimeService;


/**
 * This is the Unit Test Case for checking  "Submit Approval" action for user2 user.
 * @author Amitabh Jain
 * @version 1.0
 * @since 2015-11-19
 */
public class RemoteJUnitTestCaseNegative extends JbpmJUnitBaseTestCase {


	private static final Logger logger = LoggerFactory.getLogger(RemoteJUnitTestCaseNegative.class);


	public RemoteJUnitTestCaseNegative() {
		super(true, false);
	}

	 /**
  	 * Validate user2's Action(Submit Approval) to Complete the Task.
  	 * @result 1.Status of the task will not be "Completed".<br>
  	 *         2.Task List size will not be "Zero".
  	 */
	@Test
	public void testSubmitApproval() {
		
		List<Status> taskStatusList = new ArrayList<Status>();
		RemoteRuntimeService remoteService= new RemoteRuntimeService();
		String userId= "user1";   // to complete user2's task
		String actualUser= "user2";   // to get user2's task list
    	RuntimeEngine engine=remoteService.getRemoteRuntimeEngine(userId);	
		TaskService taskService = engine.getTaskService();
		taskStatusList.add(Status.InProgress);
		taskStatusList.add(Status.Reserved);
		
		Status status=null;
		List<TaskSummary> taskList;
		logger.info("Start Fetching TaskList:  " + System.currentTimeMillis());
		try {
			taskList=   taskService.getTasksOwnedByStatus (actualUser,taskStatusList, "en-US");

			for(TaskSummary task :taskList){				
				Long taskId =task.getId();
											
				status= task.getStatus();
				if(status.equals(Status.Reserved)){
					taskService.start(taskId,userId);
					taskService.complete(taskId,userId, null);				
				}else if(status.equals(Status.InProgress)){
					taskService.complete(taskId,userId, null);
					}
			}

		}catch (RuntimeException e) {
			
			logger.error("Runtime Exception occured in testSubmitApproval() : ", e);
			e.printStackTrace();
			assertTrue(true);
		} 
		catch (Exception e) {
			logger.error("Exception occured in testSubmitApproval() : ", e);
			e.printStackTrace();
			assertTrue(false);
			
		}

	}

}
