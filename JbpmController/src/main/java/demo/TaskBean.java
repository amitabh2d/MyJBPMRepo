package com.bpm.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskBean {
	long id;
	String name;
	String desc;
	String processId;
	long processInstanceId;
	String status;
	String taskStatus;

	String owner;
	int priority;
	Date createdOn;

	String createdBy;
	Date expirationTime;
	String applicationNo;
	String applicantName;
	String bussinessCenter;
	String productType;
	String cycleTime;
	String creationDate;
	String workFlowFileName;
	String currency;
	String applicationBranch;
	Date completedOn;
	String completedDate;
	String completedOnString;
	String expirationTimeString;
	String colorCode;
	boolean rejectedStatus;
	String taskName;
	String actualOwner;
	
 
	public String getActualOwner() {
		return actualOwner;
	}

	public void setActualOwner(String actualOwner) {
		this.actualOwner = actualOwner;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public boolean isRejectedStatus() {
		return rejectedStatus;
	}

	public void setRejectedStatus(boolean rejectedStatus) {
		this.rejectedStatus = rejectedStatus;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public String getCompletedOnString() {
		return completedOnString;
	}

	public void setCompletedOnString(String completedOnString) {
		this.completedOnString = completedOnString;
	}

	public String getExpirationTimeString() {
		return expirationTimeString;
	}

	public void setExpirationTimeString(String expirationTimeString) {
		this.expirationTimeString = expirationTimeString;
	}

	public String getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(String completedDate) {
		this.completedDate = completedDate;
	}
	public String facility;
	public String subFacility;
	private String groupCode;
	private String  appliedAmount;
	private String custType;
	
	public Date getCompletedOn() {
		return completedOn;
	}

	public void setCompletedOn(Date completedOn) {
		this.completedOn = completedOn;
	}

	public String getCustType() {
		return custType;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}

	public String getAppliedAmount() {
		return appliedAmount;
	}

	public void setAppliedAmount(String appliedAmount) {
		this.appliedAmount = appliedAmount;
	}

	public int getAppledTenure() {
		return appledTenure;
	}

	public void setAppledTenure(int appledTenure) {
		this.appledTenure = appledTenure;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	private int appledTenure;
	private String applicationStatus;
	private String currencyCode;
	public String getApplicationStatus() {
		return applicationStatus;
	}

	public void setApplicationStatus(String applicationStatus) {
		this.applicationStatus = applicationStatus;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	List<String> usersList = new ArrayList<String>(); 
	public List<String> getUsersList() {
		return usersList;
	}
	public void setUsersList(List<String> usersList) {
		this.usersList = usersList;
	}
	public String getFacility() {
		return facility;
	}
	public void setFacility(String facility) {
		this.facility = facility;
	}
	public String getSubFacility() {
		return subFacility;
	}
	public void setSubFacility(String subFacility) {
		this.subFacility = subFacility;
	}
	public String getApplicationBranch() {
		return applicationBranch;
	}
	public void setApplicationBranch(String applicationBranch) {
		this.applicationBranch = applicationBranch;
	}
	boolean processingStageEnable = false;
	
	public boolean isProcessingStageEnable() {
		return processingStageEnable;
	}
	public void setProcessingStageEnable(boolean processingStageEnable) {
		this.processingStageEnable = processingStageEnable;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getWorkFlowFileName() {
		return workFlowFileName;
	}
	public void setWorkFlowFileName(String workFlowFileName) {
		this.workFlowFileName = workFlowFileName;
	}
	public String getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	public String getApplicationNo() {
		return applicationNo;
	}
	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}
	public String getApplicantName() {
		return applicantName;
	}
	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}
	public String getBussinessCenter() {
		return bussinessCenter;
	}
	public void setBussinessCenter(String bussinessCenter) {
		this.bussinessCenter = bussinessCenter;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getCycleTime() {
		return cycleTime;
	}
	public void setCycleTime(String cycleTime) {
		this.cycleTime = cycleTime;
	}

	
	
	
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Date getExpirationTime() {
		return expirationTime;
	}
	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public long getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
