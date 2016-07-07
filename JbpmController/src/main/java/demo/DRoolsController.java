package com.bpm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DecisionTableFactory;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.io.impl.FileSystemResource;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;


public class DRoolsController {
	static Logger sysLog = Logger.getLogger(DRoolsController.class.getName());
	public static void executeRule(String ruleFile,ArrayList facts,HashMap globalParam){
		
        try {
			KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
			kbuilder.add(new ClassPathResource(ruleFile),ResourceType.DRL);
			if (kbuilder.hasErrors()) {
				for (KnowledgeBuilderError error : kbuilder.getErrors()) {
					System.out.println(">>> Error:" + error.getMessage());

				}

			}
			KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
			kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
			StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
			
			//Setting global params
			Iterator itr = globalParam.entrySet().iterator();

			while (itr.hasNext()){
				Entry entry =(Entry) itr.next();
				ksession.setGlobal(entry.getKey().toString(), entry.getValue());
			}

			for (Object fact :facts){
				ksession.insert(fact);
			}

			int fired = ksession.fireAllRules();
			ksession.dispose();
		} catch (Exception e) {
			sysLog.error("Error in executeRule method in DRoolsController Bean ",e);
			// TODO: handle exception
			e.printStackTrace();
		}
	}	
	

    public static KnowledgeBase intitialise(String ruleFile) throws Exception {
    	  KnowledgeBase kbase =null;
    		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            DecisionTableConfiguration config = KnowledgeBuilderFactory.newDecisionTableConfiguration();
            config.setInputType(DecisionTableInputType.XLS);
            Properties externalProperties = FileController.getExternalProperties();
        	String bpmnFilePath = (String) externalProperties.get("BPMN_FILE_PATH");
    	    bpmnFilePath = bpmnFilePath + ruleFile;
    	  try {    		
    		   kbuilder.add(ResourceFactory.newFileResource(bpmnFilePath), ResourceType.DTABLE, config);
    	       DecisionTableFactory
    	                .loadFromInputStream(ResourceFactory
    	                    .newFileResource(bpmnFilePath)
    	                    .getInputStream(), config);
    	 }catch (Exception e) {
    		 kbuilder.add(ResourceFactory.newFileResource(ruleFile), ResourceType.DTABLE, config);
    		 sysLog.error("Error in KnowledgeBase intitialise in DRoolsController Bean ",e);
    		 	e.printStackTrace();
    		      DecisionTableFactory
    	                .loadFromInputStream(ResourceFactory
    	                    .newClassPathResource(ruleFile)
    	                    .getInputStream(), config);
  	   }
 
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error: errors) {
                System.err.println(error);
            }
            throw new IllegalArgumentException("Could not parse knowledge.");
        }
        kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
    	  
        return kbase;
    }

    
    public static void executeDecisionTable(KnowledgeBase kbase,ArrayList facts,HashMap globalParam){
        StatefulKnowledgeSession ksession=null;
        try {
              ksession = kbase.newStatefulKnowledgeSession();
              KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
			
			//Setting global params
			Iterator itr = globalParam.entrySet().iterator();
			boolean noValueFound=false;
			String key="";
			while (itr.hasNext()){
				Entry entry =(Entry) itr.next();
				if(entry.getValue()!=null){
					ksession.setGlobal(entry.getKey().toString(), entry.getValue());
				}else{
					noValueFound=true;
					key = entry.getKey().toString();
				}
			}

			for (Object fact :facts){
				ksession.insert(fact);
			}

			//int fired = ksession.fireAllRules();
			if(!noValueFound){
				ksession.fireAllRules();
			}else{
				sysLog.error(key + " should not be blank to calculate ratios............");
			}
		
		} catch (Exception e) {
			sysLog.error("Error in executeDecisionTable method in DRoolsController Bean",e);
		}finally{
			if(ksession!=null)
			ksession.dispose();
		}
	}	
	

}
