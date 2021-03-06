package com.jbpm.executor;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.kie.internal.executor.api.Command;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.kie.internal.executor.api.Reoccurring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class HelloCommand implements Command , Reoccurring{
	 private static final Logger logger = LoggerFactory.getLogger(HelloCommand.class);
	 private Integer frequency=0;
	 private String expression="";
	public ExecutionResults execute(CommandContext arg0) throws Exception {
		 ExecutionResults execResult= new ExecutionResults();
		 Map<String, Object> outputData = new HashMap<String, Object>();
		 Map<String, Object> inputData;
		 String userId;
		 String recordId;
		 
			try {
				inputData=arg0.getData();
				userId=(String) inputData.get("userId");
				recordId= (String)inputData.get("recordId");
				//frequency= Integer.parseInt((String)inputData.get("frequency"));
				expression=(String)inputData.get("expression");
				logger.info("User Id recieved is : "+userId+ " for Record Id : "+recordId+ " and for recurring frequency : "+frequency);
				outputData.put("executionResult", "Success");
				execResult.setData(outputData);
			} catch (Exception e) {
				logger.error("Exception Occured in execute() of HelloCommand class  :" +e);
				e.printStackTrace();
			}
		return execResult;
		
	}
	
	public Date getScheduleTime() {
		
		logger.info("Expression Recieved is : "+expression);
		/*Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, frequency);  // number of days to add
		logger.info("Inside getScheduleTime() method of MyHelloCommand : "+c.getTime());*/
		return getFrequency();
	}
	
	public  Date getFrequency(){
		logger.info("Entering  getFrequency()");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		String[] ary = null;
		
		try {
			if(expression!=null && expression!=""){
				ary =expression.split(",");
				
			for(int i=0 ;i<ary.length;i++){
				
				String val=ary[i];
				if(!val.equals("0") && !val.equals("")){
					int interval= Integer.parseInt(val);
					if(i==0){
						calendar.add(Calendar.SECOND, interval);
					}
					if(i==1){
						calendar.add(Calendar.MINUTE, interval);
					}
					if(i==2){
						calendar.add(Calendar.HOUR, interval);
					}
					if(i==3){
						calendar.add(Calendar.DATE, interval);
					}
					if(i==4){
						calendar.add(Calendar.MONTH, interval);
					}
					if(i==5){
						calendar.add(Calendar.YEAR, interval);
					}
				}	
			}
		}
			} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("Scheduled Interval is :"+ calendar.getTime());
		return calendar.getTime();
	}

}
