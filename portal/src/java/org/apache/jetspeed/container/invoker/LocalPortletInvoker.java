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
import org.apache.jetspeed.factory.JetspeedPortletFactoryProxy;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * LocalPortletInvoker invokes local (internal) portlet applications.
 * Local portlet applications are stored within the Jetspeed Portlet application.
 * They are not separate web applications; but are stored under Jetspeed's
 * WEB-INF/apps directory.
 * <h3>Sample Configuration</h3>
 * <pre>
 * <code>
 * factory.invoker.local = org.apache.jetspeed.container.invoker.LocalPortletInvoker
 * factory.invoker.local.pool.size = 50
 * </code> 
 * </pre>
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
            portlet = JetspeedPortletFactoryProxy.getPortlet(jetspeedConfig, portletDefinition);            
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
            renderResponse.getWriter().print(portletDefinition.getName());

            portlet.render(renderRequest, renderResponse);
        }

        Thread.currentThread().setContextClassLoader(oldLoader);                 

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.invoker.JetspeedPortletInvoker#activate(org.apache.pluto.om.portlet.PortletDefinition, javax.servlet.ServletConfig, java.lang.String)
     */
    public void activate(PortletDefinition portletDefinition, ServletConfig servletConfig, String servletMappingName)
    {
        activate(portletDefinition, servletConfig);
    }
    
}
