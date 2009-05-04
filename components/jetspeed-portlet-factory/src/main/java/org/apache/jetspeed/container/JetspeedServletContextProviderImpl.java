/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.container.JetspeedPortletContext;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.pluto.container.PortletRequestContext;
import org.apache.pluto.container.RequestDispatcherService;

import org.apache.portals.bridges.common.ServletContextProvider;

/**
 * @version $Id$
 *
 */
public class JetspeedServletContextProviderImpl implements ServletContextProvider
{    
    private static final String SERVLET_CONTEXT = ServletContextProvider.class.getName()+".context";
    private static final String SERVLET_REQUEST = ServletContextProvider.class.getName()+".request";
    private static final String SERVLET_RESPONSE = ServletContextProvider.class.getName()+".response";
    
    private RequestDispatcherService requestDispatcherService;
    
    private static class ServletContextProxy implements InvocationHandler
    {
        private ServletContext servletContext;
        private PortletContext portletContext;
        
        @SuppressWarnings("unchecked")
        public static ServletContext createProxy(ServletContext servletContext, PortletContext portletContext)
        {
            HashSet interfaces = new HashSet();
            interfaces.add(ServletContext.class);
            Class current = servletContext.getClass();
            while (current != null)
            {
                try
                {
                    Class[] currentInterfaces = current.getInterfaces();
                    for (int i = 0; i < currentInterfaces.length; i++)
                    {
                        interfaces.add(currentInterfaces[i]);
                    }
                    current = current.getSuperclass();
                }
                catch (Exception e)
                {
                    current = null;
                }
            }
            return (ServletContext)Proxy.newProxyInstance(servletContext.getClass().getClassLoader(),
                                                          (Class[])interfaces.toArray(new Class[interfaces.size()]),
                                                          new ServletContextProxy(servletContext, portletContext));
        }
        
        private ServletContextProxy(ServletContext servletContext, PortletContext portletContext)
        {
            this.servletContext = servletContext;
            this.portletContext = portletContext;
        }
        
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            Object retval = null;
            if (("getRequestDispatcher".equals(method.getName()) || "getNamedDispatcher".equals(method.getName())) && 
                args != null && args.length == 1 && args[0] instanceof String)
            {
                if ("getRequestDispatcher".equals(method.getName()))
                {
                    retval = portletContext.getRequestDispatcher((String)args[0]);
                }
                else
                {
                    retval = portletContext.getNamedDispatcher((String)args[0]);
                }
            }
            else
            {
                try
                {
                    retval = method.invoke(servletContext, args);
                }
                catch (InvocationTargetException ite)
                {
                    throw ite.getTargetException();
                }
            }
            return retval;
        }        
    }
    
    private static class HttpServletPortletResourceResponseWrapper extends HttpServletResponseWrapper
    {
        private HttpServletResponse response;
        
        public HttpServletPortletResourceResponseWrapper(HttpServletResponse response)
        {
            super(response);
            this.response = response;
        }

        @Override
        public void sendRedirect(String location) throws IOException
        {
            response.sendRedirect(location);
        }
    }
    
    public JetspeedServletContextProviderImpl(RequestDispatcherService requestDispatcherService)
    {
        this.requestDispatcherService = requestDispatcherService;
    }
    
    public ServletContext getServletContext(GenericPortlet portlet)
    {
        PortletWindow window = Jetspeed.getCurrentRequestContext().getCurrentPortletWindow();
        
        ServletContext servletContext = (ServletContext)window.getAttribute(SERVLET_CONTEXT);
        if (servletContext == null)
        {
            servletContext = ServletContextProxy.createProxy(((JetspeedPortletContext)portlet.getPortletContext()).getServletContext(), portlet.getPortletContext());
            window.setAttribute(SERVLET_CONTEXT, servletContext);
        }
        return servletContext;
    }

    public HttpServletRequest getHttpServletRequest(GenericPortlet portlet, PortletRequest request)
    {        
        PortletWindow window = Jetspeed.getCurrentRequestContext().getCurrentPortletWindow();
        HttpServletRequest req = (HttpServletRequest)window.getAttribute(SERVLET_REQUEST);
        if (req == null)
        {
            PortletRequestContext rc = window.getPortletRequestContext();
            req = requestDispatcherService.getRequestWrapper(rc.getServletContext(),
                                                             rc.getServletRequest(),
                                                             request,
                                                             null,
                                                             true,
                                                             false);
            req.setAttribute(ContainerConstants.PORTLET_CONFIG, rc.getPortletConfig());
            req.setAttribute(ContainerConstants.PORTLET_REQUEST, window.getPortletRequest());
            req.setAttribute(ContainerConstants.PORTLET_RESPONSE, window.getPortletResponse());
            window.setAttribute(SERVLET_REQUEST, req);
        }
        return req;
    }

    public HttpServletResponse getHttpServletResponse(GenericPortlet portlet, PortletResponse response)
    {
        PortletWindow window = Jetspeed.getCurrentRequestContext().getCurrentPortletWindow();
        HttpServletResponse res = (HttpServletResponse)window.getAttribute(SERVLET_RESPONSE);
        if (res == null)
        {
            boolean included = window.getAttribute(PortalReservedParameters.PORTLET_CONTAINER_INVOKER_USE_FORWARD) == null;
            PortletRequestContext rc = window.getPortletRequestContext();
            res = requestDispatcherService.getResponseWraper(rc.getServletContext(),
                                                             rc.getServletResponse(),
                                                             window.getPortletRequest(),
                                                             response,
                                                             included);
            if (PortletWindow.Action.RENDER == window.getAction() && !included)
            {
                res = new HttpServletPortletResourceResponseWrapper(res);
            }
            window.setAttribute(SERVLET_RESPONSE, res);
        }
        return res;
    }
}
