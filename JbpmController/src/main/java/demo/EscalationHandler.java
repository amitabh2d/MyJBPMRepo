package com.bpm.util;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.task.Content;
import org.jbpm.task.Deadline;
import org.jbpm.task.Escalation;
import org.jbpm.task.Notification;
import org.jbpm.task.Reassignment;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.User;
import org.jbpm.task.service.EscalatedDeadlineHandler;

public class EscalationHandler implements EscalatedDeadlineHandler {

	@Override
	public void executeEscalatedDeadline(Task task, Deadline deadline,
			Content content, org.jbpm.task.service.TaskService service) {
		// TODO Auto-generated method stub
		if (deadline == null || deadline.getEscalations() == null) {
			System.out.println("**********No escalation detected**********");
			return;
		}

		for (Escalation escalation : deadline.getEscalations()) {
			// we won't impl constraints for now
			// escalation.getConstraints()

			// run reassignment first to allow notification to be send to new
			// potential owners
			if (!escalation.getReassignments().isEmpty()) {
				// get first and ignore the rest.
				Reassignment reassignment = escalation.getReassignments().get(0);

				task.getTaskData().setStatus(Status.Ready);
				List potentialOwners = new ArrayList(reassignment.getPotentialOwners());
				task.getPeopleAssignments().setPotentialOwners(potentialOwners);
				task.getTaskData().setActualOwner(new User("Administrator"));

			}
			for (Notification notification : escalation.getNotifications()) {
				System.out.println("****** Notify by emaill**********");
			}
		}
		deadline.setEscalated(true);
	}

}
