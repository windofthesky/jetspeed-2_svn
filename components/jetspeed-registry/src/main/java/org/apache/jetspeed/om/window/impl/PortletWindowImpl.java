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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.WindowState;

import org.apache.commons.collections.list.TreeList;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.aggregator.RenderTrackable;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.PortletWindowID;
import org.apache.jetspeed.factory.PortletInstance;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.KeyValue;
import org.apache.pluto.container.PortletEntity;
import org.apache.pluto.container.PortletRequestContext;
import org.apache.pluto.container.PortletResponseContext;
import org.w3c.dom.Element;

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
public class PortletWindowImpl implements PortletWindow, PortletEntity, PortletWindowID, RenderTrackable, Serializable
{
    private static final long serialVersionUID = 6578938580906866201L;
    
    private String id;
    private transient ContentFragment fragment;
    private transient Map<String, Object> attributes;
    
    protected transient int timeoutCount = 0;
    protected transient long expiration = 0;
    
    private transient PortletDefinition pd;
    
    private transient RequestContext requestContext;
    
    // PortletWindow invocation state
    
    private transient Action action;
    private transient PortletRequest portletRequest;
    private transient PortletResponseContext portletResponseContext;
    private transient PortletRequestContext portletRequestContext;
    private transient PortletResponse portletResponse;
    private transient PortletInstance portletInstance;
    private transient List<KeyValue<String, Element>> headElements;

    private boolean valid;
    
    public PortletWindowImpl(RequestContext requestContext, ContentFragment fragment)
    {
        this.requestContext = requestContext;
        this.fragment = fragment;
        this.id = fragment.getId();
    }
    
    public PortletWindowImpl(RequestContext requestContext, ContentFragment fragment, PortletDefinition pd)
    {
        this.requestContext = requestContext;
        this.id = fragment.getId();
        this.fragment = fragment;
        this.pd = pd;
        this.valid = true;
    }
    
    public boolean isValid()
    {
        return valid;
    }

    public String getWindowId()
    {
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
        return id;
    }

    public String toString()
    {
        return getStringId();
    }
    
    public String getStringId()
    {
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
    
    public PortletEntity getPortletEntity()
    {
        return pd != null ? this : null;
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
        return valid ? requestContext.getPortalURL().getNavigationalState().getMode(this) : PortletMode.VIEW;
    }

    public WindowState getWindowState()
    {
        return valid ? requestContext.getPortalURL().getNavigationalState().getState(this) : WindowState.NORMAL;
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
    public List<KeyValue<String, Element>> getHeadElements()
    {
        if (headElements == null)
        {
            // org.apache.commons.collections.list.TreeList is well-optimized for
            // fast insertions at any index in the list.
            // Refer to description in the javadoc for details.
            headElements = new TreeList();
            aggregateHeadElements(getFragment(), headElements);
        }
        
        return headElements;
    }
    
    private static void aggregateHeadElements( ContentFragment f, List<KeyValue<String, Element>> aggregatedHeadElements )
    {
        // TODO: Question: Does the aggregation order direction (top-down or bottom-up) need to be configurable?
        
        List<ContentFragment> contentFragments = (List<ContentFragment>) f.getContentFragments();
        
        if (contentFragments != null && !contentFragments.isEmpty())
        {
            for (ContentFragment child : contentFragments)
            {
                if (!"hidden".equals(f.getState()))
                {
                    aggregateHeadElements(child, aggregatedHeadElements);
                }
            }
        }

        PortletContent portletContent = f.getPortletContent();
        
        // portletContent can be null if this method is invoked before the portlet window starts rendering
        if (portletContent != null)
        {
            // Brief explanation on head element aggregation algorithm (Thanks to Ate for the brilliant ideas!):
            // - Precondition: start from the zero as insertion index.
            // - Rule1: if there already exists an element with the key, 
            //              set the insertion index to the matching index + 1.
            // - Rule2: if there's no existing element with the key, 
            //              insert the element at the current insertion index 
            //              and increase the insertion index.
            
            List<KeyValue<String, Element>> contentHeadElements = f.getPortletContent().getHeadElements();
            
            if (!contentHeadElements.isEmpty())
            {
                int insertionIndex = 0;
                
                for (KeyValue<String, Element> kvPair : contentHeadElements)
                {
                    int offset = aggregatedHeadElements.indexOf(kvPair);
                    
                    if (offset != -1)
                    {
                        insertionIndex = offset + 1;
                    }
                    else
                    {
                        aggregatedHeadElements.add(insertionIndex++, kvPair);
                    }
                }
            }
        }
    }

}
