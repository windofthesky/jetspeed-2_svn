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
package org.apache.jetspeed.engine;

import javax.servlet.ServletConfig;

import org.apache.commons.configuration.Configuration;
import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.components.ComponentManagement;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.pipeline.Pipeline;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.PortletContainerException;


/**
 * Engine Abstraction - to run from both unit tests and servlet
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public interface Engine extends JetspeedEngineConstants 
{
    /**
     * Initializes the engine with a commons configuration, starting all early initable services.
     *
     * @param configuration a commons <code>Configuration</code> set
     * @param applicationRoot a <code>String</code> path to the application root for resources
     * @param config the servlet configuration, this parameter can be null for unit tests or utilities
     * @throws JetspeedException when the engine fails to initilialize
     */
    public void init(Configuration configuration, String applicationRoot, ServletConfig config)
       throws JetspeedException;

    /**
     * Initializes the portlet container given a servlet configuration.
     * 
     * @param config The servlet configuration.
     * @throws PortletContainerException when the container fails to initialize.
     */
    public void initContainer(ServletConfig config)
        throws PortletContainerException; 
    
    /**
     * Shuts down the Jetspeed engine and all associated services
     *
     * @throws JetspeedException when the engine fails to shutdown
     */
    public void shutdown()
       throws JetspeedException;

    /**
     * Makes a service request to the engine.
     *
     * @param context a <code>RequestContext</code> with the state of the request.
     * @throws JetspeedException when the engine fails to initilialize
     */
    public void service(RequestContext context)
       throws JetspeedException;

    /**
     * Gets the engine's request default pipeline.
     * 
     * @return Pipeline The engine's request pipeline.
     */
    public Pipeline getPipeline();
 
    /**
     * Gets the specified engine's request pipeline.
     * 
     * @return Pipeline A specific request pipeline.
     */ 
    public Pipeline getPipeline(String pipelineName);
 
    /**
     * Get the Portal Context associated with running thread of the engine
     * 
     * @return PortalContext associated with this engine's thread
     */
    public PortalContext getContext();

    /**
     * Gets the real path to an application relative resource
     * 
     * @param path The application relative resource 
     * @return String The real path to that resource
     */
    public String getRealPath(String path);
    
    /**
     * Get the servlet configuration if this engine is running under a servlet container.
     * 
     * @return config The servlet configuration
     */    
    public ServletConfig getServletConfig();
    
    /**
     * Returns the the RequestContext associated with the current
     * thread.  This can be accessed throught <code>org.apache.jetspeed.Jetspeed</code>
     * environment class.
     * @return RequestContext associated with the current thread.
     */
    public RequestContext getCurrentRequestContext();

    public ComponentManager getComponentManager();
    

}
