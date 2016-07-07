package com.bpm.util;

import java.util.ArrayList;
import java.util.Set;

public class JBPMNode {
	String name;
	Set <String> connection;
	String processId;
	Set <String> incomingConn;

	

	public Set<String> getIncomingConn() {
		return incomingConn;
	}

	public void setIncomingConn(Set<String> incomingConn) {
		this.incomingConn = incomingConn;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}


	public Set<String> getConnection() {
		return connection;
	}

	public void setConnection(Set<String> connection) {
		this.connection = connection;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

}
