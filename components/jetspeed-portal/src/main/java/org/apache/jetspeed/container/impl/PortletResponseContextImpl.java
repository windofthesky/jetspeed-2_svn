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

package org.apache.jetspeed.container.impl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.providers.ResourceURLProviderImpl;
import org.apache.jetspeed.request.JetspeedRequestContext;
import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletResponseContext;
import org.apache.pluto.container.ResourceURLProvider;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @version $Id$
 *
 */
public abstract class PortletResponseContextImpl implements PortletResponseContext
{
    private PortletContainer container;
    private HttpServletRequest containerRequest;
    private HttpServletResponse containerResponse;
    private HttpServletRequest servletRequest;
    private HttpServletResponse servletResponse;
    private PortletWindow window;
    private boolean closed;
    private boolean released;
    private JetspeedRequestContext requestContext;
    
    public PortletResponseContextImpl(PortletContainer container, HttpServletRequest containerRequest,
                                      HttpServletResponse containerResponse, PortletWindow window)
    {
        this.container = container;
        this.containerRequest = containerRequest;
        this.containerResponse = containerResponse;
        this.window = window;
        this.requestContext = (JetspeedRequestContext)window.getRequestContext();
    }
    
    protected JetspeedRequestContext getRequestContext()
    {
        return requestContext;
    }
    
    protected boolean isClosed()
    {
        return closed;
    }
    
    protected boolean isReleased()
    {
        return released;
    }

    public void init(HttpServletRequest servletRequest, HttpServletResponse servletResponse)
    {
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;
    }
    
    public void addProperty(Cookie cookie)
    {
        if (!isClosed())
        {
            containerResponse.addCookie(cookie);
            // TODO: consider if these should be "transported" from ActionResponse to EventRequest?
        }
    }

    public void addProperty(String key, Element element)
    {
        // default no-op, see PortletRenderResponseContextImpl for MARKUP_HEAD_ELEMENT support 
    }

    public void addProperty(String key, String value)
    {
        if (!isClosed())
        {
            containerResponse.addHeader(key, value);
            // TODO: consider if these should be "transported" from ActionResponse to EventRequest?
        }
    }
    
    public Element createElement(String tagName) throws DOMException
    {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        
        try
        {
            docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            return doc.createElement(tagName);
        }
        catch (ParserConfigurationException e)
        {
            throw new DOMException((short) 0, "Initialization failure");
        }
    }

    public void close()
    {
        closed = true;
    }

    public PortletContainer getContainer()
    {
        return container;
    }

    public PortletWindow getPortletWindow()
    {
        return window;
    }

    public HttpServletRequest getContainerRequest()
    {
        return containerRequest;
    }

    public HttpServletResponse getContainerResponse()
    {
        return containerResponse;
    }

    public HttpServletRequest getServletRequest()
    {
        return servletRequest;
    }

    public HttpServletResponse getServletResponse()
    {
        return servletResponse;
    }

    public void release()
    {
        closed = true;
        released = true;
        container = null;
        servletRequest = null;
        servletResponse = null;
        requestContext = null;
        window = null;
    }

    public void setProperty(String key, String value)
    {
        if (!isClosed())
        {
            containerResponse.setHeader(key, value);
            // TODO: consider if these should be "transported" from ActionResponse to EventRequest?
        }
    }

    public ResourceURLProvider getResourceURLProvider()
    {
        if (!isReleased())
        {
            return new ResourceURLProviderImpl(requestContext, window);
        }
        return null;
    
    }
}
