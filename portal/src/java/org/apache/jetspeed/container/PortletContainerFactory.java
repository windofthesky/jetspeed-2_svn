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
