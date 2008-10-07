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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * DeploymentEventDispatcher
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class DeploymentEventDispatcher
{
    private List deploymentListeners;
    private String deploymentRoot;

    protected static final Log log = LogFactory.getLog("deployment");
    
    
    

    public DeploymentEventDispatcher(String deploymentRoot)
    {
		deploymentListeners = new ArrayList();        
		this.deploymentRoot = deploymentRoot;
    }
    
    public void addDeploymentListener(DeploymentEventListener listener)
    {
		deploymentListeners.add(listener);
    }
    
    public void dispatch(DeploymentEvent event)
    {
    	Iterator itr = deploymentListeners.iterator();
    	event.setDeploymentRoot(deploymentRoot);
    	while(itr.hasNext())
    	{
    		DeploymentEventListener listener = (DeploymentEventListener) itr.next();
    		try
            {
                listener.invoke(event);
				event.setStatus(DeploymentEvent.STATUS_OKAY);
            }
            catch (DeploymentException e)
            {   
            	log.error(e.toString(), e);           	             
                event.setStatus(DeploymentEvent.STATUS_FAILED);
            }
    	}
    }

    

}
