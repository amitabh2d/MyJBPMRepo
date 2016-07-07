package com.bpm.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListenerFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.definition.process.Process;
import org.drools.impl.EnvironmentFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.io.impl.FileSystemResource;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.process.core.Work;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItemHandler;
import org.jbpm.process.workitem.wsht.LocalHTWorkItemHandler;
import org.jbpm.task.Comment;
import org.jbpm.task.Group;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskService;
import org.jbpm.task.User;
import org.jbpm.task.event.TaskEventListener;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.local.LocalTaskService;
import org.jbpm.workflow.core.WorkflowProcess;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.Split;


public class JbpmController {

	public  static EntityManagerFactory emf; 
	private static KnowledgeBase globalKbase;
	static Logger sysLog = Logger.getLogger(JbpmController.class.getName());
	//This task service is only used for search purpose
	private static org.jbpm.task.service.TaskService  taskServiceForSearch;
    private static final String FormatWithTime="yyyy-MM-dd HH:mm:ss";   
	private static TaskService client;
	static Properties externalProperties;
	
	static{
	    externalProperties = FileController.getExternalProperties();
		try {
			initialize();
		} catch (JBPMError e) {
			sysLog.error("Error while initialize in JbpmController  ",e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    private  static void initialize() throws JBPMError {
		try {
			emf = Persistence.createEntityManagerFactory("org.jbpm.runtime.ht.mysql");		
			//globalKbase = intialiseKnowledgeBase("LOS_corporate_project_finance.bpmn");
			
			// Creating task service for search task purpose
			taskServiceForSearch = new org.jbpm.task.service.TaskService(emf, 
					SystemEventListenerFactory.getSystemEventListener(),new EscalationHandler());
			TaskEventListener listener = new JBPMTaskEventListener();
			taskServiceForSearch.addEventListener(listener);			
			
		} catch (Exception e) {
			sysLog.error("Error in initialize method in JbpmController  ",e);
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new JBPMError(e.getMessage());
		}

    }	
    
    public static TaskService initTaskService(List<String> userList,List<String> groupList,
    		TaskEventListener listener){
    	 
		//Initialize task service client
		Map<String, User> users = new HashMap<String, User>();
		Map<String, Group> groups = new HashMap<String, Group>();
		for(String user :userList){
			 users.put(user, new User(user));
		}
		for(String group :groupList){
			groups.put(group, new Group(group));
		}
		//Adding default mandatory user Administrator
		 users.put("Administrator",new User("Administrator"));
		 
		
		org.jbpm.task.service.TaskService  taskService = new org.jbpm.task.service.TaskService(emf, 
				SystemEventListenerFactory.getSystemEventListener(),new EscalationHandler());
		// TaskEventListener listener = new JBPMTaskEventListener();
		taskService.addEventListener(listener);
		 
		 
		taskService.addUsersAndGroups(users, groups);
		 
		TaskService client = new LocalTaskService(taskService);
		return client;
    	
    }
    
    
    public static TaskService initTaskService1(List<String> userList,List<String> groupList){
    	 
		//Initialize task service client
		Map<String, User> users = new HashMap<String, User>();
		Map<String, Group> groups = new HashMap<String, Group>();
		for(String user :userList){
			 users.put(user, new User(user));
		}
		for(String group :groupList){
			groups.put(group, new Group(group));
		}
		//Adding default mandatory user Administrator
		 users.put("Administrator",new User("Administrator"));
		 
		
		org.jbpm.task.service.TaskService  taskService = new org.jbpm.task.service.TaskService(emf, 
				SystemEventListenerFactory.getSystemEventListener(),new EscalationHandler());
		taskService.addUsersAndGroups(users, groups);
		TaskService client = new LocalTaskService(taskService);
		return client;
    	
    }
  
    
    private static TaskService initTaskServiceForSearch(List<String> userList,List<String> groupList){
		Map<String, User> users = new HashMap<String, User>();
		Map<String, Group> groups = new HashMap<String, Group>();
		for(String user :userList){
			 users.put(user, new User(user));
		}
		for(String group :groupList){
			groups.put(group, new Group(group));
		}
		//Adding default mandatory user Administrator
		 users.put("Administrator",new User("Administrator"));
		 
		 taskServiceForSearch.addUsersAndGroups(users, groups);
		 
		TaskService client = new LocalTaskService(taskServiceForSearch);
		return client;
    	
    	
    }
	
	public static KnowledgeBase  intialiseKnowledgeBase(String jbpmFiles){

		// check if there aer multiple jbpm files
		
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        KnowledgeBase kbase;
        String bpmnFilePath = "";

        	
    		StringTokenizer st = new StringTokenizer(jbpmFiles,"-");
    		while(st.hasMoreTokens()){
    			String bpmFile = "";
    	        try {    			
	    			bpmFile = st.nextToken();
	    			
	        		bpmnFilePath = (String) externalProperties.get("BPMN_FILE_PATH");
	        		bpmnFilePath = bpmnFilePath + bpmFile;
	        		System.out.println("Loading bpmn file from  file system");
	    			kbuilder.add(new FileSystemResource(bpmnFilePath), ResourceType.BPMN2);
	    			
	    	        if (kbuilder.hasErrors()) {
	    	            for (KnowledgeBuilderError error : kbuilder.getErrors()) {
	    	                System.out.println(">>> Error:" + error.getMessage());
	    	            }
	    	        }
	    	        sysLog.info("Loading bpmn file from  file system " +bpmFile);	

    			} catch (Exception e) {
    				sysLog.error("Error in KnowledgeBase  intialiseKnowledgeBase method in JbpmController  ",e);
    	        	kbuilder.add(new ClassPathResource(bpmFile), ResourceType.BPMN2);
    			}    			
    			
    		}
        	

        kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
       
        return kbase;

	}
	
	
	public static KnowledgeBase  intialiseKnowledgeBase(List<String>  jbpmFiles) throws JBPMError{
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        KnowledgeBase kbase;
        for (String bpmnFile : jbpmFiles) {
        	kbuilder.add(new FileSystemResource(bpmnFile), ResourceType.BPMN2);
		}
    	
        
        if (kbuilder.hasErrors()) {

        	Set<String> errorList = new HashSet<String>();
            for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                System.out.println(">>> Error:" + error.getMessage());
                errorList.add( error.getMessage());
            }
            
        	throw new JBPMError(errorList);
        }

        kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;

	}
    
	  private  static  WorkflowInfo createProcessInstance(String processID,KnowledgeBase kbase,Map<String, Object> params,List<String> userList,
			  List<String> groupList, HashMap<String,WorkItemHandler> workItemHandler,
			  TaskEventListener listener) throws JBPMError{
	    	
	    	StatefulKnowledgeSession ksession = null;
	    	WorkflowInfo wi = null;
			try {
				// intialiseKnowledgeBase(fileName);
				
				// Start session
				Environment env = EnvironmentFactory.newEnvironment();
				env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
				
/*				Properties properties = new Properties();
				properties.put("drools.processInstanceManagerFactory", "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory");
				properties.put("drools.processSignalManagerFactory", "org.jbpm.process.instance.event.DefaultSignalManagerFactory");
*/	
//				config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
				
				ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
			
			 	//Initilise task servcie with user
				TaskService client = initTaskService(userList,groupList,listener);
			
				//Register human task
				LocalHTWorkItemHandler localHTWorkItemHandler = new LocalHTWorkItemHandler(client, ksession);
				localHTWorkItemHandler.connect();
				ksession.getWorkItemManager().registerWorkItemHandler("Human Task", localHTWorkItemHandler);
				
				
				for (String name :workItemHandler.keySet()){
					ksession.getWorkItemManager().registerWorkItemHandler(name,workItemHandler.get(name));
				}
				// ksession.getWorkItemManager().registerWorkItemHandler("Service", new SystemTaskHandler());

			 
				ProcessInstance processInstance = ksession.startProcess(processID,params);
				wi = new WorkflowInfo();
				//assignTaskToUser(client,processID,processInstance.getId(),userList,groupList,userGroups,workFlowMapping);
				wi.setProcessInstanceId(processInstance.getId());
				wi.setProcessId(processID);
				wi.setSessionID(ksession.getId());
				sysLog.info("Task complted sucessfully for  processid  " +processID+ " and for workFlow"  +wi);	
				
			} catch (Exception e) {
				sysLog.error("Error in  WorkflowInfo createProcessInstance method in JbpmController  ",e);
				e.printStackTrace();
				throw new JBPMError(e.getMessage());
			 
			}finally{
	/*			if (ksession != null)
					ksession.dispose();		*/	
			}
			return wi;

	    }	
 
 
	  	public  static  boolean   getProcessInstanaceInfo(long processID,KnowledgeBase kbase) throws JBPMError{
		    	
		    	StatefulKnowledgeSession ksession = null;
		    	 long instanceId=0;
		    	 boolean found =false;
				try {
				     Environment env = EnvironmentFactory.newEnvironment();
					 env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
	 					
					ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
					ProcessInstance processInstance = ksession.getProcessInstance(processID);
					if(processInstance!=null ){
						instanceId = processInstance.getId();
						found = true;
					} 
					sysLog.info(" ProcessInstanace started sucessfully for  instanceId   " +instanceId);	
		
	  	
	  	}catch (Exception e) {
	  		sysLog.error("Error in getProcessInstanaceInfo method in JbpmController  ",e);
			 e.printStackTrace();
		}
				return found;
       }
	  
	/*	public static void  assignTaskToUser(TaskService client,String processID,Long processInstanceId, List<String> userList,
				  List<String> groupList, Map<String, List<UserGroupBO>> userGroups, Map<String, String> workFlowMapping) {
			UserGroupCallbackManager manager = UserGroupCallbackManager
					.getInstance();
			List<Status> statusList = new ArrayList<Status>();
			statusList.add(Status.Created);
			statusList.add(Status.InProgress);
			ArrayList<TaskBean> userTaskbyProcessID = getUserTaskbyProcessID(processInstanceId, userList, groupList,statusList);
			
			if (userTaskbyProcessID.size() == 0)
				return ;
			
			for (TaskBean taskBean :userTaskbyProcessID ){
					Task task = client.getTask(taskBean.getId());
					Content content = client.getContent(task.getTaskData().getDocumentContentId());
					   Map<String,Integer> taksListSize = new HashMap<String,Integer>();
				        List<TaskSummary> tasks=null;
		            Object result = ContentMarshallerHelper.unmarshall(content.getContent(), null);
		            Map<?, ?> map = (Map<?, ?>) result;
		            for (Map.Entry<?, ?> entry : map.entrySet()) {
	
					if (entry.getKey() != null && entry.getKey().equals("TaskName")) {
						
						
						
						if(workFlowMapping!=null){
						String taskMappingGroup = processID+":"+entry.getValue();	
						String groupCode = workFlowMapping.get(taskMappingGroup);
						List<UserGroupBO> userGroupList = userGroups.get(groupCode);
						for (UserGroupBO userGroupData : userGroupList) {
							List<String> groupId = new ArrayList<String>();
							groupId.add(groupCode);
							List<Status> statusList1 = new ArrayList<Status>();
							statusList1.add(Status.Reserved);
							statusList1.add(Status.InProgress);
							Properties groupCallBck = new Properties();
							groupCallBck.setProperty(userGroupData.getLoginCode(), groupCode);
							manager.setCallback(new DefaultUserGroupCallbackImpl(
									groupCallBck));
							tasks = client.getTasksAssignedAsPotentialOwnerByStatus(userGroupData.getLoginCode(),  statusList, "en-UK");
							taksListSize.put(userGroupData.getLoginCode() + ":" + groupCode, tasks.size());
							
						}
						 
						}
	
					}
				}
			       
	
			    List<String> minimumTasksUsersList = findMinimumTasksUsers(taksListSize);
			  
				if (minimumTasksUsersList.size() > 0) {
					String minimumTaskGroup = minimumTasksUsersList.get(0);
					String[] min = minimumTaskGroup.split(":");
					Properties groupCallBck = new Properties();
					groupCallBck.setProperty(min[0], min[1]);
					manager.setCallback(new DefaultUserGroupCallbackImpl(
							groupCallBck));
	
					PeopleAssignments peopleAssignments = new PeopleAssignments();
					List<OrganizationalEntity> adminsEntities = new ArrayList<OrganizationalEntity>();
					adminsEntities.add(new User(min[0]));
					peopleAssignments.setBusinessAdministrators(adminsEntities);
	
					List<OrganizationalEntity> usersEntities = new ArrayList<OrganizationalEntity>();
					usersEntities.add(new User(min[0]));
					peopleAssignments.setPotentialOwners(usersEntities);
					task.setPeopleAssignments(peopleAssignments);
					client.activate(taskBean.getId(), min[0]);
					client.claim(taskBean.getId(), min[0]);
	
				}
			 
				
				
			}
			
		}
	
	public static List<String> findMinimumTasksUsers(
			Map<String, Integer> taksListSize) {

		// list for finding the min value
		List<Integer> maxValList = new ArrayList<Integer>();
		// List for keeping the keys of the elements with the min value
		List<String> maxKeyList = new ArrayList<String>();
		// scan the map and put the values to the value list
		for (Entry<String, Integer> entry : taksListSize.entrySet()) {
			maxValList.add(entry.getValue());
		}

		for (Entry<String, Integer> entry : taksListSize.entrySet()) {

			if (entry.getValue() == Collections.min(maxValList)) {
				// add the keys of the elements with the max value at the
				// keyList
				maxKeyList.add(entry.getKey());

			}

		}
		return maxKeyList;

	}*/
    
    
	  private static ArrayList<TaskBean>  getUserTasks(String userName,List<String> userList,List<String> groupList) {
		   client = initTaskServiceForSearch(userList, groupList);
		    List<Status> statusList = setStatusList();
			List<TaskSummary> userTasks = client.getTasksAssignedAsPotentialOwnerByStatus(userName,statusList, "en-UK");
		    return getTaskBean(userTasks,client);
		}  
	  private static ArrayList<TaskBean>  getUserTasks(String userName, Integer firstRow, int numberOfRows,List<String> userList,List<String> groupList) {
		// List<TaskSummary> userTasks =
		// client.getTasksAssignedAsPotentialOwner(userName, "en-UK");
		  
		client = initTaskServiceForSearch(userList, groupList);
		List<TaskSummary> userTasks = client.getTasksAssignedAsPotentialOwner(
				userName, null, "en-UK", firstRow, numberOfRows);
		return getTaskBean(userTasks, client);
		}    

/*	  public static  ArrayList<TaskBean>  getUserTaskbyProcessID(long processID,List<String> userList,List<String> groupList){
			List <Status> statusList = new ArrayList <Status>();
			statusList.add(Status.Completed);
			statusList.add(Status.Created);
			statusList.add(Status.Error);
			statusList.add(Status.Exited);
			statusList.add(Status.Failed);
			statusList.add(Status.InProgress);
			statusList.add(Status.Obsolete);
			statusList.add(Status.Ready);
			statusList.add(Status.Reserved);
			statusList.add(Status.Suspended);
			
			TaskService client = initTaskServiceForSearch(userList,groupList);
			List<TaskSummary> userTasks = client.getTasksByStatusByProcessId(processID,statusList, "en-UK" );

			return getTaskBean(userTasks,client);		
		}*/

	  
	  public static  ArrayList<TaskBean>  getUserTaskbyProcessID(long processID,List<String> userList,List<String> groupList,List <Status> statusList){
		    if(statusList == null || (statusList != null && statusList.size() ==0 )){
				statusList = setStatusList();
		    }
		  
			TaskService client = initTaskServiceForSearch(userList,groupList);
			List<TaskSummary> userTasks = client.getTasksByStatusByProcessId(processID,statusList, "en-UK" );
			return getTaskBean(userTasks,client);		
		}

		private static List<Status> setStatusList() {
			List<Status> statusList;
			statusList = new ArrayList <Status>();
			statusList.add(Status.Completed);
			statusList.add(Status.Created);
			statusList.add(Status.Error);
			statusList.add(Status.Exited);
			statusList.add(Status.Failed);
			statusList.add(Status.InProgress);
			statusList.add(Status.Obsolete);
			statusList.add(Status.Ready);
			statusList.add(Status.Reserved);
			statusList.add(Status.Suspended);
			return statusList;
		}
			  

	  private static ArrayList<TaskBean> getTaskBean(List<TaskSummary> userTasks, TaskService client) {
			ArrayList<TaskBean> list = new ArrayList<TaskBean>();
			SimpleDateFormat dateFormat = new SimpleDateFormat(FormatWithTime);
			for (TaskSummary userTask : userTasks) {

				Task t = client.getTask(userTask.getId());
					 
				Date date = null;
				if (t.getDeadlines().getStartDeadlines().size() != 0)
					date = t.getDeadlines().getStartDeadlines().get(0).getDate();
				
				if (t.getDeadlines().getEndDeadlines().size() != 0)
					date = t.getDeadlines().getEndDeadlines().get(0).getDate();
				Date completedOn = t.getTaskData().getCompletedOn();
				
				TaskBean task = new TaskBean();

				task.setId(userTask.getId());
				task.setName(userTask.getName());
				task.setDesc(userTask.getDescription());
				task.setProcessId(userTask.getProcessId());
				task.setProcessInstanceId(userTask.getProcessInstanceId());
				task.setStatus(getStatusDesc(userTask.getStatus().toString()));
				task.setTaskStatus(userTask.getStatus().toString());
				task.setCompletedOn(completedOn);
				User actualOwner = userTask.getActualOwner();
				if(actualOwner!=null){
				task.setActualOwner(actualOwner.getId());
				}
				if(completedOn!=null ){
					String format = dateFormat.format(completedOn);
					task.setCompletedOnString(format);
				
				}
				List<OrganizationalEntity> potentialOwners = t.getPeopleAssignments().getPotentialOwners();
			 	if(potentialOwners!=null && potentialOwners.size()>0){
					String commaSepValueBuilder = CommaSepValueBuilder(potentialOwners,",");
					/*for (int i = 0; i < potentialOwners.size(); i++) {
						OrganizationalEntity orgEn =	(OrganizationalEntity)potentialOwners.get(i);
						commaSepValueBuilder.append(orgEn.getId());
						if ( i != potentialOwners.size()-1){
							commaSepValueBuilder.append(", ");
							}
						
					}*/
				   task.setOwner(commaSepValueBuilder.toString());
					 
				}
				
			 
				
				
				task.setPriority(userTask.getPriority());
				task.setCreatedOn(userTask.getCreatedOn());
				task.setCreatedBy(userTask.getCreatedBy() != null ? userTask.getCreatedBy().getId() : "");
				if(date!=null){
					task.setExpirationTime(date);
					task.setExpirationTimeString(dateFormat.format(date));
				}else{
					List<Comment> comments = t.getTaskData().getComments();
					if(comments!=null && comments.size()>0)
					for (Comment comment : comments) {
						task.setExpirationTime(comment.getAddedAt());
						 
					}
				
				}
				list.add(task);
				
			}
			 Collections.sort(list, JbpmController.procesIdComapartor);
			return list;
		}
 
	  
		
		public static Comparator<TaskBean> procesIdComapartor = new Comparator<TaskBean>() {
			public int compare(TaskBean form1, TaskBean form2) {
				 String id = String.valueOf(form1.getId());
				 String id2 = String.valueOf(form2.getId());
				return id.compareTo(id2);
	 
			}

		};
	  
	  public static String CommaSepValueBuilder(List<OrganizationalEntity> elements, String delimiter) {  
		  StringBuilder sb = new StringBuilder();  
		  for (OrganizationalEntity organizationalEntity : elements) {
			  if (sb.length() > 0)  
				  sb.append(delimiter);   
			      sb.append(organizationalEntity.getId());  
		}
		  return sb.toString();  
		} 
	  
	  /*	public static  ArrayList<TaskBean>  getUserTaskbyProcessID2(long ProcessID){
		
		Start 
      KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

      //Adds resources to the builder
      kbuilder.add(new ClassPathResource("LOS_corporate_project_finance.bpmn"), ResourceType.BPMN2);
      KnowledgeBuilderErrors errors = kbuilder.getErrors();

      //Checks for errors
      if (errors.size() > 0) {
          for (KnowledgeBuilderError error : errors) {
              System.out.println(error.getMessage());

          }
          throw new IllegalStateException("Error building kbase!");
      }

      //Creates a new kbase and add all the packages from the builder
      KnowledgeBaseConfiguration kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
      kbaseConf.setOption(EventProcessingOption.STREAM);
      KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConf);
      kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
      KnowledgeSessionConfiguration ksessionConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
      ksessionConf.setOption(ClockTypeOption.get("pseudo"));
      final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(ksessionConf, null);
      HornetQHTWorkItemHandler hornetQHTWorkItemHandler = new HornetQHTWorkItemHandler(ksession);
      ksession.getWorkItemManager().registerWorkItemHandler("Human Task",hornetQHTWorkItemHandler);
      KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession); 
		
		
		/// running taskclient
		
      TaskClient taskClient = new TaskClient(new HornetQTaskClientConnector("client 1",
              new HornetQTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
      boolean connected = taskClient.connect("127.0.0.1", 5443);

      System.out.println("Connected ? =>" + connected);
		
		End 
		
		List <Status> statusList = new ArrayList <Status>();
		statusList.add(Status.Completed);
		statusList.add(Status.Created);
		statusList.add(Status.Error);
		statusList.add(Status.Exited);
		statusList.add(Status.Failed);
		statusList.add(Status.InProgress);
		statusList.add(Status.Obsolete);
		statusList.add(Status.Ready);
		statusList.add(Status.Reserved);
		statusList.add(Status.Suspended);

		ArrayList<TaskBean> list = new ArrayList<TaskBean>();
		
		
		
		BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
		taskClient.getTasksByStatusByProcessId(ProcessID,statusList, "en-UK", responseHandler);
		List<TaskSummary> userTasks = responseHandler.getResults();
		
		for (TaskSummary userTask : userTasks) {
			TaskBean task = new TaskBean();
			task.setId(userTask.getId());
			task.setName(userTask.getName());
			task.setDesc(userTask.getDescription());
			task.setProcessId(userTask.getProcessId());
			task.setProcessInstanceId(userTask.getProcessInstanceId());
			task.setStatus(userTask.getStatus().toString());
			task.setOwner(userTask.getActualOwner().getId());
			list.add(task);
			
		}
		return list;		

	}*/
	
	
	

  
  
/*    public int addNewVersionOfProcess(){
		long processId = createProcessInstance("ProjectFinanceWorkFlowV2.bpmn","");
		return (int)processId;	
  }
  */
/*    public void removeAllProcess(){
  	kbase.removeProcess("project.finance");
  }*/
  

/*    private  KnowledgeBase readKnowledgeBase() throws Exception {
      KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
      kbuilder.add(ResourceFactory.newClassPathResource("LOS_corporate_project_finance.bpmn"), ResourceType.BPMN2);
      // kbuilder.add(ResourceFactory.newClassPathResource("lcCreation.bpmn"), ResourceType.BPMN2);
      return kbuilder.newKnowledgeBase();
  }*/
  
  //---------------------------------------------------------------------------------------------------------------------
  
  
  // Create new Process Instance
/*    private static int   createNewProcess(){
		long processInstanceId = createProcessInstance("LOS_corporate_project_finance.bpmn","los.corporate.pf");
		return (int)processInstanceId;
  }*/

	public static WorkflowInfo createNewProcess(String processId,
			Map<String, Object> params, List<String> userList,
			List<String> groupList, Map<String, List<UserGroupBO>> userGroups,
			Map<String, String> workFlowMapping,HashMap<String,WorkItemHandler> workItemHandler,
			TaskEventListener listener) throws JBPMError {
		return createNewProcess(processId, globalKbase, params, userList,
				groupList, workItemHandler,listener);
	}
		
	public static WorkflowInfo createNewProcess(String processId,
			KnowledgeBase kbase, Map<String, Object> params,
			List<String> userList, List<String> groupList,
			  HashMap<String, WorkItemHandler> workItemHandler,TaskEventListener listener
			) throws JBPMError {
				WorkflowInfo wi = createProcessInstance(processId, kbase, params,
				userList, groupList, 
				workItemHandler,listener);
		return wi;
	}
    
/*    public static int   createAlternativeProcess(){
		long processInstanceId = createProcessInstance("LOS_corporate_project_finance_2.bpmn","los.corporate.project_finance_2");
		return (int)processInstanceId;
    }*/
    
	// Search methods
	    public static ArrayList<TaskBean> findTasks(String userName, List<String> userList,List<String> groupList) {
			if (!userName.equalsIgnoreCase(""))
				return getUserTasks(userName,userList,groupList);
			else
				return new ArrayList<TaskBean>();
		}

		public static ArrayList<TaskBean> findTasks(long processID, List<String> userList,List<String> groupList) {
			return getUserTaskbyProcessID(processID, userList,groupList,null);
		}
		public static ArrayList<TaskBean> findTasks(String userName, Integer firstRow, int numberOfRows,List<String> userList,List<String> groupList) {
			if (!userName.equalsIgnoreCase("")){
				
				return getUserTasks(userName,firstRow,numberOfRows,userList,groupList);
			}
			else
				return new ArrayList<TaskBean>();
		}

	
		// Task Completion methods
		/*
		public static void completeTasks(String userName,KnowledgeBase kbase, List<String> userList,List<String> groupList) throws JBPMError {
			TaskService client = initTaskService(userList,groupList);
			System.out.println("Completing Task assigned to user  " + userName);
			completeUserTasks(userName, client,kbase);
		}
		*/
	
	
/*
	
		public static void completeTaskByProcessId(long processID,KnowledgeBase kbase,Object result, List<String> userList,List<String> groupList) throws JBPMError {
			StatefulKnowledgeSession ksession = null;
			
			// Getting Task Information
			List <Status> statusList = new ArrayList <Status>();
			statusList.add(Status.InProgress);
			statusList.add(Status.Reserved);
			
			TaskService client = initTaskService(userList,groupList);
			
			List<TaskSummary> userTasks = client.getTasksByStatusByProcessId(processID,statusList, "en-UK" );
			

			int  sessionId  =0;
			
			if (userTasks.size() > 0){
				 for (TaskSummary userTask : userTasks){
						if (userTask.getProcessSessionId() != sessionId){
						}
					 
					    // Getting task data
						sessionId = userTask.getProcessSessionId();
						
						// Loading the session
						Environment env = EnvironmentFactory.newEnvironment();
						env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
						
						ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, null, env);
						LocalHTWorkItemHandler localHTWorkItemHandler = new LocalHTWorkItemHandler(client, ksession);
						localHTWorkItemHandler.connect();
						ksession.getWorkItemManager().registerWorkItemHandler("Human Task", localHTWorkItemHandler);
						ksession.getWorkItemManager().registerWorkItemHandler("Service", new SystemTaskHandler());
					 
				         try {
							client.start(userTask.getId(), userTask.getCreatedBy().getId());
							if(result!=null ){
								client.completeWithResults(userTask.getId(), userTask.getCreatedBy().getId(), result);
							}else{
								client.complete(userTask.getId(), userTask.getCreatedBy().getId(), null);
							}
						} catch (Exception e) {
							e.printStackTrace();
							throw new JBPMError(e.getMessage());
						}   					 
					 
				 }
				 
			}		
		}	
		
		*/
			
		
		public static void taskInProgress(long taskId,KnowledgeBase kbase, List<String> userList, List<String> groupList,
				HashMap<String,WorkItemHandler> workItemHandler,TaskEventListener listener) throws JBPMError {
			StatefulKnowledgeSession ksession = null;
			
			try {
				TaskService client = initTaskService(userList,groupList,listener);
				// Getting task data using taskid
				Task task = client.getTask(taskId);
				//long processInstanceId = task.getTaskData().getProcessInstanceId();
				int  sessionId = task.getTaskData().getProcessSessionId();
				String userId = task.getTaskData().getActualOwner().getId();
				// Loading the session
				Environment env = EnvironmentFactory.newEnvironment();
				env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
				ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, null, env);
				LocalHTWorkItemHandler localHTWorkItemHandler = new LocalHTWorkItemHandler(client, ksession);
				localHTWorkItemHandler.connect();
				ksession.getWorkItemManager().registerWorkItemHandler("Human Task", localHTWorkItemHandler);
				
				for (String name :workItemHandler.keySet()){
						ksession.getWorkItemManager().registerWorkItemHandler(name,workItemHandler.get(name));
				}
				
				
				//ksession.getWorkItemManager().registerWorkItemHandler("Service", new SystemTaskHandler());
				// setting task in progress the task
				client.start(taskId, userId);
				 
				sysLog.info("Task started sucessfully for  taskId   " +taskId+ " for user  "  +userId);	
			} catch (Exception e) {
				sysLog.error("Error in taskInProgress method in JbpmController  ",e);
				e.printStackTrace();
				throw new JBPMError(e.getMessage());
			}		
		}
		
		
		
		public static WorkflowInfo completeTask(long taskId,KnowledgeBase kbase, List<String> userList, List<String> groupList,  
				HashMap<String,WorkItemHandler> workItemHandler,TaskEventListener listener, Map result) throws JBPMError {
			StatefulKnowledgeSession ksession = null;
			WorkflowInfo wi =null;
			try {
				TaskService client = initTaskService(userList,groupList,listener);
				
				// Getting task data using taskid
				Task task = client.getTask(taskId);
				//long processInstanceId = task.getTaskData().getProcessInstanceId();
				int  sessionId = task.getTaskData().getProcessSessionId();
				String userId = task.getTaskData().getActualOwner().getId();
				String processId = task.getTaskData().getProcessId();
				long processInstanceId = task.getTaskData().getProcessInstanceId() ;
				// Loading the session
				Environment env = EnvironmentFactory.newEnvironment();
				env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
				
				ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, null, env);
				LocalHTWorkItemHandler localHTWorkItemHandler = new LocalHTWorkItemHandler(client, ksession);
				localHTWorkItemHandler.connect();
				ksession.getWorkItemManager().registerWorkItemHandler("Human Task", localHTWorkItemHandler);
				//ksession.addEventListener(new LosProcessEventListener()); 	 
				for (String name : workItemHandler.keySet()) {
					ksession.getWorkItemManager().registerWorkItemHandler(name,workItemHandler.get(name));
				}

				// ksession.getWorkItemManager().registerWorkItemHandler("Service", new SystemTaskHandler());
				
				// Completing the task
				//client.start(taskId, userId);
				
				if(result==null  || result.size()==0) {
					Comment come = new Comment();
					Date date = null;
					if (task.getDeadlines().getStartDeadlines().size() != 0)
						date = task.getDeadlines().getStartDeadlines().get(0).getDate();
					if (task.getDeadlines().getEndDeadlines().size() != 0)
						date = task.getDeadlines().getEndDeadlines().get(0).getDate();
				    SimpleDateFormat dateFormat = new SimpleDateFormat(FormatWithTime);
					try {
						String format = dateFormat.format(date);
						come.setAddedAt(date);
						come.setText(format);
					} catch (Exception e) {
						e.printStackTrace();
					}

					client.addComment(taskId, come);
					client.complete(taskId, userId, null);
					
				}else{	
					Comment come = new Comment();
					Date date = null;
					if (task.getDeadlines().getStartDeadlines().size() != 0)
						date = task.getDeadlines().getStartDeadlines().get(0).getDate();
					if (task.getDeadlines().getEndDeadlines().size() != 0)
						date = task.getDeadlines().getEndDeadlines().get(0).getDate();
				   SimpleDateFormat dateFormat = new SimpleDateFormat(FormatWithTime);
					try {
					   String format = dateFormat.format(date);
					   come.setAddedAt(date);
					   come.setText(format);
					} catch (Exception e) {
						e.printStackTrace();
					}

					client.addComment(taskId, come);
					client.completeWithResults(taskId, userId, result);
				
				}
				wi = new WorkflowInfo();
				wi.setProcessId(processId);
				wi.setProcessInstanceId(processInstanceId);
				//assignTaskToUser(client, processId, processInstanceId, userList, groupList, userGroups, workFlowMapping);
				//Closing the session
	/*			if (ksession.getProcessInstances().size() == 0){
					ksession.dispose();
				}*/
				sysLog.info("Task complted sucessfully for  processid  " +processId+ " and  processinstance"  +processInstanceId);	
			} catch (Exception e) {
				sysLog.error("Error in WorkflowInfo completeTask method in JbpmController  ",e);
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new JBPMError(e.getMessage());
			}	
			return wi;
		}

		public static WorkflowInfo skipTask(long taskId,KnowledgeBase kbase,  Map<String, List<String>> usersGroupMap, String userId) throws JBPMError {
			StatefulKnowledgeSession ksession = null;
			WorkflowInfo wi =null;
			try {
				 List<String> userList = usersGroupMap.get("userList");
				 userList.add("qa1");
				 userList.add("qa2");
				 List<String> groupList = usersGroupMap.get("groupList");
				 groupList.add("QA");
				TaskService client = initTaskService1(userList,groupList);
				// Getting task data using taskid
				Task task = client.getTask(taskId);
				//long processInstanceId = task.getTaskData().getProcessInstanceId();
				int  sessionId = task.getTaskData().getProcessSessionId();
				String processId = task.getTaskData().getProcessId();
				long processInstanceId = task.getTaskData().getProcessInstanceId() ;
				// Loading the session
				Environment env = EnvironmentFactory.newEnvironment();
				env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
				
				ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, null, env);
				LocalHTWorkItemHandler localHTWorkItemHandler = new LocalHTWorkItemHandler(client, ksession);
				localHTWorkItemHandler.connect();
				
				ksession.getWorkItemManager().registerWorkItemHandler("Human Task", localHTWorkItemHandler);
			    client.skip(taskId, userId);
				wi = new WorkflowInfo();
				wi.setProcessId(processId);
				wi.setProcessInstanceId(processInstanceId);
		 
				sysLog.info("Task complted sucessfully for  processid  " +processId+ " and  processinstance"  +processInstanceId);	
			} catch (Exception e) {
				sysLog.error("Error in WorkflowInfo completeTask method in JbpmController  ",e);
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new JBPMError(e.getMessage());
			}	
			return wi;
		}

		
		
		public static  JBPMProcessInfo getWorkFlowFileMetaData(String bpmnFile,boolean onlyGatewayNodes) throws JBPMError{
			JBPMProcessInfo processInfo = new JBPMProcessInfo();
			Map<String,JBPMNode> nodeList  = new LinkedHashMap<String,JBPMNode>();
			List<String> processIdList = new ArrayList<String>();
			
			// Only one bpmn file is added to get metadata
			List<String> workFileNameList = new ArrayList<String>();
			String bpmnFilePath = (String) externalProperties.get("BPMN_FILE_PATH");
			bpmnFilePath = bpmnFilePath + bpmnFile;
			workFileNameList.add(bpmnFilePath);
			
			// Gettign process metadata
			if(workFileNameList!=null ){
				KnowledgeBase intialiseKnowledgeBase = intialiseKnowledgeBase(workFileNameList);
				
				// Getting process ids
		
				
				/*	Collection<Process> processes = intialiseKnowledgeBase.getProcesses();
				for (Process list : processes) {
					String processId = list.getId();
					processIdList.add(processId);
				}*/
				
				
				// Getting process node data
				WorkflowProcess process = (WorkflowProcess)  intialiseKnowledgeBase.getProcess(processIdList.get(0));
				Node[] nodes = process.getNodes();
			 
				for (Node node: nodes){
					
				     if(node instanceof HumanTaskNode && !onlyGatewayNodes){
				    	 HumanTaskNode humnaTaks = (HumanTaskNode)node;
				    	 Work work = humnaTaks.getWork();
				    	 Map<String, Object> parameters = work.getParameters();
				    	 Iterator<Entry<String, Object>> entries = parameters.entrySet().iterator();
				    	 while (entries.hasNext()) {
				    		     Entry<String, Object> entry = entries.next();
				    		     if( entry.getKey()!=null &&  entry.getKey().equals("TaskName")){
				    		    	 JBPMNode jbppNode = new JBPMNode();
				    		    	 jbppNode.setName((String)entry.getValue());
				    		    	 jbppNode.setProcessId(processIdList.get(0) + ":" +(String)entry.getValue());
				    		    	 nodeList.put(jbppNode.getName(),jbppNode);
				    		     }
				    		}
				     }else if (node instanceof CompositeContextNode   && !onlyGatewayNodes){
				    	 CompositeContextNode  subProcess = (CompositeContextNode )node;
				    	 Node[] subProcessNodes =  subProcess.getNodes();
				    	 
				    	 for (Node subNode: subProcessNodes){
					    	 if(subNode instanceof HumanTaskNode && !onlyGatewayNodes){
						    	 HumanTaskNode ht = (HumanTaskNode)subNode;
						    	 Work w = ht.getWork();
						    	 Map<String, Object> para = w.getParameters();
						    	 Iterator<Entry<String, Object>> es = para.entrySet().iterator();
						    	 while (es.hasNext()) {
						    		     Entry<String, Object> e = es.next();
						    		     if( e.getKey()!=null &&  e.getKey().equals("TaskName")){
						    		    	 JBPMNode jbppNode = new JBPMNode();
						    		    	 jbppNode.setName((String)e.getValue());
						    		    	 jbppNode.setProcessId(processIdList.get(0) + ":" +(String)e.getValue());
						    		    	 nodeList.put(jbppNode.getName(),jbppNode);
						    		     }
						    		}
						     }
				    		 
				    	 }
				    	 
				     }else if (node instanceof Split && onlyGatewayNodes){
							Set<String> connection = new HashSet<String>(); 
							JBPMNode jbppNode = new JBPMNode();
							
							jbppNode.setName(node.getName());
							
							// Set outgoing connection 
							Map<String, List<Connection>> Connection = node.getOutgoingConnections();
							Iterator<List<Connection>> iternation = Connection.values().iterator();
							while(iternation.hasNext()){
								List<Connection> conns = iternation.next();
								Iterator<Connection>  i= conns.iterator();
								while(i.hasNext()){
									Connection conn = i.next();
									System.out.println(conn.getTo().getName());
									connection.add(conn.getTo().getName());
								}
							}
							jbppNode.setConnection(connection);
							
							
							//Set incoming connection
							Set<String> inConnection = new HashSet<String>();
							Map<String, List<Connection>>  conn = node.getIncomingConnections();
							Iterator<List<Connection>> it = conn.values().iterator();
							while(it.hasNext()){
								List<Connection> c = it.next();
								Iterator<Connection>  i2 = c.iterator();
								while(i2.hasNext()){
									Connection c2 = i2.next();
									System.out.println(c2.getFrom().getName());
									inConnection.add(c2.getFrom().getName());
								}
							}
							jbppNode.setIncomingConn(inConnection);						
							
							nodeList.put(jbppNode.getName(),jbppNode);		    	 
				     }
				 
				}
			}
			
			processInfo.setProcessId(processIdList.get(0));
			processInfo.setNodes(nodeList);
			return processInfo;
		}
		

		public static void claimTask(long taskId,String userId, List<String> userList,List<String> groupList,
				TaskEventListener listener){
			TaskService client = initTaskService(userList,groupList,listener);
			client.claim(taskId, userId);
		}
		
		public static void claimTask(long taskId,String userId, List<String> userList,List<String> groupList){
			TaskService client = initTaskService1(userList,groupList);
			client.claim(taskId, userId);
		}
		public static void activateTask(long taskId,String userId, List<String> userList,List<String> groupList,
				TaskEventListener listener){
			TaskService client = initTaskService(userList,groupList,listener);
			client.activate(taskId, userId);
		}
		/* Not tested */
		public static void reassignTask(long taskId,String userId,String targetUserId, List<String> userList,List<String> groupList,
				TaskEventListener listener){
			TaskService client = initTaskService(userList,groupList,listener);
			client.forward(taskId, userId, targetUserId);
		}
		
		public static void existTask(long taskId,String userId, List<String> userList,List<String> groupList,
				TaskEventListener listener){
			   TaskService client = initTaskService(userList,groupList,listener);
			   client.exit(taskId, userId);
			 
		}
	 
		public static boolean delegateTask(long taskId,String userId,String targetUserId, List<String> userList,List<String> groupList,
				TaskEventListener listener){
			TaskService client = initTaskService1(userList,groupList);
			client.delegate(taskId, userId, targetUserId);
			return true;
		}
		public static  TaskService signalEvent(long taskId,KnowledgeBase kbase,List<String> userList,List<String> groupList,TaskEventListener listener) throws JBPMError {
			StatefulKnowledgeSession ksession = null;
			TaskService client =null;
			try {
			    client = initTaskService(userList,groupList,listener);
				// Getting task data using taskid
				Task task = client.getTask(taskId);
				//long processInstanceId = task.getTaskData().getProcessInstanceId();
				int  sessionId = task.getTaskData().getProcessSessionId();
				long processInstanceId = task.getTaskData().getProcessInstanceId() ;
				// Loading the session
				Environment env = EnvironmentFactory.newEnvironment();
				env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
				ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, null, env);
				LocalHTWorkItemHandler localHTWorkItemHandler = new LocalHTWorkItemHandler(client, ksession);
				localHTWorkItemHandler.connect();
				ksession.getWorkItemManager().registerWorkItemHandler("Human Task", localHTWorkItemHandler);
				ksession.signalEvent("MyMessage", "SomeValue",processInstanceId);
				 
			}catch (Exception e) {
				sysLog.error("Error in TaskService signalEvent method in JbpmController  ",e);
				e.printStackTrace();
			}
			return client;
			
		}
		
		public static  boolean  reject(long taskId, String userId,List<String> userList,List<String> groupList,TaskEventListener listener) throws JBPMError {
			 
			TaskService client =null;
			try {
			    client = initTaskService(userList,groupList,listener);
				client.exit(taskId, userId);
				return true;
			}catch (Exception e) {
				sysLog.error("Error in TaskService reject method in JbpmController  ",e);
			}
			  return false;
			
		}
		
		/* Incomplete - not tested 
		public static void changeTaskDeadline(long taskId) throws JBPMError{
		TaskServiceSession session  = taskService.createSession();
	    Task task = session.getTask( taskId );       
		
			// Chnage task deadline

			// creating date for Single start deadline
			Date date = new Date();
			date.setMinutes(date.getMinutes() + 2);
			

			// creating escalation for Single start deadline
			List<Escalation> escalations = new ArrayList<Escalation>();
			Escalation e = new Escalation();
			e.setName("test start esclation");

			
			//reassignment for the escalation
			List<Reassignment> reassignments =new ArrayList<Reassignment>();
			Reassignment reassignment = new Reassignment();
			List<OrganizationalEntity> potentialOwners =new ArrayList<OrganizationalEntity>();
			potentialOwners.add(new User("Clerk"));
			
	 		reassignment.setPotentialOwners(potentialOwners);
			reassignments.add( reassignment );
		    e.setReassignments(reassignments);
			
			// Single start deadline 
			Deadline d = new Deadline();
			d.setDate(date);
		    d.setEscalations(escalations);
		    
		    // Start deadlines
		    List<Deadline> startDeadlines = new ArrayList<Deadline>();
			startDeadlines.add(d);
			
			// All deadlines
			Deadlines deadlines = new Deadlines();
			deadlines.setStartDeadlines(startDeadlines);
			task.setDeadlines(deadlines);
			
			try {
				session.addTask(task, null);
				session.dispose();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				throw new JBPMError(e1.getMessage());
			}

		}
		*/
		
		/* Not tested
		public static void changeTaskPriority(long taskId, int priority, List<String> userList,List<String> groupList){
			TaskService client = initTaskService(userList,groupList);
			Task task = client.getTask(taskId);
			task.setPriority(priority);
		}
		*/
		
		private static String getStatusDesc(String jbpmStatus ){
			if (jbpmStatus.equals(Status.Created)){
				return "created";
			}else if (jbpmStatus.equals(Status.Error.name())){
				return "Error";
			}else if (jbpmStatus.equals(Status.Exited.name())){
				return "Exited";
			}else if (jbpmStatus.equals(Status.Failed.name())){
				return "Failed";
			}else if (jbpmStatus.equals(Status.InProgress.name())){
				return "In Progress";
			}else if (jbpmStatus.equals(Status.Obsolete.name())){
				return "Obsolete";
			}else if (jbpmStatus.equals(Status.Ready.name())){
				return "Approval Pending";
			}else if (jbpmStatus.equals(Status.Reserved.name())){
				return "Not Started";
			}else if (jbpmStatus.equals(Status.Suspended.name())){
				return "Suspended";
			}else if (jbpmStatus.equals(Status.Completed.name())){
				return "Completed";
			}
			return "";
		}
		
		public static long getProcessIntanceId(long taskId, TaskEventListener listener, List<String> userList, List<String> groupList) {
			TaskService client = initTaskService(userList,groupList,listener);
			// Getting task data using taskid
			Task task = client.getTask(taskId);
			return task.getTaskData().getProcessInstanceId();
		}
		
		public static Map<String,String> taskGroups(String bpmnFile){
			Map<String,String> newHashMap = new LinkedHashMap<String,String>();
			List<String> processIdList = new ArrayList<String>();
			List<String> workFileNameList = new ArrayList<String>();
			String bpmnFilePath = (String) externalProperties.get("BPMN_FILE_PATH");
			bpmnFilePath = bpmnFilePath + bpmnFile;
			workFileNameList.add(bpmnFilePath);
			if(workFileNameList!=null ){
				KnowledgeBase intialiseKnowledgeBase;
				try {
					intialiseKnowledgeBase = intialiseKnowledgeBase(workFileNameList);
					Collection<Process> processes = intialiseKnowledgeBase.getProcesses();
					for (Process list : processes) {
						String processId = list.getId();
						processIdList.add(processId);
					}
				 
					// Getting process node data
					WorkflowProcess process = (WorkflowProcess)  intialiseKnowledgeBase.getProcess(processIdList.get(0));
					Node[] nodes = process.getNodes();
					for (Node node: nodes){
						if (node instanceof CompositeContextNode ){
					    	 CompositeContextNode  subProcess = (CompositeContextNode )node;
					    	 String subProcessName = subProcess.getName();
					    	 Node[] subProcessNodes =  subProcess.getNodes();
					    	 for (Node subNode: subProcessNodes){
						    	 if(subNode instanceof HumanTaskNode ){
							    	 HumanTaskNode ht = (HumanTaskNode)subNode;
							    	 Work w = ht.getWork();
							    	 Map<String, Object> para = w.getParameters();
							    	 Iterator<Entry<String, Object>> es = para.entrySet().iterator();
							    	 while (es.hasNext()) {
							    		     Entry<String, Object> e = es.next();
							    		     if( e.getKey()!=null &&  e.getKey().equals("TaskName")){
							    		    	 newHashMap.put((String)e.getValue(),subProcessName);
							    		     }
							    		}
							     }
					    		 
					    	 }
					    	 
					     } else  if(node instanceof HumanTaskNode ){
					    	 HumanTaskNode humnaTaks = (HumanTaskNode)node;
					    	 Work work = humnaTaks.getWork();
					    	 Map<String, Object> parameters = work.getParameters();
					    	 Iterator<Entry<String, Object>> entries = parameters.entrySet().iterator();
					    	 while (entries.hasNext()) {
					    		     Entry<String, Object> entry = entries.next();
					    		     if( entry.getKey()!=null &&  entry.getKey().equals("TaskName")){
					    		    	 JBPMNode jbppNode = new JBPMNode();
					    		    	 jbppNode.setName((String)entry.getValue());
					    		    	 jbppNode.setProcessId(processIdList.get(0) + ":" +(String)entry.getValue());
					    		    	 newHashMap.put((String)entry.getValue(),"Appeal");
					    		     }
					    		}
					     }
					}
					
				} catch (JBPMError e) {
					e.printStackTrace();
				}
				
		 
		}
			return newHashMap;
		
		}
		
		public static void skipTask(long taskId,String userId, List<String> userList,List<String> groupList,
				TaskEventListener listener){
			    TaskService client = initTaskService(userList,groupList,listener);
			    client.skip(taskId, userId);
		}	
	}

