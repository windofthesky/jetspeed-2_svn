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
package org.apache.jetspeed.container;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerException;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalContext;

/**
 * Portlet Container Factory used to access the container.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class PortletContainerFactory
{
    public final static String CONTAINER_IMPL    = "container.impl";
    public final static String CONTAINER_WRAPPER = "container.wrapper";

    private final static Log log = LogFactory.getLog(PortletContainerFactory.class);
        
    private static PortletContainer portletContainer = null;
    private static PortletContainer portletContainerWrapper = null;

    private final static String ERR_MSG = "PortletContainerFactory constructor failed";

    /**
     * Factory method to access the portlet container  
     *
     * @throws PortletContainerException if failed to create context
     * @return PortletContainer The wrappered portlet container
     */
    public static PortletContainer getPortletContainer()
        throws PortletContainerException
    {
        if (portletContainerWrapper != null)
        {        
            return portletContainerWrapper;
        }
        
        createContainer();

        return portletContainerWrapper;        
    }

    /**
     * Used by container wrapper to get the real implementation of the container.
     * 
     * @return PortletContainer The real implementation of the portlet container.
     * @throws PortletContainerException
     */
    static PortletContainer getPortletContainerOriginal()
        throws PortletContainerException
    {
        if (portletContainer != null)
        {        
            return portletContainer;
        }
        
        createContainer();
        
        return portletContainer;
    }


    /**
     * Create the container and wrapper classes from configuration class definitions.
     * 
     * @throws PortletContainerException
     */    
    private static void createContainer()
        throws PortletContainerException
    {        
        try 
        {
            PortalContext pc = Jetspeed.getContext();
            String containerClassName = pc.getConfigurationProperty(CONTAINER_IMPL);
            String wrapperClassName = pc.getConfigurationProperty(CONTAINER_WRAPPER);
            portletContainer = (PortletContainer)Class.forName(containerClassName).newInstance();
            portletContainerWrapper = (PortletContainer)Class.forName(wrapperClassName).newInstance();            
        }
        catch (Throwable t)
        {
            throw new PortletContainerException(ERR_MSG, t);            
        }                
    }
}
