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
package org.apache.jetspeed.deployment.impl;

import org.apache.jetspeed.deployment.DeploymentEvent;
import org.apache.jetspeed.deployment.DeploymentObject;


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
	private DeploymentObject handler;
	private String deploymentRoot;
	private int status=-1;
	protected String name;
	protected String path;
	

    /**
     * 
     */
    public DeploymentEventImpl(String type, DeploymentObject handler, String depRoot)
    {
        super();
        this.type = type;
        this.handler = handler;
        this.deploymentRoot = depRoot;
        this.name = handler.getName();
		this.path = handler.getPath();       
    }
    
	public DeploymentEventImpl(String type, DeploymentObject handler)
	{
		super();
		this.type = type;
		this.handler = handler;
		this.name = handler.getName();
		this.path = handler.getPath();
	}
	
	public DeploymentEventImpl(String type, String name, String path)
	{
		super();
		this.type = type;
		this.name = name;
		this.path = path;
	}

    /**
     * @see org.apache.jetspeed.deployment.DeploymentEvent#getEventType()
     */
    public String getEventType()
    {        
        return this.type;
    }

    /**
     * @see org.apache.jetspeed.deployment.DeploymentEvent#getDeploymentObject()
     */
    public DeploymentObject getDeploymentObject()
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

    /**
     * <p>
     * getName
     * </p>
     *
     * @see org.apache.jetspeed.deployment.DeploymentEvent#getName()
     * @return
     */
    public String getName()
    {
        return name;
    }
    /**
     * <p>
     * getPath
     * </p>
     *
     * @see org.apache.jetspeed.deployment.DeploymentEvent#getPath()
     * @return
     */
    public String getPath()
    {
        return path;
    }
}
