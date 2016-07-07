package com.bpm.util;

import java.util.List;
import java.util.Map;

public class JBPMProcessInfo {
	String ProcessId;
	Map<String,JBPMNode> nodes;
	List<String> errorList;

	
	public String getProcessId() {
		return ProcessId;
	}
	public void setProcessId(String processId) {
		ProcessId = processId;
	}
	public Map<String, JBPMNode> getNodes() {
		return nodes;
	}
	public void setNodes(Map<String, JBPMNode> nodes) {
		this.nodes = nodes;
	} 
}
