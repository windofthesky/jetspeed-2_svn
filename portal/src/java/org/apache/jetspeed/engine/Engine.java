/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.engine;

import javax.servlet.ServletConfig;

import org.apache.commons.configuration.Configuration;
import org.apache.jetspeed.PortalContext;
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

}
