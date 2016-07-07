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
 * This is the Unit Test Case for checking  "Submit Approval" action for user2.
 * @author Amitabh Jain
 * @version 1.0
 * @since 2015-11-19
 */
public class RemoteJUnitTestCasePositive extends JbpmJUnitBaseTestCase {


	private static final Logger logger = LoggerFactory.getLogger(RemoteJUnitTestCasePositive.class);


	public RemoteJUnitTestCasePositive() {
		super(true, false);
	}

	 /**
  	 * Validate user2's Action(Submit Approval) to Complete the Task.
  	 * @result 
  	 *         1.Task List size will be "Zero".
  	 */
	@Test
	public void testSubmitApproval() {
		
		List<Status> taskStatusList = new ArrayList<Status>();
		String userId= "user2";   // to get user2's task list
		RemoteRuntimeService remoteService= new RemoteRuntimeService();
    	RuntimeEngine engine=remoteService.getRemoteRuntimeEngine(userId);	
		TaskService taskService = engine.getTaskService();
		taskStatusList.add(Status.InProgress);
		taskStatusList.add(Status.Reserved);

		logger.info("Start Fetching TaskList:  " + System.currentTimeMillis());
		try {
			List<TaskSummary> taskList=   taskService.getTasksOwnedByStatus (userId,taskStatusList, "en-US");

			for(TaskSummary task :taskList){				
				Long taskId =task.getId();
											
				Status status= task.getStatus();
				if(status.equals(Status.Reserved)){
					taskService.start(taskId,userId);
					taskService.complete(taskId,userId, null);				
				}else if(status.equals(Status.InProgress)){
					taskService.complete(taskId,userId, null);
					}
				
			}
			taskList =   taskService.getTasksOwnedByStatus(userId,taskStatusList, "en-US");
			assertEquals(taskList.size(), 0);

		}catch (RuntimeException e) {
			logger.error("Runtime Exception occured in testSubmitApproval() : ", e);
			e.printStackTrace();
			assertTrue(false);
		} 
		catch (Exception e) {
			logger.error("Exception occured in testSubmitApproval() : ", e);
			e.printStackTrace();
			assertTrue(false);
		}
	}

}
