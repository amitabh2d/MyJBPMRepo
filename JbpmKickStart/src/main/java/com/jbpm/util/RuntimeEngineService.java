package com.jbpm.util;

import java.io.IOException;
import java.io.InputStream;

import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author jaina2
 * 
 * This Service class is the starting point for getting RuntimeEngine instance that can interact with the remote API. 
 *  
 */
public class RuntimeEngineService {
	
	private static final Logger logger = LoggerFactory.getLogger(RuntimeEngineService.class);
	private RuntimeEngine engine;
	private RuntimeManager manager;
	InputStream input = null;
	
	/**
	 * @return RuntimeEngine instance
	 */
	
		public RuntimeEngine getRuntimeEngine() {
		
			RuntimeEnvironmentBuilder builder = null;
		try {
			
			//for testing on local 
			
			/*KieServices ks = KieServices.Factory.get();
			KieContainer kContainer = ks.getKieClasspathContainer();
			KieBase kbase = kContainer.getKieBase("kbase");
			JBPMHelper.startH2Server();
			JBPMHelper.setupDataSource();
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
			builder = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().entityManagerFactory(emf).userGroupCallback(new JBossUserGroupCallbackImpl("classpath:/usergroups.properties")).knowledgeBase(kbase);
			//builder= RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().userGroupCallback(new JBossUserGroupCallbackImpl("classpath:/usergroups.properties")).knowledgeBase(kbase);
			manager= RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(builder.get(), "com.td.cb.test:aamtest:1.0.0-SNAPSHOT");
			engine=manager.getRuntimeEngine(null);*/
			
			
			 RuntimeEnvironment env = getEnv("com.td.cb","aam", "1.0.0-SNAPSHOT");

	         RuntimeManagerFactory factory = RuntimeManagerFactory.Factory.get();

	         RuntimeManager manager = factory.newPerRequestRuntimeManager(env,"CaseCreationWorkFlowTest");    

	         engine  = manager.getRuntimeEngine(null);
			
			
		}
		catch (Exception e) {
			logger.error("Exception occured in RemoteRuntimeServices() : ", e);
			e.printStackTrace();
		}
		finally {
			try {
				if(input!=null)
				input.close();
			} catch (IOException e) {
				logger.error("Exception occured in RemoteRuntimeServices() : ", e);
				e.printStackTrace();
			}
		}
		return engine;
	}
	
		
			public RuntimeEnvironment getEnv(String groupId,String artifactId,String versionId){

            RuntimeEnvironmentBuilder env = new RuntimeEnvironmentBuilder.Factory().newDefaultBuilder(groupId,

                         artifactId, versionId, "kbase","ksession");

            return env.get();

			}


				
		public void dispose(){
			
			if(manager!=null)
			manager.disposeRuntimeEngine(engine);
		}
		
		
}
