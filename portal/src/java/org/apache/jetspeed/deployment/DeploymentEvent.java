/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.deployment;

import java.io.InputStream;

import org.apache.commons.configuration.Configuration;

/**
 * <p>
 * DeploymentEvent
 * </p>
 * <p>
 * A <code>DeploymentEvent</code> is fired when a DeploymentEventDispatcher is notified that
 * a deployment event has occurred, for example, a JAR file being drop into a specific directory.
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface DeploymentEvent 
{
    /** Standard deployment event */
    String EVENT_TYPE_DEPLOY = "deploy";
    /** Standard re-deployment event */
    String EVENT_TYPE_REDEPLOY = "redeploy";
    /** Standard un-deployment event */
    String EVENT_TYPE_UNDEPLOY = "undeploy";
    
    int STATUS_OKAY = 0;
	int STATUS_FAILED = 1;
    
	
	/**
	 * Returns the type of event this is.  You can use one of the three pre-defined types
	 * or use a custom one as event types are freeform.
	 * @return String this event's type.
	 */
	String getEventType();
	
	/**
	 * This is the absolute path where content for this event should be deployed to.
	 * @return String absolute path to the final home of the deployed content.
	 */
	String getDeploymentRoot();
	
	void setDeploymentRoot(String deploymentRoot);
	

	/**
	 * Returns an <code>java.io.InputStream</code> containing the deployed content.  Most often
	 * this will be a <code>java.utiljar.JARInputStream</code>.
	 * @return InputStream containing the information to be deployed.
	 */	
	DeploymentHandler getHandler();
	
	int getStatus();
	
	void setStatus(int status);
	
	
	
}
