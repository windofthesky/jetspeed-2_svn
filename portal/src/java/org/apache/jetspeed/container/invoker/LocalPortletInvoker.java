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
package org.apache.jetspeed.container.invoker;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.container.ContainerConstants;
import org.apache.jetspeed.container.JetspeedPortletContext;
import org.apache.jetspeed.factory.JetspeedPortletFactory;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * JetspeedPortletInvoker invokes local (internal) portlet applications.
 * Local portlet applications are stored within the Jetspeed Portlet application.
 * They are not separate web applications; but are stored under Jetspeed's
 * WEB-INF/apps directory.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class LocalPortletInvoker implements JetspeedPortletInvoker
{
    private final static Log log = LogFactory.getLog(LocalPortletInvoker.class);

    protected ServletContext jetspeedContext;
    protected ServletConfig jetspeedConfig;
    protected PortletDefinition portletDefinition;
    protected boolean activated = false;
    /**
     * One class loader per local portlet application
     */
    protected static Map classLoaders = new HashMap();

    public static final String LOCAL_CLASSES = "/WEB-INF/classes/";    
    public static final String LOCAL_JARS = "/WEB-INF/lib/";    
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.invoker.JetspeedPortletInvoker#activate(org.apache.pluto.om.portlet.PortletDefinition, javax.servlet.ServletConfig)
     */
    public void activate(PortletDefinition portletDefinition, ServletConfig servletConfig)
    {
        System.out.println("%%% internal invoker.portletdef = " + portletDefinition);

        this.jetspeedConfig = servletConfig;
        jetspeedContext = servletConfig.getServletContext();
        this.portletDefinition = portletDefinition;
        activated = true;        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.invoker.JetspeedPortletInvoker#passivate()
     */
    public void passivate()
    {
        activated = false;    
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.invoker.JetspeedPortletInvoker#isActivated()
     */
    public boolean isActivated()
    {
        return activated;
    }
    
    public LocalPortletInvoker()
    {
        activated = false;
    }
    
    /* (non-Javadoc)
     * @see org.apache.pluto.invoker.PortletInvoker#action(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void action(ActionRequest request, ActionResponse response)
        throws PortletException, IOException
    {
        invoke(request, response, ContainerConstants.METHOD_ACTION);
    }
    
    /* (non-Javadoc)
     * @see org.apache.pluto.invoker.PortletInvoker#render(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void render(RenderRequest request, RenderResponse response)
        throws PortletException, IOException
    {
        invoke(request, response, ContainerConstants.METHOD_RENDER);
    }
    
    /* (non-Javadoc)
     * @see org.apache.pluto.invoker.PortletInvoker#load(javax.portlet.PortletRequest, javax.portlet.RenderResponse)
     */
    public void load(PortletRequest request, RenderResponse response)
        throws PortletException
    {
        try
        {
            invoke(request, response, ContainerConstants.METHOD_NOOP);
        }
        catch (IOException e)
        {
            log.error("PortletInvokerImpl.load() - Error while dispatching portlet.", e);
            throw new PortletException(e);
        }
    }
    
    static private final String PHONEY_PORTLET_WINDOW = "<P>++++++++++++++++++++++++++++++++++</P>";
    
    /**
     * Invokes the specific request denoted by the <code>method</code> parameter on a portlet.
     * The portlet is invoked with a direct method call on the portlet. It is not invoked in another web application.
     * This requires manipulation of the current thread's classpath.
     * 
     * @param portletRequest
     * @param portletResponse
     * @param methodID
     * @throws PortletException
     * @throws IOException
     */
    protected void invoke(PortletRequest portletRequest, PortletResponse portletResponse, Integer method)
        throws PortletException, IOException
    {
        PortletApplicationDefinition app = portletDefinition.getPortletApplicationDefinition();
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();            

        String portletApplicationName = app.getWebApplicationDefinition().getContextRoot();
        System.out.println("%%% local invoker.pa = " + portletApplicationName);

        // gather all required data from request and response
        ServletRequest servletRequest = ((javax.servlet.http.HttpServletRequestWrapper) portletRequest).getRequest();

        ServletResponse servletResponse = ((javax.servlet.http.HttpServletResponseWrapper) portletResponse).getResponse();

        ServletContext appContext = jetspeedContext.getContext(portletApplicationName);
        if (null == appContext)
        {
            String message = "Failed to find Servlet context for Portlet Application: " + portletApplicationName;
            log.error(message);
            throw new PortletException();
        }

        
        Portlet portlet = null;
        
        try
        {
            ClassLoader loader = (ClassLoader)classLoaders.get(portletApplicationName);            
            if (null == loader)
            {
                StringBuffer localPath = new StringBuffer("file:");
                localPath.append(jetspeedContext.getRealPath(JetspeedPortletContext.LOCAL_PA_ROOT));
                localPath.append(portletApplicationName);
                String localAppPath = localPath.toString(); 
                URL[] urls = {new URL(localAppPath + LOCAL_CLASSES),
                              new URL(localAppPath + LOCAL_JARS)};
                loader = new URLClassLoader(urls, oldLoader);
                classLoaders.put(portletApplicationName, loader);
            }
            Thread.currentThread().setContextClassLoader(loader);                 
            portlet = JetspeedPortletFactory.getPortlet(jetspeedConfig, portletDefinition);            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        if (method == ContainerConstants.METHOD_NOOP)
        {
            return;
        }

        //res.getWriter().print("Rendering: Portlet Class = " + entity.getPortletClass() + "<BR/>");

        if (method == ContainerConstants.METHOD_ACTION)
        {
            ActionRequest actionRequest = (ActionRequest)portletRequest;            
            ActionResponse actionResponse = (ActionResponse)portletResponse;

            portlet.processAction(actionRequest, actionResponse);
        }
        else if (method == ContainerConstants.METHOD_RENDER)
        {
            RenderRequest renderRequest = (RenderRequest)portletRequest;            
            RenderResponse renderResponse = (RenderResponse)portletResponse;
            
            renderResponse.setContentType("text/html");            
            renderResponse.getWriter().print(PHONEY_PORTLET_WINDOW);
            renderResponse.getWriter().print(portletDefinition.getName());
            renderResponse.getWriter().print(PHONEY_PORTLET_WINDOW);

            portlet.render(renderRequest, renderResponse);

            renderResponse.getWriter().print(PHONEY_PORTLET_WINDOW);
        }

        Thread.currentThread().setContextClassLoader(oldLoader);                 

    }
    
}
