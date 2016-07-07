package com.bpm.util;

import org.jbpm.task.event.TaskEventListener;
import org.jbpm.task.event.entity.TaskUserEvent;

public class JBPMTaskEventListener implements TaskEventListener {

	public void taskCreated(TaskUserEvent event) {
		// TODO Auto-generated method stub
		System.out.println(" ### taskCreated ");
		System.out.println(" ### taskCreated ");
	}

	public void taskClaimed(TaskUserEvent event) {
		System.out.println(" ### taskClaimed ");
	}

	public void taskStarted(TaskUserEvent event) {
		// TODO Auto-generated method stub
		System.out.println(" ### taskStarted ");
		
	}

	public void taskStopped(TaskUserEvent event) {
		// TODO Auto-generated method stub
		System.out.println(" ### taskStopped ");
		
	}

	public void taskReleased(TaskUserEvent event) {
		// TODO Auto-generated method stub
		System.out.println(" ### taskReleased ");
		
	}


	public void taskCompleted(TaskUserEvent event) {
		// TODO Auto-generated method stub
		System.out.println(" ### taskCompleted ");
		
	}


	public void taskFailed(TaskUserEvent event) {
		// TODO Auto-generated method stub
		System.out.println(" ### taskFailed ");
		
	}


	public void taskSkipped(TaskUserEvent event) {
		// TODO Auto-generated method stub
		System.out.println(" ### taskSkipped ");
		
	}


	public void taskForwarded(TaskUserEvent event) {
		// TODO Auto-generated method stub
		System.out.println(" ### taskForwarded ");
		
	}

}