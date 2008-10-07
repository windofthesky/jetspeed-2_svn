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
 * DeploymentEventImpl
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class DeploymentEventImpl implements DeploymentEvent
{
	
	private String type;
	private DeploymentHandler handler;
	private String deploymentRoot;
	private int status;
	

    /**
     * 
     */
    public DeploymentEventImpl(String type, DeploymentHandler handler, String depRoot)
    {
        super();
        this.type = type;
        this.handler = handler;
        this.deploymentRoot = depRoot;
        
        
    }
    
	public DeploymentEventImpl(String type, DeploymentHandler handler)
	{
		super();
		this.type = type;
		this.handler = handler;        
	}

    /**
     * @see org.apache.jetspeed.deployment.DeploymentEvent#getEventType()
     */
    public String getEventType()
    {        
        return this.type;
    }

    /**
     * @see org.apache.jetspeed.deployment.DeploymentEvent#getDeploymentRoot()
     */
    public String getDeploymentRoot()
    {        
        return this.deploymentRoot;
    }

    /**
     * @param string
     */
    public void setDeploymentRoot(String string)
    {
        deploymentRoot = string;
    }


    /**
     * @see org.apache.jetspeed.deployment.DeploymentEvent#getHandler()
     */
    public DeploymentHandler getHandler()
    {        
        return handler;
    }

    /**
     * @return
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * @param i
     */
    public void setStatus(int i)
    {
        status = i;
    }

}
