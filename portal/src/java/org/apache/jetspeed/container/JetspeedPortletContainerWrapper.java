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

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.portlet.PortletException;

import org.apache.pluto.PortletContainer;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.services.PortletContainerEnvironment;
import org.apache.pluto.PortletContainerException;
import org.apache.jetspeed.engine.servlet.ServletObjectAccess;

/**
 * Portlet Container Wrapper to secure access to portlet container.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedPortletContainerWrapper implements PortletContainer
{
    private boolean initialiized = false;

    public void init(
        String uniqueContainerId,
        ServletConfig servletConfig,
        PortletContainerEnvironment environment,
        Properties props)
        throws PortletContainerException
    {

        PortletContainerFactory.getPortletContainerOriginal().init(uniqueContainerId, servletConfig, environment, props);
        initialiized = true;
    }

    public void shutdown() throws PortletContainerException
    {
        //        PortletContainerFactory.
        //            getPortletContainerOriginal().
        //                destroy();
        initialiized = false;
        PortletContainerFactory.getPortletContainerOriginal().shutdown();
    }

    public void renderPortlet(PortletWindow portletWindow, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
        throws PortletException, IOException, PortletContainerException
    {
        PortletContainerFactory.getPortletContainerOriginal().renderPortlet(portletWindow, servletRequest, servletResponse);
        // TODO: figure out how to access pluto-services before container kicks in
        //                              ServletObjectAccess.getServletRequest(servletRequest), 
        //                              ServletObjectAccess.getServletResponse(servletResponse));
    }

    public void processPortletAction(
        PortletWindow portletWindow,
        HttpServletRequest servletRequest,
        HttpServletResponse servletResponse)
        throws PortletException, IOException, PortletContainerException
    {
        PortletContainerFactory.getPortletContainerOriginal().processPortletAction(portletWindow, servletRequest, servletResponse);
        //                                     ServletObjectAccess.getServletRequest(servletRequest),
        //                                     ServletObjectAccess.getServletResponse(servletResponse));
    }

    public void portletLoad(PortletWindow portletWindow, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
        throws PortletException, PortletContainerException
    {
        PortletContainerFactory.getPortletContainerOriginal().portletLoad(
            portletWindow,
            ServletObjectAccess.getServletRequest(servletRequest),
            ServletObjectAccess.getServletResponse(servletResponse));
    }

    /** 
     * <p>
     * isInitialized
     * </p>
     * 
     * @see org.apache.pluto.PortletContainer#isInitialized()
     * @return
     */
    public boolean isInitialized()
    {
        return initialiized;
    }

}
