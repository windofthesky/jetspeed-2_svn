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
    int STATUS_OKAY = 1;
    int STATUS_EVAL = 0;
	int STATUS_FAILED = -1;
	
	/**
	 * 
	 * <p>
	 * getDeploymentObject
	 * </p>
	 * @see org.apache.jetspeed.deployment.DeploymentObject
	 *
	 * @return An instance of <code>org.apache.jetspeed.deployment.DeploymentObject</code>
	 */
	DeploymentObject getDeploymentObject();
	
	/**
	 * 
	 * <p>
	 * getStatus
	 * </p>
	 *
	 * @return The status of the deployment. <code>STATUS_OKAY</code> if the deployment was successful,
	 * <code>STATUS_FAILED</code> if there was a problem deploying the deployment object or <code>-1</code>
	 * if the status was never set (i.e. this event was never acted upon by a listener).
	 */
	int getStatus();
	
	/**
	 * 
	 * <p>
	 * setStatus
	 * </p>
	 *
	 * Sets the status of this event. @see getEvent()
	 * @param status
	 */
	void setStatus(int status);
	
	String getName();
	
	String getPath();
}
