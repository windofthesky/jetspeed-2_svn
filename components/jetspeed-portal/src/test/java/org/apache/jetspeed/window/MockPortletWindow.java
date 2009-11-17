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

package org.apache.jetspeed.window;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.WindowState;

import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.PortletWindowID;
import org.apache.jetspeed.factory.PortletInstance;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.portlet.HeadElement;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.KeyValue;
import org.apache.pluto.container.PortletRequestContext;
import org.apache.pluto.container.PortletResponseContext;

/**
 * @version $Id$
 *
 */
public class MockPortletWindow implements PortletWindow
{
    private static final long serialVersionUID = 6391120828720160018L;
    
    private PortletWindowID windowId;
    
    public MockPortletWindow(final String id)
    {
        this.windowId = new PortletWindowID()
        {
            private static final long serialVersionUID = 1L;

            public String getStringId()
            {
                return id;
            }

            public String toString()
            {
                return getStringId();
            }
        };
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.PortletWindow#getAttributes()
     */
    public Map<String, Object> getAttributes()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.PortletWindow#getAttribute(java.lang.String)
     */
    public Object getAttribute(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.PortletWindow#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String name)
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.PortletWindow#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String name, Object value)
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.PortletWindow#getFragment()
     */
    public ContentFragment getFragment()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.PortletWindow#getId()
     */
    public PortletWindowID getId()
    {
        return windowId;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.PortletWindow#getPortletDefinition()
     */
    public PortletDefinition getPortletDefinition()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.PortletWindow#getPortletEntityId()
     */
    public String getPortletEntityId()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.PortletWindow#getWindowId()
     */
    public String getWindowId()
    {
        return windowId.getStringId();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.PortletWindow#isInstantlyRendered()
     */
    public boolean isInstantlyRendered()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletWindow#getPortletMode()
     */
    public PortletMode getPortletMode()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.container.PortletWindow#getWindowState()
     */
    public WindowState getWindowState()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.PortletWindow#getAction()
     */
    public Action getAction()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.PortletWindow#getPortletInstance()
     */
    public PortletInstance getPortletInstance()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.PortletWindow#getPortletRequest()
     */
    public PortletRequest getPortletRequest()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.PortletWindow#getPortletRequestContext()
     */
    public PortletRequestContext getPortletRequestContext()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.PortletWindow#getPortletResponse()
     */
    public PortletResponse getPortletResponse()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.PortletWindow#getPortletResponseContext()
     */
    public PortletResponseContext getPortletResponseContext()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.PortletWindow#getRequestContext()
     */
    public RequestContext getRequestContext()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isValid()
    {
        // always return true
        return true;
    }

    public List<KeyValue<String, HeadElement>> getHeadElements()
    {
        return Collections.emptyList();
    }
}
