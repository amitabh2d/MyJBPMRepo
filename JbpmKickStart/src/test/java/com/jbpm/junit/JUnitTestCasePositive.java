package com.jbpm.junit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jbpm.util.Constants;
import com.jbpm.util.RuntimeEngineService;


/**
 * This is the Unit Test Case for validating user2's WorkList.
 * @author Amitabh Jain
 * @version 1.0
 * @since 2015-11-19
 */
public class JUnitTestCasePositive extends JbpmJUnitBaseTestCase {

    

    private static final Logger logger = LoggerFactory.getLogger(JUnitTestCasePositive.class);
    private KieSession ksession =null;
    private TaskService taskService=null;
    RuntimeEngineService runtimeService= new RuntimeEngineService();
    public JUnitTestCasePositive() {
        super(true, true);
    }
   

    @Before 
    public void setUp() throws Exception {
		RuntimeEngine engine=runtimeService.getRuntimeEngine();
		ksession = engine.getKieSession();
		taskService = engine.getTaskService();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("recordId", "rec1234");
		ProcessInstance instance =ksession.startProcess("package","processid",params);
		System.out.println("proce"+instance.getId());
		// let lender1 execute Task
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("user1", "en-UK");
		TaskSummary task = list.get(0);
		params.put("adjudicatorId", "user2");
		taskService.start(task.getId(), "user1");
		taskService.complete(task.getId(), "user1", params);
    }
    
    
    /**
	 * Positive Case Validate user2's Worklist .
	 * @result 1.Actual Owner of the Task will be "user2".<br>
  	 * 		   2.Status of the task will be "InProgress" or "Reserved".<br>
  	 *         3.Task Name will be "Approve Call Action Record".
	 */
    @Test
    public void testUser2WorkList() {
    	
    
    	List<Status> taskStatusList = new ArrayList<Status>();
    	String userId= "user2";   // to get user2's Task List
    	//RuntimeEngineService remoteService= new RuntimeEngineService();
    	/*RuntimeEngine engine=remoteService.getRuntimeEngine();
        TaskService taskService = engine.getTaskService();*/
        taskStatusList.add(Status.InProgress);
        taskStatusList.add(Status.Reserved);
     
        logger.info("Start Fetching TaskList:  " + System.currentTimeMillis());
        try {
        	
        	List<TaskSummary> taskList =   taskService.getTasksOwnedByStatus (userId,taskStatusList, "en-US");
			String recordId="";
			assertNotEquals(0, taskList.size());
			for(TaskSummary task :taskList){		
					
				Long taskId =task.getId();
				Map<String, Object> taskContent = taskService.getTaskContent(taskId);
				if(taskContent!=null){
					recordId= taskContent.get(Constants.in_recordId).toString();
				}
				long processInstanceId = task.getProcessInstanceId();
				User user = task.getActualOwner();
				//checking user 
				assertEquals(user.getId(), userId);
				
				Status status= task.getStatus();
				//checking status
				assertTrue(taskStatusList.contains(status));
				//checking task Name
				assertEquals(Constants.approveTaskName, task.getName());
				String message= "Record Id: " + recordId + " Process Instance Id: " + processInstanceId + " Task Id: " + taskId + " Task Name: " + task.getName() + " Status: " + status + "Actual Owner:"+ user;
				logger.info(userId+"task summary values : "+message,task.getName());
				}
				logger.info("End Fetching TaskList for : "+userId + System.currentTimeMillis());
				
		} 
        catch (RuntimeException e) {
			logger.error("Runtime Exception occured in testuser2WorkList() : ", e);
			e.printStackTrace();
			assertTrue(false);
		}
        catch (Exception e) {
			logger.error("Exception occured in testuser2WorkList() : ", e);
			e.printStackTrace();
			assertTrue(false);
		}
    }
    
    
    @Override
    public void tearDown() throws Exception {
    	runtimeService.dispose();
    	super.tearDown();
    }
   
}
