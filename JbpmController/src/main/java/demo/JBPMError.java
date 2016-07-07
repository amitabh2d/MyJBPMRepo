package com.bpm.util;

import java.util.Set;

public class JBPMError extends Exception {
	private Set <String> errorList;
	private String message;
	
	public JBPMError(Set<String> errorList) {
		this.errorList = errorList;
	}

	public JBPMError(String message) {
		this.message = message;
	}
	
	
	public String getMessage() {
		return message;
	}

	public Set<String> getErrorList() {
		return errorList;
	}

}
