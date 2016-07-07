package com.jbpm.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jbpm.util.RemoteRuntimeService;

/**
 * @author Amitabh Jain
 *
 */
public class WorkListMain {
	
	private static final Logger logger = LoggerFactory.getLogger(WorkListMain.class);

	public static void main(String[] args)  {
	
		testWorkList();

	}

	/**
	 * This Method will Test the Worklist
	 * 
	 */
	public static void testWorkList(){

	List<Status> taskStatusList = new ArrayList<Status>();
	String userId= "user1";  
	RemoteRuntimeService remoteService= new RemoteRuntimeService();
	RuntimeEngine engine=remoteService.getRemoteRuntimeEngine(userId);
    TaskService taskService = engine.getTaskService();
    taskStatusList.add(Status.InProgress);
    taskStatusList.add(Status.Reserved);
   

 
    logger.info("Start Fetching TaskList:  " + System.currentTimeMillis());
    try {
		List<TaskSummary> taskList=   taskService.getTasksOwnedByStatus (userId,taskStatusList, "en-US");
		int recordCount= taskList.size();
		Long[] expectedInstance= new Long[recordCount+1];
		Long[] actualInstance = new Long[recordCount+1];
		int i=0;
		for(TaskSummary task :taskList){		
				
		Long taskId =task.getId();
		Map<String, Object> taskContent = taskService.getTaskContent(taskId);
		String recordId = taskContent.get("input_1").toString();
		long processInstanceId = task.getProcessInstanceId();
		User user = task.getActualOwner();
		Status status= task.getStatus();
		long instanceId = 0;
		// implement DAO code  to fetch instanceId for the given recordId
		/*RecordDetailDAO rec= new RecordDetailDAOImpl();
		instanceId=rec.getInstanceId("campDB");*/
		String message= "Record Id: " + recordId + " Process Instance Id: " + processInstanceId + " Task Id: " + taskId + " Task Name: " + task.getName() + " Status: " + status + "Actual Owner:"+ user;
		logger.info(userId+"task summary values : "+message,task.getName());
		actualInstance[i]=processInstanceId;
		expectedInstance[i]=instanceId;
		i++;
		}
		logger.info("End Fetching TaskList for : "+userId + System.currentTimeMillis());
		
	} 
    catch (RuntimeException e) {
		logger.error("Runtime Exception occured in testWorkList() : ", e);
		e.printStackTrace();
	}
    catch (Exception e) {
		logger.error("Exception occured in testWorkList() : ", e);
		e.printStackTrace();
	}
}
	


}
