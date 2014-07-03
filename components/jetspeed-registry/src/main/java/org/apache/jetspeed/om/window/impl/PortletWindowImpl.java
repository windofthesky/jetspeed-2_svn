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
package org.apache.jetspeed.om.window.impl;

import org.apache.commons.collections.list.TreeList;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.aggregator.RenderTrackable;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.PortletWindowID;
import org.apache.jetspeed.factory.PortletInstance;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.portlet.HeadElement;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.HeadElementsUtils;
import org.apache.jetspeed.util.KeyValue;
import org.apache.pluto.container.PortletRequestContext;
import org.apache.pluto.container.PortletResponseContext;

import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.WindowState;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <P>
 * The <CODE>PortletWindow</CODE> implementation represents a single window
 * of an portlet instance as it can be shown only once on a single page. 
 * Adding the same portlet e.g. twice on a page results in two different windows.
 * </P>
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 **/
public class PortletWindowImpl implements PortletWindow, PortletWindowID, RenderTrackable
{
    private static final long serialVersionUID = 6578938580906866201L;
    
    private String id;
    private String portletEntityId;
    private ContentFragment fragment;
    private Map<String, Object> attributes;
    
    protected int timeoutCount = 0;
    protected long expiration = 0;
    
    private PortletDefinition pd;
    
    private RequestContext requestContext;
    
    // PortletWindow invocation state
    
    private Action action;
    private PortletRequest portletRequest;
    private PortletResponseContext portletResponseContext;
    private PortletRequestContext portletRequestContext;
    private PortletResponse portletResponse;
    private PortletInstance portletInstance;
    private PortletMode portletMode;
    private WindowState windowState;
    private List<KeyValue<String, HeadElement>> headElements;

    private boolean valid;
    
    public PortletWindowImpl(RequestContext requestContext, ContentFragment fragment)
    {
        this(requestContext, fragment, null);
    }
    
    public PortletWindowImpl(RequestContext requestContext, ContentFragment fragment, PortletDefinition pd)
    {
        this.requestContext = requestContext;
        this.id = fragment.getId();
        this.portletEntityId = fragment.getFragmentId();
        if (this.portletEntityId == null) {
            this.portletEntityId = this.id;
        }
        this.fragment = fragment;
        this.pd = pd;
        this.valid = pd != null;
    }
    
    public boolean isValid()
    {
        return valid;
    }

    public String getWindowId()
    {
        // return unique content fragment id that will
        // identify a unique portlet window per page
        return id;
    }

    /**
    * Returns the identifier of this portlet instance window as object id
    *
    * @return the object identifier
    **/
    public PortletWindowID getId()
    {
        return this;
    }
    
    public String getPortletEntityId()
    {
        // return unique fragment id that will identify
        // a unique fragment per portal that may appear
        // in multiple pages or potentially multiple times
        // in a single page
        return portletEntityId;
    }

    public String toString()
    {
        return getStringId();
    }
    
    public String getStringId()
    {
        // return unique content fragment id that will
        // identify a unique portlet window per page
        return id;
    }
    
    /**
     * Returns the portlet definition
     *
     * @return the portlet definition
     **/
    public PortletDefinition getPortletDefinition()
    {
        return pd;
    }
    
    public ContentFragment getFragment()
    {
        return fragment;
    }
    
    /**
     * Checks if the content is instantly rendered from JPT.
     */
    public boolean isInstantlyRendered()
    {
        return fragment.isInstantlyRendered();
    }
    
    public RequestContext getRequestContext()
    {
        return requestContext;
    }

    public PortletMode getPortletMode()
    {
        if (portletMode == null && valid)
        {
            portletMode = requestContext.getPortalURL().getNavigationalState().getMode(this);
        }
        
        return (portletMode != null ? portletMode : PortletMode.VIEW);
    }

    public WindowState getWindowState()
    {
        if (windowState == null && valid)
        {
            windowState = requestContext.getPortalURL().getNavigationalState().getState(this);
        }
        
        return (windowState != null ? windowState : WindowState.NORMAL);
    }

    public Map<String,Object> getAttributes()
    {
        if (attributes == null)
        {
            attributes = new HashMap<String,Object>();
        }
        return attributes;
    }
    
    public Object getAttribute(String name)
    {
        return getAttributes().get(name);
    }
    
    public void setAttribute(String name, Object value)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("name parameter is required");
        }
        if (value == null)
        {
            getAttributes().remove(name);
        }
        else
        {
            getAttributes().put(name, value);
        }            
    }
    
    public void removeAttribute(String name)
    {
        setAttribute(name, null);
    }

    // --- RenderTrackable implementation
    
    public int getRenderTimeoutCount()
    {
        return timeoutCount;
    }
    
    public synchronized void incrementRenderTimeoutCount()
    {
        timeoutCount++;
    }
    
    public synchronized void setExpiration(long expiration)
    {
        this.expiration = expiration;
    }
    
    public long getExpiration()
    {
        return this.expiration;
    }
    
    public void success()
    {
        timeoutCount = 0;
    }
    
    public void setRenderTimeoutCount(int timeoutCount)
    {
        this.timeoutCount = timeoutCount;
    }
    
    // PortletWindow invocation state
    public void setInvocationState(Action action, 
                                   PortletRequestContext portletRequestContext,
                                   PortletResponseContext portletResponseContext,
                                   PortletRequest portletRequest, PortletResponse portletResponse, 
                                   PortletInstance portletInstance)
    {
        if (!valid)
        {
            throw new IllegalStateException("Invalid window "+getId()+" should not have been invoked");
        }
        this.action = action;
        this.portletRequest = portletRequest;
        this.portletResponseContext = portletResponseContext;
        this.portletRequestContext = portletRequestContext;
        this.portletResponse = portletResponse;
        this.portletInstance = portletInstance;
    }
    
    public void clearInvocationState()
    {
        this.action = null;
        this.portletRequest = null;
        this.portletResponseContext = null;
        this.portletRequestContext = null;
        this.portletResponse = null;
        this.portletInstance = null;
    }

    public Action getAction()
    {
        return action;
    }

    public PortletRequest getPortletRequest()
    {
        return portletRequest;
    }

    public PortletResponseContext getPortletResponseContext()
    {
        return portletResponseContext;
    }

    public PortletRequestContext getPortletRequestContext()
    {
        return portletRequestContext;
    }

    public PortletResponse getPortletResponse()
    {
        return portletResponse;
    }

    public PortletInstance getPortletInstance()
    {
        return portletInstance;
    }

    @SuppressWarnings("unchecked")
    public List<KeyValue<String, HeadElement>> getHeadElements()
    {
        if (headElements == null && fragment != null && fragment.getPortletContent() != null)
        {
            PortletContent portletContent = fragment.getPortletContent();
            
            if (portletContent.isComplete() || !Fragment.PORTLET.equals(fragment.getType()))
            {
                // org.apache.commons.collections.list.TreeList is well-optimized for
                // fast insertions at any index in the list.
                // Refer to description in the javadoc for details.
                headElements = new TreeList();
                HeadElementsUtils.aggregateHeadElements(headElements, fragment);
                
                if (!headElements.isEmpty())
                {
                    HeadElementsUtils.mergeHeadElementsByHint(headElements);
                }
            }
        }
        
        if (headElements == null)
        {
            return Collections.emptyList();
        }
        else
        {
            return headElements;
        }
    }
    
}
