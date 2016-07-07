package com.bpm.util;


 
 

public   class UserGroupBO   {

	private String groupCode;
	private String loginCode;
	private String pbrCode;
	private String userId;
	
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPbrCode() {
		return pbrCode;
	}
	public void setPbrCode(String pbrCode) {
		this.pbrCode = pbrCode;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getLoginCode() {
		return loginCode;
	}
	public void setLoginCode(String loginCode) {
		this.loginCode = loginCode;
	}
 
 

}
