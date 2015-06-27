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
package org.apache.jetspeed.aggregator.impl;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.PortletAggregator;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.state.MutableNavigationalState;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.portlet.HeadElement;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.HeadElementUtils;
import org.apache.jetspeed.util.KeyValue;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PortletAggregator builds the content required to render a single portlet.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletAggregatorImpl implements PortletAggregator
{
    private PortletRenderer renderer;
    private boolean titleInHeader;
    private Map<String, PortletMode> availablePortletModesMap;
    private Map<String, WindowState> availableWindowStatesMap;

    public PortletAggregatorImpl(PortletRenderer renderer) 
    {
        this(renderer, false);
    }

    public PortletAggregatorImpl(PortletRenderer renderer, boolean titleInHeader) 
    {
        this.renderer = renderer;
        this.titleInHeader = titleInHeader;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.aggregator.Aggregator#build(org.apache.jetspeed.request.RequestContext)
     */
    public void build(RequestContext context) throws JetspeedException, IOException
    {
        // construct Fragment for rendering use with
        // appropriate id to match portlet entity
        String entity = context.getRequestParameter(PortalReservedParameters.PORTLET_ENTITY);
        
        if (entity == null)
        {
            entity = (String)context.getAttribute(PortalReservedParameters.PORTLET_ENTITY);
        }
        
        if (entity == null)
        {
            return;
        }
        
        PortletWindow window = context.resolvePortletWindow(entity);
        
        if (window == null) 
        {        
            String name = context.getRequestParameter(PortalReservedParameters.PORTLET);
            
            if (name == null)
            {
                name = (String)context.getAttribute(PortalReservedParameters.PORTLET);
            }
            
            if (name == null)
            {
                return;
            }
            
            window = context.getInstantlyCreatedPortletWindow(entity, name);
        }
        
        PortletMode requestedPortletMode = getRequestedPortletMode(context);
        WindowState requestedWindowState = getRequestedWindowState(context);
        
        NavigationalState navState = context.getPortalURL().getNavigationalState();
        
        if (navState instanceof MutableNavigationalState)
        {
            MutableNavigationalState mutableNavState = (MutableNavigationalState) navState;
            
            if (!requestedPortletMode.equals(navState.getMode(window)))
            {
                mutableNavState.setMode(window, requestedPortletMode);
            }
            
            if (!requestedWindowState.equals(navState.getState(window)))
            {
                mutableNavState.setState(window, requestedWindowState);
            }
        }
        String skipHead = context.getRequestParameter("skipHead");
        if (skipHead != null) {
            context.setAttribute("jetapp.headers.flag", Boolean.TRUE);
        }

        ContentFragment contentFragment = window.getFragment();
        renderer.renderNow(contentFragment, context);
        
        if (titleInHeader && contentFragment.getPortletContent() != null)
        {            
            context.getResponse().setHeader( "JS_PORTLET_TITLE", StringEscapeUtils.escapeHtml( contentFragment.getPortletContent().getTitle() ) );
        }

        if (skipHead == null) {
            writeHeadElements(context, window);
        }

        context.getResponse().getWriter().write(contentFragment.getRenderedContent());
        PortletContent content = contentFragment.getPortletContent();
        
        if (content != null && content.getExpiration() == 0)
        {
            contentFragment.getPortletContent().release();
        }        
    }
    
    protected void writeHeadElements(RequestContext context, PortletWindow window) throws IOException
    {
        List<KeyValue<String, HeadElement>> headElements = window.getHeadElements();

        if (!headElements.isEmpty())
        {
            PrintWriter out = context.getResponse().getWriter();
            
            out.println("<JS_PORTLET_HEAD_ELEMENTS>");
            
            for (KeyValue<String, HeadElement> kvPair : headElements)
            {
                HeadElement headElement = kvPair.getValue();
                out.println(HeadElementUtils.toHtmlString(headElement));
            }
            
            out.print("</JS_PORTLET_HEAD_ELEMENTS>");
        }
    }
    
    private PortletMode getRequestedPortletMode(final RequestContext context)
    {
        String portletModeName = context.getRequestParameter(PortalReservedParameters.PORTLET_MODE);
        
        if (StringUtils.isBlank(portletModeName))
        {
            return PortletMode.VIEW;
        }
        
        if (availablePortletModesMap == null)
        {
            Map<String, PortletMode> portletModesMap = new HashMap<String, PortletMode>();
            
            for (PortletMode portletMode : JetspeedActions.getStandardPortletModes())
            {
                portletModesMap.put(portletMode.toString(), portletMode);
            }
            
            for (PortletMode portletMode : JetspeedActions.getExtendedPortletModes())
            {
                portletModesMap.put(portletMode.toString(), portletMode);
            }
            
            availablePortletModesMap = portletModesMap;
        }
        
        PortletMode portletMode = availablePortletModesMap.get(portletModeName);
        return (portletMode != null ? portletMode : PortletMode.VIEW);
    }
    
    private WindowState getRequestedWindowState(final RequestContext context)
    {
        String windowStateName = context.getRequestParameter(PortalReservedParameters.WINDOW_STATE);
        
        if (StringUtils.isBlank(windowStateName))
        {
            return WindowState.NORMAL;
        }
        
        if (availableWindowStatesMap == null)
        {
            Map<String, WindowState> windowStatesMap = new HashMap<String, WindowState>();
            
            for (WindowState windowState : JetspeedActions.getStandardWindowStates())
            {
                windowStatesMap.put(windowState.toString(), windowState);
            }
            
            for (WindowState windowState : JetspeedActions.getExtendedWindowStates())
            {
                windowStatesMap.put(windowState.toString(), windowState);
            }
            
            availableWindowStatesMap = windowStatesMap;
        }
        
        WindowState windowState = availableWindowStatesMap.get(windowStateName);
        return (windowState != null ? windowState : WindowState.NORMAL);
    }
}
